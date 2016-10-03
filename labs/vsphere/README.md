
#Running a local cloud

##Setup

Have vSphere 5 installed.
It is required to have a vCenter server running: the jclouds-vsphere has to point to vcenter endpoint
Obviously it could also be a virtual appliance deployed on ESXi.

You should have 1 Datacenter created in vSphere
You should have at least 1 Datastore created in this Datacenter
You should have user with right necessary to execute Virtual Machine tasks created
You should mark at least 1 Ubuntu 12.04 server vm as a Template in order to be able to clone it

NB: this template should contain as annotation a 

Notes: `ubuntu-12.04`

Enjoy vsphere cloud by running:

"mvn clean install clojure:repl"

> (use 'org.jclouds.compute2)  
> (import 'org.jclouds.scriptbuilder.statements.login.AdminAccess)  
> (def compute (compute-service "vsphere" "root" "password" :sshj :slf4j))  
> (create-nodes compute "local-cluster" 2 (build-template compute { :run-script (AdminAccess/standard) } ))  

By default, the chosen cloning strategy is [Linked](http://www.vmware.com/support/developer/vc-sdk/linked_vms_note.pdf)
If you want to override this default please specify

-Dtest.vsphere.cloning=full

to create cloned vms completely independent.

#Interacting with jclouds-vsphere and connecting to machines
For java guidance look into src/test/java/org/jclouds/vsphere/compute/VSphereExperimentLiveTest.java.  

--------------
# Running against remote vSphere instance kindly hosted by Softlayer

An ESXi 4 is hosted at SoftLayer (50.23.154.28)
This ESXi hosts a vCenter Server 5 Appliance at 50.23.145.66

By default, jclouds-vsphere points to that vCenter Server API (https://50.23.145.66/sdk)

Please provide the mandatory credentials to run against this installation using:

-Dvsphere.identity=A_VALID_IDENTITY
-Dvsphere.credential=A_VALID_CREDENTIAL

--------------

#Notes:

- jclouds-vsphere is still at alpha stage please report any issues you find.  
- jclouds-vsphere has been mostly tested on Mac OSX, it might work on Linux, but it won't work on windows for the moment.  

--------------

#Throubleshooting

As jclouds vsphere support is quite new things might go wrong sometimes. The procedure to make things work again is the following:

Remove all relevant vm's (named "jclouds-* ") using vSphere client, if needed. 
Make sure to select "Delete from disk".  