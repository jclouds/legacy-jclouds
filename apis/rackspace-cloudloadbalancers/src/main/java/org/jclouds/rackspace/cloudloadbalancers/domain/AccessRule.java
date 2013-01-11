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
package org.jclouds.rackspace.cloudloadbalancers.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

/**
 * The access list management feature allows fine-grained network access controls to be applied to the load balancer's 
 * virtual IP address. A single IP address, multiple IP addresses, or entire network subnets can be added as an access
 * rule. Items that are configured with the ALLOW type will always take precedence over items with the DENY type. To 
 * reject traffic from all items except for those with the ALLOW type, add an access rules with an address of 
 * "0.0.0.0/0" and a DENY type.
 * 
 * @author Everett Toews
 */
public class AccessRule {

   private final int id;
   private final Type type;
   private final String address;

   protected AccessRule(int id, Type type, String address) {
      this.id = id;
      this.type = checkNotNull(type, "type");
      this.address = checkNotNull(address, "address");
   }

   public int getId() {
      return this.id;
   }

   public Type getType() {
      return this.type;
   }

   public String getAddress() {
      return this.address;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(id);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      AccessRule that = AccessRule.class.cast(obj);
      
      return Objects.equal(this.id, that.id);
   }
   
   protected ToStringHelper string() {
      return Objects.toStringHelper(this)
            .add("id", id).add("type", type).add("address", address);
   }
   
   @Override
   public String toString() {
      return string().toString();
   }

   public static enum Type {
      /**
       * Specifies items that will always take precedence over items with the DENY type.
       */
      ALLOW, 
      
      /**
       * Specifies items to which traffic can be denied.
       */
      DENY, 
      
      UNRECOGNIZED;
      
      public static Type fromValue(String type) {
         try {
            return valueOf(checkNotNull(type, "type"));
         } catch (IllegalArgumentException e) {
            return UNRECOGNIZED;
         }
      }
   }

   public static class Builder {
      private int id;
      private Type type;
      private String address;
   
      /** 
       * @see AccessRule#getId()
       */
      public Builder id(int id) {
         this.id = id;
         return this;
      }

      /** 
       * @see Type
       */
      public Builder type(Type type) {
         this.type = type;
         return this;
      }

      /** 
       * IP address for item to add to access list.
       */
      public Builder address(String address) {
         this.address = address;
         return this;
      }

      public AccessRule build() {
         return new AccessRule(id, type, address);
      }
      
      public Builder from(AccessRule in) {
         return this
               .id(in.getId())
               .type(in.getType())
               .address(in.getAddress());
      }
   }

   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return new Builder().from(this);
   }
}
