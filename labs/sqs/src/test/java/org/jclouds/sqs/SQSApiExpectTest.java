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
import org.jclouds.sqs.domain.Action;
import org.jclouds.sqs.functions.MapToQueueAttributesTest;
import org.jclouds.sqs.internal.BaseSQSApiExpectTest;
import org.jclouds.sqs.parse.CreateQueueResponseTest;
import org.jclouds.sqs.parse.GetQueueAttributesResponseTest;
import org.jclouds.sqs.parse.ReceiveMessageResponseTest;
import org.jclouds.sqs.parse.SendMessageResponseTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "SQSApiExpectTest")
public class SQSApiExpectTest extends BaseSQSApiExpectTest {

   public HttpRequest createQueue = HttpRequest.builder()
         .method("POST")
         .endpoint("https://sqs.us-east-1.amazonaws.com/")
         .addHeader("Host", "sqs.us-east-1.amazonaws.com")
         .addFormParam("Action", "CreateQueue")
         .addFormParam("QueueName", "queueName")
         .addFormParam("Signature", "I7tmwiCzJ9cvw79pmlz1rOILh2C2ZV6OpLk23JGx6AU%3D")
         .addFormParam("SignatureMethod", "HmacSHA256")
         .addFormParam("SignatureVersion", "2")
         .addFormParam("Timestamp", "2009-11-08T15%3A54%3A08.897Z")
         .addFormParam("Version", "2011-10-01")
         .addFormParam("AWSAccessKeyId", "identity").build();

   public void testCreateQueueWhenResponseIs2xx() throws Exception {
      
      HttpResponse createQueueResponse = HttpResponse.builder().statusCode(200)
            .payload(payloadFromResourceWithContentType("/create_queue.xml", "text/xml")).build();

      SQSApi apiWhenExist = requestSendsResponse(createQueue, createQueueResponse);

      assertEquals(apiWhenExist.createQueueInRegion(null, "queueName").toString(), new CreateQueueResponseTest().expected().toString());
   }
   
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

      assertEquals(apiWhenExist.sendMessage(URI.create("https://sqs.us-east-1.amazonaws.com/993194456877/adrian-sqs11/"), "hardyharhar").toString(), new SendMessageResponseTest().expected().toString());
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

