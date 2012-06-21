---
layout: jclouds
title: Using jclouds with Google App Engine
---
# Using jclouds with Google App Engine

## Introduction

Writing a jclouds application to deploy to Google App Engine is fundamentally the same as any other environment.
There are only a couple things you need to keep in mind:  

  * You have to configure your context to use Google AppEngine compatible components.
  * You also have to ensure your requests don't take longer than 30 seconds.
  * You have to ensure you don't attempt to use ssh, as it implies socket creation

### Configuring to use GAE compatible components
Configuration can be made via property or a typed module.  Here's how you can do this:

* Using typed configuration

{% highlight java %}	
modules = ImmutableSet.of(new AsyncGoogleAppEngineConfigurationModule());
// note if you are using <= beta-9, providers will be ec2 and s3
compute = new ComputeServiceContextFactory().createContext("aws-ec2", accesskey, secret, modules);
blobStore = new BlobStoreContextFactory().createContext("aws-s3", accesskey, secret, modules);
{% endhighlight %}

* Using property-based configuration

Using properties-based configuration, you can load configuration from resource, and therefore choose to include the
Google App Engine config only when running inside Google AppEngine.

{% highlight java %}
Properties overrides = new Properties();
overrides.setProperty("jclouds.modules","org.jclouds.gae.config.AsyncGoogleAppEngineConfigurationModule");
// note if you are using <= beta-9, providers will be ec2 and s3
overrides.setProperty("aws-ec2.identity","accessKey");
overrides.setProperty("aws-ec2.credential","secret");
overrides.setProperty("aws-s3.identity","accessKey");
overrides.setProperty("aws-s3.credential","secret");

compute = new ComputeServiceContextFactory().createContext("aws-ec2", overrides);
blobStore = new BlobStoreContextFactory().createContext("aws-s3", overrides);
{% endhighlight %}

### Usage with Clojure
  * note you need to include the maven dependency: 
	org.jclouds/jclouds-gae if < beta-9, or org.jclouds.driver/jclouds-gae if beta-9/snapshot

{% highlight clojure %}
  (def compute
    (compute-service
      "aws-ec2" "accessKey" "secret" :gae-async))
{% endhighlight %}

  * note that this refers to `:gae-async` and `aws-ec2` which were added after 1.0-beta-9, 
	you can use `:gae` and `ec2` if you are using 1.0-beta-8 or earlier.

### Switching configuration based on whether you are in Google App Engine

You can create code to work in both a dev and prod environment by creating properties files in 
your WEB-INF directory, e.g., here are two files and a servlet context listener you can register to handle switching.

Edit or create `WEB-INF/jclouds-local.properties` as shown below:

{% highlight text %}
jclouds.blobstore=filesystem
jclouds.filesystem.basedir=/tmp/blobstore
filesystem.identity=foo
{% endhighlight %}

Edit or create `WEB-INF/jclouds-gae.properties` 

this assumes you want to use googlestorage provider.  use the same conventions for s3, azurestorage, cloudfiles, etc.

{% highlight text %}
jclouds.blobstore=googlestorage
jclouds.filesystem.basedir=/tmp/blobstore
jclouds.modules=org.jclouds.gae.config.AsyncGoogleAppEngineConfigurationModule
googlestorage.identity=accessKey
googlestorage.credential=secret
{% endhighlight %}

#### Configuring servlet listener
`Note you'll have to register this in your web.xml`

{% highlight java %}
public class BlobStoreRegistrationListener implements ServletContextListener {

   /**
    * sets a context property of "blobstore" with what is configured.
    */
   @Override
   public void contextInitialized(ServletContextEvent servletContextEvent) {
      Properties properties = null;
      if (System.getProperty("com.google.appengine.runtime.environment") != null){
         properties = loadJCloudsProperties("gae",servletContextEvent);
      } else {
         properties = loadJCloudsProperties("local",servletContextEvent);
      }
      servletContextEvent.getServletContext().setAttribute("blobstore",
         new BlobStoreContextFactory().createContext(
            properties.getProperty("jclouds.blobstore"), properties)
         ).getBlobStore();
      super.contextInitialized(servletContextEvent);
   }

