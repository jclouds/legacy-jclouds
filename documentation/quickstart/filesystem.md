---
layout: jclouds
title: Quick Start - File System
---

# Quick Start: File System

1. Setup your project to include filesystem
	* Get the dependency `org.jclouds.api/filesystem` using jclouds [Installation](/documentation/userguide/installation-guide).
2. Start coding

{% highlight java %}
// setup where the provider must store the files
Properties properties = new Properties();
properties.setProperty(FilesystemConstants.PROPERTY_BASEDIR, "./local/filesystemstorage");
// setup the container name used by the provider (like bucket in S3)
String containerName = "test-container";

// get a context with filesystem that offers the portable BlobStore api
BlobStoreContext context = new BlobStoreContextFactory().createContext("filesystem", properties);

// create a container in the default location
BlobStore blobStore = context.getBlobStore();
blobStore.createContainerInLocation(null, containerName);

// add blob
Blob blob = blobStore.newBlob("test");
blob.setPayload("test data");
blobStore.putBlob(containerName, blob);

// retrieve blob
Blob blobRetrieved = blobStore.getBlob(containerName, "test");

// delete blob
blobStore.removeBlob(containerName, "test");

//close context
context.close();
{% endhighlight %}

### Tips & tricks

  * Container is an additional subfolder appended to FilesystemConstants.PROPERTY_BASEDIR. 
	All blobs are stored inside this folder. So its value follows common rules for allowed directory names.
  *  Blob key represent the name of the file stored in the filesystem. It can contains path separator. 
	In this case, the entire directory structure is generated, starting from the container path.
	 Also in this case, common rules for allowed file names must be followed.
