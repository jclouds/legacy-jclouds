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
package org.jclouds.elb.loadbalancer.strategy;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.concat;
import static com.google.common.collect.Iterables.transform;
import static com.google.common.util.concurrent.Futures.transform;
import static org.jclouds.concurrent.FutureIterables.transformParallel;

import java.util.Set;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.Constants;
import org.jclouds.collect.PagedIterable;
import org.jclouds.elb.ELBAsyncApi;
import org.jclouds.elb.domain.LoadBalancer;
import org.jclouds.elb.domain.regionscoped.LoadBalancerInRegion;
import org.jclouds.loadbalancer.domain.LoadBalancerMetadata;
import org.jclouds.loadbalancer.reference.LoadBalancerConstants;
import org.jclouds.loadbalancer.strategy.ListLoadBalancersStrategy;
import org.jclouds.location.Region;
import org.jclouds.logging.Logger;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class ELBListLoadBalancersStrategy implements ListLoadBalancersStrategy {
   @Resource
   @Named(LoadBalancerConstants.LOADBALANCER_LOGGER)
   protected Logger logger = Logger.NULL;

   private final ELBAsyncApi aapi;
   private final Function<LoadBalancerInRegion, LoadBalancerMetadata> converter;
   private final ListeningExecutorService userExecutor;
   private final Supplier<Set<String>> regions;

   @Inject
   protected ELBListLoadBalancersStrategy(ELBAsyncApi aapi,
            Function<LoadBalancerInRegion, LoadBalancerMetadata> converter,
            @Named(Constants.PROPERTY_USER_THREADS) ListeningExecutorService userExecutor, @Region Supplier<Set<String>> regions) {
      this.aapi = checkNotNull(aapi, "aapi");
      this.regions = checkNotNull(regions, "regions");
      this.converter = checkNotNull(converter, "converter");
      this.userExecutor = checkNotNull(userExecutor, "userExecutor");
   }

   @Override
   public Iterable<LoadBalancerMetadata> listLoadBalancers() {

      Iterable<LoadBalancerInRegion> loadBalancers = concat(transformParallel(regions.get(),
               new Function<String, ListenableFuture<? extends Iterable<LoadBalancerInRegion>>>() {

                  @Override
                  public ListenableFuture<? extends Iterable<LoadBalancerInRegion>> apply(final String from) {
                     // TODO: ELB.listLoadBalancers
                     return transform(aapi.getLoadBalancerApiForRegion(from).list(),
                              new Function<PagedIterable<LoadBalancer>, Iterable<LoadBalancerInRegion>>() {

                                 @Override
                                 public Iterable<LoadBalancerInRegion> apply(PagedIterable<LoadBalancer> input) {
                                    return input.concat()
                                                .transform(new Function<LoadBalancer, LoadBalancerInRegion>() {
                                                    @Override
                                                    public LoadBalancerInRegion apply(LoadBalancer lb) {
                                                       return new LoadBalancerInRegion(lb, from);
                                                    }
                                                 });
                                 }

                              }, userExecutor);
                  }

               }, userExecutor, null, logger, "loadbalancers"));
      return transform(loadBalancers, converter);
   }
}
