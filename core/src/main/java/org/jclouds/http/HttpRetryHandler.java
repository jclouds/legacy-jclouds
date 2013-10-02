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
package org.jclouds.http;

/**
 * Indicate whether a request should be retried after a server error response (HTTP status code >=
 * 500) based on the request's replayable status and the number of attempts already performed.
 * 
 * @author James Murty
 */
public interface HttpRetryHandler {
   public static final HttpRetryHandler ALWAYS_RETRY = new HttpRetryHandler() {
      public boolean shouldRetryRequest(HttpCommand command, HttpResponse response) {
         return true;
      }

      @Override
      public String toString() {
         return "ALWAYS_RETRY";
      }
   };

   public static final HttpRetryHandler NEVER_RETRY = new HttpRetryHandler() {
      public boolean shouldRetryRequest(HttpCommand command, HttpResponse response) {
         return false;
      }

      @Override
      public String toString() {
         return "NEVER_RETRY";
      }
   };

   /**
    * Return true if the command should be retried. This method should only be invoked when the
    * response has failed with a HTTP 5xx error indicating a server-side error.
    */
   boolean shouldRetryRequest(HttpCommand command, HttpResponse response);
}
