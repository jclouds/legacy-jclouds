---
layout: jclouds
title: Quick Start - IBM Developer Cloud
---

# Quick Start: IBM Developer Cloud

**Note that IBM Developer Cloud support is not complete**

1. Register for IBM Development and [Test BETA](http://www-180.ibm.com/cloud/enterprise/beta/dashboard)
2. Ensure you are using a recent JDK 6
3. Setup your project to include `jclouds-ibmdev`
  * Clone jclouds from git, and execute `mvn install` from the ibmdev folder
  * Get the **snapshot** dependency `org.jclouds.provider/ibmdev` using jclouds [Installation](/documentation/userguide/installation-guide).
4. Start coding

{% highlight java %}
// get a context with ibm that offers the portable ComputeService api
ComputeServiceContext context = new ComputeServiceContextFactory().createContext("ibmdev", email, password,
                                                        ImmutableSet.<Module> of(new JschSshClientModule()));

// here's an example of the portable api

// run a couple nodes accessible via group
nodes = context.getComputeService().client.runNodesInGroup("webserver", 2);

// get a synchronous object to use for manipulating vcloud objects in IBMDeveloperCloud
IBMDeveloperCloudClient ibmdevClient = IBMDeveloperCloudClient.class.cast(context.getProviderSpecificContext()
         .getApi());

// create a new keyPair from an existing rsa key
ibmdevClient.addPublicKey(keyName, Files.toString("/home/me/.ssh/id_rsa.pub", Charsets.UTF_8));

// create an instance using this keyPair
// location "1" = default datacenter, image id "11" = SUSE Linux Enterprise/10 SP2
instance = ibmdevClient.createInstanceInLocation("1", name, "11", "LARGE",
         configurationData(
                  ImmutableMap.of("insight_admin_password", "myPassword1",
                           "db2_admin_password", "myPassword2", "report_user_password",
                           "myPassword3")).authorizePublicKey(keyName));

// or.. simplify your life and use the ComputeService interface 
ComputeService service = context.getComputeService();

// release resources 
context.close();

{% endhighlight %}

6. Validate on the IBM Developer Cloud [console](https://www-180.ibm.com/cloud/enterprise/beta/user/control.jsp)
