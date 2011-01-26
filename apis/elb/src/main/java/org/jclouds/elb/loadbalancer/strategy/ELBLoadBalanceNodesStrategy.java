/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
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

package org.jclouds.elb.loadbalancer.strategy;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.aws.util.AWSUtils.getRegionFromLocationOrNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.domain.Location;
import org.jclouds.elb.ELBClient;
import org.jclouds.elb.domain.LoadBalancer;
import org.jclouds.loadbalancer.domain.LoadBalancerMetadata;
import org.jclouds.loadbalancer.reference.LoadBalancerConstants;
import org.jclouds.loadbalancer.strategy.LoadBalanceNodesStrategy;
import org.jclouds.logging.Logger;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

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
   protected final Function<LoadBalancer, LoadBalancerMetadata> converter;

   @Inject
   protected ELBLoadBalanceNodesStrategy(ELBClient client, Function<LoadBalancer, LoadBalancerMetadata> converter) {
      this.client = checkNotNull(client, "client");
      this.converter = checkNotNull(converter, "converter");
   }

   @Override
   public LoadBalancerMetadata createLoadBalancerInLocation(Location location, String name, String protocol,
         int loadBalancerPort, int instancePort, Iterable<? extends NodeMetadata> nodes) {
      checkNotNull(location, "location");
      String region = getRegionFromLocationOrNull(location);

      List<String> availabilityZones = Lists.newArrayList(Iterables.transform(nodes,
            new Function<NodeMetadata, String>() {

               @Override
               public String apply(NodeMetadata from) {
                  return from.getLocation().getId();
               }
            }));

      client.createLoadBalancerInRegion(region, name, protocol, loadBalancerPort, instancePort,
            availabilityZones.toArray(new String[] {}));

      List<String> instanceIds = Lists.newArrayList(Iterables.transform(nodes, new Function<NodeMetadata, String>() {

         @Override
         public String apply(NodeMetadata from) {
            return from.getProviderId();
         }
      }));

      String[] instanceIdArray = instanceIds.toArray(new String[] {});

      Set<String> registeredInstanceIds = client.registerInstancesWithLoadBalancerInRegion(region, name,
            instanceIdArray);

      // deregister instances
      boolean changed = registeredInstanceIds.removeAll(instanceIds);
      if (changed) {
         List<String> list = new ArrayList<String>(registeredInstanceIds);
         instanceIdArray = new String[list.size()];
         for (int i = 0; i < list.size(); i++) {
            instanceIdArray[i] = list.get(i);
         }
         if (instanceIdArray.length > 0)
            client.deregisterInstancesWithLoadBalancerInRegion(region, name, instanceIdArray);
      }
      return converter.apply(Iterables.getOnlyElement(client.describeLoadBalancersInRegion(region, name)));
   }
}
