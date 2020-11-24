/*
 * This file is part of the Markdownj Command-line Interface program
 * (aka: markdownj-cli).
 *
 * Copyright (C) 2020 Bradley Willcott
 *
 * markdownj-cli is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * markdownj-cli is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.markdownj.cli;

import com.bewsoftware.common.InvalidParameterValueException;
import com.bewsoftware.common.InvalidProgramStateException;
import com.bewsoftware.fileio.ini.IniFile;
import com.bewsoftware.fileio.ini.IniFileFormatException;
import com.bewsoftware.property.IniProperty;
import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPException;
import com.martiansoftware.jsap.JSAPResult;
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
import static org.markdownj.cli.Cli.conf;
import static org.markdownj.cli.Cli.createJarFile;
import static org.markdownj.cli.Cli.initialiseJSAP;
import static org.markdownj.cli.Cli.initialiseWrappers;
import static org.markdownj.cli.Cli.loadConf;
import static org.markdownj.cli.Cli.processFile;
import static org.markdownj.cli.Cli.provideUsageHelp;
import static org.markdownj.cli.Cli.vlevel;
import static org.markdownj.cli.Find.getUpdateList;

/**
 *
 * @author <a href="mailto:bw.opensource@yahoo.com">Bradley Willcott</a>
 *
 * @since 0.1
 * @version 1.0
 */
public class Main {

//    private static final String CtlX = Character.toString(24);
    /**
     * @param args the command line arguments
     *
     * @throws java.io.IOException
     */
    @SuppressWarnings("fallthrough")
    public static void main(String[] args) throws IOException, JSAPException, InvalidParameterValueException, IniFileFormatException, InvalidProgramStateException, URISyntaxException {

        Set<Path> dirs = new TreeSet<>();
//        StringBuilder buf = new StringBuilder();
//        BufferedReader in = null;
//        Reader reader = null;

        // Process command-line
        JSAP jsap = initialiseJSAP();
        JSAPResult config = jsap.parse(args);

        // check whether the command line was valid, and if it wasn't,
        // display usage information and exit.
        if (!config.success() || config.getBoolean("help"))
        {
            StringBuilder sb = new StringBuilder();

            // print out specific error messages describing the problems
            // with the command line, THEN print usage, THEN print full
            // help.  This is called "beating the user with a clue stick."
            for (@SuppressWarnings("unchecked") Iterator<String> errs = (Iterator<String>) config.getErrorMessageIterator();
                 errs.hasNext();)
            {
                sb.append("Error: ").append(errs.next()).append("\n");
            }

            provideUsageHelp(sb.toString(), jsap);

            if (config.getBoolean("help"))
            {
                exit(0);
            } else
            {
                exit(1);
            }
        }

        // Get command-line data
        String input = config.getString("input");
        input = (input != null ? input.trim() : null);
        String output = config.getString("output");
        output = (output != null ? output.trim() : null);
        String source = config.getString("source");
        source = (source != null ? source.trim() : null);
        String destination = config.getString("destination");
        destination = (destination != null ? destination.trim() : null);
        boolean recursive = config.getBoolean("recursive");
        boolean wrapper = config.getBoolean("wrapper");
        boolean initialise = config.getBoolean("initialise");
        String[] dirArr = config.getStringArray("initialise");
        String docRootDir = (dirArr.length > 0 ? dirArr[0] : (initialise ? "" : null));

        boolean jar = config.getBoolean("jar");
        String[] jarOptions = config.getStringArray("jar");

        String jarFilename = (jarOptions.length >= 1 ? jarOptions[0] : "");
        String jarSrcDir = (jarOptions.length == 2 ? jarOptions[1] : "");

        boolean verbose = config.getBoolean("verbose");
        int[] vlevelarr = config.getIntArray("verbose");

        vlevel = (vlevelarr.length > 0 ? vlevelarr[0] : (verbose ? 1 : 0));

        switch (vlevel)
        {
            case 3:
            case 2:
                System.err.println("input: |" + input + "|");
                System.err.println("output: |" + output + "|");
                System.err.println("source: |" + source + "|");
                System.err.println("destination: |" + destination + "|");
                System.err.println("recursive: |" + recursive + "|");
                System.err.println("wrapper: |" + wrapper + "|");
                System.err.println("initialise: |" + initialise + "|");
                System.err.println("docRootDir: |" + docRootDir + "|");
                System.err.println("jar: |" + jar + "|");
                System.err.println("jarFilename: |" + jarFilename + "|");
                System.err.println("jarSrcDir: |" + jarSrcDir + "|");

            case 1:
                System.err.println("verbose: |" + verbose + "|");
                System.err.println("verbose level: |" + vlevel + "|");
                break;

            default:
        }

        // '-j' jar file creation
        if (jar)
        {
            if (input != null
                || output != null
                || source != null
                || destination != null
                || recursive != false
                || initialise != false)
            {
                String msg = "Too many switches for \"-j\"";

                provideUsageHelp(msg, jsap);
                exit(1);
            }

            exit(createJarFile(jarFilename, jarSrcDir, vlevel));
        }

        // '-W' initialise wrapper functionality
        if (initialise)
        {
            if (input != null
                || output != null
                || source != null
                || destination != null
                || recursive != false
                || jar != false)
            {
                String msg = "Too many switches for \"-W\"";

                provideUsageHelp(msg, jsap);
                exit(1);
            }

            exit(initialiseWrappers(docRootDir));
        }

        // TODO: Rewrite to process "includeDirs" section of conf.iniDoc.
        // if '-w' switch active, copy 'css' files to destination directory
        if (wrapper)
        {
            // Load configuration file data
            String srcDir = "";

            if (source != null)
            {
                srcDir = source;
            } else if (input != null)
            {
                Path inpPath = of(input).getParent();

                if (inpPath != null)
                {
                    srcDir = inpPath.toString();
                }
            }

            try
            {
                loadConf(srcDir);
            } catch (FileNotFoundException ex)
            {
                provideUsageHelp("ERROR: " + ex.toString()
                                 + "\nHave you initialised the wrapper functionality? '-W <Doc Root Dir>'\n", jsap);
                exit(1);
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
                            copyDirTree(source + "/" + value, destination + "/" + value, "*",
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
        List<Path[]> fileList = getUpdateList((source != null ? source : null),
                                              (destination != null ? destination : null),
                                              input, null, recursive, vlevel);

        // Process files
        if (fileList.size() > 0)
        {
            if (output != null && fileList.size() == 1)
            {
                Path outPath = of(output);

                if (outPath.isAbsolute())
                {
                    fileList.get(0)[1] = outPath;
                } else
                {
                    Path target = fileList.get(0)[1].getParent();
                    fileList.get(0)[1] = target.resolve(outPath);
                }
            }

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
                processFile(filePairs[0], filePairs[1], wrapper);
            }
        }
    }
}
