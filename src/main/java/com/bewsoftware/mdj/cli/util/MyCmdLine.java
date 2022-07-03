/*
 *  File Name:    MyCmdLine.java
 *  Project Name: bewsoftware-mdj-cli
 *
 *  Copyright (c) 2021-2022 Bradley Willcott
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
package com.bewsoftware.mdj.cli.util;

import com.bewsoftware.common.InvalidParameterValueException;
import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import org.apache.commons.cli.*;

import static com.bewsoftware.mdj.cli.util.Constants.DISPLAY;
import static java.nio.file.Path.of;
import static org.apache.commons.cli.Option.builder;

/**
 * My implementation of the {@linkplain CmdLine} interface.
 *
 * @author <a href="mailto:bw.opensource@yahoo.com">Bradley Willcott</a>
 *
 * @since 1.0.7
 * @version 2.0.0
 */
public final class MyCmdLine implements CmdLine
{
    private static final int J_DOCROOTPATH = 2;

    private static final int J_JARFILE = 0;

    private static final int J_JARSOURCEPATH = 1;

    /**
     * The CommandLine instance.
     */
    private CommandLine cmdLine;

    /**
     * The destination directory name.
     */
    private Path destination;

    /**
     * Document root directory
     */
    private Path docRootPath;

    /**
     * List of Exceptions that are thrown during processing.
     */
    private final List<Exception> exceptions;

    /**
     * The input filename.
     */
    private File inputFile;

    /**
     * Jar file.
     */
    private File jarFile;

    /**
     * Jar source directory.
     */
    private Path jarSourcePath;

    /**
     * The command-line options.
     */
    private final Options options;

    /**
     * The output filename.
     */
    private File outputFile;

    /**
     * The properties for the '-P' option.
     */
    private Properties pProperties;

    /**
     * The pom.xml file.
     */
    private File pomFile;

    /**
     * The source directory name.
     */
    private Path source;

    /**
     * The state of verbosity.
     */
    private boolean verbose;

    /**
     * The level of verbosity.
     */
    private int verbosity;

    /**
     * Create an immutable instance of MyCmdLine.
     *
     * @param args The command-line args.
     */
    public MyCmdLine(final String[] args)
    {
        exceptions = new ArrayList<>();
        options = initializeOptions();

        CommandLineParser parser = new DefaultParser();

        try
        {
            cmdLine = parser.parse(options, args);

            checkForMinimumSwitches();

            processOption_d();
            processOption_i();
            processOption_jOrW();
            processOption_aOrj();
            processOption_o();
            processOption_p();
            processOption_s();
            processOption_v();
            processOption_W();
            processOption_P();

        } catch (InvalidParameterValueException | NumberFormatException | MissingOptionException ex)
        {
            exceptions.add(ex);
        } catch (ParseException ex)
        {
            exceptions.add(ex);
        }
    }

