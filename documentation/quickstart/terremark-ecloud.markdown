---
layout: jclouds
title: Quick Start - Terremark eCloud
---

# Quick Start:  Terremark eCloud

1. Sign up for Terremark eCloud: [sign-up link](http://www.terremark.com/services/cloudcomputing/theenterprisecloud.aspx)
2. Await for your Welcome Letter in email
  * Your username and password for the portal are also your credentials.
3. Ensure you are using a recent JDK 6
4. Setup your project to include trmk-ecloud
	* get the dependency `org.jclouds.provider/trmk-ecloud` using jclouds [Installation](/documentation/userguide/installation-guide).
5. Start coding

{% highlight java %}
import static org.jclouds.vcloud.terremark.options.TerremarkInstantiateVAppTemplateOptions.Builder.processorCount;

// get a context with Terremark that offers the portable ComputeService api
 ComputeServiceContext context = new ComputeServiceContextFactory().createContext("trmk-ecloud", email, password,
                                                         ImmutableSet.<Module> of(new JschSshClientModule()));

// TODO: eCloud in ComputeService example


// get a synchronous object to use for manipulating eCloud objects in terremarrk
TerremarkECloudClient eCloudClient = TerremarkECloudClient.class.cast(context.getProviderSpecificContext().getApi());

 // lookup the id of the datacenter you are deploying into
 vdc = eCloudClient.findVDCInOrgNamed(null, null);

 // lookup the id of the vAppTemplate you wish to deploy by name
 item = eCloudClient.findCatalogItemInOrgCatalogNamed(null, null"Ubuntu 8.04 LTS (x86)");

 // setup options for creating the vApp
 options = processorCount(1).memory(512);

 // instantiate, noting vApp returned has minimal details
 vApp = tmClient.instantiateVAppTemplateInVDC(vdc.getHref(), vAppTemplate.getHref(), serverName, instantiateOptions);

 // find the vApp template associated with the catalog item
 vAppTemplate = tmClient.getVAppTemplate(item.getEntity().getHref());

 // In Terremark, instantiate implies deploy. Execute this to get a reference to that task.
 Task deployTask = eCloudClient.deployVApp(vApp.getHref());
 
 // this object will loop up to 5 minutes for any task to complete on apply()
 RetryablePredicate<String> taskTester = new RetryablePredicate<String>(new TaskSuccess(context
          .getAsyncApi()), 300, 10, TimeUnit.SECONDS);

// block until deployment task shows success
if (!taskTester.apply(deployTask.getHref())) 
     throw new Exception("could not deploy "+vApp.getHref());

// after deployment, the vApp will have all details
vApp = eCloudClient.getVApp(vApp.getHref());

// turn on vApp
Task onTask = eCloudClient.powerOnVApp(vApp.getHref());

// block until poweron task shows success
if (!taskTester.apply(onTask.getHref())) 
     throw new Exception("could not turn on "+vApp.getHref());

 // add a Internet service so you can ssh outside the vpn
 ip =  eCloudClient.activatePublicIpInVDC(vdc.getHref());
 is =  eCloudClient.addInternetServiceToExistingIp(ip.getId(), "SSH", Protocol.TCP, 22);

 // connect the vApp's internal ip:22 to the internet service
 Node  node = eCloudClient.addNode(is.getHref(), Iterables.getLast(vApp.getNetworkToAddresses().values()),
          vApp.getName() + "-SSH", 22);;

 // get the ip of the public internet service
 InetAddress publicIp = is.getPublicIpAddress().getAddress();

 // Everything is setup!
 System.out.printf("you can now connect to ssh://ecloud:$Ep455l0ud!2@%s:22%n", publicIp.getHostAddress());

 context.close();
{% endhighlight %}

6. Validate results on the [Terremark console](https://icenter.digitalops.net/Default.aspx)

