/*
 *  File Name:    CmdHelp.java
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
 *  along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.bewsoftware.mdj.cli.options;

import com.bewsoftware.mdj.cli.util.CmdLine;
import java.util.Optional;

import static com.bewsoftware.mdj.cli.util.Constants.HELP_FOOTER;
import static com.bewsoftware.mdj.cli.util.Constants.HELP_HEADER;
import static com.bewsoftware.mdj.cli.util.Constants.SYNTAX;
import static java.util.Optional.of;

/**
 * Display help message.
 *
 * @author <a href="mailto:bw.opensource@yahoo.com">Bradley Willcott</a>
 *
 * @since 1.1.7
 * @version 1.1.7
 */
public class CmdHelp implements Option
{
    public CmdHelp()
    {
        // NoOp
    }

    @Override
    public Optional<Integer> execute(CmdLine cmd)
    {
        Optional<Integer> rtn = Optional.empty();

        //
        // if '-h' or '--help' then, display help message.
        //
        // returns 0.
        //
        if (cmd.hasOption('h'))
        {
            cmd.printHelp(SYNTAX, HELP_HEADER, HELP_FOOTER, true);
            rtn = of(0);
        }

        return rtn;
    }
}
