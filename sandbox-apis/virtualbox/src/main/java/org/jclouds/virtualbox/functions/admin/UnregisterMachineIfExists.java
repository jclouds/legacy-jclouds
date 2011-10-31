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

package org.jclouds.virtualbox.functions.admin;


import com.google.common.base.Function;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.logging.Logger;
import org.jclouds.virtualbox.domain.ErrorCode;
import org.virtualbox_4_1.CleanupMode;
import org.virtualbox_4_1.IMachine;
import org.virtualbox_4_1.VBoxException;
import org.virtualbox_4_1.VirtualBoxManager;

import javax.annotation.Nullable;
import javax.annotation.Resource;
import javax.inject.Named;

public class UnregisterMachineIfExists implements Function<String, Void> {

   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   private VirtualBoxManager manager;
   private CleanupMode mode;

   public UnregisterMachineIfExists(VirtualBoxManager manager, CleanupMode mode) {
      this.manager = manager;
      this.mode = mode;
   }

   @Override
   public Void apply(@Nullable String vmName) {
      try {
         IMachine machine = manager.getVBox().findMachine(vmName);
         machine.unregister(mode);
      } catch (VBoxException e) {
         ErrorCode errorCode = ErrorCode.valueOf(e);
         switch (errorCode) {
            case VBOX_E_OBJECT_NOT_FOUND:
               logger.debug("Machine %s does not exists, cannot unregister", vmName);
               break;
            default:
               throw e;
         }
      }
      return null;
   }
}
