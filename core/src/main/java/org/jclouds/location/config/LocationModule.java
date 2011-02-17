/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
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

package org.jclouds.location.config;

import static org.jclouds.Constants.PROPERTY_SESSION_INTERVAL;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.collect.Memoized;
import org.jclouds.domain.Location;
import org.jclouds.rest.AuthorizationException;
import org.jclouds.rest.suppliers.RetryOnTimeOutButNotOnAuthorizationExceptionSupplier;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.Maps;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;

/**
 * 
 * @author Adrian Cole
 */
public class LocationModule extends AbstractModule {
   protected final AtomicReference<AuthorizationException> authException;

   public LocationModule() {
      this(new AtomicReference<AuthorizationException>());
   }

   public LocationModule(AtomicReference<AuthorizationException> authException) {
      this.authException = authException;
   }

   @Provides
   @Singleton
   protected Supplier<Map<String, ? extends Location>> provideLocationMap(
            @Memoized Supplier<Set<? extends Location>> locations) {
      return Suppliers.compose(new Function<Set<? extends Location>, Map<String, ? extends Location>>() {

         @Override
         public Map<String, ? extends Location> apply(Set<? extends Location> from) {
            return Maps.uniqueIndex(from, new Function<Location, String>() {

               @Override
               public String apply(Location from) {
                  return from.getId();
               }

            });
         }

      }, locations);
   }

   @Provides
   @Singleton
   @Memoized
   protected Supplier<Set<? extends Location>> supplyLocationCache(@Named(PROPERTY_SESSION_INTERVAL) long seconds,
            final Supplier<Set<? extends Location>> locationSupplier) {
      return new RetryOnTimeOutButNotOnAuthorizationExceptionSupplier<Set<? extends Location>>(authException, seconds,
               new Supplier<Set<? extends Location>>() {
                  @Override
                  public Set<? extends Location> get() {
                     return locationSupplier.get();
                  }
               });
   }

   @Override
   protected void configure() {
   }
}