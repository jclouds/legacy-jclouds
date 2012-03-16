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
package org.jclouds.openstack.nova.v1_1.compute.functions;

import javax.inject.Inject;

import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.HardwareBuilder;
import org.jclouds.compute.domain.Processor;
import org.jclouds.compute.domain.internal.VolumeImpl;
import org.jclouds.domain.Location;
import org.jclouds.openstack.nova.v1_1.domain.Flavor;

import com.google.common.base.Function;
import com.google.common.base.Supplier;

/**
 * A function for transforming the nova specific Flavor object to the generic
 * Hardware object.
 * 
 * @author Matt Stephenson
 */
public class FlavorToHardware implements Function<Flavor, Hardware> {

   private final Supplier<Location> defaultLocation;

   @Inject
   public FlavorToHardware(Supplier<Location> defaultLocation) {
      this.defaultLocation = defaultLocation;
   }

   @Override
   public Hardware apply(Flavor flavor) {
      return new HardwareBuilder()
            // TODO: scope id to region, if there's a chance for conflict
            .id(flavor.getId()).providerId(flavor.getId()).name(flavor.getName()).ram(flavor.getRam())
            .processor(new Processor(flavor.getVcpus(), 1.0))
            .volume(new VolumeImpl(Float.valueOf(flavor.getDisk()), true, true)).location(defaultLocation.get())
            .build();
   }
}
