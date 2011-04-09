/**
 *
 * Copyright (C) 2011 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.mezeo.pcs.domain.internal;

import java.net.URI;

import javax.ws.rs.core.MediaType;

import org.jclouds.blobstore.domain.StorageType;
import org.jclouds.mezeo.pcs.domain.MutableFileInfo;

/**
 * 
 * @author Adrian Cole
 * 
 */
public class MutableFileInfoImpl extends MutableResourceInfoImpl implements
         MutableFileInfo {

   private Boolean isPublic;
   private String mimeType;
   private URI content;
   private URI permissions;
   private URI thumbnail;

   public MutableFileInfoImpl() {
      setType(StorageType.BLOB);
      setMimeType(MediaType.APPLICATION_OCTET_STREAM);
   }

   public String getMimeType() {
      return mimeType;
   }

   public URI getContent() {
      return content;
   }

   public URI getPermissions() {
      return permissions;
   }

   public URI getThumbnail() {
      return thumbnail;
   }

   public void setPublic(Boolean isPublic) {
      this.isPublic = isPublic;
   }

   public Boolean isPublic() {
      return isPublic;
   }

   public void setMimeType(String mimeType) {
      this.mimeType = mimeType;
   }

   public void setContent(URI content) {
      this.content = content;
   }

   public void setPermissions(URI permissions) {
      this.permissions = permissions;
   }

   public void setThumbnail(URI thumbnail) {
      this.thumbnail = thumbnail;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = super.hashCode();
      result = prime * result + ((content == null) ? 0 : content.hashCode());
      result = prime * result + ((isPublic == null) ? 0 : isPublic.hashCode());
      result = prime * result + ((mimeType == null) ? 0 : mimeType.hashCode());
      result = prime * result + ((permissions == null) ? 0 : permissions.hashCode());
      result = prime * result + ((thumbnail == null) ? 0 : thumbnail.hashCode());
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
      MutableFileInfoImpl other = (MutableFileInfoImpl) obj;
      if (content == null) {
         if (other.content != null)
            return false;
      } else if (!content.equals(other.content))
         return false;
      if (isPublic == null) {
         if (other.isPublic != null)
            return false;
      } else if (!isPublic.equals(other.isPublic))
         return false;
      if (mimeType == null) {
         if (other.mimeType != null)
            return false;
      } else if (!mimeType.equals(other.mimeType))
         return false;
      if (permissions == null) {
         if (other.permissions != null)
            return false;
      } else if (!permissions.equals(other.permissions))
         return false;
      if (thumbnail == null) {
         if (other.thumbnail != null)
            return false;
      } else if (!thumbnail.equals(other.thumbnail))
         return false;
      return true;
   }
}
