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

import java.util.Set;

import com.google.common.base.Objects;
import com.google.gson.annotations.SerializedName;

/**
 * @author Richard Downer
 */
public class ISOPermissions implements Comparable<ISOPermissions> {

   public static Builder builder() {
      return new Builder();
   }

   public static class Builder {

      private String id;
      private String account;
      private String domainId;
      private boolean isPublic;

      /**
       * @param id the template ID
       */
      public Builder id(String id) {
         this.id = id;
         return this;
      }

      /**
       * @param account the list of accounts the template is available for
       */
      public Builder account(String account) {
         this.account = account;
         return this;
      }

      /**
       * @param domainId the ID of the domain to which the template belongs
       */
      public Builder domainId(String domainId) {
         this.domainId = domainId;
         return this;
      }

      /**
       * @param isPublic true if this template is a public template, false otherwise
       */
      public Builder isPublic(boolean isPublic) {
         this.isPublic = isPublic;
         return this;
      }

   }

   private String id;
   @SerializedName("account")
   private Set<String> accounts;
   @SerializedName("domainid")
   private String domainId;
   @SerializedName("ispublic")
   private boolean isPublic;

   /**
    * present only for serializer
    */
   ISOPermissions() {
   }

   /**
    * @return the template ID
    */
   public String getId() {
      return id;
   }

   /**
    * @return the list of accounts the template is available for
    */
   public Set<String> getAccounts() {
      return accounts;
   }

   /**
    * @return the ID of the domain to which the template belongs
    */
   public String getDomainId() {
      return domainId;
   }

   /**
    * @return true if this template is a public template, false otherwise
    */
   public boolean getIsPublic() {
      return isPublic;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      ISOPermissions that = (ISOPermissions) o;

      if (!Objects.equal(accounts, that.accounts)) return false;
      if (!Objects.equal(domainId, that.domainId)) return false;
      if (!Objects.equal(id, that.id)) return false;
      if (!Objects.equal(isPublic, that.isPublic)) return false;

      return true;
   }

   @Override
   public int hashCode() {
       return Objects.hashCode(accounts, domainId, id, isPublic);
   }

   @Override
   public String toString() {
      return "ISOPermissions{" +
            "id=" + id +
            ", accounts='" + accounts + '\'' +
            ", domainId=" + domainId +
            ", isPublic=" + isPublic +
            '}';
   }

   @Override
   public int compareTo(ISOPermissions other) {
      return id.compareTo(other.getId());
   }

}
