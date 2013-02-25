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
 * Unles required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either expres or implied.  See the License for the
 * specific language governing permisions and limitations
 * under the License.
 */
package org.jclouds.iam.features;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.iam.IAMApi;
import org.jclouds.iam.internal.BaseIAMApiExpectTest;
import org.jclouds.iam.parse.GetUserResponseTest;
import org.jclouds.iam.parse.ListUsersResponseTest;
import org.jclouds.rest.ResourceNotFoundException;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "UserApiExpectTest")
public class UserApiExpectTest extends BaseIAMApiExpectTest {

   public void testGetCurrentWhenResponseIs2xx() throws Exception {
      HttpRequest get = HttpRequest.builder()
                                   .method("POST")
                                   .endpoint("https://iam.amazonaws.com/")
                                   .addHeader("Host", "iam.amazonaws.com")
                                   .addFormParam("Action", "GetUser")
                                   .addFormParam("Signature", "2UamWqKKgoSbaZpvixX0LKqGW/IIP9L319DLEUtYu3A%3D")
                                   .addFormParam("SignatureMethod", "HmacSHA256")
                                   .addFormParam("SignatureVersion", "2")
                                   .addFormParam("Timestamp", "2009-11-08T15%3A54%3A08.897Z")
                                   .addFormParam("Version", "2010-05-08")
                                   .addFormParam("AWSAccessKeyId", "identity").build();
      
      HttpResponse getResponse = HttpResponse.builder().statusCode(200)
            .payload(payloadFromResourceWithContentType("/get_user.xml", "text/xml")).build();

      IAMApi apiWhenExist = requestSendsResponse(
            get, getResponse);

      assertEquals(apiWhenExist.getUserApi().getCurrent().toString(), new GetUserResponseTest().expected().toString());
   }
   
   HttpRequest get = HttpRequest.builder()
                                .method("POST")
                                .endpoint("https://iam.amazonaws.com/")
                                .addHeader("Host", "iam.amazonaws.com")
                                .addFormParam("Action", "GetUser")
                                .addFormParam("Signature", "cnY/AaG656cruOmb3y7YHtjnPB1qg3aavff6PPxIMs0%3D")
                                .addFormParam("SignatureMethod", "HmacSHA256")
                                .addFormParam("SignatureVersion", "2")
                                .addFormParam("Timestamp", "2009-11-08T15%3A54%3A08.897Z")
                                .addFormParam("UserName", "name")
                                .addFormParam("Version", "2010-05-08")
                                .addFormParam("AWSAccessKeyId", "identity").build();
   
   
   public void testGetWhenResponseIs2xx() throws Exception {

      HttpResponse getResponse = HttpResponse.builder().statusCode(200)
            .payload(payloadFromResourceWithContentType("/get_user.xml", "text/xml")).build();

      IAMApi apiWhenExist = requestSendsResponse(
            get, getResponse);

      assertEquals(apiWhenExist.getUserApi().get("name").toString(), new GetUserResponseTest().expected().toString());
   }

   public void testGetWhenResponseIs404() throws Exception {

      HttpResponse getResponse = HttpResponse.builder().statusCode(404).build();

      IAMApi apiWhenDontExist = requestSendsResponse(
            get, getResponse);

      assertNull(apiWhenDontExist.getUserApi().get("name"));
   }

   HttpRequest list = HttpRequest.builder()
                                 .method("POST")
                                 .endpoint("https://iam.amazonaws.com/")
                                 .addHeader("Host", "iam.amazonaws.com")
                                 .addFormParam("Action", "ListUsers")
                                 .addFormParam("Signature", "ed4OrONGuVlGpHSY8u5X2m9LVwx6oiihu7HbvA0iZkY%3D")
                                 .addFormParam("SignatureMethod", "HmacSHA256")
                                 .addFormParam("SignatureVersion", "2")
                                 .addFormParam("Timestamp", "2009-11-08T15%3A54%3A08.897Z")
                                 .addFormParam("Version", "2010-05-08")
                                 .addFormParam("AWSAccessKeyId", "identity").build();

   public void testListWhenResponseIs2xx() throws Exception {

      HttpResponse listResponse = HttpResponse.builder().statusCode(200)
            .payload(payloadFromResourceWithContentType("/list_users.xml", "text/xml")).build();

      IAMApi apiWhenExist = requestSendsResponse(
            list, listResponse);

      assertEquals(apiWhenExist.getUserApi().list().get(0).toString(), new ListUsersResponseTest().expected().toString());
   }

   public void testList2PagesWhenResponseIs2xx() throws Exception {

      HttpResponse listResponse = HttpResponse.builder().statusCode(200)
            .payload(payloadFromResourceWithContentType("/list_users_marker.xml", "text/xml")).build();

      HttpRequest list2 = HttpRequest.builder()
                                    .method("POST")
                                    .endpoint("https://iam.amazonaws.com/")
                                    .addHeader("Host", "iam.amazonaws.com")
                                    .addFormParam("Action", "ListUsers")
                                    .addFormParam("Marker", "MARKER")
                                    .addFormParam("Signature", "LKYao6Hll/plLDqOcgbNuJ6DhmOw0tZl4Sf3pPY%2By00%3D")
                                    .addFormParam("SignatureMethod", "HmacSHA256")
                                    .addFormParam("SignatureVersion", "2")
                                    .addFormParam("Timestamp", "2009-11-08T15%3A54%3A08.897Z")
                                    .addFormParam("Version", "2010-05-08")
                                    .addFormParam("AWSAccessKeyId", "identity").build();
      
      HttpResponse list2Response = HttpResponse.builder().statusCode(200)
               .payload(payloadFromResourceWithContentType("/list_users.xml", "text/xml")).build();

      IAMApi apiWhenExist = requestsSendResponses(list, listResponse, list2, list2Response);

      assertEquals(apiWhenExist.getUserApi().list().concat().toList(),
               ImmutableList.copyOf(Iterables.concat(new ListUsersResponseTest().expected(), new ListUsersResponseTest().expected())));
   }

