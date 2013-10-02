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

import static org.jclouds.location.reference.LocationConstants.PROPERTY_REGION;

import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.config.ValueOfConfigurationKeyOrNull;
import org.jclouds.location.Provider;
import org.jclouds.location.Region;
import org.jclouds.location.Zone;
import org.jclouds.location.suppliers.RegionIdToZoneIdsSupplier;
import org.jclouds.logging.Logger;

import com.google.common.base.Splitter;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.collect.ImmutableSet;

/**
 * 
 * looks for properties bound to the naming convention jclouds.location.region.{@code regionId}
 * .zones
 * 
 * @author Adrian Cole
 */
@Singleton
public class RegionIdToZoneIdsFromConfiguration implements RegionIdToZoneIdsSupplier {

   @Resource
   protected Logger logger = Logger.NULL;

   private final ValueOfConfigurationKeyOrNull config;
   private final String provider;
   private final Supplier<Set<String>> regionsSupplier;

   @Inject
   protected RegionIdToZoneIdsFromConfiguration(ValueOfConfigurationKeyOrNull config, @Provider String provider,
            @Region Supplier<Set<String>> regionsSupplier) {
      this.config = config;
      this.provider = provider;
      this.regionsSupplier = regionsSupplier;
   }

   @Singleton
   @Zone
   @Override
   public Map<String, Supplier<Set<String>>> get() {
      Set<String> regions = regionsSupplier.get();
      if (regions.size() == 0) {
         logger.debug("no regions configured for provider %s", provider);
         return ImmutableMap.of();
      }
      Builder<String, Supplier<Set<String>>> regionToZones = ImmutableMap.builder();
      for (String region : regions) {
         String configKey = PROPERTY_REGION + "." + region + ".zones";
         String zones = config.apply(configKey);
         if (zones == null)
            logger.debug("config key %s not present", configKey);
         else
            regionToZones.put(region, Suppliers.<Set<String>> ofInstance(ImmutableSet.copyOf(Splitter.on(',').split(
                     zones))));
      }
      return regionToZones.build();

   }
}
