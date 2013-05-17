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

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.toArray;
import static com.google.common.collect.ObjectArrays.concat;

import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.Delayed;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableSet;
import com.google.common.util.concurrent.ForwardingFuture;
import com.google.common.util.concurrent.ForwardingListenableFuture;
import com.google.common.util.concurrent.ForwardingListeningExecutorService;

/**
 * 
 * @author Adrian Cole
 */
public class WithSubmissionTrace {

   private WithSubmissionTrace() {
   }

   public static ListeningExecutorService wrap(com.google.common.util.concurrent.ListeningExecutorService delegate) {
      return new ListeningExecutorService(delegate);
   }

   private static class ListeningExecutorService extends ForwardingListeningExecutorService {

      private final com.google.common.util.concurrent.ListeningExecutorService delegate;

      private ListeningExecutorService(com.google.common.util.concurrent.ListeningExecutorService delegate) {
         this.delegate = checkNotNull(delegate, "delegate");
      }

      @Override
      protected com.google.common.util.concurrent.ListeningExecutorService delegate() {
         return delegate;
      }

      @Override
      public <T> com.google.common.util.concurrent.ListenableFuture<T> submit(Callable<T> task) {
         return new ListenableFuture<T>(delegate().submit(task));
      }

      @SuppressWarnings({ "unchecked", "rawtypes" })
      @Override
      public com.google.common.util.concurrent.ListenableFuture<?> submit(Runnable task) {
         return new ListenableFuture(delegate().submit(task));
      }

      @Override
      public <T> com.google.common.util.concurrent.ListenableFuture<T> submit(Runnable task, T result) {
         return new ListenableFuture<T>(delegate().submit(task, result));
      }
   }

   private static class ListenableFuture<T> extends ForwardingListenableFuture<T> {
      private final com.google.common.util.concurrent.ListenableFuture<T> delegate;
      private final StackTraceElement[] submissionTrace;

      ListenableFuture(com.google.common.util.concurrent.ListenableFuture<T> delegate) {
         this.delegate = checkNotNull(delegate, "delegate");
         this.submissionTrace = getStackTraceHere();
      }

      @Override
      protected com.google.common.util.concurrent.ListenableFuture<T> delegate() {
         return delegate;
      }

      @Override
      public T get() throws InterruptedException, ExecutionException {
         try {
            return delegate().get();
         } catch (ExecutionException e) {
            throw addSubmissionTrace(submissionTrace, e);
         }
      }

      @Override
      public T get(long arg0, TimeUnit arg1) throws InterruptedException, ExecutionException, TimeoutException {
         try {
            return delegate().get(arg0, arg1);
         } catch (ExecutionException e) {
            throw addSubmissionTrace(submissionTrace, e);
         }
      }
   }

   private static final Set<String> stackTracesToTrim = ImmutableSet.of(WithSubmissionTrace.class.getName(),
         ListeningExecutorService.class.getName(), ListenableFuture.class.getName(),
         ListeningScheduledExecutorService.class.getName(), ScheduledFuture.class.getName());

   /** returns the stack trace at the caller */
   private static StackTraceElement[] getStackTraceHere() {
      StackTraceElement[] trace = Thread.currentThread().getStackTrace();
      return filterTrace(trace);
   }

   private static StackTraceElement[] filterTrace(StackTraceElement[] trace) {
      return toArray(filter(Arrays.asList(trace), new Predicate<StackTraceElement>() {
         public boolean apply(StackTraceElement input) {
            String className = input.getClassName();
            return !stackTracesToTrim.contains(className);
         }
      }), StackTraceElement.class);
   }

   private static ExecutionException addSubmissionTrace(StackTraceElement[] submissionTrace, ExecutionException e) {
      if (e.getCause() == null) {
         return filterTrace(e);
      }
      Throwable cause = e.getCause();
      StackTraceElement[] combined = filterTrace(concat(cause.getStackTrace(), submissionTrace, StackTraceElement.class));
      cause.setStackTrace(combined);
      return filterTrace(e);
   }

