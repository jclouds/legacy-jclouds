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
import org.virtualbox_4_1.IMachine;
import org.virtualbox_4_1.IVirtualBox;
import org.virtualbox_4_1.VBoxException;
import org.virtualbox_4_1.VirtualBoxManager;

import javax.annotation.Nullable;

public class CreateAndRegisterMachineFromIsoIfNotAlreadyExists implements Function<String, IMachine> {

   private String settingsFile;
   private String osTypeId;
   private String vmId;
   private boolean forceOverwrite;
   private VirtualBoxManager manager;

   public CreateAndRegisterMachineFromIsoIfNotAlreadyExists(String settingsFile, String osTypeId, String vmId,
                                                            boolean forceOverwrite, VirtualBoxManager manager) {
      this.settingsFile = settingsFile;
      this.osTypeId = osTypeId;
      this.vmId = vmId;
      this.forceOverwrite = forceOverwrite;
      this.manager = manager;
   }

   @Override
   public IMachine apply(@Nullable String vmName) {

      final IVirtualBox vBox = manager.getVBox();
      try {
         vBox.findMachine(vmName);
         throw new IllegalStateException("Machine " + vmName + " is already registered.");
      } catch (VBoxException e) {
         if (machineNotFoundException(e))
            return createMachine(vBox, vmName);
         else
            throw e;
      }
   }

   private boolean machineNotFoundException(VBoxException e) {
      return e.getMessage().indexOf("VirtualBox error: Could not find a registered machine named ") != -1;
   }

   private IMachine createMachine(IVirtualBox vBox, String vmName) {
      IMachine newMachine = vBox.createMachine(settingsFile, vmName, osTypeId, vmId, forceOverwrite);
      manager.getVBox().registerMachine(newMachine);
      return newMachine;
   }
}
