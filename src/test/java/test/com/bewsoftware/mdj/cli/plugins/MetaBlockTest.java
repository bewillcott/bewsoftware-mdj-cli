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
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package test.com.bewsoftware.mdj.cli.plugins;

import com.bewsoftware.mdj.cli.plugins.MetaBlock;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.fail;

/**
 *
 * @author <a href="mailto:bw.opensource@yahoo.com">Bradley Willcott</a>
 */
public class MetaBlockTest
{

    public MetaBlockTest()
    {
    }

    @BeforeAll
    public static void setUpClass()
    {
    }

    @AfterAll
    public static void tearDownClass()
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
     * Test of execute method, of class MetaBlock.
     */
    @Test
    public void testExecute()
    {
        System.out.println("execute");
        MetaBlock instance = new MetaBlock();
        instance.execute();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

}
