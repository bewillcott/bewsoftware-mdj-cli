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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static com.bewsoftware.fileio.BEWFiles.copyDirTree;
import static java.lang.System.exit;
import static java.nio.file.Path.of;
import static java.nio.file.StandardCopyOption.COPY_ATTRIBUTES;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static com.bewsoftware.mdj.cli.Cli.conf;
import static com.bewsoftware.mdj.cli.Cli.createJarFile;
import static com.bewsoftware.mdj.cli.Cli.initialiseWrappers;
import static com.bewsoftware.mdj.cli.Cli.loadConf;
import static com.bewsoftware.mdj.cli.Cli.processFile;
import static com.bewsoftware.mdj.cli.Cli.vlevel;
import static com.bewsoftware.mdj.cli.Find.getUpdateList;

/**
 *
 * @author <a href="mailto:bw.opensource@yahoo.com">Bradley Willcott</a>
 *
 * @since 0.1
 * @version 1.0.7
 */
public class Main {

    private static final String SYNTAX = "mdj-cli [OPTION]...\n\noptions:";
    private static final String HELP_HEADER = "\n" + Cli.POM.description + "\n\n";
    private static final String HELP_FOOTER = "\n" + Cli.POM.title + " " + Cli.POM.version
                                              + "\nCopyright (c) 2020 Bradley Willcott\n\n";

    /**
     * Executed from command-line.
     *
     * @param args the command line arguments
     *
     * @throws IOException
     * @throws InvalidParameterValueException
     * @throws IniFileFormatException
     * @throws URISyntaxException
     */
    public static void main(String[] args)
            throws IOException, InvalidParameterValueException,
                   IniFileFormatException, InvalidProgramStateException,
                   URISyntaxException {

        exit(execute(args));
    }

    /**
     * Called from either {@link #main(java.lang.String[]) main()} or another process.
     *
     * @param args Arguments to configure this cycle of processing.
     *
     * @return The exit code.
     *
     * @throws IOException            If any.
     * @throws URISyntaxException     If any.
     * @throws IniFileFormatException If any.
     *
     * @since 1.0.4
     * @version 1.0.4
     */
    @SuppressWarnings("fallthrough")
    public static int execute(String[] args)
            throws IOException, URISyntaxException, IniFileFormatException {

        // Process command-line
        CmdLine cmd = new MyCmdLine(args);

        // check whether the command line was valid, and if it wasn't,
        // display usage information and exit.
        if (!cmd.success() || cmd.hasOption('h'))
        {
            StringBuilder sb = new StringBuilder();

            // print out specific error messages describing the problems
            // with the command line, THEN print usage, THEN print full
            // help.  This is called "beating the user with a clue stick."
            cmd.exceptions().forEach(ex -> sb.append(ex).append('\n'));

            cmd.printHelp(sb.toString(), SYNTAX, HELP_HEADER, HELP_FOOTER, true);

            if (cmd.hasOption('h'))
            {
                return 0;
            } else
            {
                return -1;
            }
        }

        vlevel = cmd.verbosity();

        switch (vlevel)
        {
            case 3:
            case 2:
                System.err.println("input: |" + cmd.inputFile() + "|");
                System.err.println("output: |" + cmd.outputFile() + "|");
                System.err.println("source: |" + cmd.source() + "|");
                System.err.println("destination: |" + cmd.destination() + "|");
                System.err.println("recursive: |" + cmd.isRecursive() + "|");
                System.err.println("wrapper: |" + cmd.isWrapping() + "|");
                System.err.println("initialise: |" + cmd.initialize() + "|");
                System.err.println("docRootDir: |" + cmd.docRootPath() + "|");
                System.err.println("jar: |" + cmd.jar() + "|");
                System.err.println("jarFilename: |" + cmd.jarFile() + "|");
                System.err.println("jarSrcDir: |" + cmd.jarSourcePath() + "|");

            case 1:
                System.err.println("verbose: |" + cmd.isVerbose() + "|");
                System.err.println("verbose level: |" + vlevel + "|");
                break;

            default:
        }

        // '-j' jar file creation
        if (cmd.jar())
        {
            if (cmd.inputFile() != null
                || cmd.outputFile() != null
                || cmd.source() != null
                || cmd.destination() != null
                || cmd.isRecursive()
                || cmd.initialize())
            {
                String msg = "Too many switches for \"-j\"\n\n";

                cmd.printHelp(msg, SYNTAX, HELP_HEADER, HELP_FOOTER, true);
                return 5;
            }

            return createJarFile(cmd.jarFile(), cmd.jarSourcePath(), vlevel);
        }

        // '-W' initialise wrapper functionality
        if (cmd.initialize())
        {
            if (cmd.inputFile() != null
                || cmd.outputFile() != null
                || cmd.source() != null
                || cmd.destination() != null
                || cmd.isRecursive()
                || cmd.jar())
            {
                String msg = "Too many switches for \"-W\"\n\n";

                cmd.printHelp(msg, SYNTAX, HELP_HEADER, HELP_FOOTER, true);
                return 6;
            }

            return initialiseWrappers(cmd.docRootPath());
        }

        // if '-w' switch active, copy 'css' files to destination directory
        if (cmd.isWrapping())
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
                List< IniProperty<Object>> props = conf.iniDoc.getSection("includeDirs");

                for (IniProperty<Object> prop : props)
                {
                    String value = (String) prop.value();

                    if (value != null)
                    {
                        value = Cli.processSubstitutions(value, null);

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

        // Get files to process
        List<Path[]> fileList = getUpdateList(cmd.source(),
                                              cmd.destination(),
                                              cmd.inputFile(), null, cmd.isRecursive(), vlevel);

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
                    System.err.println(filePairs[0]);
                    System.err.println("    " + filePairs[1]);
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
                    System.err.println("    " + dir);
                }

                Files.createDirectories(dir);
            }

            for (Path[] filePairs : fileList)
            {
                processFile(filePairs[0], filePairs[1], cmd.isWrapping());
            }
        }

        return 0;
    }
}
