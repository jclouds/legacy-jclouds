This demo is used to show how to change volume size for EBS-backed instance. Basically, jclouds API is used to run a sequence of commands, as described in an excellent article: http://alestic.com/2009/12/ec2-ebs-boot-resize.

There are 2 directories:

First one, /resize-ebs-java, has all necessary classes for the demo (under org.jclouds.tools.ebsresize), and also a sample Java launcher (EbsResizeMain). Please change the variables in EbsResizeMain to run the demo.

Second directory, /jruby-client, has a JRuby launcher for the same Java libraries. Please change the variables in launcher.rb to run the demo.
To manage the maven dependencies, run as:
$ mvn jruby:run

Pre-requisites:
- running EBS-backed instance at Amazon EC2
- Ubuntu image
- proper settings in place (EbsResizeMain or launcher.rb)
- file system is ext3 (otherwise, change is needed in org.jclouds.tools.ebsresize.InstanceVolumeManager)
- the demo doesn't take care of freezing the database, filesystem and application data