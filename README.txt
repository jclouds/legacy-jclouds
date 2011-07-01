Overview:
 
jclouds is an open source library that helps you get started in the cloud
and reuse your java and clojure development skills. Our api allows you to 
freedom to use portable abstractions or cloud-specific features.  We have
two abstractions at the moment: compute and blobstore.  compute helps you
bootstrap machines in the cloud.  blobstore helps you manage key-value
data.
 
our current version is 1.0.0
our next maintenance version is 1.0.1-SNAPSHOT
our dev version is 1.1.0-SNAPSHOT
 
check out our examples site! https://github.com/jclouds/jclouds-examples

our compute api supports: aws-ec2, gogrid, cloudservers-us, stub (in-memory), deltacloud,
                          cloudservers-uk, vcloud (generic), ec2 (generic), byon, nova,
                          trmk-ecloud, trmk-vcloudexpress, eucalyptus (generic),
                          cloudsigma-zrh, elasticstack(generic), bluelock-vcloud-vcenterprise,
                          bluelock-vcloud-zone01, stratogen-vcloud-mycloud, rimuhosting,
                          slicehost, eucalyptus-partnercloud-ec2, elastichosts-lon-p (Peer 1), 
                          elastichosts-sat-p (Peer 1), elastichosts-lon-b (BlueSquare),
                          openhosting-east1, serverlove-z1-man, skalicloud-sdg-my,
                          greenhousedata-element-vcloud

  * note * the pom dependency org.jclouds/jclouds-allcompute gives you access to
           to all of these providers

our blobstore api supports: aws-s3, cloudfiles-us, cloudfiles-uk, filesystem,
                            azureblob, atmos (generic), synaptic-storage, 
                            cloudonestorage, walrus(generic), ninefold-storage,
                            eucalyptus-partnercloud-s3, swift (generic), transient (in-mem)
 
  * note * the pom dependency org.jclouds/jclouds-allblobstore gives you access to
           to all of these providers

our loadbalancer api supports: cloudloadbalancers-us
 
  * note * the pom dependency org.jclouds/jclouds-allloadbalancer gives you access to
           to all of these providers

we also have support for: ibmdev, mezeo, nirvanix, boxdotnet, openstack nova, scality ring,
                          hosteurope-storage, tiscali-storage, scaleup-storage, googlestorage,
                          azurequeue, simpledb, cloudstack as well as a async-http-client
                          driver in the sandbox


If you want access to all jclouds components, include the maven dependency org.jclouds/jclouds-all


BlobStore Example (Java):
  // init
  context = new BlobStoreContextFactory().createContext(
                  "aws-s3",
                  accesskeyid,
                  secretaccesskey);
  blobStore = context.getBlobStore();
 
  // create container
  blobStore.createContainerInLocation(null, "mycontainer");
  
  // add blob
  blob = blobStore.blobBuilder("test").payload("testdata").build();
  blobStore.putBlob("mycontainer", blob);

BlobStore Example (Clojure):
  (use 'org.jclouds.blobstore2)

  (def *blobstore* (blobstore "azureblob" account encodedkey))
  (create-container *blobstore* "mycontainer")
  (put-blob *blobstore* "mycontainer" (blob "test" :payload "testdata"))

Compute Example (Java):
  // init
  context = new ComputeServiceContextFactory().createContext(
                  "aws-ec2",
                  accesskeyid,
                  secretaccesskey,
                  ImmutableSet.of(new Log4JLoggingModule(),
                                  new JschSshClientModule()));
  client = context.getComputeService();
 
  // define the requirements of your node
  template = client.templateBuilder().osFamily(UBUNTU).smallest().build();

  // setup a boot user which is the same as your login
  template.getOptions().runScript(AdminAccess.standard());
 
  // these nodes will be accessible via ssh when the call returns
  nodes = client.createNodesInGroup("mycluster", 2, template);

  // you can now run ad-hoc commands on the nodes based on predicates
  responses = client.runScriptOnNodesMatching(inGroup("mycluster"), "uptime",
                  wrapInInitScript(false));

Compute Example (Clojure):
  (use 'org.jclouds.compute2)

  ; create a compute service using ssh and log4j extensions
  (def compute 
    (*compute* "trmk`-ecloud" "user" "password" :ssh :log4j))

  ; launch a couple nodes with the default operating system, installing your user.
  (create-nodes *compute* "mycluster" 2
    (TemplateOptions$Builder/runScript (AdminAccess/standard)))
 
  ; run a command on that group
  (run-script-on-nodes-matching *compute* (in-group? "mycluster") "uptime" 
    (RunScriptOptions$Builder/wrapInInitScript false))

Downloads:
  * installation guide: http://code.google.com/p/jclouds/wiki/Installation
  * maven repo: http://repo2.maven.org/maven2 (maven central - the default repository)
  * snapshot repo: https://oss.sonatype.org/content/repositories/snapshots
 
Links:
  * project page: http://code.google.com/p/jclouds/
  * javadocs: http://jclouds.rimuhosting.com/apidocs/
  * community: http://code.google.com/p/jclouds/wiki/AppsThatUseJClouds
  * user group: http://groups.google.com/group/jclouds
  * dev group: http://groups.google.com/group/jclouds-dev
  * twitter: http://twitter.com/jclouds

## License

Copyright (C) 2011 Cloud Conscious, LLC. <info@cloudconscious.com>

Licensed under the Apache License, Version 2.0

