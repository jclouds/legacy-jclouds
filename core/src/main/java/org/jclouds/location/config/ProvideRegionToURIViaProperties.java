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

import static org.jclouds.location.reference.LocationConstants.ENDPOINT;
import static org.jclouds.location.reference.LocationConstants.PROPERTY_REGION;
import static org.jclouds.location.reference.LocationConstants.PROPERTY_REGIONS;

import java.net.URI;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.location.Region;
import org.jclouds.logging.Logger;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Multimap;
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
   private final Multimap<String, String> constants;

   @Resource
   protected Logger logger = Logger.NULL;
   
   @Inject
   protected ProvideRegionToURIViaProperties(Injector injector, @Named("CONSTANTS") Multimap<String, String> constants) {
      this.injector = injector;
      this.constants = constants;
   }

   @Singleton
   @Region
   @Override
   public Map<String, URI> get() {
      try {
         String regionString = injector.getInstance(Key.get(String.class, Names.named(PROPERTY_REGIONS)));
         Builder<String, URI> regions = ImmutableMap.<String, URI> builder();
         for (String region : Splitter.on(',').split(regionString)) {
            String regionUri = injector.getInstance(Key.get(String.class, Names.named(PROPERTY_REGION + "." + region
                     + "." + ENDPOINT)));
            for (Entry<String, String> entry : constants.entries()) {
               regionUri = regionUri.replace(new StringBuilder().append('{').append(entry.getKey()).append('}').toString(), entry
                        .getValue());
            }
            regions.put(region, URI.create(regionUri));
         }
         return regions.build();
      } catch (ConfigurationException e) {
         logger.warn("no region name to endpoint mappings configured!");
         return ImmutableMap.of();
      }
   }

}