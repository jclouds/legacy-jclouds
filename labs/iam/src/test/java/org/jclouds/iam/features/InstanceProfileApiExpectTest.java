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
import org.jclouds.iam.parse.GetInstanceProfileResponseTest;
import org.jclouds.iam.parse.ListInstanceProfilesResponseTest;
import org.jclouds.rest.ResourceNotFoundException;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "InstanceProfileApiExpectTest")
public class InstanceProfileApiExpectTest extends BaseIAMApiExpectTest {
   HttpRequest create = HttpRequest.builder()
                                   .method("POST")
                                   .endpoint("https://iam.amazonaws.com/")
                                   .addHeader("Host", "iam.amazonaws.com")
                                   .addFormParam("Action", "CreateInstanceProfile")
                                   .addFormParam("InstanceProfileName", "name")
                                   .addFormParam("Signature", "UIosTnnvBVHY7m7rqz1489RQ90Mf81/aOXgh8x2mLWU%3D")
                                   .addFormParam("SignatureMethod", "HmacSHA256")
                                   .addFormParam("SignatureVersion", "2")
                                   .addFormParam("Timestamp", "2009-11-08T15%3A54%3A08.897Z")
                                   .addFormParam("Version", "2010-05-08")
                                   .addFormParam("AWSAccessKeyId", "identity").build();

   public void testCreateWhenResponseIs2xx() throws Exception {
   
      HttpResponse getResponse = HttpResponse.builder().statusCode(200)
            .payload(payloadFromResourceWithContentType("/get_instance_profile.xml", "text/xml")).build();

      IAMApi apiWhenExist = requestSendsResponse(create, getResponse);

      assertEquals(apiWhenExist.getInstanceProfileApi().create("name").toString(), new GetInstanceProfileResponseTest().expected().toString());
   }

   HttpRequest get = HttpRequest.builder()
                                .method("POST")
                                .endpoint("https://iam.amazonaws.com/")
                                .addHeader("Host", "iam.amazonaws.com")
                                .addFormParam("Action", "GetInstanceProfile")
                                .addFormParam("InstanceProfileName", "name")
                                .addFormParam("Signature", "uw5Ix/UFRqENsSWProK3%2BDMIezmvd3fFhTFMaooxFMg%3D")
                                .addFormParam("SignatureMethod", "HmacSHA256")
                                .addFormParam("SignatureVersion", "2")
                                .addFormParam("Timestamp", "2009-11-08T15%3A54%3A08.897Z")
                                .addFormParam("Version", "2010-05-08")
                                .addFormParam("AWSAccessKeyId", "identity").build();

   public void testGetWhenResponseIs2xx() throws Exception {

      HttpResponse getResponse = HttpResponse.builder().statusCode(200)
            .payload(payloadFromResourceWithContentType("/get_instance_profile.xml", "text/xml")).build();

      IAMApi apiWhenExist = requestSendsResponse(get, getResponse);

      assertEquals(apiWhenExist.getInstanceProfileApi().get("name").toString(), new GetInstanceProfileResponseTest().expected().toString());
   }

   public void testGetWhenResponseIs404() throws Exception {

      HttpResponse getResponse = HttpResponse.builder().statusCode(404).build();

      IAMApi apiWhenDontExist = requestSendsResponse(
            get, getResponse);

      assertNull(apiWhenDontExist.getInstanceProfileApi().get("name"));
   }

   HttpRequest delete = HttpRequest.builder()
                                   .method("POST")
                                   .endpoint("https://iam.amazonaws.com/")
                                   .addHeader("Host", "iam.amazonaws.com")
                                   .addFormParam("Action", "DeleteInstanceProfile")
                                   .addFormParam("InstanceProfileName", "name")
                                   .addFormParam("Signature", "7W47Gj/6NE6p6drXMtqozYOlUOQN7CzbXgrIup4iowk%3D")
                                   .addFormParam("SignatureMethod", "HmacSHA256")
                                   .addFormParam("SignatureVersion", "2")
                                   .addFormParam("Timestamp", "2009-11-08T15%3A54%3A08.897Z")
                                   .addFormParam("Version", "2010-05-08")
                                   .addFormParam("AWSAccessKeyId", "identity").build();

