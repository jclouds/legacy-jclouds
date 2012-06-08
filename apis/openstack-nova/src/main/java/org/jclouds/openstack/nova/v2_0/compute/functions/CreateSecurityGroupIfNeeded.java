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
package org.jclouds.openstack.nova.v2_0.compute.functions;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.find;
import static org.jclouds.openstack.nova.v2_0.predicates.SecurityGroupPredicates.nameEquals;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.logging.Logger;
import org.jclouds.openstack.nova.v2_0.NovaClient;
import org.jclouds.openstack.nova.v2_0.domain.Ingress;
import org.jclouds.openstack.nova.v2_0.domain.IpProtocol;
import org.jclouds.openstack.nova.v2_0.domain.SecurityGroup;
import org.jclouds.openstack.nova.v2_0.domain.zonescoped.SecurityGroupInZone;
import org.jclouds.openstack.nova.v2_0.domain.zonescoped.ZoneSecurityGroupNameAndPorts;
import org.jclouds.openstack.nova.v2_0.extensions.SecurityGroupClient;

import com.google.common.base.Function;
import com.google.common.base.Optional;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class CreateSecurityGroupIfNeeded implements Function<ZoneSecurityGroupNameAndPorts, SecurityGroupInZone> {
   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;
   protected final NovaClient novaClient;

   @Inject
   public CreateSecurityGroupIfNeeded(NovaClient novaClient) {
      this.novaClient = checkNotNull(novaClient, "novaClient");
   }

   @Override
   public SecurityGroupInZone apply(ZoneSecurityGroupNameAndPorts zoneSecurityGroupNameAndPorts) {
      checkNotNull(zoneSecurityGroupNameAndPorts, "zoneSecurityGroupNameAndPorts");

      String zoneId = zoneSecurityGroupNameAndPorts.getZone();
      Optional<SecurityGroupClient> client = novaClient.getSecurityGroupExtensionForZone(zoneId);
      checkArgument(client.isPresent(), "Security groups are required, but the extension is not availablein zone %s!", zoneId);
      logger.debug(">> creating securityGroup %s", zoneSecurityGroupNameAndPorts);
      try {

         SecurityGroup securityGroup = client.get().createSecurityGroupWithNameAndDescription(
                  zoneSecurityGroupNameAndPorts.getName(), zoneSecurityGroupNameAndPorts.getName());

         logger.debug("<< created securityGroup(%s)", securityGroup);
         for (int port : zoneSecurityGroupNameAndPorts.getPorts()) {
            authorizeGroupToItselfAndAllIPsToTCPPort(client.get(), securityGroup, port);
         }
         return new SecurityGroupInZone(client.get().getSecurityGroup(securityGroup.getId()), zoneId);
      } catch (IllegalStateException e) {
         logger.trace("<< trying to find securityGroup(%s): %s", zoneSecurityGroupNameAndPorts, e.getMessage());
         SecurityGroup group = find(client.get().listSecurityGroups(), nameEquals(zoneSecurityGroupNameAndPorts
                  .getName()));
         logger.debug("<< reused securityGroup(%s)", group.getId());
         return new SecurityGroupInZone(group, zoneId);
      }
   }

   private void authorizeGroupToItselfAndAllIPsToTCPPort(SecurityGroupClient securityGroupClient,
            SecurityGroup securityGroup, int port) {
      // NOTE that permission to itself isn't supported on trystack!
      logger.debug(">> authorizing securityGroup(%s) permission to 0.0.0.0/0 on port %d", securityGroup, port);
      securityGroupClient.createSecurityGroupRuleAllowingCidrBlock(securityGroup.getId(), Ingress.builder().ipProtocol(
               IpProtocol.TCP).fromPort(port).toPort(port).build(), "0.0.0.0/0");
      logger.debug("<< authorized securityGroup(%s) permission to 0.0.0.0/0 on port %d", securityGroup, port);

   }
}
