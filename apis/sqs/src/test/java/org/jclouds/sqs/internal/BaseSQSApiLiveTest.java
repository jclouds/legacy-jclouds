/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.sqs.internal;

import static com.google.common.collect.Iterables.get;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import java.net.URI;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import org.jclouds.apis.BaseApiLiveTest;
import org.jclouds.sqs.SQSApi;
import org.jclouds.sqs.domain.Message;
import org.jclouds.sqs.features.QueueApi;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

import com.google.common.base.Splitter;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Sets;
import com.google.common.util.concurrent.Atomics;
import com.google.common.util.concurrent.Uninterruptibles;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "live")
public class BaseSQSApiLiveTest extends BaseApiLiveTest<SQSApi> {

   protected String prefix = System.getProperty("user.name") + "-sqs";

   public BaseSQSApiLiveTest() {
      provider = "sqs";
   }

   protected Set<URI> queues = Sets.newHashSet();

   protected String getOwner(URI queue) {
      return get(Splitter.on('/').split(queue.getPath()), 1);
   }

   protected String recreateQueueInRegion(String queueName, String region) {
      QueueApi queueApi = api.getQueueApiForRegion(region);
      URI result = queueApi.get(queueName);
      if (result != null) {
         queueApi.delete(result);
      }
      URI queue = queueApi.create(queueName);
      assertQueueInList(region, queue);
      queues.add(queue);
      return queueName;
   }

   protected String assertPolicyPresent(final URI queue) {
      final AtomicReference<String> policy = Atomics.newReference();
      assertEventually(new Runnable() {
         public void run() {
            String policyForAuthorizationByAccount = api.getQueueApi().getAttribute(queue, "Policy");

            assertNotNull(policyForAuthorizationByAccount);
            policy.set(policyForAuthorizationByAccount);
         }
      });
      return policy.get();
   }

   protected void assertNoPermissions(final URI queue) {
      assertEventually(new Runnable() {
         public void run() {
            String policy = api.getQueueApi().getAttribute(queue, "Policy");
            assertTrue(policy == null || policy.indexOf("\"Statement\":[]") != -1, policy);
         }
      });
   }

   protected void assertNoMessages(final URI queue) {
      assertEventually(new Runnable() {
         public void run() {
            Message message = api.getMessageApiForQueue(queue).receive();
            assertNull(message, "message: " + message + " left in queue " + queue);
         }
      });
   }

   protected void assertQueueInList(final String region, URI queue) {
      final URI finalQ = queue;
      assertEventually(new Runnable() {
         public void run() {
            FluentIterable<URI> result = api.getQueueApiForRegion(region).list();
            assertNotNull(result);
            assert result.size() >= 1 : result;
            assertTrue(result.contains(finalQ), finalQ + " not in " + result);
         }
      });
   }

   private static final int INCONSISTENCY_WINDOW = 10000;

   /**
    * Due to eventual consistency, container commands may not return correctly
    * immediately. Hence, we will try up to the inconsistency window to see if
    * the assertion completes.
    */
   protected static void assertEventually(Runnable assertion) {
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
         Uninterruptibles.sleepUninterruptibly(INCONSISTENCY_WINDOW / 30, TimeUnit.MILLISECONDS);
      }
      if (error != null)
         throw error;
   }

   @Override
   @AfterClass(groups = "live")
   protected void tearDown() {
      for (URI queue : queues) {
         api.getQueueApi().delete(queue);
      }
      super.tearDown();
   }
}
