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

package org.jclouds.azure.storage.blob.binders;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.core.HttpHeaders;

import org.jclouds.azure.storage.blob.blobstore.functions.AzureBlobToBlob;
import org.jclouds.azure.storage.blob.domain.AzureBlob;
import org.jclouds.blobstore.binders.BindUserMetadataToHeadersWithPrefix;
import org.jclouds.http.HttpRequest;
import org.jclouds.rest.Binder;

@Singleton
public class BindAzureBlobToPayload implements Binder {

   private final AzureBlobToBlob azureBlob2Blob;
   private final BindUserMetadataToHeadersWithPrefix blobBinder;

   @Inject
   public BindAzureBlobToPayload(AzureBlobToBlob azureBlob2Blob,
            BindUserMetadataToHeadersWithPrefix blobBinder) {
      this.azureBlob2Blob = azureBlob2Blob;
      this.blobBinder = blobBinder;
   }

   public void bindToRequest(HttpRequest request, Object payload) {
      AzureBlob blob = (AzureBlob) payload;
      checkArgument(blob.getPayload().getContentLength() >= 0, "size must be set");
      request.getHeaders().put("x-ms-blob-type", blob.getProperties().getType().toString());

      switch (blob.getProperties().getType()) {
         case PAGE_BLOB:
            request.getHeaders().put(HttpHeaders.CONTENT_LENGTH, "0");
            request.getHeaders().put("x-ms-blob-content-length",
                     blob.getPayload().getContentLength().toString());
            break;
         case BLOCK_BLOB:
            checkArgument(checkNotNull(blob.getPayload().getContentLength(),
                     "blob.getContentLength()") <= 64l * 1024 * 1024,
                     "maximum size for put Blob is 64MB");
            break;
      }
      blobBinder.bindToRequest(request, azureBlob2Blob.apply(blob));

      if (blob.getProperties().getContentLanguage() != null) {
         request.getHeaders().put(HttpHeaders.CONTENT_LANGUAGE,
                  blob.getProperties().getContentLanguage());
      }

      if (blob.getProperties().getContentEncoding() != null) {
         request.getHeaders().put(HttpHeaders.CONTENT_ENCODING,
                  blob.getProperties().getContentEncoding());
      }

   }
}
