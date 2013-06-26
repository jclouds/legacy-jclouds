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
package org.jclouds.ec2.compute.extensions;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Predicates.equalTo;
import static com.google.common.base.Predicates.not;
import static com.google.common.base.Predicates.notNull;
import static com.google.common.collect.Iterables.concat;
import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.getOnlyElement;
import static com.google.common.collect.Iterables.toArray;
import static com.google.common.collect.Iterables.transform;

import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;

import org.jclouds.Constants;
import org.jclouds.aws.util.AWSUtils;
import org.jclouds.collect.Memoized;
import org.jclouds.compute.domain.SecurityGroup;
import org.jclouds.compute.domain.SecurityGroupBuilder;
import org.jclouds.compute.extensions.SecurityGroupExtension;
import org.jclouds.compute.functions.GroupNamingConvention;
import org.jclouds.compute.functions.GroupNamingConvention.Factory;
import org.jclouds.domain.Location;
import org.jclouds.ec2.EC2Client;
import org.jclouds.ec2.compute.domain.RegionAndName;
import org.jclouds.ec2.compute.domain.RegionNameAndIngressRules;
import org.jclouds.ec2.domain.RunningInstance;
import org.jclouds.ec2.domain.UserIdGroupPair;
import org.jclouds.location.Region;
import org.jclouds.net.domain.IpPermission;
import org.jclouds.net.domain.IpProtocol;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.UncheckedTimeoutException;

/**
 * An extension to compute service to allow for the manipulation of {@link SecurityGroup}s. Implementation
 * is optional by providers.
 * 
 * @author Andrew Bayer
 */
public class EC2SecurityGroupExtension implements SecurityGroupExtension {

   protected final EC2Client client;
   protected final ListeningExecutorService userExecutor;
   protected final Supplier<Set<String>> regions;
   protected final Function<org.jclouds.ec2.domain.SecurityGroup, SecurityGroup> groupConverter;
   protected final Supplier<Set<? extends Location>> locations;
   protected final LoadingCache<RegionAndName, String> groupCreator;
   protected final Factory namingConvention;

   @Inject
   public EC2SecurityGroupExtension(EC2Client client,
                                    @Named(Constants.PROPERTY_USER_THREADS) ListeningExecutorService userExecutor,
                                    @Region Supplier<Set<String>> regions,
                                    Function<org.jclouds.ec2.domain.SecurityGroup, SecurityGroup> groupConverter,
                                    @Memoized Supplier<Set<? extends Location>> locations,
                                    @Named("SECURITY") LoadingCache<RegionAndName, String> groupCreator,
                                    GroupNamingConvention.Factory namingConvention) {
                                    
      this.client = checkNotNull(client, "client");
      this.userExecutor = checkNotNull(userExecutor, "userExecutor");
      this.regions = checkNotNull(regions, "regions");
      this.groupConverter = checkNotNull(groupConverter, "groupConverter");
      this.locations = checkNotNull(locations, "locations");
      this.groupCreator = checkNotNull(groupCreator, "groupCreator");
      this.namingConvention = checkNotNull(namingConvention, "namingConvention");
   }

   @Override
   public Set<SecurityGroup> listSecurityGroups() {
      Iterable<? extends org.jclouds.ec2.domain.SecurityGroup> rawGroups = pollSecurityGroups();
      Iterable<SecurityGroup> groups = transform(filter(rawGroups, notNull()),
                                                 groupConverter);
      return ImmutableSet.copyOf(groups);
   }


   @Override
   public Set<SecurityGroup> listSecurityGroupsInLocation(final Location location) {
      String region = AWSUtils.getRegionFromLocationOrNull(location);
      if (region == null) {
         return ImmutableSet.of();
      }
      return listSecurityGroupsInLocation(region);
   }

   public Set<SecurityGroup> listSecurityGroupsInLocation(String region) {
      Iterable<? extends org.jclouds.ec2.domain.SecurityGroup> rawGroups = pollSecurityGroupsByRegion(region);
      Iterable<SecurityGroup> groups = transform(filter(rawGroups, notNull()),
                                                 groupConverter);
      return ImmutableSet.copyOf(groups);
   }
   
