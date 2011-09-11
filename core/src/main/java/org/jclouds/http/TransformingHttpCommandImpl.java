/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
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
 */
package org.jclouds.http;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.concurrent.ExecutionException;

import javax.annotation.Resource;
import javax.inject.Inject;

import org.jclouds.logging.Logger;
import org.jclouds.rest.internal.GeneratedHttpRequest;

import com.google.common.base.Function;
import com.google.common.util.concurrent.ListenableFuture;

/**
 * Executor which will invoke and transform the response of an {@code EndpointCommand} into generic
 * type <T>.
 * 
 * @see TransformingHttpCommand
 * 
 * @author Adrian Cole
 */
public class TransformingHttpCommandImpl<T> implements TransformingHttpCommand<T> {

   protected final TransformingHttpCommandExecutorService executorService;
   protected final Function<HttpResponse, T> transformer;

   protected volatile HttpRequest request;
   protected volatile int failureCount;
   protected volatile int redirectCount;
   protected volatile Exception exception;

   @Resource
   protected Logger logger = Logger.NULL;

   @Inject
   public TransformingHttpCommandImpl(TransformingHttpCommandExecutorService executorService, HttpRequest request,
            Function<HttpResponse, T> transformer) {
      this.request = checkNotNull(request, "request");
      this.executorService = checkNotNull(executorService, "executorService");
      this.transformer = checkNotNull(transformer, "transformer");
      this.failureCount = 0;
      this.redirectCount = 0;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public ListenableFuture<T> execute() throws ExecutionException {
      if (exception != null)
         throw new ExecutionException(exception);
      return executorService.submit(this, transformer);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public int getFailureCount() {
      return failureCount;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public int incrementFailureCount() {
      return ++failureCount;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void setException(Exception exception) {
      this.exception = exception;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Exception getException() {
      return exception;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public int incrementRedirectCount() {
      return ++redirectCount;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public int getRedirectCount() {
      return redirectCount;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public boolean isReplayable() {
      return (request.getPayload() == null) ? true : request.getPayload().isRepeatable();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public HttpRequest getCurrentRequest() {
      return request;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void setCurrentRequest(HttpRequest request) {
      this.request = request;
   }

   @Override
   public String toString() {
      if (request instanceof GeneratedHttpRequest<?>)
         return String.format("[method=%s.%s, request=%s]", GeneratedHttpRequest.class.cast(request).getDeclaring()
                  .getSimpleName(), GeneratedHttpRequest.class.cast(request).getJavaMethod().getName(), request
                  .getRequestLine());
      else
         return "[request=" + request.getRequestLine() + "]";
   }

}
