@@@
use : articles3
title: ${program.title} | Setup
width: 54em;
@@@

[Home]

---


##[#top] Setup

To setup everything, run the following from either your project or document root
directory:

`java -jar /path/to/mdj-cli-<version>.jar -v -W [<docRootDir>]`

For example - a new project directory structure: (Linux)

~~~
~/NetBeansProjects/MyProject
   ├── pom.xml
   └── src
       └── docs
           └── manual
               ├── css
               │   └── style.css
               ├── etc
               │   └── markdown16.png
               ├── index.md
               ├── mdj-cli.ini
               └── templates
                   ├── article_1.html
                   ├── article_2.html
                   ├── article.html
                   └── default.html
~~~

Of course, this is not the entire directory structure, but that which is 
pertinent to what would be setup should you use the following command from 
inside the project root directory:

~~~
~/NetBeansProjects/MyProject$ java -jar /path/to/mdj-cli-${program.version}.jar -v -W src/docs/manual
~~~

__Note:__ the switch is a capital `W`.

This will configure and install the `mdj-cli.ini` file and create the directories:
`css`, `etc` and `templates`, and install their files.

Now if you are writing a book: (Linux)

~~~
~/Documents/MyBook
    ├── css
    │   └── style.css
    ├── etc
    │   └── markdown16.png
    ├── index.md
    ├── mdj-cli.ini
    └── templates
        ├── article_1.html
        ├── article_2.html
        ├── article.html
        └── default.html
~~~

To setup this up, run the following command from inside the book root directory:

~~~
~/Documents/MyBook$ java -jar /path/to/mdj-cli-${program.version}.jar -v -W
~~~


[Home]:index.html
