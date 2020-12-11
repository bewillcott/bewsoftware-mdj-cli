@@@
use : articles2
title: ${program.title} | ToDo List
@@@

[Home]

---

## ToDo List

- [ ]! Revise manual.
- [ ]! Meta blocks:
    - [X]! Add file [meta block][mb]:
    - [x]! Add [named meta blocks][nmb]. `@@@[name]`
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
        - [X]! (09/12/2020) include meta data from [MANIFEST.mf] section in<br>`mdj-cli.ini`.
    - [x]! Add options related to _jar_ file creation: `-j`
- [x]! Add new [switches][opt]:
    - [x]! (26/02/2020)`-W` : Create new directories (css, templates) in current directory.  
Add default versions of: `style.css` and `default.html`.
    - [x]! (26/02/2020)`-w` : Include/use the contents of these directories as needed.
- [x]! -!(24/02/2020) Added JSAP functionality. Configure the [options][opt]![1]
- [X]! +!(04/12/2020) Replaced JSAP with Apache Commons CLI. Configure the [options][opt]!



[Home]:index.html
[mb]:Meta Blocks.html
[nmb]:Named Meta Blocks.html
[opt]:Options.html
[1]: "Deprecated"
