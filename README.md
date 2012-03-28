jclouds
======
jclouds is an open source library that helps you get started in the cloud and reuse your java and clojure development skills. Our api allows you freedom to use portable abstractions or cloud-specific features. We test support of 30 cloud providers and cloud software stacks, including Amazon, GoGrid, Ninefold, vCloud, OpenStack, and Azure.
We offer several API abstractions as java and clojure libraries. The following are the most mature:

BLOBSTORE
-----------
Simplifies dealing with key-value providers such as Amazon S3. For example, BlobStore can give you a simple Map view of a container.

BlobStore Example (Java):
	  // init
	  context = new BlobStoreContextFactory().createContext(
	                  "aws-s3",
	                  accesskeyid,
	                  secretaccesskey);
	  blobStore = context.getBlobStore();
 
	  // create container
	  blobStore.createContainerInLocation(null, "mycontainer");
  
	  // add blob
	  blob = blobStore.blobBuilder("test").payload("testdata").build();
	  blobStore.putBlob("mycontainer", blob);

BlobStore Example (Clojure):
	  (use 'org.jclouds.blobstore2)
	  (def *blobstore* (blobstore "azureblob" account encodedkey))
	  (create-container *blobstore* "mycontainer")
	  (put-blob *blobstore* "mycontainer" (blob "test" :payload "testdata"))

COMPUTESERVICE
---------------
Simplifies the task of managing machines in the cloud. For example, you can use ComputeService to start 5 machines and install your software on them.

Features
--------
Even if you don't need the portable apis we provide, or could roll it your own, programming against cloud environments can be challenging. We focus on the following areas so that you can focus on using the cloud, rather than troubleshooting it!

* SIMPLE INTERFACE
Instead of creating new object types, we reuse concepts like maps so that the programming model is familiar. In this way, you can get started without dealing with REST-like apis or WS.

* RUNTIME PORTABILITY
We have drivers that allow you to operate in restricted environments like Google App Engine. We have very few required dependencies, so we are unlikely to clash with your app.

* DEAL WITH WEB COMPLEXITY
Network based computing introduces issues such as transient failures and redirects.
We handle this for you.

* UNIT TESTABILITY
Writing tests for cloud endpoints is difficult. We provide you with Stub connections that simulate a cloud without creating network connections. In this way, you can write your unit tests without mocking complexity or the brittleness of remote connections.

* PERFORMANCE
Writing tests for cloud endpoints is difficult. We provide you with Stub connections that simulate a cloud without creating network connections. In this way, you can write your unit tests without mocking complexity or the brittleness of remote connections.

* LOCATION 
All of our abstractions are location-aware. For example, you can get ISO-3166 codes to tell which country or province a cloud runs in.

* QUALITY 
We test every provider with live scenarios before each release. If it doesn't pass, the provider goes into the sandbox.

Downloads
------------------------
* release notes: http://www.jclouds.org/documentation/releasenotes/1.3
* installation guide: http://www.jclouds.org/documentation/userguide/installation-guide
* maven repo: http://repo2.maven.org/maven2 (maven central - the default repository)
* snapshot repo: https://oss.sonatype.org/content/repositories/snapshots
 
Resources
----------------------------
* Project page: http://jclouds.org/
* Documentation: http://www.jclouds.org/documentation/index
* Javadocs (1.1.0): http://jclouds.rimuhosting.com/apidocs/
* Javadocs (1.0-SNAPSHOT): http://jclouds.rimuhosting.com/apidocs-SNAPSHOT/
* Community: http://www.jclouds.org/documentation/reference/apps-that-use-jclouds 
* User group: http://groups.google.com/group/jclouds
* Dev group: http://groups.google.com/group/jclouds-dev
* Twitter: http://twitter.com/jclouds
  
Configuring
-----------

Uptime uses [node-config](https://github.com/lorenwest/node-config) to allow YAML configuration and environment support. Here is the default configuration, taken from `config/default.yaml`:

    mongodb:
      server:   localhost
      database: uptime
      user:     root 
      password:
    
    monitor:
      name:                   origin
      apiUrl:                 'http://localhost:8082/api'
      pollingInterval:        10000      # ten seconds
      updateInterval:         60000      # one minute
      qosAggregationInterval: 600000     # ten minutes
      timeout:                5000       # five seconds
      pingHistory:            8035200000 # three months
      http_proxy:      
    
    autoStartMonitor: true
    
    server:
      port:     8082

To modify this configuration, create a `development.yaml` or a `production.yaml` file in the same directory, and override just the settings you need. For instance, to run Uptime on port 80 in production, create a `production.yaml` file as follows:

    server:
      port:     80

Using jclouds
-------------
check out our examples site! https://github.com/jclouds/jclouds-examples

License
-------

Copyright (C) 2009-2012 jclouds, Inc.

Licensed under the Apache License, Version 2.0