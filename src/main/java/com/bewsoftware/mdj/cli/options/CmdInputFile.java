/*
 *  File Name:    CmdInputFile.java
 *  Project Name: bewsoftware-mdj-cli
 *
 *  Copyright (c) 2021-2022 Bradley Willcott
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
import com.bewsoftware.utils.struct.Ref;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import org.apache.commons.io.FilenameUtils;

import static com.bewsoftware.mdj.cli.util.Constants.DEFAULT_INPUT_FILE_EXTENSION;
import static com.bewsoftware.mdj.cli.util.Constants.DEFAULT_OUTPUT_FILE_EXTENSION;
import static com.bewsoftware.mdj.cli.util.GlobalVariables.exception;
import static java.nio.file.Files.exists;
import static java.nio.file.Path.of;

/**
 * Process the InputFile option.
 *
 * @author <a href="mailto:bw.opensource@yahoo.com">Bradley Willcott</a>
 *
 * @since 1.1.7
 * @version 2.0.0
 */
public class CmdInputFile implements Option
{
    public CmdInputFile()
    {
        // NoOp
    }

    private static String appendExtensionToDottedFilename(final String fileName, final String extn)
    {
        return fileName.charAt(fileName.length() - 2) + extn;
    }

    private static boolean fileNameEndsWithDot(final String fileName)
    {
        return fileName.charAt(fileName.length() - 1) == '.';
    }

    private static String processFileExtension(final String fileName, final Ref<Boolean> fileExtensionChanged)
            throws IllegalArgumentException
    {
        String extn = FilenameUtils.getExtension(fileName);
        String fName = fileName;

        if (extn.isEmpty())
        {
            extn = DEFAULT_INPUT_FILE_EXTENSION;

            if (fileNameEndsWithDot(fileName))
            {
                fName = appendExtensionToDottedFilename(fileName, extn);
            } else
            {
                fName += extn;
            }

            fileExtensionChanged.val = true;
        }

        return fName;
    }

    private static boolean processInputFileOption(CmdLine cmd)
    {
        boolean rtn = false;

        if (exists(cmd.inputFile().toPath()))
        {
            String fileName = cmd.inputFile().getName();
            String baseName = FilenameUtils.getBaseName(fileName);
            Ref<Boolean> fileExtensionChanged = Ref.val(false);

            fileName = processFileExtension(fileName, fileExtensionChanged);

            String parent = cmd.inputFile().getParent();

            if (parent != null || fileExtensionChanged.val)
            {
                cmd.inputFile(new File(fileName));
            }

            processRelatedOptions(cmd, parent, baseName);

            rtn = true;
        } else
        {
            exception = new IOException(cmd.inputFile().toPath().toString());
        }

        return rtn;
    }

    private static void processRelatedOptions(CmdLine cmd, String parent, String baseName)
    {
        Path srcPath = of(parent != null ? parent : "");

        if (!cmd.hasOption('s'))
        {
            cmd.source(srcPath);
        }

        if (!cmd.hasOption('d'))
        {
            cmd.destination(srcPath);
        }

        if (!cmd.hasOption('o'))
        {
            cmd.outputFile(new File(baseName + DEFAULT_OUTPUT_FILE_EXTENSION));
        }
    }

    @Override
    public Optional<Integer> execute(final CmdLine cmd)
    {
        Optional<Integer> rtn = Optional.empty();

        //
        // if '-i' switch active, check and set others as necessary.
        //
        if (cmd.hasOption('i') && !processInputFileOption(cmd))
        {
            rtn = Optional.of(-1);
        }

        return rtn;
    }
}
