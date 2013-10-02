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

/**
 * Provides synchronous access to cloudstack via their REST API.
 * <p/>
 * 
 * @see <a
 *      href="http://download.cloud.com/releases/2.2.0/api_2.2.12/TOC_User.html"
 *      />
 * @author Adrian Cole
 */
@RequestFilters(AuthenticationFilter.class)
@QueryParams(keys = "response", values = "json")
public interface NATApi {

   /**
    * List the ip forwarding rules
    * 
    * @param options
    *           if present, how to constrain the list.
    * @return IPForwardingRules matching query, or empty set, if no
    *         IPForwardingRules are found
    */
   @Named("listIpForwardingRules")
   @GET
   @QueryParams(keys = { "command", "listAll" }, values = { "listIpForwardingRules", "true" })
   @SelectJson("ipforwardingrule")
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(EmptySetOnNotFoundOr404.class)
   Set<IPForwardingRule> listIPForwardingRules(ListIPForwardingRulesOptions... options);

   /**
    * get a specific IPForwardingRule by id
    * 
    * @param id
    *           IPForwardingRule to get
    * @return IPForwardingRule or null if not found
    */
   @Named("listIpForwardingRules")
   @GET
   @QueryParams(keys = { "command", "listAll" }, values = { "listIpForwardingRules", "true" })
   @SelectJson("ipforwardingrule")
   @OnlyElement
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(NullOnNotFoundOr404.class)
   IPForwardingRule getIPForwardingRule(@QueryParam("id") String id);

   /**
    * get a set of IPForwardingRules by ipaddress id
    * 
    * @param id
    *           IPAddress of rule to get
    * @return IPForwardingRule matching query or empty if not found
    */
   @Named("listIpForwardingRules")
   @GET
   @QueryParams(keys = { "command", "listAll" }, values = { "listIpForwardingRules", "true" })
   @SelectJson("ipforwardingrule")
   @Consumes(MediaType.APPLICATION_JSON)
   Set<IPForwardingRule> getIPForwardingRulesForIPAddress(@QueryParam("ipaddressid") String id);

   /**
    * get a set of IPForwardingRules by virtual machine id
    * 
    * @param id
    *           virtual machine of rule to get
    * @return IPForwardingRule matching query or empty set if not found
    */
   @Named("listIpForwardingRules")
   @GET
   @QueryParams(keys = { "command", "listAll" }, values = { "listIpForwardingRules", "true" })
   @SelectJson("ipforwardingrule")
   @Consumes(MediaType.APPLICATION_JSON)
   Set<IPForwardingRule> getIPForwardingRulesForVirtualMachine(@QueryParam("virtualmachineid") String id);

   /**
    * Creates an ip forwarding rule
    * 
    * @param IPAddressId
    *           the public IP address id of the forwarding rule, already
    *           associated via associateIp
    * @param protocol
    *           the protocol for the rule. Valid values are TCP or UDP.
    * @param startPort
    *           the start port for the rule
    * @return response used to track creation
    */
   @Named("createIpForwardingRule")
   @GET
   @QueryParams(keys = "command", values = "createIpForwardingRule")
   @Unwrap
   @Consumes(MediaType.APPLICATION_JSON)
   AsyncCreateResponse createIPForwardingRule(@QueryParam("ipaddressid") String IPAddressId,
         @QueryParam("protocol") String protocol, @QueryParam("startport") int startPort,
         CreateIPForwardingRuleOptions... options);

   @Named("enableStaticNat")
   @GET
   @QueryParams(keys = "command", values = "enableStaticNat")
   @Consumes(MediaType.APPLICATION_JSON)
   void enableStaticNATForVirtualMachine(
         @QueryParam("virtualmachineid") String virtualMachineId, @QueryParam("ipaddressid") String IPAddressId);

   /**
    * Deletes an ip forwarding rule
    * 
    * @param id
    *           the id of the forwarding rule
    */
   @Named("deleteIpForwardingRule")
   @GET
   @QueryParams(keys = "command", values = "deleteIpForwardingRule")
   @SelectJson("jobid")
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(NullOnNotFoundOr404.class)
   String deleteIPForwardingRule(@QueryParam("id") String id);

   /**
    * Disables static rule for given ip address
    * 
    * @param IPAddressId
    *           the public IP address id for which static nat feature is being
    *           disabled
    */
   @Named("disableStaticNat")
   @GET
   @QueryParams(keys = "command", values = "disableStaticNat")
   @SelectJson("jobid")
   @Consumes(MediaType.APPLICATION_JSON)
   String disableStaticNATOnPublicIP(@QueryParam("ipaddressid") String IPAddressId);

}
