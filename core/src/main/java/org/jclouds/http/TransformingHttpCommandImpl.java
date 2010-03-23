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
package org.jclouds.http;

import java.util.Collections;
import java.util.concurrent.ExecutionException;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Provider;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.UriBuilder;

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

   private final TransformingHttpCommandExecutorService executorService;
   private final Function<HttpResponse, T> transformer;
   private final Provider<UriBuilder> uriBuilderProvider;

   private GeneratedHttpRequest<?> request;
   private volatile int failureCount;

   @Resource
   protected Logger logger = Logger.NULL;

   private volatile int redirectCount;
   protected volatile Exception exception;

   @Inject
   public TransformingHttpCommandImpl(Provider<UriBuilder> uriBuilderProvider,
            TransformingHttpCommandExecutorService executorService,
            GeneratedHttpRequest<?> request, Function<HttpResponse, T> transformer) {
      this.uriBuilderProvider = uriBuilderProvider;
      this.request = request;
      this.executorService = executorService;
      this.transformer = transformer;
      this.failureCount = 0;
   }

   public ListenableFuture<T> execute() throws ExecutionException {
      if (exception != null)
         throw new ExecutionException(exception);
      return executorService.submit(this, transformer);
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
   @Override
   public void changeSchemeHostAndPortTo(String scheme, String host, int port) {
      UriBuilder builder = uriBuilderProvider.get().uri(request.getEndpoint());
      builder.scheme(scheme);
      builder.host(host);
      builder.port(port);
      request.setEndpoint(builder.build());
      request.getHeaders().replaceValues(HttpHeaders.HOST, Collections.singletonList(host));
   }

   /**
    * in some scenarios, HEAD commands cannot be redirected. This method changes the request to GET
    * in such a case.
    * 
    */
   public void changeToGETRequest() {
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
      return (request.getPayload() == null) ? true : request.getPayload().isRepeatable();
   }

   public HttpRequest getRequest() {
      return request;
   }

   @Override
   public void changePathTo(String newPath) {
      request.replacePath(newPath);
   }

   @Override
   public String toString() {
      return "[request=" + request.getRequestLine() + "]";
   }

}
