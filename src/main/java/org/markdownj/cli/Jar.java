/*
 * This file is part of the Markdownj Command-line Interface program
 * (aka: markdownj-cli).
 *
 * Copyright (C) 2020 Bradley Willcott
 *
 * markdownj-cli is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * markdownj-cli is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.markdownj.cli;

/**
 *
 * @author <a href="mailto:bw.opensource@yahoo.com">Bradley Willcott</a>
 *
 * @since 0.1
 * @version 1.0
 */
import java.io.*;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.zip.Deflater;

public class Jar {

    public static void createJAR(String jarFileName, Iterable<Path> jarEntries,
                                 Manifest manifest) throws IOException {

        try ( JarOutputStream jos = new JarOutputStream(new BufferedOutputStream(
                new FileOutputStream(jarFileName)), manifest))
        {

            jos.setLevel(Deflater.BEST_COMPRESSION);

            for (Path jarEntry : jarEntries)
            {
                File entryFile = jarEntry.toFile();

                if (!entryFile.exists())
                {
                    return;
                }

                JarEntry je = new JarEntry(jarEntry.toString());
                jos.putNextEntry(je);
                addEntryContent(jos, jarEntry.toString());
                jos.closeEntry();
            }
        }
    }

    public static Manifest getManifest() {
        Manifest manifest = new Manifest();
        Attributes mainAttribs = manifest.getMainAttributes();
        mainAttribs.put(Attributes.Name.MANIFEST_VERSION, "1.0");
        mainAttribs.put(new Attributes.Name("Created-By"), "Markdownj CLI 0.1.15");
        mainAttribs.put(Attributes.Name.CONTENT_TYPE, "text/html");

        return manifest;
    }

    public static void main(String[] args) throws Exception {
        Manifest manifest = getManifest();
        String jarFileName = "jartest.jar";
        Path[] entries = new Path[2];
        entries[0] = Path.of("manual/index.html");
        entries[1] = Path.of("manual/css/style.css");

        createJAR(jarFileName, Arrays.asList(entries), manifest);
    }

    private static void addEntryContent(JarOutputStream jos, String entryFileName)
            throws IOException, FileNotFoundException {
        try ( BufferedInputStream bis = new BufferedInputStream(new FileInputStream(
                entryFileName)))
        {

            byte[] buffer = new byte[1024];
            int count = -1;

            while ((count = bis.read(buffer)) != -1)
            {
                jos.write(buffer, 0, count);
            }
        }
    }

    private static Attributes getAttribute(String name, String value) {
        Attributes a = new Attributes();
        Attributes.Name attribName = new Attributes.Name(name);
        a.put(attribName, value);
        return a;
    }

    private Jar() {
    }
}
