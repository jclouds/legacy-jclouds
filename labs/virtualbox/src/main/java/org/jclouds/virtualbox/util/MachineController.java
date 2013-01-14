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
import static com.google.common.base.Preconditions.checkState;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.jclouds.util.Predicates2.retry;

import java.util.List;

import javax.annotation.Resource;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.logging.Logger;
import org.jclouds.virtualbox.domain.ExecutionType;
import org.jclouds.virtualbox.functions.LaunchMachineIfNotAlreadyRunning;
import org.virtualbox_4_2.AdditionsFacilityStatus;
import org.virtualbox_4_2.AdditionsFacilityType;
import org.virtualbox_4_2.AdditionsRunLevelType;
import org.virtualbox_4_2.IAdditionsFacility;
import org.virtualbox_4_2.IMachine;
import org.virtualbox_4_2.IProgress;
import org.virtualbox_4_2.ISession;
import org.virtualbox_4_2.IVirtualBox;
import org.virtualbox_4_2.LockType;
import org.virtualbox_4_2.MachineState;
import org.virtualbox_4_2.VirtualBoxManager;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.base.Strings;
import com.google.common.base.Supplier;
import com.google.common.collect.Iterables;
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
            session = machineUtils.applyForMachine(vmName, new LaunchMachineIfNotAlreadyRunning(manager.get(),
                  executionType, ""));
         } catch (RuntimeException e) {
            if (e.getMessage().contains(
                  "org.virtualbox_4_2.VBoxException: VirtualBox error: The given session is busy (0x80BB0007)")) {
               throw e;
            } else if (e.getMessage().contains("VirtualBox error: The object is not ready")) {
               continue;
            } else {
               throw e;
            }
         }
      }

      String guestAdditionsInstalled = machineUtils.sharedLockMachineAndApplyToSession(vmName,
            new Function<ISession, String>() {
               @Override
               public String apply(ISession session) {
                  retry(new FacilitiesPredicate(session), 15, 3, SECONDS).apply(4);
                  String guestAdditionsInstalled = session.getConsole().getGuest().getAdditionsVersion();
                  return guestAdditionsInstalled;
               }

            });
      if (!Strings.nullToEmpty(guestAdditionsInstalled).isEmpty()) {
         logger.debug("<< guest additions(%s) installed on vm(%s)", guestAdditionsInstalled, vmName);
         waitVBoxServiceIsActive(vmName);
      } else {
         logger.debug("<< guest additions not available on(%s)", vmName);
      }
      return checkNotNull(session, "session");
   }

   public ISession ensureMachineHasPowerDown(String vmName) {
      ISession session = machineUtils.sharedLockMachineAndApplyToSession(vmName, new Function<ISession, ISession>() {
         @Override
         public ISession apply(ISession session) {
            IProgress powerdownIProgress = session.getConsole().powerDown();
            powerdownIProgress.waitForCompletion(-1);
            return session;
         }
      });
      return checkNotNull(session, "session");
   }

   /**
    * if machine supports ACPI it can be shutdown gently - not powerdown()
    * http://askubuntu.com/questions/82015/shutting-down-ubuntu-server-running-in-headless-virtualbox
    */
   public ISession ensureMachineIsShutdown(String vmName) {
      ISession session = machineUtils.sharedLockMachineAndApplyToSession(vmName, new Function<ISession, ISession>() {
               @Override
               public ISession apply(ISession session) {
                  session.getConsole().powerButton();
                  return session;
               }
            });        
      checkState(
            retry(new MachineStatePredicate(manager.get().getVBox(), vmName), 15, 3, SECONDS).apply(
                  MachineState.PoweredOff), "vm(%s) is not shutdown correctly", vmName);
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
            // test and the call to lockSessionOnMachineAndApply
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

   private void waitVBoxServiceIsActive(final String vmName) {
      machineUtils.sharedLockMachineAndApplyToSession(vmName, new Function<ISession, Void>() {

         @Override
         public Void apply(ISession session) {
            checkState(
                  retry(new AdditionsStatusPredicate(session), 10, 2, SECONDS).apply(AdditionsRunLevelType.Userland),
                  "timed out waiting for additionsRunLevelType to be %s", AdditionsRunLevelType.Userland);
            checkState(retry(new FacilitiesPredicate(session), 15, 3, SECONDS).apply(4),
                  "timed out waiting for 4 running facilities");
            Optional<IAdditionsFacility> vboxServiceFacility = Optional.absent();
            while (!vboxServiceFacility.isPresent()) {
               List<IAdditionsFacility> facilities = session.getConsole().getGuest().getFacilities();
               vboxServiceFacility = Iterables.tryFind(facilities, new Predicate<IAdditionsFacility>() {
                  @Override
                  public boolean apply(IAdditionsFacility additionsFacility) {
                     return additionsFacility.getType().equals(AdditionsFacilityType.VBoxService)
                           && additionsFacility.getStatus().equals(AdditionsFacilityStatus.Active);
                  }
               });
            }
            logger.debug("<< virtualbox service ready on vm(%s)", vmName);
            return null;
         }
      });
   }
   
   private static class AdditionsStatusPredicate implements Predicate<AdditionsRunLevelType> {
      private final ISession session;
      
      AdditionsStatusPredicate(ISession session) {
         this.session = session;
      }
      
      @Override
      public boolean apply(AdditionsRunLevelType input) {
         return session.getConsole().getGuest().getAdditionsStatus(input);
      }
   }
   
   private static class FacilitiesPredicate implements Predicate<Integer> {
      private final ISession session;
      
      FacilitiesPredicate(ISession session) {
         this.session = session;
      }
      
      @Override
      public boolean apply(Integer input) {
         return session.getConsole().getGuest().getFacilities().size() == input;
      }
   }
   
   private static class MachineStatePredicate implements Predicate<MachineState> {
      private final IVirtualBox virtualBox;
      private final String vmName;
      
      MachineStatePredicate(IVirtualBox virtualBox, String vmName) {
         this.virtualBox = virtualBox;
         this.vmName = vmName;
      }
      
      @Override
      public boolean apply(MachineState input) {
         MachineState state = virtualBox.findMachine(vmName).getState();
         return state.equals(input);
      }
   }

}
