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
import org.jclouds.iam.parse.GetRolePolicyResponseTest;
import org.jclouds.iam.parse.ListRolePoliciesResponseTest;
import org.jclouds.rest.ResourceNotFoundException;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "RolePolicyApiExpectTest")
public class RolePolicyApiExpectTest extends BaseIAMApiExpectTest {
   String policy = "{\"Version\":\"2008-10-17\",\"Statement\":[{\"Effect\":\"Allow\",\"Action\":[\"s3:*\"],\"Resource\":[\"*\"]}]}";

   HttpRequest create = HttpRequest.builder()
                                   .method("POST")
                                   .endpoint("https://iam.amazonaws.com/")
                                   .addHeader("Host", "iam.amazonaws.com")
                                   .addFormParam("Action", "PutRolePolicy")
                                   .addFormParam("PolicyDocument", policy)
                                   .addFormParam("PolicyName", "S3AccessPolicy")
                                   .addFormParam("RoleName", "S3Access")
                                   .addFormParam("Signature", "CEf5SvDv%2BLBRwlZI/3nBghWXFHC1nMfOFccfAITNjOk%3D")
                                   .addFormParam("SignatureMethod", "HmacSHA256")
                                   .addFormParam("SignatureVersion", "2")
                                   .addFormParam("Timestamp", "2009-11-08T15%3A54%3A08.897Z")
                                   .addFormParam("Version", "2010-05-08")
                                   .addFormParam("AWSAccessKeyId", "identity").build();

   public void testCreateWhenResponseIs2xx() throws Exception {

      HttpResponse getResponse = HttpResponse.builder().statusCode(200)
            .payload(payloadFromResourceWithContentType("/put_role_policy.xml", "text/xml")).build();

      IAMApi apiWhenExist = requestSendsResponse(create, getResponse);

      apiWhenExist.getPolicyApiForRole("S3Access").create("S3AccessPolicy", policy);
   }

   HttpRequest get = HttpRequest.builder()
                                .method("POST")
                                .endpoint("https://iam.amazonaws.com/")
                                .addHeader("Host", "iam.amazonaws.com")
                                .addFormParam("Action", "GetRolePolicy")
                                .addFormParam("PolicyName", "S3AccessPolicy")
                                .addFormParam("RoleName", "S3Access")
                                .addFormParam("Signature", "MBTj0PjbypNbE7%2ByD2CJ/4NnzLFzV8RQNFPMI7GH03k%3D")
                                .addFormParam("SignatureMethod", "HmacSHA256")
                                .addFormParam("SignatureVersion", "2")
                                .addFormParam("Timestamp", "2009-11-08T15%3A54%3A08.897Z")
                                .addFormParam("Version", "2010-05-08")
                                .addFormParam("AWSAccessKeyId", "identity").build();

   public void testGetWhenResponseIs2xx() throws Exception {

      HttpResponse getResponse = HttpResponse.builder().statusCode(200)
            .payload(payloadFromResourceWithContentType("/get_role_policy.xml", "text/xml")).build();

      IAMApi apiWhenExist = requestSendsResponse(get, getResponse);

      assertEquals(apiWhenExist.getPolicyApiForRole("S3Access").get("S3AccessPolicy").toString(),
            new GetRolePolicyResponseTest().expected().toString());
   }

   public void testGetWhenResponseIs404() throws Exception {

      HttpResponse getResponse = HttpResponse.builder().statusCode(404).build();

      IAMApi apiWhenDontExist = requestSendsResponse(
            get, getResponse);

      assertNull(apiWhenDontExist.getPolicyApiForRole("S3Access").get("S3AccessPolicy"));
   }

   HttpRequest delete = HttpRequest.builder()
                                   .method("POST")
                                   .endpoint("https://iam.amazonaws.com/")
                                   .addHeader("Host", "iam.amazonaws.com")
                                   .addFormParam("Action", "DeleteRolePolicy")
                                   .addFormParam("PolicyName", "S3AccessPolicy")
                                   .addFormParam("RoleName", "S3Access")
                                   .addFormParam("Signature", "eoLLlpvrOuh9MU4d9y1frBFc6RISnzejYwh0jgtKlhY%3D")
                                   .addFormParam("SignatureMethod", "HmacSHA256")
                                   .addFormParam("SignatureVersion", "2")
                                   .addFormParam("Timestamp", "2009-11-08T15%3A54%3A08.897Z")
                                   .addFormParam("Version", "2010-05-08")
                                   .addFormParam("AWSAccessKeyId", "identity").build();

