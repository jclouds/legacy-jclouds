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

import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.core.HttpHeaders;

import org.jclouds.blobstore.domain.BlobMetadata;
import org.jclouds.http.HttpException;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.HttpUtils;
import org.jclouds.util.DateService;

import com.google.common.annotations.VisibleForTesting;

/**
 * @author Adrian Cole
 */
public class ParseSystemAndUserMetadataFromHeaders<M extends BlobMetadata> extends
         ParseContentTypeFromHeaders<M> {
   private final String metadataPrefix;
   private final DateService dateParser;

   @Inject
   public ParseSystemAndUserMetadataFromHeaders(DateService dateParser,
            @Named(PROPERTY_USER_METADATA_PREFIX) String metadataPrefix,
            BlobMetadataFactory<M> metadataFactory) {
      super(metadataFactory);
      this.dateParser = dateParser;
      this.metadataPrefix = metadataPrefix;
   }

   public M apply(HttpResponse from) {
      M to = super.apply(from);
      addETagTo(from, to);
      addContentMD5To(from, to);
      parseLastModifiedOrThrowException(from, to);
      setContentLengthOrThrowException(from, to);
      addUserMetadataTo(from, to);
      return to;
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
      if (lastModified == null)
         throw new HttpException(HttpHeaders.LAST_MODIFIED + " header not present in response: "
                  + from);
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
}