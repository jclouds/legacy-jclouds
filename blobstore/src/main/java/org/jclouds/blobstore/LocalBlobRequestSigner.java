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
package org.jclouds.blobstore;

import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.functions.BlobToHttpGetOptions;
import org.jclouds.blobstore.options.GetOptions;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.filters.BasicAuthentication;
import org.jclouds.io.ContentMetadataCodec;
import org.jclouds.location.Provider;

import com.google.common.base.Supplier;

/**
 * @author Adrian Cole
 */
@Singleton
public class LocalBlobRequestSigner implements BlobRequestSigner {

   private final BasicAuthentication basicAuth;
   private final BlobToHttpGetOptions blob2HttpGetOptions;
   private final Supplier<URI> endpoint;
   private final ContentMetadataCodec contentMetadataCodec;

   @Inject
   public LocalBlobRequestSigner(BasicAuthentication basicAuth, BlobToHttpGetOptions blob2HttpGetOptions, @Provider Supplier<URI> endpoint,
             ContentMetadataCodec contentMetadataCodec) {
      this.basicAuth = checkNotNull(basicAuth, "basicAuth");
      this.blob2HttpGetOptions = checkNotNull(blob2HttpGetOptions, "blob2HttpGetOptions");
      this.endpoint = endpoint;
      this.contentMetadataCodec = contentMetadataCodec;
   }

   @Override
   public HttpRequest signGetBlob(String container, String name) {
      HttpRequest request = HttpRequest.builder().method("GET").endpoint(String.format("%s/%s/%s", endpoint.get(), container, name)).build();
      return basicAuth.filter(request);
   }

   @Override
   public HttpRequest signGetBlob(String container, String name, long timeInSeconds) {
      throw new UnsupportedOperationException();
   }

   @Override
   public HttpRequest signPutBlob(String container, Blob blob) {
      HttpRequest request = HttpRequest.builder().method("PUT").endpoint(
               URI.create(String.format("%s/%s/%s", endpoint.get(), container, blob.getMetadata().getName()))).payload(
               blob.getPayload()).headers(
               contentMetadataCodec.toHeaders(blob.getMetadata().getContentMetadata())).build();
      return basicAuth.filter(request);
   }

   @Override
   public HttpRequest signPutBlob(String container, Blob blob, long timeInSeconds) {
      throw new UnsupportedOperationException();
   }

   @Override
   public HttpRequest signRemoveBlob(String container, String name) {
      HttpRequest request = HttpRequest.builder().method("DELETE").endpoint(String.format("%s/%s/%s", endpoint.get(), container,
               name)).build();
      return basicAuth.filter(request);
   }

   @Override
   public HttpRequest signGetBlob(String container, String name, GetOptions options) {
      HttpRequest request = HttpRequest.builder().method("GET").endpoint(
               URI.create(String.format("%s/%s/%s", endpoint.get(), container, name))).headers(
               blob2HttpGetOptions.apply(options).buildRequestHeaders()).build();
      return basicAuth.filter(request);
   }

}
