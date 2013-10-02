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
package org.jclouds.http.internal;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Throwables.propagate;
import static com.google.common.io.ByteStreams.copy;
import static com.google.common.io.ByteStreams.nullOutputStream;
import static org.jclouds.http.HttpUtils.checkRequestHasContentLengthOrChunkedEncoding;
import static org.jclouds.http.HttpUtils.wirePayloadIfEnabled;
import static org.jclouds.util.Throwables2.getFirstThrowableOfType;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.Callable;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;

import org.jclouds.Constants;
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
import org.jclouds.io.ContentMetadataCodec;
import org.jclouds.logging.Logger;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;

/**
 * 
 * @author Adrian Cole
 */
public abstract class BaseHttpCommandExecutorService<Q> implements HttpCommandExecutorService {
   protected final HttpUtils utils;
   protected final ContentMetadataCodec contentMetadataCodec;

   protected final DelegatingRetryHandler retryHandler;
   protected final IOExceptionRetryHandler ioRetryHandler;
   protected final DelegatingErrorHandler errorHandler;
   protected final ListeningExecutorService ioExecutor;

   @Resource
   protected Logger logger = Logger.NULL;
   @Resource
   @Named(Constants.LOGGER_HTTP_HEADERS)
   protected Logger headerLog = Logger.NULL;

   protected final HttpWire wire;

   @Inject
   protected BaseHttpCommandExecutorService(HttpUtils utils, ContentMetadataCodec contentMetadataCodec,
         @Named(Constants.PROPERTY_IO_WORKER_THREADS) ListeningExecutorService ioExecutor,
         DelegatingRetryHandler retryHandler, IOExceptionRetryHandler ioRetryHandler,
         DelegatingErrorHandler errorHandler, HttpWire wire) {
      this.utils = checkNotNull(utils, "utils");
      this.contentMetadataCodec = checkNotNull(contentMetadataCodec, "contentMetadataCodec");
      this.retryHandler = checkNotNull(retryHandler, "retryHandler");
      this.ioRetryHandler = checkNotNull(ioRetryHandler, "ioRetryHandler");
      this.errorHandler = checkNotNull(errorHandler, "errorHandler");
      this.ioExecutor = checkNotNull(ioExecutor, "ioExecutor");
      this.wire = checkNotNull(wire, "wire");
   }

   public static InputStream consumeOnClose(InputStream in) {
      return new ConsumeOnCloseInputStream(in);
   }

   /**
    * Ensures the content is always flushed.
    * 
    */
   static class ConsumeOnCloseInputStream extends FilterInputStream {

      protected ConsumeOnCloseInputStream(InputStream in) {
         super(in);
      }

      boolean closed;

      @Override
      public void close() throws IOException {
         if (!closed) {
            try {
               copy(this, nullOutputStream());
            } catch (IOException e) {
            } finally {
               closed = true;
               super.close();
            }
         }
      }

      @Override
      protected void finalize() throws Throwable {
         close();
         super.finalize();
      }

   }

   @Override
   public HttpResponse invoke(HttpCommand command) {
      HttpResponse response = null;
      for (;;) {
         HttpRequest request = command.getCurrentRequest();
         Q nativeRequest = null;
         try {
            for (HttpRequestFilter filter : request.getFilters()) {
               request = filter.filter(request);
            }
            checkRequestHasContentLengthOrChunkedEncoding(request,
                  "After filtering, the request has neither chunked encoding nor content length: " + request);
            logger.debug("Sending request %s: %s", request.hashCode(), request.getRequestLine());
            wirePayloadIfEnabled(wire, request);
            utils.logRequest(headerLog, request, ">>");
            nativeRequest = convert(request);
            response = invoke(nativeRequest);

            logger.debug("Receiving response %s: %s", request.hashCode(), response.getStatusLine());
            utils.logResponse(headerLog, response, "<<");
            if (response.getPayload() != null && wire.enabled())
               wire.input(response);
            nativeRequest = null; // response took ownership of streams
            int statusCode = response.getStatusCode();
            if (statusCode >= 300) {
               if (shouldContinue(command, response))
                  continue;
               else
                  break;
            } else {
               break;
            }
         } catch (Exception e) {
            IOException ioe = getFirstThrowableOfType(e, IOException.class);
            if (ioe != null && ioRetryHandler.shouldRetryRequest(command, ioe)) {
               continue;
            }
            command.setException(new HttpResponseException(e.getMessage() + " connecting to "
                  + command.getCurrentRequest().getRequestLine(), command, null, e));
            break;

         } finally {
            cleanup(nativeRequest);
         }
      }
      if (command.getException() != null)
         throw propagate(command.getException());
      return response;
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

   @Override
   public ListenableFuture<HttpResponse> submit(HttpCommand command) {
      HttpRequest request = command.getCurrentRequest();
      checkRequestHasContentLengthOrChunkedEncoding(request,
            "if the request has a payload, it must be set to chunked encoding or specify a content length: " + request);
      return ioExecutor.submit(new HttpResponseCallable(command));
   }

   public class HttpResponseCallable implements Callable<HttpResponse> {
      private final HttpCommand command;

      public HttpResponseCallable(HttpCommand command) {
         this.command = command;
      }

      public HttpResponse call() throws Exception {
         try {
            return invoke(command);
         } finally {
            if (command.getException() != null)
               throw command.getException();
         }
      }

      @Override
      public String toString() {
         return command.toString();
      }

   }

   protected abstract Q convert(HttpRequest request) throws IOException, InterruptedException;

   protected abstract HttpResponse invoke(Q nativeRequest) throws IOException, InterruptedException;

   protected abstract void cleanup(Q nativeResponse);

}
