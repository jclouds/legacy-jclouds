---
layout: jclouds
title: Quick Start - Azure Storage Service
---

# Quick Start Azure Storage Service

1. Sign up for an [Azure Account](http://www.microsoft.com/windowsazure/offers/)
2. Get your account and key
	* Login to the [Windows Azure Portal](http://windows.azure.com)
	* Click New Project, and then select Storage Account.
	* Enter a project label and click Next.
	* Enter the same name for the Storage Account Name. Make sure the account is available and then click _Create_
		* This is your account in jclouds.
	* Under the main azure screen, you should see Cloud Storage and the service you setup; Click on that. 
		* Primary Access Key is the key you use in jclouds
3. Ensure you are using a recent JDK 6
4. Setup your project to include `azureblob`
	* Get the dependency `org.jclouds.provider/azureblob` using jclouds [Installation](/documentation/userguide/installation-guide).
5. Start coding

{% highlight java %}
import static org.jclouds.azure.storage.blob.options.CreateContainerOptions.Builder.withPublicAcl;

// get a context with amazon that offers the portable BlobStore api
BlobStoreContext context = new BlobStoreContextFactory().createContext("azureblob", accesskeyid, secretkey);

//create a container in the default location
context.getBlobStore().createContainerInLocation(null, bucket);

// use the map interface for easy access to put/get things, keySet, etc.
context.createInputStreamMap(bucket).put("blob.txt", inputStream);

// when you need access to azureblob-specific features, use the provider-specific context
AzureBlobClient client = AzureBlobClient.class.cast(context.getProviderSpecificContext().getApi());

// create a root-level container with a public acl
client.createRootContainer(withPublicAc());

context.close();
{% endhighlight %}
