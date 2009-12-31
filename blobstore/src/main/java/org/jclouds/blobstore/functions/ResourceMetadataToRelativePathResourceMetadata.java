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
package org.jclouds.blobstore.functions;

import javax.inject.Singleton;

import org.jclouds.blobstore.domain.ResourceMetadata;
import org.jclouds.blobstore.domain.ResourceType;
import org.jclouds.blobstore.domain.internal.ResourceMetadataImpl;
import org.jclouds.blobstore.reference.BlobStoreConstants;

import com.google.common.base.Function;

@Singleton
public class ResourceMetadataToRelativePathResourceMetadata implements
         Function<ResourceMetadata, ResourceMetadata> {

   public ResourceMetadata apply(ResourceMetadata md) {
      String name = md.getName();
      for (String suffix : BlobStoreConstants.DIRECTORY_SUFFIXES) {
         if (name.endsWith(suffix))
            name = name.substring(0, name.length() - suffix.length());
      }
      return new ResourceMetadataImpl(ResourceType.RELATIVE_PATH, md.getId(), name, md
               .getLocation(), md.getETag(), md.getSize(), md.getLastModified(), md
               .getUserMetadata());
   }

}
