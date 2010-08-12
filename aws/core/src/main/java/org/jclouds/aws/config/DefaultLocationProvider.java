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

package org.jclouds.aws.config;

import java.util.NoSuchElementException;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.aws.Region;
import org.jclouds.domain.Location;
import org.jclouds.domain.LocationScope;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

/**
 * @author Adrian Cole
 */
@Singleton
public class DefaultLocationProvider implements javax.inject.Provider<Location> {
   private final String region;
   private final Set<? extends Location> set;

   @Inject
   DefaultLocationProvider(@Region final String region, Set<? extends Location> set) {
      this.region = region;
      this.set = set;
   }

   @Override
   @Singleton
   public Location get() {
      try {
         Location toReturn = Iterables.find(set, new Predicate<Location>() {

            @Override
            public boolean apply(Location input) {
               switch (input.getScope()) {
                  case ZONE:
                     return input.getParent().getId().equals(region);
                  case REGION:
                     return input.getId().equals(region);
                  default:
                     return false;
               }
            }

         });
         return toReturn.getScope() == LocationScope.REGION ? toReturn : toReturn.getParent();
      } catch (NoSuchElementException e) {
         throw new IllegalStateException(String.format("region: %s not found in %s", region, set));
      }
   }
}