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

package org.jclouds.cloudstack.domain;

import com.google.gson.annotations.SerializedName;

/**
 * 
 * @author Adrian Cole
 */
public class OSType implements Comparable<OSType> {

   public static Builder builder() {
      return new Builder();
   }

   public static class Builder {
      private long id;
      private long OSCategoryId;
      private String description;

      public Builder id(long id) {
         this.id = id;
         return this;
      }

      public Builder OSCategoryId(long OSCategoryId) {
         this.OSCategoryId = OSCategoryId;
         return this;
      }

      public Builder description(String description) {
         this.description = description;
         return this;
      }

      public OSType build() {
         return new OSType(id, OSCategoryId, description);
      }
   }

   // for deserialization
   OSType() {

   }

   private long id;
   @SerializedName("oscategoryid")
   private long OSCategoryId;
   private String description;

   public OSType(long id, long OSCategoryId, String description) {
      this.id = id;
      this.OSCategoryId = OSCategoryId;
      this.description = description;
   }

   /**
    * @return the ID of the OS type
    */
   public long getId() {
      return id;
   }

   /**
    * @return the ID of the OS category
    */
   public long getOSCategoryId() {
      return OSCategoryId;
   }

   /**
    * @return the name/description of the OS type
    */
   public String getDescription() {
      return description;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + (int) (OSCategoryId ^ (OSCategoryId >>> 32));
      result = prime * result + ((description == null) ? 0 : description.hashCode());
      result = prime * result + (int) (id ^ (id >>> 32));
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
      OSType other = (OSType) obj;
      if (OSCategoryId != other.OSCategoryId)
         return false;
      if (description == null) {
         if (other.description != null)
            return false;
      } else if (!description.equals(other.description))
         return false;
      if (id != other.id)
         return false;
      return true;
   }

   @Override
   public String toString() {
      return "[id=" + id + ", OSCategoryId=" + OSCategoryId + ", description=" + description + "]";
   }

   @Override
   public int compareTo(OSType arg0) {
      return new Long(id).compareTo(arg0.getId());
   }

}
