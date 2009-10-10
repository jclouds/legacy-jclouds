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
package org.jclouds.http.httpnio.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import javax.ws.rs.core.HttpHeaders;

import org.apache.http.Header;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpVersion;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.message.BasicHttpEntityEnclosingRequest;
import org.apache.http.nio.entity.NByteArrayEntity;
import org.apache.http.nio.entity.NFileEntity;
import org.apache.http.nio.entity.NStringEntity;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;

public class NioHttpUtils {
   public static HttpEntityEnclosingRequest convertToApacheRequest(HttpRequest request) {

      String path = request.getEndpoint().getRawPath();
      if (request.getEndpoint().getQuery() != null)
         path += "?" + request.getEndpoint().getQuery();
      BasicHttpEntityEnclosingRequest apacheRequest = new BasicHttpEntityEnclosingRequest(request
               .getMethod(), path, HttpVersion.HTTP_1_1);

      Object content = request.getEntity();

      // Since we may remove headers, ensure they are added to the apache
      // request after this block
      if (content != null) {
         String lengthString = request.getFirstHeaderOrNull(HttpHeaders.CONTENT_LENGTH);
         if (lengthString == null) {
            throw new IllegalStateException("no Content-Length header on request: " + apacheRequest);
         }
         long contentLength = Long.parseLong(lengthString);
         String contentType = request.getFirstHeaderOrNull(HttpHeaders.CONTENT_TYPE);
         addEntityForContent(apacheRequest, content, contentType, contentLength);
      }

      for (String header : request.getHeaders().keySet()) {
         for (String value : request.getHeaders().get(header))
            // apache automatically tries to add content length header
            if (!header.equals(HttpHeaders.CONTENT_LENGTH))
               apacheRequest.addHeader(header, value);
      }
      return apacheRequest;
   }

   public static void addEntityForContent(BasicHttpEntityEnclosingRequest apacheRequest,
            Object content, String contentType, long length) {
      if (content instanceof InputStream) {
         InputStream inputStream = (InputStream) content;
         if (length == -1)
            throw new IllegalArgumentException(
                     "you must specify size when content is an InputStream");
         InputStreamEntity entity = new InputStreamEntity(inputStream, length);
         entity.setContentType(contentType);
         apacheRequest.setEntity(entity);
      } else if (content instanceof String) {
         NStringEntity nStringEntity = null;
         try {
            nStringEntity = new NStringEntity((String) content);
         } catch (UnsupportedEncodingException e) {
            throw new UnsupportedOperationException("Encoding not supported", e);
         }
         nStringEntity.setContentType(contentType);
         apacheRequest.setEntity(nStringEntity);
      } else if (content instanceof File) {
         apacheRequest.setEntity(new NFileEntity((File) content, contentType, true));
      } else if (content instanceof byte[]) {
         NByteArrayEntity entity = new NByteArrayEntity((byte[]) content);
         entity.setContentType(contentType);
         apacheRequest.setEntity(entity);
      } else {
         throw new UnsupportedOperationException("Content class not supported: "
                  + content.getClass().getName());
      }
   }

   public static HttpResponse convertToJavaCloudsResponse(
            org.apache.http.HttpResponse apacheResponse) throws IOException {
      HttpResponse response = new HttpResponse();
      if (apacheResponse.getEntity() != null) {
         response.setContent(apacheResponse.getEntity().getContent());
      }
      for (Header header : apacheResponse.getAllHeaders()) {
         response.getHeaders().put(header.getName(), header.getValue());
      }
      response.setStatusCode(apacheResponse.getStatusLine().getStatusCode());
      response.setMessage(apacheResponse.getStatusLine().getReasonPhrase());
      return response;
   }
}