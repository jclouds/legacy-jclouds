---
layout: jclouds
title: jclouds Home
---

WHAT IS JCLOUDS? 
===============

Cloud Interfaces, Simplified.

jclouds is an open source library that helps you get started in the cloud and utilizes your [Java](http://www.oracle.com/technetwork/java/index.html) 
or [Clojure](http://clojure.org) development skills. The jclouds API gives you the freedom to use portable abstractions or cloud-specific features. 

jclouds tests support of 30 cloud providers and cloud software stacks including Amazon, GoGrid, Ninefold, vCloud, OpenStack, and Azure.  Please see the
complete list of [jclouds supported providers](http://www.jclouds.org/documentation/reference/supported-providers/) that are promoted by the jclouds API.

jclouds offers several API abstractions as Java and Clojure libraries. The most mature of these are BlobStore and ComputeService.

## [BLOBSTORE](http://www.jclouds.org/documentation/userguide/blobstore-guide/)
BlobStore is a simplified and portable means of managing your key-value storage providers.  BlobStore presents you with a straightforward 
Map view of a container to access your data.

## [COMPUTESERVICE](http://www.jclouds.org/documentation/userguide/compute/)
ComputeService streamlines the task of managing instances in the cloud by enabling you to start multiple machines at once and install software on them.


# WHY SHOULD I USE JCLOUDS?
Programming against cloud environments can be challenging. jclouds focuses on the following areas so that you can get started in the cloud sooner.

### SIMPLE INTERFACE
Get started without dealing with REST-like APIs or WS.  Instead of creating new object types, jclouds reuses concepts like maps so that the programming 
model is familiar. 

### RUNTIME PORTABILITY
jclouds drivers enable you to operate in restricted environments like Google App Engine. There are very few required dependencies, so jclouds is unlikely 
to clash with your app.

### DEALS WITH WEB COMPLEXITY
jclouds handles issues such as transient failures and redirects that traditional network based computing introduces.

### UNIT TESTABILITY
Write your unit tests without mocking complexity or the brittleness of remote connections.  Writing tests for cloud endpoints is difficult. jclouds provides 
you with Stub connections that simulate a cloud without creating network connections. 

### PERFORMANCE
Customize configuration to match your performance needs.  jclouds provides you with asynchronous commands and tunable http, date, encryption, and 
encryption modules.

### LOCATION
jclouds provides location-aware abstractions. For example, you can get ISO-3166 codes to tell which country or province a cloud runs in.

### QUALITY 
Every provider is tested with live scenarios before each release. If the provider doesn't pass, it goes back into the sandbox.