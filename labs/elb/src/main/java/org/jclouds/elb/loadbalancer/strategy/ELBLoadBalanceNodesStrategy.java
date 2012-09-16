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
package org.jclouds.elb.loadbalancer.strategy;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Predicates.in;
import static com.google.common.base.Predicates.not;
import static com.google.common.collect.Iterables.transform;
import static com.google.common.collect.Sets.filter;
import static org.jclouds.aws.util.AWSUtils.getRegionFromLocationOrNull;

import java.util.Set;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.domain.Location;
import org.jclouds.elb.ELBApi;
import org.jclouds.elb.domain.Listener;
import org.jclouds.elb.domain.Protocol;
import org.jclouds.elb.domain.regionscoped.LoadBalancerInRegion;
import org.jclouds.loadbalancer.domain.LoadBalancerMetadata;
import org.jclouds.loadbalancer.reference.LoadBalancerConstants;
import org.jclouds.loadbalancer.strategy.LoadBalanceNodesStrategy;
import org.jclouds.logging.Logger;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class ELBLoadBalanceNodesStrategy implements LoadBalanceNodesStrategy {
   @Resource
   @Named(LoadBalancerConstants.LOADBALANCER_LOGGER)
   protected Logger logger = Logger.NULL;
   protected final ELBApi api;
   protected final Function<LoadBalancerInRegion, LoadBalancerMetadata> converter;

   @Inject
   protected ELBLoadBalanceNodesStrategy(ELBApi api,
            Function<LoadBalancerInRegion, LoadBalancerMetadata> converter) {
      this.api = checkNotNull(api, "api");
      this.converter = checkNotNull(converter, "converter");
   }

   @Override
   public LoadBalancerMetadata createLoadBalancerInLocation(Location location, String name, String protocol,
            int loadBalancerPort, int instancePort, Iterable<? extends NodeMetadata> nodes) {
      checkNotNull(location, "location");
      String region = getRegionFromLocationOrNull(location);

      Set<String> zonesDesired = ImmutableSet.copyOf(transform(nodes, new Function<NodeMetadata, String>() {

         @Override
         public String apply(NodeMetadata from) {
            return from.getLocation().getId();
         }
      }));

      logger.debug(">> creating loadBalancer(%s) in zones(%s)", name, zonesDesired);
      try {
         String dnsName = api.getLoadBalancerApiForRegion(region).createListeningInAvailabilityZones(
                  name,
                  ImmutableSet.of(Listener.builder().port(loadBalancerPort).instancePort(instancePort)
                           .protocol(Protocol.valueOf(protocol)).build()), zonesDesired);
         logger.debug("<< created loadBalancer(%s) dnsName(%s)", name, dnsName);
      } catch (IllegalStateException e) {
         logger.debug("<< converging zones(%s) in loadBalancer(%s)", zonesDesired, name);
         Set<String> currentZones = api.getLoadBalancerApi().get(name).getAvailabilityZones();
         Set<String> zonesToAdd = Sets.difference(zonesDesired, currentZones);
         if (zonesToAdd.size() > 0)
            currentZones = api.getAvailabilityZoneApi().addAvailabilityZonesToLoadBalancer(zonesToAdd, name);
         Set<String> zonesToRemove = Sets.difference(currentZones, zonesDesired);
         if (zonesToRemove.size() > 0)
            api.getAvailabilityZoneApi().removeAvailabilityZonesFromLoadBalancer(zonesToRemove, name);
      }

      Set<String> instanceIds = ImmutableSet.copyOf(transform(nodes, new Function<NodeMetadata, String>() {

         @Override
         public String apply(NodeMetadata from) {
            return from.getProviderId();
         }
      }));

      logger.debug(">> converging loadBalancer(%s) to instances(%s)", name, instanceIds);
      Set<String> registeredInstanceIds = api.getInstanceApiForRegion(region).registerInstancesWithLoadBalancer(
               instanceIds, name);

      Set<String> instancesToRemove = filter(registeredInstanceIds, not(in(instanceIds)));
      if (instancesToRemove.size() > 0) {
         logger.debug(">> deregistering instances(%s) from loadBalancer(%s)", instancesToRemove, name);
         api.getInstanceApiForRegion(region).deregisterInstancesFromLoadBalancer(instancesToRemove, name);
      }
      logger.debug("<< converged loadBalancer(%s) ", name);

      return converter.apply(new LoadBalancerInRegion(api.getLoadBalancerApiForRegion(region).get(name), region));
   }
}
