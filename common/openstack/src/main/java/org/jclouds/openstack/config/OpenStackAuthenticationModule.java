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
package org.jclouds.openstack.config;

import static com.google.common.base.Suppliers.memoizeWithExpiration;
import static org.jclouds.rest.config.BinderUtils.bindSyncToAsyncHttpApi;

import java.util.Date;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.date.TimeStamp;
import org.jclouds.domain.Credentials;
import org.jclouds.http.HttpRetryHandler;
import org.jclouds.http.annotation.ClientError;
import org.jclouds.location.Provider;
import org.jclouds.openstack.domain.AuthenticationResponse;
import org.jclouds.openstack.functions.URIFromAuthenticationResponseForService;
import org.jclouds.openstack.handlers.RetryOnRenew;
import org.jclouds.openstack.internal.Authentication;
import org.jclouds.openstack.internal.OpenStackAuthAsyncClient;
import org.jclouds.openstack.internal.OpenStackAuthClient;

import com.google.common.base.Supplier;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.assistedinject.FactoryModuleBuilder;

/**
 * Configures the Rackspace authentication service connection, including logging and http transport.
 * 
 * @author Adrian Cole
 */
public class OpenStackAuthenticationModule extends AbstractModule {

   @Override
   protected void configure() {
      // OpenStackAuthClient is used directly for filters and retry handlers, so let's bind it explicitly
      bindSyncToAsyncHttpApi(binder(), OpenStackAuthClient.class, OpenStackAuthAsyncClient.class);
      install(new FactoryModuleBuilder().build(URIFromAuthenticationResponseForService.Factory.class));
      bind(HttpRetryHandler.class).annotatedWith(ClientError.class).to(RetryOnRenew.class);
   }

   /**
    * borrowing concurrency code to ensure that caching takes place properly
    */
   @Provides
   @Singleton
   @Authentication
   protected Supplier<String> provideAuthenticationTokenCache(final Supplier<AuthenticationResponse> supplier)
            throws InterruptedException, ExecutionException, TimeoutException {
      return new Supplier<String>() {
         public String get() {
            return supplier.get().getAuthToken();
         }
      };
   }

   @Singleton
   public static class GetAuthenticationResponse extends CacheLoader<Credentials, AuthenticationResponse> {
      private final OpenStackAuthClient client;

      @Inject
      public GetAuthenticationResponse(final OpenStackAuthClient client) {
         this.client = client;
      }

      @Override
      public AuthenticationResponse load(Credentials input) {
         return client.authenticate(input.identity, input.credential);
      }

      @Override
      public String toString() {
         return "authenticate()";
      }

   }

   @Provides
   @Singleton
   public LoadingCache<Credentials, AuthenticationResponse> provideAuthenticationResponseCache(
         GetAuthenticationResponse getAuthenticationResponse) {
      return CacheBuilder.newBuilder().expireAfterWrite(23, TimeUnit.HOURS).build(getAuthenticationResponse);
   }

   @Provides
   @Singleton
   protected Supplier<AuthenticationResponse> provideAuthenticationResponseSupplier(
         final LoadingCache<Credentials, AuthenticationResponse> cache, @Provider final Supplier<Credentials> creds) {
      return new Supplier<AuthenticationResponse>() {
         @Override
         public AuthenticationResponse get() {
            return cache.getUnchecked(creds.get());
         }
      };
   }

   @Provides
   @Singleton
   @TimeStamp
   protected Supplier<Date> provideCacheBusterDate() {
      return memoizeWithExpiration(new Supplier<Date>() {
         public Date get() {
            return new Date();
         }
      }, 1, TimeUnit.SECONDS);
   }

}
