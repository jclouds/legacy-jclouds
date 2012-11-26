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
 * The "later" can be specified as a delta from the current time or as an absolute time.
 * In either constructor, a reference time must be supplied to calculate the offset in milliseconds.
 *
 */
public class RetryLaterException extends RuntimeException {

   /**
    * Time in seconds after which a request can be remade.
    */
   private final long retryAfter;

   /**
    * Construct an exception
    * @param message message
    * @param retryAfter retry after time. Negative values are converted to zero
    */
   public RetryLaterException(String message, long retryAfter) {
      super(message);
      this.retryAfter = Math.max(retryAfter, 0);
   }

/**
    * Construct an exception using the specified retry date, and the reference date used to
    * calculate the difference.
    * If the difference is negative, the retryAfter field is set to 0.
    * @param message exception text
    * @param retryDate date when a retry is permitted.
    * @param now current time to use when calculating the offset.
    */
   public RetryLaterException(String message, Date retryDate, Date now) {
      super(message);
      retryAfter = Math.max((retryDate.getTime()/1000 - now.getTime()/1000), 0);
   }

   /**
    * Get the value of the retry time
    * @return the retry time, in seconds. This is always zero or positive.
    */
   public long getRetryAfter() {
      return retryAfter;
   }

@Override
   public String toString() {
      return super.toString() + " retry after " + retryAfter + "s";
   }
}