      assertEquals(
            apiWhenExist.receiveMessage(URI.create("https://sqs.us-east-1.amazonaws.com/993194456877/adrian-sqs11/"))
                  .toString(), Iterables.get(new ReceiveMessageResponseTest().expected(), 0).toString());
   }
   

   public HttpRequest receiveMessages = HttpRequest.builder()
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
   
   public HttpRequest deleteMessage = HttpRequest.builder()
         .method("POST")
         .endpoint("https://sqs.us-east-1.amazonaws.com/993194456877/adrian-sqs11/")
         .addHeader("Host", "sqs.us-east-1.amazonaws.com")
         .addFormParam("Action", "DeleteMessage")
         .addFormParam("ReceiptHandle", "eXJYhj5rDr9cAe")
         .addFormParam("Signature", "9%2FkuCc2i78gMsmul%2BRsOPcdQ1OLUKrItqgGIRRBJb8M%3D")
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

      apiWhenExist.deleteMessage(URI.create("https://sqs.us-east-1.amazonaws.com/993194456877/adrian-sqs11/"),
            "eXJYhj5rDr9cAe");
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

      apiWhenExist.changeMessageVisibility(URI.create("https://sqs.us-east-1.amazonaws.com/993194456877/adrian-sqs11/"),
            "eXJYhj5rDr9cAe", 10);
   }

   public HttpRequest getQueueAttribute = HttpRequest.builder()
         .method("POST")
         .endpoint("https://sqs.us-east-1.amazonaws.com/993194456877/adrian-sqs11/")
         .addHeader("Host", "sqs.us-east-1.amazonaws.com")
         .addFormParam("Action", "GetQueueAttributes")
         .addFormParam("AttributeName.1", "VisibilityTimeout")
         .addFormParam("Signature", "AfydayBBaIk4UGikHHY1CFNmOOAcTnogpFWydZyNass%3D")
         .addFormParam("SignatureMethod", "HmacSHA256")
         .addFormParam("SignatureVersion", "2")
         .addFormParam("Timestamp", "2009-11-08T15%3A54%3A08.897Z")
         .addFormParam("Version", "2011-10-01")
         .addFormParam("AWSAccessKeyId", "identity").build();

   public void testGetQueueAttributeWhenResponseIs2xx() throws Exception {
      
      HttpResponse getQueueAttributeResponse = HttpResponse.builder()
            .statusCode(200)
            .payload(
                  payloadFromStringWithContentType(
                        "<GetQueueAttributesResponse><GetQueueAttributesResult><Attribute><Name>VisibilityTimeout</Name><Value>30</Value></Attribute></GetQueueAttributesResult></GetQueueAttributesResponse>",
                        "text/xml")).build();

      SQSApi apiWhenExist = requestSendsResponse(getQueueAttribute, getQueueAttributeResponse);

      assertEquals(apiWhenExist.getQueueAttribute(URI.create("https://sqs.us-east-1.amazonaws.com/993194456877/adrian-sqs11/"), "VisibilityTimeout"), "30");
   }
   
   public HttpRequest getQueueAttributes = HttpRequest.builder()
         .method("POST")
         .endpoint("https://sqs.us-east-1.amazonaws.com/993194456877/adrian-sqs11/")
         .addHeader("Host", "sqs.us-east-1.amazonaws.com")
         .addFormParam("Action", "GetQueueAttributes")
         .addFormParam("AttributeName.1", "All")
         .addFormParam("Signature", "welFLn0TV6JlH6s6s60XZTJeJfFXGiXN4qNPrBx7aHc%3D")
         .addFormParam("SignatureMethod", "HmacSHA256")
         .addFormParam("SignatureVersion", "2")
         .addFormParam("Timestamp", "2009-11-08T15%3A54%3A08.897Z")
         .addFormParam("Version", "2011-10-01")
         .addFormParam("AWSAccessKeyId", "identity").build();

   public void testGetQueueAttributesWhenResponseIs2xx() throws Exception {
      
      HttpResponse getQueueAttributesResponse = HttpResponse.builder().statusCode(200)
            .payload(payloadFromResourceWithContentType("/attributes.xml", "text/xml")).build();

      SQSApi apiWhenExist = requestSendsResponse(getQueueAttributes, getQueueAttributesResponse);

      assertEquals(apiWhenExist.getQueueAttributes(URI.create("https://sqs.us-east-1.amazonaws.com/993194456877/adrian-sqs11/")).toString(), new MapToQueueAttributesTest().expected().toString());
   }
   
   public HttpRequest getQueueAttributesSubset = HttpRequest.builder()
         .method("POST")
         .endpoint("https://sqs.us-east-1.amazonaws.com/993194456877/adrian-sqs11/")
         .addHeader("Host", "sqs.us-east-1.amazonaws.com")
         .addFormParam("Action", "GetQueueAttributes")
         .addFormParam("AttributeName.1", "VisibilityTimeout")
         .addFormParam("AttributeName.2", "DelaySeconds")
         .addFormParam("Signature", "9KaiOOWWyFPTVMOnyHA3ZoXbPBPSD4AZ4q460UNMfDs%3D")
         .addFormParam("SignatureMethod", "HmacSHA256")
         .addFormParam("SignatureVersion", "2")
         .addFormParam("Timestamp", "2009-11-08T15%3A54%3A08.897Z")
         .addFormParam("Version", "2011-10-01")
         .addFormParam("AWSAccessKeyId", "identity").build();

   public void testGetQueueAttributesSubsetWhenResponseIs2xx() throws Exception {
      
      HttpResponse getQueueAttributesSubsetResponse = HttpResponse.builder().statusCode(200)
            .payload(payloadFromResourceWithContentType("/attributes.xml", "text/xml")).build();

      SQSApi apiWhenExist = requestSendsResponse(getQueueAttributesSubset, getQueueAttributesSubsetResponse);

      assertEquals(
            apiWhenExist.getQueueAttributes(
                  URI.create("https://sqs.us-east-1.amazonaws.com/993194456877/adrian-sqs11/"),
                  ImmutableSet.of("VisibilityTimeout", "DelaySeconds")).toString(),
            new GetQueueAttributesResponseTest().expected().toString());
   }
   
   public HttpRequest setQueueAttribute = HttpRequest.builder()
         .method("POST")
         .endpoint("https://sqs.us-east-1.amazonaws.com/993194456877/adrian-sqs11/")
         .addHeader("Host", "sqs.us-east-1.amazonaws.com")
         .addFormParam("Action", "SetQueueAttributes")
         .addFormParam("Attribute.Name", "MaximumMessageSize")
         .addFormParam("Attribute.Value", "1")
         .addFormParam("Signature", "ktBkQ3c%2FrwGcBSec0fkckfo73xmcoTuub5fxudM1qh0%3D")
         .addFormParam("SignatureMethod", "HmacSHA256")
         .addFormParam("SignatureVersion", "2")
         .addFormParam("Timestamp", "2009-11-08T15%3A54%3A08.897Z")
         .addFormParam("Version", "2011-10-01")
         .addFormParam("AWSAccessKeyId", "identity").build();

   public void testSetQueueAttributeWhenResponseIs2xx() throws Exception {

      HttpResponse setQueueAttributeResponse = HttpResponse.builder()
            .statusCode(200)
            .payload(
                  payloadFromStringWithContentType(
                        "<SetQueueAttributesResponse><ResponseMetadata><RequestId>b5293cb5-d306-4a17-9048-b263635abe42</RequestId></ResponseMetadata></SetQueueAttributesResponse>",
                        "text/xml")).build();

      SQSApi apiWhenExist = requestSendsResponse(setQueueAttribute, setQueueAttributeResponse);

      apiWhenExist.setQueueAttribute(URI.create("https://sqs.us-east-1.amazonaws.com/993194456877/adrian-sqs11/"),
            "MaximumMessageSize", "1");
   }
   
   public HttpRequest addPermission = HttpRequest.builder()
         .method("POST")
         .endpoint("https://sqs.us-east-1.amazonaws.com/993194456877/adrian-sqs11/")
         .addHeader("Host", "sqs.us-east-1.amazonaws.com")
         .addFormParam("Action", "AddPermission")
         .addFormParam("ActionName.1", "ReceiveMessage")
         .addFormParam("AWSAccountId.1", "125074342641")
         .addFormParam("Label", "testLabel")
         .addFormParam("Signature", "J9sV4q1rJ7dWYJDQp9JxsfEKNXQhpQBYIwBYi1IeXV0%3D")
         .addFormParam("SignatureMethod", "HmacSHA256")
         .addFormParam("SignatureVersion", "2")
         .addFormParam("Timestamp", "2009-11-08T15%3A54%3A08.897Z")
         .addFormParam("Version", "2011-10-01")
         .addFormParam("AWSAccessKeyId", "identity").build();

   public void testAddPermissionWhenResponseIs2xx() throws Exception {

      HttpResponse addPermissionResponse = HttpResponse.builder()
            .statusCode(200)
            .payload(
                  payloadFromStringWithContentType(
                        "<AddPermissionsResponse><ResponseMetadata><RequestId>b5293cb5-d306-4a17-9048-b263635abe42</RequestId></ResponseMetadata></AddPermissionsResponse>",
                        "text/xml")).build();

      SQSApi apiWhenExist = requestSendsResponse(addPermission, addPermissionResponse);

      apiWhenExist.addPermissionToAccount(URI.create("https://sqs.us-east-1.amazonaws.com/993194456877/adrian-sqs11/"), "testLabel", Action.RECEIVE_MESSAGE, "125074342641");
   }
   
   public HttpRequest removePermission = HttpRequest.builder()
         .method("POST")
         .endpoint("https://sqs.us-east-1.amazonaws.com/993194456877/adrian-sqs11/")
         .addHeader("Host", "sqs.us-east-1.amazonaws.com")
         .addFormParam("Action", "RemovePermission")
         .addFormParam("Label", "testLabel")
         .addFormParam("Signature", "VOA0L1uRVKQDQL1Klt0cYUajGoxN4Ur%2B7ISQ2I4RpRs%3D")
         .addFormParam("SignatureMethod", "HmacSHA256")
         .addFormParam("SignatureVersion", "2")
         .addFormParam("Timestamp", "2009-11-08T15%3A54%3A08.897Z")
         .addFormParam("Version", "2011-10-01")
         .addFormParam("AWSAccessKeyId", "identity").build();

   public void testRemovePermissionWhenResponseIs2xx() throws Exception {

      HttpResponse removePermissionResponse = HttpResponse.builder()
            .statusCode(200)
            .payload(
                  payloadFromStringWithContentType(
                        "<RemovePermissionsResponse><ResponseMetadata><RequestId>b5293cb5-d306-4a17-9048-b263635abe42</RequestId></ResponseMetadata></RemovePermissionsResponse>",
                        "text/xml")).build();

      SQSApi apiWhenExist = requestSendsResponse(removePermission, removePermissionResponse);

      apiWhenExist.removePermission(URI.create("https://sqs.us-east-1.amazonaws.com/993194456877/adrian-sqs11/"), "testLabel");
   }
}
