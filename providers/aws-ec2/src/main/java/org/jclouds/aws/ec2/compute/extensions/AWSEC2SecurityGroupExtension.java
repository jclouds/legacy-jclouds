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
package org.jclouds.aws.ec2.compute.extensions;

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
import org.jclouds.aws.ec2.AWSEC2Api;
import org.jclouds.aws.util.AWSUtils;
import org.jclouds.collect.Memoized;
import org.jclouds.compute.domain.SecurityGroup;
import org.jclouds.compute.domain.SecurityGroupBuilder;
import org.jclouds.compute.extensions.SecurityGroupExtension;
import org.jclouds.compute.functions.GroupNamingConvention;
import org.jclouds.compute.functions.GroupNamingConvention.Factory;
import org.jclouds.domain.Location;
import org.jclouds.ec2.compute.domain.RegionAndName;
import org.jclouds.ec2.compute.domain.RegionNameAndIngressRules;
import org.jclouds.ec2.compute.extensions.EC2SecurityGroupExtension;
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
public class AWSEC2SecurityGroupExtension extends EC2SecurityGroupExtension {
   protected final AWSEC2Api client;

   @Inject
   public AWSEC2SecurityGroupExtension(AWSEC2Api client,
                                       @Named(Constants.PROPERTY_USER_THREADS) ListeningExecutorService userExecutor,
                                       @Region Supplier<Set<String>> regions,
                                       Function<org.jclouds.ec2.domain.SecurityGroup, SecurityGroup> groupConverter,
                                       @Memoized Supplier<Set<? extends Location>> locations,
                                       @Named("SECURITY") LoadingCache<RegionAndName, String> groupCreator,
                                       GroupNamingConvention.Factory namingConvention) {
      super(client, userExecutor, regions, groupConverter, locations, groupCreator, namingConvention);
      this.client = checkNotNull(client, "client");
   }


   @Override
   public SecurityGroup addIpPermission(IpPermission ipPermission, SecurityGroup group) {

      String region = AWSUtils.getRegionFromLocationOrNull(group.getLocation());
      String name = group.getName();

      client.getSecurityGroupApi().get().authorizeSecurityGroupIngressInRegion(region, name, ipPermission);

      return getSecurityGroupById(new RegionAndName(region, group.getName()).slashEncode());
   }

   @Override
   public SecurityGroup addIpPermission(IpProtocol protocol, int startPort, int endPort,
                                        Multimap<String, String> tenantIdGroupNamePairs,
                                        Iterable<String> ipRanges,
                                        Iterable<String> groupIds, SecurityGroup group) {
      String region = AWSUtils.getRegionFromLocationOrNull(group.getLocation());
      String name = group.getName();

      IpPermission.Builder builder = IpPermission.builder();

      builder.ipProtocol(protocol);
      builder.fromPort(startPort);
      builder.toPort(endPort);
      
      if (Iterables.size(ipRanges) > 0) {
         for (String cidr : ipRanges) {
            builder.cidrBlock(cidr);
         }
      }

      if (tenantIdGroupNamePairs.size() > 0) {
         for (String userId : tenantIdGroupNamePairs.keySet()) {
            for (String groupName : tenantIdGroupNamePairs.get(userId)) {
               builder.tenantIdGroupNamePair(userId, groupName);
            }
         }
      }

      client.getSecurityGroupApi().get().authorizeSecurityGroupIngressInRegion(region, name, builder.build());

      return getSecurityGroupById(new RegionAndName(region, group.getName()).slashEncode());
   }
      
   @Override
   public SecurityGroup removeIpPermission(IpPermission ipPermission, SecurityGroup group) {
      String region = AWSUtils.getRegionFromLocationOrNull(group.getLocation());
      String name = group.getName();

      client.getSecurityGroupApi().get().revokeSecurityGroupIngressInRegion(region, name, ipPermission);

      return getSecurityGroupById(new RegionAndName(region, group.getName()).slashEncode());
   }

   @Override
   public SecurityGroup removeIpPermission(IpProtocol protocol, int startPort, int endPort,
                                           Multimap<String, String> tenantIdGroupNamePairs,
                                           Iterable<String> ipRanges,
                                           Iterable<String> groupIds, SecurityGroup group) {
      String region = AWSUtils.getRegionFromLocationOrNull(group.getLocation());
      String name = group.getName();


      IpPermission.Builder builder = IpPermission.builder();

      builder.ipProtocol(protocol);
      builder.fromPort(startPort);
      builder.toPort(endPort);
      
      if (Iterables.size(ipRanges) > 0) {
         for (String cidr : ipRanges) {
            builder.cidrBlock(cidr);
         }
      }

      if (tenantIdGroupNamePairs.size() > 0) {
         for (String userId : tenantIdGroupNamePairs.keySet()) {
            for (String groupName : tenantIdGroupNamePairs.get(userId)) {
               builder.tenantIdGroupNamePair(userId, groupName);
            }
         }
      }

      client.getSecurityGroupApi().get().revokeSecurityGroupIngressInRegion(region, name, builder.build());

      return getSecurityGroupById(new RegionAndName(region, group.getName()).slashEncode());
   }
}
