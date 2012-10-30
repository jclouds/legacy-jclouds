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
package org.jclouds.cache.internal;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.concurrent.Callable;

import org.jclouds.util.Throwables2;

import com.google.common.annotations.Beta;
import com.google.common.base.Objects;
import com.google.common.base.Throwables;
import com.google.common.collect.ForwardingObject;

/**
 * Exponentially backs off, if we encounter an exception of the given type during
 * {@link Callable#call}
 * 
 * @author Adrian Cole
 * @since 1.5
 */
@Beta
class BackoffExponentiallyAndRetryOnThrowableCallable<T> extends ForwardingObject implements Callable<T> {
   private final Class<? extends Throwable> retryableThrowable;
   private final long periodMs;
   private final long maxPeriodMs;
   private final int maxTries;
   private final Callable<T> callable;

   BackoffExponentiallyAndRetryOnThrowableCallable(Class<? extends Throwable> retryableThrowable, long periodMs,
            long maxPeriodMs, int maxTries, Callable<T> callable) {
      this.retryableThrowable = checkNotNull(retryableThrowable, "retryableThrowable");
      checkArgument(maxTries > 1, "maxTries must be more than one: %d", maxTries);
      this.maxTries = maxTries;
      checkArgument(periodMs > 0, "periodMs must be positive: %d", periodMs);
      this.periodMs = periodMs;
      checkArgument(maxPeriodMs > periodMs, "maxPeriodMs must be equal to or greater than periodMs: %d %d",
               maxPeriodMs, periodMs);
      this.maxPeriodMs = maxPeriodMs;
      this.callable = checkNotNull(callable, "callable");
   }

   @Override
   protected Callable<T> delegate() {
      return callable;
   }

   @Override
   public T call() throws Exception {
      Exception currentException = null;
      for (int currentTries = 0; currentTries < maxTries; currentTries++) {
         try {
            return delegate().call();
         } catch (Exception e) {
            currentException = e;
            if (Throwables2.getFirstThrowableOfType(e, retryableThrowable) != null) {
               imposeBackoffExponentialDelay(periodMs, maxPeriodMs, 2, currentTries, maxTries);
            } else {
               throw e;
            }
         }
      }
      throw currentException;
   }

   void imposeBackoffExponentialDelay(long period, long maxPeriod, int pow, int failureCount, int max) {
      long delayMs = (long) (period * Math.pow(failureCount, pow));
      delayMs = delayMs > maxPeriod ? maxPeriod : delayMs;
      try {
         Thread.sleep(delayMs);
      } catch (InterruptedException e) {
         Throwables.propagate(e);
      }
   }

   @Override
   public String toString() {
      return Objects.toStringHelper("").add("retryableThrowable", retryableThrowable).add("periodMs", periodMs).add(
               "maxPeriodMs", maxPeriodMs).add("maxTries", maxTries).add("callable", callable).toString();
   }
}
