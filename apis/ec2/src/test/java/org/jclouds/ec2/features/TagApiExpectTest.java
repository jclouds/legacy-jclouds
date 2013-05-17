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
package org.jclouds.ec2.features;
import static org.testng.Assert.assertEquals;

import java.util.Properties;

import org.jclouds.Constants;
import org.jclouds.ec2.EC2Api;
import org.jclouds.ec2.internal.BaseEC2ApiExpectTest;
import org.jclouds.ec2.parse.DescribeTagsResponseTest;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.rest.ResourceNotFoundException;
import org.jclouds.rest.annotations.SinceApiVersion;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class TagApiExpectTest extends BaseEC2ApiExpectTest<EC2Api> {

   /**
    * @see TagApi
    * @see SinceApiVersion
    */
   protected Properties setupProperties() {
      Properties props = super.setupProperties();
      props.put(Constants.PROPERTY_API_VERSION, "2010-08-31");
      return props;
   }
   
   HttpRequest apply = HttpRequest.builder()
            .method("POST")
            .endpoint("https://ec2.us-east-1.amazonaws.com/")
            .addHeader("Host", "ec2.us-east-1.amazonaws.com")
            .payload(
                payloadFromStringWithContentType(
                        "Action=CreateTags" +
                        "&ResourceId.1=i-43532" +
                        "&Signature=Trp5e5%2BMqeBeBZbLYa9s9gxahQ9nkx6ETfsGl82IV8Y%3D" +
                        "&SignatureMethod=HmacSHA256" +
                        "&SignatureVersion=2" +
                        "&Tag.1.Key=tag" +
                        "&Tag.1.Value=" +
                        "&Timestamp=2012-04-16T15%3A54%3A08.897Z" +
                        "&Version=2010-08-31" +
                        "&AWSAccessKeyId=identity",
                     "application/x-www-form-urlencoded"))
            .build();

   public void testApplyWhenResponseIs2xx() throws Exception {

      HttpResponse applyResponse = HttpResponse.builder().statusCode(200).build();

      EC2Api apiWhenExist = requestSendsResponse(apply, applyResponse);

      apiWhenExist.getTagApi().get().applyToResources(ImmutableSet.of("tag"), ImmutableSet.of("i-43532"));
   }

   @Test(expectedExceptions = ResourceNotFoundException.class)
   public void testApplyWhenResponseIs404() throws Exception {

      HttpResponse applyResponse = HttpResponse.builder().statusCode(404).build();

      EC2Api apiWhenDontExist = requestSendsResponse(apply, applyResponse);

      apiWhenDontExist.getTagApi().get().applyToResources(ImmutableSet.of("tag"), ImmutableSet.of("i-43532"));
   }
   
   HttpRequest applyWithValues = HttpRequest.builder()
            .method("POST")
            .endpoint("https://ec2.us-east-1.amazonaws.com/")
            .addHeader("Host", "ec2.us-east-1.amazonaws.com")
            .payload(
               payloadFromStringWithContentType(
                        "Action=CreateTags" +
                        "&ResourceId.1=i-43532" +
                        "&Signature=jwCQr50j%2BvGkav4t0BN0G8RmNJ7VaFK6/7N/HKUmHL8%3D" +
                        "&SignatureMethod=HmacSHA256" +
                        "&SignatureVersion=2" +
                        "&Tag.1.Key=tag" +
                        "&Tag.1.Value=value" +
                        "&Timestamp=2012-04-16T15%3A54%3A08.897Z" +
                        "&Version=2010-08-31" +
                        "&AWSAccessKeyId=identity",
                     "application/x-www-form-urlencoded"))
            .build();

   public void testApplyWithValuesWhenResponseIs2xx() throws Exception {

      HttpResponse applyResponse = HttpResponse.builder().statusCode(200).build();

      EC2Api apiWhenExist = requestSendsResponse(applyWithValues, applyResponse);

      apiWhenExist.getTagApi().get().applyToResources(ImmutableMap.of("tag", "value"), ImmutableSet.of("i-43532"));
   }

   @Test(expectedExceptions = ResourceNotFoundException.class)
   public void testApplyWithValuesWhenResponseIs404() throws Exception {

      HttpResponse applyResponse = HttpResponse.builder().statusCode(404).build();

      EC2Api apiWhenDontExist = requestSendsResponse(applyWithValues, applyResponse);

      apiWhenDontExist.getTagApi().get().applyToResources(ImmutableMap.of("tag", "value"), ImmutableSet.of("i-43532"));
   }
   
   HttpRequest list = HttpRequest.builder()
                                       .method("POST")
                                       .endpoint("https://ec2.us-east-1.amazonaws.com/")
                                       .addHeader("Host", "ec2.us-east-1.amazonaws.com")
                                       .payload(
                                          payloadFromStringWithContentType(
                                                   "Action=DescribeTags" +
                                                   "&Signature=DYUjPGiRl9copBtmpocMZYVAy4OTrK2AJzcAH5QiBuw%3D" +
                                                   "&SignatureMethod=HmacSHA256" +
                                                   "&SignatureVersion=2" +
                                                   "&Timestamp=2012-04-16T15%3A54%3A08.897Z" +
                                                   "&Version=2010-08-31" +
                                                   "&AWSAccessKeyId=identity",
                                                "application/x-www-form-urlencoded"))
                                       .build();
   
   public void testListWhenResponseIs2xx() throws Exception {

      HttpResponse listResponse = HttpResponse.builder().statusCode(200)
            .payload(payloadFromResourceWithContentType("/describe_tags.xml", "text/xml")).build();

      EC2Api apiWhenExist = requestSendsResponse(
            list, listResponse);

      assertEquals(apiWhenExist.getTagApi().get().list().toString(), new DescribeTagsResponseTest().expected().toString());
   }

   public void testListWhenResponseIs404() throws Exception {

      HttpResponse listResponse = HttpResponse.builder().statusCode(404).build();

      EC2Api apiWhenDontExist = requestSendsResponse(
            list, listResponse);

      assertEquals(apiWhenDontExist.getTagApi().get().list().toSet(), ImmutableSet.of());
   }
   
   HttpRequest filter =
         HttpRequest.builder()
                    .method("POST")
                    .endpoint("https://ec2.us-east-1.amazonaws.com/")
                    .addHeader("Host", "ec2.us-east-1.amazonaws.com")
                    .payload(payloadFromStringWithContentType(
                                               "Action=DescribeTags" +
                                               "&Filter.1.Name=resource-type" +
                                               "&Filter.1.Value.1=instance" +
                                               "&Filter.2.Name=key" +
                                               "&Filter.2.Value.1=stack" +
                                               "&Signature=doNEEZHEzXV/SD2eSZ6PpB1PADcsAF99lXGvsh3MbS4%3D" +
                                               "&SignatureMethod=HmacSHA256" +
                                               "&SignatureVersion=2" +
                                               "&Timestamp=2012-04-16T15%3A54%3A08.897Z" +
                                               "&Version=2010-08-31" +
                                               "&AWSAccessKeyId=identity",
                                         "application/x-www-form-urlencoded"))
                    .build();   

   public void testFilterWhenResponseIs2xx() throws Exception {
      
      HttpResponse filterResponse = HttpResponse.builder().statusCode(200)
               .payload(payloadFromResourceWithContentType("/describe_tags.xml", "text/xml")).build();

      EC2Api apiWhenExist = requestSendsResponse(filter, filterResponse);

      assertEquals(apiWhenExist.getTagApi().get().filter(ImmutableMultimap.<String, String> builder()
                                                         .put("resource-type", "instance")
                                                         .put("key", "stack")
                                                         .build()).toString(),
               new DescribeTagsResponseTest().expected().toString());
   }
   
   public void testFilterWhenResponseIs404() throws Exception {

      HttpResponse filterResponse = HttpResponse.builder().statusCode(404).build();

      EC2Api apiWhenDontExist = requestSendsResponse(filter, filterResponse);

      assertEquals(apiWhenDontExist.getTagApi().get().filter(ImmutableMultimap.<String, String> builder()
            .put("resource-type", "instance")
            .put("key", "stack")
            .build()).toSet(), ImmutableSet.of());
   }
   
   HttpRequest delete = HttpRequest.builder()
            .method("POST")
            .endpoint("https://ec2.us-east-1.amazonaws.com/")
            .addHeader("Host", "ec2.us-east-1.amazonaws.com")
            .payload(
               payloadFromStringWithContentType(
                        "Action=DeleteTags" +
                        "&ResourceId.1=i-43532" +
                        "&Signature=ytM605menR00re60wXMgBDpozrQCi0lVupf755/Mpck%3D" +
                        "&SignatureMethod=HmacSHA256" +
                        "&SignatureVersion=2" +
                        "&Tag.1.Key=tag" +
                        "&Timestamp=2012-04-16T15%3A54%3A08.897Z" +
                        "&Version=2010-08-31" +
                        "&AWSAccessKeyId=identity",
                     "application/x-www-form-urlencoded"))
            .build();

   public void testDeleteWhenResponseIs2xx() throws Exception {

      HttpResponse deleteResponse = HttpResponse.builder().statusCode(200).build();

      EC2Api apiWhenExist = requestSendsResponse(delete, deleteResponse);

      apiWhenExist.getTagApi().get().deleteFromResources(ImmutableSet.of("tag"), ImmutableSet.of("i-43532"));
   }

   @Test(expectedExceptions = ResourceNotFoundException.class)
   public void testDeleteWhenResponseIs404() throws Exception {

      HttpResponse deleteResponse = HttpResponse.builder().statusCode(404).build();

      EC2Api apiWhenDontExist = requestSendsResponse(delete, deleteResponse);

      apiWhenDontExist.getTagApi().get().deleteFromResources(ImmutableSet.of("tag"), ImmutableSet.of("i-43532"));
   }
   
   
   HttpRequest conditionallyDelete = HttpRequest.builder()
            .method("POST")
            .endpoint("https://ec2.us-east-1.amazonaws.com/")
            .addHeader("Host", "ec2.us-east-1.amazonaws.com")
            .payload(
               payloadFromStringWithContentType(
                        "Action=DeleteTags" +
                        "&ResourceId.1=i-43532" +
                        "&Signature=vRvgPegVDDjIEKudZ5Tpck0GQrVts//1jzk4W5RgI9k%3D" +
                        "&SignatureMethod=HmacSHA256" +
                        "&SignatureVersion=2" +
                        "&Tag.1.Key=tag" +
                        "&Tag.1.Value=value" +
                        "&Timestamp=2012-04-16T15%3A54%3A08.897Z" +
                        "&Version=2010-08-31" +
                        "&AWSAccessKeyId=identity",
                     "application/x-www-form-urlencoded"))
            .build();

   public void testConditionallyDeleteWhenResponseIs2xx() throws Exception {

      HttpResponse conditionallyDeleteResponse = HttpResponse.builder().statusCode(200).build();

      EC2Api apiWhenExist = requestSendsResponse(conditionallyDelete, conditionallyDeleteResponse);

      apiWhenExist.getTagApi().get().conditionallyDeleteFromResources(ImmutableMap.of("tag", "value"), ImmutableSet.of("i-43532"));
   }

   @Test(expectedExceptions = ResourceNotFoundException.class)
   public void testConditionallyDeleteWhenResponseIs404() throws Exception {

      HttpResponse conditionallyDeleteResponse = HttpResponse.builder().statusCode(404).build();

      EC2Api apiWhenDontExist = requestSendsResponse(conditionallyDelete, conditionallyDeleteResponse);

      apiWhenDontExist.getTagApi().get().conditionallyDeleteFromResources(ImmutableMap.of("tag", "value"), ImmutableSet.of("i-43532"));
   }
}
