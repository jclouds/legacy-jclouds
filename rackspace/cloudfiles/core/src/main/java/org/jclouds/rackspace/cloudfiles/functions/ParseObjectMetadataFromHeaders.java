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
package org.jclouds.rackspace.cloudfiles.functions;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;

import org.jclouds.blobstore.domain.MutableBlobMetadata;
import org.jclouds.blobstore.functions.ParseSystemAndUserMetadataFromHeaders;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.HttpUtils;
import org.jclouds.rackspace.cloudfiles.reference.CloudFilesConstants;
import org.jclouds.util.DateService;

import com.google.common.annotations.VisibleForTesting;

/**
 * @author Adrian Cole
 */
public class ParseObjectMetadataFromHeaders extends ParseSystemAndUserMetadataFromHeaders {

   @Inject
   public ParseObjectMetadataFromHeaders(Provider<MutableBlobMetadata> metadataFactory,
            DateService dateParser,
            @Named(CloudFilesConstants.PROPERTY_CLOUDFILES_METADATA_PREFIX) String metadataPrefix) {
      super(metadataFactory, dateParser, metadataPrefix);
   }

   @VisibleForTesting
   /**
    * ETag == Content-MD5
    */
   protected void addETagTo(HttpResponse from, MutableBlobMetadata metadata) {
      super.addETagTo(from, metadata);
      if (metadata.getETag() == null) {
         // etag comes back incorrect case
         String eTagHeader = from.getFirstHeaderOrNull("Etag");
         if (eTagHeader != null) {
            metadata.setETag(eTagHeader);
         }
      }
      metadata.setContentMD5(HttpUtils.fromHexString(metadata.getETag()));
   }
}