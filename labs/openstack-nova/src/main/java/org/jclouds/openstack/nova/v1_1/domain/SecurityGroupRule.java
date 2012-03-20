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

import java.util.Map;

import com.google.gson.annotations.SerializedName;

/**
 * Defines a security group rule
 * 
 */
public class SecurityGroupRule implements Comparable<SecurityGroupRule> {

   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return builder().fromSecurityGroupRule(this);
   }

   public static class Builder {
      private String id;
      private int fromPort;
      private Map<String, String> group;
      // tcp/udp/icmp - move to enum
      private IpProtocol ipProtocol;

      private int toPort;
      private String parentGroupId;

      private Map<String, String> ipRange;

      public Builder id(String id) {
         this.id = id;
         return this;
      }

      public Builder fromPort(int fromPort) {
         this.fromPort = fromPort;
         return this;
      }

      public Builder group(Map<String, String> group) {
         this.group = group;
         return this;
      }

      public Builder ipProtocol(IpProtocol ipProtocol) {
         this.ipProtocol = ipProtocol;
         return this;
      }

      public Builder toPort(int toPort) {
         this.toPort = toPort;
         return this;
      }

      public Builder parentGroupId(String parentGroupId) {
         this.parentGroupId = parentGroupId;
         return this;
      }

      public Builder ipRange(Map<String, String> ipRange) {
         this.ipRange = ipRange;
         return this;
      }

      public SecurityGroupRule build() {
         return new SecurityGroupRule(id, fromPort, group, ipProtocol, toPort, parentGroupId, ipRange);
      }

      public Builder fromSecurityGroupRule(SecurityGroupRule in) {
         return id(in.getId()).fromPort(in.getFromPort()).group(in.getGroup()).ipProtocol(in.getIpProtocol())
               .toPort(in.getToPort()).parentGroupId(in.getParentGroupId()).ipRange(in.getIpRange());
      }
   }

   protected String id;

   @SerializedName(value = "from_port")
   protected int fromPort;

   protected Map<String, String> group;

   @SerializedName(value = "ip_protocol")
   // tcp/udp/icmp
   protected IpProtocol ipProtocol;

   @SerializedName(value = "to_port")
   protected int toPort;

   @SerializedName(value = "parent_group_id")
   protected String parentGroupId;

   @SerializedName(value = "ip_range")
   protected Map<String, String> ipRange;

   protected SecurityGroupRule(String id, int fromPort, Map<String, String> group, IpProtocol ipProtocol, int toPort,
         String parentGroupId, Map<String, String> ipRange) {
      this.id = id;
      this.fromPort = fromPort;
      this.group = group;
      this.ipProtocol = ipProtocol;
      this.toPort = toPort;
      this.parentGroupId = parentGroupId;
      this.ipRange = ipRange;
   }

   public String getId() {
      return this.id;
   }

   public int getFromPort() {
      return this.fromPort;
   }

   public Map<String, String> getGroup() {
      return this.group;
   }

   public IpProtocol getIpProtocol() {
      return this.ipProtocol;
   }

   public int getToPort() {
      return this.toPort;
   }

   public String getParentGroupId() {
      return this.parentGroupId;
   }

   public Map<String, String> getIpRange() {
      return this.ipRange;
   }

   @Override
   public int compareTo(SecurityGroupRule o) {
      return this.id.compareTo(o.getId());
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
      return toStringHelper("").add("id", id).add("fromPort", fromPort).add("group", group)
            .add("ipProtocol", ipProtocol).add("toPort", toPort).add("parentGroupId", parentGroupId)
            .add("ipRange", ipRange).toString();
   }

}