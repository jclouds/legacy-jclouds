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

import java.util.Map.Entry;

import javax.ws.rs.core.HttpHeaders;

import org.jclouds.aws.s3.domain.S3Object;
import org.jclouds.aws.s3.domain.S3Object.Metadata;
import org.jclouds.aws.s3.reference.S3Headers;
import org.jclouds.http.HttpException;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.HttpUtils;
import org.jclouds.util.DateService;

import com.google.common.base.Function;
import com.google.inject.Inject;

/**
 * This parses @{link {@link org.jclouds.aws.s3.domain.S3Object.Metadata} from HTTP headers.
 * 
 * @see <a href="http://docs.amazonwebservices.com/AmazonS3/latest/RESTObjectGET.html" />
 * @author Adrian Cole
 */
public class ParseMetadataFromHeaders implements Function<HttpResponse, S3Object.Metadata> {
   private final DateService dateParser;

   @Inject
   public ParseMetadataFromHeaders(DateService dateParser) {
      this.dateParser = dateParser;
   }

   /**
    * parses the http response headers to create a new
    * {@link org.jclouds.aws.s3.domain.S3Object.Metadata} object.
    */
   public Metadata apply(HttpResponse from) {

      S3Object.Metadata to = new S3Object.Metadata("TODO");
      addAllHeadersTo(from, to);

      addUserMetadataTo(from, to);
      addETagTo(from, to);

      parseLastModifiedOrThrowException(from, to);
      setContentTypeOrThrowException(from, to);
      setContentLengthOrThrowException(from, to);

      to.setCacheControl(from.getFirstHeaderOrNull(HttpHeaders.CACHE_CONTROL));
      to.setContentDisposition(from.getFirstHeaderOrNull("Content-Disposition"));
      to.setContentEncoding(from.getFirstHeaderOrNull(HttpHeaders.CONTENT_ENCODING));
      return to;
   }

   private void addAllHeadersTo(HttpResponse from, Metadata metadata) {
      metadata.getAllHeaders().putAll(from.getHeaders());
   }

   private void setContentTypeOrThrowException(HttpResponse from, Metadata metadata)
            throws HttpException {
      String contentType = from.getFirstHeaderOrNull(HttpHeaders.CONTENT_TYPE);
      if (contentType == null)
         throw new HttpException(HttpHeaders.CONTENT_TYPE + " not found in headers");
      else
         metadata.setContentType(contentType);
   }

   private void setContentLengthOrThrowException(HttpResponse from, Metadata metadata)
            throws HttpException {
      String contentLength = from.getFirstHeaderOrNull(HttpHeaders.CONTENT_LENGTH);
      if (contentLength == null)
         throw new HttpException(HttpHeaders.CONTENT_LENGTH + " not found in headers");
      else
         metadata.setSize(Long.parseLong(contentLength));
   }

   private void parseLastModifiedOrThrowException(HttpResponse from, Metadata metadata)
            throws HttpException {
      String lastModified = from.getFirstHeaderOrNull(HttpHeaders.LAST_MODIFIED);
      metadata.setLastModified(dateParser.rfc822DateParse(lastModified));
      if (metadata.getLastModified() == null)
         throw new HttpException("could not parse: " + HttpHeaders.LAST_MODIFIED + ": "
                  + lastModified);
   }

   private void addETagTo(HttpResponse from, Metadata metadata) {
      String eTagHeader = from.getFirstHeaderOrNull(S3Headers.AMZ_MD5);
      if (eTagHeader != null) {
         metadata.setETag(HttpUtils.fromHexString(eTagHeader));
      }
      String eTag = from.getFirstHeaderOrNull(HttpHeaders.ETAG);
      if (metadata.getETag() == null && eTag != null) {
         metadata.setETag(HttpUtils.fromHexString(eTag.replaceAll("\"", "")));
      }
   }

   private void addUserMetadataTo(HttpResponse from, Metadata metadata) {
      for (Entry<String, String> header : from.getHeaders().entries()) {
         if (header.getKey() != null && header.getKey().startsWith(S3Headers.USER_METADATA_PREFIX))
            metadata.getUserMetadata().put(header.getKey(), header.getValue());
      }
   }

}