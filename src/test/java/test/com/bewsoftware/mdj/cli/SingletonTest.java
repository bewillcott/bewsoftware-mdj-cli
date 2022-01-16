/*
 *  File Name:    SingletonTest.java
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
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package test.com.bewsoftware.mdj.cli;

import com.bewsoftware.mdj.cli.Main;
import com.bewsoftware.mdj.cli.util.CmdLine;
import com.bewsoftware.mdj.cli.util.MyCmdLine;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import static java.nio.file.Path.of;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static test.com.bewsoftware.mdj.cli.TestConstants.TEST_RESOURCES;
import static test.com.bewsoftware.mdj.cli.TestConstants.TEST_RESOURCES_RESULTS;

/**
 * SingletonTest class description.
 *
 * @author <a href="mailto:bw.opensource@yahoo.com">Bradley Willcott</a>
 *
 * @since 1.0
 * @version 1.0
 */
public class SingletonTest
{

    public SingletonTest()
    {
    }

    public static Stream<Arguments> provideArgsForTestExecute()
    {
        return Stream.of(
                Arguments.of(
                        new String[]
                        {
                            "-i", TEST_RESOURCES + "singleton.markdown",
                            "-v2"
                        }, 0,
                        TEST_RESOURCES + "singleton.html",
                        TEST_RESOURCES_RESULTS + "singleton.html"
                ));
    }

    @BeforeEach
    public void setUp()
    {
        FileUtils.deleteQuietly(new File(TEST_RESOURCES + "singleton.html"));
    }

    @AfterEach
    public void tearDown() throws IOException
    {
    }

    /**
     * Test of execute method, of class Main.
     *
     * @param args
     * @param expResult
     * @param resultFile
     * @param expFile
     *
     * @throws java.io.IOException
     */
    @ParameterizedTest
    @MethodSource("provideArgsForTestExecute")
    public void testExecute(
            String[] args,
            int expResult,
            String resultFile,
            String expFile
    ) throws IOException
    {
        System.out.println("[SingletonTest.testExecute()]");
        CmdLine cmd = new MyCmdLine(args);
        System.out.println("cmd:\n" + cmd);

        int result = Main.execute(args);

        System.out.println("result: " + result);
        assertEquals(expResult, result);

        Path resultFilePath = of(resultFile);
        assertTrue(Files.exists(resultFilePath));

        Path expFilePath = of(expFile);
        assertTrue(Files.exists(expFilePath));

        long mismatch = Files.mismatch(resultFilePath, expFilePath);
        assertEquals(-1, mismatch);
    }

}
