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
package org.jclouds.loadbalancer.internal;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Sets.newLinkedHashSet;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.jclouds.util.Predicates2.retry;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import javax.annotation.Resource;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.collect.Memoized;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.domain.Location;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.loadbalancer.LoadBalancerService;
import org.jclouds.loadbalancer.LoadBalancerServiceContext;
import org.jclouds.loadbalancer.domain.LoadBalancerMetadata;
import org.jclouds.loadbalancer.reference.LoadBalancerConstants;
import org.jclouds.loadbalancer.strategy.DestroyLoadBalancerStrategy;
import org.jclouds.loadbalancer.strategy.GetLoadBalancerMetadataStrategy;
import org.jclouds.loadbalancer.strategy.ListLoadBalancersStrategy;
import org.jclouds.loadbalancer.strategy.LoadBalanceNodesStrategy;
import org.jclouds.logging.Logger;

import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.util.concurrent.Atomics;
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

   protected final Supplier<Location> defaultLocationSupplier;
   protected final LoadBalancerServiceContext context;
   protected final LoadBalanceNodesStrategy loadBalancerStrategy;
   protected final GetLoadBalancerMetadataStrategy getLoadBalancerMetadataStrategy;
   protected final DestroyLoadBalancerStrategy destroyLoadBalancerStrategy;
   protected final ListLoadBalancersStrategy listLoadBalancersStrategy;
   protected final Supplier<Set<? extends Location>> locations;

   @Inject
   protected BaseLoadBalancerService(Supplier<Location> defaultLocationSupplier, LoadBalancerServiceContext context,
         LoadBalanceNodesStrategy loadBalancerStrategy,
         GetLoadBalancerMetadataStrategy getLoadBalancerMetadataStrategy,
         DestroyLoadBalancerStrategy destroyLoadBalancerStrategy, ListLoadBalancersStrategy listLoadBalancersStrategy,
         @Memoized Supplier<Set<? extends Location>> locations) {
      this.defaultLocationSupplier = checkNotNull(defaultLocationSupplier, "defaultLocationSupplier");
      this.context = checkNotNull(context, "context");
      this.loadBalancerStrategy = checkNotNull(loadBalancerStrategy, "loadBalancerStrategy");
      this.getLoadBalancerMetadataStrategy = checkNotNull(getLoadBalancerMetadataStrategy,
            "getLoadBalancerMetadataStrategy");
      this.destroyLoadBalancerStrategy = checkNotNull(destroyLoadBalancerStrategy, "destroyLoadBalancerStrategy");
      this.listLoadBalancersStrategy = checkNotNull(listLoadBalancersStrategy, "listLoadBalancersStrategy");
      this.locations = checkNotNull(locations, "locations");
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Set<? extends Location> listAssignableLocations() {
      return locations.get();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public LoadBalancerServiceContext getContext() {
      return context;
   }

   @Override
   public LoadBalancerMetadata createLoadBalancerInLocation(@Nullable Location location, String loadBalancerName,
         String protocol, int loadBalancerPort, int instancePort, Iterable<? extends NodeMetadata> nodes) {
      if (location == null)
         location = defaultLocationSupplier.get();

      checkNotNull(loadBalancerName, "loadBalancerName");
      checkNotNull(protocol, "protocol");
      checkArgument(protocol.toUpperCase().equals("HTTP") || protocol.toUpperCase().equals("TCP"),
            "Acceptable values for protocol are HTTP or TCP");

      logger.debug(">> creating load balancer (%s)", loadBalancerName);
      LoadBalancerMetadata lb = loadBalancerStrategy.createLoadBalancerInLocation(location, loadBalancerName, protocol,
            loadBalancerPort, instancePort, nodes);
      logger.debug("<< created load balancer (%s)", loadBalancerName, lb);
      return lb;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public LoadBalancerMetadata getLoadBalancerMetadata(String id) {
      checkNotNull(id, "id");
      return getLoadBalancerMetadataStrategy.getLoadBalancer(id);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void destroyLoadBalancer(final String id) {
      checkNotNull(id, "id");
      logger.debug(">> destroying load balancer(%s)", id);
      final AtomicReference<LoadBalancerMetadata> loadBalancer = Atomics.newReference();
      Predicate<String> tester = retry(new Predicate<String>() {
         public boolean apply(String input) {
            try {
               LoadBalancerMetadata md = destroyLoadBalancerStrategy.destroyLoadBalancer(id);
               if (md != null)
                  loadBalancer.set(md);
               return true;
            } catch (IllegalStateException e) {
               logger.warn("<< illegal state destroying load balancer(%s)", id);
               return false;
            }
         }
      }, 3000, 1000, MILLISECONDS);// TODO make timeouts class like ComputeServiceconstants
      boolean successful = tester.apply(id) && loadBalancer.get() == null; // TODO add load
                                                                           // balancerTerminated
      // retryable predicate
      // (load balancer.get() == null ||
      // load balancerTerminated.apply(load balancer.get()));
      logger.debug("<< destroyed load balancer(%s) success(%s)", id, successful);
   }

   public Set<? extends LoadBalancerMetadata> listLoadBalancers() {
      logger.debug(">> listing load balancers");
      LinkedHashSet<LoadBalancerMetadata> set = newLinkedHashSet(listLoadBalancersStrategy.listLoadBalancers());
      logger.debug("<< list(%d)", set.size());
      return set;
   }

}
