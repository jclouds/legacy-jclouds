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
package org.jclouds.rackspace.cloudloadbalancers.v1.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

/**
 * The access rule management feature allows fine-grained network access controls to be applied to the load balancer's 
 * virtual IP address. A single IP address, multiple IP addresses, or entire network subnets can be added as an access
 * rule. Rules that are configured with the ALLOW type will always take precedence over rules with the DENY type. To 
 * reject traffic from all rules except for those with the ALLOW type, add an access rule with an address of 
 * "0.0.0.0/0" and a DENY type.
 * 
 * @author Everett Toews
 */
public class AccessRule {

   private final Type type;
   private final String address;
   
   /**
    * Use this method to easily construct {@link Type#ALLOW} rules for the address.
    */
   public static AccessRule allow(String address) {
      return new AccessRule(address, Type.ALLOW);
   }
   
   /**
    * Use this method to easily construct {@link Type#DENY} rules for the address.
    */
   public static AccessRule deny(String address) {
      return new AccessRule(address, Type.DENY);
   }

   public AccessRule(String address, Type type) {
      this.address = checkNotNull(address, "address");
      this.type = checkNotNull(type, "type");
   }

   public String getAddress() {
      return this.address;
   }

   public Type getType() {
      return this.type;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(address);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      AccessRule that = AccessRule.class.cast(obj);
      
      return Objects.equal(this.address, that.address);
   }
   
   protected ToStringHelper string() {
      return Objects.toStringHelper(this).omitNullValues()
            .add("address", address).add("type", type);
   }
   
   @Override
   public String toString() {
      return string().toString();
   }

   public static enum Type {
      /**
       * Specifies rules that will always take precedence over rules with the DENY type.
       */
      ALLOW, 
      
      /**
       * Specifies rules to which traffic can be denied.
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
}
