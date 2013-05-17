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
package org.jclouds.sqs;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;

import org.easymock.EasyMock;
import org.jclouds.sqs.domain.Message;
import org.jclouds.sqs.features.MessageApi;
import org.jclouds.sqs.options.ReceiveMessageOptions;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

/**
 * Tests behavior of {@code SQS}.
 *
 * @author Adrian Cole
 */
@Test(testName = "SQSTest", singleThreaded = true)
public class SQSTest {

   /**
    * Tests {@link SQS#receiveAllAtRate} where a single response returns all
    * results.
    */
   @Test
   public void testSinglePageResult() throws Exception {
      MessageApi messageClient = createMock(MessageApi.class);
      ReceiveMessageOptions options = new ReceiveMessageOptions();
      FluentIterable<Message> aMessage = FluentIterable.from(ImmutableSet.of(createMock(Message.class)));
      FluentIterable<Message> noMessages = FluentIterable.from(ImmutableSet.<Message>of());

      expect(messageClient.receive(1, options))
            .andReturn(aMessage)
            .once();
      
      expect(messageClient.receive(1, options))
            .andReturn(noMessages)
            .once();
            
      EasyMock.replay(messageClient);

      Assert.assertEquals(1, Iterables.size(SQS.receiveAllAtRate(messageClient, 1, options)));
   }
   
   /**
    * Tests {@link SQS#receiveAllAtRate} where retrieving all results requires multiple requests.
    */
   @Test
   public void testMultiPageResult() throws Exception {
      MessageApi messageClient = createMock(MessageApi.class);
      ReceiveMessageOptions options = new ReceiveMessageOptions();
      FluentIterable<Message> aMessage = FluentIterable.from(ImmutableSet.of(createMock(Message.class)));
      FluentIterable<Message> noMessages = FluentIterable.from(ImmutableSet.<Message>of());
      
      expect(messageClient.receive(1, options))
            .andReturn(aMessage)
            .times(2);
      expect(messageClient.receive(1, options))
            .andReturn(noMessages)
            .once();

      EasyMock.replay(messageClient);

      Assert.assertEquals(2, Iterables.size(SQS.receiveAllAtRate(messageClient, 1, options)));
   }
}
