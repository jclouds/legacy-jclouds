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

package org.jclouds.abiquo.internal;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.Constants.PROPERTY_SCHEDULER_THREADS;
import static org.jclouds.abiquo.config.AbiquoProperties.ASYNC_TASK_MONITOR_DELAY;

import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.abiquo.AbiquoApi;
import org.jclouds.abiquo.AbiquoAsyncApi;
import org.jclouds.abiquo.events.handlers.AbstractEventHandler;
import org.jclouds.abiquo.events.handlers.BlockingEventHandler;
import org.jclouds.abiquo.events.monitor.CompletedEvent;
import org.jclouds.abiquo.events.monitor.FailedEvent;
import org.jclouds.abiquo.events.monitor.TimeoutEvent;
import org.jclouds.abiquo.features.services.MonitoringService;
import org.jclouds.abiquo.monitor.AsyncTaskMonitor;
import org.jclouds.abiquo.monitor.ConversionMonitor;
import org.jclouds.abiquo.monitor.MonitorStatus;
import org.jclouds.abiquo.monitor.VirtualApplianceMonitor;
import org.jclouds.abiquo.monitor.VirtualMachineMonitor;
import org.jclouds.logging.Logger;
import org.jclouds.rest.RestContext;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;

/**
 * Utility service to monitor asynchronous operations.
 * 
 * @author Ignasi Barrera
 * @author Francesc Montserrat
 */
@Singleton
public class BaseMonitoringService implements MonitoringService {
   @VisibleForTesting
   protected RestContext<AbiquoApi, AbiquoAsyncApi> context;

   /** The scheduler used to perform monitoring tasks. */
   @VisibleForTesting
   protected ScheduledExecutorService scheduler;

   @VisibleForTesting
   protected Long pollingDelay;

   /**
    * The event bus used to dispatch monitoring events.
    * <p>
    * A sync bus is used by default, to prevent deadlocks when using the
    * {@link BlockingEventHandler}.
    */
   @VisibleForTesting
   protected EventBus eventBus;

   @Resource
   private Logger logger = Logger.NULL;

   @Inject
   public BaseMonitoringService(final RestContext<AbiquoApi, AbiquoAsyncApi> context,
         @Named(PROPERTY_SCHEDULER_THREADS) final ScheduledExecutorService scheduler,
         @Named(ASYNC_TASK_MONITOR_DELAY) final Long pollingDelay, final EventBus eventBus) {
      this.context = checkNotNull(context, "context");
      this.scheduler = checkNotNull(scheduler, "scheduler");
      this.pollingDelay = checkNotNull(pollingDelay, "pollingDelay");
      this.eventBus = checkNotNull(eventBus, "eventBus");
   }

   /*************** Generic monitoring methods ***************/

   @Override
   public <T> void awaitCompletion(final Function<T, MonitorStatus> completeCondition, final T... objects) {
      awaitCompletion(null, null, completeCondition, objects);
   }

   @Override
   public <T> void awaitCompletion(final Long maxWait, final TimeUnit timeUnit,
         final Function<T, MonitorStatus> completeCondition, final T... objects) {
      checkNotNull(completeCondition, "completeCondition");

      if (objects != null && objects.length > 0) {
         BlockingEventHandler<T> blockingHandler = new BlockingEventHandler<T>(logger, objects);
         register(blockingHandler);

         monitor(maxWait, timeUnit, completeCondition, objects);
         blockingHandler.lock();

         unregister(blockingHandler);
      }
   }

   @Override
   public <T> void monitor(final Function<T, MonitorStatus> completeCondition, final T... objects) {
      monitor(null, null, completeCondition, objects);
   }

   @Override
   public <T> void monitor(final Long maxWait, final TimeUnit timeUnit,
         final Function<T, MonitorStatus> completeCondition, final T... objects) {
      checkNotNull(completeCondition, "completeCondition");
      if (maxWait != null) {
         checkNotNull(timeUnit, "timeUnit");
      }

      if (objects != null && objects.length > 0) {
         for (T object : objects) {
            AsyncMonitor<T> monitor = new AsyncMonitor<T>(object, completeCondition);
            monitor.startMonitoring(maxWait, timeUnit);
         }
      }
   }

   @Override
   public <T extends AbstractEventHandler<?>> void register(final T handler) {
      logger.debug("registering event handler %s", handler);
      eventBus.register(handler);
   }

   @Override
   public <T extends AbstractEventHandler<?>> void unregister(final T handler) {
      logger.debug("unregistering event handler %s", handler);
      eventBus.unregister(handler);
   }

   /*************** Delegating monitors ***************/

