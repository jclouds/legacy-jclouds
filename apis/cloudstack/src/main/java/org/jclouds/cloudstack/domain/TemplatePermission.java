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

import com.google.gson.annotations.SerializedName;

/**
 * @author Richard Downer
 */
public class TemplatePermission implements Comparable<TemplatePermission> {

   public static Builder builder() {
      return new Builder();
   }

   public static class Builder {
      private long id;
      private String account;
      private long domainId;
      private boolean isPublic;

      public Builder id(long id) {
         this.id = id;
         return this;
      }

      public Builder account(String account) {
         this.account = account;
         return this;
      }

      public Builder domainId(long domainId) {
         this.domainId = domainId;
         return this;
      }

      public Builder isPublic(boolean isPublic) {
         this.isPublic = isPublic;
         return this;
      }

      public TemplatePermission build() {
         return new TemplatePermission(id, account, domainId, isPublic);
      }
   }

   private long id;
   private String account;
   @SerializedName("domainid") private long domainId;
   @SerializedName("ispublic") private boolean isPublic;

   /**
    * Construct a new TemplatePermission instance.
    * @param id the template ID
    * @param account the list of accounts the template is available for
    * @param domainId the ID of the domain to which the template belongs
    * @param isPublic true if this template is a public template, false otherwise
    */
   public TemplatePermission(long id, String account, long domainId, boolean isPublic) {
      this.id = id;
      this.account = account;
      this.domainId = domainId;
      this.isPublic = isPublic;
   }

   /**
    * present only for serializer
    */
   TemplatePermission() {
   }

   /**
    * Gets the template ID
    * @return the template ID
    */
   public long getId() {
      return id;
   }

   /**
    * Gets the list of accounts the template is available for
    * @return the list of accounts the template is available for
    */
   public String getAccount() {
      return account;
   }

   /**
    * Gets the ID of the domain to which the template belongs
    * @return the ID of the domain to which the template belongs
    */
   public long getDomainId() {
      return domainId;
   }

   /**
    * Returns true if this template is a public template, false otherwise
    * @return true if this template is a public template, false otherwise
    */
   public boolean isPublic() {
      return isPublic;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      TemplatePermission that = (TemplatePermission) o;

      if (domainId != that.domainId) return false;
      if (id != that.id) return false;
      if (isPublic != that.isPublic) return false;
      if (account != null ? !account.equals(that.account) : that.account != null) return false;

      return true;
   }

   @Override
   public int hashCode() {
      int result = (int) (id ^ (id >>> 32));
      result = 31 * result + (account != null ? account.hashCode() : 0);
      result = 31 * result + (int) (domainId ^ (domainId >>> 32));
      result = 31 * result + (isPublic ? 1 : 0);
      return result;
   }

   @Override
   public String toString() {
      return "TemplatePermission{" +
            "id=" + id +
            ", account='" + account + '\'' +
            ", domainId=" + domainId +
            ", isPublic=" + isPublic +
            '}';
   }

   @Override
   public int compareTo(TemplatePermission other) {
      return new Long(id).compareTo(other.getId());
   }

}
