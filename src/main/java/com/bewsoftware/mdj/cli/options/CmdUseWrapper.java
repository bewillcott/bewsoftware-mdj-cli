/*
 *  File Name:    CmdUseWrapper.java
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

import com.bewsoftware.fileio.ini.IniFile;
import com.bewsoftware.fileio.ini.IniFileFormatException;
import com.bewsoftware.mdj.cli.util.CmdLine;
import com.bewsoftware.property.IniProperty;
import com.bewsoftware.utils.struct.Ref;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import static com.bewsoftware.fileio.BEWFiles.copyDirTree;
import static com.bewsoftware.mdj.cli.options.util.Cli.loadConf;
import static com.bewsoftware.mdj.cli.options.util.Cli.processSubstitutions;
import static com.bewsoftware.mdj.cli.util.Constants.DISPLAY;
import static com.bewsoftware.mdj.cli.util.Constants.HELP_FOOTER;
import static com.bewsoftware.mdj.cli.util.Constants.HELP_HEADER;
import static com.bewsoftware.mdj.cli.util.Constants.SYNTAX;
import static com.bewsoftware.mdj.cli.util.GlobalVariables.conf;
import static com.bewsoftware.mdj.cli.util.GlobalVariables.exception;
import static java.nio.file.StandardCopyOption.COPY_ATTRIBUTES;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static java.util.Optional.of;

/**
 * CmdUseWrapper class description.
 *
 * @author <a href="mailto:bw.opensource@yahoo.com">Bradley Willcott</a>
 *
 * @since 1.1.7
 * @version 2.0.0
 */
public class CmdUseWrapper implements Option
{

    public CmdUseWrapper()
    {
        // NoOp
    }

    private static void copyFiles(String value, CmdLine cmd, Ref<Optional<Integer>> rtn)
    {
        if (!value.isEmpty())
        {
            try
            {
                copyDirTree(
                        DISPLAY,
                        cmd.source() + "/" + value,
                        cmd.destination() + "/" + value, "*",
                        COPY_ATTRIBUTES, REPLACE_EXISTING
                );
            } catch (IOException ex)
            {
                exception = ex;
                rtn.val = of(-1);
            }
        }
    }

    private static void loadConfigurationFile(Path srcDir, CmdLine cmd, Ref<Optional<Integer>> rtn)
    {
        try
        {
            loadConf(srcDir);
        } catch (FileNotFoundException ex)
        {
            String msg = ex.toString()
                    + "\nHave you initialised the wrapper functionality? '-W <Doc Root Dir>'\n";
            cmd.printHelp(msg, SYNTAX, HELP_HEADER, HELP_FOOTER, true);
            exception = ex;
            rtn.val = of(4);
        } catch (IOException | IniFileFormatException ex)
        {
            exception = ex;
            rtn.val = of(-1);
        }
    }

    private static Path loadConfigurationFileData(CmdLine cmd)
    {
        Path srcDir = Path.of("");

        if (cmd.source() != null)
        {
            srcDir = cmd.source();
        } else if (cmd.inputFile() != null)
        {
            Path inpPath = Path.of(cmd.inputFile().toString()).getParent();

            if (inpPath != null)
            {
                srcDir = inpPath;
            }
        }

        return srcDir;
    }

    private static void processIncludeDirs(CmdLine cmd, Ref<Optional<Integer>> rtn)
    {
        if (rtn.val.isEmpty() && conf.iniDoc.containsSection("includeDirs"))
        {
            List<IniProperty<String>> props = conf.iniDoc.getSection("includeDirs");

            for (IniProperty<String> prop : props)
            {
                String value = prop.value();

                if (value != null)
                {
                    value = processSubstitutions(value, null, Ref.val());
                    copyFiles(value, cmd, rtn);
                }
            }
        }
    }

    @Override
    public Optional<Integer> execute(CmdLine cmd)
    {
        Ref<Optional<Integer>> rtn = Ref.val(Optional.empty());
        //
        // if '-w' switch active, copy files in '[includeDirs]' to destination directory
        //
        if (cmd.hasOption('w'))
        {
            Path srcDir = loadConfigurationFileData(cmd);
            loadConfigurationFile(srcDir, cmd, rtn);

            if (rtn.val.isEmpty())
            {
                processIncludeDirs(cmd, rtn);
            }
        } else
        {
            conf = new IniFile();
        }

        return rtn.val;
    }
}
