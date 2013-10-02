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
package org.jclouds.openstack.nova.v2_0.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import java.beans.ConstructorProperties;

import javax.inject.Named;

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.net.domain.IpProtocol;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.collect.ForwardingObject;

/**
 * Defines a security group rule
 */
public class SecurityGroupRule extends Ingress {

   public static class Cidr extends ForwardingObject {
      private String cidr;

      @ConstructorProperties("cidr")
      protected Cidr(String cidr) {
         this.cidr = checkNotNull(cidr);
      }

      @Override
      protected Object delegate() {
         return cidr;
      }
   }

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return new ConcreteBuilder().fromSecurityGroupRule(this);
   }

   public abstract static class Builder<T extends Builder<T>> extends Ingress.Builder<T> {
      protected String id;
      protected TenantIdAndName group;
      protected String parentGroupId;
      protected String ipRange;

      /**
       * @see SecurityGroupRule#getId()
       */
      public T id(String id) {
         this.id = id;
         return self();
      }

      /**
       * @see SecurityGroupRule#getGroup()
       */
      public T group(TenantIdAndName group) {
         this.group = group;
         return self();
      }

      /**
       * @see SecurityGroupRule#getParentGroupId()
       */
      public T parentGroupId(String parentGroupId) {
         this.parentGroupId = parentGroupId;
         return self();
      }

      /**
       * @see SecurityGroupRule#getIpRange()
       */
      public T ipRange(String ipRange) {
         this.ipRange = ipRange;
         return self();
      }

      public SecurityGroupRule build() {
         return new SecurityGroupRule(ipProtocol, fromPort, toPort, id, group, parentGroupId, ipRange == null ? null : new Cidr(ipRange));
      }

      public T fromSecurityGroupRule(SecurityGroupRule in) {
         return super.fromIngress(in)
               .id(in.getId())
               .group(in.getGroup())
               .parentGroupId(in.getParentGroupId())
               .ipRange(in.getIpRange());
      }
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }

   private final String id;
   private final TenantIdAndName group;
   @Named("parent_group_id")
   private final String parentGroupId;
   @Named("ip_range")
   private final SecurityGroupRule.Cidr ipRange;

   @ConstructorProperties({
         "ip_protocol", "from_port", "to_port", "id", "group", "parent_group_id", "ip_range"
   })
   protected SecurityGroupRule(IpProtocol ipProtocol, int fromPort, int toPort, String id, @Nullable TenantIdAndName group, String parentGroupId, @Nullable Cidr ipRange) {
      super(ipProtocol, fromPort, toPort);
      this.id = checkNotNull(id, "id");
      this.group = group;
      this.parentGroupId = checkNotNull(parentGroupId, "parentGroupId");
      this.ipRange = ipRange;
   }

   public String getId() {
      return this.id;
   }

   @Nullable
   public TenantIdAndName getGroup() {
      return this.group;
   }

   public String getParentGroupId() {
      return this.parentGroupId;
   }

   @Nullable
   public String getIpRange() {
      return ipRange == null ? null : ipRange.cidr;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(id, group, parentGroupId, ipRange);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      SecurityGroupRule that = SecurityGroupRule.class.cast(obj);
      return super.equals(that) && Objects.equal(this.id, that.id)
            && Objects.equal(this.group, that.group)
            && Objects.equal(this.parentGroupId, that.parentGroupId)
            && Objects.equal(this.ipRange, that.ipRange);
   }

   protected ToStringHelper string() {
      return super.string()
            .add("id", id).add("group", group).add("parentGroupId", parentGroupId).add("ipRange", ipRange);
   }

}
