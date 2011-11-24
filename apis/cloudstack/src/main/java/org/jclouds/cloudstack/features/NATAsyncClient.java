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

import org.jclouds.cloudstack.domain.AsyncCreateResponse;
import org.jclouds.cloudstack.domain.IPForwardingRule;
import org.jclouds.cloudstack.filters.QuerySigner;
import org.jclouds.cloudstack.options.CreateIPForwardingRuleOptions;
import org.jclouds.cloudstack.options.ListIPForwardingRulesOptions;
import org.jclouds.rest.annotations.ExceptionParser;
import org.jclouds.rest.annotations.OnlyElement;
import org.jclouds.rest.annotations.QueryParams;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.SelectJson;
import org.jclouds.rest.annotations.Unwrap;
import org.jclouds.rest.functions.ReturnEmptySetOnNotFoundOr404;
import org.jclouds.rest.functions.ReturnNullOnNotFoundOr404;
import org.jclouds.rest.functions.ReturnVoidOnNotFoundOr404;

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
@RequestFilters(QuerySigner.class)
@QueryParams(keys = "response", values = "json")
public interface NATAsyncClient {

   /**
    * @see NATClient#listIPForwardingRules
    */
   @GET
   @QueryParams(keys = "command", values = "listIpForwardingRules")
   @SelectJson("ipforwardingrule")
   @Consumes(MediaType.APPLICATION_JSON)
   @ExceptionParser(ReturnEmptySetOnNotFoundOr404.class)
   ListenableFuture<Set<IPForwardingRule>> listIPForwardingRules(ListIPForwardingRulesOptions... options);

   /**
    * @see NATClient#getIPForwardingRule
    */
   @GET
   @QueryParams(keys = "command", values = "listIpForwardingRules")
   @SelectJson("ipforwardingrule")
   @OnlyElement
   @Consumes(MediaType.APPLICATION_JSON)
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<IPForwardingRule> getIPForwardingRule(@QueryParam("id") long id);

   /**
    * @see NATClient#getIPForwardingRulesForIPAddress
    */
   @GET
   @QueryParams(keys = "command", values = "listIpForwardingRules")
   @SelectJson("ipforwardingrule")
   @Consumes(MediaType.APPLICATION_JSON)
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<Set<IPForwardingRule>> getIPForwardingRulesForIPAddress(@QueryParam("ipaddressid") long id);

   /**
    * @see NATClient#getIPForwardingRulesForVirtualMachine
    */
   @GET
   @QueryParams(keys = "command", values = "listIpForwardingRules")
   @SelectJson("ipforwardingrule")
   @Consumes(MediaType.APPLICATION_JSON)
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<Set<IPForwardingRule>> getIPForwardingRulesForVirtualMachine(@QueryParam("virtualmachineid") long id);

   /**
    * @see NATClient#createIPForwardingRule
    */
   @GET
   @QueryParams(keys = "command", values = "createIpForwardingRule")
   @Unwrap
   @Consumes(MediaType.APPLICATION_JSON)
   ListenableFuture<AsyncCreateResponse> createIPForwardingRule(@QueryParam("ipaddressid") long IPAddressId,
         @QueryParam("protocol") String protocol, @QueryParam("startport") int startPort,
         CreateIPForwardingRuleOptions... options);

   /**
    * @see NATClient#enableStaticNATForVirtualMachine
    */
   @GET
   @QueryParams(keys = "command", values = "enableStaticNat")
   @Unwrap
   @Consumes(MediaType.APPLICATION_JSON)
   ListenableFuture<AsyncCreateResponse> enableStaticNATForVirtualMachine(
         @QueryParam("virtualmachineid") long virtualMachineId, @QueryParam("ipaddressid") long IPAddressId);

   /**
    * @see NATClient#deleteIPForwardingRule
    */
   @GET
   @QueryParams(keys = "command", values = "deleteIpForwardingRule")
   @Unwrap(depth = 2)
   @Consumes(MediaType.APPLICATION_JSON)
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<Long> deleteIPForwardingRule(@QueryParam("id") long id);

   /**
    * @see NATClient#disableStaticNat
    */
   @GET
   @QueryParams(keys = "command", values = "disableStaticNat")
   @Consumes(MediaType.APPLICATION_JSON)
   @ExceptionParser(ReturnVoidOnNotFoundOr404.class)
   ListenableFuture<Void> disableStaticNat(@QueryParam("ipaddressid") long IPAddressId);

}
