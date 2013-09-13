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
package org.jclouds.openstack.nova.v2_0.compute.extensions;


import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Predicates.and;
import static com.google.common.base.Predicates.notNull;
import static com.google.common.collect.Iterables.concat;
import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.transform;
import static org.jclouds.openstack.nova.v2_0.predicates.SecurityGroupPredicates.nameIn;
import static org.jclouds.openstack.nova.v2_0.predicates.SecurityGroupPredicates.ruleCidr;
import static org.jclouds.openstack.nova.v2_0.predicates.SecurityGroupPredicates.ruleEndPort;
import static org.jclouds.openstack.nova.v2_0.predicates.SecurityGroupPredicates.ruleGroup;
import static org.jclouds.openstack.nova.v2_0.predicates.SecurityGroupPredicates.ruleProtocol;
import static org.jclouds.openstack.nova.v2_0.predicates.SecurityGroupPredicates.ruleStartPort;

import java.util.Set;

import javax.inject.Inject;
import javax.inject.Named;

import org.jclouds.Constants;
import org.jclouds.compute.domain.SecurityGroup;
import org.jclouds.compute.extensions.SecurityGroupExtension;
import org.jclouds.compute.functions.GroupNamingConvention;
import org.jclouds.domain.Location;
import org.jclouds.location.Zone;
import org.jclouds.net.domain.IpPermission;
import org.jclouds.net.domain.IpProtocol;
import org.jclouds.openstack.nova.v2_0.NovaApi;
import org.jclouds.openstack.nova.v2_0.domain.Ingress;
import org.jclouds.openstack.nova.v2_0.domain.SecurityGroupRule;
import org.jclouds.openstack.nova.v2_0.domain.ServerWithSecurityGroups;
import org.jclouds.openstack.nova.v2_0.domain.zonescoped.SecurityGroupInZone;
import org.jclouds.openstack.nova.v2_0.domain.zonescoped.ZoneAndId;
import org.jclouds.openstack.nova.v2_0.domain.zonescoped.ZoneAndName;
import org.jclouds.openstack.nova.v2_0.domain.zonescoped.ZoneSecurityGroupNameAndPorts;
import org.jclouds.openstack.nova.v2_0.extensions.SecurityGroupApi;
import org.jclouds.openstack.nova.v2_0.extensions.ServerWithSecurityGroupsApi;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Supplier;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;
import com.google.common.util.concurrent.ListeningExecutorService;

/**
 * An extension to compute service to allow for the manipulation of {@link org.jclouds.compute.domain.SecurityGroup}s. Implementation
 * is optional by providers.
 *
 * @author Andrew Bayer
 */
public class NovaSecurityGroupExtension implements SecurityGroupExtension {

   protected final NovaApi api;
   protected final ListeningExecutorService userExecutor;
   protected final Supplier<Set<String>> zoneIds;
   protected final Function<SecurityGroupInZone, SecurityGroup> groupConverter;
   protected final LoadingCache<ZoneAndName, SecurityGroupInZone> groupCreator;
   protected final GroupNamingConvention.Factory namingConvention;

   @Inject
   public NovaSecurityGroupExtension(NovaApi api,
                                    @Named(Constants.PROPERTY_USER_THREADS) ListeningExecutorService userExecutor,
                                    @Zone Supplier<Set<String>> zoneIds,
                                    Function<SecurityGroupInZone, SecurityGroup> groupConverter,
                                    LoadingCache<ZoneAndName, SecurityGroupInZone> groupCreator,
                                    GroupNamingConvention.Factory namingConvention) {

      this.api = checkNotNull(api, "api");
      this.userExecutor = checkNotNull(userExecutor, "userExecutor");
      this.zoneIds = checkNotNull(zoneIds, "zoneIds");
      this.groupConverter = checkNotNull(groupConverter, "groupConverter");
      this.groupCreator = checkNotNull(groupCreator, "groupCreator");
      this.namingConvention = checkNotNull(namingConvention, "namingConvention");
   }

