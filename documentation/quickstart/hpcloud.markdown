---
layout: docs
title: Quick Start - HP Cloud Services
---

# Quick Start: HP Cloud Services

## HP Cloud Object Storage

1. Sign up for [HP Cloud Services](http://hpcloud.com/).
2. Get your Account ID and Access Key by going to this [page](https://manage.hpcloud.com/api_keys).
3. Ensure you are using a recent JDK 6 version. 
4. Setup your project to include `hpcloud-object-storage`
   The maven dependency `org.jclouds.provider/hpcloud-object-storage` will add the following jar files to your project:
  	* jclouds-core
  	* jclouds-blobstore
  	* openstack-common
  	* swift

Instructions for downloading the dependencies are available in the [Installation](/documentation/userguide/installation-guide).

5. Start coding using HP Cloud Object Storage!


{% highlight java %}
// get a context with hpcloud that offers the portable BlobStore api
BlobStoreContext context = new BlobStoreContextFactory().createContext("hpcloud-object-storage", user, password);

// create a container in the default location
context.getBlobStore().createContainerInLocation(null, container);

// use the map interface for easy access to put/get things, keySet, etc.
context.createInputStreamMap(container).put("blob.txt", inputStream);

// when you need access to hpcloud specific features, use the provider-specific context
HPCloudObjectStorageClient hpcloudClient = HPCloudObjectStorageClient.class.cast(context.getProviderSpecificContext()
         .getApi());

// get a cdn uri for the container
URI cdnURI = hpcloudClient.enableCDN(container);

context.close();
{% endhighlight %}


<!-- HP Cloud Compute -->