---
layout: jclouds
title: Contributing to jclouds Documentation
---

# Contributing to jclouds Documentation

### Introduction

The complete jclouds website and documentation are now served from Github, and the code is available at the [jclouds git repository](https://github.com/jclouds/jclouds.github.com).

This site is rebuilt and updated whenever a new commit is pushed to the repository. This makes it extremely easy for us to accept your contributions to the 
documentation. 

### How to Contribute

To revise or contribute to the documentation:

- Just fork, clone the repository, and update the source markdown [If it is easier, you can even use textile. See more details on this below].
- Commit and push to your fork.
- Send in a pull-request so a core contributor may pull your changes and push them to the site.
- We are currently undergoing site renovations.  When sending in your pull-requests, only 1 committer is pulling requests right now,
so it may take a day or two.  If it takes more than two days to pull your changes, let us know in irc at 
[freenode.net #jclouds](http://webchat.freenode.net/?channels=#jclouds).


### More about the site: 

The jclouds site is built using [Github pages](http://pages.github.com/) which uses [Jekyll](https://github.com/mojombo/jekyll/), a static site 
generator written in Ruby, under the covers. To preview any changes you make, install [Jekyll](https://github.com/mojombo/jekyll/wiki/install) 
then run `jekyll --safe --server` and open [localhost:4000](http://localhost:4000) in the browser.

The site structure is pretty simple as you can see in the [source osf this site](https://github.com/jclouds/jclouds.github.com).

All of the jclouds documentation utilizes markdown.  This includes the main menu and sidebar.

The *_layouts* folder contains the jclouds.html file which is used as the layout - containing main column and the sidebar. 
The sidebar is an *include* which is inside the *_includes folder*.  In general, this file shouldn't be changed.

Each folder under the documentation contains an *index.md* or *index.markdown* that will list all of the pages that belong in that section. You can even edit 
the pages directly on github if you don't want to clone the repo into your computer.

Please take a look at the site [jclouds.org](http://www.jclouds.org/) and let us know if we are missing anything in
the documentation.  Feel free to fork/contribute or open a bug on the [issue tracker](https://github.com/jclouds/jclouds.github.com/issues).
We'll be glad to fix it ASAP.


