/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */
package org.jclouds.concurrent.config;

import static org.jclouds.concurrent.DynamicExecutors.newScalingThreadPool;

import java.io.Closeable;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
 * Note that this uses threads
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

   private final ExecutorService userExecutorFromConstructor;
   private final ExecutorService ioExecutorFromConstructor;

   @Inject
   public ExecutorServiceModule(@Named(Constants.PROPERTY_USER_THREADS) ExecutorService userThreads,
            @Named(Constants.PROPERTY_IO_WORKER_THREADS) ExecutorService ioThreads) {
      this.userExecutorFromConstructor = checkNotGuavaSameThreadExecutor(userThreads);
      this.ioExecutorFromConstructor = checkNotGuavaSameThreadExecutor(ioThreads);
   }

   private ExecutorService checkNotGuavaSameThreadExecutor(ExecutorService executor) {
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
         return shutdownOnClose(userExecutorFromConstructor, closer);
      return shutdownOnClose(newThreadPoolNamed("user thread %d", count), closer);
   }

   @Provides
   @Singleton
   @Named(Constants.PROPERTY_IO_WORKER_THREADS)
   ExecutorService provideIOExecutor(@Named(Constants.PROPERTY_IO_WORKER_THREADS) int count, Closer closer) {
      if (ioExecutorFromConstructor != null)
         return shutdownOnClose(ioExecutorFromConstructor, closer);
      return shutdownOnClose(newThreadPoolNamed("i/o thread %d", count), closer);
   }

   @VisibleForTesting
   static ExecutorService shutdownOnClose(final ExecutorService service, Closer closer) {
      closer.addToClose(new ShutdownExecutorOnClose(service));
      return service;
   }

   @VisibleForTesting
   static ExecutorService newCachedThreadPoolNamed(String name) {
      return Executors.newCachedThreadPool(new ThreadFactoryBuilder().setNameFormat(name).setThreadFactory(
               Executors.defaultThreadFactory()).build());
   }

   @VisibleForTesting
   static ExecutorService newThreadPoolNamed(String name, int maxCount) {
      return maxCount == 0 ? newCachedThreadPoolNamed(name) : newScalingThreadPoolNamed(name, maxCount);
   }

   @VisibleForTesting
   static ExecutorService newScalingThreadPoolNamed(String name, int maxCount) {
      return newScalingThreadPool(1, maxCount, 60L * 1000, new ThreadFactoryBuilder().setNameFormat(name)
               .setThreadFactory(Executors.defaultThreadFactory()).build());
   }

}