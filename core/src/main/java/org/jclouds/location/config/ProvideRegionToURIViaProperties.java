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

import static org.jclouds.location.reference.LocationConstants.ENDPOINT;
import static org.jclouds.location.reference.LocationConstants.PROPERTY_REGION;
import static org.jclouds.location.reference.LocationConstants.PROPERTY_REGIONS;

import java.net.URI;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.location.Region;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.inject.ConfigurationException;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.name.Names;

/**
 * 
 * looks for properties bound to the naming convention jclouds.region.{@code regionId}.endpoint
 * 
 * @author Adrian Cole
 */
@Singleton
public class ProvideRegionToURIViaProperties implements javax.inject.Provider<Map<String, URI>> {

   private final Injector injector;

   @Inject
   ProvideRegionToURIViaProperties(Injector injector) {
      this.injector = injector;
   }

   @Singleton
   @Region
   @Override
   public Map<String, URI> get() {
      try {
         String regionString = injector.getInstance(Key.get(String.class, Names.named(PROPERTY_REGIONS)));
         Builder<String, URI> regions = ImmutableMap.<String, URI> builder();
         for (String region : Splitter.on(',').split(regionString)) {
            regions.put(region, URI.create(injector.getInstance(Key.get(String.class, Names.named(PROPERTY_REGION + "."
                     + region + "." + ENDPOINT)))));
         }
         return regions.build();
      } catch (ConfigurationException e) {
         // this happens if regions property isn't set
         // services not run by AWS may not have regions, so this is ok.
         return ImmutableMap.of();
      }
   }

}