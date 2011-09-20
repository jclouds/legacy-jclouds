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
package org.jclouds.softlayer.features;

import java.io.IOException;
import java.lang.reflect.Method;

import org.jclouds.http.HttpRequest;
import org.jclouds.http.functions.ParseJson;
import org.jclouds.http.functions.ReleasePayloadAndReturn;
import org.jclouds.rest.functions.ReturnEmptySetOnNotFoundOr404;
import org.jclouds.rest.functions.ReturnNullOnNotFoundOr404;
import org.jclouds.rest.functions.ReturnVoidOnNotFoundOr404;
import org.jclouds.rest.internal.RestAnnotationProcessor;
import org.testng.annotations.Test;

import com.google.common.collect.Iterables;
import com.google.inject.TypeLiteral;

/**
 * Tests annotation parsing of {@code VirtualGuestAsyncClient}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class VirtualGuestAsyncClientTest extends BaseSoftLayerAsyncClientTest<VirtualGuestAsyncClient> {

   public void testListVirtualGuests() throws SecurityException, NoSuchMethodException, IOException {
      Method method = VirtualGuestAsyncClient.class.getMethod("listVirtualGuests");
      HttpRequest httpRequest = processor.createRequest(method);

      assertRequestLineEquals(
            httpRequest,
            "GET https://api.softlayer.com/rest/v3/SoftLayer_Account/VirtualGuests.json?objectMask=powerState%3BnetworkVlans%3BoperatingSystem.passwords%3Bdatacenter%3BvirtualGuests.billingItem HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      // now make sure request filters apply by replaying
      httpRequest = Iterables.getOnlyElement(httpRequest.getFilters()).filter(httpRequest);
      httpRequest = Iterables.getOnlyElement(httpRequest.getFilters()).filter(httpRequest);

      assertRequestLineEquals(
            httpRequest,
            "GET https://api.softlayer.com/rest/v3/SoftLayer_Account/VirtualGuests.json?objectMask=powerState%3BnetworkVlans%3BoperatingSystem.passwords%3Bdatacenter%3BvirtualGuests.billingItem HTTP/1.1");
      // for example, using basic authentication, we should get "only one"
      // header
      assertNonPayloadHeadersEqual(httpRequest,
            "Accept: application/json\nAuthorization: Basic YXBpS2V5OnNlY3JldEtleQ==\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ParseJson.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnEmptySetOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }

   public void testGetVirtualGuest() throws SecurityException, NoSuchMethodException, IOException {
      Method method = VirtualGuestAsyncClient.class.getMethod("getVirtualGuest", long.class);
      HttpRequest httpRequest = processor.createRequest(method, 1234);

      assertRequestLineEquals(
            httpRequest,
            "GET https://api.softlayer.com/rest/v3/SoftLayer_Virtual_Guest/1234.json?objectMask=powerState%3BnetworkVlans%3BoperatingSystem.passwords%3Bdatacenter%3BvirtualGuests.billingItem HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ParseJson.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnNullOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }

   public void testRebootHardVirtualGuest() throws SecurityException, NoSuchMethodException, IOException {
      Method method = VirtualGuestAsyncClient.class.getMethod("rebootHardVirtualGuest", long.class);
      HttpRequest httpRequest = processor.createRequest(method, 1234);

      assertRequestLineEquals(httpRequest,
            "GET https://api.softlayer.com/rest/v3/SoftLayer_Virtual_Guest/1234/rebootHard.json HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnVoidOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }

   public void testPowerOffVirtualGuest() throws SecurityException, NoSuchMethodException, IOException {
      Method method = VirtualGuestAsyncClient.class.getMethod("powerOffVirtualGuest", long.class);
      HttpRequest httpRequest = processor.createRequest(method, 1234);

      assertRequestLineEquals(httpRequest,
            "GET https://api.softlayer.com/rest/v3/SoftLayer_Virtual_Guest/1234/powerOff.json HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnVoidOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }

   public void testPowerOnVirtualGuest() throws SecurityException, NoSuchMethodException, IOException {
      Method method = VirtualGuestAsyncClient.class.getMethod("powerOnVirtualGuest", long.class);
      HttpRequest httpRequest = processor.createRequest(method, 1234);

      assertRequestLineEquals(httpRequest,
            "GET https://api.softlayer.com/rest/v3/SoftLayer_Virtual_Guest/1234/powerOn.json HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnVoidOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }

   public void testPauseVirtualGuest() throws SecurityException, NoSuchMethodException, IOException {
      Method method = VirtualGuestAsyncClient.class.getMethod("pauseVirtualGuest", long.class);
      HttpRequest httpRequest = processor.createRequest(method, 1234);

      assertRequestLineEquals(httpRequest,
            "GET https://api.softlayer.com/rest/v3/SoftLayer_Virtual_Guest/1234/pause.json HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnVoidOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }

   public void testResumeVirtualGuest() throws SecurityException, NoSuchMethodException, IOException {
      Method method = VirtualGuestAsyncClient.class.getMethod("resumeVirtualGuest", long.class);
      HttpRequest httpRequest = processor.createRequest(method, 1234);

      assertRequestLineEquals(httpRequest,
            "GET https://api.softlayer.com/rest/v3/SoftLayer_Virtual_Guest/1234/resume.json HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnVoidOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }

   @Override
   protected TypeLiteral<RestAnnotationProcessor<VirtualGuestAsyncClient>> createTypeLiteral() {
      return new TypeLiteral<RestAnnotationProcessor<VirtualGuestAsyncClient>>() {
      };
   }
}
