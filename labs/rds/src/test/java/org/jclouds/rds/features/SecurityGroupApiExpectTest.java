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
package org.jclouds.rds.features;

import static org.jclouds.rds.options.ListSecurityGroupsOptions.Builder.afterMarker;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

import java.util.TimeZone;

import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.rds.RDSApi;
import org.jclouds.rds.internal.BaseRDSApiExpectTest;
import org.jclouds.rds.parse.DescribeDBSecurityGroupsResponseTest;
import org.jclouds.rds.parse.GetSecurityGroupResponseTest;
import org.jclouds.rest.ResourceNotFoundException;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "SecurityGroupApiExpectTest")
public class SecurityGroupApiExpectTest extends BaseRDSApiExpectTest {

   public SecurityGroupApiExpectTest() {
      TimeZone.setDefault(TimeZone.getTimeZone("America/Los_Angeles"));
   }

   public void testCreateWithNameAndDescriptionWhenResponseIs2xx() throws Exception {
      HttpRequest create = HttpRequest.builder()
               .method("POST")
               .endpoint("https://rds.us-east-1.amazonaws.com/")
               .addHeader("Host", "rds.us-east-1.amazonaws.com")
               .payload(
                  payloadFromStringWithContentType(
                           "Action=CreateDBSecurityGroup" +
                           "&DBSecurityGroupDescription=My%20new%20DBSecurityGroup" +
                           "&DBSecurityGroupName=mydbsecuritygroup" +
                           "&Signature=ZJ0F0Y5veTPir5NWc7KhmHp7cYIijAxKQFikPHJzzBI%3D" +
                           "&SignatureMethod=HmacSHA256" +
                           "&SignatureVersion=2" +
                           "&Timestamp=2009-11-08T15%3A54%3A08.897Z" +
                           "&Version=2012-04-23" +
                           "&AWSAccessKeyId=identity",
                        "application/x-www-form-urlencoded"))
               .build();
      
      HttpResponse createResponse = HttpResponse.builder().statusCode(200)
               .payload(payloadFromResourceWithContentType("/create_securitygroup.xml", "text/xml")).build();

      RDSApi apiWhenExist = requestSendsResponse(create, createResponse);

      apiWhenExist.getSecurityGroupApi().createWithNameAndDescription("mydbsecuritygroup", "My new DBSecurityGroup");
   }

   public void testCreateInVPCWithNameAndDescriptionWhenResponseIs2xx() throws Exception {
      HttpRequest create = HttpRequest.builder()
               .method("POST")
               .endpoint("https://rds.us-east-1.amazonaws.com/")
               .addHeader("Host", "rds.us-east-1.amazonaws.com")
               .payload(
                  payloadFromStringWithContentType(
                           "Action=CreateDBSecurityGroup" +
                           "&DBSecurityGroupDescription=My%20new%20DBSecurityGroup" +
                           "&DBSecurityGroupName=mydbsecuritygroup" +
                           "&EC2VpcId=vpc-1a2b3c4d" +
                           "&Signature=8MXHQRkGSKb0TzCKRIlDN9ymruqzY/QKgLMXoxYcqFI%3D" +
                           "&SignatureMethod=HmacSHA256" +
                           "&SignatureVersion=2" +
                           "&Timestamp=2009-11-08T15%3A54%3A08.897Z" +
                           "&Version=2012-04-23" +
                           "&AWSAccessKeyId=identity",
                        "application/x-www-form-urlencoded"))
               .build();
      
      HttpResponse createResponse = HttpResponse.builder().statusCode(200)
               .payload(payloadFromResourceWithContentType("/create_securitygroup.xml", "text/xml")).build();

      RDSApi apiWhenExist = requestSendsResponse(create, createResponse);

      apiWhenExist.getSecurityGroupApi().createInVPCWithNameAndDescription("vpc-1a2b3c4d", "mydbsecuritygroup", "My new DBSecurityGroup");
   }

