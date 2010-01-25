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
package org.jclouds.gae;

import static com.google.appengine.api.urlfetch.FetchOptions.Builder.disallowTruncate;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.concurrent.ExecutorService;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.ws.rs.core.HttpHeaders;

import org.jclouds.Constants;
import org.jclouds.concurrent.SingleThreaded;
import org.jclouds.http.HttpCommand;
import org.jclouds.http.HttpCommandExecutorService;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.Payload;
import org.jclouds.http.handlers.DelegatingErrorHandler;
import org.jclouds.http.handlers.DelegatingRetryHandler;
import org.jclouds.http.internal.BaseHttpCommandExecutorService;
import org.jclouds.http.internal.HttpWire;
import org.jclouds.http.payloads.ByteArrayPayload;
import org.jclouds.http.payloads.FilePayload;
import org.jclouds.http.payloads.InputStreamPayload;
import org.jclouds.http.payloads.StringPayload;

import com.google.appengine.api.urlfetch.FetchOptions;
import com.google.appengine.api.urlfetch.HTTPHeader;
import com.google.appengine.api.urlfetch.HTTPMethod;
import com.google.appengine.api.urlfetch.HTTPRequest;
import com.google.appengine.api.urlfetch.HTTPResponse;
import com.google.appengine.api.urlfetch.URLFetchService;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.io.ByteStreams;
import com.google.common.io.Closeables;
import com.google.common.util.concurrent.ListenableFuture;

/**
 * Google App Engine version of {@link HttpCommandExecutorService}
 * 
 * @author Adrian Cole
 */
@SingleThreaded
@Singleton
public class GaeHttpCommandExecutorService extends BaseHttpCommandExecutorService<HTTPRequest> {
   public static final String USER_AGENT = "jclouds/1.0 urlfetch/1.3.0";

   private final URLFetchService urlFetchService;

   @Inject
   public GaeHttpCommandExecutorService(URLFetchService urlFetchService,
            @Named(Constants.PROPERTY_IO_WORKER_THREADS) ExecutorService ioExecutor,
            @Named(Constants.PROPERTY_USER_THREADS) ExecutorService userExecutor,
            DelegatingRetryHandler retryHandler, DelegatingErrorHandler errorHandler, HttpWire wire) {
      super(ioExecutor, userExecutor, retryHandler, errorHandler, wire);
      this.urlFetchService = urlFetchService;
   }

   @Override
   public ListenableFuture<HttpResponse> submit(HttpCommand command) {
      convertHostHeaderToEndPoint(command);
      return super.submit(command);
   }

   /**
    * byte [] content is replayable and the only content type supportable by GAE. As such, we
    * convert the original request content to a byte array.
    */
   @VisibleForTesting
   void changeRequestContentToBytes(HttpRequest request) throws IOException {
      Payload content = request.getPayload();
      if (content == null || content instanceof ByteArrayPayload) {
         return;
      } else if (content instanceof StringPayload) {
         String string = ((StringPayload) content).getRawContent();
         request.setPayload(string.getBytes());
      } else if (content instanceof InputStreamPayload || content instanceof FilePayload) {
         InputStream i = content.getContent();
         try {
            request.setPayload(ByteStreams.toByteArray(i));
         } finally {
            Closeables.closeQuietly(i);
         }
      } else {
         throw new UnsupportedOperationException("Content not supported " + content.getClass());
      }

   }

   @VisibleForTesting
   protected HttpResponse convert(HTTPResponse gaeResponse) {
      HttpResponse response = new HttpResponse();
      response.setStatusCode(gaeResponse.getResponseCode());
      for (HTTPHeader header : gaeResponse.getHeaders()) {
         response.getHeaders().put(header.getName(), header.getValue());
      }
      if (gaeResponse.getContent() != null) {
         response.setContent(new ByteArrayInputStream(gaeResponse.getContent()));
      }
      return response;
   }

   @VisibleForTesting
   protected HTTPRequest convert(HttpRequest request) throws IOException {

      URL url = request.getEndpoint().toURL();

      FetchOptions options = disallowTruncate();
      options.doNotFollowRedirects();

      HTTPRequest gaeRequest = new HTTPRequest(url, HTTPMethod.valueOf(request.getMethod()
               .toString()), options);

      for (String header : request.getHeaders().keySet()) {
         for (String value : request.getHeaders().get(header)) {
            gaeRequest.addHeader(new HTTPHeader(header, value));
         }
      }
      gaeRequest.addHeader(new HTTPHeader(HttpHeaders.USER_AGENT, USER_AGENT));

      if (request.getPayload() != null) {
         changeRequestContentToBytes(request);
         gaeRequest.setPayload(((ByteArrayPayload) request.getPayload()).getRawContent());
      } else {
         gaeRequest.addHeader(new HTTPHeader(HttpHeaders.CONTENT_LENGTH, "0"));
      }
      return gaeRequest;
   }

   /**
    * As host headers are not supported in GAE/J v1.2.1, we'll change the hostname of the
    * destination to the same value as the host header
    * 
    * @param command
    */
   @VisibleForTesting
   public static void convertHostHeaderToEndPoint(HttpCommand command) {
      HttpRequest request = command.getRequest();
      String hostHeader = request.getFirstHeaderOrNull(HttpHeaders.HOST);
      if (hostHeader != null) {
         command.changeHostAndPortTo(hostHeader, request.getEndpoint().getPort());
      }
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
}
