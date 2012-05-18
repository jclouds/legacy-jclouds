/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
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
 */
package org.jclouds.cloudstack.domain;

import com.google.common.base.Objects;
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
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      OSType that = (OSType) o;

      if (!Objects.equal(OSCategoryId, that.OSCategoryId)) return false;
      if (!Objects.equal(description, that.description)) return false;
      if (!Objects.equal(id, that.id)) return false;

      return true;
   }

   @Override
   public int hashCode() {
       return Objects.hashCode(OSCategoryId, description, id);
   }

   @Override
   public String toString() {
      return "OSType{" +
            "id=" + id +
            ", OSCategoryId=" + OSCategoryId +
            ", description='" + description + '\'' +
            '}';
   }

   @Override
   public int compareTo(OSType arg0) {
      return new Long(id).compareTo(arg0.getId());
   }

}
