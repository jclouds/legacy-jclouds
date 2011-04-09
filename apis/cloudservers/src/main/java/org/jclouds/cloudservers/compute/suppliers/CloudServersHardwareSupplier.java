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
package org.jclouds.cloudservers.compute.suppliers;

import static org.jclouds.cloudservers.options.ListOptions.Builder.withDetails;

import java.util.Set;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.logging.Logger;
import org.jclouds.cloudservers.CloudServersClient;
import org.jclouds.cloudservers.domain.Flavor;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class CloudServersHardwareSupplier implements Supplier<Set<? extends Hardware>> {
   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;
   private final CloudServersClient sync;
   private final Function<Flavor, Hardware> flavorToHardware;

   @Inject
   CloudServersHardwareSupplier(CloudServersClient sync, Function<Flavor, Hardware> flavorToHardware) {
      this.sync = sync;
      this.flavorToHardware = flavorToHardware;
   }

   @Override
   public Set<? extends Hardware> get() {
      final Set<Hardware> hardware;
      logger.debug(">> providing hardware");
      hardware = Sets.newLinkedHashSet(Iterables.transform(sync.listFlavors(withDetails()), flavorToHardware));
      logger.debug("<< hardware(%d)", hardware.size());
      return hardware;
   }
}
