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
package org.jclouds.http.handlers;

import javax.inject.Singleton;

import org.jclouds.http.HttpCommand;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.HttpRetryHandler;
import org.jclouds.http.annotation.ClientError;
import org.jclouds.http.annotation.Redirection;
import org.jclouds.http.annotation.ServerError;

import com.google.common.annotations.VisibleForTesting;
import com.google.inject.Inject;

/**
 * Delegates to {@link HttpRetryHandler HttpRetryHandlers} who are annotated according to the
 * response codes they relate to.
 * 
 * @author Adrian Cole
 */
@Singleton
public class DelegatingRetryHandler implements HttpRetryHandler {

   @VisibleForTesting
   @Inject(optional = true)
   @Redirection
   HttpRetryHandler redirectionRetryHandler;

   @VisibleForTesting
   @Inject(optional = true)
   @ClientError
   HttpRetryHandler clientErrorRetryHandler;

   @VisibleForTesting
   @Inject(optional = true)
   @ServerError
   HttpRetryHandler serverErrorRetryHandler;

   @Inject
   public DelegatingRetryHandler(BackoffLimitedRetryHandler backOff,
            RedirectionRetryHandler redirectionRetryHandler) {
      this.serverErrorRetryHandler = backOff;
      this.redirectionRetryHandler = redirectionRetryHandler;
      this.clientErrorRetryHandler = HttpRetryHandler.NEVER_RETRY;
   }

   public boolean shouldRetryRequest(HttpCommand command, HttpResponse response) {
      int statusCode = response.getStatusCode();
      boolean retryRequest = false;
      if (statusCode >= 300 && statusCode < 400) {
         retryRequest = redirectionRetryHandler.shouldRetryRequest(command, response);
      } else if (statusCode >= 400 && statusCode < 500) {
         retryRequest = clientErrorRetryHandler.shouldRetryRequest(command, response);
      } else if (statusCode >= 500) {
         retryRequest = serverErrorRetryHandler.shouldRetryRequest(command, response);
      }
      return retryRequest;
   }

   public HttpRetryHandler getRedirectionRetryHandler() {
      return redirectionRetryHandler;
   }

   public HttpRetryHandler getClientErrorRetryHandler() {
      return clientErrorRetryHandler;
   }

   public HttpRetryHandler getServerErrorRetryHandler() {
      return serverErrorRetryHandler;
   }
}
