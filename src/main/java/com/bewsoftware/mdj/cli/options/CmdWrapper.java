/*
 *  File Name:    CmdWrapper.java
 *  Project Name: bewsoftware-mdj-cli
 *
 *  Copyright (c) 2021-2022 Bradley Willcott
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
 *  along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.bewsoftware.mdj.cli.options;

import com.bewsoftware.common.InvalidParameterValueException;
import com.bewsoftware.fileio.ini.IniFile;
import com.bewsoftware.fileio.ini.IniFileFormatException;
import com.bewsoftware.mdj.cli.options.util.Cli;
import com.bewsoftware.mdj.cli.util.CmdLine;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import static com.bewsoftware.fileio.BEWFiles.copyDirTree;
import static com.bewsoftware.fileio.BEWFiles.getResource;
import static com.bewsoftware.mdj.cli.util.Constants.*;
import static com.bewsoftware.mdj.cli.util.GlobalVariables.exception;
import static java.nio.file.Path.of;
import static java.nio.file.StandardCopyOption.COPY_ATTRIBUTES;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static java.util.Optional.of;

/**
 * CmdWrapper class description.
 *
 * @author <a href="mailto:bw.opensource@yahoo.com">Bradley Willcott</a>
 *
 * @since 1.1.7
 * @version 1.1.9
 */
public class CmdWrapper implements Option
{

    public CmdWrapper()
    {
        // NoOp
    }

    /**
     * Initialize the wrapper directories and files.
     *
     * @param docRootPath The document root directory.
     *
     * @return '0' if successful, '-1' if not..
     *
     * @since 0.1
     * @version 2.0.0
     */
    private static int initialiseWrappers(final Path docRootPath)
    {
        int rtn = 0;

        DISPLAY.level(3).println("docRootPath:\n" + docRootPath);
        try
        {
            // Create directories.
            if (Files.notExists(docRootPath))
            {
                Files.createDirectories(docRootPath);
            }

            // Get source directory from jar file.
            Path srcDirPath = getResource(Cli.class, "/docs/init").toAbsolutePath();

            DISPLAY.level(2)
                    .appendln("srcDirPath: " + srcDirPath)
                    .println("srcDirPath exists: " + Files.exists(srcDirPath));

            // Get source ini file from jar file.
            Path srcIniPath = getResource(Cli.class, "/" + CONF_FILENAME + "").toAbsolutePath();

            DISPLAY.level(2)
                    .appendln("srcIniPath: " + srcIniPath)
                    .println("srcIniPath exists: " + Files.exists(srcIniPath));

            // Get destination ini file path.
            Path destIniPath = of(docRootPath.toString(), CONF_FILENAME);

            DISPLAY.level(2)
                    .appendln("destIniPath: " + destIniPath)
                    .println("destIniPath exists: " + Files.exists(destIniPath));

            copyDirTree(DISPLAY, srcDirPath, docRootPath,
                    "*", COPY_ATTRIBUTES, REPLACE_EXISTING);

            IniFile iniFile;

            // If there already exists an ini file, then...
            if (Files.exists(destIniPath))
            {
                DISPLAY.level(2).println("destIniPath exists");
                iniFile = new IniFile(srcIniPath).loadFile().mergeFile(destIniPath);
            } else
            {
                DISPLAY.level(2).println("destIniPath dosen't exist");
                iniFile = new IniFile(srcIniPath).loadFile();
            }

            iniFile.iniDoc.setString("document", "docRootDir", docRootPath.toString());
            iniFile.iniDoc.setString(null, "iniVersion", POM.version,
                    "; DO NOT REMOVE/MOVE OR MODIFY: iniVersion!");

            DISPLAY.level(2)
                    .println("document.docRootDir: "
                            + iniFile.iniDoc.getString(
                                    "document",
                                    "docRootDir",
                                    "default"
                            ));

            iniFile.paddedEquals = true;

            iniFile.saveFileAs(destIniPath);
        } catch (IOException | IniFileFormatException | InvalidParameterValueException
                | NullPointerException | URISyntaxException ex)
        {
            exception = ex;
            rtn = -1;
        }

        return rtn;
    }

    @Override
    public Optional<Integer> execute(CmdLine cmd)
    {
        Optional<Integer> rtn = Optional.empty();
        //
        // '-W' initialise wrapper functionality
        //
        // returns 0.
        //
        if (cmd.hasOption('W'))
        {
            if (cmd.hasOption('i')
                    || cmd.hasOption('o')
                    || cmd.hasOption('s')
                    || cmd.hasOption('d')
                    || cmd.hasOption('p')
                    || cmd.hasOption('P')
                    || cmd.hasOption('r')
                    || cmd.hasOption('w'))
            {
                String msg = "Too many switches for \"-W\"\n\n";

                cmd.printHelp(
                        msg,
                        SYNTAX,
                        HELP_HEADER,
                        HELP_FOOTER,
                        true
                );

                rtn = of(6);
            } else
            {
                rtn = of(initialiseWrappers(cmd.docRootPath()));
            }
        }

        return rtn;
    }
}
