====

    Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>

    ====================================================================
    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
    ====================================================================
====

This sample is a "port" of jclouds-demo-gae-tweetstore with the initial context setup
and wiring carried out with Spring. It is intended to demonstrate how to integrate
jclouds into your Spring application.

It should not be regarded as a sample of how to write a web application using Spring,
however! The original jclouds-demo-gae-tweetstore has been modified in as few places as
possible; it has not been rewritten in the style of a Spring MVC application.

This sample uses the Google App Engine for Java SDK located at 
http://code.google.com/p/googleappengine/downloads/list

Please unzip the above file and modify your maven settings.xml like below before
attempting to run 'mvn -Plive install'

    <profile>
      <id>appengine</id>
      <activation>
        <activeByDefault>true</activeByDefault>
      </activation>
      <properties>
        <appengine.home>/path/to/appengine-java-sdk-1.3.5</appengine.home>
        <appengine.applicationid>yourappid</appengine.applicationid>
      </properties>
    </profile>

    <profile>
      <id>keys</id>
      <activation>
        <activeByDefault>true</activeByDefault>
      </activation>
      <properties>
        <jclouds.aws.accesskeyid>YOUR_ACCESS_KEY_ID</jclouds.aws.accesskeyid>
        <jclouds.aws.secretaccesskey>YOUR_SECRET_KEY</jclouds.aws.secretaccesskey>
        <jclouds.rackspace.user>YOUR_USER</jclouds.rackspace.user>
        <jclouds.rackspace.key>YOUR_HEX_KEY</jclouds.rackspace.key>
        <jclouds.azure.storage.account>YOUR_ACCOUNT</jclouds.azure.storage.account>
        <jclouds.azure.storage.key>YOUR_BASE64_ENCODED_KEY</jclouds.azure.storage.key>
        <jclouds.twitter.user>YOUR_TWITTER_USERNAME</jclouds.twitter.user>
        <jclouds.twitter.password>YOUR_TWITTER_PASSWORD</jclouds.twitter.password>        
      </properties>
    </profile>

    <repositories>
        <repository>
            <id>jclouds</id>
            <url>http://jclouds.googlecode.com/svn/trunk/repo</url>
        </repository>
    </repositories>
