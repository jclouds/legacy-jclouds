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
package org.jclouds.location.suppliers.all;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.domain.Location;
import org.jclouds.domain.LocationBuilder;
import org.jclouds.domain.LocationScope;
import org.jclouds.location.Iso3166;
import org.jclouds.location.Region;

import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;
import com.google.common.collect.Iterables;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class RegionToProviderOrJustProvider implements Supplier<Set<? extends Location>> {
   private final JustProvider justProvider;
   private final Supplier<Set<String>> regionsSupplier;
   private final Supplier<Map<String, Supplier<Set<String>>>> isoCodesByIdSupplier;

   @Inject
   public RegionToProviderOrJustProvider(JustProvider justProvider, @Region Supplier<Set<String>> regionsSupplier,
            @Iso3166 Supplier<Map<String, Supplier<Set<String>>>> isoCodesByIdSupplier) {
      this.justProvider = checkNotNull(justProvider, "justProvider");
      this.regionsSupplier = checkNotNull(regionsSupplier, "regionsSupplier");
      this.isoCodesByIdSupplier = checkNotNull(isoCodesByIdSupplier, "isoCodesByIdSupplier");
   }

   @Override
   public Set<? extends Location> get() {
      Builder<Location> locations = ImmutableSet.builder();
      Location provider = Iterables.getOnlyElement(justProvider.get());
      Set<String> regions = regionsSupplier.get();
      Map<String, Supplier<Set<String>>> isoCodesById = isoCodesByIdSupplier.get();
      if (regions.size() == 0)
         return locations.add(provider).build();
      else
         for (String region : regions) {
            LocationBuilder builder = new LocationBuilder().scope(LocationScope.REGION).id(region).description(region)
                     .parent(provider);
            if (isoCodesById.containsKey(region))
               builder.iso3166Codes(isoCodesById.get(region).get());
            locations.add(builder.build());
         }
      return locations.build();
   }

}
