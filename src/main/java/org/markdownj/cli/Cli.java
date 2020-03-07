/*
 * Copyright (C) 2020-2021 Bradley Willcott
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.markdownj.cli;

import com.bew.commons.InvalidParameterValueException;
import com.bew.commons.InvalidProgramStateException;
import com.bew.commons.fileio.IniDocument;
import com.bew.commons.fileio.IniFile;
import com.bew.commons.fileio.IniFileFormatException;
import com.martiansoftware.jsap.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.spi.FileSystemProvider;
import java.util.Collections;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.jar.Manifest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.markdownj.MarkdownProcessor;

import static com.martiansoftware.jsap.JSAP.INTEGER_PARSER;
import static com.martiansoftware.jsap.JSAP.NO_LONGFLAG;
import static com.martiansoftware.jsap.JSAP.STRING_PARSER;
import static java.nio.file.LinkOption.NOFOLLOW_LINKS;
import static java.nio.file.Path.of;
import static java.nio.file.StandardCopyOption.COPY_ATTRIBUTES;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static java.nio.file.StandardOpenOption.WRITE;
import static java.util.regex.Pattern.DOTALL;
import static java.util.regex.Pattern.MULTILINE;
import static org.markdownj.cli.Find.getFileList;
import static org.markdownj.cli.Jar.getManifest;
import static org.markdownj.cli.POMProperties.INSTANCE;

/**
 *
 * @author Bradley Willcott
 */
public class Cli {

    private static final String CONF_FILENAME = "markdownj-cli.ini";
    private static final MarkdownProcessor MARKDOWN = new MarkdownProcessor();
    private static final POMProperties POM = INSTANCE;
    static IniFile conf;
    static int vlevel;

    static int createJarFile(String jarFilename, String jarSrcDir, int vlevel) throws IOException {
        Path srcPath = of(jarSrcDir).toAbsolutePath();
        SortedSet<Path> fileList = getFileList(jarSrcDir, "*", true, vlevel);
        Manifest manifest = getManifest();

        Jar.createJAR(jarFilename, fileList, manifest);
        return 0;
    }

    private static Path getResource(String name) throws URISyntaxException, IOException {
        URI uri = Cli.class.getResource(name).toURI();

        if ("jar".equals(uri.getScheme())) {
            for (FileSystemProvider provider : FileSystemProvider.installedProviders()) {
                if (provider.getScheme().equalsIgnoreCase("jar")) {
                    try {
                        provider.getFileSystem(uri);
                    } catch (FileSystemNotFoundException e) {
                        // in this case we need to initialize it first:
                        provider.newFileSystem(uri, Collections.emptyMap());
                    }
                }
            }
        }

        return Paths.get(uri);
    }

    private static String getString(String use, String section, String key) {
        return conf.iniDoc.getString(section, key, conf.iniDoc.getString(use, key, ""));
    }

