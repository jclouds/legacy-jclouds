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
package org.jclouds.net.util;

import static com.google.common.base.Preconditions.checkNotNull;

import org.jclouds.net.domain.IpPermission;
import org.jclouds.net.domain.IpProtocol;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;

/**
 * 
 * Shortcut to create ingress rules
 * 
 * @author Adrian Cole
 */
public class IpPermissions extends IpPermission {

   protected IpPermissions(IpProtocol ipProtocol, int fromPort, int toPort,
         Multimap<String, String> tenantIdGroupPairs, Iterable<String> groupIds, Iterable<String> cidrBlocks) {
      super(ipProtocol, fromPort, toPort, tenantIdGroupPairs, groupIds, tenantIdGroupPairs.size() == 0 ? cidrBlocks
            : ImmutableSet.<String> of());
   }

   public static ICMPTypeSelection permitICMP() {
      return new ICMPTypeSelection();
   }

   public static ToSourceSelection permitAnyProtocol() {
      return new ToSourceSelection(IpProtocol.ALL, 1, 65535);
   }

   public static PortSelection permit(IpProtocol protocol) {
      return new PortSelection(checkNotNull(protocol, "protocol"));
   }

   public static class ICMPTypeSelection extends ToSourceSelection {

      ICMPTypeSelection() {
         super(IpProtocol.ICMP, -1, -1);
      }

      /**
       * @param type ex. 8 for ECHO (i.e. Ping)
       * @see <a href="http://www.iana.org/assignments/icmp-parameters/icmp-parameters.xml"> ICMP Types</a>
       */
      public AndCodeSelection type(int type) {
         return new AndCodeSelection(type);
      }
   }

   public static class AndCodeSelection extends ToSourceSelection {
      AndCodeSelection(int type) {
         super(IpProtocol.ICMP, type, -1);
      }

      public ToSourceSelection andCode(int code) {
         return new ToSourceSelection(getIpProtocol(), getFromPort(), code);
      }

   }

   public static class PortSelection extends ToSourceSelection {

      PortSelection(IpProtocol ipProtocol) {
         super(ipProtocol, ipProtocol == IpProtocol.ICMP ? -1 : 1, ipProtocol == IpProtocol.ICMP ? -1 : 65535);
      }

      public ToPortSelection fromPort(int port) {
         return new ToPortSelection(getIpProtocol(), port);
      }
      
      public ToSourceSelection port(int port) {
         return new ToSourceSelection(getIpProtocol(), port, port);
      }
   }

   public static class ToPortSelection extends ToSourceSelection {

      ToPortSelection(IpProtocol ipProtocol, int fromPort) {
         super(ipProtocol, fromPort, ipProtocol == IpProtocol.ICMP ? -1 : 65535);
      }

      public ToSourceSelection to(int port) {
         return new ToSourceSelection(getIpProtocol(), getFromPort(), port);
      }
   }

   public static class ToGroupSourceSelection extends IpPermissions {

      protected ToGroupSourceSelection(IpProtocol ipProtocol, int fromPort, int toPort) {
         super(ipProtocol, fromPort, toPort, ImmutableMultimap.<String, String> of(), ImmutableSet.<String> of(),
               ImmutableSet.of("0.0.0.0/0"));
      }

      public IpPermissions originatingFromSecurityGroupId(String groupId) {
         return originatingFromSecurityGroupIds(ImmutableSet.of(checkNotNull(groupId, "groupId")));
      }

      public IpPermissions originatingFromSecurityGroupIds(Iterable<String> groupIds) {
         return new IpPermissions(getIpProtocol(), getFromPort(), getToPort(), getTenantIdGroupNamePairs(), groupIds,
               ImmutableSet.<String> of());
      }
   }

   public static class ToSourceSelection extends ToGroupSourceSelection {
      ToSourceSelection(IpProtocol ipProtocol, int fromPort, int toPort) {
         super(ipProtocol, fromPort, toPort);
      }

      public IpPermissions originatingFromCidrBlock(String cidrIp) {
         return originatingFromCidrBlocks(ImmutableSet.of(checkNotNull(cidrIp, "cidrIp")));
      }

      public IpPermissions originatingFromCidrBlocks(Iterable<String> cidrIps) {
         return new IpPermissions(getIpProtocol(), getFromPort(), getToPort(),
               ImmutableMultimap.<String, String> of(), ImmutableSet.<String> of(), cidrIps);
      }

      public IpPermissions originatingFromTenantAndSecurityGroup(String tenantId, String groupName) {
         return toTenantsGroupsNamed(ImmutableMultimap.of(checkNotNull(tenantId, "tenantId"),
               checkNotNull(groupName, "groupName")));
      }

      public IpPermissions toTenantsGroupsNamed(Multimap<String, String> tenantIdGroupNamePairs) {
         return new IpPermissions(getIpProtocol(), getFromPort(), getToPort(), tenantIdGroupNamePairs, getGroupIds(),
               ImmutableSet.<String> of());
      }
   }
}
