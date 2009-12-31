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
package org.jclouds.aws.s3.binders;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import javax.inject.Inject;
import javax.ws.rs.core.HttpHeaders;

import org.jclouds.aws.s3.blobstore.functions.ObjectToBlob;
import org.jclouds.aws.s3.domain.S3Object;
import org.jclouds.blobstore.binders.BindBlobToPayloadAndUserMetadataToHeadersWithPrefix;
import org.jclouds.http.HttpRequest;
import org.jclouds.rest.Binder;

public class BindS3ObjectToPayload implements Binder {
   private final BindBlobToPayloadAndUserMetadataToHeadersWithPrefix blobBinder;
   private final ObjectToBlob object2Blob;

   @Inject
   public BindS3ObjectToPayload(ObjectToBlob object2Blob,
            BindBlobToPayloadAndUserMetadataToHeadersWithPrefix blobBinder) {
      this.blobBinder = blobBinder;
      this.object2Blob = object2Blob;
   }

   public void bindToRequest(HttpRequest request, Object payload) {
      S3Object s3Object = (S3Object) payload;
      checkNotNull(s3Object.getContentLength(), "contentLength");
      checkArgument(s3Object.getContentLength() <= 5 * 1024 * 1024 * 1024,
               "maximum size for put object is 5GB");
      blobBinder.bindToRequest(request, object2Blob.apply(s3Object));

      if (s3Object.getMetadata().getCacheControl() != null) {
         request.getHeaders().put(HttpHeaders.CACHE_CONTROL,
                  s3Object.getMetadata().getCacheControl());
      }

      if (s3Object.getMetadata().getContentDisposition() != null) {
         request.getHeaders().put("Content-Disposition",
                  s3Object.getMetadata().getContentDisposition());
      }

      if (s3Object.getMetadata().getContentEncoding() != null) {
         request.getHeaders().put(HttpHeaders.CONTENT_ENCODING,
                  s3Object.getMetadata().getContentEncoding());
      }

   }
}
