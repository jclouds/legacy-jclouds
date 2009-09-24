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

import static org.jclouds.blobstore.reference.BlobStoreConstants.PROPERTY_USER_METADATA_PREFIX;

import javax.ws.rs.core.HttpHeaders;

import org.jclouds.aws.s3.domain.ObjectMetadata;
import org.jclouds.aws.s3.reference.S3Headers;
import org.jclouds.blobstore.functions.ParseBlobMetadataFromHeaders;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.HttpUtils;
import org.jclouds.util.DateService;

import com.google.common.annotations.VisibleForTesting;
import com.google.inject.Inject;
import com.google.inject.name.Named;

/**
 * This parses @{link {@link org.jclouds.aws.s3.domain.ObjectMetadata} from HTTP headers.
 * 
 * @see <a href="http://docs.amazonwebservices.com/AmazonS3/latest/RESTObjectGET.html" />
 * @author Adrian Cole
 */
public class ParseObjectMetadataFromHeaders extends ParseBlobMetadataFromHeaders<ObjectMetadata> {

   @Inject
   public ParseObjectMetadataFromHeaders(DateService dateParser,
            @Named(PROPERTY_USER_METADATA_PREFIX) String metadataPrefix,
            BlobMetadataFactory<ObjectMetadata> metadataFactory) {
      super(dateParser, metadataPrefix, metadataFactory);
   }

   /**
    * parses the http response headers to create a new
    * {@link org.jclouds.aws.s3.domain.ObjectMetadata} object.
    */
   @Override
   public ObjectMetadata apply(HttpResponse from) {
      ObjectMetadata to = super.apply(from);
      to.setCacheControl(from.getFirstHeaderOrNull(HttpHeaders.CACHE_CONTROL));
      to.setContentDisposition(from.getFirstHeaderOrNull("Content-Disposition"));
      to.setContentEncoding(from.getFirstHeaderOrNull(HttpHeaders.CONTENT_ENCODING));
      return to;
   }

   /**
    * ETag == Content-MD5
    */
   @VisibleForTesting
   protected void addETagTo(HttpResponse from, ObjectMetadata metadata) {
      super.addETagTo(from, metadata);
      if (metadata.getETag() == null) {
         String eTagHeader = from.getFirstHeaderOrNull(S3Headers.AMZ_MD5);
         if (eTagHeader != null) {
            metadata.setETag(HttpUtils.fromHexString(eTagHeader));
         }
      }
      metadata.setContentMD5(metadata.getETag());
   }

}