    static JSAP initialiseJSAP() throws JSAPException {
        JSAP jsap = new JSAP();

        // '-i' : Input filename/pattern
        FlaggedOption opt1 = new FlaggedOption("input")
                .setStringParser(STRING_PARSER)
                .setUsageName("file name")
                .setShortFlag('i')
                .setLongFlag(NO_LONGFLAG);

        opt1.setHelp("The markdown input file to parse. (default extn: \".md\")");
        jsap.registerParameter(opt1);

        // '-o' : Output filename/pattern
        FlaggedOption opt2 = new FlaggedOption("output")
                .setStringParser(STRING_PARSER)
                .setUsageName("file name")
                .setShortFlag('o')
                .setLongFlag(NO_LONGFLAG);

        opt2.setHelp("The HTML output file. (default extn: \".html\")");
        jsap.registerParameter(opt2);

        // '-s' : Source directory
        FlaggedOption opt3 = new FlaggedOption("source")
                .setStringParser(STRING_PARSER)
                .setUsageName("directory path")
                .setShortFlag('s')
                .setLongFlag(NO_LONGFLAG);

        opt3.setHelp("The source directory for markdown files.");
        jsap.registerParameter(opt3);

        // '-d' : Destination directory
        FlaggedOption opt4 = new FlaggedOption("destination")
                .setStringParser(STRING_PARSER)
                .setUsageName("directory path")
                .setShortFlag('d')
                .setLongFlag(NO_LONGFLAG);

        opt4.setHelp("The destination directory for HTML files.");
        jsap.registerParameter(opt4);

        // '-r' : Resursive directory processing
        Switch sw2 = new Switch("recursive")
                .setShortFlag('r')
                .setLongFlag(NO_LONGFLAG);

        sw2.setHelp("Recursively process directories.");
        jsap.registerParameter(sw2);

        // '-w' : Process meta block
        Switch sw4 = new Switch("wrapper")
                .setShortFlag('w')
                .setLongFlag(NO_LONGFLAG);

        sw4.setHelp("Process meta block, wrapping your document with templates and stylesheets.");
        jsap.registerParameter(sw4);

        // '-v' : Verbose processing
        QualifiedSwitch sw1 = (QualifiedSwitch) (new QualifiedSwitch("verbose")
                                                 .setStringParser(INTEGER_PARSER)
                                                 .setUsageName("verbose level [1-2]")
                                                 .setShortFlag('v')
                                                 .setLongFlag(NO_LONGFLAG));

//        sw1.setHelp("""
//                    Verbose processing.  List files as they are processed.
//                    Set verbose level with "-v:1" or "-v:2".
//                    "-v" defaults to level '1'""");
        jsap.registerParameter(sw1);

        // '-j' : Copy html output target directory into a new 'jar' file
//        FlaggedOption opt5 = new FlaggedOption("initialise")
        QualifiedSwitch sw5 = (QualifiedSwitch) (new QualifiedSwitch("jar")
                                                 .setStringParser(STRING_PARSER)
                                                 .setList(true)
                                                 .setListSeparator(';')
                                                 .setUsageName("jarOption")
                                                 .setShortFlag('j')
                                                 .setLongFlag(NO_LONGFLAG));

//        sw5.setHelp("""
//                    Copy html output target directory into a new 'jar' file.
//                    NOTE: Can NOT be used with any other switches, except "-v:n".""");
        jsap.registerParameter(sw5);

        // '-W' : Initialise wrapper directories and files
//        FlaggedOption opt6 = new FlaggedOption("initialise")
        QualifiedSwitch sw6 = (QualifiedSwitch) (new QualifiedSwitch("initialise")
                                                 .setStringParser(STRING_PARSER)
                                                 .setUsageName("document root directory")
                                                 .setShortFlag('W')
                                                 .setLongFlag(NO_LONGFLAG));

//        sw6.setHelp("""
//                    Initialise wrapper directories and files.
//                    NOTE: Can NOT be used with any other switches, except "-v:n".""");
        jsap.registerParameter(sw6);

        // '-h' or '--help' : Provide online help
        Switch sw3 = new Switch("help")
                .setShortFlag('h')
                .setLongFlag("help");

        sw3.setHelp("Provide this online help.");
        jsap.registerParameter(sw3);

        return jsap;
    }

