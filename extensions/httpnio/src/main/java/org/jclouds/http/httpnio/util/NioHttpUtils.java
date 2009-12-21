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
import org.jclouds.http.Payload;

/**
 * 
 * @author Adrian Cole
 */
public class NioHttpUtils {
   public static final String USER_AGENT = "jclouds/1.0 httpcore-nio/4.1-alpha1";

   public static HttpEntityEnclosingRequest convertToApacheRequest(HttpRequest request) {

      String path = request.getEndpoint().getRawPath();
      if (request.getEndpoint().getQuery() != null)
         path += "?" + request.getEndpoint().getQuery();
      BasicHttpEntityEnclosingRequest apacheRequest = new BasicHttpEntityEnclosingRequest(request
               .getMethod(), path, HttpVersion.HTTP_1_1);

      Payload payload = request.getPayload();

      // Since we may remove headers, ensure they are added to the apache
      // request after this block
      if (payload != null) {
         String lengthString = request.getFirstHeaderOrNull(HttpHeaders.CONTENT_LENGTH);
         if (lengthString == null) {
            throw new IllegalStateException("no Content-Length header on request: " + apacheRequest);
         }
         long contentLength = Long.parseLong(lengthString);
         String contentType = request.getFirstHeaderOrNull(HttpHeaders.CONTENT_TYPE);
         addEntityForContent(apacheRequest, payload.getRawContent(), contentType, contentLength);
      }

      for (String header : request.getHeaders().keySet()) {
         for (String value : request.getHeaders().get(header))
            // apache automatically tries to add content length header
            if (!header.equals(HttpHeaders.CONTENT_LENGTH))
               apacheRequest.addHeader(header, value);
      }
      apacheRequest.addHeader(HttpHeaders.USER_AGENT, USER_AGENT);

      return apacheRequest;
   }

   public static void addEntityForContent(BasicHttpEntityEnclosingRequest apacheRequest,
            Object content, String contentType, long length) {
      if (content instanceof InputStream) {
         InputStream inputStream = (InputStream) content;
         if (length == -1)
            throw new IllegalArgumentException(
                     "you must specify size when content is an InputStream");
         InputStreamEntity Entity = new InputStreamEntity(inputStream, length);
         Entity.setContentType(contentType);
         apacheRequest.setEntity(Entity);
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
         NByteArrayEntity Entity = new NByteArrayEntity((byte[]) content);
         Entity.setContentType(contentType);
         apacheRequest.setEntity(Entity);
      } else {
         throw new UnsupportedOperationException("Content class not supported: "
                  + content.getClass().getName());
      }
      assert (apacheRequest.getEntity() != null);
   }

   public static HttpResponse convertToJCloudsResponse(org.apache.http.HttpResponse apacheResponse)
            throws IOException {
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