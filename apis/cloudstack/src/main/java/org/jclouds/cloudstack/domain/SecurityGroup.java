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

import javax.annotation.Nullable;
import java.util.Set;
import java.util.SortedSet;

import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedSet;
import com.google.gson.annotations.SerializedName;

/**
 * @author Adrian Cole
 */
public class SecurityGroup implements Comparable<SecurityGroup> {
   public static Builder builder() {
      return new Builder();
   }

   public static class Builder {
      private long id;
      private String account;
      private String name;
      private String description;
      private String domain;
      private long domainId;
      private Long jobId;
      private Integer jobStatus;

      private Set<IngressRule> ingressRules = ImmutableSet.of();

      public Builder id(long id) {
         this.id = id;
         return this;
      }

      public Builder account(String account) {
         this.account = account;
         return this;
      }

      public Builder name(String name) {
         this.name = name;
         return this;
      }

      public Builder description(String description) {
         this.description = description;
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

      public Builder jobId(Long jobId) {
         this.jobId = jobId;
         return this;
      }

      public Builder jobStatus(int jobStatus) {
         this.jobStatus = jobStatus;
         return this;
      }

      public Builder ingressRules(Set<IngressRule> ingressRules) {
         this.ingressRules = ImmutableSet.copyOf(checkNotNull(ingressRules, "ingressRules"));
         return this;
      }

      public SecurityGroup build() {
         return new SecurityGroup(id, account, name, description, domain, domainId, jobId, jobStatus, ingressRules);
      }
   }

   private long id;
   private String account;
   private String name;
   private String description;
   private String domain;
   @SerializedName("domainid")
   private long domainId;
   @SerializedName("jobid")
   @Nullable
   private Long jobId;
   @SerializedName("jobstatus")
   @Nullable
   private Integer jobStatus;
   @SerializedName("ingressrule")
   // so that tests and serialization come out expected
   private SortedSet<IngressRule> ingressRules = ImmutableSortedSet.<IngressRule>of();

   public SecurityGroup(long id, String account, String name, String description, String domain, long domainId,
                        Long jobId, Integer jobStatus, Set<IngressRule> ingressRules) {
      this.id = id;
      this.account = account;
      this.name = name;
      this.description = description;
      this.domain = domain;
      this.domainId = domainId;
      this.jobId = jobId;
      this.jobStatus = jobStatus;
      this.ingressRules = ImmutableSortedSet.copyOf(checkNotNull(ingressRules, "ingressRules"));
   }

   /**
    * present only for serializer
    */
   SecurityGroup() {

   }

   /**
    * @return the id of the security group
    */
   public long getId() {
      return id;
   }

   /**
    * @return the name of the security group
    */

   public String getName() {
      return name;
   }

   /**
    * @return an alternate display text of the security group.
    */
   public String getDescription() {
      return description;
   }

   /**
    * @return Domain name for the security group
    */
   public String getDomain() {
      return domain;
   }

   /**
    * @return the domain id of the security group
    */
   public long getDomainId() {
      return domainId;
   }

   /**
    * @return shows the current pending asynchronous job ID. This tag is not
    *         returned if no current pending jobs are acting on the virtual
    *         machine
    */
   @Nullable
   public Long getJobId() {
      return jobId;
   }

   /**
    * @return shows the current pending asynchronous job status
    */
   @Nullable
   public Integer getJobStatus() {
      return jobStatus;
   }

   /**
    * @return the account owning the security group
    */
   public String getAccount() {
      return account;
   }

   /**
    * @return the list of ingress rules associated with the security group
    */
   public Set<IngressRule> getIngressRules() {
      return ingressRules;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + (int) (domainId ^ (domainId >>> 32));
      result = prime * result + (int) (id ^ (id >>> 32));
      result = prime * result + ((jobStatus == null) ? 0 : jobStatus.hashCode());
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
      if (domainId != other.domainId)
         return false;
      if (id != other.id)
         return false;
      if (jobId == null) {
         if (other.jobId != null)
            return false;
      } else if (!jobId.equals(other.jobId))
         return false;
      if (jobStatus == null) {
         if (other.jobStatus != null)
            return false;
      } else if (!jobStatus.equals(other.jobStatus))
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
