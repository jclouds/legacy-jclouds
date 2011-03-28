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

package org.jclouds.savvis.vpdc.features;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.Set;

import org.jclouds.cim.OSType;
import org.jclouds.compute.domain.CIMOperatingSystem;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.rest.functions.MapHttp4xxCodesToExceptions;
import org.jclouds.rest.functions.ReturnNullOnNotFoundOr404;
import org.jclouds.rest.internal.RestAnnotationProcessor;
import org.jclouds.savvis.vpdc.domain.VMSpec;
import org.jclouds.savvis.vpdc.xml.TaskHandler;
import org.jclouds.util.Strings2;
import org.testng.annotations.Test;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;

/**
 * Tests annotation parsing of {@code VMAsyncClient}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class VMAsyncClientTest extends BaseVPDCAsyncClientTest<VMAsyncClient> {

   public void testAddVMIntoVDC() throws SecurityException, NoSuchMethodException, IOException {
      Method method = VMAsyncClient.class.getMethod("addVMIntoVDC", String.class, String.class, String.class,
               String.class, VMSpec.class);

      CIMOperatingSystem os = Iterables.find(injector.getInstance(Key.get(new TypeLiteral<Set<CIMOperatingSystem>>() {
      })), new Predicate<CIMOperatingSystem>() {

         @Override
         public boolean apply(CIMOperatingSystem arg0) {
            return arg0.getOsType() == OSType.RHEL_64;
         }

      });

      HttpRequest request = processor.createRequest(method, "11", "22", "VM Tier01", "DemoHost-1", VMSpec.builder()
               .operatingSystem(os).build());

      assertRequestLineEquals(request,
               "GET https://api.symphonyvpdc.savvis.net/rest/api/v0.8/org/11/vdc/22/vApp/ HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "");
      assertPayloadEquals(request, Strings2.toStringAndClose(getClass().getResourceAsStream("/vm-default.xml")),
               "application/xml", false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, TaskHandler.class);
      assertExceptionParserClassEquals(method, MapHttp4xxCodesToExceptions.class);

      checkFilters(request);
   }

   public void testRemoveVMFromVDC() throws SecurityException, NoSuchMethodException, IOException {
      Method method = VMAsyncClient.class.getMethod("removeVMFromVDC", String.class, String.class, String.class);
      HttpRequest request = processor.createRequest(method, "11", "22", "33");

      assertRequestLineEquals(request,
               "DELETE https://api.symphonyvpdc.savvis.net/rest/api/v0.8/org/11/vdc/22/vApp/33 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, TaskHandler.class);
      assertExceptionParserClassEquals(method, ReturnNullOnNotFoundOr404.class);

      checkFilters(request);
   }

   public void testRemoveVM() throws SecurityException, NoSuchMethodException, IOException {
      Method method = VMAsyncClient.class.getMethod("removeVM", URI.class);
      HttpRequest request = processor.createRequest(method, URI
               .create("https://api.symphonyvpdc.savvis.net/rest/api/v0.8/org/11/vdc/22/vApp/33"));

      assertRequestLineEquals(request,
               "DELETE https://api.symphonyvpdc.savvis.net/rest/api/v0.8/org/11/vdc/22/vApp/33 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, TaskHandler.class);
      assertExceptionParserClassEquals(method, ReturnNullOnNotFoundOr404.class);

      checkFilters(request);
   }

   @Override
   protected TypeLiteral<RestAnnotationProcessor<VMAsyncClient>> createTypeLiteral() {
      return new TypeLiteral<RestAnnotationProcessor<VMAsyncClient>>() {
      };
   }

}
