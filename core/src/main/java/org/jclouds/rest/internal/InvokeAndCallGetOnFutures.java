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
package org.jclouds.rest.internal;

import static com.google.common.base.Throwables.propagate;
import static com.google.common.util.concurrent.Futures.getUnchecked;

import java.lang.reflect.InvocationTargetException;

import javax.annotation.Resource;
import javax.inject.Inject;

import org.jclouds.logging.Logger;
import org.jclouds.reflect.Invocation;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.reflect.Invokable;
import com.google.common.util.concurrent.ListenableFuture;

/**
 * 
 * @author Adrian Cole
 * @deprecated will be removed in jclouds 1.7, as async interfaces are no longer supported.
 */
@Deprecated
public final class InvokeAndCallGetOnFutures<R> implements Function<Invocation, Object> {

   @Resource
   private Logger logger = Logger.NULL;

   private final Function<Invocation, Invocation> sync2async;
   private final R receiver;

   /**
    * @param receiver
    *           will have any methods that return {@link ListenableFuture} unwrapped.
    * @return blocking invocation handler
    */
   @Inject
   @VisibleForTesting
   InvokeAndCallGetOnFutures(Function<Invocation, Invocation> sync2async, R receiver) {
      this.sync2async = sync2async;
      this.receiver = receiver;
   }

   @SuppressWarnings("unchecked")
   @Override
   public Object apply(Invocation in) {
      @SuppressWarnings("rawtypes")
      Invokable target = sync2async.apply(in).getInvokable();
      Object returnVal;
      try {
         returnVal = target.invoke(receiver, in.getArgs().toArray());
      } catch (InvocationTargetException e) {
         throw propagate(e.getCause());
      } catch (IllegalAccessException e) {
         throw new Error("Method became inaccessible: " + toString(), e);
      }
      if (!isFuture(target))
         return returnVal;
      return getUnchecked(ListenableFuture.class.cast(returnVal));
   }

   private boolean isFuture(Invokable<?, ?> target) {
      return target.getReturnType().getRawType().isAssignableFrom(ListenableFuture.class);
   }

   @Override
   public String toString() {
      return String.format("InvokeAndCallGetOnFutures(%s)", receiver);
   }

}
