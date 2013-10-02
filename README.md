jclouds
======
jclouds allows provisioning and control of cloud resources, including blobstore
and compute, from Java and Clojure.  Our API gives allows developers to use
both portable abstractions and cloud-specific features.  We test support of dozens of
cloud providers and cloud software stacks, including Amazon, Azure, GoGrid,
Ninefold, OpenStack, and vCloud.  jclouds is licensed under the Apache License,
Version 2.0

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


BlobStore
-----------
Simplifies dealing with key-value providers such as Amazon S3. For example, BlobStore can give you a simple Map view of a container.

BlobStore Example (Java):

```java
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
```

BlobStore Example (Clojure):

```clojure
(use 'org.jclouds.blobstore2)
(def ^:dynamic *blobstore* (blobstore "azureblob" account encodedkey))
(create-container *blobstore* "mycontainer")
(put-blob *blobstore* "mycontainer" (blob "test" :payload "testdata"))
```

ComputeService
---------------
Simplifies the task of managing machines in the cloud. For example, you can use ComputeService to start 5 machines and install your software on them.

Compute Example (Java):

```java
// init
context = new ComputeServiceContextFactory().createContext(
"aws-ec2",
accesskeyid,
secretaccesskey,
ImmutableSet.of(new Log4JLoggingModule(), new SshjSshClientModule()));

client = context.getComputeService();

// define the requirements of your node
template = client.templateBuilder().osFamily(UBUNTU).smallest().build();

// setup a boot user which is the same as your login
template.getOptions().runScript(AdminAccess.standard());

// these nodes will be accessible via ssh when the call returns
nodes = client.createNodesInGroup("mycluster", 2, template);

// you can now run ad-hoc commands on the nodes based on predicates
responses = client.runScriptOnNodesMatching(inGroup("mycluster"), "uptime", wrapInInitScript(false));
```

Compute Example (Clojure):

```clojure
(use 'org.jclouds.compute2)

; create a compute service using sshj and log4j extensions
(def compute (*compute* "trmk`-ecloud" "user" "password" :sshj :log4j))

; launch a couple nodes with the default operating system, installing your user.
(create-nodes *compute* "mycluster" 2
(TemplateOptions$Builder/runScript (AdminAccess/standard)))

; run a command on that group 
(run-script-on-nodes-matching *compute* (in-group? "mycluster") "uptime" 
(RunScriptOptions$Builder/wrapInInitScript false))
```

Check out https://github.com/jclouds/jclouds-examples for more examples!

Downloads
------------------------
* release notes: http://www.jclouds.org/documentation/releasenotes/1.5
* installation guide: http://www.jclouds.org/documentation/userguide/installation-guide
* maven repo: http://repo2.maven.org/maven2 (maven central - the default repository)
* snapshot repo: https://oss.sonatype.org/content/repositories/snapshots
 
Resources
----------------------------
* Project page: http://jclouds.org/
* Documentation: http://www.jclouds.org/documentation/index
* Community: http://www.jclouds.org/documentation/reference/apps-that-use-jclouds 
* User group: http://groups.google.com/group/jclouds
* Dev group: http://groups.google.com/group/jclouds-dev
* Twitter: http://twitter.com/jclouds


License
-------
Copyright (C) 2009-2013 The Apache Software Foundation

Licensed under the Apache License, Version 2.0
