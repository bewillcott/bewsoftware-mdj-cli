/*
 *  File Name:    CmdSourceTest.java
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

package test.com.bewsoftware.mdj.cli.options;

import com.bewsoftware.mdj.cli.options.CmdSource;
import com.bewsoftware.mdj.cli.options.Option;
import com.bewsoftware.mdj.cli.util.CmdLine;
import com.bewsoftware.mdj.cli.util.MyCmdLine;
import java.nio.file.Path;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import static java.nio.file.Path.of;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 *
 * @author <a href="mailto:bw.opensource@yahoo.com">Bradley Willcott</a>
 */
public class CmdSourceTest
{

    public CmdSourceTest()
    {
    }

    public static Stream<Arguments> provideArgsForTestExecute()
    {
        return Stream.of(
                Arguments.of(
                        new String[]
                        {
                            "-s", "src/docs/manual"
                        },
                        "src/docs/manual"
                ),
                Arguments.of(
                        new String[]
                        {
                            "-s", "src/docs/manual",
                            "-d", "target/docs/manual"
                        },
                        "target/docs/manual"
                )
        );
    }

    /**
     * Test of execute method, of class CmdSource.
     *
     * @param args
     * @param expResultString
     */
    @ParameterizedTest
    @MethodSource("provideArgsForTestExecute")
    public void testExecute(String[] args, String expResultString)
    {
        System.out.println("[CmdSourceTest.testExecute()]");
        CmdLine cmd = new MyCmdLine(args);
        Option instance = new CmdSource();
        instance.execute(cmd);
        Path expResult = of(expResultString).normalize().toAbsolutePath();
        Path result = cmd.destination();
        assertEquals(expResult, result);
    }
}
