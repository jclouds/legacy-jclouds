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
package org.jclouds.virtualbox.util;

import javax.annotation.Resource;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.logging.Logger;
import org.jclouds.virtualbox.domain.ExecutionType;
import org.jclouds.virtualbox.functions.LaunchMachineIfNotAlreadyRunning;
import org.virtualbox_4_1.IProgress;
import org.virtualbox_4_1.ISession;
import org.virtualbox_4_1.LockType;
import org.virtualbox_4_1.MachineState;
import org.virtualbox_4_1.VirtualBoxManager;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.inject.Inject;

/**
 * Utilities for managing a VirtualBox machine.
 * 
 * @author Andrea Turli
 */

@Singleton
public class MachineController {

   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   private final Supplier<VirtualBoxManager> manager;
   private final MachineUtils machineUtils;

   @Inject
   public MachineController(Supplier<VirtualBoxManager> manager, MachineUtils machineUtils) {
      this.manager = manager;
      this.machineUtils = machineUtils;
   }

   public void ensureMachineIsRunning(String vmNameOrId) {
      machineUtils.applyForMachine(vmNameOrId, new LaunchMachineIfNotAlreadyRunning(manager.get(), ExecutionType.GUI, ""));
   }

   /**
    * ensureMachineHasPowerDown needs to have this delay just to ensure that the
    * machine is completely powered off
    * 
    * @param vmNameOrId
    */
   public void ensureMachineIsPoweredOff(final String vmNameOrId) {
      while (!isPoweredOff(vmNameOrId)) {
         machineUtils.lockSessionOnMachineAndApply(vmNameOrId, LockType.Shared, new Function<ISession, Void>() {
            @Override
            public Void apply(ISession session) {
               IProgress powerDownProgress = session.getConsole().powerDown();
               powerDownProgress.waitForCompletion(-1);
               return null;
            }
         });
      }
   }

   public boolean isPoweredOff(String vmNameOrId) {
      return manager.get().getVBox().findMachine(vmNameOrId).getState().equals(MachineState.PoweredOff);
   }

}
