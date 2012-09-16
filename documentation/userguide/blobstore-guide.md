---
layout: jclouds
title: BlobStore Guide
---
# BlobStore Guide

The BlobStore API is a portable means of managing key-value storage providers such as Microsoft Azure Blob Service
and Amazon S3. It offers both asynchronous and synchronous apis, as well as Map-based access to your data.
Our APIs are dramatically simplified from the providers, yet still offer enough sophistication to perform 
most work in a portable manner.
We also have integrations underway for popular tools such as Apache commons VFS.   

Like other components in `jclouds`, you always have means to gain access to the provider-specific interface 
if you need functionality that is not available in our abstraction.  


## Features 
### Location Aware API

Our location API helps you to portably identify a container within context, such as Americas or Europe.

We use the same model across the [ComputeGuide](/documentation/userguide/compute) which allows you to facilitate collocation of processing and data.

### Asynchronous API

You have a choice of using either synchronous or asynchronous BlobStore API. If you choose to use the Asynchronous API, 
you'll benefit by 
gaining access to the most efficient means to achieve this, regardless of whether it is via threads, non-blocking io, 
or native async clients such as google appengine's async url fetch service.

### Integration with non-java clients 

Using our `BlobRequestSigner`, you can portably generate HTTP requests that can be passed to external systems 
for execution or processing.
Use cases include javascript side-loading and curl-based processing on the bash prompt.  Be creative!

### Transient Provider

Our in-memory BlobStore allows you to test your storage code without credentials or a credit card!

### Filesystem Provider

Our file system BlobStore allows you to use the same API when persisting to disk, memory, or a remote BlobStore like Amazon S3.

## Supported Providers 

All of the following providers can be used equally in any BlobStore API tool.

| *provider* 					| *artifact* 										| 	*version*	|
|-------------------------------|---------------------------------------------------|---------------|
| transient 		    		| jclouds-blobstore 								| 1.0.0 		|
| filesystem 					| jclouds-filesystem 								| 1.0.0 		|
| eucalyptus-partnercloud-s3 	| org.jclouds.provider/eucalyptus-partnercloud-s3 	| 1.0.0 		|
| synaptic-storage				| org.jclouds.provider/synaptic-storage 			| 1.0.0 		|
| azureblob 					| org.jclouds.provider/azureblob 					| 1.0.0 		|
| cloudonestorage 				| org.jclouds.provider/cloudonestorage 				| 1.0.0 		|
| cloudfiles-us 				| org.jclouds.provider/cloudfiles-us 				| 1.0.0 		|
| cloudfiles-uk 				| org.jclouds.provider/cloudfiles-uk 				| 1.0.0 		|
| ninefold-storage 				| org.jclouds.provider/ninefold-storage 			| 1.0.0 		|
| aws-s3 						| org.jclouds.provider/aws-s3 						| 1.0.0 		|
| googlestorage 				| org.jclouds.provider/googlestorage 				| 1.0-SNAPSHOT 	|
| scaleup-storage 				| org.jclouds.provider/scaleup-storage 				| 1.0-SNAPSHOT 	|
| hosteurope-storage 			| org.jclouds.provider/scaleup-storage 				| 1.0-SNAPSHOT 	|
| tiscali-storage 				| org.jclouds.provider/scaleup-storage 				| 1.0-SNAPSHOT 	|

You can also set the context property `provider`.endpoint to use the following APIs for your private cloud                                                                                

| api  			|                                                                                                                                                                               
|---------------|
| atmos			|                                                                                                                                                                                 
| cloudfiles	|                                                                                                                                                                            
| filesystem	|                                                                                                                                                                            
| nova			|                                                                                                                                                                                  
| swift			|                                                                                                                                                                                 
| walrus		|                                                                                                                                                                                


## Concepts 

The BlobStore API requires knowledge of 3 concepts: service, container, and blob.  
A BlobStore is a key-value store such as Amazon S3, where your account exists, and where you can create containers.  
A container is a namespace for your data, and you can have many of them.  
Inside your container, you store data as a Blob referenced by a name.  In all BlobStores the combination of your account, container, 
and blob relates directly to an HTTPs url. 

Here are some key points about blobstores:

* Globally addressable
* Key, value with metadata
* Accessed via HTTP
* Containers are provisioned on demand through API calls
* Unlimited scaling
* Most are billed on a usage basis

### Container

A container is a namespace for your objects.  Depending on the service, the scope can be global, account, or sub-account scoped.  
For example, in Amazon S3, containers are called buckets, and they must be uniquely named such that no-one else in
 the world conflicts.  In other blobstores, the naming convention of the container is less strict.  
