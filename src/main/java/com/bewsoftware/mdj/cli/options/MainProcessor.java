/*
 *  File Name:    MainProcessor.java
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

import com.bewsoftware.fileio.ini.IniDocument;
import com.bewsoftware.mdj.cli.plugins.PluginInterlink;
import com.bewsoftware.mdj.cli.util.CmdLine;
import com.bewsoftware.mdj.cli.util.FileData;
import com.bewsoftware.mdj.core.MarkdownProcessor;
import com.bewsoftware.mdj.core.TextEditor;
import com.bewsoftware.utils.struct.Ref;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.bewsoftware.mdj.cli.options.util.Cli.getString;
import static com.bewsoftware.mdj.cli.options.util.Cli.processSubstitutions;
import static com.bewsoftware.mdj.cli.util.Constants.DISPLAY;
import static com.bewsoftware.mdj.cli.util.Find.getUpdateList;
import static com.bewsoftware.mdj.cli.util.GlobalVariables.conf;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static java.nio.file.StandardOpenOption.WRITE;
import static java.util.Optional.of;
import static java.util.regex.Pattern.compile;

/**
 * Process all the markdown files.
 *
 * @author <a href="mailto:bw.opensource@yahoo.com">Bradley Willcott</a>
 *
 * @since 1.1.7
 * @version 1.1.8
 */
public class MainProcessor implements Option
{
    public MainProcessor()
    {
        // NoOp
    }

    private static void loadOutputDirs(List<FileData> fileList, Set<Path> outputDirs)
    {
        DISPLAY.level(1).appendln("loadOutputDirs:");

        fileList.forEach(fileData ->
        {

            DISPLAY.appendln(fileData.sourcePath)
                    .append("    ")
                    .appendln(fileData.destinationPath);

            Path parent = fileData.destinationPath.getParent();

            if (parent != null)
            {
                DISPLAY.append("    Destination Parent:")
                        .appendln(parent);

                outputDirs.add(parent);
            }
        });

        DISPLAY.flush();
    }

    private static void processDirectories(Set<Path> outputDirs) throws IOException
    {
        DISPLAY.level(2);

        for (Path dir : outputDirs)
        {
            DISPLAY.println("    " + dir);
            Files.createDirectories(dir);
        }
    }

    private static void processFile(final FileData fileData,
            final Path destDirPath, final boolean wrapper) throws IOException
    {
        StringBuilder sb = new StringBuilder();
        IniDocument iniDoc = conf.iniDoc;
        String template;
        String use = "";

        try ( BufferedReader inReader = Files.newBufferedReader(fileData.sourcePath))
        {
            String line;

            while ((line = inReader.readLine()) != null)
            {
                sb.append(line).append("\n");
            }
        }

        iniDoc.setString("page", "text", sb.toString());

        if (wrapper)
        {
            DISPLAY.level(3)
                    .appendln("\n--------------------------------------------")
                    .println("process wrapper...");

            PluginInterlink.processMetaBlock();
            PluginInterlink.processStylesheets();
            PluginInterlink.processNamedMetaBlocks();
            String preprocessed = processSubstitutions(
                    iniDoc.getString(
                            "page",
                            "text",
                            ""
                    ),
                    use,
                    Ref.val()
            );

            use = iniDoc.getString("page", "use", null);
            template = getString("page", "template", use);
            iniDoc.setString(
                    "page",
                    "content",
                    MarkdownProcessor.convert(
                            processSubstitutions(
                                    preprocessed,
                                    use,
                                    Ref.val()
                            )
                    )
            );

            if (!template.isBlank())
            {
                iniDoc.setString("page", "srcFile", fileData.sourcePath.toString());
                iniDoc.setString("page", "destFile", fileData.destinationPath.toString());
                iniDoc.setString("page", "destDir", destDirPath.toString());
                processTemplate(use, template);
            }
        } else
        {
            iniDoc.setString("page", "content",
                    MarkdownProcessor.convert(
                            iniDoc.getString(
                                    "page",
                                    "text",
                                    ""
                            )
                    ));
        }

        try ( BufferedWriter outWriter
                = Files.newBufferedWriter(
                        fileData.destinationPath, CREATE, TRUNCATE_EXISTING,
                        WRITE))
        {
            DISPLAY.level(3)
                    .appendln("\n--------------------------------------------")
                    .appendln("Write file:")
                    .appendln(fileData.destinationPath)
                    .appendln("page.html:")
                    .appendln(iniDoc.getString("page", "html",
                            "No HTML content."))
                    .println("--------------------------------------------\n");

            outWriter.write(
                    iniDoc.getString(
                            "page",
                            "html",
                            iniDoc.getString(
                                    "page",
                                    "content",
                                    "Error during processing."
                            )
                    )
            );
        }
    }

