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
package org.jclouds.azure.storage.blob.blobstore.functions;

import javax.inject.Singleton;

import org.jclouds.azure.storage.blob.domain.ListableBlobProperties;
import org.jclouds.blobstore.domain.MutableBlobMetadata;
import org.jclouds.blobstore.domain.ResourceType;
import org.jclouds.blobstore.domain.internal.MutableBlobMetadataImpl;

import com.google.common.base.Function;

/**
 * @author Adrian Cole
 */
@Singleton
public class ListableBlobPropertiesToBlobMetadata<T extends ListableBlobProperties> implements
         Function<T, MutableBlobMetadata> {
   public MutableBlobMetadata apply(T from) {
      MutableBlobMetadata to = new MutableBlobMetadataImpl();
      if (from.getContentType() != null)
         to.setContentType(from.getContentType());
      to.setETag(from.getETag());
      to.setName(from.getName());
      to.setSize(from.getSize());
      to.setType(ResourceType.BLOB);
      if (from.getContentType() != null && from.getContentType().equals("application/directory")) {
         to.setType(ResourceType.RELATIVE_PATH);
      }
      return to;
   }
}