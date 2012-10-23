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

package org.jclouds.abiquo.monitor;

import java.util.concurrent.TimeUnit;

import org.jclouds.abiquo.domain.cloud.VirtualMachine;
import org.jclouds.abiquo.features.services.MonitoringService;
import org.jclouds.abiquo.monitor.internal.BaseVirtualMachineMonitor;

import com.abiquo.server.core.cloud.VirtualMachineState;
import com.google.inject.ImplementedBy;

/**
 * {@link VirtualMachine} monitoring features.
 * 
 * @author Ignasi Barrera
 */
@ImplementedBy(BaseVirtualMachineMonitor.class)
public interface VirtualMachineMonitor extends MonitoringService {
   /**
    * Monitor the given {@link VirtualMachine}s and block until all deploys
    * finish.
    * 
    * @param vm
    *           The {@link VirtualMachine}s to monitor.
    */
   void awaitCompletionDeploy(final VirtualMachine... vm);

   /**
    * Monitor the given {@link VirtualMachine}s and populate an event when all
    * deploys finish.
    * 
    * @param vms
    *           The {@link VirtualMachine}s to monitor.
    */
   public void monitorDeploy(final VirtualMachine... vms);

   /**
    * Monitor the given {@link VirtualMachine}s and block until all deploys
    * finish.
    * 
    * @param maxWait
    *           The maximum time to wait.
    * @param timeUnit
    *           The time unit for the maxWait parameter.
    * @param vm
    *           The {@link VirtualMachine}s to monitor.
    */
   void awaitCompletionDeploy(final Long maxWait, final TimeUnit timeUnit, final VirtualMachine... vm);

   /**
    * Monitor the given {@link VirtualMachine}s and populate an event when all
    * deploys finish.
    * 
    * @param maxWait
    *           The maximum time to wait.
    * @param timeUnit
    *           The time unit for the maxWait parameter.
    * @param vms
    *           The {@link VirtualMachine}s to monitor.
    */
   public void monitorDeploy(final Long maxWait, final TimeUnit timeUnit, final VirtualMachine... vms);

   /**
    * Monitor the given {@link VirtualMachine}s and block until all undeploys
    * finish.
    * 
    * @param vm
    *           The {@link VirtualMachine}s to monitor.
    */
   void awaitCompletionUndeploy(final VirtualMachine... vm);

   /**
    * Monitor the given {@link VirtualMachine}s and populate an event when all
    * undeploys finish.
    * 
    * @param vms
    *           The {@link VirtualMachine}s to monitor.
    */
   public void monitorUndeploy(final VirtualMachine... vms);

   /**
    * Monitor the given {@link VirtualMachine}s and blocks until all undeploys
    * finish.
    * 
    * @param maxWait
    *           The maximum time to wait.
    * @param timeUnit
    *           The time unit for the maxWait parameter.
    * @param vm
    *           The {@link VirtualMachine}s to monitor.
    */
   void awaitCompletionUndeploy(final Long maxWait, final TimeUnit timeUnit, final VirtualMachine... vm);

   /**
    * Monitor the given {@link VirtualMachine}s and populate an event when all
    * undeploys finish.
    * 
    * @param maxWait
    *           The maximum time to wait.
    * @param timeUnit
    *           The time unit for the maxWait parameter.
    * @param callback
    *           The callback.
    * @param vms
    *           The {@link VirtualMachine}s to monitor.
    */
   public void monitorUndeploy(final Long maxWait, final TimeUnit timeUnit, final VirtualMachine... vms);

   /**
    * Monitor the given {@link VirtualMachine}s and block until it is in the
    * given state.
    * 
    * @param vm
    *           The {@link VirtualMachine}s to monitor.
    */
   void awaitState(VirtualMachineState state, final VirtualMachine... vm);

   /**
    * Monitor the given {@link VirtualMachine}s and populate an event when it is
    * in the given state.
    * 
    * @param vms
    *           The {@link VirtualMachine}s to monitor.
    */
   public void monitorState(VirtualMachineState state, final VirtualMachine... vms);

   /**
    * Monitor the given {@link VirtualMachine}s and block until it is in the
    * given state.
    * 
    * @param maxWait
    *           The maximum time to wait.
    * @param timeUnit
    *           The time unit for the maxWait parameter.
    * @param vm
    *           The {@link VirtualMachine}s to monitor.
    */
   void awaitState(final Long maxWait, final TimeUnit timeUnit, VirtualMachineState state, final VirtualMachine... vm);

   /**
    * Monitor the given {@link VirtualMachine}s and populate an event when it is
    * in the given state.
    * 
    * @param maxWait
    *           The maximum time to wait.
    * @param timeUnit
    *           The time unit for the maxWait parameter.
    * @param callback
    *           The callback.
    * @param vms
    *           The {@link VirtualMachine}s to monitor.
    */
   public void monitorState(final Long maxWait, final TimeUnit timeUnit, VirtualMachineState state,
         final VirtualMachine... vms);
}
