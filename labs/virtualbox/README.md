# VirtualBox as a local cloud provider
Similarly to the other cloud providers supported by Jclouds, jclouds-virtualbox is modeling virtualbox hypervisor as a local cloud provider, by supporting the same portable abstractions offered by jclouds.

#How it works


                                              ---------------   -------------
                                             |   Image(s)    | |   Node(s)   | 
                                              ---------------   -------------
                                              -------------------------------  -------
                                             |          VirtualBox           || Jetty |
                                              -------------------------------  -------
     ---------    passwordless ssh+sudo       ----------------------------------------
    | jclouds | ---------------------------> |                 localhost              |
     ---------                                ----------------------------------------

###Components
- jclouds: it acts as a java (or clojure) client to access to virtualbox functionalities.
- localhost: it hosts the VirtualBox hypervisor and a jetty server. This server is automatically installed by jclouds-virtualbox during Images creation.
- VirtualBox: jclouds-virtualbox assumes the latest VirtualBox installed (please see https://www.virtualbox.org/wiki/Downloads)
- Jetty: in this scenario, this http server is used to serve the preseed.cfg specified in YAML descriptor file
- Image: it logically represents a master vm that can be cloned. It is a union of 2 sets of entities: 
* a list of supported images, described in the YAML descriptor files
* a list of existing virtualbox vms with name starting with "jclouds-image-0x0-"
In this way, when we ask for a node matching a particular template, jclouds will look for the template using these 2 sets: if the image has been already created in virtualbox, the node will be cloned from it, otherwise first an image will be created and then cloned into one or more nodes.
- Node: it is a virtualbox vm, linked cloned from another virtualbox vm marked as Image.

## Image creation

                        ssh                   --------------- 
        /----------------------------------> |   Image(s)    |
        |                                     --------------- 
        |                                     -------------------------------  -------
        |                                    |          VirtualBox           || Jetty |
        |                                     -------------------------------  -------
     ---------    passwordless ssh+sudo       ----------------------------------------
    | jclouds | ---------------------------> |                 localhost              |
     ---------                                ----------------------------------------

The OS supported by jclouds-virtualbox are described in a YAML file `default-images.yaml` stored at `src/main/resources/`.
For each OS supported, it stores these information:
a unique id, a name, a description, an os_arch, os_family, os_description, os_version, the iso url, the iso md5, username and credential to access this vm, a keystroke sequence for the OS installer and a preseed configuration file that contains the settings for this OS installation.

For example, for ubuntu 10.04.4 server (32 bit), the corresponding YAML section looks like:

    - id: ubuntu-10.04.4-server-i386
      name: ubuntu-10.04-server-i386
      description: ubuntu 10.04.4 server (i386)
      os_arch: x86
      os_family: ubuntu
      os_description: ubuntu
      os_version: 10.04.4
      iso: http://releases.ubuntu.com/10.04.4/ubuntu-10.04.4-server-i386.iso
      iso_md5: fc08a01e78348e3918180ea91a6883bb
      username: toor
      credential: password      
      keystroke_sequence: |
                <Esc><Esc><Enter> 
                /install/vmlinuz noapic preseed/url=http://10.0.2.2:8080/src/test/resources/preseed.cfg 
                debian-installer=en_US auto locale=en_US kbd-chooser/method=us 
                hostname=vmName 
                fb=false debconf/frontend=noninteractive
                console-setup/ask_detect=false console-setup/modelcode=pc105 console-setup/layoutcode=us
                initrd=/install/initrd.gz -- <Enter>
      preseed_cfg: |
                      ## Options to set on the command line
                      d-i debian-installer/locale string en_US
                      d-i console-setup/ask_detect boolean false
                      d-i console-setup/layoutcode string us
                      d-i netcfg/get_hostname string unassigned-hostname
                      d-i netcfg/get_domain string unassigned-domain
                      d-i time/zone string UTC
                      d-i clock-setup/utc-auto boolean true
                      d-i clock-setup/utc boolean true
                      d-i kbd-chooser/method select   American English
                      d-i netcfg/wireless_wep string
                      d-i base-installer/kernel/override-image string linux-server
                      d-i debconf debconf/frontend select Noninteractive
                      d-i pkgsel/install-language-support boolean false
                      tasksel tasksel/first multiselect standard, ubuntu-server
                      d-i partman-auto/method string lvm
                      #d-i partman-auto/purge_lvm_from_device boolean true
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
                      choose-mirror-bin mirror/http/proxy string

The OS isos and guest additions isos will be stored in the jclouds working dir (by default ~/.jclouds-vbox/isos/)

###Networking
First time jclouds uses your local virtualbox installation an Host-Only network called `vboxnew0` will be automatically created (see virtualbox->Preferences->Networks)
This network has these defaults settings:

- IPv4: 192.168.56.1
- IPv4 Network Mask: 255.255.255.0
- DHCP Server: enabled

with
- Server Address: 192.168.56.254
- Server Mask: 255.255.255.0
- Lower Address Bound: 192.168.56.2
- Upper Address Bound: 192.168.56.253

This DHCP server will be responsible to assign local IP addresses to the Nodes created by jclouds.

##Cloning nodes

              ssh                              ---------------   -------------
        /----------------------------------> |    Node(s)    | |    Image    | 
        |                                     ---------------   -------------
        |                                     ------------------------------- 
        |                                    |          VirtualBox           |
        |                                     ------------------------------- 
     ---------    (a) passwordless ssh+sudo   ----------------------------------------
    | jclouds | ---------------------------> |                 localhost              |
     ---------                                ----------------------------------------

###Cloning strategy
By default, a new node is cloned from a matching 'Image' with 'CloneOptions.Link': this advanced option will save a lot of disk space and install time as opposed to creating a completely unique VMs for each new node.

### Networking
Each Node will have 2 NIC (Network Interface Card) to enable an NAT+HostOnly networking strategy:
* NIC1 at port 0 will be attached to Host-Only network `vboxnet0`: to allow localhost-nodes communication and node-node communication
* NIC2 at port 1 will be attached to NAT network: to give each node internet access

#Setup

Identity and credential are, by default, 'user.name' and the ssh private key of this user, respectively. The ssh private key is expected at `${user.home}/.ssh/id_rsa`.

## Passwordless ssh

jclouds-virtualbox uses the current user.name and private key (by default `${user.home}/.ssh/id_rsa`) for sending commands to localhost over ssh.

The current user should have passwordless ssh access using the ssh key you generated earlier:
> ssh-copy-id -i ~/.ssh/id_rsa your-user@localhost

If your system does not have an `ssh-copy-id` command, use something like this:
> cat ~/.ssh/id_rsa.pub | ssh your-user@localhost "cat -> ~/.ssh/authorized_keys"

## Passwordless sudo

You need to have a your user with passwordless sudo rights on localhost. This is done by editing the sudoers file. You should be very careful editing this file, since by introducing errors you might lock yourself out of the system. Therefore, it is recommended to edit this file through the visudo command.

The sudoers file should have a line like this (replace your-user):
> your-user    ALL=(ALL)   NOPASSWD: ALL

At this point, you should be able to login to localhost with `ssh your-user@localhost` without password.

#Customization

##Identity and Credential
By default, jclouds-virtualbox will try to use be sure you have your ssh public key in your `.ssh/authorized_keys`, but you can also override this default by specifying 
`-Dvirtualbox.identity` and `-Dvirtualbox.credential`, if you want to use a username and password of your local machine.

##Preseed file
In order to make available a preseed file, jclouds-virtualbox will start a PreseedServer at `http://localhost:23232` that will serve a preseed.cfg file.
Make sure your firewall rules are not blocking this port.
If you need to override this default you can use `-Djclouds.virtualbox.preconfigurationurl=http://localhost:PORT/preseed.cfg`, with a different PORT.

##Working directory
Cached isos for the OS's and guest additions, vm's and most configs are kept at `~/.jclouds-vbox/` by default, 
you can override -Dtest.virtualbox.workingDir=/path/to/your/workingDir.

That's it!

--------------

#Interacting with jclouds-vbox and connecting to machines

For java guidance look into src/test/java/org/jclouds/virtualbox/compute/VirtualBoxExperimentLiveTest.java.  
For now nat+host-only is the only available network configuration, nodes should be accessible from the host by:

     ssh -i ~/.ssh/id_rsa -o "UserKnownHostsFile /dev/null" -o StrictHostKeyChecking=no your-user@192.168.56.X  

where X (2-253) is assigned by DHCP server of vboxnet0.

It *should* behave as any other provider, if not please report.

--------------

#Notes:

- jclouds-virtualbox is still at alpha stage please report any issues you find.  
- jclouds-virtualbox has been mostly tested on Mac OSX, it might work on Linux iff vbox is running and correctly set up, but it won't work on windows for the moment.  

--------------

#Throubleshooting

As jclouds vbox support is quite new things might go wrong sometimes. The procedure to make things work again is the following:

1. Remove all relevant vm's (named "jclouds-* ") with the vbox GUI. Make sure to select "delete all files".  
- Step one should be enough most of the times, but if it fails (by throwing some error):  
2. kill all vbox processes (VboxHadless, VBoxSVC, VBoxXPCOMIPCD, VirtualBox, vboxwebsrv)  
3. delete manually the files by executing: "rm -rf ~/.jclouds-vbox/jclouds-*"  
4. restart the vbox GUI and make sure to delete all remaining machines ignoring all errors.  
