/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.gae;

import static com.google.common.base.Throwables.propagate;
import static com.google.common.util.concurrent.Futures.transform;
import static com.google.common.util.concurrent.JdkFutureAdapters.listenInPoolThread;
import static org.jclouds.http.HttpUtils.checkRequestHasContentLengthOrChunkedEncoding;
import static org.jclouds.http.HttpUtils.wirePayloadIfEnabled;

import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.Constants;
import org.jclouds.JcloudsVersion;
import org.jclouds.http.HttpCommand;
import org.jclouds.http.HttpCommandExecutorService;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpRequestFilter;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.HttpResponseException;
import org.jclouds.http.HttpUtils;
import org.jclouds.http.IOExceptionRetryHandler;
import org.jclouds.http.handlers.DelegatingErrorHandler;
import org.jclouds.http.handlers.DelegatingRetryHandler;
import org.jclouds.http.internal.BaseHttpCommandExecutorService;
import org.jclouds.http.internal.HttpWire;
import org.jclouds.io.ContentMetadataCodec;
import org.jclouds.util.Throwables2;

import com.google.appengine.api.urlfetch.HTTPRequest;
import com.google.appengine.api.urlfetch.HTTPResponse;
import com.google.appengine.api.urlfetch.URLFetchService;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;

/**
 * Google App Engine version of {@link HttpCommandExecutorService} using their
 * fetchAsync call
 * 
 * @author Adrian Cole
 */
@Singleton
public class AsyncGaeHttpCommandExecutorService extends BaseHttpCommandExecutorService<HTTPRequest> {
   // TODO: look up gae version
   public static final String USER_AGENT = String.format("jclouds/%s urlfetch/%s", JcloudsVersion.get(), "1.6.5");

   private final URLFetchService urlFetchService;
   private final ConvertToGaeRequest convertToGaeRequest;
   private final ConvertToJcloudsResponse convertToJcloudsResponse;
   private final ListeningExecutorService ioExecutor;

   @Inject
   public AsyncGaeHttpCommandExecutorService(URLFetchService urlFetchService, HttpUtils utils,
         ContentMetadataCodec contentMetadataCodec,
         @Named(Constants.PROPERTY_IO_WORKER_THREADS) ListeningExecutorService ioExecutor,
         IOExceptionRetryHandler ioRetryHandler, DelegatingRetryHandler retryHandler,
         DelegatingErrorHandler errorHandler, HttpWire wire, ConvertToGaeRequest convertToGaeRequest,
         ConvertToJcloudsResponse convertToJcloudsResponse) {
      super(utils, contentMetadataCodec, ioExecutor, retryHandler, ioRetryHandler, errorHandler, wire);
      this.ioExecutor = ioExecutor;
      this.urlFetchService = urlFetchService;
      this.convertToGaeRequest = convertToGaeRequest;
      this.convertToJcloudsResponse = convertToJcloudsResponse;
   }

   @VisibleForTesting
   protected HttpResponse convert(HTTPResponse gaeResponse) {
      return convertToJcloudsResponse.apply(gaeResponse);
   }

   @VisibleForTesting
   protected HTTPRequest convert(HttpRequest request) throws IOException {
      return convertToGaeRequest.apply(request);
   }

   /**
    * nothing to clean up.
    */
   @Override
   protected void cleanup(HTTPRequest nativeRequest) {
   }

   @Override
   protected HttpResponse invoke(HTTPRequest request) throws IOException {
      return convert(urlFetchService.fetch(request));
   }

   public HTTPRequest filterLogAndConvertRe(HttpRequest request) {
      for (HttpRequestFilter filter : request.getFilters()) {
         request = filter.filter(request);
      }
      checkRequestHasContentLengthOrChunkedEncoding(request,
            "After filtering, the request has neither chunked encoding nor content length: " + request);
      logger.debug("Sending request %s: %s", request.hashCode(), request.getRequestLine());
      wirePayloadIfEnabled(wire, request);
      HTTPRequest nativeRequest = convertToGaeRequest.apply(request);
      utils.logRequest(headerLog, request, ">>");
      return nativeRequest;
   }

   @Override
   public ListenableFuture<HttpResponse> submit(final HttpCommand command) {
      HTTPRequest nativeRequest = filterLogAndConvertRe(command.getCurrentRequest());
      ListenableFuture<HttpResponse> response = transform(
            listenInPoolThread(urlFetchService.fetchAsync(nativeRequest)), convertToJcloudsResponse);

      return transform(response, new Function<HttpResponse, HttpResponse>() {
         public HttpResponse apply(HttpResponse response) {
            return receiveResponse(command, response);
         }

      }, ioExecutor);
   }

   private HttpResponse receiveResponse(HttpCommand command, HttpResponse response) {
      try {
         logger.debug("Receiving response %s: %s", command.getCurrentRequest().hashCode(), response.getStatusLine());
         utils.logResponse(headerLog, response, "<<");
         if (response.getPayload() != null && wire.enabled())
            wire.input(response);
         int statusCode = response.getStatusCode();
         if (statusCode >= 300) {
            if (shouldContinue(command, response))
               return submit(command).get();
            else
               return response;
         }
         return response;
      } catch (Exception e) {
         IOException ioe = Throwables2.getFirstThrowableOfType(e, IOException.class);
         if (ioe != null && ioRetryHandler.shouldRetryRequest(command, ioe)) {
            try {
               return submit(command).get();
            } catch (Exception e1) {
               command.setException(e1);
               return response;
            }
         } else {
            command.setException(new HttpResponseException(e.getMessage() + " connecting to "
                  + command.getCurrentRequest().getRequestLine(), command, null, e));
            return response;
         }
      } finally {
         if (command.getException() != null)
            propagate(command.getException());
      }
   }

   private boolean shouldContinue(HttpCommand command, HttpResponse response) {
      boolean shouldContinue = false;
      if (retryHandler.shouldRetryRequest(command, response)) {
         shouldContinue = true;
      } else {
         errorHandler.handleError(command, response);
      }
      return shouldContinue;
   }
}
