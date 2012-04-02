
#Setup

Have virtualbox 4.1.8 installed. 

Have an ssh daemon with passwordless login to localhost (i.e. "ssh [me]@localhost" must work without password).

That's it! 

--------------

#Running a local cloud

Enjoy local cloud goodness by running:

"mvn clean install clojure:repl"

> (use 'org.jclouds.compute2)  
> (import 'org.jclouds.scriptbuilder.statements.login.AdminAccess)  
> (def compute (compute-service "virtualbox" "admin" "12345" :sshj :slf4j))  
> (create-nodes compute "local-cluster" 2 (build-template compute { :run-script (AdminAccess/standard) } ))  

--------------

#Interacting with jclouds-vbox and connecting to machines

For java guidance look into src/test/java/org/jclouds/virtualbox/compute/VirtualBoxExperimentLiveTest.java.  
For now nat+host-only is the only available network configuration, nodes should be accessible from the host by:

> ssh -i ~/.ssh/id_rsa -o "UserKnownHostsFile /dev/null" -o StrictHostKeyChecking=no me@192.168.86.X  

where X is the node index with regard to creation order starting at 2 (2,3,4, etc...)

It *should* behave as any other provider, if not please report.

--------------

#Notes:

- jclouds-vbox is still at alpha stage please report any issues you find.  
- jclouds-vbox has been mostly tested on Mac OSX, it might work on Linux iff vbox is running and correctly set up, but it won't work on windows for the moment.  
- cached isos, vm's and most configs are kept at ~/.jclouds-vbox/ by default.  
- jclouds-vbox assumes vbox has the default host-only network vboxnet0, that the network is in 192.168.86.0/255.255.255.0 and that the host has address 1 in this network.  

--------------

#Throubleshooting

As jclouds vbox support is quite new things might go wrong sometimes. The procedure to make things work again is the following:

1. Remove all relevant vm's (named "jclouds-* ") with the vbox GUI. Make sure to select "delete all files".  
- Step one should be enough most of the times, but if it fails (by throwing some error):  
2. kill all vbox processes (VboxHadless, VBoxSVC, VBoxXPCOMIPCD, VirtualBox, vboxwebsrv)  
3. delete manually the files by executing: "rm -rf ~/.jclouds-vbox/jclouds-*"  
4. restart the vbox GUI and make sure to delete all remaining machines ignoring all errors.  
        