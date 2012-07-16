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
import org.jclouds.openstack.keystone.v2_0.KeystoneAsyncApi;
import org.jclouds.openstack.keystone.v2_0.KeystoneApi;
import org.jclouds.openstack.keystone.v2_0.features.ServiceAsyncApi;
import org.jclouds.openstack.keystone.v2_0.features.ServiceApi;
import org.jclouds.openstack.keystone.v2_0.features.TenantAsyncApi;
import org.jclouds.openstack.keystone.v2_0.features.TenantApi;
import org.jclouds.openstack.keystone.v2_0.features.TokenAsyncApi;
import org.jclouds.openstack.keystone.v2_0.features.TokenApi;
import org.jclouds.openstack.keystone.v2_0.features.UserAsyncApi;
import org.jclouds.openstack.keystone.v2_0.features.UserApi;
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
public class KeystoneRestClientModule<S extends KeystoneApi, A extends KeystoneAsyncApi> extends
         RestClientModule<S, A> {

   public static final Map<Class<?>, Class<?>> DELEGATE_MAP = ImmutableMap.<Class<?>, Class<?>> builder()
            .put(ServiceApi.class, ServiceAsyncApi.class).put(TokenApi.class, TokenAsyncApi.class)
            .put(UserApi.class, UserAsyncApi.class).put(TenantApi.class, TenantAsyncApi.class).build();

   @SuppressWarnings("unchecked")
   public KeystoneRestClientModule() {
      super(TypeToken.class.cast(TypeToken.of(KeystoneApi.class)), TypeToken.class.cast(TypeToken
               .of(KeystoneAsyncApi.class)), DELEGATE_MAP);
   }

   protected KeystoneRestClientModule(TypeToken<S> syncApiType, TypeToken<A> asyncApiType,
            Map<Class<?>, Class<?>> sync2Async) {
      super(syncApiType, asyncApiType, sync2Async);
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
