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
package org.jclouds.azureblob.binders;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.core.HttpHeaders;

import org.jclouds.azureblob.blobstore.functions.AzureBlobToBlob;
import org.jclouds.azureblob.domain.AzureBlob;
import org.jclouds.blobstore.binders.BindUserMetadataToHeadersWithPrefix;
import org.jclouds.http.HttpRequest;
import org.jclouds.rest.Binder;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.collect.Multimaps;

@Singleton
public class BindAzureBlobMetadataToRequest implements Binder {

   private final AzureBlobToBlob azureBlob2Blob;
   private final BindUserMetadataToHeadersWithPrefix blobBinder;

   @Inject
   public BindAzureBlobMetadataToRequest(AzureBlobToBlob azureBlob2Blob, BindUserMetadataToHeadersWithPrefix blobBinder) {
      this.azureBlob2Blob = checkNotNull(azureBlob2Blob, "azureBlob2Blob");
      this.blobBinder = checkNotNull(blobBinder, "blobBinder");
   }

   @SuppressWarnings("unchecked")
   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Object input) {
      checkArgument(checkNotNull(input, "input") instanceof AzureBlob, "this binder is only valid for AzureBlobs!");
      checkNotNull(request, "request");
      AzureBlob blob = AzureBlob.class.cast(input);

      checkArgument(blob.getPayload().getContentMetadata().getContentLength() != null
            && blob.getPayload().getContentMetadata().getContentLength() >= 0, "size must be set");

      Builder<String, String> headers = ImmutableMap.builder();

      headers.put("x-ms-blob-type", blob.getProperties().getType().toString());

      switch (blob.getProperties().getType()) {
      case PAGE_BLOB:
         headers.put(HttpHeaders.CONTENT_LENGTH, "0");
         headers.put("x-ms-blob-content-length", blob.getPayload().getContentMetadata().getContentLength().toString());
         break;
      case BLOCK_BLOB:
         checkArgument(
               checkNotNull(blob.getPayload().getContentMetadata().getContentLength(), "blob.getContentLength()") <= 64l * 1024 * 1024,
               "maximum size for put Blob is 64MB");
         break;
      }
      request = (R) request.toBuilder().replaceHeaders(Multimaps.forMap(headers.build())).build();

      return blobBinder.bindToRequest(request, azureBlob2Blob.apply(blob));
   }
}
