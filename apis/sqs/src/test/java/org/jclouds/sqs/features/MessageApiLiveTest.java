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

import static org.jclouds.sqs.options.ReceiveMessageOptions.Builder.attribute;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

import java.net.URI;

import org.jclouds.sqs.internal.BaseSQSApiLiveTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.base.Charsets;
import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", singleThreaded = true, testName = "MessageApiLiveTest")
public class MessageApiLiveTest extends BaseSQSApiLiveTest {

   public MessageApiLiveTest() {
      prefix = prefix + "-message";
   }

   @BeforeClass(groups = { "integration", "live" })
   @Override
   public void setup() {
      super.setup();
      recreateQueueInRegion(prefix, null);
   }

   String message = "hardyharhar";
   HashCode md5 = Hashing.md5().hashString(message, Charsets.UTF_8);

   public void testSendMessage() {
      for (URI queue : queues) {
         assertEquals(api(queue).send(message).getMD5(), md5);
      }
   }

   @Test(dependsOnMethods = "testSendMessage")
   public void testReceiveMessageWithoutHidingMessage() {
      for (URI queue : queues) {
         assertEquals(api(queue).receive(attribute("All").visibilityTimeout(0)).getMD5(), md5);
      }
   }

   String receiptHandle;

   @Test(dependsOnMethods = "testReceiveMessageWithoutHidingMessage")
   public void testChangeMessageVisibility() {
      for (URI queue : queues) {
         MessageApi messageApi = api(queue);
         // start hiding it at 5 seconds
         receiptHandle = messageApi.receive(attribute("None").visibilityTimeout(5)).getReceiptHandle();
         // hidden message, so we can't see it
         assertNull(messageApi.receive());
         // this should unhide it
         messageApi.changeVisibility(receiptHandle, 0);
         // so we can see it again
         assertEquals(messageApi.receive(attribute("All").visibilityTimeout(0)).getMD5(), md5);
      }
   }

   @Test(dependsOnMethods = "testChangeMessageVisibility")
   public void testDeleteMessage() throws InterruptedException {
      for (URI queue : queues) {
         api(queue).delete(receiptHandle);
         assertNoMessages(queue);
      }
   }

   private MessageApi api(URI queue) {
      return api.getMessageApiForQueue(queue);
   }
}
