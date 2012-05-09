---
layout: docs
title: Developer Setup instructions for Eclipse
---

# jclouds Developer Setup instructions for Eclipse

## Introduction
`Please do not check any eclipse related artifacts(e.g. .settings, .classpath, .project etc.) into git.` 

The following document will help you get started, if you are unfamiliar with eclipse, maven, and git.

## Pre-requisites
*  Eclipse 3.4 or higher

## Setup
*  Clone jclouds from git
   *  If you are a collaborator, use: git clone git@github.com:jclouds/jclouds.git or 
  	you can use the EGit plugin for eclipse - http://www.eclipse.org/egit/
  *  Otherwise clone from read-only, or clone your fork of jclouds, see the 
  	 [Developer's Guide](http://code.google.com/p/jclouds/wiki/DevelopersGuide) for more information.

*  At the root directory where you checked out, execute `mvn clean install eclipse:eclipse -Dmaven.javadoc.skip=true -DdownloadSources=true -DdownloadJavadocs=true`
   *  If the module you are working on is not in the default build list, change to that directory and execute `mvn eclipse:eclipse -DdownloadSources=true -DdownloadJavadocs=true`
   *  Note: if you are running windows, add `-DskipTests` as some tests fail on windows

*  Open eclipse
   *  Setup M2_REPO classpath variable
     *  Goto Eclipse Preferences &rarr; Java &rarr; Build Path &rarr; Classpath Variables
     *  New
       *  Name: M2_REPO
       *  Path: /path/to/.m2/repository
          *  Ex. windows: it is in your user profile (`%USERPROFILE%`), like `c:\Documents and Settings\username\.m2\repository`
          *  Ex. mac: it is in your home directory (`$HOME`). like `/Users/username/.m2/repository`
  *  Import projects
     *  File, import, General, Existing projects into workspace
     *  Choose the directory your git clone created
     *  Choose all modules
  *  Setup TestNG plugin
     *  Help, Install New Software
     *  Add 
       *  Name: testng
       *  Location: `http://beust.com/eclipse`
     *  You should see a checkbox near `TestNG` now in the `Available Software` view.  Check and click until done.

## Running Tests

Tests are created in TestNG, so make sure you have the eclipse plug-in installed.  

## Live testing 

To run tests that use a real live service like aws-s3, you will need to provide your credentials to Eclipse and tell your tests to use them.  
You'll key these on the provider name (ex. provider  is aws-s3, cloudfiles-us, aws-ec2, etc)

To implement this, open the test's Run Configurations and enter in the following into VM arguments-

{% highlight text %}

-Dbasedir=. -Dtest.provider.identity=identity -Dtest.provider.credential=credential

{% endhighlight %}

ex. for vcloud

{% highlight text %}

-Dbasedir=. -Dtest.vcloud.endpoint=https://vcloudserverilike/api -Dtest.vcloud.identity=user@org -Dtest.vcloud.credential=password

{% endhighlight %}

### Testing a BlobStore

If you are testing a BlobStore, you will also need to pass the test initializer you can find in its pom.xml file

ex. for aws-s3

{% highlight text %}

-Dbasedir=. -Dtest.aws-s3.identity=accesskey -Dtest.aws-s3.credential=secret -Dtest.initializers=org.jclouds.aws.s3.blobstore.integration.AWSS3TestInitializer

{% endhighlight %}

## Ssh testing

Ssh tests need access to an ssh host you have access to.  
Note that this is only required for running pure SSH tests.  
SSH indirectly used via a cloud will use the cloud credentials not the ones below. 
Note that the destination must be a Unix-like host that at least contains a world readable /etc/passwd file.

*  In Eclipse's Preferences open the section Run/Debug > String Substitution
*  Create two new variables named:
  *  `test.ssh.username` with the value of your ssh username
  *  `test.ssh.password` with the value of your ssh password

Then, for each test that uses ssh, open the test's Run Configurations and enter in the following into VM arguments:

{% highlight text %}
-Dtest.ssh.host=localhost -Dtest.ssh.port=22 -Dtest.ssh.username=${test.ssh.username} -Dtest.ssh.password=${test.ssh.password}
{% endhighlight %}

*  note that you can replace {{{test.ssh.host}}} and {{{test.ssh.port}}} above if you are not connecting to localhost.


## Create a demo project

For instance if you want to create a demo project in aws, you can do it with following steps:

*  Copy a sample project there (aws/demos directory) 
*  Change pom.xml from demos and add new module
*  Change pom.xml of the new project (change names and classes)
*  Add your code
*  `mvn eclipse:clean `
*  `mvn eclipse:eclipse -DskipTests=true`
*  You  can import your project now in eclipse.



## Clean build 
  * Close Eclipse
  * `mvn eclipse:clean`
  * `mvn clean` (may not be needed?)
  * `mvn install`
  * Then to start coding again:
    * `mvn eclipse:eclipse -Dmaven.javadoc.skip=true`
    * Open Eclipse
    * Delete old project references (but not underlying files)
    * Re-import projects
