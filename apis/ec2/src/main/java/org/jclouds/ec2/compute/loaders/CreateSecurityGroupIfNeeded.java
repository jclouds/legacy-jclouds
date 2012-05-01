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
package org.jclouds.ec2.compute.loaders;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.ec2.EC2Client;
import org.jclouds.ec2.compute.domain.RegionAndName;
import org.jclouds.ec2.compute.domain.RegionNameAndIngressRules;
import org.jclouds.ec2.domain.IpProtocol;
import org.jclouds.ec2.domain.UserIdGroupPair;
import org.jclouds.ec2.services.SecurityGroupClient;
import org.jclouds.logging.Logger;

import com.google.common.base.Predicate;
import com.google.common.cache.CacheLoader;
import com.google.common.collect.Iterables;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class CreateSecurityGroupIfNeeded extends CacheLoader<RegionAndName, String> {
   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;
   protected final SecurityGroupClient securityClient;
   protected final Predicate<RegionAndName> securityGroupEventualConsistencyDelay;

   @Inject
   public CreateSecurityGroupIfNeeded(EC2Client ec2Client,
         @Named("SECURITY") Predicate<RegionAndName> securityGroupEventualConsistencyDelay) {
      this(checkNotNull(ec2Client, "ec2Client").getSecurityGroupServices(), securityGroupEventualConsistencyDelay);
   }

   public CreateSecurityGroupIfNeeded(SecurityGroupClient securityClient,
         @Named("SECURITY") Predicate<RegionAndName> securityGroupEventualConsistencyDelay) {
      this.securityClient = checkNotNull(securityClient, "securityClient");
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
         securityClient.createSecurityGroupInRegion(region, name, name);
         boolean created = securityGroupEventualConsistencyDelay.apply(new RegionAndName(region, name));
         if (!created)
            throw new RuntimeException(String.format("security group %s/%s is not available after creating", region,
                  name));
         logger.debug("<< created securityGroup(%s)", name);
         for (int port : ports) {
            createIngressRuleForTCPPort(region, name, port);
         }
         if (ports.length > 0) {
            authorizeGroupToItself(region, name);
         }
      } catch (IllegalStateException e) {
         logger.debug("<< reused securityGroup(%s)", name);
      }
   }

   protected void createIngressRuleForTCPPort(String region, String name, int port) {
      logger.debug(">> authorizing securityGroup region(%s) name(%s) port(%s)", region, name, port);
      securityClient.authorizeSecurityGroupIngressInRegion(region, name, IpProtocol.TCP, port, port, "0.0.0.0/0");
      logger.debug("<< authorized securityGroup(%s)", name);
   }

   protected void authorizeGroupToItself(String region, String name) {
      logger.debug(">> authorizing securityGroup region(%s) name(%s) permission to itself", region, name);
      String myOwnerId = Iterables.get(securityClient.describeSecurityGroupsInRegion(region, name), 0).getOwnerId();
      securityClient.authorizeSecurityGroupIngressInRegion(region, name, new UserIdGroupPair(myOwnerId, name));
      logger.debug("<< authorized securityGroup(%s)", name);
   }

}
