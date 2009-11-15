/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
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
 * ====================================================================
 */
package org.jclouds.predicates;

import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.jclouds.logging.Logger;
import org.joda.time.DateTime;

import com.google.common.base.Predicate;

/**
 * 
 * Retries a condition until it is met or a timeout occurs.
 * 
 * @author Adrian Cole
 */
public class RetryablePredicate<T> implements Predicate<T> {
   private final int maxWait;
   private final int checkInterval;
   private final Predicate<T> predicate;

   @Resource
   protected Logger logger = Logger.NULL;

   public RetryablePredicate(Predicate<T> predicate, long maxWait, long checkInterval, TimeUnit unit) {
      this.predicate = predicate;
      this.maxWait = (int) unit.toMillis(maxWait);
      this.checkInterval = (int) unit.toMillis(checkInterval);
   }

   @Override
   public boolean apply(T input) {
      try {
         for (DateTime end = new DateTime().plusMillis(maxWait); before(end); Thread
                  .sleep(checkInterval)) {
            if (predicate.apply(input)) {
               return true;
            } else if (atOrAfter(end)) {
               return false;
            }
         }
      } catch (InterruptedException e) {
         logger.warn(e, "predicate %s on %s interrupted, returning false", input, predicate);
      }
      return false;
   }

   boolean before(DateTime end) {
      return new DateTime().compareTo(end) <= 1;
   }

   boolean atOrAfter(DateTime end) {
      return new DateTime().compareTo(end) >= 0;
   }
}
