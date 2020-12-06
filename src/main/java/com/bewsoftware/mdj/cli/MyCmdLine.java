/*
 * Copyright (C) 2020 <a href="mailto:bw.opensource@yahoo.com">Bradley Willcott</a>
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
package com.bewsoftware.mdj.cli;

import com.bewsoftware.common.InvalidParameterValueException;
import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.cli.*;

import static java.nio.file.Path.of;
import static org.apache.commons.cli.Option.builder;

/**
 * MyCmdLine class description.
 *
 * @author <a href="mailto:bw.opensource@yahoo.com">Bradley Willcott</a>
 *
 * @since 1.0.7
 * @version 1.0.7
 */
public final class MyCmdLine implements CmdLine {

    /**
     * Initializes the command-line Options.
     *
     * @return the Options.
     */
    private static Options initializeOptions() {
        Options options = new Options();

        // Add input file: '-i'
        options.addOption(builder("i")
                .desc("The markdown input file to parse. (*.md)")
                .hasArg()
                .argName("file name")
                .build());

        // Add ouput file: '-o'
        options.addOption(builder("o")
                .desc("The HTML output file. (*.html)")
                .hasArg()
                .argName("file name")
                .build());

        // Add source directory: '-s'
        options.addOption(builder("s")
                .desc("The source directory for markdown files. (default: \"\" - current directory)")
                .hasArg()
                .argName("directory")
                .build());

        // Add destination directory: '-d'
        options.addOption(builder("d")
                .desc("The destination directory for HTML files. (default: \"\" - current directory)")
                .hasArg()
                .argName("directory")
                .build());

        // Add initialization of wrapper directories and files: '-W'
        options.addOption(builder("W")
                .desc("Initialise wrapper directories and files.\n"
                      + "NOTE: Can NOT be used with any other switches, except \"-v <level>\".")
                .optionalArg(true)
                .argName("document root directory")
                .build());

        // Add jar file creation: '-j'
        options.addOption(builder("j")
                .desc("Copy HTML files from directory into a new 'jar' file.\n"
                      + "NOTE: Can NOT be used with any other switches, except \"-v <level>\".")
                .numberOfArgs(2)
                .valueSeparator(';')
                .argName("jarfile;srcDir")
                .build());

        // Add verbosity: '-v'
        options.addOption(builder("v")
                .desc("Verbosity. (default: '0', or '1' if set with no level [1-3])")
                .hasArg()
                .optionalArg(true)
                .argName("level")
                .build());

        // Add help: '-h' or '--help'
        options.addOption(builder("h")
                .desc("Display this help.")
                .longOpt("help")
                .build());

        // Add resursive directory processing: '-r'
        options.addOption(builder("r")
                .desc("Recursively process directories.")
                .build());

        // Add meta block processing: '-w'
        options.addOption(builder("w")
                .desc("Process meta block, wrapping your document with templates and stylesheets.")
                .build());

        return options;
    }

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
    private final List<Exception> exceptions = new ArrayList<>();

    /**
     * Is help requested?
     */
    private boolean help;

    /**
     * Do we initialize the wrapper directories and files?
     */
    private boolean initialize;

    /**
     * The input filename.
     */
    private File inputFile;

    /**
     * Do we create a jar file?
     */
    private boolean jar;

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
     * Recursive directory processing.
     */
    private boolean recursive;

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
     * Wrapping - process meta block.
     */
    private boolean wrap;

    /**
     * Create an immutable instance of MyCmdLine.
     *
     * @param args The command-line args.
     */
    public MyCmdLine(final String[] args) {
        options = initializeOptions();

        CommandLineParser parser = new DefaultParser();

        try
        {
            cmdLine = parser.parse(options, args);

            destination = hasOption('d')
                          ? of(cmdLine.getOptionValue('d', "")).normalize().toAbsolutePath()
                          : of("").toAbsolutePath();
            docRootPath = hasOption('W')
                          ? of(cmdLine.getOptionValue('W', "")).normalize().toAbsolutePath()
                          : of("").toAbsolutePath();
            help = hasOption('h');
            initialize = hasOption('W');
            inputFile = hasOption('i') ? new File(cmdLine.getOptionValue('i')) : null;
            jar = hasOption('j');
            jarFile = hasOption('j') ? new File(cmdLine.getOptionValues('j')[0]) : null;
            jarSourcePath = hasOption('j') ? of(cmdLine.getOptionValues('j')[1]) : null;
            outputFile = hasOption('o') ? new File(cmdLine.getOptionValue('o')) : null;
            recursive = hasOption('r');
            source = hasOption('s')
                     ? of(cmdLine.getOptionValue('s')).normalize().toAbsolutePath()
                     : of("").toAbsolutePath();
            verbose = hasOption('v');
            verbosity = hasOption('v') ? Integer.parseInt(cmdLine.getOptionValue('v', "1")) : 0;
            wrap = hasOption('w');

            if (verbose && (verbosity < 1 || verbosity > 3))
            {
                throw new InvalidParameterValueException("Verbosity setting out of range [1-3]: " + verbosity);
            }
        } catch (Exception ex)
        {
            exceptions.add(ex);
        }
    }

    @Override
    public Path destination() {
        return destination;
    }

    @Override
    public Path docRootPath() {
        return docRootPath;
    }

    @Override
    public List<Exception> exceptions() {
        return exceptions;
    }

    @Override
    public boolean hasOption(char opt) {
        return cmdLine.hasOption(opt);
    }

    @Override
    public boolean hasOption(String opt) {
        return cmdLine.hasOption(opt);
    }

    @Override
    public boolean initialize() {
        return initialize;
    }

    @Override
    public File inputFile() {
        return inputFile;
    }

    @Override
    public boolean isHelp() {
        return help;
    }

    @Override
    public boolean isRecursive() {
        return recursive;
    }

    @Override
    public boolean isVerbose() {
        return verbose;
    }

    @Override
    public boolean isWrapping() {
        return wrap;
    }

    @Override
    public boolean jar() {
        return jar;
    }

    @Override
    public File jarFile() {
        return jarFile;
    }

    @Override
    public Path jarSourcePath() {
        return jarSourcePath;
    }

    @Override
    public File outputFile() {
        return outputFile;
    }

    @Override
    public void printHelp(String cmdLineSyntax) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp(cmdLineSyntax, options);
    }

    @Override
    public void printHelp(String cmdLineSyntax, String header, String footer, boolean autoUsage) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp(cmdLineSyntax, header, options, footer, autoUsage);
    }

    @Override
    public void printHelp(String errorMsg, String cmdLineSyntax, String header, String footer, boolean autoUsage) {
        System.err.println("Error:\n" + errorMsg);
        printHelp(cmdLineSyntax, header, footer, autoUsage);
    }

    @Override
    public Path source() {
        return source;
    }

    @Override
    public boolean success() {
        return exceptions.isEmpty();
    }

    @Override
    public String toString() {
        return "MyCmdLine{\n" + "\tdestination = " + destination + ","
               + "\n\tinputFile = " + inputFile + ","
               + "\n\tisHelp = " + help + ","
               + "\n\tisVerbose = " + verbose + ","
               + "\n\toutputFile = " + outputFile + ","
               + "\n\tsource = " + source + ","
               + "\n\tverbosity = " + verbosity
               + "\n}";
    }

    @Override
    public int verbosity() {
        return verbosity;
    }
}
