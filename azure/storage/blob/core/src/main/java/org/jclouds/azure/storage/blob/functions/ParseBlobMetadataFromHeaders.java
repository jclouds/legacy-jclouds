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
package org.jclouds.azure.storage.blob.functions;

import static org.jclouds.blobstore.reference.BlobStoreConstants.PROPERTY_USER_METADATA_PREFIX;

import javax.ws.rs.core.HttpHeaders;

import org.jclouds.azure.storage.blob.domain.BlobMetadata;
import org.jclouds.http.HttpResponse;
import org.jclouds.util.DateService;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * This parses @{link {@link org.jclouds.azure.storage.blob.domain.BlobMetadata} from HTTP headers.
 * 
 * 
 * @author Adrian Cole
 */
public class ParseBlobMetadataFromHeaders extends org.jclouds.blobstore.functions.ParseSystemAndUserMetadataFromHeaders<BlobMetadata> {

   @Inject
   public ParseBlobMetadataFromHeaders(DateService dateParser,
            @Named(PROPERTY_USER_METADATA_PREFIX) String metadataPrefix,
            BlobMetadataFactory<BlobMetadata> metadataFactory) {
      super(dateParser, metadataPrefix, metadataFactory);
   }

   /**
    * parses the http response headers to create a new
    * {@link org.jclouds.azure.storage.blob.domain.BlobMetadata} object.
    */
   @Override
   public BlobMetadata apply(HttpResponse from) {
      BlobMetadata to = super.apply(from);
      to.setContentLanguage(from.getFirstHeaderOrNull(HttpHeaders.CONTENT_LANGUAGE));
      to.setContentEncoding(from.getFirstHeaderOrNull(HttpHeaders.CONTENT_ENCODING));
      return to;
   }

}