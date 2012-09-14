---
layout: jclouds
title: Release Notes - Beta 7
---

# Release Notes: jclouds Beta 7

There is 4 months solid effort in jclouds 1.0-beta-7 release. [65 issues](http://code.google.com/p/jclouds/issues/list?can=1&q=label%3AMilestone-1.0-beta-7) 
were addressed.  
Our !ComputeService provisioning api is much stronger, with a mock provider and new Hardware and !OperatingSystem models,
 not to mention the new vCloud 1.0 api. We've also updated BlobStore with much sought after filesystem and blob signing support.

Below is more information on these enhancements.

## New Features

* We now have an [OperatingSystem](http://code.google.com/p/jclouds/wiki/ComputeGuide#Operating_System) type to help you be more specific 
	in provisioning and operations commands.
* Our new [Hardware](http://code.google.com/p/jclouds/wiki/ComputeGuide#Hardware) type exposes lots of details 
	including number of cores and speed per processor, as well mounted volume information including Amazon EBS and vCloud.
* We've enhanced testability by adding a [stub](http://code.google.com/p/jclouds/wiki/ComputeGuide#Stub_Provider) ComputeService provider.
  With this you can write test provisioning code without launching servers.
* Our [vCloud vCloud] support is completely revamped to support the new 1.0 api implemented by [vCloud Director](http://www.vmware.com/products/vcloud-director/) 
  with many thanks to VMware and [BlueLock](http://www.bluelock.com/bluelock-cloud-hosting/virtual-cloud-enterprise/).  
  Moreover, we've revamped [Terremark](/documentation/quickstart/terremark-vcloud-express) vCloud Express and started on their eCloud offering.
* [Alfredo Morresi](http://www.rainbowbreeze.it) contributed a [filesystem blobstore](/documentation/userguide/blobstore-guide), 
  which allows you to to use the same api persisting to disk, memory, or a blobstore like Amazon S3.
* With our  [blobstore portable request signer](/documentation/userguide/blobstore-guide),
  you can pass blobstore commands to non-java environments to execute.  Be creative!
* Our core is stronger, now using [Asynchronous URLFetch](http://code.google.com/appengine/docs/java/javadoc/com/google/appengine/api/urlfetch/URLFetchService.html#fetchAsync%28com.google.appengine.api.urlfetch.HTTPRequest%29)
  in Google Appengine and jaxrs processing via [Jersey](https://jersey.dev.java.net/).

## API Breaking Changes

* Blob now uses our new Payload class; blob's data needs to be accessed via `getPayload()` not `getContent()`
* We've completely dropped the `Size` object for `Hardware`, look for corresponding method names.
* The `Architecture` enum has been replaced with a free form field due to it being too coupled to EC2 concepts.
  Generally, `os64bit(true)` replaces its functionality.
* ContentMetadata is now its own type; use `md.getContentMetadata().getContentType()` as opposed to `md.getContentType()`
* Encryption utilities are now functions; use` Payloads.calculateMD5(blob)`, not `blob.generateMD5()`
* vCloud offerings have been significantly changed to address vCloud 1.0 and a prior modeling gap.
* Look for enum of `UNRECOGNIZED` which allows jclouds to operate even when an API clone provides bad data.


## Known Issues 

  * trmk-vcloudexpress is unstable when using `runScriptOnNodesMatching`
  * eucalyptus tests skipped due to no resources on the test cloud
  * the following providers are unstable
    * trmk-ecloud
    * rimuhosting
    * ibmdev (sandbox)
  * the following are known limitations of filesystem provider
    * Provider is not tested on windows
    * Blobstore list method lists all the file inside the container in a recursive way. Other options of the method aren't manager yes
    * There are issues when compiling the class under Windows. Need to be fixed
    * No blob metadata are stored in this current implementation. Only the name and the content of the source payload is stored. 
