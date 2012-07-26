---
layout: jclouds
title: jclouds Documentation
---

# **jclouds** Documentation

Below you will find the documentation for jclouds.org including user guides, Examples, FAQs, and References. Find information about 
jclouds.org, browse all documentation, or help to improve the documentation by [contributing](http://www.jclouds.org/documentation/devguides/contributing-to-documentation/).


## API and Providers

There are many differences between cloud providers.  However, there is a common domain among them, and some of them use very similar
interfaces (APIs).  For instance, Amazon Web Services (AWS) S3 and Google Storage use the same dialect or API.

A **provider** means the real instance and the real endpoint. Google Storage and AWS S3 use the same API (S3 API) but have different properties, e.g. endpoints.

In jclouds structure, there are two different packages API and provider, but they are related to each other.

Our API allows you the freedom to use portable abstractions or cloud-specific features. 
We support many cloud providers including _Amazon_, _GoGrid_, _Azure_, _vCloud_, and _Rackspace_.

### **jclouds** provides two abstraction APIs at the moment: Compute and Blobstore. 
* [Compute API](/documentation/userguide/compute) helps you bootstrap machines in the cloud.
* [Blobstore](/documentation/userguide/blobstore-guide) API helps you manage key-value data.

### User Guides
* [Using Blob Store API](/documentation/userguide/blobstore-guide)
* [Using Compute API and Tools](/documentation/userguide/compute)
* [Google App Engine](/documentation/userguide/google-app-engine)
* [VMWare vCloud](/documentation/userguide/vmware-vcloud)
* [Terremark](/documentation/userguide/terremark)
* [File System Provider](/documentation/userguide/init-builder)
* [Init Builder](/documentation/userguide/filesystem-provider)
* [Using jclouds with Apache Karaf](/documentation/userguide/karaf)
* [Using EC2](/documentation/userguide/using-ec2)
* [Using Maven](/documentation/userguide/using-maven)

### Samples & Examples
* [jclouds with Google App Engine](/documentation/examples/google-app-engine)

### FAQs
* [Amazon EC2](/documentation/faqs/ec2-faq)

### Reference
* [jclouds Rationale and Design](/documentation/reference/rationale-design)
* [Location Metadata Design](/documentation/reference/location-metadata-design)
* [Compute API Design](/documentation/reference/compute-design)
* [Runscript using SSH Implementaion](/documentation/reference/runscript-design)
* [Columnar Data Design](/documentation/reference/columnar-datadesign)
* [jclouds API](/documentation/reference/jclouds-api)
* [jclouds OAuth Integration](/documentation/reference/oauth)
* [Using jclouds with Apache Felix OSGi Container](/documentation/reference/osgi)
* [Pool Design](/documentation/reference/pool-design)
* [Load Balancer Design](/documentation/reference/load-balancer-design)
* [Logging in jclouds](/documentation/reference/jclouds-logging)
* [VMWare vSphere Approach & Design](/documentation/reference/vmware-vsphere-design)
* [Supported Providers](/documentation/reference/supported-providers)
* [Apps that use jclouds](/documentation/reference/apps-that-use-jclouds)
* [Using Provider Metadata](/documentation/reference/using-provider-metadata)

### Contributing

* If you would like to help out with documentation, please see our guide for [contributing to jclouds documentation](http://www.jclouds.org/documentation/devguides/contributing-to-documentation/).