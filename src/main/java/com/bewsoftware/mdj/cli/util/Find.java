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
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.bewsoftware.mdj.cli.util;

import com.bewsoftware.fileio.Finder;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.SortedSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.bewsoftware.mdj.cli.util.Constants.DEFAULT_INPUT_FILE_EXTENSION;
import static com.bewsoftware.mdj.cli.util.Constants.DEFAULT_OUTPUT_FILE_EXTENSION;
import static com.bewsoftware.mdj.cli.util.Constants.DISPLAY;
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
 * @version 1.1.7
 */
public class Find
{

    /**
     * Not meant to be instantiated.
     */
    private Find()
    {
    }

    /**
     * Provides a list of files.
     *
     * @param srcPath   Start file search from this directory. (Default: "" -
     *                  Current Working Directory)
     * @param pattern   Glob file search pattern. (Default: "{@code *}")
     * @param recursive {@code True} sets recursive directory tree walk.
     *                  {@code False} keeps search to the current directory, only.
     *
     * @return Set of {@code Path}s representing the files and directories
     *         found.
     *
     * @throws IOException if any.
     */
    public static SortedSet<Path> getFileList(
            Path srcPath,
            String pattern,
            boolean recursive
    ) throws IOException
    {

        Path currentDir = FileSystems.getDefault().getPath("").toAbsolutePath();
        DISPLAY.level(1).println("PWD: " + currentDir);

        Finder finder = new Finder(
                DISPLAY,
                pattern != null ? pattern : "*" + DEFAULT_INPUT_FILE_EXTENSION
        );

        Files.walkFileTree(
                srcPath,
                EnumSet.noneOf(FileVisitOption.class),
                recursive ? MAX_VALUE : 1,
                finder
        );

        return finder.done();
    }

    /**
     * Provides a list of files that need to be updated.
     *
     * @param srcPath   Start file search from this directory. (Default: "" -
     *                  Current Working Directory)
     * @param destPath  Prepare return list with this directory merged into
     *                  output file paths. (Default: &lt;sourceDir&gt;)
     * @param pattern   Glob file search pattern. (Default: "{@code *.md}")
     * @param outExtn   Output file extension. (Default: "{@code .html}")
     * @param recursive {@code True} sets recursive directory tree walk.
     *                  {@code False} keeps search to the current directory, only.
     *
     * @return List of {@linkplain FileData} objects.
     *
     * @throws IOException if any.
     */
    public static List<FileData> getUpdateList(
            Path srcPath,
            Path destPath,
            File pattern,
            String outExtn,
            boolean recursive
    ) throws IOException
    {

        Path currentDir = FileSystems.getDefault().getPath("").toAbsolutePath();
        DISPLAY.level(1).println("PWD: " + currentDir);

        Finder finder = new Finder(
                DISPLAY,
                pattern != null ? pattern.toString() : "*" + DEFAULT_INPUT_FILE_EXTENSION
        );

        Files.walkFileTree(
                srcPath,
                EnumSet.noneOf(FileVisitOption.class),
                recursive ? MAX_VALUE : 1,
                finder
        );

        return displayFileOutList(
                processFileInputList(finder, srcPath, destPath, outExtn)
        );
    }

    private static void addInPathToOutList(
            Path outPath,
            Path inPath,
            List<FileData> outList
    ) throws IOException
    {
        if (notExists(outPath)
                || inPathFileIsNewerThanOutPathFile(inPath, outPath))
        {
            outList.add(new FileData(inPath, outPath));
        }
    }

    private static List<FileData> displayFileOutList(List<FileData> outList)
    {
        DISPLAY.level(2).appendln("outList:");
        outList.forEach(fileData -> DISPLAY.appendln(fileData.destinationPath));
        DISPLAY.flush();

        return outList;
    }

    private static boolean inPathFileIsNewerThanOutPathFile(
            Path inPath,
            Path outPath
    ) throws IOException
    {
        return getLastModifiedTime(inPath).
                compareTo(getLastModifiedTime(outPath)) > 0;
    }

    private static List<FileData> processFileInputList(
            Finder finder,
            Path srcPath,
            Path destPath,
            String outExtn
    ) throws IOException
    {
        SortedSet<Path> inList = finder.done();
        List<FileData> outList = new ArrayList<>(inList.size());

        DISPLAY.level(2).appendln("inpList:");

        for (Path inPath : inList)
        {
            processInPath(inPath, srcPath, destPath, outExtn, outList);
        }

        DISPLAY.flush();
        return outList;
    }

    private static void processInPath(Path inPath, Path srcPath, Path destPath, String outExtn, List<FileData> outList) throws IOException
    {
        DISPLAY.appendln(inPath);
        Matcher m;

        if (srcPath.toString().isEmpty() || destPath == null)
        {
            m = Pattern.compile("^(?<basename>.*?)(?:[.]\\w+)?$")
                    .matcher(inPath.toString());
        } else
        {
            m = Pattern.compile("^(?:" + srcPath + "/)(?<basename>.*?)(?:[.]\\w+)?$")
                    .matcher(inPath.toString());
        }

        if (m.find())
        {
            String basename = m.group("basename");
            Path outPath = of(
                    destPath != null ? destPath.toString() : "",
                    basename + (outExtn != null ? outExtn : DEFAULT_OUTPUT_FILE_EXTENSION)
            );

            addInPathToOutList(outPath, inPath, outList);
        }
    }
}
