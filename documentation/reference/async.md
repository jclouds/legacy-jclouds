---
layout: jclouds
title: jclouds - Async Api
---

# Introduction

jclouds supplies blocking as well as non-blocking (asynchronous) clients to cloud apis such as [Amazon S3](http://demobox.github.com/jclouds-maven-site-1.4.0/1.4.0/jclouds-multi/apidocs/org/jclouds/s3/S3AsyncClient.html).  You can reference either approach from the [RestContext](http://demobox.github.com/jclouds-maven-site-1.4.0/1.4.0/jclouds-multi/apidocs/org/jclouds/rest/RestContext.html).  Our synchronous api is accessible via `context.getApi()`.  This is the simplest way to make calls: it is a blocking api with preconfigured timeouts.  When you need to perform non-blocking operations, or control timeouts programatically, use the non-blocking client via `context.getAsyncApi()`. 

jclouds async apis allows you to make parallel, non-blocking  calls to the cloud service and retrieve the results at a later point.  The async api is the same as the synchronous one, for example the same arguments are required and each have the same semantics.  The only difference is that the returnval is wrapped in a [Future](http://docs.oracle.com/javase/1.5.0/docs/api/java/util/concurrent/Future.html), more specifically a Guava [ListenableFuture](http://docs.guava-libraries.googlecode.com/git/javadoc/com/google/common/util/concurrent/ListenableFuture.html).

We encourage you to read the docs on futures, as that context is enough preparation.  That said, for convenience, we'll cover some quick hints below.

## Working with the Async Api
When you need to perform non-blocking operations, or control timeouts programatically, use the non-blocking client via `context.getAsyncApi()` from a provider-specific context, or `context.getAsyncBlobStore()`, if using the BlobStore View.

{% highlight java %}
AsyncBlobStore nonBlocking = context.getAsyncBlobStore();
// this will take ages to finish, so we'll background downloading
ListenableFuture<Blob> movie = nonBlocking.getBlob("movies", "bigfish.mpg");
System.out.println("get the popcorn");
// the following will block until the download completes
Futures.getUnchecked(movie);
{% endhighlight %}

It is important to keep in mind that operations on the Async Client will not generally return errors, particularly those relating to the remote operation.  For example, if there's a server error, you won't know until you issue `result.get()`.

Also important if you are new to living in the Future... Many newcomers forget to call `result.get()` and scratch their heads about overlapping commands or other weirdness.  If you experience weirdness, definitely check that you called `result.get()`.