   @Override
   public Set<SecurityGroup> listSecurityGroupsForNode(String id) {
      checkNotNull(id, "id");
      String[] parts = AWSUtils.parseHandle(id);
      String region = parts[0];
      String instanceId = parts[1];
      
      RunningInstance instance = getOnlyElement(Iterables.concat(client.getInstanceServices().describeInstancesInRegion(region, instanceId)));

      if (instance == null) {
         return ImmutableSet.of();
      }
      
      Set<String> groupNames = instance.getGroupNames();
      Set<? extends org.jclouds.ec2.domain.SecurityGroup> rawGroups =
         client.getSecurityGroupServices().describeSecurityGroupsInRegion(region, Iterables.toArray(groupNames, String.class));
      
      return ImmutableSet.copyOf(transform(filter(rawGroups, notNull()), groupConverter));
   }

   @Override
   public SecurityGroup getSecurityGroupById(String id) {
      checkNotNull(id, "id");
      String[] parts = AWSUtils.parseHandle(id);
      String region = parts[0];
      String groupId = parts[1];

      Set<? extends org.jclouds.ec2.domain.SecurityGroup> rawGroups =
         client.getSecurityGroupServices().describeSecurityGroupsInRegion(region, groupId);
      
      return getOnlyElement(transform(filter(rawGroups, notNull()), groupConverter));
   }

   @Override
   public SecurityGroup createSecurityGroup(String name, Location location) {
      String region = AWSUtils.getRegionFromLocationOrNull(location);
      if (region != null) {
         return createSecurityGroup(name, region);
      } else {
         return null;
      }
   }
   
   public SecurityGroup createSecurityGroup(String name, String region) {
      String markerGroup = namingConvention.create().sharedNameForGroup(name);
      RegionNameAndIngressRules regionAndName = new RegionNameAndIngressRules(region, markerGroup, new int[] {},
                                                                              false);

      groupCreator.getUnchecked(regionAndName);

      return getSecurityGroupById(regionAndName.slashEncode());
   }

   @Override
   public boolean removeSecurityGroup(String id) {
      checkNotNull(id, "id");
      String[] parts = AWSUtils.parseHandle(id);
      String region = parts[0];
      String groupName = parts[1];
      
      if (client.getSecurityGroupServices().describeSecurityGroupsInRegion(region, groupName).size() > 0) {
         client.getSecurityGroupServices().deleteSecurityGroupInRegion(region, groupName);
         // TODO: test this clear happens
         groupCreator.invalidate(new RegionNameAndIngressRules(region, groupName, null, false));
         return true;
      }

      return false;
   }


   @Override
   public SecurityGroup addIpPermission(IpPermission ipPermission, SecurityGroup group) {
      String region = AWSUtils.getRegionFromLocationOrNull(group.getLocation());
      String name = group.getName();

      if (ipPermission.getCidrBlocks().size() > 0) {
         for (String cidr : ipPermission.getCidrBlocks()) {
            client.getSecurityGroupServices().
               authorizeSecurityGroupIngressInRegion(region,
                                                     name,
                                                     ipPermission.getIpProtocol(),
                                                     ipPermission.getFromPort(),
                                                     ipPermission.getToPort(),
                                                     cidr);
         }
      }

      if (ipPermission.getTenantIdGroupNamePairs().size() > 0) {
         for (String userId : ipPermission.getTenantIdGroupNamePairs().keySet()) {
            for (String groupName : ipPermission.getTenantIdGroupNamePairs().get(userId)) {
               client.getSecurityGroupServices().
                  authorizeSecurityGroupIngressInRegion(region,
                                                        name,
                                                        new UserIdGroupPair(userId, groupName));
            }
         }
      }
      System.out.println("group: " + group);
      return getSecurityGroupById(new RegionAndName(region, group.getName()).slashEncode());
   }

   @Override
   public SecurityGroup addIpPermission(IpProtocol protocol, int startPort, int endPort,
                                        Multimap<String, String> tenantIdGroupNamePairs,
                                        Iterable<String> ipRanges,
                                        Iterable<String> groupIds, SecurityGroup group) {
      String region = AWSUtils.getRegionFromLocationOrNull(group.getLocation());
      String name = group.getName();

      if (Iterables.size(ipRanges) > 0) {
         for (String cidr : ipRanges) {
            client.getSecurityGroupServices().
               authorizeSecurityGroupIngressInRegion(region,
                                                     name,
                                                     protocol,
                                                     startPort,
                                                     endPort,
                                                     cidr);
         }
      }

      if (tenantIdGroupNamePairs.size() > 0) {
         for (String userId : tenantIdGroupNamePairs.keySet()) {
            for (String groupName : tenantIdGroupNamePairs.get(userId)) {
               client.getSecurityGroupServices().
                  authorizeSecurityGroupIngressInRegion(region,
                                                        name,
                                                        new UserIdGroupPair(userId, groupName));
            }
         }
      }
      
      return getSecurityGroupById(new RegionAndName(region, group.getName()).slashEncode());
   }
      
