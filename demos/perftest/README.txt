====
    Licensed to jclouds, Inc. (jclouds) under one or more
    contributor license agreements.  See the NOTICE file
    distributed with this work for additional information
    regarding copyright ownership.  jclouds licenses this file
    to you under the Apache License, Version 2.0 (the
    "License"); you may not use this file except in compliance
    with the License.  You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing,
    software distributed under the License is distributed on an
    "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
    KIND, either express or implied.  See the License for the
    specific language governing permissions and limitations
    under the License.
====

This sample uses the Google App Engine for Java SDK located at http://googleappengine.googlecode.com/files/appengine-java-sdk-1.3.5.zip

Please unzip the above file and modify your maven settings.xml like below before attempting to run 'mvn -Plive install'

    <profile>
      <id>appengine</id>
      <activation>
        <activeByDefault>true</activeByDefault>
      </activation>
      <properties>
        <appengine.home>/path/to/appengine-java-sdk-1.3.5</appengine.home>
      </properties>
    </profile>

    <profile>
      <id>aws</id>
      <activation>
        <activeByDefault>true</activeByDefault>
      </activation>
      <properties>
        <jclouds.aws.accesskeyid>YOUR_ACCESS_KEY_ID</jclouds.aws.accesskeyid>
        <jclouds.aws.secretaccesskey>YOUR_SECRET_KEY</jclouds.aws.secretaccesskey>
      </properties>
    </profile>

    <repositories>
        <repository>
            <id>jclouds</id>
            <url>http://jclouds.googlecode.com/svn/trunk/repo</url>
        </repository>
        <repository>
            <id>jclouds-rimu-snapshots-nexus</id>
            <url>http://jclouds.rimuhosting.com:8081/nexus/content/repositories/snapshots</url>
            <snapshots>
                <enabled>true</enabled>
            </snapshots>
        </repository>
    </repositories>
