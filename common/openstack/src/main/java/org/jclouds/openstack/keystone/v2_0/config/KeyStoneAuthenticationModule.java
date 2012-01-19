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
import org.jclouds.openstack.keystone.v2_0.ServiceAsyncClient;
import org.jclouds.openstack.keystone.v2_0.domain.Access;
import org.jclouds.openstack.keystone.v2_0.domain.PasswordCredentials;
import org.jclouds.rest.AsyncClientFactory;

import com.google.common.base.Function;
import com.google.common.base.Splitter;
import com.google.common.base.Supplier;
import com.google.common.base.Throwables;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Iterables;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;

/**
 * 
 * @author Adrian Cole
 */
@RequiresHttp
public class KeyStoneAuthenticationModule extends AbstractModule {

   @Override
   protected void configure() {
      bind(new TypeLiteral<Function<Credentials, Access>>() {
      }).to(GetAccess.class);
   }

   /**
    * borrowing concurrency code to ensure that caching takes place properly
    */
   @Provides
   @Singleton
   @Authentication
   protected Supplier<String> provideAuthenticationTokenCache(final Supplier<Access> supplier)
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
   public static class GetAccess extends RetryOnTimeOutExceptionFunction<Credentials, Access> {

      @Inject
      public GetAccess(final ServiceAsyncClient client) {
         super(new Function<Credentials, Access>() {

            @Override
            public Access apply(Credentials input) {
               // TODO: nice error messages, etc.
               Iterable<String> usernameTenantId = Splitter.on(':').split(input.identity);
               String username = Iterables.get(usernameTenantId, 0);
               String tenantId = Iterables.get(usernameTenantId, 1);
               PasswordCredentials passwordCredentials = PasswordCredentials.createWithUsernameAndPassword(username,
                        input.credential);
               try {
                  return client.authenticateTenantWithCredentials(tenantId, passwordCredentials).get();
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
   public LoadingCache<Credentials, Access> provideAccessCache2(Function<Credentials, Access> getAccess) {
      return CacheBuilder.newBuilder().expireAfterWrite(23, TimeUnit.HOURS).build(CacheLoader.from(getAccess));
   }

   @Provides
   @Singleton
   protected Supplier<Access> provideAccessSupplier(final LoadingCache<Credentials, Access> cache,
            @Provider final Credentials creds) {
      return new Supplier<Access>() {
         @Override
         public Access get() {
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
   protected Access provideAccess(Supplier<Access> supplier) {
      return supplier.get();
   }

}