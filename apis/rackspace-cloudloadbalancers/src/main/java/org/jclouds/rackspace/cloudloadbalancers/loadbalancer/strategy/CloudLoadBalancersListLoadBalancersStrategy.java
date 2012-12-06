/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jclouds.rackspace.cloudloadbalancers.loadbalancer.strategy;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.concat;
import static com.google.common.collect.Iterables.transform;
import static org.jclouds.concurrent.FutureIterables.transformParallel;

import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.Constants;
import org.jclouds.loadbalancer.domain.LoadBalancerMetadata;
import org.jclouds.loadbalancer.reference.LoadBalancerConstants;
import org.jclouds.loadbalancer.strategy.ListLoadBalancersStrategy;
import org.jclouds.location.Zone;
import org.jclouds.logging.Logger;
import org.jclouds.rackspace.cloudloadbalancers.CloudLoadBalancersAsyncApi;
import org.jclouds.rackspace.cloudloadbalancers.domain.LoadBalancer;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.util.concurrent.ListenableFuture;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class CloudLoadBalancersListLoadBalancersStrategy implements ListLoadBalancersStrategy {
   @Resource
   @Named(LoadBalancerConstants.LOADBALANCER_LOGGER)
   protected Logger logger = Logger.NULL;

   private final CloudLoadBalancersAsyncApi aclient;
   private final Function<LoadBalancer, LoadBalancerMetadata> converter;
   private final ExecutorService executor;
   private final Supplier<Set<String>> zones;

   @Inject
   protected CloudLoadBalancersListLoadBalancersStrategy(CloudLoadBalancersAsyncApi aclient,
            Function<LoadBalancer, LoadBalancerMetadata> converter,
            @Named(Constants.PROPERTY_USER_THREADS) ExecutorService executor, @Zone Supplier<Set<String>> zones) {
      this.aclient = checkNotNull(aclient, "aclient");
      this.zones = checkNotNull(zones, "zones");
      this.converter = checkNotNull(converter, "converter");
      this.executor = checkNotNull(executor, "executor");
   }

   @Override
   public Iterable<? extends LoadBalancerMetadata> listLoadBalancers() {
      return transform(concat(transformParallel(zones.get(), new Function<String, Future<? extends Set<LoadBalancer>>>() {

         @Override
         public ListenableFuture<Set<LoadBalancer>> apply(String from) {
            return aclient.getLoadBalancerClient(from).listLoadBalancers();
         }

      }, executor, null, logger, "loadbalancers")), converter);
   }
}
