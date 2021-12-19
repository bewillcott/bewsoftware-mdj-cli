/*
 *  File Name:    CmdUseWrapper.java
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

import com.bewsoftware.fileio.ini.IniFile;
import com.bewsoftware.fileio.ini.IniFileFormatException;
import com.bewsoftware.mdj.cli.CmdLine;
import com.bewsoftware.property.IniProperty;
import com.bewsoftware.utils.struct.Ref;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import static com.bewsoftware.fileio.BEWFiles.copyDirTree;
import static com.bewsoftware.mdj.cli.Cli.conf;
import static com.bewsoftware.mdj.cli.Cli.loadConf;
import static com.bewsoftware.mdj.cli.Cli.processSubstitutions;
import static com.bewsoftware.mdj.cli.Cli.vlevel;
import static com.bewsoftware.mdj.cli.Main.DISPLAY;
import static com.bewsoftware.mdj.cli.Main.HELP_FOOTER;
import static com.bewsoftware.mdj.cli.Main.HELP_HEADER;
import static com.bewsoftware.mdj.cli.Main.SYNTAX;
import static java.nio.file.Path.of;
import static java.nio.file.StandardCopyOption.COPY_ATTRIBUTES;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

/**
 * CmdUseWrapper class description.
 *
 * @author <a href="mailto:bw.opensource@yahoo.com">Bradley Willcott</a>
 *
 * @since 1.1.7
 * @version 1.1.7
 */
public class CmdUseWrapper implements Option
{

    public CmdUseWrapper()
    {
        // NoOp
    }

    @Override
    public Integer execute(CmdLine cmd)
    {
        Ref<Integer> rtn = Ref.val(null);

        //
        // if '-w' switch active, copy files in '[includeDirs]' to destination directory
        //
        if (cmd.hasOption('w'))
        {
            Path srcDir = loadConfigurationFileData(cmd);
            loadConfigurationFile(srcDir, cmd, rtn);
            processIncludeDirs(cmd, rtn);
        } else
        {
            conf = new IniFile();
        }

        return rtn.val;
    }

    private void copyFiles(String value, CmdLine cmd, Ref<Integer> rtn)
    {
        if (!value.isEmpty())
        {
            try
            {
                copyDirTree(
                        cmd.source() + "/" + value,
                        cmd.destination() + "/" + value, "*",
                        vlevel, COPY_ATTRIBUTES, REPLACE_EXISTING
                );
            } catch (IOException ex)
            {
                if (vlevel >= 2)
                {
                    DISPLAY.println(ex);
                }

                rtn.val = -1;
            }
        }
    }

    private void loadConfigurationFile(Path srcDir, CmdLine cmd, Ref<Integer> rtn)
    {
        try
        {
            loadConf(srcDir);
        } catch (FileNotFoundException ex)
        {
            String msg = ex.toString()
                    + "\nHave you initialised the wrapper functionality? '-W <Doc Root Dir>'\n";
            cmd.printHelp(msg, SYNTAX, HELP_HEADER, HELP_FOOTER, true);
            rtn.val = 4;
        } catch (IOException | IniFileFormatException ex)
        {
            if (vlevel >= 2)
            {
                DISPLAY.println(ex);
            }

            rtn.val = -1;
        }
    }

    private Path loadConfigurationFileData(CmdLine cmd)
    {
        Path srcDir = of("");

        if (cmd.source() != null)
        {
            srcDir = cmd.source();
        } else if (cmd.inputFile() != null)
        {
            Path inpPath = of(cmd.inputFile().toString()).getParent();

            if (inpPath != null)
            {
                srcDir = inpPath;
            }
        }

        return srcDir;
    }

    private void processIncludeDirs(CmdLine cmd, Ref<Integer> rtn)
    {
        if (rtn.val == null && conf.iniDoc.containsSection("includeDirs"))
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
}
