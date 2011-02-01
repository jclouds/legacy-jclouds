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
import static org.jclouds.aws.util.AWSUtils.parseHandle;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.elb.ELBClient;
import org.jclouds.loadbalancer.domain.LoadBalancerMetadata;
import org.jclouds.loadbalancer.reference.LoadBalancerConstants;
import org.jclouds.loadbalancer.strategy.DestroyLoadBalancerStrategy;
import org.jclouds.loadbalancer.strategy.GetLoadBalancerMetadataStrategy;
import org.jclouds.logging.Logger;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class ELBDestroyLoadBalancerStrategy implements DestroyLoadBalancerStrategy {
   @Resource
   @Named(LoadBalancerConstants.LOADBALANCER_LOGGER)
   protected Logger logger = Logger.NULL;

   private final ELBClient elbClient;
   private final GetLoadBalancerMetadataStrategy getLoadBalancer;

   @Inject
   protected ELBDestroyLoadBalancerStrategy(ELBClient elbClient, GetLoadBalancerMetadataStrategy getLoadBalancer) {
      this.elbClient = checkNotNull(elbClient, "elbClient");
      this.getLoadBalancer = checkNotNull(getLoadBalancer, "getLoadBalancer");
   }

   @Override
   public LoadBalancerMetadata destroyLoadBalancer(String id) {
      String[] parts = parseHandle(id);
      String region = parts[0];
      String instanceId = parts[1];
      elbClient.deleteLoadBalancerInRegion(region, instanceId);
      return getLoadBalancer.getLoadBalancer(id);
   }
}
