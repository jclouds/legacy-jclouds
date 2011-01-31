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

package org.jclouds.location.suppliers;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.getOnlyElement;

import java.util.NoSuchElementException;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.collect.Memoized;
import org.jclouds.domain.Location;
import org.jclouds.domain.LocationScope;
import org.jclouds.location.Region;

import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.collect.Iterables;
import com.google.inject.Injector;
import com.google.inject.Key;

/**
 * @author Adrian Cole
 */
@Singleton
public class OnlyLocationOrFirstZoneOrRegionMatchingRegionId implements Supplier<Location> {
   @Singleton
   public static final class IsRegionAndIdEqualsOrIsZoneParentIdEquals implements Predicate<Location> {

      private final String region;

      @Inject
      IsRegionAndIdEqualsOrIsZoneParentIdEquals(@Region String region) {
         this.region = checkNotNull(region, "region");
      }

      @Override
      public boolean apply(Location input) {
         switch (input.getScope()) {
            case ZONE:
               return input.getParent().getId().equals(region);
            case REGION:
               return input.getId().equals(region);
            default:
               return false;
         }
      }

      @Override
      public String toString() {
         return "isRegionAndIdEqualsOrIsZoneParentIdEquals(" + region + ")";
      }
   }

   private final Injector injector;
   private final Supplier<Set<? extends Location>> locationsSupplier;

   @Inject
   OnlyLocationOrFirstZoneOrRegionMatchingRegionId(Injector injector,
            @Memoized Supplier<Set<? extends Location>> locationsSupplier) {
      this.injector = checkNotNull(injector, "injector");
      this.locationsSupplier = checkNotNull(locationsSupplier, "locationsSupplier");
   }

   @Override
   @Singleton
   public Location get() {
      Set<? extends Location> locations = locationsSupplier.get();
      if (locationsSupplier.get().size() == 1)
         return getOnlyElement(locationsSupplier.get());
      IsRegionAndIdEqualsOrIsZoneParentIdEquals matcher = null;
      try {
         String region = injector.getInstance(Key.get(String.class, Region.class));
         if (region == null)
            return Iterables.get(locationsSupplier.get(), 0);
         matcher = injector.getInstance(IsRegionAndIdEqualsOrIsZoneParentIdEquals.class);
         Location toReturn = Iterables.find(locations, matcher);
         return toReturn.getScope() == LocationScope.REGION ? toReturn : toReturn.getParent();
      } catch (NoSuchElementException e) {
         throw new IllegalStateException(String.format("region %s not found in %s", matcher, locations));
      }
   }
}