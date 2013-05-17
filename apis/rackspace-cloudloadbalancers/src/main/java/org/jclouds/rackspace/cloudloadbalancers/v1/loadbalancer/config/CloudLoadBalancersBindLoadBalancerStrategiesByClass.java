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
package org.jclouds.rackspace.cloudloadbalancers.v1.loadbalancer.config;

import org.jclouds.loadbalancer.config.BindLoadBalancerStrategiesByClass;
import org.jclouds.loadbalancer.strategy.DestroyLoadBalancerStrategy;
import org.jclouds.loadbalancer.strategy.GetLoadBalancerMetadataStrategy;
import org.jclouds.loadbalancer.strategy.ListLoadBalancersStrategy;
import org.jclouds.loadbalancer.strategy.LoadBalanceNodesStrategy;
import org.jclouds.rackspace.cloudloadbalancers.v1.loadbalancer.strategy.CloudLoadBalancersDestroyLoadBalancerStrategy;
import org.jclouds.rackspace.cloudloadbalancers.v1.loadbalancer.strategy.CloudLoadBalancersGetLoadBalancerMetadataStrategy;
import org.jclouds.rackspace.cloudloadbalancers.v1.loadbalancer.strategy.CloudLoadBalancersListLoadBalancersStrategy;
import org.jclouds.rackspace.cloudloadbalancers.v1.loadbalancer.strategy.CloudLoadBalancersLoadBalanceNodesStrategy;

/**
 * @author Adrian Cole
 */
public class CloudLoadBalancersBindLoadBalancerStrategiesByClass extends BindLoadBalancerStrategiesByClass {

   @Override
   protected Class<? extends LoadBalanceNodesStrategy> defineLoadBalanceNodesStrategy() {
      return CloudLoadBalancersLoadBalanceNodesStrategy.class;
   }

   @Override
   protected Class<? extends DestroyLoadBalancerStrategy> defineDestroyLoadBalancerStrategy() {
      return CloudLoadBalancersDestroyLoadBalancerStrategy.class;
   }

   @Override
   protected Class<? extends GetLoadBalancerMetadataStrategy> defineGetLoadBalancerMetadataStrategy() {
      return CloudLoadBalancersGetLoadBalancerMetadataStrategy.class;
   }

   @Override
   protected Class<? extends ListLoadBalancersStrategy> defineListLoadBalancersStrategy() {
      return CloudLoadBalancersListLoadBalancersStrategy.class;
   }
}
