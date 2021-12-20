/*
 * This file is part of the MDj Command-line Interface program
 * (aka: mdj-cli).
 *
 * Copyright (C) 2020 Bradley Willcott
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
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.bewsoftware.mdj.cli;

import com.bewsoftware.utils.io.ConsoleIO;
import com.bewsoftware.utils.io.Display;
import java.io.IOException;
import java.util.Optional;

import static com.bewsoftware.mdj.cli.options.OptionInterlink.*;
import static java.lang.System.exit;

/**
 *
 * @author <a href="mailto:bw.opensource@yahoo.com">Bradley Willcott</a>
 *
 * @since 0.1
 * @version 1.1.0
 */
public class Main
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

    public static final Display DISPLAY = ConsoleIO.consoleDisplay("");

    public static final String HELP_FOOTER = "\nYou must use at least one of the following options:"
            + "\n\t'-a', '-c', -i', '-s', '-j', '-m', '-W', or '-h|--help'\n"
            + "\n" + Cli.POM.name + " " + Cli.POM.version
            + "\nCopyright (c) 2020 Bradley Willcott\n"
            + "\nThis program comes with ABSOLUTELY NO WARRANTY; for details use option '-c'."
            + "\nThis is free software, and you are welcome to redistribute it"
            + "\nunder certain conditions; use option '-m', then goto the License page for details.";

    public static final String HELP_HEADER = "\n" + Cli.POM.description + "\n\n";

    public static final String SYNTAX = "java -jar /path/to/mdj-cli-<version>.jar [OPTION]...\n\noptions:";

    /**
     * Called from either {@link #main(java.lang.String[]) main()} or another
     * process.
     *
     * @param args Arguments to configure this cycle of processing.
     *
     * @return The exit code.
     *
     * @throws IOException if any.
     *
     * @since 1.0.4
     * @version 1.1.7
     */
    public static int execute(String[] args) throws IOException
    {

        //
        // Process command-line
        //
        CmdLine cmd = new MyCmdLine(args);
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
//        //
//        // '-a' jar file creation
//        //
//        // returns 0 - successs
//        //         3 - fail.
//        //
//        if (cmd.hasOption('a'))
//        {
//            if (cmd.hasOption('i')
//                || cmd.hasOption('j')
//                || cmd.hasOption('o')
//                || cmd.hasOption('s')
//                || cmd.hasOption('d')
//                || cmd.hasOption('p')
//                || cmd.hasOption('r')
//                || cmd.hasOption('W')
//                || cmd.hasOption('w'))
//            {
//                String msg = "Too many switches for \"-a\"\n\n";
//
//                cmd.printHelp(msg, SYNTAX, HELP_HEADER, HELP_FOOTER, true);
//                return 3;
//            }
//
//            // Load configuration file data
//            try
//            {
//                loadConf(cmd.docRootPath());
//            } catch (FileNotFoundException ex)
//            {
//                if (vlevel >= 2)
//                {
//                    DISPLAY.println(ex);
//                }
//            }
//
//            return createJarFile(cmd.jarFile(), cmd.jarSourcePath(), vlevel);
//        }
//
//        if (result == null)
//        {
//            conf.iniDoc.setString("system", "date", new Date().toString());
//            processFiles(cmd);
//        }
//
//        return result == null ? 0 : result;
        return result.orElse(0);
    }

    /**
     * Executed from command-line.
     *
     * @param args the command line arguments
     *
     * @throws IOException if any.
     */
    public static void main(String[] args) throws IOException
    {
        exit(execute(args));
    }

}
