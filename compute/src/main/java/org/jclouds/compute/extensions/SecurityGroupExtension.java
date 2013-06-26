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
package org.jclouds.compute.extensions;

import java.util.Set;

import org.jclouds.compute.domain.SecurityGroup;
import org.jclouds.domain.Location;
import org.jclouds.net.domain.IpPermission;
import org.jclouds.net.domain.IpProtocol;

import com.google.common.collect.Multimap;
import com.google.common.util.concurrent.ListenableFuture;

/**
 * An extension to compute service to allow for the manipulation of {@link SecurityGroup}s. Implementation
 * is optional by providers.
 * 
 * @author Andrew Bayer
 */
public interface SecurityGroupExtension {

   /**
    * List security groups.
    *
    * @return The set of @{link SecurityGroup}s we have access to.
    */
   Set<SecurityGroup> listSecurityGroups();

   /**
    * List security groups in a given @{link Location}.
    *
    * @return The set of @{link SecurityGroup}s we have access to in the given location.
    */
   Set<SecurityGroup> listSecurityGroupsInLocation(Location location);
   
   /**
    * List security groups for a given instance given the instance's ID.
    *
    * @return The set of @{link SecurityGroup}s for the given instance..
    */
   Set<SecurityGroup> listSecurityGroupsForNode(String id);

   /**
    * Get a security group by id.
    *
    * @return The @{link SecurityGroup}, if it exists.
    */
   SecurityGroup getSecurityGroupById(String id);

   /**
    * Create a new @{link SecurityGroup} from the parameters given.
    *
    * @param name
    *           The name of the security group
    * @param location
    *           The @{link Location} of the security group
    *
    * @return The SecurityGroup that has been created.
    */
   SecurityGroup createSecurityGroup(String name, Location location);

   /**
    * Remove an existing @{link SecurityGroup}, and its permissions.
    *
    * @param id
    *           The id of the SecurityGroup to delete.
    *
    * @return true if we were able to remove the group, false otherwise.
    */
   boolean removeSecurityGroup(String id);

   /**
    * Add a @{link IpPermission} to an existing @{link SecurityGroup}. Applies the permission to the
    *   security group on the provider.
    *
    * @param rule
    *           The IpPermission to add.
    * @param group
    *           The SecurityGroup to add the permission to.
    *
    * @return The SecurityGroup with the new permission added, after the permission has been applied on the provider.
    */
   SecurityGroup addIpPermission(IpPermission ipPermission, SecurityGroup group);

   /**
    * Remove a @{link IpPermission} from an existing @{link SecurityGroup}. Removes the permission from the
    *   security group on the provider.
    *
    * @param rule
    *           The IpPermission to remove.
    * @param group
    *           The SecurityGroup to remove the permission from.
    *
    * @return The SecurityGroup with the permission removed, after the permission has been removed on the provider.
    */
   SecurityGroup removeIpPermission(IpPermission ipPermission, SecurityGroup group);

   /**
    * Add a @{link IpPermission} to an existing @{link SecurityGroup}, based on the parameters given.
    *   Applies the permission to the security group on the provider.
    *
    * @param protocol
    *           The @{link IpProtocol} for the permission.
    * @param startPort
    *           The first port in the range to be opened, or -1 for ICMP.
    * @param endPort
    *           The last port in the range to be opened, or -1 for ICMP.
    * @param tenantIdGroupNamePairs
    *           source of traffic allowed is on basis of another group in a tenant, as opposed to by cidr
    * @param ipRanges
    *           An Iterable of Strings representing the IP range(s) the permission should allow.
    * @param groupIds
    *           An Iterable of @{link SecurityGroup} IDs this permission should allow.
    * @param group
    *           The SecurityGroup to add the permission to.
    *
    * @return The SecurityGroup with the new permission added, after the permission has been applied on the provider.
    */
   SecurityGroup addIpPermission(IpProtocol protocol, int startPort, int endPort,
                                 Multimap<String, String> tenantIdGroupNamePairs,
                                 Iterable<String> ipRanges,
                                 Iterable<String> groupIds, SecurityGroup group);

   /**
    * Remove a @{link IpPermission} from an existing @{link SecurityGroup}, based on the parameters given.
    *   Removes the permission from the security group on the provider.
    *
    * @param protocol
    *           The @{link IpProtocol} for the permission.
    * @param startPort
    *           The first port in the range to be opened, or -1 for ICMP.
    * @param endPort
    *           The last port in the range to be opened, or -1 for ICMP.
    * @param tenantIdGroupNamePairs
    *           source of traffic allowed is on basis of another group in a tenant, as opposed to by cidr
    * @param ipRanges
    *           An Iterable of Strings representing the IP range(s) the permission should allow.
    * @param groupIds
    *           An Iterable of @{link SecurityGroup} IDs this permission should allow.
    * @param group
    *           The SecurityGroup to remove the permission from.
    *
    * @return The SecurityGroup with the permission removed, after the permission has been removed from the provider.
    */
   SecurityGroup removeIpPermission(IpProtocol protocol, int startPort, int endPort,
                                    Multimap<String, String> tenantIdGroupNamePairs,
                                    Iterable<String> ipRanges,
                                    Iterable<String> groupIds, SecurityGroup group);

   /**
    * Returns true if this SecurityGroupExtension supports tenant ID + group name pairs.
    */
   boolean supportsTenantIdGroupNamePairs();

   /**
    * Returns true if this SecurityGroupExtension supports group IDs.
    */
   boolean supportsGroupIds();

   /**
    * Returns true if this SecurityGroupExtension supports port ranges for group authorization.
    */
   boolean supportsPortRangesForGroups();
   
}
