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
package org.jclouds.cloudservers.handlers;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import com.google.inject.*;
import org.jclouds.http.HttpCommand;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.rest.BaseRestClientTest.MockModule;
import org.jclouds.rest.config.RestModule;
import org.testng.annotations.Test;

import javax.inject.Named;
import javax.inject.Singleton;
import java.net.URI;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.*;
import static org.jclouds.Constants.PROPERTY_CREDENTIAL;
import static org.jclouds.Constants.PROPERTY_IDENTITY;

/**
 * Tests behavior of {@code DeltacloudRedirectionRetry}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class RetryOnRenewHandlerTest {
   Injector injector = Guice.createInjector(new MockModule(), new RestModule(), new AbstractModule() {
      @SuppressWarnings("unused")
      @Provides
      @Singleton
      @Named("CONSTANTS")
      protected Multimap<String, String> constants() {
         return LinkedHashMultimap.create();
      }

      @com.google.inject.name.Named(PROPERTY_IDENTITY) String identity = "testUser";

      @com.google.inject.name.Named(PROPERTY_CREDENTIAL) String credential = "testCred";

      @Override
      protected void configure() {
      }
   });

   @Test
   public void test401WillRetry() {
      HttpCommand command = createMock(HttpCommand.class);
      HttpRequest request = createMock(HttpRequest.class);
      HttpResponse response = createMock(HttpResponse.class);

      expect(command.getFailureCount()).andReturn(0);
      expect(command.getCurrentRequest()).andReturn(request);

      expect(response.getPayload()).andReturn(null).anyTimes();
      expect(response.getStatusCode()).andReturn(401).atLeastOnce();

      replay(command);
      replay(response);

      RetryOnRenew retry = injector.getInstance(
            RetryOnRenew.class);

      assert retry.shouldRetryRequest(command, response);

      verify(command);

   }

//   @Test
//   public void test302DoesRetryOnGET() {
//
//      HttpCommand command = createMock(HttpCommand.class);
//      HttpRequest request = HttpRequest.builder().method("GET").endpoint(URI.create("http://localhost")).build();
//      HttpResponse response = new HttpResponse(302, "HTTP/1.1 302 Found", null);
//
//      expect(command.getCurrentRequest()).andReturn(request).atLeastOnce();
//      expect(command.incrementRedirectCount()).andReturn(1);
//
//      replay(command);
//
//      DeltacloudRedirectionRetryHandler retry = injector.getInstance(
//            DeltacloudRedirectionRetryHandler.class);
//
//      assert !retry.shouldRetryRequest(command, response);
//
//      verify(command);
//
//   }
}
