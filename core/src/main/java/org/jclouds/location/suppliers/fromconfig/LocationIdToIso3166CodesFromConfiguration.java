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
package org.jclouds.location.suppliers.fromconfig;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.location.reference.LocationConstants.ISO3166_CODES;
import static org.jclouds.location.reference.LocationConstants.PROPERTY_REGION;
import static org.jclouds.location.reference.LocationConstants.PROPERTY_ZONE;

import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.location.Iso3166;
import org.jclouds.location.suppliers.LocationIdToIso3166CodesSupplier;
import org.jclouds.util.Predicates2;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.base.Splitter;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.collect.ImmutableSet;

/**
 * 
 * looks for properties bound to the naming conventions jclouds.region. {@code regionId}
 * .iso3166-codes and jclouds.zone.{@code zoneId}.iso3166-codes
 * 
 * @author Adrian Cole
 */
@Singleton
public class LocationIdToIso3166CodesFromConfiguration implements LocationIdToIso3166CodesSupplier {

   private final Function<Predicate<String>, Map<String, String>> filterStringsBoundByName;

   @Inject
   public LocationIdToIso3166CodesFromConfiguration(
            Function<Predicate<String>, Map<String, String>> filterStringsBoundByName) {
      this.filterStringsBoundByName = checkNotNull(filterStringsBoundByName, "filterStringsBoundByName");
   }

   @Singleton
   @Iso3166
   @Override
   public Map<String, Supplier<Set<String>>> get() {
      Map<String, String> stringsBoundWithRegionOrZonePrefix = filterStringsBoundByName.apply(Predicates.<String>or(
             Predicates2.startsWith(PROPERTY_REGION),
             Predicates2.startsWith(PROPERTY_ZONE)));
      Builder<String, Supplier<Set<String>>> codes = ImmutableMap.builder();
      for (String key : ImmutableSet.of(PROPERTY_REGION, PROPERTY_ZONE)) {
         String regionOrZoneString = stringsBoundWithRegionOrZonePrefix.get(key + "s");
         if (regionOrZoneString == null)
            continue;
         for (String region : Splitter.on(',').split(regionOrZoneString)) {
            String isoCodes = stringsBoundWithRegionOrZonePrefix.get(key + "." + region + "." + ISO3166_CODES);
            if (isoCodes != null)
               codes.put(region, Suppliers.<Set<String>> ofInstance(ImmutableSet.copyOf(Splitter.on(',')
                        .split(isoCodes))));
         }
      }
      return codes.build();
   }
}