   @Override
   public Set<SecurityGroup> listSecurityGroups() {
      Iterable<? extends SecurityGroupInZone> rawGroups = pollSecurityGroups();
      Iterable<SecurityGroup> groups = transform(filter(rawGroups, notNull()),
              groupConverter);
      return ImmutableSet.copyOf(groups);
   }


   @Override
   public Set<SecurityGroup> listSecurityGroupsInLocation(final Location location) {
      String zone = location.getId();
      if (zone == null) {
         return ImmutableSet.of();
      }
      return listSecurityGroupsInLocation(zone);
   }

   public Set<SecurityGroup> listSecurityGroupsInLocation(String zone) {
      Iterable<? extends SecurityGroupInZone> rawGroups = pollSecurityGroupsByZone(zone);
      Iterable<SecurityGroup> groups = transform(filter(rawGroups, notNull()),
              groupConverter);
      return ImmutableSet.copyOf(groups);
   }

   @Override
   public Set<SecurityGroup> listSecurityGroupsForNode(String id) {
      ZoneAndId zoneAndId = ZoneAndId.fromSlashEncoded(checkNotNull(id, "id"));
      String zone = zoneAndId.getZone();
      String instanceId = zoneAndId.getId();

      Optional<? extends ServerWithSecurityGroupsApi> serverApi = api.getServerWithSecurityGroupsExtensionForZone(zone);
      Optional<? extends SecurityGroupApi> sgApi = api.getSecurityGroupExtensionForZone(zone);

      if (!serverApi.isPresent() || !sgApi.isPresent()) {
         return ImmutableSet.of();
      }

      ServerWithSecurityGroups instance = serverApi.get().get(instanceId);
      if (instance == null) {
         return ImmutableSet.of();
      }

      Set<String> groupNames = instance.getSecurityGroupNames();
      Set<? extends SecurityGroupInZone> rawGroups =
              sgApi.get().list().filter(nameIn(groupNames)).transform(groupToGroupInZone(zone)).toSet();

      return ImmutableSet.copyOf(transform(filter(rawGroups, notNull()), groupConverter));
   }

   @Override
   public SecurityGroup getSecurityGroupById(String id) {
      ZoneAndId zoneAndId = ZoneAndId.fromSlashEncoded(checkNotNull(id, "id"));
      String zone = zoneAndId.getZone();
      String groupId = zoneAndId.getId();

      Optional<? extends SecurityGroupApi> sgApi = api.getSecurityGroupExtensionForZone(zone);

      if (!sgApi.isPresent()) {
         return null;
      }

      SecurityGroupInZone rawGroup = new SecurityGroupInZone(sgApi.get().get(groupId), zone);

      return groupConverter.apply(rawGroup);
   }

   @Override
   public SecurityGroup createSecurityGroup(String name, Location location) {
      String zone = location.getId();
      if (zone == null) {
         return null;
      }
      return createSecurityGroup(name, zone);
   }

   public SecurityGroup createSecurityGroup(String name, String zone) {
      String markerGroup = namingConvention.create().sharedNameForGroup(name);
      ZoneSecurityGroupNameAndPorts zoneAndName = new ZoneSecurityGroupNameAndPorts(zone, markerGroup, ImmutableSet.<Integer> of());

      SecurityGroupInZone rawGroup = groupCreator.apply(zoneAndName);
      return groupConverter.apply(rawGroup);
   }

   @Override
   public boolean removeSecurityGroup(String id) {
      checkNotNull(id, "id");
      ZoneAndId zoneAndId = ZoneAndId.fromSlashEncoded(id);
      String zone = zoneAndId.getZone();
      String groupId = zoneAndId.getId();

      Optional<? extends SecurityGroupApi> sgApi = api.getSecurityGroupExtensionForZone(zone);

      if (!sgApi.isPresent()) {
         return false;
      }

      if (sgApi.get().get(groupId) == null) {
         return false;
      }

      sgApi.get().delete(groupId);
      // TODO: test this clear happens
      groupCreator.invalidate(new ZoneSecurityGroupNameAndPorts(zone, groupId, ImmutableSet.<Integer> of()));
      return true;
   }

