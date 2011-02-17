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

package org.jclouds.vcloud;

import static org.jclouds.rest.RestContextFactory.contextSpec;
import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.concurrent.TimeUnit;

import javax.ws.rs.core.HttpHeaders;

import org.jclouds.concurrent.Timeout;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.filters.BasicAuthentication;
import org.jclouds.location.Provider;
import org.jclouds.rest.RestClientTest;
import org.jclouds.rest.RestContextSpec;
import org.jclouds.rest.internal.RestAnnotationProcessor;
import org.jclouds.vcloud.domain.VCloudSession;
import org.jclouds.vcloud.endpoints.VCloudLogin;
import org.jclouds.vcloud.functions.ParseLoginResponseFromHeaders;
import org.testng.annotations.Test;

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
public class VCloudLoginAsyncClientTest extends RestClientTest<VCloudLoginAsyncClient> {

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

         @SuppressWarnings("unused")
         @Provides
         @VCloudLogin
         URI provideURI(@Provider URI uri) {
            return uri;
         }

      };
   }

   @Timeout(duration = 10, timeUnit = TimeUnit.SECONDS)
   public interface VCloudLoginClient {

      VCloudSession login();
   }

   @Override
   public RestContextSpec<VCloudLoginClient, VCloudLoginAsyncClient> createContextSpec() {
      return contextSpec("test", "http://localhost:8080/login", "1", "", "identity", "credential",
               VCloudLoginClient.class, VCloudLoginAsyncClient.class);
   }
}
