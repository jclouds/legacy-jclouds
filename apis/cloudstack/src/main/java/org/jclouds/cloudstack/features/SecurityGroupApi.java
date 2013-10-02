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
import org.jclouds.cloudstack.binders.BindAccountSecurityGroupPairsToIndexedQueryParams;
import org.jclouds.cloudstack.binders.BindCIDRsToCommaDelimitedQueryParam;
import org.jclouds.cloudstack.domain.SecurityGroup;
import org.jclouds.cloudstack.filters.AuthenticationFilter;
import org.jclouds.cloudstack.options.AccountInDomainOptions;
import org.jclouds.cloudstack.options.ListSecurityGroupsOptions;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.OnlyElement;
import org.jclouds.rest.annotations.QueryParams;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.SelectJson;

import com.google.common.collect.Multimap;
/**
 * Provides synchronous access to cloudstack via their REST API.
 * <p/>
 * 
 * @see <a href="http://download.cloud.com/releases/2.2.0/api_2.2.12/TOC_User.html" />
 * @author Adrian Cole
 */
@RequestFilters(AuthenticationFilter.class)
@QueryParams(keys = "response", values = "json")
public interface SecurityGroupApi {

   /**
    * Lists security groups
    * 
    * @param options
    *           if present, how to constrain the list.
    * @return security groups matching query, or empty set, if no security
    *         groups are found
    */
   @Named("listSecurityGroups")
   @GET
   @QueryParams(keys = { "command", "listAll" }, values = { "listSecurityGroups", "true" })
   @SelectJson("securitygroup")
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(EmptySetOnNotFoundOr404.class)
   Set<SecurityGroup> listSecurityGroups(ListSecurityGroupsOptions... options);

   /**
    * get a specific security group by id
    * 
    * @param id
    *           group to get
    * @return security group or null if not found
    */
   @Named("listSecurityGroups")
   @GET
   @QueryParams(keys = { "command", "listAll" }, values = { "listSecurityGroups", "true" })
   @SelectJson("securitygroup")
   @OnlyElement
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(NullOnNotFoundOr404.class)
   SecurityGroup getSecurityGroup(@QueryParam("id") String id);

   /**
    * get a specific security group by name
    * 
    * @param securityGroupName
    *           group to get
    * @return security group or null if not found
    */
   @Named("listSecurityGroups")
   @GET
   @QueryParams(keys = { "command", "listAll" }, values = { "listSecurityGroups", "true" })
   @SelectJson("securitygroup")
   @OnlyElement
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(NullOnNotFoundOr404.class)
   SecurityGroup getSecurityGroupByName(@QueryParam("securitygroupname") String securityGroupName);

   /**
    * Creates a security group
    * 
    * @param name
    *           name of the security group
    * @return security group
    */
   @Named("createSecurityGroup")
   @GET
   @QueryParams(keys = "command", values = "createSecurityGroup")
   @SelectJson("securitygroup")
   @Consumes(MediaType.APPLICATION_JSON)
   SecurityGroup createSecurityGroup(@QueryParam("name") String name);

   /**
    * Authorizes a particular TCP or UDP ingress rule for this security group
    * 
    * @param securityGroupId
    *           The ID of the security group
    * @param protocol
    *           tcp or udp
    * @param startPort
    *           start port for this ingress rule
    * @param endPort
    *           end port for this ingress rule
    * @param cidrList
    *           the cidr list associated
    * @return response relating to the creation of this ingress rule
    */
   @Named("authorizeSecurityGroupIngress")
   @GET
   @QueryParams(keys = "command", values = "authorizeSecurityGroupIngress")
   @SelectJson("jobid")
   @Consumes(MediaType.APPLICATION_JSON)
   String authorizeIngressPortsToCIDRs(@QueryParam("securitygroupid") String securityGroupId,
         @QueryParam("protocol") String protocol, @QueryParam("startport") int startPort,
         @QueryParam("endport") int endPort,
         @BinderParam(BindCIDRsToCommaDelimitedQueryParam.class) Iterable<String> cidrList,
         AccountInDomainOptions... options);

