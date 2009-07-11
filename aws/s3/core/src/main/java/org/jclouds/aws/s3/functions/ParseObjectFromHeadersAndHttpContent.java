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
package org.jclouds.aws.s3.functions;

import javax.ws.rs.core.HttpHeaders;

import org.jclouds.aws.s3.domain.S3Object;
import org.jclouds.http.HttpException;
import org.jclouds.http.HttpResponse;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.inject.Inject;

/**
 * Parses response headers and creates a new S3Object from them and the HTTP content.
 * 
 * @see ParseMetadataFromHeaders
 * @author Adrian Cole
 */
public class ParseObjectFromHeadersAndHttpContent implements Function<HttpResponse, S3Object> {
   private final ParseMetadataFromHeaders metadataParser;

   @Inject
   public ParseObjectFromHeadersAndHttpContent(ParseMetadataFromHeaders metadataParser) {
      this.metadataParser = metadataParser;
   }

   /**
    * First, calls {@link ParseMetadataFromHeaders}.
    * 
    * Then, sets the object size based on the Content-Length header and adds the content to the
    * {@link S3Object} result.
    * 
    * @throws org.jclouds.http.HttpException
    */
   public S3Object apply(HttpResponse from) {
      S3Object.Metadata metadata = metadataParser.apply(from);
      S3Object object = new S3Object(metadata, from.getContent());
      parseContentLengthOrThrowException(from, object);
      return object;
   }

   @VisibleForTesting
   void parseContentLengthOrThrowException(HttpResponse from, S3Object object) throws HttpException {
      String contentLength = from.getFirstHeaderOrNull(HttpHeaders.CONTENT_LENGTH);
      String contentRange = from.getFirstHeaderOrNull("Content-Range");
      if (contentLength == null)
         throw new HttpException(HttpHeaders.CONTENT_LENGTH + " header not present in headers: "
                  + from.getHeaders());
      object.setContentLength(Long.parseLong(contentLength));

      if (contentRange == null) {
         object.getMetadata().setSize(object.getContentLength());
      } else {
         object.setContentRange(contentRange);
         object.getMetadata().setSize(
                  Long.parseLong(contentRange.substring(contentRange.lastIndexOf('/') + 1)));
      }
   }

}