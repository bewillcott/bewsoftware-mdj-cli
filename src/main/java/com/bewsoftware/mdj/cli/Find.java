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

import com.bewsoftware.fileio.Finder;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.Character.MAX_VALUE;
import static java.nio.file.Files.getLastModifiedTime;
import static java.nio.file.Files.notExists;
import static java.nio.file.Path.of;

/**
 * Sample code that finds files that match the specified glob pattern.
 * <p>
 * For more information on what constitutes a glob pattern, see
 * https://docs.oracle.com/javase/tutorial/essential/io/fileOps.html#glob
 * <p>
 * The file or directories that match the pattern are printed to
 * standard out. The number of matches is also printed.
 * <p>
 * When executing this application, you must put the glob pattern
 * in quotes, so the shell will not expand any wild cards:
 * java Find . -name "*.java"
 *
 * @author <a href="mailto:bw.opensource@yahoo.com">Bradley Willcott</a>
 *
 * @since 0.1
 * @version 1.0.7
 */
class Find {

    /**
     * Default HTML file extn.
     */
    private static final String DEFAULT_HTML = ".html";

    /**
     * Default Markdown file extn.
     */
    private static final String DEFAULT_MD = ".md";

    /**
     * Provides a list of files.
     *
     * @param srcPath   Start file search from this directory. (Default: "" - Current Working Directory)
     * @param pattern   Glob file search pattern. (Default: "{@code *}")
     * @param recursive {@code True} sets recursive directory tree walk.
     *                  {@code False} keeps search to the current directory, only.
     * @param vlevel    {@code 0} Run silent ...
     *                  {@code 1} Printout basic information only.
     *                  {@code 2} Printout progress/diagnostic information during processing.
     *
     *
     * @return Set of {@code Path}s representing the files and directories found.
     *
     * @throws IOException if any.
     */
    public static SortedSet<Path> getFileList(Path srcPath, String pattern, boolean recursive, int vlevel)
            throws IOException {

        Path currentDir = FileSystems.getDefault().getPath("").toAbsolutePath();

        // Debug output.
        if (vlevel >= 1)
        {
            System.out.println("PWD: " + currentDir);
        }

        Finder finder = new Finder(pattern != null ? pattern : "*" + DEFAULT_MD, vlevel);

        Files.walkFileTree(srcPath, EnumSet.noneOf(FileVisitOption.class), recursive ? MAX_VALUE : 1, finder);

        return finder.done();
    }

    /**
     * Provides a list of files that need to be updated.
     *
     * @param srcPath   Start file search from this directory. (Default: "" - Current Working Directory)
     * @param destPath  Prepare return list with this directory merged into output file paths. (Default: &lt;sourceDir&gt;)
     * @param pattern   Glob file search pattern. (Default: "{@code *.md}")
     * @param outExtn   Output file extension. (Default: "{@code .html}")
     * @param recursive {@code True} sets recursive directory tree walk.
     *                  {@code False} keeps search to the current directory, only.
     * @param vlevel    {@code 0} Run silent ...
     *                  {@code 1} Printout basic information only.
     *                  {@code 2} Printout progress/diagnostic information during processing.
     *
     * @return List containing Path arrays. Each with two elements. [0] Source file, [1] Destination file.
     *
     * @throws IOException if any.
     */
    public static List<Path[]> getUpdateList(Path srcPath, Path destPath, File pattern, String outExtn,
                                             boolean recursive, int vlevel) throws IOException {

        Path currentDir = FileSystems.getDefault().getPath("").toAbsolutePath();

        // Debug output.
        if (vlevel >= 1)
        {
            System.out.println("PWD: " + currentDir);
        }

        Finder finder = new Finder(pattern != null ? pattern.toString() : "*" + DEFAULT_MD, vlevel);

        Files.walkFileTree(srcPath, EnumSet.noneOf(FileVisitOption.class), recursive ? MAX_VALUE : 1, finder);

        SortedSet<Path> inpList = finder.done();
        List<Path[]> outList = new ArrayList<>(inpList.size());

        // Debug output.
        if (vlevel >= 2)
        {
            System.out.println("inpList:");
        }

        for (Path inPath : inpList)
        {
            Matcher m;

            if (srcPath.toString().isEmpty() || destPath == null)
            {
                m = Pattern.compile("^(?<basename>.*?)(?:[.]\\w+)?$").matcher(inPath.toString());
            } else
            {
                m = Pattern.compile("^(?:" + srcPath + "/)(?<basename>.*?)(?:[.]\\w+)?$").matcher(inPath.toString());
            }

            if (m.find())
            {
                String basename = m.group("basename");
                Path outPath = of(destPath != null ? destPath.toString() : "",
                                  basename + (outExtn != null ? outExtn : DEFAULT_HTML));

                if (notExists(outPath) || getLastModifiedTime(inPath).compareTo(getLastModifiedTime(outPath)) > 0)
                {
                    Path[] files = new Path[2];

                    files[0] = inPath;
                    files[1] = outPath;
                    outList.add(files);

                    // Debug output.
                    if (vlevel >= 2)
                    {
                        System.out.println(outPath);
                    }
                }
            }
        }

        // Debug output.
        if (vlevel >= 2)
        {
            System.out.println("outList:");

            outList.forEach((files) ->
            {
                System.out.println(files[1]);
            });
        }

        return outList;
    }

    /**
     * Not meant to be instantiated.
     */
    private Find() {
    }
}
