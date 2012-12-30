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
package org.jclouds.gogrid.services;

import java.util.List;
import java.util.Set;
import org.jclouds.gogrid.domain.IpPortPair;
import org.jclouds.gogrid.domain.LoadBalancer;
import org.jclouds.gogrid.domain.Option;
import org.jclouds.gogrid.options.AddLoadBalancerOptions;

/**
 * @author Oleksiy Yarmula
 */
public interface GridLoadBalancerClient {

   /**
    * Returns all load balancers found for the current user.
    * 
    * @return load balancers found
    */
   Set<LoadBalancer> getLoadBalancerList();

   /**
    * Returns the load balancer(s) by unique name(s).
    * 
    * Given a name or a set of names, finds one or multiple load balancers.
    * 
    * @param names
    *           to get the load balancers
    * @return load balancer(s) matching the name(s)
    */
   Set<LoadBalancer> getLoadBalancersByName(String... names);

   /**
    * Returns the load balancer(s) by unique id(s).
    * 
    * Given an id or a set of ids, finds one or multiple load balancers.
    * 
    * @param ids
    *           to get the load balancers
    * @return load balancer(s) matching the ids
    */
   Set<LoadBalancer> getLoadBalancersById(Long... ids);

   /**
    * Creates a load balancer with given properties.
    * 
    * @param name
    *           name of the load balancer
    * @param virtualIp
    *           virtual IP with IP address set in {@link org.jclouds.gogrid.domain.Ip#ip} and port
    *           set in {@link IpPortPair#port}
    * @param realIps
    *           real IPs to bind the virtual IP to, with IP address set in
    *           {@link org.jclouds.gogrid.domain.Ip#ip} and port set in {@link IpPortPair#port}
    * @param options
    *           options that specify load balancer's type (round robin, least load), persistence
    *           strategy, or description.
    * @return created load balancer object
    */
   LoadBalancer addLoadBalancer(String name, IpPortPair virtualIp, List<IpPortPair> realIps,
            AddLoadBalancerOptions... options);

   /**
    * Edits the existing load balancer to change the real IP mapping.
    * 
    * @param id
    *           id of the existing load balancer
    * @param realIps
    *           real IPs to bind the virtual IP to, with IP address set in
    *           {@link org.jclouds.gogrid.domain.Ip#ip} and port set in {@link IpPortPair#port}
    * @return edited object
    */
   LoadBalancer editLoadBalancer(long id, List<IpPortPair> realIps);

   /**
    * Edits the existing load balancer to change the real IP mapping.
    * 
    * @param name
    *           name of the existing load balancer
    * @param realIps
    *           real IPs to bind the virtual IP to, with IP address set in
    *           {@link org.jclouds.gogrid.domain.Ip#ip} and port set in {@link IpPortPair#port}
    * @return edited object
    */
   LoadBalancer editLoadBalancerNamed(String name, List<IpPortPair> realIps);

   /**
    * Deletes the load balancer by Id
    * 
    * @param id
    *           id of the load balancer to delete
    * @return load balancer before the command is executed
    */
   LoadBalancer deleteById(Long id);

   /**
    * Deletes the load balancer by name;
    * 
    * NOTE: Using this parameter may generate an error if one or more load balancers share a
    * non-unique name.
    * 
    * @param name
    *           name of the load balancer to be deleted
    * 
    * @return load balancer before the command is executed
    */
   LoadBalancer deleteByName(String name);

   /**
    * Retrieves the list of supported Datacenters to launch servers into. The objects will have
    * datacenter ID, name and description. In most cases, id or name will be used for
    * {@link #addLoadBalancer}.
    * 
    * @return supported datacenters
    */
   Set<Option> getDatacenters();

}
