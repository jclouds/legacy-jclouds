/**
 *
 * Copyright (C) 2009 Global Cloud Specialists, Inc. <info@globalcloudspecialists.com>
 *
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
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
 * ====================================================================
 */
package org.jclouds.command.pool;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

import org.jclouds.command.FutureCommand;
import org.jclouds.lifecycle.BaseLifeCycle;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.name.Named;

/**
 * // TODO: Adrian: Document this!
 * 
 * @author Adrian Cole
 */
public abstract class FutureCommandConnectionPool<E, C, O extends FutureCommand<E, ?, ?, ?>>
         extends BaseLifeCycle {

   protected final Semaphore allConnections;
   protected final BlockingQueue<C> available;

   /**
    * inputOnly: nothing is taken from this queue.
    */
   protected final BlockingQueue<O> resubmitQueue;
   protected final int maxConnectionReuse;
   protected final AtomicInteger currentSessionFailures = new AtomicInteger(0);
   protected volatile boolean hitBottom = false;
   protected final E endPoint;

   public E getEndPoint() {
      return endPoint;
   }

   public static interface Factory<E, C, O extends FutureCommand<E, ?, ?, ?>> {
      FutureCommandConnectionPool<E, C, O> create(E endPoint);
   }

   public FutureCommandConnectionPool(ExecutorService executor, Semaphore allConnections,
            BlockingQueue<O> commandQueue, @Named("maxConnectionReuse") int maxConnectionReuse,
            BlockingQueue<C> available, @Assisted E endPoint, BaseLifeCycle... dependencies) {
      super(executor, dependencies);
      this.allConnections = allConnections;
      this.resubmitQueue = commandQueue;
      this.maxConnectionReuse = maxConnectionReuse;
      this.available = available;
      this.endPoint = endPoint;
   }

   protected void setResponseException(Exception ex, C conn) {
      O command = getHandleFromConnection(conn).getCommand();
      command.getResponseFuture().setException(ex);
   }

   protected void cancel(C conn) {
      O command = getHandleFromConnection(conn).getCommand();
      command.cancel(true);
   }

   protected C getConnection() throws InterruptedException, TimeoutException {
      exceptionIfNotActive();
      if (!hitBottom) {
         hitBottom = available.size() == 0 && allConnections.availablePermits() == 0;
         if (hitBottom)
            logger.warn("%1$s - saturated connection pool", this);
      }
      logger.debug("%s - attempting to acquire connection; %s currently available", this, available
               .size());
      C conn = available.poll(5, TimeUnit.SECONDS);
      if (conn == null)
         throw new TimeoutException("could not obtain a pooled connection within 5 seconds");

      logger.trace("%1$s - %2$d - aquired", conn, conn.hashCode());
      if (connectionValid(conn)) {
         logger.debug("%1$s - %2$d - reusing", conn, conn.hashCode());
         return conn;
      } else {
         logger.debug("%1$s - %2$d - unusable", conn, conn.hashCode());
         shutdownConnection(conn);
         allConnections.release();
         return getConnection();
      }
   }

   protected void fatalException(Exception ex, C conn) {
      setResponseException(ex, conn);
      exception.set(ex);
      shutdown();
   }

   protected abstract void shutdownConnection(C conn);

   protected abstract boolean connectionValid(C conn);

   public FutureCommandConnectionHandle<E, C, O> getHandle(O command) throws InterruptedException,
            TimeoutException {
      exceptionIfNotActive();
      C conn = getConnection();
      FutureCommandConnectionHandle<E, C, O> handle = createHandle(command, conn);
      associateHandleWithConnection(handle, conn);
      return handle;
   }

   protected abstract FutureCommandConnectionHandle<E, C, O> createHandle(O command, C conn);

   protected void resubmitIfRequestIsReplayable(C connection, Exception e) {
      O command = getCommandFromConnection(connection);
      if (command != null) {
         if (isReplayable(command)) {
            logger.info("resubmitting command: %1$s", command);
            resubmitQueue.add(command);
         } else {
            command.setException(e);
         }
      }
   }

   protected abstract boolean isReplayable(O command);

   O getCommandFromConnection(C connection) {
      FutureCommandConnectionHandle<E, C, O> handle = getHandleFromConnection(connection);
      if (handle != null && handle.getCommand() != null) {
         return handle.getCommand();
      }
      return null;
   }

   protected void setExceptionOnCommand(C connection, Exception e) {
      FutureCommand<E, ?, ?, ?> command = getCommandFromConnection(connection);
      if (command != null) {
         logger.warn(e, "exception in command: %1$s", command);
         command.setException(e);
      }
   }

   protected abstract void associateHandleWithConnection(
            FutureCommandConnectionHandle<E, C, O> handle, C connection);

   protected abstract FutureCommandConnectionHandle<E, C, O> getHandleFromConnection(C connection);

   protected abstract void createNewConnection() throws InterruptedException;

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((endPoint == null) ? 0 : endPoint.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      FutureCommandConnectionPool<?, ?, ?> other = (FutureCommandConnectionPool<?, ?, ?>) obj;
      if (endPoint == null) {
         if (other.endPoint != null)
            return false;
      } else if (!endPoint.equals(other.endPoint))
         return false;
      return true;
   }

}
