---
layout: jclouds
title: Google App Engine Sample
---

# How to install and deploy the google app engine sample

## Introduction 
We have a google app engine demo in the jclouds codebase. 

  * `googleappengine ` shows compute and storage interaction with aws-ec2 and aws-s3 

These are just showing how to wire things up, but also prove that jclouds is google appengine friendly :)

## Setup

The Google App Engine for Java sample (samples/googleappengine) requires that you 
download and extract the [sdk](http://googleappengine.googlecode.com/files/appengine-java-sdk-1.4.3.zip0) 
and export the shell variable APPENGINE_HOME to that location.

*  checkout from git
  * git clone git://github.com/jclouds/jclouds.git
  * enter the right directory for aws-ec2 + aws-s3 `cd jclouds/aws/demos/googleappengine`
 
*  setup your appid
The application id corresponds to the live appengine application you wish to deploy the sample to.
  Developers will use the default.  Non-developers will have to specify an application id they control.
  * developer appid
    * _yourappid_ is default: `jclouds-aws-demo`
  * anonymous appid
    * please add the property `appengine.applicationid` to an appengine application id you control.
      * ex.  {{{<appengine.applicationid>yourappid</appengine.applicationid>}}}

* setup your credentials
	As the sample connects to  azure, rackspace, and aws, you will need to specify credentials for it to use.
	<!-- Change Link -->
  * [Maven#AWS_Access_Credentials instructions]

* Verify your setup
  * ensure your appid is correct

{% highlight text %}
$ mvn help:effective-pom |grep appengine.applicationid 
    <appengine.applicationid>your_appid</appengine.applicationid>
{% endhighlight %}

  * ensure your appengine home is correct

{% highlight text %}
bash-3.2$ export |grep APPENGINE_HOME
declare -x APPENGINE_HOME="/Users/adriancole/appengine-java-sdk-1.4.3"
{% endhighlight %}

  * if you are running the aws demo, you only need aws credentials. 

{% highlight text %}
$  mvn -Plive help:effective-pom|grep test.aws
    <test.aws.credential>YOUR_VALUE_HERE</test.aws.credential>
    <test.aws.identity>YOUR_VALUE_HERE</test.aws.identity>
{% endhighlight %}` 

## Working with the Sample

*  Testing in the dev sandbox
For the duration of the integration-test phase of maven, the following command will deploy the sample to 
the google app engine dev server (jetty listening on localhost on your machine).

  * from within the base directory you checked out the sample, execute `mvn -Plive install`

* Deploying to the live google app engine server
  * from within the base directory you checked out the sample, execute `appcfg.sh update`
    * ex `$APPENGINE_HOME/bin/appcfg.sh -e ferncam1@gmail.com update target/jclouds-googleappengine-example</code>`

* Viewing the live application output
  * The url that serves your application will be found at: http://_yourappid_.appspot.com/guice/containers.blobstore
    * [demo](http://jclouds-aws-demo.appspot.com/guice/status.check default aws-ec2/aws-s3)

* checking admin status on the live application
  * The admin url for your application is here: http://appengine.google.com/dashboard?&app_id=_yourappid_
    * [demo](http://appengine.google.com/dashboard?&app_id=jclouds-aws-demo aws-ec2/aws-s3)


