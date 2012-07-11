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

import java.net.URI;
import java.util.TimeZone;

import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.rds.RDSClient;
import org.jclouds.rds.internal.BaseRDSClientExpectTest;
import org.jclouds.rds.parse.DescribeDBSecurityGroupsResponseTest;
import org.jclouds.rds.parse.GetSecurityGroupResponseTest;
import org.jclouds.rest.ResourceNotFoundException;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMultimap;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "SecurityGroupClientExpectTest")
public class SecurityGroupClientExpectTest extends BaseRDSClientExpectTest {

   public SecurityGroupClientExpectTest() {
      TimeZone.setDefault(TimeZone.getTimeZone("America/Los_Angeles"));
   }

   HttpRequest get = HttpRequest.builder()
                                       .method("POST")
                                       .endpoint(URI.create("https://rds.us-east-1.amazonaws.com/"))
                                       .headers(ImmutableMultimap.<String, String> builder()
                                                .put("Host", "rds.us-east-1.amazonaws.com")
                                                .build())
                                       .payload(
                                          payloadFromStringWithContentType(
                                                "Action=DescribeDBSecurityGroups" +
                                                      "&DBSecurityGroupName=name" +
                                                      "&Signature=F019%2B74qM%2FivsW6ngZWfILFBss4RqPGlppawtAjwUPg%3D" +
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

      RDSClient clientWhenExist = requestSendsResponse(
            get, getResponse);

      assertEquals(clientWhenExist.getSecurityGroupClient().get("name").toString(), new GetSecurityGroupResponseTest().expected().toString());
   }

   public void testGetWhenResponseIs404() throws Exception {

      HttpResponse getResponse = HttpResponse.builder().statusCode(404).build();

      RDSClient clientWhenDontExist = requestSendsResponse(
            get, getResponse);

      assertNull(clientWhenDontExist.getSecurityGroupClient().get("name"));
   }

   HttpRequest list = HttpRequest.builder()
                                       .method("POST")
                                       .endpoint(URI.create("https://rds.us-east-1.amazonaws.com/"))
                                       .headers(ImmutableMultimap.<String, String> builder()
                                                .put("Host", "rds.us-east-1.amazonaws.com")
                                                .build())
                                       .payload(
                                          payloadFromStringWithContentType(
                                                   "Action=DescribeDBSecurityGroups" +
                                                   "&Signature=6PMtOHuBCxE%2FuujPnvn%2FnN8NIZrwcx9X0Jy6hz%2FRXtg%3D" +
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

      RDSClient clientWhenExist = requestSendsResponse(
            list, listResponse);

      assertEquals(clientWhenExist.getSecurityGroupClient().list().toString(), new DescribeDBSecurityGroupsResponseTest().expected().toString());
   }

   // TODO: this should really be an empty set
   @Test(expectedExceptions = ResourceNotFoundException.class)
   public void testListWhenResponseIs404() throws Exception {

      HttpResponse listResponse = HttpResponse.builder().statusCode(404).build();

      RDSClient clientWhenDontExist = requestSendsResponse(
            list, listResponse);

      clientWhenDontExist.getSecurityGroupClient().list();
   }
   
   public void testListWithOptionsWhenResponseIs2xx() throws Exception {
      HttpRequest listWithOptions =
            HttpRequest.builder()
                       .method("POST")
                       .endpoint(URI.create("https://rds.us-east-1.amazonaws.com/"))
                       .headers(ImmutableMultimap.<String, String>builder()
                                                 .put("Host", "rds.us-east-1.amazonaws.com")
                                                 .build())
                       .payload(payloadFromStringWithContentType(
                                                  "Action=DescribeDBSecurityGroups" +
                                                  "&Marker=MARKER" +
                                                  "&Signature=DeZcA5ViQu%2FbB3PY9EmRZavRgYxLFMvdbq7topMKKhw%3D" +
                                                  "&SignatureMethod=HmacSHA256" +
                                                  "&SignatureVersion=2" +
                                                  "&Timestamp=2009-11-08T15%3A54%3A08.897Z" +
                                                  "&Version=2012-04-23" +
                                                  "&AWSAccessKeyId=identity",
                                            "application/x-www-form-urlencoded"))
                       .build();
      
      HttpResponse listWithOptionsResponse = HttpResponse.builder().statusCode(200)
               .payload(payloadFromResourceWithContentType("/describe_securitygroups.xml", "text/xml")).build();

      RDSClient clientWhenWithOptionsExist = requestSendsResponse(listWithOptions,
               listWithOptionsResponse);

      assertEquals(clientWhenWithOptionsExist.getSecurityGroupClient().list(afterMarker("MARKER")).toString(),
               new DescribeDBSecurityGroupsResponseTest().expected().toString());
   }
   
   HttpRequest delete = HttpRequest.builder()
            .method("POST")
            .endpoint(URI.create("https://rds.us-east-1.amazonaws.com/"))
            .headers(ImmutableMultimap.<String, String> builder()
                     .put("Host", "rds.us-east-1.amazonaws.com")
                     .build())
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

      RDSClient clientWhenExist = requestSendsResponse(delete, deleteResponse);

      clientWhenExist.getSecurityGroupClient().delete("name");
   }

   public void testDeleteWhenResponseIs404() throws Exception {

      HttpResponse deleteResponse = HttpResponse.builder().statusCode(404).build();

      RDSClient clientWhenDontExist = requestSendsResponse(delete, deleteResponse);

      clientWhenDontExist.getSecurityGroupClient().delete("name");
   }
}
