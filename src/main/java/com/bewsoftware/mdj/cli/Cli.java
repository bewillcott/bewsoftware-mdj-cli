/*
 * This file is part of the MDj Command-line Interface program
 * (aka: mdj-cli).
 *
 * Copyright (C) 2020 Bradley Willcott
 *
 * mdj-cli is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * mdj-cli is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.bewsoftware.mdj.cli;

import com.bewsoftware.fileio.ini.IniDocument;
import com.bewsoftware.fileio.ini.IniFile;
import com.bewsoftware.fileio.ini.IniFileFormatException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.bewsoftware.mdj.core.MarkdownProcessor;
import com.bewsoftware.mdj.core.POMProperties;
import com.bewsoftware.mdj.core.TextEditor;
import com.bewsoftware.property.IniProperty;
import java.util.Date;
import java.util.List;

import static com.bewsoftware.fileio.BEWFiles.copyDirTree;
import static com.bewsoftware.fileio.BEWFiles.getResource;
import static java.nio.file.LinkOption.NOFOLLOW_LINKS;
import static java.nio.file.Path.of;
import static java.nio.file.StandardCopyOption.COPY_ATTRIBUTES;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static java.nio.file.StandardOpenOption.WRITE;
import static java.util.regex.Pattern.DOTALL;
import static java.util.regex.Pattern.MULTILINE;
import static com.bewsoftware.mdj.cli.MCPOMProperties.INSTANCE;
import static java.util.regex.Pattern.compile;

/**
 *
 * @author <a href="mailto:bw.opensource@yahoo.com">Bradley Willcott</a>
 *
 * @since 0.1
 * @version 1.0.14
 */
public class Cli {

    /**
     * The name of the configuration ini file.
     */
    public static final String CONF_FILENAME = "mdj-cli.ini";

    /**
     * The single instance of the {@link POMProperties} class.
     */
    public static final MCPOMProperties POM = INSTANCE;

    /**
     * The single instance of the {@link MarkdownProcessor} class.
     */
    private static final MarkdownProcessor MARKDOWN = new MarkdownProcessor();

    /**
     * The pattern used for substitutions.
     */
    private static final Pattern SUBSTITUTION_PATTERN
                                 = Pattern.compile("(?<!\\\\)(?:\\$\\{(?<group>\\w+)[.](?<key>\\w+)\\})");

    /**
     * The configuration ini file.
     */
    static IniFile conf;

    /**
     * The verbosity level.
     */
    static int vlevel;

    /**
     * Process the <i>use</i> section looking for filenames with a {@code .css} extension.
     * <p>
     * Prepend the <i>document.cssDir</i> directory to each one.
     */
    private static void configureStylesheetPaths() {
        final String extn = ".css";

        String use = conf.iniDoc.getString("page", "use", null);
        String cssDir = conf.iniDoc.getString("document", "cssDir", "");
        String systemDate = conf.iniDoc.getString("system", "date", null);

        if (systemDate == null)
        {
            systemDate = new Date().toString();
            conf.iniDoc.setString("system", "date", systemDate);
        }

        // Process stylesheet names from the inifile section.
        if (use != null && !use.isBlank())
        {
            String done = conf.iniDoc.getString(use, systemDate, null);

            if (done == null)
            {
                // Process the '.css' filenames...
                List<IniProperty<String>> keys = conf.iniDoc.getSection(use);

                keys.stream()
                        .filter(key -> key.value().endsWith(extn))
                        .forEachOrdered(key
                                -> conf.iniDoc.setString(use, key.key(),
                                                         of(cssDir, key.value()).toString(),
                                                         key.comment()));

                // Record that we have already processed the '.css' filenames.
                conf.iniDoc.setString(use, systemDate, "done");
            }
        }

        // Process stylesheet names from the metablock, stoed under section: "page".
        String done = conf.iniDoc.getString("page", systemDate, null);

        if (done == null)
        {
            // Process the '.css' filenames...
            List<IniProperty<String>> keys = conf.iniDoc.getSection("page");

            keys.stream()
                    .filter(key -> key.value().endsWith(extn))
                    .forEachOrdered(key
                            -> conf.iniDoc.setString("page", key.key(),
                                                     of(cssDir, key.value()).toString()));

            // Record that we have already processed the '.css' filenames.
            conf.iniDoc.setString("page", systemDate, "done");
        }

    }

