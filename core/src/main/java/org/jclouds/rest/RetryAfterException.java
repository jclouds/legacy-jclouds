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
package org.jclouds.rest;

import com.google.common.net.HttpHeaders;
import com.google.common.primitives.Ints;

/**
 * This exception is raised when an http endpoint returns with a response
 * telling the caller to make the same request after a certain period of time.
 * 
 * Typically, this is returned with a {@code 503} status code, as specified in
 * the {@link HttpHeaders#RETRY_AFTER} header.
 */
public class RetryAfterException extends RuntimeException {

   private static final long serialVersionUID = 1L;

   /**
    * Delta in seconds
    */
   private final int seconds;

   /**
    * Construct an exception instance to happen at a time in the future
    * 
    * @param message
    *           message
    * @param seconds
    *           retry after delta. Negative values are converted to zero
    */
   public RetryAfterException(String message, int seconds) {
      super(message);
      this.seconds = Ints.max(seconds, 0);
   }

   /**
    * Construct an exception instance to happen at a time in the future
    * 
    * @param cause
    *           cause
    * @param seconds
    *           retry after delta. Negative values are converted to zero
    */
   public RetryAfterException(Throwable cause, int seconds) {
      super(defaultMessage(seconds = Ints.max(seconds, 0)), cause);
      this.seconds = seconds;
   }
   
   private static String defaultMessage(int seconds) {
      switch (seconds) {
      case 0:
         return "retry now";
      case 1:
         return "retry in 1 second";
      default:
         return String.format("retry in %d seconds", seconds);
      }
   }

   /**
    * Construct an exception instance to happen at a time in the future
    * 
    * @param message
    *           message
    * @param cause
    *           cause
    * @param seconds
    *           retry after delta. Negative values are converted to zero
    */
   public RetryAfterException(String message, Throwable cause, int seconds) {
      super(message, cause);
      this.seconds = Ints.max(seconds, 0);
   }

   /**
    * Get the value of the retry time
    * 
    * @return the retry time, in seconds. This is always zero or positive.
    */
   public int getSeconds() {
      return seconds;
   }

}
