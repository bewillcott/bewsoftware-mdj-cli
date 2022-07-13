/*
 *  File Name:    MyCmdLineTest.java
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
 *  along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package test.com.bewsoftware.mdj.cli.util;

import com.bewsoftware.mdj.cli.util.MyCmdLine;
import com.bewsoftware.utils.struct.Ref;
import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.stream.Stream;
import org.apache.commons.cli.MissingOptionException;
import org.apache.commons.cli.UnrecognizedOptionException;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import static java.nio.file.Path.of;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 *
 * @author <a href="mailto:bw.opensource@yahoo.com">Bradley Willcott</a>
 */
public class MyCmdLineTest
{

    public MyCmdLineTest()
    {
    }

    public static Stream<Arguments> provideArgsForTestDestination_0args()
    {
        return Stream.of(
                Arguments.of(
                        new String[]
                        {
                            "-s", "src/docs/manual",
                            "-d", "target/docs/manual"
                        }, "target/docs/manual"
                )
        );
    }

    public static Stream<Arguments> provideArgsForTestDestination_Path()
    {
        return Stream.of(
                Arguments.of(
                        new String[]
                        {
                            "-s", "src/docs/manual",
                            "-d", "target/docs/manual"
                        }, "target/docs/manual2"
                )
        );
    }

    public static Stream<Arguments> provideArgsForTestDocRootPath()
    {
        return Stream.of(
                Arguments.of(
                        new String[]
                        {
                            "-j", "test.jar", "src/docs/manual", "src/docs/manual"
                        }, of("src/docs/manual").toAbsolutePath().toString()
                ),
                Arguments.of(
                        new String[]
                        {
                            "-W", "src/docs/manual"
                        }, of("src/docs/manual").toAbsolutePath().toString()
                )
        );
    }

    public static Stream<Arguments> provideArgsForTestExceptions()
    {
        String msg = "\nYou must use at least one of the following options:"
                //  + "\n\t'-a', '-c', -i', '-j', '-m', '-p', '-s', '-W', or '-h|--help'\n";
                + "\n\t'-c', -i', '-j', '-m', '-p', '-s', '-W', or '-h|--help'\n";

        return Stream.of(
                Arguments.of(
                        new String[]
                        {
                            "-o", "test.html"
                        },
                        new Exception[]
                        {
                            new MissingOptionException(msg)
                        },
                        true
                ),
                Arguments.of(
                        new String[]
                        {
                            "-i", "test.md"
                        },
                        new Exception[]
                        {

                        },
                        true
                ),
                Arguments.of(
                        new String[]
                        {
                            "-z", ""
                        },
                        new Exception[]
                        {
                            new UnrecognizedOptionException("Unrecognized option: -z")
                        },
                        true
                )
        );
    }

    public static Stream<Arguments> provideArgsForTestGetOptionProperties_String()
    {
        return Stream.of(
                Arguments.of(
                        "D",
                        new String[]
                        {
                            "-Dname=Brad", "-Dage=16", "-DIQ=101"
                        },
                        new String[][]
                        {
                            {
                                "name", "Brad"
                            },
                            {
                                "age", "16"
                            },
                            {
                                "IQ", "101"
                            }
                        },
                        true
                ),
                Arguments.of(
                        "p",
                        new String[]
                        {
                            "-p/=docs/manual", "-p"
                        },
                        new String[][]
                        {
                            {
                                "/", "docs/manual"
                            }
                        },
                        true
                )
        );
    }

    public static Stream<Arguments> provideArgsForTestGetOptionProperties_char()
    {
        return Stream.of(
                Arguments.of(
                        'D',
                        new String[]
                        {
                            "-Dname=Brad", "-Dage=16", "-DIQ=101"
                        },
                        new String[][]
                        {
                            {
                                "name", "Brad"
                            },
                            {
                                "age", "16"
                            },
                            {
                                "IQ", "101"
                            }
                        },
                        true
                ),
                Arguments.of(
                        'p',
                        new String[]
                        {
                            "-p/=docs/manual", "-p"
                        },
                        new String[][]
                        {
                            {
                                "/", "docs/manual"
                            }
                        },
                        true
                )
        );
    }

