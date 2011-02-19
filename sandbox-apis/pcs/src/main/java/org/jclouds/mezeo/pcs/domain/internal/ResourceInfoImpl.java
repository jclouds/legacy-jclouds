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

package org.jclouds.mezeo.pcs.domain.internal;

import java.net.URI;
import java.util.Date;

import org.jclouds.blobstore.domain.StorageType;
import org.jclouds.mezeo.pcs.domain.ResourceInfo;

/**
 * 
 * @author Adrian Cole
 * 
 */
public class ResourceInfoImpl implements ResourceInfo {

   private final StorageType type;
   private final URI url;
   private final String name;
   private final Date created;
   private final boolean inProject;
   private final Date modified;
   private final String owner;
   private final int version;
   private final boolean shared;
   private final Date accessed;
   private final long bytes;
   private final URI tags;
   private final URI metadata;
   private final URI parent;

   protected ResourceInfoImpl(StorageType type, URI url, String name, Date created,
            boolean inProject, Date modified, String owner, int version, boolean shared,
            Date accessed, long bytes, URI tags, URI metadata, URI parent) {
      super();
      this.type = type;
      this.url = url;
      this.name = name;
      this.created = created;
      this.inProject = inProject;
      this.modified = modified;
      this.owner = owner;
      this.version = version;
      this.shared = shared;
      this.accessed = accessed;
      this.bytes = bytes;
      this.tags = tags;
      this.metadata = metadata;
      this.parent = parent;
   }

   public int compareTo(ResourceInfo o) {
      if (getName() == null)
         return -1;
      if (o.getName() == null)
         return 1;
      return (this == o) ? 0 : getName().compareTo(o.getName());
   }

   public StorageType getType() {
      return type;
   }

   public URI getUrl() {
      return url;
   }

   public String getName() {
      return name;
   }

   public Date getCreated() {
      return created;
   }

   public Boolean isInProject() {
      return inProject;
   }

   public Date getModified() {
      return modified;
   }

   public String getOwner() {
      return owner;
   }

   public Integer getVersion() {
      return version;
   }

   public Boolean isShared() {
      return shared;
   }

   public Date getAccessed() {
      return accessed;
   }

   public Long getBytes() {
      return bytes;
   }

   public URI getTags() {
      return tags;
   }

   public URI getMetadata() {
      return metadata;
   }

   public URI getParent() {
      return parent;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((accessed == null) ? 0 : accessed.hashCode());
      result = prime * result + (int) (bytes ^ (bytes >>> 32));
      result = prime * result + ((created == null) ? 0 : created.hashCode());
      result = prime * result + (inProject ? 1231 : 1237);
      result = prime * result + ((metadata == null) ? 0 : metadata.hashCode());
      result = prime * result + ((modified == null) ? 0 : modified.hashCode());
      result = prime * result + ((name == null) ? 0 : name.hashCode());
      result = prime * result + ((owner == null) ? 0 : owner.hashCode());
      result = prime * result + ((parent == null) ? 0 : parent.hashCode());
      result = prime * result + (shared ? 1231 : 1237);
      result = prime * result + ((tags == null) ? 0 : tags.hashCode());
      result = prime * result + ((type == null) ? 0 : type.hashCode());
      result = prime * result + ((url == null) ? 0 : url.hashCode());
      result = prime * result + version;
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
      ResourceInfoImpl other = (ResourceInfoImpl) obj;
      if (accessed == null) {
         if (other.accessed != null)
            return false;
      } else if (!accessed.equals(other.accessed))
         return false;
      if (bytes != other.bytes)
         return false;
      if (created == null) {
         if (other.created != null)
            return false;
      } else if (!created.equals(other.created))
         return false;
      if (inProject != other.inProject)
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
      if (shared != other.shared)
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
      if (version != other.version)
         return false;
      return true;
   }

}
