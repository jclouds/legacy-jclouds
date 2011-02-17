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

import java.net.URI;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.domain.Location;
import org.jclouds.domain.LocationBuilder;
import org.jclouds.domain.LocationScope;
import org.jclouds.location.Iso3166;
import org.jclouds.location.Provider;
import org.jclouds.location.Region;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.ImmutableSet.Builder;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class RegionToProviderOrJustProvider extends JustProvider {
   private final Set<String> regions;
   private final Map<String, Set<String>> isoCodesById;

   @Inject
   public RegionToProviderOrJustProvider(@Iso3166 Set<String> isoCodes, @Provider String providerName,
            @Provider URI endpoint, @Region Set<String> regions, @Iso3166 Map<String, Set<String>> isoCodesById) {
      super(isoCodes, providerName, endpoint);
      this.regions = checkNotNull(regions, "regions");
      this.isoCodesById = checkNotNull(isoCodesById, "isoCodesById");
   }

   @Override
   public Set<? extends Location> get() {
      return buildJustProviderOrRegions().build();
   }

   protected Builder<Location> buildJustProviderOrRegions() {
      Builder<Location> locations = ImmutableSet.builder();
      Location provider = Iterables.getOnlyElement(super.get());
      if (regions.size() == 0)
         return locations.add(provider);
      else
         for (String region : regions) {
            LocationBuilder builder = new LocationBuilder().scope(LocationScope.REGION).id(region).description(region)
                     .parent(provider);
            if (isoCodesById.containsKey(region))
               builder.iso3166Codes(isoCodesById.get(region));
            locations.add(builder.build());
         }
      return locations;
   }

}