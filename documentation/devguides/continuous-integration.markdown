---
layout: docs
title: JClouds Automated testing
---

# JClouds Automated testing + snapshots repository.

## Introduction

There is an instance of Hudson monitoring SVN commits every 15 minutes. 
When a commit is done, it builds the code, runs the tests, and 
deploys new snapshot jars to the Apache Archiva server.

## Hudson

Hudson can be accessed from http://jclouds.rimuhosting.com/hudson. Anyone should be able to see the build status. 
If you think you need more access (i.e. to run builds manually), please ask Adrian or Ivan. 

Once every 15minutes, if SVN has been committed to, Hudson builds the project, and 
as part of the build, deploys to Apache Archiva.

### Adding a user to manage the jclouds build

  * manage-hudson > Manage users
  * look at there user id in the url of the person
  * back to manage hudson > configure system
  * then add the user with the right access

## Nexus 

Installation of Nexus is on jclouds.rimuhosting.com.

Nexus is set up to purge all snapshots older than 90 days every night (keeping a minimum of 10 builds), 
but we can of course modify that too.

Nexus details:

  * UI: [[http://jclouds.rimuhosting.com:8081/nexus/]]
  * Snapshot repo: [[https://oss.sonatype.org/content/repositories/snapshots]]

### Starting nexus 
  * `ssh root@jclouds.rimuhosting.com`
  * `su - nexus`
  * `./nexus start`

