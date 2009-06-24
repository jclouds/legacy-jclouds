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

import org.jclouds.http.HttpErrorHandler;
import org.jclouds.http.HttpFutureCommand;
import org.jclouds.http.annotation.ClientError;
import org.jclouds.http.annotation.Redirection;
import org.jclouds.http.annotation.ServerError;

import com.google.common.annotations.VisibleForTesting;
import com.google.inject.Inject;

/**
 * Delegates to {@link HttpErrorHandler HttpErrorHandlers} who are annotated according to the
 * response codes they relate to.
 * 
 * @author Adrian Cole
 */
public class DelegatingErrorHandler implements HttpErrorHandler {

   @Redirection
   @Inject(optional = true)
   @VisibleForTesting
   HttpErrorHandler redirectionHandler = new CloseContentAndSetExceptionErrorHandler();

   @ClientError
   @Inject(optional = true)
   @VisibleForTesting
   HttpErrorHandler clientErrorHandler = new CloseContentAndSetExceptionErrorHandler();

   @ServerError
   @Inject(optional = true)
   @VisibleForTesting
   HttpErrorHandler serverErrorHandler = new CloseContentAndSetExceptionErrorHandler();

   public void handleError(HttpFutureCommand<?> command, org.jclouds.http.HttpResponse response) {
      int statusCode = response.getStatusCode();
      if (statusCode >= 300 && statusCode < 400) {
         getRedirectionHandler().handleError(command, response);
      } else if (statusCode >= 400 && statusCode < 500) {
         getClientErrorHandler().handleError(command, response);
      } else if (statusCode >= 500) {
         getServerErrorHandler().handleError(command, response);
      }
   }

   public HttpErrorHandler getRedirectionHandler() {
      return redirectionHandler;
   }

   public HttpErrorHandler getClientErrorHandler() {
      return clientErrorHandler;
   }

   public HttpErrorHandler getServerErrorHandler() {
      return serverErrorHandler;
   }
}
