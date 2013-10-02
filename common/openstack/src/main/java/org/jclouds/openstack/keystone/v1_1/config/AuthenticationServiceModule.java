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
package org.jclouds.openstack.keystone.v1_1.config;

import static org.jclouds.rest.config.BinderUtils.bindSyncToAsyncHttpApi;
import static org.jclouds.Constants.PROPERTY_SESSION_INTERVAL;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.domain.Credentials;
import org.jclouds.http.HttpRetryHandler;
import org.jclouds.http.annotation.ClientError;
import org.jclouds.location.Provider;
import org.jclouds.location.suppliers.ImplicitRegionIdSupplier;
import org.jclouds.location.suppliers.RegionIdToURISupplier;
import org.jclouds.openstack.internal.Authentication;
import org.jclouds.openstack.keystone.v1_1.AuthenticationAsyncClient;
import org.jclouds.openstack.keystone.v1_1.AuthenticationClient;
import org.jclouds.openstack.keystone.v1_1.domain.Auth;
import org.jclouds.openstack.keystone.v1_1.handlers.RetryOnRenew;
import org.jclouds.openstack.keystone.v1_1.suppliers.RegionIdToURIFromAuthForServiceSupplier;
import org.jclouds.openstack.keystone.v1_1.suppliers.V1DefaultRegionIdSupplier;

import com.google.common.base.Supplier;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.inject.AbstractModule;
import com.google.inject.name.Named;
import com.google.inject.Provides;
import com.google.inject.assistedinject.FactoryModuleBuilder;

/**
 * 
 * @author Adrian Cole
 */
public class AuthenticationServiceModule extends AbstractModule {

   @Override
   protected void configure() {
      // ServiceClient is used directly for filters and retry handlers, so let's bind it explicitly
      bindSyncToAsyncHttpApi(binder(), AuthenticationClient.class, AuthenticationAsyncClient.class);
      install(new FactoryModuleBuilder().implement(RegionIdToURISupplier.class,
               RegionIdToURIFromAuthForServiceSupplier.class).build(RegionIdToURISupplier.Factory.class));
      install(new FactoryModuleBuilder().implement(ImplicitRegionIdSupplier.class, V1DefaultRegionIdSupplier.class)
               .build(V1DefaultRegionIdSupplier.Factory.class));
      bind(HttpRetryHandler.class).annotatedWith(ClientError.class).to(RetryOnRenew.class);
   }

   /**
    * borrowing concurrency code to ensure that caching takes place properly
    */
   @Provides
   @Singleton
   @Authentication
   protected Supplier<String> provideAuthenticationTokenCache(final Supplier<Auth> supplier)
            throws InterruptedException, ExecutionException, TimeoutException {
      return new Supplier<String>() {
         public String get() {
            return supplier.get().getToken().getId();
         }
      };
   }

   @Singleton
   public static class GetAuth extends CacheLoader<Credentials, Auth> {

      private final AuthenticationClient client;

      @Inject
      public GetAuth(final AuthenticationClient client) {
         this.client = client;
      }

      @Override
      public Auth load(Credentials input) {
         return client.authenticate(input.identity, input.credential);
      }

      @Override
      public String toString() {
         return "authenticate()";
      }
   }

   @Provides
   @Singleton
   protected LoadingCache<Credentials, Auth> provideAuthCache(GetAuth getAuth,
         @Named(PROPERTY_SESSION_INTERVAL) long sessionInterval) {
      return CacheBuilder.newBuilder().expireAfterWrite(sessionInterval, TimeUnit.SECONDS).build(getAuth);
   }

   @Provides
   @Singleton
   protected Supplier<Auth> provideAuthSupplier(final LoadingCache<Credentials, Auth> cache,
         @Provider final Supplier<Credentials> creds) {
      return new Supplier<Auth>() {
         @Override
         public Auth get() {
            return cache.getUnchecked(creds.get());
         }
      };
   }
}
