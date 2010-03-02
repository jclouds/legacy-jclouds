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
package org.jclouds.demo.tweetstore.integration;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.atmosonline.saas.reference.AtmosStorageConstants.PROPERTY_EMCSAAS_KEY;
import static org.jclouds.atmosonline.saas.reference.AtmosStorageConstants.PROPERTY_EMCSAAS_UID;
import static org.jclouds.aws.reference.AWSConstants.PROPERTY_AWS_ACCESSKEYID;
import static org.jclouds.aws.reference.AWSConstants.PROPERTY_AWS_SECRETACCESSKEY;
import static org.jclouds.azure.storage.reference.AzureStorageConstants.PROPERTY_AZURESTORAGE_ACCOUNT;
import static org.jclouds.azure.storage.reference.AzureStorageConstants.PROPERTY_AZURESTORAGE_KEY;
import static org.jclouds.demo.tweetstore.reference.TweetStoreConstants.PROPERTY_TWEETSTORE_CONTAINER;
import static org.jclouds.rackspace.reference.RackspaceConstants.PROPERTY_RACKSPACE_KEY;
import static org.jclouds.rackspace.reference.RackspaceConstants.PROPERTY_RACKSPACE_USER;
import static org.jclouds.twitter.reference.TwitterConstants.PROPERTY_TWITTER_PASSWORD;
import static org.jclouds.twitter.reference.TwitterConstants.PROPERTY_TWITTER_USER;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import org.jclouds.atmosonline.saas.AtmosStoragePropertiesBuilder;
import org.jclouds.aws.s3.S3PropertiesBuilder;
import org.jclouds.azure.storage.blob.AzureBlobPropertiesBuilder;
import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.blobstore.BlobStoreContextFactory;
import org.jclouds.demo.tweetstore.config.GuiceServletConfig;
import org.jclouds.rackspace.cloudfiles.CloudFilesPropertiesBuilder;
import org.jclouds.twitter.TwitterPropertiesBuilder;
import org.jclouds.util.Utils;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;

