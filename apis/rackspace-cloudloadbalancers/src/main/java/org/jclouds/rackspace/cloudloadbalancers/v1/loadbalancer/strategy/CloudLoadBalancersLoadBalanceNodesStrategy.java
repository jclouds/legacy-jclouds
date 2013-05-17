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
package org.jclouds.rackspace.cloudloadbalancers.v1.loadbalancer.strategy;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.domain.Location;
import org.jclouds.loadbalancer.domain.LoadBalancerMetadata;
import org.jclouds.loadbalancer.reference.LoadBalancerConstants;
import org.jclouds.loadbalancer.strategy.GetLoadBalancerMetadataStrategy;
import org.jclouds.loadbalancer.strategy.LoadBalanceNodesStrategy;
import org.jclouds.logging.Logger;
import org.jclouds.rackspace.cloudloadbalancers.v1.CloudLoadBalancersApi;
import org.jclouds.rackspace.cloudloadbalancers.v1.domain.LoadBalancer;
import org.jclouds.rackspace.cloudloadbalancers.v1.domain.CreateLoadBalancer;
import org.jclouds.rackspace.cloudloadbalancers.v1.domain.AddNode;
import org.jclouds.rackspace.cloudloadbalancers.v1.domain.VirtualIP.Type;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class CloudLoadBalancersLoadBalanceNodesStrategy implements LoadBalanceNodesStrategy {
   @Resource
   @Named(LoadBalancerConstants.LOADBALANCER_LOGGER)
   protected Logger logger = Logger.NULL;
   protected final CloudLoadBalancersApi client;
   protected final GetLoadBalancerMetadataStrategy getLB;

   @Inject
   protected CloudLoadBalancersLoadBalanceNodesStrategy(CloudLoadBalancersApi client,
            GetLoadBalancerMetadataStrategy getLB) {
      this.client = checkNotNull(client, "client");
      this.getLB = checkNotNull(getLB, "getLB");
   }

   @Override
   public LoadBalancerMetadata createLoadBalancerInLocation(Location location, String name, String protocol,
            int loadBalancerPort, final int instancePort, Iterable<? extends NodeMetadata> nodes) {
      String region = checkNotNull(location, "location").getId();

      // TODO need to query and update the LB per current design.
      LoadBalancer lb = client.getLoadBalancerApiForZone(region).create(
               CreateLoadBalancer.builder().name(name).protocol(protocol.toUpperCase()).port(loadBalancerPort)
                        .virtualIPType(Type.PUBLIC).nodes(
                                 Iterables.transform(nodes, new Function<NodeMetadata, AddNode>() {

                                    @Override
                                    public AddNode apply(NodeMetadata arg0) {
                                       return AddNode.builder().address(
                                                Iterables.get(arg0.getPrivateAddresses(), 0)).port(instancePort)
                                                .build();

                                    }

                                 })).build());
      return getLB.getLoadBalancer(region + "/" + lb.getId());
   }
}
