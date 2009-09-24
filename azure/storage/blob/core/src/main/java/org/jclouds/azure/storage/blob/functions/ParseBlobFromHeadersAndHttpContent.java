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

import org.jclouds.azure.storage.blob.domain.Blob;
import org.jclouds.azure.storage.blob.domain.BlobMetadata;
import org.jclouds.blobstore.functions.ParseBlobMetadataFromHeaders;

import javax.inject.Inject;

/**
 * Parses response headers and creates a new Azure Blob from them and the HTTP content.
 * 
 * @see ParseBlobMetadataFromHeaders
 * @author Adrian Cole
 */
public class ParseBlobFromHeadersAndHttpContent extends
         org.jclouds.blobstore.functions.ParseBlobFromHeadersAndHttpContent<BlobMetadata, Blob> {
   @Inject
   public ParseBlobFromHeadersAndHttpContent(
            ParseBlobMetadataFromHeaders<BlobMetadata> metadataParser,
            BlobFactory<BlobMetadata, Blob> blobFactory) {
      super(metadataParser, blobFactory);
   }
}