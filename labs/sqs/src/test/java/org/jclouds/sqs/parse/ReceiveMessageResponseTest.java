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
package org.jclouds.sqs.parse;

import static org.testng.Assert.assertEquals;

import java.io.InputStream;
import java.util.Set;

import org.jclouds.crypto.CryptoStreams;
import org.jclouds.http.functions.BaseHandlerTest;
import org.jclouds.sqs.domain.Message;
import org.jclouds.sqs.xml.ReceiveMessageResponseHandler;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;
import com.google.common.hash.HashCodes;

/**
 * @author Adrian Cole
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during
// surefire
@Test(groups = "unit", testName = "ReceiveMessageResponseTest")
public class ReceiveMessageResponseTest extends BaseHandlerTest {

   public void test() {
      InputStream is = getClass().getResourceAsStream("/messages.xml");

      Set<Message> expected = expected();

      ReceiveMessageResponseHandler handler = injector.getInstance(ReceiveMessageResponseHandler.class);
      Set<Message> result = factory.create(handler).parse(is);

      assertEquals(result.toString(), expected.toString());

   }

   public Set<Message> expected() {
      return ImmutableSet.of(Message
            .builder()
            .id("5fea7756-0ea4-451a-a703-a558b933e274")
            .receiptHandle(
                  "MbZj6wDWli+JvwwJaBV+3dcjk2YW2vA3+STFFljTM8tJJg6HRG6PYSasuWXPJB+Cw" + "\n"
                        + "        Lj1FjgXUv1uSj1gUPAWV66FU/WeR4mq2OKpEGYWbnLmpRCJVAyeMjeU5ZBdtcQ+QE" + "\n"
                        + "        auMZc8ZRv37sIW2iJKq3M9MFx1YvV11A2x/KSbkJ0=")
            .md5(HashCodes.fromBytes(CryptoStreams.hex("fafb00f5732ab283681e124bf8747ed1")))
            .body("This is a test message")
            .addAttribute("SenderId", "195004372649")
            .addAttribute("SentTimestamp", "1238099229000")
            .addAttribute("ApproximateReceiveCount", "5")
            .addAttribute("ApproximateFirstReceiveTimestamp", "1250700979248").build());
   }
}
