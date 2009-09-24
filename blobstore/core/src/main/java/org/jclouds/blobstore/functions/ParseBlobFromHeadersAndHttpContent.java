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

import javax.ws.rs.core.HttpHeaders;

import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.BlobMetadata;
import org.jclouds.http.HttpException;
import org.jclouds.http.HttpResponse;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import javax.inject.Inject;

/**
 * Parses response headers and creates a new Blob from them and the HTTP content.
 * 
 * @see ParseBlobMetadataFromHeaders
 * @author Adrian Cole
 */
public class ParseBlobFromHeadersAndHttpContent<M extends BlobMetadata, B extends Blob<M>>
         implements Function<HttpResponse, B> {
   private final ParseBlobMetadataFromHeaders<M> metadataParser;
   private final BlobFactory<M, B> blobFactory;

   public static interface BlobFactory<M extends BlobMetadata, B extends Blob<M>> {
      B create(M metadata);
   }

   @Inject
   public ParseBlobFromHeadersAndHttpContent(ParseBlobMetadataFromHeaders<M> metadataParser,
            BlobFactory<M, B> blobFactory) {
      this.metadataParser = metadataParser;
      this.blobFactory = blobFactory;
   }

   /**
    * First, calls {@link ParseBlobMetadataFromHeaders}.
    * 
    * Then, sets the object size based on the Content-Length header and adds the content to the
    * {@link Blob} result.
    * 
    * @throws org.jclouds.http.HttpException
    */
   public B apply(HttpResponse from) {
      M metadata = metadataParser.apply(from);
      B object = blobFactory.create(metadata);
      assert object.getMetadata() == metadata;
      object.setData(from.getContent());
      parseContentLengthOrThrowException(from, object);
      return object;
   }

   @VisibleForTesting
   void parseContentLengthOrThrowException(HttpResponse from, B object) throws HttpException {
      String contentLength = from.getFirstHeaderOrNull(HttpHeaders.CONTENT_LENGTH);
      if (contentLength == null)
         throw new HttpException(HttpHeaders.CONTENT_LENGTH + " header not present in headers: "
                  + from.getHeaders());
      object.setContentLength(Long.parseLong(contentLength));

      String contentRange = from.getFirstHeaderOrNull("Content-Range");
      if (contentRange == null) {
         object.getMetadata().setSize(object.getContentLength());
      } else {
         object.setContentRange(contentRange);
         object.getMetadata().setSize(
                  Long.parseLong(contentRange.substring(contentRange.lastIndexOf('/') + 1)));
      }
   }

}