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
 * Representation of the API domain response
 *
 * @author Andrei Savu
 */
public class Domain implements Comparable<Domain> {

   public static Builder builder() {
      return new Builder();
   }

   public static class Builder {

      private String id;
      private boolean hasChild;
      private long level;
      private String name;
      private String networkDomain;
      private String parentDomainId;
      private String parentDomainName;

      public Builder id(String id) {
         this.id = id;
         return this;
      }

      public Builder hasChild(boolean hasChild) {
         this.hasChild = hasChild;
         return this;
      }

      public Builder level(long level) {
         this.level = level;
         return this;
      }

      public Builder name(String name) {
         this.name = name;
         return this;
      }

      public Builder networkDomain(String networkDomain) {
         this.networkDomain = networkDomain;
         return this;
      }

      public Builder parentDomainId(String parentDomainId) {
         this.parentDomainId = parentDomainId;
         return this;
      }

      public Builder parentDomainName(String parentDomainName) {
         this.parentDomainName = parentDomainName;
         return this;
      }

      public Domain build() {
         return new Domain(id, hasChild, level, name, networkDomain,
            parentDomainId, parentDomainName);
      }
   }

   // for deserialization
   Domain() {
   }

   private String id;
   @SerializedName("haschild")
   private boolean hasChild;
   private long level;
   private String name;
   @SerializedName("networkdomain")
   private String networkDomain;
   @SerializedName("parentdomainid")
   private String parentDomainId;
   @SerializedName("parentdomainname")
   private String parentDomainName;

   public Domain(String id, boolean hasChild, long level, String name, String networkDomain,
         String parentDomainId, String parentDomainName) {
      this.id = id;
      this.hasChild = hasChild;
      this.level = level;
      this.name = name;
      this.networkDomain = networkDomain;
      this.parentDomainId = parentDomainId;
      this.parentDomainName = parentDomainName;
   }

   public String getId() {
      return id;
   }

   public boolean hasChild() {
      return hasChild;
   }

   public long getLevel() {
      return level;
   }

   public String getName() {
      return name;
   }

   public String getNetworkDomain() {
      return networkDomain;
   }

   public String getParentDomainId() {
      return parentDomainId;
   }

   public String getParentDomainName() {
      return parentDomainName;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      Domain that = (Domain) o;

      if (!Objects.equal(hasChild, that.hasChild)) return false;
      if (!Objects.equal(id, that.id)) return false;
      if (!Objects.equal(level, that.level)) return false;
      if (!Objects.equal(parentDomainId, that.parentDomainId)) return false;
      if (!Objects.equal(name, that.name)) return false;
      if (!Objects.equal(networkDomain, that.networkDomain)) return false;
      if (!Objects.equal(parentDomainName, that.parentDomainName)) return false;

      return true;
   }

   @Override
   public int hashCode() {
       return Objects.hashCode(id, hasChild, level, name, networkDomain, parentDomainId, parentDomainName);
   }

   @Override
   public String toString() {
      return "Domain{" +
         "id='" + id + '\'' +
         ", hasChild=" + hasChild +
         ", level=" + level +
         ", name='" + name + '\'' +
         ", networkDomain='" + networkDomain + '\'' +
         ", parentDomainId='" + parentDomainId + '\'' +
         ", parentDomainName='" + parentDomainName + '\'' +
         '}';
   }

   @Override
   public int compareTo(Domain arg0) {
      return id.compareTo(arg0.getId());
   }

}
