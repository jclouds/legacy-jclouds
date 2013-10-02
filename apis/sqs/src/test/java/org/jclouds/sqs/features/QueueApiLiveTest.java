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
package org.jclouds.sqs.features;

import static com.google.common.collect.Iterables.getLast;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;

import java.net.URI;
import java.util.Map;
import java.util.UUID;

import org.jclouds.sqs.internal.BaseSQSApiLiveTest;
import org.testng.annotations.Test;

import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableSet;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", singleThreaded = true, testName = "QueueApiLiveTest")
public class QueueApiLiveTest extends BaseSQSApiLiveTest {

   public QueueApiLiveTest() {
      prefix = prefix + "-queue";
   }

   @Test
   public void testListQueues() throws InterruptedException {
      listQueuesInRegion(null);
   }

   protected void listQueuesInRegion(String region) throws InterruptedException {
      FluentIterable<URI> allResults = api.getQueueApiForRegion(region).list();
      assertNotNull(allResults);
      if (allResults.size() >= 1) {
         URI queue = getLast(allResults);
         assertQueueInList(region, queue);
      }
   }
   
   @Test
   public void testGracefulNoQueue() throws InterruptedException {
      assertNull(api.getQueueApi().get(UUID.randomUUID().toString()));
   }
   
   @Test
   public void testCanRecreateQueueGracefully() throws InterruptedException {
      recreateQueueInRegion(prefix, null);
      recreateQueueInRegion(prefix, null);
   }

   @Test(dependsOnMethods = "testCanRecreateQueueGracefully")
   public void testCreateQueueWhenAlreadyExistsReturnsURI() {
      for (URI queue : queues) {
         assertEquals(api.getQueueApi().create(prefix), queue);
      }
   }
   
   @Test(dependsOnMethods = "testCanRecreateQueueGracefully")
   public void testGet() {
      for (URI queue : queues) {
         assertEquals(api.getQueueApi().get(prefix), queue);
      }
   }

   @Test(dependsOnMethods = "testCanRecreateQueueGracefully")
   public void testGetInAccount() {
      for (URI queue : queues) {
         assertEquals(api.getQueueApi().getInAccount(prefix, getOwner(queue)), queue);
      }
   }
   
   @Test(dependsOnMethods = "testCanRecreateQueueGracefully")
   public void testGetQueueAttributes() {
      for (URI queue : queues) {
         Map<String, String> attributes = api.getQueueApi().getAttributes(queue, ImmutableSet.of("All"));
         assertEquals(api.getQueueApi().getAttributes(queue, attributes.keySet()), attributes);
      }
   }

   @Test(dependsOnMethods = "testGetQueueAttributes")
   public void testSetQueueAttribute() {
      for (URI queue : queues) {
         api.getQueueApi().setAttribute(queue, "MaximumMessageSize", "1024");
         assertEquals(api.getQueueApi().getAttributes(queue).getMaximumMessageSize(), 1024);
      }
   }
}
