/*
 *  File Name:    CmdCopyright.java
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
import java.util.Optional;

import static com.bewsoftware.mdj.cli.Main.COPYRIGHT;
import static com.bewsoftware.mdj.cli.Main.DISPLAY;
import static java.util.Optional.of;

/**
 * Display Copyright notice.
 *
 * @author <a href="mailto:bw.opensource@yahoo.com">Bradley Willcott</a>
 *
 * @since 1.1.7
 * @version 1.1.7
 */
public class CmdCopyright implements Option
{

    public CmdCopyright()
    {
        // NoOp
    }

    @Override
    public Optional<Integer> execute(CmdLine cmd)
    {
        Optional<Integer> rtn = Optional.empty();

        //
        // if '-c' then, display Copyright notice.
        //
        // returns 0.
        //
        if (cmd.hasOption('c'))
        {
            DISPLAY.println(COPYRIGHT);
            rtn = of(0);
        }

        return rtn;
    }
}
