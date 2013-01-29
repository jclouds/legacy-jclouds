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
package org.jclouds.rackspace.cloudloadbalancers.features;

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
import org.jclouds.rackspace.cloudloadbalancers.domain.AccessRule;
import org.jclouds.rackspace.cloudloadbalancers.domain.AccessRuleWithId;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.SelectJson;
import org.jclouds.rest.annotations.WrapWith;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * Provides asynchronous access to Rackspace Cloud Load Balancers via their REST API.
 * <p/>
 * 
 * @see AccessRuleApi
 * @author Everett Toews
 */
@RequestFilters(AuthenticateRequest.class)
public interface AccessRuleAsyncApi {

   /**
    * @see AccessRuleApi#create(Iterable)
    */
   @Named("accessrule:create")
   @POST
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(VoidOnNotFoundOr404.class)
   @Path("/accesslist")
   ListenableFuture<Void> create(@WrapWith("accessList") Iterable<AccessRule> accessRules);

   /**
    * @see AccessRuleApi#list()
    */
   @Named("accessrule:list")
   @GET
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(EmptyPagedIterableOnNotFoundOr404.class)
   @SelectJson("accessList")
   @Path("/accesslist")
   ListenableFuture<Iterable<AccessRuleWithId>> list();

   /**
    * @see AccessRuleApi#delete(int)
    */
   @Named("accessrule:delete")
   @DELETE
   @Fallback(FalseOnNotFoundOr404.class)
   @Path("/accesslist/{id}")
   @Consumes("*/*")
   ListenableFuture<Boolean> delete(@PathParam("id") int id);

   /**
    * @see AccessRuleApi#delete(Iterable)
    */
   @Named("accessrule:delete")
   @DELETE
   @Fallback(FalseOnNotFoundOr404.class)
   @Path("/accesslist")
   @Consumes("*/*")
   ListenableFuture<Boolean> delete(@QueryParam("id") Iterable<Integer> ids);

   /**
    * @see AccessRuleApi#deleteAll()
    */
   @Named("accessrule:delete")
   @DELETE
   @Fallback(FalseOnNotFoundOr404.class)
   @Path("/accesslist")
   @Consumes("*/*")
   ListenableFuture<Boolean> deleteAll();
}
