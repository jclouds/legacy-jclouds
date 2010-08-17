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

package org.jclouds.compute.config;

import static org.jclouds.Constants.PROPERTY_SESSION_INTERVAL;
import static org.jclouds.compute.domain.OsFamily.UBUNTU;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.compute.domain.ComputeMetadata;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.Size;
import org.jclouds.compute.domain.TemplateBuilder;
import org.jclouds.domain.Location;
import org.jclouds.domain.LocationScope;
import org.jclouds.rest.AuthorizationException;
import org.jclouds.rest.suppliers.RetryOnTimeOutButNotOnAuthorizationExceptionSupplier;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;

/**
 * 
 * @author Adrian Cole
 */
public abstract class BaseComputeServiceContextModule extends AbstractModule {

   protected abstract Supplier<Set<? extends Image>> getSourceImageSupplier(Injector injector);

   protected abstract Supplier<Set<? extends Size>> getSourceSizeSupplier(Injector injector);

   /**
    * By default allows you to use a static set of locations bound to Set<? extends Location>
    */
   protected Supplier<Set<? extends Location>> getSourceLocationSupplier(Injector injector) {
      Set<? extends Location> locations = injector.getInstance(Key.get(new TypeLiteral<Set<? extends Location>>() {
      }));
      return Suppliers.<Set<? extends Location>> ofInstance(locations);
   }

   @Provides
   @Named("DEFAULT")
   protected TemplateBuilder provideTemplate(Injector injector, TemplateBuilder template) {
      return template.osFamily(UBUNTU);
   }

   @Provides
   @Named("NAMING_CONVENTION")
   @Singleton
   protected String provideNamingConvention() {
      return "%s-%s";
   }

   protected AtomicReference<AuthorizationException> authException = new AtomicReference<AuthorizationException>();

   @Provides
   @Singleton
   protected Supplier<Map<String, ? extends Image>> provideImageMap(Supplier<Set<? extends Image>> images) {
      return Suppliers.compose(new Function<Set<? extends Image>, Map<String, ? extends Image>>() {

         @Override
         public Map<String, ? extends Image> apply(Set<? extends Image> from) {
            return Maps.uniqueIndex(from, new Function<Image, String>() {

               @Override
               public String apply(Image from) {
                  return from.getId();
               }

            });
         }

      }, images);
   }

   @Provides
   @Singleton
   protected Supplier<Set<? extends Image>> supplyImageCache(@Named(PROPERTY_SESSION_INTERVAL) long seconds,
            final Injector injector) {
      return new RetryOnTimeOutButNotOnAuthorizationExceptionSupplier<Set<? extends Image>>(authException, seconds,
               new Supplier<Set<? extends Image>>() {
                  @Override
                  public Set<? extends Image> get() {
                     return getSourceImageSupplier(injector).get();
                  }
               });
   }

   @Provides
   @Singleton
   protected Supplier<Map<String, ? extends Location>> provideLocationMap(Supplier<Set<? extends Location>> locations) {
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
   protected Supplier<Set<? extends Location>> supplyLocationCache(@Named(PROPERTY_SESSION_INTERVAL) long seconds,
            final Injector injector) {
      return new RetryOnTimeOutButNotOnAuthorizationExceptionSupplier<Set<? extends Location>>(authException, seconds,
               new Supplier<Set<? extends Location>>() {
                  @Override
                  public Set<? extends Location> get() {
                     return getSourceLocationSupplier(injector).get();
                  }
               });
   }

   @Provides
   @Singleton
   protected Supplier<Map<String, ? extends Size>> provideSizeMap(Supplier<Set<? extends Size>> sizes) {
      return Suppliers.compose(new Function<Set<? extends Size>, Map<String, ? extends Size>>() {

         @Override
         public Map<String, ? extends Size> apply(Set<? extends Size> from) {
            return Maps.uniqueIndex(from, new Function<Size, String>() {

               @Override
               public String apply(Size from) {
                  return from.getId();
               }

            });
         }

      }, sizes);
   }

   @Provides
   @Singleton
   protected Supplier<Set<? extends Size>> supplySizeCache(@Named(PROPERTY_SESSION_INTERVAL) long seconds,
            final Injector injector) {
      return new RetryOnTimeOutButNotOnAuthorizationExceptionSupplier<Set<? extends Size>>(authException, seconds,
               new Supplier<Set<? extends Size>>() {
                  @Override
                  public Set<? extends Size> get() {
                     return getSourceSizeSupplier(injector).get();
                  }
               });
   }

   @Provides
   @Singleton
   protected Function<ComputeMetadata, String> indexer() {
      return new Function<ComputeMetadata, String>() {
         @Override
         public String apply(ComputeMetadata from) {
            return from.getProviderId();
         }
      };
   }

   @Provides
   @Singleton
   protected Supplier<Location> supplyDefaultLocation(Injector injector, Supplier<Set<? extends Location>> locations) {
      return Suppliers.compose(new Function<Set<? extends Location>, Location>() {

         @Override
         public Location apply(Set<? extends Location> from) {
            return Iterables.find(from, new Predicate<Location>() {

               @Override
               public boolean apply(Location input) {
                  return input.getScope() == LocationScope.ZONE;
               }

            });
         }

      }, locations);

   }

}