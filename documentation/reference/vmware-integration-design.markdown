---
layout: jclouds
title: VMWare Integration Design & Approach
---
# VMWare Integration Design & Approach

## Overall goals and approach 

See [jclouds vsphere presentation](http://www.slideshare.net/jclouds/jclouds-vsphere)

## outstanding issues 
### Non-Clone approach 

How do we allow with creating machines from scratch instead of cloning?  For example network booting.

  * relationship between vmx file and cloning?
  * which VMware products support cloning? ( vmware esx/fusion stuff?)
  * template could be generified to support network boot or image as opposed to only image.

### Next steps 
`Adrian will propose a design`

## Difference between Image and Node 

Difference between image and node is that an image is immutable, and typically doesn't have a correlation to hardware.  
However (see vcloud), sometimes there is a direct relationship.

  * need a way to filter what is a node vs what is an image (template flag?)

### Next steps

`Andrea will clarify this sec`tion with current approach and applicability.

### Managing Images

Mapping VMware volumes to blobstore could help deal with image-based use cases such as groupging, bulk transfer, conversion, etc. 
 This could provide a clean and testable means to integrate with the compute service.

  * groups or metadata on the volume could map to blob.userMetadata and then be used in the vsphere driver
  * Blobstore: link to datastore to retrieve machines (upload, download)
  * convert image to ovf, ova via a blobstore transformer

#### Next steps 
`Patrick will have a look at this after creating the means to bootstrap vmware installs`

## Snapshots

The idea of snapshots doesn't exist yet in jclouds.  Closest is Image, which has an image version which could correlate to the snapshot date.
  * snapshot in vmware also includes memory state.  is this a problem?
### Next steps
`Adrian will propose a design`

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

### Next steps

`Andrew will normalize this information into a taxonomy and check on xebia's ability to contribute the host session framework`


## Bootstrapping VMware installs

We need a means for developers to test and verify our vmware support.  This needs to support cloning which may impact applicability.

### Next steps
`Patrick will make a puppet manifest. `

## links
  * [deck by andrea](http://www.slideshare.net/jclouds/jclouds-vsphere)
  * [for compatible libvirt/vsphere status](http://jedi.be/blog/2010/12/08/libvirt-0-8-6-and-vmware-esx/)
  * [][http://java.sun.com/javase/technologies/core/mntr-mgmt/javamanagement/JSR262_Interop.pdf]]
  * [[http://distributed-frostbite.blogspot.com/2010/08/managing-windows-with-ruby-part-1-or.html]]

## Implementation 
Adrian suggests to enhance the implementation in these areas:

  * add unit tests 
  * remove commented out code
  * explict the 'write' commands: trace all the commands that leave a trace on the server 
	(Ex if it failed halfway through which would need to be undone)
  * add an HTTP log of running a vm, using jclouds HttpHelper
  * retrieve log from vmware server by using http://www.doublecloud.org/2010/10/how-you-can-use-vsphere-apis-to-collect-vcenter-and-esx-logs/
  * how to verify via api the operation on vm

