---
layout: jclouds
title: Quick Start - Eucalyptus
---

# Quick Start: Eucalyptus

1. Install Eucalyptus or signup for the [ECC cloud](https://ecc.eucalyptus.com:8443/#login)
2. Get your Access Key ID and Secret Access Key
3. Ensure you are using a recent JDK 6
4. Setup your project to include `jclouds-aws`
	* Get the dependency `org.jclouds.api/eucalyptus` or `org.jclouds.api/walrus` using jclouds [Installation](/documentation/userguide/installation-guide).
5. Start coding

### Using Walrus

{% highlight java %}
import static org.jclouds.aws.s3.options.PutObjectOptions.Builder.withAcl;

Properties overrides = new Properties();
overrides.setProperty("walrus.endpoint", "http://ecc.eucalyptus.com:8773/services/Walrus");

//get a context with amazon that offers the portable BlobStore api
BlobStoreContext context = new BlobStoreContextFactory().createContext("walrus", accesskeyid, secretkey,
	ImmutableSet.<Module> of(new Log4JLoggingModule()), overrides);

// create a container in the default location
context.getBlobStore().createContainerInLocation(null, bucket);
BlobStore blobStore = context.getBlobStore();

// create container
blobStore.createContainerInLocation(null, bucket);

// add blob
Blob blob = blobStore.newBlob("test");
blob.setPayload("test data");
blobStore.putBlob(bucket, blob);

// when you need access to s3-specific features, use the provider-specific context
S3Client s3Client = S3Client.class.cast(context.getProviderSpecificContext().getApi());

// make the object world readable
String publicReadWriteObjectKey = "public-read-write-acl";
S3Object object = s3Client.newS3Object();
object.getMetadata().setKey(publicReadWriteObjectKey);
object.setPayload("hello world");
s3Client.putObject(bucket, object, withAcl(CannedAccessPolicy.PUBLIC_READ));

context.close();
{% endhighlight %}

### Using Eucalyptus

{% highlight java %}
// get a context with eucalyptus that offers the portable ComputeService api
Properties overrides = new Properties();
overrides.setProperty("eucalyptus.endpoint", "http://173.205.188.130:8773/services/Eucalyptus");
ComputeServiceContext context = new ComputeServiceContextFactory().createContext("eucalyptus", accesskeyid ,secretkey,
                          ImmutableSet.<Module> of(new Log4JLoggingModule(), new JschSshClientModule()), overrides);

// here's an example of the portable api
Set<? extends Image> images = context.getComputeService().listImages();

// pick the highest version of the RightScale CentOs template
Template template = context.getComputeService().templateBuilder().osFamily(OsFamily.CENTOS).build();

// specify your own groups which already have the correct rules applied
template.getOptions().as(EC2TemplateOptions.class).securityGroups(group1);

// specify your own keypair for use in creating nodes
template.getOptions().as(EC2TemplateOptions.class).keyPair(keyPair);

// run a couple nodes accessible via group
Set<? extends NodeMetadata> nodes = context.getComputeService().runNodesInGroup("webserver", 2, template);

// when you need access to very ec2-specific features, use the provider-specific context
EC2Client ec2Client = EC2Client.class.cast(context.getProviderSpecificContext().getApi());

// ex. to attach a volume to a node
NodeMetadata node = Iterables.get(nodes, 0);
Attachment attachment = ec2Client.getElasticBlockStoreServices().attachVolumeInRegion(null, volumeId, node.getLocation().getId(), device);

context.close();
{% endhighlight %}
