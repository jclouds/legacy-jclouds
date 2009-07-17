/**
 *
 * Copyright (C) 2009 Global Cloud Specialists, Inc. <info@globalcloudspecialists.com>
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

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.UnsupportedEncodingException;

import javax.ws.rs.core.HttpHeaders;

import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpUtils;
import org.jclouds.rackspace.cloudfiles.domain.CFObject;
import org.jclouds.rest.EntityBinder;

public class CFObjectBinder implements EntityBinder {

   public void addEntityToRequest(Object entity, HttpRequest request) {
      CFObject object = (CFObject) entity;

      request.setEntity(checkNotNull(object.getData(), "object.getData()"));

      if (object.getMetadata().getSize() >= 0) {
         request.getHeaders().put(HttpHeaders.CONTENT_LENGTH, object.getMetadata().getSize() + "");
      } else {
         // Enable "chunked"/"streamed" data, where the size needn't be known in advance.
         request.getHeaders().put("Transfer-Encoding", "chunked");         
      }

      request.getHeaders()
               .put(
                        HttpHeaders.CONTENT_TYPE,
                        checkNotNull(object.getMetadata().getContentType(),
                                 "object.metadata.contentType()"));

      if (object.getMetadata().getCacheControl() != null) {
         request.getHeaders()
                  .put(HttpHeaders.CACHE_CONTROL, object.getMetadata().getCacheControl());
      }
      if (object.getMetadata().getContentDisposition() != null) {
         request.getHeaders().put("Content-Disposition",
                  object.getMetadata().getContentDisposition());
      }
      if (object.getMetadata().getContentEncoding() != null) {
         request.getHeaders().put(HttpHeaders.CONTENT_ENCODING,
                  object.getMetadata().getContentEncoding());
      }

      if (object.getMetadata().getETag() != null) {
         try {
            String hexETag = HttpUtils.toHexString(object.getMetadata().getETag());
            request.getHeaders().put(HttpHeaders.ETAG, hexETag);
         } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Failed to encode ETag for object: " + object, e);
         }
      }

      request.getHeaders().putAll(object.getMetadata().getUserMetadata());
   }

}
