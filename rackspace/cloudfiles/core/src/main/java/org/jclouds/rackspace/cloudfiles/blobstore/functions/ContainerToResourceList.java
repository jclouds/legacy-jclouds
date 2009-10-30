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
package org.jclouds.rackspace.cloudfiles.blobstore.functions;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.blobstore.domain.BlobMetadata;
import org.jclouds.blobstore.domain.BoundedSortedSet;
import org.jclouds.blobstore.domain.ResourceMetadata;
import org.jclouds.blobstore.domain.internal.BoundedTreeSet;
import org.jclouds.rackspace.cloudfiles.domain.ObjectInfo;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;

/**
 * @author Adrian Cole
 */
@Singleton
public class ContainerToResourceList implements
         Function<BoundedSortedSet<ObjectInfo>, BoundedSortedSet<? extends ResourceMetadata>> {
   private final ObjectToBlobMetadata object2blobMd;

   @Inject
   public ContainerToResourceList(ObjectToBlobMetadata object2blobMd) {
      this.object2blobMd = object2blobMd;
   }

   public BoundedSortedSet<? extends ResourceMetadata> apply(BoundedSortedSet<ObjectInfo> from) {
      return new BoundedTreeSet<ResourceMetadata>(Iterables.transform(Iterables.transform(from,
               object2blobMd), new Function<BlobMetadata, ResourceMetadata>() {
         public ResourceMetadata apply(BlobMetadata arg0) {
            return arg0;
         }
      }), from.getPath(), from.getMarker(), from.getMaxResults(), from.isTruncated());

   }
}