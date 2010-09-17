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

package org.jclouds.rackspace.cloudservers.compute.suppliers;

import java.util.Set;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.compute.domain.ComputeMetadata;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.Processor;
import org.jclouds.compute.domain.internal.HardwareImpl;
import org.jclouds.compute.domain.internal.VolumeImpl;
import org.jclouds.compute.predicates.ImagePredicates;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.domain.Location;
import org.jclouds.logging.Logger;
import org.jclouds.rackspace.cloudservers.CloudServersClient;
import org.jclouds.rackspace.cloudservers.domain.Flavor;
import org.jclouds.rackspace.cloudservers.options.ListOptions;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
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
   private final Supplier<Location> location;

   @Inject
   CloudServersHardwareSupplier(CloudServersClient sync, Supplier<Location> location,
            Function<ComputeMetadata, String> indexer) {
      this.sync = sync;
      this.location = location;
   }

   @Override
   public Set<? extends Hardware> get() {
      final Set<Hardware> sizes = Sets.newHashSet();
      logger.debug(">> providing sizes");
      for (final Flavor from : sync.listFlavors(ListOptions.Builder.withDetails())) {
         sizes.add(new HardwareImpl(from.getId() + "", from.getName(), from.getId() + "", location.get(), null,
                  ImmutableMap.<String, String> of(), ImmutableList.of(new Processor(from.getDisk() / 10.0, 1.0)), from
                           .getRam(), ImmutableList.of(new VolumeImpl((float) from.getDisk(), true, true)),
                  ImagePredicates.any()));
      }
      logger.debug("<< sizes(%d)", sizes.size());
      return sizes;
   }

}