/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.rackspace.config;

import static org.jclouds.rackspace.reference.RackspaceConstants.PROPERTY_RACKSPACE_ENDPOINT;
import static org.jclouds.rackspace.reference.RackspaceConstants.PROPERTY_RACKSPACE_KEY;
import static org.jclouds.rackspace.reference.RackspaceConstants.PROPERTY_RACKSPACE_USER;

import java.net.URI;
import java.util.Date;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.concurrent.ExpirableSupplier;
import org.jclouds.concurrent.RetryOnTimeOutExceptionSupplier;
import org.jclouds.date.TimeStamp;
import org.jclouds.http.RequiresHttp;
import org.jclouds.rackspace.Authentication;
import org.jclouds.rackspace.CloudFiles;
import org.jclouds.rackspace.CloudFilesCDN;
import org.jclouds.rackspace.CloudServers;
import org.jclouds.rackspace.RackspaceAuthentication;
import org.jclouds.rackspace.RackspaceAuthentication.AuthenticationResponse;
import org.jclouds.rest.RestClientFactory;

import com.google.common.base.Supplier;
import com.google.common.base.Throwables;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;

/**
 * Configures the Rackspace authentication service connection, including logging and http transport.
 * 
 * @author Adrian Cole
 */
@RequiresHttp
public class RackspaceAuthenticationRestModule extends AbstractModule {

   @Override
   protected void configure() {
   }

   /**
    * borrowing concurrency code to ensure that caching takes place properly
    */
   @Provides
   @Singleton
   @Authentication
   Supplier<String> provideAuthenticationTokenCache(final Supplier<AuthenticationResponse> supplier)
            throws InterruptedException, ExecutionException, TimeoutException {
      return new Supplier<String>() {
         public String get() {
            return supplier.get().getAuthToken();
         }
      };
   }

   @Provides
   @Singleton
   Supplier<AuthenticationResponse> provideAuthenticationResponseCache(
            final RestClientFactory factory, @Named(PROPERTY_RACKSPACE_USER) final String user,
            @Named(PROPERTY_RACKSPACE_KEY) final String key) {
      return new ExpirableSupplier<AuthenticationResponse>(
               new RetryOnTimeOutExceptionSupplier<AuthenticationResponse>(
                        new Supplier<AuthenticationResponse>() {
                           public AuthenticationResponse get() {
                              try {
                                 ListenableFuture<AuthenticationResponse> response = factory
                                          .create(RackspaceAuthentication.class).authenticate(user,
                                                   key);
                                 return response.get(30, TimeUnit.SECONDS);
                              } catch (Exception e) {
                                 Throwables.propagate(e);
                                 assert false : e;
                                 return null;
                              }
                           }
                        }), 23, TimeUnit.HOURS);
   }

   @Provides
   @Singleton
   @TimeStamp
   Supplier<Date> provideCacheBusterDate() {
      return new ExpirableSupplier<Date>(new Supplier<Date>() {
         public Date get() {
            return new Date();
         }
      }, 1, TimeUnit.SECONDS);
   }

   @Provides
   @Singleton
   @Authentication
   protected URI provideAuthenticationURI(@Named(PROPERTY_RACKSPACE_ENDPOINT) String endpoint) {
      return URI.create(endpoint);
   }

   @Provides
   @Singleton
   protected AuthenticationResponse provideAuthenticationResponse(
            Supplier<AuthenticationResponse> supplier) throws InterruptedException,
            ExecutionException, TimeoutException {
      return supplier.get();
   }

   @Provides
   @Singleton
   @CloudFiles
   protected URI provideStorageUrl(AuthenticationResponse response) {
      return response.getStorageUrl();
   }

   @Provides
   @Singleton
   @CloudServers
   protected URI provideServerUrl(AuthenticationResponse response) {
      return response.getServerManagementUrl();
   }

   @Provides
   @Singleton
   @CloudFilesCDN
   protected URI provideCDNUrl(AuthenticationResponse response) {
      return response.getCDNManagementUrl();
   }
}