   private static ExecutionException filterTrace(ExecutionException e) {
      StackTraceElement[] withoutHere = filterTrace(e.getStackTrace());
      e.setStackTrace(withoutHere);
      return e;
   }

   public static ListeningScheduledExecutorService wrap(
         com.google.common.util.concurrent.ListeningScheduledExecutorService delegate) {
      return new ListeningScheduledExecutorService(delegate);
   }

   private static class ListeningScheduledExecutorService extends ListeningExecutorService implements
         com.google.common.util.concurrent.ListeningScheduledExecutorService {

      private ListeningScheduledExecutorService(
            com.google.common.util.concurrent.ListeningScheduledExecutorService delegate) {
         super(delegate);
      }

      @Override
      protected com.google.common.util.concurrent.ListeningScheduledExecutorService delegate() {
         return com.google.common.util.concurrent.ListeningScheduledExecutorService.class.cast(super.delegate());
      }

      @Override
      public <T> ListenableFuture<T> submit(Callable<T> task) {
         return new ListenableFuture<T>(delegate().submit(task));
      }

      @SuppressWarnings({ "unchecked", "rawtypes" })
      @Override
      public ListenableFuture<?> submit(Runnable task) {
         return new ListenableFuture(delegate().submit(task));
      }

      @Override
      public <T> ListenableFuture<T> submit(Runnable task, T result) {
         return new ListenableFuture<T>(delegate().submit(task, result));
      }

      @SuppressWarnings({ "rawtypes", "unchecked" })
      @Override
      public ScheduledFuture<?> schedule(Runnable command, long delay, TimeUnit unit) {
         return new ScheduledFuture(delegate().schedule(command, delay, unit));
      }

      @Override
      public <V> ScheduledFuture<V> schedule(Callable<V> callable, long delay, TimeUnit unit) {
         return new ScheduledFuture<V>(delegate().schedule(callable, delay, unit));
      }

      @SuppressWarnings({ "rawtypes", "unchecked" })
      @Override
      public ScheduledFuture<?> scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit) {
         return new ScheduledFuture(delegate().scheduleAtFixedRate(command, initialDelay, period, unit));
      }

      @SuppressWarnings({ "rawtypes", "unchecked" })
      @Override
      public ScheduledFuture<?> scheduleWithFixedDelay(Runnable command, long initialDelay, long delay, TimeUnit unit) {
         return new ScheduledFuture(delegate().scheduleWithFixedDelay(command, initialDelay, delay, unit));
      }

   }

   private static class ScheduledFuture<T> extends ForwardingFuture<T> implements
         java.util.concurrent.ScheduledFuture<T> {

      private final java.util.concurrent.ScheduledFuture<T> delegate;
      private final StackTraceElement[] submissionTrace;

      private ScheduledFuture(java.util.concurrent.ScheduledFuture<T> delegate) {
         this.delegate = checkNotNull(delegate, "delegate");
         this.submissionTrace = getStackTraceHere();
      }

      @Override
      protected java.util.concurrent.ScheduledFuture<T> delegate() {
         return delegate;
      }

      @Override
      public long getDelay(TimeUnit arg0) {
         return delegate().getDelay(arg0);
      }

      @Override
      public int compareTo(Delayed arg0) {
         return delegate().compareTo(arg0);
      }

      @Override
      public T get() throws InterruptedException, ExecutionException {
         try {
            return delegate().get();
         } catch (ExecutionException e) {
            throw addSubmissionTrace(submissionTrace, e);
         }
      }

      @Override
      public T get(long arg0, TimeUnit arg1) throws InterruptedException, ExecutionException, TimeoutException {
         try {
            return delegate().get(arg0, arg1);
         } catch (ExecutionException e) {
            throw addSubmissionTrace(submissionTrace, e);
         }
      }
   }

}
