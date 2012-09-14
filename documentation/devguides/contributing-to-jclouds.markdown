---
layout: jclouds
title: Contributing to the jclouds Code Base
---

# Contributing to the jclouds Code Base

## Introduction

Welcome and thank you for your interest in contributing to jclouds!  This guide will take you through the process of making contributions to the jclouds 
code base.  It will begin by walking you through how to commit changes to the jclouds repository on GitHub and then move on to more advanced development 
processes.

If you are interested in helping us out with the jclouds documentation, please see our instructions for [contributing to jclouds documentation](http://www.jclouds.org/documentation/devguides/contributing-to-documentation/). 

## Working with jclouds on GitHub

The jclouds website currently resides on GitHub, and the source code is available at the [jclouds Git repository](https://github.com/jclouds/jclouds.github.com).

In order to contribute to the jclouds code base, you will need to be familiar with Git and GitHub.  If you are not familiar with Git and GitHub, please 
visit [learn.github](http://learn.github.com/p/intro.html) for an introduction and tutorial.

Git is an open source, distributed version control system.  It conveniently keeps a history on the state of the jclouds source code. The jclouds website is 
rebuilt and updated whenever a new commit is pushed to the jclouds Git repository. This makes it simple for us to review and accept your contributions. 

The following sections explain how to access the jclouds code base, make changes to it, and send us a request to pull your changes into the jclouds
master.

## Creating and Working with your Fork of jclouds

### 1.  Setup your Personal Fork

   *  [Download Git](http://git-scm.com/download)  (Alternatively, Mac users may consider [mac.github.com](http://mac.github.com/))
   *  Go to [GitHub](http://github.com) and fork [jclouds/jclouds](http://github.com/jclouds/jclouds)

### 2.  Create your Local Repository

   *  Follow the excellent instructions in the learn.github guide to [clone a repository](http://learn.github.com/p/setup.html#cloning_a_git_repo)

### 3.  Pull Changes to your Fork

   *  Run `git remote add jclouds git://github.com/jclouds/jclouds.git`
   *  Run `git pull --rebase jclouds master` [(Why rebase?)](http://stackoverflow.com/questions/5968964/avoid-unwanted-merge-commits-and-other-commits-when-doing-pull-request-in-github)
   *  Run `git push` (*Note - This will push ALL committed changes to your fork.  If you have committed changes to your local repository that are not yet in
      GitHub, those changes will be pushed in addition to the commits from the main jclouds repository.)

### 4.  Run Unit Tests

   *  Run `mvn clean test`

## Contributing your Changes back to jclouds

### 1. Commit your Changes

   *  Make sure your commit includes tests that cover the code change.
   *  Make sure everything compiles and all of the tests are successfully running for each commit.

### 2.  Update your Current Repository with jclouds Master

   *  Run `git remote add jclouds git://github.com/jclouds/jclouds.git`
   *  Run `git pull --rebase jclouds master` [(Why rebase?)](http://stackoverflow.com/questions/5968964/avoid-unwanted-merge-commits-and-other-commits-when-doing-pull-request-in-github)
   *  Run `git push` (*Note - This will push ALL committed changes to your fork.  If you have committed changes to your local repository that are not yet in
      GitHub, those changes will be pushed in addition to the commits from the main jclouds repository.)

### 3.  Run Unit Tests

   *  Run `mvn clean test`

### 4.  Create a Pull Request

   *  Go to your fork of jclouds in GitHub
   *  Click on "Pull Request" in the upper right of the page
   *  Please add a detailed description of your commit.  If you do not do this, it may take some time before we pull your commit into the jclouds master
      while we are attempting to decipher the intent behind your commit.  
   *  Please also reference any issues that your commit may resolve. 

