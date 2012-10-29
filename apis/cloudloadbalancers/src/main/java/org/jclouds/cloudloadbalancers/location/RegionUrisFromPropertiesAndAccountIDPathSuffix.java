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
package org.jclouds.cloudloadbalancers.location;

import java.net.URI;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.cloudloadbalancers.functions.AppendAccountIdToURI;
import org.jclouds.config.ValueOfConfigurationKeyOrNull;
import org.jclouds.location.Provider;
import org.jclouds.location.Region;
import org.jclouds.location.suppliers.fromconfig.RegionIdToURIFromConfigurationOrDefaultToProvider;

import com.google.common.base.Supplier;
import com.google.common.collect.Maps;

@Singleton
public class RegionUrisFromPropertiesAndAccountIDPathSuffix extends RegionIdToURIFromConfigurationOrDefaultToProvider {

   private AppendAccountIdToURI filter;

   @Inject
   protected RegionUrisFromPropertiesAndAccountIDPathSuffix(ValueOfConfigurationKeyOrNull config,
            @Provider Supplier<URI> providerURI, @Region Supplier<Set<String>> regionIds, AppendAccountIdToURI filter) {
      super(config, providerURI, regionIds);
      this.filter = filter;
   }

   @Override
   public Map<String, Supplier<URI>> get() {
      return Maps.transformValues(super.get(), filter);
   }
}
