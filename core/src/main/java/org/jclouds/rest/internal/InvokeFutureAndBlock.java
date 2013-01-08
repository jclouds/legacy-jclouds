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

import java.lang.reflect.InvocationTargetException;
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
import org.jclouds.reflect.Invokable;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.cache.Cache;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.inject.assistedinject.Assisted;

public class InvokeFutureAndBlock implements Function<Invocation, Result> {

   public static interface Factory {
      /**
       * @param receiver
       *           object whose interface matched {@code declaring} except all invokeds return {@link ListenableFuture}
       * @return blocking invocation handler
       */
      InvokeFutureAndBlock create(Object async);
   }

   @Resource
   private Logger logger = Logger.NULL;

   private final Cache<Invokable<?, ?>, Invokable<?, ?>> sync2AsyncInvokables;
   private final Map<String, Long> timeouts;
   private final Object receiver;

   @Inject
   @VisibleForTesting
   InvokeFutureAndBlock(Cache<Invokable<?, ?>, Invokable<?, ?>> sync2AsyncInvokables,
         @Named("TIMEOUTS") Map<String, Long> timeouts, @Assisted Object receiver) {
      this.receiver = receiver;
      this.sync2AsyncInvokables = sync2AsyncInvokables;
      this.timeouts = timeouts;
   }

   @Override
   public Result apply(Invocation invocation) {
      @SuppressWarnings("unchecked")
      Invokable<? super Object, ListenableFuture<?>> asyncMethod = Invokable.class.cast(sync2AsyncInvokables
            .getIfPresent(invocation.getInvokable()));
      try {
         ListenableFuture<?> future = asyncMethod.invoke(receiver, invocation.getArgs().toArray());
         Optional<Long> timeoutNanos = timeoutInNanos(invocation.getInvokable(), timeouts);
         return block(future, timeoutNanos);
      } catch (InvocationTargetException e) {
         return Result.fail(e);
      } catch (IllegalAccessException e) {
         return Result.fail(e);
      }

   }

   private Result block(ListenableFuture<?> future, Optional<Long> timeoutNanos) {
      try {
         if (timeoutNanos.isPresent()) {
            logger.debug(">> blocking on %s for %s", future, timeoutNanos);
            return Result.success(future.get(timeoutNanos.get(), TimeUnit.NANOSECONDS));
         } else {
            logger.debug(">> blocking on %s", future);
            return Result.success(future.get());
         }
      } catch (ExecutionException e) {
         return Result.fail(e.getCause());
      } catch (InterruptedException e) {
         return Result.fail(e); // TODO: should we kill the future?
      } catch (TimeoutException e) {
         return Result.fail(e);
      }
   }

   // override timeout by values configured in properties(in ms)
   private Optional<Long> timeoutInNanos(Invokable<?, ?> invoked, Map<String, Long> timeouts) {
      String className = invoked.getEnclosingType().getRawType().getSimpleName();
      Optional<Long> timeoutMillis = fromNullable(timeouts.get(className + "." + invoked.getName())).or(
            fromNullable(timeouts.get(className))).or(fromNullable(timeouts.get("default")));
      if (timeoutMillis.isPresent())
         return Optional.of(TimeUnit.MILLISECONDS.toNanos(timeoutMillis.get()));
      return Optional.absent();
   }
}