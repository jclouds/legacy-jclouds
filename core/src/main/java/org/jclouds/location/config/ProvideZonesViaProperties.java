/**
 *
 * Copyright (C) 2011 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.location.config;

import static org.jclouds.location.reference.LocationConstants.PROPERTY_REGION;

import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.location.Region;
import org.jclouds.location.Zone;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.inject.ConfigurationException;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.name.Names;

/**
 * 
 * looks for properties bound to the naming convention jclouds.location.region.{@code regionId}.zones
 * 
 * @author Adrian Cole
 */
@Singleton
public class ProvideZonesViaProperties implements javax.inject.Provider<Map<String, String>> {

   private final Injector injector;
   private final Set<String> regions;

   @Inject
   ProvideZonesViaProperties(Injector injector, @Region Set<String> regions) {
      this.injector = injector;
      this.regions = regions;
   }

   @Singleton
   @Zone
   @Override
   public Map<String, String> get() {
      try {
         Builder<String, String> zones = ImmutableMap.<String, String> builder();
         for (String region : regions) {
            for (String zone : Splitter.on(',').split(
                  injector.getInstance(Key.get(String.class, Names.named(PROPERTY_REGION + "." + region + ".zones"))))) {
               zones.put(zone, region);
            }
         }
         return zones.build();
      } catch (ConfigurationException e) {
         // this happens if regions property isn't set
         // services not run by AWS may not have zones, so this is ok.
         return ImmutableMap.<String, String> of();
      }
   }
}