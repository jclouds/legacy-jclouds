/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */

package org.jclouds.opscodeplatform;

import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;

import org.jclouds.chef.ChefAsyncClient;
import org.jclouds.chef.ChefClient;
import org.jclouds.chef.filters.SignedHeaderAuth;
import org.jclouds.chef.functions.ParseKeySetFromJson;
import org.jclouds.opscodeplatform.config.OrganizationName;
import org.jclouds.opscodeplatform.config.Username;
import org.jclouds.opscodeplatform.domain.Organization;
import org.jclouds.opscodeplatform.domain.User;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.ExceptionParser;
import org.jclouds.rest.annotations.Headers;
import org.jclouds.rest.annotations.ParamParser;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.ResponseParser;
import org.jclouds.rest.binders.BindToJsonPayload;
import org.jclouds.rest.functions.ReturnEmptySetOnNotFoundOr404;
import org.jclouds.rest.functions.ReturnFalseOnNotFoundOr404;
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
@RequestFilters(SignedHeaderAuth.class)
@Consumes(MediaType.APPLICATION_JSON)
@Headers(keys = "X-Chef-Version", values = ChefAsyncClient.VERSION)
public interface OpscodePlatformAsyncClient {

   /**
    * @see ChefUser#listUsers
    */
   @GET
   @Path("/users")
   @ResponseParser(ParseKeySetFromJson.class)
   @ExceptionParser(ReturnEmptySetOnNotFoundOr404.class)
   ListenableFuture<Set<String>> listUsers();

   /**
    * @see ChefRole#userExists
    */
   @HEAD
   @Path("/users/{username}")
   @ExceptionParser(ReturnFalseOnNotFoundOr404.class)
   ListenableFuture<Boolean> userExists(@PathParam("username") String username);

   /**
    * @see ChefClient#createUser
    */
   @POST
   @Path("/users")
   ListenableFuture<User> createUser(@BinderParam(BindToJsonPayload.class) User user);

   /**
    * @see ChefClient#updateUser
    */
   @PUT
   @Path("/users/{username}")
   @Consumes(MediaType.APPLICATION_JSON)
   ListenableFuture<User> updateUser(
         @PathParam("username") @ParamParser(Username.class) @BinderParam(BindToJsonPayload.class) User user);

   /**
    * @see ChefClient#getUser
    */
   @GET
   @Path("/users/{username}")
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   @Consumes(MediaType.APPLICATION_JSON)
   ListenableFuture<User> getUser(@PathParam("username") String username);

   /**
    * @see ChefClient#deleteUser
    */
   @DELETE
   @Path("/users/{username}")
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   @Consumes(MediaType.APPLICATION_JSON)
   ListenableFuture<User> deleteUser(@PathParam("username") String username);

   /**
    * @see ChefOrganization#listOrganizations
    */
   @GET
   @Path("/organizations")
   @ResponseParser(ParseKeySetFromJson.class)
   @ExceptionParser(ReturnEmptySetOnNotFoundOr404.class)
   ListenableFuture<Set<String>> listOrganizations();

   /**
    * @see ChefRole#organizationExists
    */
   @HEAD
   @Path("/organizations/{organizationname}")
   @ExceptionParser(ReturnFalseOnNotFoundOr404.class)
   ListenableFuture<Boolean> organizationExists(@PathParam("organizationname") String organizationname);

   /**
    * @see ChefClient#createOrganization
    */
   @POST
   @Path("/organizations")
   ListenableFuture<Organization> createOrganization(@BinderParam(BindToJsonPayload.class) Organization org);

   /**
    * @see ChefClient#updateOrganization
    */
   @PUT
   @Path("/organizations/{orgname}")
   @Consumes(MediaType.APPLICATION_JSON)
   ListenableFuture<Organization> updateOrganization(
         @PathParam("orgname") @ParamParser(OrganizationName.class) @BinderParam(BindToJsonPayload.class) Organization org);

   /**
    * @see ChefClient#getOrganization
    */
   @GET
   @Path("/organizations/{orgname}")
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   @Consumes(MediaType.APPLICATION_JSON)
   ListenableFuture<Organization> getOrganization(@PathParam("orgname") String orgname);

   /**
    * @see ChefClient#deleteOrganization
    */
   @DELETE
   @Path("/organizations/{orgname}")
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   @Consumes(MediaType.APPLICATION_JSON)
   ListenableFuture<Organization> deleteOrganization(@PathParam("orgname") String orgname);

}
