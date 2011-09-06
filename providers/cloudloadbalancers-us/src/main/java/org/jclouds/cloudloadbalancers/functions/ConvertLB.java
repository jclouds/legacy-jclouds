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
package org.jclouds.cloudloadbalancers.functions;

import org.jclouds.cloudloadbalancers.domain.LoadBalancer;
import org.jclouds.cloudloadbalancers.domain.LoadBalancer.Builder;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;

/**
 * @author Adrian Cole
 */
public class ConvertLB implements Function<LB, LoadBalancer> {

   private final String region;

   ConvertLB(String region) {
      this.region = region.toUpperCase();
   }

   @Override
   public LoadBalancer apply(LB lb) {
      Builder builder = LoadBalancer.builder().region(region).name(lb.getName()).port(lb.getPort()).protocol(
               lb.getProtocol()).algorithm(lb.getAlgorithm()).nodes(lb.getNodes()).id(lb.id).status(lb.status)
               .virtualIPs(lb.virtualIps);
      if (lb.cluster.size() == 1)
         builder.clusterName(Iterables.get(lb.cluster.values(), 0));
      if (lb.sessionPersistence.size() == 1)
         builder.sessionPersistenceType(Iterables.get(lb.sessionPersistence.values(), 0));
      if (lb.created.size() == 1)
         builder.created(Iterables.get(lb.created.values(), 0));
      if (lb.updated.size() == 1)
         builder.updated(Iterables.get(lb.updated.values(), 0));
      if (lb.connectionLogging.size() == 1)
         builder.connectionLoggingEnabled(Iterables.get(lb.connectionLogging.values(), 0));
      return builder.build();
   }

}