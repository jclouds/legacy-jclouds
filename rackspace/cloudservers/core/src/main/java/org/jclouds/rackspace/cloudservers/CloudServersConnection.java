/**
 *
 * Copyright (C) 2009 Global Cloud Specialists, Inc. <info@globalcloudspecialists.com>
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
package org.jclouds.rackspace.cloudservers;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.jclouds.http.functions.ReturnFalseOn404;
import org.jclouds.rackspace.cloudservers.domain.Flavor;
import org.jclouds.rackspace.cloudservers.domain.Image;
import org.jclouds.rackspace.cloudservers.domain.Server;
import org.jclouds.rackspace.cloudservers.functions.ParseFlavorFromGsonResponse;
import org.jclouds.rackspace.cloudservers.functions.ParseFlavorListFromGsonResponse;
import org.jclouds.rackspace.cloudservers.functions.ParseImageFromGsonResponse;
import org.jclouds.rackspace.cloudservers.functions.ParseImageListFromGsonResponse;
import org.jclouds.rackspace.cloudservers.functions.ParseServerFromGsonResponse;
import org.jclouds.rackspace.cloudservers.functions.ParseServerListFromGsonResponse;
import org.jclouds.rackspace.cloudservers.functions.ReturnFlavorNotFoundOn404;
import org.jclouds.rackspace.cloudservers.functions.ReturnImageNotFoundOn404;
import org.jclouds.rackspace.cloudservers.functions.ReturnServerNotFoundOn404;
import org.jclouds.rackspace.cloudservers.options.CreateServerOptions;
import org.jclouds.rackspace.filters.AuthenticateRequest;
import org.jclouds.rest.ExceptionParser;
import org.jclouds.rest.PostBinder;
import org.jclouds.rest.PostParam;
import org.jclouds.rest.Query;
import org.jclouds.rest.RequestFilters;
import org.jclouds.rest.ResponseParser;
import org.jclouds.rest.SkipEncoding;

/**
 * Provides access to Cloud Servers via their REST API.
 * <p/>
 * All commands return a Future of the result from Cloud Servers. Any exceptions incurred during
 * processing will be wrapped in an {@link ExecutionException} as documented in {@link Future#get()}.
 * 
 * @see <a href="http://docs.rackspacecloud.com/servers/api/cs-devguide-latest.pdf" />
 * @author Adrian Cole
 */
@SkipEncoding('/')
@RequestFilters(AuthenticateRequest.class)
public interface CloudServersConnection {

   /**
    * 
    * List all servers (IDs and names only)
    * 
    * @see #listServerDetails()
    */
   @GET
   @ResponseParser(ParseServerListFromGsonResponse.class)
   @Query(key = "format", value = "json")
   @Path("/servers")
   // TODO: Error Response Code(s): cloudServersFault (400, 500), serviceUnavailable (503),
   // unauthorized (401), badRequest (400), overLimit (413)
   List<Server> listServers();

   /**
    * This operation provides a list of servers associated with your account. Servers that have been
    * deleted are not included in this list.
    */
   @GET
   @ResponseParser(ParseServerListFromGsonResponse.class)
   @Query(key = "format", value = "json")
   @Path("/servers/detail")
   // TODO: Error Response Code(s): cloudServersFault (400, 500), serviceUnavailable (503),
   // unauthorized (401), badRequest (400), overLimit (413)
   List<Server> listServerDetails();

   /**
    * 
    * This operation returns details of the specified server.
    * 
    * @see Server
    */
   @GET
   @ResponseParser(ParseServerFromGsonResponse.class)
   @Query(key = "format", value = "json")
   @ExceptionParser(ReturnServerNotFoundOn404.class)
   @Path("/servers/{id}")
   // TODO: cloudServersFault (400, 500), serviceUnavailable (503), unauthorized (401), badRequest
   // (400)
   Server getServerDetails(@PathParam("id") int id);

