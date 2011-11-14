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

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.jclouds.cloudstack.domain.LoadBalancerRule;
import org.jclouds.cloudstack.domain.LoadBalancerRule.Algorithm;
import org.jclouds.cloudstack.domain.VirtualMachine;
import org.jclouds.cloudstack.filters.QuerySigner;
import org.jclouds.cloudstack.options.ListLoadBalancerRulesOptions;
import org.jclouds.functions.JoinOnComma;
import org.jclouds.rest.annotations.ExceptionParser;
import org.jclouds.rest.annotations.ParamParser;
import org.jclouds.rest.annotations.QueryParams;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.Unwrap;
import org.jclouds.rest.functions.ReturnEmptySetOnNotFoundOr404;
import org.jclouds.rest.functions.ReturnNullOnNotFoundOr404;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * Provides asynchronous access to cloudstack via their REST API.
 * <p/>
 * 
 * @see LoadBalancerClient
 * @see <a href="http://download.cloud.com/releases/2.2.0/api_2.2.12/TOC_User.html" />
 * @author Adrian Cole
 */
@RequestFilters(QuerySigner.class)
@QueryParams(keys = "response", values = "json")
public interface LoadBalancerAsyncClient {

   /**
    * @see LoadBalancerClient#listLoadBalancerRules
    */
   @GET
   @QueryParams(keys = "command", values = "listLoadBalancerRules")
   @Unwrap(depth = 2)
   @Consumes(MediaType.APPLICATION_JSON)
   @ExceptionParser(ReturnEmptySetOnNotFoundOr404.class)
   ListenableFuture<Set<LoadBalancerRule>> listLoadBalancerRules(ListLoadBalancerRulesOptions... options);

   /**
    * @see LoadBalancerClient#getLoadBalancerRule
    */
   @GET
   @QueryParams(keys = "command", values = "listLoadBalancerRules")
   @Unwrap(depth = 3, edgeCollection = Set.class)
   @Consumes(MediaType.APPLICATION_JSON)
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<LoadBalancerRule> getLoadBalancerRule(@QueryParam("id") long id);

   /**
    * @see LoadBalancerClient#createLoadBalancerRuleForPublicIp
    */
   @GET
   @QueryParams(keys = "command", values = "createLoadBalancerRule")
   @Unwrap(depth = 2)
   @Consumes(MediaType.APPLICATION_JSON)
   ListenableFuture<LoadBalancerRule> createLoadBalancerRuleForPublicIP(@QueryParam("publicipid") long publicIPId,
         @QueryParam("algorithm") Algorithm algorithm, @QueryParam("name") String name,
         @QueryParam("privateport") int privatePort, @QueryParam("publicport") int publicPort);

   /**
    * @see LoadBalancerClient#deleteLoadBalancerRule
    */
   @GET
   @QueryParams(keys = "command", values = "deleteLoadBalancerRule")
   @Unwrap(depth = 2)
   @Consumes(MediaType.APPLICATION_JSON)
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<Long> deleteLoadBalancerRule(@QueryParam("id") long id);

   /**
    * @see LoadBalancerClient#assignVirtualMachinesToLoadBalancerRule(long,Iterable)
    */
   @GET
   @QueryParams(keys = "command", values = "assignToLoadBalancerRule")
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   @Unwrap(depth = 2)
   @Consumes(MediaType.APPLICATION_JSON)
   ListenableFuture<Long> assignVirtualMachinesToLoadBalancerRule(@QueryParam("id") long id,
         @QueryParam("virtualmachineids") @ParamParser(JoinOnComma.class) Iterable<Long> virtualMachineIds);

   /**
    * @see LoadBalancerClient#assignVirtualMachinesToLoadBalancerRule(long,long[])
    */
   @GET
   @QueryParams(keys = "command", values = "assignToLoadBalancerRule")
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   @Unwrap(depth = 2)
   @Consumes(MediaType.APPLICATION_JSON)
   ListenableFuture<Long> assignVirtualMachinesToLoadBalancerRule(@QueryParam("id") long id,
         @QueryParam("virtualmachineids") @ParamParser(JoinOnComma.class) long... virtualMachineIds);

   /**
    * @see LoadBalancerClient#removeVirtualMachinesFromLoadBalancerRule(long,Iterable)
    */
   @GET
   @QueryParams(keys = "command", values = "removeFromLoadBalancerRule")
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   @Unwrap(depth = 2)
   @Consumes(MediaType.APPLICATION_JSON)
   ListenableFuture<Long> removeVirtualMachinesFromLoadBalancerRule(@QueryParam("id") long id,
         @QueryParam("virtualmachineids") @ParamParser(JoinOnComma.class) Iterable<Long> virtualMachineIds);

   /**
    * @see LoadBalancerClient#removeVirtualMachinesFromLoadBalancerRule(long,long[])
    */
   @GET
   @QueryParams(keys = "command", values = "removeFromLoadBalancerRule")
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   @Unwrap(depth = 2)
   @Consumes(MediaType.APPLICATION_JSON)
   ListenableFuture<Long> removeVirtualMachinesFromLoadBalancerRule(@QueryParam("id") long id,
         @QueryParam("virtualmachineids") @ParamParser(JoinOnComma.class) long... virtualMachineIds);

   /**
    * @see LoadBalancerClient#listVirtualMachinesAssignedToLoadBalancerRule
    */
   @GET
   @QueryParams(keys = "command", values = "listLoadBalancerRuleInstances")
   @Unwrap(depth = 2)
   @Consumes(MediaType.APPLICATION_JSON)
   @ExceptionParser(ReturnEmptySetOnNotFoundOr404.class)
   ListenableFuture<Set<VirtualMachine>> listVirtualMachinesAssignedToLoadBalancerRule(@QueryParam("id") long id);

}
