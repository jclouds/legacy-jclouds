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
package org.jclouds.aws.s3.binders;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import javax.ws.rs.core.HttpHeaders;

import org.jclouds.aws.s3.domain.S3Object;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpUtils;
import org.jclouds.rest.EntityBinder;

public class S3ObjectBinder implements EntityBinder {

   public void addEntityToRequest(Object entity, HttpRequest request) {
      S3Object object = (S3Object) entity;
      checkArgument(object.getMetadata().getSize() >= 0, "size must be set");

      request.setEntity(checkNotNull(object.getData(), "object.getContent()"));

      request.getHeaders()
               .put(
                        HttpHeaders.CONTENT_TYPE,
                        checkNotNull(object.getMetadata().getContentType(),
                                 "object.metadata.contentType()"));

      request.getHeaders().put(HttpHeaders.CONTENT_LENGTH, object.getMetadata().getSize() + "");

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

      if (object.getMetadata().getETag() != null)
         request.getHeaders().put(HttpHeaders.ETAG,
                  HttpUtils.toBase64String(object.getMetadata().getETag()));

      request.getHeaders().putAll(object.getMetadata().getUserMetadata());
   }

}
