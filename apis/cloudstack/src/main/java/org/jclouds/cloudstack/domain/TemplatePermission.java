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
 * @author Richard Downer
 */
public class TemplatePermission {

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return new ConcreteBuilder().fromTemplatePermission(this);
   }

   public abstract static class Builder<T extends Builder<T>> {
      protected abstract T self();

      protected String id;
      protected String account;
      protected String domainId;
      protected boolean isPublic;

      /**
       * @see TemplatePermission#getId()
       */
      public T id(String id) {
         this.id = id;
         return self();
      }

      /**
       * @see TemplatePermission#getAccount()
       */
      public T account(String account) {
         this.account = account;
         return self();
      }

      /**
       * @see TemplatePermission#getDomainId()
       */
      public T domainId(String domainId) {
         this.domainId = domainId;
         return self();
      }

      /**
       * @see TemplatePermission#isPublic()
       */
      public T isPublic(boolean isPublic) {
         this.isPublic = isPublic;
         return self();
      }

      public TemplatePermission build() {
         return new TemplatePermission(id, account, domainId, isPublic);
      }

      public T fromTemplatePermission(TemplatePermission in) {
         return this
               .id(in.getId())
               .account(in.getAccount())
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
   private final String account;
   private final String domainId;
   private final boolean isPublic;

   @ConstructorProperties({
         "id", "account", "domainid", "ispublic"
   })
   protected TemplatePermission(String id, @Nullable String account, @Nullable String domainId, boolean isPublic) {
      this.id = checkNotNull(id, "id");
      this.account = account;
      this.domainId = domainId;
      this.isPublic = isPublic;
   }

   /**
    * Gets the template ID
    *
    * @return the template ID
    */
   public String getId() {
      return this.id;
   }

   /**
    * Gets the list of accounts the template is available for
    *
    * @return the list of accounts the template is available for
    */
   @Nullable
   public String getAccount() {
      return this.account;
   }

   /**
    * Gets the ID of the domain to which the template belongs
    *
    * @return the ID of the domain to which the template belongs
    */
   @Nullable
   public String getDomainId() {
      return this.domainId;
   }

   /**
    * Returns true if this template is a public template, false otherwise
    *
    * @return true if this template is a public template, false otherwise
    */
   public boolean isPublic() {
      return this.isPublic;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(id, account, domainId, isPublic);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      TemplatePermission that = TemplatePermission.class.cast(obj);
      return Objects.equal(this.id, that.id)
            && Objects.equal(this.account, that.account)
            && Objects.equal(this.domainId, that.domainId)
            && Objects.equal(this.isPublic, that.isPublic);
   }

   protected ToStringHelper string() {
      return Objects.toStringHelper(this)
            .add("id", id).add("account", account).add("domainId", domainId).add("isPublic", isPublic);
   }

   @Override
   public String toString() {
      return string().toString();
   }

}
