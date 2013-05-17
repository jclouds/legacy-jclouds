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

import static com.google.common.base.Preconditions.checkNotNull;

import org.jclouds.rest.internal.GeneratedHttpRequest;

import com.google.common.base.Objects;

/**
 * Command whose endpoint is an http service.
 * 
 * @author Adrian Cole
 */
//TODO: get rid of all the mock tests so that this can be made final
public class HttpCommand {

   private volatile HttpRequest request;
   private volatile int failureCount;
   private volatile int redirectCount;
   private volatile Exception exception;

   public HttpCommand(HttpRequest request) {
      this.request = checkNotNull(request, "request");
      this.failureCount = 0;
      this.redirectCount = 0;
   }

   /**
    * This displays the current number of error retries for this command.
    * 
    * @see org.jclouds.Constants.PROPERTY_MAX_RETRIES
    */
   public int getFailureCount() {
      return failureCount;
   }

   /**
    * increment the current failure count.
    * 
    * @see #getFailureCount
    */
   public int incrementFailureCount() {
      return ++failureCount;
   }

   /**
    * Used to prevent a command from being re-executed, or having its response parsed.
    */
   public void setException(Exception exception) {
      this.exception = exception;
   }

   /**
    * @see #setException
    */
   public Exception getException() {
      return exception;
   }

   /**
    * increments the current number of redirect attempts for this command.
    * 
    * @see #getRedirectCount
    */
   public int incrementRedirectCount() {
      return ++redirectCount;
   }

   /**
    * This displays the current number of redirect attempts for this command.
    * 
    * @see org.jclouds.Constants.PROPERTY_MAX_REDIRECTS
    */
   public int getRedirectCount() {
      return redirectCount;
   }

   /**
    * Commands need to be replayed, if redirected or on a retryable error. Typically, this implies
    * the payload carried is not a streaming type.
    */
   public boolean isReplayable() {
      return (request.getPayload() == null) ? true : request.getPayload().isRepeatable();
   }

   /**
    * The request associated with this command.
    */
   public HttpRequest getCurrentRequest() {
      return request;
   }

   /**
    * The request associated with this command.
    */
   public void setCurrentRequest(HttpRequest request) {
      this.request = request;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(request);
   }

   @Override
   public boolean equals(Object that) {
      if (that == null)
         return false;
      if (!(that instanceof HttpCommand))
         return false;
      return Objects.equal(this.request, HttpCommand.class.cast(that).getCurrentRequest());
   }

   @Override
   public String toString() {
      if (request instanceof GeneratedHttpRequest) {
         GeneratedHttpRequest gRequest = GeneratedHttpRequest.class.cast(request);
         return String.format("[method=%s, request=%s]", gRequest.getInvocation(), gRequest.getRequestLine());
      }
      return "[request=" + request.getRequestLine() + "]";
   }

}
