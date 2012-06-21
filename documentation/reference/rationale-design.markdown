---
layout: jclouds
title: Rationale & Design
---
# Rationale & Design

[JetS3t](https://jets3t.dev.java.net/) was a pioneering effort to connect the Java world to Amazon S3 services.  

The decision to create another API was not taken lightly:  JetS3t has many useful tools, and is a mature
 implementation of S3 connectivity.  
However useful in simple environments, JetS3t (v0.7.0) is unusable in concurrent or google app engine JVMs. 
Spending about a week in JetS3t code, it became clear that the effort needed was high enough to warrant a fresh start.

## Concurrency By Design 
### Motivation
[I N F I N I S P A N](http://www.jboss.org/infinispan) is by nature a highly concurrent platform.  
When asked to develop S3 integration,I researched the three mechanisms that currently exist: 
code-copy from Amazon's sample code, JetS3t, and noderunner-s3.  

As it turned out, JetS3t seemed the most viable option due to its currency and popularity.  
While single-threaded requests worked well, neither JetS3t nor the other two apis are capable of working in a highly
 concurrent environment:  their Date and Encryption strategies simply wouldn't work.  

### Implementation Approach

#### API
At its core, the JClouds api is designed for concurrency.  All methods are written to return Futures instead of blocking.  
This allows you to do the dishes while waiting for laundry.   Have a look at S3Connection:

{% highlight java %}
S3Connection connection = context.createConnection();
Future<String> md5HashFuture = connection.addObject(s3Bucket,s3Object);
//do productive work.. why wait?
String  md5Hash =  md5HashFuture.get();  // ok. I'm ready
{% endhighlight %}

Under the scenes, `S3Connection` creates a `FutureCommand` for the `addObject()` operation.
It then submits it to a `FutureCommandClient` for execution.
In environments that support non-blocking I/O, JClouds can use a non-blocking `FutureCommandClient`.
In restricted environments, the `FutureCommandClient` used will execute the request sequentially.  
The power is that the API is the same regardless of the limitations of the environment. 
 Programming to Futures will ensure you can take most advantage of the concurrency available on the platform.

#### Dates
The Java Date class is not a thread-safe class. In other words, if you have multiple 
threads parsing or manipulating date objects, you cannot expect idempotence.  

The following key points are designed-into JClouds to avoid performance problems in dates.

* Use joda-time


<!--
	TODO  Fix Jodatime link 
-->
[joda-time](http://joda-time.sourceforge.net/) is a thread-safe date utility.  
It constructs immutable date objects.  JClouds uses DateTime objects for all domain objects. 

* Don't over-calculate

Parsing date objects is very expensive.   
Amazon S3 requires that your request is signed within 15 minutes of their clock. 
 While the joda-time parser is thread-safe, it is synchronized and therefore slows 
highly concurrent applications.  To work around this slowness, JClouds will only 
parse date objects every second. 

#### Encryption
JCE encryption apis are also not all thread-safe. 

JClouds ditched JCE in favor of more efficient and threadsafe [bouncycastle](http://www.bouncycastle.org/) apis.

## Google App Engine
### Motivation
[Google App Engine](http://code.google.com/appengine/) has a 
[restricted runtime](http://code.google.com/appengine/docs/java/runtime.html)
that among other things prevents spawning threads, JCE, and socket i/o. 
JetS3t uses libraries that violate these rules and therefore cannot be used in Google App Engine.

JClouds pluggable http layer makes it easy to overcome these hurdles.  
From an implementation standpoint, we simply created a new FutureCommandClient that abides by the rules.
Furthermore, we test continually with our SDK 1.2 [example](http://jclouds.googlecode.com/svn/trunk/samples/googleappengine/).
In other words, JClouds will operate in GAE.  There are a couple reasons for this.

* I/O and Threading

We have a special configuration module written for GAE (URLFetchServiceClientModule).  
This uses their low-level `URLFetchService`
API and does not spawn threads. Here's an example of how to create an S3 context inside GAE:

{% highlight java %}
        S3Context context = S3ContextFactory.createS3Context(accesskeyid,secretkey, new URLFetchServiceClientModule());
{% endhighlight %}

### JCE on GAE

JClouds uses `bouncycastle` instead of JCE, as explained above.  As such, it does not violate the JCE restriction of GAE.
