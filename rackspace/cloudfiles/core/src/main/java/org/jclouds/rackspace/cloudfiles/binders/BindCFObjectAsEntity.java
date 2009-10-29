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
package org.jclouds.rackspace.cloudfiles.binders;

import static com.google.common.base.Preconditions.checkArgument;

import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.core.HttpHeaders;

import org.jclouds.blobstore.binders.BindBlobToEntityAndUserMetadataToHeadersWithPrefix;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpUtils;
import org.jclouds.rackspace.cloudfiles.reference.CloudFilesConstants;

public class BindCFObjectAsEntity extends BindBlobToEntityAndUserMetadataToHeadersWithPrefix {
   @Inject
   public BindCFObjectAsEntity(
            @Named(CloudFilesConstants.PROPERTY_CLOUDFILES_METADATA_PREFIX) String metadataPrefix) {
      super(metadataPrefix);
   }

   public void bindToRequest(HttpRequest request, Object entity) {
      Blob object = (Blob) entity;
      if (object.getContentLength() >= 0) {
         checkArgument(object.getContentLength() <= 5 * 1024 * 1024 * 1024,
                  "maximum size for put object is 5GB");
         request.getHeaders().put(HttpHeaders.CONTENT_LENGTH, object.getContentLength() + "");
      } else {
         // Enable "chunked"/"streamed" data, where the size needn't be known in advance.
         request.getHeaders().put("Transfer-Encoding", "chunked");
      }
      /**
       * rackspace uses ETag header instead of Content-MD5.
       */
      if (object.getMetadata().getContentMD5() != null) {
         request.getHeaders().put(HttpHeaders.ETAG,// note it needs to be in hex!
                  HttpUtils.toHexString(object.getMetadata().getContentMD5()));
      }
      super.bindToRequest(request, entity);
      request.getHeaders().removeAll("Content-MD5");
   }
}
