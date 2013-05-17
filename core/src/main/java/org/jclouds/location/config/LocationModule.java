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
package org.jclouds.location.config;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.Constants.PROPERTY_SESSION_INTERVAL;

import java.net.URI;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.collect.Memoized;
import org.jclouds.domain.Location;
import org.jclouds.location.Iso3166;
import org.jclouds.location.Provider;
import org.jclouds.location.Region;
import org.jclouds.location.Zone;
import org.jclouds.location.predicates.RegionIdFilter;
import org.jclouds.location.predicates.ZoneIdFilter;
import org.jclouds.location.suppliers.ImplicitLocationSupplier;
import org.jclouds.location.suppliers.ImplicitRegionIdSupplier;
import org.jclouds.location.suppliers.LocationIdToIso3166CodesSupplier;
import org.jclouds.location.suppliers.LocationsSupplier;
import org.jclouds.location.suppliers.ProviderURISupplier;
import org.jclouds.location.suppliers.RegionIdToURISupplier;
import org.jclouds.location.suppliers.RegionIdToZoneIdsSupplier;
import org.jclouds.location.suppliers.RegionIdsSupplier;
import org.jclouds.location.suppliers.ZoneIdToURISupplier;
import org.jclouds.location.suppliers.ZoneIdsSupplier;
import org.jclouds.reflect.InvocationSuccess;
import org.jclouds.rest.AuthorizationException;
import org.jclouds.rest.functions.ImplicitOptionalConverter;
import org.jclouds.rest.suppliers.MemoizedRetryOnTimeOutButNotOnAuthorizationExceptionSupplier;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.Sets;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;

/**
 * All of these are memoized as locations do not change often at runtime. Note that we take care to
 * propagate authorization exceptions. this is so that we do not lock out the account.
 * 
 * @author Adrian Cole
 */
public class LocationModule extends AbstractModule {

   @Override
   protected void configure() {
      bind(new TypeLiteral<Function<InvocationSuccess, Optional<Object>>>(){}).to(ImplicitOptionalConverter.class);
   }

   @Provides
   @Singleton
   @Iso3166
   protected Supplier<Map<String, Supplier<Set<String>>>> isoCodesSupplier(
            AtomicReference<AuthorizationException> authException, @Named(PROPERTY_SESSION_INTERVAL) long seconds,
            LocationIdToIso3166CodesSupplier uncached) {
      return MemoizedRetryOnTimeOutButNotOnAuthorizationExceptionSupplier.create(authException, uncached, seconds,
               TimeUnit.SECONDS);
   }

   @Provides
   @Singleton
   @Provider
   protected Supplier<URI> provideProvider(AtomicReference<AuthorizationException> authException,
            @Named(PROPERTY_SESSION_INTERVAL) long seconds, ProviderURISupplier uncached) {
      return MemoizedRetryOnTimeOutButNotOnAuthorizationExceptionSupplier.create(authException, uncached, seconds,
               TimeUnit.SECONDS);
   }

   @Provides
   @Singleton
   protected Supplier<Location> implicitLocationSupplier(AtomicReference<AuthorizationException> authException,
            @Named(PROPERTY_SESSION_INTERVAL) long seconds, ImplicitLocationSupplier uncached) {
      return MemoizedRetryOnTimeOutButNotOnAuthorizationExceptionSupplier.create(authException, uncached, seconds,
               TimeUnit.SECONDS);
   }

   @Provides
   @Singleton
   // TODO: we should eventually get rid of memoized as an annotation, as it is confusing
   @Memoized
   protected Supplier<Set<? extends Location>> memoizedLocationsSupplier(
            AtomicReference<AuthorizationException> authException, @Named(PROPERTY_SESSION_INTERVAL) long seconds,
            LocationsSupplier uncached) {
      return MemoizedRetryOnTimeOutButNotOnAuthorizationExceptionSupplier.create(authException, uncached, seconds,
               TimeUnit.SECONDS);
   }

   @Provides
   @Singleton
   @Region
   protected Supplier<Set<String>> regionIdsSupplier(AtomicReference<AuthorizationException> authException,
            @Named(PROPERTY_SESSION_INTERVAL) long seconds, RegionIdFilter filter, RegionIdsSupplier uncached) {
      return MemoizedRetryOnTimeOutButNotOnAuthorizationExceptionSupplier.create(authException,
               Suppliers.compose(new FilterStrings(filter), uncached), seconds, TimeUnit.SECONDS);
   }
   
   @Provides
   @Singleton
   @Zone
   protected Supplier<Set<String>> zoneIdsSupplier(
            AtomicReference<AuthorizationException> authException, @Named(PROPERTY_SESSION_INTERVAL) long seconds,
            ZoneIdFilter filter, ZoneIdsSupplier uncached) {
      return MemoizedRetryOnTimeOutButNotOnAuthorizationExceptionSupplier.create(authException,
               Suppliers.compose(new FilterStrings(filter), uncached), seconds, TimeUnit.SECONDS);
   }

   static class FilterStrings implements Function<Set<String>, Set<String>>{
      public final Predicate<String> filter;

      public FilterStrings(Predicate<String> filter) {
         this.filter = checkNotNull(filter, "filter");
      }


      @Override
      public Set<String> apply(Set<String> input) {
         return Sets.filter(input, filter);
      }

      @Override
      public String toString() {
         return "filterStrings(" + filter + ")";
      }

   }
   
   @Provides
   @Singleton
   @Region
   protected Supplier<Map<String, Supplier<URI>>> regionIdToURISupplier(
            AtomicReference<AuthorizationException> authException, @Named(PROPERTY_SESSION_INTERVAL) long seconds,
            RegionIdToURISupplier uncached) {
      return MemoizedRetryOnTimeOutButNotOnAuthorizationExceptionSupplier.create(authException, uncached, seconds,
               TimeUnit.SECONDS);
   }

   @Provides
   @Singleton
   @Region
   protected Supplier<String> implicitRegionIdSupplier(AtomicReference<AuthorizationException> authException,
            @Named(PROPERTY_SESSION_INTERVAL) long seconds, ImplicitRegionIdSupplier uncached) {
      return MemoizedRetryOnTimeOutButNotOnAuthorizationExceptionSupplier.create(authException, uncached, seconds,
               TimeUnit.SECONDS);
   }


   @Provides
   @Singleton
   @Zone
   protected Supplier<Map<String, Supplier<Set<String>>>> regionIdToZoneIdsSupplier(
            AtomicReference<AuthorizationException> authException, @Named(PROPERTY_SESSION_INTERVAL) long seconds,
            RegionIdToZoneIdsSupplier uncached) {
      return MemoizedRetryOnTimeOutButNotOnAuthorizationExceptionSupplier.create(authException, uncached, seconds,
               TimeUnit.SECONDS);
   }

   @Provides
   @Singleton
   @Zone
   protected Supplier<Map<String, Supplier<URI>>> zoneIdToURISupplier(
            AtomicReference<AuthorizationException> authException, @Named(PROPERTY_SESSION_INTERVAL) long seconds,
            ZoneIdToURISupplier uncached) {
      return MemoizedRetryOnTimeOutButNotOnAuthorizationExceptionSupplier.create(authException, uncached, seconds,
               TimeUnit.SECONDS);
   }
}
