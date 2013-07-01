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

/**
 * Provides synchronous access to cloudstack via their REST API.
 * <p/>
 *
 * @author Adrian Cole
 * @see <a href="http://download.cloud.com/releases/2.2.0/api_2.2.12/TOC_User.html" />
 */
@RequestFilters(AuthenticationFilter.class)
@QueryParams(keys = "response", values = "json")
public interface FirewallApi {

   /**
    * @see FirewallApi#listFirewallRules
    */
   @Named("listFirewallRules")
   @GET
   @QueryParams(keys = { "command", "listAll" }, values = { "listFirewallRules", "true" })
   @SelectJson("firewallrule")
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(EmptySetOnNotFoundOr404.class)
   Set<FirewallRule> listFirewallRules(ListFirewallRulesOptions... options);

   /**
    * @see FirewallApi#getFirewallRule
    */
   @Named("listFirewallRules")
   @GET
   @QueryParams(keys = { "command", "listAll" }, values = { "listFirewallRules", "true" })
   @SelectJson("firewallrule")
   @OnlyElement
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(NullOnNotFoundOr404.class)
   FirewallRule getFirewallRule(@QueryParam("id") String id);

   /**
    * @see FirewallApi#createFirewallRuleForIpAndProtocol
    */
   @Named("createFirewallRule")
   @GET
   @QueryParams(keys = "command", values = "createFirewallRule")
   @Unwrap
   @Consumes(MediaType.APPLICATION_JSON)
   AsyncCreateResponse createFirewallRuleForIpAndProtocol(@QueryParam("ipaddressid") String ipAddressId,
         @QueryParam("protocol") FirewallRule.Protocol protocol, CreateFirewallRuleOptions... options);

   /**
    * @see FirewallApi#createFirewallRuleForIpProtocolAndPort
    */
   @Named("createFirewallRule")
   @GET
   @QueryParams(keys = "command", values = "createFirewallRule")
   @Unwrap
   @Consumes(MediaType.APPLICATION_JSON)
   AsyncCreateResponse createFirewallRuleForIpProtocolAndPort(@QueryParam("ipaddressid") String ipAddressId,
                                                                            @QueryParam("protocol") FirewallRule.Protocol protocol,
                                                                            @QueryParam("startPort") int startPort,
                                                                                @QueryParam("endPort") int endPort);
                                                                            

   /**
    * @see FirewallApi#deleteFirewallRule
    */
   @Named("deleteFirewallRule")
   @GET
   @QueryParams(keys = "command", values = "deleteFirewallRule")
   @Fallback(VoidOnNotFoundOr404.class)
   void deleteFirewallRule(@QueryParam("id") String id);

   /**
    * @see FirewallApi#listPortForwardingRules
    */
   @Named("listPortForwardingRules")
   @GET
   @QueryParams(keys = { "command", "listAll" }, values = { "listPortForwardingRules", "true" })
   @SelectJson("portforwardingrule")
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(EmptySetOnNotFoundOr404.class)
   Set<PortForwardingRule> listPortForwardingRules(ListPortForwardingRulesOptions... options);

   /**
    * @see FirewallApi#getPortForwardingRule
    */
   @Named("listPortForwardingRules")
   @GET
   @QueryParams(keys = { "command", "listAll" }, values = { "listPortForwardingRules", "true" })
   @SelectJson("portforwardingrule")
   @OnlyElement
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(NullOnNotFoundOr404.class)
   PortForwardingRule getPortForwardingRule(@QueryParam("id") String id);

   /**
    * @see FirewallApi#createPortForwardingRuleForVirtualMachine
    */
   @Named("createPortForwardingRule")
   @GET
   @QueryParams(keys = "command", values = "createPortForwardingRule")
   @Unwrap
   @Consumes(MediaType.APPLICATION_JSON)
   AsyncCreateResponse createPortForwardingRuleForVirtualMachine(
      @QueryParam("ipaddressid") String ipAddressId, @QueryParam("protocol") PortForwardingRule.Protocol protocol,
      @QueryParam("publicport") int publicPort, @QueryParam("virtualmachineid") String virtualMachineId,
      @QueryParam("privateport") int privatePort);

   /**
    * @see FirewallApi#deletePortForwardingRule
    */
   @Named("deletePortForwardingRule")
   @GET
   @QueryParams(keys = "command", values = "deletePortForwardingRule")
   @Fallback(VoidOnNotFoundOr404.class)
   void deletePortForwardingRule(@QueryParam("id") String id);

}
