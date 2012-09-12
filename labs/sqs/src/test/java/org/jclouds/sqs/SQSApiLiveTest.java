/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jclouds.sqs;

import static org.jclouds.sqs.options.ListQueuesOptions.Builder.queuePrefix;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.net.URI;
import java.util.Set;
import java.util.SortedSet;

import org.jclouds.aws.AWSResponseException;
import org.jclouds.sqs.internal.BaseSQSApiLiveTest;
import org.jclouds.sqs.options.ReceiveMessageOptions;
import org.testng.annotations.AfterTest;
import org.testng.annotations.Test;

import com.google.common.base.Charsets;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;

/**
 * Tests behavior of {@code SQSApi}
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", singleThreaded = true, testName = "SQSApiLiveTest")
public class SQSApiLiveTest extends BaseSQSApiLiveTest {

   private Set<URI> queues = Sets.newHashSet();

   @Test
   protected void testListQueues() throws InterruptedException {
      listQueuesInRegion(null);
   }

   protected void listQueuesInRegion(String region) throws InterruptedException {
      SortedSet<URI> allResults = Sets.newTreeSet(context.getApi().listQueuesInRegion(region));
      assertNotNull(allResults);
      if (allResults.size() >= 1) {
         URI queue = allResults.last();
         assertQueueInList(region, queue);
      }
   }

   public static final String PREFIX = System.getProperty("user.name") + "-sqs";

   @Test
   protected void testCreateQueue() throws InterruptedException {
      createQueueInRegion(null, PREFIX + "1");
   }

   public String createQueueInRegion(final String region, String queueName) throws InterruptedException {
      try {
         Set<URI> result = context.getApi().listQueuesInRegion(region, queuePrefix(queueName));
         if (result.size() >= 1) {
            context.getApi().deleteQueue(Iterables.getLast(result));
            queueName += 1;// cannot recreate a queue within 60 seconds
         }
      } catch (Exception e) {

      }
      URI queue = null;
      int tries = 0;
      while (queue == null && tries < 5) {
         try {
            tries++;
            queue = context.getApi().createQueueInRegion(region, queueName);
         } catch (AWSResponseException e) {
            queueName += "1";
            if (e.getError().getCode().equals("AWS.SimpleQueueService.QueueDeletedRecently"))// TODO
               // retry
               // handler
               continue;
            throw e;
         }
      }
      assertQueueInList(region, queue);
      queues.add(queue);
      return queueName;
   }

   String message = "hardyharhar";
   HashCode md5 = Hashing.md5().hashString(message, Charsets.UTF_8);

   @Test(dependsOnMethods = "testCreateQueue")
   protected void testSendMessage() {
      for (URI queue : queues) {
         assertEquals(context.getApi().sendMessage(queue, message).getMD5(), md5);
      }
   }

   @Test(dependsOnMethods = "testSendMessage")
   protected void testReceiveMessage() {
      for (URI queue : queues) {
         assertEquals(context.getApi().receiveMessage(queue, ReceiveMessageOptions.Builder.attribute("All")).getMD5(),
               md5);
      }
   }

   private void assertQueueInList(final String region, URI queue) throws InterruptedException {
      final URI finalQ = queue;
      assertEventually(new Runnable() {
         public void run() {
            Set<URI> result = context.getApi().listQueuesInRegion(region);
            assertNotNull(result);
            assert result.size() >= 1 : result;
            assertTrue(result.contains(finalQ), finalQ + " not in " + result);
         }
      });
   }

   @AfterTest
   public void shutdown() {
      context.close();
   }
}
