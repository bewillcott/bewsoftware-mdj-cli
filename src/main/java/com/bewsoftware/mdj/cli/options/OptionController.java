/*
 *  File Name:    OptionController.java
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
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class instantiates all the options and stores them for later use.
 *
 * @author <a href="mailto:bw.opensource@yahoo.com">Bradley Willcott</a>
 *
 * @since 1.1.7
 * @version 1.1.7
 */
public class OptionController
{
    private static final String[] ARRAY_OF_OPTIONS =
    {
        "CmdFailed",
        "CmdHelp",
        "CmdCopyright",
        "CmdManual",
        "CmdPublish",
        "CmdInputFile",
        "CmdSource",
        "CmdVerbosity",
        "CmdCreateJar",
        "CmdWrapper",
        "CmdPomAndProps",
        "CmdUseWrapper"
    };

    private static final String OPTION_PACKAGE = "com.bewsoftware.mdj.cli.options.";

    private final ConcurrentHashMap<String, Option> options;

    public OptionController()
    {
        this.options = new ConcurrentHashMap<>();
        SortedSet<String> classNames = new TreeSet<>();
        classNames.addAll(Arrays.asList(ARRAY_OF_OPTIONS));

        classNames.stream().forEachOrdered((className) ->
        {
            try
            {
                Option option = (Option) Class.forName(OPTION_PACKAGE + className)
                        .getDeclaredConstructor().newInstance();

                options.put(className, option);
            } catch (ClassNotFoundException | NoSuchMethodException | SecurityException
                    | InstantiationException | IllegalAccessException | IllegalArgumentException
                    | InvocationTargetException ex)
            {
                Logger.getLogger(OptionController.class.getName()
                ).log(Level.SEVERE, null, ex);
            }
        });
    }

    public Integer runOption(String className, CmdLine cmd)
    {
        Integer rtn = null;
        Option option = options.get(className);

        if (option != null)
        {
            rtn = option.execute(cmd);
        }

        return rtn;
    }
}
