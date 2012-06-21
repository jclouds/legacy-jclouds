---
layout: jclouds
title: jclouds Home
---

# jclouds Overview
jclouds is an open source library that helps you get started in the cloud and reuse your java and clojure development skills. Our api allows you freedom to use portable abstractions or cloud-specific features. We test support of 30 cloud providers and cloud software stacks, including Amazon, GoGrid, Ninefold, vCloud, OpenStack, and Azure.
We offer several API abstractions as java and clojure libraries. The following are the most mature:

### BLOBSTORE
Simplifies dealing with key-value providers such as Amazon S3. For example, BlobStore can give you a simple Map view of a container.

### COMPUTESERVICE
Simplifies the task of managing machines in the cloud. For example, you can use ComputeService to start 5 machines and install your software on them.

# WHY SHOULD I USE JCLOUDS?

Even if you don't need the portable apis we provide, or could roll it your own, programming against cloud environments can be challenging. We focus on the following areas so that you can focus on using the cloud, rather than troubleshooting it!

### SIMPLE INTERFACE
Instead of creating new object types, we reuse concepts like maps so that the programming model is familiar. In this way, you can get started without dealing with REST-like apis or WS.

### RUNTIME PORTABILITY
We have drivers that allow you to operate in restricted environments like Google App Engine. We have very few required dependencies, so we are unlikely to clash with your app.

### DEAL WITH WEB COMPLEXITY
Network based computing introduces issues such as transient failures and redirects.
We handle this for you.

### UNIT TESTABILITY
Writing tests for cloud endpoints is difficult. We provide you with Stub connections that simulate a cloud without creating network connections. In this way, you can write your unit tests without mocking complexity or the brittleness of remote connections.

### PERFORMANCE
Writing tests for cloud endpoints is difficult. We provide you with Stub connections that simulate a cloud without creating network connections. In this way, you can write your unit tests without mocking complexity or the brittleness of remote connections.

### LOCATION 
All of our abstractions are location-aware. For example, you can get ISO-3166 codes to tell which country or province a cloud runs in.

### QUALITY 
We test every provider with live scenarios before each release. If it doesn't pass, the provider goes into the sandbox.
