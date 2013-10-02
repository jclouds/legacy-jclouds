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

import org.jclouds.javax.annotation.Nullable;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

/**
 * Representation of the API domain response
 *
 * @author Andrei Savu
 */
public class Domain implements Comparable<Domain> {

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return new ConcreteBuilder().fromDomain(this);
   }

   public abstract static class Builder<T extends Builder<T>> {
      protected abstract T self();

      protected String id;
      protected boolean hasChild;
      protected long level;
      protected String name;
      protected String networkDomain;
      protected String parentDomainId;
      protected String parentDomainName;

      /**
       * @see Domain#getId()
       */
      public T id(String id) {
         this.id = id;
         return self();
      }

      /**
       * @see Domain#hasChild()
       */
      public T hasChild(boolean hasChild) {
         this.hasChild = hasChild;
         return self();
      }

      /**
       * @see Domain#getLevel()
       */
      public T level(long level) {
         this.level = level;
         return self();
      }

      /**
       * @see Domain#getName()
       */
      public T name(String name) {
         this.name = name;
         return self();
      }

      /**
       * @see Domain#getNetworkDomain()
       */
      public T networkDomain(String networkDomain) {
         this.networkDomain = networkDomain;
         return self();
      }

      /**
       * @see Domain#getParentDomainId()
       */
      public T parentDomainId(String parentDomainId) {
         this.parentDomainId = parentDomainId;
         return self();
      }

      /**
       * @see Domain#getParentDomainName()
       */
      public T parentDomainName(String parentDomainName) {
         this.parentDomainName = parentDomainName;
         return self();
      }

      public Domain build() {
         return new Domain(id, hasChild, level, name, networkDomain, parentDomainId, parentDomainName);
      }

      public T fromDomain(Domain in) {
         return this
               .id(in.getId())
               .hasChild(in.hasChild())
               .level(in.getLevel())
               .name(in.getName())
               .networkDomain(in.getNetworkDomain())
               .parentDomainId(in.getParentDomainId())
               .parentDomainName(in.getParentDomainName());
      }
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }

   private final String id;
   private final boolean hasChild;
   private final long level;
   private final String name;
   private final String networkDomain;
   private final String parentDomainId;
   private final String parentDomainName;

   @ConstructorProperties({
         "id", "haschild", "level", "name", "networkdomain", "parentdomainid", "parentdomainname"
   })
   protected Domain(String id, boolean hasChild, long level, @Nullable String name, @Nullable String networkDomain,
                    @Nullable String parentDomainId, @Nullable String parentDomainName) {
      this.id = checkNotNull(id, "id");
      this.hasChild = hasChild;
      this.level = level;
      this.name = name;
      this.networkDomain = networkDomain;
      this.parentDomainId = parentDomainId;
      this.parentDomainName = parentDomainName;
   }

   public String getId() {
      return this.id;
   }

   public boolean hasChild() {
      return this.hasChild;
   }

   public long getLevel() {
      return this.level;
   }

   @Nullable
   public String getName() {
      return this.name;
   }

   @Nullable
   public String getNetworkDomain() {
      return this.networkDomain;
   }

   @Nullable
   public String getParentDomainId() {
      return this.parentDomainId;
   }

   @Nullable
   public String getParentDomainName() {
      return this.parentDomainName;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(id, hasChild, level, name, networkDomain, parentDomainId, parentDomainName);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      Domain that = Domain.class.cast(obj);
      return Objects.equal(this.id, that.id)
            && Objects.equal(this.hasChild, that.hasChild)
            && Objects.equal(this.level, that.level)
            && Objects.equal(this.name, that.name)
            && Objects.equal(this.networkDomain, that.networkDomain)
            && Objects.equal(this.parentDomainId, that.parentDomainId)
            && Objects.equal(this.parentDomainName, that.parentDomainName);
   }

   protected ToStringHelper string() {
      return Objects.toStringHelper(this)
            .add("id", id).add("hasChild", hasChild).add("level", level).add("name", name).add("networkDomain", networkDomain).add("parentDomainId", parentDomainId).add("parentDomainName", parentDomainName);
   }

   @Override
   public String toString() {
      return string().toString();
   }

   @Override
   public int compareTo(Domain other) {
      return id.compareTo(other.getId());
   }

}
