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

package org.jclouds.ibmdev.compute.suppliers;

import java.util.Set;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.domain.Location;
import org.jclouds.domain.LocationScope;
import org.jclouds.domain.internal.LocationImpl;
import org.jclouds.ibmdev.IBMDeveloperCloudClient;
import org.jclouds.logging.Logger;

import com.google.common.base.Supplier;
import com.google.common.collect.Sets;

/**
 * @author Adrian Cole
 */
@Singleton
public class IBMDeveloperCloudLocationSupplier implements Supplier<Set<? extends Location>> {

   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   private final IBMDeveloperCloudClient sync;
   private final String providerName;

   @Inject
   IBMDeveloperCloudLocationSupplier(IBMDeveloperCloudClient sync,
            @org.jclouds.rest.annotations.Provider String providerName) {
      this.sync = sync;
      this.providerName = providerName;

   }

   @Override
   public Set<? extends Location> get() {
      final Set<Location> assignableLocations = Sets.newHashSet();
      logger.debug(">> providing locations");
      Location parent = new LocationImpl(LocationScope.PROVIDER, providerName, providerName, null);

      for (org.jclouds.ibmdev.domain.Location location : sync.listLocations())
         assignableLocations.add(new LocationImpl(LocationScope.ZONE, location.getId(), location.getName(), parent));

      logger.debug("<< locations(%d)", assignableLocations.size());
      return assignableLocations;
   }

}