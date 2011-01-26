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

package org.jclouds.location.suppliers;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.domain.Location;
import org.jclouds.domain.LocationScope;
import org.jclouds.domain.internal.LocationImpl;
import org.jclouds.location.Provider;
import org.jclouds.location.Region;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class RegionToProviderOrJustProvider implements Supplier<Set<? extends Location>> {
   private final Set<String> regions;
   private final String providerName;

   @Inject
   RegionToProviderOrJustProvider(@Region Set<String> regions, @Provider String providerName) {
      this.regions = checkNotNull(regions, "regions");
      this.providerName = checkNotNull(providerName, "providerName");
   }

   @Override
   public Set<? extends Location> get() {
      final Location provider = new LocationImpl(LocationScope.PROVIDER, providerName, providerName, null);
      if (regions.size() == 0)
         return ImmutableSet.of(provider);
      return ImmutableSet.<Location> copyOf(Iterables.transform(regions, new Function<String, Location>() {

         @Override
         public Location apply(String input) {
            return new LocationImpl(LocationScope.REGION, input, input, provider);
         }

      }));
   }

}