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

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URI;

import org.jclouds.http.HttpRequest;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.rest.functions.MapHttp4xxCodesToExceptions;
import org.jclouds.rest.internal.RestAnnotationProcessor;
import org.jclouds.savvis.vpdc.xml.TaskHandler;
import org.testng.annotations.Test;

import com.google.inject.TypeLiteral;

/**
 * Tests annotation parsing of {@code ServiceManagementAsyncClient}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class ServiceManagementAsyncClientTest extends BaseVPDCAsyncClientTest<ServiceManagementAsyncClient> {

   @Override
   protected TypeLiteral<RestAnnotationProcessor<ServiceManagementAsyncClient>> createTypeLiteral() {
      return new TypeLiteral<RestAnnotationProcessor<ServiceManagementAsyncClient>>() {
      };
   }

   public void testPowerOnVMVDC() throws SecurityException, NoSuchMethodException, IOException {
      Method method = ServiceManagementAsyncClient.class.getMethod("powerOnVMInVDC", String.class, String.class,
               String.class);
      HttpRequest request = processor.createRequest(method, "11", "22", "33");

      assertRequestLineEquals(request,
               "POST https://api.symphonyvpdc.savvis.net/vpdc/v1.0/org/11/vdc/22/vApp/33/action/powerOn HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, TaskHandler.class);
      assertExceptionParserClassEquals(method, MapHttp4xxCodesToExceptions.class);

      checkFilters(request);
   }

   public void testPowerOnVM() throws SecurityException, NoSuchMethodException, IOException {
      Method method = ServiceManagementAsyncClient.class.getMethod("powerOnVM", URI.class);
      HttpRequest request = processor.createRequest(method, URI
               .create("https://api.symphonyvpdc.savvis.net/vpdc/v1.0/org/11/vdc/22/vApp/33"));

      assertRequestLineEquals(request,
               "POST https://api.symphonyvpdc.savvis.net/vpdc/v1.0/org/11/vdc/22/vApp/33/action/powerOn HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, TaskHandler.class);
      assertExceptionParserClassEquals(method, MapHttp4xxCodesToExceptions.class);

      checkFilters(request);
   }

   public void testPowerOffVMVDC() throws SecurityException, NoSuchMethodException, IOException {
      Method method = ServiceManagementAsyncClient.class.getMethod("powerOffVMInVDC", String.class, String.class,
               String.class);
      HttpRequest request = processor.createRequest(method, "11", "22", "33");

      assertRequestLineEquals(request,
               "POST https://api.symphonyvpdc.savvis.net/vpdc/v1.0/org/11/vdc/22/vApp/33/action/powerOff HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, TaskHandler.class);
      assertExceptionParserClassEquals(method, MapHttp4xxCodesToExceptions.class);

      checkFilters(request);
   }

   public void testPowerOffVM() throws SecurityException, NoSuchMethodException, IOException {
      Method method = ServiceManagementAsyncClient.class.getMethod("powerOffVM", URI.class);
      HttpRequest request = processor.createRequest(method, URI
               .create("https://api.symphonyvpdc.savvis.net/vpdc/v1.0/org/11/vdc/22/vApp/33"));

      assertRequestLineEquals(request,
               "POST https://api.symphonyvpdc.savvis.net/vpdc/v1.0/org/11/vdc/22/vApp/33/action/powerOff HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, TaskHandler.class);
      assertExceptionParserClassEquals(method, MapHttp4xxCodesToExceptions.class);

      checkFilters(request);
   }

}
