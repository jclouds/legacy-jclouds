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

import com.google.gson.annotations.SerializedName;

/**
 * @author Richard Downer
 */
public class ISOPermissions implements Comparable<ISOPermissions> {

   public static Builder builder() {
      return new Builder();
   }

   public static class Builder {

      private long id;
      private String account;
      private long domainId;
      private boolean isPublic;

      /**
       * @param id the template ID
       */
      public Builder id(long id) {
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
      public Builder domainId(long domainId) {
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

   private long id;
   @SerializedName("account")
   private Set<String> accounts;
   @SerializedName("domainid")
   private long domainId;
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
   public long getId() {
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
   public long getDomainId() {
      return domainId;
   }

   /**
    * @return true if this template is a public template, false otherwise
    */
   public boolean getIsPublic() {
      return isPublic;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      ISOPermissions other = (ISOPermissions) obj;
      if (accounts == null) {
         if (other.accounts != null)
            return false;
      } else if (!accounts.equals(other.accounts))
         return false;
      if (domainId != other.domainId)
         return false;
      if (id != other.id)
         return false;
      if (isPublic != other.isPublic)
         return false;
      return true;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((accounts == null) ? 0 : accounts.hashCode());
      result = prime * result + (int) (domainId ^ (domainId >>> 32));
      result = prime * result + (int) (id ^ (id >>> 32));
      result = prime * result + (isPublic ? 1231 : 1237);
      return result;
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
      return new Long(id).compareTo(other.getId());
   }

}
