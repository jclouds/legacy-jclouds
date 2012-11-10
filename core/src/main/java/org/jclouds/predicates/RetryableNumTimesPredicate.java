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
package org.jclouds.predicates;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.util.Throwables2.getFirstThrowableOfType;

import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.annotation.Resource;

import org.jclouds.logging.Logger;

import com.google.common.base.Predicate;

/**
 * 
 * Retries a condition until it is met or the max number of retries have occurred.
 * maxAttempts parameter is required.
 * Initial retry period and retry maxPeriod are optionally configurable,
 * defaulting to 50ms and 1000ms respectively,
 * with the retrier increasing the interval by a factor of 1.5 each time within these constraints.
 * 
 * @author Aled Sage
 */
public class RetryableNumTimesPredicate<T> implements Predicate<T> {
   private final int maxAttempts;
   private final long period;
   private final long maxPeriod;
   private final Predicate<T> predicate;

   @Resource
   protected Logger logger = Logger.NULL;

   public RetryableNumTimesPredicate(Predicate<T> predicate, int maxAttempts, long period, long maxPeriod, TimeUnit unit) {
      this.predicate = checkNotNull(predicate);
      this.maxAttempts = maxAttempts;
      this.period = unit.toMillis(period);
      this.maxPeriod = unit.toMillis(maxPeriod);
      checkArgument(maxAttempts >= 0, "maxAttempts must be greater than zero, but was "+maxAttempts);
      checkArgument(period >= 0, "period must be greater than zero, but was "+period);
      checkArgument(maxPeriod >= 0, "maxPeriod must be greater than zero, but was "+maxPeriod);
      checkArgument(maxPeriod >= period, "maxPeriod must be greater than or equal to period, but was "+maxPeriod+" < "+period);
   }
   
   public RetryableNumTimesPredicate(Predicate<T> predicate, int maxAttempts, long period, TimeUnit unit) {
      this(predicate, maxAttempts, period, period*10, unit);
   }
   
   public RetryableNumTimesPredicate(Predicate<T> predicate, int maxAttempts) {
      this(predicate, maxAttempts, 50l, 1000l, TimeUnit.MILLISECONDS);
   }

   @Override
   public boolean apply(T input) {
      try {
         for (int i = 1; i <= maxAttempts; Thread.sleep(nextMaxInterval(i++))) {
            if (predicate.apply(input)) {
               return true;
            }
         }
         return false;
         
      } catch (InterruptedException e) {
         logger.warn(e, "predicate %s on %s interrupted, returning false", input, predicate);
         Thread.currentThread().interrupt();
      } catch (RuntimeException e) {
         if (getFirstThrowableOfType(e, ExecutionException.class) != null) {
            logger.warn(e, "predicate %s on %s errored [%s], returning false", input, predicate, e.getMessage());
            return false;
         } else if (getFirstThrowableOfType(e, IllegalStateException.class) != null) {
            logger.warn(e, "predicate %s on %s illegal state [%s], returning false", input, predicate, e.getMessage());
            return false;
         } else if (getFirstThrowableOfType(e, CancellationException.class) != null) {
            logger.warn(e, "predicate %s on %s cancelled [%s], returning false", input, predicate, e.getMessage());
            return false;
         } else if (getFirstThrowableOfType(e, TimeoutException.class) != null) {
            logger.warn(e, "predicate %s on %s timed out [%s], returning false", input, predicate, e.getMessage());
            return false;
         } else
            throw e;
      }
      return false;
   }

   protected long nextMaxInterval(long attempt) {
      // Interval increases exponentially, at a rate of nextInterval *= 1.5
      // Note that attempt starts counting at 1
      long interval = (long) (period * Math.pow(1.5, attempt - 1));
      return interval > maxPeriod ? maxPeriod : interval;
   }
}
