/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jclouds.location.config;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.location.reference.LocationConstants.ISO3166_CODES;
import static org.jclouds.location.reference.LocationConstants.PROPERTY_REGION;
import static org.jclouds.location.reference.LocationConstants.PROPERTY_ZONE;

import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.location.Iso3166;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.collect.ImmutableSet;

/**
 * 
 * looks for properties bound to the naming conventions jclouds.region.
 * {@code regionId} .iso3166-codes and jclouds.zone.{@code zoneId}.iso3166-codes
 * 
 * @author Adrian Cole
 */
@Singleton
public class ProvideIso3166CodesByLocationIdViaProperties implements javax.inject.Provider<Map<String, Set<String>>> {

   private final Function<Predicate<String>, Map<String, String>> filterStringsBoundByName;

   @Inject
   ProvideIso3166CodesByLocationIdViaProperties(
         Function<Predicate<String>, Map<String, String>> filterStringsBoundByName) {
      this.filterStringsBoundByName = checkNotNull(filterStringsBoundByName, "filterStringsBoundByName");
   }

   @Singleton
   @Iso3166
   @Override
   public Map<String, Set<String>> get() {
      Map<String, String> stringsBoundWithRegionOrZonePrefix = filterStringsBoundByName.apply(new Predicate<String>() {

         @Override
         public boolean apply(String input) {
            return input.startsWith(PROPERTY_REGION) || input.startsWith(PROPERTY_ZONE);
         }

      });
      Builder<String, Set<String>> codes = ImmutableMap.<String, Set<String>> builder();
      for (String key : ImmutableSet.of(PROPERTY_REGION, PROPERTY_ZONE)) {
         String regionOrZoneString = stringsBoundWithRegionOrZonePrefix.get(key + "s");
         if (regionOrZoneString == null)
            continue;
         for (String region : Splitter.on(',').split(regionOrZoneString)) {
            String isoCodes = stringsBoundWithRegionOrZonePrefix.get(key + "." + region + "." + ISO3166_CODES);
            if (isoCodes != null)
               codes.put(region, ImmutableSet.copyOf(Splitter.on(',').split(isoCodes)));
         }
      }
      return codes.build();
   }
}