   HttpRequest get = HttpRequest.builder()
                                       .method("POST")
                                       .endpoint("https://rds.us-east-1.amazonaws.com/")
                                       .addHeader("Host", "rds.us-east-1.amazonaws.com")
                                       .payload(
                                          payloadFromStringWithContentType(
                                                "Action=DescribeDBSecurityGroups" +
                                                      "&DBSecurityGroupName=name" +
                                                      "&Signature=F019%2B74qM/ivsW6ngZWfILFBss4RqPGlppawtAjwUPg%3D" +
                                                      "&SignatureMethod=HmacSHA256" +
                                                      "&SignatureVersion=2" +
                                                      "&Timestamp=2009-11-08T15%3A54%3A08.897Z" +
                                                      "&Version=2012-04-23" +
                                                      "&AWSAccessKeyId=identity",
                                                "application/x-www-form-urlencoded"))
                                       .build();
   
   
   public void testGetWhenResponseIs2xx() throws Exception {

      HttpResponse getResponse = HttpResponse.builder().statusCode(200)
            .payload(payloadFromResourceWithContentType("/get_securitygroup.xml", "text/xml")).build();

      RDSApi apiWhenExist = requestSendsResponse(
            get, getResponse);

      assertEquals(apiWhenExist.getSecurityGroupApi().get("name").toString(), new GetSecurityGroupResponseTest().expected().toString());
   }

   public void testGetWhenResponseIs404() throws Exception {

      HttpResponse getResponse = HttpResponse.builder().statusCode(404).build();

      RDSApi apiWhenDontExist = requestSendsResponse(
            get, getResponse);

      assertNull(apiWhenDontExist.getSecurityGroupApi().get("name"));
   }

   HttpRequest list = HttpRequest.builder()
                                       .method("POST")
                                       .endpoint("https://rds.us-east-1.amazonaws.com/")
                                       .addHeader("Host", "rds.us-east-1.amazonaws.com")
                                       .payload(
                                          payloadFromStringWithContentType(
                                                   "Action=DescribeDBSecurityGroups" +
                                                   "&Signature=6PMtOHuBCxE/uujPnvn/nN8NIZrwcx9X0Jy6hz/RXtg%3D" +
                                                   "&SignatureMethod=HmacSHA256" +
                                                   "&SignatureVersion=2" +
                                                   "&Timestamp=2009-11-08T15%3A54%3A08.897Z" +
                                                   "&Version=2012-04-23" +
                                                   "&AWSAccessKeyId=identity",
                                                "application/x-www-form-urlencoded"))
                                       .build();
   
   public void testListWhenResponseIs2xx() throws Exception {

      HttpResponse listResponse = HttpResponse.builder().statusCode(200)
            .payload(payloadFromResourceWithContentType("/describe_securitygroups.xml", "text/xml")).build();

      RDSApi apiWhenExist = requestSendsResponse(
            list, listResponse);

      assertEquals(apiWhenExist.getSecurityGroupApi().list().get(0).toString(), new DescribeDBSecurityGroupsResponseTest().expected().toString());
   }

   public void testList2PagesWhenResponseIs2xx() throws Exception {

      HttpResponse listResponse = HttpResponse.builder().statusCode(200)
            .payload(payloadFromResourceWithContentType("/describe_securitygroups_marker.xml", "text/xml")).build();

      HttpRequest list2 = HttpRequest.builder()
                                     .method("POST")
                                     .endpoint("https://rds.us-east-1.amazonaws.com/")
                                     .addHeader("Host", "rds.us-east-1.amazonaws.com")
                                     .payload(
                                        payloadFromStringWithContentType(
                                                 "Action=DescribeDBSecurityGroups" +
                                                 "&Marker=MARKER" +
                                                 "&Signature=DeZcA5ViQu/bB3PY9EmRZavRgYxLFMvdbq7topMKKhw%3D" +
                                                 "&SignatureMethod=HmacSHA256" +
                                                 "&SignatureVersion=2" +
                                                 "&Timestamp=2009-11-08T15%3A54%3A08.897Z" +
                                                 "&Version=2012-04-23" +
                                                  "&AWSAccessKeyId=identity",
                                              "application/x-www-form-urlencoded"))
                                     .build();
      
      HttpResponse list2Response = HttpResponse.builder().statusCode(200)
               .payload(payloadFromResourceWithContentType("/describe_securitygroups.xml", "text/xml")).build();

      RDSApi apiWhenExist = requestsSendResponses(
            list, listResponse, list2, list2Response);

      assertEquals(apiWhenExist.getSecurityGroupApi().list().concat().toList(),
               ImmutableList.copyOf(Iterables.concat(new DescribeDBSecurityGroupsResponseTest().expected(),
                        new DescribeDBSecurityGroupsResponseTest().expected())));
   }
   
