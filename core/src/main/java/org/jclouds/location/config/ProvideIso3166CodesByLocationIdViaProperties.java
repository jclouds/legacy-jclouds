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

package org.jclouds.location.config;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.inject.name.Names.named;
import static org.jclouds.location.reference.LocationConstants.ISO3166_CODES;
import static org.jclouds.location.reference.LocationConstants.PROPERTY_REGION;
import static org.jclouds.location.reference.LocationConstants.PROPERTY_ZONE;

import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.location.Iso3166;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.inject.ConfigurationException;
import com.google.inject.Injector;
import com.google.inject.Key;

/**
 * 
 * looks for properties bound to the naming conventions jclouds.region.{@code regionId}
 * .iso3166-codes and jclouds.zone.{@code zoneId}.iso3166-codes
 * 
 * @author Adrian Cole
 */
@Singleton
public class ProvideIso3166CodesByLocationIdViaProperties implements javax.inject.Provider<Map<String, Set<String>>> {

   private final Injector injector;

   @Inject
   ProvideIso3166CodesByLocationIdViaProperties(Injector injector) {
      this.injector = checkNotNull(injector, "injector");
   }

   @Singleton
   @Iso3166
   @Override
   public Map<String, Set<String>> get() {
      Builder<String, Set<String>> codes = ImmutableMap.<String, Set<String>> builder();
      for (String key : ImmutableSet.of(PROPERTY_REGION, PROPERTY_ZONE))
         try {
            String regionString = injector.getInstance(Key.get(String.class, named(key + "s")));
            for (String region : Splitter.on(',').split(regionString)) {
               try {
                  codes.put(region, ImmutableSet.copyOf(Splitter.on(',')
                           .split(
                                    injector.getInstance(Key.get(String.class, named(key + "." + region + "."
                                             + ISO3166_CODES))))));
               } catch (ConfigurationException e) {
                  // this happens if regions property isn't set
                  // services not run by AWS may not have regions, so this is ok.
               }
            }
         } catch (ConfigurationException e) {
            // this happens if regions property isn't set
            // services not run by AWS may not have regions, so this is ok.
         }
      return codes.build();
   }
}