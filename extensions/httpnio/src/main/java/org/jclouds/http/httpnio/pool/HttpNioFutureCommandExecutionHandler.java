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
package org.jclouds.http.httpnio.pool;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;

import javax.annotation.Resource;

import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpResponse;
import org.apache.http.nio.entity.ConsumingNHttpEntity;
import org.apache.http.nio.protocol.NHttpRequestExecutionHandler;
import org.apache.http.protocol.HttpContext;
import org.jclouds.http.HttpErrorHandler;
import org.jclouds.http.HttpFutureCommand;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpRetryHandler;
import org.jclouds.http.handlers.DelegatingErrorHandler;
import org.jclouds.http.handlers.DelegatingRetryHandler;
import org.jclouds.http.httpnio.util.HttpNioUtils;
import org.jclouds.logging.Logger;

import com.google.inject.Inject;

/**
 * // TODO: Adrian: Document this!
 * 
 * @author Adrian Cole
 */
public class HttpNioFutureCommandExecutionHandler implements NHttpRequestExecutionHandler {
   private final ExecutorService executor;
   @Resource
   protected Logger logger = Logger.NULL;
   private final ConsumingNHttpEntityFactory entityFactory;

   /**
    * inputOnly: nothing is taken from this queue.
    */
   private final BlockingQueue<HttpFutureCommand<?>> resubmitQueue;

   @Inject(optional = true)
   private HttpRetryHandler retryHandler = new DelegatingRetryHandler();

   @Inject(optional = true)
   private HttpErrorHandler errorHandler = new DelegatingErrorHandler();

   @Inject
   public HttpNioFutureCommandExecutionHandler(ConsumingNHttpEntityFactory entityFactory,
            ExecutorService executor, BlockingQueue<HttpFutureCommand<?>> resubmitQueue) {
      this.executor = executor;
      this.entityFactory = entityFactory;
      this.resubmitQueue = resubmitQueue;
   }

   public interface ConsumingNHttpEntityFactory {
      public ConsumingNHttpEntity create(HttpEntity httpEntity);
   }

   public void initalizeContext(HttpContext context, Object attachment) {
   }

   public HttpEntityEnclosingRequest submitRequest(HttpContext context) {
      HttpFutureCommand<?> command = (HttpFutureCommand<?>) context.removeAttribute("command");
      if (command != null) {
         HttpRequest request = command.getRequest();
         return HttpNioUtils.convertToApacheRequest(request);
      }
      return null;

   }

   public ConsumingNHttpEntity responseEntity(HttpResponse response, HttpContext context)
            throws IOException {
      return entityFactory.create(response.getEntity());
   }

   public void handleResponse(HttpResponse apacheResponse, HttpContext context) throws IOException {
      HttpNioFutureCommandConnectionHandle handle = (HttpNioFutureCommandConnectionHandle) context
               .removeAttribute("command-handle");
      if (handle != null) {
         try {
            HttpFutureCommand<?> command = handle.getCommand();
            org.jclouds.http.HttpResponse response = HttpNioUtils
                     .convertToJavaCloudsResponse(apacheResponse);
            int statusCode = response.getStatusCode();
            if (statusCode >= 300) {
               if (retryHandler.shouldRetryRequest(command, response)) {
                  resubmitQueue.add(command);
               } else {
                  errorHandler.handleError(command, response);
               }
            } else {
               processResponse(response, command);
            }
         } finally {
            releaseConnectionToPool(handle);
         }
      } else {
         throw new IllegalStateException(String.format(
                  "No command-handle associated with command %1$s", context));
      }
   }

   protected void releaseConnectionToPool(HttpNioFutureCommandConnectionHandle handle) {
      try {
         handle.release();
      } catch (InterruptedException e) {
         logger.error(e, "Interrupted releasing handle %1$s", handle);
      }
   }

   protected void processResponse(org.jclouds.http.HttpResponse response,
            HttpFutureCommand<?> command) throws IOException {
      command.getResponseFuture().setResponse(response);
      logger.trace("submitting response task %1$s", command.getResponseFuture());
      executor.submit(command.getResponseFuture());
   }

   public void finalizeContext(HttpContext context) {
      HttpNioFutureCommandConnectionHandle handle = (HttpNioFutureCommandConnectionHandle) context
               .removeAttribute("command-handle");
      if (handle != null) {
         try {
            handle.cancel();
         } catch (Exception e) {
            logger.error(e, "Error cancelling handle %1$s", handle);
         }
      }
   }
}