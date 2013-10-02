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

/**
 * Class ISOPermissions
 *
 * @author Richard Downer
 */
public class ISOPermissions {

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return new ConcreteBuilder().fromISOPermissions(this);
   }

   public abstract static class Builder<T extends Builder<T>> {
      protected abstract T self();

      protected String id;
      protected Set<String> accounts = ImmutableSet.of();
      protected String domainId;
      protected boolean isPublic;

      /**
       * @see ISOPermissions#getId()
       */
      public T id(String id) {
         this.id = id;
         return self();
      }

      /**
       * @see ISOPermissions#getAccounts()
       */
      public T accounts(Set<String> accounts) {
         this.accounts = ImmutableSet.copyOf(checkNotNull(accounts, "accounts"));
         return self();
      }

      public T accounts(String... in) {
         return accounts(ImmutableSet.copyOf(in));
      }

      /**
       * @see ISOPermissions#getDomainId()
       */
      public T domainId(String domainId) {
         this.domainId = domainId;
         return self();
      }

      /**
       * @see ISOPermissions#isPublic()
       */
      public T isPublic(boolean isPublic) {
         this.isPublic = isPublic;
         return self();
      }

      public ISOPermissions build() {
         return new ISOPermissions(id, accounts, domainId, isPublic);
      }

      public T fromISOPermissions(ISOPermissions in) {
         return this
               .id(in.getId())
               .accounts(in.getAccounts())
               .domainId(in.getDomainId())
               .isPublic(in.isPublic());
      }
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }

   private final String id;
   private final Set<String> accounts;
   private final String domainId;
   private final boolean isPublic;

   @ConstructorProperties({
         "id", "account", "domainid", "ispublic"
   })
   protected ISOPermissions(String id, @Nullable Set<String> accounts, @Nullable String domainId, boolean isPublic) {
      this.id = checkNotNull(id, "id");
      this.accounts = accounts == null ? ImmutableSet.<String>of() : ImmutableSet.copyOf(accounts);
      this.domainId = domainId;
      this.isPublic = isPublic;
   }

   /**
    * @return the template ID
    */
   public String getId() {
      return this.id;
   }

   /**
    * @return the list of accounts the template is available for
    */
   public Set<String> getAccounts() {
      return this.accounts;
   }

   /**
    * @return the ID of the domain to which the template belongs
    */
   @Nullable
   public String getDomainId() {
      return this.domainId;
   }

   /**
    * @return true if this template is a public template, false otherwise
    */
   public boolean isPublic() {
      return this.isPublic;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(id, accounts, domainId, isPublic);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      ISOPermissions that = ISOPermissions.class.cast(obj);
      return Objects.equal(this.id, that.id)
            && Objects.equal(this.accounts, that.accounts)
            && Objects.equal(this.domainId, that.domainId)
            && Objects.equal(this.isPublic, that.isPublic);
   }

   protected ToStringHelper string() {
      return Objects.toStringHelper(this)
            .add("id", id).add("accounts", accounts).add("domainId", domainId).add("isPublic", isPublic);
   }

   @Override
   public String toString() {
      return string().toString();
   }

}
