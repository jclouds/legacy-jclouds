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

import static org.jclouds.aws.ec2.options.DescribeImagesOptions.Builder.executableBy;
import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.net.URI;

import org.jclouds.aws.ec2.EC2;
import org.jclouds.aws.ec2.domain.ImageAttribute;
import org.jclouds.aws.ec2.filters.FormSigner;
import org.jclouds.aws.ec2.options.CreateImageOptions;
import org.jclouds.aws.ec2.options.DescribeImagesOptions;
import org.jclouds.aws.ec2.xml.DescribeImagesResponseHandler;
import org.jclouds.aws.reference.AWSConstants;
import org.jclouds.date.TimeStamp;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.http.functions.ReturnStringIf200;
import org.jclouds.logging.Logger;
import org.jclouds.logging.Logger.LoggerFactory;
import org.jclouds.rest.RestClientTest;
import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.jclouds.rest.internal.RestAnnotationProcessor;
import org.jclouds.util.Jsr330;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Module;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;

/**
 * Tests behavior of {@code AMIAsyncClient}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "ec2.AMIAsyncClientTest")
public class AMIAsyncClientTest extends RestClientTest<AMIAsyncClient> {

   public void testCreateImage() throws SecurityException, NoSuchMethodException, IOException {
      Method method = AMIAsyncClient.class.getMethod("createImage", String.class, String.class,
               Array.newInstance(CreateImageOptions.class, 0).getClass());
      GeneratedHttpRequest<AMIAsyncClient> httpMethod = processor.createRequest(method, "name",
               "instanceId");

      assertRequestLineEquals(httpMethod, "POST https://ec2.amazonaws.com/ HTTP/1.1");
      assertHeadersEqual(httpMethod,
               "Content-Length: 69\nContent-Type: application/x-www-form-urlencoded\nHost: ec2.amazonaws.com\n");
      assertPayloadEquals(httpMethod,
               "Version=2009-11-30&Action=CreateImage&Name=name&InstanceId=instanceId");
      filter.filter(httpMethod);
      assertPayloadEquals(
               httpMethod,
               "Action=CreateImage&InstanceId=instanceId&Name=name&Signature=DPCvwvxdNmWXHfiIB%2BRy%2F4gJDAruu6i8dQVirzkFGOU%3D&SignatureMethod=HmacSHA256&SignatureVersion=2&Timestamp=2009-11-08T15%3A54%3A08.897Z&Version=2009-11-30&AWSAccessKeyId=user");

      assertResponseParserClassEquals(method, httpMethod, ReturnStringIf200.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpMethod);
   }

   public void testCreateImageOptions() throws SecurityException, NoSuchMethodException,
            IOException {
      Method method = AMIAsyncClient.class.getMethod("createImage", String.class, String.class,
               Array.newInstance(CreateImageOptions.class, 0).getClass());
      GeneratedHttpRequest<AMIAsyncClient> httpMethod = processor.createRequest(method, "name",
               "instanceId", new CreateImageOptions().withDescription("description").noReboot());

      assertRequestLineEquals(httpMethod, "POST https://ec2.amazonaws.com/ HTTP/1.1");
      assertHeadersEqual(httpMethod,
               "Content-Length: 107\nContent-Type: application/x-www-form-urlencoded\nHost: ec2.amazonaws.com\n");
      assertPayloadEquals(
               httpMethod,
               "Version=2009-11-30&Action=CreateImage&Name=name&InstanceId=instanceId&Description=description&NoReboot=true");

      assertResponseParserClassEquals(method, httpMethod, ReturnStringIf200.class);
      assertSaxResponseParserClassEquals(method, null);
      // assertResponseParserClassEquals(method, httpMethod, ParseSax.class);
      // assertSaxResponseParserClassEquals(method, CreateImageResponseHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpMethod);
   }

   public void testDescribeImages() throws SecurityException, NoSuchMethodException, IOException {
      Method method = AMIAsyncClient.class.getMethod("describeImages", Array.newInstance(
               DescribeImagesOptions.class, 0).getClass());
      GeneratedHttpRequest<AMIAsyncClient> httpMethod = processor.createRequest(method);

      assertRequestLineEquals(httpMethod, "POST https://ec2.amazonaws.com/ HTTP/1.1");
      assertHeadersEqual(httpMethod,
               "Content-Length: 40\nContent-Type: application/x-www-form-urlencoded\nHost: ec2.amazonaws.com\n");
      assertPayloadEquals(httpMethod, "Version=2009-11-30&Action=DescribeImages");
      filter.filter(httpMethod);
      assertPayloadEquals(
               httpMethod,
               "Action=DescribeImages&Signature=z1UAagWh%2BypA%2BR66ZAOvJJm5uQcBzGDVcbVeMilfioU%3D&SignatureMethod=HmacSHA256&SignatureVersion=2&Timestamp=2009-11-08T15%3A54%3A08.897Z&Version=2009-11-30&AWSAccessKeyId=user");

      assertResponseParserClassEquals(method, httpMethod, ParseSax.class);
      assertSaxResponseParserClassEquals(method, DescribeImagesResponseHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpMethod);
   }

   public void testDescribeImagesOptions() throws SecurityException, NoSuchMethodException,
            IOException {
      Method method = AMIAsyncClient.class.getMethod("describeImages", Array.newInstance(
               DescribeImagesOptions.class, 0).getClass());
      GeneratedHttpRequest<AMIAsyncClient> httpMethod = processor.createRequest(method,
               executableBy("me").ownedBy("fred", "nancy").imageIds("1", "2"));

      assertRequestLineEquals(httpMethod, "POST https://ec2.amazonaws.com/ HTTP/1.1");
      assertHeadersEqual(httpMethod,
               "Content-Length: 107\nContent-Type: application/x-www-form-urlencoded\nHost: ec2.amazonaws.com\n");
      assertPayloadEquals(
               httpMethod,
               "Version=2009-11-30&Action=DescribeImages&ExecutableBy=me&Owner.1=fred&Owner.2=nancy&ImageId.1=1&ImageId.2=2");

      assertResponseParserClassEquals(method, httpMethod, ParseSax.class);
      assertSaxResponseParserClassEquals(method, DescribeImagesResponseHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpMethod);
   }

   public void testDescribeImageAttribute() throws SecurityException, NoSuchMethodException,
            IOException {
      Method method = AMIAsyncClient.class.getMethod("describeImageAttribute", String.class,
               ImageAttribute.class);
      GeneratedHttpRequest<AMIAsyncClient> httpMethod = processor.createRequest(method, "imageId",
               ImageAttribute.BLOCK_DEVICE_MAPPING);

      assertRequestLineEquals(httpMethod, "POST https://ec2.amazonaws.com/ HTTP/1.1");
      assertHeadersEqual(httpMethod,
               "Content-Length: 93\nContent-Type: application/x-www-form-urlencoded\nHost: ec2.amazonaws.com\n");
      assertPayloadEquals(httpMethod,
               "Version=2009-11-30&Action=DescribeImageAttribute&ImageId=imageId&Attribute=blockDeviceMapping");
      filter.filter(httpMethod);
      assertPayloadEquals(
               httpMethod,
               "Action=DescribeImageAttribute&Attribute=blockDeviceMapping&ImageId=imageId&Signature=A1%2BcJegB6Et36%2BGC%2FEJ0puDJljBY9oeF2YYsH%2FDIxUA%3D&SignatureMethod=HmacSHA256&SignatureVersion=2&Timestamp=2009-11-08T15%3A54%3A08.897Z&Version=2009-11-30&AWSAccessKeyId=user");

      assertResponseParserClassEquals(method, httpMethod, ReturnStringIf200.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpMethod);
   }

   @Override
   protected void checkFilters(GeneratedHttpRequest<AMIAsyncClient> httpMethod) {
      assertEquals(httpMethod.getFilters().size(), 1);
      assertEquals(httpMethod.getFilters().get(0).getClass(), FormSigner.class);
   }

   @Override
   protected TypeLiteral<RestAnnotationProcessor<AMIAsyncClient>> createTypeLiteral() {
      return new TypeLiteral<RestAnnotationProcessor<AMIAsyncClient>>() {
      };
   }

   private FormSigner filter;

   @Override
   @BeforeTest
   protected void setupFactory() {
      super.setupFactory();
      this.filter = injector.getInstance(FormSigner.class);
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
      };
   }
}
