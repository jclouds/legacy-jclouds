/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */
package org.jclouds.predicates;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.jclouds.logging.Logger;

import com.google.common.base.Predicate;

/**
 * 
 * Retries a condition until it is met or a timeout occurs.
 * 
 * @author Adrian Cole
 */
public class RetryablePredicate<T> implements Predicate<T> {
   private final long maxWait;
   private final long period;
   private final Predicate<T> predicate;

   @Resource
   protected Logger logger = Logger.NULL;

   public RetryablePredicate(Predicate<T> predicate, long maxWait, long period,
         TimeUnit unit) {
      this.predicate = predicate;
      this.maxWait = unit.toMillis(maxWait);
      this.period = unit.toMillis(period);
   }

   public RetryablePredicate(Predicate<T> predicate, long maxWait) {
      this.predicate = predicate;
      this.maxWait = maxWait;
      this.period = 50l;
   }

   @Override
   public boolean apply(T input) {
      try {
         long i = 1l;
         for (Date end = new Date(System.currentTimeMillis() + maxWait); before(end); Thread
               .sleep(nextMaxInterval(i++, end))) {
            if (predicate.apply(input)) {
               return true;
            } else if (atOrAfter(end)) {
               return false;
            }
         }
      } catch (InterruptedException e) {
         logger.warn(e, "predicate %s on %s interrupted, returning false",
               input, predicate);
      }
      return false;
   }

   long nextMaxInterval(long attempt, Date end) {
      long interval = (period * (long) Math.pow(attempt, 2l));
      long max = end.getTime() - System.currentTimeMillis();
      return (interval > max) ? max : interval;
   }

   boolean before(Date end) {
      return new Date().compareTo(end) <= 1;
   }

   boolean atOrAfter(Date end) {
      return new Date().compareTo(end) >= 0;
   }
}
