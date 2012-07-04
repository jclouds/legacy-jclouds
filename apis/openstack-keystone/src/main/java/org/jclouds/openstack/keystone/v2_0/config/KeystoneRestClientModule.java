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
package org.jclouds.openstack.keystone.v2_0.config;

import static org.jclouds.util.Suppliers2.getLastValueInMap;

import java.net.URI;
import java.util.Map;

import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.http.HttpErrorHandler;
import org.jclouds.http.annotation.ClientError;
import org.jclouds.http.annotation.Redirection;
import org.jclouds.http.annotation.ServerError;
import org.jclouds.openstack.keystone.v2_0.KeystoneAsyncClient;
import org.jclouds.openstack.keystone.v2_0.KeystoneClient;
import org.jclouds.openstack.keystone.v2_0.features.ServiceAsyncClient;
import org.jclouds.openstack.keystone.v2_0.features.ServiceClient;
import org.jclouds.openstack.keystone.v2_0.features.TenantAsyncClient;
import org.jclouds.openstack.keystone.v2_0.features.TenantClient;
import org.jclouds.openstack.keystone.v2_0.features.TokenAsyncClient;
import org.jclouds.openstack.keystone.v2_0.features.TokenClient;
import org.jclouds.openstack.keystone.v2_0.features.UserAsyncClient;
import org.jclouds.openstack.keystone.v2_0.features.UserClient;
import org.jclouds.openstack.keystone.v2_0.functions.PresentWhenAdminURLExistsForIdentityService;
import org.jclouds.openstack.keystone.v2_0.handlers.KeystoneErrorHandler;
import org.jclouds.openstack.keystone.v2_0.suppliers.RegionIdToAdminURIFromAccessForTypeAndVersion;
import org.jclouds.openstack.keystone.v2_0.suppliers.RegionIdToAdminURISupplier;
import org.jclouds.openstack.v2_0.ServiceType;
import org.jclouds.openstack.v2_0.services.Identity;
import org.jclouds.rest.ConfiguresRestClient;
import org.jclouds.rest.config.RestClientModule;
import org.jclouds.rest.functions.ImplicitOptionalConverter;

import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableMap;
import com.google.common.reflect.TypeToken;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.assistedinject.FactoryModuleBuilder;

/**
 * Configures the Keystone connection.
 * 
 * @author Adam Lowe
 */
@ConfiguresRestClient
public class KeystoneRestClientModule<S extends KeystoneClient, A extends KeystoneAsyncClient> extends
         RestClientModule<S, A> {

   public static final Map<Class<?>, Class<?>> DELEGATE_MAP = ImmutableMap.<Class<?>, Class<?>> builder()
            .put(ServiceClient.class, ServiceAsyncClient.class).put(TokenClient.class, TokenAsyncClient.class)
            .put(UserClient.class, UserAsyncClient.class).put(TenantClient.class, TenantAsyncClient.class).build();

   @SuppressWarnings("unchecked")
   public KeystoneRestClientModule() {
      super(TypeToken.class.cast(TypeToken.of(KeystoneClient.class)), TypeToken.class.cast(TypeToken
               .of(KeystoneAsyncClient.class)), DELEGATE_MAP);
   }

   protected KeystoneRestClientModule(TypeToken<S> syncClientType, TypeToken<A> asyncClientType,
            Map<Class<?>, Class<?>> sync2Async) {
      super(syncClientType, asyncClientType, sync2Async);
   }

   public static class KeystoneAdminURLModule extends AbstractModule {

      @Override
      protected void configure() {
         bind(ImplicitOptionalConverter.class).to(PresentWhenAdminURLExistsForIdentityService.class);
         install(new FactoryModuleBuilder().implement(RegionIdToAdminURISupplier.class,
                  RegionIdToAdminURIFromAccessForTypeAndVersion.class).build(RegionIdToAdminURISupplier.Factory.class));
      }

      // return any identity url.
      @Provides
      @Singleton
      @Identity
      protected Supplier<URI> provideStorageUrl(RegionIdToAdminURISupplier.Factory factory,
               @Named(KeystoneProperties.VERSION) String version) {
         return getLastValueInMap(factory.createForApiTypeAndVersion(ServiceType.IDENTITY, version));
      }
   }

   @Override
   protected void bindErrorHandlers() {
      bind(HttpErrorHandler.class).annotatedWith(Redirection.class).to(KeystoneErrorHandler.class);
      bind(HttpErrorHandler.class).annotatedWith(ClientError.class).to(KeystoneErrorHandler.class);
      bind(HttpErrorHandler.class).annotatedWith(ServerError.class).to(KeystoneErrorHandler.class);
   }
}
