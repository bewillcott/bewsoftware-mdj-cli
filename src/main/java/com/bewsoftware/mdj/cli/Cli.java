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
import com.bewsoftware.mdj.core.MarkdownProcessor;
import com.bewsoftware.mdj.core.TextEditor;
import com.bewsoftware.property.IniProperty;
import com.bewsoftware.utils.struct.Ref;
import java.io.*;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import static com.bewsoftware.fileio.BEWFiles.copyDirTree;
import static com.bewsoftware.fileio.BEWFiles.getResource;
import static com.bewsoftware.mdj.cli.MCPOMProperties.INSTANCE;
import static com.bewsoftware.mdj.cli.Main.DISPLAY;
import static java.nio.file.LinkOption.NOFOLLOW_LINKS;
import static java.nio.file.Path.of;
import static java.nio.file.StandardCopyOption.COPY_ATTRIBUTES;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static java.nio.file.StandardOpenOption.WRITE;
import static java.util.regex.Pattern.DOTALL;
import static java.util.regex.Pattern.MULTILINE;
import static java.util.regex.Pattern.compile;

/**
 *
 * @author <a href="mailto:bw.opensource@yahoo.com">Bradley Willcott</a>
 *
 * @since 0.1
 * @version 1.0.14
 */
public class Cli
{

    /**
     * The name of the configuration INI file.
     */
    public static final String CONF_FILENAME = "mdj-cli.ini";

    /**
     * The single instance of the {@link POMProperties} class.
     */
    public static final MCPOMProperties POM = INSTANCE;

    /**
     * The configuration INI file.
     */
    public static IniFile conf;

    /**
     * The verbosity level.
     */
    public static int vlevel;

    private static final String STYLESHEET_EXTN = ".css";

    private static final Pattern SUBSTITUTION_PATTERN
            = Pattern.compile("(?<!\\\\)(?:\\$\\{(?<group>\\w+)[.](?<key>\\w+)\\})");

    /**
     * Not meant to be instantiated.
     */
    private Cli()
    {
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
    public static int initialiseWrappers(final Path docRootPath)
            throws IOException, IniFileFormatException, URISyntaxException
    {

        if (vlevel >= 3)
        {
            DISPLAY.println("docRootPath:\n" + docRootPath);
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
            DISPLAY.println("srcDirPath: " + srcDirPath);
            DISPLAY.println("srcDirPath exists: " + Files.exists(srcDirPath));
        }

        // Get source ini file from jar file.
        Path srcIniPath = getResource(Cli.class, "/" + CONF_FILENAME + "").toAbsolutePath();

        if (vlevel >= 2)
        {
            DISPLAY.println("srcIniPath: " + srcIniPath);
            DISPLAY.println("srcIniPath exists: " + Files.exists(srcIniPath));
        }

        // Get destination ini file path.
        Path destIniPath = of(docRootPath.toString(), CONF_FILENAME);

        if (vlevel >= 2)
        {
            DISPLAY.println("destIniPath: " + destIniPath);
            DISPLAY.println("destIniPath exists: " + Files.exists(destIniPath));
        }

        copyDirTree(srcDirPath, docRootPath,
                "*", vlevel, COPY_ATTRIBUTES, REPLACE_EXISTING);

        IniFile iniFile;

        // If there already exists an ini file, then...
        if (Files.exists(destIniPath))
        {
            if (vlevel >= 2)
            {
                DISPLAY.println("destIniPath exists");
            }

            iniFile = new IniFile(srcIniPath).loadFile().mergeFile(destIniPath);
        } else
        {
            if (vlevel >= 2)
            {
                DISPLAY.println("destIniPath dosen't exist");
            }

            iniFile = new IniFile(srcIniPath).loadFile();
        }

        iniFile.iniDoc.setString("document", "docRootDir", docRootPath.toString());
        iniFile.iniDoc.setString(null, "iniVersion", POM.version, "; DO NOT REMOVE/MOVE OR MODIFY: iniVersion!");

        if (vlevel >= 2)
        {
            DISPLAY.println("document.docRootDir: " + iniFile.iniDoc.getString("document", "docRootDir", "default"));
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
    public static void loadConf(final Path srcDirPath) throws IOException, IniFileFormatException
    {
        Path iniPath = of(CONF_FILENAME).toAbsolutePath();

        if (vlevel >= 3)
        {
            DISPLAY.println("loadConf()");
        }

        if (vlevel >= 2)
        {
            DISPLAY.println("iniPath: " + iniPath.toString());
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
                        DISPLAY.println("srcPath: " + srcPath.toString());
                    }
                }
            } catch (NullPointerException ex)
            {
                throw new FileNotFoundException(CONF_FILENAME);
            }

            iniPath = srcPath.resolve(CONF_FILENAME);

            if (vlevel >= 2)
            {
                DISPLAY.println("iniPath: " + iniPath.toString());
            }
        }

        if (conf == null)
        {
            conf = new IniFile(iniPath).loadFile();
        } else
        {
            conf.mergeFile(iniPath);
        }

        conf.iniDoc.setString("program", "artifactId", POM.artifactId, "# The identifier for this artifact that is unique within the group given by the groupID");
        conf.iniDoc.setString("program", "description", POM.description, "# Project description");
        conf.iniDoc.setString("program", "filename", POM.filename, "# The filename of the binary output file");
        conf.iniDoc.setString("program", "groupId", POM.groupId, "# Project GroupId");
        conf.iniDoc.setString("program", "name", POM.name, "# Project Name");
        conf.iniDoc.setString("program", "version", POM.version, "# The version of the artifact");
        conf.iniDoc.setString("program", "details", POM.toString(), "# All of the above information laid out");

        Ref<Boolean> brtn = Ref.val();

        do
        {
            brtn.val = false;

            conf.iniDoc.getSections()
                    .forEach(section -> conf.iniDoc.getSection(section)
                    .forEach(prop ->
                    {
                        Ref<Boolean> rtn = Ref.val();
                        if (prop.value() != null)
                        {
                            if (vlevel >= 3)
                            {
                                DISPLAY.println(prop);
                            }
                            String value = processSubstitutions(prop.value(), null, rtn);
                            if (rtn.val)
                            {
                                conf.iniDoc.setString(section, prop.key(), value, prop.comment());
                                brtn.val = true;
                            }
                        }
                    }));

        } while (brtn.val);

        if (vlevel >= 2)
        {
            DISPLAY.println(POM);
        }
    }

