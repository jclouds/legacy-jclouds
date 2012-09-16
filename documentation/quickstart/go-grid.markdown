---
layout: jclouds
title: Quick Start - Go Grid
---

# Quick Start: Go Grid

## Introduction

Starting with jclouds 1.0 beta 5, we're supporting [GoGrid](http://www.gogrid.com) cloud provider, 
both for the provider-specific APIs and for [[ComputeService]] abstraction.

### Important notes

To use GoGrid, please make sure your have the *key* and *shared secret*. 
Both can be created/obtained from GoGrid's control panel under My Account > API keys. 
The key is typically 16 digits, and the shared secret is 12 digits, but yours may be different.
Using your account's email address won't substitute the key and won't let you access their APIs.

### Using provider-specific API

To create a context for all subsequent API calls, use:

{% highlight java %}
RestContext<GoGridClient, GoGridAsyncClient> context = 
					new ComputeServiceFactory().createContext(key, sharedSecret)
										.getProviderSpecificContext();

GoGridClient client = context.getApi();

{% endhighlight %}

A typical use case, as demonstrated in much detail [here](http://github.com/jclouds/jclouds/blob/master/gogrid/src/test/java/org/jclouds/gogrid/GoGridLiveTestDisabled.java):

{% highlight java %}
// make an API call to get the IPs
Set<Ip> availableIps = client.getIpServices().getUnassignedIpList(); 

// iterate over the collection. you're not required to use Iterables
Ip availableIp = Iterables.getLast(availableIps);

{% endhighlight %}

Having an available IP address, you can create a server:

{% highlight java %}
Server createdServer = client.getServerServices().addServer(nameOfServer,
                "GSI-f8979644-e646-4711-ad58-d98a5fa3612c" /*image name*/,
                "1" /*ID of ram, 1 is 512 MB, 2 is 1GB*/,
                availableIp.getIp());
{% endhighlight %}

and optionally wait until it fully starts (in GoGrid's terms, until the server creation job is completed):

{% highlight java %}
RetryablePredicate<Server> serverLatestJobCompleted = new RetryablePredicate<Server>(
                new ServerLatestJobCompleted(client.getJobServices()),
                800, 20, TimeUnit.SECONDS);
serverLatestJobCompleted.apply(createdServer); //blocks until the condition is met or timeout is exceeded
//                                                   (returns true/false whether it was never met or not)
{% endhighlight %}

Note that this predicate can be used for any type of server-related jobs (for load balancer-related jobs,
 please see [this predicate](http://github.com/jclouds/jclouds/blob/master/gogrid/src/main/java/org/jclouds/gogrid/predicates/LoadBalancerLatestJobCompleted.java)).
Also, GoGrid will mark the server creation job finished when the OS is still booting up.

To restart the server, or turn it off, a power command with [PowerCommand](http://github.com/jclouds/jclouds/blob/master/gogrid/src/main/java/org/jclouds/gogrid/domain/PowerCommand.java) enum may be used:

{% highlight java %}
client.getServerServices().power(nameOfServer, PowerCommand.RESTART);
{% endhighlight %}

Deleting an existing server is accomplished by calling:

{% highlight java %}
client.getServerServices().deleteByName(nameOfServer);
{% endhighlight %}

To ssh into a third-party image, you may need credentials that aren't default. 
To find out the proper credentials, start up a server and run:

{% highlight java %}
Credentials instanceCredentials = client.getServerServices().getServerCredentialsList().get(nameOfServer)
{% endhighlight %}

Using these credentials, it's possible to use jclouds to log in to the instance:

{% highlight java %}
SshClient sshClient = context.utils().sshFactory().create(socket,
                instanceCredentials.account, instanceCredentials.key);
sshClient.connect();
String output = sshClient.exec("df").getOutput();
sshClient.disconnect();
{% endhighlight %}

### Using ComputeService abstraction==

To create a generic context, use (as in the previous section):

{% highlight java %}
ComputeServiceContext context = new ComputeServiceContextFactory().createContext("gogrid", user, password,
                ImmutableSet.of(new Log4JLoggingModule(), new JschSshClientModule()));
ComputeService service = context.getComputeService();
{% endhighlight %}

With having a ComputeService object in place, you can search for specific type of instance and/or image, and run it:

{% highlight java %}
Template t = service.templateBuilder().minRam(4096).imageId("GSI-6890f8b6-c8fb-4ac1-bc33-2563eb4e29d2").build();
service.runNodesInGroup("testGroup", 1 /*number of instances*/, t);
{% endhighlight %}

For more information on using this abstraction, refer to 
the detailed [test case](http://github.com/jclouds/jclouds/blob/master/providers/gogrid/src/test/java/org/jclouds/gogrid/compute/GoGridComputeServiceLiveTest.java).
