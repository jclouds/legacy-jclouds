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
package org.jclouds.location.suppliers.implicit;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.find;
import static com.google.common.collect.Iterables.transform;
import static org.jclouds.location.predicates.LocationPredicates.isZone;

import java.util.NoSuchElementException;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.collect.Memoized;
import org.jclouds.domain.Location;
import org.jclouds.location.functions.ToIdAndScope;
import org.jclouds.location.suppliers.ImplicitLocationSupplier;

import com.google.common.base.Supplier;

/**
 * 
 * @author Adrian Cole
 * 
 */
@Singleton
public class FirstZone implements ImplicitLocationSupplier {

   private final Supplier<Set<? extends Location>> locationsSupplier;

   @Inject
   FirstZone(@Memoized Supplier<Set<? extends Location>> locationsSupplier) {
      this.locationsSupplier = checkNotNull(locationsSupplier, "locationsSupplierSupplier");
   }

   @Override
   public Location get() {
      Set<? extends Location> locations = locationsSupplier.get();
      try {
         return find(locations, isZone());
      } catch (NoSuchElementException e) {
         throw new NoSuchElementException("none to of the locations are scope ZONE: "
               + transform(locations, ToIdAndScope.INSTANCE));
      }
   }

}
