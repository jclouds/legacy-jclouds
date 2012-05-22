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

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Set;
import java.util.SortedSet;

import javax.annotation.Nullable;

import com.google.common.base.Objects;
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
      private String id;
      private String account;
      private String name;
      private String description;
      private String domain;
      private String domainId;
      private String jobId;
      private Integer jobStatus;

      private Set<IngressRule> ingressRules = ImmutableSet.of();

      public Builder id(String id) {
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

      public Builder domainId(String domainId) {
         this.domainId = domainId;
         return this;
      }

      public Builder jobId(String jobId) {
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

   private String id;
   private String account;
   private String name;
   private String description;
   private String domain;
   @SerializedName("domainid")
   private String domainId;
   @SerializedName("jobid")
   @Nullable
   private String jobId;
   @SerializedName("jobstatus")
   @Nullable
   private Integer jobStatus;
   @SerializedName("ingressrule")
   // so that tests and serialization come out expected
   private SortedSet<IngressRule> ingressRules = ImmutableSortedSet.<IngressRule>of();

   public SecurityGroup(String id, String account, String name, String description, String domain, String domainId,
                        String jobId, Integer jobStatus, Set<IngressRule> ingressRules) {
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
   public String getId() {
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
   public String getDomainId() {
      return domainId;
   }

   /**
    * @return shows the current pending asynchronous job ID. This tag is not
    *         returned if no current pending jobs are acting on the virtual
    *         machine
    */
   @Nullable
   public String getJobId() {
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
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      SecurityGroup that = (SecurityGroup) o;

      if (!Objects.equal(domainId, that.domainId)) return false;
      if (!Objects.equal(id, that.id)) return false;
      if (!Objects.equal(jobStatus, that.jobStatus)) return false;

      return true;
   }

   @Override
   public int hashCode() {
       return Objects.hashCode(domainId, id, jobStatus);
   }

   @Override
   public String toString() {
      return "SecurityGroup{" +
            "id=" + id +
            ", account='" + account + '\'' +
            ", name='" + name + '\'' +
            ", description='" + description + '\'' +
            ", domain='" + domain + '\'' +
            ", domainId=" + domainId +
            ", jobId=" + jobId +
            ", jobStatus=" + jobStatus +
            ", ingressRules=" + ingressRules +
            '}';
   }

   @Override
   public int compareTo(SecurityGroup arg0) {
      return id.compareTo(arg0.getId());
   }
}
