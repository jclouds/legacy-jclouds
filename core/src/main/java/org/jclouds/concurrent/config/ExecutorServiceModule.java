/**
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
package org.jclouds.concurrent.config;

import static org.jclouds.concurrent.DynamicExecutors.newScalingThreadPool;

import java.io.Closeable;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.Constants;
import org.jclouds.concurrent.MoreExecutors;
import org.jclouds.concurrent.SingleThreaded;
import org.jclouds.lifecycle.Closer;
import org.jclouds.logging.Logger;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;

/**
 * Configures {@link ExecutorService}.
 * 
 * Note that this uses threads.
 *  
 * <p>
 * This extends the underlying Future to expose a description (the task's toString) and the submission context (stack trace).
 * The submission stack trace is appended to relevant stack traces on exceptions that are returned,
 * so the user can see the logical chain of execution (in the executor, and where it was passed to the executor).
 * 
 * @author Adrian Cole
 */
@ConfiguresExecutorService
public class ExecutorServiceModule extends AbstractModule {

   @VisibleForTesting
   static final class ShutdownExecutorOnClose implements Closeable {
      @Resource
      protected Logger logger = Logger.NULL;

      private final ExecutorService service;

      private ShutdownExecutorOnClose(ExecutorService service) {
         this.service = service;
      }

      @Override
      public void close() throws IOException {
         List<Runnable> runnables = service.shutdownNow();
         if (runnables.size() > 0)
            logger.warn("when shutting down executor %s, runnables outstanding: %s", service, runnables);
      }
   }

   @VisibleForTesting
   final ExecutorService userExecutorFromConstructor;
   @VisibleForTesting
   final ExecutorService ioExecutorFromConstructor;

   @Inject
   public ExecutorServiceModule(@Named(Constants.PROPERTY_USER_THREADS) ExecutorService userThreads,
            @Named(Constants.PROPERTY_IO_WORKER_THREADS) ExecutorService ioThreads) {
      this.userExecutorFromConstructor = addToStringOnSubmit(checkNotGuavaSameThreadExecutor(userThreads));
      this.ioExecutorFromConstructor = addToStringOnSubmit(checkNotGuavaSameThreadExecutor(ioThreads));
   }

   ExecutorService addToStringOnSubmit(ExecutorService executor) {
      if (executor != null) {
         return new DescribingExecutorService(executor);
      }
      return executor;
   }

   ExecutorService checkNotGuavaSameThreadExecutor(ExecutorService executor) {
      // we detect behavior based on the class
      if (executor != null && !(executor.getClass().isAnnotationPresent(SingleThreaded.class))
               && executor.getClass().getSimpleName().indexOf("SameThread") != -1) {
         Logger.CONSOLE.warn(
                  "please switch from %s to %s or annotate your same threaded executor with @SingleThreaded", executor
                           .getClass().getName(), MoreExecutors.SameThreadExecutorService.class.getName());
         return MoreExecutors.sameThreadExecutor();
      }
      return executor;
   }

   public ExecutorServiceModule() {
      this(null, null);
   }

   @Override
   protected void configure() {
   }

   @Provides
   @Singleton
   @Named(Constants.PROPERTY_USER_THREADS)
   ExecutorService provideExecutorService(@Named(Constants.PROPERTY_USER_THREADS) int count, Closer closer) {
      if (userExecutorFromConstructor != null)
         return userExecutorFromConstructor;
      return shutdownOnClose(addToStringOnSubmit(newThreadPoolNamed("user thread %d", count)), closer);
   }

   @Provides
   @Singleton
   @Named(Constants.PROPERTY_IO_WORKER_THREADS)
   ExecutorService provideIOExecutor(@Named(Constants.PROPERTY_IO_WORKER_THREADS) int count, Closer closer) {
      if (ioExecutorFromConstructor != null)
         return ioExecutorFromConstructor;
      return shutdownOnClose(addToStringOnSubmit(newThreadPoolNamed("i/o thread %d", count)), closer);
   }

   @VisibleForTesting
   static <T extends ExecutorService> T shutdownOnClose(final T service, Closer closer) {
      closer.addToClose(new ShutdownExecutorOnClose(service));
      return service;
   }

   @VisibleForTesting
   ExecutorService newCachedThreadPoolNamed(String name) {
      return Executors.newCachedThreadPool(namedThreadFactory(name));
   }

   @VisibleForTesting
   ExecutorService newThreadPoolNamed(String name, int maxCount) {
      return maxCount == 0 ? newCachedThreadPoolNamed(name) : newScalingThreadPoolNamed(name, maxCount);
   }

   @VisibleForTesting
   ExecutorService newScalingThreadPoolNamed(String name, int maxCount) {
      return newScalingThreadPool(1, maxCount, 60L * 1000, namedThreadFactory(name));
   }

   protected ThreadFactory namedThreadFactory(String name) {
      return new ThreadFactoryBuilder().setNameFormat(name).setThreadFactory(Executors.defaultThreadFactory()).build();
   }


   /** returns the stack trace at the caller */
   static StackTraceElement[] getStackTraceHere() {
      // remove the first two items in the stack trace (because the first one refers to the call to 
      // Thread.getStackTrace, and the second one is us)
      StackTraceElement[] fullSubmissionTrace = Thread.currentThread().getStackTrace();
      StackTraceElement[] cleanedSubmissionTrace = new StackTraceElement[fullSubmissionTrace.length-2];
      System.arraycopy(fullSubmissionTrace, 2, cleanedSubmissionTrace, 0, cleanedSubmissionTrace.length);
      return cleanedSubmissionTrace;
   }
   
}
