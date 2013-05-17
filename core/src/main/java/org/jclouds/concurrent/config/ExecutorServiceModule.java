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
package org.jclouds.concurrent.config;

import static com.google.common.util.concurrent.MoreExecutors.listeningDecorator;
import static org.jclouds.Constants.PROPERTY_IO_WORKER_THREADS;
import static org.jclouds.Constants.PROPERTY_USER_THREADS;
import static org.jclouds.concurrent.DynamicExecutors.newScalingThreadPool;

import java.io.Closeable;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import javax.annotation.Resource;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.lifecycle.Closer;
import org.jclouds.logging.Logger;

import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.SimpleTimeLimiter;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.common.util.concurrent.TimeLimiter;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;

/**
 * Configures {@link ListeningExecutorService}.
 * 
 * Note that this uses threads.
 * 
 * <p>
 * This extends the underlying Future to expose a description (the task's toString) and the submission context (stack
 * trace). The submission stack trace is appended to relevant stack traces on exceptions that are returned, so the user
 * can see the logical chain of execution (in the executor, and where it was passed to the executor).
 * 
 * @author Adrian Cole
 */
@ConfiguresExecutorService
public class ExecutorServiceModule extends AbstractModule {

   static final class ShutdownExecutorOnClose implements Closeable {
      @Resource
      private Logger logger = Logger.NULL;

      private final ListeningExecutorService service;

      private ShutdownExecutorOnClose(ListeningExecutorService service) {
         this.service = service;
      }

      @Override
      public void close() throws IOException {
         List<Runnable> runnables = service.shutdownNow();
         if (runnables.size() > 0)
            logger.warn("when shutting down executor %s, runnables outstanding: %s", service, runnables);
      }
   }

   final ListeningExecutorService userExecutorFromConstructor;
   final ListeningExecutorService ioExecutorFromConstructor;

   public ExecutorServiceModule() {
      this.userExecutorFromConstructor = null;
      this.ioExecutorFromConstructor = null;
   }
   
   public ExecutorServiceModule(@Named(PROPERTY_USER_THREADS) ExecutorService userExecutor,
         @Named(PROPERTY_IO_WORKER_THREADS) ExecutorService ioExecutor) {
      this(listeningDecorator(userExecutor), listeningDecorator(ioExecutor));
   }
   
   public ExecutorServiceModule(@Named(PROPERTY_USER_THREADS) ListeningExecutorService userExecutor,
         @Named(PROPERTY_IO_WORKER_THREADS) ListeningExecutorService ioExecutor) {
      this.userExecutorFromConstructor = WithSubmissionTrace.wrap(userExecutor);
      this.ioExecutorFromConstructor = WithSubmissionTrace.wrap(ioExecutor);
   }

   @Override
   protected void configure() { // NO_UCD
   }

   @Provides
   @Singleton
   TimeLimiter timeLimiter(@Named(PROPERTY_USER_THREADS) ListeningExecutorService userExecutor){
      return new SimpleTimeLimiter(userExecutor);
   }

   @Provides
   @Singleton
   @Named(PROPERTY_USER_THREADS)
   ListeningExecutorService provideListeningUserExecutorService(@Named(PROPERTY_USER_THREADS) int count, Closer closer) { // NO_UCD
      if (userExecutorFromConstructor != null)
         return userExecutorFromConstructor;
      return shutdownOnClose(WithSubmissionTrace.wrap(newThreadPoolNamed("user thread %d", count)), closer);
   }

   @Provides
   @Singleton
   @Named(PROPERTY_IO_WORKER_THREADS)
   ListeningExecutorService provideListeningIOExecutorService(@Named(PROPERTY_IO_WORKER_THREADS) int count,
         Closer closer) { // NO_UCD
      if (ioExecutorFromConstructor != null)
         return ioExecutorFromConstructor;
      return shutdownOnClose(WithSubmissionTrace.wrap(newThreadPoolNamed("i/o thread %d", count)), closer);
   }

   @Provides
   @Singleton
   @Named(PROPERTY_USER_THREADS)
   ExecutorService provideUserExecutorService(@Named(PROPERTY_USER_THREADS) ListeningExecutorService in) { // NO_UCD
      return in;
   }

   @Provides
   @Singleton
   @Named(PROPERTY_IO_WORKER_THREADS)
   ExecutorService provideIOExecutorService(@Named(PROPERTY_IO_WORKER_THREADS) ListeningExecutorService in) { // NO_UCD
      return in;
   }

   static <T extends ListeningExecutorService> T shutdownOnClose(final T service, Closer closer) {
      closer.addToClose(new ShutdownExecutorOnClose(service));
      return service;
   }

   private ListeningExecutorService newCachedThreadPoolNamed(String name) {
      return listeningDecorator(Executors.newCachedThreadPool(namedThreadFactory(name)));
   }

   private ListeningExecutorService newThreadPoolNamed(String name, int maxCount) {
      return maxCount == 0 ? newCachedThreadPoolNamed(name) : newScalingThreadPoolNamed(name, maxCount);
   }

   private ListeningExecutorService newScalingThreadPoolNamed(String name, int maxCount) {
      return listeningDecorator(newScalingThreadPool(1, maxCount, 60L * 1000, namedThreadFactory(name)));
   }

   private ThreadFactory namedThreadFactory(String name) {
      return new ThreadFactoryBuilder().setNameFormat(name).setThreadFactory(Executors.defaultThreadFactory()).build();
   }

}
