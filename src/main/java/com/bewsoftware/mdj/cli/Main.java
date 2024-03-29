/*
 * This file is part of the MDj Command-line Interface program
 * (aka: mdj-cli).
 *
 * Copyright (C) 2020-2022 Bradley Willcott
 *
 * mdj-cli is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * mdj-cli is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.bewsoftware.mdj.cli;

import com.bewsoftware.mdj.cli.util.CmdLine;
import com.bewsoftware.mdj.cli.util.MyCmdLine;
import java.util.Optional;

import static com.bewsoftware.mdj.cli.options.OptionInterlink.*;
import static com.bewsoftware.mdj.cli.util.Constants.DISPLAY;
import static com.bewsoftware.mdj.cli.util.GlobalVariables.exception;
import static java.lang.System.exit;

/**
 *
 * @author <a href="mailto:bw.opensource@yahoo.com">Bradley Willcott</a>
 *
 * @since 0.1
 * @version 2.0.0
 */
public class Main
{
    private Main()
    {
    }

    /**
     * Called from either {@link #main(java.lang.String[]) main()} or another
     * process.
     *
     * @param args Arguments to configure this cycle of processing.
     *
     * @return The exit code.
     *
     * @since 1.0.4
     * @version 1.1.7
     */
    public static int execute(String[] args)
    {
        //
        // Process command-line
        //
        CmdLine cmd = new MyCmdLine(args);
        DISPLAY.debugLevel(cmd.verbosity());

        Optional<Integer> result;

        result = processCmdFailed(cmd)
                .or(() -> processCmdHelp(cmd))
                .or(() -> processCmdCopyright(cmd))
                .or(() -> processCmdManual(cmd))
                .or(() -> processCmdPublish(cmd))
                .or(() -> processCmdInputFile(cmd))
                .or(() -> processCmdSource(cmd))
                .or(() -> processCmdVerbosity(cmd))
                .or(() -> processCmdCreateJar(cmd))
                .or(() -> processCmdWrapper(cmd))
                .or(() -> processCmdPomAndProps(cmd))
                .or(() -> processCmdUseWrapper(cmd))
                .or(() -> runMainProcessor(cmd));

        if (result.isPresent() && result.get() != 0 && exception != null)
        {
            DISPLAY.level(0).println(exception);
        }

        return result.orElse(0);
    }

    /**
     * Executed from command-line.
     *
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {
        exit(execute(args));
    }
}
