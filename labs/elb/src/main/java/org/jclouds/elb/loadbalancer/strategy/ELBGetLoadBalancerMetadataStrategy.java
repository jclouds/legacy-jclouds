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
import static com.google.common.collect.Iterables.getOnlyElement;
import static org.jclouds.aws.util.AWSUtils.parseHandle;

import java.util.NoSuchElementException;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.elb.ELBClient;
import org.jclouds.elb.domain.CrappyLoadBalancer;
import org.jclouds.loadbalancer.domain.LoadBalancerMetadata;
import org.jclouds.loadbalancer.strategy.GetLoadBalancerMetadataStrategy;

import com.google.common.base.Function;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class ELBGetLoadBalancerMetadataStrategy implements GetLoadBalancerMetadataStrategy {

   private final ELBClient client;
   private final Function<CrappyLoadBalancer, LoadBalancerMetadata> converter;

   @Inject
   protected ELBGetLoadBalancerMetadataStrategy(ELBClient client, Function<CrappyLoadBalancer, LoadBalancerMetadata> converter) {
      this.client = checkNotNull(client, "client");
      this.converter = checkNotNull(converter, "converter");
   }

   @Override
   public LoadBalancerMetadata getLoadBalancer(String id) {
      String[] parts = parseHandle(id);
      String region = parts[0];
      String name = parts[1];
      try {
         return converter.apply(getOnlyElement(client.describeLoadBalancersInRegion(region, name)));
      } catch (NoSuchElementException e) {
         return null;
      }
   }

}