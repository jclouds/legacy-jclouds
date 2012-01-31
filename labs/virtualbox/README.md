# Administation viewpoint

These steps are usually in charge of cloud provider, in this case it's up to you

The experiment assumes almost nothing: only a running Ubuntu 11.04 where java6 and maven3 are installed.

## Prepare your Master VM
By launching "VirtualboxAdministrationTest" an helper class to execute a number of preliminary steps needed to have a properly configured ubuntu box
and to create a golden template for your experiment.

The "VirtualboxAdministrationTest" helper will run these preliminary steps:

1.  Create a working dir on 'user.home' called by default "jclouds-virtualbox-test" 
(this value can be overwritten directly on the commandline using -Dtest.virtualbox.workingDir)
2.  Install Virtualbox from the internet (mac os x lion and ubuntu host are supported at the moment) 
3.  Download by default an ubuntu 10.04.3 server i386 ISO into "jclouds-virtualbox-test" from http://releases.ubuntu.com/10.04/ubuntu-10.04.3-server-i386.iso
4.  Download VirtualBox Guest Additions ISO into "jclouds-virtualbox-test" from http://download.virtualbox.org/virtualbox/4.1.8/VBoxGuestAdditions_4.1.8.iso
5.  Disable login credential: $ VBoxManage setproperty websrvauthlibrary null
6.  Start an embedded jetty server that serves a preseed file specifically written for ubuntu 11.04
7.  Start webservice with increasead timeout: $ /usr/bin/vboxwebsrv --timeout 10000 and then will:
-   create a "jclouds-virtaulbox-kickstart-admin" vm in the the vbox default machine folder ("/user.home/VirtualBox VMs")
-   install automatically the OS (ubuntu 11.04) in this vm using preseed  
-   Set NAT on network interface eth0 
-   Set port forwarding localhost:2222->guest:22 
-   Mount Guest Additions ISO 
-   Install Guest additions 
-   Shutdown VM 
- 	create a snapshot of this machine, to enabling linked clone feature. 
-   Remove port forwarding rule
-   Change NIC from NAt to bridge 
	
At this stage, you can use your template as a master for your "private" cloud through VirtualBoxLiveTest

## Bootstrap your private cloud through VirtualBoxLiveTest helper
To use this helper, you have to specify the name of the VM (-Dtest.virtualbox.vmname=<VM-NAME>)
and choose the numberOfVirtualMachine you need using -Dtest.virtualbox.numberOfVirtualMachine=<#ofVMs>,
These VMs will be cloned starting from the golden template VM created before

It will create a "numberOfVirtualMachine" in a vbox default machine folder with 'VMName_i' name, by cloning the golden template in linked mode with a bridged NIC.


** NOTE! **
Make sure you change your VirtualBox preferences to not auto-capture keyboard, and also set host key to none.  Otherwise you may accidentally screw-up automated installs.
