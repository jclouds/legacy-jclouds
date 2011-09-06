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
package org.jclouds.location.functions;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.net.URI;
import java.util.Map;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.location.Provider;
import org.jclouds.location.Region;

import com.google.common.base.Function;

/**
 * If a mapping of regions to endpoints exists, return a uri corresponding to the name of the region
 * (passed argument). Otherwise, return the default location.
 * 
 * @author Adrian Cole
 */
@Singleton
public class RegionToEndpointOrProviderIfNull implements Function<Object, URI> {
   private final URI defaultUri;
   private final String defaultProvider;
   private final Map<String, URI> regionToEndpoint;

   @Inject
   public RegionToEndpointOrProviderIfNull(@Provider URI defaultUri, @Provider String defaultProvider,
         @Nullable @Region Map<String, URI> regionToEndpoint) {
      this.defaultUri = checkNotNull(defaultUri, "defaultUri");
      this.defaultProvider = checkNotNull(defaultProvider, "defaultProvider");
      this.regionToEndpoint = regionToEndpoint;
   }

   @Override
   public URI apply(@Nullable Object from) {
      if (from == null || from.equals(defaultProvider))
         return defaultUri;
      checkState(from.equals(defaultProvider) || regionToEndpoint != null, "requested location " + from
            + ", but only the default location " + defaultProvider + " is configured");
      checkArgument(from.equals(defaultProvider) || (regionToEndpoint != null && regionToEndpoint.containsKey(from)),
            "requested location %s, which is not in the configured locations: %s", from, regionToEndpoint);
      return regionToEndpoint.get(from);
   }
}