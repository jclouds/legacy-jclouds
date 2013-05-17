/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.trmk.vcloud_0_8.internal;

import static org.jclouds.reflect.Reflection2.method;
import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.net.URI;

import javax.ws.rs.core.HttpHeaders;

import org.jclouds.http.HttpRequest;
import org.jclouds.http.filters.BasicAuthentication;
import org.jclouds.location.Provider;
import org.jclouds.providers.AnonymousProviderMetadata;
import org.jclouds.providers.ProviderMetadata;
import org.jclouds.rest.internal.BaseAsyncClientTest;
import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.jclouds.trmk.vcloud_0_8.endpoints.VCloudLogin;
import org.jclouds.trmk.vcloud_0_8.functions.ParseLoginResponseFromHeaders;
import org.testng.annotations.Test;

import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableList;
import com.google.common.reflect.Invokable;
import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.Provides;

/**
 * Tests behavior of {@code VCloudLoginAsyncClient}
 * 
 * @author Adrian Cole
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during
// surefire
@Test(groups = "unit", testName = "VCloudLoginAsyncClientTest")
public class TerremarkVCloudLoginAsyncClientTest extends BaseAsyncClientTest<TerremarkVCloudLoginAsyncClient> {

   public void testLogin() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(TerremarkVCloudLoginAsyncClient.class, "login");
      GeneratedHttpRequest request = processor.createRequest(method, ImmutableList.of());

      assertEquals(request.getRequestLine(), "POST http://localhost:8080/login HTTP/1.1");
      assertNonPayloadHeadersEqual(request, HttpHeaders.ACCEPT + ": application/vnd.vmware.vcloud.orgList+xml\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseLoginResponseFromHeaders.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, null);

      checkFilters(request);
   }

   @Override
   protected void checkFilters(HttpRequest request) {
      assertEquals(request.getFilters().size(), 1);
      assertEquals(request.getFilters().get(0).getClass(), BasicAuthentication.class);
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
      return AnonymousProviderMetadata.forClientMappedToAsyncClientOnEndpoint(TerremarkVCloudLoginClient.class,
            TerremarkVCloudLoginAsyncClient.class, "http://localhost:8080/login");
   }

}
