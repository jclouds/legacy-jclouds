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
package org.jclouds.openstack.nova.v2_0.extensions;

import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.jclouds.openstack.keystone.v2_0.filters.AuthenticateRequest;
import org.jclouds.openstack.nova.v2_0.binders.BindSecurityGroupRuleToJsonPayload;
import org.jclouds.openstack.nova.v2_0.domain.Ingress;
import org.jclouds.openstack.nova.v2_0.domain.SecurityGroup;
import org.jclouds.openstack.nova.v2_0.domain.SecurityGroupRule;
import org.jclouds.openstack.v2_0.ServiceType;
import org.jclouds.openstack.v2_0.services.Extension;
import org.jclouds.rest.annotations.ExceptionParser;
import org.jclouds.rest.annotations.MapBinder;
import org.jclouds.rest.annotations.Payload;
import org.jclouds.rest.annotations.PayloadParam;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.SelectJson;
import org.jclouds.rest.annotations.SkipEncoding;
import org.jclouds.rest.functions.ReturnEmptySetOnNotFoundOr404;
import org.jclouds.rest.functions.ReturnFalseOnNotFoundOr404;
import org.jclouds.rest.functions.ReturnNullOnNotFoundOr404;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * Provides asynchronous access to Security Groups via the REST API.
 * <p/>
 * 
 * @see SecurityGroupClient
 * @author Jeremy Daggett
 * @see <a href= "http://docs.openstack.org/api/openstack-compute/2/content/Extensions-d1e1444.html"
 *      />
 * @see <a href="http://nova.openstack.org/api_ext" />
 * @see <a href="http://wiki.openstack.org/os-security-groups" />
 */
@Extension(of = ServiceType.COMPUTE, namespace = ExtensionNamespaces.SECURITY_GROUPS)
@SkipEncoding( { '/', '=' })
@RequestFilters(AuthenticateRequest.class)
public interface SecurityGroupAsyncClient {

   /**
    * @see SecurityGroupClient#listSecurityGroups
    */
   @GET
   @SelectJson("security_groups")
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("/os-security-groups")
   @ExceptionParser(ReturnEmptySetOnNotFoundOr404.class)
   ListenableFuture<Set<SecurityGroup>> listSecurityGroups();

   /**
    * @see SecurityGroupClient#getSecurityGroup
    */
   @GET
   @Path("/os-security-groups/{id}")
   @SelectJson("security_group")
   @Consumes(MediaType.APPLICATION_JSON)
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<SecurityGroup> getSecurityGroup(@PathParam("id") String id);

   /**
    * @see SecurityGroupClient#createSecurityGroupWithNameAndDescription
    */
   @POST
   @Path("/os-security-groups")
   @SelectJson("security_group")
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   @Payload("%7B\"security_group\":%7B\"name\":\"{name}\",\"description\":\"{description}\"%7D%7D")
   ListenableFuture<SecurityGroup> createSecurityGroupWithNameAndDescription(@PayloadParam("name") String name,
            @PayloadParam("description") String description);

   /**
    * @see SecurityGroupClient#deleteSecurityGroup
    */
   @DELETE
   @Path("/os-security-groups/{id}")
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   @Consumes(MediaType.APPLICATION_JSON)
   ListenableFuture<Boolean> deleteSecurityGroup(@PathParam("id") String id);

   /**
    * @see SecurityGroupClient#createSecurityGroupRuleAllowingCidrBlock
    */
   @POST
   @Path("/os-security-group-rules")
   @SelectJson("security_group_rule")
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   @MapBinder(BindSecurityGroupRuleToJsonPayload.class)
   ListenableFuture<SecurityGroupRule> createSecurityGroupRuleAllowingCidrBlock(
            @PayloadParam("parent_group_id") String parent_group_id, Ingress ip_protocol,
            @PayloadParam("cidr") String cidr);

   /**
    * @see SecurityGroupClient#createRuleOnSecurityGroupToCidrBlock
    */
   @POST
   @Path("/os-security-group-rules")
   @SelectJson("security_group_rule")
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   @MapBinder(BindSecurityGroupRuleToJsonPayload.class)
   ListenableFuture<SecurityGroupRule> createSecurityGroupRuleAllowingSecurityGroupId(
            @PayloadParam("parent_group_id") String parent_group_id, Ingress ip_protocol,
            @PayloadParam("group_id") String group_id);

   /**
    * @see SecurityGroupClient#deleteSecurityGroupRule
    */
   @DELETE
   @Path("/os-security-group-rules/{security_group_rule_ID}")
   @ExceptionParser(ReturnFalseOnNotFoundOr404.class)
   @Consumes
   ListenableFuture<Boolean> deleteSecurityGroupRule(@PathParam("security_group_rule_ID") String security_group_rule_ID);

}
