@@@
use : articles2
title: ${program.title} | About
@@@


## About : v${program.version}

    BEWSoftware MDj CLI (mdj-cli) is a command-line utility for processing 
    markdown text files into static html files.

    Copyright (C) 2020, 2021 Bradley Willcott

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.

MDj CLI came about because of my need as a programmer to produce readily 
accessible end-user documentation for the software I was developing. I am
by nature, lazy.  Probably why I got into programming in the first place,
as I love having a computer do for me what I was too lazy to do for myself.

I wanted a way of producing good looking documentation without having to 
code html, or some other verbose descriptive language.  When I came
across Markdownj, I believed I had found my solution.  Unfortunately, it
was only part way there.  The library I found[1] had not been updated in
more than 7 years.  Further, I was not aware of any command-line utility 
that used it.

I decided to take on the project of achieving both.  First, I had to update
the code to work with a later version of the JDK, currently 12.  Along the way
I added many features, such as "Fenced Code Blocks".  

Then came the task of the cli tool.  This gave me more opportunities to
enhance the capabilities of the markdown syntax.  Among other things, I added
"Meta Blocks".  Very similar to that used on GitHub Pages.  I also added
the use of an 'ini' file to configure **mdj-cli** on a project/book level.

Though the current version is fully functional, I am still adding to it.
I will continue to do so as I find a need for a feature it currently doesn't
have.  So, feel free to grab a copy of the code, or the binaries.  If you 
find some little feature you'd like to have that isn't there, email me.
I might have a use for it as well.

---

1.[#fn1] [Markdownj on GitHub][markdownj]! 

[1]:#fn1 "Original source on Github"
[markdownj]:https://github.com/myabc/markdownj

@@@[navbar]
- [Home]
- [@dropdown] [Setup]
[@dropdown-content]
    - [Configuration]
    - [Command-line Options]
- [@dropdown] [Meta Blocks]
[@dropdown-content]
    - [Named Meta Blocks]
- [@right dropdown active] [About](#)
[@dropdown-content]
    - [ToDo List]
    - [License]

[About]:About.html
[Configuration]:Configuration.html
[Home]:index.html
[License]:LICENSE.html
[Meta Blocks]:Meta Blocks.html
[Named Meta Blocks]:Named Meta Blocks.html
[Command-line Options]:Options.html
[Setup]:Setup.html
[ToDo List]:ToDo.html
@@@
