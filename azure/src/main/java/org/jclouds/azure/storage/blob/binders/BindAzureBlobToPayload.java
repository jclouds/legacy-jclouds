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
package org.jclouds.azure.storage.blob.binders;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.ws.rs.core.HttpHeaders;

import org.jclouds.azure.storage.blob.domain.AzureBlob;
import org.jclouds.blobstore.reference.BlobStoreConstants;
import org.jclouds.encryption.EncryptionService;
import org.jclouds.http.HttpRequest;
import org.jclouds.rest.Binder;

@Singleton
public class BindAzureBlobToPayload implements Binder {

   private final String metadataPrefix;
   private final EncryptionService encryptionService;

   @Inject
   public BindAzureBlobToPayload(
            @Named(BlobStoreConstants.PROPERTY_USER_METADATA_PREFIX) String prefix,
            EncryptionService encryptionService) {
      this.metadataPrefix = prefix;
      this.encryptionService = encryptionService;
   }

   public void bindToRequest(HttpRequest request, Object payload) {
      AzureBlob object = (AzureBlob) payload;
      checkArgument(object.getProperties().getContentLength() >= 0, "size must be set");
      request.getHeaders().put("x-ms-blob-type", object.getProperties().getType().toString());

      switch (object.getProperties().getType()) {
         case PAGE_BLOB:
            request.getHeaders().put(HttpHeaders.CONTENT_LENGTH, "0");
            request.getHeaders().put("x-ms-blob-content-length", object.getContentLength() + "");
            break;
         case BLOCK_BLOB:
            checkArgument(
                     checkNotNull(object.getContentLength(), "object.getContentLength()") <= 64 * 1024 * 1024,
                     "maximum size for put Blob is 64MB");
            request.getHeaders().put(HttpHeaders.CONTENT_LENGTH, object.getContentLength() + "");
            break;
      }

      for (String key : object.getProperties().getMetadata().keySet()) {
         request.getHeaders().put(key.startsWith(metadataPrefix) ? key : metadataPrefix + key,
                  object.getProperties().getMetadata().get(key));
      }

      request.setPayload(checkNotNull(object.getContent(), "object.getContent()"));

      // in azure content-type is optional
      if (object.getProperties().getContentType() != null)
         request.getHeaders()
                  .put(HttpHeaders.CONTENT_TYPE, object.getProperties().getContentType());

      if (object.getProperties().getContentMD5() != null) {
         request.getHeaders().put("Content-MD5",
                  encryptionService.base64(object.getProperties().getContentMD5()));
      }
      if (object.getProperties().getContentLanguage() != null) {
         request.getHeaders().put(HttpHeaders.CONTENT_LANGUAGE,
                  object.getProperties().getContentLanguage());
      }

      if (object.getProperties().getContentEncoding() != null) {
         request.getHeaders().put(HttpHeaders.CONTENT_ENCODING,
                  object.getProperties().getContentEncoding());
      }

   }
}
