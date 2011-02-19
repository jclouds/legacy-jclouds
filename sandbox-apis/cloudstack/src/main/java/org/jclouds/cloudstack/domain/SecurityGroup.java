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

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Set;

import com.google.common.collect.ImmutableSet;
import com.google.gson.annotations.SerializedName;

/**
 * 
 * @author Adrian Cole
 */
public class SecurityGroup implements Comparable<SecurityGroup> {
   private long id;
   private String account;
   private String name;
   private String description;
   private String domain;
   @SerializedName("domainid")
   private long domainId;
   @SerializedName("ingressrule")
   private Set<IngressRule> ingressRules = ImmutableSet.of();

   public SecurityGroup(long id, String account, String name, String description, String domain, long domainId,
         Set<IngressRule> ingressRules) {
      this.id = id;
      this.account = account;
      this.name = name;
      this.description = description;
      this.domain = domain;
      this.domainId = domainId;
      this.ingressRules = ImmutableSet.copyOf(checkNotNull(ingressRules, "ingressRules"));
   }

   /**
    * present only for serializer
    * 
    */
   SecurityGroup() {

   }

   /**
    * 
    * @return the id of the security group
    */
   public long getId() {
      return id;
   }

   /**
    * 
    * @return the name of the security group
    */

   public String getName() {
      return name;
   }

   /**
    * 
    * @return an alternate display text of the security group.
    */
   public String getDescription() {
      return description;
   }

   /**
    * 
    * @return Domain name for the security group
    */
   public String getDomain() {
      return domain;
   }

   /**
    * 
    * @return the domain id of the security group
    */
   public long getDomainId() {
      return domainId;
   }

   /**
    * 
    * @return the account owning the security group
    */
   public String getAccount() {
      return account;
   }

   /**
    * 
    * @return the list of ingress rules associated with the security group
    */
   public Set<IngressRule> getIngressRules() {
      return ingressRules;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((account == null) ? 0 : account.hashCode());
      result = prime * result + ((description == null) ? 0 : description.hashCode());
      result = prime * result + ((domain == null) ? 0 : domain.hashCode());
      result = prime * result + (int) (domainId ^ (domainId >>> 32));
      result = prime * result + (int) (id ^ (id >>> 32));
      result = prime * result + ((ingressRules == null) ? 0 : ingressRules.hashCode());
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
      SecurityGroup other = (SecurityGroup) obj;
      if (account == null) {
         if (other.account != null)
            return false;
      } else if (!account.equals(other.account))
         return false;
      if (description == null) {
         if (other.description != null)
            return false;
      } else if (!description.equals(other.description))
         return false;
      if (domain == null) {
         if (other.domain != null)
            return false;
      } else if (!domain.equals(other.domain))
         return false;
      if (domainId != other.domainId)
         return false;
      if (id != other.id)
         return false;
      if (ingressRules == null) {
         if (other.ingressRules != null)
            return false;
      } else if (!ingressRules.equals(other.ingressRules))
         return false;
      if (name == null) {
         if (other.name != null)
            return false;
      } else if (!name.equals(other.name))
         return false;
      return true;
   }

   @Override
   public String toString() {
      return "[id=" + id + ", account=" + account + ", name=" + name + ", description=" + description + ", domain="
            + domain + ", domainId=" + domainId + ", ingressRules=" + ingressRules + "]";
   }

   @Override
   public int compareTo(SecurityGroup arg0) {
      return new Long(id).compareTo(arg0.getId());
   }
}
