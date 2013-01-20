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
package org.jclouds.savvis.vpdc.features;

import static org.jclouds.reflect.Reflection2.method;

import java.io.IOException;
import java.net.URI;
import java.util.Set;

import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.cim.OSType;
import org.jclouds.compute.domain.CIMOperatingSystem;
import org.jclouds.fallbacks.MapHttp4xxCodesToExceptions;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.jclouds.savvis.vpdc.domain.VMSpec;
import org.jclouds.savvis.vpdc.xml.TaskHandler;
import org.jclouds.savvis.vpdc.xml.TasksListHandler;
import org.jclouds.util.Strings2;
import org.testng.annotations.Test;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.reflect.Invokable;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
/**
 * Tests annotation parsing of {@code VMAsyncApi}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class VMAsyncApiTest extends BaseVPDCAsyncApiTest<VMAsyncApi> {

   public void testAddVMIntoVDCURI() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(VMAsyncApi.class, "addVMIntoVDC", URI.class, VMSpec.class);

      CIMOperatingSystem os = Iterables.find(injector.getInstance(Key.get(new TypeLiteral<Set<CIMOperatingSystem>>() {
      })), new Predicate<CIMOperatingSystem>() {

         @Override
         public boolean apply(CIMOperatingSystem arg0) {
            return arg0.getOsType() == OSType.RHEL_64;
         }

      });

      GeneratedHttpRequest request = processor.createRequest(method, ImmutableList.<Object> of(URI
               .create("https://api.savvis.net/rest/api/v0.8/org/11/vdc/22"), VMSpec.builder().name(
               "DemoHost-1").networkTierName("VM Tier01").operatingSystem(os).build()));

      assertRequestLineEquals(request,
               "GET https://api.savvis.net/rest/api/v0.8/org/11/vdc/22/vApp/ HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "");
      assertPayloadEquals(request, Strings2.toStringAndClose(getClass().getResourceAsStream("/vm-default.xml")),
               "application/xml", false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, TaskHandler.class);
      assertFallbackClassEquals(method, MapHttp4xxCodesToExceptions.class);

      checkFilters(request);
   }

   public void testAddVMIntoVDC() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(VMAsyncApi.class, "addVMIntoVDC", String.class, String.class, VMSpec.class);

      CIMOperatingSystem os = Iterables.find(injector.getInstance(Key.get(new TypeLiteral<Set<CIMOperatingSystem>>() {
      })), new Predicate<CIMOperatingSystem>() {

         @Override
         public boolean apply(CIMOperatingSystem arg0) {
            return arg0.getOsType() == OSType.RHEL_64;
         }

      });

      GeneratedHttpRequest request = processor.createRequest(method, ImmutableList.<Object> of("11", "22", VMSpec.builder().operatingSystem(os).name(
               "DemoHost-1").networkTierName("VM Tier01").build()));

      assertRequestLineEquals(request, "GET https://api.savvis.net/vpdc/v1.0/org/11/vdc/22/vApp/ HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "");
      assertPayloadEquals(request, Strings2.toStringAndClose(getClass().getResourceAsStream("/vm-default.xml")),
               "application/xml", false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, TaskHandler.class);
      assertFallbackClassEquals(method, MapHttp4xxCodesToExceptions.class);

      checkFilters(request);
   }
   
   public void testCaptureVApp() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(VMAsyncApi.class, "captureVApp", String.class, String.class, URI.class);
      GeneratedHttpRequest request = processor.createRequest(method, ImmutableList.<Object> of("100000.0", "2736", URI.create("https://api.savvis.net/vpdc/v1.0/org/100000.0/vdc/2736/vApp/1001")));

      assertRequestLineEquals(request,
               "POST https://api.savvis.net/vpdc/v1.0/org/100000.0/vdc/2736/action/captureVApp HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "");
      assertPayloadEquals(request, Strings2.toStringAndClose(getClass().getResourceAsStream("/capture-vapp-template-default.xml")),
              "application/xml", false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, TaskHandler.class);

      checkFilters(request);
   }
   
   public void testCloneVApp() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(VMAsyncApi.class, "cloneVApp", URI.class, String.class, String.class);
      GeneratedHttpRequest request = processor.createRequest(method, ImmutableList.<Object> of(URI.create("https://api.savvis.net/vpdc/v1.0/org/100000.0/vdc/2736/vApp/1001"), "clonedvm", "VM Tier01"));

      assertRequestLineEquals(request,
               "POST https://api.savvis.net/vpdc/v1.0/org/100000.0/vdc/2736/vApp/1001/action/cloneVApp HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "");
      assertPayloadEquals(request, Strings2.toStringAndClose(getClass().getResourceAsStream("/cloneVApp-default.xml")),
              "application/xml", false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, TaskHandler.class);

      checkFilters(request);
   }
   
   public void testAddMultipleVMsIntoVDCURI() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(VMAsyncApi.class, "addMultipleVMsIntoVDC", URI.class, Iterable.class);

      CIMOperatingSystem os = Iterables.find(injector.getInstance(Key.get(new TypeLiteral<Set<CIMOperatingSystem>>() {
      })), new Predicate<CIMOperatingSystem>() {

         @Override
         public boolean apply(CIMOperatingSystem arg0) {
            return arg0.getOsType() == OSType.RHEL_64;
         }

      });

      GeneratedHttpRequest request = processor.createRequest(method, ImmutableList.<Object> of(URI
               .create("https://api.savvis.net/rest/api/v0.8/org/11/vdc/22"), ImmutableSet.of(VMSpec
               .builder().name("Test VM").networkTierName("VM Tier01").operatingSystem(os).build())));

      assertRequestLineEquals(request,
               "GET https://api.savvis.net/rest/api/v0.8/org/11/vdc/22/vApp/ HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "");
      assertPayloadEquals(request, Strings2.toStringAndClose(getClass().getResourceAsStream("/vm-multiple-default.xml")),
               "application/xml", false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, TasksListHandler.class);
      assertFallbackClassEquals(method, MapHttp4xxCodesToExceptions.class);

      checkFilters(request);
   }

   public void testAddMultipleVMsIntoVDC() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(VMAsyncApi.class, "addMultipleVMsIntoVDC", String.class, String.class, Iterable.class);

      CIMOperatingSystem os = Iterables.find(injector.getInstance(Key.get(new TypeLiteral<Set<CIMOperatingSystem>>() {
      })), new Predicate<CIMOperatingSystem>() {

         @Override
         public boolean apply(CIMOperatingSystem arg0) {
            return arg0.getOsType() == OSType.RHEL_64;
         }

      });

      GeneratedHttpRequest request = processor.createRequest(method, ImmutableList.<Object> of("11", "22", ImmutableSet.of(VMSpec.builder()
               .operatingSystem(os).name("Test VM").networkTierName("VM Tier01").build())));

      assertRequestLineEquals(request, "GET https://api.savvis.net/vpdc/v1.0/org/11/vdc/22/vApp/ HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "");
      assertPayloadEquals(request, Strings2.toStringAndClose(getClass().getResourceAsStream("/vm-multiple-default.xml")),
               "application/xml", false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, TasksListHandler.class);
      assertFallbackClassEquals(method, MapHttp4xxCodesToExceptions.class);

      checkFilters(request);
   }

   public void testRemoveVMFromVDC() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(VMAsyncApi.class, "removeVMFromVDC", String.class, String.class, String.class);
      GeneratedHttpRequest request = processor.createRequest(method, ImmutableList.<Object> of("11", "22", "33"));

      assertRequestLineEquals(request,
               "DELETE https://api.savvis.net/vpdc/v1.0/org/11/vdc/22/vApp/33 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, TaskHandler.class);
      assertFallbackClassEquals(method, NullOnNotFoundOr404.class);

      checkFilters(request);
   }

   public void testRemoveVM() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(VMAsyncApi.class, "removeVM", URI.class);
      GeneratedHttpRequest request = processor.createRequest(method, ImmutableList.<Object> of(URI
               .create("https://api.savvis.net/rest/api/v0.8/org/11/vdc/22/vApp/33")));

      assertRequestLineEquals(request,
               "DELETE https://api.savvis.net/rest/api/v0.8/org/11/vdc/22/vApp/33 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, TaskHandler.class);
      assertFallbackClassEquals(method, NullOnNotFoundOr404.class);

      checkFilters(request);
   }

   public void testPowerOffVM() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(VMAsyncApi.class, "powerOffVM", URI.class);
      GeneratedHttpRequest request = processor.createRequest(method, ImmutableList.<Object> of(URI
               .create("https://api.savvis.net/rest/api/v0.8/org/11/vdc/22/vApp/33")));

      assertRequestLineEquals(request,
               "POST https://api.savvis.net/rest/api/v0.8/org/11/vdc/22/vApp/33/action/powerOff HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, TaskHandler.class);
      assertFallbackClassEquals(method, NullOnNotFoundOr404.class);

      checkFilters(request);
   }

   public void testPowerOnVM() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(VMAsyncApi.class, "powerOnVM", URI.class);
      GeneratedHttpRequest request = processor.createRequest(method, ImmutableList.<Object> of(URI
               .create("https://api.savvis.net/rest/api/v0.8/org/11/vdc/22/vApp/33")));

      assertRequestLineEquals(request,
               "POST https://api.savvis.net/rest/api/v0.8/org/11/vdc/22/vApp/33/action/powerOn HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, TaskHandler.class);
      assertFallbackClassEquals(method, NullOnNotFoundOr404.class);

      checkFilters(request);
   }
}
