---
layout: jclouds
title: VMWare vSphere Design & Approach
---
# VMWare vSphere Design & Approach

This design document will overview our approach to creating a jclouds adapter for VMware vSphere 5.

## Overall goals

This adapter is intended for anyone who needs to run a small-scale deployment managed by jclouds. 

Tipically, developers and devops can benefit of this adapter that allows them to test/validate their jclouds code on a local, controlled environment before pointing the jclouds API towards a public supported provider.

See [supported providers](http://www.jclouds.org/documentation/reference/supported-providers/)

The overall design aims to give a cloud-ish experience to the jclouds user that want to use a vSphere installation instead of a compute provider.
At a very high level, jclouds-vsphere will be able to create new nodes by cloning an existing master vm (it could be also a template) that matches the requirements expressed using jclouds template (see [template](http://www.jclouds.org/documentation/userguide/compute/)).

Obviously vSphere has a number of missing features compared to any other compute provider that jclouds-vsphere needs to fill out.

Let's analyse them:

  * set of predefined images*: user can bootstrap a vm starting from a number of images available in any cloud provider (i.e. AMIs in EC2 context). This is an assumption that we can't do with vSphere, because these kind of images (vmware templates) could not be there.
  * VMware ESXi doesn't support clone API, vCenter Server does, so the jclouds-vsphere should check that the endpoint provided by the user supports this required API, when the context is created.

# Testing environment

## Host configuration

Thankfully, SoftLayer ended up donating equipment and licenses:
http://groups.google.com/group/jclouds-dev/browse_thread/thread/3e23bf8b10f2be97

To run jclouds-vsphere live tests against SoftLayer environment, we needed to:
* create the ESXi host via curl, using the SoftLayer api

$ curl -v -d @order.json "https://username:pwd@api.softlayer.com/rest/v3/SoftLayer_Product_Order/placeOrder.json"

where `order.json` is something like:

{
    "parameters": [{
        "complexType": "SoftLayer_Container_Product_Order_Hardware_Server",
        "packageId": 13,
        "location": 18171,
        "quantity": 1,
        "hardware": [
            {
                "hostname": "vmware",
                "domain": "example.com",
                "primaryNetworkComponent": {
                    "networkVlan": {"id": 1384}
                }
            }
        ],
        "prices": [
            {"id": 723},
            {"id": 14048},
            {"id": 883},
            {"id": 1267},
            {"id": 126},
            {"id": 272},
            {"id": 55},
            {"id": 57},
            {"id": 58},
            {"id": 876},
            {"id": 21},
            {"id": 51},
            {"id": 420},
            {"id": 418},
            {"id": 906}
        ]
    }]
}

This API call created a 'vmware.example.com' server at 50.23.154.28 (public IP) and 10.29.0.69 (private IP) with ESXi 4.1.0u1 installed with `root` and `vmadmin` user.

At SoftLayer, VMware hosts are setup to only be accessible from the private network by default, so a VPN connection is needed to reach vmware.example.com server the 10.29.0.69.
https://manage.softlayer.com/PrivateNetwork/vpn
https://manage.softlayer.com/Support/sslVpnTutorial

Later we manually configured the second NIC of `vmware.example.com` with the public IP address to simplify to avoid VPN. So now the server is publicly available at 50.23.154.28.

In order to test vSphere 5 API, we created a couple of guests:
* vCenter Server 5 appliance at 50.23.145.66 (VM Network 2 on ESXi 4)
* dhcp3-server at 50.23.154.68

This DHCP server is responsible for assigning public IP addresses (taken from  50.23.154.24/29) to the new VMs created by jclouds-vsphere.

## Guest configuration
An ubuntu 12.04 server LTS i386 is available as vmware template. This will be used as default jclouds template but the jclouds-vsphere.
NB: this template contains an annotation NOTE: ubuntu-12.04 used by jclouds to discover the OS and its version.

## Outstanding design issues

### Image builder

How do we allow with creating machines from scratch instead of cloning? Either you use an ISO or network booting, 
depending on the guest OS you wants to support, there a number of parameters that need to be passed to the OS installer.

For example, jclouds-vbox offers a solution for this problem inspired by [veewee](https://github.com/jedi4ever/veewee/):
jclouds-vbox passes a kickstart file by pushing to the installer a serie of scancodes using the keyboard api offered by virtualbox.

We need to verify if VMware offers a similar API.

Moreovere, VMware VM almost must have the VMware tools installed to enable a set of extra features to give more control over the VM.
Particularly, jclouds-vsphere needs to use a getIpAddress API only available if the VMware tools are installed.

We need to find a way to programmatically install the VMware tools possibly using ssh.

#### Next actions
Explore the solutions already available to build an image (veewee)

### Difference between Image and Node 

Difference between image and node is that an image is immutable, and typically doesn't have a correlation to hardware.  
However (see vcloud), sometimes there is a direct relationship.

* need a way to filter what is a node vs what is an image (template flag?)

#### Possible solutions
  1. VMware template: we could map jclouds image to VMware templates and jclouds node to VMware VM.
  2. VMware folders: jclouds can create different VMware folders where store images (named 'jclouds-images') and nodes, named as 'group' chosen using createNodesInGroup call.

## Low priority design issues

### Snapshots

The idea of snapshots doesn't exist yet in jclouds.  Closest is Image, but we probably won't use snapshot concept in jclouds-vsphere.

## Customization & bootstrap 

How do we generify the jclouds runScript/file injection to take advangroupe of alternate means?  
Doing so will allow easier mapping to async provisioners like ec2 spot, and applicability to windows (no-ssh).

  * ec2 userdata -> cloudinit
  * vnc -> not windows
  * run guest command -> vixjava
  * send rdp sequences (
  * run commands over rdp
  * access ssh over
  * winrm http://msdn.microsoft.com/en-us/library/aa384426(v=vs.85).aspx
  * enable VNC (vsphere level)
  * host session framework (xebia lib, yeah!)
  * flexiant use of default ip address from dhcp to pull customization
  * Push vs Pull (using default dhcp non configured) (flexiant?)

### Managing Images

Mapping VMware volumes to blobstore could help deal with image-based use cases such as groupging, bulk transfer, conversion, etc. 

This could provide a clean and testable means to integrate with the compute service.

  * groups or metadata on the volume could map to blob.userMetadata and then be used in the vsphere driver
  * Blobstore: link to datastore to retrieve machines (upload, download)
  * convert image to ovf, ova via a blobstore transformer

## Implementation

### Set up

To use jclouds-vsphere you will need:

  * one or more [ESXi 5 server](http://www.vmware.com/products/vsphere-hypervisor/overview.html)
  * a VMware vCenter server (physical or virtualized). VMware provides also a vCenter server [appliance](http://pubs.vmware.com/vsphere-50/index.jsp?topic=%2Fcom.vmware.vsphere.install.doc_50%2FGUID-25FCBA87-5D2F-4CB6-85D7-88899B4AC174.html)


### Assumptions

  * Waiting for a chosen mean to do the pre-install of an image, jclouds-vsphere will assume a pre-setting up VM with VMware tools as a master.
  * jclouds-vsphere will use initially [vijava](http://vijava.sourceforge.net/) lib 

### Action items
  * create jclouds-vsphere in jclouds/labs

## Relevant discussions
  * [VMware ESXi discussion](https://groups.google.com/forum/?fromgroups#!topic/jclouds-dev/dT3MkGT2eNo)
  * [Support vSphere](http://code.google.com/p/jclouds/issues/detail?id=167&q=vsphere)

## Resources
  * [Initial jclouds-vsphere deck](http://www.slideshare.net/jclouds/jclouds-vsphere)
  * [for compatible libvirt/vsphere status](http://jedi.be/blog/2010/12/08/libvirt-0-8-6-and-vmware-esx/)
  * [JSR 262](http://java.sun.com/javase/technologies/core/mntr-mgmt/javamanagement/JSR262_Interop.pdf)
  * [Managing Windows with Ruby](http://distributed-frostbite.blogspot.com/2010/08/managing-windows-with-ruby-part-1-or.html)
  * [Retrieve log from vmware server](http://www.doublecloud.org/2010/10/how-you-can-use-vsphere-apis-to-collect-vcenter-and-esx-logs/)
