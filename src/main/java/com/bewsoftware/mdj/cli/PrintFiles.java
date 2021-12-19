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

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import static com.bewsoftware.mdj.cli.Main.DISPLAY;
import static java.nio.file.FileVisitResult.CONTINUE;

/**
 *
 * @author <a href="mailto:bw.opensource@yahoo.com">Bradley Willcott</a>
 *
 * @since 0.1
 * @version 1.0
 */
class PrintFiles extends SimpleFileVisitor<Path>
{

    // Print each directory visited.
    @Override
    public FileVisitResult postVisitDirectory(Path dir,
            IOException exc)
    {
        DISPLAY.format("Directory: %s%n", dir);
        return CONTINUE;
    }

    // Print information about
    // each type of file.
    @Override
    public FileVisitResult visitFile(Path file,
            BasicFileAttributes attr)
    {
        if (attr.isSymbolicLink())
        {
            DISPLAY.format("Symbolic link: %s ", file);
        } else if (attr.isRegularFile())
        {
            DISPLAY.format("Regular file: %s ", file);
        } else
        {
            DISPLAY.format("Other: %s ", file);
        }
        DISPLAY.println("(" + attr.size() + "bytes)");
        return CONTINUE;
    }

    // If there is some error accessing
    // the file, let the user know.
    // If you don't override this method
    // and an error occurs, an IOException
    // is thrown.
    @Override
    public FileVisitResult visitFileFailed(Path file,
            IOException exc)
    {
        DISPLAY.println(exc);
        return CONTINUE;
    }
}
