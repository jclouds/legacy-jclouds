/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.cloudstack.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import java.beans.ConstructorProperties;
import java.util.Set;

import org.jclouds.javax.annotation.Nullable;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedSet;

/**
 * Class SecurityGroup
 *
 * @author Adrian Cole
 */
public class SecurityGroup implements Comparable<SecurityGroup> {

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return new ConcreteBuilder().fromSecurityGroup(this);
   }

   public abstract static class Builder<T extends Builder<T>> {
      protected abstract T self();

      protected String id;
      protected String account;
      protected String name;
      protected String description;
      protected String domain;
      protected String domainId;
      protected String jobId;
      protected Integer jobStatus;
      protected Set<IngressRule> ingressRules;

      /**
       * @see SecurityGroup#getId()
       */
      public T id(String id) {
         this.id = id;
         return self();
      }

      /**
       * @see SecurityGroup#getAccount()
       */
      public T account(String account) {
         this.account = account;
         return self();
      }

      /**
       * @see SecurityGroup#getName()
       */
      public T name(String name) {
         this.name = name;
         return self();
      }

      /**
       * @see SecurityGroup#getDescription()
       */
      public T description(String description) {
         this.description = description;
         return self();
      }

      /**
       * @see SecurityGroup#getDomain()
       */
      public T domain(String domain) {
         this.domain = domain;
         return self();
      }

      /**
       * @see SecurityGroup#getDomainId()
       */
      public T domainId(String domainId) {
         this.domainId = domainId;
         return self();
      }

      /**
       * @see SecurityGroup#getJobId()
       */
      public T jobId(String jobId) {
         this.jobId = jobId;
         return self();
      }

      /**
       * @see SecurityGroup#getJobStatus()
       */
      public T jobStatus(Integer jobStatus) {
         this.jobStatus = jobStatus;
         return self();
      }

      /**
       * @see SecurityGroup#getIngressRules()
       */
      public T ingressRules(Set<IngressRule> ingressRules) {
         this.ingressRules = ingressRules;
         return self();
      }

      public SecurityGroup build() {
         return new SecurityGroup(id, account, name, description, domain, domainId, jobId, jobStatus, ingressRules);
      }

      public T fromSecurityGroup(SecurityGroup in) {
         return this
               .id(in.getId())
               .account(in.getAccount())
               .name(in.getName())
               .description(in.getDescription())
               .domain(in.getDomain())
               .domainId(in.getDomainId())
               .jobId(in.getJobId())
               .jobStatus(in.getJobStatus())
               .ingressRules(in.getIngressRules());
      }
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }

   private final String id;
   private final String account;
   private final String name;
   private final String description;
   private final String domain;
   private final String domainId;
   private final String jobId;
   private final Integer jobStatus;
   private final Set<IngressRule> ingressRules;

   @ConstructorProperties({
         "id", "account", "name", "description", "domain", "domainid", "jobid", "jobstatus", "ingressrule"
   })
   protected SecurityGroup(String id, @Nullable String account, @Nullable String name, @Nullable String description,
                           @Nullable String domain, @Nullable String domainId, @Nullable String jobId, @Nullable Integer jobStatus,
                           @Nullable Set<IngressRule> ingressRules) {
      this.id = checkNotNull(id, "id");
      this.account = account;
      this.name = name;
      this.description = description;
      this.domain = domain;
      this.domainId = domainId;
      this.jobId = jobId;
      this.jobStatus = jobStatus;
      this.ingressRules = ingressRules == null ? ImmutableSet.<IngressRule>of() : ImmutableSortedSet.copyOf(ingressRules);
   }

   /**
    * @return the id of the security group
    */
   public String getId() {
      return this.id;
   }

   /**
    * @return the account owning the security group
    */
   @Nullable
   public String getAccount() {
      return this.account;
   }

   /**
    * @return the name of the security group
    */
   @Nullable
   public String getName() {
      return this.name;
   }

   /**
    * @return an alternate display text of the security group.
    */
   @Nullable
   public String getDescription() {
      return this.description;
   }

   /**
    * @return Domain name for the security group
    */
   @Nullable
   public String getDomain() {
      return this.domain;
   }

   /**
    * @return the domain id of the security group
    */
   @Nullable
   public String getDomainId() {
      return this.domainId;
   }

   /**
    * @return shows the current pending asynchronous job ID. This tag is not
    *         returned if no current pending jobs are acting on the virtual
    *         machine
    */
   @Nullable
   public String getJobId() {
      return this.jobId;
   }

   /**
    * @return shows the current pending asynchronous job status
    */
   @Nullable
   public Integer getJobStatus() {
      return this.jobStatus;
   }

   /**
    * @return the list of ingress rules associated with the security group
    */
   public Set<IngressRule> getIngressRules() {
      return this.ingressRules;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(id, account, name, description, domain, domainId, jobId, jobStatus, ingressRules);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      SecurityGroup that = SecurityGroup.class.cast(obj);
      return Objects.equal(this.id, that.id)
            && Objects.equal(this.account, that.account)
            && Objects.equal(this.name, that.name)
            && Objects.equal(this.description, that.description)
            && Objects.equal(this.domain, that.domain)
            && Objects.equal(this.domainId, that.domainId)
            && Objects.equal(this.jobId, that.jobId)
            && Objects.equal(this.jobStatus, that.jobStatus)
            && Objects.equal(this.ingressRules, that.ingressRules);
   }

   protected ToStringHelper string() {
      return Objects.toStringHelper(this).add("id", id).add("account", account).add("name", name).add("description", description)
            .add("domain", domain).add("domainId", domainId).add("jobId", jobId).add("jobStatus", jobStatus).add("ingressRules", ingressRules);
   }

   @Override
   public String toString() {
      return string().toString();
   }

   @Override
   public int compareTo(SecurityGroup o) {
      return id.compareTo(o.getId());
   }
}
