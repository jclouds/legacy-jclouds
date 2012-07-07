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
import org.jclouds.elb.ELBClient;
import org.jclouds.elb.domain.Listener;
import org.jclouds.elb.domain.Protocol;
import org.jclouds.elb.domain.regionscoped.LoadBalancerInRegion;
import org.jclouds.loadbalancer.domain.LoadBalancerMetadata;
import org.jclouds.loadbalancer.reference.LoadBalancerConstants;
import org.jclouds.loadbalancer.strategy.LoadBalanceNodesStrategy;
import org.jclouds.logging.Logger;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableSet;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class ELBLoadBalanceNodesStrategy implements LoadBalanceNodesStrategy {
   @Resource
   @Named(LoadBalancerConstants.LOADBALANCER_LOGGER)
   protected Logger logger = Logger.NULL;
   protected final ELBClient client;
   protected final Function<LoadBalancerInRegion, LoadBalancerMetadata> converter;

   @Inject
   protected ELBLoadBalanceNodesStrategy(ELBClient client,
            Function<LoadBalancerInRegion, LoadBalancerMetadata> converter) {
      this.client = checkNotNull(client, "client");
      this.converter = checkNotNull(converter, "converter");
   }

   @Override
   public LoadBalancerMetadata createLoadBalancerInLocation(Location location, String name, String protocol,
            int loadBalancerPort, int instancePort, Iterable<? extends NodeMetadata> nodes) {
      checkNotNull(location, "location");
      String region = getRegionFromLocationOrNull(location);

      Set<String> availabilityZones = ImmutableSet.copyOf(transform(nodes, new Function<NodeMetadata, String>() {

         @Override
         public String apply(NodeMetadata from) {
            return from.getLocation().getId();
         }
      }));

      logger.debug(">> creating loadBalancer(%s)", name);
      try {
         String dnsName = client.getLoadBalancerClientForRegion(region).createLoadBalancerListeningInAvailabilityZones(
                  name,
                  ImmutableSet.of(Listener.builder().port(loadBalancerPort).instancePort(instancePort)
                           .protocol(Protocol.valueOf(protocol)).build()), availabilityZones);
         logger.debug("<< created loadBalancer(%s) dnsName(%s)", name, dnsName);
      } catch (IllegalStateException e) {
         logger.debug("<< reusing loadBalancer(%s)", name);
         // TODO: converge availability zones
      }

      Set<String> instanceIds = ImmutableSet.copyOf(transform(nodes, new Function<NodeMetadata, String>() {

         @Override
         public String apply(NodeMetadata from) {
            return from.getProviderId();
         }
      }));

      logger.debug(">> converging loadBalancer(%s) to instances(%s)", name, instanceIds);
      Set<String> registeredInstanceIds = client.getInstanceClientForRegion(region).registerInstancesWithLoadBalancer(
               instanceIds, name);

      Set<String> instancesToRemove = filter(registeredInstanceIds, not(in(instanceIds)));
      if (instancesToRemove.size() > 0) {
         logger.debug(">> deregistering instances(%s) from loadBalancer(%s)", instancesToRemove, name);
         client.getInstanceClientForRegion(region).deregisterInstancesFromLoadBalancer(instancesToRemove, name);
      }
      logger.debug("<< converged loadBalancer(%s) ", name);

      return converter.apply(new LoadBalancerInRegion(client.getLoadBalancerClientForRegion(region).get(name), region));
   }
}
