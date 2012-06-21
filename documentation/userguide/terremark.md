---
layout: jclouds
title: Overview of the Terremark Compute Offerings
---
# Overview of the Terremark Compute Offerings
## Quick Start Guides
  * [Terremark VCloudExpress](/documentation/quickstart/terremark-vcloud-express)
  * [Terremark ECloud](/documentation/quickstart/terremark-ecloud)

## Introduction

Terremark offers two services extending the VMWare vCloud API.  
Their cloud offers features like load balancing (Internet Service) and customization extensions that
 are not in the vCloud specification.  

The two services Terremark offers that jclouds supports are vCloud Express (trmk-vcloudexpress) and eCloud (trmk-ecloud)

## Usage Notes for Terremerk vCloud

### Features not yet implemented

Currently, Terremark supports most of the VMware vCloud API version 0.8. 
Here are the features that are not currently supported:

  * undeployVApp
  * suspendVApp
  * InstantiateVAppTemplateOptions.disk(kilobytes)

### All IP addresses are private by default

You must use a vpn or connect an IP to an InternetService discussed below, if you need external access to Terremark vApps.

### Deploy vApp is a no-op

Terremark's instantiateVApp command automatically transitions to OFF.  
Calling deployVApp has no effect, except to return you an in-progress task.

## Usage Notes for Terremerk vCloud

### Default passwords

All unix vApps have a default user of `ecloud` with a password that is in the 
description of the vApp template. Please change these as soon as you boot your vApp.
<!-- TODO Check this date -->
Note that this will go away as of the 7-October-2010 release of eCloud where SSH key support will become available.

### Configuring DNS

You have to configure your own dns servers.  Here's how you can do this using [OpenDns](http://www.opendns.com) hosts:

{% highlight text %}
   # prime your password 
   echo p4ssw0rd|sudo -S echo hello
   echo nameserver 208.67.222.222 |sudo tee -a /etc/resolv.conf
   echo nameserver 208.67.220.220 |sudo tee -a /etc/resolv.conf
{% endhighlight %}

## Extension Guide