    static int initialiseWrappers(String docRootDir) throws IOException, NullPointerException, InvalidParameterValueException, IniFileFormatException, InvalidProgramStateException, URISyntaxException {
        Path docRootPath = of(docRootDir).normalize().toAbsolutePath();

        if (Files.notExists(docRootPath)) {
            Files.createDirectories(docRootPath);
        }

        Path srcPath = getResource("/docs").toAbsolutePath();

        if (vlevel >= 2) {
            System.err.println("srcPath: " + srcPath);
            System.err.println("srcPath exists: " + Files.exists(srcPath));
        }

        Path iniPath = getResource("/" + CONF_FILENAME + "").toAbsolutePath();

        if (vlevel >= 2) {
            System.err.println("iniPath: " + iniPath);
            System.err.println("iniPath exists: " + Files.exists(iniPath));
        }

        Path iniPath2 = of(CONF_FILENAME).toAbsolutePath();

        if (vlevel >= 2) {
            System.err.println("iniPath2: " + iniPath2);
            System.err.println("iniPath2 exists: " + Files.exists(iniPath2));
        }

        com.bew.commons.fileio.Files.copyDirTree(srcPath.toAbsolutePath(), of(docRootDir).toAbsolutePath(),
                                                 "*.{html,css}", vlevel, COPY_ATTRIBUTES, REPLACE_EXISTING);

        if (Files.exists(iniPath2)) {
            if (vlevel >= 2) {
                System.err.println("iniPath2 exists");
            }

            IniFile dest = new IniFile(iniPath2).loadFile().mergeFile(iniPath);
            dest.iniDoc.setString("document", "docRootDir", docRootPath.toString());
            dest.paddedEquals = true;
            dest.saveFile();
        } else {
            if (vlevel >= 2) {
                System.err.println("iniPath2 dosen't exist");
            }

            IniFile src = new IniFile(iniPath).loadFile();
            src.iniDoc.setString("document", "docRootDir", docRootPath.toString());

            if (vlevel >= 2) {
                System.err.println("document.docRootDir:" + src.iniDoc.getString("document", "docRootDir", "default"));
            }

            src.paddedEquals = true;
            src.saveFileAs(iniPath2);
        }

        return 0;
    }

    static void loadConf(String srcDir) throws NullPointerException, InvalidParameterValueException,
                                               IOException, IniFileFormatException, InvalidProgramStateException {
        Path iniPath = of(CONF_FILENAME).toAbsolutePath();

        if (vlevel >= 2) {
            System.err.println("iniPath: " + iniPath.toString());
        }

        if (Files.notExists(iniPath, NOFOLLOW_LINKS) && (srcDir != null)) {
            Path srcPath = of(srcDir).toAbsolutePath();

            try {
                while (Files.notExists(srcPath.resolve(CONF_FILENAME), NOFOLLOW_LINKS)) {
                    srcPath = srcPath.getParent();

                    if (vlevel >= 2) {
                        System.err.println("srcPath: " + srcPath.toString());
                    }
                }
            } catch (NullPointerException ex) {
                throw new FileNotFoundException(CONF_FILENAME);
            }

            iniPath = srcPath.resolve(CONF_FILENAME);

            if (vlevel >= 2) {
                System.err.println("iniPath: " + iniPath.toString());
            }
        }

        conf = new IniFile(iniPath).loadFile();
        conf.iniDoc.setString("project", "root", iniPath.getParent().toString());
        conf.iniDoc.setString("program", "artifactId", POM.artifactId, "# The identifier for this artifact that is unique within the group given by the groupID");
        conf.iniDoc.setString("program", "description", POM.description, "# Project description");
        conf.iniDoc.setString("program", "filename", POM.filename, "# The filename of the binary output files");
        conf.iniDoc.setString("program", "groupId", POM.groupId, "# Project GroupId");
        conf.iniDoc.setString("program", "title", POM.title, "# Project Name");
        conf.iniDoc.setString("program", "version", POM.version, "# The version of the artifact");
        conf.iniDoc.setString("program", "details", POM.toString(), "# All of the above information laid out");

        if (vlevel >= 2) {
            System.out.println(POMProperties.INSTANCE);
        }
    }

    static void processFile(Path inpPath, Path outPath, boolean wrapper) throws IOException {
        SortedMap<String, String> meta;
        StringBuilder sb = new StringBuilder();
        IniDocument iniDoc = conf.iniDoc;
        String template = "";
        String stylesheet = "";
        String use = "";

        try (BufferedReader inReader = Files.newBufferedReader(inpPath)) {
            String line;

            while ((line = inReader.readLine()) != null) {
                sb.append(line).append("\n");
            }
        }

        iniDoc.setString("page", "text", sb.toString());

        if (wrapper) {
            processMetaBlock();
        }

        iniDoc.setString("page", "content", MARKDOWN.markdown(iniDoc.getString("page", "text", "")));

        if (wrapper) {
            use = iniDoc.getString("page", "use", null);
            template = getString(use, "page", "template");
            stylesheet = getString(use, "page", "stylesheet");

            if (!template.isBlank()) {
                iniDoc.setString("page", "srcFile", inpPath.toAbsolutePath().toString());
                processTemplate(use, template);
            }
        }

        try (BufferedWriter outWriter = Files.newBufferedWriter(outPath, CREATE, TRUNCATE_EXISTING, WRITE)) {
            outWriter.write(iniDoc.getString("page", "html", iniDoc.getString("page", "content", "Error during processing.")));
        }
    }

