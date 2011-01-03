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
import static com.google.common.collect.Iterables.concat;
import static com.google.common.collect.Iterables.transform;
import static org.jclouds.concurrent.FutureIterables.transformParallel;

import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import javax.annotation.Nullable;
import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.Constants;
import org.jclouds.elb.ELBAsyncClient;
import org.jclouds.elb.ELBClient;
import org.jclouds.elb.domain.LoadBalancer;
import org.jclouds.loadbalancer.domain.LoadBalancerMetadata;
import org.jclouds.loadbalancer.reference.LoadBalancerConstants;
import org.jclouds.loadbalancer.strategy.ListLoadBalancersStrategy;
import org.jclouds.location.Region;
import org.jclouds.logging.Logger;

import com.google.common.base.Function;
import com.google.common.util.concurrent.ListenableFuture;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class ELBListLoadBalancersStrategy implements ListLoadBalancersStrategy {
   @Resource
   @Named(LoadBalancerConstants.LOADBALANCER_LOGGER)
   protected Logger logger = Logger.NULL;

   private final ELBClient client;
   private final ELBAsyncClient aclient;
   private final Function<LoadBalancer, LoadBalancerMetadata> converter;
   private final ExecutorService executor;
   private final Set<String> regions;

   @Inject
   protected ELBListLoadBalancersStrategy(ELBClient client, ELBAsyncClient aclient,
         Function<LoadBalancer, LoadBalancerMetadata> converter,
         @Named(Constants.PROPERTY_USER_THREADS) ExecutorService executor, @Nullable @Region Set<String> regions) {
      this.client = checkNotNull(client, "client");
      this.aclient = checkNotNull(aclient, "aclient");
      this.regions = checkNotNull(regions, "regions");
      this.converter = checkNotNull(converter, "converter");
      this.executor = checkNotNull(executor, "executor");
   }

   @Override
   public Iterable<? extends LoadBalancerMetadata> listLoadBalancers() {
      Iterable<? extends LoadBalancer> loadBalancers;
      if (regions != null)
         loadBalancers = concat(transformParallel(regions, new Function<String, Future<Set<? extends LoadBalancer>>>() {

            @Override
            public ListenableFuture<Set<? extends LoadBalancer>> apply(String from) {
               return aclient.describeLoadBalancersInRegion(from);
            }

         }, executor, null, logger, "loadbalancers"));
      else
         loadBalancers = client.describeLoadBalancersInRegion(null);
      return transform(loadBalancers, converter);
   }
}
