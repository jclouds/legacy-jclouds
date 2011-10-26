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

   static ExecutorService addToStringOnSubmit(ExecutorService executor) {
      if (executor != null) {
         return new DescribingExecutorService(executor);
      }
      return executor;
   }

   static ExecutorService checkNotGuavaSameThreadExecutor(ExecutorService executor) {
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

   static class DescribingExecutorService implements ExecutorService {

      private final ExecutorService delegate;

      public DescribingExecutorService(ExecutorService delegate) {
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
         return new DescribedFuture<T>(delegate.submit(task), task.toString(), getStackTraceHere());
      }

      @SuppressWarnings({ "unchecked", "rawtypes" })
      @Override
      public Future<?> submit(Runnable task) {
         return new DescribedFuture(delegate.submit(task), task.toString(), getStackTraceHere());
      }

      @Override
      public <T> Future<T> submit(Runnable task, T result) {
         return new DescribedFuture<T>(delegate.submit(task, result), task.toString(), getStackTraceHere());
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

   static class DescribedFuture<T> implements Future<T> {
      private final Future<T> delegate;
      private final String description;
      private StackTraceElement[] submissionTrace;

      public DescribedFuture(Future<T> delegate, String description, StackTraceElement[] submissionTrace) {
         this.delegate = delegate;
         this.description = description;
         this.submissionTrace = submissionTrace;
      }

      @Override
      public boolean cancel(boolean arg0) {
         return delegate.cancel(arg0);
      }

      @Override
      public T get() throws InterruptedException, ExecutionException {
         try {
            return delegate.get();
         } catch (ExecutionException e) {
            throw ensureCauseHasSubmissionTrace(e);
         } catch (InterruptedException e) {
            throw ensureCauseHasSubmissionTrace(e);
         }
      }

      @Override
      public T get(long arg0, TimeUnit arg1) throws InterruptedException, ExecutionException, TimeoutException {
         try {
            return delegate.get(arg0, arg1);
         } catch (ExecutionException e) {
            throw ensureCauseHasSubmissionTrace(e);
         } catch (InterruptedException e) {
            throw ensureCauseHasSubmissionTrace(e);
         } catch (TimeoutException e) {
            throw ensureCauseHasSubmissionTrace(e);
         }
      }

      /** This method does the work to ensure _if_ a submission stack trace was provided,
       * it is included in the exception.  most errors are thrown from the frame of the
       * Future.get call, with a cause that took place in the executor's thread.
       * We extend the stack trace of that cause with the submission stack trace.
       * (An alternative would be to put the stack trace as a root cause,
       * at the bottom of the stack, or appended to all traces, or inserted
       * after the second cause, etc ... but since we can't change the "Caused by:"
       * method in Throwable the compromise made here seems best.)
       */
      private <ET extends Exception> ET ensureCauseHasSubmissionTrace(ET e) {
         if (submissionTrace==null) return e;
         if (e.getCause()==null) {
            ExecutionException ee = new ExecutionException("task submitted from the following trace", null);
            e.initCause(ee);
            return e;
         }
         Throwable cause = e.getCause();
         StackTraceElement[] causeTrace = cause.getStackTrace();
         boolean causeIncludesSubmissionTrace = submissionTrace.length >= causeTrace.length;
         for (int i=0; causeIncludesSubmissionTrace && i<submissionTrace.length; i++) {
            if (!causeTrace[causeTrace.length-1-i].equals(submissionTrace[submissionTrace.length-1-i])) {
               causeIncludesSubmissionTrace = false;
            }
         }
         
         if (!causeIncludesSubmissionTrace) {
            cause.setStackTrace(merge(causeTrace, submissionTrace));
         }
         
         return e;
      }

      private StackTraceElement[] merge(StackTraceElement[] t1, StackTraceElement[] t2) {
         StackTraceElement[] t12 = new StackTraceElement[t1.length + t2.length];
         System.arraycopy(t1, 0, t12, 0, t1.length);
         System.arraycopy(t2, 0, t12, t1.length, t2.length);
         return t12;
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
         return description;
      }

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