---
layout: jclouds
title: Using jclouds to Manage VMWare vCloud
---
#Using jclouds to Manage VMWare vCloud

## Introduction

jclouds currently supports most of the VMware vCloud API.
We also provide tools that make developing to it easier.


## Service Providers

Here are the providers of the VMware vCloud API and the level of support in jclouds:

| *Company* | *Offering* | *Provider in jclouds* | *API Version in jclouds* |
|-----------|------------|-----------------------|--------------------------|
| <img src="http://www.vmware.com/files/images/vam/img-terremark.gif"> | [vCloud Express](http://vcloudexpress.terremark.com/blog.aspx) | trmk-vcloudexpress | 0.8a-ext1.6 |
| <img src="http://www.vmware.com/files/images/vam/img-terremark.gif">| [Enterprise Cloud](http://www.terremark.com/services/cloudcomputing/theenterprisecloud.aspx) | trmk-ecloud | 0.8b-ext2.3 |
| <img src="http://www.vmware.com/files/images/vam/img-bluelock.gif"> | [vCloud Enterprise](http://www.bluelock.com/bluelock-cloud-hosting/virtual-cloud-enterprise/)| bluelock-vcdirector | 1.0 | 
| <a href="http://www.stratogen.net/products/vmware-hosting.html"><img src="http://www.stratogen.net/images/footerlogo.gif"></a> | [VMware Hosting](http://www.stratogen.net/products/vmware-hosting.html)| stratogen-vcloud-mycloud | 1.0 |


## Obtaining a context to vCloud

Once you have jclouds libraries loaded, the easiest way to get started is to use the 
`*ComputeServiceContextFactory`.  This takes your username and password and gives you a context 
to either get a portable cloud computing interface (`getComputeService()` ), or the direct vCloud API ( `getProviderContext().getApi()` ).

* Getting a context to connect to Terremark vCloud Express:

{% highlight java %}
// add the ssh module, if you are using ComputeService, otherwise leave it out
ComputeServiceContext context = new ComputeServiceContextFactory().createContext("trmk-vcloudexpress", 
			user, password, ImmutableSet.of(new JshSshClientModule()));
 
// use context to obtain vcloud objects with terremark vCloud express extensions
RestContext<TerremarkVCloudExpressClient, TerremarkVCloudExpressAsyncClient>  providerContext = context.getProviderContext();

{% endhighlight %}

* Getting a context to use Terremark eCloud

{% highlight java %}
// add the ssh module, if you are using ComputeService, otherwise leave it out
ComputeServiceContext context = new ComputeServiceContextFactory().createContext("trmk-ecloud", 
									user, password, ImmutableSet.of(new JshSshClientModule()));

// use context to obtain vcloud objects with terremark eCloud extensions
RestContext<TerremarkECloudClient, TerremarkECloudAsyncClient>  providerContext = context.getProviderContext();

{% endhighlight %}

* Here's an example of getting a context to BlueLock vCloud Director

{% highlight java %}
// add the ssh module, if you are using ComputeService, otherwise leave it out
ComputeServiceContext context = new ComputeServiceContextFactory().createContext("bluelock-vcdirector",
 							user, password, ImmutableSet.of(new JshSshClientModule()));

RestContext<VCloudClient, VCloudAsyncClient> providerContext = context.getProviderContext();


{% endhighlight %}

* Here's an example of getting a context to StratoGen vCloud Director                                                                                                      

{% highlight java %}                                                                                                                                                                      
      // add the ssh module, if you are using ComputeService, otherwise leave it out                                                                                     
      ComputeServiceContext context = new ComputeServiceContextFactory().createContext("stratogen-vcloud-mycloud", user, password, ImmutableSet.of(new JshSshClientModul\
()));                                                                                                                                                                    
                                                                                                                                                                         
      RestContext<VCloudClient, VCloudAsyncClient> providerContext = context.getProviderContext();                                                                       
{% endhighlight %}                                                                                                                                                                  


* Getting a context to any portable version 1.0 vCloud:

{% highlight java %}

Properties overrides = new Properties();
overrides.setProperty("vcloud.endpoint", "https://****/api");

// add the ssh module, if you are using ComputeService, otherwise leave it out
ComputeServiceContext context = new ComputeServiceContextFactory().createContext("vcloud", user, password,
               		ImmutableSet.of(new Log4JLoggingModule(), new JshSshClientModule()), overrides);

RestContext<VCloudClient, VCloudAsyncClient> providerContext = context.getProviderContext();
{% endhighlight %}


## Portability 

All vClouds (Express or Director) extend the same interfaces `CommonVCloudClient` and 
`CommonVCloudAsyncClient`  You can always use this in place of the vendor-specific interface.

{% highlight text %}
// get a synchronous object to use for manipulating vcloud objects
CommonVCloudClient client = context.getApi()
{% endhighlight %}

If you are interacting with Terremark, you can cast to `VCloudExpressClient` and `VCloudExpressAsyncClient` 
to use the common features between eCloud and vCloud Express.

### Locating a vApp template to instantiate

In order to instantiate a vApp template, you must first find one.  Here's how to lookup a Ubuntu template:

{% highlight java %}
// lookup the item holding the vAppTemplate you wish to deploy by name
// note that if you don't specify a parameter for org and catalog, jclouds will search the default catalog
CatalogItem item = client.findCatalogItemInOrgCatalogNamed(null, null, "Ubuntu JeOS 9.04 (32-bit)");
     
// note that the href of the catalog item isn't necessarily the same as the actual template
// always get the "real" template from the catalog item's entity 
VAppTemplate vAppTemplate = client.getVAppTemplate(item.getEntity().getHref());

// note that vcloud 0.8 vApp template is not compatible with 1.0.  
// Use VCloudExpressVAppTemplate in that case
VCloudExpressVAppTemplate vAppTemplate = client.getVAppTemplate(item.getEntity().getHref());

{% endhighlight %}

### Instantiate, deploy, and powerOn a vApp template in vCloud 0.8 (Terremark)

To make use of a vApp, you must first instantiate it, then deploy it, finally power it on. 

{% highlight java %}
// lookup the datacenter you are deploying into, nulls for default
vdc = clent.findVDCInOrgNamed(null, null);

// instantiate, noting vApp returned has minimal details: id, name, location
VCloudExpressVApp = client.instantiateVAppTemplateInVDC(vdc.getHref(), vAppTemplate.getHref(), serverName);

// Execute this to start deployment
Task deployTask = client.deployVApp(vApp.getHref());

// this object will loop up to 5 minutes for any task to complete on apply()
RetryablePredicate<String> taskTester = new RetryablePredicate<String>(new TaskSuccess(context
         .getAsyncApi()), 300, 10, TimeUnit.SECONDS);

// block until deployment task shows success
if (!taskTester.apply(deployTask.getHref()) 
      throw new Exception("could not deploy "+vApp.getHref());

// turn on vApp
Task onTask = client.powerOnVApp(vApp.getHref());

// block until poweron task shows success
if (!taskTester.apply(onTask.getHref())) 
      throw new Exception("could not turn on "+vApp.getHref());
{% endhighlight %}

### Instantiate, deploy, and powerOn a vApp template in vCloud 1.0 

To make use of a vApp, you must first instantiate it, then deploy it, finally power it on.  Default in vCloud 1.0 is 
to transition to powerOn state from instantiate.

{% highlight java %}
// lookup the datacenter you are deploying into, nulls for default
vdc = clent.findVDCInOrgNamed(null, null);

// instantiate, noting vApp returned has minimal details: id, name, location
VApp = client.instantiateVAppTemplateInVDC(vdc.getHref(), vAppTemplate.getHref(), serverName);

// the task inside the app will complete when the power is on
Task task = vAppResponse.getTasks().get(0);
 
// this object will loop up to 5 minutes for any task to complete on apply()
RetryablePredicate<String> taskTester = new RetryablePredicate<String>(new TaskSuccess(context
          .getAsyncApi()), 300, 10, TimeUnit.SECONDS);

// block until task shows success
if (!taskTester.apply(task.getHref()) 
     throw new Exception("could not deploy and powerOn "+vApp.getHref());
{% endhighlight %}

### Getting vApp details

In order to get details such as the private IP address, the vApp must at least be deployed.

{% highlight java %}
     // after deployment, the vApp will have all details. 
     // note that 0.8 or VCloudExpress objects are not compatible with 1.0 ones

     // for terremark or other vCloud Express 0.8 based services
     VCloudExpressVApp = client.getVApp(vApp.getHref());

     // for vCloud 1.0 based services
     vApp = client.getVApp(vApp.getHref());

{% endhighlight %}

### Closing the context 

The context object uses threads and other resources.  When you are finished, close it.

{% highlight java %}
      context.close();
{% endhighlight %}

### vApp Templates

vApp Templates are like images in other systems.  However, these also include virtual hardware settings, which can be customized.

### FAQ

 
* Can I tell the template that my vApp was created from?
 No.
	
* How do I list the vApp instances in a VDC?

vApps are listed as ResourceEntities in the vDC. 

{% highlight java %}
for (ReferenceType item :  vdc.getResourceEntities().values()) {
   if (item.getType().equals(VCloudMediaType.VAPP_XML)) {
      // version 1.0
      VApp app = client.getVApp(item.getHref());
      // version 0.8 (terremark)
      VCloudExpressVApp app = client.getVApp(item.getHref());
   }
}
{% endhighlight %}

* How do I get the size of a vApp? (1.0)
Get a reference to the vApp object and query the ResourceAllocations in the hardware section of the enclosed VMs

{% highlight java %}
import static com.google.common.collect.Iterables.*;
import static org.jclouds.vcloud.predicates.VCloudPredicates.resourceType;

Vm vm = get(from.getChildren(), 0);
VirtualHardwareSection hardware = vm.getVirtualHardwareSection();

int megsRam = (int) find(hardware.getResourceAllocations(), resourceType(ResourceType.MEMORY)).getVirtualQuantity();

// be careful as you may have multiple processors
int vpus = (int) find(hardware.getResourceAllocations(), resourceType(ResourceType.PROCESSOR)).getVirtualQuantity();

// be careful as you may have multiple disks
int kbDisk = (int) find(hardware.getResourceAllocations(), resourceType(ResourceType.DISK_DRIVE)).getVirtualQuantity();
{% endhighlight %}

*  How do I get the size of a vApp? (0.8)

Get a reference to the vApp object and query the ResourceAllocations

{% highlight java %}
import static com.google.common.collect.Iterables.*;
import static org.jclouds.vcloud.predicates.VCloudPredicates.resourceType;

int megsRam = (int) find(vApp.getResourceAllocations(), resourceType(ResourceType.MEMORY)).getVirtualQuantity();

// be careful as you may have multiple processors
int vpus = (int) find(vApp.getResourceAllocations(), resourceType(ResourceType.PROCESSOR)).getVirtualQuantity();

// be careful as you may have multiple disks
int kbDisk = (int) find(vApp.getResourceAllocations(), resourceType(ResourceType.DISK_DRIVE)).getVirtualQuantity();
{% endhighlight %}

* How do I clone my vApp without starting the clone?

`Note that the the source vApp must be off before you can clone it.`

This is the default behavior of cloneVApp.

{% highlight java %}
// lookup the datacenter you are deploying into, nulls for default
vdc = clent.findVDCInOrgNamed(null, null);

String newName = "clone of "+sourceVApp.getName();

Task task = client.cloneVAppInVDC(vdc.getHref(), sourceVApp.getHref(), newName);
{% endhighlight %}

*  How do I make a clone which automatically powers on?

You need to set a couple options for this.  deploy, then poweron.

{% highlight java %}
import static org.jclouds.vcloud.options.CloneVAppOptions.Builder.deploy;

// lookup the datacenter you are deploying into, nulls for default
vdc = clent.findVDCInOrgNamed(null, null);

String newName = "clone of "+sourceVApp.getName();

Task task = client.cloneVAppInVDC(vdc.getHref(), sourceVApp.getHref(), newName, deploy().powerOn());
{% endhighlight %}