    public static Stream<Arguments> provideArgsForTestHasOption_String()
    {
        return Stream.of(
                Arguments.of(
                        "D",
                        new String[]
                        {
                            "-Dname=Brad"
                        },
                        true
                ),
                Arguments.of(
                        "p",
                        new String[]
                        {
                            "-p"
                        },
                        true
                ),
                Arguments.of(
                        "i",
                        new String[]
                        {
                            "-p"
                        },
                        false
                )
        );
    }

    public static Stream<Arguments> provideArgsForTestHasOption_char()
    {
        return Stream.of(
                Arguments.of(
                        'D',
                        new String[]
                        {
                            "-Dname=Brad"
                        },
                        true
                ),
                Arguments.of(
                        'p',
                        new String[]
                        {
                            "-p"
                        },
                        true
                ),
                Arguments.of(
                        'i',
                        new String[]
                        {
                            "-p"
                        },
                        false
                )
        );
    }

    public static Stream<Arguments> provideArgsForTestInputFile()
    {
        return Stream.of(
                Arguments.of(
                        new String[]
                        {
                            "-i", "test.md"
                        }, "alternate.md"
                ),
                Arguments.of(
                        new String[]
                        {
                            "-W", "src/docs/manual"
                        }, "hasone.md"
                )
        );
    }

    public static Stream<Arguments> provideArgsForTestInputFile_0args()
    {
        return Stream.of(
                Arguments.of(
                        new String[]
                        {
                            "-i", "test.md"
                        }, "test.md"
                ),
                Arguments.of(
                        new String[]
                        {
                            "-W", "src/docs/manual"
                        }, null
                )
        );
    }

    public static Stream<Arguments> provideArgsForTestJarFile()
    {
        return Stream.of(
                Arguments.of(
                        new String[]
                        {
                            "-j", "test.jar", "src/docs/manual", "src/docs/manual"
                        }, "test.jar"
                )
        );
    }

    public static Stream<Arguments> provideArgsForTestJarSourcePath()
    {
        return Stream.of(
                Arguments.of(
                        new String[]
                        {
                            "-j", "test.jar", "src/docs/manual", "src/docs/manual"
                        }, "src/docs/manual"
                )
        );
    }

    public static Stream<Arguments> provideArgsForTestOutputFile_0args()
    {
        return Stream.of(
                Arguments.of(
                        new String[]
                        {
                            "-i", "test.md"
                        }, null
                ),
                Arguments.of(
                        new String[]
                        {
                            "-i", "test.md",
                            "-o", "test.html"
                        }, "test.html"
                )
        );
    }

    public static Stream<Arguments> provideArgsForTestOutputFile_File()
    {
        return Stream.of(
                Arguments.of(
                        new String[]
                        {
                            "-i", "test.md"
                        }, null
                ),
                Arguments.of(
                        new String[]
                        {
                            "-i", "test.md",
                            "-o", "test.html"
                        }, "test2.html"
                )
        );
    }

    public static Stream<Arguments> provideArgsForTestPomFile()
    {
        return Stream.of(
                Arguments.of(
                        new String[]
                        {
                            "-i", "test.md",
                            "-P", "pom.xml"
                        }, "pom.xml"
                ),
                Arguments.of(
                        new String[]
                        {
                            "-i", "test2.md",
                            "-P", "pom2.xml"
                        }, "pom2.xml"
                )
        );
    }

    public static Stream<Arguments> provideArgsForTestSource_0args()
    {
        return Stream.of(
                Arguments.of(
                        new String[]
                        {
                            "-s", "src/docs/manual"
                        }, "src/docs/manual"
                )
        );
    }

    public static Stream<Arguments> provideArgsForTestSource_Path()
    {
        return Stream.of(
                Arguments.of(
                        new String[]
                        {
                            "-s", "src/docs/manual"
                        }, "src/docs/manual2"
                )
        );
    }

    public static Stream<Arguments> provideArgsForTestSuccess()
    {
        return Stream.of(
                Arguments.of(
                        new String[]
                        {
                            "-i", "test.md"
                        }, true
                ),
                Arguments.of(
                        new String[]
                        {
                            "-o", "test.html"
                        }, false
                )
        );
    }

