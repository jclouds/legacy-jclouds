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

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.*;
import com.google.inject.*;
import com.google.inject.name.Named;
import org.jclouds.cloudservers.CloudServersAsyncClient;
import org.jclouds.cloudservers.CloudServersContextBuilder;
import org.jclouds.cloudservers.config.CloudServersRestClientModule;
import org.jclouds.http.HttpCommand;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.RequiresHttp;
import org.jclouds.openstack.OpenStackAuthAsyncClient;
import org.jclouds.openstack.OpenStackAuthAsyncClient.AuthenticationResponse;
import org.jclouds.openstack.TestOpenStackAuthenticationModule;
import org.jclouds.openstack.config.OpenStackAuthenticationModule.GetAuthenticationCache;
import org.jclouds.openstack.filters.AddTimestampQuery;
import org.jclouds.openstack.filters.AuthenticateRequest;
import org.jclouds.rest.ConfiguresRestClient;
import org.jclouds.rest.RestClientTest;
import org.jclouds.rest.RestContextFactory;
import org.jclouds.rest.RestContextSpec;
import org.jclouds.rest.internal.RestAnnotationProcessor;
import org.testng.annotations.Test;

import javax.annotation.Nullable;
import java.net.URI;
import java.util.*;

import static org.jclouds.Constants.PROPERTY_API_VERSION;
import static org.jclouds.Constants.PROPERTY_CREDENTIAL;
import static org.jclouds.Constants.PROPERTY_IDENTITY;
import static org.jclouds.location.reference.LocationConstants.PROPERTY_REGIONS;
import static org.testng.Assert.*;
import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.*;

/**
 * Tests behavior of {@code RetryOnRenew} handler
 * 
 * @author grkvlt@apache.org
 */
@Test(groups = "unit", testName = "RetryOnRenewTest")
public class RetryOnRenewTest extends RestClientTest<CloudServersAsyncClient> {
   @Inject
   @Named(PROPERTY_IDENTITY) String identity = "testUser";

   @Inject
   @Named(PROPERTY_CREDENTIAL) String credential = "testCred";

   @Test
   public void test401ShouldRetry() {

      Injector injector = Guice.createInjector();
      GetAuthenticationCache supplier = injector.getInstance(GetAuthenticationCache.class);

      assertNotNull(supplier);

      HttpCommand command = createMock(HttpCommand.class);
      HttpRequest request = createMock(HttpRequest.class);
      HttpResponse response = createMock(HttpResponse.class);

      expect(command.getFailureCount()).andReturn(0);
      expect(command.getCurrentRequest()).andReturn(request);

      expect(response.getPayload()).andReturn(null).anyTimes();
      expect(response.getStatusCode()).andReturn(401).atLeastOnce();

      replay(command);
      replay(response);

      RetryOnRenew retry = new RetryOnRenew();

      assertTrue(retry.shouldRetryRequest(command, response));

      // verify(command);
      // verify(response);
   }

   @Override
   protected TypeLiteral<RestAnnotationProcessor<CloudServersAsyncClient>> createTypeLiteral() {
      return new TypeLiteral<RestAnnotationProcessor<CloudServersAsyncClient>>() {
      };
   }

   @Override
   protected void checkFilters(HttpRequest request) {
      assertEquals(request.getFilters().size(), 2);
      assertEquals(request.getFilters().get(0).getClass(), AuthenticateRequest.class);
      assertEquals(request.getFilters().get(1).getClass(), AddTimestampQuery.class);
   }


   @Override
   protected Module createModule() {
      return new TestCloudServersRestClientModule();
   }

   @ConfiguresRestClient
   @RequiresHttp
   protected static class TestCloudServersRestClientModule extends CloudServersRestClientModule {
      private TestCloudServersRestClientModule() {
         super(new TestOpenStackAuthenticationModule());
      }

      @Override
      protected URI provideServerUrl(OpenStackAuthAsyncClient.AuthenticationResponse response) {
         return URI.create("http://serverManagementUrl");
      }

   }

   protected String provider = "cloudservers";

   @Override
   public RestContextSpec<?, ?> createContextSpec() {
      return new RestContextFactory(getProperties()).createContextSpec(provider, "user", "password", new Properties());
   }

   @Override
   protected Properties getProperties() {
      Properties overrides = new Properties();
      overrides.setProperty(PROPERTY_REGIONS, "US");
      overrides.setProperty(PROPERTY_API_VERSION, "1");
      overrides.setProperty(provider + ".endpoint", "https://auth");
      overrides.setProperty(provider + ".contextbuilder", CloudServersContextBuilder.class.getName());

      return overrides;
   }
}
