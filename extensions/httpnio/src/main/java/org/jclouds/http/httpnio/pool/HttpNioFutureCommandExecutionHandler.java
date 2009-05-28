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
import org.jclouds.http.HttpFutureCommand;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponseHandler;
import org.jclouds.http.HttpRetryHandler;
import org.jclouds.http.annotation.ClientErrorHandler;
import org.jclouds.http.annotation.RedirectHandler;
import org.jclouds.http.annotation.RetryHandler;
import org.jclouds.http.annotation.ServerErrorHandler;
import org.jclouds.http.handlers.BackoffLimitedRetryHandler;
import org.jclouds.http.handlers.CloseContentAndSetExceptionHandler;
import org.jclouds.http.httpnio.util.HttpNioUtils;
import org.jclouds.logging.Logger;

import com.google.inject.Inject;

/**
 * // TODO: Adrian: Document this!
 *
 * @author Adrian Cole
 */
public class HttpNioFutureCommandExecutionHandler implements
        NHttpRequestExecutionHandler {
    private final ExecutorService executor;
    @Resource
    protected Logger logger = Logger.NULL;
    private final ConsumingNHttpEntityFactory entityFactory;
    private final BlockingQueue<HttpFutureCommand<?>> commandQueue;

    @RedirectHandler
    @Inject(optional = true)
    private HttpResponseHandler redirectHandler = new CloseContentAndSetExceptionHandler();

    @ClientErrorHandler
    @Inject(optional = true)
    private HttpResponseHandler clientErrorHandler = new CloseContentAndSetExceptionHandler();

    @ServerErrorHandler
    @Inject(optional = true)
    private HttpResponseHandler serverErrorHandler = new CloseContentAndSetExceptionHandler();

    @RetryHandler
    @Inject(optional = true)
    protected HttpRetryHandler httpRetryHandler = new BackoffLimitedRetryHandler(5);   

    public interface ConsumingNHttpEntityFactory {
        public ConsumingNHttpEntity create(HttpEntity httpEntity);
    }

    @Inject
    public HttpNioFutureCommandExecutionHandler(
            ConsumingNHttpEntityFactory entityFactory,
            ExecutorService executor,
            BlockingQueue<HttpFutureCommand<?>> commandQueue) {
        this.executor = executor;
        this.entityFactory = entityFactory;
        this.commandQueue = commandQueue;
    }

    public void initalizeContext(HttpContext context, Object attachment) {
    }

    public HttpEntityEnclosingRequest submitRequest(HttpContext context) {
        HttpFutureCommand<?> command = (HttpFutureCommand<?>) context
                .removeAttribute("command");
        if (command != null) {
            HttpRequest object = command.getRequest();
            return HttpNioUtils.convertToApacheRequest(object);
        }
        return null;

    }

    public ConsumingNHttpEntity responseEntity(HttpResponse response,
                                               HttpContext context) throws IOException {
        return entityFactory.create(response.getEntity());
    }

    public void handleResponse(HttpResponse apacheResponse, HttpContext context)
            throws IOException {
        HttpNioFutureCommandConnectionHandle handle = (HttpNioFutureCommandConnectionHandle) context
                .removeAttribute("command-handle");
        if (handle != null) {
            try {
                HttpFutureCommand<?> command = handle.getCommand();
                org.jclouds.http.HttpResponse response = HttpNioUtils
                        .convertToJavaCloudsResponse(apacheResponse);

                int code = response.getStatusCode();
                if (code >= 500) {
                   boolean retryRequest = false;
                   try {
                	   retryRequest = httpRetryHandler.retryRequest(command, response);
                   } catch (InterruptedException ie) {
                      // TODO: Add interrupt exception to command and abort? 
                   }
                   if (retryRequest) {
                      commandQueue.add(command);
                   } else {
                      this.serverErrorHandler.handle(command, response);
                   }
                } else if (code >= 400 && code < 500) {
                    this.clientErrorHandler.handle(command, response);
                } else if (code >= 300 && code < 400) {
                    this.redirectHandler.handle(command, response);
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

    protected void releaseConnectionToPool(
            HttpNioFutureCommandConnectionHandle handle) {
        try {
            handle.release();
        } catch (InterruptedException e) {
            logger.error(e, "Interrupted releasing handle %1$s", handle);
        }
    }

    protected void processResponse(org.jclouds.http.HttpResponse response,
                                   HttpFutureCommand<?> command) throws IOException {
        command.getResponseFuture().setResponse(response);
        logger.trace("submitting response task %1$s", command
                .getResponseFuture());
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