    public static Stream<Arguments> provideArgsForTestVerbosity()
    {
        return Stream.of(
                Arguments.of(
                        new String[]
                        {
                            "-i", "test.md"
                        }, 0
                ),
                Arguments.of(
                        new String[]
                        {
                            "-i", "test.md",
                            "-v"
                        }, 1
                ),
                Arguments.of(
                        new String[]
                        {
                            "-i", "test.md",
                            "-v", "2"
                        }, 2
                ),
                Arguments.of(
                        new String[]
                        {
                            "-i", "test.md",
                            "-v", "3"
                        }, 3
                )
        );
    }

    private static boolean compareLists(final List<Exception> expResult, final List<Exception> result)
    {
        Ref<Boolean> rtn = Ref.val(false);

        if (expResult == null && result == null)
        {
            rtn.val = true;
        } else if (expResult != null && result != null)
        {
            if (result.size() == expResult.size())
            {
                rtn.val = true;

                expResult.forEach(t ->
                {
                    String expName = t.getClass().getName();
                    Exception resultException = getFromList(result, expName);

                    if (resultException != null)
                    {
                        rtn.val = t.getMessage().equals(resultException.getMessage());
                    }
                });
            }
        }

        return rtn.val;
    }

    private static boolean compareProperties(final Properties expResult, final Properties result)
    {
        Ref<Boolean> rtn = Ref.val(false);

        if (expResult == null && result == null)
        {
            rtn.val = true;
        } else if (expResult != null && result != null)
        {
            if (result.size() == expResult.size())
            {
                rtn.val = true;

                expResult.forEach((Object keyObj, Object valueObj) ->
                {
                    String key = (String) keyObj;
                    String value = (String) valueObj;

                    String resultValue = (String) result.get(key);

                    if (!value.equals(resultValue))
                    {
                        rtn.val = false;
                    }
                });
            }
        }

        return rtn.val;
    }

    private static Exception getFromList(List<Exception> result, String expName)
    {
        Ref<Exception> rtn = Ref.val();

        result.forEach(t ->
        {
            String name = t.getClass().getName();

            if (name.equals(expName))
            {
                rtn.val = t;
            }
        });

        return rtn.val;
    }

    private static Properties makeProperties(String[][] resultStrings)
    {
        Properties rtn = new Properties();

        for (String[] resultString : resultStrings)
        {
            rtn.put(resultString[0], resultString[1]);
        }

        return rtn;
    }

    /**
     * Test of destination method, of class MyCmdLine.
     *
     * @param args
     * @param resultString
     */
    @ParameterizedTest
    @MethodSource("provideArgsForTestDestination_0args")
    public void testDestination_0args(String[] args, String resultString)
    {
        System.out.println("[MyCmdLineTest.testDestination_0args()]");
        MyCmdLine instance = new MyCmdLine(args);
        Path expResult = of(resultString).toAbsolutePath();
        Path result = instance.destination();
        assertEquals(expResult, result);
    }

    /**
     * Test of destination method, of class MyCmdLine.
     *
     * @param args
     * @param resultString
     */
    @ParameterizedTest
    @MethodSource("provideArgsForTestDestination_Path")
    public void testDestination_Path(String[] args, String resultString)
    {
        System.out.println("[MyCmdLineTest.testDestination_Path()]");
        MyCmdLine instance = new MyCmdLine(args);
        Path path = of(resultString).toAbsolutePath();
        instance.destination(path);
        Path result = instance.destination();
        assertEquals(path, result);
    }

    /**
     * Test of docRootPath method, of class MyCmdLine.
     *
     * @param args
     * @param resultString
     */
    @ParameterizedTest
    @MethodSource("provideArgsForTestDocRootPath")
    public void testDocRootPath(String[] args, String resultString)
    {
        System.out.println("[MyCmdLineTest.testDocRootPath()]");
        MyCmdLine instance = new MyCmdLine(args);
        Path expResult = of(resultString);
        Path result = instance.docRootPath();
        assertEquals(expResult, result);
    }

    /**
     * Test of exceptions method, of class MyCmdLine.
     *
     * @param args
     * @param exceptionsArray
     * @param expResult
     */
    @ParameterizedTest
    @MethodSource("provideArgsForTestExceptions")
    public void testExceptions(String[] args, Exception[] exceptionsArray, boolean expResult)
    {
        System.out.println("[MyCmdLineTest.testExceptions()]");
        MyCmdLine instance = new MyCmdLine(args);
        List<Exception> expected = new ArrayList<>(Arrays.asList(exceptionsArray));
        List<Exception> exceptions = instance.exceptions();
        boolean result = compareLists(expected, exceptions);
        assertEquals(expResult, result);
    }

