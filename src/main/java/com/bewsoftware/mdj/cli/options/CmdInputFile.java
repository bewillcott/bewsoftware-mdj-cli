/*
 *  File Name:    CmdInputFile.java
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

import com.bewsoftware.mdj.cli.util.CmdLine;
import com.bewsoftware.utils.struct.Ref;
import java.io.File;
import java.nio.file.Path;
import java.util.Optional;
import org.apache.commons.io.FilenameUtils;

import static com.bewsoftware.mdj.cli.util.Constants.DEFAULT_MARKDOWN_EXTENSION;
import static java.nio.file.Path.of;

/**
 * Process the InputFile option.
 *
 * @author <a href="mailto:bw.opensource@yahoo.com">Bradley Willcott</a>
 *
 * @since 1.1.7
 * @version 1.1.7
 */
public class CmdInputFile implements Option
{
    public CmdInputFile()
    {
        // NoOp
    }

    private static boolean fileNameEndsWithDot(String fileName)
    {
        return fileName.charAt(fileName.length() - 1) == '.';
    }

    @Override
    public Optional<Integer> execute(CmdLine cmd)
    {
        Optional<Integer> rtn = Optional.empty();

        //
        // if '-i' switch active, check and set others as necessary.
        //
        if (cmd.hasOption('i'))
        {
            processInputFileOption(cmd);
        }

        return rtn;
    }

    private String appendExtensionToDottedFilename(String fileName, String extn)
    {
        return fileName.charAt(fileName.length() - 2) + extn;
    }

    private String processFileExtension(String fileName, Ref<Boolean> fileExtensionChanged)
            throws IllegalArgumentException
    {
        String extn = FilenameUtils.getExtension(fileName);

        if (extn.isEmpty())
        {
            extn = DEFAULT_MARKDOWN_EXTENSION;

            if (fileNameEndsWithDot(fileName))
            {
                fileName = appendExtensionToDottedFilename(fileName, extn);
            } else
            {
                fileName += extn;
            }

            fileExtensionChanged.val = true;
        }

        return fileName;
    }

    private void processInputFileOption(CmdLine cmd)
    {
        String fileName = cmd.inputFile().getName();
        String baseName = FilenameUtils.getBaseName(fileName);
        Ref<Boolean> fileExtensionChanged = Ref.val(Boolean.FALSE);

        fileName = processFileExtension(fileName, fileExtensionChanged);

        String parent = cmd.inputFile().getParent();

        if (parent != null || fileExtensionChanged.val)
        {
            cmd.inputFile(new File(fileName));
        }

        processRelatedOptions(cmd, parent, baseName);
    }

    private void processRelatedOptions(CmdLine cmd, String parent, String baseName)
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
            cmd.outputFile(new File(baseName + ".html"));
        }
    }
}
