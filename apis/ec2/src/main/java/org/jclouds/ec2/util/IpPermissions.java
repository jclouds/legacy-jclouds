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
package org.jclouds.ec2.util;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;
import java.util.Map.Entry;

import org.jclouds.ec2.domain.IpPermission;
import org.jclouds.ec2.domain.IpPermissionImpl;
import org.jclouds.ec2.domain.IpProtocol;
import org.jclouds.util.Maps2;

import com.google.common.annotations.Beta;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;

/**
 * 
 * Shortcut to create ingress rules
 * 
 * @author Adrian Cole
 */
public class IpPermissions extends IpPermissionImpl {

   protected IpPermissions(IpProtocol ipProtocol, int fromPort, int toPort,
         Multimap<String, String> userIdGroupPairs, Iterable<String> groupIds, Iterable<String> ipRanges) {
      super(ipProtocol, fromPort, toPort, userIdGroupPairs, groupIds, userIdGroupPairs.size() == 0 ? ipRanges
            : ImmutableSet.<String> of());
   }

   /**
    * don't rely on this being here.. it will move
    */
   @Beta
   public static Multimap<String, String> buildFormParametersForIndex(final int index, IpPermission permission) {
      Map<String, String> headers = Maps.newLinkedHashMap();
      headers.put("IpPermissions.%d.IpProtocol", permission.getIpProtocol().toString());
      headers.put("IpPermissions.%d.FromPort", permission.getFromPort() + "");
      headers.put("IpPermissions.%d.ToPort", permission.getToPort() + "");
      String prefix = "IpPermissions.%d.IpRanges.";
      int i = 0;
      for (String cidrIp : checkNotNull(permission.getIpRanges(), "cidrIps")) {
         headers.put(prefix + i++ + ".CidrIp", cidrIp);
      }
      prefix = "IpPermissions.%d.Groups.";
      i = 0;
      for (String groupId : checkNotNull(permission.getGroupIds(), "groupIds")) {
         headers.put(prefix + i++ + ".GroupId", groupId);
      }
      prefix = "IpPermissions.%d.Groups.";
      i = 0;
      for (Entry<String, String> userIdGroupNamePair : checkNotNull(permission.getUserIdGroupPairs(),
            "userIdGroupNamePairs").entries()) {
         headers.put(prefix + i++ + ".UserId", userIdGroupNamePair.getKey());
         headers.put(prefix + i + ".GroupName", userIdGroupNamePair.getValue());
      }
      prefix = "IpPermissions.%d.IpRanges.";
      i = 0;
      for (String cidrIp : checkNotNull(permission.getIpRanges(), "cidrIps")) {
         headers.put(prefix + i++ + ".CidrIp", cidrIp);
      }
      return Multimaps.forMap(Maps2.transformKeys(headers, new Function<String, String>() {

         @Override
         public String apply(String arg0) {
            return String.format(arg0, index);
         }

      }));
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

   public static class ToVPCSourceSelection extends IpPermissions {

      protected ToVPCSourceSelection(IpProtocol ipProtocol, int fromPort, int toPort) {
         super(ipProtocol, fromPort, toPort, ImmutableMultimap.<String, String> of(), ImmutableSet.<String> of(),
               ImmutableSet.of("0.0.0.0/0"));
      }

      public IpPermissions originatingFromSecurityGroupId(String groupId) {
         return toVPCSecurityGroups(ImmutableSet.of(checkNotNull(groupId, "groupId")));
      }

      public IpPermissions toVPCSecurityGroups(Iterable<String> groupIds) {
         return new IpPermissions(getIpProtocol(), getFromPort(), getToPort(), getUserIdGroupPairs(), groupIds,
               ImmutableSet.<String> of());
      }
   }

   public static class ToSourceSelection extends ToVPCSourceSelection {
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

      public IpPermissions originatingFromUserAndSecurityGroup(String userId, String groupName) {
         return toEC2SecurityGroups(ImmutableMultimap.of(checkNotNull(userId, "userId"),
               checkNotNull(groupName, "groupName")));
      }

      public IpPermissions toEC2SecurityGroups(Multimap<String, String> userIdGroupNamePairs) {
         return new IpPermissions(getIpProtocol(), getFromPort(), getToPort(), userIdGroupNamePairs, getGroupIds(),
               ImmutableSet.<String> of());
      }
   }
}
