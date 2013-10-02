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
import static com.google.common.base.Preconditions.checkState;

import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.domain.Location;
import org.jclouds.domain.LocationBuilder;
import org.jclouds.domain.LocationScope;
import org.jclouds.location.Iso3166;
import org.jclouds.location.Zone;
import org.jclouds.location.suppliers.LocationsSupplier;
import org.jclouds.logging.Logger;

import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;
import com.google.common.collect.Iterables;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class ZoneToProvider implements LocationsSupplier {

   @Resource
   protected Logger logger = Logger.NULL;

   private final JustProvider justProvider;
   private final Supplier<Set<String>> zoneIdsSupplier;
   private final Supplier<Map<String, Supplier<Set<String>>>> isoCodesByIdSupplier;

   @Inject
   ZoneToProvider(JustProvider justProvider, @Zone Supplier<Set<String>> zoneIdsSupplier,
            @Iso3166 Supplier<Map<String, Supplier<Set<String>>>> isoCodesByIdSupplier) {
      this.justProvider = checkNotNull(justProvider, "justProvider");
      this.zoneIdsSupplier = checkNotNull(zoneIdsSupplier, "zoneIdsSupplier");
      this.isoCodesByIdSupplier = checkNotNull(isoCodesByIdSupplier, "isoCodesByIdSupplier");
   }

   @Override
   public Set<? extends Location> get() {
      Location provider = Iterables.getOnlyElement(justProvider.get());
      Set<String> zoneIds = zoneIdsSupplier.get();
      checkState(zoneIds.size() > 0, "no zones found for provider %s, using supplier %s", provider, zoneIdsSupplier);
      Map<String, Supplier<Set<String>>> isoCodesById = isoCodesByIdSupplier.get();

      Builder<Location> locations = ImmutableSet.builder();
      for (String zoneId : zoneIds) {
         LocationBuilder builder = new LocationBuilder().scope(LocationScope.ZONE).id(zoneId).description(zoneId)
                  .parent(provider);
         if (isoCodesById.containsKey(zoneId))
            builder.iso3166Codes(isoCodesById.get(zoneId).get());
         locations.add(builder.build());
      }
      return locations.build();
   }

}
