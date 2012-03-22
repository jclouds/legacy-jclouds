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

import javax.annotation.Resource;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.logging.Logger;
import org.virtualbox_4_1.IMachine;
import org.virtualbox_4_1.ISession;
import org.virtualbox_4_1.LockType;
import org.virtualbox_4_1.VBoxException;
import org.virtualbox_4_1.VirtualBoxManager;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.inject.Inject;

@Singleton
public class MutableMachine implements Function<String, ISession> {

   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   private final Supplier<VirtualBoxManager> manager;
   private final LockType lockType;


   @Inject
   public MutableMachine(Supplier<VirtualBoxManager> manager,
         LockType lockType) {
      this.manager = manager;
      this.lockType = lockType;
   }

	@Override
	public ISession apply(String machineId) {
      return lockSessionOnMachineAndReturn(manager.get(), lockType, machineId);
	}

   /**
    * Locks the machine and executes the given function using the current session.
    * Since the machine is locked it is possible to perform some modifications to the IMachine.
    * If locking failes tries to lock some until retries are exhausted.
    * <p/>
    * Unlocks the machine before returning.
    * 
    *
    * @param manager   the VirtualBoxManager
    * @param type      the kind of lock to use when initially locking the machine.
    * @param machineId the id of the machine
    * @return the ISession bounded to the machine locked.
    */
   public ISession lockSessionOnMachineAndReturn(VirtualBoxManager manager, LockType type, String machineId) {
      int retries = 5;
      int count = 0;
      while (true) {
         try {
            ISession session = manager.getSessionObject();
            IMachine immutableMachine = manager.getVBox().findMachine(machineId);
            immutableMachine.lockMachine(session, type);
            return session;
         } catch (VBoxException e) {
            count++;
            logger.warn("Could not lock machine (try %i of %i). Error: %s", retries, count, e.getMessage());
            if (count == retries){
               throw new RuntimeException(String.format("error locking %s with %s lock: %s", machineId,
                        type, e.getMessage()), e);   
            }
         }
      }
   }


}
