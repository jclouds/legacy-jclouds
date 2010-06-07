/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.ibmdev;

import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.lang.reflect.Method;

import org.jclouds.http.filters.BasicAuthentication;
import org.jclouds.http.functions.CloseContentAndReturn;
import org.jclouds.ibmdev.config.IBMDeveloperCloudRestClientModule;
import org.jclouds.ibmdev.domain.Image;
import org.jclouds.ibmdev.functions.ParseImageFromJson;
import org.jclouds.ibmdev.functions.ParseImagesFromJson;
import org.jclouds.ibmdev.functions.ParseInstanceFromJson;
import org.jclouds.ibmdev.functions.ParseInstancesFromJson;
import org.jclouds.logging.config.NullLoggingModule;
import org.jclouds.rest.RestClientTest;
import org.jclouds.rest.functions.ReturnNullOnNotFoundOr404;
import org.jclouds.rest.functions.ReturnVoidOnNotFoundOr404;
import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.jclouds.rest.internal.RestAnnotationProcessor;
import org.testng.annotations.Test;

import com.google.common.collect.Iterables;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;

/**
 * Tests annotation parsing of {@code IBMDeveloperCloudAsyncClient}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "ibmdevelopercloud.IBMDeveloperCloudAsyncClientTest")
public class IBMDeveloperCloudAsyncClientTest extends RestClientTest<IBMDeveloperCloudAsyncClient> {

   public void testListImages() throws SecurityException, NoSuchMethodException, IOException {
      Method method = IBMDeveloperCloudAsyncClient.class.getMethod("listImages");
      GeneratedHttpRequest<IBMDeveloperCloudAsyncClient> httpRequest = processor
               .createRequest(method);

      assertRequestLineEquals(httpRequest,
               "GET https://www-180.ibm.com/cloud/enterprise/beta/api/rest/20090403/images HTTP/1.1");
      assertHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null);

      // now make sure request filters apply by replaying
      Iterables.getOnlyElement(httpRequest.getFilters()).filter(httpRequest);
      Iterables.getOnlyElement(httpRequest.getFilters()).filter(httpRequest);

      assertRequestLineEquals(httpRequest,
               "GET https://www-180.ibm.com/cloud/enterprise/beta/api/rest/20090403/images HTTP/1.1");
      // for example, using basic authentication, we should get "only one" header
      assertHeadersEqual(httpRequest,
               "Accept: application/json\nAuthorization: Basic Zm9vOmJhcg==\n");
      assertPayloadEquals(httpRequest, null);

      assertResponseParserClassEquals(method, httpRequest, ParseImagesFromJson.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpRequest);

   }

   public void testGetImage() throws SecurityException, NoSuchMethodException, IOException {
      Method method = IBMDeveloperCloudAsyncClient.class.getMethod("getImage", long.class);
      GeneratedHttpRequest<IBMDeveloperCloudAsyncClient> httpRequest = processor.createRequest(
               method, 1);

      assertRequestLineEquals(httpRequest,
               "GET https://www-180.ibm.com/cloud/enterprise/beta/api/rest/20090403/images/1 HTTP/1.1");
      assertHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null);

      assertResponseParserClassEquals(method, httpRequest, ParseImageFromJson.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnNullOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }

   public void testDeleteImage() throws SecurityException, NoSuchMethodException, IOException {
      Method method = IBMDeveloperCloudAsyncClient.class.getMethod("deleteImage", long.class);
      GeneratedHttpRequest<IBMDeveloperCloudAsyncClient> httpRequest = processor.createRequest(
               method, 1);

      assertRequestLineEquals(httpRequest,
               "DELETE https://www-180.ibm.com/cloud/enterprise/beta/api/rest/20090403/images/1 HTTP/1.1");
      assertHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null);

      assertResponseParserClassEquals(method, httpRequest, CloseContentAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnVoidOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }

   public void testSetImageVisibility() throws SecurityException, NoSuchMethodException,
            IOException {
      Method method = IBMDeveloperCloudAsyncClient.class.getMethod("setImageVisibility",
               long.class, Image.Visibility.class);
      GeneratedHttpRequest<IBMDeveloperCloudAsyncClient> httpRequest = processor.createRequest(
               method, 1, Image.Visibility.PUBLIC);

      assertRequestLineEquals(httpRequest,
               "PUT https://www-180.ibm.com/cloud/enterprise/beta/api/rest/20090403/images/1 HTTP/1.1");
      assertHeadersEqual(httpRequest,
               "Accept: application/json\nContent-Length: 23\nContent-Type: application/json\n");
      assertPayloadEquals(httpRequest, "{\"visibility\":\"PUBLIC\"}");

      assertResponseParserClassEquals(method, httpRequest, ParseImageFromJson.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnNullOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }

   public void testListInstances() throws SecurityException, NoSuchMethodException, IOException {
      Method method = IBMDeveloperCloudAsyncClient.class.getMethod("listInstances");
      GeneratedHttpRequest<IBMDeveloperCloudAsyncClient> httpRequest = processor
               .createRequest(method);

      assertRequestLineEquals(httpRequest,
               "GET https://www-180.ibm.com/cloud/enterprise/beta/api/rest/20090403/instances HTTP/1.1");
      assertHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null);

      assertResponseParserClassEquals(method, httpRequest, ParseInstancesFromJson.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpRequest);

   }

   public void testGetInstance() throws SecurityException, NoSuchMethodException, IOException {
      Method method = IBMDeveloperCloudAsyncClient.class.getMethod("getInstance", long.class);
      GeneratedHttpRequest<IBMDeveloperCloudAsyncClient> httpRequest = processor.createRequest(
               method, 1);

      assertRequestLineEquals(httpRequest,
               "GET https://www-180.ibm.com/cloud/enterprise/beta/api/rest/20090403/instances/1 HTTP/1.1");
      assertHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null);

      assertResponseParserClassEquals(method, httpRequest, ParseInstanceFromJson.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnNullOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }

   public void testDeleteInstance() throws SecurityException, NoSuchMethodException, IOException {
      Method method = IBMDeveloperCloudAsyncClient.class.getMethod("deleteInstance", long.class);
      GeneratedHttpRequest<IBMDeveloperCloudAsyncClient> httpRequest = processor.createRequest(
               method, 1);

      assertRequestLineEquals(httpRequest,
               "DELETE https://www-180.ibm.com/cloud/enterprise/beta/api/rest/20090403/instances/1 HTTP/1.1");
      assertHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null);

      assertResponseParserClassEquals(method, httpRequest, CloseContentAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnVoidOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }

   @Override
   protected void checkFilters(GeneratedHttpRequest<IBMDeveloperCloudAsyncClient> httpRequest) {
      assertEquals(httpRequest.getFilters().size(), 1);
      assertEquals(httpRequest.getFilters().get(0).getClass(), BasicAuthentication.class);
   }

   @Override
   protected TypeLiteral<RestAnnotationProcessor<IBMDeveloperCloudAsyncClient>> createTypeLiteral() {
      return new TypeLiteral<RestAnnotationProcessor<IBMDeveloperCloudAsyncClient>>() {
      };
   }

   @Override
   protected Module createModule() {
      return new IBMDeveloperCloudRestClientModule() {
         @Override
         protected void configure() {
            Names.bindProperties(binder(), new IBMDeveloperCloudPropertiesBuilder("foo", "bar")
                     .build());
            install(new NullLoggingModule());
            super.configure();
         }

      };
   }
}
