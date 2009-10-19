/**
 *
 * Copyright (C) 2009 Global Cloud Specialists, Inc. <info@globalcloudspecialists.com>
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
package org.jclouds.mezeo.pcs2.domain;

import java.net.URI;

import org.jclouds.blobstore.internal.BlobMetadataImpl;
import org.jclouds.mezeo.pcs2.util.PCSUtils;
import org.joda.time.DateTime;

/**
 * 
 * @author Adrian Cole
 * 
 */
public class FileMetadata extends BlobMetadataImpl {
   /** The serialVersionUID */
   private static final long serialVersionUID = 1L;
   private URI url;
   private DateTime created;
   private DateTime accessed;
   private String owner;
   private Boolean isShared;
   private Boolean isInProject;
   private Integer version;
   private Boolean isPublic;

   @Override
   public String toString() {
      return "FileMetadata [key=" + name + ", created=" + created + ", isInProject=" + isInProject
               + ", isPublic=" + isPublic + ", isShared=" + isShared + ", owner=" + owner
               + ", url=" + url + ", version=" + version + ", allHeaders=" + allHeaders
               + ", dataType=" + dataType + ", eTag=" + eTag + ", accessed="
               + accessed + ", lastModified=" + lastModified + ", size=" + size + ", userMetadata="
               + userMetadata + "]";
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = super.hashCode();
      result = prime * result + ((accessed == null) ? 0 : accessed.hashCode());
      result = prime * result + ((created == null) ? 0 : created.hashCode());
      result = prime * result + ((isInProject == null) ? 0 : isInProject.hashCode());
      result = prime * result + ((isPublic == null) ? 0 : isPublic.hashCode());
      result = prime * result + ((isShared == null) ? 0 : isShared.hashCode());
      result = prime * result + ((owner == null) ? 0 : owner.hashCode());
      result = prime * result + ((url == null) ? 0 : url.hashCode());
      result = prime * result + ((version == null) ? 0 : version.hashCode());
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
      FileMetadata other = (FileMetadata) obj;
      if (accessed == null) {
         if (other.accessed != null)
            return false;
      } else if (!accessed.equals(other.accessed))
         return false;
      if (created == null) {
         if (other.created != null)
            return false;
      } else if (!created.equals(other.created))
         return false;
      if (isInProject == null) {
         if (other.isInProject != null)
            return false;
      } else if (!isInProject.equals(other.isInProject))
         return false;
      if (isPublic == null) {
         if (other.isPublic != null)
            return false;
      } else if (!isPublic.equals(other.isPublic))
         return false;
      if (isShared == null) {
         if (other.isShared != null)
            return false;
      } else if (!isShared.equals(other.isShared))
         return false;
      if (owner == null) {
         if (other.owner != null)
            return false;
      } else if (!owner.equals(other.owner))
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

   public FileMetadata(String name, URI url, DateTime created, DateTime lastModified,
            DateTime accessed, String owner, boolean isShared, boolean isInProject, int version,
            long bytes, String contentType, boolean isPublic) {
      super(name);
      setLastModified(lastModified);
      setSize(bytes);
      setContentType(contentType);
      this.url = url;
      this.created = created;
      this.accessed = accessed;
      this.owner = owner;
      this.isShared = isShared;
      this.isInProject = isInProject;
      this.version = version;
      this.isPublic = isPublic;
      setETag(PCSUtils.getId(url));
   }

   public FileMetadata(String key) {
      super(key);
   }

   public FileMetadata() {
      super();
   }

   public URI getUrl() {
      return url;
   }

   public DateTime getCreated() {
      return created;
   }

   public DateTime getLastModified() {
      return lastModified;
   }

   public DateTime getAccessed() {
      return accessed;
   }

   public String getOwner() {
      return owner;
   }

   public Boolean isShared() {
      return isShared;
   }

   public Boolean isInProject() {
      return isInProject;
   }

   public Integer getVersion() {
      return version;
   }

   public Boolean isPublic() {
      return isPublic;
   }

}
