/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
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
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import javax.inject.Singleton;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.core.HttpHeaders;

import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.entity.StringEntity;
import org.jclouds.http.HttpRequest;
import org.jclouds.io.Payload;
import org.jclouds.io.payloads.BasePayload;
import org.jclouds.io.payloads.ByteArrayPayload;
import org.jclouds.io.payloads.DelegatingPayload;
import org.jclouds.io.payloads.FilePayload;
import org.jclouds.io.payloads.StringPayload;

import com.google.common.base.Throwables;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class ApacheHCUtils {

   public static final String USER_AGENT = "jclouds/1.0 httpclient/4.0.1";

   public static HttpUriRequest convertToApacheRequest(HttpRequest request) {
      HttpUriRequest apacheRequest;
      if (request.getMethod().equals(HttpMethod.HEAD)) {
         apacheRequest = new HttpHead(request.getEndpoint());
      } else if (request.getMethod().equals(HttpMethod.GET)) {
         apacheRequest = new HttpGet(request.getEndpoint());
      } else if (request.getMethod().equals(HttpMethod.DELETE)) {
         apacheRequest = new HttpDelete(request.getEndpoint());
      } else if (request.getMethod().equals(HttpMethod.PUT)) {
         apacheRequest = new HttpPut(request.getEndpoint());
      } else if (request.getMethod().equals(HttpMethod.POST)) {
         apacheRequest = new HttpPost(request.getEndpoint());
      } else {
         final String method = request.getMethod();
         if (request.getPayload() != null)
            apacheRequest = new HttpEntityEnclosingRequestBase() {

               @Override
               public String getMethod() {
                  return method;
               }

            };
         else
            apacheRequest = new HttpRequestBase() {

               @Override
               public String getMethod() {
                  return method;
               }

            };
         HttpRequestBase.class.cast(apacheRequest).setURI(request.getEndpoint());
      }
      Payload payload = request.getPayload();

      // Since we may remove headers, ensure they are added to the apache
      // request after this block
      if (apacheRequest instanceof HttpEntityEnclosingRequest) {
         if (payload != null) {
            addEntityForContent(HttpEntityEnclosingRequest.class.cast(apacheRequest), payload);
         }
      } else {
         apacheRequest.addHeader(HttpHeaders.CONTENT_LENGTH, "0");
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

   public static void addEntityForContent(HttpEntityEnclosingRequest apacheRequest, Payload payload) {
      payload = payload instanceof DelegatingPayload ? DelegatingPayload.class.cast(payload).getDelegate() : payload;
      if (payload instanceof StringPayload) {
         StringEntity nStringEntity = null;
         try {
            nStringEntity = new StringEntity((String) payload.getRawContent());
         } catch (UnsupportedEncodingException e) {
            throw new UnsupportedOperationException("Encoding not supported", e);
         }
         nStringEntity.setContentType(payload.getContentType());
         apacheRequest.setEntity(nStringEntity);
      } else if (payload instanceof FilePayload) {
         apacheRequest.setEntity(new FileEntity((File) payload.getRawContent(), payload.getContentType()));
      } else if (payload instanceof ByteArrayPayload) {
         ByteArrayEntity Entity = new ByteArrayEntity((byte[]) payload.getRawContent());
         Entity.setContentType(payload.getContentType());
         apacheRequest.setEntity(Entity);
      } else {
         InputStream inputStream = payload.getInput();
         if (payload.getContentLength() == null)
            throw new IllegalArgumentException("you must specify size when content is an InputStream");
         InputStreamEntity Entity = new InputStreamEntity(inputStream, payload.getContentLength());
         Entity.setContentType(payload.getContentType());
         apacheRequest.setEntity(Entity);
      }
      if (payload.getContentDisposition() != null)
         apacheRequest.addHeader("Content-Disposition", payload.getContentDisposition());
      if (payload.getContentEncoding() != null)
         apacheRequest.addHeader("Content-Encoding", payload.getContentEncoding());
      if (payload.getContentLanguage() != null)
         apacheRequest.addHeader("Content-Language", payload.getContentLanguage());
      assert (apacheRequest.getEntity() != null);
   }

   public static class HttpEntityPayload extends BasePayload<HttpEntity> {

      HttpEntityPayload(HttpEntity content) {
         super(content, content.getContentType().getValue(), content.getContentLength(), null);
      }

      @Override
      public InputStream getInput() {
         try {
            return content.getContent();
         } catch (IllegalStateException e) {
            Throwables.propagate(e);
         } catch (IOException e) {
            Throwables.propagate(e);
         }
         return null;
      }

      @Override
      public boolean isRepeatable() {
         return content.isRepeatable();
      }

      @Override
      public void release() {
         try {
            content.consumeContent();
         } catch (IOException e) {
         }
      }

      @Override
      public void writeTo(OutputStream outstream) throws IOException {
         super.writeTo(outstream);
      }

   }

}