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
package org.jclouds.ibm.smartcloud.compute.suppliers;

import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.HardwareBuilder;
import org.jclouds.compute.domain.Processor;
import org.jclouds.compute.domain.Volume;
import org.jclouds.compute.domain.internal.VolumeImpl;
import org.jclouds.compute.predicates.ImagePredicates;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.domain.Location;
import org.jclouds.ibm.smartcloud.IBMSmartCloudClient;
import org.jclouds.ibm.smartcloud.domain.InstanceType;
import org.jclouds.logging.Logger;

import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;

/**
 * @author Adrian Cole
 */
@Singleton
public class IBMSmartCloudHardwareSupplier implements Supplier<Set<? extends Hardware>> {

   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;
   private final IBMSmartCloudClient sync;
   private final Supplier<Map<String, ? extends Location>> locations;

   @Inject
   IBMSmartCloudHardwareSupplier(IBMSmartCloudClient sync, Supplier<Map<String, ? extends Location>> locations) {
      this.sync = sync;
      this.locations = locations;
   }

   @Override
   public Set<? extends Hardware> get() {
      final Set<Hardware> hardware = Sets.newHashSet();
      logger.debug(">> providing hardware");
      for (org.jclouds.ibm.smartcloud.domain.Image image : sync.listImages()) {
         for (InstanceType instanceType : image.getSupportedInstanceTypes()) {
            hardware.add(new HardwareBuilder()
                  .id(image.getId() + "/" + instanceType.getId())
                  .providerId(instanceType.getId())
                  .name(instanceType.getLabel())
                  .location(locations.get().get(image.getLocation()))
                  .uri(image.getManifest())
                  .processors(ImmutableList.of(new Processor((instanceType.getPrice().getRate() * 100), 1.0)))
                  .ram((int) instanceType.getPrice().getRate() * 1024)
                  .supportsImage(ImagePredicates.idEquals(image.getId()))
                  .volumes(
                        ImmutableList.<Volume> of(new VolumeImpl((float) (instanceType.getPrice().getRate() * 100d),
                              true, true))).build());

         }
      }
      logger.debug("<< hardware(%d)", hardware.size());
      return hardware;
   }
}
