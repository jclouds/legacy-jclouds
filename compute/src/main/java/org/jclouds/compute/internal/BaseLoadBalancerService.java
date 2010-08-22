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

package org.jclouds.compute.internal;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.LoadBalancerService;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.predicates.NodePredicates;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.compute.strategy.DestroyLoadBalancerStrategy;
import org.jclouds.compute.strategy.LoadBalanceNodesStrategy;
import org.jclouds.domain.Location;
import org.jclouds.http.handlers.BackoffLimitedRetryHandler;
import org.jclouds.logging.Logger;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.inject.Inject;

/**
 * 
 * @author Lili Nadar
 * @author Adrian Cole
 */
@Singleton
public class BaseLoadBalancerService implements LoadBalancerService {

   @Inject(optional = true)
   @Named("jclouds.lb.max_retries")
   @VisibleForTesting
   int dnsRetries = 5;

   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   protected final ComputeServiceContext context;
   protected final LoadBalanceNodesStrategy loadBalancerStrategy;
   protected final DestroyLoadBalancerStrategy destroyLoadBalancerStrategy;
   // protected final ListLoadBalancersStrategy listLoadBalancersStrategy;
   protected final BackoffLimitedRetryHandler backoffLimitedRetryHandler;

   @Inject
   protected BaseLoadBalancerService(ComputeServiceContext context, LoadBalanceNodesStrategy loadBalancerStrategy,
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
   public ComputeServiceContext getContext() {
      return context;
   }

   @Override
   public Set<String> loadBalanceNodesMatching(Predicate<NodeMetadata> filter, String loadBalancerName,
            String protocol, int loadBalancerPort, int instancePort) {
      checkNotNull(loadBalancerName, "loadBalancerName");
      checkNotNull(protocol, "protocol");
      checkArgument(protocol.toUpperCase().equals("HTTP") || protocol.toUpperCase().equals("TCP"),
               "Acceptable values for protocol are HTTP or TCP");

      Map<Location, Set<String>> locationMap = Maps.newHashMap();
      for (NodeMetadata node : Iterables.filter(context.getComputeService().listNodesDetailsMatching(
               NodePredicates.all()), Predicates.and(filter, Predicates.not(NodePredicates.TERMINATED)))) {
         Set<String> ids = locationMap.get(node.getLocation());
         if (ids == null)
            ids = Sets.newHashSet();
         ids.add(node.getProviderId());
         locationMap.put(node.getLocation(), ids);
      }
      Set<String> dnsNames = Sets.newHashSet();
      for (Location location : locationMap.keySet()) {
         logger.debug(">> creating load balancer (%s)", loadBalancerName);
         String dnsName = loadBalancerStrategy.execute(location, loadBalancerName, protocol, loadBalancerPort,
                  instancePort, locationMap.get(location));
         dnsNames.add(dnsName);
         logger.debug("<< created load balancer (%s) DNS (%s)", loadBalancerName, dnsName);
      }
      return dnsNames;
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