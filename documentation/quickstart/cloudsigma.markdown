---
layout: jclouds
title: Quick Start - Cloud Sigma
---

# QuickStart: Cloud Sigma

1. Signup for [CloudSigma](https://cs.cloudsigma.com/accounts/signup/)
2. Ensure you are using a recent JDK 6
3. Setup your project to include `cloudsigma-zrh`
	* Get the dependency `org.jclouds.provider/cloudsigma-zrh` using jclouds [Installation](/documentation/userguide/installation-guide).
4. Start coding

{% highlight java %}
// get a context with ibm that offers the portable ComputeService api
ComputeServiceContext context = new ComputeServiceContextFactory().createContext(
                    "cloudsigma-zrh", email, password,
                    ImmutableSet.<Module> of(new JschSshClientModule()));

// run a couple nodes accessible via group webserver
nodes = context.getComputeService().client.runNodesInGroup("webserver", 2);

// While the portable context is easier, you can always perform the same commands manually,
// using the provider-specific context.
CloudSigmaClient client = CloudSigmaClient.class.cast(context.getProviderSpecificContext().getApi());

// clone a drive from another drive
DriveInfo drive = client.cloneDrive("source-id", "name", size(sizeInBytesYesBytes));

// Note you'll have to block until the drive has no exclusive lock before starting your server.  Here's how I do it:
boolean success =  new RetryablePredicate<DriveInfo>(Predicates.not(new DriveClaimed(client)), maxDriveImageTime, 1, TimeUnit.SECONDS).apply(drive);

// After your boot disk is up, you can create a server with it as below. Names needn't be unique.:
Server toCreate = Servers.small(name, drive.getUuid(),defaultVncPassword).mem(ramMB).cpu(mhz).build();
ServerInfo newServer = client.createServer(toCreate);

// release resources 
context.close();
{% endhighlight %}

5. Validate on the [CloudSigma console](https://cs.cloudsigma.com)
