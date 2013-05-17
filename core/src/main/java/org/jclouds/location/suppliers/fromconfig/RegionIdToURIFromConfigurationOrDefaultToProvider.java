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

import java.net.URI;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.config.ValueOfConfigurationKeyOrNull;
import org.jclouds.location.Provider;
import org.jclouds.location.Region;
import org.jclouds.location.suppliers.RegionIdToURISupplier;

import com.google.common.base.Supplier;

/**
 * 
 * looks for properties bound to the naming convention jclouds.region.{@code regionId}.endpoint
 * 
 * @author Adrian Cole
 */
@Singleton
public class RegionIdToURIFromConfigurationOrDefaultToProvider extends LocationIdToURIFromConfigurationOrDefaultToProvider implements RegionIdToURISupplier {

   @Inject
   protected RegionIdToURIFromConfigurationOrDefaultToProvider(ValueOfConfigurationKeyOrNull config, @Provider Supplier<URI> providerURI,
            @Region Supplier<Set<String>> regionIds) {
      super(config, providerURI, regionIds, PROPERTY_REGION);
   }

}
