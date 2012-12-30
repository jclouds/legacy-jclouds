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
package org.jclouds.cloudstack.features;

import java.util.Set;
import org.jclouds.cloudstack.domain.SecurityGroup;
import org.jclouds.cloudstack.options.AccountInDomainOptions;
import org.jclouds.cloudstack.options.ListSecurityGroupsOptions;

import com.google.common.collect.Multimap;

/**
 * Provides synchronous access to CloudStack security group features.
 * <p/>
 * 
 * @see SecurityGroupAsyncClient
 * @see <a href="http://download.cloud.com/releases/2.2.0/api_2.2.12/TOC_User.html" />
 * @author Adrian Cole
 */
public interface SecurityGroupClient {
   /**
    * Lists security groups
    * 
    * @param options
    *           if present, how to constrain the list.
    * @return security groups matching query, or empty set, if no security
    *         groups are found
    */
   Set<SecurityGroup> listSecurityGroups(ListSecurityGroupsOptions... options);

   /**
    * Authorizes a particular TCP or UDP ingress rule for this security group
    * 
    * @param securityGroupId
    *           The ID of the security group
    * @param protocol
    *           tcp or udp
    * @param startPort
    *           start port for this ingress rule
    * @param endPort
    *           end port for this ingress rule
    * @param cidrList
    *           the cidr list associated
    * @return response relating to the creation of this ingress rule
    */
   String authorizeIngressPortsToCIDRs(String securityGroupId, String protocol, int startPort, int endPort,
         Iterable<String> cidrList, AccountInDomainOptions... options);

   /**
    * Authorizes a particular TCP or UDP ingress rule for this security group
    * 
    * @param securityGroupId
    *           The ID of the security group
    * @param protocol
    *           tcp or udp
    * @param startPort
    *           start port for this ingress rule
    * @param endPort
    *           end port for this ingress rule
    * @param accountToGroup
    *           mapping of account names to security groups you wish to
    *           authorize
    * @return response relating to the creation of this ingress rule
    */
   String authorizeIngressPortsToSecurityGroups(String securityGroupId, String protocol, int startPort, int endPort,
         Multimap<String, String> accountToGroup, AccountInDomainOptions... options);

   /**
    * Authorizes a particular ICMP ingress rule for this security group
    * 
    * @param securityGroupId
    *           The ID of the security group
    * @param ICMPCode
    *           type of the icmp message being sent
    * @param ICMPType
    *           error code for this icmp message
    * @param cidrList
    *           the cidr list associated
    * @return response relating to the creation of this ingress rule
    */
   String authorizeIngressICMPToCIDRs(String securityGroupId, int ICMPCode, int ICMPType, Iterable<String> cidrList,
         AccountInDomainOptions... options);

   /**
    * Authorizes a particular ICMP ingress rule for this security group
    * 
    * @param securityGroupId
    *           The ID of the security group
    * @param ICMPCode
    *           type of the icmp message being sent
    * @param ICMPType
    *           error code for this icmp message
    * @param accountToGroup
    *           mapping of account names to security groups you wish to
    *           authorize
    * @return response relating to the creation of this ingress rule
    */
   String authorizeIngressICMPToSecurityGroups(String securityGroupId, int ICMPCode, int ICMPType,
         Multimap<String, String> accountToGroup, AccountInDomainOptions... options);

   /**
    * Deletes a particular ingress rule from this security group
    * 
    * @param id
    *           The ID of the ingress rule
    * @param options
    *           scope of the rule.
    */
   String revokeIngressRule(String id, AccountInDomainOptions... options);;

   /**
    * get a specific security group by id
    * 
    * @param id
    *           group to get
    * @return security group or null if not found
    */
   SecurityGroup getSecurityGroup(String id);

   /**
    * Creates a security group
    * 
    * @param name
    *           name of the security group
    * @return security group
    */
   SecurityGroup createSecurityGroup(String name);

   /**
    * delete a specific security group by id
    * 
    * @param id
    *           group to delete
    */
   void deleteSecurityGroup(String id);
}
