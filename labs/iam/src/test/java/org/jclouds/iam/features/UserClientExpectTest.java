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

import static org.jclouds.iam.options.ListUsersOptions.Builder.pathPrefix;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

import java.net.URI;
import java.util.TimeZone;

import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.iam.IAMClient;
import org.jclouds.iam.internal.BaseIAMClientExpectTest;
import org.jclouds.iam.parse.GetUserResponseTest;
import org.jclouds.iam.parse.ListUsersResponseTest;
import org.jclouds.rest.ResourceNotFoundException;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMultimap;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "UserClientExpectTest")
public class UserClientExpectTest extends BaseIAMClientExpectTest {

   public UserClientExpectTest() {
      TimeZone.setDefault(TimeZone.getTimeZone("America/Los_Angeles"));
   }

   public void testGetCurrentWhenResponseIs2xx() throws Exception {
      HttpRequest get = HttpRequest.builder()
                                    .method("POST")
                                    .endpoint(URI.create("https://iam.amazonaws.com/"))
                                    .headers(ImmutableMultimap.<String, String> builder()
                                             .put("Host", "iam.amazonaws.com")
                                             .build())
                                    .payload(
                                       payloadFromStringWithContentType(
                                             "Action=GetUser" +
                                                   "&Signature=2UamWqKKgoSbaZpvixX0LKqGW%2FIIP9L319DLEUtYu3A%3D" +
                                                   "&SignatureMethod=HmacSHA256" +
                                                   "&SignatureVersion=2" +
                                                   "&Timestamp=2009-11-08T15%3A54%3A08.897Z" +
                                                   "&Version=2010-05-08" +
                                                   "&AWSAccessKeyId=identity",
                                             "application/x-www-form-urlencoded"))
                                    .build();
      HttpResponse getResponse = HttpResponse.builder().statusCode(200)
            .payload(payloadFromResourceWithContentType("/get_user.xml", "text/xml")).build();

      IAMClient clientWhenExist = requestSendsResponse(
            get, getResponse);

      assertEquals(clientWhenExist.getUserClient().getCurrent().toString(), new GetUserResponseTest().expected().toString());
   }
   
   HttpRequest get = HttpRequest.builder()
                                       .method("POST")
                                       .endpoint(URI.create("https://iam.amazonaws.com/"))
                                       .headers(ImmutableMultimap.<String, String> builder()
                                                .put("Host", "iam.amazonaws.com")
                                                .build())
                                       .payload(
                                          payloadFromStringWithContentType(
                                                "Action=GetUser" +
                                                      "&Signature=cnY%2FAaG656cruOmb3y7YHtjnPB1qg3aavff6PPxIMs0%3D" +
                                                      "&SignatureMethod=HmacSHA256" +
                                                      "&SignatureVersion=2" +
                                                      "&Timestamp=2009-11-08T15%3A54%3A08.897Z" +
                                                      "&UserName=name" +
                                                      "&Version=2010-05-08" +
                                                      "&AWSAccessKeyId=identity",
                                                "application/x-www-form-urlencoded"))
                                       .build();
   
   
   public void testGetWhenResponseIs2xx() throws Exception {

      HttpResponse getResponse = HttpResponse.builder().statusCode(200)
            .payload(payloadFromResourceWithContentType("/get_user.xml", "text/xml")).build();

      IAMClient clientWhenExist = requestSendsResponse(
            get, getResponse);

      assertEquals(clientWhenExist.getUserClient().get("name").toString(), new GetUserResponseTest().expected().toString());
   }

   public void testGetWhenResponseIs404() throws Exception {

      HttpResponse getResponse = HttpResponse.builder().statusCode(404).build();

      IAMClient clientWhenDontExist = requestSendsResponse(
            get, getResponse);

      assertNull(clientWhenDontExist.getUserClient().get("name"));
   }

   HttpRequest list = HttpRequest.builder()
                                       .method("POST")
                                       .endpoint(URI.create("https://iam.amazonaws.com/"))
                                       .headers(ImmutableMultimap.<String, String> builder()
                                                .put("Host", "iam.amazonaws.com")
                                                .build())
                                       .payload(
                                          payloadFromStringWithContentType(
                                                "Action=ListUsers" +
                                                      "&Signature=ed4OrONGuVlGpHSY8u5X2m9LVwx6oiihu7HbvA0iZkY%3D" +
                                                      "&SignatureMethod=HmacSHA256" +
                                                      "&SignatureVersion=2" +
                                                      "&Timestamp=2009-11-08T15%3A54%3A08.897Z" +
                                                      "&Version=2010-05-08" +
                                                      "&AWSAccessKeyId=identity",
                                                "application/x-www-form-urlencoded"))
                                       .build();
   
   public void testListWhenResponseIs2xx() throws Exception {

      HttpResponse listResponse = HttpResponse.builder().statusCode(200)
            .payload(payloadFromResourceWithContentType("/list_users.xml", "text/xml")).build();

      IAMClient clientWhenExist = requestSendsResponse(
            list, listResponse);

      assertEquals(clientWhenExist.getUserClient().list().toString(), new ListUsersResponseTest().expected().toString());
   }

   // TODO: this should really be an empty set
   @Test(expectedExceptions = ResourceNotFoundException.class)
   public void testListWhenResponseIs404() throws Exception {

      HttpResponse listResponse = HttpResponse.builder().statusCode(404).build();

      IAMClient clientWhenDontExist = requestSendsResponse(
            list, listResponse);

      clientWhenDontExist.getUserClient().list();
   }
   
   public void testListWithOptionsWhenResponseIs2xx() throws Exception {
      HttpRequest listWithOptions =
            HttpRequest.builder()
                       .method("POST")
                       .endpoint(URI.create("https://iam.amazonaws.com/"))
                       .headers(ImmutableMultimap.<String, String>builder()
                                                 .put("Host", "iam.amazonaws.com")
                                                 .build())
                       .payload(payloadFromStringWithContentType(
                                                  "Action=ListUsers" +
                                                  "&Marker=MARKER" +
                                                  "&PathPrefix=%2Ffoo" +
                                                  "&Signature=1%2BeCgNIAjHr%2BraNdDd3rsVC5Qok3AuTrJOa5mZwmE7g%3D" +
                                                  "&SignatureMethod=HmacSHA256" +
                                                  "&SignatureVersion=2" +
                                                  "&Timestamp=2009-11-08T15%3A54%3A08.897Z" +
                                                  "&Version=2010-05-08" +
                                                  "&AWSAccessKeyId=identity",
                                            "application/x-www-form-urlencoded"))
                       .build();
      
      HttpResponse listWithOptionsResponse = HttpResponse.builder().statusCode(200)
               .payload(payloadFromResourceWithContentType("/list_users.xml", "text/xml")).build();

      IAMClient clientWhenWithOptionsExist = requestSendsResponse(listWithOptions,
               listWithOptionsResponse);

      assertEquals(clientWhenWithOptionsExist.getUserClient().list(pathPrefix("/foo").afterMarker("MARKER")).toString(),
               new ListUsersResponseTest().expected().toString());
   }
}
