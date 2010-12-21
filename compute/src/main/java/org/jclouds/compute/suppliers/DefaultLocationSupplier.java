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

package org.jclouds.compute.suppliers;

import static com.google.common.collect.Iterables.find;
import static com.google.common.collect.Iterables.getOnlyElement;

import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.collect.Memoized;
import org.jclouds.domain.Location;
import org.jclouds.domain.LocationScope;

import com.google.common.base.Predicate;
import com.google.common.base.Supplier;

/**
 * 
 * @author Adrian Cole
 * 
 */
@Singleton
public class DefaultLocationSupplier implements Supplier<Location> {
   private final Supplier<Set<? extends Location>> locations;

   @Inject
   DefaultLocationSupplier(@Memoized Supplier<Set<? extends Location>> locations) {
      this.locations = locations;
   }

   @Override
   public Location get() {
      if (locations.get().size() == 1)
         return getOnlyElement(locations.get());
      return find(locations.get(), new Predicate<Location>() {

         @Override
         public boolean apply(Location input) {
            return input.getScope() == LocationScope.ZONE;
         }

      });
   }

}