@@@
use:defaults
title: Markdownj CLI | Setup
@@@

[Home]

---

##[#top] Setup

To setup everything, run the following from either your project or document root
directory:

`java -jar /path/to/dirof/markdownj-cli\${version}.jar -v -W[:\${docRootDir}]`

For example, the Markdownj CLI project root directory structure: (Linux)

~~~
~/NetBeansProjects/markdownj/markdownj-cli
    ├── markdownj-cli.ini
    ├── pom.xml
    └── src
        └── docs
            └── manual
                ├── css
                │   └── style.css
                ├── index.html
                ├── index.md
                └── templates
                    └── default.html
~~~

To setup the above structure, run:

~~~
$ pwd
$ ~/NetBeansProjects/markdownj/markdownj-cli
$ java -jar /path/to/dirof/markdownj-cli${program.version}.jar -v -W:src/docs/manual
~~~

__Note:__ the switch is a capital `W`.

This will configure and install the `markdownj-cli.ini` and create the directories:
`css` and `templates`, and install their respective files: ``styles.css` and `default.html`.

Now if you are writing a book: (Windows)

~~~
~/Documents/MyBook
    ├── markdownj-cli.ini
    ├── pom.xml
    ├── css
    │   └── style.css
    ├── index.html
    ├── index.md
    └── templates
        └── default.html
~~~

To setup the above structure, run:

~~~
$ pwd
$ ~/Documents/MyBook
$ java -jar /path/to/dirof/markdownj-cli${program.version}.jar -v -W
~~~

---
${document.copyright}  
Last updated: ${system.date}

[Home]:index.html
