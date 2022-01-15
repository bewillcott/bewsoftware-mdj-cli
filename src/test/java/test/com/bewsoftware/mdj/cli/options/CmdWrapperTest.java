/*
 *  File Name:    CmdWrapperTest.java
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

import com.bewsoftware.mdj.cli.options.CmdWrapper;
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
public class CmdWrapperTest
{
    public CmdWrapperTest()
    {
    }

    public static Stream<Arguments> provideArgsForTestExecute()
    {
        return Stream.of(
                Arguments.of(
                        new String[]
                        {
                            "-W", "src/docs/manual",
                            "-i", "index.md"
                        }, 6
                ),
                Arguments.of(
                        new String[]
                        {
                            "-w",
                            "-i", "index.md"
                        }, null
                ),
                Arguments.of(
                        new String[]
                        {
                            "-W", "target/docs/test"
                        }, 0
                )
        );
    }

    /**
     * Test of execute method, of class CmdWrapper.
     *
     * @param args
     * @param expResulInteger
     */
    @ParameterizedTest
    @MethodSource("provideArgsForTestExecute")
    public void testExecute(String[] args, Integer expResulInteger)
    {
        System.out.println("[CmdWrapperTest.testExecute()]");
        CmdLine cmd = new MyCmdLine(args);
        Option instance = new CmdWrapper();
        Optional<Integer> expResult = expResulInteger != null
                ? Optional.of(expResulInteger)
                : Optional.empty();
        Optional<Integer> result = instance.execute(cmd);
        assertEquals(expResult, result);
    }
}
