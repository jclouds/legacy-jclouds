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
package org.jclouds.blobstore.functions;

import static org.jclouds.blobstore.reference.BlobStoreConstants.PROPERTY_USER_METADATA_PREFIX;

import java.util.Map.Entry;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.ws.rs.core.HttpHeaders;

import org.jclouds.blobstore.domain.MutableBlobMetadata;
import org.jclouds.http.HttpException;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.HttpUtils;
import org.jclouds.rest.InvocationContext;
import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.jclouds.util.DateService;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;

/**
 * @author Adrian Cole
 */
public class ParseSystemAndUserMetadataFromHeaders implements
         Function<HttpResponse, MutableBlobMetadata>, InvocationContext {
   private final String metadataPrefix;
   private final DateService dateParser;
   private final Provider<MutableBlobMetadata> metadataFactory;
   private GeneratedHttpRequest<?> request;

   @Inject
   public ParseSystemAndUserMetadataFromHeaders(Provider<MutableBlobMetadata> metadataFactory,
            DateService dateParser, @Named(PROPERTY_USER_METADATA_PREFIX) String metadataPrefix) {
      this.metadataFactory = metadataFactory;
      this.dateParser = dateParser;
      this.metadataPrefix = metadataPrefix;
   }

   public MutableBlobMetadata apply(HttpResponse from) {
      String objectKey = getKeyFor(from);
      MutableBlobMetadata to = metadataFactory.get();
      to.setName(objectKey);
      setContentTypeOrThrowException(from, to);
      addETagTo(from, to);
      addContentMD5To(from, to);
      parseLastModifiedOrThrowException(from, to);
      setContentLength(from, to);
      addUserMetadataTo(from, to);
      return to;
   }

   @VisibleForTesting
   void addUserMetadataTo(HttpResponse from, MutableBlobMetadata metadata) {
      for (Entry<String, String> header : from.getHeaders().entries()) {
         if (header.getKey() != null && header.getKey().startsWith(metadataPrefix))
            metadata.getUserMetadata().put(
                     (header.getKey().substring(metadataPrefix.length())).toLowerCase(),
                     header.getValue());
      }
   }

   @VisibleForTesting
   void setContentLength(HttpResponse from, MutableBlobMetadata metadata) throws HttpException {
      String contentLength = from.getFirstHeaderOrNull(HttpHeaders.CONTENT_LENGTH);
      metadata.setSize(contentLength == null ? 0 : Long.parseLong(contentLength));
   }

   @VisibleForTesting
   void parseLastModifiedOrThrowException(HttpResponse from, MutableBlobMetadata metadata)
            throws HttpException {
      String lastModified = from.getFirstHeaderOrNull(HttpHeaders.LAST_MODIFIED);
      if (lastModified == null)
         throw new HttpException(HttpHeaders.LAST_MODIFIED + " header not present in response: "
                  + from.getStatusLine());
      metadata.setLastModified(dateParser.rfc822DateParse(lastModified));
      if (metadata.getLastModified() == null)
         throw new HttpException("could not parse: " + HttpHeaders.LAST_MODIFIED + ": "
                  + lastModified);
   }

   @VisibleForTesting
   protected void addETagTo(HttpResponse from, MutableBlobMetadata metadata) {
      String eTag = from.getFirstHeaderOrNull(HttpHeaders.ETAG);
      if (metadata.getETag() == null && eTag != null) {
         metadata.setETag(eTag);
      }
   }

   @VisibleForTesting
   protected void addContentMD5To(HttpResponse from, MutableBlobMetadata metadata) {
      String contentMD5 = from.getFirstHeaderOrNull("Content-MD5");
      if (contentMD5 != null) {
         metadata.setContentMD5(HttpUtils.fromBase64String(contentMD5));
      }
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
   void setContentTypeOrThrowException(HttpResponse from, MutableBlobMetadata metadata)
            throws HttpException {
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