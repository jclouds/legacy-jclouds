---
layout: jclouds
title: User Guide- How to use the Compute API and tools
---
# User Guide: How to use the Compute API and tools

## Introduction 
The jclouds Compute API provides a basic abstraction across Compute APIs such as Amazon EC2 and VMware vCloud.  
We also have integrations for popular tools such as Ant and Maven.

## Features
### Location Aware API 

Unlike other tools, our compute api doesn't require you to establish multiple connections to clouds who are multi-homed. 
For example, you can use the same object to access your resources in all regions of EC2.  
This allows you greater visibility into your resources and provides means to logically tie cross-wan resources together.

### Node Sets
Using our compute API, you can run multiple nodes as a set regardless of the underlying cloud API.  
In this way, you can fire up a 20 node cluster and manage it by its group name, as opposed to dealing with each node individually.

### SSH keys
Our compute API natively helps with moving your ssh keys to the node on startup, so that 
you don't have to worry about remembering or storing random passwords or keys provided by the service.

### Run Script
Our runScript features provide simple means for executing scripts on the machines in a set.  
It also includes special exception types that will allow you to handle errors appropriately.

### Stub Provider
As more people are looking at concepts like devops, it follows that infrastructure as code implies testability as a concern. 
One of our steps to help is the addition of a stub compute provider, which you can join with our in-memory blobstore to 
test your provisioning instructions before you try them out with your credit card:

### Credential persistence

As of jclouds beta-9, you can now supply a map to persist credentials of your nodes across connections.  Using a blobstore-backed map, 
this means you can keep track of all of your cloud nodes' credentials in a single place.

### Supported Providers
All of the following providers can be used equally in any Compute API tool.

<table>
	<thead>
		<tr>
			<th>maven dependency*</th>
			<th>iso 3166 codes</th>
		</tr>
		<tbody>
			<tr>
				<td>org.jclouds.provider/aws-ec2</td>
				<td>US-VA,US-CA,IE,SG</td>
			</tr>
			<tr>
				<td>org.jclouds.provider/bluelock-vcloud-vcenterprise</td>
				<td>US-IN</td>
			</tr>
			<tr>
				<td>org.jclouds.provider/bluelock-vcloud-zone01</td>
				<td>US-IN</td>
			</tr>
			<tr>
				<td>org.jclouds.provider/cloudservers-uk</td>
				<td>GB-SLG</td>
			</tr>
			<tr>
				<td>org.jclouds.provider/cloudservers-us</td>
				<td>US-IL,US-TX</td>
			</tr>
			<tr>
				<td>org.jclouds.provider/cloudsigma-zrh</td>
				<td>CH-ZH</td>
			</tr>
			<tr>
				<td>org.jclouds.provider/elastichosts-lon-b</td>
				<td>GB-LND</td>
			</tr>
			<tr>
				<td>org.jclouds.provider/elastichosts-lon-p</td>
				<td>GB-LND</td>
			</tr>
			<tr>
				<td>org.jclouds.provider/elastichosts-sat-p</td>
				<td>US-TX</td>
			</tr>
			<tr>
				<td>org.jclouds.provider/eucalyptus-partnercloud-ec2</td>
				<td>US-CA</td>
			</tr>
			<tr>
				<td>org.jclouds.provider/gogrid</td>
				<td>US-CA,US-VA</td>
			</tr>
			<tr>
				<td>org.jclouds.provider/openhosting-east1</td>
				<td>US-VA</td>
			</tr>
			<tr>
				<td>org.jclouds.provider/rimuhosting (compute)</td>
				<td>NZ-AUK,US-TX,AU-NSW,GB-LND</td>
			</tr>
			<tr>
				<td>org.jclouds.provider/serverlove-z1-man</td>
				<td>GB-MAN</td>
			</tr>
			<tr>
				<td>org.jclouds.provider/skalicloud-sdg-my</td>
				<td>MY-10</td>
			</tr>
			<tr>
				<td>org.jclouds.provider/slicehost</td>
				<td>US-IL,US-TX,US-MO</td>
			</tr>
			<tr>
				<td>org.jclouds.provider/stratogen-vcloud-mycloud (compute)</td>
				<td>GB</td>
			</tr>
			<tr>
				<td>org.jclouds.provider/trmk-ecloud</td>
				<td>US-FL,NL-NH</td>
			</tr>
			<tr>
				<td>org.jclouds./trmk-vcloudexpress</td>
				<td>US-FL</td>
			</tr>
		</tbody>

	</thead>
