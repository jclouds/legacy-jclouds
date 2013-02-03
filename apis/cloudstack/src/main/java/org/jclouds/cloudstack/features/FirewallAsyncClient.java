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
import org.jclouds.Fallbacks.VoidOnNotFoundOr404;
import org.jclouds.cloudstack.domain.AsyncCreateResponse;
import org.jclouds.cloudstack.domain.FirewallRule;
import org.jclouds.cloudstack.domain.PortForwardingRule;
import org.jclouds.cloudstack.filters.AuthenticationFilter;
import org.jclouds.cloudstack.options.CreateFirewallRuleOptions;
import org.jclouds.cloudstack.options.ListFirewallRulesOptions;
import org.jclouds.cloudstack.options.ListPortForwardingRulesOptions;
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
 * @author Adrian Cole
 * @see FirewallClient
 * @see <a href="http://download.cloud.com/releases/2.2.0/api_2.2.12/TOC_User.html" />
 */
@RequestFilters(AuthenticationFilter.class)
@QueryParams(keys = "response", values = "json")
public interface FirewallAsyncClient {

   /**
    * @see FirewallClient#listFirewallRules
    */
   @Named("listFirewallRules")
   @GET
   @QueryParams(keys = { "command", "listAll" }, values = { "listFirewallRules", "true" })
   @SelectJson("firewallrule")
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(EmptySetOnNotFoundOr404.class)
   ListenableFuture<Set<FirewallRule>> listFirewallRules(ListFirewallRulesOptions... options);

   /**
    * @see FirewallClient#getFirewallRule
    */
   @Named("listFirewallRules")
   @GET
   @QueryParams(keys = { "command", "listAll" }, values = { "listFirewallRules", "true" })
   @SelectJson("firewallrule")
   @OnlyElement
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(NullOnNotFoundOr404.class)
   ListenableFuture<FirewallRule> getFirewallRule(@QueryParam("id") String id);

   /**
    * @see FirewallClient#createFirewallRuleForIpAndProtocol
    */
   @Named("createFirewallRule")
   @GET
   @QueryParams(keys = "command", values = "createFirewallRule")
   @Unwrap
   @Consumes(MediaType.APPLICATION_JSON)
   ListenableFuture<AsyncCreateResponse> createFirewallRuleForIpAndProtocol(@QueryParam("ipaddressid") String ipAddressId,
         @QueryParam("protocol") FirewallRule.Protocol protocol, CreateFirewallRuleOptions... options);

   /**
    * @see FirewallClient#createFirewallRuleForIpProtocolAndPort
    */
   @Named("createFirewallRule")
   @GET
   @QueryParams(keys = "command", values = "createFirewallRule")
   @Unwrap
   @Consumes(MediaType.APPLICATION_JSON)
   ListenableFuture<AsyncCreateResponse> createFirewallRuleForIpProtocolAndPort(@QueryParam("ipaddressid") String ipAddressId,
                                                                            @QueryParam("protocol") FirewallRule.Protocol protocol,
                                                                            @QueryParam("startPort") int startPort,
                                                                                @QueryParam("endPort") int endPort);
                                                                            

   /**
    * @see FirewallClient#deleteFirewallRule
    */
   @Named("deleteFirewallRule")
   @GET
   @QueryParams(keys = "command", values = "deleteFirewallRule")
   @Fallback(VoidOnNotFoundOr404.class)
   ListenableFuture<Void> deleteFirewallRule(@QueryParam("id") String id);

   /**
    * @see FirewallClient#listPortForwardingRules
    */
   @Named("listPortForwardingRules")
   @GET
   @QueryParams(keys = { "command", "listAll" }, values = { "listPortForwardingRules", "true" })
   @SelectJson("portforwardingrule")
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(EmptySetOnNotFoundOr404.class)
   ListenableFuture<Set<PortForwardingRule>> listPortForwardingRules(ListPortForwardingRulesOptions... options);

   /**
    * @see FirewallClient#getPortForwardingRule
    */
   @Named("listPortForwardingRules")
   @GET
   @QueryParams(keys = { "command", "listAll" }, values = { "listPortForwardingRules", "true" })
   @SelectJson("portforwardingrule")
   @OnlyElement
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(NullOnNotFoundOr404.class)
   ListenableFuture<PortForwardingRule> getPortForwardingRule(@QueryParam("id") String id);

   /**
    * @see FirewallClient#createPortForwardingRuleForVirtualMachine
    */
   @Named("createPortForwardingRule")
   @GET
   @QueryParams(keys = "command", values = "createPortForwardingRule")
   @Unwrap
   @Consumes(MediaType.APPLICATION_JSON)
   ListenableFuture<AsyncCreateResponse> createPortForwardingRuleForVirtualMachine(
      @QueryParam("ipaddressid") String ipAddressId, @QueryParam("protocol") PortForwardingRule.Protocol protocol,
      @QueryParam("publicport") int publicPort, @QueryParam("virtualmachineid") String virtualMachineId,
      @QueryParam("privateport") int privatePort);

   /**
    * @see FirewallClient#deletePortForwardingRule
    */
   @Named("deletePortForwardingRule")
   @GET
   @QueryParams(keys = "command", values = "deletePortForwardingRule")
   @Fallback(VoidOnNotFoundOr404.class)
   ListenableFuture<Void> deletePortForwardingRule(@QueryParam("id") String id);

}
