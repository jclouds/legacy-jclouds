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

import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.loadbalancer.domain.LoadBalancerMetadata;
import org.jclouds.loadbalancer.strategy.ListLoadBalancersStrategy;
import org.jclouds.location.Zone;
import org.jclouds.rackspace.cloudloadbalancers.v1.CloudLoadBalancersApi;
import org.jclouds.rackspace.cloudloadbalancers.v1.domain.LoadBalancer;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class CloudLoadBalancersListLoadBalancersStrategy implements ListLoadBalancersStrategy {

   private final CloudLoadBalancersApi aclient;
   private final Function<LoadBalancer, LoadBalancerMetadata> converter;
   private final Supplier<Set<String>> zones;

   @Inject
   protected CloudLoadBalancersListLoadBalancersStrategy(CloudLoadBalancersApi aclient,
         Function<LoadBalancer, LoadBalancerMetadata> converter, @Zone Supplier<Set<String>> zones) {
      this.aclient = checkNotNull(aclient, "aclient");
      this.zones = checkNotNull(zones, "zones");
      this.converter = checkNotNull(converter, "converter");
   }

   @Override
   public Iterable<? extends LoadBalancerMetadata> listLoadBalancers() {
      Builder<LoadBalancerMetadata> loadBalancers = ImmutableSet.<LoadBalancerMetadata> builder();
      for (String zone : zones.get()) { // TODO: parallel
         loadBalancers.addAll(aclient.getLoadBalancerApiForZone(zone).list().concat().transform(converter));
      }
      return loadBalancers.build();
   }
}
