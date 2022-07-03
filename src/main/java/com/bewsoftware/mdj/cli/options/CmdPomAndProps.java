/*
 *  File Name:    CmdPomAndProps.java
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
import java.util.Optional;
import java.util.Properties;

import static com.bewsoftware.mdj.cli.options.util.Cli.loadPom;
import static com.bewsoftware.mdj.cli.util.Constants.DISPLAY;
import static java.util.Optional.of;

/**
 * CmdPomAndProps class description.
 *
 * @author <a href="mailto:bw.opensource@yahoo.com">Bradley Willcott</a>
 *
 * @since 1.1.7
 * @version 2.0.0
 */
public class CmdPomAndProps implements Option
{
    public CmdPomAndProps()
    {
        // NoOp
    }

    @Override
    public Optional<Integer> execute(CmdLine cmd)
    {
        Optional<Integer> rtn = Optional.empty();

        //
        // if '-p' Add pom.xml file
        //
        if (cmd.hasOption('p'))
        {
            Properties props = cmd.getOptionProperties('D');

            try
            {
                loadPom(cmd.pomFile(), props);
            } catch (IOException ex)
            {
                DISPLAY.level(2).println(ex);
                rtn = of(-1);
            }
        }

        return rtn;
    }
}
