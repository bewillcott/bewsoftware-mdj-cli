/*
 *  File Name:    PluginInterlink.java
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

/**
 * Provide simple connectivity between calling method and required plugin.
 * <p>
 * This is to provide some an intermediate layer of separation.
 *
 * @author <a href="mailto:bw.opensource@yahoo.com">Bradley Willcott</a>
 *
 * @since 1.1.7
 * @version 1.1.7
 */
public class PluginInterlink
{
    private static final PluginController PLUGIN_CONTROLLER = new PluginController();

    private PluginInterlink()
    {
    }

    public static void processMetaBlock()
    {
        PLUGIN_CONTROLLER.runPlugin("MetaBlock");
    }

    public static void processNamedMetaBlocks()
    {
        PLUGIN_CONTROLLER.runPlugin("NamedMetaBlocks");
    }

    public static void processStylesheets()
    {
        PLUGIN_CONTROLLER.runPlugin("Stylesheets");
    }

}
