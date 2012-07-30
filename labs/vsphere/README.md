
#Setup

Have vSphere 5 installed.
It is required to have a vCenter server running: the jclouds-vsphere has to point to vcenter endpoint
Obviously it could also be a virtual appliance deployed on ESXi.

That's it! 

--------------

#Running a local cloud

Enjoy local cloud goodness by running:

"mvn clean install clojure:repl"

> (use 'org.jclouds.compute2)  
> (import 'org.jclouds.scriptbuilder.statements.login.AdminAccess)  
> (def compute (compute-service "vsphere" "root" "password" :sshj :slf4j))  
> (create-nodes compute "local-cluster" 2 (build-template compute { :run-script (AdminAccess/standard) } ))  

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