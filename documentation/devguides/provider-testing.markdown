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

We currently use maven 3.0 beta 3, so ensure you have "mvn" in your path.  
To test this out, issue the `mvn -version` command.  It should look like below:

{% highlight text %}
Adrian-Coles-MacBook-Pro:~ adrian$ mvn --version
Apache Maven 3.0-beta-3 (r990787; 2010-08-30 13:44:03+0100)
Java version: 1.6.0_20
Java home: /System/Library/Frameworks/JavaVM.framework/Versions/1.6.0/Home
Default locale: en_US, platform encoding: MacRoman
OS name: "mac os x" version: "10.6.4" arch: "x86_64" Family: "mac"
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

XXX


### Running live tests 

To run against the live service, you'll need to specify the maven profile live (`-Plive`). 
When live is enabled, any tests that have `LiveTest` suffix will be run during the integration-test phase.  
In order for this to operate, you must specify the following either inside your `~/.m2/settings.xml` 
or directly on the commandline:

*  test._provider_.identity
*  test._provider_.credential (some clouds do not require this)
*  test._provider_.endpoint (optional)
*  test._provider_.apiversion (optional)

Here's an example of running a live test with a specific username and password:

`mvn -Plive clean install -Dtest.trmk-vcloudexpress.identity=adrian@jclouds.org -Dtest.trmk-vcloudexpress.endpoint=https://services.vcloudexpress.terremark.com/api -Dtest.trmk-vcloudexpress.credential=12312412`

### Running specific tests

XXX


### Writing Unit Tests

## "Expect"-Style unit tests

XXX

{% highlight java %}
@Test(groups = "unit", testName = "S3RedirectionRetryHandlerExpectTest")
public class S3RedirectionRetryHandlerExpectTest extends
BaseS3ClientExpectTest {

   public void testRedirectOnHeadBucketChangesRequestToGetBucket() {

      HttpRequest bucketFooExists =
HttpRequest.builder().method("HEAD").endpoint(
               URI.create("https://foo.s3.amazonaws.com/?max-keys=0")).headers(
               ImmutableMultimap.<String, String>
builder().put("Host", "foo.s3.amazonaws.com").put("Date",
                        CONSTANT_DATE).put("Authorization", "AWS
identity:86P4BBb7xT+gBqq7jxM8Tc28ktY=").build())
               .build();

      HttpResponse redirectResponse =
HttpResponse.builder().statusCode(301).build();

      HttpRequest bucketFooExistsNowUsesGET =
HttpRequest.builder().method("GET").endpoint(
               URI.create("https://foo.s3.amazonaws.com/?max-keys=0")).headers(
               ImmutableMultimap.<String, String>
builder().put("Host", "foo.s3.amazonaws.com").put("Date",
                        CONSTANT_DATE).put("Authorization", "AWS
identity:ZWVz2v/jGB+ZMmijoyfH9mFMPo0=").build())
               .build();

      HttpResponse success = HttpResponse.builder().statusCode(200).build();

      S3Client clientWhenBucketExists =
requestsSendsResponses(bucketFooExists, redirectResponse,
bucketFooExistsNowUsesGET, success);

      assert clientWhenBucketExists.bucketExists("foo");

   }
}
{% endhighlight %}


## Annotated unit tests

XXX

## Curl Cheat Sheet

XXX


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



## Advanced topics in testing

