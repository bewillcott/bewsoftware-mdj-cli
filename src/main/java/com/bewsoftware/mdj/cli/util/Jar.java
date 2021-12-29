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
package com.bewsoftware.mdj.cli.util;

/**
 * Create a 'jar' file.
 *
 * @author <a href="mailto:bw.opensource@yahoo.com">Bradley Willcott</a>
 *
 * @since 0.1
 * @version 1.0.14
 */
import com.bewsoftware.fileio.ini.IniFile;
import com.bewsoftware.httpserver.HTTPServer;
import java.io.*;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.zip.Deflater;

import static com.bewsoftware.fileio.BEWFiles.getResource;
import static com.bewsoftware.mdj.cli.util.Constants.DISPLAY;
import static com.bewsoftware.mdj.cli.util.Constants.POM;
import static com.bewsoftware.mdj.cli.util.Find.getFileList;
import static com.bewsoftware.mdj.cli.util.GlobalVariables.conf;

/**
 * Utility class providing static methods to work with JAR files.
 *
 * @author <a href="mailto:bw.opensource@yahoo.com">Bradley Willcott</a>
 *
 * @since 0.1
 * @version 1.1.7
 */
public class Jar
{
    /**
     * Not meant to be instantiated.
     */
    private Jar()
    {
    }

    /**
     * Add the HTTP Server to an existing 'jar' file.
     * <p>
     * TODO Finish this.
     *
     * @param jarFile      The existing jar file.
     * @param jarFilePaths Jar file paths to include.
     * @param manifest     The manifest to include.
     *
     * @throws IOException if any.
     */
    public static void addHttpServer(
            final File jarFile,
            final List<Path> jarFilePaths,
            final Manifest manifest
    ) throws IOException
    {
        createJAR(
                jarFile,
                jarFilePaths,
                null,
                null,
                null,
                manifest
        );
    }

    /**
     * Create a 'jar' file containing the files whose paths are supplied.
     *
     * @param jarFile        The new jar file.
     * @param jarFilePaths   Jar file paths to include.
     * @param jarFileDirPath Jar file directory path.
     * @param filePaths      Paths to the files to include.
     * @param fileDirPath    Directory to process.
     * @param manifest       The manifest to include.
     *
     * @throws IOException if any.
     */
    public static void createJAR(
            final File jarFile,
            final List<Path> jarFilePaths,
            final Path jarFileDirPath,
            final List<Path> filePaths,
            final Path fileDirPath,
            final Manifest manifest
    ) throws IOException
    {

        DISPLAY.level(3)
                .append("jarFile: |").append(jarFile).println("|");

        List<IOException> exceptions = new ArrayList<>();

        try (JarOutputStream jos
                = new JarOutputStream(
                        new BufferedOutputStream(
                                new FileOutputStream(jarFile)),
                        manifest
                ))
        {

            jos.setLevel(Deflater.BEST_COMPRESSION);
            copyFilesFromMDjCLIJarFile(jarFilePaths, jos, jarFileDirPath, exceptions);
            copyFilesFromUserDocsDirectory(filePaths, jos, fileDirPath, exceptions);

            if (!exceptions.isEmpty())
            {
                throw exceptions.remove(0);
            }
        }
    }

    /**
     * Create jar file.
     *
     * @param jarFile       Output file name.
     * @param jarSourcePath Path of directory to process.
     *
     * @return Always '0'.
     *
     * @throws IOException        if any.
     * @throws URISyntaxException if any.
     */
    public static int createJarFile(
            final File jarFile,
            final Path jarSourcePath
    ) throws IOException, URISyntaxException
    {

        // Get source directory from jar file.
        Path jarDirPath = getResource(Jar.class, "/docs/jar").toAbsolutePath();

        DISPLAY.level(2)
                .append("srcDirPath: ").appendln(jarDirPath)
                .append("srcDirPath exists: ").println(Files.exists(jarDirPath));

        SortedSet<Path> jarFileSet = getFileList(jarDirPath, "*", true);
        SortedSet<Path> fileSet = getFileList(jarSourcePath, "*", true);
        Manifest manifest = getManifest(POM, conf);

        Jar.createJAR(
                jarFile,
                new ArrayList<>(jarFileSet),
                jarDirPath,
                new ArrayList<>(fileSet),
                jarSourcePath,
                manifest
        );

        return 0;
    }

