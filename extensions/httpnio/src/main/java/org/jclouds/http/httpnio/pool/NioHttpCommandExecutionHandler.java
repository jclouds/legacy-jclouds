/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
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
import java.util.Map.Entry;
import java.util.concurrent.BlockingQueue;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpResponse;
import org.apache.http.nio.entity.ConsumingNHttpEntity;
import org.apache.http.nio.protocol.NHttpRequestExecutionHandler;
import org.apache.http.protocol.HttpContext;
import org.jclouds.http.HttpCommand;
import org.jclouds.http.HttpCommandRendezvous;
import org.jclouds.http.HttpConstants;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpRequestFilter;
import org.jclouds.http.handlers.DelegatingErrorHandler;
import org.jclouds.http.handlers.DelegatingRetryHandler;
import org.jclouds.http.httpnio.util.NioHttpUtils;
import org.jclouds.http.internal.HttpWire;
import org.jclouds.logging.Logger;

/**
 * // TODO: Adrian: Document this!
 * 
 * @author Adrian Cole
 */
public class NioHttpCommandExecutionHandler implements NHttpRequestExecutionHandler {
   private final ConsumingNHttpEntityFactory entityFactory;
   private final DelegatingRetryHandler retryHandler;
   private final DelegatingErrorHandler errorHandler;
   private final HttpWire wire;

   /**
    * inputOnly: nothing is taken from this queue.
    */
   private final BlockingQueue<HttpCommandRendezvous<?>> resubmitQueue;

   @Resource
   protected Logger logger = Logger.NULL;
   @Resource
   @Named(HttpConstants.HTTP_HEADERS_LOGGER)
   protected Logger headerLog = Logger.NULL;

   @Inject
   public NioHttpCommandExecutionHandler(ConsumingNHttpEntityFactory entityFactory,
            BlockingQueue<HttpCommandRendezvous<?>> resubmitQueue,
            DelegatingRetryHandler retryHandler, DelegatingErrorHandler errorHandler, HttpWire wire) {
      this.entityFactory = entityFactory;
      this.resubmitQueue = resubmitQueue;
      this.retryHandler = retryHandler;
      this.errorHandler = errorHandler;
      this.wire = wire;
   }

   public interface ConsumingNHttpEntityFactory {
      public ConsumingNHttpEntity create(HttpEntity httpEntity);
   }

   public void initalizeContext(HttpContext context, Object attachment) {
   }

   public HttpEntityEnclosingRequest submitRequest(HttpContext context) {
      HttpCommandRendezvous<?> rendezvous = (HttpCommandRendezvous<?>) context
               .removeAttribute("command");
      if (rendezvous != null) {
         HttpRequest request = rendezvous.getCommand().getRequest();
         for (HttpRequestFilter filter : request.getFilters()) {
            filter.filter(request);
         }
         logger.debug("Sending request %s: %s", request.hashCode(), request.getRequestLine());
         if (request.getEntity() != null && wire.enabled())
            request.setEntity(wire.output(request.getEntity()));
         HttpEntityEnclosingRequest nativeRequest = NioHttpUtils.convertToApacheRequest(request);
         if (headerLog.isDebugEnabled()) {
            headerLog.debug(">> %s", request.getRequestLine().toString());
            for (Entry<String, String> header : request.getHeaders().entries()) {
               headerLog.debug(">> %s: %s", header.getKey(), header.getValue());
            }
         }
         return nativeRequest;
      }
      return null;
   }

   public ConsumingNHttpEntity responseEntity(HttpResponse response, HttpContext context)
            throws IOException {
      return entityFactory.create(response.getEntity());
   }

   public void handleResponse(HttpResponse apacheResponse, HttpContext context) throws IOException {
      NioHttpCommandConnectionHandle handle = (NioHttpCommandConnectionHandle) context
               .removeAttribute("command-handle");
      if (handle != null) {
         try {
            HttpCommandRendezvous<?> rendezvous = handle.getCommandRendezvous();
            HttpCommand command = rendezvous.getCommand();
            org.jclouds.http.HttpResponse response = NioHttpUtils
                     .convertToJavaCloudsResponse(apacheResponse);
            logger
                     .debug("Receiving response %s: %s", response.hashCode(), response
                              .getStatusLine());
            if (headerLog.isDebugEnabled()) {
               headerLog.debug("<< %s", response.getStatusLine().toString());
               for (Entry<String, String> header : response.getHeaders().entries()) {
                  headerLog.debug("<< %s: %s", header.getKey(), header.getValue());
               }
            }
            if (response.getContent() != null && wire.enabled())
               response.setContent(wire.input(response.getContent()));
            int statusCode = response.getStatusCode();
            if (statusCode >= 300) {
               if (retryHandler.shouldRetryRequest(command, response)) {
                  resubmitQueue.add(rendezvous);
               } else {
                  errorHandler.handleError(command, response);
                  assert command.getException() != null : "errorHandler should have set an exception!";
                  rendezvous.setException(command.getException());
               }
            } else {
               rendezvous.setResponse(response);
            }
         } catch (InterruptedException e) {
            logger.error(e, "interrupted processing response task");
         } finally {
            releaseConnectionToPool(handle);
         }
      } else {
         throw new IllegalStateException(String.format(
                  "No command-handle associated with command %1$s", context));
      }
   }

   protected void releaseConnectionToPool(NioHttpCommandConnectionHandle handle) {
      try {
         handle.release();
      } catch (InterruptedException e) {
         logger.error(e, "Interrupted releasing handle %1$s", handle);
      }
   }

   public void finalizeContext(HttpContext context) {
      NioHttpCommandConnectionHandle handle = (NioHttpCommandConnectionHandle) context
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