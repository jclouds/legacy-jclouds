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
package org.jclouds.ec2.services;

import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.jclouds.concurrent.Timeout;
import org.jclouds.ec2.domain.IpProtocol;
import org.jclouds.ec2.domain.SecurityGroup;
import org.jclouds.ec2.domain.UserIdGroupPair;
import org.jclouds.javax.annotation.Nullable;

/**
 * Provides access to EC2 via their REST API.
 * <p/>
 * 
 * @author Adrian Cole
 */
@Timeout(duration = 45, timeUnit = TimeUnit.SECONDS)
public interface SecurityGroupClient {

   /**
    * Creates a new security group. Group names must be unique per identity.
    * 
    * @param region
    *           Security groups are not copied across Regions. Instances within the Region cannot
    *           communicate with instances outside the Region using group-based firewall rules.
    *           Traffic from instances in another Region is seen as WAN bandwidth.
    * @param name
    *           Name of the security group. Accepts alphanumeric characters, spaces, dashes, and
    *           underscores.
    * @param description
    *           Description of the group. This is informational only. If the description contains
    *           spaces, you must enc lose it in single quotes (') or URL-encode it. Accepts
    *           alphanumeric characters, spaces, dashes, and underscores.
    * @see #runInstances
    * @see #describeSecurityGroups
    * @see #authorizeSecurityGroupIngress
    * @see #revokeSecurityGroupIngress
    * @see #deleteSecurityGroup
    * 
    * @see <a href="http://docs.amazonwebservices.com/AWSEC2/latest/APIReference/ApiReference-query-CreateSecurityGroup.html"
    *      />
    */
   void createSecurityGroupInRegion(@Nullable String region, String name, String description);

   /**
    * Deletes a security group that you own.
    * 
    * @param region
    *           Security groups are not copied across Regions. Instances within the Region cannot
    *           communicate with instances outside the Region using group-based firewall rules.
    *           Traffic from instances in another Region is seen as WAN bandwidth.
    * @param name
    *           Name of the security group to delete.
    * 
    * @see #describeSecurityGroups
    * @see #authorizeSecurityGroupIngress
    * @see #revokeSecurityGroupIngress
    * @see #createSecurityGroup
    * 
    * @see <a href="http://docs.amazonwebservices.com/AWSEC2/latest/APIReference/ApiReference-query-DeleteSecurityGroup.html"
    *      />
    */
   void deleteSecurityGroupInRegion(@Nullable String region, String name);

   /**
    * Returns information about security groups that you own.
    * 
    * @param region
    *           Security groups are not copied across Regions. Instances within the Region cannot
    *           communicate with instances outside the Region using group-based firewall rules.
    *           Traffic from instances in another Region is seen as WAN bandwidth.
    * @param securityGroupNames
    *           Name of the security groups
    * 
    * @see #createSecurityGroup
    * @see #authorizeSecurityGroupIngress
    * @see #revokeSecurityGroupIngress
    * @see #deleteSecurityGroup
    * 
    * @see <a href="http://docs.amazonwebservices.com/AWSEC2/latest/APIReference/ApiReference-query-DescribeSecurityGroups.html"
    *      />
    */
   Set<SecurityGroup> describeSecurityGroupsInRegion(@Nullable String region,
            String... securityGroupNames);

   /**
    * 
    * Adds permissions to a security group based on another group.
    * 
    * @param region
    *           Security groups are not copied across Regions. Instances within the Region cannot
    *           communicate with instances outside the Region using group-based firewall rules.
    *           Traffic from instances in another Region is seen as WAN bandwidth.
    * @param groupName
    *           Name of the group to modify. The name must be valid and belong to the identity
    * @param sourceSecurityGroup
    *           group to associate with this group.
    * 
    * @see #createSecurityGroup
    * @see #describeSecurityGroups
    * @see #revokeSecurityGroupIngress
    * @see #deleteSecurityGroup
    * 
    * @see <a href="http://docs.amazonwebservices.com/AWSEC2/latest/APIReference/ApiReference-query-AuthorizeSecurityGroupIngress.html"
    * 
    */
   void authorizeSecurityGroupIngressInRegion(@Nullable String region, String groupName,
            UserIdGroupPair sourceSecurityGroup);

