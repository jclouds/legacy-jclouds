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
import java.util.Map;

import org.jclouds.javax.annotation.Nullable;

import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;

/**
 * Class ResourceLimit
 *
 * @author Vijay Kiran
 */
public class ResourceLimit {

   /**
    * Type of resource to update.
    */
   public enum ResourceType {
      /**
       * 0 - Instance. Number of instances a user can create.
       */
      INSTANCE(0),
      /**
       * 1 - IP. Number of public IP addresses a user can own.
       */
      IP(1),
      /**
       * 2 - Volume. Number of disk volumes a user can create.
       */
      VOLUME(2),
      /**
       * 3 - Snapshot. Number of snapshots a user can create.
       */
      SNAPSHOT(3),
      /**
       * 4 - Template. Number of templates that a user can register/create.
       */
      TEMPLATE(4),
      /**
       * 5 - Projects.
       */
      PROJECT(5),
      /**
       * 6 - Networks.
       */
      NETWORK(6),
      /**
       * 7 - VPC. Number of VPC the user can own.
       */
      VPC(7),
      /**
       * 8 - CPU. The number of CPUs the user can allocate.
       */
      CPU(8),
      /**
       * 9 - Memory. The amount of memory the user can allocate.
       */
      MEMORY(9),
      /**
       * 10 - Primary storage.
       */
      PRIMARY_STORAGE(10),
      /**
       * 11 - Secondary storage.
       */
      SECONDARY_STORAGE(11),
         
      UNRECOGNIZED(Integer.MAX_VALUE);

      private int code;

      private static final Map<Integer, ResourceType> INDEX = Maps.uniqueIndex(ImmutableSet.copyOf(ResourceType.values()),
            new Function<ResourceType, Integer>() {

               @Override
               public Integer apply(ResourceType input) {
                  return input.code;
               }

            });

      ResourceType(int code) {
         this.code = code;
      }

      public int getCode() {
         return code;
      }

      @Override
      public String toString() {
         return name();
      }

      public static ResourceType fromValue(String resourceType) {
         Integer code = Integer.valueOf(checkNotNull(resourceType, "resourcetype"));
         return INDEX.containsKey(code) ? INDEX.get(code) : UNRECOGNIZED;
      }
   }

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return new ConcreteBuilder().fromResourceLimit(this);
   }

   public abstract static class Builder<T extends Builder<T>> {
      protected abstract T self();

      protected String account;
      protected String domain;
      protected String domainId;
      protected int max;
      protected ResourceType resourceType;

      /**
       * @see ResourceLimit#getAccount()
       */
      public T account(String account) {
         this.account = account;
         return self();
      }

      /**
       * @see ResourceLimit#getDomain()
       */
      public T domain(String domain) {
         this.domain = domain;
         return self();
      }

      /**
       * @see ResourceLimit#getDomainId()
       */
      public T domainId(String domainId) {
         this.domainId = domainId;
         return self();
      }

      /**
       * @see ResourceLimit#getMax()
       */
      public T max(int max) {
         this.max = max;
         return self();
      }

      /**
       * @see ResourceLimit#getResourceType()
       */
      public T resourceType(ResourceType resourceType) {
         this.resourceType = resourceType;
         return self();
      }

      public ResourceLimit build() {
         return new ResourceLimit(account, domain, domainId, max, resourceType);
      }

      public T fromResourceLimit(ResourceLimit in) {
         return this
               .account(in.getAccount())
               .domain(in.getDomain())
               .domainId(in.getDomainId())
               .max(in.getMax())
               .resourceType(in.getResourceType());
      }
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }

   private final String account;
   private final String domain;
   private final String domainId;
   private final int max;
   private final ResourceLimit.ResourceType resourceType;

   @ConstructorProperties({
         "account", "domain", "domainid", "max", "resourcetype"
   })
   protected ResourceLimit(@Nullable String account, @Nullable String domain, @Nullable String domainId, int max,
                           @Nullable ResourceType resourceType) {
      this.account = account;
      this.domain = domain;
      this.domainId = domainId;
      this.max = max;
      this.resourceType = resourceType;
   }

   @Nullable
   public String getAccount() {
      return this.account;
   }

   @Nullable
   public String getDomain() {
      return this.domain;
   }

   @Nullable
   public String getDomainId() {
      return this.domainId;
   }

   public int getMax() {
      return this.max;
   }

   @Nullable
   public ResourceType getResourceType() {
      return this.resourceType;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(account, domain, domainId, max, resourceType);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      ResourceLimit that = ResourceLimit.class.cast(obj);
      return Objects.equal(this.account, that.account)
            && Objects.equal(this.domain, that.domain)
            && Objects.equal(this.domainId, that.domainId)
            && Objects.equal(this.max, that.max)
            && Objects.equal(this.resourceType, that.resourceType);
   }

   protected ToStringHelper string() {
      return Objects.toStringHelper(this)
            .add("account", account).add("domain", domain).add("domainId", domainId).add("max", max).add("resourceType", resourceType);
   }

   @Override
   public String toString() {
      return string().toString();
   }

}