   /**
    * Authorizes a particular TCP or UDP ingress rule for this security group
    * 
    * @param securityGroupId
    *           The ID of the security group
    * @param protocol
    *           tcp or udp
    * @param startPort
    *           start port for this ingress rule
    * @param endPort
    *           end port for this ingress rule
    * @param accountToGroup
    *           mapping of account names to security groups you wish to
    *           authorize
    * @return response relating to the creation of this ingress rule
    */
   @Named("authorizeSecurityGroupIngress")
   @GET
   @QueryParams(keys = "command", values = "authorizeSecurityGroupIngress")
   @SelectJson("jobid")
   @Consumes(MediaType.APPLICATION_JSON)
   String authorizeIngressPortsToSecurityGroups(@QueryParam("securitygroupid") String securityGroupId,
         @QueryParam("protocol") String protocol, @QueryParam("startport") int startPort,
         @QueryParam("endport") int endPort,
         @BinderParam(BindAccountSecurityGroupPairsToIndexedQueryParams.class) Multimap<String, String> accountToGroup,
         AccountInDomainOptions... options);

   /**
    * Authorizes a particular ICMP ingress rule for this security group
    * 
    * @param securityGroupId
    *           The ID of the security group
    * @param ICMPCode
    *           type of the icmp message being sent
    * @param ICMPType
    *           error code for this icmp message
    * @param cidrList
    *           the cidr list associated
    * @return response relating to the creation of this ingress rule
    */
   @Named("authorizeSecurityGroupIngress")
   @GET
   @QueryParams(keys = { "command", "protocol" }, values = { "authorizeSecurityGroupIngress", "ICMP" })
   @SelectJson("jobid")
   @Consumes(MediaType.APPLICATION_JSON)
   String authorizeIngressICMPToCIDRs(@QueryParam("securitygroupid") String securityGroupId,
         @QueryParam("icmpcode") int ICMPCode, @QueryParam("icmptype") int ICMPType,
         @BinderParam(BindCIDRsToCommaDelimitedQueryParam.class) Iterable<String> cidrList,
         AccountInDomainOptions... options);

   /**
    * Authorizes a particular ICMP ingress rule for this security group
    * 
    * @param securityGroupId
    *           The ID of the security group
    * @param ICMPCode
    *           type of the icmp message being sent
    * @param ICMPType
    *           error code for this icmp message
    * @param accountToGroup
    *           mapping of account names to security groups you wish to
    *           authorize
    * @return response relating to the creation of this ingress rule
    */
   @Named("authorizeSecurityGroupIngress")
   @GET
   @QueryParams(keys = { "command", "protocol" }, values = { "authorizeSecurityGroupIngress", "ICMP" })
   @SelectJson("jobid")
   @Consumes(MediaType.APPLICATION_JSON)
   String authorizeIngressICMPToSecurityGroups(@QueryParam("securitygroupid") String securityGroupId,
         @QueryParam("icmpcode") int ICMPCode, @QueryParam("icmptype") int ICMPType,
         @BinderParam(BindAccountSecurityGroupPairsToIndexedQueryParams.class) Multimap<String, String> accountToGroup,
         AccountInDomainOptions... options);

   /**
    * Deletes a particular ingress rule from this security group
    * 
    * @param id
    *           The ID of the ingress rule
    * @param options
    *           scope of the rule.
    */
   @Named("revokeSecurityGroupIngress")
   @GET
   @QueryParams(keys = "command", values = "revokeSecurityGroupIngress")
   @Fallback(VoidOnNotFoundOr404.class)
   @SelectJson("jobid")
   @Consumes(MediaType.APPLICATION_JSON)
   String revokeIngressRule(@QueryParam("id") String id, AccountInDomainOptions... options);

   /**
    * delete a specific security group by id
    * 
    * @param id
    *           group to delete
    */
   @Named("deleteSecurityGroup")
   @GET
   @QueryParams(keys = "command", values = "deleteSecurityGroup")
   @Fallback(VoidOnNotFoundOr404.class)
   void deleteSecurityGroup(@QueryParam("id") String id);

}
