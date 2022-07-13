/*
 *  File Name:    CliTest.java
 *  Project Name: bewsoftware-mdj-cli
 *
 *  Copyright (c) 2022 Bradley Willcott
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

package test.com.bewsoftware.mdj.cli.options.util;

import com.bewsoftware.fileio.ini.IniFile;
import com.bewsoftware.mdj.cli.options.util.Cli;
import com.bewsoftware.utils.struct.Ref;
import java.io.File;
import java.nio.file.Path;
import java.util.Properties;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.bewsoftware.mdj.cli.util.Constants.PROJECT;
import static com.bewsoftware.mdj.cli.util.GlobalVariables.conf;
import static java.nio.file.Path.of;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 *
 * @author <a href="mailto:bw.opensource@yahoo.com">Bradley Willcott</a>
 */
public class CliTest
{

    public CliTest()
    {
    }

    @BeforeEach
    public void setUp()
    {
    }

    @AfterEach
    public void tearDown()
    {
    }

    /**
     * Test of getString method, of class Cli.
     */
    @Test
    public void testGetString()
    {
        System.out.println("[CliTest.testGetString()]");
        conf = new IniFile();
        String section = "test1";
        String key = "key1";
        String use = "test2";
        String expResult = "Hi there!";
        String altResult = "What the!?!";
        conf.iniDoc.setString(section, key, expResult);
        conf.iniDoc.setString(use, key, altResult);
        String result = Cli.getString(section, key, use);
        assertEquals(expResult, result);
    }

    /**
     * Test of loadConf method, of class Cli.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testLoadConf() throws Exception
    {
        System.out.println("[CliTest.testLoadConf()]");
        Path srcDirPath = of("src/docs/manual");
        Cli.loadConf(srcDirPath);
        String iniVersion = Cli.getString(null, "iniVersion", null);
        assertNotNull(iniVersion, "testLoadConf() failed");
    }

    /**
     * Test of loadPom method, of class Cli.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testLoadPom() throws Exception
    {
        System.out.println("[CliTest.testLoadPom()]");
        File pomFile = new File("pom.xml");
        Properties props = new Properties();
        String key = "test1";
        String value = "One";
        props.setProperty(key, value);
        Cli.loadPom(pomFile, props);
        assertNotNull(
                Cli.getString(PROJECT, "name", null),
                "project.name is null"
        );

        assertEquals(
                value,
                Cli.getString(PROJECT, key, null),
                "project.test1 is null"
        );

    }

    /**
     * Test of processSubstitutions method, of class Cli.
     */
    @Test
    public void testProcessSubstitutions()
    {
        System.out.println("[CliTest.testProcessSubstitutions()]");
        conf = new IniFile();
        String section = "test1";
        String key = "key1";
        String use = "test2";
        String value = "Hi there!";
        String altValue = "What the!?!";
        conf.iniDoc.setString(section, key, value);
        conf.iniDoc.setString(use, key, altValue);
        String text = "This is the 'test1' value: ${" + section + "." + key + "}";
        String expResult = "This is the 'test1' value: Hi there!";
        Ref<Boolean> found = Ref.val(false);
        String result = Cli.processSubstitutions(text, use, found);
        assertTrue(found.val);
        assertEquals(expResult, result);
    }
}
