/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */
package org.jclouds.aws.ec2.services;

import static org.jclouds.aws.ec2.options.DescribeAvailabilityZonesOptions.Builder.availabilityZones;
import static org.jclouds.aws.ec2.options.DescribeRegionsOptions.Builder.regions;
import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.Map;

import javax.inject.Singleton;

import org.jclouds.aws.domain.Region;
import org.jclouds.aws.ec2.EC2;
import org.jclouds.aws.ec2.domain.AvailabilityZone;
import org.jclouds.aws.ec2.options.DescribeAvailabilityZonesOptions;
import org.jclouds.aws.ec2.options.DescribeRegionsOptions;
import org.jclouds.aws.ec2.xml.DescribeAvailabilityZonesResponseHandler;
import org.jclouds.aws.ec2.xml.DescribeRegionsResponseHandler;
import org.jclouds.aws.filters.FormSigner;
import org.jclouds.aws.reference.AWSConstants;
import org.jclouds.date.TimeStamp;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.logging.Logger;
import org.jclouds.logging.Logger.LoggerFactory;
import org.jclouds.rest.RestClientTest;
import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.jclouds.rest.internal.RestAnnotationProcessor;
import org.jclouds.util.Jsr330;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;
import com.google.inject.AbstractModule;
import com.google.inject.Module;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;

