---
layout: docs
title: Provider Testing
---

# Testing in jclouds

## Introduction

jclouds owes a lot of its success to the reliability it gives its users, and
good test coverage is an essential ingredient in this reliability.
Testing can be made more challenging because the services it connects to
are live, remote and changeable -- but an immense amount of effort has gone
in to ensuring testability with the result that with a bit of up-front
learning, you shouldn't find it too difficult.
(And if you do, or it could be easier, just mail the list!)

The two main categories of tests used in jclouds are: 

* *Live* tests, running with credentials against a service provider, asserting that the resources 
(compute VM, blob storage, etc) actually get created, changed, etc 

* *Unit* tests, asserting that functions do what they should -- like normal unit tests --
but focussed in many cases on ensuring that clients parse responses correctly and
do the right thing:  when writing provider unit tests, expect to use `curl` extensively
(cheat sheet below), with the nice side benefit that you have evidence when,
heaven forbid, a provider mysteriously changes schema!

These types of tests are described below, 
particularly unit tests where a lot of magic
has been done to facilitate testing.


## Running tests from the commandline

There are two common ways of running tests that connect directly against the service, 
through your favourite IDE or via the commandline using maven. 
This section is about how to run using *maven*.
(See also (Using Eclipse)[].)

### Preliminaries

#### Verifying installation

We currently use maven 3.0.3, so ensure you have "mvn" in your path.  
To test this out, issue the `mvn -version` command.  It should look like below:

{% highlight text %}
Apache Maven 3.0.3 (r1075438; 2011-02-28 09:31:09-0800)
Maven home: /Users/adriancole/apache-maven-3.0.3
Java version: 1.6.0_29, vendor: Apple Inc.
Java home: /System/Library/Java/JavaVirtualMachines/1.6.0.jdk/Contents/Home
Default locale: en_US, platform encoding: MacRoman
OS name: "mac os x", version: "10.7.2", arch: "x86_64", family: "mac"
{% endhighlight %}

Note you should be in the directory of the service you'd like to test.  For example, 
if you are testing the terremark module, you should already have cloned jclouds and
changed into the vcloud/terremark directory.  Here's an example:

{% highlight text%}
Adrian-Coles-MacBook-Pro:tmp adrian$ git clone git://github.com/jclouds/jclouds.git
Initialized empty Git repository in /private/tmp/jclouds/.git/
remote: Counting objects: 58795, done.
remote: Compressing objects: 100% (14698/14698), done.
remote: Total 58795 (delta 28015), reused 58636 (delta 27916)
Receiving objects: 100% (58795/58795), 17.41 MiB | 45 KiB/s, done.
Resolving deltas: 100% (28015/28015), done.
Adrian-Coles-MacBook-Pro:tmp adrian$ cd jclouds/vcloud/terremark/
{% endhighlight %}


### Running unit tests

Unit tests run automatically as part of the build. To run these for the whole project, simply run:

`mvn clean install`

from the `jclouds` root directory. It may take a while. To run for only the project you are actively working on,
run the same command from the root directory of that project. 


### Running live tests 

To run against the live service, you'll need to specify the maven profile live (`-Plive`). 
When live is enabled, any tests that have `LiveTest` suffix will be run during the integration-test phase.  
In order for this to operate, you must specify the following either inside your `~/.m2/settings.xml` 
or directly on the commandline:

 * test._provider_.identity
 * test._provider_.credential (some clouds do not require this)

The following parameters can also be specified (*optional*):

 * test._provider_.endpoint
 * test._provider_.api-version
 * test._provider_.build-version (when an implementation targets a specific build running on the server)
 * test._provider_.image-id (compute)
 * test._provider_.image.login-user (compute, as username or username:password)
 * test._provider_.image.authenticate-sudo (compute, password for username above is required for sudo)

Here's an example of running a live test with a specific username and password (from the `providers/trmk-vcloudexpress` directory):

`mvn -Plive clean install -Dtest.trmk-vcloudexpress.identity=adrian@jclouds.org -Dtest.trmk-vcloudexpress.endpoint=https://services.vcloudexpress.terremark.com/api -Dtest.trmk-vcloudexpress.credential=12312412`



### Writing Unit Tests

These instructions assume you are familiar with (TestNG)[testng.org].
Some of the tests make heavy use of `@Before` and `@Test(dependsOn...)`, as well as TestNG's parametrization support, 
so if you haven't been a power user of TestNG it might be helpful to review some of its more advanced features.

