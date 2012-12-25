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
package org.jclouds.eucalyptus.internal;

import javax.ws.rs.core.MediaType;

import org.jclouds.date.DateService;
import org.jclouds.date.internal.SimpleDateFormatDateService;
import org.jclouds.ec2.EC2AsyncClient;
import org.jclouds.ec2.EC2Client;
import org.jclouds.ec2.config.EC2RestClientModule;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.rest.ConfiguresRestClient;
import org.jclouds.rest.internal.BaseRestClientExpectTest;

import com.google.inject.Module;
import com.google.inject.Provides;

/**
 * 
 * @author Adrian Cole
 */
public abstract class BaseEucalyptusExpectTest<T> extends BaseRestClientExpectTest<T> {
   protected static final String CONSTANT_DATE = "2012-04-16T15:54:08.897Z";
   
   protected DateService dateService = new SimpleDateFormatDateService();
   
   protected HttpRequest describeRegionsRequest = HttpRequest
         .builder()
         .method("POST")
         .endpoint("http://partnercloud.eucalyptus.com:8773/services/Eucalyptus/")
         .addHeader("Host", "partnercloud.eucalyptus.com:8773")
         .payload(payloadFromStringWithContentType(
                  "Action=DescribeRegions&Signature=tp9WpT8503JdxIXYu6Eu2Dmu%2Bd/pqviST7N7Fvr/yQo%3D&SignatureMethod=HmacSHA256&SignatureVersion=2&Timestamp=2012-04-16T15%3A54%3A08.897Z&Version=2010-06-15&AWSAccessKeyId=identity",
                  MediaType.APPLICATION_FORM_URLENCODED)).build();
   
   protected HttpResponse describeRegionsResponse = HttpResponse.builder().statusCode(200)
         .payload(payloadFromResourceWithContentType("/regionEndpoints-euca.xml", MediaType.APPLICATION_XML))
         .build();
   
   protected HttpRequest describeAZRequest = HttpRequest.builder()
         .method("POST")
         .endpoint("http://eucalyptus.partner.eucalyptus.com:8773/services/Eucalyptus/")
         .addHeader("Host", "eucalyptus.partner.eucalyptus.com:8773")
         .payload(payloadFromStringWithContentType(
                "Action=DescribeAvailabilityZones&Signature=i4OkMed1sqQV7hlF/l1KdbQwmwJ4Fh4o9W32eVGayPk%3D&SignatureMethod=HmacSHA256&SignatureVersion=2&Timestamp=2012-04-16T15%3A54%3A08.897Z&Version=2010-06-15&AWSAccessKeyId=identity",
                MediaType.APPLICATION_FORM_URLENCODED)).build();
   
   protected HttpResponse describeAZResponse = HttpResponse.builder().statusCode(200)
          .payload(payloadFromResourceWithContentType(
                "/availabilityZones-eucalyptus.xml", MediaType.APPLICATION_XML)).build();               


   public BaseEucalyptusExpectTest() {
      provider = "eucalyptus";
   }

   @ConfiguresRestClient
   private static final class TestEucalyptusRestClientModule extends EC2RestClientModule<EC2Client, EC2AsyncClient> {
      @Override
      @Provides
      protected String provideTimeStamp(DateService dateService) {
         return CONSTANT_DATE;
      }
   }

   @Override
   protected Module createModule() {
      return new TestEucalyptusRestClientModule();
   }
}
