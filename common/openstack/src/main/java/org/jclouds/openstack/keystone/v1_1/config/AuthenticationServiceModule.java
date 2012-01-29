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
package org.jclouds.openstack.keystone.v1_1.config;

import static com.google.common.base.Throwables.propagate;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.Constants;
import org.jclouds.concurrent.RetryOnTimeOutExceptionFunction;
import org.jclouds.domain.Credentials;
import org.jclouds.http.RequiresHttp;
import org.jclouds.location.Provider;
import org.jclouds.openstack.Authentication;
import org.jclouds.openstack.keystone.v1_1.ServiceAsyncClient;
import org.jclouds.openstack.keystone.v1_1.domain.Auth;
import org.jclouds.rest.AsyncClientFactory;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.base.Throwables;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;

/**
 * 
 * @author Adrian Cole
 */
@RequiresHttp
public class AuthenticationServiceModule extends AbstractModule {

   @Override
   protected void configure() {
      bind(new TypeLiteral<Function<Credentials, Auth>>() {
      }).to(GetAuth.class);
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

   @Provides
   @Singleton
   protected ServiceAsyncClient provideServiceClient(AsyncClientFactory factory) {
      return factory.create(ServiceAsyncClient.class);
   }

   @Provides
   @Provider
   protected Credentials provideAuthenticationCredentials(@Named(Constants.PROPERTY_IDENTITY) String user,
            @Named(Constants.PROPERTY_CREDENTIAL) String key) {
      return new Credentials(user, key);
   }

   @Singleton
   public static class GetAuth extends RetryOnTimeOutExceptionFunction<Credentials, Auth> {
      
      // passing factory here to avoid a circular dependency on
      // OpenStackAuthAsyncClient resolving OpenStackAuthAsyncClient
      @Inject
      public GetAuth(final AsyncClientFactory factory) {
         super(new Function<Credentials, Auth>() {

            @Override
            public Auth apply(Credentials input) {
               try {
                  return factory.create(ServiceAsyncClient.class).authenticate(input.identity, input.credential).get();
               } catch (Exception e) {
                  throw Throwables.propagate(e);
               }
            }

            @Override
            public String toString() {
               return "authenticate()";
            }
         });

      }
   }

   @Provides
   @Singleton
   public LoadingCache<Credentials, Auth> provideAuthCache2(Function<Credentials, Auth> getAuth) {
      return CacheBuilder.newBuilder().expireAfterWrite(23, TimeUnit.HOURS).build(CacheLoader.from(getAuth));
   }

   @Provides
   @Singleton
   protected Supplier<Auth> provideAuthSupplier(final LoadingCache<Credentials, Auth> cache,
            @Provider final Credentials creds) {
      return new Supplier<Auth>() {
         @Override
         public Auth get() {
            try {
               return cache.get(creds);
            } catch (ExecutionException e) {
               throw propagate(e.getCause());
            }
         }
      };
   }

   @Provides
   @Singleton
   protected Auth provideAuth(Supplier<Auth> supplier) {
      return supplier.get();
   }

}