/**
 *
 * Copyright (C) 2009 Adrian Cole <adriancole@jclouds.org>
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

import com.google.inject.Inject;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpResponse;
import org.apache.http.nio.NHttpClientConnection;
import org.apache.http.nio.entity.ConsumingNHttpEntity;
import org.apache.http.nio.protocol.NHttpRequestExecutionHandler;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HttpContext;
import org.jclouds.Logger;
import org.jclouds.command.FutureCommand;
import org.jclouds.http.HttpFutureCommand;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.httpnio.HttpNioUtils;

import java.io.IOException;
import java.util.concurrent.ExecutorService;


/**
 * // TODO: Adrian: Document this!
 *
 * @author Adrian Cole
 */
public class HttpNioFutureCommandExecutionHandler implements NHttpRequestExecutionHandler {
    private final ExecutorService executor;
    protected final Logger logger;
    private final ConsumingNHttpEntityFactory entityFactory;
    private final HttpNioFutureCommandConnectionRetry futureOperationRetry;

    public interface ConsumingNHttpEntityFactory {
        public ConsumingNHttpEntity create(HttpEntity httpEntity);
    }

    @Inject
    public HttpNioFutureCommandExecutionHandler(java.util.logging.Logger logger, ConsumingNHttpEntityFactory entityFactory, ExecutorService executor, HttpNioFutureCommandConnectionRetry futureOperationRetry) {
        this.logger = new Logger(logger);
        this.executor = executor;
        this.entityFactory = entityFactory;
        this.futureOperationRetry = futureOperationRetry;
    }


    public void initalizeContext(HttpContext context, Object attachment) {
    }

    public HttpEntityEnclosingRequest submitRequest(HttpContext context) {
        HttpFutureCommand operation = (HttpFutureCommand) context.removeAttribute("operation");
        if (operation != null) {
            //TODO determine why type is lost
            HttpRequest object = (HttpRequest) operation.getRequest();
            return HttpNioUtils.convertToApacheRequest(object);
        }
        return null;

    }

    public ConsumingNHttpEntity responseEntity(HttpResponse response, HttpContext context) throws IOException {
        return entityFactory.create(response.getEntity());
    }


    public void handleResponse(HttpResponse response, HttpContext context) throws IOException {
        HttpNioFutureCommandConnectionHandle handle = (HttpNioFutureCommandConnectionHandle) context.removeAttribute("operation-handle");
        if (handle != null) {
            try {
                FutureCommand command = handle.getOperation();
                int code = response.getStatusLine().getStatusCode();
                //normal codes for rest commands
                if ((code >= 200 && code < 300) || code == 404) {
                    processResponse(response, command);
                } else {
                    if (isRetryable(response)) {
                        futureOperationRetry.shutdownConnectionAndRetryOperation((NHttpClientConnection) context.getAttribute(ExecutionContext.HTTP_CONNECTION));
                    } else {
                        operationFailed(command);
                    }
                }
            } finally {
                releaseConnectionToPool(handle);
            }
        } else {
            throw new IllegalStateException(String.format("No operation-handle associated with operation %1s", context));
        }
    }

    protected boolean isRetryable(HttpResponse response) throws IOException {
        int code = response.getStatusLine().getStatusCode();
        return code == 500 || code == 503;
    }

    protected void releaseConnectionToPool(HttpNioFutureCommandConnectionHandle handle) {
        try {
            handle.release();
        } catch (InterruptedException e) {
            logger.error(e, "Interrupted releasing handle %1s", handle);
        }
    }

    protected void operationFailed(FutureCommand command) throws IOException {
        String message = String.format("command failed: %1s", command);
        logger.error(message);
        command.getResponseFuture().setException(new IOException(message));
    }

    protected void processResponse(HttpResponse apacheResponse, FutureCommand command) throws IOException {
        org.jclouds.http.HttpResponse response = HttpNioUtils.convertToJavaCloudsResponse(apacheResponse);
        command.getResponseFuture().setResponse(response);
        logger.trace("submitting response task %1s", command.getResponseFuture());
        executor.submit(command.getResponseFuture());
    }

    public void finalizeContext(HttpContext context) {
        HttpNioFutureCommandConnectionHandle handle = (HttpNioFutureCommandConnectionHandle) context.removeAttribute("operation-handle");
        if (handle != null) {
            try {
                handle.cancel();
            } catch (Exception e) {
                logger.error(e, "Error cancelling handle %1s", handle);
            }
        }
    }
}