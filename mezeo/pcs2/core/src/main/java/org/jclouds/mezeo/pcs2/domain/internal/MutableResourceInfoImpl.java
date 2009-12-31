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
package org.jclouds.mezeo.pcs2.domain.internal;

import java.net.URI;
import java.util.Date;

import org.jclouds.blobstore.domain.ResourceType;
import org.jclouds.mezeo.pcs2.domain.MutableResourceInfo;
import org.jclouds.mezeo.pcs2.domain.ResourceInfo;

/**
 * 
 * @author Adrian Cole
 * 
 */
public class MutableResourceInfoImpl implements MutableResourceInfo {

   private ResourceType type;
   private URI url;
   private String name;
   private Date created;
   private Boolean inProject;
   private Date modified;
   private String owner;
   private Integer version;
   private Boolean shared;
   private Date accessed;
   private Long bytes;
   private URI tags;
   private URI metadata;
   private URI parent;

   public int compareTo(ResourceInfo o) {
      if (getName() == null)
         return -1;
      return (this == o) ? 0 : getName().compareTo(o.getName());
   }

   public ResourceType getType() {
      return type;
   }

   public void setType(ResourceType type) {
      this.type = type;
   }

   public URI getUrl() {
      return url;
   }

   public void setUrl(URI url) {
      this.url = url;
   }

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public Date getCreated() {
      return created;
   }

   public void setCreated(Date created) {
      this.created = created;
   }

   public Boolean isInProject() {
      return inProject;
   }

   public void setInProject(Boolean inProject) {
      this.inProject = inProject;
   }

   public Date getModified() {
      return modified;
   }

   public void setModified(Date modified) {
      this.modified = modified;
   }

   public String getOwner() {
      return owner;
   }

   public void setOwner(String owner) {
      this.owner = owner;
   }

   public Integer getVersion() {
      return version;
   }

   public void setVersion(Integer version) {
      this.version = version;
   }

   public Boolean isShared() {
      return shared;
   }

   public void setShared(Boolean shared) {
      this.shared = shared;
   }

   public Date getAccessed() {
      return accessed;
   }

   public void setAccessed(Date accessed) {
      this.accessed = accessed;
   }

   public Long getBytes() {
      return bytes;
   }

   public void setBytes(Long bytes) {
      this.bytes = bytes;
   }

   public URI getTags() {
      return tags;
   }

   public void setTags(URI tags) {
      this.tags = tags;
   }

   public URI getMetadata() {
      return metadata;
   }

   public void setMetadata(URI metadata) {
      this.metadata = metadata;
   }

   public URI getParent() {
      return parent;
   }

   public void setParent(URI parent) {
      this.parent = parent;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((accessed == null) ? 0 : accessed.hashCode());
      result = prime * result + ((bytes == null) ? 0 : bytes.hashCode());
      result = prime * result + ((created == null) ? 0 : created.hashCode());
      result = prime * result + ((inProject == null) ? 0 : inProject.hashCode());
      result = prime * result + ((metadata == null) ? 0 : metadata.hashCode());
      result = prime * result + ((modified == null) ? 0 : modified.hashCode());
      result = prime * result + ((name == null) ? 0 : name.hashCode());
      result = prime * result + ((owner == null) ? 0 : owner.hashCode());
      result = prime * result + ((parent == null) ? 0 : parent.hashCode());
      result = prime * result + ((shared == null) ? 0 : shared.hashCode());
      result = prime * result + ((tags == null) ? 0 : tags.hashCode());
      result = prime * result + ((type == null) ? 0 : type.hashCode());
      result = prime * result + ((url == null) ? 0 : url.hashCode());
      result = prime * result + ((version == null) ? 0 : version.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      MutableResourceInfoImpl other = (MutableResourceInfoImpl) obj;
      if (accessed == null) {
         if (other.accessed != null)
            return false;
      } else if (!accessed.equals(other.accessed))
         return false;
      if (bytes == null) {
         if (other.bytes != null)
            return false;
      } else if (!bytes.equals(other.bytes))
         return false;
      if (created == null) {
         if (other.created != null)
            return false;
      } else if (!created.equals(other.created))
         return false;
      if (inProject == null) {
         if (other.inProject != null)
            return false;
      } else if (!inProject.equals(other.inProject))
         return false;
      if (metadata == null) {
         if (other.metadata != null)
            return false;
      } else if (!metadata.equals(other.metadata))
         return false;
      if (modified == null) {
         if (other.modified != null)
            return false;
      } else if (!modified.equals(other.modified))
         return false;
      if (name == null) {
         if (other.name != null)
            return false;
      } else if (!name.equals(other.name))
         return false;
      if (owner == null) {
         if (other.owner != null)
            return false;
      } else if (!owner.equals(other.owner))
         return false;
      if (parent == null) {
         if (other.parent != null)
            return false;
      } else if (!parent.equals(other.parent))
         return false;
      if (shared == null) {
         if (other.shared != null)
            return false;
      } else if (!shared.equals(other.shared))
         return false;
      if (tags == null) {
         if (other.tags != null)
            return false;
      } else if (!tags.equals(other.tags))
         return false;
      if (type == null) {
         if (other.type != null)
            return false;
      } else if (!type.equals(other.type))
         return false;
      if (url == null) {
         if (other.url != null)
            return false;
      } else if (!url.equals(other.url))
         return false;
      if (version == null) {
         if (other.version != null)
            return false;
      } else if (!version.equals(other.version))
         return false;
      return true;
   }

}
