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
import org.virtualbox_4_1.VirtualBoxManager;
import org.virtualbox_4_1.jaxws.MachineState;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.inject.Inject;

/**
 * Utilities to manage VirtualBox machine life cycle.
 * 
 * @author Adrian Cole, Mattias Holmqvist, Andrea Turli
 */

@Singleton
public class MachineController {

   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   private final Supplier<VirtualBoxManager> manager;
   private final MachineUtils machineUtils;
   private final ExecutionType executionType;


   @Inject
   public MachineController(Supplier<VirtualBoxManager> manager, MachineUtils machineUtils, ExecutionType executionType) {
      this.manager = manager;
      this.machineUtils = machineUtils;
      this.executionType = executionType;
   }

   public void ensureMachineIsLaunched(String vmName) {
	      machineUtils.applyForMachine(vmName, new LaunchMachineIfNotAlreadyRunning(manager.get(), executionType, ""));
	   }
   
   public void ensureMachineHasPowerDown(String vmName) {
	      while (!manager.get().getVBox().findMachine(vmName).getState().equals(MachineState.POWERED_OFF)) {
	         try {
	            machineUtils.lockSessionOnMachineAndApply(vmName, LockType.Shared, new Function<ISession, Void>() {
	               @Override
	               public Void apply(ISession session) {
	                  IProgress powerDownProgress = session.getConsole().powerDown();
	                  powerDownProgress.waitForCompletion(-1);
	                  return null;
	               }
	            });
	         } catch (RuntimeException e) {
	            // sometimes the machine might be powered of between the while test and the call to
	            // lockSessionOnMachineAndApply
	            if (e.getMessage().contains("Invalid machine state: PoweredOff")) {
	               return;
	            } else if (e.getMessage().contains("VirtualBox error: The object is not ready")) {
	               continue;
	            } else {
	               throw e;
	            }
	         }
	      }
	   }
}
