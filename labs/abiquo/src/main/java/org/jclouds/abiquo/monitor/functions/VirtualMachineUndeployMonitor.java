/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.jclouds.abiquo.monitor.functions;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.annotation.Resource;
import javax.inject.Singleton;

import org.jclouds.abiquo.domain.cloud.VirtualMachine;
import org.jclouds.abiquo.monitor.MonitorStatus;
import org.jclouds.logging.Logger;
import org.jclouds.rest.ResourceNotFoundException;

import com.abiquo.server.core.cloud.VirtualMachineState;
import com.google.common.base.Function;

/**
 * This class takes care of monitoring the a undeploy of a
 * {@link VirtualMachine}.
 * 
 * @author Serafin Sedano
 */
@Singleton
public class VirtualMachineUndeployMonitor implements Function<VirtualMachine, MonitorStatus> {
   @Resource
   protected Logger logger = Logger.NULL;

   @Override
   public MonitorStatus apply(final VirtualMachine virtualMachine) {
      checkNotNull(virtualMachine, "virtualMachine");

      try {
         VirtualMachineState state = virtualMachine.getState();

         // This state may be reached if the undeploy process fails and a
         // rollback is done
         if (state.existsInHypervisor()) {
            return MonitorStatus.FAILED;
         }

         switch (state) {
            case UNKNOWN:
               return MonitorStatus.FAILED;
            case NOT_ALLOCATED:
               return MonitorStatus.DONE;
            default:
               return MonitorStatus.CONTINUE;
         }
      } catch (ResourceNotFoundException nfe) {
         logger.warn("virtual machine %s not found, assuming it was undeployed successfully, "
               + "stop monitor with DONE", virtualMachine);
         return MonitorStatus.DONE;
      } catch (Exception ex) {
         logger.warn(ex, "exception thrown while monitoring %s on %s, returning CONTINUE", virtualMachine, getClass()
               .getName());

         return MonitorStatus.CONTINUE;
      }
   }
}
