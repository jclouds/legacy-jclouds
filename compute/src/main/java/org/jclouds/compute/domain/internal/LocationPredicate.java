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
package org.jclouds.compute.domain.internal;

import org.jclouds.compute.domain.ComputeMetadata;
import org.jclouds.domain.Location;

import com.google.common.base.Predicate;
import com.google.common.base.Supplier;

/**
 * If the current location id is null, then we don't care where to launch a node.
 * 
 * If the input location is null, then the data isn't location sensitive
 * 
 * If the input location is a parent of the specified location, then we are ok.
 */
public class LocationPredicate implements Predicate<ComputeMetadata> {
   private final Supplier<Location> locationSupplier;

   public LocationPredicate(Supplier<Location> locationSupplier) {
      this.locationSupplier = locationSupplier;
   }

   @Override
   public boolean apply(ComputeMetadata input) {
      Location location = locationSupplier.get();
      boolean returnVal = true;
      if (location != null && input.getLocation() != null)
         returnVal = location.equals(input.getLocation()) || location.getParent() != null
               && location.getParent().equals(input.getLocation()) || location.getParent().getParent() != null
               && location.getParent().getParent().equals(input.getLocation());
      return returnVal;
   }

   @Override
   public String toString() {
      return locationSupplier.get() == null ? "anyLocation()" : "locationEqualsOrChildOf(" + locationSupplier.get().getId() + ")";
   }
}