---
layout: docs
title: jclouds Documentation
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

   * [Compute API](/documentation/userguide/compute) helps you bootstrap machines in the cloud.
   * [Blobstore](/documentation/userguide/blobstore-guide) API helps you manage key-value data.

## Contents

### Getting Started

* [Installation](/documentation/userguide/installation-guide)

### Quick Start Guides

* [Amazon Web Services](/documentation/quickstart/aws)
* [Azure Storage Service](/documentation/quickstart/azure-storage)
* [BlueLock vCloud](/documentation/quickstart/bluelock)
* [Cloud Sigma](/documentation/quickstart/cloudsigma)
* [Eucalyptus](/documentation/quickstart/eucalyptus)
* [File System](/documentation/quickstart/filesystem)
* [Go Grid](/documentation/quickstart/go-grid)
* [HP Cloud Services](/documentation/quickstart/hpcloud)
* [IBM Developer Cloud](/documentation/quickstart/ibm-developer-cloud)
* [Open Stack](/documentation/quickstart/openstack)
* [Rackspace](/documentation/quickstart/rackspace)
* [RimuHosting](/documentation/quickstart/rimuhosting)
* [Terremark eCloud](/documentation/quickstart/terremark-ecloud)
* [Terremark vCloud Express](/documentation/quickstart/terremark-vcloud-express)

### Release Notes

_see sidebar for links to Release Notes, Maven Sites and Javadocs_

### User Guides

* [Using Blob Store API](/documentation/userguide/blobstore-guide)
* [Using Compute API and Tools](/documentation/userguide/compute)
* [Google App Engine](/documentation/userguide/google-app-engine)
* [VMWare vCloud](/documentation/userguide/vmware-vcloud)
* [Terremark](/documentation/userguide/terremark)
* [File System Provider](/documentation/userguide/filesystem-provider)
* [Init Builder](/documentation/userguide/init-builder)
* [Using jclouds with Apache Karaf](/documentation/userguide/karaf)
* [Using EC2](/documentation/userguide/using-ec2)
* [Using Maven](/documentation/userguide/using-maven)

### Samples & Examples

* [jclouds with Google App Engine](/documentation/examples/google-app-engine)

### Reference

* [jclouds Rationale and Design](/documentation/reference/rationale-design)
* [Location Metadata Design](/documentation/reference/location-metadata-design)
* [Compute API Design](/documentation/reference/compute-design)
* [Columnar Data Design](/documentation/reference/columnar-datadesign)
* [JClouds API](/documentation/reference/jclouds-api)
* [JClouds OAuth Integration](/documentation/reference/oauth)
* [Using jclouds with Apache Felix OSGi Container](/documentation/reference/osgi)
* [Pool Design](/documentation/reference/pool-design)
* [Load Balancer Design](/documentation/reference/load-balancer-design)
* [Logging in jclouds](/documentation/reference/jclouds-logging)
* [VMWare Integration Approach & Design](/documentation/reference/vmware-integration-design)
* [Supported Providers](/documentation/reference/Supported-providers)
* [Apps that use JClouds](/documentation/reference/apps-that-use-jclouds)
* [Using Provider Metadata](/documentation/reference/using-provider-metadata)

### FAQs
* [Amazon EC2 FAQ](/documentation/faqs/ec2-faq.html)

### Developer Resources

* [Contributing to jclouds](/documentation/devguides/contributing-to-jclouds)
* [Contributing to Documentation](/documentation/devguides/contributing-to-documentation)
* [Using Eclipse](/documentation/devguides/using-eclipse)
* [JClouds Continuous Integration](/documentation/devguides/continuous-integration)
* [Provider Metadata](/documentation/devguides/provider-metadata)

