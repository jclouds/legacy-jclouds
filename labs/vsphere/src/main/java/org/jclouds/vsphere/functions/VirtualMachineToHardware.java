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
package org.jclouds.vsphere.functions;

import javax.inject.Singleton;

import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.HardwareBuilder;
import org.jclouds.compute.domain.Processor;
import org.jclouds.javax.annotation.Nullable;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.vmware.vim25.mo.VirtualMachine;

/**
 * @author Andrea Turli
 */
@Singleton
public class VirtualMachineToHardware implements Function<VirtualMachine, Hardware> {

   @Override
   public Hardware apply(@Nullable VirtualMachine from) {
      HardwareBuilder builder = new HardwareBuilder();
      builder.ids(from.getName())
         .name(from.getName())
         .processors(ImmutableList.of(new Processor(from.getConfig().getHardware().getNumCPU(), 1.0)))
         .ram(from.getConfig().getHardware().getMemoryMB())
         .hypervisor("vsphere");
      return builder.build();
   }

}
