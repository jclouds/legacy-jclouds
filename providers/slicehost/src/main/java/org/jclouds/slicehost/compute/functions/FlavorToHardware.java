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
package org.jclouds.slicehost.compute.functions;

import javax.inject.Singleton;

import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.HardwareBuilder;
import org.jclouds.compute.domain.Processor;
import org.jclouds.compute.domain.Volume;
import org.jclouds.compute.domain.internal.VolumeImpl;
import org.jclouds.slicehost.domain.Flavor;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;

/**
 * @author Adrian Cole
 */
@Singleton
public class FlavorToHardware implements Function<Flavor, Hardware> {
   public Hardware apply(Flavor from) {
      return new HardwareBuilder().ids(from.getId() + "").name(from.getName()).hypervisor("xen")
            .processors(ImmutableList.of(new Processor(from.getRam() / 1024.0, 1.0))).ram(from.getRam())
            .volumes(ImmutableList.<Volume> of(new VolumeImpl((from.getRam() * 4) / 1024.0f, true, true))).build();
   }
}