</table>



You can also set the context property `provider`.endpoint to use the following APIs for your private cloud                                                                                
                                                                                                                                                                                          
| *maven dependency*  		|                                                                                                                                                            
|:--------------------------|
| org.jclouds.api/byon 		|                                                                                                                                                                  
| org.jclouds.api/deltacloud	|                                                                                                                                                            
| org.jclouds.api/elasticstack	|                                                                                                                                                          
| org.jclouds.api/eucalyptus	|                                                                                                                                                            
| org.jclouds.api/nova		|                                                                                                                                                                  
| org.jclouds.api/vcloud	|                                                                                                                                                                
                                                                                                                                                                                          


*Note, that if you use the `jclouds-allcompute` dependency, your project will have all of the above!*

## API

### Concepts 
The Compute API is a portable means of managing nodes in clouds. It can manage nodes as a 
set and address resources in any cloud without needing separate connections. It also has a 
Template feature which allows you to search for configurations that match parameters such as 
CPU count or operating system.  Finally, it contains utilities to execute scripts as part of 
the bootstrap process of your nodes.

### Template

Templates are a way of encapsulating the requirements of your nodes such that similar configurations can be launched in other clouds.  

A Template consists of the following elements:

  * Image - defines which bytes boot your node, and details such as the operating system you wish to run.
  * Hardware - defines CPU, memory, disk, and supported architecture .
  * Location - defines the region or datacenter in which your node(s) should run.
  * Options - defines optional parameters such as inbound ports to open or scripts to execute at boot time.

Templates can be created from your service context via a TemplateBuilder.  This builder allows you 
to specify your requirements with statements such as `minCores` and `imageId`.

### Operating System
An Image is different from an Operating System. It is really a bunch of state that includes an operating system, 
software and configuration. It is often versioned independently of the operating system. 
Before, we lumped together operating system with image, as this is what amazon and some others did. 
However, this assumes nodes are created from images. This is not the case in OVF-based systems like vCloud, 
and we really had no clean way to address this before. Operating System as a type works much cleaner and across more systems.

### Hardware
We previously had a class called Size which held a combination of size and hardware configuration data. 
While simple, it limited our ability to address fine grained concerns, as it assumed there is 
only a single disk, processor, etc. Our new Hardware type is much more robust, while still extremely simple to use. 
While mounted volume information is read-only at the moment, expect us to add portability over systems like EBS and vCloud volumes in the near future.

### Usage

Using the API is straightforward. You need to create a context to the service you wish to manage, and then act on it. 
Here's how to perform common commands.

### Open your context and get a service reference

Here, you specify the particular service you wish to manage and get a reference to ComputeService.

{% highlight java %}

ComputeServiceContext context = 
	new ComputeServiceContextFactory().createContext("terremark", user, password);

ComputeService computeService = context.getComputeService();

{% endhighlight %}

### List the nodes you have in all locations
As mentioned above, this context can operate all of your nodes across the globe.  Here's how to list them

{% highlight java %}

for (ComputeMetadata node : client.listNodes()) {
   node.getId(); // how does jclouds address this in a global scope
   node.getProviderId(); // how does the provider api address this in a specific scope
   node.getName(); // if the node is named, what is it?
   node.getLocation(); // where in the world is the node
}

{% endhighlight %}

Note that the result is of type `ComputeMetadata` rather than the more useful `NodeMetadata`.  
This is because many services offer only minimal details on listing.  To flesh out the objects you want, 
call the _Get Node Metadata_ command.

### Get Node Metadata
Use the _Get Node Metadata_ command to retrieve commonly required information about a node.

{% highlight java %}

NodeMetadata metadata = client.getNodeMetadata(node);
metadata.getId();
metadata.getProviderId(); 
metadata.getLocation();
metadata.getName();
metadata.getGroup();// if part of a nodeset, this identifies which one.
metadata.getHardware();
metadata.getImageId(); // if the node was created by an image, what is its id?
metadata.getOperatingSystem();
metadata.getState();
metadata.getPrivateAddresses();
metadata.getPublicAddresses();
metadata.getCredentials();// only available after createNodesInGroup, identifies login user/credential
{% endhighlight %}
### List Assignable Locations

