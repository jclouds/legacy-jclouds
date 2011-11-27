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

package org.jclouds.virtualbox.functions;

import javax.annotation.Nullable;

import org.virtualbox_4_1.IMachine;
import org.virtualbox_4_1.VBoxException;

import com.google.common.base.Function;

/**
 * @author Andrea Turli
 */
public class DetachDistroMediumFromMachine implements Function<IMachine, Void> {

   private final String controller;
   private int controllerPort;
   private int device;

   public DetachDistroMediumFromMachine(String controller, int controllerPort, int device) {
      this.controller = controller;
      this.controllerPort = controllerPort;
      this.device = device;
   }

   @Override
   public Void apply(@Nullable IMachine machine) {
      try {
         machine.detachDevice(controller, controllerPort, device);
         machine.saveSettings();
      } catch (VBoxException e) {
         if (!alreadyDetached(e))
            throw e;
      }
      return null;
   }

   private boolean alreadyDetached(VBoxException e) {
      return e.getMessage().contains("is already detached from port");
   }

}