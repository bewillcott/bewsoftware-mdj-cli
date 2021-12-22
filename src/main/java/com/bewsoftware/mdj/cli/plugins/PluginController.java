/*
 *  File Name:    PluginController.java
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

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * PluginController class description.
 *
 * @author <a href="mailto:bw.opensource@yahoo.com">Bradley Willcott</a>
 *
 * @since 1.1.7
 * @version 1.1.7
 */
public class PluginController
{
    private static final String[] ARRAY_OF_PLUGINS =
    {
        "Stylesheets",
        "MetaBlock",
        "NamedMetaBlocks"
    };

    private static final String PLUGIN_PACKAGE = "com.bewsoftware.mdj.cli.plugins.";

    private final ConcurrentHashMap<String, Plugin> plugins;

    public PluginController()
    {
        this.plugins = new ConcurrentHashMap<>();
        SortedSet<String> classNames = new TreeSet<>();
        classNames.addAll(Arrays.asList(ARRAY_OF_PLUGINS));

        classNames.stream().forEachOrdered((className) ->
        {
            try
            {
                Plugin plugin = (Plugin) Class.forName(PLUGIN_PACKAGE + className)
                        .getDeclaredConstructor().newInstance();

                plugins.put(className, plugin);
            } catch (ClassNotFoundException | NoSuchMethodException | SecurityException
                    | InstantiationException | IllegalAccessException | IllegalArgumentException
                    | InvocationTargetException ex)
            {
                Logger.getLogger(PluginController.class.getName()
                ).log(Level.SEVERE, null, ex);
            }
        });
    }

    public void runPlugin(String className)
    {
        Plugin plugin = plugins.get(className);

        if (plugin != null)
        {
            plugin.execute();
        }
    }
}
