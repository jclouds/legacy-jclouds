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

import static org.jclouds.blobstore.reference.BlobStoreConstants.PROPERTY_USER_METADATA_PREFIX;

import java.util.Map.Entry;

import javax.ws.rs.core.HttpHeaders;

import org.jclouds.blobstore.domain.BlobMetadata;
import org.jclouds.http.HttpException;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.HttpUtils;
import org.jclouds.util.DateService;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author Adrian Cole
 */
public class ParseBlobMetadataFromHeaders<M extends BlobMetadata> implements
         Function<HttpResponse, M> {
   private final DateService dateParser;
   private final String metadataPrefix;
   private final BlobMetadataFactory<M> metadataFactory;

   public static interface BlobMetadataFactory<M extends BlobMetadata> {
      M create(String key);
   }

   @Inject
   public ParseBlobMetadataFromHeaders(DateService dateParser,
            @Named(PROPERTY_USER_METADATA_PREFIX) String metadataPrefix,
            BlobMetadataFactory<M> metadataFactory) {
      this.dateParser = dateParser;
      this.metadataPrefix = metadataPrefix;
      this.metadataFactory = metadataFactory;
   }

   public M apply(HttpResponse from) {
      String objectKey = from.getRequestURL().getPath();
      if (objectKey.startsWith("/")) {
         // Trim initial slash from object key name.
         objectKey = objectKey.substring(1);
      }
      M to = metadataFactory.create(objectKey);
      addAllHeadersTo(from, to);

      addUserMetadataTo(from, to);
      addETagTo(from, to);
      addContentMD5To(from, to);

      parseLastModifiedOrThrowException(from, to);
      setContentTypeOrThrowException(from, to);
      setContentLengthOrThrowException(from, to);
      return to;
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

   @VisibleForTesting
   void setContentLengthOrThrowException(HttpResponse from, M metadata) throws HttpException {
      String contentLength = from.getFirstHeaderOrNull(HttpHeaders.CONTENT_LENGTH);
      if (contentLength == null)
         throw new HttpException(HttpHeaders.CONTENT_LENGTH + " not found in headers");
      else
         metadata.setSize(Long.parseLong(contentLength));
   }

   @VisibleForTesting
   void parseLastModifiedOrThrowException(HttpResponse from, M metadata) throws HttpException {
      String lastModified = from.getFirstHeaderOrNull(HttpHeaders.LAST_MODIFIED);
      metadata.setLastModified(dateParser.rfc822DateParse(lastModified));
      if (metadata.getLastModified() == null)
         throw new HttpException("could not parse: " + HttpHeaders.LAST_MODIFIED + ": "
                  + lastModified);
   }

   @VisibleForTesting
   protected void addETagTo(HttpResponse from, M metadata) {
      String eTag = from.getFirstHeaderOrNull(HttpHeaders.ETAG);
      if (metadata.getETag() == null && eTag != null) {
         metadata.setETag(HttpUtils.fromHexString(eTag.replaceAll("\"", "")));
      }
   }

   @VisibleForTesting
   protected void addContentMD5To(HttpResponse from, M metadata) {
      String contentMD5 = from.getFirstHeaderOrNull("Content-MD5");
      if (contentMD5 != null) {
         metadata.setContentMD5(HttpUtils.fromBase64String(contentMD5));
      }
   }

   @VisibleForTesting
   void addUserMetadataTo(HttpResponse from, M metadata) {
      for (Entry<String, String> header : from.getHeaders().entries()) {
         if (header.getKey() != null && header.getKey().startsWith(metadataPrefix))
            metadata.getUserMetadata().put(
                     (header.getKey().substring(metadataPrefix.length())).toLowerCase(),
                     header.getValue());
      }
   }

}