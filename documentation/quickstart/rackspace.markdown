---
layout: docs
title: Quick Start - Rackspace Cloud
---

# Quick Start: Rackspace Cloud

1. Sign up for Rackspace Cloud by going to this [page](https://www.rackspacecloud.com/signup)
2. Login to the portal and get your username and key from [the management portal](https://manage.rackspacecloud.com/Login.do)
3. Ensure you are using a recent JDK 6
4. Setup your project to include cloudfiles-us,-uk or cloudservers-us,-uk
	get the dependency `org.jclouds.provider/cloudfiles-us`, `org.jclouds.provider/cloudfiles-uk`, 
	`org.jclouds.provider/cloudservers-us`, or `org.jclouds.provider/cloudservers-uk`
	using jclouds [Installation](/documentation/userguide/installation-guide).
5. Start coding

## Cloud Files

{% highlight java %}
// get a context with rackspace that offers the portable BlobStore api
BlobStoreContext context = new BlobStoreContextFactory().createContext("cloudfiles-us", user, apikey);

// create a container in the default location
context.getBlobStore().createContainerInLocation(null, container);

// use the map interface for easy access to put/get things, keySet, etc.
context.createInputStreamMap(container).put("blob.txt", inputStream);

// when you need access to rackspace-specific features, use the provider-specific context
CloudFilesClient rackspaceClient = CloudFilesClient.class.cast(context.getProviderSpecificContext()
         .getApi());

// get a cdn uri for the container
URI cdnURI = rackspaceClient.enableCDN(container);

context.close();
{% endhighlight %}

## Cloud Servers
{% highlight java %}
import static org.jclouds.cloudservers.options.CreateServerOptions.Builder.withFile;
import static org.jclouds.cloudservers.options.ListOptions.Builder.*;

// get a context with rackspace that offers the portable ComputeService api
 ComputeServiceContext context = new ComputeServiceContextFactory().createContext("cloudservers-us", user, apikey,
                                                         ImmutableSet.<Module> of(new SshjSshClientModule()));

 // here's an example of the portable api

 // run a couple nodes accessible via group
 Set<? extends NodeMetadata> nodes = context.getComputeService().createNodesInGroup("webserver", 2);
 
 // when you need access to rackspace-specific features, use the provider-specific context
 CloudServersClient rackspaceClient = CloudServersClient.class.cast(context.getProviderSpecificContext()
          .getApi());
 
 
 // create a server with a new file called /etc/jclouds.txt and some metadata
 Map<String, String> metadata = ImmutableMap.of("jclouds", "rackspace");
 int imageId = 2;
 int flavorId = 1;
 Server server = rackspaceClient.createServer("myservername", imageId, flavorId,
        withFile("/etc/jclouds.txt", "rackspace".getBytes()).withMetadata(metadata));

 // list all of my servers including details such as metadata
 Set<Server> servers = rackspaceClient.listServers(withDetails());

 // list the id and name of my servers that were modified since yesterday
 servers = rackspaceClient.listServers(changesSince(yesterday));

 // list the id and name of images I have access to, starting at index 200 limited to 100 results.
 Set<Image> images = rackspaceClient.listImages(startAt(200).maxResults(100));

 context.close();
{% endhighlight %}