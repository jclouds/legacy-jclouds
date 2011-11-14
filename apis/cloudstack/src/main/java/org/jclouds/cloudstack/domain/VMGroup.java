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

import java.util.Date;

/**
 * @author Richard Downer
 */
public class VMGroup implements Comparable<VMGroup> {

   public static Builder builder() {
      return new Builder();
   }

   public static class Builder {

      private long id;
      private String account;
      private Date created;
      private String domain;
      private long domainId;
      private String name;

      public Builder id(long id) {
         this.id = id;
         return this;
      }

      public Builder account(String account) {
         this.account = account;
         return this;
      }

      public Builder created(Date created) {
         this.created = created;
         return this;
      }

      public Builder domain(String domain) {
         this.domain = domain;
         return this;
      }

      public Builder domainId(long domainId) {
         this.domainId = domainId;
         return this;
      }

      public Builder name(String name) {
         this.name = name;
         return this;
      }

      public VMGroup build() {
         return new VMGroup(id, account, created, domain, domainId, name);
      }
   }

   private long id;
   private String account;
   private Date created;
   private String domain;
   @SerializedName("domainid")
   private long domainId;
   private String name;

   public VMGroup(long id, String account, Date created, String domain, long domainId, String name) {
      this.id = id;
      this.account = account;
      this.created = created;
      this.domain = domain;
      this.domainId = domainId;
      this.name = name;
   }

   /**
    * present only for serializer
    */
   VMGroup() {
   }

   /**
    * @return the VMGroup's ID
    */
   public long getId() {
      return id;
   }

   /**
    * @return the account that owns the VMGroup
    */
   public String getAccount() {
      return account;
   }

   /**
    * @return the VMGroup's creation timestamp
    */
   public Date getCreated() {
      return created;
   }

   /**
    * @return the domain that contains the VMGroup
    */
   public String getDomain() {
      return domain;
   }

   /**
    * @return the ID of the domain that contains the VMGroup
    */
   public long getDomainId() {
      return domainId;
   }

   /**
    * @return the name of the VMGroup
    */
   public String getName() {
      return name;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      VMGroup vmGroup = (VMGroup) o;

      if (domainId != vmGroup.domainId) return false;
      if (id != vmGroup.id) return false;
      if (account != null ? !account.equals(vmGroup.account) : vmGroup.account != null) return false;
      if (created != null ? !created.equals(vmGroup.created) : vmGroup.created != null) return false;
      if (domain != null ? !domain.equals(vmGroup.domain) : vmGroup.domain != null) return false;
      if (name != null ? !name.equals(vmGroup.name) : vmGroup.name != null) return false;

      return true;
   }

   @Override
   public int hashCode() {
      int result = (int) (id ^ (id >>> 32));
      result = 31 * result + (account != null ? account.hashCode() : 0);
      result = 31 * result + (created != null ? created.hashCode() : 0);
      result = 31 * result + (domain != null ? domain.hashCode() : 0);
      result = 31 * result + (int) (domainId ^ (domainId >>> 32));
      result = 31 * result + (name != null ? name.hashCode() : 0);
      return result;
   }

   @Override
   public String toString() {
      return "[id=" + id +
         ", account='" + account + '\'' +
         ", created=" + created +
         ", domain='" + domain + '\'' +
         ", domainId=" + domainId +
         ", name='" + name + '\'' +
         '}';
   }

   @Override
   public int compareTo(VMGroup vmGroup) {
      return new Long(id).compareTo(vmGroup.getId());
   }
}
