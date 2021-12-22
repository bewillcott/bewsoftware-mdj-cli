/*
 *  File Name:    MetaBlock.java
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

package com.bewsoftware.mdj.cli.plugins;

import java.util.regex.Matcher;

import static com.bewsoftware.mdj.cli.options.util.Cli.conf;
import static com.bewsoftware.mdj.cli.options.util.Cli.vlevel;
import static com.bewsoftware.mdj.cli.util.GlobalVariables.DISPLAY;
import static java.util.regex.Pattern.DOTALL;
import static java.util.regex.Pattern.MULTILINE;
import static java.util.regex.Pattern.compile;

/**
 * Process the file's meta block, if any.
 *
 * @author <a href="mailto:bw.opensource@yahoo.com">Bradley Willcott</a>
 *
 * @since 1.1.7
 * @version 1.1.7
 */
public class MetaBlock implements Plugin
{
    public MetaBlock()
    {
        // NoOp
    }

    @Override
    public void execute()
    {
        String text = conf.iniDoc.getString("page", "text", "");
        Matcher m = compile("\\A(?:@@@\\n(?<metablock>.*?)\\n@@@\\n)(?<body>.*)\\z", DOTALL)
                .matcher(text);
        conf.iniDoc.removeSection("page");

        if (m.find())
        {
            String metaBlock = m.group("metablock");
            text = m.group("body");

            if (vlevel >= 3)
            {
                DISPLAY.println("\n============================================\n"
                        + "file metablock:\n"
                        + "--------------------------------------------\n"
                        + metaBlock
                        + "\n--------------------------------------------");
            }

            Matcher m2 = compile("^\\s*(?<key>\\w+)\\s*:\\s*(?<value>.*?)?\\s*?$", MULTILINE)
                    .matcher(metaBlock);

            while (m2.find())
            {
                String key = m2.group("key");
                String value = m2.group("value");

                if (vlevel >= 3)
                {
                    DISPLAY.println("key = " + key + "\n"
                            + "value = " + value);
                }

                conf.iniDoc.setString("page", key, value);
            }

            if (vlevel >= 3)
            {
                DISPLAY.println("============================================\n");
            }

        }

        conf.iniDoc.setString("page", "text", text);
    }
}
