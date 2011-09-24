/*
 * *
 *  * Licensed to jclouds, Inc. (jclouds) under one or more
 *  * contributor license agreements.  See the NOTICE file
 *  * distributed with this work for additional information
 *  * regarding copyright ownership.  jclouds licenses this file
 *  * to you under the Apache License, Version 2.0 (the
 *  * "License"); you may not use this file except in compliance
 *  * with the License.  You may obtain a copy of the License at
 *  *
 *  *   http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing,
 *  * software distributed under the License is distributed on an
 *  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  * KIND, either express or implied.  See the License for the
 *  * specific language governing permissions and limitations
 *  * under the License.
 *
 */

package org.jclouds.virtualbox.functions;

import com.google.common.base.Function;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.HardwareBuilder;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.virtualbox.VirtualBox;
import org.virtualbox_4_1.IGuestOSType;
import org.virtualbox_4_1.IMachine;
import org.virtualbox_4_1.VirtualBoxManager;

import javax.inject.Inject;

public class IMachineToHardware implements Function<IMachine, Hardware> {

   private VirtualBox vbox;

   @Inject
   public IMachineToHardware(VirtualBox vbox) {
      this.vbox = vbox;
   }

   @Override
   public Hardware apply(@Nullable IMachine vm) {
      String osTypeId = vm.getOSTypeId();

      IGuestOSType guestOSType = vbox.manager().getVBox().getGuestOSType(osTypeId);
      Boolean is64Bit = guestOSType.getIs64Bit();
      HardwareBuilder hardwareBuilder = new HardwareBuilder();
      hardwareBuilder.ids(vm.getId());
      vm.getSessionPid();
      hardwareBuilder.is64Bit(is64Bit);
      return null;
   }
}
