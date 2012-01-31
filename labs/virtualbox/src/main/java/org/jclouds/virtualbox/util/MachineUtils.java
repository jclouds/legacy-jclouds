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

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.inject.Inject;
import org.jclouds.compute.callables.RunScriptOnNode;
import org.jclouds.compute.callables.RunScriptOnNode.Factory;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.logging.Logger;
import org.jclouds.scriptbuilder.domain.Statement;
import org.jclouds.util.Throwables2;
import org.jclouds.virtualbox.functions.MutableMachine;
import org.virtualbox_4_1.*;

import javax.annotation.Resource;
import javax.inject.Named;
import javax.inject.Singleton;

import static org.jclouds.compute.options.RunScriptOptions.Builder.runAsRoot;
import static org.jclouds.scriptbuilder.domain.Statements.*;

/**
 * Utilities for executing functions on a VirtualBox machine.
 *
 * @author Adrian Cole, Mattias Holmqvist, Andrea Turli
 */

@Singleton
public class MachineUtils {

   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   private final Supplier<VirtualBoxManager> manager;
   private final Factory scriptRunner;
   private final Supplier<NodeMetadata> host;

   @Inject
   public MachineUtils(Supplier<VirtualBoxManager> manager, RunScriptOnNode.Factory scriptRunner, Supplier<NodeMetadata> host) {
      super();
      this.manager = manager;
      this.scriptRunner = scriptRunner;
      this.host = host;
   }

   /**
    * Locks the machine and executes the given function using the machine
    * matching the given id. Since the machine is locked it is possible to
    * perform some modifications to the IMachine.
    * <p/>
    * Unlocks the machine before returning.
    *
    * @param machineId the id of the machine
    * @param function  the function to execute
    * @return the result from applying the function to the machine.
    */
   public <T> T writeLockMachineAndApply(final String machineId, final Function<IMachine, T> function) {
      return lockSessionOnMachineAndApply(machineId, LockType.Write,
              new Function<ISession, T>() {

                 @Override
                 public T apply(ISession session) {
                    return function.apply(session.getMachine());
                 }

                 @Override
                 public String toString() {
                    return function.toString();
                 }

              });
   }

   /**
    * Locks the machine and executes the given function using the current
    * session. Since the machine is locked it is possible to perform some
    * modifications to the IMachine.
    * <p/>
    * Unlocks the machine before returning.
    *
    * @param type      the kind of lock to use when initially locking the machine.
    * @param machineId the id of the machine
    * @param function  the function to execute
    * @return the result from applying the function to the session.
    */
   public <T> T lockSessionOnMachineAndApply(String machineId, LockType type, Function<ISession, T> function) {
      try {
         ISession session = lockSessionOnMachine(type, machineId);
         try {
            return function.apply(session);
         } finally {
            session.unlockMachine();
         }
      } catch (VBoxException e) {
         throw new RuntimeException(String.format(
                 "error applying %s to %s with %s lock: %s", function, machineId,
                 type, e.getMessage()), e);
      }
   }

   private ISession lockSessionOnMachine(LockType type, String machineId) {
      return new MutableMachine(manager, type).apply(machineId);
   }

   private void unlockMachine(final String machineId) {
      IMachine immutableMachine = manager.get().getVBox().findMachine(machineId);
      if (immutableMachine.getSessionState().equals(SessionState.Locked)) {
         Statement kill = newStatementList(call("default"),
                 findPid(immutableMachine.getSessionPid().toString()), kill());
         scriptRunner
                 .create(host.get(), kill,
                         runAsRoot(false).wrapInInitScript(false)).init().call();
      }
   }

   /**
    * Unlocks the machine and executes the given function using the machine
    * matching the given id. Since the machine is unlocked it is possible to
    * delete the IMachine.
    * <p/>
    * <p/>
    * <h3>Note!</h3> Currently, this can only unlock the machine, if the lock
    * was created in the current session.
    *
    * @param machineId the id of the machine
    * @param function  the function to execute
    * @return the result from applying the function to the machine.
    */
   public <T> T unlockMachineAndApply(final String machineId, final Function<IMachine, T> function) {

      try {
         unlockMachine(machineId);
         IMachine immutableMachine = manager.get().getVBox().findMachine(machineId);
         return function.apply(immutableMachine);

      } catch (VBoxException e) {
         throw new RuntimeException(String.format(
                 "error applying %s to %s: %s", function, machineId,
                 e.getMessage()), e);
      }
   }

   /**
    * Unlocks the machine and executes the given function, if the machine is
    * registered. Since the machine is unlocked it is possible to delete the
    * machine.
    * <p/>
    *
    * @param machineId the id of the machine
    * @param function  the function to execute
    * @return the result from applying the function to the session.
    */
   public <T> T unlockMachineAndApplyOrReturnNullIfNotRegistered(String machineId,
                                                                 Function<IMachine, T> function) {
      try {
         return unlockMachineAndApply(machineId, function);
      } catch (RuntimeException e) {
         VBoxException vbex = Throwables2.getFirstThrowableOfType(e,
                 VBoxException.class);
         if (vbex != null
                 && vbex.getMessage().indexOf("not find a registered") == -1)
            throw e;
         return null;
      }
   }

   /**
    * @param machineId
    * @param function
    * @return
    */
   public <T> T applyForMachine(final String machineId, final Function<IMachine, T> function) {
      final IMachine immutableMachine = manager.get().getVBox().findMachine(machineId);
      return new Function<IMachine, T>() {
         @Override
         public T apply(IMachine machine) {
            return function.apply(machine);
         }

         @Override
         public String toString() {
            return function.toString();
         }
      }.apply(immutableMachine);
   }

}
