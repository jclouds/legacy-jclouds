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

import static org.jclouds.location.reference.LocationConstants.ENDPOINT;

import java.net.URI;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.config.ValueOfConfigurationKeyOrNull;
import org.jclouds.location.Provider;
import org.jclouds.logging.Logger;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.inject.assistedinject.Assisted;

@Singleton
public class LocationIdToURIFromConfigurationOrDefaultToProvider implements Supplier<Map<String, Supplier<URI>>>{

   @Resource
   protected Logger logger = Logger.NULL;

   protected final ValueOfConfigurationKeyOrNull config;
   protected final Supplier<Set<String>> locationIds;
   protected final Supplier<URI> providerURI;
   protected final String configPrefix;
   
   @Inject
   public LocationIdToURIFromConfigurationOrDefaultToProvider(ValueOfConfigurationKeyOrNull config, @Provider Supplier<URI> providerURI, @Assisted Supplier<Set<String>> locationIds,
            @Assisted String configPrefix) {
      this.config = config;
      this.locationIds = locationIds;
      this.providerURI = providerURI;
      this.configPrefix = configPrefix;
   }

   @Override
   public Map<String, Supplier<URI>> get() {
      Builder<String, Supplier<URI>> locations = ImmutableMap.builder();
      for (String location : locationIds.get()) {
         String configKey = configPrefix + "." + location + "." + ENDPOINT;
         String locationUri = config.apply(configKey);
         if (locationUri == null) {
            logger.debug("config key %s not present, defaulting to %s", configKey, providerURI);
            locations.put(location, providerURI);
         } else {
            locations.put(location, Suppliers.ofInstance(URI.create(locationUri)));
   
         }
      }
      return locations.build();
   }

}
