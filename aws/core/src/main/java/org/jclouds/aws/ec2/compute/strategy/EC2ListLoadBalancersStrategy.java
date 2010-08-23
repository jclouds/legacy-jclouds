/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
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

package org.jclouds.aws.ec2.compute.strategy;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.aws.elb.ELBClient;
import org.jclouds.aws.elb.domain.LoadBalancer;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.compute.strategy.ListLoadBalancersStrategy;
import org.jclouds.logging.Logger;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class EC2ListLoadBalancersStrategy implements ListLoadBalancersStrategy {
   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   private final ELBClient elbClient;

   @Inject
   protected EC2ListLoadBalancersStrategy(ELBClient elbClient) {
      this.elbClient = checkNotNull(elbClient, "elbClient");
   }

   @Override
   public Set<String> execute() {

      Set<LoadBalancer> loadBalancers = elbClient.describeLoadBalancersInRegion(null);
      Set<String> loadBalancerDnsNames = new HashSet<String>();
      for(LoadBalancer loadBalancer: loadBalancers)
      {
          loadBalancerDnsNames.add(loadBalancer.getDnsName());
      }
      return loadBalancerDnsNames;
   }
}