   public void testDeleteWhenResponseIs2xx() throws Exception {

      HttpResponse deleteResponse = HttpResponse.builder().statusCode(200)
            .payload(payloadFromResourceWithContentType("/delete_role_policy.xml", "text/xml")).build();

      IAMApi apiWhenExist = requestSendsResponse(delete, deleteResponse);

      apiWhenExist.getPolicyApiForRole("S3Access").delete("S3AccessPolicy");
   }

   public void testDeleteWhenResponseIs404() throws Exception {

      HttpResponse deleteResponse = HttpResponse.builder().statusCode(404).build();

      IAMApi apiWhenDontExist = requestSendsResponse(delete, deleteResponse);

      apiWhenDontExist.getPolicyApiForRole("S3Access").delete("S3AccessPolicy");
   }

   HttpRequest list = HttpRequest.builder()
                                 .method("POST")
                                 .endpoint("https://iam.amazonaws.com/")
                                 .addHeader("Host", "iam.amazonaws.com")
                                 .addFormParam("Action", "ListRolePolicies")
                                 .addFormParam("RoleName", "S3Access")
                                 .addFormParam("Signature", "qsfSpvDHNcMtKgnCiiYO1cikVVYrEHo/vqSt0tztvuY%3D")
                                 .addFormParam("SignatureMethod", "HmacSHA256")
                                 .addFormParam("SignatureVersion", "2")
                                 .addFormParam("Timestamp", "2009-11-08T15%3A54%3A08.897Z")
                                 .addFormParam("Version", "2010-05-08")
                                 .addFormParam("AWSAccessKeyId", "identity").build();

   public void testListWhenResponseIs2xx() throws Exception {

      HttpResponse listResponse = HttpResponse.builder().statusCode(200)
            .payload(payloadFromResourceWithContentType("/list_role_policies.xml", "text/xml")).build();

      IAMApi apiWhenExist = requestSendsResponse(list, listResponse);

      assertEquals(apiWhenExist.getPolicyApiForRole("S3Access").list().get(0).toString(),
            new ListRolePoliciesResponseTest().expected().toString());
   }

   public void testList2PagesWhenResponseIs2xx() throws Exception {

      HttpResponse listResponse = HttpResponse.builder().statusCode(200)
            .payload(payloadFromResourceWithContentType("/list_role_policies_marker.xml", "text/xml")).build();

      HttpRequest list2 = HttpRequest.builder()
                                    .method("POST")
                                    .endpoint("https://iam.amazonaws.com/")
                                    .addHeader("Host", "iam.amazonaws.com")
                                    .addFormParam("Action", "ListRolePolicies")
                                    .addFormParam("Marker", "MARKER")
                                    .addFormParam("RoleName", "S3Access")
                                    .addFormParam("Signature", "GdoVCf2QZ7rld%2BHvIgbSPhjgwYtigqYXdi/LfVzlWaM%3D")
                                    .addFormParam("SignatureMethod", "HmacSHA256")
                                    .addFormParam("SignatureVersion", "2")
                                    .addFormParam("Timestamp", "2009-11-08T15%3A54%3A08.897Z")
                                    .addFormParam("Version", "2010-05-08")
                                    .addFormParam("AWSAccessKeyId", "identity").build();
      
      HttpResponse list2Response = HttpResponse.builder().statusCode(200)
               .payload(payloadFromResourceWithContentType("/list_role_policies.xml", "text/xml")).build();

      IAMApi apiWhenExist = requestsSendResponses(list, listResponse, list2, list2Response);

      assertEquals(apiWhenExist.getPolicyApiForRole("S3Access").list().concat().toList(),
            ImmutableList.copyOf(Iterables.concat(new ListRolePoliciesResponseTest().expected(),
                  new ListRolePoliciesResponseTest().expected())));
   }

   // TODO: this should really be an empty set
   @Test(expectedExceptions = ResourceNotFoundException.class)
   public void testListWhenResponseIs404() throws Exception {

      HttpResponse listResponse = HttpResponse.builder().statusCode(404).build();

      IAMApi apiWhenDontExist = requestSendsResponse(list, listResponse);

      apiWhenDontExist.getPolicyApiForRole("S3Access").list().get(0);
   }
}
