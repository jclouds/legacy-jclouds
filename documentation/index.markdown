---
layout: docs
title: JClouds Documentation
---

# **jclouds** documentation

[**jclouds**](http://www.jclouds.org/) is an open source library that helps you get started in the cloud and reuse your java and 
Clojure development skills. 


## API and Providers

In the cloud world there are many differences between providers, but there is a common domain among them, and some of them use very similar interface (API).
For instance, AWS S3 and Google Storage use the same dialect, or in other words, they use the same API.
A **provider** means the real instance, and the real endpoint. Google Storage and AWS S3 use the same API (S3 API) but both have different properties, e.g. endpoints.

In jclouds structure, there are two different packages API and provider, but they are related to each other.

Our API allows you the freedom to use portable abstractions or cloud-specific features. 
We support many cloud providers including _Amazon_, _GoGrid_, _Azure_, _vCloud_, and _Rackspace_.



**jclouds** provides two abstraction APIs at the moment: Compute and Blobstore. 

   * [Compute API](refere) helps you bootstrap machines in the cloud.
   * [[Blobstore]] API helps you manage key-value data.

## Contents

**Getting Started**

* [[Installation]]

**Quick Start Guides**

* [Amazon Web Services](/documentation/quickstart/aws)
* [Azure Storage Service](/documentation/quickstart/azure-storage)
* [BlueLock vCloud](/documentation/quickstart/bluelock)
* [Cloud Sigma](/documentation/quickstart/cloudsigma)
* [Eucalyptus](/documentation/quickstart/eucalyptus)
* [File System](/documentation/quickstart/filesystem)
* [Go Grid](/documentation/quickstart/go-grid)
* [IBM Developer Cloud](/documentation/quickstart/ibm-developer-cloud)
* [Open Stack](/documentation/quickstart/openstack)
* [Rackspace](/documentation/quickstart/rackspace)
* [RimuHosting](/documentation/quickstart/rimuhosting)
* [Terremark eCloud](/documentation/quickstart/terremark-ecloud)
* [Terremark vCloud Express](/documentation/quickstart/terremark-vcloud-express)

**Release Notes**

* [[Beta 7|ReleaseNotesBeta7]]
* [[Beta 8|ReleaseNotesBeta8]]

**User Guides**

* [[Using Blob Store API|BlobStore]]
* [[Using Compute API and Tools|ComputeGuide]]
* [[Google App Engine|UsingJcloudsWithGAE]]
* [[VMWare vCloud|VMWareVCloud]]
* [[Terremerk|Terremerk]]
* [[File System Provider|FileSystemProvider]]
* [[Init Builder|InitBuilder]]
* [[Using jclouds with Apache Karaf|ApacheKarafGuide]]
* [[Using EC2|UsingEC2]]
* [[Using Maven|UsingMaven]]

**Samples & Examples**

* [[jclouds with Google App Engine|GoogleAppEngineSample]]

**Reference**

* [[jclouds Rationale and Design|RationaleAndDesign]]
* [[Location Metadata Design|LocationMetadataDesign]]
* [[Compute API Design|ComputeDesign]]
* [[Columnar Data Design|ColumnarDataDesign]]
* [[JClouds API|jcloudsAPI]]
* [[JClouds OAuth Integration|OAuth]]
* [[Using jclounds with Apache Felix OSGi Container|OSGi]]
* [[Pool Design|PoolDesign]]
* [[Load Balancer Design|LoadBlancerDesign]]
* [[Logging in jclouds|jcloudsLogging]]
* [[VMWare Integration Approach & Design|VMWareIntegrationDesign]]
* [[JClouds Continuous Integration|JCloudsContinuousIntegration]]
* [[Supported Providers|SupportedProviders]]
* [[Apps that use JClouds|AppsThatUseJClouds]]
* [[Using Provider Metadata|UsingProviderMetadata]]


**jclouds Developer Resources**

* [[Contributing to jclouds|ContributingToJclouds]]
* [[Using Eclipse|UsingEclipse]]
* [[Provider Metadata|ProviderMetadata]]

