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
package com.bewsoftware.mdj.cli.util;

import com.bewsoftware.httpserver.*;
import com.bewsoftware.utils.struct.Ref;
import java.awt.HeadlessException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.swing.JOptionPane;
import org.apache.commons.cli.MissingArgumentException;

import static com.bewsoftware.httpserver.HTTPServer.TITLE;
import static com.bewsoftware.httpserver.HTTPServer.VERSION;
import static com.bewsoftware.httpserver.HTTPServer.addContentTypes;
import static com.bewsoftware.httpserver.Utils.openURL;
import static com.bewsoftware.mdj.cli.util.Constants.DISPLAY;
import static com.bewsoftware.mdj.cli.util.GlobalVariables.exception;
import static java.nio.file.LinkOption.NOFOLLOW_LINKS;
import static java.nio.file.Path.of;

/**
 * MCHttpServer class provides a local focus for the server's classes.
 *
 * @author <a href="mailto:bw.opensource@yahoo.com">Bradley Willcott</a>
 *
 * @since 1.0.28
 * @version 2.0.0
 */
public class MCHttpServer extends HTTPServer
{

    /**
     * Constructs an HTTPServer which can accept connections on the default HTTP
     * port 80.
     * Note: the {@link #start()} method must be called to start accepting
     * connections.
     */
    public MCHttpServer()
    {
        super();
    }

    /**
     * Constructs an HTTPServer which can accept connections on the given port.
     * Note: the {@link #start()} method must be called to start accepting
     * connections.
     *
     * @param port the port on which this server will accept connections
     */
    public MCHttpServer(int port)
    {
        super(port);
    }

    /**
     * Starts a stand-alone HTTP server, serving files from disk.
     *
     * @param cmd The command line options, if any.
     *
     * @return exit code
     */
    public static int execute(final CmdLine cmd)
    {
        int rtn = 0;
        MCHttpServer server;
        String context = "/";

        try
        {
            server = new MCHttpServer(DEFAULT_PORT_RANGE[0]);

            setupServer();

            context = setupHost(server, cmd, context);

            server.start();
            String msg = TITLE + " (" + VERSION + ") is listening on port " + server.port;
            DISPLAY.level(0).println(msg);

            openURL(new URI("http", "", "localhost", server.port, context, "", "").toURL());

            // GUI dialog to show server running, with button to
            // shutdown server.
            //
            //Custom button text
            Object[] options =
            {
                "Stop Server"
            };

            JOptionPane.showOptionDialog(null, msg, TITLE + " (" + VERSION + ")",
                    JOptionPane.OK_OPTION, JOptionPane.WARNING_MESSAGE,
                    null, options, null);

            server.stop();
            msg = TITLE + " (" + VERSION + ") on port " + server.port + " has terminated.";
            DISPLAY.level(0).println(msg);
        } catch (HeadlessException | IOException | InterruptedException | URISyntaxException | MissingArgumentException ex)
        {
            exception = ex;
            rtn = -1;
        }

        return rtn;
    }

    private static void addContextForContainingJar(
            final String context,
            final VirtualHost host,
            final MCHttpServer server,
            final String directory
    ) throws IOException, URISyntaxException
    {
        // The containing 'jar' file.
        URI jarURI = URI.create(
                "jar:"
                + server
                        .getClass()
                        .getProtectionDomain()
                        .getCodeSource()
                        .getLocation()
                        .toURI()
        );

        host.addContext(context, new JarContextHandler(jarURI, directory));
    }

