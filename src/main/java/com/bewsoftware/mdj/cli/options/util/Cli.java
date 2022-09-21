/*
 * This file is part of the MDj Command-line Interface program
 * (aka: mdj-cli).
 *
 * Copyright (C) 2020-2022 Bradley Willcott
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
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.bewsoftware.mdj.cli.options.util;

import com.bewsoftware.common.InvalidParameterValueException;
import com.bewsoftware.fileio.ini.IniDocument;
import com.bewsoftware.fileio.ini.IniFile;
import com.bewsoftware.fileio.ini.IniFileFormatException;
import com.bewsoftware.mdj.cli.util.Constants;
import com.bewsoftware.mdj.core.TextEditor;
import com.bewsoftware.property.IniProperty;
import com.bewsoftware.utils.struct.Ref;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Properties;
import java.util.regex.Pattern;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import static com.bewsoftware.mdj.cli.util.Constants.*;
import static com.bewsoftware.mdj.cli.util.GlobalVariables.conf;
import static com.bewsoftware.mdj.cli.util.Keys.*;
import static java.nio.file.LinkOption.NOFOLLOW_LINKS;
import static java.nio.file.Path.of;

/**
 *
 * @author <a href="mailto:bw.opensource@yahoo.com">Bradley Willcott</a>
 *
 * @since 0.1
 * @version 2.1.0
 */
public class Cli
{
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
    public static String getString(
            final String section,
            final String key,
            final String use
    )
    {
        String rtn = conf.iniDoc.getString(section, key, null);
        return rtn != null ? rtn : conf.iniDoc.getString(use, key, "");
    }

    /**
     * Load the configuration file:
     * {@link Constants#CONF_FILENAME CONF_FILENAME}
     *
     * @param srcDirPath Initial directory to look for the file.
     *
     * @throws IOException            If any.
     * @throws IniFileFormatException If any.
     */
    public static void loadConf(final Path srcDirPath)
            throws IOException, IniFileFormatException
    {
        Path iniPath = of(CONF_FILENAME).toAbsolutePath();

        DISPLAY.level(3).println("loadConf()");
        DISPLAY.level(2).println("iniPath: " + iniPath.toString());

        if (Files.notExists(iniPath, NOFOLLOW_LINKS) && srcDirPath != null)
        {
            iniPath = findConfFile(srcDirPath);
        }

        if (conf == null)
        {
            conf = new IniFile(iniPath).loadFile();
        } else
        {
            conf.mergeFile(iniPath);
        }

        addProgramSettings();
        processAllSubstitutions();

        DISPLAY.level(2).println(POM);
    }

