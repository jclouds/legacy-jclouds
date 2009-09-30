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

import org.joda.time.DateTime;

/**
 * 
 * @author Adrian Cole
 * 
 */
public class ContainerMetadata extends org.jclouds.blobstore.domain.ContainerMetadata implements
         PCSObject {
   private URI url;
   private DateTime created;
   private DateTime lastModified;
   private DateTime accessed;
   private String owner;
   private boolean isShared;
   private boolean isInProject;
   private int version;
   private long bytes;

   @Override
   public String toString() {
      return "ContainerMetadata [name=" + name + ", url=" + url + ", accessed=" + accessed
               + ", bytes=" + bytes + ", created=" + created + ", isInProject=" + isInProject
               + ", isShared=" + isShared + ", lastModified=" + lastModified + ", owner=" + owner
               + ", version=" + version + "]";
   }

   @Override
   public int hashCode() {
      int prime = 31;
      int result = super.hashCode();
      result = prime * result + ((accessed == null) ? 0 : accessed.hashCode());
      result = prime * result + (int) (bytes ^ (bytes >>> 32));
      result = prime * result + ((created == null) ? 0 : created.hashCode());
      result = prime * result + (isInProject ? 1231 : 1237);
      result = prime * result + (isShared ? 1231 : 1237);
      result = prime * result + ((lastModified == null) ? 0 : lastModified.hashCode());
      result = prime * result + ((owner == null) ? 0 : owner.hashCode());
      result = prime * result + ((url == null) ? 0 : url.hashCode());
      result = prime * result + version;
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
      ContainerMetadata other = (ContainerMetadata) obj;
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
      if (isInProject != other.isInProject)
         return false;
      if (isShared != other.isShared)
         return false;
      if (lastModified == null) {
         if (other.lastModified != null)
            return false;
      } else if (!lastModified.equals(other.lastModified))
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
      if (version != other.version)
         return false;
      return true;
   }

   public ContainerMetadata() {
      super();
   }

   public ContainerMetadata(String name, URI url, DateTime created, DateTime lastModified,
            DateTime accessed, String owner, boolean isShared, boolean isInProject, int version,
            long bytes) {
      super(name);
      this.url = url;
      this.created = created;
      this.lastModified = lastModified;
      this.accessed = accessed;
      this.owner = owner;
      this.isShared = isShared;
      this.isInProject = isInProject;
      this.version = version;
      this.bytes = bytes;
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

   public boolean isShared() {
      return isShared;
   }

   public boolean isInProject() {
      return isInProject;
   }

   public int getVersion() {
      return version;
   }

   public long getSize() {
      return bytes;
   }

}
