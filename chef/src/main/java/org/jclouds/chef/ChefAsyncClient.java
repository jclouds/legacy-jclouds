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
package org.jclouds.chef;

import java.io.File;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;

import org.jclouds.chef.binders.BindClientnameToJsonPayload;
import org.jclouds.chef.binders.BindGenerateKeyForClientToJsonPayload;
import org.jclouds.chef.filters.SignedHeaderAuth;
import org.jclouds.chef.functions.ParseKeyFromJson;
import org.jclouds.chef.functions.ParseKeySetFromJson;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.Endpoint;
import org.jclouds.rest.annotations.ExceptionParser;
import org.jclouds.rest.annotations.PartParam;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.ResponseParser;
import org.jclouds.rest.functions.ReturnFalseOnNotFoundOr404;
import org.jclouds.rest.functions.ReturnVoidOnNotFoundOr404;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * Provides asynchronous access to Chef via their REST API.
 * <p/>
 * 
 * @see ChefClient
 * @see <a href="TODO: insert URL of provider documentation" />
 * @author Adrian Cole
 */
@Endpoint(Chef.class)
@RequestFilters(SignedHeaderAuth.class)
@Consumes(MediaType.APPLICATION_JSON)
public interface ChefAsyncClient {
   /**
    * @see ChefCookbooks#listCookbooks
    */
   @GET
   @Path("cookbooks")
   ListenableFuture<String> listCookbooks();

   /**
    * @see ChefClient#createCookbook(String,File)
    */
   @POST
   @Path("cookbooks")
   ListenableFuture<String> createCookbook(@FormParam("name") String name,
            @PartParam(name = "file", contentType = MediaType.APPLICATION_OCTET_STREAM) File content);

   /**
    * @see ChefClient#createCookbook(String,byte[])
    */
   @POST
   @Path("cookbooks")
   ListenableFuture<String> createCookbook(@FormParam("name") String name,
            @PartParam(name = "file", contentType = MediaType.APPLICATION_OCTET_STREAM) byte[] content);

   /**
    * @see ChefClient#createClient
    */
   @POST
   @Path("clients")
   @ResponseParser(ParseKeyFromJson.class)
   ListenableFuture<String> createClient(
            @BinderParam(BindClientnameToJsonPayload.class) String clientname);

   /**
    * @see ChefClient#generateKeyForClient
    */
   @PUT
   @Path("clients/{clientname}")
   @ResponseParser(ParseKeyFromJson.class)
   ListenableFuture<String> generateKeyForClient(
            @PathParam("clientname") @BinderParam(BindGenerateKeyForClientToJsonPayload.class) String clientname);

   /**
    * @see ChefClient#clientExists
    */
   @HEAD
   @Path("clients/{clientname}")
   @ExceptionParser(ReturnFalseOnNotFoundOr404.class)
   ListenableFuture<Boolean> clientExists(@PathParam("clientname") String clientname);

   /**
    * @see ChefClient#deleteClient
    */
   @DELETE
   @Path("clients/{clientname}")
   @ExceptionParser(ReturnVoidOnNotFoundOr404.class)
   ListenableFuture<Void> deleteClient(@PathParam("clientname") String clientname);

   /**
    * @see ChefClient#listClients
    */
   @GET
   @Path("clients")
   @ResponseParser(ParseKeySetFromJson.class)
   ListenableFuture<Set<String>> listClients();

}
