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
package org.jclouds.http;

import java.io.InputStream;
import java.util.Collections;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.UriBuilder;

import org.jclouds.logging.Logger;
import org.jclouds.rest.internal.GeneratedHttpRequest;

import com.google.common.base.Function;
import com.google.inject.internal.Nullable;

/**
 * Executor which will invoke and transform the response of an {@code EndpointCommand} into generic
 * type <T>.
 * 
 * @see TransformingEndpointCommand
 * 
 * @author Adrian Cole
 */
public class TransformingHttpCommandImpl<T> implements TransformingHttpCommand<T> {

   private final TransformingHttpCommandExecutorService executorService;
   private final Function<HttpResponse, T> transformer;
   private final Function<Exception, T> exceptionTransformer;

   private GeneratedHttpRequest<?> request;
   private volatile int failureCount;

   @Resource
   protected Logger logger = Logger.NULL;

   private volatile int redirectCount;
   protected volatile Exception exception;

   @Inject
   public TransformingHttpCommandImpl(TransformingHttpCommandExecutorService executorService,
            GeneratedHttpRequest<?> request, Function<HttpResponse, T> transformer,
            @Nullable Function<Exception, T> exceptionTransformer) {
      this.request = request;
      this.executorService = executorService;
      this.transformer = transformer;
      this.exceptionTransformer = exceptionTransformer;
      this.failureCount = 0;
   }

   public Future<T> execute() throws ExecutionException {
      if (exception != null)
         throw new ExecutionException(exception);
      return executorService.submit(this, transformer, exceptionTransformer);
   }

   public int getFailureCount() {
      return failureCount;
   }

   public int incrementFailureCount() {
      return ++failureCount;
   }

   /**
    * {@inheritDoc}
    * <p />
    * This also removes the Host header in order to avoid ssl problems.
    */
   public void redirect(String host, int port) {
      UriBuilder builder = UriBuilder.fromUri(request.getEndpoint());
      builder.host(host);
      builder.port(port);
      request.setEndpoint(builder.build());
      request.getHeaders().replaceValues(HttpHeaders.HOST, Collections.singletonList(host));
   }

   /**
    * mutable for redirects
    * 
    * @param method
    */
   public void redirectAsGet() {
      request.setMethod(HttpMethod.GET);
   }

   public void setException(Exception exception) {
      this.exception = exception;
   }

   public Exception getException() {
      return exception;
   }

   public int incrementRedirectCount() {
      return ++redirectCount;
   }

   public int getRedirectCount() {
      return redirectCount;
   }

   public boolean isReplayable() {
      Object content = request.getEntity();
      if (content != null && content instanceof InputStream) {
         logger.warn("%1$s: InputStreams are not replayable", toString());
         return false;
      }
      return true;
   }

   /**
    * public void checkCode() { int code = getResponse().getStatusCode(); if (code >= 300) {
    * IOUtils.closeQuietly(getResponse().getContent()); throw new
    * IllegalStateException("incorrect code for this operation: " + getResponse()); } }
    **/

   public HttpRequest getRequest() {
      return request;
   }

   @Override
   public void redirectPath(String newPath) {
      request.replacePath(newPath);
   }

}
