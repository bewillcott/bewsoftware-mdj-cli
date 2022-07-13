/*
 *  File Name:    CmdFailed.java
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

import com.bewsoftware.mdj.cli.util.CmdLine;
import java.util.Optional;

import static com.bewsoftware.mdj.cli.util.Constants.HELP_FOOTER;
import static com.bewsoftware.mdj.cli.util.Constants.HELP_HEADER;
import static com.bewsoftware.mdj.cli.util.Constants.SYNTAX;
import static java.util.Optional.of;

/**
 * Check to see if the processing of the command-line caused an error condition.
 * <p>
 * If it has, then process it.
 *
 * @author <a href="mailto:bw.opensource@yahoo.com">Bradley Willcott</a>
 *
 * @since 1.1.7
 * @version 1.1.9
 */
public class CmdFailed implements Option
{

    public CmdFailed()
    {
        // NoOp
    }

    @Override
    public Optional<Integer> execute(CmdLine cmd)
    {
        Optional<Integer> rtn = Optional.empty();

        // check whether the command line was valid, and if it wasn't,
        // display usage information and exit.
        if (!cmd.success())
        {

            StringBuilder sb = new StringBuilder();

            // print out specific error messages describing the problems
            // with the command line, THEN print usage, THEN print full
            // help.  This is called "beating the user with a clue stick."
            cmd.exceptions().forEach(ex -> sb.append(ex).append('\n'));

            cmd.printHelp(sb.toString(), SYNTAX, HELP_HEADER,
                    HELP_FOOTER, true);

            rtn = of(-1);
        }

        return rtn;
    }

}
