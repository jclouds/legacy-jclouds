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
package org.jclouds.location.suppliers.implicit;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.getOnlyElement;
import static com.google.common.collect.Iterables.transform;
import static org.jclouds.location.predicates.LocationPredicates.isRegion;
import static org.jclouds.location.predicates.LocationPredicates.isZone;
import static org.jclouds.location.predicates.LocationPredicates.isZoneOrRegionWhereRegionIdEquals;

import java.util.NoSuchElementException;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.collect.Memoized;
import org.jclouds.domain.Location;
import org.jclouds.domain.LocationScope;
import org.jclouds.location.Region;
import org.jclouds.location.functions.ToIdAndScope;
import org.jclouds.location.suppliers.ImplicitLocationSupplier;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.base.Supplier;
import com.google.common.collect.Iterables;

/**
 * @author Adrian Cole
 */
@Singleton
public class OnlyLocationOrFirstRegionOptionallyMatchingRegionId implements ImplicitLocationSupplier {
   private final Supplier<String>  regionSupplier;
   private final Supplier<Set<? extends Location>> locationsSupplier;

   @Inject
   OnlyLocationOrFirstRegionOptionallyMatchingRegionId(@Region Supplier<String> regionSupplier,
            @Memoized Supplier<Set<? extends Location>> locationsSupplier) {
      this.regionSupplier = checkNotNull(regionSupplier, "regionSupplier");
      this.locationsSupplier = checkNotNull(locationsSupplier, "locationsSupplier");
   }

   @Override
   @Singleton
   public Location get() {
      String region = regionSupplier.get();
      Predicate<Location> locationPredicate = region == null ? Predicates.<Location>or(isZone(), isRegion())
               : isZoneOrRegionWhereRegionIdEquals(region);
      Set<? extends Location> locations = locationsSupplier.get();
      if (locationsSupplier.get().size() == 1)
         return getOnlyElement(locationsSupplier.get());
      try {
         Location toReturn = Iterables.find(locations, locationPredicate);
         return toReturn.getScope() == LocationScope.REGION ? toReturn : toReturn.getParent();
      } catch (NoSuchElementException e) {
         throw new NoSuchElementException(String.format("couldn't find region matching %s in %s", locationPredicate,
               transform(locations, ToIdAndScope.INSTANCE)));
      }
   }
}