    /**
     * Convenience method to access the iniDoc
     * {@link IniDocument#getString(java.lang.String, java.lang.String, java.lang.String) getString()}.
     *
     * @param section label.
     * @param key     name.
     * @param use     Alternate section label.
     *
     * @return result.
     */
    private static String getString(final String section, final String key, final String use) {
        return conf.iniDoc.getString(section, key, conf.iniDoc.getString(use, key, ""));
    }

    /**
     * Process the file's meta block, if any.
     */
    private static void processMetaBlock() {
        String text = conf.iniDoc.getString("page", "text", "");
        Matcher m = compile("\\A(?:@@@\\n(?<metablock>.*?)\\n@@@\\n)(?<body>.*)\\z", DOTALL)
                .matcher(text);
        conf.iniDoc.removeSection("page");

        if (m.find())
        {
            String metaBlock = m.group("metablock");
            text = m.group("body");

            if (vlevel >= 3)
            {
                System.out.println("\n============================================\n"
                                   + "file metablock:\n"
                                   + "--------------------------------------------\n"
                                   + metaBlock
                                   + "\n--------------------------------------------");
            }

            Matcher m2 = compile("^\\s*(?<key>\\w+)\\s*:\\s*(?<value>.*?)?\\s*?$", MULTILINE).matcher(metaBlock);

            while (m2.find())
            {
                String key = m2.group("key");
                String value = m2.group("value");

                if (vlevel >= 3)
                {
                    System.out.println("key = " + key + "\n"
                                       + "value = " + value);
                }

                conf.iniDoc.setString("page", key, value);
            }

            if (vlevel >= 3)
            {
                System.out.println("============================================\n");
            }

        }

        conf.iniDoc.setString("page", "text", text);
    }

    /**
     * Process Named Meta Blocks.
     * <p>
     * A named meta block looks like this:
     * <hr>
     * <pre><code>
     *
     * &#64;&#64;&#64;[name]
     *
     * Some text.
     * Some more text.
     *
     * &#64;&#64;&#64;
     * </code></pre>
     * <hr>
     * The line spacing is <b>required</b>.<br>
     * '{@code name}' is used to refer to this block in the markup text for
     * substitution: ${page.name}.
     * <p>
     * Bradley Willcott
     *
     * @since 14/12/2020
     */
    private static void processNamedMetaBlocks() {
        TextEditor text = new TextEditor(conf.iniDoc.getString("page", "text", ""));
        Pattern p = compile("(?<=\\n)(?:@@@\\[(?<name>\\w+)\\]\\n(?<metablock>.*?)\\n@@@\\n)", DOTALL);

        text.replaceAll(p, m ->
                {
                    String name = m.group("name");
                    String metaBlock = m.group("metablock");

                    //
                    // Add <divX class="<name>"></div> wrapping.
                    // This will allow css control of the formatting of the
                    // contents.
                    metaBlock = "\n<div class=\"" + name + "\">\n" + MARKDOWN.markdown(metaBlock) + "\n</div>\n";

                    if (vlevel >= 3)
                    {
                        System.out.println("============================================\n"
                                           + "name: " + name + "\n"
                                           + "metablock:\n" + metaBlock
                                           + "\n============================================\n");
                    }

                    conf.iniDoc.setString("page", name, metaBlock);
                    return "";
                });

        text.replaceAll("\\\\@@@", "@@@");

        conf.iniDoc.setString("page", "text", text.toString());
    }

