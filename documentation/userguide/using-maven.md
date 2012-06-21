---
layout: jclouds
title: Using jClouds with Apache Maven
---
#Using jClouds with Apache Maven

## Introduction

To obtain jclouds artifacts you need to define the repository, as well as the specific packages you need:

## Environment Variables

* JAVA_HOME : You must use a recent version of java 6 in order to use jclouds.
* M2_HOME : jclouds requires Apache Maven 3.0.2 or higher
* MAVEN_OPTS : Some of jclouds tests are heavyweight, as they launch many threads and attempt concurrent activity.  
	Default JVM parameters will not work, if you attempt to run `mvn install` from the root of jclouds directory.  
	Please use the following to setup your environment appropriate to execute.
	* Windows 32 bit JDK
	`set MAVEN_OPTS=-Xmn128m -Xms256m -Xmx256m -XX:PermSize=64m -XX:MaxPermSize=64m -Xss1024k`
	* Non-Windows 64-bit JDK
	`export MAVEN_OPTS="-Xms128m -Xmx256m"`

## Dependencies

Depending on what you want to do, you may need different dependencies.

### BlobStore

This assumes you are only interested in Basic BlobStore commands, like below:

{% highlight java %}
BlobStoreContext context = new BlobStoreContextFactory().createContext(provider, identity, credential);
Map<String, InputStream> map = context.createInputStreamMap("adrian.home");
  // do work
context.close();
{% endhighlight %}

This also assumes that you do not have heavy-duty needs that warrant connection-pooling or NIO, 
or that you are in an environment that cannot use them.

In this case, you only need to add the following to your project's dependencies:
{% highlight xml %}
<dependency>
    <groupId>org.jclouds</groupId>
    <artifactId>jclouds-blobstore</artifactId>
    <version>1.2.1</version>
</dependency>
{% endhighlight %}
Using the above, you will gain access to the provider named `transient`.  Note that this is in-memory.  Typically, you will instead want to add dependencies on a specific provider of resources like those for S3:

{% highlight xml %}
<dependency>
    <groupId>org.jclouds.provider</groupId>
    <artifactId>aws-s3</artifactId>
    <version>1.2.1</version>
</dependency>
{% endhighlight %}

Noting that you could alternatively ask for all of our supported blobstores using the dependency `jclouds-allblobstore` instead.

Then, you'd substitute the correct credentials and such here:
{% highlight java %}
BlobStoreContext context = new BlobStoreContextFactory().createContext("aws-s3", accessKey, secret);
{% endhighlight %}

### BlobStore from Google App Engine

There is no change to the api, in order to switch your code to use `URLFetchService` from within GAE.  
However, you do have to configure your connection differently:

{% highlight java %}
BlobStoreContext context = new BlobStoreContextFactory().createContext(provider, identity, credential, 
				ImmutableSet.<Module>of(new AsyncGoogleAppEngineConfigurationModule()));
{% endhighlight %}

Here are the dependencies needed to use google's UrlFetchService::
{% highlight xml %}
<dependency>
    <groupId>org.jclouds</groupId>
    <artifactId>jclouds-blobstore</artifactId>
    <version>1.2.1</version>
</dependency>
<dependency>
    <groupId>org.jclouds.driver</groupId>
    <artifactId>jclouds-gae</artifactId>
    <version>1.2.1</version>
</dependency>
{% endhighlight %}

### Logging

jclouds will by default use JDK logging.  To switch to log4J or slf4j, you have to add a logging module to your configuration code:

{% highlight java %}
BlobStoreContext context = new BlobStoreContextFactory().createContext(provider, identity, credential, 
										ImmutableSet.<Module>of(new Log4JLoggingModule()));
{% endhighlight %}

Here are the dependencies for BlobStore and log4j logging:

{% highlight xml %}
<dependency>
    <groupId>org.jclouds</groupId>
    <artifactId>jclouds-blobstore</artifactId>
    <version>1.2.1</version>
</dependency>
<dependency>
    <groupId>org.jclouds.driver</groupId>
    <artifactId>jclouds-log4j</artifactId>
    <version>1.2.1</version>
</dependency>
{% endhighlight %}

The log categories are set to package names, so plan on assigning `org.jclouds` appropriately in your log4j.xml.

## Testing against real services with the live profile
By default, running `mvn clean install` will not depend on any external services.  However, we have a live profile that helps with this.  In almost every case, the live profile requires parameters relevant to the account you are testing against.  Here's how to configure these.

### Testing api or provider modules

Credentials and sometimes endpoints are required to run tests that connect to services such as BlobStore or ComputeService. These credentials will be used when you run `mvn -Plive clean install` from the directory containing the provider or api module you are testing.  Note that it isn't currently possible to run a single integration test due to constraints in maven.  If you'd like this to change, please read the [following](http://stackoverflow.com/questions/894737/how-to-run-individual-test-in-the-integration-test-target-in-maven) and escalate to surefire people.

Please add a [maven profile](http://maven.apache.org/guides/introduction/introduction-to-profiles.html) to settings.xml
so that your credentials can be used for integration tests:

{% highlight xml %}
<profile>
  <id>provider</id>
  <activation>
    <activeByDefault>true</activeByDefault>
  </activation>
  <properties>
    <test.provider.identity>account,user,apikey,etc</test.provider.identity>
    <test.provider.credential>password,secret,etc</test.provider.credential>
  </properties>
</profile>
{% endhighlight %}

Here is an example of a complete _settings.xml_ file you can save to the ~/.m2 directory, just remember to add your personal credentials:

{% highlight xml %}
<settings>
  <profiles>
    <profile>
      <id>provider</id>
      <activation>
        <activeByDefault>true</activeByDefault>
      </activation>
      <properties>
        <test.aws.identity>123908109d</test.aws.identity>
        <test.aws.credential>SDAIFJASKL9uiojfd</test.aws.credential>
        <test.trmk-vcloudexpress.identity>adrian@jclouds.org</test.trmk-vcloudexpress.identity>
        <test.trmk-vcloudexpress.credential>veggieBurgersInParadise</test.trmk-vcloudexpress.credential>
      </properties>
    </profile>
  </profiles>
</settings>
{% endhighlight %}

### Testing SSH drivers

SSH Credentials are required to run tests for SSH extensions.
Please add a [maven profile](http://maven.apache.org/guides/introduction/introduction-to-profiles.html) to settings.xml
so that your credentials can be used for integration tests:

{% highlight xml %}
<profile>
  <id>ssh</id>
  <activation>
    <activeByDefault>true</activeByDefault>
  </activation>
  <properties>
     <test.ssh.keyfile>PATH_TO/id_rsa</test.ssh.keyfile>
    <test.ssh.username>LOCAL_SSH_ID</test.ssh.username>
    <test.ssh.password>LOCAL_SSH_PASSWORD</test.ssh.password>
  </properties>
</profile>
{% endhighlight %}

Here is an example of a complete _settings.xml_ file you can save to the ~/.m2 directory, just remember to change the ssh credentials:

{% highlight xml %}
<settings>
  <profiles>
    <profile>
      <id>ssh</id>
      <activation>
        <activeByDefault>true</activeByDefault>
      </activation>
      <properties>
        <test.ssh.username>nobody</test.ssh.username>
        <test.ssh.password>asdf</test.ssh.password>
      </properties>
    </profile>
  </profiles>
</settings>
{% endhighlight %}


