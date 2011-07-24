/**
 *
 * Copyright (C) 2011 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.vcloud.compute.config;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.find;

import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.collect.Memoized;
import org.jclouds.compute.config.BindComputeSuppliersByClass;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.Image;
import org.jclouds.domain.Location;
import org.jclouds.domain.LocationScope;
import org.jclouds.vcloud.compute.suppliers.OrgAndVDCToLocationSupplier;
import org.jclouds.vcloud.compute.suppliers.VCloudHardwareSupplier;
import org.jclouds.vcloud.compute.suppliers.VCloudImageSupplier;
import org.jclouds.vcloud.domain.ReferenceType;
import org.jclouds.vcloud.endpoints.VDC;

import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
/**
 * @author Adrian Cole
 */
public class VCloudBindComputeSuppliersByClass extends BindComputeSuppliersByClass {

   @Override
   protected Class<? extends Supplier<Set<? extends Image>>> defineImageSupplier() {
      return VCloudImageSupplier.class;
   }

   @Override
   protected Class<? extends Supplier<Set<? extends Location>>> defineLocationSupplier() {
      return OrgAndVDCToLocationSupplier.class;
   }

   @Override
   protected Class<? extends Supplier<Location>> defineDefaultLocationSupplier() {
      return DefaultVDC.class;
   }

   @Singleton
   public static class DefaultVDC implements Supplier<Location> {
      @Singleton
      public static final class IsDefaultVDC implements Predicate<Location> {
         private final ReferenceType defaultVDC;

         @Inject
         IsDefaultVDC(@VDC ReferenceType defaultVDC) {
            this.defaultVDC = checkNotNull(defaultVDC, "defaultVDC");
         }

         @Override
         public boolean apply(Location input) {
            return input.getScope() == LocationScope.ZONE && input.getId().equals(defaultVDC.getHref().toASCIIString());
         }

         @Override
         public String toString() {
            return "isDefaultVDC()";
         }
      }

      private final Supplier<Set<? extends Location>> locationsSupplier;
      private final IsDefaultVDC isDefaultVDC;

      @Inject
      DefaultVDC(@Memoized Supplier<Set<? extends Location>> locationsSupplier, IsDefaultVDC isDefaultVDC) {
         this.locationsSupplier = checkNotNull(locationsSupplier, "locationsSupplierSupplier");
         this.isDefaultVDC = checkNotNull(isDefaultVDC, "isDefaultVDC");
      }

      @Override
      public Location get() {
         return find(locationsSupplier.get(), isDefaultVDC);
      }

   }

   @Override
   protected Class<? extends Supplier<Set<? extends Hardware>>> defineHardwareSupplier() {
      return VCloudHardwareSupplier.class;
   }
}