   @Override
   public SecurityGroup addIpPermission(IpPermission ipPermission, SecurityGroup group) {
      String zone = group.getLocation().getId();
      String id = group.getId();

      Optional<? extends SecurityGroupApi> sgApi = api.getSecurityGroupExtensionForZone(zone);

      if (!sgApi.isPresent()) {
         return null;
      }

      if (ipPermission.getCidrBlocks().size() > 0) {
         for (String cidr : ipPermission.getCidrBlocks()) {
            sgApi.get().createRuleAllowingCidrBlock(id,
                    Ingress.builder()
                            .ipProtocol(ipPermission.getIpProtocol())
                            .fromPort(ipPermission.getFromPort())
                            .toPort(ipPermission.getToPort())
                            .build(),
                    cidr);
         }
      }

      if (ipPermission.getGroupIds().size() > 0) {
         for (String zoneAndGroupRaw : ipPermission.getGroupIds()) {
            ZoneAndId zoneAndId = ZoneAndId.fromSlashEncoded(zoneAndGroupRaw);
            String groupId = zoneAndId.getId();
            sgApi.get().createRuleAllowingSecurityGroupId(id,
                    Ingress.builder()
                            .ipProtocol(ipPermission.getIpProtocol())
                            .fromPort(ipPermission.getFromPort())
                            .toPort(ipPermission.getToPort())
                            .build(),
                    groupId);
         }
      }

      return getSecurityGroupById(ZoneAndId.fromZoneAndId(zone, id).slashEncode());
   }

   @Override
   public SecurityGroup addIpPermission(IpProtocol protocol, int startPort, int endPort,
                                        Multimap<String, String> tenantIdGroupNamePairs,
                                        Iterable<String> ipRanges,
                                        Iterable<String> groupIds, SecurityGroup group) {
      String zone = group.getLocation().getId();
      String id = group.getId();

      Optional<? extends SecurityGroupApi> sgApi = api.getSecurityGroupExtensionForZone(zone);

      if (!sgApi.isPresent()) {
         return null;
      }

      if (Iterables.size(ipRanges) > 0) {
         for (String cidr : ipRanges) {
            sgApi.get().createRuleAllowingCidrBlock(id,
                    Ingress.builder()
                            .ipProtocol(protocol)
                            .fromPort(startPort)
                            .toPort(endPort)
                            .build(),
                    cidr);
         }
      }

      if (Iterables.size(groupIds) > 0) {
         for (String zoneAndGroupRaw : groupIds) {
            ZoneAndId zoneAndId = ZoneAndId.fromSlashEncoded(zoneAndGroupRaw);
            String groupId = zoneAndId.getId();
            sgApi.get().createRuleAllowingSecurityGroupId(id,
                    Ingress.builder()
                            .ipProtocol(protocol)
                            .fromPort(startPort)
                            .toPort(endPort)
                            .build(),
                    groupId);
         }
      }

      return getSecurityGroupById(ZoneAndId.fromZoneAndId(zone, id).slashEncode());
   }

   @Override
   public SecurityGroup removeIpPermission(IpPermission ipPermission, SecurityGroup group) {
      String zone = group.getLocation().getId();
      String id = group.getId();

      Optional<? extends SecurityGroupApi> sgApi = api.getSecurityGroupExtensionForZone(zone);

      if (!sgApi.isPresent()) {
         return null;
      }

      org.jclouds.openstack.nova.v2_0.domain.SecurityGroup securityGroup = sgApi.get().get(id);

      if (ipPermission.getCidrBlocks().size() > 0) {
         for (String cidr : ipPermission.getCidrBlocks()) {
            for (SecurityGroupRule rule : filter(securityGroup.getRules(),
                    and(ruleCidr(cidr), ruleProtocol(ipPermission.getIpProtocol()),
                            ruleStartPort(ipPermission.getFromPort()),
                            ruleEndPort(ipPermission.getToPort())))) {
               sgApi.get().deleteRule(rule.getId());
            }
         }
      }

      if (ipPermission.getGroupIds().size() > 0) {
         for (String groupId : ipPermission.getGroupIds()) {
            for (SecurityGroupRule rule : filter(securityGroup.getRules(),
                    and(ruleGroup(groupId), ruleProtocol(ipPermission.getIpProtocol()),
                            ruleStartPort(ipPermission.getFromPort()),
                            ruleEndPort(ipPermission.getToPort())))) {
               sgApi.get().deleteRule(rule.getId());
            }

         }
      }

      return getSecurityGroupById(ZoneAndId.fromZoneAndId(zone, id).slashEncode());
   }

