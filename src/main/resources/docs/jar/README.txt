========================================================================

Copyright © 2005-2019 Amichai Rothman
Copyright © 2020 Bradley Willcott

HTTPServer is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

HTTPServer is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <https://www.gnu.org/licenses/>.

For additional info see http://www.freeutils.net/source/jlhttp/

========================================================================

This archive ('jar' file) was created by BEWSoftware MDj CLI.
A program that processes markdown files into static html files.
Along with those files, there is a stand-alone http server
included in this archive.

HTTPServer is a modified version of the code from:
<https://www.freeutils.net/source/jlhttp/>
JLHTTP - Java Lightweight HTTP Server (Web Server)

With this server you can very easily view the contents of this
archive using your default web browser.

NOTE: If you have set another program as the default viewer
for HTML files, then _that_ program will be opened instead.
However, you will still be able to connect to the server with
your favorite web browser, by manually typing the connection
address:

http://localhost:<port>

The port number will be found in the dialog box that will also
have a button to [stop] the server.  You can start and stop the
server as many times as you want/need.  The only limit, is it
has been set to use a port in the range: 9000 - 9010 (inclusive).
So, at most you can only have 11 copies running at once.  Though
why you would need that I have no idea.  Since this is a proper
compliant web server, it can take multiple client connections on
the one port.

To start everything, you need at least Java SE 18, or later installed
on your system.  Then on a command line (terminal/cmd) type:

  java -jar <jarfilename>.jar

Do this in the same directory you stored the 'jar' file.  You should
see something like this:

HTTP Server (v2.7.0) is listening on port 9000
Browser: http://localhost:9000/

and a dialog box will also pop up, along with your browser.

Happy viewing,
Bradley Willcott
<www.bewsoftware.com>
