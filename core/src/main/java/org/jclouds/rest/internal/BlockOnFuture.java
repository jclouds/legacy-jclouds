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
package org.jclouds.rest.internal;

import static com.google.common.base.Optional.fromNullable;
import static com.google.common.collect.ObjectArrays.concat;
import static com.google.common.util.concurrent.Uninterruptibles.getUninterruptibly;
import static java.util.concurrent.TimeUnit.NANOSECONDS;

import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;

import org.jclouds.logging.Logger;
import org.jclouds.reflect.Invocation;
import org.jclouds.reflect.Invocation.Result;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.reflect.Invokable;
import com.google.common.reflect.TypeToken;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.UncheckedExecutionException;
import com.google.common.util.concurrent.UncheckedTimeoutException;
import com.google.inject.assistedinject.Assisted;

public class BlockOnFuture implements Function<ListenableFuture<?>, Result> {

   public static interface Factory {
      /**
       * @param invocation
       *           context for how the future was created
       */
      BlockOnFuture create(TypeToken<?> enclosingType, Invocation invocation);
   }

   @Resource
   private Logger logger = Logger.NULL;

   private final Map<String, Long> timeouts;
   private final TypeToken<?> enclosingType;
   private final Invocation invocation;

   @Inject
   @VisibleForTesting
   BlockOnFuture(@Named("TIMEOUTS") Map<String, Long> timeouts, @Assisted TypeToken<?> enclosingType,
         @Assisted Invocation invocation) {
      this.timeouts = timeouts;
      this.enclosingType = enclosingType;
      this.invocation = invocation;
   }

   @Override
   public Result apply(ListenableFuture<?> future) {
      Optional<Long> timeoutNanos = timeoutInNanos(invocation.getInvokable(), timeouts);
      return block(future, timeoutNanos);
   }

   private Result block(ListenableFuture<?> future, Optional<Long> timeoutNanos) {
      try {
         if (timeoutNanos.isPresent()) {
            logger.debug(">> blocking on %s for %s", future, timeoutNanos);
            return Result.success(getUninterruptibly(future, timeoutNanos.get(), NANOSECONDS));
         } else {
            logger.debug(">> blocking on %s", future);
            return Result.success(getUninterruptibly(future));
         }
      } catch (ExecutionException e) {
         throw propagateCause(e);
      } catch (TimeoutException e) {
         future.cancel(true);
         throw new UncheckedTimeoutException(e);
      }
   }

   private static RuntimeException propagateCause(Exception e) {
      Throwable cause = e.getCause();
      if (cause == null) {
         UncheckedExecutionException unchecked = new UncheckedExecutionException(e.getMessage()) {
            private static final long serialVersionUID = 1L;
         };
         unchecked.setStackTrace(e.getStackTrace());
         throw unchecked;
      }
      StackTraceElement[] combined = concat(cause.getStackTrace(), e.getStackTrace(), StackTraceElement.class);
      cause.setStackTrace(combined);
      if (cause instanceof RuntimeException) {
         throw (RuntimeException) cause;
      }
      if (cause instanceof Error) {
         throw (Error) cause;
      }
      // The cause is a weird kind of Throwable, so throw the outer exception.
      throw new RuntimeException(e);
   }

   // override timeout by values configured in properties(in ms)
   private Optional<Long> timeoutInNanos(Invokable<?, ?> invoked, Map<String, Long> timeouts) {
      String className = enclosingType.getRawType().getSimpleName();
      Optional<Long> timeoutMillis = fromNullable(timeouts.get(className + "." + invoked.getName())).or(
            fromNullable(timeouts.get(className))).or(fromNullable(timeouts.get("default")));
      if (timeoutMillis.isPresent())
         return Optional.of(TimeUnit.MILLISECONDS.toNanos(timeoutMillis.get()));
      return Optional.absent();
   }
}