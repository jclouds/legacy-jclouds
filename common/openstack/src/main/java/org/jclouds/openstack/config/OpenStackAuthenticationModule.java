/**
 *
 * Copyright (C) 2011 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */
package org.jclouds.openstack.config;

import java.util.Date;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.Constants;
import org.jclouds.concurrent.RetryOnTimeOutExceptionSupplier;
import org.jclouds.date.TimeStamp;
import org.jclouds.http.RequiresHttp;
import org.jclouds.openstack.Authentication;
import org.jclouds.openstack.OpenStackAuthAsyncClient;
import org.jclouds.openstack.OpenStackAuthAsyncClient.AuthenticationResponse;
import org.jclouds.rest.AsyncClientFactory;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.base.Throwables;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;

/**
 * Configures the Rackspace authentication service connection, including logging and http transport.
 * 
 * @author Adrian Cole
 */
@RequiresHttp
public class OpenStackAuthenticationModule extends AbstractModule {

   @Override
   protected void configure() {
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
   public static class GetAuthenticationResponse implements Supplier<AuthenticationResponse> {
      protected final OpenStackAuthAsyncClient client;
      protected final String user;
      protected final String key;

      @Inject
      public GetAuthenticationResponse(AsyncClientFactory factory, @Named(Constants.PROPERTY_IDENTITY) String user,
               @Named(Constants.PROPERTY_CREDENTIAL) String key) {
         this.client = factory.create(OpenStackAuthAsyncClient.class);
         this.user = user;
         this.key = key;
      }

      @Override
      public AuthenticationResponse get() {
         try {
            Future<AuthenticationResponse> response = authenticate();
            return response.get(30, TimeUnit.SECONDS);
         } catch (Exception e) {
            Throwables.propagate(e);
            assert false : e;
            return null;
         }
      }

      protected Future<AuthenticationResponse> authenticate() {
         return client.authenticate(user, key);
      }

   }

   @Provides
   @Singleton
   Supplier<AuthenticationResponse> provideAuthenticationResponseCache(
            final GetAuthenticationResponse getAuthenticationResponse) {
      return Suppliers.memoizeWithExpiration(new RetryOnTimeOutExceptionSupplier<AuthenticationResponse>(
               getAuthenticationResponse), 23, TimeUnit.HOURS);
   }

   @Provides
   @Singleton
   @TimeStamp
   protected Supplier<Date> provideCacheBusterDate() {
      return Suppliers.memoizeWithExpiration(new Supplier<Date>() {
         public Date get() {
            return new Date();
         }
      }, 1, TimeUnit.SECONDS);
   }

   @Provides
   @Singleton
   protected AuthenticationResponse provideAuthenticationResponse(Supplier<AuthenticationResponse> supplier)
            throws InterruptedException, ExecutionException, TimeoutException {
      return supplier.get();
   }

}