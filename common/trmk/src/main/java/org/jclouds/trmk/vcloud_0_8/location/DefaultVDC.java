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
package org.jclouds.trmk.vcloud_0_8.location;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.find;

import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.collect.Memoized;
import org.jclouds.domain.Location;
import org.jclouds.domain.LocationScope;
import org.jclouds.location.suppliers.ImplicitLocationSupplier;
import org.jclouds.trmk.vcloud_0_8.domain.ReferenceType;
import org.jclouds.trmk.vcloud_0_8.endpoints.VDC;

import com.google.common.base.Predicate;
import com.google.common.base.Supplier;

@Singleton
public class DefaultVDC implements ImplicitLocationSupplier {
   @Singleton
   public static final class IsDefaultVDC implements Predicate<Location> {
      private final Supplier<ReferenceType> defaultVDC;

      @Inject
      IsDefaultVDC(@VDC Supplier<ReferenceType> defaultVDC) {
         this.defaultVDC = checkNotNull(defaultVDC, "defaultVDC");
      }

      @Override
      public boolean apply(Location input) {
         return input.getScope() == LocationScope.ZONE && input.getId().equals(defaultVDC.get().getHref().toASCIIString());
      }

      @Override
      public String toString() {
         return "isDefaultVDC()";
      }
   }

   private final Supplier<Set<? extends Location>> locationsSupplier;
   private final DefaultVDC.IsDefaultVDC isDefaultVDC;

   @Inject
   DefaultVDC(@Memoized Supplier<Set<? extends Location>> locationsSupplier, DefaultVDC.IsDefaultVDC isDefaultVDC) {
      this.locationsSupplier = checkNotNull(locationsSupplier, "locationsSupplierSupplier");
      this.isDefaultVDC = checkNotNull(isDefaultVDC, "isDefaultVDC");
   }

   @Override
   public Location get() {
      return find(locationsSupplier.get(), isDefaultVDC);
   }

}
