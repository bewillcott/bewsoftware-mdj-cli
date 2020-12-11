@@@
use:articles2
title: ${program.title} | Named Meta Blocks
@@@

[Home]

---

${page.myTOC}

---

## Named Meta Blocks

A named meta block is a block of fenced text, that can be located anywhere within 
a document, that is used during the substitution phase.  It would be cut from the
document and then inserted where designated by its name.

A named meta block begins with a fence of three at signs followed by a bracketed 
option containing a name that is unique within the `page` context: `@@@[myTOC]`, 
and ends with a fence of three more at signs: `@@@`.  In both cases, they must be 
at the beginning of an otherwise empty line.

Unlike [Meta Blocks][mb], Named Meta Blocks don't contain key/value pairs.  All the
text between the two fence lines is substituted where ever its name is used: 
`\${page.myTOC}`.

This is a copy of the text near the top of the source file for this page:
~~~
---

\${page.myTOC}

---

## Named Meta Blocks
~~~

The following would produce the TOC at the top of the page:

~~~
\@@@[myTOC]
### Table of Contents

- My First Heading
    - Number Two Heading
    - And Another One

\@@@
~~~

@@@[myTOC]
#### Table of Contents

- My First Heading
    - Number Two Heading
    - And Another One

@@@

__Note:__ The naming of this _Named Meta Block_ was arbitrarily set to: `myTOC`.
It could have been any valid text e.g.: `Junk_Mail`.  Valid characters are the set:
`[a-zA-Z_0-9]`.

Further, the substitution could just as easily have been made into the template used for 
this page.


[mb]:Meta Blocks.html
