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

package org.jclouds.aws.elb.loadbalancer.strategy;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.aws.ec2.util.EC2Utils;
import org.jclouds.aws.ec2.util.EC2Utils.GetRegionFromLocation;
import org.jclouds.aws.elb.ELBClient;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.domain.Location;
import org.jclouds.loadbalancer.domain.LoadBalancerMetadata;
import org.jclouds.loadbalancer.domain.LoadBalancerType;
import org.jclouds.loadbalancer.domain.internal.LoadBalancerMetadataImpl;
import org.jclouds.loadbalancer.reference.LoadBalancerConstants;
import org.jclouds.loadbalancer.strategy.LoadBalanceNodesStrategy;
import org.jclouds.logging.Logger;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
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
   protected final ELBClient elbClient;
   protected final GetRegionFromLocation getRegionFromLocation;

   @Inject
   protected ELBLoadBalanceNodesStrategy(ELBClient elbClient, GetRegionFromLocation getRegionFromLocation) {
      this.elbClient = elbClient;
      this.getRegionFromLocation = getRegionFromLocation;
   }

   @Override
   public LoadBalancerMetadata execute(Location location, String name, String protocol, int loadBalancerPort,
         int instancePort, Iterable<? extends NodeMetadata> nodes) {
      String region = getRegionFromLocation.apply(location);
      String dnsName = new String();

      dnsName = elbClient.createLoadBalancerInRegion(region, name, protocol, loadBalancerPort, instancePort,
            EC2Utils.getAvailabilityZonesForRegion(region));

      List<String> instanceIds = Lists.newArrayList(Iterables.transform(nodes, new Function<NodeMetadata, String>() {

         @Override
         public String apply(NodeMetadata from) {
            return from.getProviderId();
         }
      }));

      String[] instanceIdArray = instanceIds.toArray(new String[] {});

      Set<String> registeredInstanceIds = elbClient.registerInstancesWithLoadBalancerInRegion(region, name,
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
            elbClient.deregisterInstancesWithLoadBalancerInRegion(region, name, instanceIdArray);
      }

      return new LoadBalancerMetadataImpl(LoadBalancerType.LB, dnsName, name, dnsName, location, null,
            ImmutableMap.<String, String> of(), ImmutableSet.of(dnsName));
   }
}