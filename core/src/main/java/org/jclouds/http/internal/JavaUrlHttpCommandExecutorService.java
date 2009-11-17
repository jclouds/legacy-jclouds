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
package org.jclouds.http.internal;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.ExecutorService;

import javax.inject.Named;
import javax.inject.Singleton;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;
import javax.ws.rs.core.HttpHeaders;

import org.apache.commons.io.IOUtils;
import org.jclouds.http.HttpCommandExecutorService;
import org.jclouds.http.HttpConstants;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.handlers.DelegatingErrorHandler;
import org.jclouds.http.handlers.DelegatingRetryHandler;

import com.google.common.collect.Maps;
import com.google.inject.Inject;

/**
 * Basic implementation of a {@link HttpCommandExecutorService}.
 * 
 * @author Adrian Cole
 */
@Singleton
public class JavaUrlHttpCommandExecutorService extends
         BaseHttpCommandExecutorService<HttpURLConnection> {

   @Inject(optional = true)
   @Named(HttpConstants.PROPERTY_HTTP_RELAX_HOSTNAME)
   private boolean relaxHostname = false;
   private final Map<String, String> sslMap;

   @Inject
   public JavaUrlHttpCommandExecutorService(ExecutorService executorService,
            DelegatingRetryHandler retryHandler, DelegatingErrorHandler errorHandler, HttpWire wire) {
      super(executorService, retryHandler, errorHandler, wire);
      sslMap = Maps.newHashMap();
   }

   /**
    * 
    * Used to get more information about HTTPS hostname wrong errors.
    * 
    * @author Adrian Cole
    */
   class LogToMapHostnameVerifier implements HostnameVerifier {

      public boolean verify(String hostname, SSLSession session) {
         logger.warn("hostname was %s while session was %s", hostname, session.getPeerHost());
         sslMap.put(hostname, session.getPeerHost());
         return true;
      }
   }

   @Override
   protected HttpResponse invoke(HttpURLConnection connection) throws IOException {
      HttpResponse response = new HttpResponse();
      InputStream in;
      try {
         in = connection.getInputStream();
      } catch (IOException e) {
         in = connection.getErrorStream();
      }
      if (in != null) {
         response.setContent(in);
      }
      for (String header : connection.getHeaderFields().keySet()) {
         response.getHeaders().putAll(header, connection.getHeaderFields().get(header));
      }
      response.setStatusCode(connection.getResponseCode());
      response.setMessage(connection.getResponseMessage());
      return response;
   }

   @Override
   protected HttpURLConnection convert(HttpRequest request) throws IOException {
      URL url = request.getEndpoint().toURL();
      HttpURLConnection connection = (HttpURLConnection) url.openConnection();
      if (relaxHostname && connection instanceof HttpsURLConnection) {
         HttpsURLConnection sslCon = (HttpsURLConnection) connection;
         sslCon.setHostnameVerifier(new LogToMapHostnameVerifier());
      }
      connection.setDoOutput(true);
      connection.setAllowUserInteraction(false);
      // do not follow redirects since https redirects don't work properly
      // ex. Caused by: java.io.IOException: HTTPS hostname wrong: should be
      // <adriancole.s3int0.s3-external-3.amazonaws.com>
      connection.setInstanceFollowRedirects(false);
      connection.setRequestMethod(request.getMethod().toString());
      for (String header : request.getHeaders().keySet()) {
         for (String value : request.getHeaders().get(header)) {
            connection.setRequestProperty(header, value);

            if ("Transfer-Encoding".equals(header) && "chunked".equals(value)) {
               connection.setChunkedStreamingMode(8192);
            }
         }
      }
      connection.setRequestProperty(HttpHeaders.HOST, request.getEndpoint().getHost());
      if (request.getEntity() != null) {
         OutputStream out = connection.getOutputStream();
         try {
            if (request.getEntity() instanceof String) {
               OutputStreamWriter writer = new OutputStreamWriter(out);
               writer.write((String) request.getEntity());
               writer.close();
            } else if (request.getEntity() instanceof InputStream) {
               IOUtils.copy((InputStream) request.getEntity(), out);
            } else if (request.getEntity() instanceof File) {
               IOUtils.copy(new FileInputStream((File) request.getEntity()), out);
            } else if (request.getEntity() instanceof byte[]) {
               IOUtils.write((byte[]) request.getEntity(), out);
            } else {
               throw new UnsupportedOperationException("Content not supported "
                        + request.getEntity().getClass());
            }
         } finally {
            IOUtils.closeQuietly(out);
         }
      } else {
         connection.setRequestProperty(HttpHeaders.CONTENT_LENGTH, "0");
      }
      return connection;
   }

   /**
    * Only disconnect if there is no content, as disconnecting will throw away unconsumed content.
    */
   @Override
   protected void cleanup(HttpURLConnection connection) {
      if (connection != null && connection.getContentLength() == 0)
         connection.disconnect();
   }

}
