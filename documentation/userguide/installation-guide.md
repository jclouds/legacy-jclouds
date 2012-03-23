---
layout: docs
title: Installation
---
# Installation

### Getting latest jclouds binaries

  * download [lein](https://github.com/technomancy/leiningen/raw/stable/bin/lein leins) and make it executable
  * create a __project.clj__ file with the below contents
{% highlight clojure %}
(defproject deps "1" :dependencies [[org.jclouds/jclouds-all "1.5.0-alpha.1"] [org.jclouds.driver/jclouds-sshj "1.5.0-alpha.1"]])
{% endhighlight %}
  * execute __lein deps__ which will fill a lib dir with all the jclouds jars

Replace the __provider__ and __api__ in the above directory paths to the ones you want to use in your project.

### Adding jclouds to your Apache Maven project

If your project is managed using Apache Maven, then it is very easy to use the jclouds, just add
the following your project's __pom.xml__:

{% highlight xml %}
<dependencies>
   <dependency>
        <groupId>org.jclouds</groupId>
        <artifactId>jclouds-allcompute</artifactId>
        <version>1.5.0-alpha.1</version>
   </dependency>
   <dependency>
       <groupId>org.jclouds</groupId>
       <artifactId>jclouds-allblobstore</artifactId>
       <version>1.5.0-alpha.1</version>
   </dependency>
</dependencies>
{% endhighlight %}

### Adding jclouds to your Apache Ant project.

If you use ant, you will need to install [maven ant tasks](http://maven.apache.org/ant-tasks/index.html).
Then, add jclouds to your __build.xml__ as shown below:

{% highlight xml %}
<artifact:dependencies pathId="jclouds.classpath">
 <dependency groupId="org.jclouds"
			 artifactId="jclouds-allcompute"
			version="1.5.0-alpha.1" />
 <dependency groupId="org.jclouds"
			 artifactId="jclouds-allblobstore"
			version="1.5.0-alpha.1" />
</artifact:dependencies>
{% endhighlight %}

### Getting the binaries using Apache Ant

If you want to automate fetching the jclouds binaries, you can use the following ant script.

Install ant, copy the following into a __build.xml__ file, tweaking things like 'provider' and 'driver' as necessary.
The following example uses *aws-s3* as provider and *jclouds-gae* as driver.
You can see the list of supported providers in the [[Supported Providers|SupportedProviders]].

When you runt this script, it will build a __lib__ directory full of jars you can later copy into your own project.

{% highlight xml %}
<project default="sync-lib" xmlns:artifact="urn:maven-artifact-ant" >
  <target name="sync-lib" depends="initmvn">
    <delete dir="lib" />
    <mkdir dir="lib" />
    <artifact:dependencies filesetId="jclouds.fileset" versionsId="dependency.versions">
      <dependency groupId="org.jclouds.provider" artifactId="aws-s3" version="1.5.0-alpha.1" />
      <dependency groupId="org.jclouds.driver" artifactId="jclouds-gae" version="1.5.0-alpha.1" />
    </artifact:dependencies>
    <copy todir="lib" verbose="true">
      <fileset refid="jclouds.fileset"/>
      <mapper type="flatten" />
    </copy>
  </target>
  <get src="http://opensource.become.com/apache/maven/binaries/maven-ant-tasks-2.1.1.jar" dest="maven-ant-tasks"/>
  <target name="initmvn">
    <path id="maven-ant-tasks.classpath" path="maven-ant-tasks"/>
    <typedef resource="org/apache/maven/artifact/ant/antlib.xml"
			uri="urn:maven-artifact-ant"
			classpathref="maven-ant-tasks.classpath"/>
  </target>
</project>
{% endhighlight %}

### Adding jclouds to your Clojure project using lieningen

If you use lieningen, you can add jclouds to your project.clj like below, supporting clojure 1.2 and 1.3:

{% highlight clojure %}
:dependencies [[org.clojure/clojure "1.3.0"]
               [org.clojure/core.incubator "0.1.0"]
               [org.clojure/tools.logging "0.2.3"]
               [org.jclouds/jclouds-allcompute "1.5.0-alpha.1"]
               [org.jclouds/jclouds-allblobstore "1.5.0-alpha.1"]]
{% endhighlight %}

### making your own lib dir
  * using maven
    * Create a pom.xml file with dependencies you need (ex. org.jclouds/jclouds-all) and the snapshot repository, if you want snapshot version (1.5.0-SNAPSHOT)
    * execute mvn dependency:copy-dependencies
    * you'll notice a new directory target/dependency with all the jars you need
  * using lein
    * Create a project.clj file with dependencies you need (ex. org.jclouds/jclouds-all) and the snapshot repository, if you want snapshot version (1.5.0-SNAPSHOT)
    * execute lein deps
    * you'll notice a new directory lib with all the jars you need
  * using ant
    * Install ant and run the following build script, tweaking things like __provider__ and __driver__ as necessary.  This will build a lib dir full of jars you can copy into your project.
{% highlight xml %}
<project default="sync-lib" xmlns:artifact="urn:maven-artifact-ant" >

  <target name="sync-lib" depends="initmvn">
    <delete dir="lib" />
    <mkdir dir="lib" />
    <artifact:dependencies filesetId="jclouds.fileset" versionsId="dependency.versions">
      <dependency groupId="org.jclouds.provider" artifactId="aws-s3" version="1.5.0-alpha.1" />
      <dependency groupId="org.jclouds.driver" artifactId="jclouds-gae" version="1.5.0-alpha.1" />
    </artifact:dependencies>

    <copy todir="lib" verbose="true">
      <fileset refid="jclouds.fileset"/>
      <mapper type="flatten" />
    </copy>
  </target>

  <get src="http://opensource.become.com/apache/maven/binaries/maven-ant-tasks-2.1.3.jar" dest="maven-ant-tasks"/>

  <target name="initmvn">
    <path id="maven-ant-tasks.classpath" path="maven-ant-tasks"/>
    <typedef resource="org/apache/maven/artifact/ant/antlib.xml" uri="urn:maven-artifact-ant" classpathref="maven-ant-tasks.classpath"/>
  </target>
</project>
{% endhighlight %}

### Using the jclouds Snapshot Builds

If you want to use the bleeding edge release of jclouds, you'll need to setup a maven dependency pointing to our sonatype snapshot repo.

You need to update your repositories and add the following in your project's pom.xml:

{% highlight xml %}
<repositories>
    <repository>
        <id>jclouds-snapshots</id>
        <url>https://oss.sonatype.org/content/repositories/snapshots</url>
        <snapshots>
             <enabled>true</enabled>
        </snapshots>
    </repository>
</repositories>
<dependencies>
   <dependency>
        <groupId>org.jclouds</groupId>
        <artifactId>jclouds-allcompute</artifactId>
        <version>1.5.0-SNAPSHOT</version>
   </dependency>
   <dependency>
        <groupId>org.jclouds</groupId>
        <artifactId>jclouds-allblobstore</artifactId>
        <version>1.5.0-SNAPSHOT</version>
   </dependency>
</dependencies>
{% endhighlight %}

### Adding jclouds snapshot to your ant project

If you use ant, you will need to install [maven ant tasks](http://maven.apache.org/ant-tasks/index.html).

Then, add jclouds snapshot dependencies to your __build.xml__ as shown below:

{% highlight xml %}
<artifact:remoteRepository id="jclouds.snapshot.repository"
  url="https://oss.sonatype.org/content/repositories/snapshots" />
<artifact:dependencies pathId="jclouds.classpath">
 <dependency groupId="org.jclouds"
			 artifactId="jclouds-allcompute"
			 version="1.5.0-SNAPSHOT" />
 <dependency groupId="org.jclouds"
			artifactId="jclouds-allblobstore"
			version="1.5.0-SNAPSHOT" />
 <remoteRepository refid="jclouds.snapshot.repository" />
</artifact:dependencies>
{% endhighlight %}

### Adding jclouds snapshots to your leiningen (clojure) project

If you use lieningen, you can add jclouds snapshots to your __project.clj__ like below:

{% highlight clojure %}
  :dependencies [[org.clojure/clojure "1.3.0"]
                 [org.clojure/core.incubator "0.1.0"]
                 [org.clojure/tools.logging "0.2.3"]
                 [org.jclouds/jclouds-allcompute "1.5.0-SNAPSHOT"]
                 [org.jclouds/jclouds-allblobstore "1.5.0-SNAPSHOT"]]
  :repositories { "jclouds-snapshot" "https://oss.sonatype.org/content/repositories/snapshots"}
{% endhighlight %}
