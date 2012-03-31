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

import static com.google.common.base.Preconditions.checkNotNull;

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
import com.google.common.base.Throwables;
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

   public ISession ensureMachineIsLaunched(String vmName) {
      ISession session = null;
      while (!manager.get().getVBox().findMachine(vmName).getState().equals(MachineState.Running)) {
         try {
            session = machineUtils.applyForMachine(vmName, new LaunchMachineIfNotAlreadyRunning(manager.get(),
                     executionType, ""));
         } catch (RuntimeException e) {
            if (e.getMessage().contains(
                     "org.virtualbox_4_1.VBoxException: VirtualBox error: The given session is busy (0x80BB0007)")) {
               throw e;
            } else if (e.getMessage().contains("VirtualBox error: The object is not ready")) {
               continue;
            } else {
               throw e;
            }
         }
      }
      return checkNotNull(session, "session");
   }

   public ISession ensureMachineHasPowerDown(String vmName) {
      ISession session = manager.get().getSessionObject();
      while (!manager.get().getVBox().findMachine(vmName).getState().equals(MachineState.PoweredOff)) {
         try {
            session = machineUtils.lockSessionOnMachineAndApply(vmName, LockType.Shared,
                     new Function<ISession, ISession>() {
                        @Override
                        public ISession apply(ISession session) {
                           IProgress powerDownProgress = session.getConsole().powerDown();
                           powerDownProgress.waitForCompletion(-1);
                           return session;
                        }
                     });
         } catch (RuntimeException e) {
            // sometimes the machine might be powered of between the while
            // test and the call to
            // lockSessionOnMachineAndApply
            if (e.getMessage().contains("Invalid machine state: PoweredOff")) {
               throw e;
            } else if (e.getMessage().contains("VirtualBox error: The object is not ready")) {
               continue;
            } else {
               throw e;
            }
         }
      }
      return checkNotNull(session, "session");
   }
   
   /** 
    * if machine supports ACPI it can be shutdown gently - not powerdown()
    * http://askubuntu.com/questions/82015/shutting-down-ubuntu-server-running-in-headless-virtualbox
    */
   public ISession ensureMachineIsShutdown(String vmName) {
      ISession session = machineUtils.lockSessionOnMachineAndApply(vmName, LockType.Shared,
                     new Function<ISession, ISession>() {
                        @Override
                        public ISession apply(ISession session) {
                           session.getConsole().powerButton();
                           return session;
                        }
                     });
      int count = 0;
      while (!manager.get().getVBox().findMachine(vmName).getState().equals(MachineState.PoweredOff) && count < 10) {
         try {
            Thread.sleep(500l * count);
         } catch (InterruptedException e) {
            Throwables.propagate(e);
         }
         count++;
      }
      return checkNotNull(session, "session");
   }

   public void ensureMachineIsPaused(String vmName) {
      while (!manager.get().getVBox().findMachine(vmName).getState().equals(MachineState.Paused)) {
         try {
            machineUtils.lockSessionOnMachineAndApply(vmName, LockType.Shared, new Function<ISession, Void>() {
               @Override
               public Void apply(ISession session) {
                  session.getConsole().pause();
                  return null;
               }
            });
         } catch (RuntimeException e) {
            // sometimes the machine might be powered of between the while
            // test and the call to
            // lockSessionOnMachineAndApply
            if (e.getMessage().contains("Invalid machine state: Paused")) {
               return;
            } else if (e.getMessage().contains("VirtualBox error: The object is not ready")) {
               continue;
            } else {
               throw e;
            }
         }
      }
   }
   
   public void ensureMachineIsResumed(String vmName) {
      while (!manager.get().getVBox().findMachine(vmName).getState().equals(MachineState.Running)) {
         try {
            machineUtils.lockSessionOnMachineAndApply(vmName, LockType.Shared, new Function<ISession, Void>() {
               @Override
               public Void apply(ISession session) {
                  session.getConsole().resume();
                  return null;
               }
            });
         } catch (RuntimeException e) {
            // sometimes the machine might be powered of between the while
            // test and the call to
            // lockSessionOnMachineAndApply
            if (e.getMessage().contains("Invalid machine state: Resumed")) {
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