   /**
    * closes the blobstore context.
    */
   @Override
   public void contextDestroyed(ServletContextEvent servletContextEvent) {
      BlobStore blobstore = getContextAttributeOrNull(servletContextEvent, "blobstore");
      if (blobstore != null) {
         blobstore.getContext().close();
      }
   }

   @SuppressWarnings("unchecked")
   static <T> T getContextAttributeOrNull(ServletContextEvent servletContextEvent, String name) {
      return (T) servletContextEvent.getServletContext().getAttribute(name);
   }

   static Properties loadJCloudsProperties(String key, ServletContextEvent servletContextEvent) {
      InputStream input = servletContextEvent.getServletContext().getResourceAsStream(
               "/WEB-INF/jclouds"+key+".properties");
      Properties props = new Properties();
      try {
         props.load(input);
      } catch (IOException e) {
         throw new RuntimeException(e);
      } finally {
         Closeables.closeQuietly(input);
      }
      return props;
   }
}
{% endhighlight %}

### Logging

Add to your google appengine logging.properties the following to get more data:

<!--
	TODO Table!!!!
-->
||setting||purpose||
||jclouds.headers.level=FINE||show the headers going to/from the provider (aws-ec2, aws-s3, etc.)||
||jclouds.compute.level=FINE||show compute commands||
||jclouds.blobstore.level=FINE||show blobstore commands||
||org.jclouds.level=FINE|| for all other jclouds loggers||

### ComputeService hints

If you plan to use the ComputeService api, you'll need to be careful not to use SSH functionality or 
exceed timeouts.  Here are the most important things you'll need to do:

* Don't attempt to use runScript, authorizePubliccredential, or installPrivatecredential templateOptions. 
	These operations require ssh and also take a long time to operate

* Always set blockUntilRunning(false) on your template

Every current cloud takes longer than 30 seconds to provision a node. 
You do not have luxury to synchronously block.  Setting the blockUntilRunning(false) option will help.

{% highlight java %}
computeService = context.getComputeService();
options = computeService.templateOptions().blockUntilRunning(false);
template = computeService.templateBuilder().options(options).build();
{% endhighlight %}

* Tone down all default wait properties to under 30 seconds

{% highlight java %}
import static org.jclouds.compute.reference.ComputeServiceConstants.PROPERTY_TIMEOUT_NODE_RUNNING;
import static org.jclouds.compute.reference.ComputeServiceConstants.PROPERTY_TIMEOUT_NODE_TERMINATED;
import static org.jclouds.compute.reference.ComputeServiceConstants.PROPERTY_TIMEOUT_PORT_OPEN;
import static org.jclouds.compute.reference.ComputeServiceConstants.PROPERTY_TIMEOUT_SCRIPT_COMPLETE;

Properties overrides = new Properties();
overrides.setProperty(PROPERTY_TIMEOUT_NODE_TERMINATED, "25000");
overrides.setProperty(PROPERTY_TIMEOUT_NODE_RUNNING, "25000");
overrides.setProperty(PROPERTY_TIMEOUT_SCRIPT_COMPLETE, "25000");
overrides.setProperty(PROPERTY_TIMEOUT_PORT_OPEN, "25000");

context = new ComputeServiceContextFactory()
      .createContext(service, identity, credential, ImmutableSet.of(
            new AsyncGoogleAppEngineConfigurationModule()), overrides);
{% endhighlight %}

### FAQ
1. I get a log message: "Failed to start reference finalizer" what gives?
 * A log message like below is an overly verbose way of Guice saying it can't spawn threads.  
	This is expected and also not a problem, as it works around the issue.
	
{% highlight text %}
com.google.inject.internal.util.$FinalizableReferenceQueue <init>: Failed to start reference finalizer thread. Reference cleanup will only occur when new references are created.
java.lang.reflect.InvocationTargetException
	at com.google.appengine.runtime.Request.process-8e3e8ebfb87d6827(Request.java)
	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
	at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:57)
{% endhighlight %}