    /**
     * Initializes the command-line Options.
     *
     * @return the Options.
     */
    private static Options initializeOptions()
    {
        Options options = new Options();

//        // Add add http server to existing 'jar' file: '-a'
//        options.addOption(builder("a")
//                .desc("Copy HTTP Server files into an existing 'jar' file.\n"
//                      + "NOTE: Can NOT be used with any other switches,\nexcept \"-v <level>\".")
//                .hasArg()
//                .argName("jarfile")
//                .build());
        // Add resursive directory processing: '-c'
        options.addOption(builder("c")
                .desc("Display Copyright notice.")
                .build());

        // Add destination directory: '-d'
        options.addOption(builder("d")
                .desc("The destination directory for HTML files.\n(default: \"\" - current directory)")
                .hasArg()
                .argName("directory")
                .build());

        // Add POM build property: '-D'
        options.addOption(builder("D")
                .desc("POM build property.\n"
                        + "Is made available as: ${project.<property>}.")
                .numberOfArgs(2)
                .valueSeparator()
                .argName("property=value")
                .build());

        // Add input file: '-i'
        options.addOption(builder("i")
                .desc("The markdown input file to parse. (*.md)")
                .hasArg()
                .argName("fileName")
                .build());

        // Add jar file creation: '-j'
        options.addOption(builder("j")
                .desc("Copy HTML files from directory into a new 'jar' file.\n"
                        + "NOTE: Can NOT be used with any other switches,\nexcept \"-v <level>\".")
                .numberOfArgs(3)
                .valueSeparator(';')
                .argName("jarFile;jarSrcDir;docRootDir")
                .build());

        // Add resursive directory processing: '-m'
        options.addOption(builder("m")
                .desc("Display web based manual in system default web browser.")
                .build());

        // Add ouput file: '-o'
        options.addOption(builder("o")
                .desc("The HTML output file. (*.html)")
                .hasArg()
                .argName("fileName")
                .build());

        // Add publish directory/jar file: '-P'
        options.addOption(builder("P")
                .desc("Publish the HTML files from either a directory, or a 'jar' file.\n"
                        + " 'htmlSource' is either the directory to publish,\n"
                        + " or the path to the 'jar' file (including it's name and extension).\n"
                        + "(defaults: context: \"/\", htmlSource: \"\" - current directory)\n"
                        + "Can be used multiple times to publish multiple sources at once.")
                .numberOfArgs(2)
                .optionalArg(true)
                .valueSeparator()
                .argName("context=htmlSource")
                .build());

        // Add pom.xml file: '-p'
        options.addOption(builder("p")
                .desc("The /path/to/the/pom.xml file. (pom.xml)")
                .hasArg()
                .argName("filePath")
                .build());

        // Add resursive directory processing: '-r'
        options.addOption(builder("r")
                .desc("Recursively process directories.")
                .build());

        // Add source directory: '-s'
        options.addOption(builder("s")
                .desc("The source directory for markdown files.\n(default: \"\" - current directory)")
                .hasArg()
                .argName("directory")
                .build());

        // Add verbosity: '-v'
        options.addOption(builder("v")
                .desc("Verbosity. (default: '0', or '1' if set with no level [1-3])")
                .hasArg()
                .optionalArg(true)
                .argName("level")
                .build());

        // Add meta block processing: '-w'
        options.addOption(builder("w")
                .desc("Process meta block, wrapping your document with templates and stylesheets.")
                .build());

        // Add initialization of wrapper directories and files: '-W'
        options.addOption(builder("W")
                .desc("Initialise wrapper directories and files.\n"
                        + "NOTE: Can NOT be used with any other switches,\nexcept \"-v <level>\".")
                .hasArg()
                .optionalArg(true)
                .argName("docRootDir")
                .build());

        // Add "--allowGeneratedIndex"
        options.addOption(builder()
                .desc("Allow a directory listing to be generated, if no 'index' file found.\n"
                        + "Use with option: '-p'.\n"
                        + "(default: 'false')")
                .longOpt("allowGeneratedIndex")
                .build());

        // Add "--disallowBrowserFileCaching"
        options.addOption(builder()
                .desc("Disallow web browsers caching the files sent by this instance of the web server.\n"
                        + "Use with option: '-p'.\n"
                        + "(default: 'false')")
                .longOpt("disallowBrowserFileCaching")
                .build());

        // Add help: '-h' or '--help'
        options.addOption(builder("h")
                .desc("Display this help.")
                .longOpt("help")
                .build());

        return options;
    }

    @Override
    public Path destination()
    {
        return destination;
    }

    @Override
    public void destination(Path path)
    {
        destination = path;
    }

    @Override
    public Path docRootPath()
    {
        return docRootPath;
    }

    @Override
    public List<Exception> exceptions()
    {
        return Collections.unmodifiableList(exceptions);
    }

    @Override
    public Properties getOptionProperties(char opt)
    {
        return getOptionProperties(String.valueOf(opt));
    }

    @Override
    public Properties getOptionProperties(String opt)
    {
        Properties rtn = cmdLine.getOptionProperties(opt);

        if (rtn.isEmpty() && "P".equals(opt))
        {
            rtn = pProperties;
        }

        return rtn;
    }

    @Override
    public boolean hasOption(char opt)
    {
        return cmdLine.hasOption(opt);
    }

    @Override
    public boolean hasOption(String opt)
    {
        return cmdLine.hasOption(opt);
    }

    @Override
    public File inputFile()
    {
        return inputFile;
    }

    @Override
    public void inputFile(File file)
    {
        inputFile = file;
    }

    @Override
    public File jarFile()
    {
        return jarFile;
    }

    @Override
    public Path jarSourcePath()
    {
        return jarSourcePath;
    }

    @Override
    public File outputFile()
    {
        return outputFile;
    }

    @Override
    public void outputFile(File file)
    {
        outputFile = file;
    }

    @Override
    public File pomFile()
    {
        return pomFile;
    }

    @Override
    public void printHelp(String cmdLineSyntax)
    {
        HelpFormatter formatter = new HelpFormatter();
        formatter.setWidth(100);
        formatter.printHelp(cmdLineSyntax, options);
    }

    @Override
    public void printHelp(String cmdLineSyntax, String header, String footer, boolean autoUsage)
    {
        HelpFormatter formatter = new HelpFormatter();
        formatter.setWidth(100);
        formatter.printHelp(cmdLineSyntax, header, options, footer, autoUsage);
    }

