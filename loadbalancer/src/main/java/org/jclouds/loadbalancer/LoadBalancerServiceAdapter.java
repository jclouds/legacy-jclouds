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
package org.jclouds.loadbalancer;

import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.domain.Location;
import org.jclouds.javax.annotation.Nullable;

/**
 * A means of specifying the interface between the {@link LoadBalancerService LoadBalancerServices}
 * and a concrete compute cloud implementation, jclouds or otherwise.
 * 
 * @author Adrian Cole
 * 
 */
public interface LoadBalancerServiceAdapter<B, L> {

   /**
    * 
    * @see LoadBalancerService#createLoadBalancerInLocation
    */
   B createLoadBalancerInLocation(@Nullable Location location, String loadBalancerName, String protocol,
         int loadBalancerPort, int instancePort, Iterable<? extends NodeMetadata> nodes);

   /**
    * 
    * @see LoadBalancerService#listAssignableLocations
    */
   Iterable<L> listAssignableLocations();

   /**
    * 
    * @see LoadBalancerService#getLoadBalancerMetadata
    */
   B getLoadBalancer(String id);

   /**
    * 
    * @see LoadBalancerService#destroyLoadBalancer
    */
   void destroyLoadBalancer(String id);

   /**
    * 
    * @see LoadBalancerService#listLoadBalancers
    */
   Iterable<B> listLoadBalancers();

}
