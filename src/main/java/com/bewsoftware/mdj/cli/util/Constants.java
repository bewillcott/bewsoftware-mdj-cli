/*
 *  File Name:    Constants.java
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

package com.bewsoftware.mdj.cli.util;

import com.bewsoftware.mdj.cli.options.util.Cli;

/**
 * Contains various constants.
 *
 * @author <a href="mailto:bw.opensource@yahoo.com">Bradley Willcott</a>
 *
 * @since 1.1.7
 * @version 1.1.7
 */
public class Constants
{
    public static final String COPYRIGHT
            = " This is the MDj Command-line Interface program (aka: mdj-cli).\n"
            + "\n"
            + " Copyright (C) 2020 Bradley Willcott\n"
            + "\n"
            + " mdj-cli is free software: you can redistribute it and/or modify\n"
            + " it under the terms of the GNU General Public License as published by\n"
            + " the Free Software Foundation, either version 3 of the License, or\n"
            + " (at your option) any later version.\n"
            + "\n"
            + " mdj-cli is distributed in the hope that it will be useful,\n"
            + " but WITHOUT ANY WARRANTY; without even the implied warranty of\n"
            + " MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the\n"
            + " GNU General Public License for more details.\n"
            + "\n"
            + " You should have received a copy of the GNU General Public License\n"
            + " along with this program.  If not, see <http://www.gnu.org/licenses/>.\n";

    public static final String DEFAULT_MARKDOWN_EXTENSION = ".md";

    public static final String HELP_FOOTER = "\nYou must use at least one of the following options:"
            + "\n\t'-a', '-c', -i', '-s', '-j', '-m', '-W', or '-h|--help'\n"
            + "\n" + Cli.POM.name + " " + Cli.POM.version
            + "\nCopyright (c) 2020 Bradley Willcott\n"
            + "\nThis program comes with ABSOLUTELY NO WARRANTY; for details use option '-c'."
            + "\nThis is free software, and you are welcome to redistribute it"
            + "\nunder certain conditions; use option '-m', then goto the License page for details.";

    public static final String HELP_HEADER = "\n" + Cli.POM.description + "\n\n";

    public static final String SYNTAX = "java -jar /path/to/mdj-cli-<version>.jar [OPTION]...\n\noptions:";

    private Constants()
    {
    }

}
