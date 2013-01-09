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

import static com.google.common.base.Preconditions.checkNotNull;

import java.lang.reflect.InvocationTargetException;

import javax.annotation.Resource;
import javax.inject.Inject;

import org.jclouds.logging.Logger;
import org.jclouds.reflect.Invocation;
import org.jclouds.reflect.Invocation.Result;
import com.google.common.reflect.Invokable;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.cache.Cache;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

/**
 * 
 * @author Adrian Cole
 */
public final class InvokeAndCallGetOnFutures<R> implements Function<Invocation, Result> {

   @Resource
   private Logger logger = Logger.NULL;

   private final Cache<Invokable<?, ?>, Invokable<?, ?>> sync2AsyncInvokables;
   private final R receiver;

   /**
    * @param receiver
    *           will have any methods that return {@link ListenableFuture} unwrapped.
    * @return blocking invocation handler
    */
   @Inject
   @VisibleForTesting
   InvokeAndCallGetOnFutures(Cache<Invokable<?, ?>, Invokable<?, ?>> sync2AsyncInvokables, R receiver) {
      this.sync2AsyncInvokables = sync2AsyncInvokables;
      this.receiver = receiver;
   }

   @SuppressWarnings("unchecked")
   @Override
   public Result apply(Invocation in) {
      @SuppressWarnings("rawtypes")
      Invokable target = checkNotNull(sync2AsyncInvokables.getIfPresent(in.getInvokable()), "invokable %s not in %s",
            in.getInvokable(), sync2AsyncInvokables);
      Object returnVal;
      try {
         returnVal = target.invoke(receiver, in.getArgs().toArray());
      } catch (InvocationTargetException e) {
         return Result.fail(e);
      } catch (IllegalAccessException e) {
         return Result.fail(e);
      }
      if (!isFuture(target))
         return Result.success(returnVal);
      return Result.success(Futures.getUnchecked(ListenableFuture.class.cast(returnVal)));
   }

   private boolean isFuture(Invokable<?, ?> target) {
      return target.getReturnType().getRawType().isAssignableFrom(ListenableFuture.class);
   }

   @Override
   public String toString() {
      return String.format("InvokeAndCallGetOnFutures(%s)", receiver);
   }

}
