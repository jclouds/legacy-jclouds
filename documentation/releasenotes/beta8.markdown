---
layout: jclouds
title: Release Notes Beta 8
---

# Release Notes: jclouds Beta 8


## Introduction

We resolved [23 issues](http://code.google.com/p/jclouds/issues/list?can=1&q=label%3AMilestone-1.0-beta-8)
 in our jclouds 1.0-beta-8 release.  While mostly a bugfix release we have a few notable features to discuss.

## New Features
  * Tested support for a [peer1-storage](http://www.peer1.com/hosting/cloudone-storage.php)
  * jclouds can now persist credentials of started nodes across contexts, which allows you
 to run scripts without ever knowing the login credentials of nodes.
  * Integration tests are stronger including os version match tests and running web services on launched nodes
  * Stabalized terremark ecloud support

## API Breaking Changes 
  * `EC2Hardware object` has been removed in favor of the base `Hardware` class.
  * `ScriptBuilder.build` is now `ScriptBuilder.render` so that it can implement the `Statement` interface.

## Known Issues
  * trmk-ecloud
    * only the centos template work in ComputeService
  * slicehost
    * note that soft followed by hard reboot ends often throws a server error saying wait.
  * bluelock-vcdirector/vcloud
    * you have to copy vAppTemplates into your catalog from the public one before they can be used.
    * ComputeService has no hooks for changing the hardware yet
  * eucalyptus tests skipped due to no resources on the test cloud
  * the following providers are unstable
    * rimuhosting
