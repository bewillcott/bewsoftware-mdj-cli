/*
 *  File Name:    Stylesheets.java
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

package com.bewsoftware.mdj.cli.plugins;

import com.bewsoftware.fileio.ini.IniFile;
import com.bewsoftware.property.IniProperty;
import java.util.Date;
import java.util.List;

import static com.bewsoftware.mdj.cli.util.GlobalVariables.conf;
import static java.nio.file.Path.of;

/**
 * Process the Stylesheets.
 *
 * @author <a href="mailto:bw.opensource@yahoo.com">Bradley Willcott</a>
 *
 * @since 1.1.7
 * @version 1.1.7
 */
public class Stylesheets implements Plugin
{
    public static final String STYLESHEET_EXTN = ".css";

    public Stylesheets()
    {
        // NoOp
    }

    private static void processStylesheetNamesFromTheMetaBlock(String systemDate, String cssDir)
    {
        String done = conf.iniDoc.getString("page", systemDate, null);

        if (done == null)
        {
            // Process the '.css' filenames...
            List<IniProperty<String>> keys = conf.iniDoc.getSection("page");

            keys.stream()
                    .filter(key -> key.value().endsWith(STYLESHEET_EXTN))
                    .forEachOrdered(key
                            -> conf.iniDoc.setString("page", key.key(),
                            of(cssDir, key.value()).toString()));

            // Record that we have already processed the '.css' filenames.
            conf.iniDoc.setString("page", systemDate, "done");
        }
    }

    private static void processStylesheetNamesfromTheInifileSection(
            String use, String systemDate, String cssDir)
    {
        if (use != null && !use.isBlank())
        {
            String done = conf.iniDoc.getString(use, systemDate, null);

            if (done == null)
            {
                // Process the '.css' filenames...
                List<IniProperty<String>> keys = conf.iniDoc.getSection(use);

                keys.stream()
                        .filter(key -> key.value().endsWith(STYLESHEET_EXTN))
                        .forEachOrdered(key
                                -> conf.iniDoc.setString(use, key.key(),
                                of(cssDir, key.value()).toString(),
                                key.comment()));

                // Record that we have already processed the '.css' filenames.
                conf.iniDoc.setString(use, systemDate, "done");
            }
        }
    }

    private static String processSystemDate(IniFile conf)
    {
        String systemDate = conf.iniDoc.getString("system", "date", null);

        if (systemDate == null)
        {
            systemDate = new Date().toString();
            conf.iniDoc.setString("system", "date", systemDate);
        }

        return systemDate;
    }

    @Override
    public void execute()
    {
        String use = conf.iniDoc.getString("page", "use", null);
        String cssDir = conf.iniDoc.getString("document", "cssDir", "");

        String systemDate = processSystemDate(conf);
        processStylesheetNamesfromTheInifileSection(use, systemDate, cssDir);
        processStylesheetNamesFromTheMetaBlock(systemDate, cssDir);
    }
}
