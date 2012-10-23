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

package org.jclouds.abiquo.events.handlers;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.jclouds.abiquo.events.monitor.MonitorEvent;
import org.jclouds.logging.Logger;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import com.google.common.eventbus.Subscribe;

/**
 * An event handler that blocks the thread until all monitored objects have been
 * finished being watched.
 * <p>
 * Due to <a
 * href="http://code.google.com/p/guava-libraries/issues/detail?id=783">Guava
 * Issue 786</a> {@link #handle(MonitorEvent)} is marked <code>final</code>to
 * avoid having duplicate events.
 * 
 * @author Ignasi Barrera
 * @param <T>
 *           The monitored object.
 */
public class BlockingEventHandler<T> extends AbstractEventHandler<T> {
   /** The signal used to lock the thread. */
   @VisibleForTesting
   CountDownLatch completeSignal;

   /**
    * The objects being locked.
    * <p>
    * This class handles events in a thread safe way. Otherwise this collections
    * should be synchronised.
    */
   protected List<T> lockedObjects;

   public BlockingEventHandler(final T... lockedObjects) {
      this(Logger.NULL, lockedObjects);
   }

   public BlockingEventHandler(final Logger logger, final T... lockedObjects) {
      super();
      checkArgument(checkNotNull(lockedObjects, "lockedObjects").length > 0, "must provide at least one object");
      this.logger = checkNotNull(logger, "logger");
      this.lockedObjects = Lists.newArrayList(lockedObjects);
      this.logger.debug("created BlockingEventHandler locking %s objects", lockedObjects.length);
   }

   @Override
   protected boolean handles(final MonitorEvent<T> event) {
      logger.debug("checking if %s event on %s must be handled by %s", event.getType(), event.getTarget(), this);
      boolean handles = lockedObjects.contains(event.getTarget());
      logger.debug("%s event on %s must %sbe handled", event.getType(), event.getTarget(), handles ? "" : "not ");
      return handles;
   }

   /**
    * Handles the dispatched event in a thread safe way.
    * <p>
    * Due to <a
    * href="http://code.google.com/p/guava-libraries/issues/detail?id=783">Guava
    * Issue 786</a> {@link #handle(MonitorEvent)} is marked <code>final</code>to
    * avoid having duplicate events.
    * 
    * @see {@link #doBeforeRelease(MonitorEvent)}
    */
   @Subscribe
   public final void handle(final MonitorEvent<T> event) {
      if (handles(event)) {
         logger.debug("handling %s", event);

         try {
            doBeforeRelease(event);
         } finally {
            // Always release the lock, even if the handler code fails
            release(event.getTarget());
         }
      }
   }

   /**
    * Blocks the thread until all locked objects have been released.
    */
   public void lock() {
      // When invoking the lock, it is possible that all events have
      // already been consumed. If there are no objects to monitor,
      // just ignore the lock.
      if (!lockedObjects.isEmpty()) {
         try {
            completeSignal = new CountDownLatch(lockedObjects.size());
            logger.debug("creating lock for %s object(s)", lockedObjects.size());
            completeSignal.await();
         } catch (InterruptedException ex) {
            Throwables.propagate(ex);
         }
      } else {
         logger.debug("there is nothing to watch. Ignoring lock.");
      }
   }

   /**
    * Releases the lock on the given object.
    */
   protected void release(final T target) {
      logger.debug("releasing %s", target);
      lockedObjects.remove(target);

      // The completeSignal might be null if the events have been consumed
      // before acquiring the lock
      if (completeSignal != null) {
         completeSignal.countDown();
         logger.debug("releasing lock for %s. %s remaining objects", target, completeSignal.getCount());
      }
   }

   /**
    * Convenience method to bypass the <a
    * href="http://code.google.com/p/guava-libraries/issues/detail?id=783">Guava
    * Issue 786</a> that forces the subscriber method to be <code>final</code>.
    */
   protected void doBeforeRelease(final MonitorEvent<T> event) {
      // Let subclasses may override it to customize behavior
   }
}
