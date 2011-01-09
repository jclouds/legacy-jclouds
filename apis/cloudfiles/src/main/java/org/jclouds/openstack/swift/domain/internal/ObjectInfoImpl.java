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

package org.jclouds.openstack.swift.domain.internal;

import java.util.Arrays;
import java.util.Date;

import org.jclouds.openstack.swift.domain.ObjectInfo;

public class ObjectInfoImpl implements ObjectInfo {
   String name;
   byte[] hash;
   long bytes;
   String content_type;
   Date last_modified;

   ObjectInfoImpl() {

   }

   public int compareTo(ObjectInfoImpl o) {
      return (this == o) ? 0 : name.compareTo(o.name);
   }

   public Long getBytes() {
      return bytes;
   }

   public String getContentType() {
      return content_type;
   }

   public byte[] getHash() {
      return hash;
   }

   public Date getLastModified() {
      return last_modified;
   }

   public String getName() {
      return name;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + (int) (bytes ^ (bytes >>> 32));
      result = prime * result
            + ((content_type == null) ? 0 : content_type.hashCode());
      result = prime * result + Arrays.hashCode(hash);
      result = prime * result
            + ((last_modified == null) ? 0 : last_modified.hashCode());
      result = prime * result + ((name == null) ? 0 : name.hashCode());
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
      ObjectInfoImpl other = (ObjectInfoImpl) obj;
      if (bytes != other.bytes)
         return false;
      if (content_type == null) {
         if (other.content_type != null)
            return false;
      } else if (!content_type.equals(other.content_type))
         return false;
      if (!Arrays.equals(hash, other.hash))
         return false;
      if (last_modified == null) {
         if (other.last_modified != null)
            return false;
      } else if (!last_modified.equals(other.last_modified))
         return false;
      if (name == null) {
         if (other.name != null)
            return false;
      } else if (!name.equals(other.name))
         return false;
      return true;
   }

   public int compareTo(ObjectInfo o) {
      return (this == o) ? 0 : getName().compareTo(o.getName());
   }

   @Override
   public String toString() {
      return "ObjectInfoImpl [bytes=" + bytes + ", content_flavor="
            + content_type + ", hash=" + Arrays.asList(hash)
            + ", last_modified=" + last_modified.getTime() + ", name=" + name
            + "]";
   }
}
