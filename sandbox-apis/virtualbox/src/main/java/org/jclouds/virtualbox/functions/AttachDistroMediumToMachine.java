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

import javax.annotation.Nullable;

import org.virtualbox_4_1.DeviceType;
import org.virtualbox_4_1.IMachine;
import org.virtualbox_4_1.IMedium;
import org.virtualbox_4_1.VBoxException;

import com.google.common.base.Function;

/**
 * @author Mattias Holmqvist
 */
public class AttachDistroMediumToMachine implements Function<IMachine, Void> {

   private final String controllerIDE;
   private final IMedium distroMedium;

   public AttachDistroMediumToMachine(String controllerIDE, IMedium distroMedium) {
      this.controllerIDE = controllerIDE;
      this.distroMedium = distroMedium;
   }

   @Override
   public Void apply(@Nullable IMachine machine) {
      try {
         int controllerPort = 0;
         int device = 0;
         machine.attachDevice(controllerIDE, controllerPort, device, DeviceType.DVD, distroMedium);
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