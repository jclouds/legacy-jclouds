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
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.jclouds.command.FutureCommand;
import org.jclouds.command.FutureCommandClient;
import org.jclouds.lifecycle.BaseLifeCycle;
import org.jclouds.util.Utils;

import com.google.inject.Inject;

/**
 * // TODO: Adrian: Document this!
 * 
 * @author Adrian Cole
 */
public class FutureCommandConnectionPoolClient<C, O extends FutureCommand<?, ?, ?>> extends
         BaseLifeCycle implements FutureCommandClient<O> {
   private final FutureCommandConnectionPool<C, O> futureCommandConnectionPool;
   private final BlockingQueue<O> commandQueue;

   @Inject
   public FutureCommandConnectionPoolClient(ExecutorService executor,
            FutureCommandConnectionPool<C, O> futureCommandConnectionPool,
            BlockingQueue<O> commandQueue) {
      super(executor, futureCommandConnectionPool);
      this.futureCommandConnectionPool = futureCommandConnectionPool;
      this.commandQueue = commandQueue;
   }

   /**
    * {@inheritDoc}
    * <p/>
    * we continue while the connection pool is active
    */
   @Override
   protected boolean shouldDoWork() {
      return super.shouldDoWork() && futureCommandConnectionPool.getStatus().equals(Status.ACTIVE);
   }

   /**
    * {@inheritDoc}
    * 
    * If the reason we are shutting down is due an exception, we set that exception on all pending
    * commands. Otherwise, we cancel the pending commands.
    */
   @Override
   protected void doShutdown() {
      exception.compareAndSet(null, futureCommandConnectionPool.getException());
      while (!commandQueue.isEmpty()) {
         FutureCommand<?, ?, ?> command = (FutureCommand<?, ?, ?>) commandQueue.remove();
         if (command != null) {
            if (exception.get() != null)
               command.setException(exception.get());
            else
               command.cancel(true);
         }
      }
   }

   @Override
   protected void doWork() throws InterruptedException {
      takeACommandOffTheQueueAndInvokeIt();
   }

   private void takeACommandOffTheQueueAndInvokeIt() throws InterruptedException {
      O command = commandQueue.poll(1, TimeUnit.SECONDS);
      if (command != null) {
         try {
            invoke(command);
         } catch (Exception e) {
            Utils.<InterruptedException> rethrowIfRuntimeOrSameType(e);
            logger.error(e, "Error processing command %s", command);
         }
      }
   }

   /**
    * This is an asynchronous operation that puts the <code>command</code> onto a queue. Later, it
    * will be processed via the {@link #invoke(FutureCommand) invoke} method.
    */
   public void submit(O command) {
      exceptionIfNotActive();
      commandQueue.add(command);
   }

   /**
    * Invoke binds a command with a connection from the pool. This binding is called a
    * {@link FutureCommandConnectionHandle handle}. The handle will keep this binding until the
    * command's response is parsed or an exception is set on the Command object.
    * 
    * @param command
    */
   protected void invoke(O command) {
      exceptionIfNotActive();
      FutureCommandConnectionHandle<C, O> connectionHandle = null;
      try {
         connectionHandle = futureCommandConnectionPool.getHandle(command);
      } catch (InterruptedException e) {
         logger.warn(e, "Interrupted getting a connection for command %1$s; retrying", command);
         commandQueue.add(command);
         return;
      } catch (TimeoutException e) {
         logger.warn(e, "Timeout getting a connection for command %1$s; retrying", command);
         commandQueue.add(command);
         return;
      }

      if (connectionHandle == null) {
         logger.error("Failed to obtain connection for command %1$s; retrying", command);
         commandQueue.add(command);
         return;
      }
      connectionHandle.startConnection();
   }

   @Override
   public String toString() {
      final StringBuilder sb = new StringBuilder();
      sb.append("FutureCommandConnectionPoolClient");
      sb.append("{status=").append(status);
      sb.append(", commandQueue=").append((commandQueue != null) ? commandQueue.size() : 0);
      sb.append(", futureCommandConnectionPool=").append(futureCommandConnectionPool);
      sb.append('}');
      return sb.toString();
   }

}