The _List Assignable Location_ command returns all the valid locations for nodes.  
The list locations command returns all the valid locations for nodes. 
A location has a scope, which is typically region or zone. A region is a general area, 
like eu-west, where a zone is similar to a datacenter. If a location has a parent, 
that implies it is within that location. For example a location can be a rack, whose parent is likely to be a zone.

{% highlight java %}
Set<? extends Location> listAssignableLocations();
{% endhighlight %}

### List Hardware Profiles
The _List Hardware Profiles_ command returns settings including virtual CPU count, memory, and disks. 

CPU count is not a portable quantity across clouds, as they are The list hardware profiles 
command shows you the options including virtual cpu count, memory, and disks. cpu count is not 
a portable quantity across clouds, as they are measured differently. However, it is a good indicator of
 relative speed within a cloud. memory is measured in megabytes and disks in gigabytes.

{% highlight java %}
Set<? extends Hardware> listHardwareProfiles();
{% endhighlight %}

### List Images
The _List Images_ command define the operating system and metadata related to a node.  
In some clouds, images are bound to a specific region, and their identifiers are different across regions.  
For this reason, you should consider matching image requirements like operating system family with TemplateBuilder as opposed to choosing an image explicitly.

{% highlight java %}
   Set<? extends Image> listImages();
{% endhighlight %}

### Create Nodes with Group 

The compute API treats nodes as a group based on a group you specify. Using this group, 
you can choose to operate on one or many nodes as a logical unit without regard to the implementation details of the cloud.

`createNodesInGroup` returns all of the nodes the API was able to launch into in a running state with port 22 open. 
If resources such as security groups are needed, they will be reused or created for you.
Here's an example of how to start a nodeSet:

{% highlight java %}
NodeSet nodes = client.createNodesInGroup(group, 2, template);
{% endhighlight %}

The set that is returned will include credentials you can use to ssh into the nodes. 
The "credential" part of the credentials is either a password or a private key. You have to inspect the value to determine this.  
Make sure you look also at the "identity" part of the credentials object so that you don't attempt to login as the wrong user.

{% highlight java %}
if (node.getCredentials().credential.startsWith("-----BEGIN RSA PRIVATE KEY-----"))
 // it is a private key, not a password.
{% endhighlight %}

Note: If all you want to do is execute a script at bootup, you should consider use of the runScript option. 

### Predicate Commands
Commands ending in `Matching` allow you to decide which subset of nodes you with to affect.  
All predicate commands are run in parallel for highest efficiency.

#### Example Predicates

There are a number of predicates in the `NodePredicates` class.  Here are a few combinations that you may find interesting:
  
  * `runningInGroup(group)` - affect any nodes that are already running, refined to a specific group

{% highlight java %}
import static org.jclouds.compute.predicates.NodePredicates.runningInGroup;
import static org.jclouds.compute.options.RunScriptOptions.Builder.overrideCredentialsWith;

Map<? extends NodeMetadata, ExecResponse>  responses = client.runScriptOnNodesMatching(runningInGroup(group), script,
                  overrideCredentialsWith(creds));
{% endhighlight %}

  * `and(withGroup(group), not(TERMINATED))` match everything that has a group, but not destroyed.

{% highlight java %}
import static com.google.common.base.Predicates.*;
import static org.jclouds.compute.predicates.NodePredicates.*;
import static com.google.common.collect.Iterables.*;

Iterable<? extends NodeMetadata> billedNodes = filter(client.listNodesDetailsMatching(all()), and(withGroup(group), not(TERMINATED))));

{% endhighlight %}

#### Commands

##### Destroy Nodes Matching Predicate

nodes matching the filter are destroyed as a logical set.   
When the last node in a set is destroyed, any indirect resources it uses, such as keypairs, are also destroyed.  Ex. here's how to destroy all nodes with a specific group:

{% highlight java %}
import static org.jclouds.compute.predicates.NodePredicates.*;
   client.destroyNodesMatching(withGroup(group));
{% endhighlight %}

##### Reboot Nodes Matching Predicate 
Ex. here's how to reboot all nodes with a specific group:

{% highlight java %}
import static org.jclouds.compute.predicates.NodePredicates.*;
   client.rebootNodesMatching(withGroup(group));
{% endhighlight %}

##### Run Script on Nodes Matching Predicate
Here's how to run a script on all nodes as root with the credentials that they were created with.

