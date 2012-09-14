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

import org.jclouds.crypto.CryptoStreams;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.functions.ReturnStringIf2xx;
import org.jclouds.sqs.domain.MessageIdAndMD5;
import org.jclouds.sqs.xml.RegexMessageIdAndMD5Handler;
import org.testng.annotations.Test;

import com.google.common.hash.HashCodes;

/**
 * @author Adrian Cole
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during
// surefire
@Test(groups = "unit", testName = "SendMessageResponseTest")
public class SendMessageResponseTest {

   public void test() {
      InputStream is = getClass().getResourceAsStream("/send_message.xml");

      MessageIdAndMD5 expected = expected();

      RegexMessageIdAndMD5Handler handler = new RegexMessageIdAndMD5Handler(new ReturnStringIf2xx());
      MessageIdAndMD5 result = handler.apply(HttpResponse.builder().statusCode(200).payload(is).build());

      assertEquals(result.toString(), expected.toString());

   }

   public MessageIdAndMD5 expected() {
      return MessageIdAndMD5.builder().id("c332b2b0-b61f-42d3-8832-d03ebd89f68d")
            .md5(HashCodes.fromBytes(CryptoStreams.hex("e32aedf2b2b25355d04b1507055532e6"))).build();
   }
}
