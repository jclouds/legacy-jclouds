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
import org.jclouds.sqs.domain.BatchResult;
import org.jclouds.sqs.domain.MessageIdAndMD5;
import org.jclouds.sqs.xml.SendMessageBatchResponseHandler;
import org.testng.annotations.Test;

import com.google.common.hash.HashCodes;

/**
 * @author Adrian Cole
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during
// surefire
@Test(groups = "unit", testName = "SendMessageBatchResponseTest")
public class SendMessageBatchResponseTest extends BaseHandlerTest {

   public void test() {
      InputStream is = getClass().getResourceAsStream("/send_message_batch.xml");

      BatchResult<MessageIdAndMD5> expected = expected();

      SendMessageBatchResponseHandler handler = injector.getInstance(SendMessageBatchResponseHandler.class);
      BatchResult<MessageIdAndMD5> result = factory.create(handler).parse(is);

      assertEquals(result.toString(), expected.toString());

   }

   public BatchResult<MessageIdAndMD5> expected() {
      return BatchResult
            .<MessageIdAndMD5> builder()
            .put("test_msg_001",
                  MessageIdAndMD5.builder().id("0a5231c7-8bff-4955-be2e-8dc7c50a25fa")
                        .md5(HashCodes.fromBytes(base16().lowerCase().decode("0e024d309850c78cba5eabbeff7cae71"))).build())
            .put("test_msg_002",
                  MessageIdAndMD5.builder().id("15ee1ed3-87e7-40c1-bdaa-2e49968ea7e9")
                        .md5(HashCodes.fromBytes(base16().lowerCase().decode("7fb8146a82f95e0af155278f406862c2"))).build())
            .build();
   }
}
