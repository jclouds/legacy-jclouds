---
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

   * [[Compute API|ComputeGuide]] helps you bootstrap machines in the cloud.
   * [[Blobstore]] API helps you manage key-value data.



`Last Updated: 2011-05-10`
