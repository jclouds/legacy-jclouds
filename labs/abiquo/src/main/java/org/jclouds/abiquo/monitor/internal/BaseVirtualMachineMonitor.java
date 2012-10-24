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

package org.jclouds.abiquo.monitor.internal;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.Constants.PROPERTY_SCHEDULER_THREADS;
import static org.jclouds.abiquo.config.AbiquoProperties.ASYNC_TASK_MONITOR_DELAY;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.abiquo.AbiquoApi;
import org.jclouds.abiquo.AbiquoAsyncApi;
import org.jclouds.abiquo.domain.cloud.VirtualMachine;
import org.jclouds.abiquo.internal.BaseMonitoringService;
import org.jclouds.abiquo.monitor.VirtualMachineMonitor;
import org.jclouds.abiquo.monitor.functions.VirtualMachineDeployMonitor;
import org.jclouds.abiquo.monitor.functions.VirtualMachineStateMonitor;
import org.jclouds.abiquo.monitor.functions.VirtualMachineUndeployMonitor;
import org.jclouds.rest.RestContext;

import com.abiquo.server.core.cloud.VirtualMachineState;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.eventbus.EventBus;

/**
 * Default monitor for {@link VirtualMachine} objects.
 * 
 * @author Ignasi Barrera
 */
@Singleton
public class BaseVirtualMachineMonitor extends BaseMonitoringService implements VirtualMachineMonitor {
   @VisibleForTesting
   protected VirtualMachineDeployMonitor deployMonitor;

   @VisibleForTesting
   protected VirtualMachineUndeployMonitor undeployMonitor;

   @Inject
   public BaseVirtualMachineMonitor(final RestContext<AbiquoApi, AbiquoAsyncApi> context,
         @Named(PROPERTY_SCHEDULER_THREADS) final ScheduledExecutorService scheduler,
         @Named(ASYNC_TASK_MONITOR_DELAY) final Long pollingDelay, final EventBus eventBus,
         final VirtualMachineDeployMonitor deployMonitor, final VirtualMachineUndeployMonitor undeployMonitor) {
      super(context, scheduler, pollingDelay, eventBus);
      this.deployMonitor = checkNotNull(deployMonitor, "deployMonitor");
      this.undeployMonitor = checkNotNull(undeployMonitor, "undeployMonitor");
   }

   @Override
   public void awaitCompletionDeploy(final VirtualMachine... vms) {
      awaitCompletion(deployMonitor, vms);
   }

   @Override
   public void monitorDeploy(final VirtualMachine... vms) {
      monitor(deployMonitor, vms);
   }

   @Override
   public void awaitCompletionDeploy(final Long maxWait, final TimeUnit timeUnit, final VirtualMachine... vms) {
      awaitCompletion(maxWait, timeUnit, deployMonitor, vms);
   }

   @Override
   public void monitorDeploy(final Long maxWait, final TimeUnit timeUnit, final VirtualMachine... vms) {
      monitor(maxWait, timeUnit, deployMonitor, vms);
   }

   @Override
   public void awaitCompletionUndeploy(final VirtualMachine... vms) {
      awaitCompletion(undeployMonitor, vms);
   }

   @Override
   public void monitorUndeploy(final VirtualMachine... vms) {
      monitor(undeployMonitor, vms);
   }

   @Override
   public void awaitCompletionUndeploy(final Long maxWait, final TimeUnit timeUnit, final VirtualMachine... vms) {
      awaitCompletion(maxWait, timeUnit, undeployMonitor, vms);
   }

   @Override
   public void monitorUndeploy(final Long maxWait, final TimeUnit timeUnit, final VirtualMachine... vms) {
      monitor(maxWait, timeUnit, undeployMonitor, vms);
   }

   @Override
   public void awaitState(final VirtualMachineState state, final VirtualMachine... vms) {
      awaitCompletion(new VirtualMachineStateMonitor(state), vms);
   }

   @Override
   public void monitorState(final VirtualMachineState state, final VirtualMachine... vms) {
      monitor(new VirtualMachineStateMonitor(state), vms);
   }

   @Override
   public void awaitState(final Long maxWait, final TimeUnit timeUnit, final VirtualMachineState state,
         final VirtualMachine... vms) {
      awaitCompletion(maxWait, timeUnit, new VirtualMachineStateMonitor(state), vms);
   }

   @Override
   public void monitorState(final Long maxWait, final TimeUnit timeUnit, final VirtualMachineState state,
         final VirtualMachine... vms) {
      monitor(maxWait, timeUnit, new VirtualMachineStateMonitor(state), vms);
   }
}
