/*
 *  File Name:    CmdCreateJar.java
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
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.bewsoftware.mdj.cli.options;

import com.bewsoftware.mdj.cli.util.CmdLine;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Optional;

import static com.bewsoftware.mdj.cli.util.Constants.DISPLAY;
import static com.bewsoftware.mdj.cli.util.Constants.HELP_FOOTER;
import static com.bewsoftware.mdj.cli.util.Constants.HELP_HEADER;
import static com.bewsoftware.mdj.cli.util.Constants.SYNTAX;
import static com.bewsoftware.mdj.cli.util.Jar.createJarFile;
import static java.util.Optional.of;

/**
 * CmdCreateJar class description.
 *
 * @author <a href="mailto:bw.opensource@yahoo.com">Bradley Willcott</a>
 *
 * @since 1.1.7
 * @version 1.1.9
 */
public class CmdCreateJar implements Option
{
    public CmdCreateJar()
    {
        // NoOp
    }

    @Override
    public Optional<Integer> execute(CmdLine cmd)
    {
        Optional<Integer> rtn = Optional.empty();

        //
        // '-j' jar file creation
        //
        // returns 0 - successs
        //         5 - fail.
        //
        if (cmd.hasOption('j'))
        {
            if (cmd.hasOption('i')
                    || cmd.hasOption('o')
                    || cmd.hasOption('s')
                    || cmd.hasOption('d')
                    || cmd.hasOption('p')
                    || cmd.hasOption('r')
                    || cmd.hasOption('W')
                    || cmd.hasOption('w'))
            {
                cmd.printHelp(
                        "Too many switches for \"-j\"\n\n",
                        SYNTAX,
                        HELP_HEADER,
                        HELP_FOOTER,
                        true
                );

                rtn = of(5);
            } else
            {
                try
                {
                    rtn = of(createJarFile(
                            cmd.jarFile(),
                            cmd.jarSourcePath()
                    ));
                } catch (IOException | URISyntaxException ex)
                {
                    DISPLAY.level(2).println(ex);
                    rtn = of(-1);
                }
            }
        }

        return rtn;
    }
}
