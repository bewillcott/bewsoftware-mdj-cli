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

import com.bewsoftware.common.InvalidParameterValueException;
import com.bewsoftware.common.InvalidProgramStateException;
import com.bewsoftware.fileio.ini.IniFile;
import com.bewsoftware.fileio.ini.IniFileFormatException;
import com.bewsoftware.property.IniProperty;
import com.bewsoftware.utils.struct.BooleanReturn;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static com.bewsoftware.fileio.BEWFiles.copyDirTree;
import static com.bewsoftware.mdj.cli.Cli.conf;
import static com.bewsoftware.mdj.cli.Cli.initialiseWrappers;
import static com.bewsoftware.mdj.cli.Cli.loadConf;
import static com.bewsoftware.mdj.cli.Cli.processFile;
import static com.bewsoftware.mdj.cli.Cli.vlevel;
import static com.bewsoftware.mdj.cli.Find.getUpdateList;
import static com.bewsoftware.mdj.cli.Jar.createJarFile;
import static java.lang.System.exit;
import static java.nio.file.Path.of;
import static java.nio.file.StandardCopyOption.COPY_ATTRIBUTES;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

/**
 *
 * @author <a href="mailto:bw.opensource@yahoo.com">Bradley Willcott</a>
 *
 * @since 0.1
 * @version 1.1.0
 */
public class Main {

    private static final String COPYRIGHT
                                = " This is the MDj Command-line Interface program (aka: mdj-cli).\n"
                                  + "\n"
                                  + " Copyright (C) 2020 Bradley Willcott\n"
                                  + "\n"
                                  + " mdj-cli is free software: you can redistribute it and/or modify\n"
                                  + " it under the terms of the GNU General Public License as published by\n"
                                  + " the Free Software Foundation, either version 3 of the License, or\n"
                                  + " (at your option) any later version.\n"
                                  + "\n"
                                  + " mdj-cli is distributed in the hope that it will be useful,\n"
                                  + " but WITHOUT ANY WARRANTY; without even the implied warranty of\n"
                                  + " MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the\n"
                                  + " GNU General Public License for more details.\n"
                                  + "\n"
                                  + " You should have received a copy of the GNU General Public License\n"
                                  + " along with this program.  If not, see <http://www.gnu.org/licenses/>.\n";
    private static final String HELP_FOOTER = "\nYou must use at least one of the following options:"
                                              + "\n\t'-a', '-c', -i', '-s', '-j', '-m', '-W', or '-h|--help'\n"
                                              + "\n" + Cli.POM.name + " " + Cli.POM.version
                                              + "\nCopyright (c) 2020 Bradley Willcott\n"
                                              + "\nThis program comes with ABSOLUTELY NO WARRANTY; for details use option '-c'."
                                              + "\nThis is free software, and you are welcome to redistribute it"
                                              + "\nunder certain conditions; use option '-m', then goto the License page for details.";
    private static final String HELP_HEADER = "\n" + Cli.POM.description + "\n\n";

    private static final String SYNTAX = "java -jar /path/to/mdj-cli-<version>.jar [OPTION]...\n\noptions:";

