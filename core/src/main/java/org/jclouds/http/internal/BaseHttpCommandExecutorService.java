/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */
package org.jclouds.http.internal;

import static org.jclouds.concurrent.ConcurrentUtils.makeListenable;

import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;

import org.jclouds.Constants;
import org.jclouds.http.HttpCommand;
import org.jclouds.http.HttpCommandExecutorService;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpRequestFilter;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.HttpUtils;
import org.jclouds.http.Payloads;
import org.jclouds.http.handlers.DelegatingErrorHandler;
import org.jclouds.http.handlers.DelegatingRetryHandler;
import org.jclouds.logging.Logger;

import com.google.common.util.concurrent.ListenableFuture;

public abstract class BaseHttpCommandExecutorService<Q> implements HttpCommandExecutorService {

   private final DelegatingRetryHandler retryHandler;
   private final DelegatingErrorHandler errorHandler;
   private final ExecutorService ioWorkerExecutor;

   @Resource
   protected Logger logger = Logger.NULL;
   @Resource
   @Named(Constants.LOGGER_HTTP_HEADERS)
   protected Logger headerLog = Logger.NULL;

   private final HttpWire wire;

   @Inject
   protected BaseHttpCommandExecutorService(
            @Named(Constants.PROPERTY_IO_WORKER_THREADS) ExecutorService ioWorkerExecutor,
            DelegatingRetryHandler retryHandler, DelegatingErrorHandler errorHandler, HttpWire wire) {
      this.retryHandler = retryHandler;
      this.errorHandler = errorHandler;
      this.ioWorkerExecutor = ioWorkerExecutor;
      this.wire = wire;
   }

   public ListenableFuture<HttpResponse> submit(HttpCommand command) {
      return makeListenable(ioWorkerExecutor.submit(new HttpResponseCallable(command)),
               ioWorkerExecutor);
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
                  filter.filter(request);
               }
               logger.debug("Sending request %s: %s", request.hashCode(), request.getRequestLine());
               if (request.getPayload() != null && wire.enabled())
                  request.setPayload(Payloads.newPayload(wire.output(request.getPayload()
                           .getRawContent())));
               nativeRequest = convert(request);
               HttpUtils.logRequest(headerLog, request, ">>");
               response = invoke(nativeRequest);
               logger.debug("Receiving response %s: %s", request.hashCode(), response
                        .getStatusLine());
               HttpUtils.logResponse(headerLog, response, "<<");
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

   protected abstract Q convert(HttpRequest request) throws IOException, InterruptedException;

   /**
    * 
    * FIXME Comment this
    * 
    * @param nativeRequest
    * @return
    * @throws IOException
    * @throws InterruptedException
    *            if interrupted waiting for a connection.
    */
   protected abstract HttpResponse invoke(Q nativeRequest) throws IOException, InterruptedException;

   protected abstract void cleanup(Q nativeResponse);

}