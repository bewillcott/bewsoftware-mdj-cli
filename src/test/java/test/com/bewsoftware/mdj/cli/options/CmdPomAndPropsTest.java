/*
 *  File Name:    CmdPomAndPropsTest.java
 *  Project Name: bewsoftware-mdj-cli
 *
 *  Copyright (c) 2021 Bradley Willcott
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

import com.bewsoftware.mdj.cli.options.CmdPomAndProps;
import com.bewsoftware.mdj.cli.options.Option;
import com.bewsoftware.mdj.cli.util.CmdLine;
import com.bewsoftware.mdj.cli.util.MyCmdLine;
import java.util.Optional;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 *
 * @author <a href="mailto:bw.opensource@yahoo.com">Bradley Willcott</a>
 */
public class CmdPomAndPropsTest
{

    public CmdPomAndPropsTest()
    {
    }

    public static Stream<Arguments> provideArgsForTestExecute()
    {
        return Stream.of(
                Arguments.of(
                        new String[]
                        {
                            "-p", "pom.xml",
                            "-i", "test.md"
                        }, null
                ),
                Arguments.of(
                        new String[]
                        {
                            "-j", "test.jar", "src/docs/manual", "src/docs/manual",
                            "-i", "test.md"
                        }, null
                ),
                Arguments.of(
                        new String[]
                        {
                            "-p", "input.md",
                            "-i", "test.md"
                        }, -1
                )
        );
    }

    /**
     * Test of execute method, of class CmdPomAndProps.
     *
     * @param args
     * @param expResulInteger
     */
    @ParameterizedTest
    @MethodSource("provideArgsForTestExecute")
    public void testExecute(String[] args, Integer expResulInteger)
    {
        System.out.println("[CmdPomAndPropsTest.testExecute()]");
        CmdLine cmd = new MyCmdLine(args);
        Option instance = new CmdPomAndProps();
        Optional<Integer> expResult = expResulInteger != null
                ? Optional.of(expResulInteger)
                : Optional.empty();
        Optional<Integer> result = instance.execute(cmd);
        assertEquals(expResult, result);
    }
}
