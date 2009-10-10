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
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import javax.annotation.Resource;
import javax.inject.Named;

import org.jclouds.http.HttpCommand;
import org.jclouds.http.HttpCommandExecutorService;
import org.jclouds.http.HttpConstants;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpRequestFilter;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.handlers.DelegatingErrorHandler;
import org.jclouds.http.handlers.DelegatingRetryHandler;
import org.jclouds.logging.Logger;

public abstract class BaseHttpCommandExecutorService<Q> implements HttpCommandExecutorService {

   private final DelegatingRetryHandler retryHandler;
   private final DelegatingErrorHandler errorHandler;
   private final ExecutorService executorService;

   @Resource
   protected Logger logger = Logger.NULL;
   @Resource
   @Named(HttpConstants.HTTP_HEADERS_LOGGER)
   protected Logger headerLog = Logger.NULL;

   private final Wire wire;

   protected BaseHttpCommandExecutorService(ExecutorService executorService,
            DelegatingRetryHandler retryHandler, DelegatingErrorHandler errorHandler, Wire wire) {
      this.retryHandler = retryHandler;
      this.errorHandler = errorHandler;
      this.executorService = executorService;
      this.wire = wire;
   }

   public Future<HttpResponse> submit(HttpCommand command) {
      return executorService.submit(new HttpResponseCallable(command));
   }

   public class HttpResponseCallable implements Callable<HttpResponse> {
      private final HttpCommand command;

      public HttpResponseCallable(HttpCommand command) {
         this.command = command;
      }

      public HttpResponse call() throws Exception {

         HttpResponse response = null;
         for (;;) {
            HttpRequest request = command.getRequest();
            Q nativeRequest = null;
            try {
               for (HttpRequestFilter filter : request.getFilters()) {
                  request = filter.filter(request);
               }
               logger.debug("Sending request: %s", request.getRequestLine());
               if (request.getEntity() != null && wire.enabled())
                  request.setEntity(wire.output(request.getEntity()));
               nativeRequest = convert(request);
               if (headerLog.isDebugEnabled()) {
                  headerLog.debug(">> %s", request.getRequestLine().toString());
                  for (Entry<String, String> header : request.getHeaders().entries()) {
                     if (header.getKey() != null)
                        headerLog.debug(">> %s: %s", header.getKey(), header.getValue());
                  }
               }
               response = invoke(nativeRequest);
               logger.debug("Receiving response: " + response.getStatusLine());
               if (headerLog.isDebugEnabled()) {
                  headerLog.debug("<< " + response.getStatusLine().toString());
                  for (Entry<String, String> header : response.getHeaders().entries()) {
                     if (header.getKey() != null)
                        headerLog.debug("<< %s: %s", header.getKey(), header.getValue());
                  }
               }
               if (response.getContent() != null && wire.enabled())
                  response.setContent(wire.input(response.getContent()));
               int statusCode = response.getStatusCode();
               if (statusCode >= 300) {
                  if (retryHandler.shouldRetryRequest(command, response)) {
                     continue;
                  } else {
                     errorHandler.handleError(command, response);
                     break;
                  }
               } else {
                  break;
               }
            } finally {
               cleanup(nativeRequest);
            }
         }
         if (command.getException() != null)
            throw command.getException();
         return response;
      }
   }

   protected abstract Q convert(HttpRequest request) throws IOException;

   protected abstract HttpResponse invoke(Q nativeRequest) throws IOException;

   protected abstract void cleanup(Q nativeResponse);

}