   // TODO: this should really be an empty set
   @Test(expectedExceptions = ResourceNotFoundException.class)
   public void testListWhenResponseIs404() throws Exception {

      HttpResponse listResponse = HttpResponse.builder().statusCode(404).build();

      RDSApi apiWhenDontExist = requestSendsResponse(
            list, listResponse);

      apiWhenDontExist.getSecurityGroupApi().list().get(0);
   }
   
   public void testListWithOptionsWhenResponseIs2xx() throws Exception {
      HttpRequest listWithOptions =
            HttpRequest.builder()
                       .method("POST")
                       .endpoint("https://rds.us-east-1.amazonaws.com/")
                       .addHeader("Host", "rds.us-east-1.amazonaws.com")
                       .payload(payloadFromStringWithContentType(
                                                  "Action=DescribeDBSecurityGroups" +
                                                  "&Marker=MARKER" +
                                                  "&Signature=DeZcA5ViQu/bB3PY9EmRZavRgYxLFMvdbq7topMKKhw%3D" +
                                                  "&SignatureMethod=HmacSHA256" +
                                                  "&SignatureVersion=2" +
                                                  "&Timestamp=2009-11-08T15%3A54%3A08.897Z" +
                                                  "&Version=2012-04-23" +
                                                  "&AWSAccessKeyId=identity",
                                            "application/x-www-form-urlencoded"))
                       .build();
      
      HttpResponse listWithOptionsResponse = HttpResponse.builder().statusCode(200)
               .payload(payloadFromResourceWithContentType("/describe_securitygroups.xml", "text/xml")).build();

      RDSApi apiWhenWithOptionsExist = requestSendsResponse(listWithOptions,
               listWithOptionsResponse);

      assertEquals(apiWhenWithOptionsExist.getSecurityGroupApi().list(afterMarker("MARKER")).toString(),
               new DescribeDBSecurityGroupsResponseTest().expected().toString());
   }
   
   public void testAuthorizeIngressToIPRangeWhenResponseIs2xx() throws Exception {
      HttpRequest authorize = HttpRequest.builder()
               .method("POST")
               .endpoint("https://rds.us-east-1.amazonaws.com/")
               .addHeader("Host", "rds.us-east-1.amazonaws.com")
               .payload(
                  payloadFromStringWithContentType(
                           "Action=AuthorizeDBSecurityGroupIngress" +
                           "&CIDRIP=0.0.0.0/0" +
                           "&DBSecurityGroupName=mydbsecuritygroup" +
                           "&Signature=Wk06HjnbFH5j/yfguUK6p3ZJU9kpYPgOlN9IGctLVSk%3D" +
                           "&SignatureMethod=HmacSHA256" +
                           "&SignatureVersion=2" +
                           "&Timestamp=2009-11-08T15%3A54%3A08.897Z" +
                           "&Version=2012-04-23" +
                           "&AWSAccessKeyId=identity",
                        "application/x-www-form-urlencoded"))
               .build();
      
      HttpResponse authorizeResponse = HttpResponse.builder().statusCode(200)
               .payload(payloadFromResourceWithContentType("/authorize_securitygroup.xml", "text/xml")).build();

      RDSApi apiWhenExist = requestSendsResponse(authorize, authorizeResponse);

      apiWhenExist.getSecurityGroupApi().authorizeIngressToIPRange("mydbsecuritygroup", "0.0.0.0/0");
   }
   
