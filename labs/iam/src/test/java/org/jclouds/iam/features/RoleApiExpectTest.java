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
import org.jclouds.iam.parse.GetRoleResponseTest;
import org.jclouds.iam.parse.ListRolesResponseTest;
import org.jclouds.rest.ResourceNotFoundException;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "RoleApiExpectTest")
public class RoleApiExpectTest extends BaseIAMApiExpectTest {
   String policy = "{\"Version\":\"2008-10-17\",\"Statement\":[{\"Sid\":\"\",\"Effect\":\"Allow\",\"Principal\":{\"Service\":\"ec2.amazonaws.com\"},\"Action\":\"sts:AssumeRole\"}]}";

   HttpRequest create = HttpRequest.builder()
                                   .method("POST")
                                   .endpoint("https://iam.amazonaws.com/")
                                   .addHeader("Host", "iam.amazonaws.com")
                                   .addFormParam("Action", "CreateRole")
                                   .addFormParam("AssumeRolePolicyDocument", policy)
                                   .addFormParam("RoleName", "name")
                                   .addFormParam("Signature", "zl7UtZElpvnkjo81NmA%2BCvYu0xFEeXQlSRtqTgok2OU%3D")
                                   .addFormParam("SignatureMethod", "HmacSHA256")
                                   .addFormParam("SignatureVersion", "2")
                                   .addFormParam("Timestamp", "2009-11-08T15%3A54%3A08.897Z")
                                   .addFormParam("Version", "2010-05-08")
                                   .addFormParam("AWSAccessKeyId", "identity").build();

   public void testCreateWhenResponseIs2xx() throws Exception {
   
      HttpResponse getResponse = HttpResponse.builder().statusCode(200)
            .payload(payloadFromResourceWithContentType("/get_role.xml", "text/xml")).build();

      IAMApi apiWhenExist = requestSendsResponse(create, getResponse);

      assertEquals(apiWhenExist.getRoleApi().createWithPolicy("name", policy).toString(), new GetRoleResponseTest().expected().toString());
   }

   HttpRequest get = HttpRequest.builder()
                                .method("POST")
                                .endpoint("https://iam.amazonaws.com/")
                                .addHeader("Host", "iam.amazonaws.com")
                                .addFormParam("Action", "GetRole")
                                .addFormParam("RoleName", "name")
                                .addFormParam("Signature", "OhV4oxbGMEJtWEDOUhR5n4u5TfGT9YtX/nVXHRyxDrs%3D")
                                .addFormParam("SignatureMethod", "HmacSHA256")
                                .addFormParam("SignatureVersion", "2")
                                .addFormParam("Timestamp", "2009-11-08T15%3A54%3A08.897Z")
                                .addFormParam("Version", "2010-05-08")
                                .addFormParam("AWSAccessKeyId", "identity").build();

   public void testGetWhenResponseIs2xx() throws Exception {

      HttpResponse getResponse = HttpResponse.builder().statusCode(200)
            .payload(payloadFromResourceWithContentType("/get_role.xml", "text/xml")).build();

      IAMApi apiWhenExist = requestSendsResponse(
            get, getResponse);

      assertEquals(apiWhenExist.getRoleApi().get("name").toString(), new GetRoleResponseTest().expected().toString());
   }

   public void testGetWhenResponseIs404() throws Exception {

      HttpResponse getResponse = HttpResponse.builder().statusCode(404).build();

      IAMApi apiWhenDontExist = requestSendsResponse(
            get, getResponse);

      assertNull(apiWhenDontExist.getRoleApi().get("name"));
   }

   HttpRequest delete = HttpRequest.builder()
                                   .method("POST")
                                   .endpoint("https://iam.amazonaws.com/")
                                   .addHeader("Host", "iam.amazonaws.com")
                                   .addFormParam("Action", "DeleteRole")
                                   .addFormParam("RoleName", "name")
                                   .addFormParam("Signature", "yhONyyLjFFtLgearEBrBNpSGTafh35LvRaaK8VagOVA%3D")
                                   .addFormParam("SignatureMethod", "HmacSHA256")
                                   .addFormParam("SignatureVersion", "2")
                                   .addFormParam("Timestamp", "2009-11-08T15%3A54%3A08.897Z")
                                   .addFormParam("Version", "2010-05-08")
                                   .addFormParam("AWSAccessKeyId", "identity").build();

