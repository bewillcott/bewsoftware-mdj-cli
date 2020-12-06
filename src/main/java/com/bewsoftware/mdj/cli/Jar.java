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

/**
 * Create a 'jar' file.
 *
 * @author <a href="mailto:bw.opensource@yahoo.com">Bradley Willcott</a>
 *
 * @since 0.1
 * @version 1.0.7
 */
import java.io.*;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.zip.Deflater;

import static java.nio.file.Path.of;

public class Jar {

    /**
     * Create a 'jar' file containing the files whose paths are supplied.
     *
     * @param jarFile       The new jar file.
     * @param filePaths     Paths to the files to include.
     * @param jarSourcePath Directory to process.
     * @param manifest      The manifest to include.
     *
     * @throws IOException If any.
     */
    public static void createJAR(final File jarFile, final List<Path> filePaths,
                                 final Path jarSourcePath,
                                 final Manifest manifest) throws IOException {

        // Hold the exceptions.
        List<IOException> exceptions = new ArrayList<>();

        try ( JarOutputStream jos = new JarOutputStream(new BufferedOutputStream(
                new FileOutputStream(jarFile)), manifest))
        {

            jos.setLevel(Deflater.BEST_COMPRESSION);

            filePaths.stream().<File>map(Path::toFile)
                    .filter(name -> name.exists() && !name.isDirectory())
                    .map(File::toPath)
                    .forEachOrdered(filePath ->
                    {
                        try
                        {
                            jos.putNextEntry(new JarEntry(
                                    jarSourcePath.relativize(filePath).toString()));

                            addEntryContent(jos, filePath);
                            jos.closeEntry();
                        } catch (IOException ex)
                        {
                            exceptions.add(ex);
                        }
                    });

            if (!exceptions.isEmpty())
            {
                throw exceptions.remove(0);
            }
        }
    }

    /**
     * Create a new Manifest.
     *
     * @param progName The name of the program.
     *
     * @return the new Manifest.
     */
    public static Manifest getManifest(String progName) {
        Manifest manifest = new Manifest();
        Attributes mainAttribs = manifest.getMainAttributes();
        mainAttribs.put(Attributes.Name.MANIFEST_VERSION, "1.0");
        mainAttribs.put(new Attributes.Name("Created-By"), progName);
        mainAttribs.put(Attributes.Name.CONTENT_TYPE, "text/html");

        return manifest;
    }

    /**
     * For testing purposes only.
     *
     * @param args if any.
     *
     * @throws IOException if any.
     */
    public static void main(String[] args) throws IOException {
        Manifest manifest = getManifest("MDj CLI Test");
        File jarFile = new File("jartest.jar");
        Path[] entries = new Path[2];
        entries[0] = of("manual/index.html");
        entries[1] = of("manual/css/style.css");

        createJAR(jarFile, Arrays.asList(entries), of("manual"), manifest);
    }

    /**
     * Add the contents of the file for the new entry into the jar file.
     *
     * @param jos           The jar file.
     * @param entryFilePath The file to copy in.
     *
     * @throws IOException if any.
     */
    private static void addEntryContent(JarOutputStream jos, Path entryFilePath)
            throws IOException {

        try ( BufferedInputStream bis = new BufferedInputStream(
                new FileInputStream(entryFilePath.toFile())))
        {

            byte[] buffer = new byte[1024];
            int count = -1;

            while ((count = bis.read(buffer)) != -1)
            {
                jos.write(buffer, 0, count);
            }
        }
    }

    /**
     * Not meant to be instantiated.
     */
    private Jar() {
    }
}