{% highlight java %}
import static org.jclouds.compute.predicates.NodePredicates.runningInGroup;

responses = client.runScriptOnNodesMatching(runningInGroup(group), script);
{% endhighlight %}

If you created your nodes using the `authorizePublicKey` option, then you are probably interested in using that again here.  
However, you should always look up the login user associated with the host, as it may not be root.

*Note* if you think this should change, please file an issue.  For example, we could in the future create a 
sudo-able login user on the nodes, simplifying this process.

{% highlight java %}
import static org.jclouds.compute.predicates.NodePredicates.runningInGroup;
import static org.jclouds.compute.options.RunScriptOptions.Builder.overrideCredentialsWith;
import static org.jclouds.compute.options.TemplateOptions.Builder.authorizePublicKey;

nodes = client.createNodesInGroup(group, 1, authorizePublicKey(myKey));
Credentials good = nodes.iterator().next().getCredentials();

// save this off
loginUser = good.identity;

// later, you will use your key with the default login user
responses = client.runScriptOnNodesMatching(runningInGroup(group), script,
                  overrideCredentialsWith(new Credentials(loginUser, myKey));
{% endhighlight %}

##### Working with credentials

Note that by default, jclouds stores credentials in a static member.  
If you close and reopen your compute context, your credentials will still be accessible.  
If you'd like to have credentials persist across compute service contexts, then supply a backing map like below:

{% highlight java %}
// set the location of the filesystem you wish to persist credentials to
props.setProperty(FilesystemConstants.PROPERTY_BASEDIR, "/var/gogrid");

blobContext = new BlobStoreContextFactory().createContext("filesystem", "foo", "bar", ImmutableSet.<Module>of(), props);

credentialsMap = blobContext.createInputStreamMap("credentials");

computeContext = new ComputeServiceContextFactory().createContext("gogrid", secret, apiKey,
         ImmutableSet.of(new CredentialStoreModule(credentialsMap)));
{% endhighlight %}
### Individual Node Commands
Individual commands are executed against a specific node's `id` (not `providerId`!).  
You can save time if you know you are only affecting one node, and don't need jclouds' help finding it.

#### Commands
##### Get Node Metadata

If you save a node's id to disk, you can inflate it later without querying by using the `getNodeMetadata` command.

{% highlight java %}
NodeMetadata metadata = client.getNodeMetadata(savedId);
{% endhighlight %}
##### Destroy Node
##### Reboot Node

### Logging
You can now see status of compute commands by enabling at least DEBUG on the log category: "jclouds.compute".  Here is example output:

{% highlight text %}
2010-02-06 09:43:54,985 DEBUG [jclouds.compute] (main) >> providing images
2010-02-06 09:44:01,186 DEBUG [jclouds.compute] (main) << didn't match os(folding)
2010-02-06 09:44:01,642 DEBUG [jclouds.compute] (main) << images(614)
2010-02-06 09:44:01,672 DEBUG [jclouds.compute] (main) >> terminating servers by group(ec2)
2010-02-06 09:44:03,314 DEBUG [jclouds.compute] (main) << destroyed
2010-02-06 09:44:03,900 DEBUG [jclouds.compute] (main) >> searching params([arch=null, biggest=false, fastest=false, imageDescription=null, imageId=null, imageVersion=null, location=us-east-1, minCores=0, minRam=0, os=ubuntu, osDescription=null, sizeId=null])
2010-02-06 09:44:03,906 DEBUG [jclouds.compute] (main) <<   matched image([id=ami-87a243ee, name=9.10, locationId=us-east-1, architecture=X86_64, osDescription=alestic-64/ubuntu-9.10-karmic-base-64-20090623.manifest.xml, osFamily=ubuntu, version=20090623])
2010-02-06 09:44:03,907 DEBUG [jclouds.compute] (main) <<   matched size([id=m1.large, cores=4, ram=7680, disk=850, supportedArchitectures=[X86_64]])
2010-02-06 09:44:03,907 DEBUG [jclouds.compute] (main) <<   matched location([id=us-east-1, scope=REGION, description=us-east-1, parent=null, assignable=true])
2010-02-06 09:44:03,909 DEBUG [jclouds.compute] (main) >> creating keyPair region(us-east-1) name(ec2)
2010-02-06 09:44:06,886 DEBUG [jclouds.compute] (main) << created keyPair(ec2)
2010-02-06 09:44:06,886 DEBUG [jclouds.compute] (main) >> creating securityGroup region(us-east-1) name(ec2)
2010-02-06 09:44:07,243 DEBUG [jclouds.compute] (main) << created securityGroup(ec2)
2010-02-06 09:44:07,243 DEBUG [jclouds.compute] (main) >> authorizing securityGroup region(us-east-1) name(ec2) port(22)
2010-02-06 09:44:07,824 DEBUG [jclouds.compute] (main) << authorized securityGroup(ec2)
2010-02-06 09:44:07,828 DEBUG [jclouds.compute] (main) >> running 2 instance region(us-east-1) zone(null) ami(ami-87a243ee) type(m1.large) keyPair(ec2) securityGroup(ec2)
2010-02-06 09:44:09,239 DEBUG [jclouds.compute] (main) << started instances(i-7c180614,i-7e180616)
{% endhighlight %}

If you are using the Log4JLoggingModule, here is an example log4j.xml stanza you can use to enable compute logging:

{% highlight xml %}
<appender name="COMPUTEFILE" class="org.apache.log4j.DailyRollingFileAppender">
     <param name="File" value="logs/jclouds-compute.log" />
     <param name="Append" value="true" />
     <param name="DatePattern" value="'.'yyyy-MM-dd" />
     <param name="Threshold" value="TRACE" />
     <layout class="org.apache.log4j.PatternLayout">
         <param name="ConversionPattern" value="%d %-5p [%c] (%t) %m%n" />
     </layout>
</appender>

<appender name="ASYNCCOMPUTE" class="org.apache.log4j.AsyncAppender">
     <appender-ref ref="COMPUTEFILE" />
</appender>

<category name="jclouds.compute">
     <priority value="TRACE" />
     <appender-ref ref="ASYNCCOMPUTE" />
</category>
{% endhighlight %}

## Usage in Google AppEngine
Please see [][UsingJCloudsWithGAE]]

