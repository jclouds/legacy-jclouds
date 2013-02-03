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

import com.google.common.util.concurrent.ListenableFuture;

/**
 * Provides asynchronous access to cloudstack via their REST API.
 * <p/>
 * 
 * @see LoadBalancerClient
 * @see <a href="http://download.cloud.com/releases/2.2.0/api_2.2.12/TOC_User.html" />
 * @author Adrian Cole
 */
@RequestFilters(AuthenticationFilter.class)
@QueryParams(keys = "response", values = "json")
public interface LoadBalancerAsyncClient {

   /**
    * @see LoadBalancerClient#listLoadBalancerRules
    */
   @Named("listLoadBalancerRules")
   @GET
   @QueryParams(keys = { "command", "listAll" }, values = { "listLoadBalancerRules", "true" })
   @SelectJson("loadbalancerrule")
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(EmptySetOnNotFoundOr404.class)
   ListenableFuture<Set<LoadBalancerRule>> listLoadBalancerRules(ListLoadBalancerRulesOptions... options);

   /**
    * @see LoadBalancerClient#getLoadBalancerRule
    */
   @Named("listLoadBalancerRules")
   @GET
   @QueryParams(keys = { "command", "listAll" }, values = { "listLoadBalancerRules", "true" })
   @SelectJson("loadbalancerrule")
   @OnlyElement
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(NullOnNotFoundOr404.class)
   ListenableFuture<LoadBalancerRule> getLoadBalancerRule(@QueryParam("id") String id);

   /**
    * @see LoadBalancerClient#createLoadBalancerRuleForPublicIP
    */
   @Named("createLoadBalancerRule")
   @GET
   @QueryParams(keys = "command", values = "createLoadBalancerRule")
   @SelectJson("jobid")
   @Consumes(MediaType.APPLICATION_JSON)
   ListenableFuture<String> createLoadBalancerRuleForPublicIP(@QueryParam("publicipid") String publicIPId,
         @QueryParam("algorithm") Algorithm algorithm, @QueryParam("name") String name,
         @QueryParam("privateport") int privatePort, @QueryParam("publicport") int publicPort,
         CreateLoadBalancerRuleOptions... options);

   /**
    * @see LoadBalancerClient#updateLoadBalancerRule
    */
   @Named("updateLoadBalancerRule")
   @GET
   @QueryParams(keys = "command", values ="updateLoadBalancerRule")
   @SelectJson("loadbalancerrule")
   @OnlyElement
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(NullOnNotFoundOr404.class)
   ListenableFuture<LoadBalancerRule> updateLoadBalancerRule(@QueryParam("id") String id, UpdateLoadBalancerRuleOptions... options);

   /**
    * @see LoadBalancerClient#deleteLoadBalancerRule
    */
   @Named("deleteLoadBalancerRule")
   @GET
   @QueryParams(keys = "command", values = "deleteLoadBalancerRule")
   @SelectJson("jobid")
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(NullOnNotFoundOr404.class)
   ListenableFuture<String> deleteLoadBalancerRule(@QueryParam("id") String id);

   /**
    * @see LoadBalancerClient#assignVirtualMachinesToLoadBalancerRule(String,Iterable)
    */
   @Named("assignToLoadBalancerRule")
   @GET
   @QueryParams(keys = "command", values = "assignToLoadBalancerRule")
   @Fallback(NullOnNotFoundOr404.class)
   @SelectJson("jobid")
   @Consumes(MediaType.APPLICATION_JSON)
   ListenableFuture<String> assignVirtualMachinesToLoadBalancerRule(@QueryParam("id") String id,
         @QueryParam("virtualmachineids") @ParamParser(JoinOnComma.class) Iterable<String> virtualMachineIds);

   /**
    * @see LoadBalancerClient#assignVirtualMachinesToLoadBalancerRule(String,String[])
    */
   @Named("assignToLoadBalancerRule")
   @GET
   @QueryParams(keys = "command", values = "assignToLoadBalancerRule")
   @Fallback(NullOnNotFoundOr404.class)
   @SelectJson("jobid")
   @Consumes(MediaType.APPLICATION_JSON)
   ListenableFuture<String> assignVirtualMachinesToLoadBalancerRule(@QueryParam("id") String id,
         @QueryParam("virtualmachineids") @ParamParser(JoinOnComma.class) String... virtualMachineIds);

   /**
    * @see LoadBalancerClient#removeVirtualMachinesFromLoadBalancerRule(String,Iterable)
    */
   @Named("removeFromLoadBalancerRule")
   @GET
   @QueryParams(keys = "command", values = "removeFromLoadBalancerRule")
   @Fallback(NullOnNotFoundOr404.class)
   @SelectJson("jobid")
   @Consumes(MediaType.APPLICATION_JSON)
   ListenableFuture<String> removeVirtualMachinesFromLoadBalancerRule(@QueryParam("id") String id,
         @QueryParam("virtualmachineids") @ParamParser(JoinOnComma.class) Iterable<String> virtualMachineIds);

   /**
    * @see LoadBalancerClient#removeVirtualMachinesFromLoadBalancerRule(String,String[])
    */
   @Named("removeFromLoadBalancerRule")
   @GET
   @QueryParams(keys = "command", values = "removeFromLoadBalancerRule")
   @Fallback(NullOnNotFoundOr404.class)
   @SelectJson("jobid")
   @Consumes(MediaType.APPLICATION_JSON)
   ListenableFuture<String> removeVirtualMachinesFromLoadBalancerRule(@QueryParam("id") String id,
         @QueryParam("virtualmachineids") @ParamParser(JoinOnComma.class) String... virtualMachineIds);

   /**
    * @see LoadBalancerClient#listVirtualMachinesAssignedToLoadBalancerRule
    */
   @Named("listLoadBalancerRuleInstances")
   @GET
   @QueryParams(keys = { "command", "listAll" }, values = { "listLoadBalancerRuleInstances", "true" })
   @SelectJson("loadbalancerruleinstance")
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(EmptySetOnNotFoundOr404.class)
   ListenableFuture<Set<VirtualMachine>> listVirtualMachinesAssignedToLoadBalancerRule(@QueryParam("id") String id);

}
