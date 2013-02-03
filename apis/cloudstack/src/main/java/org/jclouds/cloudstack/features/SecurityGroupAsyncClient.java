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
import com.google.common.util.concurrent.ListenableFuture;

/**
 * Provides asynchronous access to cloudstack via their REST API.
 * <p/>
 * 
 * @see OfferingClient
 * @see <a href="http://download.cloud.com/releases/2.2.0/api_2.2.12/TOC_User.html" />
 * @author Adrian Cole
 */
@RequestFilters(AuthenticationFilter.class)
@QueryParams(keys = "response", values = "json")
public interface SecurityGroupAsyncClient {

   /**
    * @see SecurityGroupClient#listSecurityGroups
    */
   @Named("listSecurityGroups")
   @GET
   @QueryParams(keys = { "command", "listAll" }, values = { "listSecurityGroups", "true" })
   @SelectJson("securitygroup")
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(EmptySetOnNotFoundOr404.class)
   ListenableFuture<Set<SecurityGroup>> listSecurityGroups(ListSecurityGroupsOptions... options);

   /**
    * @see SecurityGroupClient#getSecurityGroup
    */
   @Named("listSecurityGroups")
   @GET
   @QueryParams(keys = { "command", "listAll" }, values = { "listSecurityGroups", "true" })
   @SelectJson("securitygroup")
   @OnlyElement
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(NullOnNotFoundOr404.class)
   ListenableFuture<SecurityGroup> getSecurityGroup(@QueryParam("id") String id);

   /**
    * @see SecurityGroupClient#createSecurityGroup
    */
   @Named("createSecurityGroup")
   @GET
   @QueryParams(keys = "command", values = "createSecurityGroup")
   @SelectJson("securitygroup")
   @Consumes(MediaType.APPLICATION_JSON)
   ListenableFuture<SecurityGroup> createSecurityGroup(@QueryParam("name") String name);

   /**
    * @see SecurityGroupClient#authorizeIngressPortsToCIDRs
    */
   @Named("authorizeSecurityGroupIngress")
   @GET
   @QueryParams(keys = "command", values = "authorizeSecurityGroupIngress")
   @SelectJson("jobid")
   @Consumes(MediaType.APPLICATION_JSON)
   ListenableFuture<String> authorizeIngressPortsToCIDRs(@QueryParam("securitygroupid") String securityGroupId,
         @QueryParam("protocol") String protocol, @QueryParam("startport") int startPort,
         @QueryParam("endport") int endPort,
         @BinderParam(BindCIDRsToCommaDelimitedQueryParam.class) Iterable<String> cidrList,
         AccountInDomainOptions... options);

   /**
    * @see SecurityGroupClient#authorizeIngressPortsToSecurityGroups
    */
   @Named("authorizeSecurityGroupIngress")
   @GET
   @QueryParams(keys = "command", values = "authorizeSecurityGroupIngress")
   @SelectJson("jobid")
   @Consumes(MediaType.APPLICATION_JSON)
   ListenableFuture<String> authorizeIngressPortsToSecurityGroups(@QueryParam("securitygroupid") String securityGroupId,
         @QueryParam("protocol") String protocol, @QueryParam("startport") int startPort,
         @QueryParam("endport") int endPort,
         @BinderParam(BindAccountSecurityGroupPairsToIndexedQueryParams.class) Multimap<String, String> accountToGroup,
         AccountInDomainOptions... options);

   /**
    * @see SecurityGroupClient#authorizeIngressICMPToCIDRs
    */
   @Named("authorizeSecurityGroupIngress")
   @GET
   @QueryParams(keys = { "command", "protocol" }, values = { "authorizeSecurityGroupIngress", "ICMP" })
   @SelectJson("jobid")
   @Consumes(MediaType.APPLICATION_JSON)
   ListenableFuture<String> authorizeIngressICMPToCIDRs(@QueryParam("securitygroupid") String securityGroupId,
         @QueryParam("icmpcode") int ICMPCode, @QueryParam("icmptype") int ICMPType,
         @BinderParam(BindCIDRsToCommaDelimitedQueryParam.class) Iterable<String> cidrList,
         AccountInDomainOptions... options);

   /**
    * @see SecurityGroupClient#authorizeIngressICMPToSecurityGroups
    */
   @Named("authorizeSecurityGroupIngress")
   @GET
   @QueryParams(keys = { "command", "protocol" }, values = { "authorizeSecurityGroupIngress", "ICMP" })
   @SelectJson("jobid")
   @Consumes(MediaType.APPLICATION_JSON)
   ListenableFuture<String> authorizeIngressICMPToSecurityGroups(@QueryParam("securitygroupid") String securityGroupId,
         @QueryParam("icmpcode") int ICMPCode, @QueryParam("icmptype") int ICMPType,
         @BinderParam(BindAccountSecurityGroupPairsToIndexedQueryParams.class) Multimap<String, String> accountToGroup,
         AccountInDomainOptions... options);

   /**
    * @see SecurityGroupClient#revokeIngressRule
    */
   @Named("revokeSecurityGroupIngress")
   @GET
   @QueryParams(keys = "command", values = "revokeSecurityGroupIngress")
   @Fallback(VoidOnNotFoundOr404.class)
   @SelectJson("jobid")
   @Consumes(MediaType.APPLICATION_JSON)
   ListenableFuture<String> revokeIngressRule(@QueryParam("id") String id, AccountInDomainOptions... options);

   /**
    * @see SecurityGroupClient#deleteSecurityGroup
    */
   @Named("deleteSecurityGroup")
   @GET
   @QueryParams(keys = "command", values = "deleteSecurityGroup")
   @Fallback(VoidOnNotFoundOr404.class)
   ListenableFuture<Void> deleteSecurityGroup(@QueryParam("id") String id);

}
