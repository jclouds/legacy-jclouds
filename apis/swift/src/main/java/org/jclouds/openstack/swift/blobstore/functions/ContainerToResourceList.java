/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.openstack.swift.blobstore.functions;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.blobstore.domain.BlobMetadata;
import org.jclouds.blobstore.domain.PageSet;
import org.jclouds.blobstore.domain.StorageMetadata;
import org.jclouds.blobstore.domain.StorageType;
import org.jclouds.blobstore.domain.internal.PageSetImpl;
import org.jclouds.blobstore.domain.internal.StorageMetadataImpl;
import org.jclouds.openstack.swift.domain.ObjectInfo;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;

/**
 * @author Adrian Cole
 */
@Singleton
public class ContainerToResourceList implements Function<PageSet<ObjectInfo>, PageSet<? extends StorageMetadata>> {
   private final ObjectToBlobMetadata object2blobMd;

   @Inject
   public ContainerToResourceList(ObjectToBlobMetadata object2blobMd) {
      this.object2blobMd = object2blobMd;
   }

   public PageSet<? extends StorageMetadata> apply(PageSet<ObjectInfo> from) {
      return new PageSetImpl<StorageMetadata>(Iterables.transform(Iterables.transform(from, object2blobMd),
               new Function<BlobMetadata, StorageMetadata>() {
                  public StorageMetadata apply(BlobMetadata input) {
                     if (input.getContentMetadata().getContentType().equals("application/directory")) {
                        return new StorageMetadataImpl(StorageType.RELATIVE_PATH, input.getProviderId(), input
                                 .getName(), input.getLocation(), input.getUri(), input.getETag(),
                                 input.getCreationDate(), input.getLastModified(), input.getUserMetadata());
                     }
                     return input;
                  }
               }), from.getNextMarker());

   }
}
