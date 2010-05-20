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
package org.jclouds.aws.ec2.services;

import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nullable;

import org.jclouds.aws.ec2.domain.ElasticLoadBalancer;
import org.jclouds.concurrent.Timeout;

/**
 * Provides access to EC2 Elastic Load Balancer via their REST API.
 * <p/>
 * 
 * @author Lili Nader
 */
@Timeout(duration = 30, timeUnit = TimeUnit.SECONDS)
public interface ElasticLoadBalancerClient {
    
    /**
     * Creates a load balancer
     * 
     * @param name  Name of the load balancer
     * @param loadBalancerPort Port for the load balancer to listen on
     * @param instancePort Port to forward the request to
     * @return dns the DNS name for the load balancer
     *  @see <a href="http://docs.amazonwebservices.com/ElasticLoadBalancing/latest/DeveloperGuide/"
     */
   String createLoadBalancer(@Nullable String region, String name, String protocol,  Integer loadBalancerPort, Integer instancePort, String availabilityZone);

   /**
    * Delete load balancer
    * 
    * @param name  Name of the load balancer
    * @return
    * @see <a href="http://docs.amazonwebservices.com/ElasticLoadBalancing/2009-05-15/DeveloperGuide/"
    */
   void deleteLoadBalancer(@Nullable String region, String name);

    /**
     * Register instances with an existing load balancer
     * @param name Load Balancer name
     * @param instanceIds Set of instance Ids to register with load balancer
     * @return instanceIds registered with load balancer
     * 
     * @see <a href="http://docs.amazonwebservices.com/ElasticLoadBalancing/2009-05-15/DeveloperGuide/"
     */
   Set<String> registerInstancesWithLoadBalancer(@Nullable String region, String name, String... instanceIds);
   
   /**
    * Deregister instances with an existing load balancer
    * @param name Load Balancer name
    * @param instanceIds Set of instance Ids to deregister with load balancer
    * @return
    * 
    * @see <a href="http://docs.amazonwebservices.com/ElasticLoadBalancing/2009-05-15/DeveloperGuide/"
    */
   void deregisterInstancesWithLoadBalancer(@Nullable String region, String name, String... instanceIds);


   Set<ElasticLoadBalancer> describeLoadBalancers(@Nullable String region, @Nullable String name);

}