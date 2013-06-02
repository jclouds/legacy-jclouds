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
package org.jclouds.cloudstack.functions;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.find;
import static org.jclouds.cloudstack.predicates.SecurityGroupPredicates.nameEquals;
import static org.jclouds.cloudstack.predicates.SecurityGroupPredicates.portInRangeForCidr;
import static org.jclouds.cloudstack.predicates.ZonePredicates.supportsSecurityGroups;

import java.util.Set;
import java.util.concurrent.ExecutionException;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.logging.Logger;
import org.jclouds.cloudstack.CloudStackClient;
import org.jclouds.cloudstack.domain.IngressRule;
import org.jclouds.cloudstack.domain.SecurityGroup;
import org.jclouds.cloudstack.domain.Zone;
import org.jclouds.cloudstack.domain.ZoneSecurityGroupNamePortsCidrs;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.base.Throwables;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableSet;

/**
 * 
 * @author Adrian Cole
 * @author Andrew Bayer
 */
@Singleton
public class CreateSecurityGroupIfNeeded implements Function<ZoneSecurityGroupNamePortsCidrs, SecurityGroup> {
   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;
   protected final CloudStackClient client;
   protected final Supplier<LoadingCache<String, Zone>> zoneIdToZone;
   protected final Predicate<String> jobComplete;

   @Inject
   public CreateSecurityGroupIfNeeded(CloudStackClient client,
                                      Predicate<String> jobComplete,
                                      Supplier<LoadingCache<String, Zone>> zoneIdToZone) {
      this.client = checkNotNull(client, "client");
      this.jobComplete = checkNotNull(jobComplete, "jobComplete");
      this.zoneIdToZone = zoneIdToZone;
   }

   @Override
   public SecurityGroup apply(ZoneSecurityGroupNamePortsCidrs input) {
      checkNotNull(input, "input");

      String zoneId = input.getZone();
      Zone zone = zoneIdToZone.get().getUnchecked(zoneId);

      checkArgument(supportsSecurityGroups().apply(zone),
                    "Security groups are required, but the zone %s does not support security groups", zoneId);
      logger.debug(">> creating securityGroup %s", input);
      try {

         SecurityGroup securityGroup = client.getSecurityGroupClient().createSecurityGroup(input.getName());

         logger.debug("<< created securityGroup(%s)", securityGroup);
         ImmutableSet<String> cidrs;
         if (input.getCidrs().size() > 0) {
            cidrs = ImmutableSet.copyOf(input.getCidrs());
         } else {
            cidrs = ImmutableSet.of("0.0.0.0/0");
         }
         for (int port : input.getPorts()) {
            authorizeGroupToItselfAndToTCPPortAndCidr(client, securityGroup, port, cidrs);
         }
         return securityGroup;
      } catch (IllegalStateException e) {
         logger.trace("<< trying to find securityGroup(%s): %s", input, e.getMessage());
         SecurityGroup group = client.getSecurityGroupClient().getSecurityGroupByName(input.getName());
         logger.debug("<< reused securityGroup(%s)", group.getId());
         return group;
      }
   }

   private void authorizeGroupToItselfAndToTCPPortAndCidr(CloudStackClient client,
                                                          SecurityGroup securityGroup,
                                                          int port,
                                                          Set<String> cidrs) {
      for (String cidr : cidrs) {
         logger.debug(">> authorizing securityGroup(%s) permission to %s on port %d", securityGroup, cidr, port);
         if (!portInRangeForCidr(port, cidr).apply(securityGroup)) {
            jobComplete.apply(client.getSecurityGroupClient().authorizeIngressPortsToCIDRs(securityGroup.getId(),
                                                                                           "TCP",
                                                                                           port,
                                                                                           port,
                                                                                           ImmutableSet.of(cidr)));
            logger.debug("<< authorized securityGroup(%s) permission to %s on port %d", securityGroup, cidr, port);
         }
      }
   }
}
