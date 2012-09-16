---
layout: jclouds
title: Quick Start - BlueLock vCloud Service
---

# Quick Start: BlueLock vCloud Service

**Please note that the support for BlueLock vCloud Director is not complete**

1. Sign up for [BlueLock vCloud](http://www.bluelock.com/bluelock-cloud-hosting/)
2. Ensure you are using a recent JDK 6
3. Setup your project to include bluelock-vcdirector
	a. Get the dependency `org.jclouds.provider/bluelock-vcdirector` using jclouds [Installation](/documentation/userguide/installation-guide).
4. Start coding.

{% highlight java %}

ComputeServiceContext context = 
	new ComputeServiceContextFactory().createContext("bluelock-vcdirector", 
													  "username@orgname", 
													   password,
    												  ImmutableSet.<Module> of(new JschSshClientModule()));


// create a customization script to run when the machine starts up 
String script = "cat > /root/foo.txt<<EOF\nI love candy\nEOF\n";
TemplateOptions options = client.templateOptions();
options.as(VCloudTemplateOptions.class).customizationScript(script);

// run a couple nodes accessible via group
nodes = context.getComputeService().createNodesInGroup("webserver", 2, options);

// get a synchronous object to use for manipulating vcloud objects in BlueLock
VCloudClient client = 
	VCloudClient.class.cast(context.getProviderSpecificContext().getApi());

// look at all the thumbnails of my vApps
VApp app = client.getVApp(vApp.getHref());

for (Vm vm : app.getChildren()) {
     assert client.getThumbnailOfVm(vm.getHref()) != null;
}

// release resources 
context.close();

{% endhighlight %}

6. Validate on the [BlueLock](https://vcenterprise.bluelock.com/cloud/)

