@@@
use : articles
title: ${program.title} | Configuration
@@@

[Home]

---

##[#top] Configuration

MDj CLI is configured via an _ini_ file: `mdj-cli.ini`.  If [setup] properly,
this file should be either in your project directory, or in the root directory of the
document you are working on.

For example, the MDj CLI project directory structure: (Linux)

~~~
~/NetBeansProjects/mdj-cli
    ├── jshell.history
    ├── licenseheader.txt
    ├── markdownj-cli.ini
    ├── nbactions.xml
    ├── nb-configuration.xml
    ├── pom.xml
    └── src
        └── docs
            └── manual
                ├── configuration.html
                ├── configuration.md
                ├── css
                │   └── style.css
                ├── index.html
                ├── index.md
                ├── meta blocks.html
                ├── meta blocks.md
                ├── options.html
                ├── options.md
                ├── templates
                │   ├── article.html
                │   └── default.html
                ├── todo.html
                └── todo.md

~~~



[Home]:index.html
[setup]:Setup.html
