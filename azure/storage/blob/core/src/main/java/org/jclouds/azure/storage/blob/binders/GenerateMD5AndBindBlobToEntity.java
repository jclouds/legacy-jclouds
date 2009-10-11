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
package org.jclouds.azure.storage.blob.binders;

import static org.jclouds.blobstore.reference.BlobStoreConstants.PROPERTY_USER_METADATA_PREFIX;

import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Named;

import org.jclouds.blobstore.domain.Blob;
import org.jclouds.http.HttpRequest;

public class GenerateMD5AndBindBlobToEntity extends BindBlobToEntity {

   @Inject
   public GenerateMD5AndBindBlobToEntity(@Named(PROPERTY_USER_METADATA_PREFIX) String metadataPrefix) {
      super(metadataPrefix);
   }

   @Override
   public void bindToRequest(HttpRequest request, Object entity) {
      Blob<?> object = (Blob<?>) entity;
      if (object.getMetadata().getContentMD5() == null) {
         try {
            object.generateMD5();
         } catch (IOException e) {
            throw new RuntimeException("Could not generate MD5 for " + object.getKey(), e);
         }
      }
      super.bindToRequest(request, entity);
   }
}
