Overview:
 
jclouds is an open source framework that helps you get started in the cloud
and reuse your java development skills. Our api allows you to freedom to use
portable abstractions or cloud-specific features.
 
our current version is 1.0-beta-3
 
our compute api supports: ec2, rackspace, rimuhosting, vcloud, terremark, 
                          hosting.com
our blobstore api supports: s3, rackspace, azure, atmos online
 
 
BlobStore Example:
  // init
  context = new BlobStoreContextFactory().createContext(
                  "s3",
                  accesskeyid,
                  secretaccesskey);
  blobStore = context.getBlobStore();
 
  // create container
  blobStore.createContainerInLocation("default", "mycontainer");
  
  // add blob
  blob = blobStore.newBlob("test");
  blob.setPayload("testdata");
  blobStore.putBlob(containerName, blob);
 
Compute Example:
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
  nodes = client.runNodesWithTag("mycluster", 2, template);
 
Downloads:
  * distribution zip: http://jclouds.googlecode.com/files/jclouds-1.0-beta-3-package.zip
  * maven repo: http://jclouds.googlecode.com/svn/repo 
  * snapshot repo: http://jclouds.rimuhosting.com/maven2/snapshots
 
Links:
  * project page: http://code.google.com/p/jclouds/
  * dev group: http://groups.google.com/group/jclouds-dev
  * twitter: http://twitter.com/jclouds