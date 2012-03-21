
#Setup

Have virtualbox 4.1.8 installed. 

Make sure you change your VirtualBox preferences to not auto-capture keyboard, and also set host key to none.  Otherwise you may accidentally screw-up automated installs.

That's it! Enjoy local cluster goodness by running:

"mvn clean install clojure:repl"

> (use 'org.jclouds.compute2)
> (import 'org.jclouds.scriptbuilder.statements.login.AdminAccess)
> (def compute (compute-service "virtualbox" "admin" "12345" :sshj :slf4j))
> (create-nodes compute "local-cluster" 2 (build-template compute { :run-script (AdminAccess/standard) } ))

For java guidance look into src/test/java/org/jclouds/virtualbox/compute/VirtualBoxExperimentLiveTest.java.

For now nat+host-only is the only available network configuration, nodes should be accessible from the host by:
> ssh -i ~/.ssh/id_rsa -o "UserKnownHostsFile /dev/null" -o StrictHostKeyChecking=no me@192.168.86.X
where X is the node index with regard to creation order starting at 2 (2,3,4, etc...)

It *should* behave as anyother provider, if not please report.

#Notes:

- jclouds-vbox is still at alpha stage please report any issues you find.
- jclouds-vbox has been mostly tested on Mac OSX, it might work on Linux, but it won't work on windows for the moment. 
- cached isos, vm's and most configs are kept at ~/.jclouds-vbox/ by default.
- jclouds-vbox assumes vbox has the default host-only network vboxnet0, that the network is in 192.168.86.0/255.255.255.0 and that the host as address 1 in this network.