====

    Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>

    ====================================================================
    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
    ====================================================================
====

Overview:
 
jclouds is an open source library that helps you get started in the cloud
and reuse your java and clojure development skills. Our api allows you to 
freedom to use portable abstractions or cloud-specific features.  We have
two abstractions at the moment: compute and blobstore.  compute helps you
bootstrap machines in the cloud.  blobstore helps you manage key-value
data.
 
our current version is 1.0-beta-8
our dev version is 1.0-SNAPSHOT
 
our compute api supports: aws-ec2, gogrid, cloudservers (generic), cloudservers-us,
                          cloudservers-uk, vcloud (generic), ec2 (generic), byon,
                          trmk-ecloud, trmk-vcloudexpress, eucalyptus (generic),
                          cloudsigma-zrh, elasticstack(generic), bluelock-vclouddirector,
                          slicehost, eucalyptus-partnercloud-ec2, elastichosts-lon-p (Peer 1), 
                          elastichosts-sat-p (Peer 1), elastichosts-lon-b (BlueSquare),
                          openhosting-east1, serverlove-z1-man, skalicloud-sdg-my,
                          vcloudexpress (generic), stub (in-memory)

  * note * the pom dependency org.jclouds/jclouds-allcompute gives you access to
           to all of these providers

our blobstore api supports: aws-s3, cloudfiles (generic), cloudfiles-us, cloudfiles-uk,
                            azureblob, atmos (generic), synaptic-storage, scaleup-storage,
                            cloudonestorage, s3 (generic), walrus(generic), googlestorage, 
                            eucalyptus-partnercloud-s3, swift (generic), transient (in-mem), 
                            filesystem (on-disk)
 
  * note * the pom dependency org.jclouds/jclouds-allblobstore gives you access to
           to all of these providers

we also have support for: ibmdev, mezeo, nirvanix, boxdotnet, rimuhosting, openstack nova,
                          azurequeue, simpledb, scaleup-storage as well as a async-http-client
                          driver in the sandbox


If you want access to all jclouds components, include the maven dependency org.jclouds/jclouds-all


BlobStore Example (Java):
  // init
  context = new BlobStoreContextFactory().createContext(
                  "s3",
                  accesskeyid,
                  secretaccesskey);
  blobStore = context.getBlobStore();
 
  // create container
  blobStore.createContainerInLocation(null, "mycontainer");
  
  // add blob
  blob = blobStore.newBlob("test");
  blob.setPayload("testdata");
  blobStore.putBlob("mycontainer", blob);

BlobStore Example (Clojure):
  (use 'org.jclouds.blobstore)

  (with-blobstore ["azureblob" account encodedkey]
    (create-container "mycontainer")
    (upload-blob "mycontainer" "test" "testdata"))

Compute Example (Java):
  // init
  context = new ComputeServiceContextFactory().createContext(
                  "ec2",
                  accesskeyid,
                  secretaccesskey,
                  ImmutableSet.of(new Log4JLoggingModule(),
                                  new JschSshClientModule()));
  client = context.getComputeService();
 
  // define the requirements of your node
  template = client.templateBuilder().osFamily(UBUNTU).smallest().build();
 
  // these nodes will be accessible via ssh when the call returns
  nodes = client.createNodesInGroup("mycluster", 2, template);

Compute Example (Clojure):
  (use 'org.jclouds.compute)

  ; create a compute service using ssh and log4j extensions
  (def compute 
    (compute-service "terremark" "user" "password" :ssh :log4j))

  ; use the default node template and launch a couple nodes
  ; these will have your ~/.ssh/id_rsa.pub authorized when complete
  (with-compute-service [compute]
    (create-nodes "mycluster" 2))
 
Downloads:
  * distribution zip: http://jclouds.googlecode.com/files/jclouds-1.0-beta-8.zip
  * maven repo: http://repo2.maven.org/maven2 (maven central - the default repository)
  * snapshot repo: https://oss.sonatype.org/content/repositories/snapshots
 
Links:
  * project page: http://code.google.com/p/jclouds/
  * javadocs (1.0-beta-8): http://jclouds.rimuhosting.com/apidocs/
  * javadocs (1.0-SNAPSHOT): http://jclouds.rimuhosting.com/apidocs-SNAPSHOT/
  * community: http://code.google.com/p/jclouds/wiki/AppsThatUseJClouds
  * user group: http://groups.google.com/group/jclouds
  * dev group: http://groups.google.com/group/jclouds-dev
  * twitter: http://twitter.com/jclouds
