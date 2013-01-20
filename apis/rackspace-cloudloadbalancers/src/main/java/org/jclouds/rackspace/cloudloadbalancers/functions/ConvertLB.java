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
package org.jclouds.rackspace.cloudloadbalancers.functions;

import javax.annotation.Resource;
import javax.inject.Inject;

import org.jclouds.logging.Logger;
import org.jclouds.rackspace.cloudloadbalancers.domain.AccessRuleWithId;
import org.jclouds.rackspace.cloudloadbalancers.domain.LoadBalancer;
import org.jclouds.rackspace.cloudloadbalancers.domain.LoadBalancer.Builder;
import org.jclouds.rackspace.cloudloadbalancers.domain.Metadata;
import org.jclouds.rackspace.cloudloadbalancers.domain.VirtualIPWithId;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.inject.assistedinject.Assisted;

/**
 * @author Adrian Cole
 */
public class ConvertLB implements Function<LB, LoadBalancer> {

   public static interface Factory {
      ConvertLB createForRegion(String region);
   }

   @Resource
   protected Logger logger = Logger.NULL;

   private final String region;

   @Inject
   ConvertLB(@Assisted String region) {
      this.region = region.toUpperCase();
   }

   @Override
   public LoadBalancer apply(LB lb) {
      try {
         Builder builder = LoadBalancer.builder().id(lb.id).region(region).status(lb.status).name(lb.getName())
               .protocol(lb.getProtocol()).port(lb.getPort()).nodeCount(lb.nodeCount).nodes(lb.getNodes())
               .timeout(lb.getTimeout()).algorithm(lb.getAlgorithm()).halfClosed(lb.isHalfClosed())
               .sessionPersistenceType(lb.getSessionPersistenceType()).connectionLogging(lb.isConnectionLogging())
               .connectionThrottle(lb.getConnectionThrottle()).healthMonitor(lb.getHealthMonitor());

         if (lb.cluster.size() == 1)
            builder.clusterName(Iterables.get(lb.cluster.values(), 0));
         if (lb.created.size() == 1)
            builder.created(Iterables.get(lb.created.values(), 0));
         if (lb.updated.size() == 1)
            builder.updated(Iterables.get(lb.updated.values(), 0));
         if (lb.contentCaching.size() == 1)
            builder.contentCaching(Iterables.get(lb.contentCaching.values(), 0));
         if (lb.sslTermination != null)
            builder.sslTermination(lb.sslTermination);
         if (lb.sourceAddresses != null)
            builder.sourceAddresses(lb.sourceAddresses);
         if (lb.accessList == null)
            builder.accessRules(ImmutableSet.<AccessRuleWithId> of());
         else
            builder.accessRules(lb.accessList);
         if (lb.virtualIps == null)
            builder.virtualIPs(ImmutableSet.<VirtualIPWithId> of());
         else
            builder.virtualIPs(lb.virtualIps);
         if (lb.metadata == null)
            builder.metadata(new Metadata());
         else
            builder.metadata(ParseMetadata.transformCLBMetadataToMetadata(lb.metadata));

         return builder.build();
      }
      catch (NullPointerException e) {
         logger.warn(e, "nullpointer found parsing %s", lb);
         throw e;
      }
   }

}
