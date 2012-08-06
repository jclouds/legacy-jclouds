---
layout: jclouds
title: Contributing to jclouds Documentation
---

# Contributing to jclouds Documentation

## Introduction

Welcome and thank you for your interest in contributing to the jclouds documentation!  This guide will give you a bit of information about jclouds.org, 
walk you through the process of forking the website, and show you how to commit your changes to the documentation so that we may pull them into the 
jclouds master. 

Please see [Contributing to the jclouds Code Base](http://www.jclouds.org/documentation/devguides/contributing-to-jclouds/) if you are interested in 
contributing code to jclouds.

## Information about jclouds.org for Getting Started on Documentation

The jclouds website is built using [Github pages](http://pages.github.com/) which uses [Jekyll](https://github.com/mojombo/jekyll/). To preview any changes 
you make, install [Jekyll](https://github.com/mojombo/jekyll/wiki/install) then run `jekyll --auto --server` and open [localhost:4000](http://localhost:4000) 
in the browser.  Running `--auto` will automatically regenerate the site when you make changes.

We use markdown.  This includes the main menu and sidebar.

Each folder under the documentation contains an *index.md* or *index.markdown* that will list all of the pages that belong in that section. You can even edit 
the pages directly on github if you don't want to clone the repo into your computer.

Please be aware that making changes to the jclouds.html file under the *_layouts* folder requires extensive reviews and may take quite a while.  Editing 
our layouts requires considerable knowledge of CSS and html.  In general, it is a good idea to stick to content of the main column and sidebar rather 
than the layout.

jclouds currently has a technical writer dedicated to documentation on a full time basis.  If you should come across a documentation issue while browsing 
[jclouds.org](http://www.jclouds.org/), and you would feel more comfortable having someone else work on the issue, feel free to log an issue on the
[issue tracker](http://code.google.com/p/jclouds/issues/list?can=2&q=Component%3DDocs).  Our technical writer keeps track of all documentation issues that are logged and will 
respond to them quickly.

## Working with jclouds on GitHub

jclouds.org currently resides on GitHub, and the documentation is available at the [jclouds Git repository](https://github.com/jclouds/jclouds.github.com).

In order to contribute to the jclouds documentation, you will need to be familiar with Git and GitHub.  If you are not familiar with Git and GitHub, please 
visit [learn.github](http://learn.github.com/p/intro.html) for an introduction and tutorial.

Git is an open source, distributed version control system.  It conveniently keeps a history on the state of the jclouds source code. The jclouds website is 
rebuilt and updated whenever a new commit is pushed to the jclouds Git repository. This makes it simple for us to review and accept your contributions. 

The following sections explain how to access the jclouds documentation, make changes to it, and send us a request to pull your changes into the jclouds
master.

## Creating and Working with your Fork of the jclouds Documentation

### 1.  Setup your Personal Fork

   *  [Download Git](http://git-scm.com/download)  (Alternatively, Mac users may consider [mac.github.com](http://mac.github.com/))
   *  Go to [GitHub](http://github.com) and fork [jclouds/jclouds.github.com](https://github.com/jclouds/jclouds.github.com)

### 2.  Create your Local Repository

   *  Follow the excellent instructions in the learn.github guide to [clone a repository](http://learn.github.com/p/setup.html#cloning_a_git_repo)

### 3.  Pull Changes to your Fork

   *  Run `git remote add jclouds git://github.com/jclouds/jclouds.github.com.git`
   *  Run `git pull --rebase jclouds master` [(Why rebase?)](http://stackoverflow.com/questions/5968964/avoid-unwanted-merge-commits-and-other-commits-when-doing-pull-request-in-github)
   *  Run `git push` (*Note - This will push ALL committed changes to your fork.  If you have committed changes to your local repository that are not yet in
      GitHub, those changes will be pushed in addition to the commits from the main jclouds.github.com repository.)

## Contributing your Changes back to the jclouds Documentation

### 1. Commit your Changes

   *  Make sure that the documentation looks correct on your [local server](http://localhost:4000) before doing the next steps.

### 2.  Update your Current Repository with jclouds Master

   *  Run `git remote add jclouds git://github.com/jclouds/jclouds.github.com.git`
   *  Run `git pull --rebase jclouds master` [(Why rebase?)](http://stackoverflow.com/questions/5968964/avoid-unwanted-merge-commits-and-other-commits-when-doing-pull-request-in-github)
   *  Run `git push` (*Note - This will push ALL committed changes to your fork.  If you have committed changes to your local repository that are not yet in
      GitHub, those changes will be pushed in addition to the commits from the main jclouds.github.com repository.)

### 3.  Create a Pull Request

   *  Go to your fork of jclouds.github.com in GitHub
   *  Click on "Pull Request" in the upper right of the page
   *  Please add a detailed description of your commit.  If you do not do this, it may take some time before we pull your commit into the jclouds master
      while we are attempting to decipher the intent behind your commit.  
   *  Please also reference any issues that your commit may resolve. 



