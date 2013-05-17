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
package org.jclouds.trmk.vcloud_0_8.compute.suppliers;

import java.util.Set;

import javax.inject.Singleton;

import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.HardwareBuilder;
import org.jclouds.compute.domain.Processor;
import org.jclouds.compute.domain.Volume;
import org.jclouds.compute.domain.internal.VolumeImpl;

import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;

/**
 * @author Adrian Cole
 */
@Singleton
public class StaticHardwareSupplier implements Supplier<Set<? extends Hardware>> {

   @Override
   public Set<? extends Hardware> get() {
      Set<Hardware> hardware = Sets.newHashSet();
      for (int cpus : new int[] { 1, 2, 4, 8 })
         for (int ram : new int[] { 512, 1024, 2048, 4096, 8192, 16384 }) {
            String id = String.format("cpu=%d,ram=%s,disk=%d", cpus, ram, 10);
            hardware.add(new HardwareBuilder().ids(id).ram(ram).processors(ImmutableList.of(new Processor(cpus, 1.0)))
                     .volumes(ImmutableList.<Volume> of(new VolumeImpl(10f, true, true))).hypervisor("VMware").build());
         }
      return hardware;
   }
}
