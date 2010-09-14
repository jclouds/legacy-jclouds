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

package org.jclouds.blobstore;

import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.core.HttpHeaders;

import org.jclouds.blobstore.domain.Blob;
import org.jclouds.crypto.CryptoStreams;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.filters.BasicAuthentication;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class TransientBlobRequestSigner implements BlobRequestSigner {

   private final BasicAuthentication basicAuth;

   @Inject
   public TransientBlobRequestSigner(BasicAuthentication basicAuth) {
      this.basicAuth = checkNotNull(basicAuth, "basicAuth");
   }

   @Override
   public HttpRequest signGetBlob(String container, String name) {
      HttpRequest request = new HttpRequest("GET", URI.create(String.format("http://localhost/%s/%s", container, name)));
      basicAuth.filter(request);
      return request;
   }

   @Override
   public HttpRequest signPutBlob(String container, Blob blob) {
      HttpRequest request = new HttpRequest("PUT", URI.create(String.format("http://localhost/%s/%s", container, blob
               .getMetadata().getName())));
      if (blob.getPayload().getContentLength() != null)
         request.getHeaders().put(HttpHeaders.CONTENT_LENGTH, blob.getPayload().getContentLength().toString());
      if (blob.getPayload().getContentType() != null)
         request.getHeaders().put(HttpHeaders.CONTENT_TYPE, blob.getPayload().getContentType());
      if (blob.getPayload().getContentMD5() != null)
         request.getHeaders().put("Content-MD5", CryptoStreams.base64(blob.getPayload().getContentMD5()));
      request.setPayload(blob.getPayload());
      basicAuth.filter(request);
      return request;
   }

   @Override
   public HttpRequest signRemoveBlob(String container, String name) {
      HttpRequest request = new HttpRequest("DELETE", URI.create(String.format("http://localhost/%s/%s", container,
               name)));
      basicAuth.filter(request);
      return request;
   }

}