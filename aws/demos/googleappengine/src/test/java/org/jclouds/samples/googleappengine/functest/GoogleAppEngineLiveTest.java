/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */
package org.jclouds.samples.googleappengine.functest;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

import org.jclouds.aws.ec2.EC2PropertiesBuilder;
import org.jclouds.aws.s3.S3PropertiesBuilder;
import org.jclouds.util.Utils;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

/**
 * Starts up the Google App Engine for Java Development environment and deploys an application which
 * tests S3.
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", sequential = true, testName = "functionalTests")
public class GoogleAppEngineLiveTest {

   GoogleDevServer server;
   private URL url;

   @BeforeTest
   @Parameters( { "warfile", "devappserver.address", "devappserver.port" })
   public void startDevAppServer(final String warfile, final String address, final String port)
            throws Exception {
      url = new URL(String.format("http://%s:%s", address, port));
      Properties props = new Properties();
      String identity = checkNotNull(System.getProperty("jclouds.test.identity"), "jclouds.test.identity");
      String credential = checkNotNull(System.getProperty("jclouds.test.credential"), "jclouds.test.credential");

      props = new S3PropertiesBuilder(props).credentials(identity, credential).build();

      props = new EC2PropertiesBuilder(props).credentials(identity, credential).build();

      server = new GoogleDevServer();
      server.writePropertiesAndStartServer(address, port, warfile, props);
   }

   @Test
   public void shouldPass() throws InterruptedException, IOException {
      InputStream i = url.openStream();
      String string = Utils.toStringAndClose(i);
      assert string.indexOf("Welcome") >= 0 : string;
   }

   @Test(invocationCount = 5, enabled = true)
   public void testGuiceJCloudsSerial() throws InterruptedException, IOException {
      URL gurl = new URL(url, "/guice/status.check");
      InputStream i = gurl.openStream();
      String string = Utils.toStringAndClose(i);
      assert string.indexOf("List") >= 0 : string;
   }

   @Test(invocationCount = 10, enabled = true, threadPoolSize = 3)
   public void testGuiceJCloudsParallel() throws InterruptedException, IOException {
      URL gurl = new URL(url, "/guice/status.check");
      InputStream i = gurl.openStream();
      String string = Utils.toStringAndClose(i);
      assert string.indexOf("List") >= 0 : string;
   }
}