   public void testDeleteWhenResponseIs2xx() throws Exception {

      HttpResponse deleteResponse = HttpResponse.builder().statusCode(200)
            .payload(payloadFromResourceWithContentType("/delete_instance_profile.xml", "text/xml")).build();

      IAMApi apiWhenExist = requestSendsResponse(delete, deleteResponse);

      apiWhenExist.getInstanceProfileApi().delete("name");
   }

   public void testDeleteWhenResponseIs404() throws Exception {

      HttpResponse deleteResponse = HttpResponse.builder().statusCode(404).build();

      IAMApi apiWhenDontExist = requestSendsResponse(delete, deleteResponse);

      apiWhenDontExist.getInstanceProfileApi().delete("name");
   }

   HttpRequest list = HttpRequest.builder()
                                 .method("POST")
                                 .endpoint("https://iam.amazonaws.com/")
                                 .addHeader("Host", "iam.amazonaws.com")
                                 .addFormParam("Action", "ListInstanceProfiles")
                                 .addFormParam("Signature", "i2V6ZeplNRVaZ/9XfD4jv53Qh%2BNQdl3ZuoZc%2BLguf0o%3D")
                                 .addFormParam("SignatureMethod", "HmacSHA256")
                                 .addFormParam("SignatureVersion", "2")
                                 .addFormParam("Timestamp", "2009-11-08T15%3A54%3A08.897Z")
                                 .addFormParam("Version", "2010-05-08")
                                 .addFormParam("AWSAccessKeyId", "identity").build();

   public void testListWhenResponseIs2xx() throws Exception {

      HttpResponse listResponse = HttpResponse.builder().statusCode(200)
            .payload(payloadFromResourceWithContentType("/list_instance_profiles.xml", "text/xml")).build();

      IAMApi apiWhenExist = requestSendsResponse(list, listResponse);

      assertEquals(apiWhenExist.getInstanceProfileApi().list().get(0).toString(), new ListInstanceProfilesResponseTest().expected().toString());
   }

   public void testList2PagesWhenResponseIs2xx() throws Exception {

      HttpResponse listResponse = HttpResponse.builder().statusCode(200)
            .payload(payloadFromResourceWithContentType("/list_instance_profiles_marker.xml", "text/xml")).build();

      HttpRequest list2 = HttpRequest.builder()
                                    .method("POST")
                                    .endpoint("https://iam.amazonaws.com/")
                                    .addHeader("Host", "iam.amazonaws.com")
                                    .addFormParam("Action", "ListInstanceProfiles")
                                    .addFormParam("Marker", "MARKER")
                                    .addFormParam("Signature", "x7G5OvKxTIMEjl58OVurKrwf7wEA7exXSml63T89mSY%3D")
                                    .addFormParam("SignatureMethod", "HmacSHA256")
                                    .addFormParam("SignatureVersion", "2")
                                    .addFormParam("Timestamp", "2009-11-08T15%3A54%3A08.897Z")
                                    .addFormParam("Version", "2010-05-08")
                                    .addFormParam("AWSAccessKeyId", "identity").build();
      
      HttpResponse list2Response = HttpResponse.builder().statusCode(200)
               .payload(payloadFromResourceWithContentType("/list_instance_profiles.xml", "text/xml")).build();

      IAMApi apiWhenExist = requestsSendResponses(list, listResponse, list2, list2Response);

      assertEquals(apiWhenExist.getInstanceProfileApi().list().concat().toList(),
               ImmutableList.copyOf(Iterables.concat(new ListInstanceProfilesResponseTest().expected(), new ListInstanceProfilesResponseTest().expected())));
   }

