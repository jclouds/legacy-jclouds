/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */
package org.jclouds.azure.storage.blob.blobstore.functions;

import java.util.SortedSet;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.azure.storage.blob.domain.ListBlobsResponse;
import org.jclouds.blobstore.domain.ListContainerResponse;
import org.jclouds.blobstore.domain.StorageMetadata;
import org.jclouds.blobstore.domain.internal.ListContainerResponseImpl;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

/**
 * @author Adrian Cole
 */
@Singleton
public class ListBlobsResponseToResourceList implements
         Function<ListBlobsResponse, ListContainerResponse<? extends StorageMetadata>> {
   private final BlobPropertiesToBlobMetadata object2blobMd;
   private final CommonPrefixesToResourceMetadata prefix2ResourceMd;

   @Inject
   public ListBlobsResponseToResourceList(BlobPropertiesToBlobMetadata object2blobMd,
            CommonPrefixesToResourceMetadata prefix2ResourceMd) {
      this.object2blobMd = object2blobMd;
      this.prefix2ResourceMd = prefix2ResourceMd;
   }

   public ListContainerResponse<? extends StorageMetadata> apply(ListBlobsResponse from) {
      SortedSet<StorageMetadata> contents = Sets.newTreeSet(Iterables.concat(Iterables.transform(
               from, object2blobMd), prefix2ResourceMd.apply(from.getBlobPrefixes())));
      return new ListContainerResponseImpl<StorageMetadata>(contents, from.getPrefix(), from
               .getMarker(), from.getMaxResults(), from.size() == from.getMaxResults());

   }
}