   public void testAuthorizeIngressToEC2SecurityGroupOfOwnerWhenResponseIs2xx() throws Exception {
      HttpRequest authorize = HttpRequest.builder()
               .method("POST")
               .endpoint("https://rds.us-east-1.amazonaws.com/")
               .addHeader("Host", "rds.us-east-1.amazonaws.com")
               .payload(
                  payloadFromStringWithContentType(
                           "Action=AuthorizeDBSecurityGroupIngress" +
                           "&DBSecurityGroupName=mydbsecuritygroup" +
                           "&EC2SecurityGroupName=myec2securitygroup" +
                           "&EC2SecurityGroupOwnerId=054794666394" +
                           "&Signature=MM%2B8ccK7Mh%2BWLS4qA1NUyOqtkjC1ICXug8wcEyD4a6c%3D" +
                           "&SignatureMethod=HmacSHA256" +
                           "&SignatureVersion=2" +
                           "&Timestamp=2009-11-08T15%3A54%3A08.897Z" +
                           "&Version=2012-04-23" +
                           "&AWSAccessKeyId=identity",
                        "application/x-www-form-urlencoded"))
               .build();
      
      HttpResponse authorizeResponse = HttpResponse.builder().statusCode(200)
               .payload(payloadFromResourceWithContentType("/authorize_securitygroup.xml", "text/xml")).build();

      RDSApi apiWhenExist = requestSendsResponse(authorize, authorizeResponse);

      apiWhenExist.getSecurityGroupApi().authorizeIngressToEC2SecurityGroupOfOwner("mydbsecuritygroup", "myec2securitygroup", "054794666394");
   }
   
   public void testAuthorizeIngressToVPCSecurityGroupWhenResponseIs2xx() throws Exception {
      HttpRequest authorize = HttpRequest.builder()
               .method("POST")
               .endpoint("https://rds.us-east-1.amazonaws.com/")
               .addHeader("Host", "rds.us-east-1.amazonaws.com")
               .payload(
                  payloadFromStringWithContentType(
                           "Action=AuthorizeDBSecurityGroupIngress" +
                           "&DBSecurityGroupName=mydbsecuritygroup" +
                           "&EC2SecurityGroupId=sg-1312321312" +
                           "&Signature=o31Wey/wliTbHJoxdF7KGqIJwSM6pfqzkjIYio3XNGs%3D" +
                           "&SignatureMethod=HmacSHA256" +
                           "&SignatureVersion=2" +
                           "&Timestamp=2009-11-08T15%3A54%3A08.897Z" +
                           "&Version=2012-04-23" +
                           "&AWSAccessKeyId=identity",
                        "application/x-www-form-urlencoded"))
               .build();
      
      HttpResponse authorizeResponse = HttpResponse.builder().statusCode(200)
               .payload(payloadFromResourceWithContentType("/authorize_securitygroup.xml", "text/xml")).build();

      RDSApi apiWhenExist = requestSendsResponse(authorize, authorizeResponse);

      apiWhenExist.getSecurityGroupApi().authorizeIngressToVPCSecurityGroup("mydbsecuritygroup", "sg-1312321312");
   }

   public void testRevokeIngressFromIPRangeWhenResponseIs2xx() throws Exception {
      HttpRequest revoke = HttpRequest.builder()
               .method("POST")
               .endpoint("https://rds.us-east-1.amazonaws.com/")
               .addHeader("Host", "rds.us-east-1.amazonaws.com")
               .payload(
                  payloadFromStringWithContentType(
                           "Action=RevokeDBSecurityGroupIngress" +
                           "&CIDRIP=0.0.0.0/0" +
                           "&DBSecurityGroupName=mydbsecuritygroup" +
                           "&Signature=YD1%2BzKmoWyYCmqWq1X9f/Vj6UC7UnkwkPf%2BA5urnz%2BE%3D" +
                           "&SignatureMethod=HmacSHA256" +
                           "&SignatureVersion=2" +
                           "&Timestamp=2009-11-08T15%3A54%3A08.897Z" +
                           "&Version=2012-04-23" +
                           "&AWSAccessKeyId=identity",
                        "application/x-www-form-urlencoded"))
               .build();
      
      HttpResponse revokeResponse = HttpResponse.builder().statusCode(200)
               .payload(payloadFromResourceWithContentType("/revoke_securitygroup.xml", "text/xml")).build();

      RDSApi apiWhenExist = requestSendsResponse(revoke, revokeResponse);

      apiWhenExist.getSecurityGroupApi().revokeIngressFromIPRange("mydbsecuritygroup", "0.0.0.0/0");
   }
   
