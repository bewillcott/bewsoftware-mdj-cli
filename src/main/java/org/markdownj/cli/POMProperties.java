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
 * <hr>
 * <pre><code>
 * POMProperties pom = POMProperties.INSTANCE;
 * System.out.println(pom.title):
 * </code></pre>
 * <hr>
 *
 * @author <a href="mailto:bw.opensource@yahoo.com">Bradley Willcott</a>
 *
 * @since 0.1
 * @version 1.0
 */
public final class POMProperties {

    /**
     * Set this to the file you are using.
     * <p>
     * Source location of file is usually: {@code ${basedir}/src/main/resources/}
     * <p>
     * Configure your {@code pom.xml} file:
     * <hr>
     * <pre><code>
     *    &lt;resources&gt;
     *        &lt;resource&gt;
     *           &lt;directory&gt;src/main/resources&lt;/directory&gt;
     *           &lt;filtering&gt;true&lt;/filtering&gt;
     *           &lt;includes&gt;
     *               &lt;include&gt;**&#47;pom.properties&lt;/include&gt;
     *           &lt;/includes&gt;
     *       &lt;/resource&gt;
     *       &lt;resource&gt;
     *           &lt;directory&gt;src/main/resources&lt;/directory&gt;
     *           &lt;filtering&gt;false&lt;/filtering&gt;
     *           &lt;excludes&gt;
     *               &lt;exclude&gt;**&#47;pom.properties&lt;/exclude&gt;
     *           &lt;/excludes&gt;
     *       &lt;/resource&gt;
     *   &lt;/resources&gt;
     * </code></pre>
     * <hr>
     */
    public static final String PROPERTIES_FILENAME = "/pom.properties";

    /**
     * Provides single instance of this class.
     */
    public static final POMProperties INSTANCE = new POMProperties();

    /**
     * For testing purposes.
     *
     * @param args Ignored.
     */
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
            properties.load(POMProperties.class.getResourceAsStream(PROPERTIES_FILENAME));
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
                .append("  groupId: ").append(groupId).append("\n")
                .append("  artifactId: ").append(artifactId).append("\n")
                .append("  version: ").append(version).append("\n")
                .append("  filename: ").append(filename).append("\n").toString();
    }
}
