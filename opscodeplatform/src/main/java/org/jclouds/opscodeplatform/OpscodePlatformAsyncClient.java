/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
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
 * ====================================================================
 */
package org.jclouds.opscodeplatform;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;

import org.jclouds.chef.ChefAsyncClient;
import org.jclouds.chef.ChefClient;
import org.jclouds.chef.domain.Organization;
import org.jclouds.chef.domain.User;
import org.jclouds.chef.filters.SignedHeaderAuth;
import org.jclouds.chef.functions.OrganizationName;
import org.jclouds.chef.functions.ParseKeyFromJson;
import org.jclouds.chef.functions.ParseOrganizationFromJson;
import org.jclouds.chef.functions.ParseUserFromJson;
import org.jclouds.chef.functions.Username;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.Delegate;
import org.jclouds.rest.annotations.Endpoint;
import org.jclouds.rest.annotations.ExceptionParser;
import org.jclouds.rest.annotations.ParamParser;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.ResponseParser;
import org.jclouds.rest.binders.BindToJsonPayload;
import org.jclouds.rest.functions.ReturnNullOnNotFoundOr404;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * Provides asynchronous access to the Opscode Platform via their REST API.
 * <p/>
 * 
 * @see OpscodePlatformClient
 * @see <a href="TODO: insert URL of provider documentation" />
 * @author Adrian Cole
 */
@Endpoint(OpscodePlatform.class)
@RequestFilters(SignedHeaderAuth.class)
@Consumes(MediaType.APPLICATION_JSON)
public interface OpscodePlatformAsyncClient {
   /**
    * @see ChefCookbooks#listCookbooksInOrg
    */
   @Delegate
   @Path("/organizations/{orgname}")
   ChefAsyncClient getChefClientForOrg(@PathParam("orgname") String orgname);

   /**
    * @see ChefClient#createUser
    */
   @POST
   @Path("/users")
   @ResponseParser(ParseKeyFromJson.class)
   ListenableFuture<String> createUser(
         @BinderParam(BindToJsonPayload.class) User user);

   /**
    * @see ChefClient#updateUser
    */
   @PUT
   @Path("/users/{username}")
   @ResponseParser(ParseUserFromJson.class)
   ListenableFuture<User> updateUser(
         @PathParam("username") @ParamParser(Username.class) @BinderParam(BindToJsonPayload.class) User user);

   /**
    * @see ChefClient#getUser
    */
   @GET
   @Path("/users/{username}")
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   @ResponseParser(ParseUserFromJson.class)
   ListenableFuture<User> getUser(@PathParam("username") String username);

   /**
    * @see ChefClient#deleteUser
    */
   @DELETE
   @Path("/users/{username}")
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   @ResponseParser(ParseUserFromJson.class)
   ListenableFuture<User> deleteUser(@PathParam("username") String username);

   /**
    * @see ChefClient#createOrg
    */
   @POST
   @Path("/organizations")
   @ResponseParser(ParseKeyFromJson.class)
   ListenableFuture<String> createOrg(
         @BinderParam(BindToJsonPayload.class) Organization org);

   /**
    * @see ChefClient#updateOrg
    */
   @PUT
   @Path("/organizations/{orgname}")
   @ResponseParser(ParseOrganizationFromJson.class)
   ListenableFuture<Organization> updateOrg(
         @PathParam("orgname") @ParamParser(OrganizationName.class) @BinderParam(BindToJsonPayload.class) Organization org);

   /**
    * @see ChefClient#getOrg
    */
   @GET
   @Path("/organizations/{orgname}")
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   @ResponseParser(ParseOrganizationFromJson.class)
   ListenableFuture<Organization> getOrg(@PathParam("orgname") String orgname);

   /**
    * @see ChefClient#deleteOrg
    */
   @DELETE
   @Path("/organizations/{orgname}")
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   @ResponseParser(ParseOrganizationFromJson.class)
   ListenableFuture<Organization> deleteOrg(@PathParam("orgname") String orgname);

}