   @Override
   public SecurityGroup removeIpPermission(IpPermission ipPermission, SecurityGroup group) {
      String region = AWSUtils.getRegionFromLocationOrNull(group.getLocation());
      String name = group.getName();

      if (ipPermission.getCidrBlocks().size() > 0) {
         for (String cidr : ipPermission.getCidrBlocks()) {
            client.getSecurityGroupServices().
               revokeSecurityGroupIngressInRegion(region,
                                                  name,
                                                  ipPermission.getIpProtocol(),
                                                  ipPermission.getFromPort(),
                                                  ipPermission.getToPort(),
                                                  cidr);
         }
      }

      if (ipPermission.getTenantIdGroupNamePairs().size() > 0) {
         for (String userId : ipPermission.getTenantIdGroupNamePairs().keySet()) {
            for (String groupName : ipPermission.getTenantIdGroupNamePairs().get(userId)) {
               client.getSecurityGroupServices().
                  revokeSecurityGroupIngressInRegion(region,
                                                     name,
                                                     new UserIdGroupPair(userId, groupName));
            }
         }
      }

      return getSecurityGroupById(new RegionAndName(region, group.getName()).slashEncode());
   }

   @Override
   public SecurityGroup removeIpPermission(IpProtocol protocol, int startPort, int endPort,
                                           Multimap<String, String> tenantIdGroupNamePairs,
                                           Iterable<String> ipRanges,
                                           Iterable<String> groupIds, SecurityGroup group) {
      String region = AWSUtils.getRegionFromLocationOrNull(group.getLocation());
      String name = group.getName();

      if (Iterables.size(ipRanges) > 0) {
         for (String cidr : ipRanges) {
            client.getSecurityGroupServices().
               revokeSecurityGroupIngressInRegion(region,
                                                  name,
                                                  protocol,
                                                  startPort,
                                                  endPort,
                                                  cidr);
         }
      }

      if (tenantIdGroupNamePairs.size() > 0) {
         for (String userId : tenantIdGroupNamePairs.keySet()) {
            for (String groupName : tenantIdGroupNamePairs.get(userId)) {
               client.getSecurityGroupServices().
                  revokeSecurityGroupIngressInRegion(region,
                                                     name,
                                                     new UserIdGroupPair(userId, groupName));
            }
         }
      }
      
      return getSecurityGroupById(new RegionAndName(region, group.getName()).slashEncode());
   }

   @Override
   public boolean supportsTenantIdGroupNamePairs() {
      return true;
   }

   @Override
   public boolean supportsGroupIds() {
      return false;
   }

   @Override
   public boolean supportsPortRangesForGroups() {
      return false;
   }

   protected Iterable<? extends org.jclouds.ec2.domain.SecurityGroup> pollSecurityGroups() {
      Iterable<? extends Set<? extends org.jclouds.ec2.domain.SecurityGroup>> groups
         = transform(regions.get(), allSecurityGroupsInRegion());
      
      return concat(groups);
   }

   
   protected Iterable<? extends org.jclouds.ec2.domain.SecurityGroup> pollSecurityGroupsByRegion(String region) {
      return allSecurityGroupsInRegion().apply(region);
   }

   protected Function<String, Set<? extends org.jclouds.ec2.domain.SecurityGroup>> allSecurityGroupsInRegion() {
      return new Function<String, Set<? extends org.jclouds.ec2.domain.SecurityGroup>>() {
         
         @Override
         public Set<? extends org.jclouds.ec2.domain.SecurityGroup> apply(String from) {
            return client.getSecurityGroupServices().describeSecurityGroupsInRegion(from);
         }
         
      };
   }

   protected Location findLocationWithId(final String locationId) {
      if (locationId == null)
         return null;
      try {
         Location location = Iterables.find(locations.get(), new Predicate<Location>() {

            @Override
            public boolean apply(Location input) {
               return input.getId().equals(locationId);
            }

         });
         return location;

      } catch (NoSuchElementException e) {
         return null;
      }
   }

}
