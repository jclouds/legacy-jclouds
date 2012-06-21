---
layout: jclouds
title: Using the amazon EC2 cloud
---
# Using the amazon EC2 cloud

## Introduction

The compute service is implemented fully for EC2 - so you can follow the guides to using the [ComputeService API](/documentation/userguide/compute). 

For credentials you will want you access key id and secret access key (don't use your user id or anything else). 
For practical EC2 usage - you will probably want to check out [AWS Quick Start](/documentation/quickstart/aws) 

## Images
### Default image
The default image for version 1.0.0 is [Amazon Linux](http://aws.amazon.com/amazon-linux-ami) and 64 bit.  
This by default chooses t1.micro size.  The m1.small instance size does not support 64 bit images, 
if you need this, you'll have to revise the template:

{% highlight java %}
// use the m1 small with amazon linux
Template template = compute.templateBuilder().hardwareId(InstanceType.M1_SMALL).osFamily(OsFamily.AMZN_LINUX).build();
{% endhighlight %}

In order to match an Ubuntu, or CentOs image, you'll need to see the Image Parsing section.

### Image Parsing

We had feedback from one of our users that our default image list for EC2 didn't contain CentOs. 
As you may know, parsing all images would take minutes due to the delay in calling 
ec2 across all 4 regions. We now by default parse images from Amazon, Canonical, Alestic, and
 RightScale. With the addition of RightScale, we now support CentOs.

{% highlight java %}

// pick the highest version of the RightScale CentOs template
Template template = compute.templateBuilder().osFamily(CENTOS).build();

// pick version 10.04 of ubuntu from canonical
Template template = compute.templateBuilder().hardwareId(InstanceType.M1_SMALL)
                  .osVersionMatches("10.04").imageDescriptionMatches("ubuntu-images").osFamily(OsFamily.UBUNTU).build();
{% endhighlight %}

### Image Filters [v1.1.0+]

Even refining lists to a set of user ids, using EC2 can be bogged down, parsing the thousands of public images published by the Amazon EC2 community.  Several users have expressed the desire to refine further, based on qualifications they choose.  You can set a property to restrict the list to a subset matching any recognized [http://docs.amazonwebservices.com/AWSEC2/latest/APIReference/ApiReference-query-DescribeImages.html image query].
{% highlight java %}
   overrides = new Properties();
   // choose only amazon images that are ebs-backed
   overrides.setProperty(AWSEC2Constants.PROPERTY_EC2_AMI_QUERY,
                         "owner-id=137112412989;state=available;image-type=machine;root-device-type=ebs");
   context = new ComputeServiceContextFactory().createContext("aws-ec2", access, secret,
            ImmutableSet.<Module> of(new SshjSshClientModule()), overrides)
{% endhighlight %}

#### Properties to set cluster compute images 
Cluster compute images are assigned the following default query, corresponding to the property key constant AWSEC2Constants.PROPERTY_EC2_CC_AMI_QUERY
```
	virtualization-type=hvm;architecture=x86_64;owner-id=137112412989,099720109477;hypervisor=xen;state=available;image-type=machine;root-device-type=ebs
```
In order to save time, we only look for cluster compute images in regions that support them.  In order to change the default for this, affect the property PROPERTY_EC2_CC_REGIONS, currently set to "us-east-1"


### Lazy Image Fetching

By default, the ComputeService will prefetch images from Alestic, Canonical, and 
RightScale. This allows you to query the images, which is great, 
if you don't know what you are looking for. However, if you already know the imageId you want, this is wasteful. 
We now provide the option to lazy-fetch images, which speed-ups execution.

#### Release 1.0.0 and below

{% highlight java %}
Properties overrides = new Properties();
// set owners to nothing
overrides.setProperty(EC2Constants.PROPERTY_EC2_AMI_OWNERS, "");

context = new ComputeServiceContextFactory().createContext("aws-ec2",
   accessid, secretkey, ImmutableSet.of(new Log4JLoggingModule()), overrides);

Template template = context.getComputeService().templateBuilder().imageId(
         "ami-ccb35ea5").build();
{% endhighlight %}

#### Release 1.1.0 and above 
{% highlight java %}
   Properties overrides = new Properties();

   // set AMI queries to nothing
   overrides.setProperty(AWSEC2Constants.PROPERTY_EC2_AMI_QUERY, "");
   overrides.setProperty(AWSEC2Constants.PROPERTY_EC2_CC_AMI_QUERY, "");

   context = new ComputeServiceContextFactory().createContext("aws-ec2",
      accessid, secretkey, ImmutableSet.of(new Log4JLoggingModule()), overrides);

   Template template = context.getComputeService().templateBuilder().imageId(
            "ami-ccb35ea5").build();
{% endhighlight %}

### Private Images
Amazon EC2 has the concept of private-but-shared amis - images that were bundled on 
another account, and not made public, but shared with a specified account. 
This is useful in the consolidated billing scenario - where you have one user 
managing image curation, and others using them (but all billed together). 

This private image may not show up in listImages, and jclouds will struggle with this.

Here's the solution:


#### Release 1.0.0 and below

{% highlight java %}
Properties props = new Properties();

//have a myFavouriteOwner - the user ID of the owner of the image.
props.setProperty(EC2Constants.PROPERTY_EC2_AMI_OWNERS, myFavoriteOwner + ",063491364108,099720109477");

// or.. you can set to '*' but this will take forever on amazon's ec2 service
props.setProperty(EC2Constants.PROPERTY_EC2_AMI_OWNERS, "*");


ComputeServiceContext context = new ComputeServiceContextFactory().createContext("aws-ec2", "accesss", "secret",
                                        ImmutableSet.<Module> of(new JschSshClientModule()), props);
{% endhighlight %}

#### Release 1.1.0 and above
{% highlight java %}
Properties props = new Properties();

//have a myFavoriteOwner - the user ID of the owner of the image.
props.setProperty(EC2Constants.PROPERTY_EC2_AMI_QUERY, "owner-id=137112412989,063491364108,099720109477,411009282317,"+myFavoriteOwner+";state=available;image-type=machine");

// or.. you can remove the owner part of the query, but this will take forever on amazon's ec2 service
props.setProperty(EC2Constants.PROPERTY_EC2_AMI_QUERY, "state=available;image-type=machine");


ComputeServiceContext context = new ComputeServiceContextFactory().createContext("aws-ec2", "accesss", "secret",
                                        ImmutableSet.<Module> of(new JschSshClientModule()), props);
{% endhighlight %}


You can then create nodes using the templateBuilder.imageId() method. 

#### Parsing private images

If you publish your own images, you probably would like to be able to choose 
the latest version using templateBuilder.  For example, you might want to say 
`templateBuilder.imageNameMatches("my-image").imageVersionMatches("1.1.0-.*").build()` 
which would pick the latest version of your image within the `1.1.0` range.

Amazon EC2 does not have typed fields for things like operating system family, image version, etc.  
In jclouds, we attempt to parse these fields from well known naming conventions.  
However, you might not be using naming conventions we know of, so often images show up as 
OsFamily.UNRECOGNIZED, with no image version.  Here's how to instruct jclouds to parse your images.

#### Create a custom parser
You'll need to override the default image parser with one that knows all the intimate secrets of your image.

{% highlight java %}
package com.foo;

import java.util.Map;

import javax.inject.Inject;

import org.jclouds.aws.ec2.compute.strategy.AWSEC2ReviseParsedImage;
import org.jclouds.compute.domain.ImageBuilder;
import org.jclouds.compute.domain.OperatingSystemBuilder;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.domain.Credentials;

import com.google.common.collect.ImmutableMap;
import com.google.inject.Singleton;

@Singleton
public class FooAWSEC2ReviseParsedImage extends AWSEC2ReviseParsedImage {

   @Inject
   public FooAWSEC2ReviseParsedImage() {
      super(ImmutableMap.<OsFamily, Map<String, String>> of());
   }

   /**
    * revide image
    */
   @Override
   public void reviseParsedImage(org.jclouds.ec2.domain.Image from, ImageBuilder builder, OsFamily family,
            OperatingSystemBuilder osBuilder) {
      // I always build Ubuntu 10.10 images
      osBuilder.family(OsFamily.UBUNTU);
      osBuilder.version("10.10");

      // our image version naming convention is /us-west-1/foo/1.1.0.20110224-0001
      builder.version(from.getImageLocation().substring(from.getImageLocation().lastIndexOf('/') + 1));
      builder.name("foo");

      // our image has a default user that's sudo-enable named foo-user
      builder.defaultCredentials(new Credentials("foo-user", "password"));
   }
}
{% endhighlight %}

#### Create a test for your parser

{% highlight java %}
package com.foo;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;

import org.jclouds.compute.domain.ImageBuilder;
import org.jclouds.compute.domain.OperatingSystemBuilder;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.domain.Credentials;
import org.testng.annotations.Test;

@Test
public class FooAWSEC2ReviseParsedImageTest {
   public void testFooImage() {

      ImageBuilder builder = createMock(ImageBuilder.class);
      OperatingSystemBuilder osBuilder = createMock(OperatingSystemBuilder.class);
      org.jclouds.ec2.domain.Image from = createMock(org.jclouds.ec2.domain.Image.class);

      expect(from.getImageLocation()).andReturn("/us-west-1/foo/1.1.0.20110224-0001").times(2);

      expect(osBuilder.family(OsFamily.UBUNTU)).andReturn(osBuilder);
      expect(osBuilder.version("10.10")).andReturn(osBuilder);
      // ensure our version parses properly
      expect(builder.version("1.1.0.20110224-0001")).andReturn(builder);
      expect(builder.name("foo")).andReturn(builder);
      expect(builder.defaultCredentials(new Credentials("foo-user", "password"))).andReturn(builder);

      // replay mocks
      replay(builder);
      replay(osBuilder);
      replay(from);

      new FooAWSEC2ReviseParsedImage().reviseParsedImage(from, builder, OsFamily.UNRECOGNIZED, osBuilder);

      // verify mocks
      verify(builder);
      verify(osBuilder);
      verify(from);
   }

}

{% endhighlight %}

#### Instruct jclouds to use your parser

{% highlight java %}
Properties overrides = setupProperties();
// your owner id
overrides.setProperty(EC2Constants.PROPERTY_EC2_AMI_OWNERS, "123123123213123");

context = new ComputeServiceContextFactory().createContext(provider, ImmutableSet
         .<Module> of(new JschSshClientModule(), new AbstractModule(){

            @Override
            protected void configure() {
               bind(AWSEC2ReviseParsedImage.class).to(FooAWSEC2ReviseParsedImage.class);
            }
            
         }), overrides);
{% endhighlight %}

#### Use your image version
jclouds will now pick the lexicographic highest version, as it now can parse your images.

{% highlight java %}
Template template = context.getComputeService().templateBuilder().imageVersionMatches("1.1.0.*").build();
{% endhighlight %}

## ComputeService API extensions
Power users have requested more control over the choices jclouds ComputeService makes when provisioning nodes. 
By default, we automatically create a security group and keypair for your nodes before launching them. 
We now allow you to control this a bit through extended template options.

## Spot Instances
                                                                                                                                                                                          
If you are using the `aws-ec2` provider, you can use spot instances via the spotPrice parameter on template options:                                                                      
{% highlight java %}                                                                                                                                                                                      
options.as(AWSEC2TemplateOptions.class).spotPrice(0.3f);                                                                                                                                  
{% endhighlight %}                                                                                                                                                                                   
AWSEC2ComputeServiceLiveTest.testExtendedOptionsAndLogin() uses the spot price option on the portable interface.                                                                          
                                                                                                                                                                                          
### Details                                                                                                                                                                           
In the code, AWSEC2CreateNodesInGroupThenAddToSet actually manages creating the spot instance request.  The way jclouds manages this is that it looks for both regular reservations and a\
so spot requests when listing nodes.  The convergence of these 2 is what you'll see in a listNodes command (AWSEC2ListNodesStrategy ex. does these multiple listings in parallel)         
                                                                                                                                                                                          
                                                                                                                                                                                          
If there's an error on the parameters requesting nodes, you'll receive an HttpResponseException in the 400 range from ec2 itself.  The other condition you should be aware of is when the\
spot request goes through, but perhaps takes longer to provision the nodes than the jclouds default timeout (jclouds.compute.timeout.node-running), something I've not seen.              


### Security Groups
{% highlight java %}
// specify your own groups which already have the correct rules applied
template.getOptions().as(EC2TemplateOptions.class).securityGroups(group1, group2);
{% endhighlight %}
### Key Pairs
{% highlight java %}
// specify your own keypair for use in creating nodes
template.getOptions().as(EC2TemplateOptions.class).keyPair(group);

// if your image doesn't use keypairs (ex enstratus), skip creating one
template.getOptions().as(EC2TemplateOptions.class).noKeyPair();

{% endhighlight %}

### VPC
To create nodes in a subnet under Amazon VPC add the following option to your template options.  Note that VPCs
 and Security Groups are mutually exclusive.

{% highlight java %}
TemplateOptions options = compute.templateOptions();
options.as(AWSEC2TemplateOptions.class).subnetId(subnetId);

Set<? extends NodeMetadata> nodes = client.runNodesInGroup(group, 1,
               options);
{% endhighlight %}

You can also checkout `EC2ComputeServiceListTest.testExtendedOptionsWithSubnetId()`

### Monitoring (Cloud Watch)

_Note this is currently only in 1.0-SNAPSHOT_

To create nodes that are automatically monitored in CloudWatch, add the following to your template options:

{% highlight java %}
TemplateOptions options =  compute.templateOptions();
options.as(EC2TemplateOptions.class).enableMonitoring();

Set<? extends NodeMetadata> monitoredNodes = compute.runNodesInGroup(group, 1, options);

{% endhighlight %}

You can then use the !CloudWatchClient to get statistics on your nodes.
{% highlight java %}
RestContext<CloudWatchClient, CloudWatchAsyncClient> cloudWatchContext =
       new RestContextFactory().createContext("cloudwatch",  accessid, secretkey);

String region = node.getLocation().getParent().getId();

Set<Datapoint> datapoints = monitoringContext.getApi().getMetricStatisticsInRegion(region,
                  "CPUUtilization", before, new Date(), 60, "Average");
{% endhighlight %}

