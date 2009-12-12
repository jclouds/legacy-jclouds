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
package org.jclouds.blobstore.binders;

import static org.jclouds.blobstore.reference.BlobStoreConstants.PROPERTY_USER_METADATA_PREFIX;

import java.util.Map;
import java.util.Map.Entry;

import javax.inject.Inject;
import javax.inject.Named;

import org.jclouds.http.HttpRequest;
import org.jclouds.rest.Binder;

public class BindMapToHeadersWithPrefix implements Binder {
   private final String metadataPrefix;

   @Inject
   public BindMapToHeadersWithPrefix(@Named(PROPERTY_USER_METADATA_PREFIX) String metadataPrefix) {
      this.metadataPrefix = metadataPrefix;
   }

   @SuppressWarnings("unchecked")
   public void bindToRequest(HttpRequest request, Object entity) {
      Map<String, String> userMetadata = (Map<String, String>) entity;
      for (Entry<String, String> entry : userMetadata.entrySet()) {
         if (entry.getKey().startsWith(metadataPrefix)) {
            request.getHeaders().put(entry.getKey().toLowerCase(), entry.getValue());
         } else {
            request.getHeaders().put((metadataPrefix + entry.getKey()).toLowerCase(),
                     entry.getValue());
         }
      }
   }

}
