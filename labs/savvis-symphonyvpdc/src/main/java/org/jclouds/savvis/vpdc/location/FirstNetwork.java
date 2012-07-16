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
package org.jclouds.savvis.vpdc.location;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.find;

import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.collect.Memoized;
import org.jclouds.domain.Location;
import org.jclouds.domain.LocationScope;
import org.jclouds.location.suppliers.ImplicitLocationSupplier;

import com.google.common.base.Predicate;
import com.google.common.base.Supplier;

/**
 * 
 * @author Adrian Cole
 * 
 */
@Singleton
public class FirstNetwork implements ImplicitLocationSupplier {
   @Singleton
   public static final class IsNetwork implements Predicate<Location> {
      @Override
      public boolean apply(Location input) {
         return input.getScope() == LocationScope.NETWORK;
      }

      @Override
      public String toString() {
         return "isNetwork()";
      }
   }

   private final Supplier<Set<? extends Location>> locationsSupplier;
   private final IsNetwork isNetwork;

   @Inject
   FirstNetwork(@Memoized Supplier<Set<? extends Location>> locationsSupplier, IsNetwork isNetwork) {
      this.locationsSupplier = checkNotNull(locationsSupplier, "locationsSupplierSupplier");
      this.isNetwork = checkNotNull(isNetwork, "isNetwork");
   }

   @Override
   public Location get() {
      return find(locationsSupplier.get(), isNetwork);
   }

}
