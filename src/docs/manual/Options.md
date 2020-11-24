@@@
use : articles
title: Markdownj CLI | Command-Line Options
@@@

[Home]

---

## Command-Line Options

These are the available options:

Command Line Options
|Option|Description|
|----|----|
|`-i <filename>`|Input file|
|`-o <filename>`|Output file|
|`-s <Directory>`|Source directory (default: "")|
|`-d <Directory>`|Destination directory (default: "")|
|`-r`|Recursive processing of directory tree.|
|`-w`|Process meta block, providing access to stylesheets and templates.|
|`-v:n`|Verbose processing.  List files as they are processed.<br>Set verbose level with "`-v:1`" or "`-v:2`".  "`-v`" defaults to level '1' |
|`-j[:]`|Copy html output target directory into a new 'jar' file.<br>**Note:** Can *not* be used with any other switches, except `-v:n`.|
|`-W[:docRootDir]`|Create directories: `css` and `templates` in current directory.<br>**Note:** Can *not* be used with any other switches, except `-v:n`.|
|`-h --help`|Online help|[total]

[Home]:index.html
