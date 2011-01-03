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

package org.jclouds.elb.loadbalancer.config;

import org.jclouds.elb.loadbalancer.strategy.ELBDestroyLoadBalancerStrategy;
import org.jclouds.elb.loadbalancer.strategy.ELBGetLoadBalancerMetadataStrategy;
import org.jclouds.elb.loadbalancer.strategy.ELBListLoadBalancersStrategy;
import org.jclouds.elb.loadbalancer.strategy.ELBLoadBalanceNodesStrategy;
import org.jclouds.loadbalancer.config.BindLoadBalancerStrategiesByClass;
import org.jclouds.loadbalancer.strategy.DestroyLoadBalancerStrategy;
import org.jclouds.loadbalancer.strategy.GetLoadBalancerMetadataStrategy;
import org.jclouds.loadbalancer.strategy.ListLoadBalancersStrategy;
import org.jclouds.loadbalancer.strategy.LoadBalanceNodesStrategy;

/**
 * @author Adrian Cole
 */
public class ELBBindLoadBalancerStrategiesByClass extends BindLoadBalancerStrategiesByClass {

   @Override
   protected Class<? extends LoadBalanceNodesStrategy> defineLoadBalanceNodesStrategy() {
      return ELBLoadBalanceNodesStrategy.class;
   }

   @Override
   protected Class<? extends DestroyLoadBalancerStrategy> defineDestroyLoadBalancerStrategy() {
      return ELBDestroyLoadBalancerStrategy.class;
   }

   @Override
   protected Class<? extends GetLoadBalancerMetadataStrategy> defineGetLoadBalancerMetadataStrategy() {
      return ELBGetLoadBalancerMetadataStrategy.class;
   }

   @Override
   protected Class<? extends ListLoadBalancersStrategy> defineListLoadBalancersStrategy() {
      return ELBListLoadBalancersStrategy.class;
   }
}