/*
 *  File Name:    MainProcessor.java
 *  Project Name: bewsoftware-mdj-cli
 *
 *  Copyright (c) 2021 Bradley Willcott
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.bewsoftware.mdj.cli.options;

import com.bewsoftware.mdj.cli.CmdLine;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static com.bewsoftware.mdj.cli.Cli.conf;
import static com.bewsoftware.mdj.cli.Cli.processFile;
import static com.bewsoftware.mdj.cli.Cli.vlevel;
import static com.bewsoftware.mdj.cli.Find.getUpdateList;
import static com.bewsoftware.mdj.cli.Main.DISPLAY;
import static java.util.Optional.of;

/**
 * MainProcessor class description.
 *
 * @author <a href="mailto:bw.opensource@yahoo.com">Bradley Willcott</a>
 *
 * @since 1.0
 * @version 1.0
 */
public class MainProcessor implements Option
{
    public MainProcessor()
    {
        // NoOp
    }

    @Override
    public Optional<Integer> execute(CmdLine cmd)
    {
        Optional<Integer> rtn = Optional.empty();

        try
        {
            conf.iniDoc.setString("system", "date", new Date().toString());
            processFiles(cmd);
        } catch (IOException ex)
        {
            if (vlevel >= 2)
            {
                DISPLAY.println(ex);
            }

            rtn = of(-1);
        }

        return rtn;
    }
    private static void loadOutputDirs(List<Path[]> fileList, Set<Path> outputDirs)
    {
        fileList.forEach(filePairs ->
        {
            if (vlevel >= 1)
            {
                DISPLAY.println(filePairs[0]);
                DISPLAY.println("    " + filePairs[1]);
            }

            Path parent = filePairs[1].getParent();

            if (parent != null)
            {
                outputDirs.add(parent);
            }
        });
    }

    private static void processFiles(CmdLine cmd) throws IOException
    {
        //
        // Get files to process
        //
        List<Path[]> fileList = getUpdateList(cmd.source(),
                cmd.destination(),
                cmd.inputFile(), null, cmd.hasOption('r'), vlevel);

        // Process files
        if (!fileList.isEmpty())
        {
            if (singleFileWithOutputFile(cmd, fileList))
            {
                updateFileList(cmd, fileList);
            }

            Set<Path> outputDirs = new TreeSet<>();
            loadOutputDirs(fileList, outputDirs);

            for (Path dir : outputDirs)
            {
                if (vlevel >= 2)
                {
                    DISPLAY.println("    " + dir);
                }

                Files.createDirectories(dir);
            }

            for (Path[] filePairs : fileList)
            {
                processFile(filePairs[0], filePairs[1], cmd.destination(), cmd.hasOption('w'));
            }
        }
    }

    private static boolean singleFileWithOutputFile(CmdLine cmd, List<Path[]> fileList)
    {
        return cmd.outputFile() != null && fileList.size() == 1;
    }

    private static void updateFileList(CmdLine cmd, List<Path[]> fileList)
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

}
