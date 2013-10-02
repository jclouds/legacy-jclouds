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

import static org.testng.Assert.assertEquals;

import java.io.InputStream;
import java.util.Map;

import org.jclouds.http.functions.BaseHandlerTest;
import org.jclouds.sqs.xml.AttributesHandler;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;

/**
 * @author Adrian Cole
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during
// surefire
@Test(groups = "unit", testName = "GetQueueAttributesResponseTest")
public class GetQueueAttributesResponseTest extends BaseHandlerTest {

   public void test() {
      InputStream is = getClass().getResourceAsStream("/attributes.xml");

      Map<String, String> expected = expected();

      AttributesHandler handler = injector.getInstance(AttributesHandler.class);
      Map<String, String> result = factory.create(handler).parse(is);

      assertEquals(result.toString(), expected.toString());

   }

   public Map<String, String> expected() {
      return ImmutableMap.<String, String>builder()
            .put("QueueArn", "arn:aws:sqs:us-east-1:993194456877:adrian-sqs1")
            .put("ApproximateNumberOfMessages", "0")
            .put("ApproximateNumberOfMessagesNotVisible", "0")
            .put("ApproximateNumberOfMessagesDelayed", "0")
            .put("CreatedTimestamp", "1347566436")
            .put("LastModifiedTimestamp", "1347566436")
            .put("VisibilityTimeout","30")
            .put("MaximumMessageSize", "65536")
            .put("MessageRetentionPeriod", "345600")
            .put("DelaySeconds", "0")
            .build();
   }
}
