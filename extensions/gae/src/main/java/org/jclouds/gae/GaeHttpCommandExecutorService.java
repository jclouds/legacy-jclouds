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
package org.jclouds.gae;

import static com.google.appengine.api.urlfetch.FetchOptions.Builder.disallowTruncate;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import javax.inject.Inject;
import javax.ws.rs.core.HttpHeaders;

import org.apache.commons.io.IOUtils;
import org.jclouds.concurrent.SingleThreaded;
import org.jclouds.http.HttpCommand;
import org.jclouds.http.HttpCommandExecutorService;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.handlers.DelegatingErrorHandler;
import org.jclouds.http.handlers.DelegatingRetryHandler;
import org.jclouds.http.internal.BaseHttpCommandExecutorService;
import org.jclouds.http.internal.Wire;

import com.google.appengine.api.urlfetch.FetchOptions;
import com.google.appengine.api.urlfetch.HTTPHeader;
import com.google.appengine.api.urlfetch.HTTPMethod;
import com.google.appengine.api.urlfetch.HTTPRequest;
import com.google.appengine.api.urlfetch.HTTPResponse;
import com.google.appengine.api.urlfetch.URLFetchService;
import com.google.common.annotations.VisibleForTesting;

/**
 * Google App Engine version of {@link HttpCommandExecutorService}
 * 
 * @author Adrian Cole
 */
@SingleThreaded
public class GaeHttpCommandExecutorService extends BaseHttpCommandExecutorService<HTTPRequest> {
   private final URLFetchService urlFetchService;

   @Inject
   public GaeHttpCommandExecutorService(URLFetchService urlFetchService,
            ExecutorService executorService, DelegatingRetryHandler retryHandler,
            DelegatingErrorHandler errorHandler, Wire wire) {
      super(executorService, retryHandler, errorHandler, wire);
      this.urlFetchService = urlFetchService;
   }

   @Override
   public Future<HttpResponse> submit(HttpCommand command) {
      convertHostHeaderToEndPoint(command);
      return super.submit(command);
   }

   /**
    * byte [] content is replayable and the only content type supportable by GAE. As such, we
    * convert the original request content to a byte array.
    */
   @VisibleForTesting
   void changeRequestContentToBytes(HttpRequest request) throws IOException {
      Object content = request.getEntity();
      if (content == null || content instanceof byte[]) {
         return;
      } else if (content instanceof String) {
         String string = (String) content;
         request.setEntity(string.getBytes());
      } else if (content instanceof InputStream || content instanceof File) {
         InputStream i = content instanceof InputStream ? (InputStream) content
                  : new FileInputStream((File) content);
         try {
            request.setEntity(IOUtils.toByteArray(i));
         } finally {
            IOUtils.closeQuietly(i);
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

      if (request.getEntity() != null) {
         changeRequestContentToBytes(request);
         gaeRequest.setPayload((byte[]) request.getEntity());
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
         command.setHostAndPort(hostHeader, request.getEndpoint().getPort());
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