## Advanced Usage
Almost all advanced features require ssh.  You will likely also want to use log4j and our 
enterprise configuration module.  Here's how to configure these.

{% highlight java %}
Properties overrides = new Properties();
Set<Module> wiring = ImmutableSet.of(new JschSshClientModule(), new Log4JLoggingModule(), new EnterpriseConfigurationModule());
ComputeServiceContext context = new ComputeServiceContextFactory().createContext("terremark", user, password,
             wiring, overrides);
{% endhighlight %}

For mode information, check out the [[jcloudsAPI]] wiki page.

### Uploading binary files to the node

You'll need to get an ssh client for the node in order to upload files in the current version of jclouds.

if you just created the node, it will have login credentials set in node.getCredentials().  Ask for an ssh client based on the node.

{% highlight java %}
client = context.utils().sshForNode().apply(node);
{% endhighlight %}

if the node's credentials aren't set, you'll have to assign them first.

{% highlight java %}
client = context.utils().sshForNode().apply(NodeMetadataBuilder.fromNodeMetadata(node).credentials(
	                   new Credentials("adrian",sshKeyInString)).build());
{% endhighlight %}

Once you have the client, you can push files to it

{% highlight java %}
client.put("/path/to/file", Payloads.newFilePayload(contents));
{% endhighlight %}


Here's a full example:

{% highlight java %}
      SshClient ssh = context.utils().sshForNode().apply(node);
      try {
         ssh.connect();
         ssh.put("/path/to/file",
Payloads.newPayload(fileOrInputStreamOrBytesOrString));
      } finally {
         if (ssh != null)
            ssh.disconnect();
      }
{% endhighlight %}

### Selecting a correct package manager

Below, you'll see how you can consolidate bootstrap instructions when empowered with an operating system type.
 No more listing the dozen flavors of compatible unix's when all you're really concerned about is the package manager:

{% highlight java %}
if (OperatingSystemPredicates.supportsApt().apply(os))
  return RunScriptData.APT_RUN_SCRIPT;
else if (OperatingSystemPredicates.supportsYum().apply(os))
  return RunScriptData.YUM_RUN_SCRIPT
else if (OperatingSystemPredicates.supportsZypper().apply(os))
  return RunScriptData.ZYPPER_RUN_SCRIPT;
else
  throw new IllegalArgumentException("don't know how to handle" + os.toString());
{% endhighlight %}

