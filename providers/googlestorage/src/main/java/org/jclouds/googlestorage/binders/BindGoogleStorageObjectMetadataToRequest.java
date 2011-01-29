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

package org.jclouds.googlestorage.binders;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.core.HttpHeaders;

import org.jclouds.blobstore.binders.BindUserMetadataToHeadersWithPrefix;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.utils.ModifyRequest;
import org.jclouds.s3.binders.BindS3ObjectMetadataToRequest;
import org.jclouds.s3.blobstore.functions.ObjectToBlob;
import org.jclouds.s3.domain.S3Object;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class BindGoogleStorageObjectMetadataToRequest extends BindS3ObjectMetadataToRequest {
   private final BindUserMetadataToHeadersWithPrefix blobBinder;
   private final ObjectToBlob object2Blob;

   @Inject
   public BindGoogleStorageObjectMetadataToRequest(ObjectToBlob object2Blob,
            BindUserMetadataToHeadersWithPrefix blobBinder) {
      super(object2Blob, blobBinder);
      this.blobBinder = checkNotNull(blobBinder, "blobBinder");
      this.object2Blob = checkNotNull(object2Blob, "object2Blob");
   }

   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Object input) {
      checkArgument(checkNotNull(input, "input") instanceof S3Object, "this binder is only valid for S3Object!");
      checkNotNull(request, "request");

      S3Object s3Object = S3Object.class.cast(input);
      checkArgument(s3Object.getMetadata().getKey() != null, "s3Object.getMetadata().getKey() must be set!");

      if (s3Object.getPayload().getContentMetadata().getContentLength() != null
               && s3Object.getPayload().getContentMetadata().getContentLength() >= 0) {
         checkArgument(s3Object.getPayload().getContentMetadata().getContentLength() <= 5l * 1024 * 1024 * 1024,
                  "maximum size for put object is 5GB");
      } else {
         // Enable "chunked"/"streamed" data, where the size needn't be known in advance.
         request = ModifyRequest.replaceHeader(request, "Transfer-Encoding", "chunked");
      }

      request = blobBinder.bindToRequest(request, object2Blob.apply(s3Object));

      if (s3Object.getMetadata().getCacheControl() != null) {
         request = ModifyRequest.replaceHeader(request, HttpHeaders.CACHE_CONTROL, s3Object.getMetadata()
                  .getCacheControl());
      }
      return request;
   }
}
