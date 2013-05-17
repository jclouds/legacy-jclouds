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

import org.jclouds.loadbalancer.domain.LoadBalancerMetadata;
import org.jclouds.loadbalancer.reference.LoadBalancerConstants;
import org.jclouds.loadbalancer.strategy.DestroyLoadBalancerStrategy;
import org.jclouds.loadbalancer.strategy.GetLoadBalancerMetadataStrategy;
import org.jclouds.logging.Logger;
import org.jclouds.rackspace.cloudloadbalancers.v1.CloudLoadBalancersApi;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class CloudLoadBalancersDestroyLoadBalancerStrategy implements DestroyLoadBalancerStrategy {
   @Resource
   @Named(LoadBalancerConstants.LOADBALANCER_LOGGER)
   protected Logger logger = Logger.NULL;

   private final CloudLoadBalancersApi client;
   private final GetLoadBalancerMetadataStrategy getLoadBalancer;

   @Inject
   protected CloudLoadBalancersDestroyLoadBalancerStrategy(CloudLoadBalancersApi client,
            GetLoadBalancerMetadataStrategy getLoadBalancer) {
      this.client = checkNotNull(client, "client");
      this.getLoadBalancer = checkNotNull(getLoadBalancer, "getLoadBalancer");
   }

   @Override
   public LoadBalancerMetadata destroyLoadBalancer(String id) {
      String[] parts = checkNotNull(id, "id").split("/");
      String region = parts[0];
      int lbId = Integer.parseInt(parts[1]);
      client.getLoadBalancerApiForZone(region).delete(lbId);
      return getLoadBalancer.getLoadBalancer(id);
   }
}
