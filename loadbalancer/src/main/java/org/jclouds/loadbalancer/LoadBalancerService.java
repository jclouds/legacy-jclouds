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

import java.util.Set;

import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.domain.Location;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.loadbalancer.domain.LoadBalancerMetadata;
import org.jclouds.loadbalancer.internal.BaseLoadBalancerService;

import com.google.common.annotations.Beta;
import com.google.inject.ImplementedBy;

/**
 * Provides portable access to load balancer services.
 * 
 * @author Lili Nadar
 */
@Beta
@ImplementedBy(BaseLoadBalancerService.class)
public interface LoadBalancerService {

   /**
    * The list locations command returns all the valid locations for load balancers. A location has
    * a scope, which is typically region or zone. A region is a general area, like eu-west, where a
    * zone is similar to a datacenter. If a location has a parent, that implies it is within that
    * location. For example a location can be a rack, whose parent is likely to be a zone.
    */
   Set<? extends Location> listAssignableLocations();

   /**
    * @return a reference to the context that created this LoadBalancerService.
    */
   LoadBalancerServiceContext getContext();

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
    * @see org.jclouds.compute.ComputeService
    */
   // TODO: this needs to be split up into 2 items: create load balancer and registernodes
   @Beta
   LoadBalancerMetadata createLoadBalancerInLocation(@Nullable Location location, String loadBalancerName,
         String protocol, int loadBalancerPort, int instancePort, Iterable<? extends NodeMetadata> nodes);

   @Beta
   void destroyLoadBalancer(String id);

   @Beta
   Set<? extends LoadBalancerMetadata> listLoadBalancers();

   LoadBalancerMetadata getLoadBalancerMetadata(String id);

}
