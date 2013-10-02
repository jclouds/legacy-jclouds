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
package org.jclouds.cloudstack.features;

import java.util.Set;

import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.jclouds.Fallbacks.EmptySetOnNotFoundOr404;
import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.cloudstack.domain.LoadBalancerRule;
import org.jclouds.cloudstack.domain.LoadBalancerRule.Algorithm;
import org.jclouds.cloudstack.domain.VirtualMachine;
import org.jclouds.cloudstack.filters.AuthenticationFilter;
import org.jclouds.cloudstack.options.CreateLoadBalancerRuleOptions;
import org.jclouds.cloudstack.options.ListLoadBalancerRulesOptions;
import org.jclouds.cloudstack.options.UpdateLoadBalancerRuleOptions;
import org.jclouds.functions.JoinOnComma;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.OnlyElement;
import org.jclouds.rest.annotations.ParamParser;
import org.jclouds.rest.annotations.QueryParams;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.SelectJson;

/**
 * Provides synchronous access to cloudstack via their REST API.
 * <p/>
 * 
 * @see <a href="http://download.cloud.com/releases/2.2.0/api_2.2.12/TOC_User.html" />
 * @author Adrian Cole
 */
@RequestFilters(AuthenticationFilter.class)
@QueryParams(keys = "response", values = "json")
public interface LoadBalancerApi {

   /**
    * List the load balancer rules
    * 
    * @param options
    *           if present, how to constrain the list.
    * @return load balancer rules matching query, or empty set, if no load
    *         balancer rules are found
    */
   @Named("listLoadBalancerRules")
   @GET
   @QueryParams(keys = { "command", "listAll" }, values = { "listLoadBalancerRules", "true" })
   @SelectJson("loadbalancerrule")
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(EmptySetOnNotFoundOr404.class)
   Set<LoadBalancerRule> listLoadBalancerRules(ListLoadBalancerRulesOptions... options);

   /**
    * get a specific LoadBalancerRule by id
    * 
    * @param id
    *           LoadBalancerRule to get
    * @return LoadBalancerRule or null if not found
    */
   @Named("listLoadBalancerRules")
   @GET
   @QueryParams(keys = { "command", "listAll" }, values = { "listLoadBalancerRules", "true" })
   @SelectJson("loadbalancerrule")
   @OnlyElement
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(NullOnNotFoundOr404.class)
   LoadBalancerRule getLoadBalancerRule(@QueryParam("id") String id);

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
   @Named("createLoadBalancerRule")
   @GET
   @QueryParams(keys = "command", values = "createLoadBalancerRule")
   @SelectJson("jobid")
   @Consumes(MediaType.APPLICATION_JSON)
   String createLoadBalancerRuleForPublicIP(@QueryParam("publicipid") String publicIPId,
         @QueryParam("algorithm") Algorithm algorithm, @QueryParam("name") String name,
         @QueryParam("privateport") int privatePort, @QueryParam("publicport") int publicPort,
         CreateLoadBalancerRuleOptions... options);

   /**
    * Update a load balancer rule.
    *
    * @param id
    *       rule id
    * @param options
    *       optional arguments
    * @return updated rule
    */
   @Named("updateLoadBalancerRule")
   @GET
   @QueryParams(keys = "command", values = "updateLoadBalancerRule")
   @SelectJson("loadbalancerrule")
   @OnlyElement
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(NullOnNotFoundOr404.class)
   LoadBalancerRule updateLoadBalancerRule(@QueryParam("id") String id, UpdateLoadBalancerRuleOptions... options);

   /**
    * 
    * deletes a loadbalancer rule
    * 
    * @param id
    *           id of the rule to delete
    * @return async job id of the job completing or null, if the load balancer
    *         rule was not found.
    */
   @Named("deleteLoadBalancerRule")
   @GET
   @QueryParams(keys = "command", values = "deleteLoadBalancerRule")
   @SelectJson("jobid")
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(NullOnNotFoundOr404.class)
   String deleteLoadBalancerRule(@QueryParam("id") String id);

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
   @Named("assignToLoadBalancerRule")
   @GET
   @QueryParams(keys = "command", values = "assignToLoadBalancerRule")
   @Fallback(NullOnNotFoundOr404.class)
   @SelectJson("jobid")
   @Consumes(MediaType.APPLICATION_JSON)
   String assignVirtualMachinesToLoadBalancerRule(@QueryParam("id") String id,
         @QueryParam("virtualmachineids") @ParamParser(JoinOnComma.class) Iterable<String> virtualMachineIds);

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
   @Named("assignToLoadBalancerRule")
   @GET
   @QueryParams(keys = "command", values = "assignToLoadBalancerRule")
   @Fallback(NullOnNotFoundOr404.class)
   @SelectJson("jobid")
   @Consumes(MediaType.APPLICATION_JSON)
   String assignVirtualMachinesToLoadBalancerRule(@QueryParam("id") String id,
         @QueryParam("virtualmachineids") @ParamParser(JoinOnComma.class) String... virtualMachineIds);

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
   @Named("removeFromLoadBalancerRule")
   @GET
   @QueryParams(keys = "command", values = "removeFromLoadBalancerRule")
   @Fallback(NullOnNotFoundOr404.class)
   @SelectJson("jobid")
   @Consumes(MediaType.APPLICATION_JSON)
   String removeVirtualMachinesFromLoadBalancerRule(@QueryParam("id") String id,
         @QueryParam("virtualmachineids") @ParamParser(JoinOnComma.class) Iterable<String> virtualMachineIds);

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
   @Named("removeFromLoadBalancerRule")
   @GET
   @QueryParams(keys = "command", values = "removeFromLoadBalancerRule")
   @Fallback(NullOnNotFoundOr404.class)
   @SelectJson("jobid")
   @Consumes(MediaType.APPLICATION_JSON)
   String removeVirtualMachinesFromLoadBalancerRule(@QueryParam("id") String id,
         @QueryParam("virtualmachineids") @ParamParser(JoinOnComma.class) String... virtualMachineIds);

   /**
    * List all virtual machine instances that are assigned to a load balancer
    * rule.
    * 
    * @param id
    *           id of the rule
    * @return VirtualMachines matching query, or empty set, if no
    *         VirtualMachines are assigned
    */
   @Named("listLoadBalancerRuleInstances")
   @GET
   @QueryParams(keys = { "command", "listAll" }, values = { "listLoadBalancerRuleInstances", "true" })
   @SelectJson("loadbalancerruleinstance")
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(EmptySetOnNotFoundOr404.class)
   Set<VirtualMachine> listVirtualMachinesAssignedToLoadBalancerRule(@QueryParam("id") String id);

}
