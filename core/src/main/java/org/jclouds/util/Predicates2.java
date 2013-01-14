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
package org.jclouds.util;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.jclouds.util.Throwables2.getFirstThrowableOfType;

import java.util.Date;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.annotation.Resource;

import org.jclouds.logging.Logger;

import com.google.common.base.Predicate;

public class Predicates2 {
   /** Returns a predicate that evaluates to true if the String being tested starts with a prefix. */
   public static Predicate<String> startsWith(final String prefix) {
      return new Predicate<String>() {
         @Override
         public boolean apply(final String input) {
             return input.startsWith(prefix);
         }

         @Override
         public String toString() {
            return "startsWith(" + prefix + ")";
         }
      };
   }

   /**
    * Retries a predicate until it is met, a timeout occurs, or an exception occurs.
    */
   public static <T> Predicate<T> retry(Predicate<T> findOrBreak, long timeout, long period, long maxPeriod,
         TimeUnit unit) {
      return new RetryablePredicate<T>(findOrBreak, timeout, period, maxPeriod, unit);
   }

   /**
    * like {@link #retry(Predicate, long, long, long, TimeUnit)} where {@code maxPeriod} is 10x {@code period}
    */
   public static <T> Predicate<T> retry(Predicate<T> findOrBreak, long timeout, long period, TimeUnit unit) {
      return retry(findOrBreak, timeout, period, period * 10l, unit);
   }

   /**
    * like {@link #retry(Predicate, long, long, long, TimeUnit)} where {@code unit} is in milliseconds
    */
   public static <T> Predicate<T> retry(Predicate<T> findOrBreak, long timeout, long period, long maxPeriod) {
      return retry(findOrBreak, timeout, period, maxPeriod, MILLISECONDS);
   }

   public static final long DEFAULT_PERIOD = 50l;
   public static final long DEFAULT_MAX_PERIOD = 1000l;

   /**
    * like {@link #retry(Predicate, long, long, long, TimeUnit)} where {@code unit} is in milliseconds, {@code period}
    * is 50ms, and {@code maxPeriod} 1s.
    */
   public static <T> Predicate<T> retry(Predicate<T> findOrBreak, long timeout) {
      return retry(findOrBreak, timeout, DEFAULT_PERIOD, DEFAULT_MAX_PERIOD, MILLISECONDS);
   }

   private static class RetryablePredicate<T> implements Predicate<T> {
      private final long timeout;
      private final long period;
      private final long maxPeriod;
      private final Predicate<T> findOrBreak;

      @Resource
      protected Logger logger = Logger.NULL;

      protected RetryablePredicate(Predicate<T> findOrBreak, long timeout, long period, long maxPeriod, TimeUnit unit) {
         this.findOrBreak = findOrBreak;
         this.timeout = unit.toMillis(timeout);
         this.period = unit.toMillis(period);
         this.maxPeriod = unit.toMillis(maxPeriod);
      }
      
      @Override
      public boolean apply(T input) {
         try {
            long i = 1l;
            for (Date end = new Date(System.currentTimeMillis() + timeout); before(end); Thread.sleep(nextMaxInterval(i++,
                     end))) {
               if (findOrBreak.apply(input)) {
                  return true;
               } else if (atOrAfter(end)) {
                  return false;
               }
            }
         } catch (InterruptedException e) {
            logger.warn(e, "predicate %s on %s interrupted, returning false", input, findOrBreak);
            Thread.currentThread().interrupt();
         } catch (RuntimeException e) {
            if (getFirstThrowableOfType(e, ExecutionException.class) != null) {
               logger.warn(e, "predicate %s on %s errored [%s], returning false", input, findOrBreak, e.getMessage());
               return false;
            } else if (getFirstThrowableOfType(e, IllegalStateException.class) != null) {
               logger.warn(e, "predicate %s on %s illegal state [%s], returning false", input, findOrBreak, e.getMessage());
               return false;
            } else if (getFirstThrowableOfType(e, CancellationException.class) != null) {
               logger.warn(e, "predicate %s on %s cancelled [%s], returning false", input, findOrBreak, e.getMessage());
               return false;
            } else if (getFirstThrowableOfType(e, TimeoutException.class) != null) {
               logger.warn(e, "predicate %s on %s timed out [%s], returning false", input, findOrBreak, e.getMessage());
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
}
