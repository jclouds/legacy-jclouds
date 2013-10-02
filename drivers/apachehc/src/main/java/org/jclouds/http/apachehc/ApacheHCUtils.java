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
package org.jclouds.http.apachehc;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.Set;

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
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.util.EntityUtils;
import org.jclouds.JcloudsVersion;
import org.jclouds.http.HttpRequest;
import org.jclouds.io.ContentMetadataCodec;
import org.jclouds.io.MutableContentMetadata;
import org.jclouds.io.Payload;
import org.jclouds.io.payloads.BasePayload;
import org.jclouds.io.payloads.ByteArrayPayload;
import org.jclouds.io.payloads.DelegatingPayload;
import org.jclouds.io.payloads.FilePayload;
import org.jclouds.io.payloads.StringPayload;

import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableSet;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class ApacheHCUtils {
   //TODO: look up httpclient version
   public static final String USER_AGENT = String.format("jclouds/%s httpclient/%s", JcloudsVersion.get(), "4.1.1");
   
   private final ContentMetadataCodec contentMetadataCodec;

   public ApacheHCUtils(ContentMetadataCodec contentMetadataCodec) {
      this.contentMetadataCodec = contentMetadataCodec;
   }
   
   public HttpUriRequest convertToApacheRequest(HttpRequest request) {
      HttpUriRequest apacheRequest;
      if (request.getMethod().equals(HttpMethod.HEAD)) {
         apacheRequest = new HttpHead(request.getEndpoint());
      } else if (request.getMethod().equals(HttpMethod.GET)) {
         apacheRequest = new HttpGet(request.getEndpoint());
      } else if (request.getMethod().equals(HttpMethod.DELETE)) {
         apacheRequest = new HttpDelete(request.getEndpoint());
      } else if (request.getMethod().equals(HttpMethod.PUT)) {
         apacheRequest = new HttpPut(request.getEndpoint());
         apacheRequest.getParams().setBooleanParameter(CoreProtocolPNames.USE_EXPECT_CONTINUE, true);
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

      for (Map.Entry<String, String> entry : request.getHeaders().entries()) {
         String header = entry.getKey();
         // apache automatically tries to add content length header
         if (!header.equals(HttpHeaders.CONTENT_LENGTH))
            apacheRequest.addHeader(header, entry.getValue());
      }
      apacheRequest.addHeader(HttpHeaders.USER_AGENT, USER_AGENT);
      return apacheRequest;
   }

   public void addEntityForContent(HttpEntityEnclosingRequest apacheRequest, Payload payload) {
      payload = payload instanceof DelegatingPayload ? DelegatingPayload.class.cast(payload).getDelegate() : payload;
      if (payload instanceof StringPayload) {
         StringEntity nStringEntity = null;
         try {
            nStringEntity = new StringEntity((String) payload.getRawContent());
         } catch (UnsupportedEncodingException e) {
            throw new UnsupportedOperationException("Encoding not supported", e);
         }
         nStringEntity.setContentType(payload.getContentMetadata().getContentType());
         apacheRequest.setEntity(nStringEntity);
      } else if (payload instanceof FilePayload) {
         apacheRequest.setEntity(new FileEntity((File) payload.getRawContent(), payload.getContentMetadata()
               .getContentType()));
      } else if (payload instanceof ByteArrayPayload) {
         ByteArrayEntity Entity = new ByteArrayEntity((byte[]) payload.getRawContent());
         Entity.setContentType(payload.getContentMetadata().getContentType());
         apacheRequest.setEntity(Entity);
      } else {
         InputStream inputStream = payload.getInput();
         if (payload.getContentMetadata().getContentLength() == null)
            throw new IllegalArgumentException("you must specify size when content is an InputStream");
         InputStreamEntity entity = new InputStreamEntity(inputStream, payload.getContentMetadata().getContentLength());
         entity.setContentType(payload.getContentMetadata().getContentType());
         apacheRequest.setEntity(entity);
      }
      
      // TODO Reproducing old behaviour exactly; ignoring Content-Type, Content-Length and Content-MD5
      Set<String> desiredHeaders = ImmutableSet.of("Content-Disposition", "Content-Encoding", "Content-Language", "Expires");
      MutableContentMetadata md = payload.getContentMetadata();
      for (Map.Entry<String,String> entry : contentMetadataCodec.toHeaders(md).entries()) {
         if (desiredHeaders.contains(entry.getKey())) {
            apacheRequest.addHeader(entry.getKey(), entry.getValue());
         }
      }
      
      assert apacheRequest.getEntity() != null;
   }

   public static class HttpEntityPayload extends BasePayload<HttpEntity> {

      HttpEntityPayload(HttpEntity content) {
         super(content);
         getContentMetadata().setContentType(content.getContentType().getValue());
         getContentMetadata().setContentLength(content.getContentLength());
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
            EntityUtils.consume(content);
         } catch (IOException e) {
         }
      }

      @Override
      public void writeTo(OutputStream outstream) throws IOException {
         super.writeTo(outstream);
      }

   }

}