   HttpRequest listPathPrefix = HttpRequest.builder()
                                           .method("POST")
                                           .endpoint("https://iam.amazonaws.com/")
                                           .addHeader("Host", "iam.amazonaws.com")
                                           .addFormParam("Action", "ListUsers")
                                           .addFormParam("PathPrefix", "/subdivision")
                                           .addFormParam("Signature", "giw/28vFH9GZoqf60bP4Ka80kBXhJDcncC%2BWA0Dkg/o%3D")
                                           .addFormParam("SignatureMethod", "HmacSHA256")
                                           .addFormParam("SignatureVersion", "2")
                                           .addFormParam("Timestamp", "2009-11-08T15%3A54%3A08.897Z")
                                           .addFormParam("Version", "2010-05-08")
                                           .addFormParam("AWSAccessKeyId", "identity").build();

   public void testListPathPrefixWhenResponseIs2xx() throws Exception {

      HttpResponse listResponse = HttpResponse.builder().statusCode(200)
            .payload(payloadFromResourceWithContentType("/list_users.xml", "text/xml")).build();

      IAMApi apiWhenExist = requestSendsResponse(
            listPathPrefix, listResponse);

      assertEquals(apiWhenExist.getUserApi().listPathPrefix("/subdivision").get(0).toString(), new ListUsersResponseTest().expected().toString());
   }

   public void testListPathPrefix2PagesWhenResponseIs2xx() throws Exception {

      HttpResponse listResponse = HttpResponse.builder().statusCode(200)
            .payload(payloadFromResourceWithContentType("/list_users_marker.xml", "text/xml")).build();

      HttpRequest listPathPrefix2 = HttpRequest.builder()
                                    .method("POST")
                                    .endpoint("https://iam.amazonaws.com/")
                                    .addHeader("Host", "iam.amazonaws.com")
                                    .addFormParam("Action", "ListUsers")
                                    .addFormParam("Marker", "MARKER")
                                    .addFormParam("PathPrefix", "/subdivision")
                                    .addFormParam("Signature", "JSAWWjJdoz8Twn3CSjkuqWIJmmokjQyKPOdekNeVl30%3D")
                                    .addFormParam("SignatureMethod", "HmacSHA256")
                                    .addFormParam("SignatureVersion", "2")
                                    .addFormParam("Timestamp", "2009-11-08T15%3A54%3A08.897Z")
                                    .addFormParam("Version", "2010-05-08")
                                    .addFormParam("AWSAccessKeyId", "identity").build();
      
      HttpResponse list2Response = HttpResponse.builder().statusCode(200)
               .payload(payloadFromResourceWithContentType("/list_users.xml", "text/xml")).build();

      IAMApi apiWhenExist = requestsSendResponses(listPathPrefix, listResponse, listPathPrefix2, list2Response);

      assertEquals(apiWhenExist.getUserApi().listPathPrefix("/subdivision").concat().toList(),
               ImmutableList.copyOf(Iterables.concat(new ListUsersResponseTest().expected(), new ListUsersResponseTest().expected())));
   }

   // TODO: this should really be an empty set
   @Test(expectedExceptions = ResourceNotFoundException.class)
   public void testListWhenResponseIs404() throws Exception {

      HttpResponse listResponse = HttpResponse.builder().statusCode(404).build();

      IAMApi apiWhenDontExist = requestSendsResponse(
            list, listResponse);

      apiWhenDontExist.getUserApi().list().get(0);
   }
   
   public void testListPathPrefixAtWhenResponseIs2xx() throws Exception {
      HttpRequest listWithOptions =
            HttpRequest.builder()
                       .method("POST")
                       .endpoint("https://iam.amazonaws.com/")
                       .addHeader("Host", "iam.amazonaws.com")
                       .addFormParam("Action", "ListUsers")
                       .addFormParam("Marker", "MARKER")
                       .addFormParam("PathPrefix", "/foo")
                       .addFormParam("Signature", "1%2BeCgNIAjHr%2BraNdDd3rsVC5Qok3AuTrJOa5mZwmE7g%3D")
                       .addFormParam("SignatureMethod", "HmacSHA256")
                       .addFormParam("SignatureVersion", "2")
                       .addFormParam("Timestamp", "2009-11-08T15%3A54%3A08.897Z")
                       .addFormParam("Version", "2010-05-08")
                       .addFormParam("AWSAccessKeyId", "identity").build();

      HttpResponse listWithOptionsResponse = HttpResponse.builder().statusCode(200)
               .payload(payloadFromResourceWithContentType("/list_users.xml", "text/xml")).build();

      IAMApi apiWhenWithOptionsExist = requestSendsResponse(listWithOptions,
               listWithOptionsResponse);

      assertEquals(apiWhenWithOptionsExist.getUserApi().listPathPrefixAt("/foo", "MARKER").toString(),
               new ListUsersResponseTest().expected().toString());
   }
}
