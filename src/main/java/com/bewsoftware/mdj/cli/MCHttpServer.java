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
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import javax.swing.JOptionPane;

import static com.bewsoftware.httpserver.HTTPServer.TITLE;
import static com.bewsoftware.httpserver.HTTPServer.VERSION;
import static com.bewsoftware.httpserver.HTTPServer.addContentTypes;
import static com.bewsoftware.httpserver.Utils.openURL;
import static java.lang.System.exit;

/**
 * MCHttpServer class provides a local focus for the server's classes.
 *
 * @author <a href="mailto:bw.opensource@yahoo.com">Bradley Willcott</a>
 *
 * @since 1.0.28
 * @version 1.0.28
 */
public class MCHttpServer extends HTTPServer {

    /**
     * Starts a stand-alone HTTP server, serving files from disk.
     */
    public static void execute() {
        MCHttpServer server = null;

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

            // The containing 'jar' file.
            URI jarURI = URI.create("jar:" + server.getClass().getProtectionDomain()
                    .getCodeSource()
                    .getLocation()
                    .toURI().toString());

            VirtualHost host = server.getVirtualHost(null); // default host
            host.setAllowGeneratedIndex(true); // with directory index pages
            host.addContext("/", new JarContextHandler(jarURI, "/manual"));
            host.addContext("/api/time", (Request req, Response resp) ->
                    {
                        long now = System.currentTimeMillis();
                        resp.getHeaders().add("Content-Type", "text/plain");
                        resp.send(200, String.format("Server time: %tF %<tT", now));
                        return 0;
                    });

            server.start();
            String msg = TITLE + " (" + VERSION + ") is listening on port " + server.port;
            System.out.println(msg);
            openURL(new URL("http", "localhost", server.port, "/"));

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
        } catch (IOException | NumberFormatException | URISyntaxException | InterruptedException e)
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

}
