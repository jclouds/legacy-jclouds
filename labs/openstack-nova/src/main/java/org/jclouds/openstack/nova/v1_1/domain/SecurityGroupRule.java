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
package org.jclouds.openstack.nova.v1_1.domain;

import static com.google.common.base.Objects.toStringHelper;
import static com.google.common.base.Preconditions.checkNotNull;

import org.jclouds.javax.annotation.Nullable;

import com.google.common.collect.ForwardingObject;
import com.google.gson.annotations.SerializedName;

/**
 * Defines a security group rule
 * 
 */
public class SecurityGroupRule extends Ingress {

   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return builder().fromSecurityGroupRule(this);
   }

   public static class Builder extends Ingress.Builder {
      private String id;
      private String parentGroupId;
      private TenantIdAndName group;
      private String ipRange;

      public Builder id(String id) {
         this.id = id;
         return this;
      }

      public Builder group(TenantIdAndName group) {
         this.group = group;
         return this;
      }

      public Builder parentGroupId(String parentGroupId) {
         this.parentGroupId = parentGroupId;
         return this;
      }

      public Builder ipRange(String ipRange) {
         this.ipRange = ipRange;
         return this;
      }

      @Override
      public SecurityGroupRule build() {
         return new SecurityGroupRule(ipProtocol, fromPort, toPort, id, parentGroupId, group, ipRange);
      }

      public Builder fromSecurityGroupRule(SecurityGroupRule in) {
         return id(in.getId()).fromPort(in.getFromPort()).group(in.getGroup()).ipProtocol(in.getIpProtocol()).toPort(
                  in.getToPort()).parentGroupId(in.getParentGroupId()).ipRange(in.getIpRange());
      }

      @Override
      public Builder ipProtocol(IpProtocol ipProtocol) {
         super.ipProtocol(ipProtocol);
         return this;
      }

      @Override
      public Builder fromPort(int fromPort) {
         super.fromPort(fromPort);
         return this;
      }

      @Override
      public Builder toPort(int toPort) {
         super.toPort(toPort);
         return this;
      }

   }

   protected final String id;

   protected final TenantIdAndName group;

   @SerializedName(value = "parent_group_id")
   protected final String parentGroupId;

   // type to get around unnecessary structure
   private static class Cidr extends ForwardingObject {
      private String cidr;

      private Cidr(String cidr) {
         this.cidr = cidr;
      }

      @Override
      protected Object delegate() {
         return cidr;
      }
   }

   @SerializedName(value = "ip_range")
   protected final Cidr ipRange;

   protected SecurityGroupRule(IpProtocol ipProtocol, int fromPort, int toPort, String id, String parentGroupId,
            @Nullable TenantIdAndName group, @Nullable String ipRange) {
      super(ipProtocol, fromPort, toPort);
      this.parentGroupId = checkNotNull(parentGroupId, "parentGroupId");
      this.id = checkNotNull(id, "id");
      this.group = group;
      this.ipRange = ipRange != null ? new Cidr(ipRange) : null;
   }

   public String getParentGroupId() {
      return this.parentGroupId;
   }

   public String getId() {
      return this.id;
   }

   @Nullable
   public TenantIdAndName getGroup() {
      if (this.group == null || this.group.getName() == null)
         return null;
      return this.group;
   }

   @Nullable
   public String getIpRange() {
      return this.ipRange == null ? null : ipRange.cidr;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + fromPort;
      result = prime * result + ((group == null) ? 0 : group.hashCode());
      result = prime * result + ((id == null) ? 0 : id.hashCode());
      result = prime * result + ((ipProtocol == null) ? 0 : ipProtocol.hashCode());
      result = prime * result + ((ipRange == null) ? 0 : ipRange.hashCode());
      result = prime * result + ((parentGroupId == null) ? 0 : parentGroupId.hashCode());
      result = prime * result + toPort;
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      SecurityGroupRule other = (SecurityGroupRule) obj;
      if (fromPort != other.fromPort)
         return false;
      if (group == null) {
         if (other.group != null)
            return false;
      } else if (!group.equals(other.group))
         return false;
      if (id == null) {
         if (other.id != null)
            return false;
      } else if (!id.equals(other.id))
         return false;
      if (ipProtocol != other.ipProtocol)
         return false;
      if (ipRange == null) {
         if (other.ipRange != null)
            return false;
      } else if (!ipRange.equals(other.ipRange))
         return false;
      if (parentGroupId == null) {
         if (other.parentGroupId != null)
            return false;
      } else if (!parentGroupId.equals(other.parentGroupId))
         return false;
      if (toPort != other.toPort)
         return false;
      return true;
   }

   @Override
   public String toString() {
      return toStringHelper("").add("id", id).add("fromPort", fromPort).add("group", getGroup()).add("ipProtocol",
               ipProtocol).add("toPort", toPort).add("parentGroupId", parentGroupId).add("ipRange", getIpRange())
               .toString();
   }

}