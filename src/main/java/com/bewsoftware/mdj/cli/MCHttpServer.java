/*
 * This file is part of the MDj Command-line Interface program
 * (aka: mdj-cli).
 *
 * Copyright (C) 2020 Bradley Willcott
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
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.bewsoftware.mdj.cli;

import com.bewsoftware.httpserver.*;
import com.bewsoftware.utils.struct.ExceptionReturn;
import com.bewsoftware.utils.struct.StringReturn;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.swing.JOptionPane;
import org.apache.commons.cli.MissingArgumentException;

import static com.bewsoftware.httpserver.HTTPServer.TITLE;
import static com.bewsoftware.httpserver.HTTPServer.VERSION;
import static com.bewsoftware.httpserver.HTTPServer.addContentTypes;
import static com.bewsoftware.httpserver.Utils.openURL;
import static java.lang.System.exit;
import static java.nio.file.LinkOption.NOFOLLOW_LINKS;
import static java.nio.file.Path.of;

/**
 * MCHttpServer class provides a local focus for the server's classes.
 *
 * @author <a href="mailto:bw.opensource@yahoo.com">Bradley Willcott</a>
 *
 * @since 1.0.28
 * @version 1.0.28
 */
class MCHttpServer extends HTTPServer {

    /**
     * Starts a stand-alone HTTP server, serving files from disk.
     *
     * @param cmd The command line options, if any.
     */
    public static void execute(CmdLine cmd) {
        MCHttpServer server = null;
        String context = "/";

        try
        {
            server = new MCHttpServer(DEFAULT_PORT_RANGE[0]);

            // set up server
            File f = new File("/etc/mime.types");

            if (f.exists())
            {
                addContentTypes(new FileInputStream(f));
            } else
            {
                addContentTypes(HTTPServer.class.getResourceAsStream("/docs/jar/etc/mime.types"));
            }

            VirtualHost host = server.getVirtualHost(null); // default host
            host.setAllowGeneratedIndex(cmd.hasOption('p') && cmd.hasOption("allowGeneratedIndex")); // with directory index pages
            host.addContext("/time", (Request req, Response resp) ->
                    {
                        long now = System.currentTimeMillis();
                        resp.getHeaders().add("Content-Type", "text/plain");
                        resp.send(200, String.format("Server time: %tF %<tT", now));
                        return 0;
                    });

            if (cmd.hasOption('m'))
            {
                // The containing 'jar' file.
                URI jarURI = URI.create("jar:" + server.getClass().getProtectionDomain()
                        .getCodeSource()
                        .getLocation()
                        .toURI());
                host.addContext(context, new JarContextHandler(jarURI, "/manual"));
            } else if (cmd.hasOption('p'))
            {
                // An External directory or 'jar' file.
                final ExceptionReturn er = new ExceptionReturn();
                final StringReturn contextReturn = new StringReturn();
                final String contextDefault = context;

                cmd.getOptionProperties('p').forEach((contextObj, textObj) ->
                {
                    String strContext = (String) contextObj;

                    if (strContext.equals(contextDefault))
                    {
                        contextReturn.val = strContext;
                    }

                    if (contextReturn.val == null)
                    {
                        contextReturn.val = strContext;
                    }

                    try
                    {
                        ContextToPublish publish = new ContextToPublish((String) textObj);

                        if (publish.htmlSource != null)
                        {
                            host.addContext(strContext, new FileContextHandler(publish.htmlSource.toString()));
                        } else
                        {
                            host.addContext(strContext, new JarContextHandler(
                                            URI.create("jar:" + publish.jarFile.toURI()), "/"));
                        }
                    } catch (Exception ex)
                    {
                        er.val = ex;
                    }
                });

                if (er.val != null)
                {
                    throw er.val;
                }

                context = contextReturn.val;
            }

            server.start();
            String msg = TITLE + " (" + VERSION + ") is listening on port " + server.port;
            System.out.println(msg);
            openURL(new URL("http", "localhost", server.port, context));

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
            System.out.println(msg);
            exit(0);
        } catch (Exception e)
        {
            System.err.println("error: " + e);
        }
    }

    /**
     * Constructs an HTTPServer which can accept connections on the default HTTP port 80.
     * Note: the {@link #start()} method must be called to start accepting connections.
     */
    public MCHttpServer() {
        super();
    }

    /**
     * Constructs an HTTPServer which can accept connections on the given port.
     * Note: the {@link #start()} method must be called to start accepting
     * connections.
     *
     * @param port the port on which this server will accept connections
     */
    public MCHttpServer(int port) {
        super(port);
    }

    /**
     * Used to decode the property value for the '-p' option.
     *
     * @since 1.0.30
     * @version 1.0.30
     */
    private static class ContextToPublish {

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
         * @param text The option's property value text.
         *
         * @throws IOException              if any.
         * @throws MissingArgumentException if any.
         */
        private ContextToPublish(String text) throws IOException, MissingArgumentException {
            if (text == null)
            {
                text = "";
            }

            Path srcPath = of(text.replace('\\', '/')).toRealPath(NOFOLLOW_LINKS);

            if (!Files.isDirectory(srcPath, NOFOLLOW_LINKS))
            {
                if (srcPath.toString().endsWith(".jar"))
                {
                    jarFile = srcPath.toFile();
                } else
                {
                    throw new MissingArgumentException(
                            "\n'htmlSource' is neither a directory or a 'jar' file.");
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
