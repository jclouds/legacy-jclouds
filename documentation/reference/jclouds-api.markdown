---
layout: jclouds
title: jclouds API
---
# jclouds API
 
This page explains the jclouds API which is underneath all tools that use jclouds. 
The jclouds API binds HTTP rest services to synchronous and asynchronous java apis. 

These bindings can be found in the RestContext, a highly configurable object that represents a scope of account to a service, 
where account is tied to your username, and service represents a _REST_ like interface for service such as Amazon S3 or Microsoft Azure. 


## Configuration Examples
### Http Proxy

In order to use jclouds with an http proxy, you must use the default http engine and
specify the proxy parameters as system properties to the java virtual machine.

```
-Dhttps.proxyHost=proxy  -Dhttps.proxyPort=3128
```

### Enterprise Configuration
Enterprise configuration attempts to make the best choices for you, with regards to which components to use to power your connections to the cloud. 
Specifically, this chooses `joda` dates, `bouncycastle` encryption, and `apache HTTP Client 4.0` connection pools.

You can also add further configuration, by passing it in the set of modules to configure.  For example, here's how to change the default connection and thread limits, and add log4j.

{% highlight java %}
import static org.jclouds.Constants.*;


  Properties overrides = new Properties();
  overrides.setProperty(PROPERTY_MAX_CONNECTIONS_PER_CONTEXT, 20 + "");
  overrides.setProperty(PROPERTY_MAX_CONNECTIONS_PER_HOST, 0 + "");
  overrides.setProperty(PROPERTY_CONNECTION_TIMEOUT, 5000 + "");
  overrides.setProperty(PROPERTY_SO_TIMEOUT, 5000 + "");
  overrides.setProperty(PROPERTY_IO_WORKER_THREADS, 20 + "");
  // unlimited user threads
  overrides.setProperty(PROPERTY_USER_THREADS, 0 + "");
 
 
  Set<Module> wiring =  ImmutableSet.of(new EnterpriseConfigurationModule(), new Log4JLoggingModule());

  // same properties and wiring can be used for many services, although the limits are per context
  blobStoreContext = new BlobStoreContextFactory().createContext("s3", account, key, wiring, overrides);
  computeContext = new ComputeServiceContextFactory().createContext("ec2", account, key, wiring, overrides);
{% endhighlight %}

### Timeout

Aggregate commands will take as long as necessary to complete, as controlled by `FutureIterables.awaitCompletion`.  
If you need to increase or decrease this, you will need to adjust the property `jclouds.request-timeout` or `Constants.PROPERTY_REQUEST_TIMEOUT`.  
This is described in the Advanced Configuration section.

### Google App Engine

## Standard RuntimeExceptions

Certain exceptions are propagated, even if they are nested inside other exceptions.  
These allow `jclouds` code to operate without exception searching.  

For example, you can wrap any `jclouds` command in the following reliably:

{% highlight java %}
  try {
      String = client.getFoo("bar");
  } catch (AuthorizationException e) {
      // even if the auth exception was under the scenes wrapped in an http exception, 
      // the first exception of type AuthorizationException will show up here.  This allows
      // you to make preventative measures that ensure your code doesn't lock out accounts.
  }
{% endhighlight %}

Here's a list of our "standard" exceptions which are defined in `org.jclouds.util.Utils#returnFirstExceptionIfInListOrThrowStandardExceptionOrCause`:

  * IllegalStateException
  * UnsupportedOperationException
  * IllegalArgumentException
  * AuthorizationException
  * ResourceNotFoundException
  * HttpResponseException

## *NULL* Return values

All API methods, either provider-specific or abstracti, must return _null_ when an requested object is not found.
Throwing exceptions is only appropriate when there is a state problem, for example requesting an object from a container that does not 
exist is a state problem, and should throw an exception.