    /**
     * Test of getOptionProperties method, of class MyCmdLine.
     *
     * @param opt
     * @param args
     * @param resultStrings
     * @param expResult
     */
    @ParameterizedTest
    @MethodSource("provideArgsForTestGetOptionProperties_String")
    public void testGetOptionProperties_String(String opt, String[] args, String[][] resultStrings, boolean expResult)
    {
        System.out.println("[MyCmdLineTest.testGetOptionProperties_String()]");
        MyCmdLine instance = new MyCmdLine(args);
        Properties props = makeProperties(resultStrings);
        Properties optinProps = instance.getOptionProperties(opt);
        boolean result = compareProperties(props, optinProps);
        assertEquals(expResult, result);
    }

    /**
     * Test of getOptionProperties method, of class MyCmdLine.
     *
     * @param opt
     * @param args
     * @param resultStrings
     * @param expResult
     */
    @ParameterizedTest
    @MethodSource("provideArgsForTestGetOptionProperties_char")
    public void testGetOptionProperties_char(char opt, String[] args, String[][] resultStrings, boolean expResult)
    {
        System.out.println("[MyCmdLineTest.testGetOptionProperties_char()]");
        MyCmdLine instance = new MyCmdLine(args);
        Properties props = makeProperties(resultStrings);
        Properties optinProps = instance.getOptionProperties(opt);
        boolean result = compareProperties(props, optinProps);
        assertEquals(expResult, result);
    }

    /**
     * Test of hasOption method, of class MyCmdLine.
     *
     * @param opt
     * @param args
     * @param expResult
     */
    @ParameterizedTest
    @MethodSource("provideArgsForTestHasOption_String")
    public void testHasOption_String(String opt, String[] args, boolean expResult)
    {
        System.out.println("[MyCmdLineTest.testHasOption_String()]");
        MyCmdLine instance = new MyCmdLine(args);
        boolean result = instance.hasOption(opt);
        assertEquals(expResult, result);
    }

    /**
     * Test of hasOption method, of class MyCmdLine.
     *
     * @param opt
     * @param args
     * @param expResult
     */
    @ParameterizedTest
    @MethodSource("provideArgsForTestHasOption_char")
    public void testHasOption_char(char opt, String[] args, boolean expResult)
    {
        System.out.println("[MyCmdLineTest.testHasOption_char()]");
        MyCmdLine instance = new MyCmdLine(args);
        boolean result = instance.hasOption(opt);
        assertEquals(expResult, result);
    }

    /**
     * Test of inputFile method, of class MyCmdLine.
     *
     * @param args
     * @param resultString
     */
    @ParameterizedTest
    @MethodSource("provideArgsForTestInputFile_0args")
    public void testInputFile_0args(String[] args, String resultString)
    {
        System.out.println("[MyCmdLineTest.testInputFile_0args()]");
        MyCmdLine instance = new MyCmdLine(args);
        File expResult = resultString != null ? new File(resultString) : null;
        File result = instance.inputFile();
        assertEquals(expResult, result);
    }

    /**
     * Test of inputFile method, of class MyCmdLine.
     *
     * @param args
     * @param resultString
     */
    @ParameterizedTest
    @MethodSource("provideArgsForTestInputFile")
    public void testInputFile_File(String[] args, String resultString)
    {
        System.out.println("[MyCmdLineTest.testInputFile_File()]");
        MyCmdLine instance = new MyCmdLine(args);
        File expResult = resultString != null ? new File(resultString) : null;
        instance.inputFile(expResult);
        File result = instance.inputFile();
        assertEquals(expResult, result);
    }

    /**
     * Test of jarFile method, of class MyCmdLine.
     *
     * @param args
     * @param resultString
     */
    @ParameterizedTest
    @MethodSource("provideArgsForTestJarFile")
    public void testJarFile(String[] args, String resultString)
    {
        System.out.println("[MyCmdLineTest.testJarFile()]");
        MyCmdLine instance = new MyCmdLine(args);
        File expResult = of(resultString).toFile();
        File result = instance.jarFile();
        assertEquals(expResult, result);
    }

