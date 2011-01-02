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

package org.jclouds.location.functions;

import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;
import java.util.Map;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.location.Provider;
import org.jclouds.location.Region;

import com.google.common.base.Function;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class RegionToEndpointOrProviderIfNull implements Function<Object, URI> {
   private final URI defaultUri;
   private final Map<String, URI> regionToEndpoint;

   @Inject
   public RegionToEndpointOrProviderIfNull(@Provider URI defaultUri, @Region Map<String, URI> regionToEndpoint) {
      this.defaultUri = checkNotNull(defaultUri, "defaultUri");
      this.regionToEndpoint = checkNotNull(regionToEndpoint, "regionToEndpoint");
   }

   @Override
   public URI apply(@Nullable Object from) {
      return from == null ? defaultUri : regionToEndpoint.get(from);
   }

}