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
import org.jclouds.cloudstack.domain.FirewallRule;
import org.jclouds.cloudstack.domain.PortForwardingRule;
import org.jclouds.cloudstack.filters.QuerySigner;
import org.jclouds.cloudstack.options.CreateFirewallRuleOptions;
import org.jclouds.cloudstack.options.ListFirewallRulesOptions;
import org.jclouds.cloudstack.options.ListPortForwardingRulesOptions;
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
 * @author Adrian Cole
 * @see FirewallClient
 * @see <a href="http://download.cloud.com/releases/2.2.0/api_2.2.12/TOC_User.html" />
 */
@RequestFilters(QuerySigner.class)
@QueryParams(keys = "response", values = "json")
public interface FirewallAsyncClient {

   /**
    * @see FirewallClient#listFirewallRules
    */
   @GET
   @QueryParams(keys = "command", values = "listFirewallRules")
   @SelectJson("firewallrule")
   @Consumes(MediaType.APPLICATION_JSON)
   @ExceptionParser(ReturnEmptySetOnNotFoundOr404.class)
   ListenableFuture<Set<FirewallRule>> listFirewallRules(ListFirewallRulesOptions... options);

   /**
    * @see FirewallClient#getFirewallRule
    */
   @GET
   @QueryParams(keys = "command", values = "listFirewallRules")
   @SelectJson("firewallrule")
   @OnlyElement
   @Consumes(MediaType.APPLICATION_JSON)
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<FirewallRule> getFirewallRule(@QueryParam("id") long id);

   /**
    * @see FirewallClient#createFirewallRuleForIpAndProtocol
    */
   @GET
   @QueryParams(keys = "command", values = "createFirewallRule")
   @Unwrap
   @Consumes(MediaType.APPLICATION_JSON)
   ListenableFuture<AsyncCreateResponse> createFirewallRuleForIpAndProtocol(@QueryParam("ipaddressid") long ipAddressId,
         @QueryParam("protocol") FirewallRule.Protocol protocol, CreateFirewallRuleOptions... options);

   /**
    * @see FirewallClient#deleteFirewallRule
    */
   @GET
   @QueryParams(keys = "command", values = "deleteFirewallRule")
   @ExceptionParser(ReturnVoidOnNotFoundOr404.class)
   ListenableFuture<Void> deleteFirewallRule(@QueryParam("id") long id);

   /**
    * @see FirewallClient#listPortForwardingRules
    */
   @GET
   @QueryParams(keys = "command", values = "listPortForwardingRules")
   @SelectJson("portforwardingrule")
   @Consumes(MediaType.APPLICATION_JSON)
   @ExceptionParser(ReturnEmptySetOnNotFoundOr404.class)
   ListenableFuture<Set<PortForwardingRule>> listPortForwardingRules(ListPortForwardingRulesOptions... options);

   /**
    * @see FirewallClient#getPortForwardingRule
    */
   @GET
   @QueryParams(keys = "command", values = "listPortForwardingRules")
   @SelectJson("portforwardingrule")
   @OnlyElement
   @Consumes(MediaType.APPLICATION_JSON)
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<PortForwardingRule> getPortForwardingRule(@QueryParam("id") long id);

   /**
    * @see FirewallClient#createPortForwardingRuleForVirtualMachine
    */
   @GET
   @QueryParams(keys = "command", values = "createPortForwardingRule")
   @Unwrap
   @Consumes(MediaType.APPLICATION_JSON)
   ListenableFuture<AsyncCreateResponse> createPortForwardingRuleForVirtualMachine(
      @QueryParam("ipaddressid") long ipAddressId, @QueryParam("protocol") String protocol,
      @QueryParam("publicport") int publicPort, @QueryParam("virtualmachineid") long virtualMachineId,
      @QueryParam("privateport") int privatePort);

   /**
    * @see FirewallClient#deletePortForwardingRule
    */
   @GET
   @QueryParams(keys = "command", values = "deletePortForwardingRule")
   @ExceptionParser(ReturnVoidOnNotFoundOr404.class)
   ListenableFuture<Void> deletePortForwardingRule(@QueryParam("id") long id);

}
