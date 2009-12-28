/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
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
 * ====================================================================
 */
package org.jclouds.aws.ec2.services;

import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.Map;

import javax.inject.Singleton;

import org.jclouds.aws.ec2.EC2;
import org.jclouds.aws.ec2.domain.AvailabilityZone;
import org.jclouds.aws.ec2.domain.Region;
import org.jclouds.aws.ec2.filters.FormSigner;
import org.jclouds.aws.ec2.options.RunInstancesOptions;
import org.jclouds.aws.ec2.xml.DescribeInstancesResponseHandler;
import org.jclouds.aws.ec2.xml.RunInstancesResponseHandler;
import org.jclouds.aws.ec2.xml.TerminateInstancesResponseHandler;
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
 * Tests behavior of {@code InstanceAsyncClient}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "ec2.InstanceAsyncClientTest")
public class InstanceAsyncClientTest extends RestClientTest<InstanceAsyncClient> {

   public void testDescribeInstances() throws SecurityException, NoSuchMethodException, IOException {
      Method method = InstanceAsyncClient.class.getMethod("describeInstancesInRegion",
               Region.class, Array.newInstance(String.class, 0).getClass());
      GeneratedHttpRequest<InstanceAsyncClient> httpMethod = processor.createRequest(method,
               Region.DEFAULT);

      assertRequestLineEquals(httpMethod, "POST https://ec2.amazonaws.com/ HTTP/1.1");
      assertHeadersEqual(httpMethod,
               "Content-Length: 43\nContent-Type: application/x-www-form-urlencoded\nHost: ec2.amazonaws.com\n");
      assertPayloadEquals(httpMethod, "Version=2009-11-30&Action=DescribeInstances");

      assertResponseParserClassEquals(method, httpMethod, ParseSax.class);
      assertSaxResponseParserClassEquals(method, DescribeInstancesResponseHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpMethod);
   }

   public void testDescribeInstancesArgs() throws SecurityException, NoSuchMethodException,
            IOException {
      Method method = InstanceAsyncClient.class.getMethod("describeInstancesInRegion",
               Region.class, Array.newInstance(String.class, 0).getClass());
      GeneratedHttpRequest<InstanceAsyncClient> httpMethod = processor.createRequest(method,
               Region.DEFAULT, "1", "2");

      assertRequestLineEquals(httpMethod, "POST https://ec2.amazonaws.com/ HTTP/1.1");
      assertHeadersEqual(httpMethod,
               "Content-Length: 43\nContent-Type: application/x-www-form-urlencoded\nHost: ec2.amazonaws.com\n");
      assertPayloadEquals(httpMethod,
               "Version=2009-11-30&Action=DescribeInstances&InstanceId.1=1&InstanceId.2=2");

      assertResponseParserClassEquals(method, httpMethod, ParseSax.class);
      assertSaxResponseParserClassEquals(method, DescribeInstancesResponseHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpMethod);
   }

   public void testTerminateInstances() throws SecurityException, NoSuchMethodException,
            IOException {
      Method method = InstanceAsyncClient.class.getMethod("terminateInstancesInRegion",
               Region.class, String.class, Array.newInstance(String.class, 0).getClass());
      GeneratedHttpRequest<InstanceAsyncClient> httpMethod = processor.createRequest(method,
               Region.DEFAULT, "1", "2");

      assertRequestLineEquals(httpMethod, "POST https://ec2.amazonaws.com/ HTTP/1.1");
      assertHeadersEqual(httpMethod,
               "Content-Length: 59\nContent-Type: application/x-www-form-urlencoded\nHost: ec2.amazonaws.com\n");
      assertPayloadEquals(httpMethod,
               "Version=2009-11-30&Action=TerminateInstances&InstanceId.0=1&InstanceId.1=2");

      assertResponseParserClassEquals(method, httpMethod, ParseSax.class);
      assertSaxResponseParserClassEquals(method, TerminateInstancesResponseHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpMethod);
   }

   public void testRunInstances() throws SecurityException, NoSuchMethodException, IOException {
      Method method = InstanceAsyncClient.class.getMethod("runInstancesInRegion", Region.class,
               AvailabilityZone.class, String.class, int.class, int.class, Array.newInstance(
                        RunInstancesOptions.class, 0).getClass());
      GeneratedHttpRequest<InstanceAsyncClient> httpMethod = processor.createRequest(method,
               Region.DEFAULT, null, "ami-voo", 1, 1);

      assertRequestLineEquals(httpMethod, "POST https://ec2.amazonaws.com/ HTTP/1.1");
      assertHeadersEqual(httpMethod,
               "Content-Length: 76\nContent-Type: application/x-www-form-urlencoded\nHost: ec2.amazonaws.com\n");
      assertPayloadEquals(httpMethod,
               "Version=2009-11-30&Action=RunInstances&ImageId=ami-voo&MinCount=1&MaxCount=1");

      assertResponseParserClassEquals(method, httpMethod, ParseSax.class);
      assertSaxResponseParserClassEquals(method, RunInstancesResponseHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpMethod);
   }

   public void testRunInstancesOptions() throws SecurityException, NoSuchMethodException,
            IOException {
      Method method = InstanceAsyncClient.class.getMethod("runInstancesInRegion", Region.class,
               AvailabilityZone.class, String.class, int.class, int.class, Array.newInstance(
                        RunInstancesOptions.class, 0).getClass());
      GeneratedHttpRequest<InstanceAsyncClient> httpMethod = processor.createRequest(method,
               Region.EU_WEST_1, AvailabilityZone.EU_WEST_1A, "ami-voo", 1, 5,
               new RunInstancesOptions().withKernelId("kernelId").enableMonitoring());

      assertRequestLineEquals(httpMethod, "POST https://ec2.eu-west-1.amazonaws.com/ HTTP/1.1");
      assertHeadersEqual(
               httpMethod,
               "Content-Length: 118\nContent-Type: application/x-www-form-urlencoded\nHost: ec2.eu-west-1.amazonaws.com\n");
      assertPayloadEquals(
               httpMethod,
               "Version=2009-11-30&Action=RunInstances&ImageId=ami-voo&MinCount=1&MaxCount=5&KernelId=kernelId&Monitoring.Enabled=true&Placement.AvailabilityZone=eu-west-1a");

      assertResponseParserClassEquals(method, httpMethod, ParseSax.class);
      assertSaxResponseParserClassEquals(method, RunInstancesResponseHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpMethod);
   }

   @Override
   protected void checkFilters(GeneratedHttpRequest<InstanceAsyncClient> httpMethod) {
      assertEquals(httpMethod.getFilters().size(), 1);
      assertEquals(httpMethod.getFilters().get(0).getClass(), FormSigner.class);
   }

   @Override
   protected TypeLiteral<RestAnnotationProcessor<InstanceAsyncClient>> createTypeLiteral() {
      return new TypeLiteral<RestAnnotationProcessor<InstanceAsyncClient>>() {
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