    /**
     * Load the {@code pom.xml} file.
     *
     * @param pomFile The {@code pom.xml} file.
     * @param props   Additional properties to be added to "project" section.
     *
     * @throws IOException If any.
     */
    public static void loadPom(final File pomFile, final Properties props) throws IOException
    {

        SAXReader reader = new SAXReader();
        Document document = null;

        // Read file.
        try
        {
            document = reader.read(pomFile);
        } catch (DocumentException ex)
        {
            throw new IOException("\nError reading from '" + pomFile.getName() + "'", ex);
        }

        // root is 'project'.
        Element projectElement = document.getRootElement();

        if (conf == null)
        {
            conf = new IniFile();
        }

        Element groupId = projectElement.element("groupId");
        Element artifactId = projectElement.element("artifactId");
        Element version = projectElement.element("version");
        Element name = projectElement.element("name");
        Element description = projectElement.element("description");

        conf.iniDoc.setString("project", "groupId", groupId != null ? groupId.getTextTrim() : "", "# Project GroupId");
        conf.iniDoc.setString("project", "artifactId", artifactId != null ? artifactId.getTextTrim() : "", "# The identifier for this artifact that is unique within the group given by the groupID");
        conf.iniDoc.setString("project", "version", version != null ? version.getTextTrim() : "", "# The version of the artifact");
        conf.iniDoc.setString("project", "name", name != null ? name.getTextTrim() : "", "# Project Name");
        conf.iniDoc.setString("project", "description", description != null ? description.getTextTrim() : "", "# Project description");

        // Process -Dkey=value properties from commandline.
        // Add them to the "project" section.
        if (props != null)
        {
            props.forEach((keyObj, valueObj) ->
            {
                String key = (String) keyObj;
                String value = (String) valueObj;

                conf.iniDoc.setString("project", key, value);
            });
        }

        conf.iniDoc.getSection("project").forEach(prop
                -> DISPLAY.println("project." + prop.key() + ": " + prop.value())
        );
    }

