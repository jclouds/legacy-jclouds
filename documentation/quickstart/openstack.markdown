---
layout: jclouds
title: Quick Start - OpenStack
---

# Quick Start: OpenStack Nova

`Note: This page is a work in progress and will be updated often. Check back later if something is missing.`

1. Install OpenStack, or get access to a running instance
  * [Compute(nova)](http://www.openstack.org/projects/compute/latest-release/). At least Cactus release with OpenStack API v1.1 is required.
2. Get credentials for the OpenStack API
  * You will require a Username and API Key to use the OpenStack API. 
	The valid credentials are in novarc file generated for your Openstack project with `nova-manage project zipfile` command.
	 API endpoint URL is in this file as well.
3. Ensure you are using a recent JDK 6
  * There is a bug in HotSpot JDK v1.6.18 HTTP library. Host header of the HTTP request omits port of the remote service sometimes.
 Correct information in the Host header is required during the authentication part. Just use the recent JDK.
4. Setup your project to include nova
	The maven dependency `org.jclouds.api/nova` will add the following jar files to your project:
  	* jclouds-core
  	* jclouds-compute
  	* openstack-common
  	* nova

Instructions for downloading the dependencies are available in the [Installation](/documentation/userguide/installation-guide).

5. Start coding using Nova

{% highlight java %}
import static org.jclouds.openstack.nova.options.CreateServerOptions.Builder.withFile;
import static org.jclouds.openstack.nova.options.ListOptions.Builder.*;
import org.jclouds.Constants;
{% endhighlight %}

{% highlight java %}
Properties overrides = new Properties();
//point the JClouds to the Openstack v 1.1 API endpoint URL
//for swift version 1.4.4 the URL is of the form: http(s)://ip:port/auth
overrides.setProperty(Constants.PROPERTY_ENDPOINT, "https://authurl");

/ get a context with nova that offers the portable ComputeService api
ComputeServiceContext context = new ComputeServiceContextFactory().createContext("nova", user, apiKey, 
													ImmutableSet.<Module> of(new JschSshClientModule()),
														 overrides);

// here's an example of the portable api

// run some nodes
nodes = context.getComputeService().client.runNodesInGroup("webserver", 2);

// when you need access to nova-specific features, use the provider-specific context
NovaClient novaClient = NovaClient.class.cast(context.getProviderSpecificContext()
         .getApi());


// Test this code on recent build!
// create a server with a new file called /etc/jclouds.txt and some metadata
Map<String, String> metadata = ImmutableMap.of("jclouds", "nova");
int imageId = 2;
int flavorId = 1;
Server server = novaClient.createServer("myservername", imageId, flavorId,
      withMetadata(metadata));

// list all of my servers including details such as metadata
List<Server> servers = novaClient.listServers(withDetails());

// list the id and name of my servers that were modified since yesterday
servers = novaClient.listServers(changesSince(yesterday));

// list the id and name of images I have access to, starting at index 200 limited to 100 results.
List<Image> images = novaClient.listImages(startAt(200).maxResults(100));

context.close();
{% endhighlight %}



## Known Issues
<!-- TODO Change let us know link -->
This provider is a work in progress. Some issues remain open and are described here. If you are missing some features,
 [let us know](http://groups.google.com/group/jclouds?pli=1) Or just [fork](https://github.com/jclouds/jclouds) the code 
on github and scratch that itch yourself.

* No public IP on current Cactus Release: The current version of Openstack API has a shortcoming. 
  Nova Compute system boots the nodes without a public IP if the vlan network topology is used. 
  This makes them unreachable from the outside world. And there is no way to assign the public IP to a node using
   the Openstack API. There is a patch which adds the additional `auto_assign_floating_ip` parameter to the Nova config. 
  When this parameter is set to true Nova automatically assigns theIP from the pool of floating IPs to a node after its startup.
   This patch has already been submitted to OpenStack. Just take the nighty builds of the Openstack Compute or 
  use Openstack Cactus packages for RHEL from [here](http://yum.griddynamics.net/)which is also include this patch.
  
* Some features are currently not working if KVM hypervisor is used but may work on other hypervisors supported by OpenStack. 
  It looks that Openstack does not support that features on KVM.
  * root password returned in create server call is not the right password.
  * image customization by uploading additional files before the instance creation is not working
  * injection of an SSH key from the JClouds is not working (but working for the registered user's default key)

* Request to create image from a running instance returns incorrect JSON output. Possibly a bug in OpenStack.

* If the node in running status does not have the public IP addresses yet and have only 
  private address (the public addresses can be assigned a bit later after the node status is changed to running)
  then some features of the JClouds are not working, namely:
  
* A script can't be executed as a part of node creation process (`TemplateOptions.runScript` attribute). 
  The node is pinged using the private address and then the node creation process exits on timeout.
  * Node creation process can't wait until the ports on node are opened (`TemplateOptions.blockOnPort` attribute). 
    The reason is the same - private IP address is used.
  * Flavor reference is not returned in servers/details/list output by Openstack 
    (but is returned if the instance details are requested directly in servers/id/details call) and 
    therefore sometimes node metadata does not have the correct hardware info.

* The format of the endpoint URL has changed since version 1.4.4 of swift.
  It is now of the form: http(s)://ip:port/auth
  In previous versions 'auth' was not required.

