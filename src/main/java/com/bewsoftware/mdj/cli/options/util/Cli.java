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
package com.bewsoftware.mdj.cli.options.util;

import com.bewsoftware.fileio.ini.IniDocument;
import com.bewsoftware.fileio.ini.IniFile;
import com.bewsoftware.fileio.ini.IniFileFormatException;
import com.bewsoftware.mdj.cli.util.MCPOMProperties;
import com.bewsoftware.mdj.core.TextEditor;
import com.bewsoftware.utils.struct.Ref;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;
import java.util.regex.Pattern;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import static com.bewsoftware.mdj.cli.util.GlobalVariables.DISPLAY;
import static com.bewsoftware.mdj.cli.util.MCPOMProperties.INSTANCE;
import static java.nio.file.LinkOption.NOFOLLOW_LINKS;
import static java.nio.file.Path.of;

/**
 *
 * @author <a href="mailto:bw.opensource@yahoo.com">Bradley Willcott</a>
 *
 * @since 0.1
 * @version 1.1.7
 */
public class Cli
{

    /**
     * The name of the configuration INI file.
     */
    public static final String CONF_FILENAME = "mdj-cli.ini";

    /**
     * The single instance of the {@link MCPOMProperties} class.
     */
    public static final MCPOMProperties POM = INSTANCE;

    /**
     * The configuration INI file.
     */
    public static IniFile conf;

    /**
     * The verbosity level.
     */
    public static int vlevel;

    private static final Pattern SUBSTITUTION_PATTERN
            = Pattern.compile("(?<!\\\\)(?:\\$\\{(?<group>\\w+)[.](?<key>\\w+)\\})");

    /**
     * Not meant to be instantiated.
     */
    private Cli()
    {
    }

    /**
     * Convenience method to access the iniDoc
     * {@link IniDocument#getString(java.lang.String, java.lang.String, java.lang.String) getString()}.
     *
     * @param section label.
     * @param key     name.
     * @param use     Alternate section label.
     *
     * @return result.
     */
    public static String getString(final String section, final String key, final String use)
    {
        return conf.iniDoc.getString(section, key, conf.iniDoc.getString(use, key, ""));
    }

    /**
     * Load the configuration file: {@link #CONF_FILENAME}
     *
     * @param srcDirPath Initial directory to look for the file.
     *
     * @throws IOException            If any.
     * @throws IniFileFormatException If any.
     */
    public static void loadConf(final Path srcDirPath) throws IOException, IniFileFormatException
    {
        Path iniPath = of(CONF_FILENAME).toAbsolutePath();

        if (vlevel >= 3)
        {
            DISPLAY.println("loadConf()");
        }

        if (vlevel >= 2)
        {
            DISPLAY.println("iniPath: " + iniPath.toString());
        }

        if (Files.notExists(iniPath, NOFOLLOW_LINKS) && (srcDirPath != null))
        {
            Path srcPath = of(srcDirPath.toString());

            try
            {
                while (Files.notExists(srcPath.resolve(CONF_FILENAME), NOFOLLOW_LINKS))
                {
                    if (Files.exists(srcPath.resolve("pom.xml"), NOFOLLOW_LINKS))
                    {
                        throw new FileNotFoundException(CONF_FILENAME);
                    }

                    srcPath = srcPath.getParent();

                    if (vlevel >= 2)
                    {
                        DISPLAY.println("srcPath: " + srcPath.toString());
                    }
                }
            } catch (NullPointerException ex)
            {
                throw new FileNotFoundException(CONF_FILENAME);
            }

            iniPath = srcPath.resolve(CONF_FILENAME);

            if (vlevel >= 2)
            {
                DISPLAY.println("iniPath: " + iniPath.toString());
            }
        }

        if (conf == null)
        {
            conf = new IniFile(iniPath).loadFile();
        } else
        {
            conf.mergeFile(iniPath);
        }

        conf.iniDoc.setString("program", "artifactId", POM.artifactId, "# The identifier for this artifact that is unique within the group given by the groupID");
        conf.iniDoc.setString("program", "description", POM.description, "# Project description");
        conf.iniDoc.setString("program", "filename", POM.filename, "# The filename of the binary output file");
        conf.iniDoc.setString("program", "groupId", POM.groupId, "# Project GroupId");
        conf.iniDoc.setString("program", "name", POM.name, "# Project Name");
        conf.iniDoc.setString("program", "version", POM.version, "# The version of the artifact");
        conf.iniDoc.setString("program", "details", POM.toString(), "# All of the above information laid out");

        Ref<Boolean> brtn = Ref.val();

        do
        {
            brtn.val = false;

            conf.iniDoc.getSections()
                    .forEach(section -> conf.iniDoc.getSection(section)
                    .forEach(prop ->
                    {
                        Ref<Boolean> rtn = Ref.val();
                        if (prop.value() != null)
                        {
                            if (vlevel >= 3)
                            {
                                DISPLAY.println(prop);
                            }
                            String value = processSubstitutions(prop.value(), null, rtn);
                            if (rtn.val)
                            {
                                conf.iniDoc.setString(section, prop.key(), value, prop.comment());
                                brtn.val = true;
                            }
                        }
                    }));

        } while (brtn.val);

        if (vlevel >= 2)
        {
            DISPLAY.println(POM);
        }
    }

