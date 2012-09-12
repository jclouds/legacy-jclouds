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
package org.jclouds.sqs;

import static org.testng.Assert.assertEquals;

import java.net.URI;

import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.sqs.internal.BaseSQSApiExpectTest;
import org.jclouds.sqs.parse.ReceiveMessageResponseTest;
import org.jclouds.sqs.parse.SendMessageResponseTest;
import org.testng.annotations.Test;

import com.google.common.collect.Iterables;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "SQSApiExpectTest")
public class SQSApiExpectTest extends BaseSQSApiExpectTest {

   HttpRequest sendMessage = HttpRequest.builder()
         .method("POST")
         .endpoint("https://sqs.us-east-1.amazonaws.com/993194456877/adrian-sqs11/")
         .addHeader("Host", "sqs.us-east-1.amazonaws.com")
         .addFormParam("Action", "SendMessage")
         .addFormParam("MessageBody", "hardyharhar")
         .addFormParam("Signature", "PVzszzgIcT1xt9%2BEzGzWB2Bt8zDadBc48HsgF89AoJE%3D")
         .addFormParam("SignatureMethod", "HmacSHA256")
         .addFormParam("SignatureVersion", "2")
         .addFormParam("Timestamp", "2009-11-08T15%3A54%3A08.897Z")
         .addFormParam("Version", "2011-10-01")
         .addFormParam("AWSAccessKeyId", "identity").build();

   public void testSendMessageWhenResponseIs2xx() throws Exception {
      
      HttpResponse sendMessageResponse = HttpResponse.builder().statusCode(200)
            .payload(payloadFromResourceWithContentType("/send_message.xml", "text/xml")).build();

      SQSApi apiWhenExist = requestSendsResponse(sendMessage, sendMessageResponse);

      assertEquals(apiWhenExist.sendMessage(URI.create("https://sqs.us-east-1.amazonaws.com/993194456877/adrian-sqs11/"), "hardyharhar").toString(), new SendMessageResponseTest().expected().toString());
   }
   

   HttpRequest receiveMessage = HttpRequest.builder()
         .method("POST")
         .endpoint("https://sqs.us-east-1.amazonaws.com/993194456877/adrian-sqs11/")
         .addHeader("Host", "sqs.us-east-1.amazonaws.com")
         .addFormParam("Action", "ReceiveMessage")
         .addFormParam("Signature", "UURXsAjggoaz5P1h2EFswRd8Ji9euHmXhHvrAmIqM1E%3D")
         .addFormParam("SignatureMethod", "HmacSHA256")
         .addFormParam("SignatureVersion", "2")
         .addFormParam("Timestamp", "2009-11-08T15%3A54%3A08.897Z")
         .addFormParam("Version", "2011-10-01")
         .addFormParam("AWSAccessKeyId", "identity").build();

   public void testReceiveMessageWhenResponseIs2xx() throws Exception {
      
      HttpResponse receiveMessageResponse = HttpResponse.builder().statusCode(200)
            .payload(payloadFromResourceWithContentType("/messages.xml", "text/xml")).build();

      SQSApi apiWhenExist = requestSendsResponse(receiveMessage, receiveMessageResponse);

      assertEquals(
            apiWhenExist.receiveMessage(URI.create("https://sqs.us-east-1.amazonaws.com/993194456877/adrian-sqs11/"))
                  .toString(), Iterables.get(new ReceiveMessageResponseTest().expected(), 0).toString());
   }
   

   HttpRequest receiveMessages = HttpRequest.builder()
         .method("POST")
         .endpoint("https://sqs.us-east-1.amazonaws.com/993194456877/adrian-sqs11/")
         .addHeader("Host", "sqs.us-east-1.amazonaws.com")
         .addFormParam("Action", "ReceiveMessage")
         .addFormParam("MaxNumberOfMessages", "10")
         .addFormParam("Signature", "pZ9B4%2BTBvQA4n0joP4t8ue5x0xmKMd9prpVLVoT%2F7qU%3D")
         .addFormParam("SignatureMethod", "HmacSHA256")
         .addFormParam("SignatureVersion", "2")
         .addFormParam("Timestamp", "2009-11-08T15%3A54%3A08.897Z")
         .addFormParam("Version", "2011-10-01")
         .addFormParam("AWSAccessKeyId", "identity").build();

   public void testReceiveMessagesWhenResponseIs2xx() throws Exception {
      
      HttpResponse receiveMessagesResponse = HttpResponse.builder().statusCode(200)
            .payload(payloadFromResourceWithContentType("/messages.xml", "text/xml")).build();

      SQSApi apiWhenExist = requestSendsResponse(receiveMessages, receiveMessagesResponse);

      assertEquals(
            apiWhenExist.receiveMessages(URI.create("https://sqs.us-east-1.amazonaws.com/993194456877/adrian-sqs11/"), 10)
                  .toString(), new ReceiveMessageResponseTest().expected().toString());
   }
}
