---
layout: docs
title: Contributing to jclouds Documentation
---

# How to contribute to jClouds Documentation

The complete jclouds website (and documentation) is now served from github, and the code for the same is available the [git repository](https://github.com/jclouds/jclouds.github.com)  

This site is rebuilt and updated whenever a new commit is pushed to the repository. 

This makes it extremely easy for accepting the contributions to the documentation as well using 
the standard github fork/commit/pull-req based workflow. 

To change or contribute to the documentation:

- Just fork, clone the repository & Update the source markdown [you can even use textile, if it is easier, more details on this below].
- Commit and push to your fork.
- Send in a pull-req so a core contributor will pull the changes and push them to the site.


More about the site: 

As indicated above, the site is built using [github pages](http://pages.github.com/) - which is built using Jekyll[2], 
a static site generator written ruby. Jekyll. 

The site structure is pretty simple as you can see in the [source of this site](https://github.com/jclouds/jclouds.github.com).
The *_layouts* folder contains the docs.html file which is used as the layout - containing main column and the sidebar. 
The sidebar is an *include* which is inside the *_includes folder*. 

`(Due a bug the sidebar can't be a markdown file, but this will be changed as soon as the bug is fixed).`

Each folder under the documentation contains an *index.markdown* that will list all the pages that belong to 
this "section". You can even edit the pages directly on github if you don't want to 
clone the repo into your computer.

Please take a look at the site [jclouds.org](http://www.jclouds.org/) and let us know if we missed anything from 
the documentation, feel free to fork/contribute or open a bug on the [issue tracker](https://github.com/jclouds/jclouds.github.com/issues)
We'll be glad to fix them ASAP.


