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
package org.jclouds.sqs.parse;

import static com.google.common.io.BaseEncoding.base16;
import static org.testng.Assert.assertEquals;

import java.io.InputStream;

import org.jclouds.http.functions.BaseHandlerTest;
import org.jclouds.sqs.domain.Message;
import org.jclouds.sqs.xml.ReceiveMessageResponseHandler;
import org.testng.annotations.Test;

import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
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

      FluentIterable<Message> expected = expected();

      ReceiveMessageResponseHandler handler = injector.getInstance(ReceiveMessageResponseHandler.class);
      FluentIterable<Message> result = factory.create(handler).parse(is);

      assertEquals(result.toString(), expected.toString());

   }

   public FluentIterable<Message> expected() {
      return FluentIterable.from(ImmutableList.of(Message
            .builder()
            .id("5fea7756-0ea4-451a-a703-a558b933e274")
            .receiptHandle(
                  "+eXJYhj5rDr9cAe/9BuheT5fysi9BoqtEZSkO7IazVbNHg60eCCINxLqaSVv2pFHrWeWNpZwbleSkWRbCtZaQGgpOx/3cWJZiNSG1KKlJX4IOwISFvb3FwByMx4w0lnINeXzcw2VcKQXNrCatO9gdIiVPvJC3SCKatYM/7YTidtjqc8igrtYW2E2mHlCy3NXPCeXxP4tSvyEwIxpDAmMT7IF0mWvTHS6+JBUtFUsrmi61oIHlESNrD1OjdB1QQw+kdvJ6VbsntbJNNYKw+YqdqWNpZkiGQ8y1z9OdHsr1+4=")
            .md5(HashCodes.fromBytes(base16().lowerCase().decode("fafb00f5732ab283681e124bf8747ed1")))
            .body("This is a test message")
            .addAttribute("SenderId", "195004372649")
            .addAttribute("SentTimestamp", "1238099229000")
            .addAttribute("ApproximateReceiveCount", "5")
            .addAttribute("ApproximateFirstReceiveTimestamp", "1250700979248").build()));
   }
}
