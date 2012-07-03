/*
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
package org.jclouds.openstack.nova.v2_0.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import java.beans.ConstructorProperties;

import javax.inject.Named;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

/**
 * Class TenantIdAndName
 * 
 * @author Adrian Cole
*/
public class TenantIdAndName {

   public static Builder<?> builder() { 
      return new ConcreteBuilder();
   }
   
   public Builder<?> toBuilder() { 
      return new ConcreteBuilder().fromTenantIdAndName(this);
   }

   public static abstract class Builder<T extends Builder<T>>  {
      protected abstract T self();

      protected String tenantId;
      protected String name;
   
      /** 
       * @see TenantIdAndName#getTenantId()
       */
      public T tenantId(String tenantId) {
         this.tenantId = tenantId;
         return self();
      }

      /** 
       * @see TenantIdAndName#getName()
       */
      public T name(String name) {
         this.name = name;
         return self();
      }

      public TenantIdAndName build() {
         return new TenantIdAndName(tenantId, name);
      }
      
      public T fromTenantIdAndName(TenantIdAndName in) {
         return this
                  .tenantId(in.getTenantId())
                  .name(in.getName());
      }
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }

   @Named("tenant_id")
   private final String tenantId;
   private final String name;

   @ConstructorProperties({
      "tenant_id", "name"
   })
   protected TenantIdAndName(String tenantId, String name) {
      this.tenantId = checkNotNull(tenantId, "tenantId");
      this.name = checkNotNull(name, "name");
   }

   public String getTenantId() {
      return this.tenantId;
   }

   public String getName() {
      return this.name;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(tenantId, name);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      TenantIdAndName that = TenantIdAndName.class.cast(obj);
      return Objects.equal(this.tenantId, that.tenantId)
               && Objects.equal(this.name, that.name);
   }
   
   protected ToStringHelper string() {
      return Objects.toStringHelper(this)
            .add("tenantId", tenantId).add("name", name);
   }
   
   @Override
   public String toString() {
      return string().toString();
   }

}