   HttpRequest listPathPrefix = HttpRequest.builder()
                                           .method("POST")
                                           .endpoint("https://iam.amazonaws.com/")
                                           .addHeader("Host", "iam.amazonaws.com")
                                           .addFormParam("Action", "ListInstanceProfiles")
                                           .addFormParam("PathPrefix", "/subdivision")
                                           .addFormParam("Signature", "EEVeWhJhORpibHahIj1skQ3rhHaVb/iaqD22vIFQH7o%3D")
                                           .addFormParam("SignatureMethod", "HmacSHA256")
                                           .addFormParam("SignatureVersion", "2")
                                           .addFormParam("Timestamp", "2009-11-08T15%3A54%3A08.897Z")
                                           .addFormParam("Version", "2010-05-08")
                                           .addFormParam("AWSAccessKeyId", "identity").build();

   public void testListPathPrefixWhenResponseIs2xx() throws Exception {

      HttpResponse listResponse = HttpResponse.builder().statusCode(200)
            .payload(payloadFromResourceWithContentType("/list_instance_profiles.xml", "text/xml")).build();

      IAMApi apiWhenExist = requestSendsResponse(listPathPrefix, listResponse);

      assertEquals(apiWhenExist.getInstanceProfileApi().listPathPrefix("/subdivision").get(0).toString(), new ListInstanceProfilesResponseTest().expected().toString());
   }

   public void testListPathPrefix2PagesWhenResponseIs2xx() throws Exception {

      HttpResponse listResponse = HttpResponse.builder().statusCode(200)
            .payload(payloadFromResourceWithContentType("/list_instance_profiles_marker.xml", "text/xml")).build();

      HttpRequest listPathPrefix2 = HttpRequest.builder()
                                    .method("POST")
                                    .endpoint("https://iam.amazonaws.com/")
                                    .addHeader("Host", "iam.amazonaws.com")
                                    .addFormParam("Action", "ListInstanceProfiles")
                                    .addFormParam("Marker", "MARKER")
                                    .addFormParam("PathPrefix", "/subdivision")
                                    .addFormParam("Signature", "8xo94VlrqsoMoa6bpbqQbuVx8TLh8UmiQnc9QC58EhU%3D")
                                    .addFormParam("SignatureMethod", "HmacSHA256")
                                    .addFormParam("SignatureVersion", "2")
                                    .addFormParam("Timestamp", "2009-11-08T15%3A54%3A08.897Z")
                                    .addFormParam("Version", "2010-05-08")
                                    .addFormParam("AWSAccessKeyId", "identity").build();
      
      HttpResponse list2Response = HttpResponse.builder().statusCode(200)
               .payload(payloadFromResourceWithContentType("/list_instance_profiles.xml", "text/xml")).build();

      IAMApi apiWhenExist = requestsSendResponses(listPathPrefix, listResponse, listPathPrefix2, list2Response);

      assertEquals(apiWhenExist.getInstanceProfileApi().listPathPrefix("/subdivision").concat().toList(),
               ImmutableList.copyOf(Iterables.concat(new ListInstanceProfilesResponseTest().expected(), new ListInstanceProfilesResponseTest().expected())));
   }

   // TODO: this should really be an empty set
   @Test(expectedExceptions = ResourceNotFoundException.class)
   public void testListWhenResponseIs404() throws Exception {

      HttpResponse listResponse = HttpResponse.builder().statusCode(404).build();

      IAMApi apiWhenDontExist = requestSendsResponse(
            list, listResponse);

      apiWhenDontExist.getInstanceProfileApi().list().get(0);
   }
   
