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
package org.jclouds.vcloud.internal;

import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URI;

import javax.ws.rs.core.HttpHeaders;

import org.jclouds.http.HttpRequest;
import org.jclouds.http.filters.BasicAuthentication;
import org.jclouds.location.Provider;
import org.jclouds.providers.AnonymousProviderMetadata;
import org.jclouds.providers.ProviderMetadata;
import org.jclouds.rest.internal.BaseAsyncClientTest;
import org.jclouds.rest.internal.RestAnnotationProcessor;
import org.jclouds.vcloud.endpoints.VCloudLogin;
import org.jclouds.vcloud.functions.ParseLoginResponseFromHeaders;
import org.testng.annotations.Test;

import com.google.common.base.Supplier;
import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;

/**
 * Tests behavior of {@code VCloudLoginAsyncClient}
 * 
 * @author Adrian Cole
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during surefire
@Test(groups = "unit", testName = "VCloudLoginAsyncClientTest")
public class VCloudLoginAsyncClientTest extends BaseAsyncClientTest<VCloudLoginAsyncClient> {

   public void testLogin() throws SecurityException, NoSuchMethodException, IOException {
      Method method = VCloudLoginAsyncClient.class.getMethod("login");
      HttpRequest request = processor.createRequest(method);

      assertEquals(request.getRequestLine(), "POST http://localhost:8080/login HTTP/1.1");
      assertNonPayloadHeadersEqual(request, HttpHeaders.ACCEPT + ": application/vnd.vmware.vcloud.orgList+xml\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseLoginResponseFromHeaders.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   @Override
   protected void checkFilters(HttpRequest request) {
      assertEquals(request.getFilters().size(), 1);
      assertEquals(request.getFilters().get(0).getClass(), BasicAuthentication.class);
   }

   @Override
   protected TypeLiteral<RestAnnotationProcessor<VCloudLoginAsyncClient>> createTypeLiteral() {
      return new TypeLiteral<RestAnnotationProcessor<VCloudLoginAsyncClient>>() {
      };
   }

   @Override
   protected Module createModule() {
      return new Module() {

         @Override
         public void configure(Binder binder) {
         }

         @Provides
         @VCloudLogin
         Supplier<URI> provideURI(@Provider Supplier<URI> uri) {
            return uri;
         }

      };
   }

   @Override
   protected ProviderMetadata createProviderMetadata() {
      return AnonymousProviderMetadata.forClientMappedToAsyncClientOnEndpoint(VCloudLoginClient.class,
            VCloudLoginAsyncClient.class, "http://localhost:8080/login");
   }

}
