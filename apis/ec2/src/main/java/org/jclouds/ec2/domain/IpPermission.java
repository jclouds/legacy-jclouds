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
package org.jclouds.ec2.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Set;

import com.google.common.base.Objects;
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
public class IpPermission {
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
         this.toPort = toPort;
         return this;
      }

      public Builder ipProtocol(IpProtocol ipProtocol) {
         this.ipProtocol = checkNotNull(ipProtocol, "ipProtocol");
         return this;
      }

      public Builder userIdGroupPair(String userId, String groupNameOrId) {
         this.userIdGroupPairs.put(checkNotNull(userId, "userId"), checkNotNull(groupNameOrId, "groupNameOrId of %s", userId));
         return this;
      }

      public Builder userIdGroupPairs(Multimap<String, String> userIdGroupPairs) {
         this.userIdGroupPairs.putAll(checkNotNull(userIdGroupPairs, "userIdGroupPairs"));
         return this;
      }

      public Builder ipRange(String ipRange) {
         this.ipRanges.add(ipRange);
         return this;
      }

      public Builder ipRanges(Iterable<String> ipRanges) {
         Iterables.addAll(this.ipRanges, checkNotNull(ipRanges, "ipRanges"));
         return this;
      }

      public Builder groupId(String groupId) {
         this.groupIds.add(checkNotNull(groupId, "groupId"));
         return this;
      }

      public Builder groupIds(Iterable<String> groupIds) {
         Iterables.addAll(this.groupIds, checkNotNull(groupIds, "groupIds"));
         return this;
      }

      public IpPermission build() {
         return new IpPermission(ipProtocol, fromPort, toPort, userIdGroupPairs, groupIds, ipRanges);
      }
   }

   private final int fromPort;
   private final int toPort;
   private final Multimap<String, String> userIdGroupPairs;
   private final Set<String> groupIds;
   private final IpProtocol ipProtocol;
   private final Set<String> ipRanges;

   public IpPermission(IpProtocol ipProtocol, int fromPort, int toPort, Multimap<String, String> userIdGroupPairs,
         Iterable<String> groupIds, Iterable<String> ipRanges) {
      this.fromPort = fromPort;
      this.toPort = toPort;
      this.userIdGroupPairs = ImmutableMultimap.copyOf(checkNotNull(userIdGroupPairs, "userIdGroupPairs"));
      this.ipProtocol = checkNotNull(ipProtocol, "ipProtocol");
      this.groupIds = ImmutableSet.copyOf(checkNotNull(groupIds, "groupIds"));
      this.ipRanges = ImmutableSet.copyOf(checkNotNull(ipRanges, "ipRanges"));
   }

   /**
    * Start of port range for the TCP and UDP protocols, or an ICMP type number.
    * An ICMP type number of -1 indicates a wildcard (i.e., any ICMP type
    * number).
    */
   public int getFromPort() {
      return fromPort;
   }

   /**
    * End of port range for the TCP and UDP protocols, or an ICMP code. An ICMP
    * code of -1 indicates a wildcard (i.e., any ICMP code).
    */
   public int getToPort() {
      return toPort;
   }

   /**
    * List of security group and user ID pairs.
    */
   public Multimap<String, String> getUserIdGroupPairs() {
      return userIdGroupPairs;
   }

   /**
    * List of security group Ids
    */
   public Set<String> getGroupIds() {
      return groupIds;
   }

   /**
    * IP protocol
    */
   public IpProtocol getIpProtocol() {
      return ipProtocol;
   }

   /**
    * IP ranges.
    */
   public Set<String> getIpRanges() {
      return ipRanges;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(fromPort, toPort, groupIds, ipProtocol, ipRanges, userIdGroupPairs);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null || getClass() != obj.getClass())
         return false;
      IpPermission that = IpPermission.class.cast(obj);
      return Objects.equal(this.fromPort, that.fromPort) && Objects.equal(this.toPort, that.toPort)
            && Objects.equal(this.groupIds, that.groupIds) && Objects.equal(this.ipProtocol, that.ipProtocol)
            && Objects.equal(this.ipRanges, that.ipRanges)
            && Objects.equal(this.userIdGroupPairs, that.userIdGroupPairs);
   }

   @Override
   public String toString() {
      return Objects.toStringHelper(this).omitNullValues().add("fromPort", fromPort == -1 ? null : fromPort)
            .add("toPort", toPort == -1 ? null : toPort).add("groupIds", groupIds.size() == 0 ? null : groupIds)
            .add("ipProtocol", ipProtocol).add("ipRanges", ipRanges.size() == 0 ? null : ipRanges)
            .add("userIdGroupPairs", userIdGroupPairs.size() == 0 ? null : userIdGroupPairs).toString();
   }

}
