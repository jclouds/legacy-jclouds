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
package org.jclouds.openstack.swift.binders;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.core.MediaType;

import com.google.common.io.BaseEncoding;
import com.google.common.net.HttpHeaders;

import org.jclouds.blobstore.binders.BindUserMetadataToHeadersWithPrefix;
import org.jclouds.http.HttpRequest;
import org.jclouds.openstack.swift.blobstore.functions.ObjectToBlob;
import org.jclouds.openstack.swift.domain.SwiftObject;
import org.jclouds.rest.Binder;

@Singleton
public class BindSwiftObjectMetadataToRequest implements Binder {

   private final BindUserMetadataToHeadersWithPrefix mdBinder;
   private final ObjectToBlob object2Blob;

   @Inject
   public BindSwiftObjectMetadataToRequest(ObjectToBlob object2Blob, BindUserMetadataToHeadersWithPrefix mdBinder) {
      this.mdBinder = mdBinder;
      this.object2Blob = object2Blob;
   }

   @SuppressWarnings("unchecked")
   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Object input) {
      checkArgument(checkNotNull(input, "input") instanceof SwiftObject, "this binder is only valid for SwiftObject!");
      checkNotNull(request, "request");

      SwiftObject object = (SwiftObject) input;
      if (object.getPayload().getContentMetadata().getContentType() == null)
         object.getPayload().getContentMetadata().setContentType(MediaType.APPLICATION_OCTET_STREAM);

      if (object.getPayload().getContentMetadata().getContentLength() != null
            && object.getPayload().getContentMetadata().getContentLength() >= 0) {
         checkArgument(object.getPayload().getContentMetadata().getContentLength() <= 5l * 1024 * 1024 * 1024,
               "maximum size for put object is 5GB");
      } else {
         // Enable "chunked"/"streamed" data, where the size needn't be known in advance.
         request = (R) request.toBuilder().replaceHeader("Transfer-Encoding", "chunked").build();
      }

      byte[] contentMD5 = object.getInfo().getHash();
      if (contentMD5 != null) {
         // Swizzle hash to ETag
         object.getInfo().setHash(null);
         request = (R) request.toBuilder()
               .addHeader(HttpHeaders.ETAG,
                     BaseEncoding.base16().lowerCase().encode(contentMD5))
               .build();
      }

      request = mdBinder.bindToRequest(request, object2Blob.apply(object));
      return request;
   }
}
