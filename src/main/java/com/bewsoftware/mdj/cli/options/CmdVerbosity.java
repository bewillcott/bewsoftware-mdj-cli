/*
 *  File Name:    CmdVerbosity.java
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

import static com.bewsoftware.mdj.cli.Cli.vlevel;
import static com.bewsoftware.mdj.cli.Main.DISPLAY;

/**
 * CmdVerbosity class description.
 *
 * @author <a href="mailto:bw.opensource@yahoo.com">Bradley Willcott</a>
 *
 * @since 1.1.7
 * @version 1.1.7
 */
public class CmdVerbosity implements Option
{
    public CmdVerbosity()
    {
        // NoOp
    }

    @Override
    @SuppressWarnings("fallthrough")
    public Optional<Integer> execute(CmdLine cmd)
    {
        Optional<Integer> rtn = Optional.empty();

        vlevel = cmd.verbosity();

        switch (vlevel)
        {
            case 3:
            case 2:
                DISPLAY.println("input: |" + cmd.inputFile() + "|");
                DISPLAY.println("output: |" + cmd.outputFile() + "|");
                DISPLAY.println("source: |" + cmd.source() + "|");
                DISPLAY.println("destination: |" + cmd.destination() + "|");
                DISPLAY.println("recursive: |" + cmd.hasOption('r') + "|");
                DISPLAY.println("wrapper: |" + cmd.hasOption('w') + "|");
                DISPLAY.println("initialise: |" + cmd.hasOption('W') + "|");
                DISPLAY.println("docRootDir: |" + cmd.docRootPath() + "|");
                DISPLAY.println("jar: |" + cmd.hasOption('j') + "|");
                DISPLAY.println("jarFilename: |" + cmd.jarFile() + "|");
                DISPLAY.println("jarSrcDir: |" + cmd.jarSourcePath() + "|");
                DISPLAY.println("pomFile: |" + cmd.pomFile() + "|");

                if (cmd.hasOption('D'))
                {
                    cmd.getOptionProperties('D').forEach((key, value)
                            -> DISPLAY.println(key + ": |" + value + "|")
                    );
                }

            case 1:
                DISPLAY.println("verbose: |" + cmd.hasOption('v') + "|");
                DISPLAY.println("verbose level: |" + vlevel + "|");
                break;

            default:
        }

        return rtn;
    }
}
