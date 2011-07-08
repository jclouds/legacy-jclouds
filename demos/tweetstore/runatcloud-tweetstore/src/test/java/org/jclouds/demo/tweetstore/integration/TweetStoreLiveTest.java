/**
 *
 * Copyright (C) 2011 Cloud Conscious, LLC. <info@cloudconscious.com>
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
import static org.jclouds.demo.tweetstore.controller.StoreTweetsController.AUTHORIZED_REQUEST_ORIGINATOR_HEADER;
import static org.jclouds.demo.tweetstore.reference.TweetStoreConstants.PROPERTY_TWEETSTORE_BLOBSTORES;
import static org.jclouds.demo.tweetstore.reference.TweetStoreConstants.PROPERTY_TWEETSTORE_CONTAINER;
import static org.jclouds.demo.tweetstore.reference.TwitterConstants.PROPERTY_TWITTER_ACCESSTOKEN;
import static org.jclouds.demo.tweetstore.reference.TwitterConstants.PROPERTY_TWITTER_ACCESSTOKEN_SECRET;
import static org.jclouds.demo.tweetstore.reference.TwitterConstants.PROPERTY_TWITTER_CONSUMER_KEY;
import static org.jclouds.demo.tweetstore.reference.TwitterConstants.PROPERTY_TWITTER_CONSUMER_SECRET;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.blobstore.BlobStoreContextFactory;
import org.jclouds.demo.tweetstore.config.GuiceServletConfig;
import org.jclouds.demo.tweetstore.controller.StoreTweetsController;
import org.jclouds.logging.log4j.config.Log4JLoggingModule;
import org.jclouds.rest.AuthorizationException;
import org.jclouds.util.Strings2;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.inject.Module;

/**
 * Starts up the Google App Engine for Java Development environment and deploys an application which
 * tests accesses twitter and blobstores.
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", singleThreaded = true)
public class TweetStoreLiveTest {

   RunAtCloudServer server;
   private URL url;
   private Map<String, BlobStoreContext> contexts;
   private String container;
   private static final Iterable<String> blobstores = 
       Splitter.on(',').split(getRequiredSystemProperty(PROPERTY_TWEETSTORE_BLOBSTORES)); 
   private static final Properties props = new Properties();

   @BeforeTest
   void clearAndCreateContainers() throws InterruptedException, ExecutionException, TimeoutException, IOException,
         TwitterException {
       container = getRequiredSystemProperty(PROPERTY_TWEETSTORE_CONTAINER);

      props.setProperty(PROPERTY_TWEETSTORE_CONTAINER, container);
      props.setProperty(GuiceServletConfig.PROPERTY_BLOBSTORE_CONTEXTS, Joiner.on(',').join(blobstores));

      // put all identity/credential pairs into the client
      addCredentialsForBlobStores(props);

      // example of an ad-hoc client configuration
      addConfigurationForTwitter(props);

      final BlobStoreContextFactory factory = new BlobStoreContextFactory();
      // for testing, capture logs.
      final Set<Module> wiring = ImmutableSet.<Module> of(new Log4JLoggingModule());
      this.contexts = Maps.newConcurrentMap();

      for (String provider : blobstores) {
         contexts.put(provider, factory.createContext(provider, wiring, props));
      }

      Configuration conf = new ConfigurationBuilder()
          .setOAuthConsumerKey(props.getProperty(PROPERTY_TWITTER_CONSUMER_KEY))
          .setOAuthConsumerSecret(props.getProperty(PROPERTY_TWITTER_CONSUMER_SECRET))
          .setOAuthAccessToken(props.getProperty(PROPERTY_TWITTER_ACCESSTOKEN))
          .setOAuthAccessTokenSecret(props.getProperty(PROPERTY_TWITTER_ACCESSTOKEN_SECRET))
          .build();
      Twitter client = new TwitterFactory(conf).getInstance();
      StoreTweetsController controller = new StoreTweetsController(contexts, container, client);

      ResponseList<Status> statuses = client.getMentions();

      boolean deleted = false;
      for (BlobStoreContext context : contexts.values()) {
         try {
            if (context.getBlobStore().containerExists(container)) {
               System.err.printf("deleting container %s at %s%n", container, context.getProviderSpecificContext()
                     .getEndpoint());
               context.getBlobStore().deleteContainer(container);
               deleted = true;
            }
         } catch (AuthorizationException e) {
            throw new AuthorizationException("for context: " + context, e);
         }
      }
      if (deleted) {
         System.err.println("sleeping 60 seconds to allow containers to clear");
         Thread.sleep(60000);
      }
      for (BlobStoreContext context : contexts.values()) {
         System.err.printf("creating container %s at %s%n", container, context.getProviderSpecificContext()
               .getEndpoint());
         context.getBlobStore().createContainerInLocation(null, container);
      }

      if (deleted) {
         System.err.println("sleeping 5 seconds to allow containers to create");
         Thread.sleep(5000);
      }

      for (Entry<String, BlobStoreContext> entry : contexts.entrySet()) {
         System.err.printf("filling container %s at %s%n", container, entry.getKey());
         controller.addMyTweets(entry.getKey(), statuses);
      }
   }

   private static String getRequiredSystemProperty(String key) {
       return checkNotNull(System.getProperty(key), key);
   }
   
   private void addConfigurationForTwitter(Properties props) {
       props.setProperty(PROPERTY_TWITTER_CONSUMER_KEY, 
               getRequiredSystemProperty("test." + PROPERTY_TWITTER_CONSUMER_KEY));
       props.setProperty(PROPERTY_TWITTER_CONSUMER_SECRET,
               getRequiredSystemProperty("test." + PROPERTY_TWITTER_CONSUMER_SECRET));
       props.setProperty(PROPERTY_TWITTER_ACCESSTOKEN, 
               getRequiredSystemProperty("test." + PROPERTY_TWITTER_ACCESSTOKEN));
       props.setProperty(PROPERTY_TWITTER_ACCESSTOKEN_SECRET,
               getRequiredSystemProperty("test." + PROPERTY_TWITTER_ACCESSTOKEN_SECRET));
    }

    private void addCredentialsForBlobStores(Properties props) {
       for (String provider : blobstores) {
          props.setProperty(provider + ".identity", 
                  getRequiredSystemProperty("test." + provider + ".identity"));
          props.setProperty(provider + ".credential",
                  getRequiredSystemProperty("test." + provider + ".credential"));
       }
    }
   
   @BeforeTest(dependsOnMethods = "clearAndCreateContainers")
   @Parameters({ "warfile", "bees.address", "bees.port", "bees.basedir" })
   public void startDevAppServer(final String warfile, final String address, final String port,
           String serverBaseDirectory) throws Exception {
      url = new URL(String.format("http://%s:%s", address, port));

      server = new RunAtCloudServer();
      server.writePropertiesAndStartServer(address, port, warfile, "itest", 
              serverBaseDirectory, props);
   }

   @Test
   public void shouldPass() throws InterruptedException, IOException {
      InputStream i = url.openStream();
      String string = Strings2.toStringAndClose(i);
      assert string.indexOf("Welcome") >= 0 : string;
   }

   @Test(dependsOnMethods = "shouldPass", expectedExceptions = IOException.class)
   public void shouldFail() throws InterruptedException, IOException {
      new URL(url, "/store/do").openStream();
   }

   @Test(dependsOnMethods = "shouldFail")
   public void testPrimeContainers() throws IOException, InterruptedException {
      URL gurl = new URL(url, "/store/do");

      for (String context : blobstores) {
         System.out.println("storing at context: " + context);
         HttpURLConnection connection = (HttpURLConnection) gurl.openConnection();
         connection.addRequestProperty(AUTHORIZED_REQUEST_ORIGINATOR_HEADER, "twitter");
         connection.addRequestProperty("context", context);
         InputStream i = connection.getInputStream();
         String string = Strings2.toStringAndClose(i);
         assert string.indexOf("Done!") >= 0 : string;
         connection.disconnect();
      }

      System.err.println("sleeping 20 seconds to allow for eventual consistency delay");
      Thread.sleep(20000);
      for (BlobStoreContext context : contexts.values()) {
         assert context.createInputStreamMap(container).size() > 0 : context.getProviderSpecificContext().getEndpoint();
      }
   }

   @Test(invocationCount = 5, dependsOnMethods = "testPrimeContainers")
   public void testSerial() throws InterruptedException, IOException {
      URL gurl = new URL(url, "/tweets/get");
      InputStream i = gurl.openStream();
      String string = Strings2.toStringAndClose(i);
      assert string.indexOf("Tweets in Clouds") >= 0 : string;
   }

   @Test(invocationCount = 10, dependsOnMethods = "testPrimeContainers", threadPoolSize = 3)
   public void testParallel() throws InterruptedException, IOException {
      URL gurl = new URL(url, "/tweets/get");
      InputStream i = gurl.openStream();
      String string = Strings2.toStringAndClose(i);
      assert string.indexOf("Tweets in Clouds") >= 0 : string;
   }
}
