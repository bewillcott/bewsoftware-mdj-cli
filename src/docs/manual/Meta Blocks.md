@@@
use:articles2
title: ${program.title} | Meta Blocks
@@@

[Home]

---

####[#top]Table of Page Contents

- [Meta Blocks](#top)
    - [Sections](#sections)
        - [Program](#program)
    - [More information about keys](#keys)
        - [Use](#page.use)
        - [Title](#page.title)
        - [Template](#template)
        - [Stylesheet](#stylesheet)
        - [Text](#page.text)

---

## Meta Blocks

A meta block is a collection of key/value pairs located at the very top of a markdown file.  
It begins with a fence of 3 at signs: "@@@" alone at the beginning of the first line
of the file.  This is followed by any number of key/value pairs.

The meta block ends on a line that contains only another fence, as with the start,
alone at the beginning of the line.  The meta block is processed and removed from the text,
read-in from the file, prior to the markdown processing.

You can create any number of your own key/value pairs that can be used in 
substitution into the template and/or the document as it is processed.

However, there are some keys that are used by the application:

[MDj CLI Page Level Keys]
|Key|Description|[2]
|-:-|:--|[3]
|`use:`|Set this to the program configuration file [section] containing your desired settings for this page.|[]
|`template:`|Name of the template file to wrap the page's markdown output into.|
|`stylesheet:`|The stylesheet to use in the template for the page.|

MDj CLI provides some preset keys:

[MDj CLI Preset Keys]
|Key|Description|[2]
|-:-|:--|[3]
|`system.date`|Current date/time (`Date.toString()`: dow mon dd hh:mm:ss zzz yyyy).|[]
|`program.version`|Version of **mdj-cli** used to create html output file.|
|`page.content`|Markdown processed page text (html).|

The default template has a set of key substitutions:

[Default Template Keys]
|Key|Description|[2]
|-:-|:--|[3]
|`\${program.title}`|The name of the program used to create html output file.|[]
|`\${program.version}`|Version of **mdj-cli** used to create html output file.|[]
|`\${system.date}`|Current date/time (`Date.toString()`: dow mon dd hh:mm:ss zzz yyyy).|
|`\${page.title}`|Header Title for web page.|
|`\${page.stylesheet}`|Same as above, but used by template.|
|`\${page.content}`|Markdown processed page text (html).|


###[#sections] Sections ###[&uarr;](#top)

Sections or groups are a collection of information with a common relationship.

####[#program] Program ####[&uarr;](#top)

The `program` group contains information related to the MDj CLI application,  
taken from its project pom.properties file. This information may or may not be of any interest to you.

[`program` group]
|Key|Description|[2]
|-:-|:--|[3]
|`artifactId`|The identifier for this artifact that is unique within the group given by the groupID.|[]
|`description`|Project description.|
|`filename`|The filename of the binary output file.|
|`groupId`|Project GroupId.|
|`title`|Project Name.|
|`version`|The version of the artifact.|
|`details`|All of the above information laid out.|

###[#keys] More information about keys ###[&uarr;](#top)

The following is a copy of the top of the source file for this page:

~~~
@@@
use:articles
title: MDj CLI | Meta Blocks
@@@
~~~

####[#page_use] Use ####[&uarr;](#top)
`use` is a reserved key word in the context of `page`: `page.use`.

As you can see, there is a `use` key with a value of `articles`.  If you were to check the configuration file:
`mdj-cli.ini` in the root directory of this project, you will find a section labeled as `[articles]`.
The key/value pairs in this section are the ones being used for this page.  If needed, you could add any
number of additional such pairs, and they would be available to any page set to `use : articles`.

If needed, you could easily add your own special sections to the configuration file, with their key/value pairs.
This would be a way of providing consistency across various sections and pages of your document.

Though you can save time and effort by using this method, it doesn't lock you in, so to speak.  Any of the 
_section_ level settings can be overridden at the _page_ level.  For instance, you may need a specific 
stylesheet for a page, which is different to that provided by the section you are `use`ing.  You would do that
by adding a `stylesheet : myspecial.css` to the meta block for the page.  Note, you would of course put the
name of _your_ stylesheet instead of 'myspecial.css'.

####[#page_title] Title ####[&uarr;](#top)
`title` is a reserved key word in the context of `program`: `program.title`

Though it is _not_ reserved in any other context, by convention it should be used to set the page's title: 
`page.title`. It is used in this manner in the default.html template, and it is suggested that it not be used
for any other purpose.

If you have a look at that file, you will see how these parameters can be used.  Further, you will see that the
`page.title` parameter is used inside `<title>\${page.title}</title>` tags.  It is highly recommended that every 
web page has a title tag.

According to [w3schools.com][w3s]!:

"_The `<title>` tag is required in all HTML documents and it defines the title of the document_".

Further, 

"_The `<title>` element:_

- _defines a title in the browser toolbar_
- _provides a title for the page when it is added to favorites_
- _displays a title for the page in search-engine results_"

####[#template] Template ####[&uarr;](#top)
`template` is a reserved key word in all contexts.

It must only refer to an actual `.html` file.  This file must be located in the
directory referred to by the `templatesDir` key's value.  The initial entry being:
`templatesDir = templates`.

####[#stylesheet] Stylesheet ####[&uarr;](#top)
`stylesheet` is a reserved key word in all contexts.

It must only refer to an actual `.css` file.  This file must be located in the
directory referred to by the `cssDir` key's value.  The initial entry being:
`cssDir = css`.

####[#page_text] Text ####[&uarr;](#top)
`text` is a reserved key word in the context of `page`: `page.text`.

It contains the unprocessed markdown text of the body of the document.




[Home]:index.html
[w3s]:https://www.w3schools.com/tags/tag_title.asp
