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

import java.net.InetAddress;
import java.util.Map;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.aws.ec2.services.ElasticLoadBalancerClient;
import org.jclouds.aws.ec2.util.EC2Utils;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.compute.strategy.DestroyLoadBalancerStrategy;
import org.jclouds.logging.Logger;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class EC2DestroyLoadBalancerStrategy implements DestroyLoadBalancerStrategy {
   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   private final ElasticLoadBalancerClient elbClient;

   @Inject
   protected EC2DestroyLoadBalancerStrategy(ElasticLoadBalancerClient elbClient) {
      this.elbClient = checkNotNull(elbClient, "elbClient");
   }

   @Override
   public boolean execute(InetAddress loadBalancer) {
      Map<String, String> tuple = EC2Utils.getLoadBalancerNameAndRegionFromDnsName(loadBalancer
               .getCanonicalHostName());
      // Only one load balancer per DNS name is expected
      for (String key : tuple.keySet()) {
         elbClient.deleteLoadBalancerInRegion(key, tuple.get(key));
      }
      return true;
   }
}