    /**
     * Test of jarSourcePath method, of class MyCmdLine.
     *
     * @param args
     * @param resultString
     */
    @ParameterizedTest
    @MethodSource("provideArgsForTestJarSourcePath")
    public void testJarSourcePath(String[] args, String resultString)
    {
        System.out.println("[MyCmdLineTest.testJarSourcePath()]");
        MyCmdLine instance = new MyCmdLine(args);
        Path expResult = of(resultString).normalize().toAbsolutePath();
        Path result = instance.jarSourcePath();
        assertEquals(expResult, result);
    }

    /**
     * Test of outputFile method, of class MyCmdLine.
     *
     * @param args
     * @param resultString
     */
    @ParameterizedTest
    @MethodSource("provideArgsForTestOutputFile_0args")
    public void testOutputFile_0args(String[] args, String resultString)
    {
        System.out.println("[MyCmdLineTest.testOutputFile_0args()]");
        MyCmdLine instance = new MyCmdLine(args);
        File expResult = resultString != null ? of(resultString).toFile() : null;
        File result = instance.outputFile();
        assertEquals(expResult, result);
    }

    /**
     * Test of outputFile method, of class MyCmdLine.
     *
     * @param args
     * @param resultString
     */
    @ParameterizedTest
    @MethodSource("provideArgsForTestOutputFile_File")
    public void testOutputFile_File(String[] args, String resultString)
    {
        System.out.println("[MyCmdLineTest.testOutputFile_File()]");
        MyCmdLine instance = new MyCmdLine(args);
        File expResult = resultString != null ? of(resultString).toFile() : null;
        instance.outputFile(expResult);
        File result = instance.outputFile();
        assertEquals(expResult, result);
    }

    /**
     * Test of pomFile method, of class MyCmdLine.
     *
     * @param args
     * @param resultString
     */
    @ParameterizedTest
    @MethodSource("provideArgsForTestPomFile")
    public void testPomFile(String[] args, String resultString)
    {
        System.out.println("[MyCmdLineTest.testPomFile()]");
        MyCmdLine instance = new MyCmdLine(args);
        File expResult = of(resultString).toFile();
        File result = instance.pomFile();
        assertEquals(expResult, result);
    }

    /**
     * Test of source method, of class MyCmdLine.
     *
     * @param args
     * @param resultString
     */
    @ParameterizedTest
    @MethodSource("provideArgsForTestSource_0args")
    public void testSource_0args(String[] args, String resultString)
    {
        System.out.println("[MyCmdLineTest.testSource_0args()]");
        MyCmdLine instance = new MyCmdLine(args);
        Path expResult = of(resultString).normalize().toAbsolutePath();
        Path result = instance.source();
        assertEquals(expResult, result);
    }

    /**
     * Test of source method, of class MyCmdLine.
     *
     * @param args
     * @param resultString
     */
    @ParameterizedTest
    @MethodSource("provideArgsForTestSource_Path")
    public void testSource_Path(String[] args, String resultString)
    {
        System.out.println("[MyCmdLineTest.testSource_Path()]");
        MyCmdLine instance = new MyCmdLine(args);
        Path expResult = of(resultString).normalize().toAbsolutePath();
        instance.source(expResult);
        Path result = instance.source();
        assertEquals(expResult, result);
    }

    /**
     * Test of success method, of class MyCmdLine.
     *
     * @param args
     * @param expResult
     */
    @ParameterizedTest
    @MethodSource("provideArgsForTestSuccess")
    public void testSuccess(String[] args, boolean expResult)
    {
        System.out.println("[MyCmdLineTest.testSuccess()]");
        MyCmdLine instance = new MyCmdLine(args);
        boolean result = instance.success();
        assertEquals(expResult, result);
    }

    /**
     * Test of verbosity method, of class MyCmdLine.
     *
     * @param args
     * @param expResult
     */
    @ParameterizedTest
    @MethodSource("provideArgsForTestVerbosity")
    public void testVerbosity(String[] args, int expResult)
    {
        System.out.println("[MyCmdLineTest.testVerbosity()]");
        MyCmdLine instance = new MyCmdLine(args);
        int result = instance.verbosity();
        assertEquals(expResult, result);
    }
}
