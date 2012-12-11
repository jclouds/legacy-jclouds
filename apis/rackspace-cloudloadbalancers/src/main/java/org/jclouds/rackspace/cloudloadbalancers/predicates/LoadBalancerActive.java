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
package org.jclouds.rackspace.cloudloadbalancers.predicates;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.annotation.Resource;
import javax.inject.Singleton;

import org.jclouds.logging.Logger;
import org.jclouds.rackspace.cloudloadbalancers.CloudLoadBalancersApi;
import org.jclouds.rackspace.cloudloadbalancers.domain.LoadBalancer;
import org.jclouds.rackspace.cloudloadbalancers.domain.LoadBalancer.Status;

import com.google.common.base.Predicate;
import com.google.inject.Inject;

/**
 * 
 * Tests to see if a loadBalancer is running
 * 
 * @author Adrian Cole
 */
@Singleton
public class LoadBalancerActive implements Predicate<LoadBalancer> {

   private final CloudLoadBalancersApi client;

   @Resource
   protected Logger logger = Logger.NULL;

   @Inject
   public LoadBalancerActive(CloudLoadBalancersApi client) {
      this.client = client;
   }

   public boolean apply(LoadBalancer loadBalancer) {
      logger.trace("looking for status on loadBalancer %s", checkNotNull(loadBalancer, "loadBalancer"));
      loadBalancer = refresh(loadBalancer);
      if (loadBalancer == null)
         return false;
      logger.trace("%s: looking for loadBalancer status %s: currently: %s", loadBalancer.getId(), Status.ACTIVE,
               loadBalancer.getStatus());
      if (loadBalancer.getStatus() == Status.ERROR)
         throw new IllegalStateException("loadBalancer in error status: " + loadBalancer);
      return loadBalancer.getStatus() == Status.ACTIVE;
   }

   private LoadBalancer refresh(LoadBalancer loadBalancer) {
      return client.getLoadBalancerApiForZone(loadBalancer.getRegion()).get(loadBalancer.getId());
   }
}
