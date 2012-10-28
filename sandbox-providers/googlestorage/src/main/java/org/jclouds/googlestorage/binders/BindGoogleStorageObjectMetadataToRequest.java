/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
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
 */
package org.jclouds.googlestorage.binders;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.core.HttpHeaders;

import org.jclouds.blobstore.binders.BindMapToHeadersWithPrefix;
import org.jclouds.http.HttpRequest;
import org.jclouds.s3.binders.BindS3ObjectMetadataToRequest;
import org.jclouds.s3.domain.S3Object;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class BindGoogleStorageObjectMetadataToRequest extends BindS3ObjectMetadataToRequest {
   @Inject
   public BindGoogleStorageObjectMetadataToRequest(BindMapToHeadersWithPrefix metadataPrefixer) {
      super(metadataPrefixer);
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
         request =  (R)request.toBuilder().replaceHeader("Transfer-Encoding", "chunked").build();
      }

      request = metadataPrefixer.bindToRequest(request, s3Object.getMetadata().getUserMetadata());

      if (s3Object.getMetadata().getCacheControl() != null) {
         request = (R)request.toBuilder().replaceHeader(HttpHeaders.CACHE_CONTROL, s3Object.getMetadata()
                  .getCacheControl()).build();
      }
      return request;
   }
}
