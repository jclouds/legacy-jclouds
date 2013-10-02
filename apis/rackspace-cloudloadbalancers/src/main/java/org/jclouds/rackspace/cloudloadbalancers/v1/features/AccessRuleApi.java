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
package org.jclouds.rackspace.cloudloadbalancers.v1.features;

import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.jclouds.Fallbacks.EmptyPagedIterableOnNotFoundOr404;
import org.jclouds.Fallbacks.FalseOnNotFoundOr404;
import org.jclouds.Fallbacks.VoidOnNotFoundOr404;
import org.jclouds.openstack.keystone.v2_0.filters.AuthenticateRequest;
import org.jclouds.rackspace.cloudloadbalancers.v1.domain.AccessRule;
import org.jclouds.rackspace.cloudloadbalancers.v1.domain.AccessRuleWithId;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.SelectJson;
import org.jclouds.rest.annotations.WrapWith;

/**
 * The access list management feature allows fine-grained network access controls to be applied to the load balancer's
 * virtual IP address.
 * <p/>
 * 
 * @author Everett Toews
 */
@RequestFilters(AuthenticateRequest.class)
public interface AccessRuleApi {
   /**
    * Create new access rules or append to existing access rules.
    * 
    * When creating access rules, one or more AccessRules are required. If populated access rules already exist 
    * for the load balancer, it will be appended to with subsequent creates. One access list may include up to 100 
    * AccessRules. A single address or subnet definition is considered unique and cannot be duplicated between rules
    * in an access list.
    */
   @Named("accessrule:create")
   @POST
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(VoidOnNotFoundOr404.class)
   @Path("/accesslist")
   void create(@WrapWith("accessList") Iterable<AccessRule> accessRules);

   /**
    * List the AccessRules.
    */
   @Named("accessrule:list")
   @GET
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(EmptyPagedIterableOnNotFoundOr404.class)
   @SelectJson("accessList")
   @Path("/accesslist")
   Iterable<AccessRuleWithId> list();
   
   /**
    * Delete an access rule from the access list.
    * 
    * @return true on a successful delete, false if the access rule was not found
    */
   @Named("accessrule:delete")
   @DELETE
   @Fallback(FalseOnNotFoundOr404.class)
   @Path("/accesslist/{id}")
   @Consumes("*/*")
   boolean delete(@PathParam("id") int id);
   
   /**
    * Batch delete the access rules given the specified ids.
    * 
    * @return true on a successful delete, false if the access rule was not found
    */
   @Named("accessrule:delete")
   @DELETE
   @Fallback(FalseOnNotFoundOr404.class)
   @Path("/accesslist")
   @Consumes("*/*")
   boolean delete(@QueryParam("id") Iterable<Integer> ids);
   
   /**
    * Delete the entire access list.
    * 
    * @return true on a successful delete, false if the access rule was not found
    */
   @Named("accessrule:delete")
   @DELETE
   @Fallback(FalseOnNotFoundOr404.class)
   @Path("/accesslist")
   @Consumes("*/*")
   boolean deleteAll();
}
