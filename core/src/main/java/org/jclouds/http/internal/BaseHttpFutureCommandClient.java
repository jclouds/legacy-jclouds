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
package org.jclouds.http.internal;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;

import org.jclouds.http.HttpFutureCommand;
import org.jclouds.http.HttpFutureCommandClient;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpRequestFilter;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.HttpResponseException;
import org.jclouds.http.handlers.DelegatingErrorHandler;
import org.jclouds.http.handlers.DelegatingRetryHandler;
import org.jclouds.logging.Logger;

import com.google.inject.Inject;

public abstract class BaseHttpFutureCommandClient<Q> implements HttpFutureCommandClient {

   private final DelegatingRetryHandler retryHandler;
   private final DelegatingErrorHandler errorHandler;

   @Resource
   protected Logger logger = Logger.NULL;

   @Inject(optional = true)
   protected List<HttpRequestFilter> requestFilters = Collections.emptyList();

   protected BaseHttpFutureCommandClient(DelegatingRetryHandler retryHandler,
            DelegatingErrorHandler errorHandler) {
      this.retryHandler = retryHandler;
      this.errorHandler = errorHandler;
   }

   public void submit(HttpFutureCommand<?> command) {
      HttpRequest request = command.getRequest();

      HttpResponse response = null;
      try {
         for (;;) {
            Q nativeRequest = null;
            try {
               logger.trace("%s - converting request %s", request.getEndPoint(), request);
               for (HttpRequestFilter filter : requestFilters) {
                  filter.filter(request);
               }
               nativeRequest = convert(request);
               response = invoke(nativeRequest);
               int statusCode = response.getStatusCode();
               if (statusCode >= 300) {
                  if (retryHandler.shouldRetryRequest(command, response)) {
                     continue;
                  } else {
                     errorHandler.handleError(command, response);
                     break;
                  }

               } else {
                  processResponse(response, command);
                  break;
               }
            } finally {
               cleanup(nativeRequest);
            }
         }
      } catch (Exception e) {
         logger.error(e, "command: %s failed with response %s", command, response);
         command.setException(new HttpResponseException(command, response, e));
      }
   }

   protected abstract Q convert(HttpRequest request) throws IOException;

   protected abstract HttpResponse invoke(Q nativeRequest) throws IOException;

   protected abstract void cleanup(Q nativeResponse);

   protected void processResponse(HttpResponse response, HttpFutureCommand<?> command) {
      command.getResponseFuture().setResponse(response);
      command.getResponseFuture().run();
   }

}