### Opening ports
Unless you specify otherwise, only access to public IP on port 22 is explicitly configured.  
In clouds such as hosting.com and Rackspace, this doesn't matter, as all services are available by default.  
However, in clouds like Terremark and EC2, you will want to open at least 1 additional port most of the time.  
Here's how:

{% highlight java %}
import static org.jclouds.compute.options.TemplateOptions.Builder.inboundPorts;
...
      Template template = client.templateBuilder().options(inboundPorts(22, 8080)).build();
      // start 2 nodes
      nodes = client.createNodesInGroup(group, 2, template);

{% endhighlight %}
### Template Matching

#### Matching an operating system version
{% highlight java %}
template = client.templateBuilder().hardwareId(InstanceType.M1_SMALL)
                  .osVersionMatches("10.04").imageDescriptionMatches("ubuntu-images").osFamily(OsFamily.UBUNTU).build();
{% endhighlight %}

### Authorizing your RSA SSH Public Key

The compute API supports authorizing a public key on a node or node set.  
This allows you to use a single credential across the entire set of nodes.  
Note that this does not change the private key of the node.  
If you are interested in changing the private key, look at the `installPrivateKey` option.

In order to use this feature, you must generate or load your RSA public key into a String.  Set the option `authorizePublicKey` to this value.  Note that if you have done this correctly, your key will start with `ssh-rsa`.