jclouds also makes heavy use of (Google Guava)[http://code.google.com/p/guava-libraries/]'s `Function` interface 
to allow smaller pieces to be more easily unit-tested (as well as composed, ie chained together, at runtime). 
You can see examples of this type of testing in, for instance, `apis/ec2/src/test/java/org/jclouds/ec2/compute/functions`.

For testing provider-client behaviour, we have developed some other design patterns and helpful fixtures, described below. 

*Important:* If you're in doubt about how to unit test some code, hollar for help.
We don't like code which is *only* exercised in live tests!  


## "Expect"-Style unit tests

"Expect" tests allow you to simulate a conversation with a running service by recording the 
requests we expect to generate, and the corresponding responses the service would return.

For a given provider, testing a specific method on the `Client` API (for example `S3Client.bufferExists(name)`,
as shown below), these tests:

 * create the _request_ which the client should send, using `org.jclouds.http.HttpRequest.builder()`
 * create a _response_ which the server might return, using `org.jclouds.http.HttpResponse.builder()`
 * build a mock `Client` implementation with those objects

Then, when the specific method being tested is invoked on that implementation, the tests:

 * assert that the `Client` sends a request equivalent to the one we created
 * passed the response we created to the client as a response
 * returns the value of the method we invoked, once the client has done the _actual_ computation it would do on such a response from a real server 

You can of course define a sequence of request/response objects, as in the following example
asserting that `S3Client.bufferExists` correctly responds to a 301 status code (redirect) in response 
to a HEAD request by sending the same request as a GET:

{% highlight java %}
@Test(groups = "unit", testName = "S3RedirectionRetryHandlerExpectTest")
public class S3RedirectionRetryHandlerExpectTest extends BaseS3ClientExpectTest {

   public void testRedirectOnHeadBucketChangesRequestToGetBucket() {

      HttpRequest bucketFooExists = HttpRequest.builder().method("HEAD").
               endpoint(URI.create("https://foo.s3.amazonaws.com/?max-keys=0")).
               headers(ImmutableMultimap.<String, String>builder().
                  put("Host", "foo.s3.amazonaws.com").
                  put("Date", CONSTANT_DATE).
                  put("Authorization", "AWS identity:86P4BBb7xT+gBqq7jxM8Tc28ktY=").
                  build())
               .build();

      HttpResponse redirectResponse = HttpResponse.builder().statusCode(301).build();

      HttpRequest bucketFooExistsNowUsesGET = HttpRequest.builder().method("GET").
               endpoint(URI.create("https://foo.s3.amazonaws.com/?max-keys=0")).
               headers(ImmutableMultimap.<String, String>builder().
                  put("Host", "foo.s3.amazonaws.com").
                  put("Date", CONSTANT_DATE).
                  put("Authorization", "AWS identity:ZWVz2v/jGB+ZMmijoyfH9mFMPo0=").
                  build())
               .build();

      HttpResponse success = HttpResponse.builder().statusCode(200).build();

      S3Client clientWhenBucketExists = requestsSendsResponses(
         bucketFooExists, redirectResponse, bucketFooExistsNowUsesGET, success);

      assert clientWhenBucketExists.bucketExists("foo");
      
   }
}
{% endhighlight %}

A few final comments to help you on your way:
 * These `Builder` classes allow you to base requests on existing calls, so the above test could have instead been implemented using `HttpRequest bucketFooExistsNowUsesGET = HttpRequest.Builder.from(bucketFooExists).method("GET").build()`
 * The request object should _exactly_ match what the `Client` produces; if a partial match or more flexible comparison is required, the API could be extended to permit this, or use the syntax below
 * For an even more elaborate multi-step example, see `apis/cloudservers/src/test/java/org/jclouds/cloudservers/CloudServersExpectTest.java`


## Other aspects of unit tests

Many of the tests have not been ported to the _Expect_ test syntax described above,
and capture the request generated by a client and perform assertions on the fields therein.
This allows more intricate testing, although it is more work and harder to pass a result back to it.
For an example of this type of test, see for example `SlicehostAsyncClientTest`.


## Curl Cheat Sheet

The tool `curl` is a very handy and powerful way to download contents from web sites and (more importantly for us) services. 
(It is similar to `wget` but slightly more geared towards services than sites.)
Here are some of the main command-line optoins:

 * `curl -o output_file url` : download from `url` and write to `output_file`
  * `-u user:pass` : use the given user and password (inserted in above)
  * `-H line` : pass `line` as a header
  * `-d data` : pass `data` for a POST
  * `-F name=content` : pass `name=content` as multi-part POST data
 * `curl -h` : show help


## Post-mortem: when failures occur

Tests can fail because of problems in code, problems in the service, or configuration issues, 
such as passing the wrong credentials into the service.  
The first thing to do is to review the logs of the tests that failed.

Here's an example of a failure:
{% highlight text %}
Failed tests: 
  testConfigureNode(org.jclouds.vcloud.terremark.TerremarkVCloudClientLiveTest)
  testGet(org.jclouds.vcloud.terremark.compute.TerremarkVCloudComputeServiceLiveTest)
  cleanup(org.jclouds.vcloud.terremark.compute.TerremarkVCloudComputeServiceLiveTest)

Tests run: 41, Failures: 3, Errors: 0, Skipped: 2

[INFO] ------------------------------------------------------------------------
[ERROR] BUILD FAILURE
[INFO] ------------------------------------------------------------------------
[INFO] There are test failures.
{% endhighlight %}

In this case, a few tests didn't pass, although most did.  There are a few logs to check into:

<table>
	<thead>
		<tr>
			<th>log</th>
			<th>path</th>
			<th>purpose</th>
		</tr>
	</thead>
	<tr>
	 	<td>test log</td>
		<td>target/surefire-reports/TestSuite.txt</td> 
		<td>shows you the line in the test code that failed and specific reason </td>
	</tr>
	<tr>
		<td>wire log</td>
		<td>target/test-data/jclouds-wire.log</td> 
		<td>shows you all http packets sent to the service and their responses</td>
	</tr>
	<tr>
		<td>jclouds log</td>
		<td>target/test-data/jclouds.log</td>
		<td>shows you which java methods created which packets and also other debug info</td>
	</tr>	
	<tr>
		<td>ssh log</td>
		<td>target/test-data/jclouds-ssh.log</td>
		<td>shows you connections to other machines</td>
	</tr>
	<tr>
		<td>abstraction log</td>
		<td>target/test-data/jclouds-_compute or blobstore_.log</td>
		<td>shows you high-level commands, like what node is being deployed at what time </td>
	</tr>
	
</table>


<!--
  could have advanced topics section

  could talk about running a _specific_ test

  want more info on logging (likely a different page) 
-->

