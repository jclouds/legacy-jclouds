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
package org.jclouds.azure.storage.blob.binders;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.core.HttpHeaders;

import org.jclouds.azure.storage.blob.blobstore.functions.AzureBlobToBlob;
import org.jclouds.azure.storage.blob.domain.AzureBlob;
import org.jclouds.azure.storage.blob.reference.AzureBlobConstants;
import org.jclouds.blobstore.binders.BindBlobToEntityAndUserMetadataToHeadersWithPrefix;
import org.jclouds.http.HttpRequest;

public class BindAzureBlobToEntity extends BindBlobToEntityAndUserMetadataToHeadersWithPrefix {

   private final AzureBlobToBlob azureBlob2Blob;

   @Inject
   public BindAzureBlobToEntity(AzureBlobToBlob azureBlob2Blob,
            @Named(AzureBlobConstants.PROPERTY_AZUREBLOB_METADATA_PREFIX) String prefix) {
      super(prefix);
      this.azureBlob2Blob = azureBlob2Blob;
   }

   public void bindToRequest(HttpRequest request, Object entity) {
      AzureBlob object = (AzureBlob) entity;
      checkArgument(object.getProperties().getSize() >= 0, "size must be set");
      checkArgument(
               checkNotNull(object.getContentLength(), "object.getContentLength()") <= 64 * 1024 * 1024,
               "maximum size for put Blob is 64MB");
      super.bindToRequest(request, azureBlob2Blob.apply(object));

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