   public void testDeleteWhenResponseIs2xx() throws Exception {

      HttpResponse deleteResponse = HttpResponse.builder().statusCode(200)
            .payload(payloadFromResourceWithContentType("/delete_role.xml", "text/xml")).build();

      IAMApi apiWhenExist = requestSendsResponse(delete, deleteResponse);

      apiWhenExist.getRoleApi().delete("name");
   }

   public void testDeleteWhenResponseIs404() throws Exception {

      HttpResponse deleteResponse = HttpResponse.builder().statusCode(404).build();

      IAMApi apiWhenDontExist = requestSendsResponse(delete, deleteResponse);

      apiWhenDontExist.getRoleApi().delete("name");
   }

   HttpRequest list = HttpRequest.builder()
                                 .method("POST")
                                 .endpoint("https://iam.amazonaws.com/")
                                 .addHeader("Host", "iam.amazonaws.com")
                                 .addFormParam("Action", "ListRoles")
                                 .addFormParam("Signature", "aUfKE6CqT%2BAiRMmcRWmGrw/6wNpzrKCwd35UufAVEbs%3D")
                                 .addFormParam("SignatureMethod", "HmacSHA256")
                                 .addFormParam("SignatureVersion", "2")
                                 .addFormParam("Timestamp", "2009-11-08T15%3A54%3A08.897Z")
                                 .addFormParam("Version", "2010-05-08")
                                 .addFormParam("AWSAccessKeyId", "identity").build();

   public void testListWhenResponseIs2xx() throws Exception {

      HttpResponse listResponse = HttpResponse.builder().statusCode(200)
            .payload(payloadFromResourceWithContentType("/list_roles.xml", "text/xml")).build();

      IAMApi apiWhenExist = requestSendsResponse(
            list, listResponse);

      assertEquals(apiWhenExist.getRoleApi().list().get(0).toString(), new ListRolesResponseTest().expected().toString());
   }

   public void testList2PagesWhenResponseIs2xx() throws Exception {

      HttpResponse listResponse = HttpResponse.builder().statusCode(200)
            .payload(payloadFromResourceWithContentType("/list_roles_marker.xml", "text/xml")).build();

      HttpRequest list2 = HttpRequest.builder()
                                    .method("POST")
                                    .endpoint("https://iam.amazonaws.com/")
                                    .addHeader("Host", "iam.amazonaws.com")
                                    .addFormParam("Action", "ListRoles")
                                    .addFormParam("Marker", "MARKER")
                                    .addFormParam("Signature", "gOfxvq54UyrEck9AmMy4tm5zcNlRWwWtLBzGpKASskk%3D")
                                    .addFormParam("SignatureMethod", "HmacSHA256")
                                    .addFormParam("SignatureVersion", "2")
                                    .addFormParam("Timestamp", "2009-11-08T15%3A54%3A08.897Z")
                                    .addFormParam("Version", "2010-05-08")
                                    .addFormParam("AWSAccessKeyId", "identity").build();
      
      HttpResponse list2Response = HttpResponse.builder().statusCode(200)
               .payload(payloadFromResourceWithContentType("/list_roles.xml", "text/xml")).build();

      IAMApi apiWhenExist = requestsSendResponses(list, listResponse, list2, list2Response);

      assertEquals(apiWhenExist.getRoleApi().list().concat().toList(),
               ImmutableList.copyOf(Iterables.concat(new ListRolesResponseTest().expected(), new ListRolesResponseTest().expected())));
   }

   HttpRequest listPathPrefix = HttpRequest.builder()
                                           .method("POST")
                                           .endpoint("https://iam.amazonaws.com/")
                                           .addHeader("Host", "iam.amazonaws.com")
                                           .addFormParam("Action", "ListRoles")
                                           .addFormParam("PathPrefix", "/subdivision")
                                           .addFormParam("Signature", "ELuhOLquxfQw5pv9381CBuUfqiXv5FHl836m31HA2BI%3D")
                                           .addFormParam("SignatureMethod", "HmacSHA256")
                                           .addFormParam("SignatureVersion", "2")
                                           .addFormParam("Timestamp", "2009-11-08T15%3A54%3A08.897Z")
                                           .addFormParam("Version", "2010-05-08")
                                           .addFormParam("AWSAccessKeyId", "identity").build();

