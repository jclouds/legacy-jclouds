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
package org.jclouds.blobstore.functions;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.ws.rs.core.HttpHeaders;

import org.jclouds.blobstore.domain.BlobMetadata;
import org.jclouds.http.HttpException;
import org.jclouds.http.HttpResponse;
import org.jclouds.rest.InvocationContext;
import org.jclouds.rest.internal.GeneratedHttpRequest;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;

/**
 * @author Adrian Cole
 */
public class ParseContentTypeFromHeaders<M extends BlobMetadata> implements
         Function<HttpResponse, M>, InvocationContext {
   private final Provider<M> metadataFactory;
   private GeneratedHttpRequest<?> request;

   @Inject
   public ParseContentTypeFromHeaders(Provider<M> metadataFactory) {
      this.metadataFactory = metadataFactory;
   }

   public M apply(HttpResponse from) {
      String objectKey = getKeyFor(from);
      M to = metadataFactory.get();
      to.setName(objectKey);
      addAllHeadersTo(from, to);
      setContentTypeOrThrowException(from, to);
      return to;
   }

   protected String getKeyFor(HttpResponse from) {
      String objectKey = request.getEndpoint().getPath();
      if (objectKey.startsWith("/")) {
         // Trim initial slash from object key name.
         objectKey = objectKey.substring(1);
      }
      return objectKey;
   }

   @VisibleForTesting
   void addAllHeadersTo(HttpResponse from, M metadata) {
      metadata.getAllHeaders().putAll(from.getHeaders());
   }

   @VisibleForTesting
   void setContentTypeOrThrowException(HttpResponse from, M metadata) throws HttpException {
      String contentType = from.getFirstHeaderOrNull(HttpHeaders.CONTENT_TYPE);
      if (contentType == null)
         throw new HttpException(HttpHeaders.CONTENT_TYPE + " not found in headers");
      else
         metadata.setContentType(contentType);
   }

   public void setContext(GeneratedHttpRequest<?> request) {
      this.request = request;
   }

}