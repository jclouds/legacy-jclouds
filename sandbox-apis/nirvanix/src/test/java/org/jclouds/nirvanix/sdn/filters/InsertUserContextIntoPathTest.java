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
import org.testng.annotations.Test;

import com.google.common.base.Throwables;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;

@Test(groups = "unit", singleThreaded = true, testName = "sdn.InsertUserContextIntoPathTest")
// sequential as easymock isn't threadsafe
public class InsertUserContextIntoPathTest extends RestClientTest<TestService> {

   private Method method;

   public void testRequestInvalid() {
      GeneratedHttpRequest request = processor.createRequest(method, URI.create("https://host/path"));
      request = filter.filter(request);
      request = filter.filter(request);
      assertEquals(request.getEndpoint().getPath(), "/sessiontoken/appname/username/path");
      assertEquals(request.getEndpoint().getHost(), "host");
   }

   public void testRequestNoSession() {
      GeneratedHttpRequest request = processor.createRequest(method, URI.create("https://host/path"));
      request = filter.filter(request);
      assertEquals(request.getEndpoint().getPath(), "/sessiontoken/appname/username/path");
      assertEquals(request.getEndpoint().getHost(), "host");
   }

   public void testRequestAlreadyHasSession() {
      GeneratedHttpRequest request = processor.createRequest(method, URI.create("https://host/sessiontoken/appname/username/path"));
      request = filter.filter(request);
      assertEquals(request.getEndpoint().getPath(), "/sessiontoken/appname/username/path");
      assertEquals(request.getEndpoint().getHost(), "host");
   }

   private InsertUserContextIntoPath filter;

   @BeforeClass
   @Override
   protected void setupFactory() throws IOException {
      super.setupFactory();
      filter = injector.getInstance(InsertUserContextIntoPath.class);
      try {
         method = TestService.class.getMethod("foo", URI.class);
      } catch (Exception e) {
        Throwables.propagate(e);
      }
   }

   @Override
   protected TypeLiteral<RestAnnotationProcessor<TestService>> createTypeLiteral() {
      return new TypeLiteral<RestAnnotationProcessor<TestService>>() {
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

   @Override
   protected void checkFilters(HttpRequest request) {
      
   }
}

interface TestService {
   @POST
   public void foo(@EndpointParam URI endpoint);
}
