---
layout: jclouds
title: Quick Start- Terremark vCloud Express
---


# Quick Start: Terremark vCloud Express

1. Sign up for [Terremark vCloud Express](https://signup.vcloudexpress.terremark.com sign-up link)
2. Await for your Welcome Letter in email
    * Your username and password for the portal are also your credentials.
3. Ensure you are using a recent JDK 6 
4. Setup your project to include trmk-vcloudexpress
	* get the dependency `org.jclouds.provider/trmk-vcloudexpress` using jclouds [Installation](/documentation/userguide/installation-guide).
5. Start coding

{% highlight java %}
import static org.jclouds.vcloud.terremark.options.TerremarkInstantiateVAppTemplateOptions.Builder.processorCount;

// get a context with terremark that offers the portable ComputeService api
ComputeServiceContext context = new ComputeServiceContextFactory().createContext("trmk-vcloudexpress", email, password,
                                                         ImmutableSet.<Module> of(new JschSshClientModule()));

// here's an example of the portable api

// run a couple nodes accessible via group
nodes = context.getComputeService().client.runNodesInGroup("webserver", 2);


// get a synchronous object to use for manipulating vcloud objects in terremarrk
TerremarkVCloudExpressClient  tmClient = 
			TerremarkVCloudExpressClient .class.cast(
				context.getProviderSpecificContext().getApi());

// lookup the org you want to create an ssh key in
TerremarkOrg org =  tmClient.findOrgNamed(null);

// create a new ssh keypair, but don't make it default
key = tmClient.generateKeyPairInOrg(org.getHref(), "livetest", false);

// lookup the id of the datacenter you are deploying into
vdc = tmClient.findVDCInOrgNamed(null, null);

// lookup the catalog item you wish to deploy by name
item = tmClient.findCatalogItemInOrgCatalogNamed(null, null,"Ubuntu JeOS 9.04 (32-bit)");

// find the vApp template associated with the catalog item
vAppTemplate = tmClient.getVAppTemplate(item.getEntity().getHref());

// setup options for creating the vApp
options = processorCount(1).memory(512).sshKeyFingerprint(key.getFingerPrint())

// instantiate, noting vApp returned has minimal details
vApp = tmClient.instantiateVAppTemplateInVDC(vdc.getHref(), vAppTemplate.getHref(), serverName, instantiateOptions);

// In Terremark, instantiate implies deploy. Execute this to get a reference to that task.
deployTask = tmClient.deployVApp(vApp.getHref());

// this object will loop up to 5 minutes for any task to complete on apply()
RetryablePredicate<String> taskTester = new RetryablePredicate<String>(new TaskSuccess(context
         .getAsyncApi()), 300, 10, TimeUnit.SECONDS);

// block until deployment task shows success
if (!taskTester.apply(deployTask.getHref())) 
     throw new Exception("could not deploy "+vApp.getHref());

// after deployment, the vApp will have all details
vApp = tmClient.getVApp(vApp.getHref());

// turn on vApp
onTask = tmClient.powerOnVApp(vApp.getId());

// block until poweron task shows success
if (!taskTester.apply(onTask.getHref())) 
    throw new Exception("could not turn on "+vApp.getHref());

// add a Internet service so you can ssh outside the vpn
is = tmClient.addInternetServiceToVDC(vdc.getHref(), "SSH", Protocol.TCP, 22);

// connect the vApp's internal ip:22 to the internet service
node = tmClient.addNode(is.getId(), Iterables.getLast(vApp.getNetworkToAddresses().values()), vApp.getName()
         + "-SSH", 22);

// get the ip of the public internet service
InetAddress publicIp = is.getPublicIpAddress().getAddress();

// Everything is setup!
System.out.printf("you can now connect to ssh://vcloud@%s:22%nusing private key %s%n", publicIp.getHostAddress(), 
	key.getPrivateKey());

context.close();
{% endhighlight %}

6. Validate on the [Terremark console](https://my.vcloudexpress.terremark.com/default.aspx)