    private static void processFiles(CmdLine cmd) throws IOException
    {
        //
        // Get files to process
        //
        List<FileData> fileList = getUpdateList(
                cmd.source(),
                cmd.destination(),
                cmd.inputFile(),
                null,
                cmd.hasOption('r'));

        // Process files
        if (!fileList.isEmpty())
        {
            if (singleFileWithOutputFile(cmd, fileList))
            {
                updateFileList(cmd, fileList);
            }

            Set<Path> outputDirs = new TreeSet<>();
            loadOutputDirs(fileList, outputDirs);
            processDirectories(outputDirs);

            for (FileData fileData : fileList)
            {
                processFile(
                        fileData,
                        cmd.destination(),
                        cmd.hasOption('w')
                );
            }
        }
    }

    private static void processTemplate(final String use, final String template) throws IOException
    {
        StringBuilder sbin = new StringBuilder(
                "<!DOCTYPE html>\n"
                + "<!--\n"
                + "Generated by ${program.title}\n"
                + "version: ${program.version}\n"
                + "on ${system.date}\n"
                + "-->\n"
        );

        IniDocument iniDoc = conf.iniDoc;

        Path docRootPath = Path.of(
                iniDoc.getString(
                        "project",
                        "root",
                        ""
                )
        )
                .resolve(
                        iniDoc.getString(
                                "document",
                                "docRootDir",
                                ""
                        )
                )
                .toAbsolutePath();

        Path templatesPath = docRootPath.resolve(
                iniDoc.getString(
                        "document",
                        "templatesDir",
                        ""
                )
        )
                .resolve(template).toAbsolutePath();

        //
        // Set the 'base' path.  <base href="<base>">
        //
        Path srcPath = Path.of(iniDoc.getString("page", "srcFile", ""));
        Path basePath = srcPath.getParent().relativize(docRootPath);
        iniDoc.setString("page", "base", basePath.toString());

        DISPLAY.level(2)
                .appendln("base:")
                .appendln(basePath)
                .appendln("srcFile:")
                .appendln(srcPath)
                .appendln("template:")
                .println(templatesPath);

        try ( BufferedReader inReader = Files.newBufferedReader(templatesPath))
        {
            String line;

            while ((line = inReader.readLine()) != null)
            {
                sbin.append(line).append("\n");
            }
        }

        TextEditor textEd = new TextEditor(
                processSubstitutions(
                        sbin.toString(),
                        use,
                        Ref.val()
                )
        );
        textEd.replaceAllLiteral("\\\\\\$", "$");
        textEd.replaceAllLiteral("\\\\\\[", "[");

        if (!basePath.toString().isBlank())
        {
            //
            // Set the detination file path.
            //
            Path destPath = Path.of(
                    iniDoc.getString("page", "destFile", null)
            );
            Path destDirPath = Path.of(
                    iniDoc.getString("page", "destDir", null)
            );
            String destHTML = destDirPath.relativize(destPath).toString();

            DISPLAY.level(3)
                    .appendln("destPath:")
                    .appendln(destPath)
                    .appendln("destDirPath:")
                    .appendln(destDirPath)
                    .appendln("destHTML:")
                    .println(destHTML);

            Pattern p = compile("href\\=\"(?<ref>#[^\"]*)?\"");

            textEd.replaceAll(p, (Matcher m) ->
            {
                String text = m.group();
                String ref = m.group("ref");

                DISPLAY.level(3)
                        .append("text: ").appendln(text)
                        .append("ref: ").println(ref);

                return text.replace(ref, destHTML + ref);
            });

        }

        iniDoc.setString("page", "html", textEd.toString());
    }

    private static boolean singleFileWithOutputFile(CmdLine cmd, List<FileData> fileList)
    {
        return cmd.outputFile() != null && fileList.size() == 1;
    }

    private static void updateFileList(CmdLine cmd, List<FileData> fileList)
    {
        Path outPath = cmd.outputFile().toPath();

        if (outPath.isAbsolute())
        {
            fileList.get(0).destinationPath = outPath;
        } else
        {
            Path target = fileList.get(0).destinationPath.getParent();

            if (target != null)
            {
                fileList.get(0).destinationPath = target.resolve(outPath);
            } else
            {
                fileList.get(0).destinationPath = outPath;
            }
        }
    }

    @Override
    public Optional<Integer> execute(CmdLine cmd)
    {
        Optional<Integer> rtn = Optional.empty();

        try
        {
            conf.iniDoc.setString("system", "date", new Date().toString());
            processFiles(cmd);
        } catch (IOException ex)
        {
            DISPLAY.level(2).println(ex);
            rtn = of(-1);
        }

        return rtn;
    }

}
