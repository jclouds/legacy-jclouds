
#Enviroment setup

Note: These steps will be automated in the future:
- create a ~/.jclouds-vbox/ directory
- create a ~/.jclouds-vbox/isos directory
- download an ubuntu 11.04 server i386 ISO into "~/.jclouds-vbox/isos" from http://releases.ubuntu.com/11.04/ubuntu-11.04-server-i386.iso
- download VirtualBox Guest Additions ISO into "~/.jclouds-vbox/isos" from http://download.virtualbox.org/virtualbox/4.1.8/VBoxGuestAdditions_4.1.8.iso
- copy the following into an images.yaml file to place in"~/.jclouds-vbox/" :
"images:
    - id: myTestId
      name: ubuntu-11.04-server-i386
      description: ubuntu 11.04 server (i386)
      os_arch: x86
      os_family: ubuntu
      os_description: ubuntu
      os_version: 11.04
      iso: http://releases.ubuntu.com/11.04/ubuntu-11.04-server-i386.iso
      keystroke_sequence: |
                <Esc><Esc><Enter> 
                /install/vmlinuz noapic preseed/url=http://10.0.2.2:8080/src/test/resources/preseed.cfg 
                debian-installer=en_US auto locale=en_US kbd-chooser/method=us 
                hostname=vmName 
                fb=false debconf/frontend=noninteractive 
                keyboard-configuration/layout=USA keyboard-configuration/variant=USA console-setup/ask_detect=false 
                initrd=/install/initrd.gz -- <Enter>
      preseed_cfg: |                
                      ## Options to set on the command line
                      d-i debian-installer/locale string en_US.utf8
                      d-i console-setup/ask_detect boolean false
                      d-i console-setup/layout string USA
                      d-i netcfg/get_hostname string unassigned-hostname
                      d-i netcfg/get_domain string unassigned-domain
                      # Continue without a default route
                      # Not working , specify a dummy in the DHCP
                      d-i time/zone string UTC
                      d-i clock-setup/utc-auto boolean true
                      d-i clock-setup/utc boolean true
                      d-i kbd-chooser/method	select	American English
                      d-i netcfg/wireless_wep string
                      d-i base-installer/kernel/override-image string linux-server
                      # Choices: Dialog, Readline, Gnome, Kde, Editor, Noninteractive
                      d-i debconf debconf/frontend select Noninteractive
                      d-i pkgsel/install-language-support boolean false
                      tasksel tasksel/first multiselect standard, ubuntu-server
                      d-i partman-auto/method string lvm
                      d-i partman-lvm/confirm boolean true
                      d-i partman-lvm/device_remove_lvm boolean true
                      d-i partman-auto/choose_recipe select atomic
                      d-i partman/confirm_write_new_label boolean true
                      d-i partman/confirm_nooverwrite boolean true
                      d-i partman/choose_partition select finish
                      d-i partman/confirm boolean true
                      # Write the changes to disks and configure LVM?
                      d-i partman-lvm/confirm boolean true
                      d-i partman-lvm/confirm_nooverwrite boolean true
                      d-i partman-auto-lvm/guided_size string max
                      ## Default user, we can get away with a recipe to change this
                      d-i passwd/user-fullname string toor
                      d-i passwd/username string toor
                      d-i passwd/user-password password password
                      d-i passwd/user-password-again password password
                      d-i user-setup/encrypt-home boolean false
                      d-i user-setup/allow-password-weak boolean true
                      # Individual additional packages to install
                      d-i pkgsel/include string openssh-server ntp
                      # Whether to upgrade packages after debootstrap.
                      # Allowed values: none, safe-upgrade, full-upgrade
                      d-i pkgsel/upgrade select full-upgrade
                      d-i grub-installer/only_debian boolean true
                      d-i grub-installer/with_other_os boolean true
                      d-i finish-install/reboot_in_progress note
                      #For the update
                      d-i pkgsel/update-policy select none
                      # debconf-get-selections --install
                      #Use mirror
                      choose-mirror-bin mirror/http/proxy string"

** NOTE! **
Make sure you change your VirtualBox preferences to not auto-capture keyboard, and also set host key to none.  Otherwise you may accidentally screw-up automated installs.

- Old Notes - 

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
