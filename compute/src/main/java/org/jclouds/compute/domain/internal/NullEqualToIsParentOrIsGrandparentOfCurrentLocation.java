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
package org.jclouds.compute.domain.internal;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.base.Predicates.equalTo;
import static com.google.common.base.Predicates.or;

import org.jclouds.compute.domain.ComputeMetadata;
import org.jclouds.domain.Location;
import org.jclouds.domain.LocationScope;

import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;

/**
 * If the current location id is null, then we don't care where to launch a node.
 * 
 * If the input location is null, then the data isn't location sensitive
 * 
 * If the input location equals, is a parent or grandparent of the specified location, then we are ok.
 */
public class NullEqualToIsParentOrIsGrandparentOfCurrentLocation implements Predicate<ComputeMetadata> {
   private final Supplier<Location> locationSupplier;

   public NullEqualToIsParentOrIsGrandparentOfCurrentLocation(Supplier<Location> locationSupplier) {
      this.locationSupplier = locationSupplier;
   }

   @Override
   public boolean apply(ComputeMetadata input) {
      Location current = locationSupplier.get();
      if (current == null)
         return true;
      
      if (input.getLocation() == null)
         return true;
      
      Location parent = current.getParent();
      checkArgument(
            parent != null || current.getScope() == LocationScope.PROVIDER,
            "only locations of scope PROVIDER can have a null parent; arg: %s",
            current);
      
      checkState(
            input.getLocation().getParent() != null || input.getLocation().getScope() == LocationScope.PROVIDER,
            "only locations of scope PROVIDER can have a null parent; input: %s",
            input.getLocation());

      Builder<Predicate<Location>> predicates = ImmutableSet.builder();

      predicates.add(equalTo(current));
      
      if (parent != null) {
         predicates.add(equalTo(parent));
         
         Location grandparent = parent.getParent();
         if (grandparent != null)
            predicates.add(equalTo(grandparent));
      }
      
      return or(predicates.build()).apply(input.getLocation());

   }

   @Override
   public String toString() {
      // not calling .get() here, as it could accidentally cause eager api fetch
      return "nullEqualToIsParentOrIsGrandparentOfCurrentLocation()";
   }
}