    /**
     * Process a template.
     *
     * @param use      Alternate section label.
     * @param template name.
     *
     * @throws IOException If any.
     */
    private static void processTemplate(final String use, final String template) throws IOException {
        StringBuilder sbin = new StringBuilder("<!DOCTYPE html>\n"
                                               + "<!--\n"
                                               + "Generated by ${program.title}\n"
                                               + "version: ${program.version}\n"
                                               + "on ${system.date}\n"
                                               + "-->\n");

        IniDocument iniDoc = conf.iniDoc;

        Path docRootPath = of(iniDoc.getString("project", "root", ""))
                .resolve(iniDoc.getString("document", "docRootDir", "")).toAbsolutePath();

        Path templatesPath = docRootPath.resolve(iniDoc.getString("document", "templatesDir", ""))
                .resolve(template).toAbsolutePath();

        //
        // Set the 'base' path.  <base href="<base>">
        //
        Path srcPath = of(iniDoc.getString("page", "srcFile", ""));
        Path basePath = srcPath.getParent().relativize(docRootPath);
        iniDoc.setString("page", "base", basePath.toString());

        if (vlevel >= 2)
        {
            System.err.println("base:\n" + basePath);
            System.err.println("srcFile:\n" + srcPath);
            System.err.println("template:\n" + templatesPath);
        }

        try ( BufferedReader inReader = Files.newBufferedReader(templatesPath))
        {
            String line;

            while ((line = inReader.readLine()) != null)
            {
                sbin.append(line).append("\n");
            }
        }

        TextEditor textEd = new TextEditor(processSubstitutions(sbin.toString(), use, new BooleanReturn()));
        textEd.replaceAllLiteral("\\\\\\$", "$");
        textEd.replaceAllLiteral("\\\\\\[", "[");

        if (!basePath.toString().isBlank())
        {
            //
            // Set the detination file path.
            //
            Path destPath = of(iniDoc.getString("page", "destFile", null));
            Path destDirPath = of(iniDoc.getString("page", "destDir", null));
            String destHTML = destDirPath.relativize(destPath).toString();

            if (vlevel >= 3)
            {
                System.err.println("destPath:\n" + destPath);
                System.err.println("destDirPath:\n" + destDirPath);
                System.err.println("destHTML:\n" + destHTML);
            }

            Pattern p = compile("href\\=\"(?<ref>#[^\"]*)?\"");

            textEd.replaceAll(p, (Matcher m) ->
                      {
                          String text = m.group();
                          String ref = m.group("ref");

                          if (vlevel >= 3)
                          {
                              System.err.println("text: " + text);
                              System.err.println("ref: " + ref);
                          }

                          return text.replace(ref, destHTML + ref);
                      });

        }

        iniDoc.setString("page", "html", textEd.toString());
    }

    /**
     * Initialize the wrapper directories and files.
     *
     * @param docRootPath The document root directory.
     *
     * @return Always '0'.
     *
     * @throws IOException            If any.
     * @throws IniFileFormatException If any.
     * @throws URISyntaxException     If any.
     *
     * @since 0.1
     * @version 1.0.7
     */
    static int initialiseWrappers(final Path docRootPath)
            throws IOException, IniFileFormatException, URISyntaxException {

        if (vlevel >= 3)
        {
            System.out.println("docRootPath:\n" + docRootPath);
        }

        // Create directories.
        if (Files.notExists(docRootPath))
        {
            Files.createDirectories(docRootPath);
        }

        // Get source directory from jar file.
        Path srcDirPath = getResource(Cli.class, "/docs/init").toAbsolutePath();

        if (vlevel >= 2)
        {
            System.err.println("srcDirPath: " + srcDirPath);
            System.err.println("srcDirPath exists: " + Files.exists(srcDirPath));
        }

        // Get source ini file from jar file.
        Path srcIniPath = getResource(Cli.class, "/" + CONF_FILENAME + "").toAbsolutePath();

        if (vlevel >= 2)
        {
            System.err.println("srcIniPath: " + srcIniPath);
            System.err.println("srcIniPath exists: " + Files.exists(srcIniPath));
        }

        // Get destination ini file path.
        Path destIniPath = of(docRootPath.toString(), CONF_FILENAME);

        if (vlevel >= 2)
        {
            System.err.println("destIniPath: " + destIniPath);
            System.err.println("destIniPath exists: " + Files.exists(destIniPath));
        }

        copyDirTree(srcDirPath, docRootPath,
                    "*", vlevel, COPY_ATTRIBUTES, REPLACE_EXISTING);

        IniFile iniFile;

        // If there already exists an ini file, then...
        if (Files.exists(destIniPath))
        {
            if (vlevel >= 2)
            {
                System.err.println("destIniPath exists");
            }

            iniFile = new IniFile(srcIniPath).loadFile().mergeFile(destIniPath);
        } else
        {
            if (vlevel >= 2)
            {
                System.err.println("destIniPath dosen't exist");
            }

            iniFile = new IniFile(srcIniPath).loadFile();
        }

        iniFile.iniDoc.setString("document", "docRootDir", docRootPath.toString());
        iniFile.iniDoc.setString(null, "iniVersion", POM.version, "; DO NOT REMOVE/MOVE OR MODIFY: iniVersion!");

        if (vlevel >= 2)
        {
            System.err.println("document.docRootDir: " + iniFile.iniDoc.getString("document", "docRootDir", "default"));
        }

        iniFile.paddedEquals = true;

        iniFile.saveFileAs(destIniPath);

        return 0;
    }