/**
 * Tests behavior of {@code AvailabilityZoneAndRegionAsyncClient}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "ec2.AvailabilityZoneAndRegionAsyncClientTest")
public class AvailabilityZoneAndRegionAsyncClientTest extends
         RestClientTest<AvailabilityZoneAndRegionAsyncClient> {

   public void testDescribeAvailabilityZones() throws SecurityException, NoSuchMethodException,
            IOException {
      Method method = AvailabilityZoneAndRegionAsyncClient.class.getMethod(
               "describeAvailabilityZonesInRegion", Region.class, Array.newInstance(
                        DescribeAvailabilityZonesOptions.class, 0).getClass());
      GeneratedHttpRequest<AvailabilityZoneAndRegionAsyncClient> httpMethod = processor
               .createRequest(method, Region.US_WEST_1);

      assertRequestLineEquals(httpMethod, "POST https://ec2.us-west-1.amazonaws.com/ HTTP/1.1");
      assertHeadersEqual(
               httpMethod,
               "Content-Length: 51\nContent-Type: application/x-www-form-urlencoded\nHost: ec2.us-west-1.amazonaws.com\n");
      assertPayloadEquals(httpMethod, "Version=2009-11-30&Action=DescribeAvailabilityZones");

      assertResponseParserClassEquals(method, httpMethod, ParseSax.class);
      assertSaxResponseParserClassEquals(method, DescribeAvailabilityZonesResponseHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpMethod);
   }

   public void testDescribeAvailabilityZonesOptions() throws SecurityException,
            NoSuchMethodException, IOException {
      Method method = AvailabilityZoneAndRegionAsyncClient.class.getMethod(
               "describeAvailabilityZonesInRegion", Region.class, Array.newInstance(
                        DescribeAvailabilityZonesOptions.class, 0).getClass());
      GeneratedHttpRequest<AvailabilityZoneAndRegionAsyncClient> httpMethod = processor
               .createRequest(method, Region.US_EAST_1, availabilityZones(
                        AvailabilityZone.US_EAST_1A, AvailabilityZone.US_EAST_1B));

      assertRequestLineEquals(httpMethod, "POST https://ec2.us-east-1.amazonaws.com/ HTTP/1.1");
      assertHeadersEqual(
               httpMethod,
               "Content-Length: 95\nContent-Type: application/x-www-form-urlencoded\nHost: ec2.us-east-1.amazonaws.com\n");
      assertPayloadEquals(httpMethod,
               "Version=2009-11-30&Action=DescribeAvailabilityZones&ZoneName.1=us-east-1a&ZoneName.2=us-east-1b");

      assertResponseParserClassEquals(method, httpMethod, ParseSax.class);
      assertSaxResponseParserClassEquals(method, DescribeAvailabilityZonesResponseHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpMethod);
   }

   public void testDescribeRegions() throws SecurityException, NoSuchMethodException, IOException {
      Method method = AvailabilityZoneAndRegionAsyncClient.class.getMethod("describeRegions", Array
               .newInstance(DescribeRegionsOptions.class, 0).getClass());
      GeneratedHttpRequest<AvailabilityZoneAndRegionAsyncClient> httpMethod = processor
               .createRequest(method);

      assertRequestLineEquals(httpMethod, "POST https://ec2.amazonaws.com/ HTTP/1.1");
      assertHeadersEqual(httpMethod,
               "Content-Length: 41\nContent-Type: application/x-www-form-urlencoded\nHost: ec2.amazonaws.com\n");
      assertPayloadEquals(httpMethod, "Version=2009-11-30&Action=DescribeRegions");

      assertResponseParserClassEquals(method, httpMethod, ParseSax.class);
      assertSaxResponseParserClassEquals(method, DescribeRegionsResponseHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpMethod);
   }

   public void testDescribeRegionsOptions() throws SecurityException, NoSuchMethodException,
            IOException {
      Method method = AvailabilityZoneAndRegionAsyncClient.class.getMethod("describeRegions", Array
               .newInstance(DescribeRegionsOptions.class, 0).getClass());
      GeneratedHttpRequest<AvailabilityZoneAndRegionAsyncClient> httpMethod = processor
               .createRequest(method, regions(Region.US_EAST_1, Region.US_WEST_1));

      assertRequestLineEquals(httpMethod, "POST https://ec2.amazonaws.com/ HTTP/1.1");
      assertHeadersEqual(httpMethod,
               "Content-Length: 87\nContent-Type: application/x-www-form-urlencoded\nHost: ec2.amazonaws.com\n");
      assertPayloadEquals(httpMethod,
               "Version=2009-11-30&Action=DescribeRegions&RegionName.1=us-east-1&RegionName.2=us-west-1");

      assertResponseParserClassEquals(method, httpMethod, ParseSax.class);
      assertSaxResponseParserClassEquals(method, DescribeRegionsResponseHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpMethod);
   }

   @Override
   protected void checkFilters(GeneratedHttpRequest<AvailabilityZoneAndRegionAsyncClient> httpMethod) {
      assertEquals(httpMethod.getFilters().size(), 1);
      assertEquals(httpMethod.getFilters().get(0).getClass(), FormSigner.class);
   }

   @Override
   protected TypeLiteral<RestAnnotationProcessor<AvailabilityZoneAndRegionAsyncClient>> createTypeLiteral() {
      return new TypeLiteral<RestAnnotationProcessor<AvailabilityZoneAndRegionAsyncClient>>() {
      };
   }

   @Override
   protected Module createModule() {
      return new AbstractModule() {
         @Override
         protected void configure() {
            bind(URI.class).annotatedWith(EC2.class).toInstance(
                     URI.create("https://ec2.amazonaws.com"));
            bindConstant().annotatedWith(Jsr330.named(AWSConstants.PROPERTY_AWS_ACCESSKEYID)).to(
                     "user");
            bindConstant().annotatedWith(Jsr330.named(AWSConstants.PROPERTY_AWS_SECRETACCESSKEY))
                     .to("key");
            bind(Logger.LoggerFactory.class).toInstance(new LoggerFactory() {
               public Logger getLogger(String category) {
                  return Logger.NULL;
               }
            });
         }

         @SuppressWarnings("unused")
         @Provides
         @TimeStamp
         String provide() {
            return "2009-11-08T15:54:08.897Z";
         }

         @SuppressWarnings("unused")
         @Singleton
         @Provides
         Map<Region, URI> provideMap() {
            return ImmutableMap.<Region, URI> of(Region.DEFAULT, URI.create("https://booya"),
                     Region.EU_WEST_1, URI.create("https://ec2.eu-west-1.amazonaws.com"),
                     Region.US_EAST_1, URI.create("https://ec2.us-east-1.amazonaws.com"),
                     Region.US_WEST_1, URI.create("https://ec2.us-west-1.amazonaws.com"));
         }
      };
   }
}