    /**
     * Called from either {@link #main(java.lang.String[]) main()} or another process.
     *
     * @param args Arguments to configure this cycle of processing.
     *
     * @return The exit code.
     *
     * @throws IOException            if any.
     * @throws URISyntaxException     if any.
     * @throws IniFileFormatException if any.
     *
     * @since 1.0.4
     * @version 1.0.26
     */
    @SuppressWarnings("fallthrough")
    public static int execute(String[] args)
            throws IOException, URISyntaxException, IniFileFormatException {

        //
        // Process command-line
        //
        CmdLine cmd = new MyCmdLine(args);

        // check whether the command line was valid, and if it wasn't,
        // display usage information and exit.
        if (!cmd.success())
        {

            StringBuilder sb = new StringBuilder();

            // print out specific error messages describing the problems
            // with the command line, THEN print usage, THEN print full
            // help.  This is called "beating the user with a clue stick."
            cmd.exceptions().forEach(ex -> sb.append(ex).append('\n'));

            cmd.printHelp(sb.toString(), SYNTAX, HELP_HEADER, HELP_FOOTER, true);
            return -1;
        }

        //
        // if '-h' or '--help' then, display help message.
        //
        // returns 0.
        //
        if (cmd.hasOption('h'))
        {
            cmd.printHelp(SYNTAX, HELP_HEADER, HELP_FOOTER, true);
            return 0;
        }

        //
        // if '-c' then, display Copyright notice.
        //
        // returns 0.
        //
        if (cmd.hasOption('c'))
        {
            System.out.println(COPYRIGHT);
            return 0;
        }

        //
        // if '-m' then, display manual.
        //
        // returns 0.
        //
        if (cmd.hasOption('m'))
        {
            System.out.println("Displaying manual...");
            MCHttpServer.execute(cmd);
            return 0;
        }

        //
        // if '-p' then, publish http files from either: jarFile or docRootPath.
        //
        // returns 0.
        //
        if (cmd.hasOption('p'))
        {
            System.out.println("Publishing files...");
            MCHttpServer.execute(cmd);
            return 0;
        }

        //
        // if '-i' switch active, check and set others as necessary.
        //
        if (cmd.hasOption('i'))
        {
            String filename = cmd.inputFile().getName();
            int dotIdx = filename.lastIndexOf('.');

            if (dotIdx <= 0 || dotIdx == filename.length() - 1)
            {

            }

            String parent = cmd.inputFile().getParent();

            if (parent != null)
            {
                cmd.inputFile(new File(cmd.inputFile().getName()));
            }

            Path srcPath = of(parent != null ? parent : "");

            if (!cmd.hasOption('s'))
            {
                cmd.source(srcPath);
            }

            if (!cmd.hasOption('d'))
            {
                cmd.destination(srcPath);
            }

            if (!cmd.hasOption('o'))
            {
                File outFile = new File(cmd.inputFile().getName().replace(".md", ".html"));
                cmd.outputFile(outFile);
            }
        }

        //
        // if '-s' switch active, check and set others as necessary.
        //
        if (cmd.hasOption('s'))
        {
            if (!cmd.hasOption('d'))
            {
                cmd.destination(cmd.source());
            }
        }

        vlevel = cmd.verbosity();

        switch (vlevel)
        {
            case 3:
            case 2:
                System.out.println("input: |" + cmd.inputFile() + "|");
                System.out.println("output: |" + cmd.outputFile() + "|");
                System.out.println("source: |" + cmd.source() + "|");
                System.out.println("destination: |" + cmd.destination() + "|");
                System.out.println("recursive: |" + cmd.hasOption('r') + "|");
                System.out.println("wrapper: |" + cmd.hasOption('w') + "|");
                System.out.println("initialise: |" + cmd.hasOption('W') + "|");
                System.out.println("docRootDir: |" + cmd.docRootPath() + "|");
                System.out.println("jar: |" + cmd.hasOption('j') + "|");
                System.out.println("jarFilename: |" + cmd.jarFile() + "|");
                System.out.println("jarSrcDir: |" + cmd.jarSourcePath() + "|");
                System.out.println("pomFile: |" + cmd.pomFile() + "|");

                if (cmd.hasOption('D'))
                {
                    cmd.getOptionProperties('D').forEach((key, value)
                            -> System.out.println(key + ": |" + value + "|")
                    );
                }

            case 1:
                System.out.println("verbose: |" + cmd.hasOption('v') + "|");
                System.out.println("verbose level: |" + vlevel + "|");
                break;

            default:
        }

//        //
//        // '-a' jar file creation
//        //
//        // returns 0 - successs
//        //         3 - fail.
//        //
//        if (cmd.hasOption('a'))
//        {
//            if (cmd.hasOption('i')
//                || cmd.hasOption('j')
//                || cmd.hasOption('o')
//                || cmd.hasOption('s')
//                || cmd.hasOption('d')
//                || cmd.hasOption('p')
//                || cmd.hasOption('r')
//                || cmd.hasOption('W')
//                || cmd.hasOption('w'))
//            {
//                String msg = "Too many switches for \"-a\"\n\n";
//
//                cmd.printHelp(msg, SYNTAX, HELP_HEADER, HELP_FOOTER, true);
//                return 3;
//            }
//
//            // Load configuration file data
//            try
//            {
//                loadConf(cmd.docRootPath());
//            } catch (FileNotFoundException ex)
//            {
//                if (vlevel >= 2)
//                {
//                    System.out.println(ex);
//                }
//            }
//
//            return createJarFile(cmd.jarFile(), cmd.jarSourcePath(), vlevel);
//        }
//
        //
        // '-j' jar file creation
        //
        // returns 0 - successs
        //         5 - fail.
        //
        if (cmd.hasOption('j'))
        {
            if (cmd.hasOption('i')
                || cmd.hasOption('o')
                || cmd.hasOption('s')
                || cmd.hasOption('d')
                || cmd.hasOption('p')
                || cmd.hasOption('r')
                || cmd.hasOption('W')
                || cmd.hasOption('w'))
            {
                String msg = "Too many switches for \"-j\"\n\n";

                cmd.printHelp(msg, SYNTAX, HELP_HEADER, HELP_FOOTER, true);
                return 5;
            }

            // Load configuration file data
            try
            {
                loadConf(cmd.docRootPath());
            } catch (FileNotFoundException ex)
            {
                if (vlevel >= 2)
                {
                    System.out.println(ex);
                }
            }

            return createJarFile(cmd.jarFile(), cmd.jarSourcePath(), vlevel);
        }

        //
        // '-W' initialise wrapper functionality
        //
        // returns 0.
        //
        if (cmd.hasOption('W'))
        {
            if (cmd.hasOption('i')
                || cmd.hasOption('o')
                || cmd.hasOption('s')
                || cmd.hasOption('d')
                || cmd.hasOption('p')
                || cmd.hasOption('r')
                || cmd.hasOption('w'))
            {
                String msg = "Too many switches for \"-W\"\n\n";

                cmd.printHelp(msg, SYNTAX, HELP_HEADER, HELP_FOOTER, true);
                return 6;
            }

            return initialiseWrappers(cmd.docRootPath());
        }

        //
        // if '-P' Add pom.xml file
        //
        if (cmd.hasOption('P'))
        {
            Properties props = null;

            if (cmd.hasOption('D'))
            {
                props = cmd.getOptionProperties('D');
            }

            Cli.loadPom(cmd.pomFile(), props);
        }

        //
        // if '-w' switch active, copy filesin '[includeDirs]' to destination directory
        //
        if (cmd.hasOption('w'))
        {
            // Load configuration file data
            Path srcDir = of("");

            if (cmd.source() != null)
            {
                srcDir = cmd.source();
            } else if (cmd.inputFile() != null)
            {
                Path inpPath = of(cmd.inputFile().toString()).getParent();

                if (inpPath != null)
                {
                    srcDir = inpPath;
                }
            }

            try
            {
                loadConf(srcDir);
            } catch (FileNotFoundException ex)
            {
                String msg = ex.toString()
                             + "\nHave you initialised the wrapper functionality? '-W <Doc Root Dir>'\n";
                cmd.printHelp(msg, SYNTAX, HELP_HEADER, HELP_FOOTER, true);
                return 4;
            }

            if (conf.iniDoc.containsSection("includeDirs"))
            {
                List<IniProperty<String>> props = conf.iniDoc.getSection("includeDirs");

                for (IniProperty<String> prop : props)
                {
                    String value = prop.value();

                    if (value != null)
                    {
                        value = Cli.processSubstitutions(value, null, new BooleanReturn());

                        if (!value.isEmpty())
                        {
                            copyDirTree(cmd.source() + "/" + value, cmd.destination() + "/" + value, "*",
                                        vlevel, COPY_ATTRIBUTES, REPLACE_EXISTING);
                        }
                    }
                }
            }

        } else
        {
            conf = new IniFile();
        }

        conf.iniDoc.setString("system", "date", new Date().toString());

        //
        // Get files to process
        //
        List<Path[]> fileList = getUpdateList(cmd.source(),
                                              cmd.destination(),
                                              cmd.inputFile(), null, cmd.hasOption('r'), vlevel);

        // Process files
        if (!fileList.isEmpty())
        {
            if (cmd.outputFile() != null && fileList.size() == 1)
            {
                Path outPath = cmd.outputFile().toPath();

                if (outPath.isAbsolute())
                {
                    fileList.get(0)[1] = outPath;
                } else
                {
                    Path target = fileList.get(0)[1].getParent();
                    fileList.get(0)[1] = target.resolve(outPath);
                }
            }

            Set<Path> dirs = new TreeSet<>();

            fileList.forEach(filePairs ->
            {
                if (vlevel >= 1)
                {
                    System.out.println(filePairs[0]);
                    System.out.println("    " + filePairs[1]);
                }

                Path parent = filePairs[1].getParent();

                if (parent != null)
                {
                    dirs.add(parent);
                }
            });

            for (Path dir : dirs)
            {
                if (vlevel >= 2)
                {
                    System.out.println("    " + dir);
                }

                Files.createDirectories(dir);
            }

            for (Path[] filePairs : fileList)
            {
                processFile(filePairs[0], filePairs[1], cmd.destination(), cmd.hasOption('w'));
            }
        }

        return 0;
    }

    /**
     * Executed from command-line.
     *
     * @param args the command line arguments
     *
     * @throws IOException                    if any.
     * @throws InvalidParameterValueException if any.
     * @throws IniFileFormatException         if any.
     * @throws URISyntaxException             if any.
     * @throws InterruptedException           if any.
     */
    public static void main(String[] args)
            throws IOException, InvalidParameterValueException,
                   IniFileFormatException, InvalidProgramStateException,
                   URISyntaxException, InterruptedException {

        exit(execute(args));
    }
}
