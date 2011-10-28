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

import com.google.common.base.Function;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.logging.Logger;
import org.jclouds.virtualbox.domain.ErrorCode;
import org.jclouds.virtualbox.domain.ExecutionType;
import org.virtualbox_4_1.*;
import org.virtualbox_4_1.jaxws.RuntimeFaultMsg;

import javax.annotation.Nullable;
import javax.annotation.Resource;
import javax.inject.Named;
import javax.lang.model.type.ExecutableType;

import static com.google.common.base.Throwables.propagate;
import static javax.xml.bind.DatatypeConverter.parseUnsignedInt;

/**
 * Starts a machine using launchMachine() with the provided type and environment.
 * <p/>
 * Note that launchMachine() may throw VBoxException with the following error codes:
 * <p/>
 * VBOX_E_UNEXPECTED:	Virtual machine not registered.
 * VBOX_E_INVALIDARG:	Invalid session type type.
 * VBOX_E_OBJECT_NOT_FOUND:	No machine matching machineId found.
 * VBOX_E_INVALID_OBJECT_STATE:	Session already open or being opened.
 * VBOX_E_IPRT_ERROR:	Launching process for machine failed.
 * VBOX_E_VM_ERROR:	Failed to assign machine to session.
 *
 * @author Mattias Holmqvist
 * 
 * @see ErrorCode
 */
public class LaunchMachineIfNotAlreadyRunning implements Function<IMachine, Void> {

   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   private final VirtualBoxManager manager;
   private final ExecutionType type;
   private final String environment;

   public LaunchMachineIfNotAlreadyRunning(VirtualBoxManager manager, ExecutionType type, String environment) {
      this.manager = manager;
      this.type = type;
      this.environment = environment;
   }

   @Override
   public Void apply(@Nullable IMachine machine) {
      try {
         final IProgress progress = machine.launchVMProcess(manager.getSessionObject(), type.stringValue(), environment);
         progress.waitForCompletion(-1);
         Thread.sleep(5000);
      } catch (InterruptedException e) {
         propagate(e);
      } catch (VBoxException e) {
         ErrorCode errorCode = ErrorCode.valueOf(e);
         switch (errorCode) {
            case VBOX_E_INVALID_OBJECT_STATE:
               logger.warn(e, "Could not start machine. Got error code %s from launchMachine(). " +
                       "The machine might already be running.", errorCode);
               break;
            default:
               propagate(e);
         }
      }
      return null;
   }
}