    /**
     * Process substitutions.
     *
     * @param text  Text to be processed.
     * @param use   Alternate section to use.
     * @param found {@code true} if found.
     *
     * @return result.
     */
    public static String processSubstitutions(final String text, final String use, final Ref<Boolean> found)
    {
        TextEditor textEd = new TextEditor(text);
        found.val = false;

        do
        {
            textEd.replaceAll(SUBSTITUTION_PATTERN, m ->
            {
                if (vlevel >= 3)
                {
                    DISPLAY.println("m.group: " + m.group());
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
                    DISPLAY.println("rtn: " + rtn);
                }

                return rtn;
            });

            if (textEd.wasFound())
            {
                found.val = true;
            }
        } while (textEd.wasFound());

        return textEd.toString();
    }

    /**
     * Process the <i>use</i> section looking for filenames with a {@code .css}
     * extension.
     * <p>
     * Prepend the <i>document.cssDir</i> directory to each one.
     */
    private static void configureStylesheetPaths()
    {
        String use = conf.iniDoc.getString("page", "use", null);
        String cssDir = conf.iniDoc.getString("document", "cssDir", "");
        String systemDate = conf.iniDoc.getString("system", "date", null);

        systemDate = processSystemDate(systemDate);
        processStylesheetNamesfromTheInifileSection(use, systemDate, cssDir);
        processStylesheetNamesFromTheMetaBlock(systemDate, cssDir);
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
    private static String getString(final String section, final String key, final String use)
    {
        return conf.iniDoc.getString(section, key, conf.iniDoc.getString(use, key, ""));
    }

    /**
     * Process the file's meta block, if any.
     */
    private static void processMetaBlock()
    {
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
                DISPLAY.println("\n============================================\n"
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
                    DISPLAY.println("key = " + key + "\n"
                            + "value = " + value);
                }

                conf.iniDoc.setString("page", key, value);
            }

            if (vlevel >= 3)
            {
                DISPLAY.println("============================================\n");
            }

        }

