/*
 *  File Name:    CmdAddHttpServer.java
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

/**
 * This class has not yet been implemented.
 * <p>
 * This is just a hollow place-holder.
 *
 * @author <a href="mailto:bw.opensource@yahoo.com">Bradley Willcott</a>
 *
 * @since 1.0
 * @version 1.0
 */
public class CmdAddHttpServer implements Option
{
    public CmdAddHttpServer()
    {
        // NoOp
    }

    @Override
    public Optional<Integer> execute(CmdLine cmd)
    {
//        //
//        // '-a' jar file creation
//        //
//        // returns 0 - successs
//        //         3 - fail.
//        //
//        if (cmd.hasOption('a'))
//        {
//            if (cmd.hasOption('i')
//                || cmd.hasOption('j')
//                || cmd.hasOption('o')
//                || cmd.hasOption('s')
//                || cmd.hasOption('d')
//                || cmd.hasOption('p')
//                || cmd.hasOption('r')
//                || cmd.hasOption('W')
//                || cmd.hasOption('w'))
//            {
//                String msg = "Too many switches for \"-a\"\n\n";
//
//                cmd.printHelp(msg, SYNTAX, HELP_HEADER, HELP_FOOTER, true);
//                return 3;
//            }
//
//            // Load configuration file data
//            try
//            {
//                loadConf(cmd.docRootPath());
//            } catch (FileNotFoundException ex)
//            {
//                if (vlevel >= 2)
//                {
//                    DISPLAY.println(ex);
//                }
//            }
//
//            return createJarFile(cmd.jarFile(), cmd.jarSourcePath(), vlevel);
//        }

        return Optional.empty();
    }
}