   public void testRevokeIngressFromEC2SecurityGroupOfOwnerWhenResponseIs2xx() throws Exception {
      HttpRequest revoke = HttpRequest.builder()
               .method("POST")
               .endpoint("https://rds.us-east-1.amazonaws.com/")
               .addHeader("Host", "rds.us-east-1.amazonaws.com")
               .payload(
                  payloadFromStringWithContentType(
                           "Action=RevokeDBSecurityGroupIngress" +
                           "&DBSecurityGroupName=mydbsecuritygroup" +
                           "&EC2SecurityGroupName=myec2securitygroup" +
                           "&EC2SecurityGroupOwnerId=054794666394" +
                           "&Signature=OknWXceQDAgmZBNzDdhxjaOJI48hYrnFJDOySBc4Qy4%3D" +
                           "&SignatureMethod=HmacSHA256" +
                           "&SignatureVersion=2" +
                           "&Timestamp=2009-11-08T15%3A54%3A08.897Z" +
                           "&Version=2012-04-23" +
                           "&AWSAccessKeyId=identity",
                        "application/x-www-form-urlencoded"))
               .build();
      
      HttpResponse revokeResponse = HttpResponse.builder().statusCode(200)
               .payload(payloadFromResourceWithContentType("/revoke_securitygroup.xml", "text/xml")).build();

      RDSApi apiWhenExist = requestSendsResponse(revoke, revokeResponse);

      apiWhenExist.getSecurityGroupApi().revokeIngressFromEC2SecurityGroupOfOwner("mydbsecuritygroup", "myec2securitygroup", "054794666394");
   }
   
   public void testRevokeIngressFromVPCSecurityGroupWhenResponseIs2xx() throws Exception {
      HttpRequest revoke = HttpRequest.builder()
               .method("POST")
               .endpoint("https://rds.us-east-1.amazonaws.com/")
               .addHeader("Host", "rds.us-east-1.amazonaws.com")
               .payload(
                  payloadFromStringWithContentType(
                           "Action=RevokeDBSecurityGroupIngress" +
                           "&DBSecurityGroupName=mydbsecuritygroup" +
                           "&EC2SecurityGroupId=sg-1312321312" +
                           "&Signature=YI2oGYI%2BCx4DGYx43WH/ehW6CWe6X6wEipsp5zPySzw%3D" +
                           "&SignatureMethod=HmacSHA256" +
                           "&SignatureVersion=2" +
                           "&Timestamp=2009-11-08T15%3A54%3A08.897Z" +
                           "&Version=2012-04-23" +
                           "&AWSAccessKeyId=identity",
                        "application/x-www-form-urlencoded"))
               .build();
      
      HttpResponse revokeResponse = HttpResponse.builder().statusCode(200)
               .payload(payloadFromResourceWithContentType("/revoke_securitygroup.xml", "text/xml")).build();

      RDSApi apiWhenExist = requestSendsResponse(revoke, revokeResponse);

      apiWhenExist.getSecurityGroupApi().revokeIngressFromVPCSecurityGroup("mydbsecuritygroup", "sg-1312321312");
   }
   
   
   HttpRequest delete = HttpRequest.builder()
            .method("POST")
            .endpoint("https://rds.us-east-1.amazonaws.com/")
            .addHeader("Host", "rds.us-east-1.amazonaws.com")
            .payload(
               payloadFromStringWithContentType(
                        "Action=DeleteDBSecurityGroup" +
                        "&DBSecurityGroupName=name" +
                        "&Signature=7lQqK7wJUuc7nONYnXdHVibudxqnfJPFrfdnzwEEKxE%3D" +
                        "&SignatureMethod=HmacSHA256" +
                        "&SignatureVersion=2" +
                        "&Timestamp=2009-11-08T15%3A54%3A08.897Z" +
                        "&Version=2012-04-23" +
                        "&AWSAccessKeyId=identity",
                     "application/x-www-form-urlencoded"))
            .build();

   public void testDeleteWhenResponseIs2xx() throws Exception {

      HttpResponse deleteResponse = HttpResponse.builder().statusCode(200).build();

      RDSApi apiWhenExist = requestSendsResponse(delete, deleteResponse);

      apiWhenExist.getSecurityGroupApi().delete("name");
   }

   public void testDeleteWhenResponseIs404() throws Exception {

      HttpResponse deleteResponse = HttpResponse.builder().statusCode(404).build();

      RDSApi apiWhenDontExist = requestSendsResponse(delete, deleteResponse);

      apiWhenDontExist.getSecurityGroupApi().delete("name");
   }
}
