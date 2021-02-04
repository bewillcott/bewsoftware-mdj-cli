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
import java.util.Properties;

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
     * Set the destination directory.
     *
     * @param path The new destination.
     */
    public void destination(Path path);

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
     * Retrieve the map of values associated to the option.
     *
     * @param opt name of the option.
     *
     * @return The Properties mapped by the option, never null even if the option doesn't exists.
     */
    public Properties getOptionProperties(char opt);

    /**
     * Retrieve the map of values associated to the option.
     *
     * @param opt name of the option.
     *
     * @return The Properties mapped by the option, never null even if the option doesn't exists.
     */
    public Properties getOptionProperties(String opt);

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
     * Get the input file.
     *
     * @return the file.
     */
    public File inputFile();

    /**
     * Set the input file.
     *
     * @param file The new input file.
     */
    public void inputFile(File file);

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
     * Set the output file.
     *
     * @param file The new output file.
     */
    public void outputFile(File file);

    /**
     * The pom.xml file.
     *
     * @return the file.
     */
    public File pomFile();

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
     * Set the source directory.
     *
     * @param path The new source directory.
     */
    public void source(Path path);

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
