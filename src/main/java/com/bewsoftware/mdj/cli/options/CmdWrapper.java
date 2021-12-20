/*
 *  File Name:    CmdWrapper.java
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

import com.bewsoftware.fileio.ini.IniFileFormatException;
import com.bewsoftware.mdj.cli.CmdLine;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Optional;

import static com.bewsoftware.mdj.cli.Cli.initialiseWrappers;
import static com.bewsoftware.mdj.cli.Cli.vlevel;
import static com.bewsoftware.mdj.cli.Main.DISPLAY;
import static com.bewsoftware.mdj.cli.Main.HELP_FOOTER;
import static com.bewsoftware.mdj.cli.Main.HELP_HEADER;
import static com.bewsoftware.mdj.cli.Main.SYNTAX;
import static java.util.Optional.of;

/**
 * CmdWrapper class description.
 *
 * @author <a href="mailto:bw.opensource@yahoo.com">Bradley Willcott</a>
 *
 * @since 1.1.7
 * @version 1.1.7
 */
public class CmdWrapper implements Option
{

    public CmdWrapper()
    {
        // NoOp
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
                    || cmd.hasOption('r')
                    || cmd.hasOption('w'))
            {
                String msg = "Too many switches for \"-W\"\n\n";

                cmd.printHelp(msg, SYNTAX, HELP_HEADER, HELP_FOOTER, true);
                rtn = of(6);
            } else
            {
                try
                {
                    rtn = of(initialiseWrappers(cmd.docRootPath()));
                } catch (IOException | IniFileFormatException | URISyntaxException ex)
                {
                    if (vlevel >= 2)
                    {
                        DISPLAY.println(ex);
                    }

                    rtn = of(-1);
                }
            }
        }

        return rtn;
    }
}
