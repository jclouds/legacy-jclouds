---
layout: jclouds
title: jclouds Continuous Integration
---

# jclouds Automated testing + snapshots repository.

## Introduction

[CloudBees](http://www.cloudbees.com) is kindly supporting jclouds by providing free access to their hosted [DEV@cloud](http://www.cloudbees.com/dev.cb) continuous integration service. jclouds' [Jenkins image there](http://jclouds.ci.cloudbees.com) is set up to build all active jclouds branches under Java 6 and 7, as well as a number of other projects.

From the main trunk build, snapshot artifacts are published to [Sonatype's OSS Nexus](https://oss.sonatype.org/) and are available from its [snapshot repository](https://oss.sonatype.org/content/repositories/snapshots/) (under [org/jclouds](https://oss.sonatype.org/content/repositories/snapshots/org/jclouds/)).

## Jenkins

If you think you might need access to jclouds' Jenkins, or would like a job set up there to build your jclouds-related project, please post a request to the [jclouds-dev mailing list](http://groups.google.com/group/jclouds-dev).
