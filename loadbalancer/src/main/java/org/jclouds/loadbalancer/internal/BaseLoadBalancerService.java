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

package org.jclouds.loadbalancer.internal;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Set;

import javax.annotation.Resource;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.domain.Location;
import org.jclouds.http.handlers.BackoffLimitedRetryHandler;
import org.jclouds.loadbalancer.LoadBalancerService;
import org.jclouds.loadbalancer.LoadBalancerServiceContext;
import org.jclouds.loadbalancer.domain.LoadBalancerMetadata;
import org.jclouds.loadbalancer.reference.LoadBalancerConstants;
import org.jclouds.loadbalancer.strategy.DestroyLoadBalancerStrategy;
import org.jclouds.loadbalancer.strategy.LoadBalanceNodesStrategy;
import org.jclouds.logging.Logger;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Inject;

/**
 * 
 * @author Lili Nadar
 * @author Adrian Cole
 */
@Singleton
public class BaseLoadBalancerService implements LoadBalancerService {

   @Resource
   @Named(LoadBalancerConstants.LOADBALANCER_LOGGER)
   protected Logger logger = Logger.NULL;

   protected final LoadBalancerServiceContext context;
   protected final LoadBalanceNodesStrategy loadBalancerStrategy;
   protected final DestroyLoadBalancerStrategy destroyLoadBalancerStrategy;
   // protected final ListLoadBalancersStrategy listLoadBalancersStrategy;
   protected final BackoffLimitedRetryHandler backoffLimitedRetryHandler;

   @Inject
   protected BaseLoadBalancerService(LoadBalancerServiceContext context, LoadBalanceNodesStrategy loadBalancerStrategy,
         DestroyLoadBalancerStrategy destroyLoadBalancerStrategy,
         // ListLoadBalancersStrategy listLoadBalancersStrategy,
         BackoffLimitedRetryHandler backoffLimitedRetryHandler) {
      this.context = checkNotNull(context, "context");
      this.loadBalancerStrategy = checkNotNull(loadBalancerStrategy, "loadBalancerStrategy");
      this.destroyLoadBalancerStrategy = checkNotNull(destroyLoadBalancerStrategy, "destroyLoadBalancerStrategy");
      // this.listLoadBalancersStrategy = checkNotNull(listLoadBalancersStrategy,
      // "listLoadBalancersStrategy");
      this.backoffLimitedRetryHandler = checkNotNull(backoffLimitedRetryHandler, "backoffLimitedRetryHandler");
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public LoadBalancerServiceContext getContext() {
      return context;
   }

   @Override
   public LoadBalancerMetadata createLoadBalancerInLocation(Location location, String loadBalancerName,
         String protocol, int loadBalancerPort, int instancePort, Iterable<? extends NodeMetadata> nodes) {
      checkNotNull(loadBalancerName, "loadBalancerName");
      checkNotNull(protocol, "protocol");
      checkArgument(protocol.toUpperCase().equals("HTTP") || protocol.toUpperCase().equals("TCP"),
            "Acceptable values for protocol are HTTP or TCP");

      logger.debug(">> creating load balancer (%s)", loadBalancerName);
      LoadBalancerMetadata lb = loadBalancerStrategy.execute(location, loadBalancerName, protocol, loadBalancerPort,
            instancePort, nodes);
      logger.debug("<< created load balancer (%s)", loadBalancerName, lb);

      return lb;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void destroyLoadBalancer(String loadBalancer) {
      checkNotNull(loadBalancer, "loadBalancer");
      logger.debug(">> destroying load balancer(%s)", loadBalancer);
      boolean successful = destroyLoadBalancerStrategy.execute(loadBalancer);
      logger.debug("<< destroyed load balancer(%s) success(%s)", loadBalancer, successful);
   }

   public Set<String> listLoadBalancers() {
      return ImmutableSet.of();
      // TODO
      // Set<String> loadBalancerSet = listLoadBalancersStrategy.execute();
      // return loadBalancerSet;
   }

}