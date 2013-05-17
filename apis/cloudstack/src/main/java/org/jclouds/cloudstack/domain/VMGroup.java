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
import java.util.Date;

import org.jclouds.javax.annotation.Nullable;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

/**
 * Class VMGroup
 *
 * @author Richard Downer
 */
public class VMGroup {

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return new ConcreteBuilder().fromVMGroup(this);
   }

   public abstract static class Builder<T extends Builder<T>> {
      protected abstract T self();

      protected String id;
      protected String account;
      protected Date created;
      protected String domain;
      protected String domainId;
      protected String name;

      /**
       * @see VMGroup#getId()
       */
      public T id(String id) {
         this.id = id;
         return self();
      }

      /**
       * @see VMGroup#getAccount()
       */
      public T account(String account) {
         this.account = account;
         return self();
      }

      /**
       * @see VMGroup#getCreated()
       */
      public T created(Date created) {
         this.created = created;
         return self();
      }

      /**
       * @see VMGroup#getDomain()
       */
      public T domain(String domain) {
         this.domain = domain;
         return self();
      }

      /**
       * @see VMGroup#getDomainId()
       */
      public T domainId(String domainId) {
         this.domainId = domainId;
         return self();
      }

      /**
       * @see VMGroup#getName()
       */
      public T name(String name) {
         this.name = name;
         return self();
      }

      public VMGroup build() {
         return new VMGroup(id, account, created, domain, domainId, name);
      }

      public T fromVMGroup(VMGroup in) {
         return this
               .id(in.getId())
               .account(in.getAccount())
               .created(in.getCreated())
               .domain(in.getDomain())
               .domainId(in.getDomainId())
               .name(in.getName());
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
   private final Date created;
   private final String domain;
   private final String domainId;
   private final String name;

   @ConstructorProperties({
         "id", "account", "created", "domain", "domainid", "name"
   })
   protected VMGroup(String id, @Nullable String account, @Nullable Date created, @Nullable String domain,
                     @Nullable String domainId, @Nullable String name) {
      this.id = checkNotNull(id, "id");
      this.account = account;
      this.created = created;
      this.domain = domain;
      this.domainId = domainId;
      this.name = name;
   }

   /**
    * @return the VMGroup's ID
    */
   public String getId() {
      return this.id;
   }

   /**
    * @return the account that owns the VMGroup
    */
   @Nullable
   public String getAccount() {
      return this.account;
   }

   /**
    * @return the VMGroup's creation timestamp
    */
   @Nullable
   public Date getCreated() {
      return this.created;
   }

   /**
    * @return the domain that contains the VMGroup
    */
   @Nullable
   public String getDomain() {
      return this.domain;
   }

   /**
    * @return the ID of the domain that contains the VMGroup
    */
   @Nullable
   public String getDomainId() {
      return this.domainId;
   }

   /**
    * @return the name of the VMGroup
    */
   public String getName() {
      return this.name;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(id, account, created, domain, domainId, name);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      VMGroup that = VMGroup.class.cast(obj);
      return Objects.equal(this.id, that.id)
            && Objects.equal(this.account, that.account)
            && Objects.equal(this.created, that.created)
            && Objects.equal(this.domain, that.domain)
            && Objects.equal(this.domainId, that.domainId)
            && Objects.equal(this.name, that.name);
   }

   protected ToStringHelper string() {
      return Objects.toStringHelper(this)
            .add("id", id).add("account", account).add("created", created).add("domain", domain).add("domainId", domainId).add("name", name);
   }

   @Override
   public String toString() {
      return string().toString();
   }

}