   public void testListPathPrefixWhenResponseIs2xx() throws Exception {

      HttpResponse listResponse = HttpResponse.builder().statusCode(200)
            .payload(payloadFromResourceWithContentType("/list_roles.xml", "text/xml")).build();

      IAMApi apiWhenExist = requestSendsResponse(
            listPathPrefix, listResponse);

      assertEquals(apiWhenExist.getRoleApi().listPathPrefix("/subdivision").get(0).toString(), new ListRolesResponseTest().expected().toString());
   }

   public void testListPathPrefix2PagesWhenResponseIs2xx() throws Exception {

      HttpResponse listResponse = HttpResponse.builder().statusCode(200)
            .payload(payloadFromResourceWithContentType("/list_roles_marker.xml", "text/xml")).build();

      HttpRequest listPathPrefix2 = HttpRequest.builder()
                                    .method("POST")
                                    .endpoint("https://iam.amazonaws.com/")
                                    .addHeader("Host", "iam.amazonaws.com")
                                    .addFormParam("Action", "ListRoles")
                                    .addFormParam("Marker", "MARKER")
                                    .addFormParam("PathPrefix", "/subdivision")
                                    .addFormParam("Signature", "Y05M4vbhJpd35erXuhECszxjtx56cdIULGHnRaVr13s%3D")
                                    .addFormParam("SignatureMethod", "HmacSHA256")
                                    .addFormParam("SignatureVersion", "2")
                                    .addFormParam("Timestamp", "2009-11-08T15%3A54%3A08.897Z")
                                    .addFormParam("Version", "2010-05-08")
                                    .addFormParam("AWSAccessKeyId", "identity").build();
      
      HttpResponse list2Response = HttpResponse.builder().statusCode(200)
               .payload(payloadFromResourceWithContentType("/list_roles.xml", "text/xml")).build();

      IAMApi apiWhenExist = requestsSendResponses(listPathPrefix, listResponse, listPathPrefix2, list2Response);

      assertEquals(apiWhenExist.getRoleApi().listPathPrefix("/subdivision").concat().toList(),
               ImmutableList.copyOf(Iterables.concat(new ListRolesResponseTest().expected(), new ListRolesResponseTest().expected())));
   }

   // TODO: this should really be an empty set
   @Test(expectedExceptions = ResourceNotFoundException.class)
   public void testListWhenResponseIs404() throws Exception {

      HttpResponse listResponse = HttpResponse.builder().statusCode(404).build();

      IAMApi apiWhenDontExist = requestSendsResponse(
            list, listResponse);

      apiWhenDontExist.getRoleApi().list().get(0);
   }
   
   public void testListPathPrefixAtWhenResponseIs2xx() throws Exception {
      HttpRequest listWithOptions =
            HttpRequest.builder()
                       .method("POST")
                       .endpoint("https://iam.amazonaws.com/")
                       .addHeader("Host", "iam.amazonaws.com")
                       .addFormParam("Action", "ListRoles")
                       .addFormParam("Marker", "MARKER")
                       .addFormParam("PathPrefix", "/foo")
                       .addFormParam("Signature", "HUXPIey7u7ajfog4wFgJn59fcFWpMSjd5yjomenL7jc%3D")
                       .addFormParam("SignatureMethod", "HmacSHA256")
                       .addFormParam("SignatureVersion", "2")
                       .addFormParam("Timestamp", "2009-11-08T15%3A54%3A08.897Z")
                       .addFormParam("Version", "2010-05-08")
                       .addFormParam("AWSAccessKeyId", "identity").build();

      HttpResponse listWithOptionsResponse = HttpResponse.builder().statusCode(200)
               .payload(payloadFromResourceWithContentType("/list_roles.xml", "text/xml")).build();

      IAMApi apiWhenWithOptionsExist = requestSendsResponse(listWithOptions,
               listWithOptionsResponse);

      assertEquals(apiWhenWithOptionsExist.getRoleApi().listPathPrefixAt("/foo", "MARKER").toString(),
               new ListRolesResponseTest().expected().toString());
   }
}
