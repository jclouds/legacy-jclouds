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
package org.jclouds.ec2.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Set;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

/**
 * 
 * @see <a href=
 *      "http://docs.amazonwebservices.com/AWSEC2/latest/APIReference/ApiReference-ItemType-IpPermissionType.html"
 *      />
 * @author Adrian Cole
 */
public class IpPermissionImpl implements IpPermission {
   public static Builder builder() {
      return new Builder();
   }

   public static class Builder {
      private int fromPort;
      private int toPort;
      private IpProtocol ipProtocol;
      private Multimap<String, String> userIdGroupPairs = LinkedHashMultimap.create();
      private Set<String> groupIds = Sets.newLinkedHashSet();
      private Set<String> ipRanges = Sets.newLinkedHashSet();

      public Builder fromPort(int fromPort) {
         this.fromPort = fromPort;
         return this;
      }

      public Builder toPort(int toPort) {
         this.fromPort = toPort;
         return this;
      }

      public Builder ipProtocol(IpProtocol ipProtocol) {
         this.ipProtocol = ipProtocol;
         return this;
      }

      public Builder userIdGroupPair(String userId, String groupNameOrId) {
         this.userIdGroupPairs.put(userId, groupNameOrId);
         return this;
      }

      public Builder userIdGroupPairs(Multimap<String, String> userIdGroupPairs) {
         this.userIdGroupPairs.putAll(userIdGroupPairs);
         return this;
      }

      public Builder ipRange(String ipRange) {
         this.ipRanges.add(ipRange);
         return this;
      }

      public Builder ipRanges(Iterable<String> ipRanges) {
         Iterables.addAll(this.ipRanges, ipRanges);
         return this;
      }

      public Builder groupId(String groupId) {
         this.groupIds.add(groupId);
         return this;
      }

      public Builder groupIds(Iterable<String> groupIds) {
         Iterables.addAll(this.groupIds, groupIds);
         return this;
      }

      public IpPermission build() {
         return new IpPermissionImpl(ipProtocol, fromPort, toPort, userIdGroupPairs, groupIds, ipRanges);
      }
   }

   private final int fromPort;
   private final int toPort;
   private final Multimap<String, String> userIdGroupPairs;
   private final Set<String> groupIds;
   private final IpProtocol ipProtocol;
   private final Set<String> ipRanges;

   public IpPermissionImpl(IpProtocol ipProtocol, int fromPort, int toPort,
         Multimap<String, String> userIdGroupPairs, Iterable<String> groupIds, Iterable<String> ipRanges) {
      this.fromPort = fromPort;
      this.toPort = toPort;
      this.userIdGroupPairs = ImmutableMultimap.copyOf(checkNotNull(userIdGroupPairs, "userIdGroupPairs"));
      this.ipProtocol = checkNotNull(ipProtocol, "ipProtocol");
      this.groupIds = ImmutableSet.copyOf(checkNotNull(groupIds, "groupIds"));
      this.ipRanges = ImmutableSet.copyOf(checkNotNull(ipRanges, "ipRanges"));
   }

   /**
    * {@inheritDoc}
    */
   public int compareTo(IpPermission o) {
      return (this == o) ? 0 : getIpProtocol().compareTo(o.getIpProtocol());
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public int getFromPort() {
      return fromPort;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public int getToPort() {
      return toPort;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Multimap<String, String> getUserIdGroupPairs() {
      return userIdGroupPairs;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Set<String> getGroupIds() {
      return groupIds;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public IpProtocol getIpProtocol() {
      return ipProtocol;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Set<String> getIpRanges() {
      return ipRanges;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + fromPort;
      result = prime * result + ((groupIds == null) ? 0 : groupIds.hashCode());
      result = prime * result + ((ipProtocol == null) ? 0 : ipProtocol.hashCode());
      result = prime * result + ((ipRanges == null) ? 0 : ipRanges.hashCode());
      result = prime * result + toPort;
      result = prime * result + ((userIdGroupPairs == null) ? 0 : userIdGroupPairs.hashCode());
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
      IpPermissionImpl other = (IpPermissionImpl) obj;
      if (fromPort != other.fromPort)
         return false;
      if (groupIds == null) {
         if (other.groupIds != null)
            return false;
      } else if (!groupIds.equals(other.groupIds))
         return false;
      if (ipProtocol != other.ipProtocol)
         return false;
      if (ipRanges == null) {
         if (other.ipRanges != null)
            return false;
      } else if (!ipRanges.equals(other.ipRanges))
         return false;
      if (toPort != other.toPort)
         return false;
      if (userIdGroupPairs == null) {
         if (other.userIdGroupPairs != null)
            return false;
      } else if (!userIdGroupPairs.equals(other.userIdGroupPairs))
         return false;
      return true;
   }

   @Override
   public String toString() {
      return "[fromPort=" + fromPort + ", toPort=" + toPort + ", userIdGroupPairs=" + userIdGroupPairs + ", groupIds="
            + groupIds + ", ipProtocol=" + ipProtocol + ", ipRanges=" + ipRanges + "]";
   }

}
