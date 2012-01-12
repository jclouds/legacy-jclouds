---
layout: docs
title: Contributing to jclouds
---

# How to develop and extend jclouds providers

## Creating and working with your fork

### setup your fork
*  go to github,com and fork jclouds/jclouds
*  `git clone http://github.com/___YOUR_USER___/jclouds.git`
*  if you are already a jclouds collaborator you can use git:// instead of http://
*  `git remote add jclouds git://github.com/jclouds/jclouds.git`

### pulling changes from master
*  `git pull --rebase jclouds master`
*  [why rebase](http://stackoverflow.com/questions/5968964/avoid-unwanted-merge-commits-and-other-commits-when-doing-pull-request-in-github)


## Creating new providers with maven archetypes

*  Help developers _working on_ jclouds get sub-projects up and running quickly
*  Give developers trying to _use_ jclouds a hands-up in terms of project setup, dependencies and sample code

The quickest way to make the jclouds archetypes available locally is to run
`mvn install` from the `jclouds/archetypes` directory. This will build the archetypes and place them in your local catalogue. 

In order to create a project based _on_ an archetype, navigate to the directory in which you wish the project to be created and run

`mvn archetype:generate`

### rest-client-archetype 

A template project for a jclouds client of a cloud service.

Parameters:

<table>
	<thead>
		<tr>
			<th>Name</th>
			<th>Description</th>
			<th>Default</th>
			<th>Example</th>
		</tr>
	</thead>
	<tr>
		<td>*providerName*</td>
		<td>The camel case name for the service. Will be used in class names, so should not contain spaces or other invalid characters. </td>
		<td></td>
		<td>Tweeter</td>
	</tr>
	<tr>
		<td>*artifactId*</td> 
		<td>The name of the project. Will be prefixed with `jclouds-`, and will also be the name of the generated project directory, so should not contain invalid characters. </td>
		<td></td>
		<td>tweeter</td>
	</tr>	
	<tr>
		<td>*providerEndpoint* </td>
		<td>The URL at which the service can be accessed. </td> 
		<td></td>
		<td>http://tweeter.com/api</td>
	</tr>
	<tr>
		<td>*providerIdentity*</td>
		<td>What does this provider call an identity? </td>
		<td></td>
		<td>user, account, apikey </td>
	</tr>
	<tr>	
		<td>*providerCredential* </td>
		<td>What does this provider call a credential, associated with above?</td>
		<td></td>
		<td>password, secret, key</td>
	</tr>
	<tr>
		<td>*providerApiVersion*</td>
		<td>What version of the rest api are you working on? </td>
		<td></td> 
		<td>1.0, 2001-11-01</td>
	</tr>
	<tr>
		<td>*groupId*</td>
		<td>The Maven project groupId.</td>
		<td>org.jclouds</td>
		<td></td> 
	</tr>	
	<tr>
		<td>*author*</td>
		<td>The author of the project.</td>
		<td>Adrian Cole</td>
		<td></td>
	</tr>
	<tr>
		<td>*package*</td> 
		<td>The Java base package of the project's classes.</td>
		<td><i>groupId.artifactId</i></td>
		<td></td>
	</tr>
	<tr>
		<td>*version*</td>
		<td>The Maven project version.</td>
		<td>1.0-SNAPSHOT</td>
		<td></td>
	</tr>
	
</table>


### compute-service-archetype

Adds compute service components to an *existing* project.  

#### Prerequisites
In order for this to work, you must use the identical parameters as json-client-archetype, and beforehand, 
you must delete the files this code will replace.  Here are the files:

<table>
	<thead>
		<tr>
			<th>file</th>
			<th>Reason</th>
		</tr>
	</thead>
	<tr>
		<td>src/test/resources/log4j.xml</td>
		<td>adding compute logger</td>
	</tr>
	<tr>
		<td>src/main/java/org/jclouds/ <i>artifactId</i>/<i>providerName</i>ContextBuilder</td>
		<td>converts to build a ComputeServiceContext instead of a RestContext</td>
	</tr>
	<tr>
		<td>src/main/java/org/jclouds/ <i>artifactId</i>/<i>providerName</i>ContextFactory</td>
		<td>converts to build a ComputeServiceContext instead of a RestContext</td>
	</tr>
</table>

#### Fixing up

*  You will have to change usage like context.getAPI() to context.getProviderSpecificContext().getAPI();
*  add in appropriate lines to compute.properties hooking this provider to it.
*  code in `providerName`ComputeServiceContextModule and run `providerName`ComputeServiceLiveTest until it passes.


### Coding

#### Adding new operations to an existing provider

*  You will need to add http markup in `providerName`AsyncClient and its respective unit test to create a new service
   -  Note that `providerName`Client must mirror method signatures in `providerName`AsyncClient except the return val is not a Future
*  Here's advise for coding new methods that need http bodies parsed
   *  create a method with the appropriate markup in `providerName`AsyncClient; have it return ListenableFuture*&lt;String*&gt; as a returnVal.
   *  create the identical method in  `providerName`Client but have it return a String and don't add any annotations.
   *  create a test method in `providerName`AsyncClientTest, and make sure this passes.
   *  create a test method in `providerName`ClientLiveTest and capture the resultant string to stdout or stderr.
   *  save that output to a file under test/resources related to the name
   *  create a domain object under the domain package related to this output
   *  create a parser that subclasses `ParseJson`, if json or `ParseSax.HandlerWithResult, if xml
      -  ex ParseContainerListFromJsonResponse
      -  ex AccountNameEnumerationResultsHandler
   * create a test that subclasses `BaseHandlerTest`
      -  ex ParseContainerListFromJsonResponseTest
      - ex AccountNameEnumerationResultsHandlerTest
   *  when this works
      *  change the returnVal of the methods in `providerName`AsyncClient and `providerName`Client appropriately
      *  add an annotation linking the parser to the result, @XMLResponseParser for xml and  @ResponseParser for anything else
      *  validate/correct `providerName`AsyncClientTest
   *  now check your `providerName`ClientLiveTest to ensure the value indeed comes back as expected from the service.

For more information on writing tests, and to run your tests or stock jclouds tests, 
see (Provider Testing in jclouds)[/documentation/devguides/provider-testing].)


#### Multiple API versions and dialects 

You may be working with a service that has dialects or multiple versions present.  In general, 
if there are multiple api versions present on a BETA or otherwise <1.0 level service, 
please keep jclouds object model in sync with the latest and make the implementation deal with prior versions.  
This keeps the number of parallel classes in our source down.  


#### Implementing a service dialect

Services some time are based on a specific API, but follow slightly different conventions or have additional api calls.  
The way to handle this is to create a subinterface of the master AsyncClient and Client interfaces.  
Then, override the annotations on the AsyncClient where the conventions are different and also add in new methods as needed.


## How to get help

When failures happen, and you cannot figure out why, here's a couple places to check.  
Please make sure you have log files handy, and at least paste the test log for others.

*  IRC - #jclouds on freenode 
*  [jclouds-dev google group](http://groups.google.com/group/jclouds-dev)


## Advanced

### Creating a new release

<!-- TODO  -->
TODO but check [here](http://code.google.com/p/jclouds/issues/detail?id=265) for now.


### Exception conversion 

Jclouds converts exceptions from http into domain specific exceptions, 
and can also convert domain-specific exceptions into valid values.

#### Per context coercion of http data into an Exception
The `HttpErrorHandler` applies to all commands within a rest context.  

For example, you can use `bindErrorHandlers()` in your subclass 
of `RestClientModule` to specify a `HttpErrorHandler` which takes 
an  `HttpCommand`and  `HttpResponse` and convert it them into a relevant exception.  
Here's an example of taking a 404 and turning it into a `ResourceNotFoundException`

{% highlight  java %}
case 404:
  if (!command.getRequest().getMethod().equals("DELETE")) {
     exception = new ResourceNotFoundException(message, exception);
  }
  break;
{% endhighlight %}
#### Special RuntimeException Types

Certain exceptions are propagated, even if nested inside other exceptions.  
These allow jclouds code to operate without exception searching.  

For example, you could wrap any jclouds command in the following reliably:

{% highlight  java %}
  try {
      String = client.getFoo("bar");
  } catch (AuthorizationException e) {
      // even if the auth exception was under the scenes wrapped in an http exception, 
      // the first exception of type AuthorizationException will show up here.  This allows
      // you to make preventative measures that ensure your code doesn't lock out accounts.
  }
{% endhighlight %}

Here's a list of our "standard" exceptions which are defined in
 `org.jclouds.util.Utils#returnFirstExceptionIfInListOrThrowStandardExceptionOrCause`:
  
*  IllegalStateException
*  UnsupportedOperationException
*  IllegalArgumentException
*  AuthorizationException
*  ResourceNotFoundException
*  HttpResponseException

#### Per method conversion of an Exception into another exception or a value

You may have a method that is valid if an exception occurs.

For example, an `exists(resource)` method would return ListenableFuture&lt;Boolean&gt;, if in the AsyncClient, 
and boolean if in the normal Client.  In this case, a `ResourceNotFoundException` 
simply means the resource isn't there, and should return false, rather than throw an exception.  
On you AsyncClient, declare an `ExceptionParser` on the method you'd like to control.  

Here's an example of common conventions, where an exception is ok:

{% highlight java %}

@DELETE
@ExceptionParser(ReturnVoidOnNotFoundOr404.class)
@Path("")
ListenableFuture<Void> deleteResource(@EndpointParam URI resourceHref);

@GET
@Endpoint(Images.class)
@Path("")
@ExceptionParser(ReturnEmptySetOnNotFoundOr404.class)
@XMLResponseParser(ImagesHandler.class)
ListenableFuture<? extends Set<Image>> listImages();

@GET
@ExceptionParser(ReturnNullOnNotFoundOr404.class)
@Path("")
@XMLResponseParser(InstanceHandler.class)
ListenableFuture<Instance> getInstance(@EndpointParam URI instanceHref);

{% endhighlight %}


### Creating new http methods

Sometimes, standard http methods will not do.  For example, you may need to use http PROPFIND. 
To do this, you first need to create an annotation for the new method, then use that in your markup.  
See below:

{% highlight java %}

@Target( { ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@HttpMethod("ROWDY")
public @interface ROWDY {
}

@ROWDY
@Path("objects/{id}")
ListenableFuture<Boolean> rowdy(@PathParam("id") String path);
{% endhighlight %}
### Dealing with Enums

Many clouds are attempting to create ec2, s3, or vcloud compatible apis.  
Often it is the case that they get data values wrong.  When this happens, IllegalArgumentExceptions are thrown.  
As a general practice, we shouldn't throw an exception on something that is parsed from a server, as it probably 
isn't critical (ex. new instance type, state, etc.)  
In fact, throwing an exception essentially disables functionality.   Instead, let's return UNRECOGNIZED for any enums we cannot parse.

### How we ensure BlobStore doesn't overrun our account with hundreds of containers

The test initializer makes a BlobStoreContext shared across all tests in the same suite.  Tests that extend BaseBlobStoreIntegrationTest use this BlobStore, and share a pool of containers during tests.  These containers are created before the suite is started.

This is so that you can run many tests concurrently, even if your blobstore can only allow creation of 5 buckets or containers.  That's achieved by try/finally block like below:

{% highlight java %}
 @Test(groups = { "integration", "live" })
 public void testGetIfNoneMatch() throws UnsupportedEncodingException {
   String container = getContainerName();
   try {

     String name = "apples";

     String goodETag = addObjectAndValidateContent(container, name);

     context.getBlobStore().getBlob(container, name, ifETagDoesntMatch("powerfrisbee"));
     validateContent(container, name);

     try {
       context.getBlobStore().getBlob(container, name, ifETagDoesntMatch(goodETag));
     } catch (HttpResponseException ex) {
       assertEquals(ex.getResponse().getStatusCode(), 304);
     }
   } finally {
     returnContainer(container);
   }
 }
{% endhighlight %}

The test initializer manages how to run the test in integration and live test groups.  Integration tests are offline expensive tests using the transient providers.  Love tests are online expensive tests using some BlobStore provider.  Since live tests are in fact live, you need to pass the same properties you would in any live test, including test.providername.identity and test.providername.credential.

Note that the test initializer is a legacy object that can probably be replaced with "test.blobstore.provider=aws-s3" or something, once we get around to refactoring it out.

