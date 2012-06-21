---
layout: jclouds
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

As indicated above, the site is built using [github pages](http://pages.github.com/) which uses [Jekyll](https://github.com/mojombo/jekyll/), a static site generator written in Ruby, under the covers. To preview any changes you make, install Jekyll (from [here](https://github.com/mojombo/jekyll/wiki/install), with Pygments too) then run `jekyll -pygments --safe --server` and open [localhost:4000](http://localhost:4000) in the browser.

The site structure is pretty simple as you can see in the [source of this site](https://github.com/jclouds/jclouds.github.com).
The *_layouts* folder contains the docs.html file which is used as the layout - containing main column and the sidebar. 
The sidebar is an *include* which is inside the *_includes folder*. 

(Due a bug the sidebar can't be a markdown file, but this will be changed as soon as the bug is fixed. Also when running in the jekyll server, links without the .html are not resolved, and you'll have to manually append `.html` to some links; this runs fine on the server, so don't worry about it; however if you know a better way please let us know!)

Each folder under the documentation contains an *index.markdown* that will list all the pages that belong to 
this "section". You can even edit the pages directly on github if you don't want to 
clone the repo into your computer.

Please take a look at the site [jclouds.org](http://www.jclouds.org/) and let us know if we missed anything from 
the documentation, feel free to fork/contribute or open a bug on the [issue tracker](https://github.com/jclouds/jclouds.github.com/issues)
We'll be glad to fix them ASAP.