    private static void processMetaBlock() {
        String text = conf.iniDoc.getString("page", "text", "");
        Matcher m = Pattern.compile("\\A(?:@@@\\n(?<metablock>.*?)\\n@@@\\n)?(?<body>.*)\\z", DOTALL)
                .matcher(text);
        conf.iniDoc.removeSection("page");

        if (m.find()) {
            String metaBlock = m.group("metablock");
            text = m.group("body");

            Matcher m2 = Pattern.compile("^\\s*(?<key>\\w+)\\s*:\\s*(?<value>.*?)?\\s*?$", MULTILINE).matcher(metaBlock);
            StringBuilder sb = new StringBuilder();

            while (m2.find()) {
                String key = m2.group("key");
                String value = m2.group("value");

                conf.iniDoc.setString("page", key, value);
            }
        }

        conf.iniDoc.setString("page", "text", text);
    }

    private static String processSubstitution(String use, String text) {
        Pattern p = Pattern.compile("(?:\\\\(?<keep>\\$\\{\\w+[.]\\w+\\})|\\$\\{(?<group>\\w+)[.](?<key>\\w+)\\})");
        Matcher m = p.matcher(text);
        StringBuilder sb = new StringBuilder();
        int lastIndex = 0;
        boolean found = false;

        while (m.find()) {
            found = true;
            sb.append(text.subSequence(lastIndex, m.start()));

            String keep = m.group("keep");
            String group = m.group("group");
            String key = m.group("key");

            if (keep != null) {
                sb.append(keep);
            } else {
                sb.append(processSubstitution(use, getString(use, group, key)));
            }

            lastIndex = m.end();
        }

        sb.append(text.subSequence(lastIndex, text.length()));
        return sb.toString();
    }

    private static void processTemplate(String use, String template) throws IOException {
        StringBuilder sbin = new StringBuilder();

        Path docRootPath = of(conf.iniDoc.getString("project", "root", ""))
                .resolve(conf.iniDoc.getString("document", "docRootDir", "")).toAbsolutePath();

        Path templatesPath = docRootPath.resolve(conf.iniDoc.getString("document", "templatesDir", ""))
                .resolve(template).toAbsolutePath();

        // Relativize path to stylesheet
        Path stylesheetPath = docRootPath.resolve(conf.iniDoc.getString("document", "cssDir", ""))
                .resolve(getString(use, "page", "stylesheet")).toAbsolutePath();
        Path srcPath = of(conf.iniDoc.getString("page", "srcFile", ""));
        Path cssPath = srcPath.getParent().relativize(stylesheetPath);
        conf.iniDoc.setString("page", "stylesheet", cssPath.toString());

        if (vlevel >= 2) {
            System.err.println("template:\n" + templatesPath.toString());
        }

        try (BufferedReader inReader = Files.newBufferedReader(templatesPath)) {
            String line;

            while ((line = inReader.readLine()) != null) {
                sbin.append(line).append("\n");
            }
        }

        conf.iniDoc.setString("page", "html", processSubstitution(use, sbin.toString()));
    }

    static void provideUsageHelp(String msg, JSAP jsap) {
        System.err.println();

        if (msg != null && !msg.isBlank()) {
            System.err.println(msg);
        }

        System.err.println("Usage: java -jar " + POM.filename);
        System.err.println("            " + jsap.getUsage());
        System.err.println();

        // show full help as well
        System.err.println(jsap.getHelp());
    }

    private Cli() {
    }
}
