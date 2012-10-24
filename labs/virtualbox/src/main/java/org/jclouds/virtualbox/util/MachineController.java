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

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.logging.Logger;
import org.jclouds.virtualbox.domain.ExecutionType;
import org.jclouds.virtualbox.functions.LaunchMachineIfNotAlreadyRunning;
import org.virtualbox_4_1.AdditionsFacilityStatus;
import org.virtualbox_4_1.AdditionsFacilityType;
import org.virtualbox_4_1.AdditionsRunLevelType;
import org.virtualbox_4_1.IAdditionsFacility;
import org.virtualbox_4_1.IMachine;
import org.virtualbox_4_1.IProgress;
import org.virtualbox_4_1.ISession;
import org.virtualbox_4_1.LockType;
import org.virtualbox_4_1.MachineState;
import org.virtualbox_4_1.SessionState;
import org.virtualbox_4_1.VirtualBoxManager;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.base.Strings;
import com.google.common.base.Supplier;
import com.google.common.collect.Iterables;
import com.google.common.util.concurrent.Uninterruptibles;
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
      IMachine machine = manager.get().getVBox().findMachine(vmName);
      while (!machine.getState().equals(MachineState.Running)) {
         try {
            session = machineUtils.applyForMachine(vmName,
                  new LaunchMachineIfNotAlreadyRunning(manager.get(),
                        executionType, ""));
         } catch (RuntimeException e) {
            if (e.getMessage()
                  .contains(
                        "org.virtualbox_4_1.VBoxException: VirtualBox error: The given session is busy (0x80BB0007)")) {
               throw e;
            } else if (e.getMessage().contains(
                  "VirtualBox error: The object is not ready")) {
               continue;
            } else {
               throw e;
            }
         }
      }
      // for scancode
      Uninterruptibles.sleepUninterruptibly(5, TimeUnit.SECONDS);
      
      String guestAdditionsInstalled = machineUtils.sharedLockMachineAndApplyToSession(vmName,
            new Function<ISession, String>() {

               @Override
               public String apply(ISession session) {
                  int attempts = 0;
                  String guestAdditionsInstalled = null;
                  while (!!session.getConsole().getGuest()
                        .getAdditionsVersion().isEmpty() && attempts  < 3) {
                     Uninterruptibles.sleepUninterruptibly(2, TimeUnit.SECONDS);
                     guestAdditionsInstalled = session.getConsole().getGuest()
                           .getAdditionsVersion();
                     attempts++;
                  }
                  return guestAdditionsInstalled;
               }
               
      });
      if(!Strings.nullToEmpty(guestAdditionsInstalled).isEmpty()) {
         waitVBoxServiceIsActive(vmName);
      }

      return checkNotNull(session, "session");
   }

   public ISession ensureMachineHasPowerDown(String vmName) {
      ISession session = manager.get().getSessionObject();
      IMachine machine = manager.get().getVBox().findMachine(vmName);
      while (!machine.getState().equals(MachineState.PoweredOff)) {
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
            if (e.getMessage().contains("Invalid machine state: PoweredOff")) {
               throw e;
            } else if (e.getMessage().contains("VirtualBox error: The object is not ready")) {
               continue;
            } else {
               throw e;
            }
         }
      }
      safeCheckMachineIsUnlocked(machine);
      return checkNotNull(session, "session");
   }
   
   /** 
    * if machine supports ACPI it can be shutdown gently - not powerdown()
    * http://askubuntu.com/questions/82015/shutting-down-ubuntu-server-running-in-headless-virtualbox
    */
   public ISession ensureMachineIsShutdown(String vmName) {
      IMachine machine = manager.get().getVBox().findMachine(vmName);
      ISession session = machineUtils.lockSessionOnMachineAndApply(vmName, LockType.Shared,
                     new Function<ISession, ISession>() {
                        @Override
                        public ISession apply(ISession session) {
                           session.getConsole().powerButton();
                           return session;
                        }
                     });
      safeCheckMachineIsUnlocked(machine);
      return checkNotNull(session, "session");
   }

   public void ensureMachineIsPaused(String vmName) {
      IMachine machine = manager.get().getVBox().findMachine(vmName);
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
      safeCheckMachineIsUnlocked(machine);
   }
   
   public void ensureMachineIsResumed(String vmName) {
      IMachine machine = manager.get().getVBox().findMachine(vmName);
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
      safeCheckMachineIsUnlocked(machine);
   }
   
   private void safeCheckMachineIsUnlocked(IMachine machine) {
      int guard = 0;
       while (!machine.getSessionState().equals(SessionState.Unlocked)) {
          if(guard >= 5) {
             logger.warn("Machine session (%s) possibly still unlocked!!!", machine.getName());
             break;
          }
          logger.debug("Machine session (%s) not unlocked - wait ...", machine.getName());
          Uninterruptibles.sleepUninterruptibly(3, TimeUnit.SECONDS);
          guard++;
       }
       logger.debug("Machine session (%s) is %s", machine.getName(), machine.getSessionState());
   }
   
   private void waitVBoxServiceIsActive(String vmName) {
      machineUtils.sharedLockMachineAndApplyToSession(vmName, new Function<ISession, Void>() {

         @Override
         public Void apply(ISession session) {
            session.getConsole().getGuest().setStatisticsUpdateInterval(1l);
            while (!session.getConsole().getGuest().getAdditionsStatus(AdditionsRunLevelType.Userland)) {
               Uninterruptibles.sleepUninterruptibly(200, TimeUnit.MILLISECONDS);
            }
            
            List<IAdditionsFacility> facilities = session.getConsole().getGuest().getFacilities();
            while (facilities.size() != 4) {
               Uninterruptibles.sleepUninterruptibly(200, TimeUnit.MILLISECONDS);
               facilities = session.getConsole().getGuest().getFacilities();
            }
            facilities = session.getConsole().getGuest().getFacilities();

            Optional<IAdditionsFacility> vboxServiceFacility = Optional.absent();
            while (!vboxServiceFacility.isPresent()) {
               vboxServiceFacility = Iterables.tryFind(session.getConsole().getGuest().getFacilities(),
                     new Predicate<IAdditionsFacility>() {
                        @Override
                        public boolean apply(IAdditionsFacility additionsFacility) {
                           return additionsFacility.getType().equals(AdditionsFacilityType.VBoxService);
                        };
                     });
            }

            while(!vboxServiceFacility.get().getStatus().equals(AdditionsFacilityStatus.Active)) {
               Uninterruptibles.sleepUninterruptibly(1, TimeUnit.SECONDS);
            }
            Uninterruptibles.sleepUninterruptibly(3, TimeUnit.SECONDS);
            return null;
         }
      });
   }

}
