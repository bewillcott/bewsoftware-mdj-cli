@@@
use : articles2
title: ${document.name} | Command-Line Options
@@@

## Command-Line Options

These are the available options:

Command Line Options
|Option|Description|
|----|----|
|`-c`|Display Copyright notice.|
|`-d <directory>`|The destination directory for HTML files.<br>(default: `""` - current directory)|[@alt]
|`-D <property>=<value>`|POM build property. \
    Is made available as: `${project.<property>}`.|[@reset]
|`-i <fileName>`|The markdown input file to parse. (`*.md`)|
|`-j <jarFile>;<jarSrcDir>;<docRootDir>`|Copy HTML files from directory into a new \
    'jar' file.<br>**Note:** Can *not* be used with any other switches, except `-v [<level>]`.|
|`-m`|Display web based manual in system default web browser.|
|`-o <fileName>`|The HTML output file. (`*.html`)|
|`-P <context>=<htmlSource>`|Publish the HTML files from either a directory, \
    or a 'jar' file.<br> 'htmlSource' is either the directory to publish,<br> or \
    the path to the 'jar' file (including it's name and extension).<br>(defaults: \
    context: `"/"`, htmlSource: `""` - current directory)<br>Can be used multiple \
    times to publish multiple sources at once.|
|`--allowGeneratedIndex`|Allow a directory listing to be generated, if no 'index' \
    file found.<br>Use with option: `-P`.<br>(default: `false`)|
|`--disallowBrowserFileCaching`|Disallow web browsers caching the files sent by \
    this instance of the web server.<br>Use with option: `-P`.<br>(default: `false`)|
|`-p <filePath>`|The /path/to/the/pom.xml file. (pom.xml)|
|`-r`|Recursively process directories.|
|`-s <directory>`|The source directory for markdown files.<br>(default: `""` - current directory)|
|`-v [<level>]`|Verbosity. (default: `<level>` = `0`, or `1` if set with no level [`1`-`3`])|
|`-w`|Process meta block, wrapping your document with templates and stylesheets.|
|`-W [<docRootDir>]`|Initialize wrapper directories and files.<br>**Note:** Can \
    *not* be used with any other switches, except `-v [<level>]`.|
|`-h --help`|Display the help text.|[total]

@@@[#navbar]
- [Home]
- [@subactive] [Setup]
    - [Configuration]
    - [@active] [Command-line Options](#)
- [Meta Blocks]
    - [Named Meta Blocks]
- [@right] [About]
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
