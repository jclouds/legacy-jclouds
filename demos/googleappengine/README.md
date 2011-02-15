# googleappengine
This example will list the current instances and buckets in your Amazon account, using portable apis.  This example runs inside Google AppEngine as a war file.

## Prepare

Please unzip http://googleappengine.googlecode.com/files/appengine-java-sdk-1.4.0.zip and export the system variable APPENGINE_HOME accordingly.

## Build

To install your test on your remote appengine application, first prepare locally via the below instructions:
mvn -Dappengine.applicationid=YOUR_APPLICATION -Dtest.aws.identity=YOUR_ACCESS_KEY_ID -Dtest.aws.credential=YOUR_SECRET_KEY -Plive install

## Deploy

then, you can upload this to google appengine like below:
appcfg.sh -e YOUR_EMAIL update target//jclouds-googleappengine-example

## Test

finally, you can verify with a web url:
http://YOUR_APPLICATION_ID.appspot.com/guice/status.check

## License

Copyright (C) 2011 Cloud Conscious, LLC. <info@cloudconscious.com>

Licensed under the Apache License, Version 2.0 

