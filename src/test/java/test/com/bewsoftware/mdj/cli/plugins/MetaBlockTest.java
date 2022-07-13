/*
 *  File Name:    MetaBlockTest.java
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

package test.com.bewsoftware.mdj.cli.plugins;

import com.bewsoftware.fileio.ini.IniFile;
import com.bewsoftware.mdj.cli.plugins.MetaBlock;
import com.bewsoftware.property.IniProperty;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import static com.bewsoftware.mdj.cli.util.Constants.PAGE;
import static com.bewsoftware.mdj.cli.util.Constants.TEXT;
import static com.bewsoftware.mdj.cli.util.GlobalVariables.conf;
import static java.nio.file.Path.of;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static test.com.bewsoftware.mdj.cli.TestConstants.ERROR;
import static test.com.bewsoftware.mdj.cli.TestConstants.TEST_RESOURCES;
import static test.com.bewsoftware.mdj.cli.TestConstants.TEST_RESOURCES_RESULTS;

/**
 *
 * @author <a href="mailto:bw.opensource@yahoo.com">Bradley Willcott</a>
 */
public class MetaBlockTest
{

    public MetaBlockTest()
    {
    }

    public static Stream<Arguments> provideArgsForTestExecute()
    {
        return Stream.of(
                Arguments.of(
                        TEST_RESOURCES + "index.md",
                        TEST_RESOURCES_RESULTS + "MetaBlockResults.prop",
                        true
                ));
    }

    @BeforeAll
    public static void setUpClass()
    {
        conf = new IniFile();
    }

    @BeforeEach
    public void setUp()
    {
        conf.iniDoc.removeSection(PAGE);
    }

    /**
     * Test of execute method, of class MetaBlock.
     *
     * @param sourceFile
     * @param propertiesFile
     * @param expResult
     *
     * @throws java.io.IOException
     */
    @ParameterizedTest
    @MethodSource("provideArgsForTestExecute")
    public void testExecute(
            final String sourceFile,
            final String propertiesFile,
            final boolean expResult
    ) throws IOException
    {
        System.out.println("[MetaBlockTest.testExecute()]");
        loadTestString(sourceFile);

        MetaBlock instance = new MetaBlock();
        instance.execute();

        assertEquals(
                expResult,
                checkResults(propertiesFile),
                () -> conf.iniDoc.getStringG(ERROR, "No Error Message")
        );
    }

    private boolean checkResults(final String filename) throws IOException
    {
        boolean rtn = false;

        Properties props = new Properties();
        props.load(Files.newBufferedReader(of(filename)));
        List<IniProperty<String>> pageProps = conf.iniDoc.getSection(PAGE);

        if (pageProps.size() >= props.size())
        {
            rtn = true;

            for (Entry<Object, Object> entry : props.entrySet())
            {
                String key = (String) entry.getKey();
                String expValue = (String) entry.getValue();

                String actualValue = conf.iniDoc.getString(PAGE, key, "DEFAULT VALUE");

                if (!expValue.equals(actualValue))
                {
                    String error = "Result: key{" + key + "}:actualValue{" + actualValue
                            + "} does not equal expValue{" + expValue + "}";
                    conf.iniDoc.setStringG(ERROR, error);
                    rtn = false;

                    break;
                }
            }
        } else
        {
            String error = "pageProps.size(){" + pageProps.size()
                    + "} does not equal props.size(){" + props.size() + "}";
            conf.iniDoc.setStringG(ERROR, error);
        }

        return rtn;
    }

    private void loadTestString(final String filename) throws IOException
    {
        String sourceString = Files.readString(of(filename));
        conf.iniDoc.setString(PAGE, TEXT, sourceString);
    }
}
