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

import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.sqs.SQSApi;
import org.jclouds.sqs.domain.Action;
import org.jclouds.sqs.internal.BaseSQSApiExpectTest;
import org.testng.annotations.Test;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "PermissionApiExpectTest")
public class PermissionApiExpectTest extends BaseSQSApiExpectTest {
   
   public HttpRequest addPermission = HttpRequest.builder()
         .method("POST")
         .endpoint("https://sqs.us-east-1.amazonaws.com/993194456877/adrian-sqs11/")
         .addHeader("Host", "sqs.us-east-1.amazonaws.com")
         .addFormParam("Action", "AddPermission")
         .addFormParam("AWSAccountId.1", "125074342641")
         .addFormParam("ActionName.1", "ReceiveMessage")
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

      apiWhenExist.getPermissionApiForQueue(queue).addPermissionToAccount("testLabel", Action.RECEIVE_MESSAGE, "125074342641");
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

      apiWhenExist.getPermissionApiForQueue(queue).remove("testLabel");
   }
}
