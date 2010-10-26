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

import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.domain.Location;

import com.google.common.base.Supplier;

/**
 * 
 * @author Adrian Cole
 * 
 *         By default allows you to use a static set of locations bound to Set<? extends Location>
 */
@Singleton
public class LocationSupplier implements Supplier<Set<? extends Location>> {
   private final Set<? extends Location> locations;

   @Inject
   LocationSupplier(Set<? extends Location> locations) {
      this.locations = locations;
   }

   @Override
   public Set<? extends Location> get() {
      return locations;
   }

}