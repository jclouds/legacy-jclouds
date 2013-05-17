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
package org.jclouds.openstack.nova.ec2.internal;

import java.net.URI;

import javax.ws.rs.core.MediaType;

import org.jclouds.date.DateService;
import org.jclouds.date.internal.SimpleDateFormatDateService;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.openstack.nova.ec2.NovaEC2Client;
import org.jclouds.openstack.nova.ec2.config.NovaEC2RestClientModule;
import org.jclouds.rest.ConfiguresRestClient;
import org.jclouds.rest.internal.BaseRestClientExpectTest;

import com.google.inject.Module;
import com.google.inject.Provides;

public abstract class BaseNovaEC2RestClientExpectTest extends BaseRestClientExpectTest<NovaEC2Client> {
   protected static final String CONSTANT_DATE = "2012-04-16T15:54:08.897Z";
   
   protected DateService dateService = new SimpleDateFormatDateService();
   protected URI endpoint = URI.create("http://localhost:8773/services/Cloud/");

   protected HttpRequest describeAvailabilityZonesRequest = HttpRequest
            .builder()
            .method("POST")
            .endpoint(endpoint)
            .addHeader("Host", "localhost:8773")
            .payload(payloadFromStringWithContentType(
                     "Action=DescribeAvailabilityZones&Signature=S3fa5fybw4KAq4o11IpKHlqwx3cVJdKfeAKw3FIJYvM%3D&SignatureMethod=HmacSHA256&SignatureVersion=2&Timestamp=2012-04-16T15%3A54%3A08.897Z&Version=2009-04-04&AWSAccessKeyId=identity",
                     MediaType.APPLICATION_FORM_URLENCODED)).build();
   protected HttpResponse describeAvailabilityZonesResponse = HttpResponse.builder().statusCode(200)
            .payload(payloadFromResourceWithContentType("/nova_ec2_availabilityZones.xml", MediaType.APPLICATION_XML))
            .build();

   public BaseNovaEC2RestClientExpectTest() {
      provider = "openstack-nova-ec2";
   }

   @ConfiguresRestClient
   private static final class TestNovaEC2RestClientModule extends NovaEC2RestClientModule {
      @Override
      @Provides
      protected String provideTimeStamp(DateService dateService) {
         return CONSTANT_DATE;
      }
   }

   @Override
   protected Module createModule() {
      return new TestNovaEC2RestClientModule();
   }
}
