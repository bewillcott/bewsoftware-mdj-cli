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

import java.io.File;
import java.nio.file.Path;
import java.util.List;

/**
 * CmdLine interface description.
 *
 * @author <a href="mailto:bw.opensource@yahoo.com">Bradley Willcott</a>
 *
 * @since 1.0.7
 * @version 1.0.7
 */
public interface CmdLine {

    /**
     * Get the destination directory.
     *
     * @return the directory.
     */
    public Path destination();

    /**
     * Document root directory
     *
     * @return the directory.
     */
    public Path docRootPath();

    /**
     * Get the list of Exceptions, if any.
     * <p>
     * This should be tested before accessing any of the other fields.
     * If there are Exceptions, then they will need to be attended to
     * before you can be sure of the veracity of the contents of the
     * other fields.
     *
     * @return the List. If no Exceptions, then the List will be empty.
     */
    public List<Exception> exceptions();

    /**
     * Has the option been set.
     *
     * @param opt Character name of the option.
     *
     * @return result.
     */
    public boolean hasOption(char opt);

    /**
     * Has the option been set.
     *
     * @param opt Short name of the option.
     *
     * @return result.
     */
    public boolean hasOption(String opt);

    /**
     * Do we initialize the wrapper directories and files?
     *
     * @return result.
     */
    public boolean initialize();

    /**
     * Get the input file.
     *
     * @return the file.
     */
    public File inputFile();

    /**
     * Is help requested?
     *
     * @return result.
     */
    public boolean isHelp();

    /**
     * Recursively process directories?
     *
     * @return result.
     */
    public boolean isRecursive();

    /**
     * Is verbosity turned on?
     *
     * @return result.
     */
    public boolean isVerbose();

    /**
     * Wrapping - process meta block.
     *
     * @return result.
     */
    public boolean isWrapping();

    /**
     * Do we create a jar file?
     *
     * @return result.
     */
    public boolean jar();

    /**
     * Get Jar file.
     *
     * @return the file.
     */
    public File jarFile();

    /**
     * Get Jar source directory.
     *
     * @return the directory.
     */
    public Path jarSourcePath();

    /**
     * Get the output file.
     *
     * @return the file.
     */
    public File outputFile();

    /**
     * Print default help message.
     *
     * @param cmdLineSyntax the syntax for this application
     */
    public void printHelp(String cmdLineSyntax);

    /**
     * Print the help with the specified command line syntax.
     * <p>
     * This method prints help information to System.out.
     *
     * @param cmdLineSyntax the syntax for this application
     * @param header        the banner to display at the beginning of the help
     * @param footer        the banner to display at the end of the help
     * @param autoUsage     whether to print an automatically generated
     *                      usage statement
     */
    public void printHelp(String cmdLineSyntax, String header, String footer, boolean autoUsage);

    /**
     * Print the help with the specified command line syntax.
     * <p>
     * This method prints help information to System.out.
     *
     * @param errorMsg      error message to be prepended to output.
     * @param cmdLineSyntax the syntax for this application.
     * @param header        the banner to display at the beginning of the help.
     * @param footer        the banner to display at the end of the help.
     * @param autoUsage     whether to print an automatically generated
     *                      usage statement.
     */
    public void printHelp(String errorMsg, String cmdLineSyntax, String header, String footer, boolean autoUsage);

    /**
     * Get the source directory.
     *
     * @return the directory.
     */
    public Path source();

    /**
     * Processed args successfully.
     *
     * @return result.
     */
    public boolean success();

    /**
     * Get the level of verbosity.
     *
     * @return the level.
     */
    public int verbosity();
}
