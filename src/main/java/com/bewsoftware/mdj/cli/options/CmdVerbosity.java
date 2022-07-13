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
 *  along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.bewsoftware.mdj.cli.options;

import com.bewsoftware.mdj.cli.util.CmdLine;
import java.util.Optional;

import static com.bewsoftware.mdj.cli.util.Constants.DISPLAY;
import static com.bewsoftware.mdj.cli.util.GlobalVariables.vlevel;

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

        DISPLAY.level(2)
                .append("input: |").append(cmd.inputFile()).appendln("|")
                .append("output: |").append(cmd.outputFile()).appendln("|")
                .append("source: |").append(cmd.source()).appendln("|")
                .append("destination: |").append(cmd.destination()).appendln("|")
                .append("recursive: |").append(cmd.hasOption('r')).appendln("|")
                .append("wrapper: |").append(cmd.hasOption('w')).appendln("|")
                .append("initialise: |").append(cmd.hasOption('W')).appendln("|")
                .append("docRootDir: |").append(cmd.docRootPath()).appendln("|")
                .append("jar: |").append(cmd.hasOption('j')).appendln("|")
                .append("jarFilename: |").append(cmd.jarFile()).appendln("|")
                .append("jarSrcDir: |").append(cmd.jarSourcePath()).appendln("|")
                .append("pomFile: |").append(cmd.pomFile()).println("|");

        if (cmd.verbosity() >= 2 && cmd.hasOption('D'))
        {
            DISPLAY.level(2);

            cmd.getOptionProperties('D').forEach((key, value)
                    -> DISPLAY.append(key).append(": |").append(value).appendln("|")
            );

            DISPLAY.flush();
        }

        DISPLAY.level(1)
                .append("verbose: |").append(cmd.hasOption('v')).appendln("|")
                .append("verbose level: |").append(vlevel).println("|");

        return rtn;
    }
}