   public void testListPathPrefixAtWhenResponseIs2xx() throws Exception {
      HttpRequest listWithOptions =
            HttpRequest.builder()
                       .method("POST")
                       .endpoint("https://iam.amazonaws.com/")
                       .addHeader("Host", "iam.amazonaws.com")
                       .addFormParam("Action", "ListInstanceProfiles")
                       .addFormParam("Marker", "MARKER")
                       .addFormParam("PathPrefix", "/foo")
                       .addFormParam("Signature", "IBRktzqZ/GE8Y7DZqsjuOUNfJZTbkCnOZnHAmzwtju8%3D")
                       .addFormParam("SignatureMethod", "HmacSHA256")
                       .addFormParam("SignatureVersion", "2")
                       .addFormParam("Timestamp", "2009-11-08T15%3A54%3A08.897Z")
                       .addFormParam("Version", "2010-05-08")
                       .addFormParam("AWSAccessKeyId", "identity").build();

      HttpResponse listWithOptionsResponse = HttpResponse.builder().statusCode(200)
               .payload(payloadFromResourceWithContentType("/list_instance_profiles.xml", "text/xml")).build();

      IAMApi apiWhenWithOptionsExist = requestSendsResponse(listWithOptions,
               listWithOptionsResponse);

      assertEquals(apiWhenWithOptionsExist.getInstanceProfileApi().listPathPrefixAt("/foo", "MARKER").toString(),
               new ListInstanceProfilesResponseTest().expected().toString());
   }

   HttpRequest addRole = HttpRequest.builder()
                                   .method("POST")
                                   .endpoint("https://iam.amazonaws.com/")
                                   .addHeader("Host", "iam.amazonaws.com")
                                   .addFormParam("Action", "AddRoleToInstanceProfile")
                                   .addFormParam("InstanceProfileName", "name")
                                   .addFormParam("RoleName", "WebServer")
                                   .addFormParam("Signature", "QTM12yD9GwUKEE9wqbt03VlfZ/%2BfO0UWe9SbNoI9d3c%3D")
                                   .addFormParam("SignatureMethod", "HmacSHA256")
                                   .addFormParam("SignatureVersion", "2")
                                   .addFormParam("Timestamp", "2009-11-08T15%3A54%3A08.897Z")
                                   .addFormParam("Version", "2010-05-08")
                                   .addFormParam("AWSAccessKeyId", "identity").build();

   public void testAddRoleWhenResponseIs2xx() throws Exception {

      HttpResponse addRoleResponse = HttpResponse.builder().statusCode(200)
            .payload(payloadFromResourceWithContentType("/delete_instance_profile.xml", "text/xml")).build();

      IAMApi apiWhenExist = requestSendsResponse(addRole, addRoleResponse);

      apiWhenExist.getInstanceProfileApi().addRole("name", "WebServer");
   }

   HttpRequest removeRole = HttpRequest.builder()
                                       .method("POST")
                                       .endpoint("https://iam.amazonaws.com/")
                                       .addHeader("Host", "iam.amazonaws.com")
                                       .addFormParam("Action", "RemoveRoleFromInstanceProfile")
                                       .addFormParam("InstanceProfileName", "name")
                                       .addFormParam("RoleName", "WebServer")
                                       .addFormParam("Signature", "o1Uz2bOwe8H3DOnyNL5TK9lNDoKvWo7CNhspN7ml5Sc%3D")
                                       .addFormParam("SignatureMethod", "HmacSHA256")
                                       .addFormParam("SignatureVersion", "2")
                                       .addFormParam("Timestamp", "2009-11-08T15%3A54%3A08.897Z")
                                       .addFormParam("Version", "2010-05-08")
                                       .addFormParam("AWSAccessKeyId", "identity").build();

   public void testRemoveRoleWhenResponseIs2xx() throws Exception {

      HttpResponse removeRoleResponse = HttpResponse.builder().statusCode(200)
            .payload(payloadFromResourceWithContentType("/delete_instance_profile.xml", "text/xml")).build();

      IAMApi apiWhenExist = requestSendsResponse(removeRole, removeRoleResponse);

      apiWhenExist.getInstanceProfileApi().removeRole("name", "WebServer");
   }
}
