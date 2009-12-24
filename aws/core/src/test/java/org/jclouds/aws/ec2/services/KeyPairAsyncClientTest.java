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

import org.jclouds.aws.ec2.EC2;
import org.jclouds.aws.ec2.filters.FormSigner;
import org.jclouds.aws.ec2.xml.DescribeKeyPairsResponseHandler;
import org.jclouds.aws.reference.AWSConstants;
import org.jclouds.date.TimeStamp;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.http.functions.ReturnVoidIf2xx;
import org.jclouds.logging.Logger;
import org.jclouds.logging.Logger.LoggerFactory;
import org.jclouds.rest.RestClientTest;
import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.jclouds.rest.internal.RestAnnotationProcessor;
import org.jclouds.util.Jsr330;
import org.testng.annotations.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Module;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;

/**
 * Tests behavior of {@code KeyPairAsyncClient}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "ec2.KeyPairAsyncClientTest")
public class KeyPairAsyncClientTest extends RestClientTest<KeyPairAsyncClient> {

   public void testDeleteKeyPair() throws SecurityException, NoSuchMethodException, IOException {
      Method method = KeyPairAsyncClient.class.getMethod("deleteKeyPair", String.class);
      GeneratedHttpRequest<KeyPairAsyncClient> httpMethod = processor
               .createRequest(method, "mykey");

      assertRequestLineEquals(httpMethod, "POST https://ec2.amazonaws.com/ HTTP/1.1");
      assertHeadersEqual(httpMethod,
               "Content-Length: 53\nContent-Type: application/x-www-form-urlencoded\nHost: ec2.amazonaws.com\n");
      assertPayloadEquals(httpMethod, "Version=2009-11-30&Action=DeleteKeyPair&KeyName=mykey");

      assertResponseParserClassEquals(method, httpMethod, ReturnVoidIf2xx.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpMethod);
   }

   public void testDescribeKeyPairs() throws SecurityException, NoSuchMethodException, IOException {
      Method method = KeyPairAsyncClient.class.getMethod("describeKeyPairs", Array.newInstance(
               String.class, 0).getClass());
      GeneratedHttpRequest<KeyPairAsyncClient> httpMethod = processor.createRequest(method);

      assertRequestLineEquals(httpMethod, "POST https://ec2.amazonaws.com/ HTTP/1.1");
      assertHeadersEqual(httpMethod,
               "Content-Length: 42\nContent-Type: application/x-www-form-urlencoded\nHost: ec2.amazonaws.com\n");
      assertPayloadEquals(httpMethod, "Version=2009-11-30&Action=DescribeKeyPairs");

      assertResponseParserClassEquals(method, httpMethod, ParseSax.class);
      assertSaxResponseParserClassEquals(method, DescribeKeyPairsResponseHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpMethod);
   }

   public void testDescribeKeyPairsArgs() throws SecurityException, NoSuchMethodException,
            IOException {
      Method method = KeyPairAsyncClient.class.getMethod("describeKeyPairs", Array.newInstance(
               String.class, 0).getClass());
      GeneratedHttpRequest<KeyPairAsyncClient> httpMethod = processor.createRequest(method, "1",
               "2");

      assertRequestLineEquals(httpMethod, "POST https://ec2.amazonaws.com/ HTTP/1.1");
      assertHeadersEqual(httpMethod,
               "Content-Length: 42\nContent-Type: application/x-www-form-urlencoded\nHost: ec2.amazonaws.com\n");
      assertPayloadEquals(httpMethod,
               "Version=2009-11-30&Action=DescribeKeyPairs&KeyName.1=1&KeyName.2=2");

      assertResponseParserClassEquals(method, httpMethod, ParseSax.class);
      assertSaxResponseParserClassEquals(method, DescribeKeyPairsResponseHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpMethod);
   }

   @Override
   protected void checkFilters(GeneratedHttpRequest<KeyPairAsyncClient> httpMethod) {
      assertEquals(httpMethod.getFilters().size(), 1);
      assertEquals(httpMethod.getFilters().get(0).getClass(), FormSigner.class);
   }

   @Override
   protected TypeLiteral<RestAnnotationProcessor<KeyPairAsyncClient>> createTypeLiteral() {
      return new TypeLiteral<RestAnnotationProcessor<KeyPairAsyncClient>>() {
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
      };
   }
}