   @Override
   public VirtualMachineMonitor getVirtualMachineMonitor() {
      return checkNotNull(context.getUtils().getInjector().getInstance(VirtualMachineMonitor.class),
            "virtualMachineMonitor");
   }

   @Override
   public VirtualApplianceMonitor getVirtualApplianceMonitor() {
      return checkNotNull(context.getUtils().getInjector().getInstance(VirtualApplianceMonitor.class),
            "virtualApplianceMonitor");
   }

   @Override
   public AsyncTaskMonitor getAsyncTaskMonitor() {
      return checkNotNull(context.getUtils().getInjector().getInstance(AsyncTaskMonitor.class), "asyncTaskMonitor");
   }

   @Override
   public ConversionMonitor getConversionMonitor() {
      return checkNotNull(context.getUtils().getInjector().getInstance(ConversionMonitor.class), "conversionMonitor");
   }

   /**
    * Performs the periodical monitoring tasks.
    * 
    * @author Ignasi Barrera
    * @param <T> The type of the object being monitored.
    */
   @VisibleForTesting
   class AsyncMonitor<T> implements Runnable {
      /** The object being monitored. */
      private T monitoredObject;

      /** The function used to monitor the target object. */
      private Function<T, MonitorStatus> completeCondition;

      /**
       * The future representing the monitoring job. Needed to be able to cancel
       * it when monitor finishes.
       */
      private Future<?> future;

      /** The timeout for this monitor. */
      private Long timeout;

      public AsyncMonitor(final T monitoredObject, final Function<T, MonitorStatus> completeCondition) {
         super();
         this.monitoredObject = checkNotNull(monitoredObject, "monitoredObject");
         this.completeCondition = checkNotNull(completeCondition, "completeCondition");
      }

      /**
       * Starts the monitoring job with the given timeout.
       * 
       * @param maxWait The timeout.
       * @param timeUnit The timeunit used in the maxWait parameter.
       */
      public void startMonitoring(final Long maxWait, TimeUnit timeUnit) {
         if (maxWait != null) {
            checkNotNull(timeUnit, "timeUnit must not be null when using timeouts");
         }
         future = scheduler.scheduleWithFixedDelay(this, 0L, pollingDelay, TimeUnit.MILLISECONDS);
         timeout = maxWait == null ? null : System.currentTimeMillis() + timeUnit.toMillis(maxWait);
         logger.debug("started monitor job for %s with %s timeout", monitoredObject,
               timeout == null ? "no" : String.valueOf(timeout));
      }

      /**
       * Stops the monitoring job, if running.
       */
      public void stopMonitoring() {
         logger.debug("stopping monitor job for %s", monitoredObject);

         try {
            if (future != null && !future.isCancelled() && !future.isDone()) {
               // Do not force future cancel. Let it finish gracefully
               logger.debug("cancelling future");
               future.cancel(false);
            }
         } catch (Exception ex) {
            logger.warn(ex, "failed to stop monitor job for %s", monitoredObject);
         }
      }

      /**
       * Checks if the monitor has timed out.
       */
      public boolean isTimeout() {
         return timeout != null && timeout < System.currentTimeMillis();
      }

      @Override
      public void run() {
         // Do not use Thread.interrupted() since it will clear the interrupted
         // flag
         // and subsequent calls to it may not return the appropriate value
         if (Thread.currentThread().isInterrupted()) {
            // If the thread as already been interrupted, just stop monitoring
            // the task and
            // return
            stopMonitoring();
            return;
         }

         MonitorStatus status = completeCondition.apply(monitoredObject);
         logger.debug("monitored object %s status %s", monitoredObject, status.name());

         switch (status) {
            case DONE:
               stopMonitoring();
               logger.debug("publishing COMPLETED event");
               eventBus.post(new CompletedEvent<T>(monitoredObject));
               break;
            case FAILED:
               stopMonitoring();
               logger.debug("publishing FAILED event");
               eventBus.post(new FailedEvent<T>(monitoredObject));
               break;
            case CONTINUE:
            default:
               if (isTimeout()) {
                  logger.warn("monitor for object %s timed out. Shutting down monitor.", monitoredObject);
                  stopMonitoring();
                  logger.debug("publishing TIMEOUT event");
                  eventBus.post(new TimeoutEvent<T>(monitoredObject));
               }
               break;
         }
      }

      public T getMonitoredObject() {
         return monitoredObject;
      }

      public Function<T, MonitorStatus> getCompleteCondition() {
         return completeCondition;
      }

      public Future<?> getFuture() {
         return future;
      }

      public Long getTimeout() {
         return timeout;
      }
   }

}
