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
package org.jclouds.loadbalancer.strategy;

import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.domain.Location;
import org.jclouds.loadbalancer.domain.LoadBalancerMetadata;

import com.google.common.annotations.Beta;

/**
 * Creates a load balancer for nodes listed
 * 
 * @author Lili Nader
 */
public interface LoadBalanceNodesStrategy {
   /**
    * @param location
    *           null if default
    * @param loadBalancerName
    *           Load balancer name
    * @param protocol
    *           LoadBalancer transport protocol to use for routing - TCP or HTTP. This property
    *           cannot be modified for the life of the LoadBalancer.
    * @param loadBalancerPort
    *           The external TCP port of the LoadBalancer. Valid LoadBalancer ports are - 80, 443
    *           and 1024 through 65535. This property cannot be modified for the life of the
    *           LoadBalancer.
    * @param instancePort
    *           The InstancePort data type is simple type of type: integer. It is the TCP port on
    *           which the server on the instance is listening. Valid instance ports are one (1)
    *           through 65535. This property cannot be modified for the life of the LoadBalancer.
    * @param nodes
    *           nodes to loadbalance
    * 
    * @return newly created loadbalancer
    * @see org.jclouds.compute.ComputeService
    */
   @Beta
   LoadBalancerMetadata createLoadBalancerInLocation(Location location, String name, String protocol, int loadBalancerPort,
         int instancePort, Iterable<? extends NodeMetadata> nodes);

}
