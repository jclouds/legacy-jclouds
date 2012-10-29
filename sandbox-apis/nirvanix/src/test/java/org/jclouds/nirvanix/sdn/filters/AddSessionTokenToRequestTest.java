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
package org.jclouds.nirvanix.sdn.filters;

import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.Properties;

import javax.ws.rs.POST;

import org.jclouds.http.HttpRequest;
import org.jclouds.http.RequiresHttp;
import org.jclouds.nirvanix.sdn.SDNAsyncClient;
import org.jclouds.nirvanix.sdn.SDNClient;
import org.jclouds.nirvanix.sdn.SessionToken;
import org.jclouds.nirvanix.sdn.config.SDNRestClientModule;
import org.jclouds.nirvanix.sdn.reference.SDNConstants;
import org.jclouds.rest.ConfiguresRestClient;
import org.jclouds.rest.RestClientTest;
import org.jclouds.rest.RestContextFactory;
import org.jclouds.rest.RestContextSpec;
import org.jclouds.rest.annotations.EndpointParam;
import org.jclouds.rest.internal.RestAnnotationProcessor;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;

@Test(groups = "unit", testName = "AddSessionTokenToRequestTest")
public class AddSessionTokenToRequestTest extends RestClientTest<SDNAsyncClient> {

   private static interface TestService {
      @POST
      public void foo(@EndpointParam URI endpoint);
   }

   @DataProvider
   public Object[][] dataProvider() throws SecurityException, NoSuchMethodException {

      RestAnnotationProcessor<TestService> factory = injector.getInstance(Key
               .get(new TypeLiteral<RestAnnotationProcessor<TestService>>() {
               }));

      Method method = TestService.class.getMethod("foo", URI.class);
      return new Object[][] { { factory.createRequest(method, new Object[] { URI.create("https://host:443") }) },
               { factory.createRequest(method, new Object[] { URI.create("https://host/path") }) },
               { factory.createRequest(method, new Object[] { URI.create("https://host/?query") })

               } };
   }

   @Test(dataProvider = "dataProvider")
   public void testRequests(HttpRequest request) {
      String token = filter.getSessionToken();
      String query = request.getEndpoint().getQuery();
      request = filter.filter(request);
      assertEquals(request.getEndpoint().getQuery(), query == null ? "sessionToken=" + token : query + "&sessionToken="
               + token);
   }

   @Test
   void testUpdatesOnlyOncePerSecond() throws NoSuchMethodException, InterruptedException {
      String token = filter.getSessionToken();
      for (int i = 0; i < 10; i++)
         filter.updateIfTimeOut();
      assert token.equals(filter.getSessionToken());
   }

   @Override
   protected void checkFilters(HttpRequest request) {
   }

   private AddSessionTokenToRequest filter;

   @BeforeClass
   @Override
   protected void setupFactory() throws IOException {
      super.setupFactory();
      filter = injector.getInstance(AddSessionTokenToRequest.class);
   }

   @Override
   protected TypeLiteral<RestAnnotationProcessor<SDNAsyncClient>> createTypeLiteral() {
      return new TypeLiteral<RestAnnotationProcessor<SDNAsyncClient>>() {
      };
   }

   protected Module createModule() {
      return new TestSDNRestClientModule();
   }

   @RequiresHttp
   @ConfiguresRestClient
   static class TestSDNRestClientModule extends SDNRestClientModule {
      @Override
      public void configure() {
         bind(String.class).annotatedWith(SessionToken.class).toInstance("sessiontoken");
         bind(String.class).annotatedWith(Names.named(SDNConstants.PROPERTY_SDN_APPKEY)).toInstance("appKey");
         bind(String.class).annotatedWith(Names.named(SDNConstants.PROPERTY_SDN_APPNAME)).toInstance("appname");
         bind(String.class).annotatedWith(Names.named(SDNConstants.PROPERTY_SDN_USERNAME)).toInstance("username");
      }

   }

   @Override
   public RestContextSpec<SDNClient, SDNAsyncClient> createContextSpec() {
      return new RestContextFactory().createContextSpec("sdn", "user", "password", new Properties());
   }
}
