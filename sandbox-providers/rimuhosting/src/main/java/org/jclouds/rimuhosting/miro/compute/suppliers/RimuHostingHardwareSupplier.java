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
package org.jclouds.rimuhosting.miro.compute.suppliers;

import java.util.Set;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.collect.Memoized;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.HardwareBuilder;
import org.jclouds.compute.domain.Processor;
import org.jclouds.compute.domain.Volume;
import org.jclouds.compute.domain.internal.VolumeImpl;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.domain.Location;
import org.jclouds.logging.Logger;
import org.jclouds.rimuhosting.miro.RimuHostingClient;
import org.jclouds.rimuhosting.miro.domain.PricingPlan;

import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class RimuHostingHardwareSupplier implements Supplier<Set<? extends Hardware>> {

   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   private final RimuHostingClient sync;
   private final Supplier<Set<? extends Location>> locations;

   @Inject
   RimuHostingHardwareSupplier(RimuHostingClient sync, @Memoized Supplier<Set<? extends Location>> locations) {
      this.sync = sync;
      this.locations = locations;
   }

   @Override
   public Set<? extends Hardware> get() {
      final Set<Hardware> sizes = Sets.newHashSet();
      logger.debug(">> providing sizes");
      for (final PricingPlan from : sync.getPricingPlanList()) {
         try {

            final Location location = Iterables.find(locations.get(), new Predicate<Location>() {

               @Override
               public boolean apply(Location input) {
                  return input.getId().equals(from.getDataCenter().getId());
               }

            });
            sizes.add(new HardwareBuilder().ids(from.getId()).location(location).processors(
                     ImmutableList.of(new Processor(1, 1.0))).ram(from.getRam()).volumes(
                     ImmutableList.<Volume> of(new VolumeImpl((float) from.getDiskSize(), true, true))).build());
         } catch (NullPointerException e) {
            logger.warn("datacenter not present in " + from.getId());
         }
      }
      logger.debug("<< sizes(%d)", sizes.size());
      return sizes;
   }
}