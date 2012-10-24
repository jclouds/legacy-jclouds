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
import org.jclouds.abiquo.domain.task.AsyncTask;
import org.jclouds.abiquo.internal.BaseMonitoringService;
import org.jclouds.abiquo.monitor.AsyncTaskMonitor;
import org.jclouds.abiquo.monitor.functions.AsyncTaskStatusMonitor;
import org.jclouds.rest.RestContext;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.eventbus.EventBus;

/**
 * Default monitor for {@link AsyncTask} objects.
 * 
 * @author Ignasi Barrera
 */
@Singleton
public class BaseAsyncTaskMonitor extends BaseMonitoringService implements AsyncTaskMonitor {

   @VisibleForTesting
   protected AsyncTaskStatusMonitor taskMonitor;

   @Inject
   public BaseAsyncTaskMonitor(final RestContext<AbiquoApi, AbiquoAsyncApi> context,
         @Named(PROPERTY_SCHEDULER_THREADS) final ScheduledExecutorService scheduler,
         @Named(ASYNC_TASK_MONITOR_DELAY) final Long pollingDelay, final EventBus eventBus,
         final AsyncTaskStatusMonitor monitor) {
      super(context, scheduler, pollingDelay, eventBus);
      this.taskMonitor = checkNotNull(monitor, "monitor");
   }

   @Override
   public void awaitCompletion(final AsyncTask... tasks) {
      awaitCompletion(taskMonitor, tasks);
   }

   @Override
   public void monitor(final AsyncTask... tasks) {
      monitor(taskMonitor, tasks);
   }

   @Override
   public void awaitCompletion(final Long maxWait, final TimeUnit timeUnit, final AsyncTask... tasks) {
      awaitCompletion(maxWait, timeUnit, taskMonitor, tasks);
   }

   @Override
   public void monitor(final Long maxWait, final TimeUnit timeUnit, final AsyncTask... tasks) {
      monitor(maxWait, timeUnit, taskMonitor, tasks);
   }

}
