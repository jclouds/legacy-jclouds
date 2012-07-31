
#Setup

Have vSphere 5 installed.
It is required to have a vCenter server running: the jclouds-vsphere has to point to vcenter endpoint
Obviously it could also be a virtual appliance deployed on ESXi.

<<<<<<< HEAD
You should have 1 Datacenter created in vSphere
You should have at least 1 Datastore created in this Datacenter
You should have user with right necessary to execute Virtual Machine tasks created
You should mark at least 1 Ubuntu 12.04 server vm as a Template in order to be able to clone it
=======
That's it! 
>>>>>>> a337b6ca8e4b24dee2f093235a7291fc83b31b3f

--------------

#Running a local cloud

<<<<<<< HEAD
Enjoy vsphere cloud by running:
=======
Enjoy local cloud goodness by running:
>>>>>>> a337b6ca8e4b24dee2f093235a7291fc83b31b3f

"mvn clean install clojure:repl"

> (use 'org.jclouds.compute2)  
> (import 'org.jclouds.scriptbuilder.statements.login.AdminAccess)  
> (def compute (compute-service "vsphere" "root" "password" :sshj :slf4j))  
> (create-nodes compute "local-cluster" 2 (build-template compute { :run-script (AdminAccess/standard) } ))  

<<<<<<< HEAD
By default, the chosen cloning strategy is [Linked](http://www.vmware.com/support/developer/vc-sdk/linked_vms_note.pdf)
If you want to override this default please specify

-Dtest.vsphere.cloning=full

to create cloned vms completely indipendent.

=======
>>>>>>> a337b6ca8e4b24dee2f093235a7291fc83b31b3f
--------------

#Interacting with jclouds-vbox and connecting to machines

For java guidance look into src/test/java/org/jclouds/vsphere/compute/VSphereExperimentLiveTest.java.  

--------------

#Notes:

- jclouds-vsphere is still at alpha stage please report any issues you find.  
- jclouds-vsphere has been mostly tested on Mac OSX, it might work on Linux, but it won't work on windows for the moment.  

--------------

#Throubleshooting

As jclouds vsphere support is quite new things might go wrong sometimes. The procedure to make things work again is the following:

1. Remove all relevant vm's (named "jclouds-* ") with the VMware vCenter servcer GUI. Make sure to select "Delete from disk".  