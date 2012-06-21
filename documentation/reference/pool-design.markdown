---
layout: jclouds
title: Discussion of a Pool provider Design
---
# Discussion of a Pool provider Design

# Introduction 

This design will overview our approach to creating a scalable pool of nodes, backed by any IaaS provider.  
This will be used by PaaS providers, test automation, analytics, nosql, data grids, elastic middleware, and 
other projects or products that require medium-size elastic infrastructure.

# Goals
## Scale 
The goal is to manage at least 100 nodes of at least 1GB ram each.  Management of 20 nodes is the minimum requirement.

## Elasticity
Provider-limited, we'd like elasticity movements in groups of up to 20 nodes.

## No service dependency
It should be a java api, and not require maintenance of an external service outside the cloud controller.  
To this end, it should be possible to run as a library even inside Google !AppEngine, where threads are prohibited.  
Optimizations can optionally employ extra services, such as BlobStore.

# Appendix 
## Relevant Mailing list discussions 

[discussion of this design kick-off](https://groups.google.com/group/jclouds-dev/browse_thread/thread/363d7a429c95ca5b?hl=en)

## Request to Service Providers and Cloud Controllers

I'm looking to put together an set of expectations as we design our pool provider.  
Main thing of interest is to understand what limits exist for each of your IaaS offerings 
(broken down by class, geography, if differ) with regards to creating servers on say 2 scales: default limits and power limits.  
I'll define power user as the level of scale easily requestable to extend to, and hope to get info on 
the link or email address to send requests to extend via.

We'd like to break this down technically to the limitations of simple scale (ex. fire parallel requests and hope for the best), 
and what can be achieved via optimizations.  Let's normalize to a 1GB VM size, with an arbitrary amount of cores.

For example, some ask to reduce the number of POSTs by sending aggregate requests, 
others say reduce rate of requests to 1/10 seconds or so, yet others request to query 
an api or inspect response status for rate info.  Sometimes, there's a process or strategy that can be employed, such as pre-allocating disks.

Here's an example of the data, which of course you can answer only what you are comfortable answering, and those which are relevant.

{% highlight text %}
service provider: cloud2cloud

offering: enterprise-de
cloud controller: in-house java backend over ESXi & KVM
default limits for server creation: 2/hour 20/max
power limits for server creation: 20+/minute 100/max
request to extend limits: http://cloud2cloud.de/cloudup
simple scale/customer: 10 servers launched in 3 minutes
optimized scale/customer: 100 servers launched in 10 minutes
optimization strategy: pre-instantiate KVM-backed vms in stopped
state, then you can start 100 concurrently, if using only private
addresses, 20 if public addresses

offering: express-uk
cloud controller: in-house java backend over KVM
default limits for server creation: 20+/minute 100/max
power limits for server creation: same
request to extend limits: http://express.cloud2cloud.co.uk/cloudup
simple scale/customer: 20 servers launched in 3 minutes
optimized scale/customer: same
optimization strategy: n/a
{% endhighlight %}

This information will go into a public wiki and become inputs to our design of how to start pools that 
can ideally supply >20 nodes potentially many more per cloud.  
We will want to test this, at least up to a scale of 20 machines.  [[Customers|AppsThatUseJClouds]] of this design will
likely include nosql users such as !BackType and Twitter, load test platforms such as Neotys, PaaS providers like 
CloudBees and OpenShift Flex (Makara), and middleware like Cloudsoft & GigaSpaces, not to mention 
open source tools like jgroups, whirr and pallet.

## Feedback from IaaS Controllers and  Service Providers
### IaaS Software 
#### Cloud.com !CloudStack
Source: Kevin Kluge

The CloudStack supports creating hundreds of VMs in parallel when provided with sufficient hardware.  
Some public cloud providers have rate limited requests to the CloudStack based on either source IP or account. 
 It's best to ask as much of the CloudStack as possible as quickly as possible.  The CloudStack will respond by 
scheduling the work in parallel across hardware as quickly as the hardware can accommodate the requests.  When 
creating a large number of VMs it can be helpful if there is already at least one VM running for the account in 
the destination zone.

### IaaS Providers
#### Rackspace Cloud Servers

Source: Matt Wilbanks

The speed in which you can create servers is gated by an absolute limit of 
[10/minute per account](http://docs.rackspacecloud.com/servers/api/v1.0/cs-devguide/content/ch03s08s01.html) and 
then your account-specific limits, which default to 50/day.  The total count of servers you can 
operate at a time is a function of their aggregate ram, set to 
[50GB](http://docs.rackspacecloud.com/servers/api/v1.0/cs-devguide/content/ch03s08s02.html) by default.  
So, with the default limits, you should be able to start 50 1GB servers by starting 10 each minute across 5 minutes.  
Support suggests not starting these concurrently and instead spreading them equally over that duration, 
for example every 6 seconds.  This will lower the error rate on the server by giving the backend provisioner 
time to place the servers on different machines where possible.

The limits of 50 server creations/day and 50GB per account are arbitrary and there only to prevent 
fraud, hackers, and spammers from abusing accounts.  You can submit a request to increase these limits via 
the support portal, phone, or chat, or email.  Please suggest your expected capacity so they can plan to 
have infrastructure available and/or technical support ready to help should you need it.  For example, 
you could ask to increase to 250GB/250 server launches/day.  Keep in mind that the rate of provisioning 
will still be at 10/minute, so if your servers take 2 minutes to boot, you can expect to have
 250 servers available within 27minutes.

Limits apply equally to rackspace us and uk.  The global limit of 10 servers/minute is not likely to change soon. 
 If it does, it would coincide with !OpenStack deployment.

If you need to launch a large amount of servers, you have another option, 
which is to create multiple accounts and launch servers simultaneously striping across your set of accounts.  
For example, using the 250 servers scenario, you could split this across 5 accounts, 
then lowering the total duration from 27 minutes to about 7 minutes.  
Make sure you request to remove the api and ram constraints equally across all of your accounts.

#### BlueLock vCloud Director
Source: John Ellis

We're mostly constrained by the limits imposed by vCloud Director and vSphere, 
which are largely relegated to x concurrent deployments per ESXi node and y depth of the deployment queue.

#### RimuHosting
Source: Peter Bryant
<!-- TODO Table -->
||offering||LaunchtimeVPS||
||cloud controller||in-house java controller over Xen implementation. Servers in Dallas, Australia, NZ and London||
||default limits for server creation||2/hr||
||power limits for server creation||20/hr||
||request to extend limits||support ticket||

#### Amazon EC2
Source: Jeff Barr

We don't publish rate limits because they are often dynamic based on a combination of factors. 
Most of our APIs will return an appropriate HTTP error code if they are being invoked too frequently.

As far as instance creation, each EC2 account is initially limited to 20 instances. 
As they approach this limit they can request a larger limit using the form at http://aws.amazon.com/contact-us/ec2-request/ . 
We turn these around fairly quickly. Over time, customers can grow to the point where they are 
using hundreds or even thousands of instances or cores.

Because each launch of an EC2 instance references a customer-provided machine image, 
I can't say anything definitive about launch time. I have seen bare-bones images launched and 
ready to use in less than 30 seconds.

EC2 will queue up large requests as needed. Here’s an extreme case:
 
http://blog.cyclecomputing.com/2011/04/single-click-starts-a-10000-core-cyclecloud-cluster-for-1060-hr.html
 
As the blog notes, they had their first 2000 cores within 15 minutes and all 10000 of them within 45. 
Lots of factors feed into this and there are no real rules of thumb.

Each EC2 instance is assigned a public IP address that lasts for the lifetime of the instance.

## BYON Requirements

To simplify the usage of a pre-allocated pool of nodes, it would be useful to extend the current 
byon provider to allow it to operate as a very simple cloud controller. 

## Node allocation 

The concept would be to have the pool of nodes initially unassigned to any group (or assigned to some "unassigned" group).  
The jclouds  api calls to run nodes in a specific group would then take nodes matching the template from the unassigned pool, 
and assign them to the requested group. Destroying nodes for a group would return them to the unassigned pool.

Node cleanup on return to pool could be handled externally initially.

## Persistence 

The assignment of nodes to groups needs to be persisted somewhere.  
A first iteration could assume a single location for this data.  
A second iteration could allow the configuration of some form of external storage for this.

Since a major use case for byon is outside of a cloud provider context, the information should be storable 
outside of a blobstore. One idea is to use git for saving the assignments.

## Location

A single level of location would be very useful. This would allow each node to declare a location. 
The controller could be configured with a list of known locations, or the distinct location id's collected from the node declarations.

The template matching on region-id would then take the node's location into consideration.

This probably makes most sense at the Region, or availability zone level.

This is now [complete](http://code.google.com/p/jclouds/issues/detail?id=561) scheduled for 1.0-beta-10

## Working Design *OUT OF DATE*

*NOTE* this is not the intended path and needs to be revisited entirely.  
Latest word is that we don't need durability of pool state, which can significantly simplify design.


The working design is being tracked here and in 
[issue 558](http://code.google.com/p/jclouds/issues/detail?id=558) in a branch called 
[nodepool](https://github.com/jclouds/jclouds/tree/nodepool)

## Pool Design and Rationale

####  How are nodes supplied? 
	
* For starters, we can filter all nodes that are in Running state and not in the map above, placing these into a blocking queue.
		If the createNodes request is more than the size of the queue, we can start by throwing an exception.  
		Later, we can add hooks to grow, if the backing computeservice supports growth (for example, byon does not).

#### What about firewall rules? 
	
* As the rules may change from one reservation to another, we may need to add in a Firewall hook into ComputeService


### How do we manage ids? 

#### Introduction
Exposing the backend ids directly could be dangerous.  For example, one could accidentally reboot, or 
run scripts on a node they didn't reserve.  It is ideal to not require enumerating all nodes on the 
backend for a frontend pool, which may contain a subset (ex. 10 out of 1000 nodes).  

Finally, using backend ids directly requires us to block and fetch a node just to get its id, 
introducing coupling we may not want.

#### frontend id

The frontend id will be generated using a UUID mechanism to guarantee uniqueness within a frontend pool.  
This can be injected into the `NodePoolComputeServiceAdapter` via a 
Supplier of String so that we can produce expected ids during testing.  
The real implementation will likely use the java UUID class.

The frontend id is generated by the client and is the key of the Map of String -> NodeRequest

#### reservation key

Once a supplier assigns a backend node to the frontend id, it will be encoded together to make the reservation key.  
the format of this key is `frontendid->backendid`

Formatting the key in this way allows the pool consumer to collect the relevant subset of ids 
(either frontend or backend) without a large query to the backend supplier

#### example use
Suggest a client asks to reboot one of its instances.  the call the client receives is to the 
frontendid SDFDSFDS.  the `NodePool` implementation will look at the keyset of its reservations - the keyset of all tombstones. 
 it will then parse each key looking for a match to `SDFDSFDS->(.*)`  If it finds a match, 
it will pass this to the backend computeService to reboot the node.  
When there are no matches, it cannot succeed and will throw an exception.

#### How is state persisted
Reservations and transitions should be persistent.  
Current reservations will include overriding metadata relating to the 
backend node, for example: name, group, tags.  The easiest way to manage this 
is to use a Map<String,Blob> associated with the scope of the backing pool of node, 
where the key is the id of the node, and the value is a byon formatted object 
that reflects the metadata that is temporarily associated with the node.

#### How do we manage state transitions?
Nodes that can be assigned are the relative complement of all nodes in active state and 
those reserved or being cleaned.  Nodes that are in active state can be obtained by the backend ComputeService via 
listNodesWithDetailsMatching(active).  Those reserved or being cleaned can be obtained by a complement of all 
blobstoremetadata under path reserved (recursive) and being tombstoned (recursive).

#### Request

Request is where a node and its relevant templateoptions placed into a map, awaiting a node to be assigned.

#### How do we clean nodes?
[perhaps lxc?](http://sysadvent.blogspot.com/2010/12/day-1-linux-containers-lxc.html)

