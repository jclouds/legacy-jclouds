/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.lifecycle;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;

import org.jclouds.logging.Logger;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.Atomics;
import com.google.common.util.concurrent.ListeningExecutorService;

/**
 * // TODO: Adrian: Document this!
 * 
 * @author Adrian Cole
 */
public abstract class BaseLifeCycle implements Runnable, LifeCycle {
   @Resource
   protected Logger logger = Logger.NULL;
   
   protected final ListeningExecutorService userExecutor;
   protected final List<LifeCycle> dependencies;
   protected final Object statusLock;
   protected volatile Status status;
   protected AtomicReference<Exception> exception = Atomics.newReference();

   public BaseLifeCycle(ListeningExecutorService userExecutor, LifeCycle... dependencies) {
      this.userExecutor = userExecutor;
      this.dependencies = Lists.newArrayList();
      this.dependencies.addAll(Arrays.asList(dependencies));
      this.statusLock = new Object();
      this.status = Status.INACTIVE;
   }

   public void addDependency(LifeCycle lifeCycle) {
      dependencies.add(lifeCycle);
   }

   public Status getStatus() {
      return status;
   }

   public void run() {
      try {
         while (shouldDoWork()) {
            doWork();
         }
      } catch (Exception e) {
         logger.error(e, "Exception doing work");
         exception.set(e);
      }
      this.status = Status.SHUTTING_DOWN;
      doShutdown();
      this.status = Status.SHUT_DOWN;
      logger.info("Shutdown %s", this);
   }

   protected abstract void doWork() throws Exception;

   protected abstract void doShutdown();

   /**
    * @return false if any dependencies are inactive, or we are inactive, or we have a global
    *         exception.
    */
   protected boolean shouldDoWork() {
      try {
         exceptionIfDependenciesNotActive();
      } catch (IllegalStateException e) {
         return false;
      }
      return status.equals(Status.ACTIVE) && exception.get() == null;
   }

   @PostConstruct
   public void start() {
      logger.info("Starting %s", this);
      synchronized (this.statusLock) {
         if (this.status.compareTo(Status.SHUTDOWN_REQUEST) >= 0) {
            doShutdown();
            this.status = Status.SHUT_DOWN;
            this.statusLock.notifyAll();
            return;
         }
         if (this.status.compareTo(Status.ACTIVE) == 0) {
            this.statusLock.notifyAll();
            return;
         }

         if (this.status.compareTo(Status.INACTIVE) != 0) {
            throw new IllegalStateException("Illegal state: " + this.status);
         }

         exceptionIfDependenciesNotActive();

         this.status = Status.ACTIVE;
      }
      userExecutor.execute(this);
   }

   protected void exceptionIfDependenciesNotActive() {
      for (LifeCycle dependency : dependencies) {
         if (dependency.getStatus().compareTo(Status.ACTIVE) != 0) {
            throw new IllegalStateException(String.format("Illegal state: %s for component: %s",
                     dependency.getStatus(), dependency));
         }
      }
   }

   protected Exception getExceptionFromDependenciesOrNull() {
      for (LifeCycle dependency : dependencies) {
         if (dependency.getException() != null) {
            return dependency.getException();
         }
      }
      return null;
   }

   public Exception getException() {
      return this.exception.get();
   }

   protected void awaitShutdown(long timeout) throws InterruptedException {
      awaitStatus(Status.SHUT_DOWN, timeout);
   }

   protected void awaitStatus(Status intended, long timeout) throws InterruptedException {
      synchronized (this.statusLock) {
         long deadline = System.currentTimeMillis() + timeout;
         long remaining = timeout;
         while (this.status != intended) {
            this.statusLock.wait(remaining);
            if (timeout > 0) {
               remaining = deadline - System.currentTimeMillis();
               if (remaining <= 0) {
                  break;
               }
            }
         }
      }
   }

   @PreDestroy
   public void shutdown() {
      shutdown(2000);
   }

   public void shutdown(long waitMs) {
      synchronized (this.statusLock) {
         if (this.status.compareTo(Status.ACTIVE) > 0) {
            return;
         }
         this.status = Status.SHUTDOWN_REQUEST;
         try {
            awaitShutdown(waitMs);
         } catch (InterruptedException ignore) {
         }
      }
   }

   protected void exceptionIfNotActive() {
      if (!status.equals(Status.ACTIVE))
         throw new IllegalStateException(String.format("not active: %s", this));
   }

}