    private static String addContextForExternalDirOrJar(
            final String context,
            final CmdLine cmd,
            final VirtualHost host
    )
    {
        // An External directory or 'jar' file.
        final Ref<Exception> exRtn = Ref.val();
        final Ref<String> ctRtn = Ref.val();

        cmd.getOptionProperties('P').forEach((contextObj, textObj) ->
        {
            String contextString = (String) contextObj;
            ctRtn.val = contextString;

            try
            {
                processContextForExternalDirOrJar((String) textObj, host, contextString);
            } catch (IOException | MissingArgumentException | URISyntaxException ex)
            {
                exRtn.val = ex;
            }
        });

        if (ctRtn.isEmpty())
        {
            try
            {
                processContextForExternalDirOrJar("", host, context);
            } catch (IOException | MissingArgumentException | URISyntaxException ex)
            {
                exRtn.val = ex;
            }
        }

        if (exRtn.isPresent())
        {
            exception = exRtn.val;
        }

        return ctRtn.val;
    }

    private static void processContextForExternalDirOrJar(
            final String htmlSourceString,
            final VirtualHost host,
            final String context
    ) throws IOException, MissingArgumentException, URISyntaxException
    {
        ContextToPublish publish = new ContextToPublish(htmlSourceString);

        if (publish.htmlSource != null)
        {
            host.addContext(
                    context,
                    new FileContextHandler(
                            publish.htmlSource.toString()
                    )
            );
        } else
        {
            host.addContext(
                    context,
                    new JarContextHandler(
                            URI.create(
                                    "jar:" + publish.jarFile.toURI()
                            ),
                            "/"
                    )
            );
        }
    }

    private static String setupHost(
            MCHttpServer server1,
            final CmdLine cmd,
            final String context
    ) throws IOException, MissingArgumentException, URISyntaxException
    {
        String rtn = context;

        // default host
        VirtualHost host = server1.getVirtualHost(null);

        // with directory index pages
        host.setAllowGeneratedIndex(
                cmd.hasOption('P')
                && cmd.hasOption("allowGeneratedIndex")
        );

        host.addContext(
                "/time",
                (Request req, Response resp) ->
        {
            long now = System.currentTimeMillis();
            resp.getHeaders().add("Content-Type", "text/plain");
            resp.send(200, String.format("Server time: %tF %<tT", now));
            return 0;
        });

        if (cmd.hasOption('m'))
        {
            addContextForContainingJar(context, host, server1, "/manual");
        } else if (cmd.hasOption('P'))
        {
            rtn = addContextForExternalDirOrJar(context, cmd, host);
        }

        return rtn;
    }

    private static void setupServer() throws IOException
    {
        // set up server
        File mimeTypes = new File("/etc/mime.types");

        if (mimeTypes.exists())
        {
            addContentTypes(new FileInputStream(mimeTypes));
        } else
        {
            addContentTypes(HTTPServer.class.getResourceAsStream(
                    "/docs/jar/etc/mime.types"
            ));
        }
    }

    /**
     * Used to decode the property value for the '-P' option.
     *
     * @since 1.0.30
     * @version 1.1.7
     */
    private static class ContextToPublish
    {

        /**
         * The HTML source path, or {@code null].
         */
        public final Path htmlSource;

        /**
         * The 'jar' file, or {@code null}.
         */
        public final File jarFile;

        /**
         * Instantiate this class.
         *
         * @param htmlSourceString The option's property value text.
         *
         * @throws IOException              if any.
         * @throws MissingArgumentException if any.
         */
        @SuppressWarnings("AssignmentToMethodParameter")
        private ContextToPublish(String htmlSourceString) throws IOException, MissingArgumentException
        {
            if (htmlSourceString == null)
            {
                htmlSourceString = "";
            }

            Path srcPath = of(htmlSourceString.replace('\\', '/')).toRealPath(NOFOLLOW_LINKS);

            if (!Files.isDirectory(srcPath, NOFOLLOW_LINKS))
            {
                if (srcPath.toString().endsWith(".jar"))
                {
                    jarFile = srcPath.toFile();
                } else
                {
                    throw new MissingArgumentException(
                            "\n'htmlSource' is neither a directory nor a 'jar' file.");
                }

                htmlSource = null;
            } else
            {
                htmlSource = srcPath;
                jarFile = null;
            }
        }
    }
}