All blobstores allow you to list your containers and also the contents within them.  These contents can 
either be blobs, folders, or virtual paths.

Everything in a BlobStore is stored in a container.  A container is like a website.  So, if my container name is adrian, 
it will be created in an http accessible way.  
For example, if I'm using Amazon S3, a container looks like this: `http://adrian.s3.amazonaws.com`.  
If I store my photo with a key "mymug.jpg," you can guess it will end up here: `http://adrian.s3.amazonaws.com/mymug.jpg`   

### Blob

A blob is unstructured data that is stored in a container.  
Some blobstores call them objects, blobs, or files.  You lookup blobs in a container by a text key, which often relates 
directly to the HTTP url used to manipulate it.  Blobs can be zero length or larger, some restricting size to 5GB, 
and others not restricting at all.  

Finally, blobs can have metadata in the form of text key-value pairs you can store alongside the data.  
When a blob is in a folder, its name is relative to that folder.  Otherwise, it is its full path.

### Folder

A folder is a subcontainer.  It can contain blobs or other folders.  The names of items in a folder are `basenames`. 
Blob names incorporate folders via "/" - just like you would with a "regular" file system. 

### Virtual Path 

A virtual path can either be a marker file or a prefix.  In either case, they are purely used to 
give the appearance of a hierarchical structure in a flat BlobStore.  
When you perform a list at a virtual path, the blob names returned are absolute paths.

### Access Control

By default, every item you put into a container is private, if you are interested in giving access to others, 
you will have to explicitly configure that.  

Currently, means to expose your containers to the public are provider-specific.

### limitations 
Each blobstore has its own limitations. 

  * *S3* According to Amazon, it is better to create or delete buckets in a separate initialization or setup routine that you run less often.
  	You are also only allowed 100 buckets per account, so be parsimonious (frugal).
  * *Azure* You have to wait 30 seconds before recreating a container with the same name.
  * _Azure_ currently supports max 64MB files, google storage has a very large limit, 
  * Amazon S3 introduced a multipart upload possibility which allow files until 5TB size.
  * _S3_ and _Rackspace_ CloudFiles have a 5GB limit.  We've engineered jclouds to not put further limits on blob size.


## Using BlobStore API

### Connecting to a BlobStore

A connection to a `BlobStore` like S3 in jclouds is called a `BlobStoreContext`.  
An `BlobStoreContext` associates an identity on a provider to a set of network connections. 
At a minimum, you need to specify your identity (in the case of S3, AWS Access Key ID) and a credential (in S3, your Secret Access Key).  
Once you have your credentials, connecting to your `BlobStore` service is easy:

{% highlight java %}
BlobStoreContext context = new BlobStoreContextFactory().createContext("aws-s3", identity, credential);
{% endhighlight %}

This will give you a connection to the BlobStore, and if it is remote, it will be SSL unless unsupported by 
the provider.  Everything you access from this context will use the same credentials and potentially the same objects. 

### Disconnecting

When you are finished with a context, you should close it using close method:

{% highlight java %}
context.close();
{% endhighlight %}
<!-- TODO Check Links -->
There are many options available for creating contexts.  Please see the javadoc for 
[BlobStoreContextFactory](http://jclouds.rimuhosting.com/apidocs/org/jclouds/blobstore/BlobStoreContextFactory.html)
for detailed description.

### APIs

You can choose from four apis in increasing complexity: Map, BlobMap, BlobStore, and AsyncBlobStore.  
For simple applications, you may find the most basic `Map<String,InputStream>` interface most appropriate.  
As complexity increases, you are also able to use the AsyncBlobStore interface: `FutureCommand`.  Let's review the `Map` apis first.

#### InputStreamMap

If you don't want to be bothered with the details of a BlobStore like Amazon S3, you may consider just accessing containers
 as a plain `Map<String, InputStream>` object.  Just create your context to to the BlobStore, choose the container of the stuff
 you want to manage, and get to work:

{% highlight java %}
BlobStoreContext context = new BlobStoreContextFactory().createContext("aws-s3", identity, credential);
Map<String, InputStream> map = context.createInputStreamMap("adrian.photos");
// do work
context.close();
{% endhighlight %}

##### Tips
* Always close your InputStreams
When you do something like this, the `InputStream` returned may be holding a connection to the provider.  
Be sure to close your `InputStream` promptly.

{% highlight java %}
InputStream aGreatMovie = map.get("theshining.mpg");
try {
      //watch
} finally {
if (aGreatMovie != null) aGreatMovie.close();
}

{% endhighlight %}

* Extra put methods
While you can feel free to use `map.put("stuff", new FileInputStream("stuff.txt")`, jclouds does provide some extra goodies.  
To use these, use the `InputStreamMap` class as opposed to `Map<String,InputStream>` when creating you Map view.

{% highlight java %}
InputStreamMap map = context.createInputStreamMap("adrian.photos");
map.putFile("stuff", new File("stuff.txt"));
map.putBytes("secrets", Util.encrypt("secrets.txt"));
map.putString("index.html", "<html><body>hello world</body></html>");
{% endhighlight %}

There are also corresponding `putAllFiles`, `Bytes`, `Strings` methods if you have bulk stuff to store.

#### BlobMap

There are some limitations when using the `Map<String, InputStream>` api.  For starters, you cannot pass any extra data
 to the provider.  For example, if you want to pass a default filename via the `Content-Disposition` group, 
it cannot be done this way.  `BlobMap`  allows you do customize the data you are sending at the cost of coding to a `jclouds` API. 
Considering it is only one class at this point, this is a decent tradeoff for many.  

Here is an example that shows how to use the `BlobMap` api:

{% highlight java %}
BlobStoreContext context = new BlobStoreContextFactory().createContext("aws-s3", identity, credential);
BlobMap map = context.createBlobMap("adrian.photos");

Blob blob = map.blobBuilder("sushi.jpg")                                                                                                                                                  
               .payload(new File("sushi.jpg"))// or byte[]. InputStream, etc.                                                                                                             
               .contentDisposition("attachment; filename=sushi.jpg")                                                                                                                      
               .contentType("image/jpeg")                                                                                                                                                 
               .calculateMD5().build();

map.put(blob.getName(), blob);

context.close();
{% endhighlight %}

#### BlobStore (Synchronous)
<!-- TODO The difference between BlobStore/Introduction -->

Here is an example of the `BlobStore` interface.

{% highlight java %}
// init
context = new BlobStoreContextFactory().createContext(
												"aws-s3",
                  								accesskeyid,
                  								secretaccesskey);
blobStore = context.getBlobStore();

// create container
blobStore.createContainerInLocation(null, "mycontainer");
  
// add blob
blob = blobStore.blobBuilder("test")  // you can use folders via newBlob(folderName + "/sushi.jpg")                                                                                     
                  .payload("testdata").build();
blobStore.putBlob(containerName, blob);
{% endhighlight %}

##### Creating a Container
If you don't already have a container, you will need to create one.  First, get a BlobStore from your context:

{% highlight java %}
BlobStore blobstore = context.getBlobStore();
{% endhighlight %}

`Location` is a region, provider or another scope in which a container can be created to ensure data locality.
If you don't have a location concern, pass `null` to accept the default.

{% highlight java %}
boolean created = blobStore.createContainerInLocation(null, String container);
if (created){
	// the container didn't exist, but does now
}else{
 	// the container already existed
}
    
{% endhighlight %}

#### AsyncBlobStore

`AsyncBlobStore` is the third and most powerful way to interact with a BlobStore. The API are asynchronous even 
if the engine you choose is not asynchronous.
`AsyncBlobStore` has the same methods as the `BlobStore`, but all commands return `java.util.concurrent.Future` results.  
Using `AsyncBlobStore` you can perform a lot of commands simultaneously, such as aggregating hundreds of blobs for processing.  
You can also attach listeners to the result, so that you can do things like publish to a message queue when an operation completes.

Here's an example of uploading tons of blobs at the same time:

{% highlight java %}
import static org.jclouds.concurrent.FutureIterables.awaitCompletion;

Map<Blob, Future<?>> responses = Maps.newHashMap();
for (Blob blob : blobs) {
   responses.put(blob, context.getAsyncBlobStore().putBlob(containerName, blob));
}
exceptions = awaitCompletion(responses, 
      context.utils().userExecutor(),
      maxTime,
      logger,
      String.format("putting into containerName: %s", containerName));
{% endhighlight %}


### Multipart upload

Providers may implement multipart upload for large or very files.                                                                                                                         
Here's an example of `multipart upload` using aws-s3 provider, which [allow uploading files large as 5TB.](http://docs.amazonwebservices.com/AmazonS3/latest/dev/index.html?qfacts.html)   

{% highlight java %}
import static org.jclouds.blobstore.options.PutOptions.Builder.multipart;                                                                                                                 

  // init                                                                                                                                                                                 
  context = new BlobStoreContextFactory().createContext(                                                                                                                                  
                  "aws-s3",                                                                                                                                                               
                  accesskeyid,                                                                                                                                                            
                  secretaccesskey);                                                                                                                                                       
  AsyncBlobStore blobStore = context.getAsyncBlobStore();                                                                                                                                 
                                                                                                                                                                                          
  // create container                                                                                                                                                                     
  blobStore.createContainerInLocation(null, "mycontainer");                                                                                                                               
                                                                                                                                                                                          
  File input = new File(fileName);                                                                                                                                                        
  // Add a Blob                                                                                                                                                                           
  Blob blob = blobStore.blobBuilder(objectName).payload(input)                                                                                                                            
               .contentType(MediaType.APPLICATION_OCTET_STREAM).contentDisposition(objectName).build();                                                                                   
  // Upload a file                                                                                                                                                                        
  ListenableFuture<String> futureETag = blobStore.putBlob(containerName, blob, multipart());                                                                                              
                                                                                                                                                                                          
  // asynchronously wait for the upload                                                                                                                                                   
  String eTag = futureETag.get();                                                                                                                                                         
{% endhighlight %}

### Logging

You can now see status of aggregate blobstore commands by enabling at least DEBUG on the log category: "jclouds.blobstore".  

Here is example output:
{% highlight text %}
2010-01-31 14:41:14,921 TRACE [jclouds.blobstore] (pool-4-thread-4) deleting from containerName: adriancole-blobstore2, 
completed: 5001/5001, errors: 0, rate: 14ms/op
{% endhighlight %}

If you are using the Log4JLoggingModule, here is an example log4j.xml stanza you can use to enable blobstore logging:

{% highlight xml %}
   <appender name="BLOBSTOREFILE" class="org.apache.log4j.DailyRollingFileAppender">
        <param name="File" value="logs/jclouds-blobstore.log" />
        <param name="Append" value="true" />
        <param name="DatePattern" value="'.'yyyy-MM-dd" />
        <param name="Threshold" value="TRACE" />
        <layout class="org.apache.log4j.PatternLayout">
            <param name="ConversionPattern" value="%d %-5p [%c] (%t) %m%n" />
        </layout>
    </appender>

    <appender name="ASYNCBLOBSTORE" class="org.apache.log4j.AsyncAppender">
        <appender-ref ref="BLOBSTOREFILE" />
    </appender>

   <category name="jclouds.blobstore">
        <priority value="TRACE" />
        <appender-ref ref="ASYNCBLOBSTORE" />
    </category>
{% endhighlight %}

# Clojure

The above examples show how to use the `BlobStore` API in Java. You can also use the API in Clojure.

## Setup 

  * Install [leiningen](http://github.com/technomancy/leiningen)
  * `lein new mygroup/myproject`
  * `cd myproject`
  * `vi project.clj`
  * for jclouds 1.1 and earlier (clojure 1.2 only)

{% highlight clojure %}
(defproject mygroup/myproject "1.0.0" 
  :description "FIXME: write"
  :dependencies [[org.clojure/clojure "1.2.0"]
                 [org.clojure/clojure-contrib "1.2.0"]
		 [org.jclouds/jclouds-allblobstore "1.1.0"]])
{% endhighlight %}

    * for jclouds 1.2 / snapshot (clojure 1.2 and 1.3)
{% highlight clojure %}
(defproject mygroup/myproject "1.0.0" 
  :description "FIXME: write"
  :dependencies [[org.clojure/clojure "1.3.0"]
                 [org.clojure/core.incubator "0.1.0"]
                 [org.clojure/tools.logging "0.2.3"]
                 [org.jclouds/jclouds-allcompute "1.2.0-SNAPSHOT"]]
  :repositories {"jclouds-snapshot" "https://oss.sonatype.org/content/repositories/snapshots"}) 
{% endhighlight %}

  * Execute `lein deps`

## Usage 

Execute `lein repl` to get a repl, then paste the following or write your own code.  
Clearly, you need to substitute your accounts and keys below.

{% highlight clojure %}
(use 'org.jclouds.blobstore2)

(def *blobstore* (blobstore "azureblob" account encodedkey))                                                                                                                            
(create-container *blobstore* "mycontainer")                                                                                                                                            
(put-blob *blobstore* "mycontainer" (blob "test" :payload "testdata"))
{% endhighlight %}

# Advanced Concepts

This section covers advanced topics typically needed by developers of clouds.

## Signing requests

### java example 
{% highlight java %}

HttpRequest request = context.getSigner().
                              signGetBlob("adriansmovies",
                                          "sushi.avi");
{% endhighlight %}
### clojure example 

{% highlight clojure %}
(let [request (sign-blob-request "adriansmovies"
                                 "sushi.avi" {:method :get})])

{% endhighlight %}

## Configure multipart upload strategies ==                                                                                                                                               
There are two `MultipartUploadStrategy` implementations: `SequentialMultipartUploadStrategy` and `ParallelMultipartUploadStrategy`.                                                       
Default strategy is the `ParallelMultipartUploadStrategy`. With parallel strategy the number of threads running in parallel can be configured using `jclouds.mpu.parallel.degree` propert\
y, the default value is 4.

# Design 
<!-- TODO -->

## Marker Files

Marker Files allow you to establish presence of directories in a flat key-value store.  
Azure, S3, and Rackspace all use pseudo-directories, but in a different ways.  For example, some tools look for a 
content type "application/directory", while others look for naming patterns such as a trailing slash or the suffix `_$folder$`. 

In jclouds, we attempt to detect whether a blob is pretending to be a directory, and if so, type it as StorageType.RELATIVE_PATH.  
Then, in a `list()` command, it will appear as a normal directory.   The two objects responsible for this 
are `IfDirectoryReturnNameStrategy` and `MkdirStrategy`.

There is a problem with this approach, there are multiple ways to suggest presence of a directory.  For example, it is 
entirely possible that both the trailing slash and _$folder$ suffixes exist.  For this reason, a simple remove, 
or `rmDir` will not work, as may be the case that there are multiple tokens relating to the same directory.

For this reason, we have a `DeleteDirectoryStrategy` strategy.  The default version of this used 
for flat trees removes all known deviations of directory markers.

## Content Metadata : Content Disposition

You may be using jclouds to upload some phots to the cloud, show thumbnails of them to the user 
via a website and allow to download the original image.  When the user clicks on the thumbnail, 
a the download dialog appears.  To control the name of the file in the "save as" dialog, 
you must set [Content Disposition](http://www.jtricks.com/bits/content_disposition.html).  Here's how you can do 
it with BlobStore API:

{% highlight java %}
Blob blob = context.getBlobStore().blobBuilder("sushi.jpg")                                                                                                                               
               .payload(new File("sushi.jpg"))// or byte[]. InputStream, etc.                                                                                                             
               .contentDisposition("attachment; filename=sushi.jpg")                                                                                                                      
               .contentType("image/jpeg")                                                                                                                                                 
               .calculateMD5().build();
{% endhighlight %}


## Large Lists

A listing is a set of metadata about items in a container.  It is normally associated with a single GET request 
against your container. 

Large lists are those who exceed the default or maximum list size of the blob store.  In S3, Azure, and 
Rackspace, this is 1000, 5000, and 10000 respectively.  Upon hitting this threshold, you need to continue the 
list in another HTTP request.  

In the new `BlobStore` api, list responses return a `PageSet` object.

A `PageSet` object is the same as a normal `Set`, except that it has a new method 

{% highlight java %}
String getNextMarker();
{% endhighlight %}

If this returns `null`, you have the entire listing.  If not, you can choose to continue iterating the list by 
specifying the `ListContainerOption` 
`afterMarker` to this value.

Our Map api knows how to concatenate lists via the `ListMetadataStrategy` object.

## Return Null on Not Found

All apis, provider-specific or abstraction, must return null when an object is requested, but not found.  
Throwing exceptions is only appropriate when there is a state problem, for example requesting an object from a container
that does not exist is a state problem, and should throw an exception.

## Uploading Large Files

As long as you use either `InputStream` or File as the payload your blob, you should be fine.  
Note that in S3, you must calculate the length ahead of time, since it doesn't support chunked encoding. 
Our integration tests ensure that we don't rebuffer in memory on upload:
[testUploadBigFile](http://github.com/jclouds/jclouds/blob/master/core/src/test/java/org/jclouds/http/BaseHttpCommandExecutorServiceIntegrationTest.java).

This is verified against all of our http clients, conceding that it isn't going to help limited environments such as 
google app engine.

## Downloading Large Files

A blob you've downloaded via `blobstore.getBlob()` can be accessed via `blob.getPayload().getInput()` or 
`blob.getPayload().writeTo(outputStream)`.  Since these are streaming, you shouldn't have a problem with memory 
unless you rebuffer it.