   /**
    * 
    * This operation deletes a cloud server instance from the system.
    * <p/>
    * Note: When a server is deleted, all images created from that server are also removed.
    * 
    * 
    * @see Server
    */
   @DELETE
   @ExceptionParser(ReturnFalseOn404.class)
   @Path("/servers/{id}")
   // TODO:cloudServersFault (400, 500), serviceUnavailable (503), unauthorized (401), badRequest
   // (400), itemNotFound (404), buildInProgress (409), overLimit (413)
   boolean deleteServer(@PathParam("id") int id);

   /**
    * This operation asynchronously provisions a new server. The progress of this operation depends
    * on several factors including location of the requested image, network i/o, host load, and the
    * selected flavor. The progress of the request can be checked by performing a GET on /server/id,
    * which will return a progress attribute (0-100% completion). A password will be randomly
    * generated for you and returned in the response object. For security reasons, it will not be
    * returned in subsequent GET calls against a given server ID.
    */
   @POST
   @ResponseParser(ParseServerFromGsonResponse.class)
   @Query(key = "format", value = "json")
   @Path("/servers")
   @PostBinder(CreateServerOptions.class)
   // TODO:cloudServersFault (400, 500), serviceUnavailable (503), unauthorized (401),
   // badMediaType(415), badRequest (400), serverCapacityUnavailable (503), overLimit (413)
   Server createServer(@PostParam("name") String name, @PostParam("imageId") int imageId,
            @PostParam("flavorId") int flavorId);

   /**
    * 
    * List available flavors (IDs and names only)
    * 
    * @see #listFlavorDetails()
    */
   @GET
   @ResponseParser(ParseFlavorListFromGsonResponse.class)
   @Query(key = "format", value = "json")
   @Path("/flavors")
   // TODO: cloudServersFault (400, 500), serviceUnavailable (503), unauthorized (401), badRequest
   // (400)
   List<Flavor> listFlavors();

   /**
    * 
    * List available flavors (all details)
    * 
    * @see Flavor
    */
   @GET
   @ResponseParser(ParseFlavorListFromGsonResponse.class)
   @Query(key = "format", value = "json")
   @Path("/flavors/detail")
   // TODO: cloudServersFault (400, 500), serviceUnavailable (503), unauthorized (401), badRequest
   // (400)
   List<Flavor> listFlavorDetails();

   /**
    * 
    * List available images (IDs and names only)
    * 
    * @see #listImageDetails()
    */
   @GET
   @ResponseParser(ParseImageListFromGsonResponse.class)
   @Query(key = "format", value = "json")
   @Path("/images")
   // TODO: cloudServersFault (400, 500), serviceUnavailable (503), unauthorized (401), badRequest
   // (400)
   List<Image> listImages();

   /**
    * 
    * This operation will list all images visible by the account.
    * 
    * @see Image
    */
   @GET
   @ResponseParser(ParseImageListFromGsonResponse.class)
   @Query(key = "format", value = "json")
   @Path("/images/detail")
   // TODO: cloudServersFault (400, 500), serviceUnavailable (503), unauthorized (401), badRequest
   // (400)
   List<Image> listImageDetails();

   /**
    * 
    * This operation returns details of the specified image.
    * 
    * @see Image
    */
   @GET
   @ResponseParser(ParseImageFromGsonResponse.class)
   @Query(key = "format", value = "json")
   @ExceptionParser(ReturnImageNotFoundOn404.class)
   @Path("/images/{id}")
   // TODO: cloudServersFault (400, 500), serviceUnavailable (503), unauthorized (401), badRequest
   // (400)
   Image getImageDetails(@PathParam("id") int id);

   /**
    * 
    * This operation returns details of the specified flavor.
    * 
    * @see Flavor
    */
   @GET
   @ResponseParser(ParseFlavorFromGsonResponse.class)
   @Query(key = "format", value = "json")
   @ExceptionParser(ReturnFlavorNotFoundOn404.class)
   @Path("/flavors/{id}")
   // TODO: cloudServersFault (400, 500), serviceUnavailable (503), unauthorized (401), badRequest
   // (400)
   Flavor getFlavorDetails(@PathParam("id") int id);

}
