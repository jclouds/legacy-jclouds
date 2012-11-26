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

package org.jclouds.openstack.nova.v2_0.handlers;

import java.util.Date;

/**
 * This exception is raised when the Nova endpoint returns with a response telling the caller to make
 * the same request later. 
 * The "later" can be specified as an absolute time, or as a delta from the current time.
 * In either constructor, a reference time must be supplied to calculate the other value.
 * 
 * Callers 
 */
public class RetryLaterException extends RuntimeException {

   /** System time when a request can be retried */
   private final Date retryDate;

   /**
    * Time in seconds after which a request can be remade.
    */
   private final long retryAfter;

   /**
    * Construct an exception
    * @param message message
    * @param retryAfter
    * @param now
    */
   public RetryLaterException(String message, long retryAfter, long now) {
      super(message);
      this.retryAfter = retryAfter;
      retryDate = new Date(now + (retryAfter * 1000));
   }

   public RetryLaterException(String message, Date retryDate, long now) {
      super(message);
      this.retryDate = retryDate;
      long after = (retryDate.getTime() - now) / 1000;
      if (after < 0) {
         after = 0;
      }
      retryAfter = after;
   }

   public Date getRetryDate() {
      return retryDate;
   }

   public long getRetryAfter() {
      return retryAfter;
   }

   @Override
   public String toString() {
      return super.toString() + " retry after " + retryAfter + "s  - at " + retryDate;
   }
}
