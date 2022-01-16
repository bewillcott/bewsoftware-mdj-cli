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

import static com.bewsoftware.mdj.cli.util.Constants.DISPLAY;
import static com.bewsoftware.mdj.cli.util.Constants.PAGE;
import static com.bewsoftware.mdj.cli.util.Constants.TEXT;
import static com.bewsoftware.mdj.cli.util.GlobalVariables.conf;
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
        String text = conf.iniDoc.getString(PAGE, TEXT, "");
        Matcher m = compile("\\A(?:@@@\\n(?<metablock>.*?)\\n@@@\\n)(?<body>.*)\\z", DOTALL)
                .matcher(text);
        conf.iniDoc.removeSection(PAGE);

        if (m.find())
        {
            String metaBlock = m.group("metablock");
            text = m.group("body");

            DISPLAY.level(3)
                    .appendln("\n============================================")
                    .appendln("file metablock:")
                    .appendln("--------------------------------------------")
                    .appendln(metaBlock)
                    .println("--------------------------------------------");

            Matcher m2 = compile("^\\s*(?<key>\\w+)\\s*:\\s*(?<value>.*?)?\\s*?$", MULTILINE)
                    .matcher(metaBlock);

            while (m2.find())
            {
                String key = m2.group("key");
                String value = m2.group("value");

                DISPLAY.level(3)
                        .append("key = ").appendln(key)
                        .append("value = ").println(value);

                conf.iniDoc.setString(PAGE, key, value);
            }

            DISPLAY.level(3)
                    .println("============================================\n");
        }

        conf.iniDoc.setString(PAGE, TEXT, text);
    }
}
