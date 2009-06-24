/**
 *
 * Copyright (C) 2009 Global Cloud Specialists, Inc. <info@globalcloudspecialists.com>
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
package org.jclouds.http.handlers;

import org.jclouds.http.HttpFutureCommand;
import org.jclouds.http.HttpRetryHandler;
import org.jclouds.http.annotation.ClientError;
import org.jclouds.http.annotation.Redirection;
import org.jclouds.http.annotation.ServerError;

import com.google.inject.Inject;

/**
 * Delegates to {@link HttpRetryHandler HttpRetryHandlers} who are annotated according to the
 * response codes they relate to.
 * 
 * @author Adrian Cole
 */
public class DelegatingRetryHandler implements HttpRetryHandler {

   @Redirection
   @Inject(optional = true)
   private HttpRetryHandler redirectionRetryHandler = new RedirectionRetryHandler(5);

   @ClientError
   @Inject(optional = true)
   private HttpRetryHandler clientErrorRetryHandler = new CannotRetryHandler();

   @ServerError
   @Inject(optional = true)
   private HttpRetryHandler serverErrorRetryHandler = new BackoffLimitedRetryHandler(5);

   public boolean shouldRetryRequest(HttpFutureCommand<?> command,
            org.jclouds.http.HttpResponse response) {
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

}
