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
package org.jclouds.deltacloud.config;

import static org.jclouds.Constants.PROPERTY_SESSION_INTERVAL;

import java.net.URI;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.deltacloud.DeltacloudAsyncClient;
import org.jclouds.deltacloud.DeltacloudClient;
import org.jclouds.deltacloud.collections.HardwareProfiles;
import org.jclouds.deltacloud.collections.Images;
import org.jclouds.deltacloud.collections.InstanceStates;
import org.jclouds.deltacloud.collections.Instances;
import org.jclouds.deltacloud.collections.Realms;
import org.jclouds.deltacloud.domain.DeltacloudCollection;
import org.jclouds.deltacloud.handlers.DeltacloudErrorHandler;
import org.jclouds.deltacloud.handlers.DeltacloudRedirectionRetryHandler;
import org.jclouds.http.HttpErrorHandler;
import org.jclouds.http.HttpRetryHandler;
import org.jclouds.http.annotation.ClientError;
import org.jclouds.http.annotation.Redirection;
import org.jclouds.http.annotation.ServerError;
import org.jclouds.rest.AuthorizationException;
import org.jclouds.rest.ConfiguresRestClient;
import org.jclouds.rest.config.RestClientModule;
import org.jclouds.rest.suppliers.MemoizedRetryOnTimeOutButNotOnAuthorizationExceptionSupplier;
import org.jclouds.util.Suppliers2;

import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.collect.Iterables;
import com.google.common.util.concurrent.Atomics;
import com.google.inject.Provides;

/**
 * Configures the deltacloud connection.
 * 
 * @author Adrian Cole
 */
@ConfiguresRestClient
public class DeltacloudRestClientModule extends RestClientModule<DeltacloudClient, DeltacloudAsyncClient> {

   @Override
   protected void bindErrorHandlers() {
      bind(HttpErrorHandler.class).annotatedWith(Redirection.class).to(DeltacloudErrorHandler.class);
      bind(HttpErrorHandler.class).annotatedWith(ClientError.class).to(DeltacloudErrorHandler.class);
      bind(HttpErrorHandler.class).annotatedWith(ServerError.class).to(DeltacloudErrorHandler.class);
   }

   @Override
   protected void bindRetryHandlers() {
      bind(HttpRetryHandler.class).annotatedWith(Redirection.class).to(DeltacloudRedirectionRetryHandler.class);
   }

   protected AtomicReference<AuthorizationException> authException = Atomics.newReference();

   @Provides
   @Singleton
   protected Supplier<Set<? extends DeltacloudCollection>> provideCollections(
         @Named(PROPERTY_SESSION_INTERVAL) long seconds, final DeltacloudClient client) {
      return MemoizedRetryOnTimeOutButNotOnAuthorizationExceptionSupplier.create(
            authException, new Supplier<Set<? extends DeltacloudCollection>>() {
               @Override
               public Set<? extends DeltacloudCollection> get() {
                  return client.getCollections();
               }
               @Override
               public String toString() {
                  return Objects.toStringHelper(client).add("method", "getCollections").toString();
               }
            }, seconds, TimeUnit.SECONDS);
   }

   /**
    * since the supplier is memoized, and there are no objects created here, this doesn't need to be
    * singleton.
    */
   @Provides
   @Images
   protected Supplier<URI> provideImageCollection(Supplier<Set<? extends DeltacloudCollection>> collectionSupplier) {
      return Suppliers2.compose(new FindCollectionWithRelAndReturnHref("images"), collectionSupplier);
   }

   public static class FindCollectionWithRelAndReturnHref implements Function<Set<? extends DeltacloudCollection>, URI> {
      private final String rel;

      public FindCollectionWithRelAndReturnHref(String rel) {
         this.rel = rel;
      }

      @Override
      public URI apply(Set<? extends DeltacloudCollection> arg0) {
         try {
            return Iterables.find(arg0, new Predicate<DeltacloudCollection>() {

               @Override
               public boolean apply(DeltacloudCollection arg0) {
                  return arg0.getRel().equals(rel);
               }

            }).getHref();
         } catch (NoSuchElementException e) {
            throw new NoSuchElementException("could not find rel " + rel + " in collections " + arg0);
         }
      }

   }

   @Provides
   @HardwareProfiles
   protected Supplier<URI> provideHardwareProfileCollection(Supplier<Set<? extends DeltacloudCollection>> collectionSupplier) {
      return Suppliers2.compose(new FindCollectionWithRelAndReturnHref("hardware_profiles"), collectionSupplier);
   }

   @Provides
   @Instances
   protected Supplier<URI> provideInstanceCollection(Supplier<Set<? extends DeltacloudCollection>> collectionSupplier) {
      return Suppliers2.compose(new FindCollectionWithRelAndReturnHref("instances"), collectionSupplier);
   }

   @Provides
   @Realms
   protected Supplier<URI> provideRealmCollection(Supplier<Set<? extends DeltacloudCollection>> collectionSupplier) {
      return Suppliers2.compose(new FindCollectionWithRelAndReturnHref("realms"), collectionSupplier);
   }

   @Provides
   @InstanceStates
   protected Supplier<URI> provideInstanceStateCollection(Supplier<Set<? extends DeltacloudCollection>> collectionSupplier) {
      return Suppliers2.compose(new FindCollectionWithRelAndReturnHref("instance_states"), collectionSupplier);
   }
}
