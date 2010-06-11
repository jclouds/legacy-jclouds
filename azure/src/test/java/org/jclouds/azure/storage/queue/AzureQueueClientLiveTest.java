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
package org.jclouds.azure.storage.queue;

import static org.jclouds.azure.storage.options.ListOptions.Builder.prefix;
import static org.jclouds.azure.storage.queue.options.GetOptions.Builder.maxMessages;
import static org.jclouds.azure.storage.queue.options.PutMessageOptions.Builder.withTTL;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.security.SecureRandom;
import java.util.Set;

import org.jclouds.azure.storage.domain.BoundedSet;
import org.jclouds.azure.storage.options.CreateOptions;
import org.jclouds.azure.storage.queue.domain.QueueMessage;
import org.jclouds.azure.storage.queue.domain.QueueMetadata;
import org.jclouds.http.HttpResponseException;
import org.jclouds.logging.log4j.config.Log4JLoggingModule;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Iterables;
import com.google.inject.Injector;

/**
 * Tests behavior of {@code AzureQueueClient}
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", sequential = true, testName = "azurequeue.AzureQueueClientLiveTest")
public class AzureQueueClientLiveTest {
   String account;

   protected AzureQueueClient connection;

   private String queuePrefix = System.getProperty("user.name") + "-azurequeue";

   @BeforeGroups(groups = { "live" })
   public void setupClient() {
      account = System.getProperty("jclouds.test.user");
      String key = System.getProperty("jclouds.test.key");
      Injector injector = new AzureQueueContextBuilder("azurequeue",
               new AzureQueuePropertiesBuilder(account, key).build()).withModules(
               new Log4JLoggingModule()).buildInjector();
      connection = injector.getInstance(AzureQueueClient.class);
   }

   @Test
   public void testListQueues() throws Exception {

      BoundedSet<QueueMetadata> response = connection.listQueues();
      assert null != response;
      long initialQueueCount = response.size();
      assertTrue(initialQueueCount >= 0);

   }

   String privateQueue;

   @Test(timeOut = 5 * 60 * 1000)
   public void testCreateQueue() throws Exception {
      boolean created = false;
      while (!created) {
         privateQueue = queuePrefix + new SecureRandom().nextInt();
         try {
            created = connection.createQueue(privateQueue, CreateOptions.Builder
                     .withMetadata(ImmutableMultimap.of("foo", "bar")));
         } catch (HttpResponseException htpe) {
            if (htpe.getResponse().getStatusCode() == 409) {
               continue;
            } else {
               throw htpe;
            }
         }
      }
      BoundedSet<QueueMetadata> response = connection.listQueues();
      assert null != response;
      long queueCount = response.size();
      assertTrue(queueCount >= 1);
      // TODO ... check to see the queue actually exists
   }

   @Test(timeOut = 5 * 60 * 1000, dependsOnMethods = { "testCreateQueue" })
   public void testListQueuesWithOptions() throws Exception {
      BoundedSet<QueueMetadata> response = connection
               .listQueues(prefix(privateQueue).maxResults(1));
      assert null != response;
      long initialQueueCount = response.size();
      assertTrue(initialQueueCount >= 0);
      assertEquals(privateQueue, response.getPrefix());
      assertEquals(1, response.getMaxResults());
   }

   @Test(timeOut = 5 * 60 * 1000, dependsOnMethods = { "testCreateQueue" })
   public void testPutMessage() throws Exception {
      connection.putMessage(privateQueue, "holycow", withTTL(4));
      connection.putMessage(privateQueue, "holymoo", withTTL(4));
   }

   @Test(timeOut = 5 * 60 * 1000, dependsOnMethods = { "testPutMessage" })
   public void testGetMessages() throws Exception {
      Set<QueueMessage> messages = connection.getMessages(privateQueue, maxMessages(2));
      QueueMessage m1 = Iterables.get(messages, 0);
      assertEquals(m1.getMessageText(), "holycow");
      QueueMessage m2 = Iterables.get(messages, 1);
      assertEquals(m2.getMessageText(), "holymoo");
      assertEquals(connection.getMessages(privateQueue).size(), 0);
   }

   @Test(timeOut = 5 * 60 * 1000, dependsOnMethods = { "testGetMessages" })
   public void testDeleteQueue() throws Exception {
      connection.clearMessages(privateQueue);
      connection.deleteQueue(privateQueue);
      // TODO loop for up to 30 seconds checking if they are really gone
   }

}
