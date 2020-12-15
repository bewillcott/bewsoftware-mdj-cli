@@@
use : articles3
title: ${program.title} | ToDo List
width: 55em;
@@@


## ToDo List

- [ ]! Revise manual.
    - [x]! (12/12/2020) Add option to display copyright notice: `-c`.
    - [x]! (12/12/2020) Add option to display web based manual in system default web browser: `-m`.
- [ ]! Meta blocks:
    - [X]! Add file [meta block][mb]:
    - [x]! (14/12/2020) Add [named meta blocks][nmb]. `@@@[name]`
    - [ ]! Enhance usage of [named meta blocks][nmb]:
        - [ ]! Wrap each in `<div class="<block-name>"></div>`
        - [ ]! Add special meta block names: `navbar`, `footnotes`, `paging_navbar`.
    - [ ]! Add reading project POM file: `-p <path/to/pom.xml>`. `${project.*}`
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
- [x]! Add new [switches][opt]:
    - [x]! (26/02/2020)`-W` : Create new directories (css, templates) in current directory.  
Add default versions of: `style.css` and `default.html`.
    - [x]! (26/02/2020)`-w` : Include/use the contents of these directories as needed.
- [x]! -!(24/02/2020) Added JSAP functionality. Configure the [options][opt]![1]
- [X]! +!(04/12/2020) Replaced JSAP with Apache Commons CLI. Configure the [options][opt]!



[mb]:Meta Blocks.html
[nmb]:Named Meta Blocks.html
[opt]:Options.html
[1]: "Deprecated"

@@@[navbar]
- [Home]
- [@dropdown] [Setup][@dropbtn]
[@dropdown-content]
    - [Configuration]
    - [Command-line Options]
- [@dropdown] [Meta Blocks][@dropbtn]
[@dropdown-content]
    - [Named Meta Blocks]
- [@right dropdown] [About][@dropbtn]
[@dropdown-content]
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