        conf.iniDoc.setString("page", "text", text);
    }

    /**
     * Process Named Meta Blocks.
     * <p>
     * A named meta block looks like each of the following:
     * <hr>
     * <pre><code>
     *
     * &#64;&#64;&#64;[#name]
     *
     * Some text.
     * Some more text.
     *
     * &#64;&#64;&#64;
     * </code></pre>
     * <hr>
     * The HTML from the above code will begin with something like this:
     * {@code <div id="name">}.<br>
     * Therefore, you can set up formatting via CSS using the 'id' value:
     * "{@code #name}".
     * <hr>
     * <pre><code>
     *
     * &#64;&#64;&#64;[@name]
     *
     * Some text.
     * Some more text.
     *
     * &#64;&#64;&#64;
     * </code></pre>
     * <hr>
     * The HTML from the above code will begin with something like this:
     * {@code <div class="name">}.<br>
     * Therefore, you can set up formatting via CSS using the 'class' value:
     * "{@code .name}".
     * <p>
     * The blank line before the beginning is <b>required</b>.<br>
     * '{@code name}' is used to refer to the block in the markup text for
     * substitution: ${page.name}.
     * <p>
     * <b>Note:</b> The label "{@code name}" used throughout this comment is an
     * example only.
     * You would use your own label as appropriate to your requirements.
     * <p>
     * Bradley Willcott
     *
     * @since 14/12/2020
     */
    private static void processNamedMetaBlocks()
    {
        TextEditor text = new TextEditor(conf.iniDoc.getString("page", "text", ""));
        Pattern p = compile(
                "(?<=\\n)(?:@@@\\[(?<type>[@#])(?<name>\\w+)\\]\\n(?<metablock>.*?)\\n@@@\\n)",
                DOTALL);

        text.replaceAll(p, m ->
        {
            String type = m.group("type");
            String name = m.group("name");
            String metaBlock = m.group("metablock");

            String html = "";

            if ("#".equals(type))
            {
                html = "\n<div id=\"" + name + "\">\n";
            } else if ("@".equals(type))
            {
                html = "\n<div class=\"" + name + "\">\n";
            }

            html += MarkdownProcessor.convert(metaBlock) + "\n</div>\n";

            if (vlevel >= 3)
            {
                DISPLAY.println(
                        "============================================\n"
                        + "name: " + name + "\n"
                        + "metablock:\n" + html
                        + "\n============================================\n");
            }

            conf.iniDoc.setString("page", name, html);
            return "";
        });

        text.replaceAll("\\\\@@@", "@@@");

        conf.iniDoc.setString("page", "text", text.toString());
    }

    private static void processStylesheetNamesFromTheMetaBlock(String systemDate, String cssDir)
    {
        String done = conf.iniDoc.getString("page", systemDate, null);

        if (done == null)
        {
            // Process the '.css' filenames...
            List<IniProperty<String>> keys = conf.iniDoc.getSection("page");

            keys.stream()
                    .filter(key -> key.value().endsWith(STYLESHEET_EXTN))
                    .forEachOrdered(key
                            -> conf.iniDoc.setString("page", key.key(),
                            of(cssDir, key.value()).toString()));

            // Record that we have already processed the '.css' filenames.
            conf.iniDoc.setString("page", systemDate, "done");
        }
    }

    private static void processStylesheetNamesfromTheInifileSection(
            String use, String systemDate, String cssDir)
    {
        if (use != null && !use.isBlank())
        {
            String done = conf.iniDoc.getString(use, systemDate, null);

            if (done == null)
            {
                // Process the '.css' filenames...
                List<IniProperty<String>> keys = conf.iniDoc.getSection(use);

                keys.stream()
                        .filter(key -> key.value().endsWith(STYLESHEET_EXTN))
                        .forEachOrdered(key
                                -> conf.iniDoc.setString(use, key.key(),
                                of(cssDir, key.value()).toString(),
                                key.comment()));

                // Record that we have already processed the '.css' filenames.
                conf.iniDoc.setString(use, systemDate, "done");
            }
        }
    }

    private static String processSystemDate(String systemDate)
    {
        if (systemDate == null)
        {
            systemDate = new Date().toString();
            conf.iniDoc.setString("system", "date", systemDate);
        }

        return systemDate;
    }

    /**
     * Process a template.
     *
     * @param use      Alternate section label.
     * @param template name.
     *
     * @throws IOException If any.
     */
    private static void processTemplate(final String use, final String template) throws IOException
    {
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
            DISPLAY.println("base:\n" + basePath);
            DISPLAY.println("srcFile:\n" + srcPath);
            DISPLAY.println("template:\n" + templatesPath);
        }

        try (BufferedReader inReader = Files.newBufferedReader(templatesPath))
        {
            String line;

            while ((line = inReader.readLine()) != null)
            {
                sbin.append(line).append("\n");
            }
        }

        TextEditor textEd = new TextEditor(processSubstitutions(sbin.toString(), use, Ref.val()));
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
                DISPLAY.println("destPath:\n" + destPath);
                DISPLAY.println("destDirPath:\n" + destDirPath);
                DISPLAY.println("destHTML:\n" + destHTML);
            }

            Pattern p = compile("href\\=\"(?<ref>#[^\"]*)?\"");

            textEd.replaceAll(p, (Matcher m) ->
            {
                String text = m.group();
                String ref = m.group("ref");

                if (vlevel >= 3)
                {
                    DISPLAY.println("text: " + text);
                    DISPLAY.println("ref: " + ref);
                }

                return text.replace(ref, destHTML + ref);
            });

        }

        iniDoc.setString("page", "html", textEd.toString());
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
    public static void processFile(final Path inpPath, final Path outPath, final Path destDirPath,
            final boolean wrapper) throws IOException
    {
        StringBuilder sb = new StringBuilder();
        IniDocument iniDoc = conf.iniDoc;
        String template;
        String use = "";

        try (BufferedReader inReader = Files.newBufferedReader(inpPath))
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
                DISPLAY.println("\n--------------------------------------------\n"
                        + "process wrapper...");
            }

            processMetaBlock();
            configureStylesheetPaths();
            processNamedMetaBlocks();
            String preprocessed = processSubstitutions(
                    iniDoc.getString("page", "text", ""), use, Ref.val());

            use = iniDoc.getString("page", "use", null);
            template = getString("page", "template", use);
            iniDoc.setString("page", "content",
                    MarkdownProcessor.convert(processSubstitutions(preprocessed, use,
                            Ref.val())));

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
                    MarkdownProcessor.convert(iniDoc.getString("page", "text", "")));

        }

        try (BufferedWriter outWriter
                = Files.newBufferedWriter(outPath, CREATE, TRUNCATE_EXISTING, WRITE))
        {
            if (vlevel >= 3)
            {
                DISPLAY.println("\n--------------------------------------------\n"
                        + "Write file: " + outPath);
                DISPLAY.println("page.html:\n" + iniDoc.getString("page", "html",
                        "No HTML content.")
                        + "--------------------------------------------\n");
            }

            outWriter.write(iniDoc.getString("page", "html",
                    iniDoc.getString("page", "content", "Error during processing.")));
        }
    }

}
