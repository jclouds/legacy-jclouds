---
layout: jclouds
title: Introduction
---
# Introduction

JClouds log design has the following goals:

  * Introduce no dependencies on third party apis such as commons-logging or log4j
  * Support pluggable log implementations or optionally none


JClouds aims to be compatible with as many environments as possible.  Therefore, it has its own `org.jclouds.logging.Logger`.
 This interface serves two purposes: 

  * allow logging to be pluggable
  * allow zero-logging to be possible

## Pluggable logging

Pluggable logging is important.  While some users are ok using JDK logging, many prefer to use other
 apis such as commons logging or log4j.  
To widen the audience, we need a layer that protects java logging users from unnecessary  
dependencies while at the same time allowing log4j users the flexibility to retain a single config file.  

## Implementation

In jclouds, logging implementations are plugged-in by Guice.  Essentially classes declare a Logger member
annotated with `@Resource`, but set to a null-safe `Logger.NULL`. 

<!-- TODO change link to github -->
Post-construction, a subclass of [LoggerModule](http://jclouds.googlecode.com/svn/trunk/core/src/main/java/org/jclouds/logging/config/LoggingModule.java)
binds [BindLoggersAnnotatedWithResource](http://jclouds.googlecode.com/svn/trunk/core/src/main/java/org/jclouds/logging/config/BindLoggersAnnotatedWithResource.java),
 which in turn selects an appropriate Logger for the object.  

As this injection is handled in Guice modules, developers can be largely unaware of its existence, or even Guice itself.

## Zero logging

As high-performance API, jclouds must have means to determine the overhead incurred by I/O systems 
such as logging.  As such, it is standard practice to design your components initialized with an instance of `NullLogger`. 

At runtime, this will be overridden with an appropriate logger implementation so that benchmarks can assess performance impact.  

Here's an example of how a developer can achieve this:

{% highlight java %}
@Resource
protected Logger logger = Logger.NULL;
{% endhighlight %}

## Usage

Use Guice to select the type of logging you wish to use.  

In jclouds-core, there are two modules:

  * `NullLoggingModule` - does nothing
  * `JDKLoggingModule` - uses java.util.logging.logger

An additional implementation is available when you add the optional jclouds-log4j dependency:

  * `Log4JLoggingModule`

Here is example code of how to configure your components to use Log4J:
{% highlight java %}
    public static class A {
	@Resource
	private Logger logger = Logger.NULL;
    }

    A a = Guice.createInjector(new Log4JLoggingModule()).getInstance(A.class);
{% endhighlight %}

Note that some classes hide this away.  For example, S3ContextFactory will by default create a 
`JDKLoggingModule` unless you specify otherwise.

{% highlight java %}
S3Context contextWithJDKLogging = S3ContextFactory.createS3Context("myaccesskeyid","mysecretkey");
S3Context contextWithLog4JLogging = S3ContextFactory.createS3Context("myaccesskeyid","mysecretkey", 
										new Log4JLoggingModule());
{% endhighlight %}


## Logging in jclouds

In effort to not conflict with other libraries, `jclouds` contains its own logging classes.
These are by default bound to `java.util.logging`, but they can be rebound to other libraries such as `Log4J` or use `Guice` configuration modules.

Here's an example of adding log4j logging:
{% highlight java %}
// add properties that override defaults, as necessary
Properties overrides = new Properties();

//in case you are passing in configuration such as log4j
Set<Module> wiring = ImmutableSet.<Module> of(new Log4JLoggingModule());

// construct your context with the overrides in place
ComputeServiceContext context = new ComputeServiceContextFactory().createContext("terremark", user, key,
                                                              wiring, overrides);
{% endhighlight %}

jclouds performs three different kinds of logging: 

  * Standard context logging - used within each class
  * Wire logging -- similar to what is used in the apache commons http client project
  * Abstraction logging

## Context Logging

Context Logging will show you how jclouds translates the ReST API of a service into HTTP calls, 
and how HTTP results are translated back into java objects.

Each class has its own log named according to the class's fully qualified name. 
For example the class `JavaUrlHttpCommandExecutorService` has a log named `org.jclouds.http.internal.JavaUrlHttpCommandExecutorService`. 
Since all classes follow this convention it is possible to configure context logging for all classes using the single logging rule using `org.jclouds`.

## Wire Logging

Wire Logging is intentionally near identical to the apache HTTP components project system of the same name.

The wire log is used to log all data transmitted to and from servers when executing HTTP requests.
This log should only be enabled to debug problems, as it will produce an extremely large amount of log data, some of it in binary format.

Because the content of HTTP requests is usually less important for debugging than the HTTP headers, 
these two types of data have been separated into different wire logs. 

The content log is `jclouds.wire` and the header log is `jclouds.headers`.

## Abstraction Logging
<!-- TODO Need more info -->
jclouds.compute and jclouds.blobstore