   /**
    * 
    * Adds permissions to a security group.
    * <p/>
    * Permissions are specified by the IP protocol (TCP, UDP or ICMP), the source of the request (by
    * IP range or an Amazon EC2 user-group pair), the source and destination port ranges (for TCP
    * and UDP), and the ICMP codes and types (for ICMP). When authorizing ICMP, -1 can be used as a
    * wildcard in the type and code fields. Permission changes are propagated to instances within
    * the security group as quickly as possible. However, depending on the number of instances, a
    * small delay might occur.
    * 
    * @param region
    *           Security groups are not copied across Regions. Instances within the Region cannot
    *           communicate with instances outside the Region using group-based firewall rules.
    *           Traffic from instances in another Region is seen as WAN bandwidth.
    * @param groupName
    *           Name of the group to modify. The name must be valid and belong to the identity
    * @param ipProtocol
    *           IP protocol.
    * @param fromPort
    *           Start of port range for the TCP and UDP protocols, or an ICMP type number. An ICMP
    *           type number of -1 indicates a wildcard (i.e., any ICMP type number).
    * @param toPort
    *           End of port range for the TCP and UDP protocols, or an ICMP code. An ICMP code of -1
    *           indicates a wildcard (i.e., any ICMP code).
    * @param cidrIp
    *           CIDR range.
    * 
    * @see #createSecurityGroup
    * @see #describeSecurityGroups
    * @see #revokeSecurityGroupIngress
    * @see #deleteSecurityGroup
    * 
    * @see <a href="http://docs.amazonwebservices.com/AWSEC2/latest/APIReference/ApiReference-query-AuthorizeSecurityGroupIngress.html"
    * 
    */
   void authorizeSecurityGroupIngressInRegion(@Nullable String region, String groupName,
            IpProtocol ipProtocol, int fromPort, int toPort, String cidrIp);

   /**
    * 
    * Revokes permissions from a security group. The permissions used to revoke must be specified
    * using the same values used to grant the permissions.
    * 
    * @param region
    *           Security groups are not copied across Regions. Instances within the Region cannot
    *           communicate with instances outside the Region using group-based firewall rules.
    *           Traffic from instances in another Region is seen as WAN bandwidth.
    * @param groupName
    *           Name of the group to modify. The name must be valid and belong to the identity
    * @param sourceSecurityGroup
    *           group to associate with this group.
    * 
    * @see #createSecurityGroup
    * @see #describeSecurityGroups
    * @see #authorizeSecurityGroupIngress
    * @see #deleteSecurityGroup
    * 
    * @see <a href="http://docs.amazonwebservices.com/AWSEC2/latest/APIReference/ApiReference-query-RevokeSecurityGroupIngress.html"
    * 
    */
   void revokeSecurityGroupIngressInRegion(@Nullable String region, String groupName,
            UserIdGroupPair sourceSecurityGroup);

   /**
    * 
    * Revokes permissions from a security group. The permissions used to revoke must be specified
    * using the same values used to grant the permissions.
    * <p/>
    * Permissions are specified by IP protocol (TCP, UDP, or ICMP), the source of the request (by IP
    * range or an Amazon EC2 user-group pair), the source and destination port ranges (for TCP and
    * UDP), and the ICMP codes and types (for ICMP).
    * 
    * Permission changes are quickly propagated to instances within the security group. However,
    * depending on the number of instances in the group, a small delay is might occur.
    * 
    * @param region
    *           Security groups are not copied across Regions. Instances within the Region cannot
    *           communicate with instances outside the Region using group-based firewall rules.
    *           Traffic from instances in another Region is seen as WAN bandwidth.
    * @param groupName
    *           Name of the group to modify. The name must be valid and belong to the identity
    * @param ipProtocol
    *           IP protocol.
    * @param fromPort
    *           Start of port range for the TCP and UDP protocols, or an ICMP type number. An ICMP
    *           type number of -1 indicates a wildcard (i.e., any ICMP type number).
    * @param toPort
    *           End of port range for the TCP and UDP protocols, or an ICMP code. An ICMP code of -1
    *           indicates a wildcard (i.e., any ICMP code).
    * @param cidrIp
    *           CIDR range.
    * 
    * @see #createSecurityGroup
    * @see #describeSecurityGroups
    * @see #authorizeSecurityGroupIngress
    * @see #deleteSecurityGroup
    * 
    * @see <a href="http://docs.amazonwebservices.com/AWSEC2/latest/APIReference/ApiReference-query-RevokeSecurityGroupIngress.html"
    * 
    */
   void revokeSecurityGroupIngressInRegion(@Nullable String region, String groupName, IpProtocol ipProtocol,
            int fromPort, int toPort, String cidrIp);
}
