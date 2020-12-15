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
 * @version 1.0.14
 */
public final class MyCmdLine implements CmdLine {

    /**
     * Initializes the command-line Options.
     *
     * @return the Options.
     */
    private static Options initializeOptions() {
        Options options = new Options();

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

        // Add input file: '-i'
        options.addOption(builder("i")
                .desc("The markdown input file to parse. (*.md)")
                .hasArg()
                .argName("file name")
                .build());

        // Add jar file creation: '-j'
        options.addOption(builder("j")
                .desc("Copy HTML files from directory into a new 'jar' file.\n"
                      + "NOTE: Can NOT be used with any other switches,\nexcept \"-v <level>\".")
                .numberOfArgs(3)
                .valueSeparator(';')
                .argName("jarfile;jarSrcDir;docRootDir")
                .build());

        // Add resursive directory processing: '-m'
        options.addOption(builder("m")
                .desc("Display web based manual in system default web browser.")
                .build());

        // Add ouput file: '-o'
        options.addOption(builder("o")
                .desc("The HTML output file. (*.html)")
                .hasArg()
                .argName("file name")
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

        // Add help: '-h' or '--help'
        options.addOption(builder("h")
                .desc("Display this help.")
                .longOpt("help")
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
    public MyCmdLine(final String[] args) {
        options = initializeOptions();

        CommandLineParser parser = new DefaultParser();

        try
        {
            cmdLine = parser.parse(options, args);

            destination = hasOption('d')
                          ? of(cmdLine.getOptionValue('d', "").replace('\\', '/')).normalize().toAbsolutePath()
                          : null;
            docRootPath = hasOption('j')
                          ? of(cmdLine.getOptionValues('j')[2].replace('\\', '/')).normalize().toAbsolutePath()
                          : hasOption('W')
                            ? of(cmdLine.getOptionValue('W', "").replace('\\', '/')).normalize().toAbsolutePath()
                            : null;
            inputFile = hasOption('i') ? new File(cmdLine.getOptionValue('i').replace('\\', '/')) : null;
            jarFile = hasOption('j') ? new File(cmdLine.getOptionValues('j')[0].replace('\\', '/')) : null;
            jarSourcePath = hasOption('j')
                            ? of(cmdLine.getOptionValues('j')[1].replace('\\', '/')).normalize().toAbsolutePath() : null;
            outputFile = hasOption('o') ? new File(cmdLine.getOptionValue('o').replace('\\', '/')) : null;
            source = hasOption('s')
                     ? of(cmdLine.getOptionValue('s').replace('\\', '/')).normalize().toAbsolutePath()
                     : null;
            verbose = hasOption('v');
            verbosity = hasOption('v') ? Integer.parseInt(cmdLine.getOptionValue('v', "1")) : 0;

            if (verbose && (verbosity < 1 || verbosity > 3))
            {
                throw new InvalidParameterValueException("Verbosity setting out of range [1-3]: " + verbosity);
            }

            if (verbosity >= 2 && hasOption('W'))
            {
                System.out.println("docRootPath:\n" + docRootPath);
                System.out.println("-W: " + of(cmdLine.getOptionValue('W')));
            }

            //
            // Check for minimum switches: '-i', or '-s', or '-j', or '-W', -h, or --help.
            //
            if (!(hasOption('c') || hasOption('i') || hasOption('m') || hasOption('s')
                  || hasOption('j') || hasOption('W') || hasOption('h')))
            {
                String msg = "\nYou must use at least one of the following options:"
                             + "\n\t'-c', -i', '-s', '-j', '-m', '-W', or '-h|--help'\n";
                throw new MissingOptionException(msg);
            }

        } catch (InvalidParameterValueException | NumberFormatException
                 | ParseException | NullPointerException ex)
        {
            exceptions.add(ex);
        }
    }

    @Override
    public Path destination() {
        return destination;
    }

    @Override
    public void destination(Path path) {
        destination = path;
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
    public File inputFile() {
        return inputFile;
    }

    @Override
    public void inputFile(File file) {
        inputFile = file;
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
    public void outputFile(File file) {
        outputFile = file;
    }

    @Override
    public void printHelp(String cmdLineSyntax) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.setWidth(100);
        formatter.printHelp(cmdLineSyntax, options);
    }

    @Override
    public void printHelp(String cmdLineSyntax, String header, String footer, boolean autoUsage) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.setWidth(100);
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
    public void source(Path path) {
        source = path;
    }

    @Override
    public boolean success() {
        return exceptions.isEmpty();
    }

    @Override
    public String toString() {
        return "MyCmdLine{"
               + "\n\tdestination = " + destination + ","
               + "\n\tinputFile = " + inputFile + ","
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