    /**
     * Load the {@code pom.xml} file.
     *
     * @param pomFile The {@code pom.xml} file.
     * @param props   Additional properties to be added to "project" section.
     *
     * @throws IOException If any.
     */
    public static void loadPom(final File pomFile, final Properties props)
            throws IOException
    {
        Document document = readInPomFile(pomFile);

        if (conf == null)
        {
            conf = new IniFile();
        }

        addProjectSettings(document);
        processCommandlineProperties(props);
        displayProjectProperties();
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
    public static String processSubstitutions(
            final String text,
            final String use,
            final Ref<Boolean> found
    )
    {
        TextEditor textEd = new TextEditor(text);
        found.val = false;

        do
        {
            textEd.replaceAll(SUBSTITUTION_PATTERN, m ->
            {
                DISPLAY.level(3).append("m.group: ").println(m.group());

                String group = m.group("group");
                String key = m.group("key");
                String rtn = "ERROR: Substitution!";

                if (group != null)
                {
                    rtn = getString(group, key, use);
                }

                DISPLAY.level(3).append("rtn: ").println(rtn);
                return rtn;
            });

            if (textEd.wasFound())
            {
                found.val = true;
            }
        } while (textEd.wasFound());

        return textEd.toString();
    }

    private static void addProgramSettings()
    {
        Settings[] programSettings =
        {
            new Settings(
            NAME,
            POM.name,
            "# Project Name"
            ),
            new Settings(
            GROUP_ID,
            POM.groupId,
            "# Project GroupId"
            ),
            new Settings(
            ARTIFACT_ID,
            POM.artifactId,
            "# The identifier for this artifact that is unique "
            + "within the group given by the groupID"
            ),
            new Settings(
            VERSION,
            POM.version,
            "# The version of the artifact"
            ),
            new Settings(
            DESCRIPTION,
            POM.description,
            "# Project description"
            ),
            new Settings(
            FILENAME,
            POM.filename,
            "# The filename of the binary output file"
            ),
            new Settings(
            DETAILS,
            POM.toString(),
            "# All of the above information laid out"
            )
        };

        Arrays.asList(programSettings)
                .forEach(
                        settings
                        -> conf.iniDoc.setString(
                                PROGRAM,
                                settings.key,
                                settings.value,
                                settings.comment
                        )
                );
    }

    private static void addProjectSettings(Document document)
    {
        // root is 'project'.
        Element projectElement = document.getRootElement();

        Element groupId = projectElement.element(GROUP_ID);
        Element artifactId = projectElement.element(ARTIFACT_ID);
        Element version = projectElement.element(VERSION);
        Element name = projectElement.element(NAME);
        Element description = projectElement.element(DESCRIPTION);

        if (version == null)
        {
            version = projectElement.element(PARENT).element(VERSION);
        }

        Settings[] projectSettings =
        {
            new Settings(
            NAME,
            name != null ? name.getTextTrim() : "",
            "# Project Name"
            ),
            new Settings(
            GROUP_ID,
            groupId != null ? groupId.getTextTrim() : "",
            "# Project GroupId"
            ),
            new Settings(
            ARTIFACT_ID,
            artifactId != null ? artifactId.getTextTrim() : "",
            "# The identifier for this artifact that is unique "
            + "within the group given by the groupID"
            ),
            new Settings(
            VERSION,
            version != null ? version.getTextTrim() : "",
            "# The version of the artifact"
            ),
            new Settings(
            DESCRIPTION,
            description != null ? description.getTextTrim() : "",
            "# Project description"
            )
        };

        Arrays.asList(projectSettings)
                .forEach(
                        settings
                        -> conf.iniDoc.setString(
                                PROJECT,
                                settings.key,
                                settings.value,
                                settings.comment
                        )
                );

    }

    private static void displayProjectProperties()
    {
        DISPLAY.level(0);

        conf.iniDoc.getSection(PROJECT).forEach(prop
                -> DISPLAY.append("project.")
                        .append(prop.key())
                        .append(": ")
                        .appendln(prop.value())
        );

        DISPLAY.flush();
    }

    private static Path findConfFile(final Path srcDirPath)
            throws FileNotFoundException
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
                DISPLAY.level(2).println("srcPath: " + srcPath.toString());
            }
        } catch (NullPointerException ex)
        {
            throw new FileNotFoundException(CONF_FILENAME);
        }

        Path iniPath = srcPath.resolve(CONF_FILENAME);
        DISPLAY.level(2).println("iniPath: " + iniPath.toString());

        return iniPath;
    }

    private static void processAllSubstitutions()
    {
        Ref<Boolean> brtn = Ref.val();

        do
        {
            brtn.val = false;

            conf.iniDoc.getSections()
                    .forEach((String section) -> conf.iniDoc.getSection(section)
                    .forEach((IniProperty<String> prop)
                            -> processSubstitutionsForAProperty(prop, section, brtn)
                    ));
        } while (brtn.val);
    }

    private static void processCommandlineProperties(final Properties props)
    {
        // Process -Dkey=value properties from commandline.
        // Add them to the "project" section.
        props.forEach((keyObj, valueObj) ->
        {
            String key = (String) keyObj;
            String value = (String) valueObj;

            conf.iniDoc.setString(PROJECT, key, value);
        });

    }

    private static void processSubstitutionsForAProperty(
            IniProperty<String> prop,
            String section,
            Ref<Boolean> brtn
    ) throws InvalidParameterValueException
    {
        Ref<Boolean> rtn = Ref.val();

        if (prop.value() != null)
        {
            DISPLAY.level(3).println(prop);
            String value = processSubstitutions(prop.value(), null, rtn);

            if (rtn.val)
            {
                conf.iniDoc.setString(section, prop.key(), value, prop.comment());
                brtn.val = true;
            }
        }
    }

    private static Document readInPomFile(final File pomFile) throws IOException
    {
        try
        {
            return new SAXReader().read(pomFile);
        } catch (DocumentException ex)
        {
            throw new IOException("\nError reading from '" + pomFile.getName() + "'", ex);
        }
    }

    private static class Settings
    {
        public final String comment;

        public final String key;

        public final String value;

        private Settings(String key, String value, String comment)
        {
            this.key = key;
            this.value = value;
            this.comment = comment;
        }
    }

}
