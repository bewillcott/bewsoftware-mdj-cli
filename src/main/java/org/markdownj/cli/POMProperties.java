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

import java.io.IOException;
import java.util.Properties;

/**
 * Provides access to some of the projects pom.properties.
 * <p>
 * To access the properties:
 * </p>
 * <pre><code>
 * POMProperties pom = POMProperties.INSTANCE;
 * System.out.println(pom.title):
 * </code></pre>
 *
 * @author <a href="mailto:bw.opensource@yahoo.com">Bradley Willcott</a>
 *
 * @since 0.1
 * @version 1.0
 */
public final class POMProperties {

    /**
     * Provides single instance of this class.
     */
    public final static POMProperties INSTANCE = new POMProperties();

    public static void main(String[] args) {
        System.out.println(POMProperties.INSTANCE);
    }
    /**
     * The identifier for this artifact that is unique within
     * the group given by the group ID.
     */
    public final String artifactId;

    /**
     * Project Description
     */
    public final String description;

    /**
     * The filename of the binary output file.
     * <p>
     * This is usually a '.jar' file.
     * </p>
     */
    public final String filename;

    /**
     * Project GroupId
     */
    public final String groupId;

    /**
     * Project Name
     */
    public final String title;

    /**
     * The version of the artifact.
     */
    public final String version;

    private POMProperties() {
        Properties properties = new Properties();
        try
        {
            properties.load(POMProperties.class.getResourceAsStream("/markdownj-cli.properties"));
        } catch (IOException ex)
        {
            throw new RuntimeException("FileIOError", ex);
        }

        title = properties.getProperty("title");
        description = properties.getProperty("description");
        version = properties.getProperty("version");
        artifactId = properties.getProperty("artifactId");
        groupId = properties.getProperty("groupId");
        filename = properties.getProperty("filename");
    }

    @Override
    public String toString() {
        return new StringBuilder(POMProperties.class.getName()).append(":\n")
                .append("  title: ").append(title).append("\n")
                .append("  description: ").append(description).append("\n")
                .append("  version: ").append(version).append("\n")
                .append("  artifactId: ").append(artifactId).append("\n")
                .append("  groupId: ").append(groupId).append("\n").toString();
    }

}
