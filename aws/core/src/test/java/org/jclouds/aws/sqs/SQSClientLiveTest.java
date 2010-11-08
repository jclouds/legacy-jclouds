/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
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

package org.jclouds.aws.sqs;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.aws.sqs.options.ListQueuesOptions.Builder.queuePrefix;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.io.IOException;
import java.util.Properties;
import java.util.Set;
import java.util.SortedSet;

import org.jclouds.Constants;
import org.jclouds.aws.AWSResponseException;
import org.jclouds.aws.domain.Region;
import org.jclouds.aws.sqs.domain.Queue;
import org.jclouds.crypto.CryptoStreams;
import org.jclouds.logging.log4j.config.Log4JLoggingModule;
import org.jclouds.rest.RestContext;
import org.jclouds.rest.RestContextFactory;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.inject.Module;

/**
 * Tests behavior of {@code SQSClient}
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", sequential = true, testName = "sqs.SQSClientLiveTest")
public class SQSClientLiveTest {

   private SQSClient client;

   private RestContext<SQSClient, SQSAsyncClient> context;

   private Set<Queue> queues = Sets.newHashSet();
   protected String provider = "sqs";
   protected String identity;
   protected String credential;
   protected String endpoint;
   protected String apiversion;

   protected void setupCredentials() {
      identity = checkNotNull(System.getProperty("test." + provider + ".identity"), "test." + provider + ".identity");
      credential = checkNotNull(System.getProperty("test." + provider + ".credential"), "test." + provider
            + ".credential");
      endpoint = System.getProperty("test." + provider + ".endpoint");
      apiversion = System.getProperty("test." + provider + ".apiversion");
   }

   protected Properties setupProperties() {
      Properties overrides = new Properties();
      overrides.setProperty(Constants.PROPERTY_TRUST_ALL_CERTS, "true");
      overrides.setProperty(Constants.PROPERTY_RELAX_HOSTNAME, "true");
      overrides.setProperty(provider + ".identity", identity);
      if (credential != null)
         overrides.setProperty(provider + ".credential", credential);
      if (endpoint != null)
         overrides.setProperty(provider + ".endpoint", endpoint);
      if (apiversion != null)
         overrides.setProperty(provider + ".apiversion", apiversion);
      return overrides;
   }

   @BeforeGroups(groups = { "live" })
   public void setupClient() {
      setupCredentials();
      Properties overrides = setupProperties();
      context = new RestContextFactory().createContext(provider, ImmutableSet.<Module> of(new Log4JLoggingModule()),
            overrides);
      this.client = context.getApi();
   }

   @Test
   void testListQueuesInRegion() throws InterruptedException {
      for (String region : Lists.newArrayList(null, Region.EU_WEST_1, Region.US_EAST_1, Region.US_WEST_1,
            Region.AP_SOUTHEAST_1)) {
         SortedSet<Queue> allResults = Sets.newTreeSet(client.listQueuesInRegion(region));
         assertNotNull(allResults);
         if (allResults.size() >= 1) {
            Queue queue = allResults.last();
            assertQueueInList(region, queue);
         }
      }
   }

   public static final String PREFIX = System.getProperty("user.name") + "-sqs";

   @Test
   void testCreateQueue() throws InterruptedException {
      String queueName = PREFIX + "1";

      for (final String region : Lists.newArrayList(null, Region.EU_WEST_1, Region.US_EAST_1, Region.US_WEST_1,
            Region.AP_SOUTHEAST_1)) {
         try {
            SortedSet<Queue> result = Sets.newTreeSet(client.listQueuesInRegion(region, queuePrefix(queueName)));
            if (result.size() >= 1) {
               client.deleteQueue(result.last());
               queueName += 1;// cannot recreate a queue within 60 seconds
            }
         } catch (Exception e) {

         }
         Queue queue = null;
         int tries = 0;
         while (queue == null && tries < 5) {
            try {
               tries++;
               queue = client.createQueueInRegion(region, queueName);
            } catch (AWSResponseException e) {
               queueName += "1";
               if (e.getError().getCode().equals("AWS.SimpleQueueService.QueueDeletedRecently"))// TODO
                  // retry
                  // handler
                  continue;
               throw e;
            }
         }
         if (region != null)
            assertEquals(queue.getRegion(), region);
         assertEquals(queue.getName(), queueName);
         assertQueueInList(region, queue);
         queues.add(queue);
      }
   }

   @Test(dependsOnMethods = "testCreateQueue")
   void testSendMessage() throws InterruptedException, IOException {
      String message = "hardyharhar";
      byte[] md5 = CryptoStreams.md5(message.getBytes());
      for (Queue queue : queues) {
         assertEquals(client.sendMessage(queue, message), md5);
      }
   }

   private void assertQueueInList(final String region, Queue queue) throws InterruptedException {
      final Queue finalQ = queue;
      assertEventually(new Runnable() {
         public void run() {
            SortedSet<Queue> result = Sets.newTreeSet(client.listQueuesInRegion(region, queuePrefix(finalQ.getName())));
            assertNotNull(result);
            assert result.size() >= 1 : result;
            assertEquals(result.first(), finalQ);
         }
      });
   }

   private static final int INCONSISTENCY_WINDOW = 10000;

   /**
    * Due to eventual consistency, container commands may not return correctly immediately. Hence,
    * we will try up to the inconsistency window to see if the assertion completes.
    */
   protected static void assertEventually(Runnable assertion) throws InterruptedException {
      long start = System.currentTimeMillis();
      AssertionError error = null;
      for (int i = 0; i < 30; i++) {
         try {
            assertion.run();
            if (i > 0)
               System.err.printf("%d attempts and %dms asserting %s%n", i + 1, System.currentTimeMillis() - start,
                     assertion.getClass().getSimpleName());
            return;
         } catch (AssertionError e) {
            error = e;
         }
         Thread.sleep(INCONSISTENCY_WINDOW / 30);
      }
      if (error != null)
         throw error;
   }

   @AfterTest
   public void shutdown() {
      context.close();
   }
}
