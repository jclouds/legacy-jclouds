/**
 *
 * Copyright (C) 2011 Cloud Conscious, LLC. <info@cloudconscious.com>
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
import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.concurrent.DynamicExecutors.newScalingThreadPool;

import java.io.Closeable;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

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

   @VisibleForTesting
   final ExecutorService userExecutorFromConstructor;
   @VisibleForTesting
   final ExecutorService ioExecutorFromConstructor;

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

   static class AddToStringOnSubmitExecutorService implements ExecutorService {

      private final ExecutorService delegate;

      public AddToStringOnSubmitExecutorService(ExecutorService delegate) {
         this.delegate = checkNotNull(delegate, "delegate");
      }

      @Override
      public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
         return delegate.awaitTermination(timeout, unit);
      }

      @Override
      public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException {
         return delegate.invokeAll(tasks);
      }

      @Override
      public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit)
               throws InterruptedException {
         return delegate.invokeAll(tasks, timeout, unit);
      }

      @Override
      public <T> T invokeAny(Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException {
         return delegate.invokeAny(tasks);
      }

      @Override
      public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit)
               throws InterruptedException, ExecutionException, TimeoutException {
         return delegate.invokeAny(tasks, timeout, unit);
      }

      @Override
      public boolean isShutdown() {
         return delegate.isShutdown();
      }

      @Override
      public boolean isTerminated() {
         return delegate.isTerminated();
      }

      @Override
      public void shutdown() {
         delegate.shutdown();
      }

      @Override
      public List<Runnable> shutdownNow() {
         return delegate.shutdownNow();
      }

      @Override
      public <T> Future<T> submit(Callable<T> task) {
         return new AddToStringFuture<T>(delegate.submit(task), task.toString());
      }

      @SuppressWarnings({ "unchecked", "rawtypes" })
      @Override
      public Future<?> submit(Runnable task) {
         return new AddToStringFuture(delegate.submit(task), task.toString());
      }

      @Override
      public <T> Future<T> submit(Runnable task, T result) {
         return new AddToStringFuture<T>(delegate.submit(task, result), task.toString());
      }

      @Override
      public void execute(Runnable arg0) {
         delegate.execute(arg0);
      }

      @Override
      public boolean equals(Object obj) {
         return delegate.equals(obj);
      }

      @Override
      public int hashCode() {
         return delegate.hashCode();
      }

      @Override
      public String toString() {
         return delegate.toString();
      }

   }

   static class AddToStringFuture<T> implements Future<T> {
      private final Future<T> delegate;
      private final String toString;

      public AddToStringFuture(Future<T> delegate, String toString) {
         this.delegate = delegate;
         this.toString = toString;
      }

      @Override
      public boolean cancel(boolean arg0) {
         return delegate.cancel(arg0);
      }

      @Override
      public T get() throws InterruptedException, ExecutionException {
         return delegate.get();
      }

      @Override
      public T get(long arg0, TimeUnit arg1) throws InterruptedException, ExecutionException, TimeoutException {
         return delegate.get(arg0, arg1);
      }

      @Override
      public boolean isCancelled() {
         return delegate.isCancelled();
      }

      @Override
      public boolean isDone() {
         return delegate.isDone();
      }

      @Override
      public boolean equals(Object obj) {
         return delegate.equals(obj);
      }

      @Override
      public int hashCode() {
         return delegate.hashCode();
      }

      @Override
      public String toString() {
         return toString;
      }

   }

   @Provides
   @Singleton
   @Named(Constants.PROPERTY_USER_THREADS)
   ExecutorService provideExecutorService(@Named(Constants.PROPERTY_USER_THREADS) int count, Closer closer) {
      if (userExecutorFromConstructor != null)
         return userExecutorFromConstructor;
      return shutdownOnClose(newThreadPoolNamed("user thread %d", count), closer);
   }

   @Provides
   @Singleton
   @Named(Constants.PROPERTY_IO_WORKER_THREADS)
   ExecutorService provideIOExecutor(@Named(Constants.PROPERTY_IO_WORKER_THREADS) int count, Closer closer) {
      if (ioExecutorFromConstructor != null)
         return ioExecutorFromConstructor;
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