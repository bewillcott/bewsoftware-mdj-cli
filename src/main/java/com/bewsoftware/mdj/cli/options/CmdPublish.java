/*
 *  File Name:    CmdPublish.java
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
import com.bewsoftware.mdj.cli.util.MCHttpServer;
import java.util.Optional;

import static com.bewsoftware.mdj.cli.util.Constants.DISPLAY;
import static java.util.Optional.of;

/**
 * Publish HTTP files.
 *
 * @author <a href="mailto:bw.opensource@yahoo.com">Bradley Willcott</a>
 *
 * @since 1.1.7
 * @version 2.0.0
 */
public class CmdPublish implements Option
{

    public CmdPublish()
    {
        // NoOp
    }

    @Override
    public Optional<Integer> execute(CmdLine cmd)
    {
        Optional<Integer> rtn = Optional.empty();

        //
        // if '-P' then, publish http files from either: jarFile or docRootPath.
        //
        // returns 0.
        //
        if (cmd.hasOption('P'))
        {
            DISPLAY.level(0).println("Publishing files...");
            MCHttpServer.execute(cmd);
            rtn = of(0);
        }

        return rtn;
    }
}
