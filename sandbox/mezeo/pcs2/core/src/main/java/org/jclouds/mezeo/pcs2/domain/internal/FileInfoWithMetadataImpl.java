/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
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

package org.jclouds.mezeo.pcs2.domain.internal;

import java.net.URI;
import java.util.Date;
import java.util.Map;

import org.jclouds.mezeo.pcs2.domain.FileInfoWithMetadata;

/**
 * 
 * @author Adrian Cole
 * 
 */
public class FileInfoWithMetadataImpl extends FileInfoImpl implements FileInfoWithMetadata {
   private final Map<String, URI> metadataItems;

   public FileInfoWithMetadataImpl(URI url, String name, Date created, boolean inProject,
            Date modified, String owner, int version, boolean shared, Date accessed,
            boolean isPublic, String mimeType, long bytes, URI content, URI parent,
            URI permissions, URI tags, URI metadata, Map<String, URI> metadataItems, URI thumbnail) {
      super(url, name, created, inProject, modified, owner, version, shared, accessed, isPublic,
               mimeType, bytes, content, parent, permissions, tags, metadata, thumbnail);
      this.metadataItems = metadataItems;
   }

   public Map<String, URI> getMetadataItems() {
      return metadataItems;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = super.hashCode();
      result = prime * result + ((metadataItems == null) ? 0 : metadataItems.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (!super.equals(obj))
         return false;
      if (getClass() != obj.getClass())
         return false;
      FileInfoWithMetadataImpl other = (FileInfoWithMetadataImpl) obj;
      if (metadataItems == null) {
         if (other.metadataItems != null)
            return false;
      } else if (!metadataItems.equals(other.metadataItems))
         return false;
      return true;
   }
}
