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
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jclouds.ec2.internal;

import java.net.URI;
import java.util.Map;

import javax.ws.rs.core.MediaType;

import org.jclouds.aws.filters.FormSigner;
import org.jclouds.date.DateService;
import org.jclouds.date.internal.SimpleDateFormatDateService;
import org.jclouds.ec2.EC2AsyncClient;
import org.jclouds.ec2.EC2Client;
import org.jclouds.ec2.config.EC2RestClientModule;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.rest.ConfiguresRestClient;
import org.jclouds.rest.internal.BaseRestClientExpectTest;

import com.google.common.base.Functions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;
import com.google.inject.Provides;

public abstract class BaseEC2ExpectTest<T> extends BaseRestClientExpectTest<T> {
   protected static final String CONSTANT_DATE = "2012-04-16T15:54:08.897Z";
   
   protected DateService dateService = new SimpleDateFormatDateService();
   
   protected HttpRequest describeRegionsRequest = HttpRequest
         .builder()
         .method("POST")
         .endpoint(URI.create("https://ec2.us-east-1.amazonaws.com/"))
         .headers(ImmutableMultimap.of("Host", "ec2.us-east-1.amazonaws.com"))
         .payload(payloadFromStringWithContentType(
                  "Action=DescribeRegions&Signature=s5OXKqaaeKhJW5FVrRntuMsUL4Ed5fjzgUWeukU96ko%3D&SignatureMethod=HmacSHA256&SignatureVersion=2&Timestamp=2012-04-16T15%3A54%3A08.897Z&Version=2010-06-15&AWSAccessKeyId=identity",
                  MediaType.APPLICATION_FORM_URLENCODED)).build();
   protected HttpResponse describeRegionsResponse = HttpResponse.builder().statusCode(200)
         .payload(payloadFromResourceWithContentType("/regionEndpoints-all.xml", MediaType.APPLICATION_XML))
         .build();
   
   protected final Map<HttpRequest, HttpResponse> describeAvailabilityZonesRequestResponse;

   public BaseEC2ExpectTest() {
      provider = "ec2";
      FormSigner formSigner = createInjector(Functions.forMap(ImmutableMap.<HttpRequest, HttpResponse> of()),
            createModule(), setupProperties()).getInstance(FormSigner.class);
      Builder<HttpRequest, HttpResponse> builder = ImmutableMap.<HttpRequest, HttpResponse> builder();
      for (String region : ImmutableSet.of("ap-northeast-1", "ap-southeast-1", "eu-west-1", "sa-east-1", "us-east-1", "us-west-1", "us-west-2")){
         builder.put(
               formSigner.filter(HttpRequest.builder()
                          .method("POST")
                          .endpoint(URI.create("https://ec2." + region + ".amazonaws.com/"))
                          .headers(ImmutableMultimap.of("Host", "ec2." + region + ".amazonaws.com"))
                          .payload(payloadFromStringWithContentType(
                                 "Action=DescribeAvailabilityZones&Version=2010-06-15",
                                 MediaType.APPLICATION_FORM_URLENCODED)).build()),
               HttpResponse.builder().statusCode(200)
                           .payload(payloadFromResourceWithContentType(
                                 "/availabilityZones-" + region + ".xml", MediaType.APPLICATION_XML)).build());               

      }
      describeAvailabilityZonesRequestResponse = builder.build();
   }

   @ConfiguresRestClient
   private static final class TestEC2RestClientModule extends EC2RestClientModule<EC2Client, EC2AsyncClient> {
      @Override
      @Provides
      protected String provideTimeStamp(DateService dateService) {
         return CONSTANT_DATE;
      }
   }

   @Override
   protected Module createModule() {
      return new TestEC2RestClientModule();
   }
}
