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
package org.jclouds.joyent.cloudapi.v6_5.compute.functions;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.util.Map;

import javax.inject.Inject;

import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.HardwareBuilder;
import org.jclouds.compute.domain.Processor;
import org.jclouds.compute.domain.internal.VolumeImpl;
import org.jclouds.domain.Location;
import org.jclouds.joyent.cloudapi.v6_5.domain.datacenterscoped.PackageInDatacenter;

import com.google.common.base.Function;
import com.google.common.base.Supplier;

/**
 * A function for transforming the cloudApi specific PackageInDatacenter object to
 * the generic Hardware object.
 * 
 * @author Adrian Cole
 */
public class PackageInDatacenterToHardware implements Function<PackageInDatacenter, Hardware> {

   private final Supplier<Map<String, Location>> locationIndex;

   @Inject
   public PackageInDatacenterToHardware(Supplier<Map<String, Location>> locationIndex) {
      this.locationIndex = checkNotNull(locationIndex, "locationIndex");
   }

   @Override
   public Hardware apply(PackageInDatacenter pkgInDatacenter) {
      Location location = locationIndex.get().get(pkgInDatacenter.getDatacenter());
      checkState(location != null, "location %s not in locationIndex: %s", pkgInDatacenter.getDatacenter(),
            locationIndex.get());
      org.jclouds.joyent.cloudapi.v6_5.domain.Package pkg = pkgInDatacenter.get();
      return new HardwareBuilder().id(pkgInDatacenter.slashEncode()).providerId(pkg.getName()).name(pkg.getName())
            .ram(pkg.getMemorySizeMb())
            // TODO: no api call to get processors.. either hard-code or
            // calculate
            .processor(new Processor(1, 1.0)).volume(new VolumeImpl(Float.valueOf(pkg.getDiskSizeGb()), true, true))
            .location(location).build();
   }
}
