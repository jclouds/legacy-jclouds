---
layout: jclouds
title: Deploying jclouds to Apache Fleix OSGi container
---
# Deploying jclouds to Apache Fleix OSGi container

## Introduction

Deploy jclouds to osgi felix container


## Details 

* Download bnd tool from [here](http://www.aqute.biz/Bnd/Download)
* change to jclouds/all
*  run `mvn dependency:copy-dependencies` command
* change to target/dependency
 run the following command to create bundles of non-osgi dependencies

{% highlight text %}
java -jar biz.aQute.bnd.jar wrap aopalliance-1.0.jar \
bsh-2.0b4.jar \
cglib-nodep-2.1_3.jar \
clojure-1.2.0.jar \
clojure-contrib-1.2.0.jar \
easymock-2.4.jar \
easymockclassextension-2.4.jar \
guava-r09.jar \
java-xmlbuilder-0.3.jar \
javax.inject-1.jar \
jsch-0.1.44-1.jar \
jsr250-api-1.0.jar \
jsr305-1.3.9.jar \
junit-3.8.1.jar \
oauth-20100527.jar \
snakeyaml-1.6.jar \
swank-clojure-1.2.1.jar

for f in *bar ; do mv $f `basename $f bar`jar; done
{% endhighlight %}

* Download felix file install and copy to felix/bundle.
* Create felix/load dir and copy all jar files from target/dependency here.
* Start felix framework using `java -jar bin/felix.jar`.
* Wait a few seconds and list the bundles using `lb` command.


