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

package org.jclouds.chef.domain;

import java.util.Date;
import java.util.Set;

import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.google.gson.annotations.SerializedName;

/**
 * Sandbox object.
 * 
 * @author Adrian Cole
 */
public class Sandbox {

   @SerializedName("_rev")
   private String rev;
   @SerializedName("is_completed")
   private boolean isCompleted;
   @SerializedName("create_time")
   private Date createTime;
   private Set<String> checksums = Sets.newLinkedHashSet();
   private String name;
   private String guid;

   // internal
   @SuppressWarnings("unused")
   @SerializedName("json_class")
   private String _jsonClass = "Chef::Sandbox";
   @SerializedName("chef_type")
   @SuppressWarnings("unused")
   private String _chefType = "sandbox";

   public Sandbox(String rev, boolean isCompleted, Date createTime, Iterable<String> checksums, String name, String guid) {
      this.rev = rev;
      this.isCompleted = isCompleted;
      this.createTime = createTime;
      Iterables.addAll(this.checksums, checksums);
      this.name = name;
      this.guid = guid;
   }

   public Sandbox() {

   }

   public String getRev() {
      return rev;
   }

   public boolean isCompleted() {
      return isCompleted;
   }

   public Date getCreateTime() {
      return createTime;
   }

   public Set<String> getChecksums() {
      return checksums;
   }

   public String getName() {
      return name;
   }

   public String getGuid() {
      return guid;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((checksums == null) ? 0 : checksums.hashCode());
      result = prime * result + ((createTime == null) ? 0 : createTime.hashCode());
      result = prime * result + ((guid == null) ? 0 : guid.hashCode());
      result = prime * result + (isCompleted ? 1231 : 1237);
      result = prime * result + ((name == null) ? 0 : name.hashCode());
      result = prime * result + ((rev == null) ? 0 : rev.hashCode());
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
      Sandbox other = (Sandbox) obj;
      if (checksums == null) {
         if (other.checksums != null)
            return false;
      } else if (!checksums.equals(other.checksums))
         return false;
      if (createTime == null) {
         if (other.createTime != null)
            return false;
      } else if (!createTime.equals(other.createTime))
         return false;
      if (guid == null) {
         if (other.guid != null)
            return false;
      } else if (!guid.equals(other.guid))
         return false;
      if (isCompleted != other.isCompleted)
         return false;
      if (name == null) {
         if (other.name != null)
            return false;
      } else if (!name.equals(other.name))
         return false;
      if (rev == null) {
         if (other.rev != null)
            return false;
      } else if (!rev.equals(other.rev))
         return false;
      return true;
   }

   @Override
   public String toString() {
      return "Sandbox [checksums=" + checksums + ", createTime=" + createTime + ", guid=" + guid + ", isCompleted="
            + isCompleted + ", name=" + name + ", rev=" + rev + "]";
   }
}