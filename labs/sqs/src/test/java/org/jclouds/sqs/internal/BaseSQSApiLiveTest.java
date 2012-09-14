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
package org.jclouds.sqs.internal;

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import java.net.URI;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import org.jclouds.apis.BaseContextLiveTest;
import org.jclouds.rest.RestContext;
import org.jclouds.sqs.SQSApi;
import org.jclouds.sqs.SQSApiMetadata;
import org.jclouds.sqs.SQSAsyncApi;
import org.jclouds.sqs.domain.Message;
import org.testng.annotations.Test;

import com.google.common.reflect.TypeToken;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "live")
public class BaseSQSApiLiveTest extends BaseContextLiveTest<RestContext<SQSApi, SQSAsyncApi>> {

   public BaseSQSApiLiveTest() {
      provider = "sqs";
   }

   @Override
   protected TypeToken<RestContext<SQSApi, SQSAsyncApi>> contextType() {
      return SQSApiMetadata.CONTEXT_TOKEN;
   }

   protected String assertPolicyPresent(final URI queue) throws InterruptedException {
      final AtomicReference<String> policy = new AtomicReference<String>();
      assertEventually(new Runnable() {
         public void run() {
            String policyForAuthorizationByAccount = api().getQueueAttribute(queue, "Policy");

            assertNotNull(policyForAuthorizationByAccount);
            policy.set(policyForAuthorizationByAccount);
         }
      });
      return policy.get();
   }

   protected void assertNoPermissions(final URI queue) throws InterruptedException {
      assertEventually(new Runnable() {
         public void run() {
            String policy = api().getQueueAttribute(queue, "Policy");
            assertTrue(policy == null || policy.indexOf("\"Statement\":[]") != -1, policy);
         }
      });
   }

   protected void assertNoMessages(final URI queue) throws InterruptedException {
      assertEventually(new Runnable() {
         public void run() {
            Message message = api().receiveMessage(queue);
            assertNull(message, "message: " + message + " left in queue " + queue);
         }
      });
   }

   protected void assertQueueInList(final String region, URI queue) throws InterruptedException {
      final URI finalQ = queue;
      assertEventually(new Runnable() {
         public void run() {
            Set<URI> result = api().listQueuesInRegion(region);
            assertNotNull(result);
            assert result.size() >= 1 : result;
            assertTrue(result.contains(finalQ), finalQ + " not in " + result);
         }
      });
   }

   private SQSApi api() {
      return context.getApi();
   }

   private static final int INCONSISTENCY_WINDOW = 10000;

   /**
    * Due to eventual consistency, container commands may not return correctly
    * immediately. Hence, we will try up to the inconsistency window to see if
    * the assertion completes.
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
}
