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

import static com.google.common.base.Preconditions.checkState;
import static org.jclouds.compute.options.RunScriptOptions.Builder.runAsRoot;
import static org.jclouds.virtualbox.config.VirtualBoxConstants.VIRTUALBOX_INSTALLATION_KEY_SEQUENCE;
import static org.jclouds.virtualbox.util.MachineUtils.applyForMachine;
import static org.jclouds.virtualbox.util.MachineUtils.lockSessionOnMachineAndApply;
import static org.virtualbox_4_1.LockType.Shared;

import java.net.URI;

import javax.annotation.Resource;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.compute.callables.RunScriptOnNode;
import org.jclouds.compute.callables.RunScriptOnNode.Factory;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.config.ValueOfConfigurationKeyOrNull;
import org.jclouds.logging.Logger;
import org.jclouds.scriptbuilder.domain.Statements;
import org.jclouds.ssh.SshClient;
import org.jclouds.util.Throwables2;
import org.jclouds.virtualbox.Preconfiguration;
import org.jclouds.virtualbox.domain.ExecutionType;
import org.jclouds.virtualbox.domain.VmSpec;
import org.jclouds.virtualbox.settings.KeyboardScancodes;
import org.virtualbox_4_1.IMachine;
import org.virtualbox_4_1.IProgress;
import org.virtualbox_4_1.ISession;
import org.virtualbox_4_1.LockType;
import org.virtualbox_4_1.VBoxException;
import org.virtualbox_4_1.VirtualBoxManager;

import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.inject.Inject;

@Singleton
public class MutateMachine implements Function<String, IMachine> {

   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   private final Supplier<VirtualBoxManager> manager;
   private final CreateAndRegisterMachineFromIsoIfNotAlreadyExists createAndRegisterMachineFromIsoIfNotAlreadyExists;
   private final ValueOfConfigurationKeyOrNull valueOfConfigurationKeyOrNull;

   private final Supplier<URI> preconfiguration;
   private final Predicate<SshClient> sshResponds;
   private final ExecutionType executionType;

   private final Factory scriptRunner;
   private final Supplier<NodeMetadata> host;

   private final Function<IMachine, SshClient> sshClientForIMachine;

   @Inject
   public MutateMachine(Supplier<VirtualBoxManager> manager,
            CreateAndRegisterMachineFromIsoIfNotAlreadyExists CreateAndRegisterMachineFromIsoIfNotAlreadyExists,
            ValueOfConfigurationKeyOrNull valueOfConfigurationKeyOrNull, Predicate<SshClient> sshResponds,
            Function<IMachine, SshClient> sshClientForIMachine, Supplier<NodeMetadata> host,
            RunScriptOnNode.Factory scriptRunner, @Preconfiguration Supplier<URI> preconfiguration,
            ExecutionType executionType) {
      this.manager = manager;
      this.createAndRegisterMachineFromIsoIfNotAlreadyExists = CreateAndRegisterMachineFromIsoIfNotAlreadyExists;
      this.valueOfConfigurationKeyOrNull = valueOfConfigurationKeyOrNull;
      this.sshResponds = sshResponds;
      this.sshClientForIMachine = sshClientForIMachine;
      this.scriptRunner = scriptRunner;
      this.host = host;
      this.preconfiguration = preconfiguration;
      this.executionType = executionType;
   }

	@Override
	public IMachine apply(String machineId) {
	 return	lockMachineAndApply(manager.get(), LockType.Write, machineId);
	}
	
   /**
    * Locks the machine and executes the given function using the machine matching the given id.
    * Since the machine is locked it is possible to perform some modifications to the IMachine.
    * <p/>
    * Unlocks the machine before returning.
    *
    * @param manager   the VirtualBoxManager
    * @param type      the kind of lock to use when initially locking the machine.
    * @param machineId the id of the machine
    * @return the locked machine.
    */
   public static IMachine lockMachineAndApply(VirtualBoxManager manager, final LockType type, final String machineId) {
      return lockSessionOnMachineAndReturn(manager, type, machineId);
   }
	
   /**
    * Locks the machine and executes the given function using the current session.
    * Since the machine is locked it is possible to perform some modifications to the IMachine.
    * <p/>
    * Unlocks the machine before returning.
    *
    * @param manager   the VirtualBoxManager
    * @param type      the kind of lock to use when initially locking the machine.
    * @param machineId the id of the machine
    * @return the result from applying the function to the session.
    */
   public static IMachine lockSessionOnMachineAndReturn(VirtualBoxManager manager, LockType type, String machineId) {
      try {
         ISession session = manager.getSessionObject();
         IMachine immutableMachine = manager.getVBox().findMachine(machineId);
         immutableMachine.lockMachine(session, type);
         return immutableMachine;
      } catch (VBoxException e) {
         throw new RuntimeException(String.format("error locking %s with %s lock: %s", machineId,
                 type, e.getMessage()), e);
      }
   }


}
