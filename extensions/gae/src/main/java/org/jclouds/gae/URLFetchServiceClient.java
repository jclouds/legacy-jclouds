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
import java.net.URI;
import java.net.URL;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.jclouds.http.HttpConstants;
import org.jclouds.http.HttpFutureCommandClient;
import org.jclouds.http.HttpHeaders;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.handlers.DelegatingErrorHandler;
import org.jclouds.http.handlers.DelegatingRetryHandler;
import org.jclouds.http.internal.BaseHttpFutureCommandClient;

import com.google.appengine.api.urlfetch.FetchOptions;
import com.google.appengine.api.urlfetch.HTTPHeader;
import com.google.appengine.api.urlfetch.HTTPMethod;
import com.google.appengine.api.urlfetch.HTTPRequest;
import com.google.appengine.api.urlfetch.HTTPResponse;
import com.google.appengine.api.urlfetch.URLFetchService;
import com.google.common.annotations.VisibleForTesting;
import com.google.inject.Inject;

/**
 * Google App Engine version of {@link HttpFutureCommandClient}
 * 
 * @author Adrian Cole
 */
public class URLFetchServiceClient extends BaseHttpFutureCommandClient<HTTPRequest> {
   private final URLFetchService urlFetchService;

   @Inject
   public URLFetchServiceClient(URLFetchService urlFetchService,
            DelegatingRetryHandler retryHandler, DelegatingErrorHandler errorHandler) {
      super(retryHandler, errorHandler);
      this.urlFetchService = urlFetchService;
   }

   /**
    * byte [] content is replayable and the only content type supportable by GAE. As such, we
    * convert the original request content to a byte array.
    */
   @VisibleForTesting
   void changeRequestContentToBytes(HttpRequest request) throws IOException {
      Object content = request.getPayload();
      if (content == null || content instanceof byte[]) {
         return;
      } else if (content instanceof String) {
         String string = (String) content;
         request.setPayload(string.getBytes());
      } else if (content instanceof InputStream || content instanceof File) {
         InputStream i = content instanceof InputStream ? (InputStream) content
                  : new FileInputStream((File) content);
         try {
            request.setPayload(IOUtils.toByteArray(i));
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

      convertHostHeaderToEndPoint(request);

      URL url = new URL(request.getEndPoint().toURL(), request.getUri());

      FetchOptions options = disallowTruncate();
      followRedirectsUnlessRequestContainsPayload(request, options);

      HTTPRequest gaeRequest = new HTTPRequest(url, HTTPMethod.valueOf(request.getMethod()
               .toString()), options);

      for (String header : request.getHeaders().keySet()) {
         for (String value : request.getHeaders().get(header)) {
            gaeRequest.addHeader(new HTTPHeader(header, value));
         }
      }

      if (request.getPayload() != null) {
         changeRequestContentToBytes(request);
         gaeRequest.setPayload((byte[]) request.getPayload());
      } else {
         gaeRequest.addHeader(new HTTPHeader(HttpConstants.CONTENT_LENGTH, "0"));
      }
      return gaeRequest;
   }

   /**
    * As host headers are not supported in GAE/J v1.2.1, we'll change the hostname of the
    * destination to the same value as the host header
    * 
    * @param request
    */
   @VisibleForTesting
   void convertHostHeaderToEndPoint(HttpRequest request) {

      String hostHeader = request.getFirstHeaderOrNull(HttpConstants.HOST);

      if (hostHeader != null) {
         request.setEndPoint(URI.create(String.format("%1$s://%2$s:%3$d", request.getEndPoint()
                  .getScheme(), hostHeader, request.getEndPoint().getPort())));
         request.getHeaders().removeAll(HttpHeaders.HOST);
      }
   }

   private void followRedirectsUnlessRequestContainsPayload(HttpRequest request,
            FetchOptions options) {
      if (request.getPayload() != null || request.getMethod().equals(HTTPMethod.PUT)
               || request.getMethod().equals(HTTPMethod.POST))
         options.doNotFollowRedirects();
      else
         options.followRedirects();
   }

   /**
    * nothing to clean up.
    */
   @Override
   protected void cleanup(HTTPRequest nativeRequest) {
   }

   @Override
   protected HttpResponse invoke(HTTPRequest request) throws IOException {
      if (logger.isTraceEnabled())
         logger.trace("%1$s - submitting request %2$s, headers: %3$s", request.getURL().getHost(),
                  request.getURL(), headersAsString(request.getHeaders()));
      HTTPResponse response = urlFetchService.fetch(request);
      if (logger.isTraceEnabled())
         logger.info("%1$s - received response code %2$s, headers: %3$s", request.getURL()
                  .getHost(), response.getResponseCode(), headersAsString(response.getHeaders()));
      return convert(response);
   }

   String headersAsString(List<HTTPHeader> headers) {
      StringBuilder builder = new StringBuilder("");
      for (HTTPHeader header : headers)
         builder.append("[").append(header.getName()).append("=").append(header.getValue()).append(
                  "],");
      return builder.toString();
   }

}
