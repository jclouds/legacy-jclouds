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
package org.jclouds.ec2.internal;

import java.util.Map;
import java.util.TimeZone;

import javax.ws.rs.core.MediaType;

import org.jclouds.aws.filters.FormSigner;
import org.jclouds.date.DateService;
import org.jclouds.date.internal.SimpleDateFormatDateService;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.rest.internal.BaseRestClientExpectTest;
import org.testng.annotations.BeforeClass;

import com.google.common.base.Functions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Injector;

public abstract class BaseEC2ExpectTest<T> extends BaseRestClientExpectTest<T> {
   protected static final String CONSTANT_DATE = "2012-04-16T15:54:08.897Z";
   
   protected DateService dateService = new SimpleDateFormatDateService();
   
   protected FormSigner formSigner;

   protected HttpRequest describeRegionsRequest;
   
   protected HttpResponse describeRegionsResponse = HttpResponse.builder().statusCode(200)
         .payload(payloadFromResourceWithContentType("/regionEndpoints-all.xml", MediaType.APPLICATION_XML))
         .build();
   
   protected Map<HttpRequest, HttpResponse> describeAvailabilityZonesRequestResponse;

   public BaseEC2ExpectTest() {
      provider = "ec2";
      TimeZone.setDefault(TimeZone.getTimeZone("America/Los_Angeles"));
   }

   @BeforeClass
   protected void setupDefaultRequests() {
      Injector injector = createInjector(Functions.forMap(ImmutableMap.<HttpRequest, HttpResponse> of()),
            createModule(), setupProperties());
      formSigner = injector.getInstance(FormSigner.class);
      describeRegionsRequest = formSigner.filter(HttpRequest.builder().method("POST")
               .endpoint("https://ec2.us-east-1.amazonaws.com/").addHeader("Host", "ec2.us-east-1.amazonaws.com")
               .addFormParam("Action", "DescribeRegions").build());
      
      Builder<HttpRequest, HttpResponse> builder = ImmutableMap.<HttpRequest, HttpResponse> builder();
      for (String region : ImmutableSet.of("ap-northeast-1", "ap-southeast-1", "eu-west-1", "sa-east-1", "us-east-1", "us-west-1", "us-west-2")){
         builder.put(
               formSigner.filter(HttpRequest.builder()
                          .method("POST")
                          .endpoint("https://ec2." + region + ".amazonaws.com/")
                          .addHeader("Host", "ec2." + region + ".amazonaws.com")
                          .payload(payloadFromStringWithContentType(
                                 "Action=DescribeAvailabilityZones&Version=2010-06-15",
                                 MediaType.APPLICATION_FORM_URLENCODED)).build()),
               HttpResponse.builder().statusCode(200)
                           .payload(payloadFromResourceWithContentType(
                                 "/availabilityZones-" + region + ".xml", MediaType.APPLICATION_XML)).build());               

      }
      describeAvailabilityZonesRequestResponse = builder.build();
   }

}