Ex.
{% highlight java %}
import static org.jclouds.compute.options.TemplateOptions.Builder.authorizePublicKey;
...
      Template template = client.templateBuilder().options(authorizePublicKey(Payloads.newPayload(new File("/home/me/.ssh/id_rsa.pub")).build();
      // start 10 nodes
      nodes = client.createNodesInGroup(group, 10, template);
{% endhighlight %}

Note that SSH must be configured for this feature to work.

To install an SSH private key for a different user, override the credentials with the `overrideCredentialsWith` method.

### Installing your RSA SSH Private Key

The compute API supports replacing the node's private key with one you specify.  
This is different than authorizing your public key.  The private key determines the identity of 
root on your machine so that outgoing SSH can be authorized. You can use this feature alongside 
authorizing RSA to establish an SSH mesh.  An SSH mesh can be used to manage tools such as Hadoop.

In order to use this feature, you must generate or load your RSA key into a String.  Set the option 
`installPrivateKey` to this value.  Note that if you have done this correctly, your key will start with
`-----BEGIN RSA PRIVATE KEY-----`.

Ex.
{% highlight java %}
import static org.jclouds.compute.options.TemplateOptions.Builder.installPrivateKey;
...
      // start 10 nodes
      nodes = client.createNodesInGroup(group, 10, installPrivateKey(Files.toString(new File("/home/me/.ssh/id_rsa"));
{% endhighlight %}

Note that SSH must be configured for this feature to work.

### Adding a post-boot script

The compute API supports injection and execution of a single file as `root` post-bootup.  
The exact implementation depends on the features offered by the target cloud.

In order to use this feature, you must generate or load a script as a jclouds `Statement` or a String value.  
Set the option `runScript` to this value.

Ex.
{% highlight java %}
import static org.jclouds.compute.options.TemplateOptions.Builder.runScript;
...
      // start 10 nodes
      nodes = client.createNodesInGroup(group, 10, runScript(Files.toString(new File("runscript.sh")));
{% endhighlight %}

Note that SSH must be configured for this feature to work. 

#### Default Template
While many configurations may work with runScript, we setup the default template so that it can work 
without parameters (ex. `templateBulder().build()` works). 


##### 1.0.0

As of jclouds 1.0.0 here are the template patterns that represent the default template. 

*Unless specified below, the default template is `osFamily(UBUNTU).osVersionMatches("10.04").os64Bit(true)`*

_This is significantly out of date._


| *provider* | *default template* |
|------------|--------------------|
| vcloud/bluelock-vcdirector | osFamily(UBUNTU).os64Bit(true) |
| trmk-vcloudexpress | osFamily(UBUNTU).osDescriptionMatches(".*JeOS.*").os64Bit(true) |
| trmk-ecloud | osFamily(CENTOS).os64Bit(true) |
| ec2 | osFamily(AMZN_LINUX).os64Bit(true) |
| eucalyptus | osFamily(CENTOS) |
| gogrid | osFamily(CENTOS).imageNameMatches(".*w/ None.*") |

##### Test Scripts 

  * OperatingSystemPredicates.supportsApt().apply(node.getOperatingSystem())

{% highlight text %}
echo nameserver 208.67.222.222 >> /etc/resolv.conf
cp /etc/apt/sources.list /etc/apt/sources.list.old
sed 's~us.archive.ubuntu.com~mirror.anl.gov/pub~g' /etc/apt/sources.list.old >/etc/apt/sources.list
apt-get update
apt-get install -f -y --force-yes openjdk-6-jdk
{% endhighlight %}

  * OperatingSystemPredicates.supportsYum().apply(node.getOperatingSystem())

{% highlight bash %}
echo nameserver 208.67.222.222 >> /etc/resolv.conf
echo "[jdkrepo]" >> /etc/yum.repos.d/CentOS-Base.repo
echo "name=jdkrepository" >> /etc/yum.repos.d/CentOS-Base.repo
echo "baseurl=http://ec2-us-east-mirror.rightscale.com/epel/5/i386/" >> /etc/yum.repos.d/CentOS-Base.repo
echo "enabled=1" >> /etc/yum.repos.d/CentOS-Base.repo
yum --nogpgcheck -y install java-1.6.0-openjdk
echo "export PATH=\"/usr/lib/jvm/jre-1.6.0-openjdk/bin/:\$PATH\"" >> /root/.bashrc
{% endhighlight %}


### Creating non-root users

To add additional users on nodes, use the `UserAdd.Builder` facility, like this:
{% highlight java %}
UserAdd.Builder userBuilder = UserAdd.builder();
userBuilder.login("john.doe");
userBuilder.authorizeRSAPublicKey("john's public key");
Statement userBuilderStatement = userBuilder.build();
{% endhighlight %}

and run the built statement in `templateOptions.runScript` or add to a `StatementList`.

#### Note on internal implementation and debugging
The jclouds API allows many `Statements` to be built entirely from high-level concepts, 
without having to resort to OS-specific scripts. This enables developers to express what they mean without having
 to deal with the gory details of various OS flavors.
To see the commands that will be executed, print the result of `Statement.render(OsFamily.UNIX)`, for example.



## Clojure
## Setup
  * install [lein](http://github.com/technomancy/leiningen)
  * `lein new mygroup/myproject`
  * `cd myproject`
  * `vi project.clj`
  * for jclouds 1.1 and earlier (clojure 1.2 only)

{% highlight clojure %}
(defproject mygroup/myproject "1.0.0" 
  :description "FIXME: write"
  :dependencies [[org.clojure/clojure "1.2.0"]
                 [org.clojure/clojure-contrib "1.2.0"]
		 [org.jclouds/jclouds-allcompute "1.1.0"]])
{% endhighlight %}

    * for jclouds 1.2 / snapshot (clojure 1.2 and 1.3)
{% highlight clojure %}
(defproject mygroup/myproject "1.0.0" 
  :description "FIXME: write"
  :dependencies [[org.clojure/clojure "1.3.0"]
                 [org.clojure/core.incubator "0.1.0"]
                 [org.clojure/tools.logging "0.2.3"]
                 [org.jclouds/jclouds-allcompute "1.2.0-SNAPSHOT"]]
  :repositories {"jclouds-snapshot" "https://oss.sonatype.org/content/repositories/snapshots"}) 
{% endhighlight %}

  * `lein deps`


### Usage
Execute `lein repl` to get a repl, then paste the following or write your own code.  Clearly, 
you need to substitute your accounts and keys below.

{% highlight clojure %}
(use 'org.jclouds.compute)

(with-compute-service ["cloudservers" "email" "password"]
  (nodes))
{% endhighlight %}

The above will list all nodes with cloudservers. Here's an example of creating and running a small linux node with the group webserver, using ssh and log4j extensions:

{% highlight clojure %}  
(def provider "cloudservers")
(def user "email")
(def password "password")

(def my-compute 
  (compute-service provider user password :ssh :log4j))

(create-node "webserver" my-compute)
{% endhighlight %}

You'll likely want to run the following when you're done:

{% highlight clojure %}  
(destroy-nodes-in-group "webserver" my-compute)
{% endhighlight %}

## Tools

We've looked at many tools and chosen a few to spend time integrating with.  
These tools enable developers to focus on working code, as opposed to  build and infrastructure engineering

### Ant

Please check [[ApacheAntComputeGuide]] on how to use ant for compute API tasks.

