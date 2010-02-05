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
package org.jclouds.http.apachehc;

import java.io.File;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import javax.annotation.Resource;
import javax.inject.Singleton;
import javax.ws.rs.core.HttpHeaders;

import org.apache.http.Header;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpVersion;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHttpEntityEnclosingRequest;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.Payload;
import org.jclouds.logging.Logger;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class ApacheHCUtils {
   public static final String USER_AGENT = "jclouds/1.0 httpclient/4.0.1";

   @Resource
   protected Logger logger = Logger.NULL;

   public HttpEntityEnclosingRequest convertToApacheRequest(HttpRequest request) {

      String uri = request.getEndpoint().toASCIIString();
      if (request.getEndpoint().getQuery() != null)
         uri += "?" + request.getEndpoint().getQuery();
      BasicHttpEntityEnclosingRequest apacheRequest = new BasicHttpEntityEnclosingRequest(request
               .getMethod(), uri, HttpVersion.HTTP_1_1);

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

   public void addEntityForContent(HttpEntityEnclosingRequest apacheRequest, Object content,
            String contentType, long length) {
      if (content instanceof InputStream) {
         InputStream inputStream = (InputStream) content;
         if (length == -1)
            throw new IllegalArgumentException(
                     "you must specify size when content is an InputStream");
         InputStreamEntity Entity = new InputStreamEntity(inputStream, length);
         Entity.setContentType(contentType);
         apacheRequest.setEntity(Entity);
      } else if (content instanceof String) {
         StringEntity nStringEntity = null;
         try {
            nStringEntity = new StringEntity((String) content);
         } catch (UnsupportedEncodingException e) {
            throw new UnsupportedOperationException("Encoding not supported", e);
         }
         nStringEntity.setContentType(contentType);
         apacheRequest.setEntity(nStringEntity);
      } else if (content instanceof File) {
         apacheRequest.setEntity(new FileEntity((File) content, contentType));
      } else if (content instanceof byte[]) {
         ByteArrayEntity Entity = new ByteArrayEntity((byte[]) content);
         Entity.setContentType(contentType);
         apacheRequest.setEntity(Entity);
      } else {
         throw new UnsupportedOperationException("Content class not supported: "
                  + content.getClass().getName());
      }
      assert (apacheRequest.getEntity() != null);
   }

   public HttpResponse convertToJCloudsResponse(org.apache.http.HttpResponse apacheResponse)
            throws IOException {
      HttpResponse response = new HttpResponse();
      if (apacheResponse.getEntity() != null) {
         response
                  .setContent(new ConsumeOnCloseInputStream(apacheResponse.getEntity().getContent()));
      }
      for (Header header : apacheResponse.getAllHeaders()) {
         response.getHeaders().put(header.getName(), header.getValue());
      }
      response.setStatusCode(apacheResponse.getStatusLine().getStatusCode());
      response.setMessage(apacheResponse.getStatusLine().getReasonPhrase());
      return response;
   }

   class ConsumeOnCloseInputStream extends FilterInputStream {

      protected ConsumeOnCloseInputStream(InputStream in) {
         super(in);
      }

      boolean closed;

      @Override
      public void close() throws IOException {
         try {
            if (!closed) {
               int result = 0;
               while (result != -1) {
                  result = read();
               }
            }
         } catch (IOException e) {
            logger.warn(e, "error reading stream");
         } finally {
            closed = true;
            super.close();
         }
      }

   }

}