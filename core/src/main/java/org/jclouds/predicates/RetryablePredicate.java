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

import static org.jclouds.util.Throwables2.getFirstThrowableOfType;

import java.util.Date;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.annotation.Resource;

import org.jclouds.logging.Logger;

import com.google.common.base.Predicate;

/**
 * 
 * Retries a condition until it is met or a timeout occurs.
 * maxWait parameter is required.
 * Initial retry period and retry maxPeriod are optionally configurable,
 * defaulting to 50ms and 1000ms respectively,
 * with the retrier increasing the interval by a factor of 1.5 each time within these constraints.
 * All values taken as millis unless TimeUnit specified.
 * 
 * @author Adrian Cole
 */
public class RetryablePredicate<T> implements Predicate<T> {
   public static final long DEFAULT_PERIOD = 50l;
   public static final long DEFAULT_MAX_PERIOD = 1000l;

   private final long maxWait;
   private final long period;
   private final long maxPeriod;
   private final Predicate<T> predicate;

   @Resource
   protected Logger logger = Logger.NULL;

   public RetryablePredicate(Predicate<T> predicate, long maxWait, long period, long maxPeriod, TimeUnit unit) {
      this.predicate = predicate;
      this.maxWait = unit.toMillis(maxWait);
      this.period = unit.toMillis(period);
      this.maxPeriod = unit.toMillis(maxPeriod);
   }

   public RetryablePredicate(Predicate<T> predicate, long maxWait, long period, TimeUnit unit) {
      this(predicate, maxWait, period, period * 10l, unit);
   }

   public RetryablePredicate(Predicate<T> predicate, long maxWait, long period, long maxPeriod) {
      this(predicate, maxWait, period, maxPeriod, TimeUnit.MILLISECONDS);
   }

   public RetryablePredicate(Predicate<T> predicate, long maxWait) {
      this(predicate, maxWait, DEFAULT_PERIOD, DEFAULT_MAX_PERIOD, TimeUnit.MILLISECONDS);
   }

   @Override
   public boolean apply(T input) {
      try {
         long i = 1l;
         for (Date end = new Date(System.currentTimeMillis() + maxWait); before(end); Thread.sleep(nextMaxInterval(i++,
                  end))) {
            if (predicate.apply(input)) {
               return true;
            } else if (atOrAfter(end)) {
               return false;
            }
         }
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

   protected long nextMaxInterval(long attempt, Date end) {
      // Interval increases exponentially, at a rate of nextInterval *= 1.5
      // Note that attempt starts counting at 1
      long interval = (long) (period * Math.pow(1.5, attempt - 1));
      interval = interval > maxPeriod ? maxPeriod : interval;
      long max = end.getTime() - System.currentTimeMillis();
      return (interval > max) ? max : interval;
   }

   protected boolean before(Date end) {
      return new Date().compareTo(end) <= 1;
   }

   protected boolean atOrAfter(Date end) {
      return new Date().compareTo(end) >= 0;
   }
}
