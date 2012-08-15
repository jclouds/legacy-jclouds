
#Running a local cloud

##Setup

Have vSphere 5 installed.
It is required to have a vCenter server running: the jclouds-vsphere has to point to vcenter endpoint
Obviously it could also be a virtual appliance deployed on ESXi.

You should have 1 Datacenter created in vSphere
You should have at least 1 Datastore created in this Datacenter
You should have user with right necessary to execute Virtual Machine tasks created
You should mark at least 1 Ubuntu 12.04 server vm as a Template in order to be able to clone it

Enjoy vsphere cloud by running:

"mvn clean install clojure:repl"

> (use 'org.jclouds.compute2)  
> (import 'org.jclouds.scriptbuilder.statements.login.AdminAccess)  
> (def compute (compute-service "vsphere" "root" "password" :sshj :slf4j))  
> (create-nodes compute "local-cluster" 2 (build-template compute { :run-script (AdminAccess/standard) } ))  

By default, the chosen cloning strategy is [Linked](http://www.vmware.com/support/developer/vc-sdk/linked_vms_note.pdf)
If you want to override this default please specify

-Dtest.vsphere.cloning=full

to create cloned vms completely indipendent.

#Interacting with jclouds-vsphere and connecting to machines
For java guidance look into src/test/java/org/jclouds/vsphere/compute/VSphereExperimentLiveTest.java.  

--------------
# Running against remote vSphere instance kindly hosted by Softlayer

## Softlayer VPN instructions
Connection instructions:

### Windows XP: (Internet Explorer)
Open Internet Explorer and head to http://vpn.softlayer.com. Once you have entered your username and password you will be prompted to install an ActiveX plug-in. You must install this plug-in in order to be able to connect to your backend network. You must also have administrative rights on your workstation in order to install the ActiveX plug-in. If you do not have rights to install the plug-in, ask you local System Administrator to install it for you. Once the ActiveX plug-in is installed, an Array SSL VPN network connector will launch and establish a connection to the VPN device. If successful, a red 'A' will appear in your task bar. You may click on the 'A' at any time to see the status of your SSL VPN connection like status, assigned IP address, assigned DNS servers, network routes, and time connected. You may minimize your browser session at any time and continue to use other applications securely on the private network. Once you are finished you can disconnect by clicking on the disconnect button to the right or simply close the window.

### Mac OSX: (Safari)
The first time you use the SSL VPN the Java applet will install a VPN tunneling device. In order to install the device you must run the first few commands as an administrative user.

 - Open the Terminal.app program. Navigate through Macintosh HD -> Applications -> Utilities -> Terminal in your Finder.
 - Run the command sudo /Applications/Safari.app/Contents/MacOS/Safari. Enter your password when prompted.
 - This will open Safari. Head to http://vpn.softlayer.com. Hit the "accept" button to allow our SSL VPN Java applet install the VPN client.
 - Login with your portal username and password
 - Once connected click on the network tab
 - Click the "Trust" button to run the SSL VPN Java applet
 - Hit the "Connect" button. Since this is your first time connecting the Java applet will install the VPN device onto your computer.
 - Once the VPN client is installed and connected hit the "Disconnect" button then quit the Safari and Terminal programs.

Once the SSL client is installed you only need to follow the instructions below to connect.
- Open Safari and head to http://vpn.softlayer.com. Hit the "accept" button to allow our SSL VPN Java applet install the VPN client.
- Login with your portal username and password
- Once connected click on the network tab
- Click the "Trust" button to run the SSL VPN Java applet
- Hit the "Connect" button. This will connect you to the SSL VPN.
- After it is connected it should pop up a window that says you are connected
- This window must remain open to utilize the VPN tunnel, you may minimize it. 
- To test ping 10.0.80.11 or your server's private address and if you get a reply you are successfully connected.
- When you are finished, you may click on the disconnect button under the 'network' tab or simply close all your browser windows. 

--------------

#Notes:

- jclouds-vsphere is still at alpha stage please report any issues you find.  
- jclouds-vsphere has been mostly tested on Mac OSX, it might work on Linux, but it won't work on windows for the moment.  

--------------

#Throubleshooting

As jclouds vsphere support is quite new things might go wrong sometimes. The procedure to make things work again is the following:

1. Remove all relevant vm's (named "jclouds-* ") with the VMware vCenter servcer GUI. Make sure to select "Delete from disk".  