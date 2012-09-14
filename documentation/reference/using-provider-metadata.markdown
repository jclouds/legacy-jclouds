---
layout: jclouds
title: Using Provider Metadata
---
# Using Provider Metadata

In the ProviderMetadata wiki page we learned how to implement [Provider Meta Data|ProviderMetadata].
Now let's see how we might consume [Provider Meta Data|ProviderMetadata] to make our lives easier.
We'll start with the basics on how to obtain [Provider Meta Data|ProviderMetadata] based on id or type.

## Finding Provider Meta Data Based on Type or ID

For all basic usage, you will rely on using the [org.jclouds.providers.Providers](https://github.com/jclouds/jclouds/blob/master/core/src/main/java/org/jclouds/providers/Providers.java) class.
This class exposes static helper methods to obtain [Provider Meta Data|ProviderMetadata] based on many of things the most basic of is by id.
To find an exact [Provider Meta Data|ProviderMetadata] implementation based on its identifier, you'd use something like this:

{% highlight java %}
...
// Retrieves the Amazon Elastic Compute Cloud (EC2) ProviderMetadata by its id
ProviderMetadata awsEC2 = Providers.withId("aws-ec2");
...
{% endhighlight %}

Pretty simple stuff.  What if you wanted a list of all compute types?  
Well, org.jclouds.providers.Providers has a getType(String) method that will let you look items up by their "type",
which corresponds to a public static final string exposed from the [org.jclouds.providers.ProviderMetadata](https://github.com/jclouds/jclouds/blob/master/core/src/main/java/org/jclouds/providers/ProviderMetadata.java) class.  
For all known types there is also a helper method on `org.jclouds.providers.Providers` that you can use instead.  
Below is a code sample with both usage examples:                                                                                            
                                                                                                                                            
{% highlight java %}                                                                                                                                     
...                                                                                                                                         
// Retrieve ProviderMetadata for all compute providers (manual way)                                                                         
Iterable<ProviderMetadata> mcProviders = Providers.ofType(ProviderMetadata.COMPUTE_TYPE);                                                   
                                                                                                                                            
// Retrieve ProviderMetadata for all compute providers (helper way)                                                                         
Iterable<ProviderMetadata hcProviders = Providers.allCompute()                                                                              
...                                                                                                                                         
{% endhighlight %}
                                                                                                                                            
As you can easy, [Provider Meta Data|ProviderMetadata] has made things pretty simple when it comes to findings providers based on a         
particular type or id.  What about when you want to find things based on geo-location?                                                      
Well, `org.jclouds.providers.Providers` helps with that too.                                                                                
                                                                                                                                            
## Finding [Provider Meta Data|ProviderMetadata] Based on ISO 3166 Code                                                                     
                                                                                                                                            
As with the example above, we'll resort to using org.jclouds.providers.Providers to find [Provider Meta Data|ProviderMetadata] based on     
[ISO 3166](http://en.wikipedia.org/wiki/ISO_3166) code(s).                                                                                  
The first example is easy, give me all providers that are in the US-CA location:                                                            
                                                                                                                                            
{% highlight java %}                                                                                                                                     
...                                                                                                                                         
Iterable<ProviderMetadata> usCAProviders = Providers.boundedByIso3166Code("US-CA");                                                         
...                                                                                                                                         
{% endhighlight %}
                                                                                                                                            
Oh?  You only wanted blobstore providers in US-CA?  Here you go:                                                                            
                                                                                                                                            
{% highlight java %}                                                                                                                                     
...                                                                                                                                         
Iterable<ProviderMetadata> usCABlobStoreProviders = Providers.boundedByIso3166Code("US-CA", ProviderMetadata.BLOBSTORE_TYPE);               
...                                                                                                                                         
{% endhighlight %}
                                                                                                                                            
Ah, that's better.  Now, what if you wanted to find all US providers?                                                                       
Don't worry, you don't have to maintain a list of US ISO 3166 codes, you'd just use something like this:                                    
                                                                                                                                            
{% highlight java %}                                                                                                                                     
...                                                                                                                                         
Iterable<ProviderMetadata> usProviders = Providers.boundedByIso3166Code("US");                                                              
...                                                                                                                                         
{% endhighlight %}
                                                                                                                                            
As you can see, [Provider Meta Data|ProviderMetadata] has made things very, very simple when it comes to                                    
locating things based on geo-location.  What?  You want to find providers based on a reference provider?                                    
Well, we have that documented next.                                                                                                         
                                                                                                                                            
## Finding [Provider Meta Data|ProviderMetadata] Based on Relativity to Another Provider                                                    
                                                                                                                                            
Locating by type, id and ISO 3166 code have been demonstrated above and they are pretty straight forward.                                   
One last basic way to find [Provider Meta Data|ProviderMetadata] is based on similar hosting locations or                                   
being able to find "collocated" providers.  Let's do this based on the AWS EC2 example above.                                               
Let's find all providers that are in regions/locations that AWS EC2 supports ("US-VA", "US-CA", "IE", "SG", "JP-13"):                       
                                                                                                                                            
{% highlight java %}                                                                                                                                     
...                                                                                                                                         
ProviderMetadata awsEC2 = Providers.withId("aws-ec2");                                                                                      
Iterable<ProviderMetadata> collocatedProviders = Providers.collocatedWith(awsEC2):                                                          
...                                                                                                                                         
{% endhighlight %}
                                                                                                                                            
Yup, all providers that are in the the same locations AWS EC2 is would had been returned.                                                   
Huh?  You didn't mention before that you only wanted blobstore providers.                                                                   
Well, good for you [Provider Meta Data|ProviderMetadata] has you covered:                                                                   
                                                                                                                                            
{% highlight java %}                                                                                                                                     
...                                                                                                                                         
ProviderMetadata awsEC2 = Providers.withId("aws-ec2");                                                                                      
Iterable<ProviderMetadata> collocatedBlobstoreProviders = Providers.collocatedWith(awsEC2, ProviderMetadata.BLOBSTORE_TYPE):                
...                                                                                                                                         
{% endhighlight %}
                                                                                                                                            
## Summary                                                                                                                                  
                                                                                                                                            
As you can see by the examples above, using Jclouds' [Provider Meta Data|ProviderMetadata]  (introduced in Jclouds 1.0.0) makes finding     
 [Provider Meta Data|ProviderMetadata] extremely easy.                                                                                      
All of these examples should have all of us "cloud fluffers", a term I never heard before talking to Adrian,                                
dreaming of auto-populated drop down lists for our UIs, simple wizards and other things that                                                
 [Provider Meta Data|ProviderMetadata] makes available.                                                                                     
                                                                                                                                            
[Provider Meta Data|ProviderMetadata] is still a moving target so to stay up to date, click one of the ProviderMetadata links in this       
 wiki page to see exactly what provider metadata is available.                                                                              
