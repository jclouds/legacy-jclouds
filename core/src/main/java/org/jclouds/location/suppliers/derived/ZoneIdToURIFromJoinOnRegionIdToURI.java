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
package org.jclouds.location.suppliers.derived;

import java.net.URI;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.location.Region;
import org.jclouds.location.Zone;
import org.jclouds.location.suppliers.ZoneIdToURISupplier;

import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

/**
 * 
 */
@Singleton
public class ZoneIdToURIFromJoinOnRegionIdToURI implements ZoneIdToURISupplier {

   private final Supplier<Map<String, Supplier<URI>>> regionIdToURISupplier;
   private final Supplier<Map<String, Supplier<Set<String>>>> regionIdToZoneIdsSupplier;

   @Inject
   protected ZoneIdToURIFromJoinOnRegionIdToURI(@Region Supplier<Map<String, Supplier<URI>>> regionIdToURISupplier,
         @Zone Supplier<Map<String, Supplier<Set<String>>>> regionIdToZoneIdsSupplier) {
      this.regionIdToURISupplier = regionIdToURISupplier;
      this.regionIdToZoneIdsSupplier = regionIdToZoneIdsSupplier;
   }

   @Override
   public Map<String, Supplier<URI>> get() {
      Builder<String, Supplier<URI>> builder = ImmutableMap.<String, Supplier<URI>> builder();
      for (Entry<String, Supplier<URI>> regionToURI : regionIdToURISupplier.get().entrySet()) {
         for (String zone : regionIdToZoneIdsSupplier.get().get(regionToURI.getKey()).get()) {
            builder.put(zone, regionToURI.getValue());
         }
      }
      return builder.build();
   }

}
