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

package org.jclouds.elb;

import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nullable;

import org.jclouds.concurrent.Timeout;
import org.jclouds.elb.domain.LoadBalancer;

import com.google.common.annotations.Beta;

/**
 * Provides access to EC2 Elastic Load Balancer via their REST API.
 * <p/>
 * 
 * @author Lili Nader
 */
@Beta
// see ELBAsyncClient
@Timeout(duration = 30, timeUnit = TimeUnit.SECONDS)
public interface ELBClient {

   /**
    * Creates a load balancer
    * 
    * @param name
    *           Name of the load balancer
    * @param loadBalancerPort
    *           Port for the load balancer to listen on
    * @param instancePort
    *           Port to forward the request to
    * @param availabilityZones
    *           load balancer availability zones
    * @return dns the DNS name for the load balancer
    * @see <a href="http://docs.amazonwebservices.com/ElasticLoadBalancing/latest/DeveloperGuide/"
    */
   @Beta
   // see ELBAsyncClient
   String createLoadBalancerInRegion(@Nullable String region, String name, String protocol, int loadBalancerPort,
            int instancePort, String... availabilityZones);

   /**
    * Delete load balancer
    * 
    * @param name
    *           Name of the load balancer
    * @return
    * @see <a
    *      href="http://docs.amazonwebservices.com/ElasticLoadBalancing/2009-05-15/DeveloperGuide/"
    */
   void deleteLoadBalancerInRegion(@Nullable String region, String name);

   /**
    * Register instances with an existing load balancer
    * 
    * @param name
    *           Load Balancer name
    * @param instanceIds
    *           Set of instance Ids to register with load balancer
    * @return instanceIds registered with load balancer
    * 
    * @see <a
    *      href="http://docs.amazonwebservices.com/ElasticLoadBalancing/2009-05-15/DeveloperGuide/"
    */
   Set<String> registerInstancesWithLoadBalancerInRegion(@Nullable String region, String name, String... instanceIds);

   /**
    * Deregister instances with an existing load balancer
    * 
    * @param name
    *           Load Balancer name
    * @param instanceIds
    *           Set of instance Ids to deregister with load balancer
    * @return
    * 
    * @see <a
    *      href="http://docs.amazonwebservices.com/ElasticLoadBalancing/2009-05-15/DeveloperGuide/"
    */
   void deregisterInstancesWithLoadBalancerInRegion(@Nullable String region, String name, String... instanceIds);

   /**
    * Returns a set of elastic load balancers
    * 
    * @param region
    * @param loadbalancerNames
    *           names associated with the LoadBalancers at creation time.
    * @return
    */
   Set<? extends LoadBalancer> describeLoadBalancersInRegion(@Nullable String region, String... loadbalancerNames);

}
