/**
 *
 * Copyright (C) 2011 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */
package org.jclouds.ec2.compute.functions;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.ec2.EC2Client;
import org.jclouds.ec2.compute.domain.RegionNameAndIngressRules;
import org.jclouds.ec2.domain.IpProtocol;
import org.jclouds.ec2.domain.UserIdGroupPair;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.logging.Logger;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class CreateSecurityGroupIfNeeded implements Function<RegionNameAndIngressRules, String> {
   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;
   protected final EC2Client ec2Client;

   @Inject
   public CreateSecurityGroupIfNeeded(EC2Client ec2Client) {
      this.ec2Client = ec2Client;
   }

   @Override
   public String apply(RegionNameAndIngressRules from) {
      createSecurityGroupInRegion(from.getRegion(), from.getName(), from.getPorts());
      return from.getName();
   }

   private void createSecurityGroupInRegion(String region, String name, int... ports) {
      checkNotNull(region, "region");
      checkNotNull(name, "name");
      logger.debug(">> creating securityGroup region(%s) name(%s)", region, name);
      try {
         ec2Client.getSecurityGroupServices().createSecurityGroupInRegion(region, name, name);
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

   private void createIngressRuleForTCPPort(String region, String name, int port) {
      logger.debug(">> authorizing securityGroup region(%s) name(%s) port(%s)", region, name, port);
      ec2Client.getSecurityGroupServices().authorizeSecurityGroupIngressInRegion(region, name, IpProtocol.TCP, port,
               port, "0.0.0.0/0");
      logger.debug("<< authorized securityGroup(%s)", name);
   }

   private void authorizeGroupToItself(String region, String name) {
      logger.debug(">> authorizing securityGroup region(%s) name(%s) permission to itself", region, name);
      String myOwnerId = Iterables.get(ec2Client.getSecurityGroupServices().describeSecurityGroupsInRegion(region), 0)
               .getOwnerId();
      ec2Client.getSecurityGroupServices().authorizeSecurityGroupIngressInRegion(region, name,
               new UserIdGroupPair(myOwnerId, name));
      logger.debug("<< authorized securityGroup(%s)", name);
   }

}
