/*
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

package org.jclouds.googlecompute.compute.functions;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableSet;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.HardwareBuilder;
import org.jclouds.compute.domain.Processor;
import org.jclouds.compute.domain.Volume;
import org.jclouds.compute.domain.internal.VolumeImpl;
import org.jclouds.googlecompute.domain.MachineType;

/**
 * Transforms a google compute domain specific machine type to a generic Hardware object.
 *
 * @author David Alves
 */
public class MachineTypeToHardware implements Function<MachineType, Hardware> {

   @Override
   public Hardware apply(MachineType input) {
      return new HardwareBuilder()
              .id(input.getName())
              .name(input.getName())
              .hypervisor("kvm")
              .processor(new Processor(input.getGuestCpus(), 1.0))
              .providerId(input.getId())
              .ram(input.getMemoryMb())
              .uri(input.getSelfLink())
              .volumes(collectVolumes(input))
              .build();
   }

   private Iterable<Volume> collectVolumes(MachineType input) {
      ImmutableSet.Builder<Volume> volumes = ImmutableSet.builder();
      for (MachineType.EphemeralDisk disk : input.getEphemeralDisks()) {
         volumes.add(new VolumeImpl(null, Volume.Type.LOCAL, new Integer(disk.getDiskGb()).floatValue(), null, true,
                 false));
      }
      return volumes.build();
   }
}
