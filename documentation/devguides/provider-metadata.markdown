---
layout: jclouds
title: Provider Metadata
---

# Provider Metadata in jclouds

## Introduction

Provider metadata in jclouds lets the provider implementor/maintainer to do two major things very easily:

*  Describe the provider (Verbose name, documentation url, homepage url, console url, etc.)
*  Hook into a provider loading mechanism similar to a "plugin loader"

The purpose for this document it to further explain how you can implement provider metadata for a provider.

## Implementing Provider Metadata 

Implementing provider metadata for a jclouds provider is a two step process.  
First you need to have an implementation class that will be loaded by the [Java](http://java.sun.com) 
[ServiceLoader](http://download.oracle.com/javase/6/docs/api/java/util/ServiceLoader.html).  
Next you need to create a "provider configuration file" that `ServiceLoader` can locate and use. 
 We'll talk about the actual details below.

## Example Implementation Class 

The process for implementing provider metadata in jclouds is really simple.  
All you have to do is extend a concrete base class
 [org.jclouds.providers.BaseProviderMetadata](https://github.com/jclouds/jclouds/blob/master/core/src/main/java/org/jclouds/providers/BaseProviderMetadata.java) which is in jclouds/core.  
This class is abstract class because it implements the 
[org.jclouds.providers.ProviderMetadata](https://github.com/jclouds/jclouds/blob/master/core/src/main/java/org/jclouds/providers/ProviderMetadata.java) interface yet the provider metadata implementor 
is the one that should be implementing the `ProviderMetadata` interface methods.  

The reason for this is Java's `ServiceLoader` requires an interface for its plugin system but for us to 
do real object equality checks we needed a class to implement the equals/hashCode methods.  

That being said, here is an example of implementing provider metadata for [Amazon Elastic Compute Cloud (EC2)](http://aws.amazon.com/) 
[AWSEC2ProviderMetadata.java](https://github.com/jclouds/jclouds/blob/master/providers/aws-ec2/src/main/java/org/jclouds/aws/ec2/AWSEC2ProviderMetadata.java):

{% highlight  java %}
/**
 *
 * Copyright (C) 2011 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */
package org.jclouds.aws.ec2;

import com.google.common.collect.ImmutableSet;

import java.net.URI;
import java.util.Set;

import org.jclouds.providers.BaseProviderMetadata;
import org.jclouds.providers.ProviderMetadata;

/**
 * Implementation of {@ link org.jclouds.types.ProviderMetadata} for Amazon's
 * Elastic Compute Cloud (EC2) provider.
 *
 * @author Jeremy Whitlock <jwhitlock@apache.org>
 */
public class AWSEC2ProviderMetadata extends BaseProviderMetadata {

   /**
    * {@inheritDoc}
    */
   @Override
   public String getId() {
      return "aws-ec2";
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String getType() {
      return ProviderMetadata.COMPUTE_TYPE;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String getName() {
      return "Amazon Elastic Compute Cloud (EC2)";
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String getIdentityName() {
      return "Access Key ID";
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String getCredentialName() {
      return "Secret Access Key";
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public URI getHomepage() {
      return URI.create("http://aws.amazon.com/ec2/");
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public URI getConsole() {
      return URI.create("https://console.aws.amazon.com/ec2/home");
   }
   /**
    * {@inheritDoc}
    */
   @Override
   public URI getApiDocumentation() {
      return URI.create("http://docs.amazonwebservices.com/AWSEC2/latest/APIReference");
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Set<String> getLinkedServices() {
      return ImmutableSet.of("aws-s3", "aws-ec2", "aws-elb", "aws-simpledb");
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Set<String> getIso3166Codes() {
      return ImmutableSet.of("US-VA", "US-CA", "IE", "SG", "JP-13");
   }

}
{% endhighlight %}

The code above is pretty self-explanatory but if you want more details, 
the `org.jclouds.provider.ProviderMetadata` has the actual javadocs that have more detail.  
Now that you have an example implementation class, let's look at how to create the service file.

## Example Provider Configuration File

The `ServiceLoader`'s provider configuration file has two requirements:

*  The location of the file is META-INF/services/&lt;fully-qualified binary name of the interface&gt;
*  The contents of the file is a line-delimited list of fully-qualified binary names of the concrete classes

In our example, we would end up with a provider configuration file of *META-INF/services/org.jclouds.providers.ProviderMetadata*
 with a single line in it looking like this:

{% highlight  java %}
org.jclouds.aws.ec2.AWSEC2ProviderMetadata
{% endhighlight %}

Now that we know how to implement provider metadata for jclouds providers, let's look at an example test for 
the above provider to see how you might go about testing your provider metadata implementation.

## Example Provider Metadata Test

Testing provider metadata in jclouds is very simple thanks to the `org.jclouds.providers.BaseProviderMetadata` class. 
All you have to do is check for object equality to make sure the provider you expect to receive is the one the `ServiceLoader` is giving you. 

Below is the [org.jclouds.providers.AWSEC2ProviderTest.java](https://github.com/jclouds/jclouds/blob/master/providers/aws-ec2/src/test/java/org/jclouds/aws/ec2/AWSEC2ProviderTest.java) 
class that tests the `org.jclouds.aws.ec2.AWSEC2ProviderMetadata` class:

{% highlight java %}
/**
 *
 * Copyright (C) 2011 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */
package org.jclouds.aws.ec2;

import org.jclouds.aws.ec2.AWSEC2ProviderMetadata;
import org.jclouds.providers.BaseProviderMetadataTest;
import org.jclouds.providers.ProviderMetadata;
import org.testng.annotations.Test;

/**
 * The AWSEC2ProviderTest tests the org.jclouds.aws.ec2.AWSEC2Provider class.
 * 
 * @author Jeremy Whitlock <jwhitlock@apache.org>
 */
@Test(groups = "unit", testName = "AWSEC2ProviderTest")
public class AWSEC2ProviderTest extends BaseProviderMetadataTest {

   public AWSEC2ProviderTest() {
      super(new AWSEC2ProviderMetadata(), ProviderMetadata.COMPUTE_TYPE);
   }
}
{% endhighlight %}

Now that we know how to implement and test provider metadata, below is an example of why this might be useful 
to jclouds consumers. 

For examples on how to use !ProviderMetadata now that you know how to implement your own, check out the 
[Using Provider Metadata](/documentation/reference/using-provider-metadata) page.

