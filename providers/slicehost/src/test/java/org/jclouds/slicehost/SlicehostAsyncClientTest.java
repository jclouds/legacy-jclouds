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

package org.jclouds.slicehost;

import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Properties;

import org.jclouds.http.HttpRequest;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.http.functions.ReleasePayloadAndReturn;
import org.jclouds.rest.RestClientTest;
import org.jclouds.rest.RestContextFactory;
import org.jclouds.rest.RestContextSpec;
import org.jclouds.rest.functions.ReturnEmptySetOnNotFoundOr404;
import org.jclouds.rest.functions.ReturnNullOnNotFoundOr404;
import org.jclouds.rest.functions.ReturnVoidOnNotFoundOr404;
import org.jclouds.rest.internal.RestAnnotationProcessor;
import org.jclouds.slicehost.filters.SlicehostBasic;
import org.jclouds.slicehost.xml.FlavorHandler;
import org.jclouds.slicehost.xml.FlavorsHandler;
import org.jclouds.slicehost.xml.ImageHandler;
import org.jclouds.slicehost.xml.ImagesHandler;
import org.jclouds.slicehost.xml.SliceHandler;
import org.jclouds.slicehost.xml.SlicesHandler;
import org.testng.annotations.Test;

import com.google.inject.TypeLiteral;

/**
 * Tests behavior of {@code SlicehostAsyncClient}
 * 
 * @author Adrian Cole
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during surefire
@Test(groups = "unit", testName = "SlicehostAsyncClientTest")
public class SlicehostAsyncClientTest extends RestClientTest<SlicehostAsyncClient> {

   public void testCreateSlice() throws IOException, SecurityException, NoSuchMethodException {
      Method method = SlicehostAsyncClient.class.getMethod("createSlice", String.class, int.class, int.class);
      HttpRequest request = processor.createRequest(method, "ralphie", 2, 1);

      assertRequestLineEquals(request, "POST https://api.slicehost.com/slices.xml HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "");
      assertPayloadEquals(
            request,
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?><slice><flavor-id type=\"integer\">1</flavor-id><image-id type=\"integer\">2</image-id><name>ralphie</name></slice>",
            "application/xml", false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, SliceHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);

   }

   public void testDestroySlice() throws IOException, SecurityException, NoSuchMethodException {
      Method method = SlicehostAsyncClient.class.getMethod("destroySlice", int.class);
      HttpRequest request = processor.createRequest(method, 2);

      assertRequestLineEquals(request, "DELETE https://api.slicehost.com/slices/2/destroy.xml HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnVoidOnNotFoundOr404.class);

      checkFilters(request);
   }

   public void testListSlices() throws IOException, SecurityException, NoSuchMethodException {
      Method method = SlicehostAsyncClient.class.getMethod("listSlices");
      HttpRequest request = processor.createRequest(method);

      assertRequestLineEquals(request, "GET https://api.slicehost.com/slices.xml HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, SlicesHandler.class);
      assertExceptionParserClassEquals(method, ReturnEmptySetOnNotFoundOr404.class);

      checkFilters(request);
   }

   public void testGetSlice() throws IOException, SecurityException, NoSuchMethodException {
      Method method = SlicehostAsyncClient.class.getMethod("getSlice", int.class);
      HttpRequest request = processor.createRequest(method, 2);

      assertRequestLineEquals(request, "GET https://api.slicehost.com/slices/2.xml HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, SliceHandler.class);
      assertExceptionParserClassEquals(method, ReturnNullOnNotFoundOr404.class);

      checkFilters(request);
   }

   public void testListFlavors() throws IOException, SecurityException, NoSuchMethodException {
      Method method = SlicehostAsyncClient.class.getMethod("listFlavors");
      HttpRequest request = processor.createRequest(method);

      assertRequestLineEquals(request, "GET https://api.slicehost.com/flavors.xml HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, FlavorsHandler.class);
      assertExceptionParserClassEquals(method, ReturnEmptySetOnNotFoundOr404.class);

      checkFilters(request);
   }

   public void testGetFlavor() throws IOException, SecurityException, NoSuchMethodException {
      Method method = SlicehostAsyncClient.class.getMethod("getFlavor", int.class);
      HttpRequest request = processor.createRequest(method, 2);

      assertRequestLineEquals(request, "GET https://api.slicehost.com/flavors/2.xml HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, FlavorHandler.class);
      assertExceptionParserClassEquals(method, ReturnNullOnNotFoundOr404.class);

      checkFilters(request);
   }

   public void testListImages() throws IOException, SecurityException, NoSuchMethodException {
      Method method = SlicehostAsyncClient.class.getMethod("listImages");
      HttpRequest request = processor.createRequest(method);

      assertRequestLineEquals(request, "GET https://api.slicehost.com/images.xml HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, ImagesHandler.class);
      assertExceptionParserClassEquals(method, ReturnEmptySetOnNotFoundOr404.class);

      checkFilters(request);
   }

   public void testGetImage() throws IOException, SecurityException, NoSuchMethodException {
      Method method = SlicehostAsyncClient.class.getMethod("getImage", int.class);
      HttpRequest request = processor.createRequest(method, 2);

      assertRequestLineEquals(request, "GET https://api.slicehost.com/images/2.xml HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, ImageHandler.class);
      assertExceptionParserClassEquals(method, ReturnNullOnNotFoundOr404.class);

      checkFilters(request);
   }


   public void testRebuildSliceFromImage() throws IOException, SecurityException, NoSuchMethodException {
      Method method = SlicehostAsyncClient.class.getMethod("rebuildSliceFromImage", int.class, int.class);
      HttpRequest request = processor.createRequest(method, 3, 1);

      assertRequestLineEquals(request, "PUT https://api.slicehost.com/slices/3/rebuild.xml?image_id=1 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   public void testRebuildSliceFromBackup() throws IOException, SecurityException, NoSuchMethodException {
      Method method = SlicehostAsyncClient.class.getMethod("rebuildSliceFromBackup", int.class, int.class);
      HttpRequest request = processor.createRequest(method, 3, 1);

      assertRequestLineEquals(request, "PUT https://api.slicehost.com/slices/3/rebuild.xml?backup_id=1 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   public void testReboot() throws IOException, SecurityException, NoSuchMethodException {
      Method method = SlicehostAsyncClient.class.getMethod("rebootSlice", int.class);
      HttpRequest request = processor.createRequest(method, 2);

      assertRequestLineEquals(request, "PUT https://api.slicehost.com/slices/2/reboot.xml HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   public void testHardReboot() throws IOException, SecurityException, NoSuchMethodException {
      Method method = SlicehostAsyncClient.class.getMethod("hardRebootSlice", int.class);
      HttpRequest request = processor.createRequest(method, 2);

      assertRequestLineEquals(request, "PUT https://api.slicehost.com/slices/2/hard_reboot.xml HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   @Override
   protected TypeLiteral<RestAnnotationProcessor<SlicehostAsyncClient>> createTypeLiteral() {
      return new TypeLiteral<RestAnnotationProcessor<SlicehostAsyncClient>>() {
      };
   }

   @Override
   protected void checkFilters(HttpRequest request) {
      assertEquals(request.getFilters().size(), 1);
      assertEquals(request.getFilters().get(0).getClass(), SlicehostBasic.class);

   }

   @Override
   public RestContextSpec<SlicehostClient, SlicehostAsyncClient> createContextSpec() {
      return new RestContextFactory().createContextSpec("slicehost", "apikey", null, new Properties());
   }

}
