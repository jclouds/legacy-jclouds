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
package org.jclouds.providers.config;

import static org.testng.Assert.assertEquals;

import java.util.Properties;
import java.util.Set;

import javax.inject.Inject;

import org.jclouds.Constants;
import org.jclouds.Context;
import org.jclouds.apis.ApiMetadata;
import org.jclouds.domain.Credentials;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.http.IntegrationTestAsyncClient;
import org.jclouds.http.IntegrationTestClient;
import org.jclouds.internal.FilterStringsBoundToInjectorByName;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.location.Iso3166;
import org.jclouds.location.Provider;
import org.jclouds.providers.AnonymousProviderMetadata;
import org.jclouds.providers.ProviderMetadata;
import org.jclouds.rest.annotations.Api;
import org.jclouds.rest.annotations.ApiVersion;
import org.jclouds.rest.annotations.BuildVersion;
import org.testng.annotations.Test;

import com.google.common.base.Predicates;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.reflect.TypeToken;
import com.google.inject.Guice;
import com.google.inject.Key;
import com.google.inject.name.Names;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "BindProviderMetadataContextAndCredentialsTest")
public class BindProviderMetadataContextAndCredentialsTest {

   @SuppressWarnings("unused")
   private static class ExpectedBindings {
      private final javax.inject.Provider<Context> backend;
      private final ProviderMetadata providerMetadata;
      private final Credentials creds;
      private final String providerId;
      private final Set<String> iso3166Codes;
      private final String apiId;
      private final String apiVersion;
      private final String buildVersion;

      @Inject
      private ExpectedBindings(@Provider javax.inject.Provider<Context> backend, ProviderMetadata providerMetadata,
            @Provider Supplier<Credentials> creds, @Provider String providerId, @Iso3166 Set<String> iso3166Codes,
            @Api String apiId, @ApiVersion String apiVersion, @Nullable @BuildVersion String buildVersion,
            @Provider TypeToken<? extends Context> backendToken, FilterStringsBoundToInjectorByName filter) {
         this.backend = backend;
         assertEquals(backendToken, providerMetadata.getApiMetadata().getContext());
         this.providerMetadata = providerMetadata;
         Properties props = new Properties();
         props.putAll(filter.apply(Predicates.<String> alwaysTrue()));
         Properties expected = new Properties();
         expected.putAll(providerMetadata.getApiMetadata().getDefaultProperties());
         expected.putAll(providerMetadata.getDefaultProperties());
         assertEquals(props, expected);
         this.creds = creds.get();
         this.providerId = providerId;
         assertEquals(providerId, providerMetadata.getId());
         this.iso3166Codes = iso3166Codes;
         assertEquals(iso3166Codes, providerMetadata.getIso3166Codes());
         this.apiId = apiId;
         assertEquals(apiId, providerMetadata.getApiMetadata().getId());
         this.apiVersion = apiVersion;
         assertEquals(apiVersion, providerMetadata.getApiMetadata().getVersion());
         this.buildVersion = buildVersion;
         assertEquals(buildVersion, providerMetadata.getApiMetadata().getBuildVersion().orNull());
      }

   }

   @Test
   public void testExpectedBindingsWhenCredentialIsNotNull() {

      ProviderMetadata md = AnonymousProviderMetadata.forClientMappedToAsyncClientOnEndpoint(
               IntegrationTestClient.class, IntegrationTestAsyncClient.class, "http://localhost");
      Supplier<Credentials> creds = Suppliers.<Credentials> ofInstance(LoginCredentials.builder().user("user")
            .password("password").build());

      ExpectedBindings bindings = Guice.createInjector(new BindProviderMetadataContextAndCredentials(md, creds))
               .getInstance(ExpectedBindings.class);
      assertEquals(bindings.creds.identity, "user");
      assertEquals(bindings.creds.credential, "password");
   }

   @Test
   public void testExpectedBindingsWhenCredentialIsNull() {

      ProviderMetadata md = AnonymousProviderMetadata.forClientMappedToAsyncClientOnEndpoint(
               IntegrationTestClient.class, IntegrationTestAsyncClient.class, "http://localhost");
      Supplier<Credentials> creds = Suppliers.<Credentials> ofInstance(LoginCredentials.builder().user("user").build());

      ExpectedBindings bindings = Guice.createInjector(new BindProviderMetadataContextAndCredentials(md, creds))
               .getInstance(ExpectedBindings.class);
      assertEquals(bindings.creds.identity, "user");
      assertEquals(bindings.creds.credential, null);
   }
   
   @Test
   public void testExpectedBindingsWhenBuildVersionAbsent() {

      ProviderMetadata md = AnonymousProviderMetadata.forClientMappedToAsyncClientOnEndpoint(
               IntegrationTestClient.class, IntegrationTestAsyncClient.class, "http://localhost");
      ApiMetadata apiMd = md.getApiMetadata().toBuilder().buildVersion(null).build();
      md = md.toBuilder().apiMetadata(apiMd).build();
      Supplier<Credentials> creds = Suppliers.<Credentials> ofInstance(LoginCredentials.builder().user("user").build());

      ExpectedBindings bindings = Guice.createInjector(new BindProviderMetadataContextAndCredentials(md, creds))
               .getInstance(ExpectedBindings.class);
      assertEquals(bindings.buildVersion, null);
   }

   @Test
   public void testProviderOverridesApiMetadataProperty() {

      ProviderMetadata md = AnonymousProviderMetadata.forClientMappedToAsyncClientOnEndpoint(
               IntegrationTestClient.class, IntegrationTestAsyncClient.class, "http://localhost");
      Properties defaultProps = md.getDefaultProperties();
      defaultProps.setProperty(Constants.PROPERTY_SESSION_INTERVAL, Integer.MAX_VALUE + "");
      md = md.toBuilder().defaultProperties(defaultProps).build();

      Supplier<Credentials> creds = Suppliers.<Credentials> ofInstance(LoginCredentials.builder().user("user").build());

      int session = Guice.createInjector(new BindProviderMetadataContextAndCredentials(md, creds)).getInstance(
               Key.get(int.class, Names.named(Constants.PROPERTY_SESSION_INTERVAL)));
      assertEquals(session, Integer.MAX_VALUE);
   }
}
