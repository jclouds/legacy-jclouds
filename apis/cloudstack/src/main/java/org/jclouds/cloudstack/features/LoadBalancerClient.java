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
package org.jclouds.cloudstack.features;

import java.util.Set;
import org.jclouds.cloudstack.domain.LoadBalancerRule;
import org.jclouds.cloudstack.domain.VirtualMachine;
import org.jclouds.cloudstack.domain.LoadBalancerRule.Algorithm;
import org.jclouds.cloudstack.options.CreateLoadBalancerRuleOptions;
import org.jclouds.cloudstack.options.ListLoadBalancerRulesOptions;
import org.jclouds.cloudstack.options.UpdateLoadBalancerRuleOptions;

/**
 * Provides synchronous access to CloudStack LoadBalancer features.
 * <p/>
 * 
 * @see LoadBalancerAsyncClient
 * @see <a href="http://download.cloud.com/releases/2.2.0/api_2.2.12/TOC_User.html" />
 * @author Adrian Cole
 */
public interface LoadBalancerClient {
   /**
    * List the load balancer rules
    * 
    * @param options
    *           if present, how to constrain the list.
    * @return load balancer rules matching query, or empty set, if no load
    *         balancer rules are found
    */
   Set<LoadBalancerRule> listLoadBalancerRules(ListLoadBalancerRulesOptions... options);

   /**
    * get a specific LoadBalancerRule by id
    * 
    * @param id
    *           LoadBalancerRule to get
    * @return LoadBalancerRule or null if not found
    */
   LoadBalancerRule getLoadBalancerRule(String id);

   /**
    * Creates a load balancer rule.
    * 
    * @param publicIPId
    *           the public port from where the network traffic will be load
    *           balanced from
    * @param algorithm
    *           load balancer algorithm (source, roundrobin, leastconn)
    * @param name
    *           name of the load balancer rule
    * @param privatePort
    *           the private port of the private ip address/virtual machine where
    *           the network traffic will be load balanced to
    * @param publicPort
    *           public ip address id from where the network traffic will be load
    *           balanced from
    * @param options optional call arguments
    * @return newly created rule
    */
   String createLoadBalancerRuleForPublicIP(String publicIPId, Algorithm algorithm, String name,
         int privatePort, int publicPort, CreateLoadBalancerRuleOptions... options);

   /**
    * Update a load balancer rule.
    *
    * @param id
    *       rule id
    * @param options
    *       optional arguments
    * @return updated rule
    */
   LoadBalancerRule updateLoadBalancerRule(String id, UpdateLoadBalancerRuleOptions... options);

   /**
    * 
    * deletes a loadbalancer rule
    * 
    * @param id
    *           id of the rule to delete
    * @return async job id of the job completing or null, if the load balancer
    *         rule was not found.
    */
   String deleteLoadBalancerRule(String id);

   /**
    * List all virtual machine instances that are assigned to a load balancer
    * rule.
    * 
    * @param id
    *           id of the rule
    * @return VirtualMachines matching query, or empty set, if no
    *         VirtualMachines are assigned
    */
   Set<VirtualMachine> listVirtualMachinesAssignedToLoadBalancerRule(String id);

   /**
    * Assigns virtual machine or a list of virtual machines to a load balancer
    * rule.
    * 
    * @param id
    *           the ID of the load balancer rule
    * @param virtualMachineIds
    *           the list of IDs of the virtual machine that are being assigned
    *           to the load balancer rule
    * @return job id related to the operation
    */
   String assignVirtualMachinesToLoadBalancerRule(String id, Iterable<String> virtualMachineIds);

   /**
    * Assigns virtual machine or a list of virtual machines to a load balancer
    * rule.
    * 
    * @param id
    *           the ID of the load balancer rule
    * @param virtualMachineIds
    *           the list of IDs of the virtual machine that are being assigned
    *           to the load balancer rule
    * @return job id related to the operation
    */
   String assignVirtualMachinesToLoadBalancerRule(String id, String... virtualMachineIds);

   /**
    * Removes a virtual machine or a list of virtual machines from a load
    * balancer rule.
    * 
    * @param id
    *           the ID of the load balancer rule
    * @param virtualMachineIds
    *           the list of IDs of the virtual machine that are being removed
    *           from the load balancer rule
    * @return job id related to the operation
    */
   String removeVirtualMachinesFromLoadBalancerRule(String id, Iterable<String> virtualMachineIds);

   /**
    * Removes a virtual machine or a list of virtual machines from a load
    * balancer rule.
    * 
    * @param id
    *           the ID of the load balancer rule
    * @param virtualMachineIds
    *           the list of IDs of the virtual machine that are being removed
    *           from the load balancer rule
    * @return job id related to the operation
    */
   String removeVirtualMachinesFromLoadBalancerRule(String id, String... virtualMachineIds);
}
