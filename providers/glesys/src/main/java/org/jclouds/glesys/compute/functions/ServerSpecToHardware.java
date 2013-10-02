/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.glesys.compute.functions;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.location.predicates.LocationPredicates.idEquals;

import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.collect.Memoized;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.HardwareBuilder;
import org.jclouds.compute.domain.Processor;
import org.jclouds.compute.domain.Volume;
import org.jclouds.compute.domain.internal.VolumeImpl;
import org.jclouds.compute.predicates.ImagePredicates;
import org.jclouds.domain.Location;
import org.jclouds.glesys.domain.ServerSpec;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class ServerSpecToHardware implements Function<ServerSpec, Hardware> {

   private final Supplier<Set<? extends Location>> locations;

   @Inject
   ServerSpecToHardware(@Memoized Supplier<Set<? extends Location>> locations) {
      this.locations = checkNotNull(locations, "locations");
   }

   @Override
   public Hardware apply(ServerSpec spec) {
      Location location = FluentIterable.from(locations.get()).firstMatch(idEquals(spec.getDatacenter())).orNull();
      assert location != null : String.format("no location matched ServerSpec %s", spec);
      return new HardwareBuilder().ids(spec.toString()).ram(spec.getMemorySizeMB()).processors(
               ImmutableList.of(new Processor(spec.getCpuCores(), 1.0))).volumes(
               ImmutableList.<Volume> of(new VolumeImpl((float) spec.getDiskSizeGB(), true, true))).hypervisor(
               spec.getPlatform()).location(location).supportsImage(
               ImagePredicates.idEquals(spec.getTemplateName())).build();
   }
}
