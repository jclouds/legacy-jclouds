---
layout: jclouds
title: Quick Start - Amazon Web Services
---

# Quick Start: Amazon Web Services

This page helps you get started with `jclouds` API with Amazon Web Services

1. Sign up for [Amazon Web Services Signup](https://aws-portal.amazon.com/gp/aws/developer/registration/index.html).
2. Get your Access Key ID and Secret Access Key by going to this [page](https://aws-portal.amazon.com/gp/aws/developer/account/index.html?ie=UTF8&action=access-key).
3. Ensure you are using a recent JDK 6 version. 
4. Setup your project to include aws-ec2 and aws-s3.
	* Get the dependencies `org.jclouds.provider/aws-ec2` and `org.jclouds.provider/aws-s3` using jclouds [Installation](/documentation/userguide/installation-guide).
5. Start coding

## Using S3

{% highlight java %}
import static 
	org.jclouds.aws.s3.options.PutObjectOptions.Builder.withAcl;

// get a context with amazon that offers the portable BlobStore API
BlobStoreContext context = new BlobStoreContextFactory().
                     createContext("aws-s3", accesskeyid, secretkey);

// create a container in the default location
BlobStore blobStore = context.getBlobStore();
blobStore.createContainerInLocation(null, bucket);

// add blob
Blob blob = blobStore.newBlob("test");
blob.setPayload("test data");
blobStore.putBlob(bucket, blob);

// when you need access to s3-specific features,
// use the provider-specific context
AWSS3Client s3Client =
     AWSS3Client.class.cast(context.getProviderSpecificContext().getApi());

// make the object world readable
String publicReadWriteObjectKey = "public-read-write-acl";
S3Object object = s3Client.newS3Object();

object.getMetadata().setKey(publicReadWriteObjectKey);
object.setPayload("hello world");
s3Client.putObject(bucket, object, withAcl(CannedAccessPolicy.PUBLIC_READ));

context.close();
{% endhighlight %}

## Using EC2

{% highlight java %}
// get a context with ec2 that offers the portable ComputeService API
ComputeServiceContext context =
                new ComputeServiceContextFactory().createContext("aws-ec2",
                                                                accesskeyid,
                                                                secretkey,
                                                                ImmutableSet.<Module> of(new Log4JLoggingModule(),
                                                                new SshjSshClientModule()));

// here's an example of the portable api
Set<? extends Location> locations =
        context.getComputeService().listAssignableLocations();

Set<? extends Image> images = context.getComputeService().listImages();

// pick the highest version of the RightScale CentOS template
Template template = context.getComputeService().templateBuilder().osFamily(OsFamily.CENTOS).build();

// specify your own groups which already have the correct rules applied
template.getOptions().as(AWSEC2TemplateOptions.class).securityGroups(group1);

// specify your own keypair for use in creating nodes
template.getOptions().as(AWSEC2TemplateOptions.class).keyPair(keyPair);

// run a couple nodes accessible via group
Set<? extends NodeMetadata> nodes = context.getComputeService().createNodesInGroup("webserver", 2, template);

// when you need access to very ec2-specific features, use the provider-specific context
AWSEC2Client ec2Client = AWSEC2Client.class.cast(context.getProviderSpecificContext().getApi());

// ex. to get an ip and associate it with a node
NodeMetadata node = Iterables.get(nodes, 0);
String ip = ec2Client.getElasticIPAddressServices().allocateAddressInRegion(node.getLocation().getId());
ec2Client.getElasticIPAddressServices().associateAddressInRegion(node.getLocation().getId(),ip, node.getProviderId());

context.close();
{% endhighlight %}


