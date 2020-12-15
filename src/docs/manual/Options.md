@@@
use : articles3
title: ${program.title} | Command-Line Options
width: 61em;
@@@


## Command-Line Options

These are the available options:

Command Line Options
|Option|Description|
|----|----|
|`-c`|Display Copyright notice.|
|`-d <directory>`|The destination directory for HTML files.<br>(default: "" - current directory)|
|`-i <filename>`|The markdown input file to parse. (`*.md`)|
|`-j <jarfile>;<jarSrcDir>;<docRootDir>`|Copy HTML files from directory into a new `jar` file.<br>**Note:** Can *not* be used with any other switches, except `-v [<level>]`.|
|`-m`|Display web based manual in system default web browser.|
|`-o <filename>`|The HTML output file. (`*.html`)|
|`-r`|Recursively process directories.|
|`-s <directory>`|The source directory for markdown files.<br>(default: "" - current directory)|
|`-v [<level>]`|Verbosity. (default: `<level>` = `0`, or `1` if set with no level [`1`-`3`])|
|`-w`|Process meta block, wrapping your document with templates and stylesheets.|
|`-W [<docRootDir>]`|Initialise wrapper directories and files.<br>**Note:** Can *not* be used with any other switches, except `-v [<level>]`.|
|`-h --help`|Display the help text.|[total]

@@@[navbar]
- [Home]
- [@dropdown] [Setup][@dropbtn]
[@dropdown-content]
    - [Configuration]
    - [@active] [Command-line Options](#)
- [@dropdown] [Meta Blocks][@dropbtn]
[@dropdown-content]
    - [Named Meta Blocks]
- [@right dropdown] [About][@dropbtn]
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
