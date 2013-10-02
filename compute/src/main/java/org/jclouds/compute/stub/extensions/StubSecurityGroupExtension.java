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
package org.jclouds.compute.stub.extensions;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Predicates.equalTo;
import static com.google.common.base.Predicates.not;
import static com.google.common.collect.Iterables.filter;

import java.util.Set;
import java.util.concurrent.ConcurrentMap;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;

import org.jclouds.Constants;
import org.jclouds.compute.domain.SecurityGroup;
import org.jclouds.compute.domain.SecurityGroupBuilder;
import org.jclouds.compute.extensions.SecurityGroupExtension;
import org.jclouds.domain.Location;
import org.jclouds.location.suppliers.all.JustProvider;
import org.jclouds.net.domain.IpPermission;
import org.jclouds.net.domain.IpProtocol;

import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;
import com.google.common.util.concurrent.ListeningExecutorService;

/**
 * An extension to compute service to allow for the manipulation of {@link SecurityGroup}s. Implementation
 * is optional by providers.
 * 
 * @author Andrew Bayer
 */
public class StubSecurityGroupExtension implements SecurityGroupExtension {

   private final Supplier<Location> location;
   private final Provider<Integer> groupIdProvider;
   private final Supplier<Set<? extends Location>> locationSupplier;
   private final ListeningExecutorService ioExecutor;
   private final ConcurrentMap<String, SecurityGroup> groups;
   private final Multimap<String, SecurityGroup> groupsForNodes;

   @Inject
   public StubSecurityGroupExtension(ConcurrentMap<String, SecurityGroup> groups,
                                     @Named(Constants.PROPERTY_IO_WORKER_THREADS) ListeningExecutorService ioExecutor,
                                     Supplier<Location> location,
                                     @Named("GROUP_ID") Provider<Integer> groupIdProvider, 
                                     JustProvider locationSupplier,
                                     Multimap<String, SecurityGroup> groupsForNodes) {
      this.groups = groups;
      this.ioExecutor = ioExecutor;
      this.location = location;
      this.groupIdProvider = groupIdProvider;
      this.locationSupplier = locationSupplier;
      this.groupsForNodes = groupsForNodes;
   }

   @Override
   public Set<SecurityGroup> listSecurityGroups() {
      return ImmutableSet.copyOf(groups.values());
   }

   @Override
   public Set<SecurityGroup> listSecurityGroupsInLocation(final Location location) {
      return ImmutableSet.copyOf(filter(groups.values(), new Predicate<SecurityGroup>() {
               @Override
               public boolean apply(SecurityGroup group) {
                  return group.getLocation().equals(location);
               }
            }));
   }

   @Override
   public Set<SecurityGroup> listSecurityGroupsForNode(String nodeId) {
      return ImmutableSet.copyOf(groupsForNodes.get(nodeId));
   }

   @Override
   public SecurityGroup getSecurityGroupById(String id) {
      return groups.get(id);
   }
   
   @Override
   public SecurityGroup createSecurityGroup(String name, Location location) {
      SecurityGroupBuilder builder = new SecurityGroupBuilder();

      String id = groupIdProvider.get() + "";
      builder.ids(id);
      builder.name(name);
      builder.location(location);

      SecurityGroup group = builder.build();

      groups.put(group.getId(), group);

      return group;
   }

   @Override
   public boolean removeSecurityGroup(String id) {
      if (groups.containsKey(id)) {
         groups.remove(id);
         return true;
      }
      return false;
   }

   @Override
   public SecurityGroup addIpPermission(IpPermission ipPermission, SecurityGroup group) {
      SecurityGroupBuilder builder = SecurityGroupBuilder.fromSecurityGroup(checkNotNull(group, "group"));

      builder.ipPermission(checkNotNull(ipPermission, "ipPermission"));

      SecurityGroup newGroup = builder.build();

      if (groups.containsKey(newGroup.getId())) {
         groups.remove(newGroup.getId());
      }

      groups.put(newGroup.getId(), newGroup);

      return newGroup;
   }

   @Override
   public SecurityGroup addIpPermission(IpProtocol protocol, int startPort, int endPort,
                                 Multimap<String, String> tenantIdGroupNamePairs,
                                 Iterable<String> ipRanges,
                                 Iterable<String> groupIds, SecurityGroup group) {
      IpPermission.Builder ipBuilder = IpPermission.builder();

      ipBuilder.ipProtocol(protocol);
      ipBuilder.fromPort(startPort);
      ipBuilder.toPort(endPort);
      if (tenantIdGroupNamePairs.size() > 0) {
         ipBuilder.tenantIdGroupNamePairs(tenantIdGroupNamePairs);
      }
      if (Iterables.size(ipRanges) > 0) {
         ipBuilder.cidrBlocks(ipRanges);
      }
      if (Iterables.size(groupIds) > 0) {
         ipBuilder.groupIds(groupIds);
      }

      IpPermission perm = ipBuilder.build();

      SecurityGroupBuilder builder = SecurityGroupBuilder.fromSecurityGroup(checkNotNull(group, "group"));

      builder.ipPermission(perm);

      SecurityGroup newGroup = builder.build();

      if (groups.containsKey(newGroup.getId())) {
         groups.remove(newGroup.getId());
      }

      groups.put(newGroup.getId(), newGroup);

      return newGroup;
   }
      
   @Override
   public SecurityGroup removeIpPermission(IpPermission ipPermission, SecurityGroup group) {
      SecurityGroupBuilder builder = SecurityGroupBuilder.fromSecurityGroup(checkNotNull(group, "group"));

      builder.ipPermissions();

      builder.ipPermissions(filter(group.getIpPermissions(), not(equalTo(ipPermission))));
                            
      SecurityGroup newGroup = builder.build();

      if (groups.containsKey(newGroup.getId())) {
         groups.remove(newGroup.getId());
      }

      groups.put(newGroup.getId(), newGroup);

      return newGroup;
   }
   

   @Override
   public SecurityGroup removeIpPermission(IpProtocol protocol, int startPort, int endPort,
                                    Multimap<String, String> tenantIdGroupNamePairs,
                                    Iterable<String> ipRanges,
                                    Iterable<String> groupIds, SecurityGroup group) {
      IpPermission.Builder ipBuilder = IpPermission.builder();

      ipBuilder.ipProtocol(protocol);
      ipBuilder.fromPort(startPort);
      ipBuilder.toPort(endPort);
      if (tenantIdGroupNamePairs.size() > 0) {
         ipBuilder.tenantIdGroupNamePairs(tenantIdGroupNamePairs);
      }
      if (Iterables.size(ipRanges) > 0) {
         ipBuilder.cidrBlocks(ipRanges);
      }
      if (Iterables.size(groupIds) > 0) {
         ipBuilder.groupIds(groupIds);
      }

      IpPermission perm = ipBuilder.build();

      SecurityGroupBuilder builder = SecurityGroupBuilder.fromSecurityGroup(checkNotNull(group, "group"));

      builder.ipPermissions();

      builder.ipPermissions(filter(group.getIpPermissions(), not(equalTo(perm))));
      
      SecurityGroup newGroup = builder.build();

      if (groups.containsKey(newGroup.getId())) {
         groups.remove(newGroup.getId());
      }

      groups.put(newGroup.getId(), newGroup);

      return newGroup;
   }

   @Override
   public boolean supportsTenantIdGroupNamePairs() {
      return false;
   }

   @Override
   public boolean supportsTenantIdGroupIdPairs() {
      return false;
   }

   @Override
   public boolean supportsGroupIds() {
      return true;
   }

   @Override
   public boolean supportsPortRangesForGroups() {
      return true;
   }
}
