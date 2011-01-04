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
import static com.google.common.collect.Maps.uniqueIndex;
import static com.google.common.collect.Sets.newLinkedHashSet;

import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.domain.Location;
import org.jclouds.domain.LocationScope;
import org.jclouds.domain.internal.LocationImpl;
import org.jclouds.location.Provider;
import org.jclouds.location.Zone;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class ZoneToRegionToProviderOrJustProvider implements Supplier<Set<? extends Location>> {
   private final Map<String, String> zoneToRegion;
   private final String providerName;

   @Inject
   ZoneToRegionToProviderOrJustProvider(@Zone Map<String, String> zoneToRegion, @Provider String providerName) {
      this.zoneToRegion = checkNotNull(zoneToRegion, "zoneToRegion");
      this.providerName = checkNotNull(providerName, "providerName");
   }

   @Override
   public Set<? extends Location> get() {
      Location provider = new LocationImpl(LocationScope.PROVIDER, providerName, providerName, null);
      if (zoneToRegion.size() == 0)
         return ImmutableSet.of(provider);
      Set<Location> providers = newLinkedHashSet();
      for (String region : newLinkedHashSet(zoneToRegion.values())) {
         providers.add(new LocationImpl(LocationScope.REGION, region, region, provider));
      }
      ImmutableMap<String, Location> idToLocation = uniqueIndex(providers, new Function<Location, String>() {
         @Override
         public String apply(Location from) {
            return from.getId();
         }
      });
      for (String zone : zoneToRegion.keySet()) {
         providers.add(new LocationImpl(LocationScope.ZONE, zone, zone, idToLocation.get(zoneToRegion.get(zone))));
      }
      return providers;
   }

}