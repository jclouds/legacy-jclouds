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

import java.util.Set;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.domain.Location;
import org.jclouds.domain.LocationScope;
import org.jclouds.domain.internal.LocationImpl;
import org.jclouds.location.Provider;
import org.jclouds.logging.Logger;
import org.jclouds.rimuhosting.miro.RimuHostingClient;
import org.jclouds.rimuhosting.miro.domain.PricingPlan;

import com.google.common.base.Supplier;
import com.google.common.collect.Sets;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class RimuHostingLocationSupplier implements Supplier<Set<? extends Location>> {

   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;
   private final RimuHostingClient sync;
   private final String providerName;

   @Inject
   RimuHostingLocationSupplier(RimuHostingClient sync, @Provider String providerName) {
      this.providerName = providerName;
      this.sync = sync;
   }

   @Override
   public Set<? extends Location> get() {
      final Set<Location> locations = Sets.newHashSet();
      logger.debug(">> providing locations");
      Location provider = new LocationImpl(LocationScope.PROVIDER, providerName, providerName, null);
      for (final PricingPlan from : sync.getPricingPlanList()) {
         try {
            locations.add(new LocationImpl(LocationScope.ZONE, from.getDataCenter().getId(), from.getDataCenter()
                     .getName(), provider));
         } catch (NullPointerException e) {
            logger.warn("datacenter not present in " + from.getId());
         }
      }
      logger.debug("<< locations(%d)", locations.size());
      return locations;
   }
}