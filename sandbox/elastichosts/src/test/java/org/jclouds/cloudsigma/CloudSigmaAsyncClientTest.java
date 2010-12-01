/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
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

package org.jclouds.cloudsigma;

import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Properties;

import org.jclouds.cloudsigma.functions.KeyValuesDelimitedByBlankLinesToDriveInfo;
import org.jclouds.cloudsigma.functions.ListOfKeyValuesDelimitedByBlankLinesToDriveInfoSet;
import org.jclouds.elastichosts.domain.CreateDriveRequest;
import org.jclouds.elastichosts.domain.DriveData;
import org.jclouds.elastichosts.functions.SplitNewlines;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.filters.BasicAuthentication;
import org.jclouds.rest.RestClientTest;
import org.jclouds.rest.RestContextFactory;
import org.jclouds.rest.RestContextSpec;
import org.jclouds.rest.functions.ReturnNullOnNotFoundOr404;
import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.jclouds.rest.internal.RestAnnotationProcessor;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.google.inject.TypeLiteral;

/**
 * Tests annotation parsing of {@code CloudSigmaAsyncClient}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "cloudsigma.CloudSigmaAsyncClientTest")
public class CloudSigmaAsyncClientTest extends RestClientTest<CloudSigmaAsyncClient> {

   public void testListStandardDrives() throws SecurityException, NoSuchMethodException, IOException {
      Method method = CloudSigmaAsyncClient.class.getMethod("listStandardDrives");
      GeneratedHttpRequest<CloudSigmaAsyncClient> httpRequest = processor.createRequest(method);

      assertRequestLineEquals(httpRequest, "GET https://api.cloudsigma.com/drives/standard/list HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: text/plain\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, SplitNewlines.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpRequest);
   }

   public void testListStandardCds() throws SecurityException, NoSuchMethodException, IOException {
      Method method = CloudSigmaAsyncClient.class.getMethod("listStandardCds");
      GeneratedHttpRequest<CloudSigmaAsyncClient> httpRequest = processor.createRequest(method);

      assertRequestLineEquals(httpRequest, "GET https://api.cloudsigma.com/drives/standard/cd/list HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: text/plain\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, SplitNewlines.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpRequest);
   }

   public void testListStandardImages() throws SecurityException, NoSuchMethodException, IOException {
      Method method = CloudSigmaAsyncClient.class.getMethod("listStandardImages");
      GeneratedHttpRequest<CloudSigmaAsyncClient> httpRequest = processor.createRequest(method);

      assertRequestLineEquals(httpRequest, "GET https://api.cloudsigma.com/drives/standard/img/list HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: text/plain\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, SplitNewlines.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpRequest);
   }

   public void testListDriveInfo() throws SecurityException, NoSuchMethodException, IOException {
      Method method = CloudSigmaAsyncClient.class.getMethod("listDriveInfo");
      GeneratedHttpRequest<CloudSigmaAsyncClient> httpRequest = processor.createRequest(method);

      assertRequestLineEquals(httpRequest, "GET https://api.cloudsigma.com/drives/info HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: text/plain\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ListOfKeyValuesDelimitedByBlankLinesToDriveInfoSet.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpRequest);
   }

   public void testGetDriveInfo() throws SecurityException, NoSuchMethodException, IOException {
      Method method = CloudSigmaAsyncClient.class.getMethod("getDriveInfo", String.class);
      GeneratedHttpRequest<CloudSigmaAsyncClient> httpRequest = processor.createRequest(method, "uuid");

      assertRequestLineEquals(httpRequest, "GET https://api.cloudsigma.com/drives/uuid/info HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: text/plain\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, KeyValuesDelimitedByBlankLinesToDriveInfo.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnNullOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }

   public void testCreateDrive() throws SecurityException, NoSuchMethodException, IOException {
      Method method = CloudSigmaAsyncClient.class.getMethod("createDrive", CreateDriveRequest.class);
      GeneratedHttpRequest<CloudSigmaAsyncClient> httpRequest = processor.createRequest(method,
            new CreateDriveRequest.Builder().name("foo").tags(ImmutableList.of("production", "candy")).size(10000l).build());

      assertRequestLineEquals(httpRequest, "POST https://api.cloudsigma.com/drives/create HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: text/plain\n");
      assertPayloadEquals(httpRequest, "name foo\nsize 10000\nuse production,candy", "text/plain", false);

      assertResponseParserClassEquals(method, httpRequest, KeyValuesDelimitedByBlankLinesToDriveInfo.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnNullOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }

   public void testSetDriveData() throws SecurityException, NoSuchMethodException, IOException {
      Method method = CloudSigmaAsyncClient.class.getMethod("setDriveData", String.class, DriveData.class);
      GeneratedHttpRequest<CloudSigmaAsyncClient> httpRequest = processor.createRequest(method, "100",
            new DriveData.Builder().name("foo").size(10000l).tags(ImmutableList.of("production", "candy")).build());

      assertRequestLineEquals(httpRequest, "POST https://api.cloudsigma.com/drives/100/set HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: text/plain\n");
      assertPayloadEquals(httpRequest, "name foo\nsize 10000\nuse production,candy", "text/plain", false);

      assertResponseParserClassEquals(method, httpRequest, KeyValuesDelimitedByBlankLinesToDriveInfo.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnNullOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }

   @Override
   protected void checkFilters(HttpRequest request) {
      assertEquals(request.getFilters().size(), 1);
      assertEquals(request.getFilters().get(0).getClass(), BasicAuthentication.class);
   }

   @Override
   protected TypeLiteral<RestAnnotationProcessor<CloudSigmaAsyncClient>> createTypeLiteral() {
      return new TypeLiteral<RestAnnotationProcessor<CloudSigmaAsyncClient>>() {
      };
   }

   @Override
   public RestContextSpec<CloudSigmaClient, CloudSigmaAsyncClient> createContextSpec() {
      return new RestContextFactory().createContextSpec("cloudsigma", "foo", "bar", new Properties());
   }
}
