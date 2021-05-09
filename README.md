# BEWSoftware MDj CLI
Command-line program that uses the BEWSoftware MDj Core library to process markdown 
text files into static html files.

This is a renamed package.  It was the Markdownj-Cli project, and the core
was Markdownj-Core.  However, as I have done a lot of code changes to the core
library and plan to publish both this program and the core, I believe that the
rename was necessary.  Further, the code in both has been refactored to a new
package basename of: `com.bewsoftware.mdj.*`.  `bewsoftware.com` is a domain 
name that I now own.

## Manual
The current manual can viewed [here]!.

## Status
This is a program under active development.  It is not yet ready for release.
However, I have placed it here for public reference purposes.

Feel free to clone what is here, but realize that I am still working on it.  
The documentation is limited.  That is part of the work in progress.

## Dependencies
You will also need two other projects to compile this one:

- [BEWSoftware Libraries][bewl]
- [BEWSoftware MDj Core Library][mjc]

Sorry, but I have not as yet uploaded any of these libraries to the Maven
Repository.  Once I have everything ship-shape, then I will.

## See also
The original source code project: [Markdownj].


[here]:https://mdjcli.bewsoftware.com/
[Markdownj]:https://github.com/myabc/markdownj
[bewl]:https://github.com/bewillcott/bewsoftware-libs
[mjc]:https://github.com/bewillcott/bewsoftware-mdj
