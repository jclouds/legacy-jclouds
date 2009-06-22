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
package org.jclouds.http.internal;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.commons.io.IOUtils;
import org.jclouds.http.HttpConstants;
import org.jclouds.http.HttpFutureCommandClient;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;

/**
 * Basic implementation of a {@link HttpFutureCommandClient}.
 * 
 * @author Adrian Cole
 */
public class JavaUrlHttpFutureCommandClient extends BaseHttpFutureCommandClient<HttpURLConnection> {

   @Override
   protected HttpResponse invoke(HttpURLConnection connection) throws IOException {
      HttpResponse response = new HttpResponse();
      if (logger.isTraceEnabled())
         logger.trace("%1$s - submitting request %2$s, headers: %3$s", connection.getURL()
                  .getHost(), connection.getURL(), connection.getRequestProperties());
      InputStream in;
      try {
         in = connection.getInputStream();
      } catch (IOException e) {
         in = connection.getErrorStream();
      }
      if (logger.isTraceEnabled())
         logger.info("%1$s - received response code %2$s, headers: %3$s", connection.getURL()
                  .getHost(), connection.getResponseCode(), connection.getHeaderFields());
      if (in != null) {
         response.setContent(in);
      }
      response.setStatusCode(connection.getResponseCode());
      for (String header : connection.getHeaderFields().keySet()) {
         response.getHeaders().putAll(header, connection.getHeaderFields().get(header));
      }

      response.setMessage(connection.getResponseMessage());
      return response;
   }

   @Override
   protected HttpURLConnection convert(HttpRequest request) throws IOException {
      URL url = new URL(request.getEndPoint().toURL(), request.getUri());
      HttpURLConnection connection = (HttpURLConnection) url.openConnection();
      connection.setDoOutput(true);
      connection.setAllowUserInteraction(false);
      connection.setInstanceFollowRedirects(true);
      connection.setRequestMethod(request.getMethod().toString());
      for (String header : request.getHeaders().keySet()) {
         for (String value : request.getHeaders().get(header))
            connection.setRequestProperty(header, value);
      }
      if (request.getPayload() != null) {
         OutputStream out = connection.getOutputStream();
         try {
            if (request.getPayload() instanceof String) {
               OutputStreamWriter writer = new OutputStreamWriter(out);
               writer.write((String) request.getPayload());
               writer.close();
            } else if (request.getPayload() instanceof InputStream) {
               IOUtils.copy((InputStream) request.getPayload(), out);
            } else if (request.getPayload() instanceof File) {
               IOUtils.copy(new FileInputStream((File) request.getPayload()), out);
            } else if (request.getPayload() instanceof byte[]) {
               IOUtils.write((byte[]) request.getPayload(), out);
            } else {
               throw new UnsupportedOperationException("Content not supported "
                        + request.getPayload().getClass());
            }
         } finally {
            IOUtils.closeQuietly(out);
         }

      } else {
         connection.setRequestProperty(HttpConstants.CONTENT_LENGTH, "0");
      }
      return connection;
   }

   /**
    * Only disconnect if there is no content, as disconnecting will throw away unconsumed content.
    */
   @Override
   protected void cleanup(HttpURLConnection connection) {
      if (connection.getContentLength() == 0)
         connection.disconnect();
   }

}
