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
package org.jclouds.aws.s3.binders;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.io.BaseEncoding.base64;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.core.HttpHeaders;

import org.jclouds.blobstore.binders.BindMapToHeadersWithPrefix;
import org.jclouds.http.HttpRequest;
import org.jclouds.rest.Binder;
import org.jclouds.s3.domain.ObjectMetadata;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableMultimap.Builder;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class BindObjectMetadataToRequest implements Binder {
   protected final BindMapToHeadersWithPrefix metadataPrefixer;

   @Inject
   public BindObjectMetadataToRequest(BindMapToHeadersWithPrefix metadataPrefixer) {
      this.metadataPrefixer = checkNotNull(metadataPrefixer, "metadataPrefixer");
   }

   @SuppressWarnings("unchecked")
   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Object input) {
      checkArgument(checkNotNull(input, "input") instanceof ObjectMetadata,
               "this binder is only valid for ObjectMetadata!");
      checkNotNull(request, "request");

      ObjectMetadata md = ObjectMetadata.class.cast(input);
      checkArgument(md.getKey() != null, "objectMetadata.getKey() must be set!");

      request = metadataPrefixer.bindToRequest(request, md.getUserMetadata());

      Builder<String, String> headers = ImmutableMultimap.builder();
      if (md.getCacheControl() != null) {
         headers.put(HttpHeaders.CACHE_CONTROL, md.getCacheControl());
      }

      if (md.getContentMetadata().getContentDisposition() != null) {
         headers.put("Content-Disposition", md.getContentMetadata().getContentDisposition());
      }

      if (md.getContentMetadata().getContentEncoding() != null) {
         headers.put("Content-Encoding", md.getContentMetadata().getContentEncoding());
      }
      if (md.getContentMetadata().getContentType() != null) {
         headers.put(HttpHeaders.CONTENT_TYPE, md.getContentMetadata().getContentType());
      } else {
         headers.put(HttpHeaders.CONTENT_TYPE, "binary/octet-stream");
      }

      if (md.getContentMetadata().getContentMD5() != null) {
         headers.put("Content-MD5", base64().encode(md.getContentMetadata().getContentMD5()));
      }

      return (R) request.toBuilder().replaceHeaders(headers.build()).build();
   }
}
