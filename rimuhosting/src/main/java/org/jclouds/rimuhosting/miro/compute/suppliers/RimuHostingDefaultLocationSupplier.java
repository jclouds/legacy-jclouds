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

package org.jclouds.rimuhosting.miro.compute.suppliers;

import static org.jclouds.rimuhosting.miro.reference.RimuHostingConstants.PROPERTY_RIMUHOSTING_DEFAULT_DC;

import java.util.Set;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.collect.Memoized;
import org.jclouds.domain.Location;

import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.collect.Iterables;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class RimuHostingDefaultLocationSupplier implements Supplier<Location> {
   private final Supplier<Set<? extends Location>> locations;
   private final String defaultDC;

   @Inject
   RimuHostingDefaultLocationSupplier(@Memoized Supplier<Set<? extends Location>> locations,
            @Named(PROPERTY_RIMUHOSTING_DEFAULT_DC) String defaultDC) {
      this.locations = locations;
      this.defaultDC = defaultDC;
   }

   @Override
   public Location get() {
      return Iterables.find(locations.get(), new Predicate<Location>() {

         @Override
         public boolean apply(Location input) {
            return input.getId().equals(defaultDC);
         }

      });
   }
}