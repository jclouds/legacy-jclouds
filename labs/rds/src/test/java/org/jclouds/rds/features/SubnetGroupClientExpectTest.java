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

import static org.jclouds.rds.options.ListSubnetGroupsOptions.Builder.afterMarker;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

import java.net.URI;
import java.util.TimeZone;

import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.rds.RDSClient;
import org.jclouds.rds.internal.BaseRDSClientExpectTest;
import org.jclouds.rds.parse.DescribeDBSubnetGroupsResponseTest;
import org.jclouds.rds.parse.GetSubnetGroupResponseTest;
import org.jclouds.rest.ResourceNotFoundException;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMultimap;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "SubnetGroupClientExpectTest")
public class SubnetGroupClientExpectTest extends BaseRDSClientExpectTest {

   public SubnetGroupClientExpectTest() {
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
                                                "Action=DescribeDBSubnetGroups" +
                                                      "&DBSubnetGroupName=name" +
                                                      "&Signature=U7DwaG%2BDARTb1iQxztQN%2BBe042ywyD7wxEVUlm4%2FA20%3D" +
                                                      "&SignatureMethod=HmacSHA256" +
                                                      "&SignatureVersion=2" +
                                                      "&Timestamp=2009-11-08T15%3A54%3A08.897Z" +
                                                      "&Version=2012-04-23" +
                                                      "&AWSAccessKeyId=identity",
                                                "application/x-www-form-urlencoded"))
                                       .build();
   
   
   public void testGetWhenResponseIs2xx() throws Exception {

      HttpResponse getResponse = HttpResponse.builder().statusCode(200)
            .payload(payloadFromResourceWithContentType("/get_subnetgroup.xml", "text/xml")).build();

      RDSClient clientWhenExist = requestSendsResponse(
            get, getResponse);

      assertEquals(clientWhenExist.getSubnetGroupClient().get("name").toString(), new GetSubnetGroupResponseTest().expected().toString());
   }

   public void testGetWhenResponseIs404() throws Exception {

      HttpResponse getResponse = HttpResponse.builder().statusCode(404).build();

      RDSClient clientWhenDontExist = requestSendsResponse(
            get, getResponse);

      assertNull(clientWhenDontExist.getSubnetGroupClient().get("name"));
   }

   HttpRequest list = HttpRequest.builder()
                                       .method("POST")
                                       .endpoint(URI.create("https://rds.us-east-1.amazonaws.com/"))
                                       .headers(ImmutableMultimap.<String, String> builder()
                                                .put("Host", "rds.us-east-1.amazonaws.com")
                                                .build())
                                       .payload(
                                          payloadFromStringWithContentType(
                                                   "Action=DescribeDBSubnetGroups" +
                                                   "&Signature=KLYL7jWGWT2ItwBv2z0ZNAFv1KAnPwNUhVqTHm0nWcI%3D" +
                                                   "&SignatureMethod=HmacSHA256" +
                                                   "&SignatureVersion=2" +
                                                   "&Timestamp=2009-11-08T15%3A54%3A08.897Z" +
                                                   "&Version=2012-04-23" +
                                                   "&AWSAccessKeyId=identity",
                                                "application/x-www-form-urlencoded"))
                                       .build();
   
   public void testListWhenResponseIs2xx() throws Exception {

      HttpResponse listResponse = HttpResponse.builder().statusCode(200)
            .payload(payloadFromResourceWithContentType("/describe_subnetgroups.xml", "text/xml")).build();

      RDSClient clientWhenExist = requestSendsResponse(
            list, listResponse);

      assertEquals(clientWhenExist.getSubnetGroupClient().list().toString(), new DescribeDBSubnetGroupsResponseTest().expected().toString());
   }

   // TODO: this should really be an empty set
   @Test(expectedExceptions = ResourceNotFoundException.class)
   public void testListWhenResponseIs404() throws Exception {

      HttpResponse listResponse = HttpResponse.builder().statusCode(404).build();

      RDSClient clientWhenDontExist = requestSendsResponse(
            list, listResponse);

      clientWhenDontExist.getSubnetGroupClient().list();
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
                                                  "Action=DescribeDBSubnetGroups" +
                                                  "&Marker=MARKER" +
                                                  "&Signature=1yK3VgSfUKDNHEwicyYbnMvSPAeJ7DZvi52gQeUUFSQ%3D" +
                                                  "&SignatureMethod=HmacSHA256" +
                                                  "&SignatureVersion=2" +
                                                  "&Timestamp=2009-11-08T15%3A54%3A08.897Z" +
                                                  "&Version=2012-04-23" +
                                                  "&AWSAccessKeyId=identity",
                                            "application/x-www-form-urlencoded"))
                       .build();
      
      HttpResponse listWithOptionsResponse = HttpResponse.builder().statusCode(200)
               .payload(payloadFromResourceWithContentType("/describe_subnetgroups.xml", "text/xml")).build();

      RDSClient clientWhenWithOptionsExist = requestSendsResponse(listWithOptions,
               listWithOptionsResponse);

      assertEquals(clientWhenWithOptionsExist.getSubnetGroupClient().list(afterMarker("MARKER")).toString(),
               new DescribeDBSubnetGroupsResponseTest().expected().toString());
   }
   
   HttpRequest delete = HttpRequest.builder()
            .method("POST")
            .endpoint(URI.create("https://rds.us-east-1.amazonaws.com/"))
            .headers(ImmutableMultimap.<String, String> builder()
                     .put("Host", "rds.us-east-1.amazonaws.com")
                     .build())
            .payload(
               payloadFromStringWithContentType(
                        "Action=DeleteDBSubnetGroup" +
                        "&DBSubnetGroupName=name" +
                        "&Signature=BbT14zD9UyRQzelQYzg%2F0FVcX%2Fs46ZyRtyxsdylOw7o%3D" +
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

      clientWhenExist.getSubnetGroupClient().delete("name");
   }

   public void testDeleteWhenResponseIs404() throws Exception {

      HttpResponse deleteResponse = HttpResponse.builder().statusCode(404).build();

      RDSClient clientWhenDontExist = requestSendsResponse(delete, deleteResponse);

      clientWhenDontExist.getSubnetGroupClient().delete("name");
   }
}