    /**
     * Load the configuration file: {@link #CONF_FILENAME}
     *
     * @param srcDirPath Initial directory to look for the file.
     *
     * @throws IOException            If any.
     * @throws IniFileFormatException If any.
     */
    static void loadConf(final Path srcDirPath) throws IOException, IniFileFormatException {
        Path iniPath = of(CONF_FILENAME).toAbsolutePath();

        if (vlevel >= 3)
        {
            System.err.println("loadConf()");
        }

        if (vlevel >= 2)
        {
            System.err.println("iniPath: " + iniPath.toString());
        }

        if (Files.notExists(iniPath, NOFOLLOW_LINKS) && (srcDirPath != null))
        {
            Path srcPath = of(srcDirPath.toString());

            try
            {
                while (Files.notExists(srcPath.resolve(CONF_FILENAME), NOFOLLOW_LINKS))
                {
                    if (Files.exists(srcPath.resolve("pom.xml"), NOFOLLOW_LINKS))
                    {
                        throw new FileNotFoundException(CONF_FILENAME);
                    }

                    srcPath = srcPath.getParent();

                    if (vlevel >= 2)
                    {
                        System.err.println("srcPath: " + srcPath.toString());
                    }
                }
            } catch (NullPointerException ex)
            {
                throw new FileNotFoundException(CONF_FILENAME);
            }

            iniPath = srcPath.resolve(CONF_FILENAME);

            if (vlevel >= 2)
            {
                System.err.println("iniPath: " + iniPath.toString());
            }
        }

        conf = new IniFile(iniPath).loadFile();
        conf.iniDoc.setString("program", "artifactId", POM.artifactId, "# The identifier for this artifact that is unique within the group given by the groupID");
        conf.iniDoc.setString("program", "description", POM.description, "# Project description");
        conf.iniDoc.setString("program", "filename", POM.filename, "# The filename of the binary output files");
        conf.iniDoc.setString("program", "groupId", POM.groupId, "# Project GroupId");
        conf.iniDoc.setString("program", "title", POM.title, "# Project Name");
        conf.iniDoc.setString("program", "version", POM.version, "# The version of the artifact");
        conf.iniDoc.setString("program", "details", POM.toString(), "# All of the above information laid out");

        BooleanReturn brtn = new BooleanReturn();

        do
        {
            brtn.value = false;

            conf.iniDoc.getSections()
                    .forEach(section ->
                    {
                        for (IniProperty<String> prop : conf.iniDoc.getSection(section))
                        {
                            BooleanReturn rtn = new BooleanReturn();

                            if (prop.value() != null)
                            {
                                if (vlevel >= 3)
                                {
                                    System.out.println(prop);
                                }

                                String value = processSubstitutions(prop.value(), null, rtn);

                                if (rtn.value)
                                {
                                    conf.iniDoc.setString(section, prop.key(), value, prop.comment());
                                    brtn.value = true;
                                }
                            }
                        }
                    });
        } while (brtn.value);

        if (vlevel >= 2)
        {
            System.out.println(POM);
        }
    }

