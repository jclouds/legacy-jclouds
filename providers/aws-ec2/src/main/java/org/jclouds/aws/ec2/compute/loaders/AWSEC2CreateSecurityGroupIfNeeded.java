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
package org.jclouds.aws.ec2.compute.loaders;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.compute.util.ComputeServiceUtils.getPortRangesFromList;

import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.aws.ec2.AWSEC2Api;
import org.jclouds.aws.ec2.features.AWSSecurityGroupApi;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.ec2.compute.domain.RegionAndName;
import org.jclouds.ec2.compute.domain.RegionNameAndIngressRules;
import org.jclouds.logging.Logger;
import org.jclouds.net.domain.IpPermission;
import org.jclouds.net.domain.IpProtocol;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.cache.CacheLoader;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

/**
 * 
 * @author Adrian Cole
 * @author Andrew Bayer
 */
@Singleton
public class AWSEC2CreateSecurityGroupIfNeeded extends CacheLoader<RegionAndName, String> {
   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;
   protected final AWSSecurityGroupApi securityApi;
   protected final Predicate<RegionAndName> securityGroupEventualConsistencyDelay;
   protected final Function<String, String> groupNameToId;
   @Inject
   public AWSEC2CreateSecurityGroupIfNeeded(AWSEC2Api ec2Api,
                                            @Named("SECGROUP_NAME_TO_ID") Function<String, String> groupNameToId,
                                            @Named("SECURITY") Predicate<RegionAndName> securityGroupEventualConsistencyDelay) {
      this(checkNotNull(ec2Api, "ec2Api").getSecurityGroupApi().get(), groupNameToId, securityGroupEventualConsistencyDelay);
   }

   public AWSEC2CreateSecurityGroupIfNeeded(AWSSecurityGroupApi securityApi,
                                            @Named("SECGROUP_NAME_TO_ID") Function<String, String> groupNameToId,
                                            @Named("SECURITY") Predicate<RegionAndName> securityGroupEventualConsistencyDelay) {
      this.securityApi = checkNotNull(securityApi, "securityApi");
      this.groupNameToId = checkNotNull(groupNameToId, "groupNameToId");
      this.securityGroupEventualConsistencyDelay = checkNotNull(securityGroupEventualConsistencyDelay,
            "securityGroupEventualConsistencyDelay");
   }

   @Override
   public String load(RegionAndName from) {
      RegionNameAndIngressRules realFrom = RegionNameAndIngressRules.class.cast(from);
      createSecurityGroupInRegion(from.getRegion(), from.getName(), realFrom.getPorts());
      return from.getName();
   }

   private void createSecurityGroupInRegion(String region, String name, int... ports) {
      checkNotNull(region, "region");
      checkNotNull(name, "name");
      logger.debug(">> creating securityGroup region(%s) name(%s)", region, name);

      try {
         securityApi.createSecurityGroupInRegion(region, name, name);
         boolean created = securityGroupEventualConsistencyDelay.apply(new RegionAndName(region, name));
         if (!created)
            throw new RuntimeException(String.format("security group %s/%s is not available after creating", region,
                  name));
         logger.debug("<< created securityGroup(%s)", name);

         ImmutableSet.Builder<IpPermission> permissions = ImmutableSet.builder();
         String id;
         if (name.startsWith("sg-")) {
            id = name;
         } else {
            id = groupNameToId.apply(new RegionAndName(region, name).slashEncode());
         }

         if (ports.length > 0) {
            for (Map.Entry<Integer, Integer> range : getPortRangesFromList(ports).entrySet()) {
               permissions.add(IpPermission.builder()
                               .fromPort(range.getKey())
                               .toPort(range.getValue())
                               .ipProtocol(IpProtocol.TCP)
                               .cidrBlock("0.0.0.0/0")
                               .build());
            }

            String myOwnerId = Iterables.get(securityApi.describeSecurityGroupsInRegion(region, name), 0).getOwnerId();
            permissions.add(IpPermission.builder()
                            .fromPort(0)
                            .toPort(65535)
                            .ipProtocol(IpProtocol.TCP)
                            .tenantIdGroupNamePair(myOwnerId, id)
                            .build());
            permissions.add(IpPermission.builder()
                            .fromPort(0)
                            .toPort(65535)
                            .ipProtocol(IpProtocol.UDP)
                            .tenantIdGroupNamePair(myOwnerId, id)
                            .build());
         }

         Set<IpPermission> perms = permissions.build();

         if (perms.size() > 0) {
            logger.debug(">> authorizing securityGroup region(%s) name(%s) IpPermissions(%s)", region, name, perms);
            securityApi.authorizeSecurityGroupIngressInRegion(region, id, perms);
            logger.debug("<< authorized securityGroup(%s)", name);
         }            

      } catch (IllegalStateException e) {
         logger.debug("<< reused securityGroup(%s)", name);
      }
   }

}