    /**
     * Create a new Manifest.
     *
     * @param pom  The program's POM properties.
     * @param conf The document's configuration data.
     *
     * @return the new Manifest.
     */
    public static Manifest getManifest(final MCPOMProperties pom, final IniFile conf)
    {
        Manifest manifest = getManifest(pom.name + " (" + pom.version + ")");

        if (conf != null && conf.iniDoc.containsSection("MANIFEST.mf"))
        {
            Attributes mainAttribs = manifest.getMainAttributes();

            conf.iniDoc.getSection("MANIFEST.mf").forEach(prop
                    -> mainAttribs.put(
                            new Attributes.Name(prop.key()),
                            prop.value()
                    )
            );
        }

        return manifest;
    }

    /**
     * Create a new Manifest.
     * <p>
     * Helper method that calls the following:
     * {@link #getManifest(java.lang.String, java.util.jar.Manifest) getManifest(progname, null)}.
     *
     * @param progname The program name - should include version data:
     *                 {@code <program name> (<version>)}
     *
     * @return the new Manifest.
     */
    public static Manifest getManifest(final String progname)
    {
        return getManifest(progname, null);
    }

    /**
     * Modify an existing manifest, or create a new one.
     *
     * @param progname The program name - should include version data:
     *                 {@code <program name> (<version>)}
     * @param manifest An existing manifest, or {@code null}.
     *
     * @return the new Manifest.
     */
    public static Manifest getManifest(final String progname, Manifest manifest)
    {

        if (manifest == null)
        {
            manifest = new Manifest();
        }

        Attributes mainAttribs = manifest.getMainAttributes();
        mainAttribs.put(Attributes.Name.MANIFEST_VERSION, "1.0");
        mainAttribs.put(new Attributes.Name("Created-By"), progname);
        mainAttribs.put(Attributes.Name.CONTENT_TYPE, "text/html");
        mainAttribs.put(new Attributes.Name("Build-Jdk-Spec"), "12");
        mainAttribs.put(new Attributes.Name("HTTPServer-version"), HTTPServer.VERSION);
        mainAttribs.put(Attributes.Name.MAIN_CLASS, "com.bewsoftware.httpserver.HTTPServer");

        return manifest;
    }

    /**
     * Add the contents of the file for the new entry into the jar file.
     *
     * @param jos           The jar file.
     * @param entryFilePath The file to copy in.
     *
     * @throws IOException if any.
     */
    private static void addEntryContent(
            final JarOutputStream jos,
            final Path entryFilePath
    ) throws IOException
    {
        try (BufferedInputStream bis = new BufferedInputStream(
                Files.newInputStream(entryFilePath)))
        {
            byte[] buffer = new byte[1024];
            int count;

            while ((count = bis.read(buffer)) != -1)
            {
                jos.write(buffer, 0, count);
            }
        }
    }

    private static void copyFilesFromMDjCLIJarFile(
            final List<Path> jarFilePaths,
            final JarOutputStream jos,
            final Path jarFileDirPath,
            final List<IOException> exceptions
    )
    {
        jarFilePaths
                .stream()
                .forEachOrdered(
                        jarFilePath ->
                {
                    try
                    {
                        jos.putNextEntry(
                                new JarEntry(
                                        jarFileDirPath
                                                .relativize(jarFilePath)
                                                .toString()
                                )
                        );

                        addEntryContent(jos, jarFilePath);
                        jos.closeEntry();
                    } catch (IOException ex)
                    {
                        exceptions.add(ex);
                    }
                });
    }

    private static void copyFilesFromUserDocsDirectory(
            final List<Path> filePaths,
            final JarOutputStream jos,
            final Path fileDirPath,
            final List<IOException> exceptions
    )
    {
        filePaths.stream()
                .filter(
                        name -> Files.exists(name)
                        && !Files.isDirectory(name)
                )
                .forEachOrdered(
                        filePath ->
                {
                    try
                    {
                        jos.putNextEntry(
                                new JarEntry(
                                        fileDirPath
                                                .relativize(filePath)
                                                .toString()
                                )
                        );

                        addEntryContent(jos, filePath);
                        jos.closeEntry();
                    } catch (IOException ex)
                    {
                        exceptions.add(ex);
                    }
                });
    }
}
