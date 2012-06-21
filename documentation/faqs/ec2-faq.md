---
layout: jclouds
title: Frequently Asked Questions - JClouds EC2 integration
---
# Frequently Asked Questions - JClouds EC2 integration

## How to use your keypair with EC2 AMI

Amazon doesn't store the private key data, so if you supply an existing public key for jclouds to use, you'll also 
need to supply the private key correlating to it.

There are two ways you can tell jclouds to use your keypair for an EC2 AMI.

The *preferable way* is to just pass it as Template Option. 
If you are using an image where jclouds doesn't know the login user, you'll need to specify the 
`option.overrideCredentialsWith` and pass along the user that's baked in. 

{% highlight java%}
overrideLoginCredentialWith(your_id_rsa_string)
overrideCredentialsWith(new Credentials("root", your_id_rsa_string))
{% endhighlight %}

If you want to authorize your keypair, you can use the `auhtorizePublicKey(yourKey)` method 
{% highlight java %}
client.createNodesInGroup(group, 1, authorizePublicKey(myKey));
{% endhighlight %}

Here's how you can run a script using the overrideLoginCredentialsWith

{% highlight java %}
templateOptions.runScript(_script_). authorizePublicKey().overrideLoginCredentialWith(private)
{% endhighlight %}

Keep in mind that _authorizePublicKey()_ is redundant, if it is the same as what corresponds to the `keyPair()` option.

With respect to the security group, jclouds creates a security group for you, with rules corresponding to the 
`inboundPorts()` option (defaults to open port 22), unless you use the option
`EC2TemplateOptions.securityGroups()`.

The other way is the push your credentials into the credentials store so that jClouds uses it.

When a keypair is automatically created, jclouds [puts](https://github.com/jclouds/jclouds/blob/master/apis/ec2/src/main/java/org/jclouds/ec2/compute/strategy/CreateKeyPairAndSecurityGroupsAsNeededAndReturnRunOptions.java) 
the keypair into the Credentials Map. You can use the same option to put your credentials into the Credentials Map using the credentialStore

{% highlight java %}
ComputeServiceContext.credentialStore
{% endhighlight %}


_Note, the more you use features like security groups and keypairs, the less portable your code will be across clouds._

