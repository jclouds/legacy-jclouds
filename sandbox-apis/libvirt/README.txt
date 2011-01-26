The libvirt library is used to interface with different virtualization technologies (http://libvirt.org/)

libvirt supports:
The Xen hypervisor on Linux and Solaris hosts.
The QEMU emulator
The KVM Linux hypervisor
The LXC Linux container system
The OpenVZ Linux container system
The User Mode Linux paravirtualized kernel
The VirtualBox hypervisor
The VMware ESX and GSX hypervisors
Storage on IDE/SCSI/USB disks, FibreChannel, LVM, iSCSI, NFS and filesystems

Getting Started Guide for jclouds-libvirt

install libvirt on your os
  * if os/x, see http://github.com/justinclift/libvirt
  * if you are using Linux, let's suppose you want to use KVM:
- install libvirt and KVM (http://www.linux-kvm.org/page/Main_Page). 

Remember to run
	egrep '(vmx|svm)' /proc/cpuinfo
		If nothing is printed, it means that your cpu does not support hardware virtualization.

Verify Installation
$ virsh -c qemu:///system list
 Id Name                 State
----------------------------------

(for Ubuntu users: look also at this good turorial https://help.ubuntu.com/community/KVM)

Create your first guest
- download, for example, an ubuntu 10.04 LTS ISO
- create a libvirt domain by using:
	virt-manager: a GUI tool at http://virt-manager.et.redhat.com/ 
	virt-install, a python script developed by Red Hat (sudo apt-get install python-virtinst)
	ubuntu-vm-builder, developed by Canonical. (sudo apt-get install ubuntu-vm-builder)
		NB: use Javascript tool that generates the parameters for ubuntu-vm-builder: http://people.ubuntu.com/~kirkland/ubuntu-vm-builder.html

Now that you have a libvirt domain, your workstation is ready to use jclouds-libvirt.
You can now download jclouds-libvirt and give a try by running 

ComputeServiceContext context = null;
      try {
         context = new ComputeServiceContextFactory()
               .createContext(new StandaloneComputeServiceContextSpec<Domain, Domain, Image, Datacenter>("libvirt",
                     endpoint, apiversion, identity, credential, new LibvirtComputeServiceContextModule(), ImmutableSet
                           .<Module> of()));

         Template defaultTemplate = context.getComputeService().templateBuilder()
         	.hardwareId("c7ff2039-a9f1-a659-7f91-e0f82f59d52e").imageId("1").build();
         	

         context.getComputeService().runNodesWithTag(domainName, 1, defaultTemplate);
        
      } catch (RunNodesException e) {
		e.printStackTrace();
	} finally {
         if (context != null)
            context.close();
      }

where identity=your_name, endpoint=qemu:///system
      and domainName equals to the name chosen during the creation of libvirt domain

NB: apiversion, credential can be null 







