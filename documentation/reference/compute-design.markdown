---
layout: jclouds
title: Design of jclouds compute abstraction
---
# Design of jclouds compute abstraction

## Introduction

jclouds goal is to provide simple tools developers can use to access resources in the cloud.  
We present both abstraction and value-add apis, making it the user's choice whether to lock-in to a
vendor's value-added feature, or avoid it with abstraction.  

We also provide developers choice to either wait for the provider to finish a command (blocking), or 
perform them in the background (async).  We do this in a way that works in many environments, including 
google appengine and android.    To date, we provide over 10 cloud apis and an abstraction called BlobStore. 
BlobStore offers Amazon S3-like portability across a few different cloud storage providers.    

We've had high demand for a compute answer.  This document discusses our current thinking on that problem.

In the context of this design, compute basically means managing elastic operating systems.  
These could end up as Windows, or Linux, and their goal is to run platforms or applications.  
Most of the time, these end up implemented as virtual machines, but this design doesn't really care
 if they are bare metal or not.  What's cloud is that these operating systems are requested on-demand and 
are only billed for the time they are running.

## Motivation

There are a lot of service providers that provide this type of IaaS functionality, 
ones currently supported by jclouds are [Amazon EC2](http://aws.amazon.com/ec2/), 
[Rackspace Cloud Servers](http://www.rackspacecloud.com/cloud_hosting_products/servers), 
[VMware vCloud API](http://www.vmware.com/appliances/services/vcloud-express.html), and
[RimuHosting VPS](http://rimuhosting.com/vps/aboutvps.jsp).  

For better or worse, each of these services model their systems in different ways and
 therefore have different APIs to access them.  In some cases, there is large overlap in features.  
For example, there is generally an idea of an Image, which is basically a template operating system with 
layered software which can be launched many times.  Some areas, there's less clear overlap. 

For example, EC2 has a InstanceType, which declares the number or cores, amount of memory, etc.  
In other clouds, such as those running the VMware vCloud API, the user chooses the number of cores, memory, etc.  
Very often, services truly differentiate.  One example of this is Rackspace, who provides a scheduled backup service.

Even with differentiation, there are still enough overlaps to make useful abstractions across providers.  
Abstractions help avoid lock-in by presenting portable APIs that massage differences away. 
There are a few good software projects that provide compute portability, 
including [deltacloud](http://incubator.apache.org/deltacloud/) (ruby and ReST), 
[fog](http://github.com/geemus/fog)(ruby), and [Dasein](http://www.dasein.org/)(java).

This design does not replace or preclude any of the above.  Some of the apis above have scope much larger 
than this design.  For example, Dasein covers features including networking technology.  
jclouds has a different development, integration, and usage focus.  

For context, we'll these here:

  * jclouds is first and foremost a JVM story.  We focus on being useful from Java, Groovy, Scala, JRuby, Clojure, and
 	any other JVM based language.  Java focus allows us to present advanced features such as Futures and
 	pluggable configuration without the user having to deal with how these actually work.

  * jclouds is an integration platform.  We encourage other tools, like Dasein and ShrinkWrap, 
	to integrate with us.  We don't want to divert focus and attempt to solve 
	every cloud problem down to the gui.  Ecosystems matter.  We want our design end up simple, 
	but feature-rich enough to build platforms on.
	
  * jclouds wants to allow service providers to present value-added features.  
	Abstractions are nice, but often too limited to solve the specific task of the user.  
	We do not want to discourage innovation by precluding access to vendor apis.  
	Every abstraction we present also presents the native vendor functionality.  
	This allows the developer choice to make the right call.  
	If you can do 90% with abstraction, great!  That said, we don't want you to have to learn and
	 manage a different framework to get to that last 10%.

As said above, shared vocabulary is important. Where it makes sense, we'll try 
to use vocabulary present in other cloud apis such as Apache Deltacloud, Fog, Dasein, and vCloud.  
Using the same vocabulary will widen the understanding of jclouds compute abstraction and
 easily show similarities/differences.

Considering the above, we have the following goals:

   * make it easy to manage the lifecycle of compute nodes in the cloud by
 	reducing the provider specific details required to operate.

   * provide clear integration paths for open source tools such as ant, maven, hudson, cargo, and controltier

   * model location as a first class concept, where location is a virtual datacenter, or cloud provider endpoint.

We have extended goals of:

   * make it easy to map jclouds concepts to concepts present in other cloud apis
   * model operating systems in a more resource oriented way

The compute design is based on the above context and the knowledge of the many involved.  
At the time of this writing, we are in first draft state.  Please get involved and participate.  
This is a community design.

## Design 
### Vocabulary 
For this design, we have the current working vocabulary. 

  * *ComputeService* - a service level api endpoint such as EC2.  
	Contains an inventory of of Hardware profiles, Images, and Nodes.  
	Also contains the ability to create and destroy Nodes and resolve Templates.
	
  * *Provider* - Runs a compute provisioning api such as vCloud, EC2, etc.  
	May run multiple endpoints (ex. west, east, north).  Context is generally bound to a provider and 
	a unique identity within it.
	
  * *Image* - A pre-configured operating system representing the base software a new Node will run.  
	This is a part of the template used to create nodes.
	
  * *Hardware* - a set of resource configuration that includes at least memory, cpu, and disk.  
	This size may act on an "at least" principle which guarantees at least the resources listed profile, 
	but possibly more. This is a required input to Server creation.
	
  * *Location* - an assignable physical or logical location inside a provider where you can launch nodes.  
	Location can be scoped as Provider, Region, Zone, etc, so that you can determine its precision.  
	This is often a virtual or physical datacenter. This is a required input to Server creation.
	
  * *TemplateOptions* - options for creations of resources, including ports to open, scripts 
	to run at bootstrap.  Can be extended per provider to expose vendor hooks.
	
  * *Template* - Composite if Image, Hardware, Location and TemplateOptions for Node creation.  
	This allows for repeated creation of Nodes.
	
  * *Node* - An named instance of a Template.  Sometimes called a server, vApp, or virtual machine.  
	This typically has login, ip, and security metadata.  
	
  * *Group* - A name that aggregates nodes and related incidental resources such as keys, 
	so that you can control groups of nodes as a unit.  As many clouds do not support multiple groups, 
	our use of group implies a primary grouping.
	
### Platform Extension

Via extension, the NodeService has means to create Instances of Platforms on Nodes, and may do so by default.

  * *Platform* - an archive of bundled code and configuration that presents a value-added service to applications.  
	Examples include application servers like Tomcat, a database like MySql, network memory like Terracotta.
	
  * *Instance* - a running instance of a Platform, typically associated with a process on the host operating system.  
	Metadata typically includes at least environment variables and often ip socket data.

## Node Service 
### Resource-Oriented as opposed to OO

Providing a resource-oriented interface as opposed to 
an object oriented one allows outside integrators the following flexibility:

  * Create their own OO interfaces that may not match ours

  * Simple access to both synchronous and asynchronous apis, without the need for mirrored object trees.

  * Ability to easily stand up a Rest service that simply delegates to jclouds calls.

## Node
### State Machine

Node is a view of a host's running operating system with state.  
Whether or not you are personally connected to it, the node exists in some state from 
the time it is created until someone invokes the destroy method.  
When a node is created, it is implicitly started.  This is due to the reality 
that most providers start their nodes when they are created, and there is no way to disable this.

Like other jclouds components, this will have asynchronous calls (*note async is not implemented, yet*).  
However, we will have helpers for calls that always take a long time.  

Here's what creating and starting an Node might look like if you let jclouds handle reasonable timeouts:

{% highlight java %}
// setup an option to wait up to 120 seconds for port 22 to open
TemplateOptions options = computeService.templateOptions().blockOnPort(22, 120);
// run a single node accessible via the group "foo"
Set<? extends NodeMetadata> nodes = computeService.runNodesInGroup("foo", 1, options);
{% endhighlight %}

## Template 

A template provides an ability to encapsulate the implementation details corresponding to a user's desires.  
For example, a user may desire the cheapest node configuration in dallas.  `computeService.templateBuilder()` would accept
 arguments to return the best match of this request.  The Template is then used to create a node, for example:

{% highlight java %}
Template template = computeService.templateBuilder().hardwareId(InstanceType.M1_SMALL)
              .osVersionMatches("10.04").imageDescriptionMatches("ubuntu-images").osFamily(OsFamily.UBUNTU).build();

// run 5 nodes accessible with the group "webserver"
Set<? extends NodeMetadata> nodes = computeService.runNodesInGroup("webserver", 5, template);

{% endhighlight %}

As you can see above, Profile allows users to use cloud-specific lookups while at the same time use the portable interface.  
It also allows you to create multiple nodes.

## Use Cases

_please extend!_

### Provisioning

  * specifying Profiles
  * creating and starting Servers with specified Profiles and Images
  * specifying Platforms
  * creating Instances of Platforms on existing Servers
  * suspending and stopping Instances and Servers
  * scheduling the creation, starting, suspending or stopping of Instances or Servers

### Monitoring

  * retrieve information on running Nodes (i.e. mainly "low-level" info)
  * retrieve information on running Instances (i.e. data specific to the software in the Platform running on the Instance)
  * query information across a set of Instances or Nodes (e.g. "show me all nodes with a CPU load > X")
  * request to be notified if a certain Node or Instance metric reaches a defined threshold value

The above are fairly "fine-grained" use cases, and as such it would appear helpful to add some higher-level context in terms of applications or scenarios in which these Use Cases might occur.

  * Provisioning as part of the build cycle, e.g. from [Ant](http://ant.apache.org/), [Cargo](http://cargo.codehaus.org/) or [Hudson](https://hudson.dev.java.net/)
  * Monitoring from existing infrastructure monitors, e.g. [Nagios](http://www.nagios.org/about/)
  * Monitoring from within a cloud application
  * Monitoring and auto-provisioning from an infrastructure management application, e.g. a [hypervisor](http://en.wikipedia.org/wiki/Hypervisor) or [RightScale](http://www.rightscale.com/)
  * Monitoring and auto-provisioning from an application platfrom, e.g. [GigaSpaces]http://www.gigaspaces.com/)


## Platform Extension 

`Note this isn't implemented as of jclouds 1.0.0`

## Container 

The node has operations, but also acts as a container for Platforms and Instances of them.   
Here's what that might look like in java:

{% highlight java %}
if (!node.containsPlatform("mybilling-1.0.1")){
   Archive billingArchive = // from archive
   Future<PlatformMetadata> = service.createPlatform(node.getId(), "mybilling-1.0.1", archive, readOnly().onSharedMount(volume));
   // validate  
}

SortedSet<InstanceMetadata> billingInstances = service.listInstances(node.getId(), platform("mybilling-1.0.1").groups("production"));

if (billingInstances.size() <5){
  Future<Instance>  = service.createInstance(node.getId(), "mybilling-1.0.1", groupAs("production"));
  //  check it, etc.
}

{% endhighlight %}

