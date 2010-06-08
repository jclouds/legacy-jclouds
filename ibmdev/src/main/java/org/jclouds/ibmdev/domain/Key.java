/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the ;License;);
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an ;AS IS; BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */
package org.jclouds.ibmdev.domain;

import java.util.Date;
import java.util.Set;

import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.google.gson.annotations.SerializedName;

/**
 * 
 * The current state of a public or private key.
 * 
 * @author Adrian Cole
 */
public class Key {
   @SerializedName("default")
   private boolean isDefault;
   private Set<String> instanceIds = Sets.newLinkedHashSet();
   private String keyMaterial;
   @SerializedName("keyName")
   private String name;
   private Date lastModifiedTime;

   public Key(boolean isDefault, Iterable<String> instanceIds, String keyMaterial, String name,
            Date lastModifiedTime) {
      this.isDefault = isDefault;
      Iterables.addAll(this.instanceIds, instanceIds);
      this.keyMaterial = keyMaterial;
      this.name = name;
      this.lastModifiedTime = lastModifiedTime;
   }

   public Key() {

   }

   public boolean isDefault() {
      return isDefault;
   }

   public void setDefault(boolean isDefault) {
      this.isDefault = isDefault;
   }

   public String getKeyMaterial() {
      return keyMaterial;
   }

   public void setKeyMaterial(String keyMaterial) {
      this.keyMaterial = keyMaterial;
   }

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public Date getLastModifiedTime() {
      return lastModifiedTime;
   }

   public void setLastModifiedTime(Date lastModifiedTime) {
      this.lastModifiedTime = lastModifiedTime;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((instanceIds == null) ? 0 : instanceIds.hashCode());
      result = prime * result + (isDefault ? 1231 : 1237);
      result = prime * result + ((keyMaterial == null) ? 0 : keyMaterial.hashCode());
      result = prime * result + ((name == null) ? 0 : name.hashCode());
      result = prime * result + ((lastModifiedTime == null) ? 0 : lastModifiedTime.hashCode());
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
      Key other = (Key) obj;
      if (instanceIds == null) {
         if (other.instanceIds != null)
            return false;
      } else if (!instanceIds.equals(other.instanceIds))
         return false;
      if (isDefault != other.isDefault)
         return false;
      if (keyMaterial == null) {
         if (other.keyMaterial != null)
            return false;
      } else if (!keyMaterial.equals(other.keyMaterial))
         return false;
      if (name == null) {
         if (other.name != null)
            return false;
      } else if (!name.equals(other.name))
         return false;
      if (lastModifiedTime == null) {
         if (other.lastModifiedTime != null)
            return false;
      } else if (!lastModifiedTime.equals(other.lastModifiedTime))
         return false;
      return true;
   }

   @Override
   public String toString() {
      return "Key [isDefault=" + isDefault + ", instanceIds=" + instanceIds + ", name=" + name
               + ", keyMaterial=" + keyMaterial + ", lastModifiedTime=" + lastModifiedTime + "]";
   }

   public Set<String> getInstanceIds() {
      return instanceIds;
   }

   public void setInstanceIds(Set<String> instanceIds) {
      this.instanceIds = instanceIds;
   }

}