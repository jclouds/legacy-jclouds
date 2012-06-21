---
layout: jclouds
title: jclouds - Error Handling
---

# Introduction

Initial rudimentary content on error handling.


## `RunNodesException` 

`RunNodesException` indicates that something went wrong when creating nodes. However, 
because some other things might also have gone right, care needs to be taken to recover correctly. 
For example, when creating 10 nodes, some might have started up fine, and others might have failed.

Method `getSuccessfulNodes()` returns the set of nodes that started up correctly.

Method `getNodeErrors()` returns the unsuccessful Nodes, mapped to the problem that occurred. 
Note that "unsuccessful" does not mean the Node did not start up. It more likely means that a script did not run 
successfully after the node booted.

To undo the entire operation, iterate over the content of `getSuccessfulNodes()` and of `getNodeErrors().keySet()`.
