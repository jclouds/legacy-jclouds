---
layout: jclouds
title: Using jclouds ant task for Compute API Operations
---
# Using jclouds ant task for Compute API Operations

Ant is a popular Java build tool...kind of like make.  We have written a few Ant tasks in the 
[antcontrib](http://jclouds.googlecode.com/svn/trunk/tools/antcontrib) project that make building cloud projects easier.

### Setup

You must be using JDK6 and Ant 1.7.1 for these tasks to work.  To prepare Ant, place required libraries 
in the `lib` folder or use Ant's Maven integration to download them dynamically. 

#### Maven classpath

Set up your Ant `build.xml` to obtain a classpath ref called `jclouds.classpath`.  
In order for this to work, the [Ant Maven integration JAR](http://www.apache.org/dyn/closer.cgi/maven/binaries/maven-ant-tasks-2.1.0.jar) 
must be in your Ant `lib` directory.

Here's an example that gets all the libraries needed for the compute task.

{% highlight xml %}
<project group="your_project" default="your_target" basedir="." xmlns:artifact="antlib:org.apache.maven.artifact.ant">

  <typedef resource="org/apache/maven/artifact/ant/antlib.xml" />

  <artifact:localRepository id="local.repository" path="${user.home}/.m2/repository" />
  <artifact:remoteRepository id="jclouds.repository" url="https://oss.sonatype.org/content/repositories/releases" />

  <artifact:dependencies pathId="jclouds.classpath">
    <dependency groupId="org.jclouds" artifactId="jclouds-antcontrib" version="1.0.0" />
    <dependency groupId="org.jclouds" artifactId="jclouds-rimuhosting" version="1.0.0" />
    <dependency groupId="org.jclouds" artifactId="jclouds-aws" version="1.0.0" />
    <dependency groupId="org.jclouds" artifactId="jclouds-rackspace" version="1.0.0" />
    <dependency groupId="org.jclouds" artifactId="jclouds-terremark" version="1.0.0" />
    <localRepository refid="local.repository" />
    <remoteRepository refid="jclouds.repository" />
  </artifact:dependencies>
{% endhighlight %}

### Compute Task

The _compute_ task allows you to manage cloud nodes within your build script. 
 It supports simple commands such as _create_, _get_, _list_, _list-details_, and _destroy_. 
In order to use this task, you must load the jclouds 
[antcontrib](http://github.com/jclouds/jclouds/blob/1.0.0/tools/antcontrib) libraries and those of the provider.  

*NOTE:* This is still an alpha product.  Please expect implementation and usage to change.

Here is the current antcontrib library
|| *Artifact* || *Version* ||
|| jclouds-antcontrib || 1.0.0 ||

 Once you have your classpath in order, define the Ant task like below:

{% highlight xml %}
<typedef group="compute" classgroup="org.jclouds.tools.ant.taskdefs.compute.ComputeTask" classpathref="jclouds.classpath" />
{% endhighlight %}
#### Basic usag

If you are using the default [build.xml](http://github.com/jclouds/jclouds/blob/1.0.0/tools/antcontrib/samples/compute/build.xml) 
and have the [Ant Maven integration JAR](http://jclouds.googlecode.com/svn/trunk/tools/ant-plugin/maven-ant-tasks-2.1.0.jar) 
in you current directory, it is easy to start.  The syntax is:

{% highlight bash %}
ant  -Dprovider=provider -Ddriver=mvn-dependency -Didentity=account -Dcredential=key command
{% endhighlight %}

Example:

{% highlight bash %}
ant  -Dprovider=trmk-vcloudexpress 
     -Ddriver=terremark 
     -Didentity=your_registered_email 
     -Dcredential=your_password 
     -Dgroup=eduardo destroy
{% endhighlight %}
#### create

The _create_ target takes you through the quickest way to startup one or more nodes.  
By default, it opens port 22 to the internet through your public IP.  The default OS is Ubuntu.

*Note:* You must specify the ant property `group`.

Here's an example run, which creates 2 nodes.

{% highlight text %}
$ ant  -Dprovider=bluelock-vcdirector -Ddriver=bluelock -Didentity=acole@jclouds -Dcredential=**** -Dcount=2 -Dgroup=test create
create:
    [input] skipping input as property group has already been set.
  [compute] create group: test, count: 2, hardware: SMALLEST, os: UBUNTU
      >> searching params([biggest=false, fastest=false, imageName=null, imageDescription=null, imageId=null, imageVersion=null, location=[id=https://vcenterprise.bluelock.com/api/v1.0/vdc/105186609, scope=ZONE, description=Jclouds-Commit-compG1xstorA01, parent=https://vcenterprise.bluelock.com/api/v1.0/org/744127301], minCores=0.0, minRam=0, osFamily=ubuntu, osName=null, osDescription=null, osVersion=null, osArch=null, os64Bit=null, hardwareId=null])
      <<   matched hardware([id=https://vcenterprise.bluelock.com/api/v1.0/vAppTemplate/vappTemplate-1785525267, providerId=https://vcenterprise.bluelock.com/api/v1.0/vAppTemplate/vappTemplate-1785525267, name=jeos_v2, processors=[[cores=1.0, speed=1.0]], ram=256, volumes=[[id=0, type=LOCAL, size=1.0, device=null, durable=true, isBootDevice=true]], supportsImage=idEquals(https://vcenterprise.bluelock.com/api/v1.0/vAppTemplate/vappTemplate-1785525267)])
      <<   matched image([id=https://vcenterprise.bluelock.com/api/v1.0/vAppTemplate/vappTemplate-1785525267, name=jeos_v2, operatingSystem=[osType=UBUNTU, version=null, arch=null, description=Ubuntu Linux (32-bit)], description=jeos_v2, version=, location=[id=https://vcenterprise.bluelock.com/api/v1.0/org/744127301, scope=REGION, description=jclouds, parent=bluelock-vcdirector]])
      >> running 2 nodes group(test) location(https://vcenterprise.bluelock.com/api/v1.0/vdc/105186609) image(https://vcenterprise.bluelock.com/api/v1.0/vAppTemplate/vappTemplate-1785525267) hardwareProfile(https://vcenterprise.bluelock.com/api/v1.0/vAppTemplate/vappTemplate-1785525267) options([customizationScript=null, inboundPorts=[22], privateKey=true, publicKey=true, runScript=false, port:seconds=-1:-1, metadata/details: false])
      >> starting node(test-c9c) group(test)
      >> starting node(test-6d7) group(test)
      >> instantiating vApp vDC(https://vcenterprise.bluelock.com/api/v1.0/vdc/105186609) template(https://vcenterprise.bluelock.com/api/v1.0/vAppTemplate/vappTemplate-1785525267) name(test-6d7) options(InstantiateVAppTemplateOptions [cpuCount=1, memorySizeMegabytes=256, diskSizeKilobytes=1048576, networkConfig=[], customizeOnInstantiate=null, deploy=true, powerOn=true]) 
      >> instantiating vApp vDC(https://vcenterprise.bluelock.com/api/v1.0/vdc/105186609) template(https://vcenterprise.bluelock.com/api/v1.0/vAppTemplate/vappTemplate-1785525267) name(test-c9c) options(InstantiateVAppTemplateOptions [cpuCount=1, memorySizeMegabytes=256, diskSizeKilobytes=1048576, networkConfig=[], customizeOnInstantiate=null, deploy=true, powerOn=true]) 
      << instantiated VApp(test-c9c)
      << instantiated VApp(test-6d7)
      << ready vApp(test-c9c)
      << ready vApp(test-6d7)
      << RUNNING node(https://vcenterprise.bluelock.com/api/v1.0/vApp/vapp-1327477559)
      << RUNNING node(https://vcenterprise.bluelock.com/api/v1.0/vApp/vapp-1432200068)
      >> authorizing rsa public key for root@174.47.101.69
      << complete(0)
      >> installing rsa key for root@174.47.101.69
      << options applied node(https://vcenterprise.bluelock.com/api/v1.0/vApp/vapp-1327477559)
      >> authorizing rsa public key for root@174.47.101.68
      << complete(0)
      >> installing rsa key for root@174.47.101.68
      << options applied node(https://vcenterprise.bluelock.com/api/v1.0/vApp/vapp-1432200068)
  [compute]    node id=https://vcenterprise.bluelock.com/api/v1.0/vApp/vapp-1327477559, name=test-6d7, group=test, location=[id=https://vcenterprise.bluelock.com/api/v1.0/vdc/105186609, scope=ZONE, description=Jclouds-Commit-compG1xstorA01, parent=https://vcenterprise.bluelock.com/api/v1.0/org/744127301], state=RUNNING, publicIp=174.47.101.69, privateIp=, hardware=[id=https://vcenterprise.bluelock.com/api/v1.0/vApp/vapp-1327477559, providerId=https://vcenterprise.bluelock.com/api/v1.0/vApp/vapp-1327477559, name=test-6d7, processors=[[cores=1.0, speed=1.0]], ram=256, volumes=[[id=0, type=LOCAL, size=1.0, device=null, durable=true, isBootDevice=true]], supportsImage=idEquals(https://vcenterprise.bluelock.com/api/v1.0/vApp/vapp-1327477559)]
  [compute]    node id=https://vcenterprise.bluelock.com/api/v1.0/vApp/vapp-1432200068, name=test-c9c, group=test, location=[id=https://vcenterprise.bluelock.com/api/v1.0/vdc/105186609, scope=ZONE, description=Jclouds-Commit-compG1xstorA01, parent=https://vcenterprise.bluelock.com/api/v1.0/org/744127301], state=RUNNING, publicIp=174.47.101.68, privateIp=, hardware=[id=https://vcenterprise.bluelock.com/api/v1.0/vApp/vapp-1432200068, providerId=https://vcenterprise.bluelock.com/api/v1.0/vApp/vapp-1432200068, name=test-c9c, processors=[[cores=1.0, speed=1.0]], ram=256, volumes=[[id=0, type=LOCAL, size=1.0, device=null, durable=true, isBootDevice=true]], supportsImage=idEquals(https://vcenterprise.bluelock.com/api/v1.0/vApp/vapp-1432200068)]

{% endhighlight %}
#### Additional Ports
 You can set the property `openports` to a comma-separated list of ports, if you wish.  
ex.
{% highlight xml %}
<compute actions="create" provider="${jclouds.compute.url}">
	<nodes privatekeyfile="${privatekeyfile}" publickeyfile="${publickeyfile}" group="${group}" count="${count}" os="${os}" hardware="SMALLEST" hostproperty="host" usernameproperty="username" passwordproperty="password" openports="22,80,443" />
</compute>
{% endhighlight xml %}
#### Post-installation Script

You can also specify the file property `runscript` which will be invoked as root on the machine following startup.  
This is performed in a portable manner, and potentially uses SSH.

#### Example Run

Here's an example run that executes a runscript and opens a couple ports.  In this case, we are using the task to create a VMware vCloud vApp on the BlueLock network: 
{% highlight text %}
      >> searching params([biggest=false, fastest=false, imageName=null, imageDescription=null, imageId=null, imageVersion=null, location=[id=https://vcenterprise.bluelock.com/api/v1.0/vdc/105186609, scope=ZONE, description=Jclouds-Commit-compG1xstorA01, parent=https://vcenterprise.bluelock.com/api/v1.0/org/744127301], minCores=0.0, minRam=0, osFamily=ubuntu, osName=null, osDescription=null, osVersion=null, osArch=null, os64Bit=null, hardwareId=null])
      <<   matched hardware([id=https://vcenterprise.bluelock.com/api/v1.0/vAppTemplate/vappTemplate-1785525267, providerId=https://vcenterprise.bluelock.com/api/v1.0/vAppTemplate/vappTemplate-1785525267, name=jeos_v2, processors=[[cores=1.0, speed=1.0]], ram=256, volumes=[[id=0, type=LOCAL, size=1.0, device=null, durable=true, isBootDevice=true]], supportsImage=idEquals(https://vcenterprise.bluelock.com/api/v1.0/vAppTemplate/vappTemplate-1785525267)])
      <<   matched image([id=https://vcenterprise.bluelock.com/api/v1.0/vAppTemplate/vappTemplate-1785525267, name=jeos_v2, operatingSystem=[osType=UBUNTU, version=null, arch=null, description=Ubuntu Linux (32-bit)], description=jeos_v2, version=, location=[id=https://vcenterprise.bluelock.com/api/v1.0/org/744127301, scope=REGION, description=jclouds, parent=bluelock-vcdirector]])
      >> running 1 node group(test) location(https://vcenterprise.bluelock.com/api/v1.0/vdc/105186609) image(https://vcenterprise.bluelock.com/api/v1.0/vAppTemplate/vappTemplate-1785525267) hardwareProfile(https://vcenterprise.bluelock.com/api/v1.0/vAppTemplate/vappTemplate-1785525267) options([customizationScript=null, inboundPorts=[22, 8080], privateKey=true, publicKey=true, runScript=true, port:seconds=-1:-1, metadata/details: false])
      >> starting node(test-669) group(test)
      >> instantiating vApp vDC(https://vcenterprise.bluelock.com/api/v1.0/vdc/105186609) template(https://vcenterprise.bluelock.com/api/v1.0/vAppTemplate/vappTemplate-1785525267) name(test-669) options(InstantiateVAppTemplateOptions [cpuCount=1, memorySizeMegabytes=256, diskSizeKilobytes=1048576, networkConfig=[], customizeOnInstantiate=null, deploy=true, powerOn=true]) 
      << instantiated VApp(test-669)
      << ready vApp(test-669)
      << RUNNING node(https://vcenterprise.bluelock.com/api/v1.0/vApp/vapp-409958461)
      >> authorizing rsa public key for root@174.47.101.68
      << initialized(0)
      >> running [./runscript start] as root@174.47.101.68
      << complete(0)
      << start(0)
      << complete(true)
      << stdout from runscript as root@174.47.101.68
Setting up openjdk-6-jre (6b18-1.8.1-0ubuntu1~8.04.3) ...

Setting up openjdk-6-jdk (6b18-1.8.1-0ubuntu1~8.04.3) ...

Setting up ca-certificates-java (20100406ubuntu1~hardy1) ...
creating /etc/ssl/certs/java/cacerts...
done.

Processing triggers for libc6 ...
ldconfig deferred processing now taking place
      << stderr from runscript as root@174.47.101.68
dpkg-preconfigure: unable to re-open stdin: 
dpkg-preconfigure: unable to re-open stdin: 
dpkg-preconfigure: unable to re-open stdin: 
      >> installing rsa key for root@174.47.101.68
      << options applied node(https://vcenterprise.bluelock.com/api/v1.0/vApp/vapp-409958461)
  [compute]    node id=https://vcenterprise.bluelock.com/api/v1.0/vApp/vapp-409958461, name=test-669, group=test, location=[id=https://vcenterprise.bluelock.com/api/v1.0/vdc/105186609, scope=ZONE, description=Jclouds-Commit-compG1xstorA01, parent=https://vcenterprise.bluelock.com/api/v1.0/org/744127301], state=RUNNING, publicIp=174.47.101.68, privateIp=, hardware=[id=https://vcenterprise.bluelock.com/api/v1.0/vApp/vapp-409958461, providerId=https://vcenterprise.bluelock.com/api/v1.0/vApp/vapp-409958461, name=test-669, processors=[[cores=1.0, speed=1.0]], ram=256, volumes=[[id=0, type=LOCAL, size=1.0, device=null, durable=true, isBootDevice=true]], supportsImage=idEquals(https://vcenterprise.bluelock.com/api/v1.0/vApp/vapp-409958461)]
{% endhighlight %}

#### Integration with other tasks
In order to integrate with other tasks, you'll want to use the `hostproperty`, `usernameproperty`, and 
`passwordproperty` options.  
These will set Ant project properties accordingly on successful completion.

See an example below:
{% highlight xml %}
<compute actions="destroy,create" provider="${url}">
   <nodes group="${group}" os="UBUNTU" hardware="SMALLEST"
         runscript="runscript.sh" openports="22,${listenport}"
         privatekeyfile="${privatekeyfile}" publickeyfile="${publickeyfile}"
         hostproperty="host" usernameproperty="username" />
</compute>
{% endhighlight %}
##### Alternate endpoints 
If you are using an alternate endpoint, or standing up a private cloud without a proper certifcate,
 you can add options to ensure clouds will be flexible on ssl

{% highlight bash %}
ant  -Dprovider=vcloud -Ddriver=vcloud -Dvcloud.endpoint=https://192.168.0.1/api -Djclouds.trust-all-certs=true -Djclouds.relax-hostname=true -Didentity=user@org -Dcredential='password'
{% endhighlight %}
#### get

The _get_ target retrieves details about the node, including its IP addresses and state.

*Note:* You must specify the Ant property `group`.
{% highlight bash %}
$ ant  -Dprovider=bluelock-vcdirector -Ddriver=bluelock -Didentity=acole@jclouds -Dcredential=XXXX -Dgroup=test get
get-group:
    [input] skipping input as property group has already been set.
  [compute] get group: test
      >> listing node details matching(AlwaysTrue)
      << list(1)
  [compute]    node id=https://vcenterprise.bluelock.com/api/v1.0/vApp/vapp-409958461, name=test-669, group=test, location=[id=https://vcenterprise.bluelock.com/api/v1.0/vdc/105186609, scope=ZONE, description=Jclouds-Commit-compG1xstorA01, parent=https://vcenterprise.bluelock.com/api/v1.0/org/744127301], state=RUNNING, publicIp=174.47.101.68, privateIp=, hardware=[id=https://vcenterprise.bluelock.com/api/v1.0/vApp/vapp-409958461, providerId=https://vcenterprise.bluelock.com/api/v1.0/vApp/vapp-409958461, name=test-669, processors=[[cores=1.0, speed=1.0]], ram=256, volumes=[[id=0, type=LOCAL, size=1.0, device=null, durable=true, isBootDevice=true]], supportsImage=idEquals(https://vcenterprise.bluelock.com/api/v1.0/vApp/vapp-409958461)]
{% endhighlight %}
#### list
_list_ will show you the mapping between names and node IDs.

{% highlight bash %}
$ ant  -Dprovider=bluelock-vcdirector -Ddriver=bluelock -Didentity=acole@jclouds -Dcredential=XXXX list
list:
  [compute] list
      >> listing nodes
      << list(1)
  [compute]    location=[id=https://vcenterprise.bluelock.com/api/v1.0/vdc/105186609, scope=ZONE, description=Jclouds-Commit-compG1xstorA01, parent=https://vcenterprise.bluelock.com/api/v1.0/org/744127301], id=https://vcenterprise.bluelock.com/api/v1.0/vApp/vapp-409958461, group=test-669
{% endhighlight %}

#### list-details 

_list-details_ is the same as doing `get` for each node.

{% highlight bash %}
$ ant  -Dprovider=trmk-vcloudexpress -Ddriver=terremark -Didentity=adrian@jclouds.org -Dcredential=**** list-details
list-details:
  [compute] list details
      >> listing nodes
      << list(1)
  [compute]    node id=https://services.vcloudexpress.terremark.com/api/v0.8a-ext1.6/vapp/368599, name=vcxrun-28d, group=vcxrun, location=[id=https://services.vcloudexpress.terremark.com/api/v0.8a-ext1.6/vdc/32, scope=ZONE, description=Miami Environment 1, parent=https://services.vcloudexpress.terremark.com/api/v0.8a-ext1.6/org/48], state=RUNNING, publicIp=204.51.114.153, privateIp=10.114.34.131, hardware=[id=https://services.vcloudexpress.terremark.com/api/v0.8a-ext1.6/vapp/368599, providerId=https://services.vcloudexpress.terremark.com/api/v0.8a-ext1.6/vapp/368599, name=vcxrun-28d, processors=[[cores=1.0, speed=1.0]], ram=512, volumes=[[id=0, type=LOCAL, size=4.0, device=null, durable=true, isBootDevice=true]], supportsImage=idEquals(https://services.vcloudexpress.terremark.com/api/v0.8a-ext1.6/vapp/368599)

BUILD SUCCESSFUL
Total time: 14 seconds
{% endhighlight %}

#### list-locations
_list-locations_ will display all the locations you can configure a node to run in.

{% highlight bash %}
$ ant  -Dprovider=trmk-vcloudexpress -Ddriver=terremark -Didentity=adrian@jclouds.org -Dcredential=**** list-locations
list-locations:
  [compute] list locations
  [compute]    location id=https://services.vcloudexpress.terremark.com/api/v0.8a-ext1.6/vdc/32, scope=ZONE, description=Miami Environment 1, parent=[id=https://services.vcloudexpress.terremark.com/api/v0.8a-ext1.6/org/48, scope=REGION, description=adrian@jclouds.org, parent=trmk-vcloudexpress]
{% endhighlight %}
#### list-images
_list-images_ will display all the images you can run on a node.

{% highlight bash %}
$ ant  -Dprovider=bluelock-vcdirector -Ddriver=bluelock -Didentity=acole@jclouds -Dcredential=**** list-images
list-images:
  [compute] list images
  [compute]    image location=[id=https://vcenterprise.bluelock.com/api/v1.0/org/744127301, scope=REGION, description=jclouds, parent=bluelock-vcdirector], id=https://vcenterprise.bluelock.com/api/v1.0/vAppTemplate/vappTemplate-639788890, name=RHEL5_v2, version=, osArch=null, osfam=rhel, osdesc=Red Hat Enterprise Linux 5 (64-bit), desc=RHEL5_v2
  [compute]    image location=[id=https://vcenterprise.bluelock.com/api/v1.0/org/744127301, scope=REGION, description=jclouds, parent=bluelock-vcdirector], id=https://vcenterprise.bluelock.com/api/v1.0/vAppTemplate/vappTemplate-1015395941, name=Windows2008R2_v1, version=, osArch=null, osfam=unrecognized, osdesc=Microsoft Windows Server 2008 R2 (64-bit), desc=This is a special place-holder used for disconnected network interfaces.
  [compute]    image location=[id=https://vcenterprise.bluelock.com/api/v1.0/org/744127301, scope=REGION, description=jclouds, parent=bluelock-vcdirector], id=https://vcenterprise.bluelock.com/api/v1.0/vAppTemplate/vappTemplate-254826226, name=SLES11_v1, version=, osArch=null, osfam=suse, osdesc=Suse Linux Enterprise 11 (64-bit), desc=This is a special place-holder used for disconnected network interfaces.
  [compute]    image location=[id=https://vcenterprise.bluelock.com/api/v1.0/org/744127301, scope=REGION, description=jclouds, parent=bluelock-vcdirector], id=https://vcenterprise.bluelock.com/api/v1.0/vAppTemplate/vappTemplate-1785525267, name=jeos_v2, version=, osArch=null, osfam=ubuntu, osdesc=Ubuntu Linux (32-bit), desc=jeos_v2
  [compute]    image location=[id=https://vcenterprise.bluelock.com/api/v1.0/org/744127301, scope=REGION, description=jclouds, parent=bluelock-vcdirector], id=https://vcenterprise.bluelock.com/api/v1.0/vAppTemplate/vappTemplate-699683881, name=Ubuntu10.04_v2, version=, osArch=null, osfam=ubuntu, osdesc=Ubuntu Linux (64-bit), desc=Ubuntu10.04_v2

{% endhighlight %}
#### list-hardware
_list-hardware_  will display all the hardware profiles you can configure a node with.

{% highlight bash %}
$ ant  -Dprovider=bluelock-vcdirector -Ddriver=bluelock -Didentity=acole@jclouds -Dcredential=**** list-hardware
list-hardware:
  [compute] list hardwares
  [compute]    hardware id=https://vcenterprise.bluelock.com/api/v1.0/vAppTemplate/vappTemplate-639788890, cores=1.0, ram=384, volumes=[[id=0, type=LOCAL, size=30.0, device=null, durable=true, isBootDevice=true]]
  [compute]    hardware id=https://vcenterprise.bluelock.com/api/v1.0/vAppTemplate/vappTemplate-1015395941, cores=1.0, ram=4096, volumes=[[id=0, type=LOCAL, size=40.0, device=null, durable=true, isBootDevice=true]]
  [compute]    hardware id=https://vcenterprise.bluelock.com/api/v1.0/vAppTemplate/vappTemplate-254826226, cores=1.0, ram=512, volumes=[[id=0, type=LOCAL, size=30.0, device=null, durable=true, isBootDevice=true]]
  [compute]    hardware id=https://vcenterprise.bluelock.com/api/v1.0/vAppTemplate/vappTemplate-1785525267, cores=1.0, ram=256, volumes=[[id=0, type=LOCAL, size=1.0, device=null, durable=true, isBootDevice=true]]
  [compute]    hardware id=https://vcenterprise.bluelock.com/api/v1.0/vAppTemplate/vappTemplate-699683881, cores=1.0, ram=512, volumes=[[id=0, type=LOCAL, size=30.0, device=null, durable=true, isBootDevice=true]]

{% endhighlight %}
#### destroy
_destroy_ does the reverse of create: it takes down your internet services, powers off and destroys all nodes matching a group.

*Note:* You must specify the Ant property `group`.
{% highlight bash %}
$ ant  -Dprovider=trmk-vcloudexpress -Ddriver=terremark -Didentity=adrian@jclouds.org -Dcredential=**** -Dgroup=vcxrun destroy
destroy-id:

destroy-group:
    [input] skipping input as property group has already been set.
  [compute] destroy group: vcxrun
      >> destroying nodes matching(withGroup(vcxrun))
      >> destroying node(https://services.vcloudexpress.terremark.com/api/v0.8a-ext1.6/vapp/368599)
      >> deleting Node(vcxrun-28d-22) 204.51.114.153:22 -> 10.114.34.131:22
      << deleted Node(vcxrun-28d-22)
      >> deleting InternetService(vcxrun-28d-22) 204.51.114.153:22
      << deleted InternetService(vcxrun-28d-22)
      >> deleting PublicIpAddress(https://services.vcloudexpress.terremark.com/api/v0.8a-ext1.6/extensions/publicIp/368602) 204.51.114.153
      << deleted PublicIpAddress(https://services.vcloudexpress.terremark.com/api/v0.8a-ext1.6/extensions/publicIp/368602)
      >> powering off vApp(vcxrun-28d), current status: ON
      << OFF vApp(vcxrun-28d)
      >> deleting vApp(vcxrun-28d)
      << deleted vApp(vcxrun-28d))
      << destroyed node(https://services.vcloudexpress.terremark.com/api/v0.8a-ext1.6/vapp/368599) success(true)
      << destroyed(1)
      >> deleting keyPair(jclouds#vcxrun#23)
      << deleted keyPair(jclouds#vcxrun#23)
{% endhighlight %}