    /**
     * Process a markdown text file.
     *
     * @param inpPath     Path of source file (.md).
     * @param outPath     Path of destination files (.html).
     * @param destDirPath Path of the destination directory.
     * @param wrapper     Process wrapper files?
     *
     * @throws IOException If any.
     */
    static void processFile(final Path inpPath, final Path outPath, final Path destDirPath, final boolean wrapper) throws IOException {
        StringBuilder sb = new StringBuilder();
        IniDocument iniDoc = conf.iniDoc;
        String template = "";
        String use = "";

        try ( BufferedReader inReader = Files.newBufferedReader(inpPath))
        {
            String line;

            while ((line = inReader.readLine()) != null)
            {
                sb.append(line).append("\n");
            }
        }

        iniDoc.setString("page", "text", sb.toString());

        if (wrapper)
        {
            if (vlevel >= 3)
            {
                System.out.println("\n--------------------------------------------\n"
                                   + "process wrapper...");
            }

            processMetaBlock();
            configureStylesheetPaths();
            processNamedMetaBlocks();
            String preprocessed = processSubstitutions(
                    iniDoc.getString("page", "text", ""), use, new BooleanReturn());

            use = iniDoc.getString("page", "use", null);
            template = getString("page", "template", use);
            iniDoc.setString("page", "content",
                             MARKDOWN.markdown(processSubstitutions(
                                     preprocessed, use, new BooleanReturn())));

            if (!template.isBlank())
            {
                iniDoc.setString("page", "srcFile", inpPath.toString());
                iniDoc.setString("page", "destFile", outPath.toString());
                iniDoc.setString("page", "destDir", destDirPath.toString());
                processTemplate(use, template);
            }
        } else
        {
            iniDoc.setString("page", "content",
                             MARKDOWN.markdown(iniDoc.getString("page", "text", "")));

        }

        try ( BufferedWriter outWriter
                             = Files.newBufferedWriter(outPath, CREATE, TRUNCATE_EXISTING, WRITE))
        {
            if (vlevel >= 3)
            {
                System.out.println("\n--------------------------------------------\n"
                                   + "Write file: " + outPath);
                System.out.println("page.html:\n" + iniDoc.getString("page", "html", "No HTML content.")
                                   + "--------------------------------------------\n");
            }

            outWriter.write(iniDoc.getString("page", "html",
                                             iniDoc.getString("page", "content", "Error during processing.")));
        }
    }

    /**
     * Process substitutions.
     *
     * @param text Text to be processed.
     * @param use  Alternate section to use.
     *
     * @return result.
     */
    static String processSubstitutions(final String text, final String use, BooleanReturn found) {
        TextEditor textEd = new TextEditor(text);
        found.value = false;

        do
        {
            textEd.replaceAll(SUBSTITUTION_PATTERN, m ->
                      {

                          if (vlevel >= 3)
                          {
                              System.out.println("m.group: " + m.group());
                          }

                          String group = m.group("group");
                          String key = m.group("key");
                          String rtn = "ERROR: Substitution!";

                          if (group != null)
                          {
                              rtn = getString(group, key, use);
                          }

                          if (vlevel >= 3)
                          {
                              System.out.println("rtn: " + rtn);
                          }

                          return rtn;
                      });

            if (textEd.wasFound())
            {
                found.value = true;
            }
        } while (textEd.wasFound());

        return textEd.toString();
    }

    /**
     * Not meant to be instantiated.
     */
    private Cli() {
    }

    /**
     * A simple struct to return a boolean value through a method parameter.
     */
    public static class BooleanReturn {

        /**
         * The return value.
         */
        public boolean value = false;
    }
}
