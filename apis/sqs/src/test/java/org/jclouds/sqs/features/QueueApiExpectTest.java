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
package org.jclouds.sqs.features;

import static org.testng.Assert.assertEquals;

import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.sqs.SQSApi;
import org.jclouds.sqs.functions.MapToQueueAttributesTest;
import org.jclouds.sqs.internal.BaseSQSApiExpectTest;
import org.jclouds.sqs.parse.CreateQueueResponseTest;
import org.jclouds.sqs.parse.GetQueueAttributesResponseTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "QueueApiExpectTest")
public class QueueApiExpectTest extends BaseSQSApiExpectTest {
   
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

      assertEquals(apiWhenExist.getQueueApi().create("queueName").toString(), new CreateQueueResponseTest().expected()
            .toString());
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

      assertEquals(apiWhenExist.getQueueApi().getAttribute(queue, "VisibilityTimeout"), "30");
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

      assertEquals(apiWhenExist.getQueueApi().getAttributes(queue).toString(), new MapToQueueAttributesTest()
            .expected().toString());
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

      assertEquals(apiWhenExist.getQueueApi()
            .getAttributes(queue, ImmutableSet.of("VisibilityTimeout", "DelaySeconds")).toString(),
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

      apiWhenExist.getQueueApi().setAttribute(queue, "MaximumMessageSize", "1");
   }

}
