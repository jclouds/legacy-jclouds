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
package org.jclouds.aws.s3.functions;

import javax.inject.Inject;
import javax.ws.rs.core.HttpHeaders;

import org.jclouds.aws.s3.blobstore.functions.BlobToObjectMetadata;
import org.jclouds.aws.s3.domain.MutableObjectMetadata;
import org.jclouds.aws.s3.reference.S3Headers;
import org.jclouds.blobstore.domain.BlobMetadata;
import org.jclouds.blobstore.functions.ParseSystemAndUserMetadataFromHeaders;
import org.jclouds.http.HttpResponse;
import org.jclouds.rest.InvocationContext;
import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.jclouds.util.EncryptionService;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;

/**
 * This parses @{link {@link org.jclouds.aws.s3.domain.internal.MutableObjectMetadata} from HTTP
 * headers.
 * 
 * @see <a href="http://docs.amazonwebservices.com/AmazonS3/latest/RESTObjectGET.html" />
 * @author Adrian Cole
 */
public class ParseObjectMetadataFromHeaders implements
         Function<HttpResponse, MutableObjectMetadata>, InvocationContext {
   private final ParseSystemAndUserMetadataFromHeaders blobMetadataParser;
   private final BlobToObjectMetadata blobToObjectMetadata;
   private final EncryptionService encryptionService;

   @Inject
   public ParseObjectMetadataFromHeaders(ParseSystemAndUserMetadataFromHeaders blobMetadataParser,
            BlobToObjectMetadata blobToObjectMetadata, EncryptionService encryptionService) {
      this.blobMetadataParser = blobMetadataParser;
      this.blobToObjectMetadata = blobToObjectMetadata;
      this.encryptionService = encryptionService;
   }

   /**
    * parses the http response headers to create a new
    * {@link org.jclouds.aws.s3.domain.internal.MutableObjectMetadata} object.
    */
   public MutableObjectMetadata apply(HttpResponse from) {
      BlobMetadata base = blobMetadataParser.apply(from);
      MutableObjectMetadata to = blobToObjectMetadata.apply(base);
      addETagTo(from, to);
      to.setContentMD5(encryptionService.fromHexString(to.getETag().replaceAll("\"", "")));
      to.setCacheControl(from.getFirstHeaderOrNull(HttpHeaders.CACHE_CONTROL));
      to.setContentDisposition(from.getFirstHeaderOrNull("Content-Disposition"));
      to.setContentEncoding(from.getFirstHeaderOrNull(HttpHeaders.CONTENT_ENCODING));
      return to;
   }

   /**
    * ETag == Content-MD5
    */
   @VisibleForTesting
   protected void addETagTo(HttpResponse from, MutableObjectMetadata metadata) {
      if (metadata.getETag() == null) {
         String eTagHeader = from.getFirstHeaderOrNull(S3Headers.AMZ_MD5);
         if (eTagHeader != null) {
            metadata.setETag(eTagHeader);
         }
      }
   }

   public void setContext(GeneratedHttpRequest<?> request) {
      blobMetadataParser.setContext(request);
   }

}