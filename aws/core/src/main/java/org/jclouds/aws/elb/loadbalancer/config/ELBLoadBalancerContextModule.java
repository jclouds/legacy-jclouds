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

package org.jclouds.aws.elb.loadbalancer.config;

import org.jclouds.aws.elb.loadbalancer.strategy.ELBDestroyLoadBalancerStrategy;
import org.jclouds.aws.elb.loadbalancer.strategy.ELBLoadBalanceNodesStrategy;
import org.jclouds.http.RequiresHttp;
import org.jclouds.loadbalancer.strategy.DestroyLoadBalancerStrategy;
import org.jclouds.loadbalancer.strategy.LoadBalanceNodesStrategy;
import org.jclouds.rest.ConfiguresRestClient;

import com.google.inject.AbstractModule;

/**
 * Configures the ELB connection.
 * 
 * @author Adrian Cole
 */
@RequiresHttp
@ConfiguresRestClient
public class ELBLoadBalancerContextModule extends AbstractModule {


   @Override
   protected void configure() {
      bind(LoadBalanceNodesStrategy.class).to(ELBLoadBalanceNodesStrategy.class);
      bind(DestroyLoadBalancerStrategy.class).to(ELBDestroyLoadBalancerStrategy.class);
   }
}