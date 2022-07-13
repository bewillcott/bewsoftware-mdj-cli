/*
 *  File Name:    OptionInterlink.java
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
 * OptionInterlink class description.
 *
 * @author <a href="mailto:bw.opensource@yahoo.com">Bradley Willcott</a>
 *
 * @since 1.1.7
 * @version 1.1.7
 */
public class OptionInterlink
{
    private static final OptionController OPTION_CONTROLLER = new OptionController();

    private OptionInterlink()
    {
    }

    public static Optional<Integer> processCmdAddHttpServer(CmdLine cmd)
    {
        return OPTION_CONTROLLER.runOption("CmdAddHttpServer", cmd);
    }

    public static Optional<Integer> processCmdCopyright(CmdLine cmd)
    {
        return OPTION_CONTROLLER.runOption("CmdCopyright", cmd);
    }

    public static Optional<Integer> processCmdCreateJar(CmdLine cmd)
    {
        return OPTION_CONTROLLER.runOption("CmdCreateJar", cmd);
    }

    public static Optional<Integer> processCmdFailed(CmdLine cmd)
    {
        return OPTION_CONTROLLER.runOption("CmdFailed", cmd);
    }

    public static Optional<Integer> processCmdHelp(CmdLine cmd)
    {
        return OPTION_CONTROLLER.runOption("CmdHelp", cmd);
    }

    public static Optional<Integer> processCmdInputFile(CmdLine cmd)
    {
        return OPTION_CONTROLLER.runOption("CmdInputFile", cmd);
    }

    public static Optional<Integer> processCmdManual(CmdLine cmd)
    {
        return OPTION_CONTROLLER.runOption("CmdManual", cmd);
    }

    public static Optional<Integer> processCmdPomAndProps(CmdLine cmd)
    {
        return OPTION_CONTROLLER.runOption("CmdPomAndProps", cmd);
    }

    public static Optional<Integer> processCmdPublish(CmdLine cmd)
    {
        return OPTION_CONTROLLER.runOption("CmdPublish", cmd);
    }

    public static Optional<Integer> processCmdSource(CmdLine cmd)
    {
        return OPTION_CONTROLLER.runOption("CmdSource", cmd);
    }

    public static Optional<Integer> processCmdUseWrapper(CmdLine cmd)
    {
        return OPTION_CONTROLLER.runOption("CmdUseWrapper", cmd);
    }

    public static Optional<Integer> processCmdVerbosity(CmdLine cmd)
    {
        return OPTION_CONTROLLER.runOption("CmdVerbosity", cmd);
    }

    public static Optional<Integer> processCmdWrapper(CmdLine cmd)
    {
        return OPTION_CONTROLLER.runOption("CmdWrapper", cmd);
    }

    public static Optional<Integer> runMainProcessor(CmdLine cmd)
    {
        return OPTION_CONTROLLER.runOption("MainProcessor", cmd);
    }
}
