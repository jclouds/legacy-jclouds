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

package org.jclouds.virtualbox.functions;

import com.google.common.base.Function;
import org.virtualbox_4_1.*;

import javax.annotation.Nullable;

import static org.virtualbox_4_1.DeviceType.HardDisk;

/**
 * @author Mattias Holmqvist
 */
public class AttachMediumToMachineIfNotAlreadyAttached implements Function<IMachine, Void> {

   private String controllerIDE;
   private IMedium hardDisk;
   private int controllerPort;
   private int device;
   private DeviceType deviceType;

   public AttachMediumToMachineIfNotAlreadyAttached(String controllerIDE, IMedium hardDisk, int controllerPort, int device, DeviceType deviceType) {
      this.controllerIDE = controllerIDE;
      this.hardDisk = hardDisk;
      this.controllerPort = controllerPort;
      this.device = device;
      this.deviceType = deviceType;
   }

   @Override
   public Void apply(@Nullable IMachine machine) {
      // Create and attach medium
      try {
         machine.attachDevice(controllerIDE, controllerPort, device, deviceType, hardDisk);
         machine.saveSettings();
      } catch (VBoxException e) {
         if (!alreadyAttached(e))
            throw e;
      }
      return null;
   }

   private boolean alreadyAttached(VBoxException e) {
      return e.getMessage().indexOf("is already attached to port") != -1;
   }

}
