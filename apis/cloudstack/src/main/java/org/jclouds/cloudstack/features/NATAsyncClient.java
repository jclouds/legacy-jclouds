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
import org.jclouds.cloudstack.domain.AsyncCreateResponse;
import org.jclouds.cloudstack.domain.IPForwardingRule;
import org.jclouds.cloudstack.filters.AuthenticationFilter;
import org.jclouds.cloudstack.options.CreateIPForwardingRuleOptions;
import org.jclouds.cloudstack.options.ListIPForwardingRulesOptions;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.OnlyElement;
import org.jclouds.rest.annotations.QueryParams;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.SelectJson;
import org.jclouds.rest.annotations.Unwrap;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * Provides asynchronous access to cloudstack via their REST API.
 * <p/>
 * 
 * @see NATClient
 * @see <a
 *      href="http://download.cloud.com/releases/2.2.0/api_2.2.12/TOC_User.html"
 *      />
 * @author Adrian Cole
 */
@RequestFilters(AuthenticationFilter.class)
@QueryParams(keys = "response", values = "json")
public interface NATAsyncClient {

   /**
    * @see NATClient#listIPForwardingRules
    */
   @Named("listIpForwardingRules")
   @GET
   @QueryParams(keys = { "command", "listAll" }, values = { "listIpForwardingRules", "true" })
   @SelectJson("ipforwardingrule")
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(EmptySetOnNotFoundOr404.class)
   ListenableFuture<Set<IPForwardingRule>> listIPForwardingRules(ListIPForwardingRulesOptions... options);

   /**
    * @see NATClient#getIPForwardingRule
    */
   @Named("listIpForwardingRules")
   @GET
   @QueryParams(keys = { "command", "listAll" }, values = { "listIpForwardingRules", "true" })
   @SelectJson("ipforwardingrule")
   @OnlyElement
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(NullOnNotFoundOr404.class)
   ListenableFuture<IPForwardingRule> getIPForwardingRule(@QueryParam("id") String id);

   /**
    * @see NATClient#getIPForwardingRulesForIPAddress
    */
   @Named("listIpForwardingRules")
   @GET
   @QueryParams(keys = { "command", "listAll" }, values = { "listIpForwardingRules", "true" })
   @SelectJson("ipforwardingrule")
   @Consumes(MediaType.APPLICATION_JSON)
   ListenableFuture<Set<IPForwardingRule>> getIPForwardingRulesForIPAddress(@QueryParam("ipaddressid") String id);

   /**
    * @see NATClient#getIPForwardingRulesForVirtualMachine
    */
   @Named("listIpForwardingRules")
   @GET
   @QueryParams(keys = { "command", "listAll" }, values = { "listIpForwardingRules", "true" })
   @SelectJson("ipforwardingrule")
   @Consumes(MediaType.APPLICATION_JSON)
   ListenableFuture<Set<IPForwardingRule>> getIPForwardingRulesForVirtualMachine(@QueryParam("virtualmachineid") String id);

   /**
    * @see NATClient#createIPForwardingRule
    */
   @Named("createIpForwardingRule")
   @GET
   @QueryParams(keys = "command", values = "createIpForwardingRule")
   @Unwrap
   @Consumes(MediaType.APPLICATION_JSON)
   ListenableFuture<AsyncCreateResponse> createIPForwardingRule(@QueryParam("ipaddressid") String IPAddressId,
         @QueryParam("protocol") String protocol, @QueryParam("startport") int startPort,
         CreateIPForwardingRuleOptions... options);

   /**
    * @see NATClient#enableStaticNATForVirtualMachine
    */
   @Named("enableStaticNat")
   @GET
   @QueryParams(keys = "command", values = "enableStaticNat")
   @Consumes(MediaType.APPLICATION_JSON)
   ListenableFuture<Void> enableStaticNATForVirtualMachine(
         @QueryParam("virtualmachineid") String virtualMachineId, @QueryParam("ipaddressid") String IPAddressId);

   /**
    * @see NATClient#deleteIPForwardingRule
    */
   @Named("deleteIpForwardingRule")
   @GET
   @QueryParams(keys = "command", values = "deleteIpForwardingRule")
   @SelectJson("jobid")
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(NullOnNotFoundOr404.class)
   ListenableFuture<String> deleteIPForwardingRule(@QueryParam("id") String id);

   /**
    * @see NATClient#disableStaticNATOnPublicIP
    */
   @Named("disableStaticNat")
   @GET
   @QueryParams(keys = "command", values = "disableStaticNat")
   @SelectJson("jobid")
   @Consumes(MediaType.APPLICATION_JSON)
   ListenableFuture<String> disableStaticNATOnPublicIP(@QueryParam("ipaddressid") String IPAddressId);

}
