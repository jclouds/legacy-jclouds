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

import static org.testng.Assert.assertEquals;

import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.sqs.SQSApi;
import org.jclouds.sqs.internal.BaseSQSApiExpectTest;
import org.jclouds.sqs.parse.ChangeMessageVisibilityBatchResponseTest;
import org.jclouds.sqs.parse.DeleteMessageBatchResponseTest;
import org.jclouds.sqs.parse.ReceiveMessageResponseTest;
import org.jclouds.sqs.parse.SendMessageBatchResponseTest;
import org.jclouds.sqs.parse.SendMessageResponseTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableTable;
import com.google.common.collect.Iterables;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "MessageApiExpectTest")
public class MessageApiExpectTest extends BaseSQSApiExpectTest {
   
   public HttpRequest sendMessage = HttpRequest.builder()
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

      assertEquals(apiWhenExist.getMessageApiForQueue(queue).send("hardyharhar").toString(),
            new SendMessageResponseTest().expected().toString());
   }
   
   public HttpRequest sendMessageIterable = HttpRequest.builder()
         .method("POST")
         .endpoint("https://sqs.us-east-1.amazonaws.com/993194456877/adrian-sqs11/")
         .addHeader("Host", "sqs.us-east-1.amazonaws.com")
         .addFormParam("Action", "SendMessageBatch")
         .addFormParam("SendMessageBatchRequestEntry.1.Id", "1")
         .addFormParam("SendMessageBatchRequestEntry.1.MessageBody", "payload1")
         .addFormParam("SendMessageBatchRequestEntry.2.Id", "2")
         .addFormParam("SendMessageBatchRequestEntry.2.MessageBody", "payload2")
         .addFormParam("Signature", "2AYMDMLhoLncALJgBfHBGfOkaTB5ut3PeFRJeWffxdI%3D")
         .addFormParam("SignatureMethod", "HmacSHA256")
         .addFormParam("SignatureVersion", "2")
         .addFormParam("Timestamp", "2009-11-08T15%3A54%3A08.897Z")
         .addFormParam("Version", "2011-10-01")
         .addFormParam("AWSAccessKeyId", "identity").build();

   public void testSendMessageIterableWhenResponseIs2xx() throws Exception {

      HttpResponse sendMessageResponse = HttpResponse.builder()
            .statusCode(200)
            .payload(payloadFromResourceWithContentType("/send_message_batch.xml", "text/xml")).build();

      SQSApi apiWhenExist = requestSendsResponse(sendMessageIterable, sendMessageResponse);

      assertEquals(apiWhenExist.getMessageApiForQueue(queue).send(ImmutableSet.of("payload1", "payload2"))
            .toString(), new SendMessageBatchResponseTest().expected().toString());
   }

   public HttpRequest sendMessageMap = HttpRequest.builder()
         .method("POST")
         .endpoint("https://sqs.us-east-1.amazonaws.com/993194456877/adrian-sqs11/")
         .addHeader("Host", "sqs.us-east-1.amazonaws.com")
         .addFormParam("Action", "SendMessageBatch")
         .addFormParam("SendMessageBatchRequestEntry.1.Id", "foo1")
         .addFormParam("SendMessageBatchRequestEntry.1.MessageBody", "payload1")
         .addFormParam("SendMessageBatchRequestEntry.2.Id", "foo2")
         .addFormParam("SendMessageBatchRequestEntry.2.MessageBody", "payload2")
         .addFormParam("Signature", "f9v8e/rPXTI3zhBYMhg7U8yCfvPqHjAV8bFjhGL6%2BXc%3D")
         .addFormParam("SignatureMethod", "HmacSHA256")
         .addFormParam("SignatureVersion", "2")
         .addFormParam("Timestamp", "2009-11-08T15%3A54%3A08.897Z")
         .addFormParam("Version", "2011-10-01")
         .addFormParam("AWSAccessKeyId", "identity").build();
   
   public void testSendMessageMapWhenResponseIs2xx() throws Exception {

      HttpResponse sendMessageResponse = HttpResponse.builder()
            .statusCode(200)
            .payload(payloadFromResourceWithContentType("/send_message_batch.xml", "text/xml")).build();

      SQSApi apiWhenExist = requestSendsResponse(sendMessageMap, sendMessageResponse);

      assertEquals(
            apiWhenExist.getMessageApiForQueue(queue)
                  .send(ImmutableMap.of("foo1", "payload1", "foo2", "payload2")).toString(),
            new SendMessageBatchResponseTest().expected().toString());
   }
   
   public HttpRequest sendMessageWithDelayMap = HttpRequest.builder()
         .method("POST")
         .endpoint("https://sqs.us-east-1.amazonaws.com/993194456877/adrian-sqs11/")
         .addHeader("Host", "sqs.us-east-1.amazonaws.com")
         .addFormParam("Action", "SendMessageBatch")
         .addFormParam("SendMessageBatchRequestEntry.1.DelaySeconds", "10")
         .addFormParam("SendMessageBatchRequestEntry.1.Id", "foo1")
         .addFormParam("SendMessageBatchRequestEntry.1.MessageBody", "payload1")
         .addFormParam("SendMessageBatchRequestEntry.2.DelaySeconds", "10")
         .addFormParam("SendMessageBatchRequestEntry.2.Id", "foo2")
         .addFormParam("SendMessageBatchRequestEntry.2.MessageBody", "payload2")
         .addFormParam("Signature", "COjjEaJ76EwziEFtkT2FuSRSbrCIu/hlJf1Zmu7cYoU%3D")
         .addFormParam("SignatureMethod", "HmacSHA256")
         .addFormParam("SignatureVersion", "2")
         .addFormParam("Timestamp", "2009-11-08T15%3A54%3A08.897Z")
         .addFormParam("Version", "2011-10-01")
         .addFormParam("AWSAccessKeyId", "identity").build();

   public void testSendMessageWithDelayMapWhenResponseIs2xx() throws Exception {

      HttpResponse sendMessageResponse = HttpResponse.builder()
            .statusCode(200)
            .payload(payloadFromResourceWithContentType("/send_message_batch.xml", "text/xml")).build();

      SQSApi apiWhenExist = requestSendsResponse(sendMessageWithDelayMap, sendMessageResponse);

      assertEquals(apiWhenExist.getMessageApiForQueue(queue).sendWithDelay(ImmutableMap.<String, String>builder()
            .put("foo1", "payload1")
            .put("foo2", "payload2")
            .build(), 10)
            .toString(), new SendMessageBatchResponseTest().expected().toString());
   }

   public HttpRequest sendMessageWithDelayIterable = HttpRequest.builder()
         .method("POST")
         .endpoint("https://sqs.us-east-1.amazonaws.com/993194456877/adrian-sqs11/")
         .addHeader("Host", "sqs.us-east-1.amazonaws.com")
         .addFormParam("Action", "SendMessageBatch")
         .addFormParam("SendMessageBatchRequestEntry.1.DelaySeconds", "10")
         .addFormParam("SendMessageBatchRequestEntry.1.Id", "1")
         .addFormParam("SendMessageBatchRequestEntry.1.MessageBody", "payload1")
         .addFormParam("SendMessageBatchRequestEntry.2.DelaySeconds", "10")
         .addFormParam("SendMessageBatchRequestEntry.2.Id", "2")
         .addFormParam("SendMessageBatchRequestEntry.2.MessageBody", "payload2")
         .addFormParam("Signature", "8AVNvSVXPSnoXjJAc6h1rysMBBZPnSycbnmD2/qpdV8%3D")
         .addFormParam("SignatureMethod", "HmacSHA256")
         .addFormParam("SignatureVersion", "2")
         .addFormParam("Timestamp", "2009-11-08T15%3A54%3A08.897Z")
         .addFormParam("Version", "2011-10-01")
         .addFormParam("AWSAccessKeyId", "identity").build();
   
   public void testSendMessageWithDelayIterableWhenResponseIs2xx() throws Exception {

      HttpResponse sendMessageResponse = HttpResponse.builder()
            .statusCode(200)
            .payload(payloadFromResourceWithContentType("/send_message_batch.xml", "text/xml")).build();

      SQSApi apiWhenExist = requestSendsResponse(sendMessageWithDelayIterable, sendMessageResponse);

      assertEquals(
            apiWhenExist.getMessageApiForQueue(queue).sendWithDelay(ImmutableSet.of("payload1", "payload2"), 10)
                  .toString(), new SendMessageBatchResponseTest().expected().toString());
   }
   public HttpRequest sendMessageWithDelaysTable = HttpRequest.builder()
         .method("POST")
         .endpoint("https://sqs.us-east-1.amazonaws.com/993194456877/adrian-sqs11/")
         .addHeader("Host", "sqs.us-east-1.amazonaws.com")
         .addFormParam("Action", "SendMessageBatch")
         .addFormParam("SendMessageBatchRequestEntry.1.DelaySeconds", "1")
         .addFormParam("SendMessageBatchRequestEntry.1.Id", "foo1")
         .addFormParam("SendMessageBatchRequestEntry.1.MessageBody", "payload1")
         .addFormParam("SendMessageBatchRequestEntry.2.DelaySeconds", "10")
         .addFormParam("SendMessageBatchRequestEntry.2.Id", "foo2")
         .addFormParam("SendMessageBatchRequestEntry.2.MessageBody", "payload2")
         .addFormParam("Signature", "M2X8Al%2BbyyDM%2B9kdN28rMn1yJWl78hJ5i4GnaMZ1sYg%3D")
         .addFormParam("SignatureMethod", "HmacSHA256")
         .addFormParam("SignatureVersion", "2")
         .addFormParam("Timestamp", "2009-11-08T15%3A54%3A08.897Z")
         .addFormParam("Version", "2011-10-01")
         .addFormParam("AWSAccessKeyId", "identity").build();

   public void testSendMessageWithDelaysTableWhenResponseIs2xx() throws Exception {

      HttpResponse sendMessageResponse = HttpResponse.builder()
            .statusCode(200)
            .payload(payloadFromResourceWithContentType("/send_message_batch.xml", "text/xml")).build();

      SQSApi apiWhenExist = requestSendsResponse(sendMessageWithDelaysTable, sendMessageResponse);

      assertEquals(apiWhenExist.getMessageApiForQueue(queue).sendWithDelays(ImmutableTable.<String, String, Integer>builder()
            .put("foo1", "payload1", 1)
            .put("foo2", "payload2", 10)
            .build())
            .toString(), new SendMessageBatchResponseTest().expected().toString());
   }

   public HttpRequest sendMessageWithDelaysMap = HttpRequest.builder()
         .method("POST")
         .endpoint("https://sqs.us-east-1.amazonaws.com/993194456877/adrian-sqs11/")
         .addHeader("Host", "sqs.us-east-1.amazonaws.com")
         .addFormParam("Action", "SendMessageBatch")
         .addFormParam("SendMessageBatchRequestEntry.1.DelaySeconds", "1")
         .addFormParam("SendMessageBatchRequestEntry.1.Id", "1")
         .addFormParam("SendMessageBatchRequestEntry.1.MessageBody", "payload1")
         .addFormParam("SendMessageBatchRequestEntry.2.DelaySeconds", "10")
         .addFormParam("SendMessageBatchRequestEntry.2.Id", "2")
         .addFormParam("SendMessageBatchRequestEntry.2.MessageBody", "payload2")
         .addFormParam("Signature", "nbA4UnKDAuQCiCcvQHH/1UjMMeo2s3d94A27Q3t9SlI%3D")
         .addFormParam("SignatureMethod", "HmacSHA256")
         .addFormParam("SignatureVersion", "2")
         .addFormParam("Timestamp", "2009-11-08T15%3A54%3A08.897Z")
         .addFormParam("Version", "2011-10-01")
         .addFormParam("AWSAccessKeyId", "identity").build();
   
   public void testSendMessageWithDelaysMapWhenResponseIs2xx() throws Exception {

      HttpResponse sendMessageResponse = HttpResponse.builder()
            .statusCode(200)
            .payload(payloadFromResourceWithContentType("/send_message_batch.xml", "text/xml")).build();

      SQSApi apiWhenExist = requestSendsResponse(sendMessageWithDelaysMap, sendMessageResponse);

      assertEquals(
            apiWhenExist.getMessageApiForQueue(queue).sendWithDelays(ImmutableMap.of("payload1", 1, "payload2", 10))
                  .toString(), new SendMessageBatchResponseTest().expected().toString());
   }
   
   public HttpRequest receiveMessage = HttpRequest.builder()
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

      assertEquals(apiWhenExist.getMessageApiForQueue(queue).receive().toString(),
            Iterables.get(new ReceiveMessageResponseTest().expected(), 0).toString());
   }
   

   public HttpRequest receiveMessages = HttpRequest.builder()
         .method("POST")
         .endpoint("https://sqs.us-east-1.amazonaws.com/993194456877/adrian-sqs11/")
         .addHeader("Host", "sqs.us-east-1.amazonaws.com")
         .addFormParam("Action", "ReceiveMessage")
         .addFormParam("MaxNumberOfMessages", "10")
         .addFormParam("Signature", "pZ9B4%2BTBvQA4n0joP4t8ue5x0xmKMd9prpVLVoT/7qU%3D")
         .addFormParam("SignatureMethod", "HmacSHA256")
         .addFormParam("SignatureVersion", "2")
         .addFormParam("Timestamp", "2009-11-08T15%3A54%3A08.897Z")
         .addFormParam("Version", "2011-10-01")
         .addFormParam("AWSAccessKeyId", "identity").build();

   public void testReceiveMessagesWhenResponseIs2xx() throws Exception {
      
      HttpResponse receiveMessagesResponse = HttpResponse.builder().statusCode(200)
            .payload(payloadFromResourceWithContentType("/messages.xml", "text/xml")).build();

      SQSApi apiWhenExist = requestSendsResponse(receiveMessages, receiveMessagesResponse);

      assertEquals(apiWhenExist.getMessageApiForQueue(queue).receive(10).toString(), new ReceiveMessageResponseTest()
            .expected().toString());
   }
   
   public HttpRequest deleteMessage = HttpRequest.builder()
         .method("POST")
         .endpoint("https://sqs.us-east-1.amazonaws.com/993194456877/adrian-sqs11/")
         .addHeader("Host", "sqs.us-east-1.amazonaws.com")
         .addFormParam("Action", "DeleteMessage")
         .addFormParam("ReceiptHandle", "eXJYhj5rDr9cAe")
         .addFormParam("Signature", "9/kuCc2i78gMsmul%2BRsOPcdQ1OLUKrItqgGIRRBJb8M%3D")
         .addFormParam("SignatureMethod", "HmacSHA256")
         .addFormParam("SignatureVersion", "2")
         .addFormParam("Timestamp", "2009-11-08T15%3A54%3A08.897Z")
         .addFormParam("Version", "2011-10-01")
         .addFormParam("AWSAccessKeyId", "identity").build();

   public void testDeleteMessageWhenResponseIs2xx() throws Exception {

      HttpResponse deleteMessageResponse = HttpResponse.builder()
            .statusCode(200)
            .payload(
                  payloadFromStringWithContentType(
                        "<DeleteMessageResponse><ResponseMetadata><RequestId>b5293cb5-d306-4a17-9048-b263635abe42</RequestId></ResponseMetadata></DeleteMessageResponse>",
                        "text/xml")).build();

      SQSApi apiWhenExist = requestSendsResponse(deleteMessage, deleteMessageResponse);

      apiWhenExist.getMessageApiForQueue(queue).delete("eXJYhj5rDr9cAe");
   }

   public HttpRequest deleteMessageIterable = HttpRequest.builder()
         .method("POST")
         .endpoint("https://sqs.us-east-1.amazonaws.com/993194456877/adrian-sqs11/")
         .addHeader("Host", "sqs.us-east-1.amazonaws.com")
         .addFormParam("Action", "DeleteMessageBatch")
         .addFormParam("DeleteMessageBatchRequestEntry.1.Id", "1")
         .addFormParam("DeleteMessageBatchRequestEntry.1.ReceiptHandle", "eXJYhj5rDr9cAe")
         .addFormParam("DeleteMessageBatchRequestEntry.2.Id", "2")
         .addFormParam("DeleteMessageBatchRequestEntry.2.ReceiptHandle", "fffeeerrr")
         .addFormParam("Signature", "S4xIobjm3LOkJvibeI2X54nxKJw9r1a5zj/dvHlfDMY%3D")
         .addFormParam("SignatureMethod", "HmacSHA256")
         .addFormParam("SignatureVersion", "2")
         .addFormParam("Timestamp", "2009-11-08T15%3A54%3A08.897Z")
         .addFormParam("Version", "2011-10-01")
         .addFormParam("AWSAccessKeyId", "identity").build();

   public void testDeleteMessageIterableWhenResponseIs2xx() throws Exception {

      HttpResponse deleteMessageResponse = HttpResponse.builder()
            .statusCode(200)
            .payload(payloadFromResourceWithContentType("/delete_message_batch.xml", "text/xml")).build();

      SQSApi apiWhenExist = requestSendsResponse(deleteMessageIterable, deleteMessageResponse);

      assertEquals(apiWhenExist.getMessageApiForQueue(queue).delete(ImmutableSet.of("eXJYhj5rDr9cAe", "fffeeerrr"))
            .toString(), new DeleteMessageBatchResponseTest().expected().toString());
   }

   public HttpRequest deleteMessageMap = HttpRequest.builder()
         .method("POST")
         .endpoint("https://sqs.us-east-1.amazonaws.com/993194456877/adrian-sqs11/")
         .addHeader("Host", "sqs.us-east-1.amazonaws.com")
         .addFormParam("Action", "DeleteMessageBatch")
         .addFormParam("DeleteMessageBatchRequestEntry.1.Id", "foo1")
         .addFormParam("DeleteMessageBatchRequestEntry.1.ReceiptHandle", "eXJYhj5rDr9cAe")
         .addFormParam("DeleteMessageBatchRequestEntry.2.Id", "foo2")
         .addFormParam("DeleteMessageBatchRequestEntry.2.ReceiptHandle", "fffeeerrr")
         .addFormParam("Signature", "kwHC3F3ZoJvfibhZWVTeIwFHUzoaVMR4OViyJbsmuV0%3D")
         .addFormParam("SignatureMethod", "HmacSHA256")
         .addFormParam("SignatureVersion", "2")
         .addFormParam("Timestamp", "2009-11-08T15%3A54%3A08.897Z")
         .addFormParam("Version", "2011-10-01")
         .addFormParam("AWSAccessKeyId", "identity").build();

   public void testDeleteMessageMapWhenResponseIs2xx() throws Exception {

      HttpResponse deleteMessageResponse = HttpResponse.builder()
            .statusCode(200)
            .payload(payloadFromResourceWithContentType("/delete_message_batch.xml", "text/xml")).build();

      SQSApi apiWhenExist = requestSendsResponse(deleteMessageMap, deleteMessageResponse);

      assertEquals(
            apiWhenExist.getMessageApiForQueue(queue)
                  .delete(ImmutableMap.of("foo1", "eXJYhj5rDr9cAe", "foo2", "fffeeerrr")).toString(),
            new DeleteMessageBatchResponseTest().expected().toString());
   }
   
   public HttpRequest changeMessageVisibility = HttpRequest.builder()
         .method("POST")
         .endpoint("https://sqs.us-east-1.amazonaws.com/993194456877/adrian-sqs11/")
         .addHeader("Host", "sqs.us-east-1.amazonaws.com")
         .addFormParam("Action", "ChangeMessageVisibility")
         .addFormParam("ReceiptHandle", "eXJYhj5rDr9cAe")
         .addFormParam("Signature", "gvmSHleGLkmszYU6aURCBImuec2k0O3pg3tAYhDvkNs%3D")
         .addFormParam("SignatureMethod", "HmacSHA256")
         .addFormParam("SignatureVersion", "2")
         .addFormParam("Timestamp", "2009-11-08T15%3A54%3A08.897Z")
         .addFormParam("Version", "2011-10-01")
         .addFormParam("VisibilityTimeout", "10")
         .addFormParam("AWSAccessKeyId", "identity").build();

   public void testChangeMessageVisibilityWhenResponseIs2xx() throws Exception {

      HttpResponse changeMessageVisibilityResponse = HttpResponse.builder()
            .statusCode(200)
            .payload(
                  payloadFromStringWithContentType(
                        "<ChangeMessageVisibilityResponse><ResponseMetadata><RequestId>b5293cb5-d306-4a17-9048-b263635abe42</RequestId></ResponseMetadata></ChangeMessageVisibilityResponse>",
                        "text/xml")).build();

      SQSApi apiWhenExist = requestSendsResponse(changeMessageVisibility, changeMessageVisibilityResponse);

      apiWhenExist.getMessageApiForQueue(queue).changeVisibility("eXJYhj5rDr9cAe", 10);
   }

   public HttpRequest changeMessageVisibilityTable = HttpRequest.builder()
         .method("POST")
         .endpoint("https://sqs.us-east-1.amazonaws.com/993194456877/adrian-sqs11/")
         .addHeader("Host", "sqs.us-east-1.amazonaws.com")
         .addFormParam("Action", "ChangeMessageVisibilityBatch")
         .addFormParam("ChangeMessageVisibilityBatchRequestEntry.1.Id", "foo1")
         .addFormParam("ChangeMessageVisibilityBatchRequestEntry.1.ReceiptHandle", "aaaaaaaaa")
         .addFormParam("ChangeMessageVisibilityBatchRequestEntry.1.VisibilityTimeout", "1")
         .addFormParam("ChangeMessageVisibilityBatchRequestEntry.2.Id", "foo2")
         .addFormParam("ChangeMessageVisibilityBatchRequestEntry.2.ReceiptHandle", "bbbbbbbbb")
         .addFormParam("ChangeMessageVisibilityBatchRequestEntry.2.VisibilityTimeout", "10")
         .addFormParam("Signature", "KjDusYiiC3hTdy3ZxLwBRHryrNoNaFb2AHJqUDu3mtQ%3D")
         .addFormParam("SignatureMethod", "HmacSHA256")
         .addFormParam("SignatureVersion", "2")
         .addFormParam("Timestamp", "2009-11-08T15%3A54%3A08.897Z")
         .addFormParam("Version", "2011-10-01")
         .addFormParam("AWSAccessKeyId", "identity").build();

   public void testChangeMessageVisibilityTableWhenResponseIs2xx() throws Exception {

      HttpResponse sendMessageResponse = HttpResponse.builder()
            .statusCode(200)
            .payload(payloadFromResourceWithContentType("/change_message_visibility_batch.xml", "text/xml")).build();

      SQSApi apiWhenExist = requestSendsResponse(changeMessageVisibilityTable, sendMessageResponse);

      assertEquals(apiWhenExist.getMessageApiForQueue(queue).changeVisibility(ImmutableTable.<String, String, Integer>builder()
            .put("foo1", "aaaaaaaaa", 1)
            .put("foo2", "bbbbbbbbb", 10)
            .build())
            .toString(), new ChangeMessageVisibilityBatchResponseTest().expected().toString());
   }

   public HttpRequest changeMessageVisibilityMap = HttpRequest.builder()
         .method("POST")
         .endpoint("https://sqs.us-east-1.amazonaws.com/993194456877/adrian-sqs11/")
         .addHeader("Host", "sqs.us-east-1.amazonaws.com")
         .addFormParam("Action", "ChangeMessageVisibilityBatch")
         .addFormParam("ChangeMessageVisibilityBatchRequestEntry.1.Id", "1")
         .addFormParam("ChangeMessageVisibilityBatchRequestEntry.1.ReceiptHandle", "aaaaaaaaa")
         .addFormParam("ChangeMessageVisibilityBatchRequestEntry.1.VisibilityTimeout", "1")
         .addFormParam("ChangeMessageVisibilityBatchRequestEntry.2.Id", "2")
         .addFormParam("ChangeMessageVisibilityBatchRequestEntry.2.ReceiptHandle", "bbbbbbbbb")
         .addFormParam("ChangeMessageVisibilityBatchRequestEntry.2.VisibilityTimeout", "10")
         .addFormParam("Signature", "zj2cftkpHtiYb9iOjPR3AhcVhoobi0JvOy22PvQJtho%3D")
         .addFormParam("SignatureMethod", "HmacSHA256")
         .addFormParam("SignatureVersion", "2")
         .addFormParam("Timestamp", "2009-11-08T15%3A54%3A08.897Z")
         .addFormParam("Version", "2011-10-01")
         .addFormParam("AWSAccessKeyId", "identity").build();
   
   public void testChangeMessageVisibilityMapWhenResponseIs2xx() throws Exception {

      HttpResponse sendMessageResponse = HttpResponse.builder()
            .statusCode(200)
            .payload(payloadFromResourceWithContentType("/change_message_visibility_batch.xml", "text/xml")).build();

      SQSApi apiWhenExist = requestSendsResponse(changeMessageVisibilityMap, sendMessageResponse);

      assertEquals(
            apiWhenExist.getMessageApiForQueue(queue).changeVisibility(ImmutableMap.of("aaaaaaaaa", 1, "bbbbbbbbb", 10))
                  .toString(), new ChangeMessageVisibilityBatchResponseTest().expected().toString());
   }

   public HttpRequest changeMessageVisibilityMapInt = HttpRequest.builder().method("POST")
                                                                           .endpoint("https://sqs.us-east-1.amazonaws.com/993194456877/adrian-sqs11/")
                                                                           .addHeader("Host", "sqs.us-east-1.amazonaws.com")
                                                                           .addFormParam("Action", "ChangeMessageVisibilityBatch")
                                                                           .addFormParam("ChangeMessageVisibilityBatchRequestEntry.1.Id", "foo1")
                                                                           .addFormParam("ChangeMessageVisibilityBatchRequestEntry.1.ReceiptHandle", "aaaaaaaaa")
                                                                           .addFormParam("ChangeMessageVisibilityBatchRequestEntry.1.VisibilityTimeout", "10")
                                                                           .addFormParam("ChangeMessageVisibilityBatchRequestEntry.2.Id", "foo2")
                                                                           .addFormParam("ChangeMessageVisibilityBatchRequestEntry.2.ReceiptHandle", "bbbbbbbbb")
                                                                           .addFormParam("ChangeMessageVisibilityBatchRequestEntry.2.VisibilityTimeout", "10")
                                                                           .addFormParam("Signature", "y/gaaxoE5wrG2P7NIAyfDo7DTgRx2PLJUi9/zNnWQ6A%3D")
                                                                           .addFormParam("SignatureMethod", "HmacSHA256")
                                                                           .addFormParam("SignatureVersion", "2")
                                                                           .addFormParam("Timestamp", "2009-11-08T15%3A54%3A08.897Z")
                                                                           .addFormParam("Version", "2011-10-01")
                                                                           .addFormParam("AWSAccessKeyId", "identity").build();

   public void testChangeMessageVisibilityMapIntWhenResponseIs2xx() throws Exception {

      HttpResponse sendMessageResponse = HttpResponse.builder()
            .statusCode(200)
            .payload(payloadFromResourceWithContentType("/change_message_visibility_batch.xml", "text/xml")).build();

      SQSApi apiWhenExist = requestSendsResponse(changeMessageVisibilityMapInt, sendMessageResponse);

      assertEquals(apiWhenExist.getMessageApiForQueue(queue).changeVisibility(ImmutableMap.<String, String>builder()
            .put("foo1", "aaaaaaaaa")
            .put("foo2", "bbbbbbbbb")
            .build(), 10)
            .toString(), new ChangeMessageVisibilityBatchResponseTest().expected().toString());
   }

   public HttpRequest changeMessageVisibilityIterableInt = HttpRequest.builder().method("POST")
                                                                      .endpoint("https://sqs.us-east-1.amazonaws.com/993194456877/adrian-sqs11/")
                                                                      .addHeader("Host", "sqs.us-east-1.amazonaws.com")
                                                                      .addFormParam("Action", "ChangeMessageVisibilityBatch")
                                                                      .addFormParam("ChangeMessageVisibilityBatchRequestEntry.1.Id", "1")
                                                                      .addFormParam("ChangeMessageVisibilityBatchRequestEntry.1.ReceiptHandle", "aaaaaaaaa")
                                                                      .addFormParam("ChangeMessageVisibilityBatchRequestEntry.1.VisibilityTimeout", "10")
                                                                      .addFormParam("ChangeMessageVisibilityBatchRequestEntry.2.Id", "2")
                                                                      .addFormParam("ChangeMessageVisibilityBatchRequestEntry.2.ReceiptHandle", "bbbbbbbbb")
                                                                      .addFormParam("ChangeMessageVisibilityBatchRequestEntry.2.VisibilityTimeout", "10")
                                                                      .addFormParam("Signature", "f5aq7zdKFErM3%2BIdtDX5NOzPO7mqCRzPGj2wUUEWjgE%3D")
                                                                      .addFormParam("SignatureMethod", "HmacSHA256")
                                                                      .addFormParam("SignatureVersion", "2")
                                                                      .addFormParam("Timestamp", "2009-11-08T15%3A54%3A08.897Z")
                                                                      .addFormParam("Version", "2011-10-01")
                                                                      .addFormParam("AWSAccessKeyId", "identity").build();
   
   public void testChangeMessageVisibilityIterableIntWhenResponseIs2xx() throws Exception {

      HttpResponse sendMessageResponse = HttpResponse.builder()
            .statusCode(200)
            .payload(payloadFromResourceWithContentType("/change_message_visibility_batch.xml", "text/xml")).build();

      SQSApi apiWhenExist = requestSendsResponse(changeMessageVisibilityIterableInt, sendMessageResponse);

      assertEquals(
            apiWhenExist.getMessageApiForQueue(queue).changeVisibility(ImmutableSet.of("aaaaaaaaa", "bbbbbbbbb"), 10)
                  .toString(), new ChangeMessageVisibilityBatchResponseTest().expected().toString());
   }
}
