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

package org.jclouds.slicehost.compute.suppliers;

import java.util.Set;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.compute.domain.Size;
import org.jclouds.compute.domain.internal.SizeImpl;
import org.jclouds.compute.predicates.ImagePredicates;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.domain.Location;
import org.jclouds.logging.Logger;
import org.jclouds.slicehost.SlicehostClient;
import org.jclouds.slicehost.domain.Flavor;

import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class SlicehostSizeSupplier implements Supplier<Set<? extends Size>> {

   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;
   private final SlicehostClient sync;
   private final Supplier<Location> location;

   @Inject
   SlicehostSizeSupplier(SlicehostClient sync, Supplier<Location> location) {
      this.sync = sync;
      this.location = location;
   }

   @Override
   public Set<? extends Size> get() {
      final Set<Size> sizes = Sets.newHashSet();
      logger.debug(">> providing sizes");
      for (final Flavor from : sync.listFlavors()) {
         sizes.add(new SizeImpl(from.getId() + "", from.getName(), from.getId() + "", location.get(), null,
                  ImmutableMap.<String, String> of(), from.getRam() / 1024.0, from.getRam(),
                  (from.getRam() * 4) / 1024, ImagePredicates.any()));
      }
      logger.debug("<< sizes(%d)", sizes.size());
      return sizes;
   }
}