/**
 * Starts up the Google App Engine for Java Development environment and deploys an application which
 * tests accesses twitter and blobstores.
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", sequential = true, testName = "functionalTests")
public class TweetStoreLiveTest {

   GoogleDevServer server;
   private URL url;
   private Iterable<BlobStoreContext> contexts;
   private String container;

   @BeforeTest
   @Parameters( { "warfile", "devappserver.address", "devappserver.port" })
   public void startDevAppServer(final String warfile, final String address, final String port)
            throws Exception {
      url = new URL(String.format("http://%s:%s", address, port));
      Properties props = new Properties();
      props.setProperty(PROPERTY_TWEETSTORE_CONTAINER, checkNotNull(System
               .getProperty(PROPERTY_TWEETSTORE_CONTAINER), PROPERTY_TWEETSTORE_CONTAINER));

      // WATCH THIS.. when adding a new context, you must update the string
      props
               .setProperty(GuiceServletConfig.PROPERTY_BLOBSTORE_CONTEXTS,
                        "cloudfiles,s3,azureblob");

      props = new TwitterPropertiesBuilder(props).withCredentials(
               checkNotNull(System.getProperty(PROPERTY_TWITTER_USER), PROPERTY_TWITTER_USER),
               System.getProperty(PROPERTY_TWITTER_PASSWORD, PROPERTY_TWITTER_PASSWORD)).build();

      props = new S3PropertiesBuilder(props)
               .withCredentials(
                        checkNotNull(System.getProperty(PROPERTY_AWS_ACCESSKEYID),
                                 PROPERTY_AWS_ACCESSKEYID),
                        System.getProperty(PROPERTY_AWS_SECRETACCESSKEY,
                                 PROPERTY_AWS_SECRETACCESSKEY)).build();

      props = new CloudFilesPropertiesBuilder(props).withCredentials(
               checkNotNull(System.getProperty(PROPERTY_RACKSPACE_USER), PROPERTY_RACKSPACE_USER),
               System.getProperty(PROPERTY_RACKSPACE_KEY, PROPERTY_RACKSPACE_KEY)).build();

      props = new AzureBlobPropertiesBuilder(props).withCredentials(
               checkNotNull(System.getProperty(PROPERTY_AZURESTORAGE_ACCOUNT),
                        PROPERTY_AZURESTORAGE_ACCOUNT),
               System.getProperty(PROPERTY_AZURESTORAGE_KEY, PROPERTY_AZURESTORAGE_KEY)).build();

      server = new GoogleDevServer();
      server.writePropertiesAndStartServer(address, port, warfile, props);
   }

   @BeforeClass
   void clearAndCreateContainers() throws InterruptedException, ExecutionException,
            TimeoutException, IOException {
      container = checkNotNull(System.getProperty(PROPERTY_TWEETSTORE_CONTAINER));
      BlobStoreContextFactory factory = new BlobStoreContextFactory();
      BlobStoreContext s3Context = factory.createContext("s3", checkNotNull(System
               .getProperty(PROPERTY_AWS_ACCESSKEYID), PROPERTY_AWS_ACCESSKEYID), System
               .getProperty(PROPERTY_AWS_SECRETACCESSKEY, PROPERTY_AWS_SECRETACCESSKEY));

      BlobStoreContext cfContext = factory.createContext("cloudfiles", checkNotNull(System
               .getProperty(PROPERTY_RACKSPACE_USER), PROPERTY_RACKSPACE_USER), System.getProperty(
               PROPERTY_RACKSPACE_KEY, PROPERTY_RACKSPACE_KEY));

      BlobStoreContext azContext = factory.createContext("azureblob", checkNotNull(System
               .getProperty(PROPERTY_AZURESTORAGE_ACCOUNT), PROPERTY_AZURESTORAGE_ACCOUNT), System
               .getProperty(PROPERTY_AZURESTORAGE_KEY, PROPERTY_AZURESTORAGE_KEY));

      this.contexts = ImmutableList.of(s3Context, cfContext, azContext);
      boolean deleted = false;
      for (BlobStoreContext context : contexts) {
         if (context.getBlobStore().containerExists(container)) {
            System.err.printf("deleting container %s at %s%n", container, context
                     .getProviderSpecificContext().getEndPoint());
            context.getBlobStore().deleteContainer(container);
            deleted = true;
         }
      }
      if (deleted) {
         System.err.println("sleeping 60 seconds to allow containers to clear");
         Thread.sleep(60000);
      }
      for (BlobStoreContext context : contexts) {
         System.err.printf("creating container %s at %s%n", container, context
                  .getProviderSpecificContext().getEndPoint());
         context.getBlobStore().createContainerInLocation("default", container);
      }
      if (deleted) {
         System.err.println("sleeping 5 seconds to allow containers to create");
         Thread.sleep(30000);
      }
   }

   @Test
   public void shouldPass() throws InterruptedException, IOException {
      InputStream i = url.openStream();
      String string = Utils.toStringAndClose(i);
      assert string.indexOf("Welcome") >= 0 : string;
   }

   @Test(dependsOnMethods = "shouldPass", expectedExceptions = IOException.class)
   public void shouldFail() throws InterruptedException, IOException {
      new URL(url, "/store/do").openStream();
   }

   @Test(dependsOnMethods = "shouldFail")
   public void testPrimeContainers() throws IOException, InterruptedException {
      URL gurl = new URL(url, "/store/do");

      // WATCH THIS, you need to add a context each time
      for (String context : new String[] { "cloudfiles", "s3", "azureblob" }) {
         System.out.println("storing at context: " + context);
         HttpURLConnection connection = (HttpURLConnection) gurl.openConnection();
         connection.addRequestProperty("X-AppEngine-QueueName", "twitter");
         connection.addRequestProperty("context", context);
         InputStream i = connection.getInputStream();
         String string = Utils.toStringAndClose(i);
         assert string.indexOf("Done!") >= 0 : string;
         connection.disconnect();
      }

      System.err.println("sleeping 20 seconds to allow for eventual consistency delay");
      Thread.sleep(20000);
      for (BlobStoreContext context : contexts) {
         assert context.createInputStreamMap(container).size() > 0 : context
                  .getProviderSpecificContext().getEndPoint();
      }
   }

   @Test(invocationCount = 5, dependsOnMethods = "testPrimeContainers")
   public void testSerial() throws InterruptedException, IOException {
      URL gurl = new URL(url, "/tweets/get");
      InputStream i = gurl.openStream();
      String string = Utils.toStringAndClose(i);
      assert string.indexOf("Tweets in Clouds") >= 0 : string;
   }

   @Test(invocationCount = 10, dependsOnMethods = "testPrimeContainers", threadPoolSize = 3)
   public void testParallel() throws InterruptedException, IOException {
      URL gurl = new URL(url, "/tweets/get");
      InputStream i = gurl.openStream();
      String string = Utils.toStringAndClose(i);
      assert string.indexOf("Tweets in Clouds") >= 0 : string;
   }
}