    /**
     * Load the {@code pom.xml} file.
     *
     * @param pomFile The {@code pom.xml} file.
     * @param props   Additional properties to be added to "project" section.
     *
     * @throws IOException If any.
     */
    public static void loadPom(final File pomFile, final Properties props) throws IOException
    {

        SAXReader reader = new SAXReader();
        Document document = null;

        // Read file.
        try
        {
            document = reader.read(pomFile);
        } catch (DocumentException ex)
        {
            throw new IOException("\nError reading from '" + pomFile.getName() + "'", ex);
        }

        // root is 'project'.
        Element projectElement = document.getRootElement();

        if (conf == null)
        {
            conf = new IniFile();
        }

        Element groupId = projectElement.element("groupId");
        Element artifactId = projectElement.element("artifactId");
        Element version = projectElement.element("version");
        Element name = projectElement.element("name");
        Element description = projectElement.element("description");

        conf.iniDoc.setString("project", "groupId", groupId != null ? groupId.getTextTrim() : "", "# Project GroupId");
        conf.iniDoc.setString("project", "artifactId", artifactId != null ? artifactId.getTextTrim() : "", "# The identifier for this artifact that is unique within the group given by the groupID");
        conf.iniDoc.setString("project", "version", version != null ? version.getTextTrim() : "", "# The version of the artifact");
        conf.iniDoc.setString("project", "name", name != null ? name.getTextTrim() : "", "# Project Name");
        conf.iniDoc.setString("project", "description", description != null ? description.getTextTrim() : "", "# Project description");

        // Process -Dkey=value properties from commandline.
        // Add them to the "project" section.
        if (props != null)
        {
            props.forEach((keyObj, valueObj) ->
            {
                String key = (String) keyObj;
                String value = (String) valueObj;

                conf.iniDoc.setString("project", key, value);
            });
        }

        conf.iniDoc.getSection("project").forEach(prop
                -> DISPLAY.println("project." + prop.key() + ": " + prop.value())
        );
    }

    /**
     * Process substitutions.
     *
     * @param text  Text to be processed.
     * @param use   Alternate section to use.
     * @param found {@code true} if found.
     *
     * @return result.
     */
    public static String processSubstitutions(final String text, final String use, final Ref<Boolean> found)
    {
        TextEditor textEd = new TextEditor(text);
        found.val = false;

        do
        {
            textEd.replaceAll(SUBSTITUTION_PATTERN, m ->
            {
                if (vlevel >= 3)
                {
                    DISPLAY.println("m.group: " + m.group());
                }

                String group = m.group("group");
                String key = m.group("key");
                String rtn = "ERROR: Substitution!";

                if (group != null)
                {
                    rtn = getString(group, key, use);
                }

                if (vlevel >= 3)
                {
                    DISPLAY.println("rtn: " + rtn);
                }

                return rtn;
            });

            if (textEd.wasFound())
            {
                found.val = true;
            }
        } while (textEd.wasFound());

        return textEd.toString();
    }

}