jclouds supports all services offered by Terremark vCloud Express and most from Terremark eCloud. 
Please read the [vCloud Express](https://community.vcloudexpress.terremark.com/en-us/product_docs/w/wiki/6-using-the-vcloud-express-api.aspx) 
or [eCloud](http://support.theenterprisecloud.com/kb/default.asp?id=601&Lang=1&SID=) documentation to understand the concepts of 
their implementation of vCloud.

Also review our [[vCloud Guide|VMWareVCloud]] to catch up to speed before progressing. This guide will only talk about differences.

### Login Details

vCloud Express (and in Oct 2010 eCloud) has a ssh key mechanism.  
You must use ssh keys instead of passwords to login to the host via ssh.  
You must have a default ssh key, although you can assign any key to a machine at instantiate time.

### Generating Keys

You must have at least a default key. The last parameter of generateKeyPairInOrg decides whether or not the keyPair will be default.

{% highlight java %}
// null for default org
TerremarkOrg org = vCloudExpressClient.findOrgNamed(null);
key = vCloudExpressClient.generateKeyPairInOrg(org.getHref(), "livetest", false);

{% endhighlight %}
### Listing Keys

{% highlight java %}
// null for default org,  note that the private part of the key is not stored at terremark
Set<KeyPair> response = vCloudExpressClient.listKeyPairsInOrg(null);
	
{% endhighlight %}

### Password management

* How do I determine whether a template supports changing the admin password? 

Rule of thumb is you can in Windows and you can't in UNIX.

{% highlight java %}
// nulls will look in the default org and catalog
TerremarkCatalogItem item = client.findCatalogItemInOrgCatalogNamed(null, null, "template name");

CustomizationParameters customizationOptions = client.getCustomizationOptions(item.getCustomizationOptions().getHref());
if (customizationOptions.canCustomizePassword())
         // you can set it
{% endhighlight %}

*  If I can't customize the password, what is it?

This is listed in the `description` of the VCloudExpressVAppTemplate. 
Here's is how you can get the description:

{% highlight java %}
// the vAppTemplateId tends to be the same as the itemId, but just in case, convert
TerremarkCatalogItem item = client.findCatalogItemInOrgCatalogNamed(null, null, "Ubuntu 8.04 LTS (x86)");

String description = client.getVAppTemplate(item.getHref()).getDescription();
{% endhighlight %}

### Instantiating vApp Templates 

* Instantiate vApp template automatically calls deploy

To make use of a vApp, you must first instantiate it, then deploy it, finally power it on. 
 In Terremark, instantiation automatically transitions to deploy.  
Even though this happens, you need to call deploy as it will return the task, which you can poll until the process is finished.

{% highlight java %}
      // lookup the datacenter you are deploying into, nulls for default
      vdc = clent.findVDCInOrgNamed(null, null);

      // instantiate, noting vApp returned has minimal details: id, name, location
      VCloudExpressVApp vApp = client.instantiateVAppTemplateInVDC(vdc.getHref(), vAppTemplate.getHref(), serverName);

      // In Terremark, instantiate implies deploy. Execute this to get a reference to that task.
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

*  How do I set the size of my boot drive? 

You can't. You can only customize the processorCount and memory size of a vApp.
However, you can add disks after the instantiate call, and before the deploy one.

*  How do I set the processor count and memory size of my vApp? 

Unless you specify otherwise, cpu count and memory size will be 1 and 512m respectively.  
You can change this by adding an options object to your instantiate command.  
Static import `processorCount` or `memory` and pass it as the last argument to instantiateVApp.

Note that you should guard this with a query to `getComputeOptionsOfCatalogItem` to ensure you don't pass an unsupported combination.

{% highlight java %}
import static org.jclouds.vcloud.terremark.options.TerremarkInstantiateVAppTemplateOptions.Builder. processorCount;

...

vApp = client.instantiateVAppTemplateInVDC(vdc.getHref(), vAppTemplate.getHref(), serverName, processorCount(2).memory(1024));
{% endhighlight %}

*  How do I assign a ssh key to a unix host? 

You will want to assign a ssh key in order to login to a unix machine you instantiate.  Do this through an instantiation option.

{% highlight java %}
import static org.jclouds.vcloud.terremark.options.TerremarkInstantiateVAppTemplateOptions.Builder.sshKeyFingerprint;

vApp = client.instantiateVAppTemplateInVDC(vdc.getHref(), vAppTemplate.getHref(), serverName, sshKeyFingerprint(key.getFingerPrint()));
{% endhighlight %}

*  How do I set the admin password when instantiating a vApp

jclouds syntax has required parameters followed by an options bundle.
In Terremark vCloud, the option you want is `withPassword()`.  
Static import this method and pass it as the last argument to instantiateVApp.  
Note that if a password is sent and not supported, it is quietly ignored.

{% highlight java %}
import static org.jclouds.vcloud.terremark.options.TerremarkInstantiateVAppTemplateOptions.Builder.withPassword;

...
      vApp = client.instantiateVAppTemplateInVDC(vdc.getHref(), vAppTemplate.getHref(), serverName, withPassword("robotsarefun"));
{% endhighlight %}
### Configuring vApps

Note that your vApp must be off before you can configure it.  You can configure multiple items at the same time.

*  How do I change the name of my vApp? 

{% highlight text %}
import static org.jclouds.vcloud.terremark.domain.VAppConfiguration.Builder.changeNameTo;
...

   Task task = client.configureVApp(vApp, changeNameTo("eduardo"));
{% endhighlight %}

* How do I change the number of processors in my vApp? 

{% highlight java %}
import static org.jclouds.vcloud.terremark.domain.VAppConfiguration.Builder.changeProcessorCountTo;
...

   Task task = client.configureVApp(vApp, changeProcessorCountTo(2));
{% endhighlight %}

* How do I change the amount of memory in my vApp?

{% highlight java %}
import static org.jclouds.vcloud.terremark.domain.VAppConfiguration.Builder.changeMemoryTo;
...

Task task = client.configureVApp(vApp, changeMemoryTo(1024));
{% endhighlight %}


*  How do I add a disk to my vApp?

{% highlight java %}
import static org.jclouds.vcloud.terremark.domain.VAppConfiguration.Builder.addDisk;
...
   Task task = client.configureVApp(vApp, addDisk(1048576));
{% endhighlight %}

*  How do I delete a disk from my vApp?

In order to delete a disk, you first need to figure out which one you want to delete.  
Get a reference to your vApp and select disks from it.  You cannot delete the system disk (address 0).

{% highlight java %}
import static org.jclouds.vcloud.terremark.domain.VAppConfiguration.Builder.deleteDiskWithAddressOnParent;
...

// extract the disks on the vApp sorted by addressOnParent
List<ResourceAllocation> disks = Lists.newArrayList(vApp.getResourceAllocationByType()
          .get(ResourceType.DISK_DRIVE));
 
// delete the second disk
Task task = client.configureVApp(vApp, deleteDiskWithAddressOnParent(disks.get(1).getAddressOnParent()));

{% endhighlight %}
### IP Addresses

Terremark vApps always have private IP addresses by default.  
You can go to the console and VPN into the network associated with your vApp.
Otherwise, you'll need to setup an Internet Service as described in the
[manual](https://community.vcloudexpress.terremark.com/en-us/product_docs/w/wiki/6-using-the-vcloud-express-api.aspx).

### Public Access to the vApp

Using the Terremark console, you can get VPN access to your vApp.  
If you wish the SSH or connect via HTTP, you need to create an Internet Service.  
Here's how:

{% highlight java %}
// lookup the datacenter you are deploying into, nulls for default
vdc = clent.findVDCInOrgNamed(null, null);

// add a Internet service so you can ssh outside the vpn, noting ecloud and vcloud express are slightly different
PublicIpAddress ip;
if (client instanceof TerremarkVCloudExpressClient) {
  is = TerremarkVCloudExpressClient.class.cast(client).addInternetServiceToVDC(
           vdc.getHref(), "SSH", Protocol.TCP, 22);
  ip = is.getPublicIpAddress();
} else {
  ip = TerremarkECloudClient.class.cast(client).activatePublicIpInVDC(
          vdc.getHref());
  is = tmClient.addInternetServiceToExistingIp(ip.getId(), "SSH", Protocol.TCP, 22);
}
//get the IP of the public internet service
publicIp = ip.getAddress();

// connect the vApp's internal ip:22 to the internet service
Node  node = client.addNode(is.getHref(), Iterables.getLast(vApp.getNetworkToAddresses().values()),
         vApp.getName() + "-SSH", 22);

// If you want, add a service and node for HTTP
is = client.addInternetServiceToExistingIp(publicIp.getHref(), "HTTP",
      Protocol.HTTP, 80)
node = client.addNode(is.getHref(), Iterables.getLast(vApp.getNetworkToAddresses().values()),
      vApp.getName() + "-HTTP", 80);

// Everything is setup!
System.out.printf("you can now connect to ssh://%s:22%n", publicIp.getHostAddress());
System.out.printf("when you start a webserver it will be at http://%s%n", publicIp.getHostAddress());

{% endhighlight %}

* How do I allocate a new a public IP? 

Depending on whether you are using vCloud express or eCloud, there are slightly different ways to get a
 new public ip address.  After you have a public IP, you can assign Internet Services to it.

{% highlight java %}
Set<PublicIpAddress> existingIps = client.getPublicIpsAssociatedWithVDC(vdc.getHref());

   PublicIpAddress ip;
// in vCloud Express, you have to create a new internet service to get a new IP
if (client instanceof TerremarkVCloudExpressClient) {
  is = TerremarkVCloudExpressClient.class.cast(client).addInternetServiceToVDC(
           vdc.getHref(), "SSH", Protocol.TCP, 22);
  ip = is.getPublicIpAddress();
// in eCloud, there is an activate command
} else {
  ip = TerremarkECloudClient.class.cast(client).activatePublicIpInVDC(
          vdc.getHref());
}

{% endhighlight %}

*  How do I connect my vApp to a public IP?

You'll need to add your vApp as a Node on each InternetService associated with the public IP you wish.  
For example, if you want to open SSH and HTTP, you'll have to find/configure an `InternetService` on
 the public IP for each port, then add your vApp to them.

{% highlight java %}
// you only need to connect one private IP to the Internet Service.
InetAddress privateAddress = Iterables.getLast(vApp.getNetworkToAddresses().values());

// a Node is a connection to a public IP on a particular port.
Node node = tmClient
       .addNode(is.getHref(), privateAddress, vApp.getName() + "-" + port, port);

{% endhighlight %}

## Troubleshooting 

*  java.lang.!IllegalStateException: you must specify the property jclouds.vcloud.defaults.network

There can be multiple networks, and jclouds needs to know which is the default to launch vApps into.
  This can be done via specifying an additional property when connecting.  
The list of available networks is in the exception message.  For example:

{% highlight text %}
1) Error in custom provider, java.lang.IllegalStateException: you must specify the property jclouds.vcloud.defaults.network as one of [10.1.1.160/27, 10.1.1.192/27, 10.1.1.224/27, 10.1.1.0/28]
{% endhighlight %}`

in the above, there are 4 networks to choose from.  Choose one and set it as the context property `VCloudConstants.PROPERTY_VCLOUD_DEFAULT_NETWORK`

{% highlight java %}
Properties overrides = new Properties();
overrides.setProperty(VCloudConstants.PROPERTY_VCLOUD_DEFAULT_NETWORK, "10.1.1.160/27");
ComputeServiceContext context = new ComputeServiceContextFactory().createContext("trmk-ecloud",USER ,PASSWORD,ImmutableSet.<Module> of(new JschSshClientModule()), overrides);
{% endhighlight %}

