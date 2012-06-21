---
layout: jclouds
title: Quick Start - Elastic Block Store Models
---

# Introduction

Amazon EC2 (`aws-ec2`) and compatible apis such as Eucalyptus (`eucalyptus`) include support for 
remountable block devices, called EBS.  In jclouds, control features for this are exposed in 3 ways: 
hooks within ComputeService (`EC2TemplateOptions` and viewing via `NodeMetadata`), 
directly through the provider api (`ElasticBlockStoreClient`), and our clojure functions (`ebs2.clj`).


## ComputeService Integration
jclouds reports current status of volume mappings inside a Node's Hardware field.  
You can setup volume mappings with extended template options.

### Viewing the current volumes attached to a node

EBS volumes will report in the collection returned by `node.getHardware().getVolumes()` as type `Volume.Type.SAN`

### Setting up Volume Mappings

jclouds also has a means to setup volume mappings when you start your nodes.  Here's an example:

{% highlight java %}
      template.getOptions().as(EC2TemplateOptions.class)//
               // .unmapDeviceNamed("/dev/foo)
               .mapEphemeralDeviceToDeviceName("/dev/sdm", "ephemeral0")//
               .mapNewVolumeToDeviceName("/dev/sdn", volumeSize, true)//
               .mapEBSSnapshotToDeviceName("/dev/sdo", snapshot.getId(), volumeSize, true);
{% endhighlight %}

## Elastic Block Store Client

getting access to the client

{% highlight java %}
      ElasticBlockStoreClient ebsClient = EC2Client.class.cast(context.getProviderSpecificContext().getApi())
               .getElasticBlockStoreServices();
{% endhighlight %}

generating a snapshot

{% highlight java %}
      Snapshot snapshot = ebsClient.createSnapshotInRegion(volume.getRegion(), volume.getId());
{% endhighlight %}


## ebs2.clj 

{% highlight clojure %}
(use 'org.jclouds.ec2.ebs2)

(def ec2 (compute-service "aws-ec2" "ACCESSKEY" "SECRETKEY" :sshj))

(volumes ec2)

{% endhighlight %}