   @Override
   public SecurityGroup removeIpPermission(IpProtocol protocol, int startPort, int endPort,
                                           Multimap<String, String> tenantIdGroupNamePairs,
                                           Iterable<String> ipRanges,
                                           Iterable<String> groupIds, SecurityGroup group) {
      String zone = group.getLocation().getId();
      String id = group.getId();

      Optional<? extends SecurityGroupApi> sgApi = api.getSecurityGroupExtensionForZone(zone);

      if (!sgApi.isPresent()) {
         return null;
      }

      org.jclouds.openstack.nova.v2_0.domain.SecurityGroup securityGroup = sgApi.get().get(id);

      if (Iterables.size(ipRanges) > 0) {
         for (String cidr : ipRanges) {
            for (SecurityGroupRule rule : filter(securityGroup.getRules(),
                    and(ruleCidr(cidr),
                            ruleProtocol(protocol),
                            ruleStartPort(startPort),
                            ruleEndPort(endPort)))) {
               sgApi.get().deleteRule(rule.getId());
            }
         }
      }

      if (Iterables.size(groupIds) > 0) {
         for (String groupId : groupIds) {
            for (SecurityGroupRule rule : filter(securityGroup.getRules(),
                    and(ruleGroup(groupId),
                            ruleProtocol(protocol),
                            ruleStartPort(startPort),
                            ruleEndPort(endPort)))) {
               sgApi.get().deleteRule(rule.getId());
            }
         }
      }

      return getSecurityGroupById(ZoneAndId.fromZoneAndId(zone, id).slashEncode());
   }

   @Override
   public boolean supportsTenantIdGroupNamePairs() {
      return false;
   }

   @Override
   public boolean supportsGroupIds() {
      return true;
   }

   @Override
   public boolean supportsPortRangesForGroups() {
      return false;
   }

   protected Iterable<? extends SecurityGroupInZone> pollSecurityGroups() {
      Iterable<? extends Set<? extends SecurityGroupInZone>> groups
              = transform(zoneIds.get(), allSecurityGroupsInZone());

      return concat(groups);
   }


   protected Iterable<? extends SecurityGroupInZone> pollSecurityGroupsByZone(String zone) {
      return allSecurityGroupsInZone().apply(zone);
   }

   protected Function<String, Set<? extends SecurityGroupInZone>> allSecurityGroupsInZone() {
      return new Function<String, Set<? extends SecurityGroupInZone>>() {

         @Override
         public Set<? extends SecurityGroupInZone> apply(final String from) {
            Optional<? extends SecurityGroupApi> sgApi = api.getSecurityGroupExtensionForZone(from);

            if (!sgApi.isPresent()) {
               return ImmutableSet.of();
            }


            return sgApi.get().list().transform(groupToGroupInZone(from)).toSet();
         }

      };
   }

   protected Function<org.jclouds.openstack.nova.v2_0.domain.SecurityGroup, SecurityGroupInZone> groupToGroupInZone(final String zone) {
      return new Function<org.jclouds.openstack.nova.v2_0.domain.SecurityGroup, SecurityGroupInZone>() {
         @Override
         public SecurityGroupInZone apply(org.jclouds.openstack.nova.v2_0.domain.SecurityGroup group) {
            return new SecurityGroupInZone(group, zone);
         }
      };
   }
}
