# Administation viewpoint

These steps are usually in charge of cloud provider, in this case it's up to you

The experiment assumes almost nothing: only a running Ubuntu 11.04 where java6 and maven3 are installed.

## Prepare your Master VM
By launching "VirtualboxAdministrationTest" an helper class to execute a number of preliminary steps needed to have a properly configured ubuntu box
and to create a golden template for your experiment.

The "VirtualboxAdministrationTest" helper will run these preliminary steps:

1.  Create a working dir on 'user.home' called by default "jclouds-virtualbox-test" 
(this value can be overwritten directly on the commandline using -Dtest.virtualbox.workingDir)
2.  Install VBOX-OSE (available through ubuntu repo) 
3.  Download a VDI from [here](http://downloads.sourceforge.net/virtualboximage/centos-5.2-x86.7z) into your working dir
3.  Install p7zip on your ubuntu machine, if missing
3.  Extract the 7z archive into your working dir
4.  Download VirtualBox Guest Additions ISO (tested with VBoxGuestAdditions_4.0.2-update-69551.iso) into "jclouds-virtualbox-test"
5.  Disable login credential: $ VBoxManage setproperty websrvauthlibrary null
6.  Start webservice with increasead timeout: $ /usr/bin/vboxwebsrv --timeout 10000 and then will:
-   Clone originalDisk to clonedDisk in workingDir
-   Set NAT on network interface eth0 
-   Set port forwarding localhost:2222->guest:22 
-   Create a VM in the the vbox default machine folder ("/user.home/VirtualBox VMs")
-   Mount guest additions ISO 
-   Install guest additions 
-   Shutdown VM 
-   Remove port forwarding rule
-   Remove NAT network interface 
-   Detach vdisk from VM template
	
At this stage, you can use your template as a master for your "private" cloud through VirtualBoxLiveTest

## Bootstrap your private cloud through VirtualBoxLiveTest helper
To use this helper, you have to specify the name of the VM (-Dtest.virtualbox.vmname=<VM-NAME>)
and choose the numberOfVirtualMachine you need using -Dtest.virtualbox.numberOfVirtualMachine=<#ofVMs>,
These VMs will be cloned starting from the master VM created before

It will create a "numberOfVirtualMachine" in a vbox default machine folder with 'VMName_i' name

NB: for perfomance reason, these VM will share the same disk (the template created at step 1) attached in "multiattach mode" (http://www.virtualbox.org/manual/ch05.html#hdimagewrites) 