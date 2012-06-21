---
layout: jclouds
title: Quick Start - RimuHosting
---

# Quick Start: RimuHosting

RimuHosting's cloud is built on top of Xen with an in-house API layer to control it.

1. Sign up with Rimu Hosting
	To start using the API to create instances a VPS needs to be manually ordered and setup the first time. 
	This helps stop CC fraud and spammers. 
	After the first setup has been successfully run the API can be used. You can Order a VPS 
	from [here](http://rimuhosting.com/order/startorder1.jsp?hom=t-vps)

2. The RimuHosting API uses a special key to authenticate. It is available from [http://rimuhosting.com/cp/apikeys.jsp]

3. Setup jclouds
 * get the dependency `jclouds-rimuhosting` using our [Installation Guide](/documentation/userguide/installation-guide).

4. Start Coding
* Obtaining a context to Rimuhosting
Once you have jclouds libraries loaded, the easiest way to get started is to use the `ComputeServiceContextFactory`.  
	This takes your apikey and gives you a context to either get a synchronous interface (`getApi()`) or 
	asynchronous interface (`getAsyncApi()`) to the RimuHosting service.

{% highlight java %}
// create context to obtain rimuhosting objects
RestContext<RimuHostingClient, RimuHostingAsyncClient> context = 
   				new ComputeServiceContextFactory().createContext("rimuhosting", 
																apiKey, null).getProviderSpecificContext();

// get a synchronous object to use for manipulating objects in the RimuHosting Cloud
RimuHostingClient rhClient = context.getApi();
{% endhighlight %}