    @Override
    public void printHelp(String errorMsg, String cmdLineSyntax, String header, String footer, boolean autoUsage)
    {
        System.err.println("Error:\n" + errorMsg);
        printHelp(cmdLineSyntax, header, footer, autoUsage);
    }

    @Override
    public Path source()
    {
        return source;
    }

    @Override
    public void source(Path path)
    {
        source = path;
    }

    @Override
    public boolean success()
    {
        return exceptions.isEmpty();
    }

    @Override
    public String toString()
    {
        return "MyCmdLine{"
                + "\n\tdestination = " + destination + ","
                + "\n\tinputFile = " + inputFile + ","
                + "\n\toutputFile = " + outputFile + ","
                + "\n\tsource = " + source + ","
                + "\n\tverbosity = " + verbosity
                + "\n}";
    }

    @Override
    public int verbosity()
    {
        return verbosity;
    }

    private void checkForMinimumSwitches() throws MissingOptionException
    {
        //
        // Check for minimum switches: '-a', or '-c', or '-i', or '-j', or '-m',
        //                             or '-p', or '-s', or '-W', -h, or --help.
        //
        if (!(hasOption('a') || hasOption('c') || hasOption('i') || hasOption('j')
                || hasOption('m') || hasOption('P') || hasOption('s') || hasOption('W') || hasOption('h')))
        {
            String msg = "\nYou must use at least one of the following options:"
                    //  + "\n\t'-a', '-c', -i', '-j', '-m', '-p', '-s', '-W', or '-h|--help'\n";
                    + "\n\t'-c', -i', '-j', '-m', '-P', '-s', '-W', or '-h|--help'\n";
            throw new MissingOptionException(msg);
        }
    }

    private void processOption_P()
    {
        // Handler publish option: '-P'
        if (hasOption('P'))
        {
            String[] values = cmdLine.getOptionValues('P');

            if (values == null)
            {
                pProperties = new Properties();
                pProperties.put("/", "");
            }
        }
    }

    private void processOption_W()
    {
        if (hasOption('W'))
        {
            DISPLAY.level(2)
                    .appendln("docRootPath:\n" + docRootPath)
                    .println("-W: " + of(cmdLine.getOptionValue('W')));
        }
    }

    private void processOption_aOrj()
    {
        jarFile = hasOption('a') ? new File(cmdLine.getOptionValue('a').replace('\\', '/'))
                : hasOption('j') ? new File(cmdLine.getOptionValues('j')[J_JARFILE].replace('\\', '/')) : null;
    }

    private void processOption_d()
    {
        destination = hasOption('d')
                ? of(cmdLine.getOptionValue('d', "").replace('\\', '/')).normalize().toAbsolutePath()
                : null;
    }

    private void processOption_i()
    {
        inputFile = hasOption('i') ? new File(cmdLine.getOptionValue('i').replace('\\', '/')) : null;
    }

    private void processOption_jOrW()
    {
        docRootPath = hasOption('j')
                ? of(cmdLine.getOptionValues('j')[J_DOCROOTPATH].replace('\\', '/')).normalize().toAbsolutePath()
                : hasOption('W')
                ? of(cmdLine.getOptionValue('W', "").replace('\\', '/')).normalize().toAbsolutePath()
                : null;

        jarSourcePath = hasOption('j')
                ? of(cmdLine.getOptionValues('j')[J_JARSOURCEPATH].replace('\\', '/')).normalize().toAbsolutePath() : null;
    }

    private void processOption_o()
    {
        outputFile = hasOption('o') ? new File(cmdLine.getOptionValue('o').replace('\\', '/')) : null;
        DISPLAY.level(1).appendln(outputFile).flush();
    }

    private void processOption_p()
    {
        pomFile = hasOption('p') ? new File(cmdLine.getOptionValue('p').replace('\\', '/')) : null;
    }

    private void processOption_s()
    {
        source = hasOption('s')
                ? of(cmdLine.getOptionValue('s').replace('\\', '/')).normalize().toAbsolutePath()
                : null;
    }

    private void processOption_v() throws NumberFormatException, InvalidParameterValueException
    {
        verbose = hasOption('v');
        verbosity = hasOption('v') ? Integer.parseInt(cmdLine.getOptionValue('v', "1")) : 0;

        if (verbose && (verbosity < 1 || verbosity > 3))
        {
            throw new InvalidParameterValueException("Verbosity setting out of range [1-3]: " + verbosity);
        }
    }
}
