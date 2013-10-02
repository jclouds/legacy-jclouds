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
package org.jclouds.cloudstack.suppliers;

import static org.jclouds.Constants.PROPERTY_SESSION_INTERVAL;

import java.util.concurrent.TimeUnit;

import javax.inject.Named;

import org.jclouds.cloudstack.domain.Zone;

import com.google.common.base.Supplier;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.inject.Inject;

/**
 * Supplies a cache that maps from zone IDs to zones.
 *
 * @author Richard Downer
 */
public class ZoneIdToZoneSupplier implements Supplier<LoadingCache<String, Zone>> {
   private final LoadingCache<String, Zone> cache;

   @Inject
   public ZoneIdToZoneSupplier(CacheLoader<String, Zone> zoneIdToZone, @Named(PROPERTY_SESSION_INTERVAL) long expirationSecs) {
      cache = CacheBuilder.newBuilder().expireAfterWrite(expirationSecs, TimeUnit.SECONDS).build(zoneIdToZone);
   }

   @Override
   public LoadingCache<String, Zone> get() {
      return cache;
   }
}
