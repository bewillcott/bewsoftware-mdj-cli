@@@
use : articles2
title: ${document.name} | ToDo List
@@@


## ToDo List

- [ ]! Revise manual.
    - [ ]! Configuration.md
- [x]! (12/12/2020) Add option to display copyright notice: `-c`.
- [x]! (12/12/2020) Add option to display web based manual in system default web browser: `-m`.
- [x]! (24/12/2020) Add option to publish files in a directory of the file system, or from an
        external 'jar' file: `-p`.
    - [x]! (24/12/2020) Add switch to allow the generation of directory listings: `--allowGeneratedIndex`.
    - [x]! (24/12/2020) Add switch to disallow browsers caching files: `--disallowBrowserFileCaching`.
- [ ]! Add option to insert the HTTP Server into an existing 'jar' file, to server pages
       from within.  For example: a program's javadoc.jar file: `-a`.
- [ ]! Meta blocks:
    - [X]! Add file [meta block][mb]:
    - [x]! (14/12/2020) Add [named meta blocks][nmb]. `@@@[name]`
    - [ ]! Enhance usage of [named meta blocks][nmb]:
        - [x]! Wrap each in `<div class="<name>"></div>`
        - [x]! Change `@@@[name]` to two options: 
            - [x]! `@@@[#name]` - `<div id="<name>"></div>`
            - [x]! `@@@[@name]` - `<div class="<name>"></div>`
        - [ ]! Add special meta block names: `navbar`, `footnotes`, `paging_navbar`.
    - [x]! Add reading project POM file: `-P <path/to/pom.xml>`. `${project.*}`
    - [ ]! Add include file:
        - [ ]! Include markdown file: `includeMD` : `filename.md`
        - [ ]! Include html file: `includeHTML` : `filename.html`
    - [x]! Add html template file option.
    - [x]! Add including CSS file.
    - [x]! Add variables for template replacement parameters.
- [x]! Add configuration file: `mdj-cli.ini`
- [ ]! Add auto-generated Table of Contents for each page.
- [X]! (09/12/2020) Add archive creation as `jar` file:
    - [x]! (04/12/2020) Auto-generate MANIFEST.mf file.
        - [X]! (09/12/2020) include meta data from [MANIFEST.mf] section in `mdj-cli.ini`.
    - [x]! Add options related to _jar_ file creation: `-j`
- [ ]! Add new [switches][opt]:
    - [x]! (26/02/2020)`-W` : Create new directories (css, templates) in current directory.  
            Add default versions of: `style.css` and `default.html`.
    - [x]! (26/02/2020)`-w` : Include/use the contents of these directories as needed.
    - [ ]! `-e` : `pom.xml` element to include in available list of substitution variables.  
           E.g.: `-e properties\javadoc.title` will become: `${project\properties\javadoc.title}`  
           for `<properties><javadoc.title>My Title</javadoc.title></properties>`  
           All elements are children of `<project>`.
    - [ ]! `-E` : Add a new element to the substitution variables.  
           E.g.: `-E mydata\id=12345` will become: `${project\mydata\id}`
- [x]! -!(24/02/2020) Added JSAP functionality. Configure the [options][opt]![1]
- [X]! +!(04/12/2020) Replaced JSAP with Apache Commons CLI. Configure the [options][opt]!
- [ ]! Add new ordered list for type: `a)`, `b)`, `c)`, etc.


[mb]:Meta Blocks.html
[nmb]:Named Meta Blocks.html
[opt]:Options.html
[1]: "Deprecated"

@@@[#navbar]
- [Home]
- [Setup]
    - [Configuration]
    - [Command-line Options]
- [Meta Blocks]
    - [Named Meta Blocks]
- [@right subactive] [About]
    - [@active] [ToDo List](#)
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
