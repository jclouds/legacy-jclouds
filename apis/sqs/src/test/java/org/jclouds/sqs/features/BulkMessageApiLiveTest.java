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
import java.util.Map.Entry;
import java.util.Set;

import org.jclouds.sqs.SQS;
import org.jclouds.sqs.domain.BatchResult;
import org.jclouds.sqs.domain.Message;
import org.jclouds.sqs.domain.MessageIdAndMD5;
import org.jclouds.sqs.internal.BaseSQSApiLiveTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.base.Charsets;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.collect.Iterables;
import com.google.common.hash.Hashing;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", singleThreaded = true, testName = "BulkMessageApiLiveTest")
public class BulkMessageApiLiveTest extends BaseSQSApiLiveTest {

   private ImmutableMap<String, String> idPayload;

   public BulkMessageApiLiveTest() {
      prefix = prefix + "-bulk";

      Builder<String, String> builder = ImmutableMap.<String, String> builder();
      for (int i = 0; i < 10; i++) {
         String message = "hardyharhar" + i;
         builder.put(i + "", message);
      }
      idPayload = builder.build();
   }

   @BeforeClass(groups = { "integration", "live" })
   @Override
   public void setup() {
      super.setup();
      recreateQueueInRegion(prefix, null);
   }

   public void testSendMessages() {
      for (URI queue : queues) {
         BatchResult<? extends MessageIdAndMD5> acks = api.getMessageApiForQueue(queue).send(idPayload);

         assertEquals(acks.size(), idPayload.size(), "error sending " + acks);
         assertEquals(acks.keySet(), idPayload.keySet());

         for (Entry<String, ? extends MessageIdAndMD5> entry : acks.entrySet()) {
            assertEquals(entry.getValue().getMD5(),
                  Hashing.md5().hashString(idPayload.get(entry.getKey()), Charsets.UTF_8), "bad md5 for: " + entry);
         }
      }
   }

   private Iterable<String> receiptHandles;

   @Test(dependsOnMethods = "testSendMessages")
   public void testChangeMessageVisibility() {
      for (URI queue : queues) {
         MessageApi messageApi = api.getMessageApiForQueue(queue);
         
         Set<Message> messages = collectMessages(messageApi);

         receiptHandles = Iterables.transform(messages, new Function<Message, String>() {
            @Override
            public String apply(Message in) {
               return in.getReceiptHandle();
            }
         });

         // hidden message, so we can't see it
         assertNull(messageApi.receive());

         // this should unhide it
         BatchResult<String> acks = messageApi.changeVisibility(receiptHandles, 0);
         assertEquals(acks.size(), messages.size(), "error changing visibility " + acks);

         // so we can see it again
         assertEquals(collectMessages(messageApi).size(), messages.size());
      }
   }

   protected Set<Message> collectMessages(MessageApi api) {
      return SQS.receiveAllAtRate(api, idPayload.size(), attribute("None").visibilityTimeout(5)).toSet();
   }

   @Test(dependsOnMethods = "testChangeMessageVisibility")
   public void testDeleteMessage() throws InterruptedException {
      for (URI queue : queues) {
         BatchResult<String> acks = api.getMessageApiForQueue(queue).delete(receiptHandles);
         assertEquals(acks.size(), Iterables.size(receiptHandles), "error deleting messages " + acks);
         assertNoMessages(queue);
      }
   }

}
