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
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.jclouds.http.functions.ReturnFalseOn404;
import org.jclouds.rackspace.cloudservers.binders.ChangeAdminPassBinder;
import org.jclouds.rackspace.cloudservers.binders.ChangeServerNameBinder;
import org.jclouds.rackspace.cloudservers.domain.Flavor;
import org.jclouds.rackspace.cloudservers.domain.Image;
import org.jclouds.rackspace.cloudservers.domain.Server;
import org.jclouds.rackspace.cloudservers.domain.SharedIpGroup;
import org.jclouds.rackspace.cloudservers.functions.ParseFlavorFromGsonResponse;
import org.jclouds.rackspace.cloudservers.functions.ParseFlavorListFromGsonResponse;
import org.jclouds.rackspace.cloudservers.functions.ParseImageFromGsonResponse;
import org.jclouds.rackspace.cloudservers.functions.ParseImageListFromGsonResponse;
import org.jclouds.rackspace.cloudservers.functions.ParseServerFromGsonResponse;
import org.jclouds.rackspace.cloudservers.functions.ParseServerListFromGsonResponse;
import org.jclouds.rackspace.cloudservers.functions.ParseSharedIpGroupFromGsonResponse;
import org.jclouds.rackspace.cloudservers.functions.ParseSharedIpGroupListFromGsonResponse;
import org.jclouds.rackspace.cloudservers.functions.ReturnFlavorNotFoundOn404;
import org.jclouds.rackspace.cloudservers.functions.ReturnImageNotFoundOn404;
import org.jclouds.rackspace.cloudservers.functions.ReturnServerNotFoundOn404;
import org.jclouds.rackspace.cloudservers.functions.ReturnSharedIpGroupNotFoundOn404;
import org.jclouds.rackspace.cloudservers.options.CreateServerOptions;
import org.jclouds.rackspace.cloudservers.options.CreateSharedIpGroupOptions;
import org.jclouds.rackspace.cloudservers.options.ListOptions;
import org.jclouds.rackspace.filters.AuthenticateRequest;
import org.jclouds.rest.EntityParam;
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
    * This operation provides a list of servers associated with your account. Servers that have been
    * deleted are not included in this list.
    * <p/>
    * in order to retrieve all details, pass the option {@link ListOptions#withDetails()
    * withDetails()}
    */
   @GET
   @ResponseParser(ParseServerListFromGsonResponse.class)
   @Query(key = "format", value = "json")
   @Path("/servers")
   // TODO: Error Response Code(s): cloudServersFault (400, 500), serviceUnavailable (503),
   // unauthorized (401), badRequest (400), overLimit (413)
   List<Server> listServers(ListOptions... options);

   /**
    * 
    * This operation returns details of the specified server.
    * 
    * @return {@link Server#NOT_FOUND} if the server is not found
    * @see Server
    */
   @GET
   @ResponseParser(ParseServerFromGsonResponse.class)
   @Query(key = "format", value = "json")
   @ExceptionParser(ReturnServerNotFoundOn404.class)
   @Path("/servers/{id}")
   // TODO: cloudServersFault (400, 500), serviceUnavailable (503), unauthorized (401), badRequest
   // (400)
   Server getServer(@PathParam("id") int id);

   /**
    * 
    * This operation deletes a cloud server instance from the system.
    * <p/>
    * Note: When a server is deleted, all images created from that server are also removed.
    * 
    * @return false if the server is not found
    * @see Server
    */
   @DELETE
   @ExceptionParser(ReturnFalseOn404.class)
   @Path("/servers/{id}")
   // TODO:cloudServersFault (400, 500), serviceUnavailable (503), unauthorized (401), badRequest
   // (400), itemNotFound (404), buildInProgress (409), overLimit (413)
   boolean deleteServer(@PathParam("id") int id);

   /**
    * The reboot function allows for either a soft or hard reboot of a server. With a soft reboot,
    * the operating system is signaled to restart, which allows for a graceful shutdown of all
    * processes. A hard reboot is the equivalent of power cycling the server.
    */

   /**
    * The rebuild function removes all data on the server and replaces it with the specified image.
    * Server ID and IP addresses remain the same.
    */

   /**
    * The resize function converts an existing server to a different flavor, in essence, scaling the
    * server up or down. The original server is saved for a period of time to allow rollback if
    * there is a problem. All resizes should be tested and explicitly confirmed, at which time the
    * original server is removed. All resizes are automatically confirmed after 24 hours if they are
    * not confirmed or reverted.
    */

   /**
    * This operation asynchronously provisions a new server. The progress of this operation depends
    * on several factors including location of the requested image, network i/o, host load, and the
    * selected flavor. The progress of the request can be checked by performing a GET on /server/id,
    * which will return a progress attribute (0-100% completion). A password will be randomly
    * generated for you and returned in the response object. For security reasons, it will not be
    * returned in subsequent GET calls against a given server ID.
    * 
    * @param options
    *           - used to specify extra files, metadata, or ip parameters during server creation.
    */
   @POST
   @ResponseParser(ParseServerFromGsonResponse.class)
   @Query(key = "format", value = "json")
   @Path("/servers")
   @PostBinder(CreateServerOptions.class)
   // TODO:cloudServersFault (400, 500), serviceUnavailable (503), unauthorized (401),
   // badMediaType(415), badRequest (400), serverCapacityUnavailable (503), overLimit (413)
   Server createServer(@PostParam("name") String name, @PostParam("imageId") int imageId,
            @PostParam("flavorId") int flavorId, CreateServerOptions... options);

   /**
    * This operation allows you to change the administrative password.
    * <p/>
    * Status Transition: ACTIVE - PASSWORD - ACTIVE
    * 
    * @return false if the server is not found
    */
   @PUT
   @ExceptionParser(ReturnFalseOn404.class)
   @Path("/servers/{id}")
   // TODO: cloudServersFault (400, 500), serviceUnavailable (503), unauthorized (401), badRequest
   // (400), badMediaType(415), buildInProgress (409), overLimit (413)
   boolean changeAdminPass(@PathParam("id") int id,
            @EntityParam(ChangeAdminPassBinder.class) String adminPass);

   /**
    * This operation allows you to update the name of the server. This operation changes the name of
    * the server in the Cloud Servers system and does not change the server host name itself.
    * <p/>
    * Status Transition: ACTIVE - PASSWORD - ACTIVE
    * 
    * @return false if the server is not found
    */
   @PUT
   @ExceptionParser(ReturnFalseOn404.class)
   @Path("/servers/{id}")
   // TODO: cloudServersFault (400, 500), serviceUnavailable (503), unauthorized (401), badRequest
   // (400), badMediaType(415), buildInProgress (409), overLimit (413)
   boolean renameServer(@PathParam("id") int id,
            @EntityParam(ChangeServerNameBinder.class) String newName);

   /**
    * 
    * List available flavors (IDs and names only)
    * 
    * in order to retrieve all details, pass the option {@link ListOptions#withDetails()
    * withDetails()}
    */
   @GET
   @ResponseParser(ParseFlavorListFromGsonResponse.class)
   @Query(key = "format", value = "json")
   @Path("/flavors")
   // TODO: cloudServersFault (400, 500), serviceUnavailable (503), unauthorized (401), badRequest
   // (400)
   List<Flavor> listFlavors(ListOptions... options);

   /**
    * 
    * This operation returns details of the specified flavor.
    * 
    * @return {@link Flavor#NOT_FOUND} if the flavor is not found
    * @see Flavor
    */
   @GET
   @ResponseParser(ParseFlavorFromGsonResponse.class)
   @Query(key = "format", value = "json")
   @ExceptionParser(ReturnFlavorNotFoundOn404.class)
   @Path("/flavors/{id}")
   // TODO: cloudServersFault (400, 500), serviceUnavailable (503), unauthorized (401), badRequest
   // (400)
   Flavor getFlavor(@PathParam("id") int id);

   /**
    * 
    * List available images (IDs and names only)
    * 
    * in order to retrieve all details, pass the option {@link ListOptions#withDetails()
    * withDetails()}
    */
   @GET
   @ResponseParser(ParseImageListFromGsonResponse.class)
   @Query(key = "format", value = "json")
   @Path("/images")
   // TODO: cloudServersFault (400, 500), serviceUnavailable (503), unauthorized (401), badRequest
   // (400)
   List<Image> listImages(ListOptions... options);

   /**
    * 
    * This operation returns details of the specified image.
    * 
    * @return {@link Image#NOT_FOUND} if the image is not found
    * @see Image
    */
   @GET
   @ResponseParser(ParseImageFromGsonResponse.class)
   @Query(key = "format", value = "json")
   @ExceptionParser(ReturnImageNotFoundOn404.class)
   @Path("/images/{id}")
   // TODO: cloudServersFault (400, 500), serviceUnavailable (503), unauthorized (401), badRequest
   // (400)
   Image getImage(@PathParam("id") int id);

   /**
    * 
    * List shared IP groups (IDs and names only)
    * 
    * in order to retrieve all details, pass the option {@link ListOptions#withDetails()
    * withDetails()}
    */
   @GET
   @ResponseParser(ParseSharedIpGroupListFromGsonResponse.class)
   @Query(key = "format", value = "json")
   @Path("/shared_ip_groups")
   // TODO: cloudServersFault (400, 500), serviceUnavailable (503), unauthorized (401), badRequest
   // (400)
   List<SharedIpGroup> listSharedIpGroups(ListOptions... options);

   /**
    * 
    * This operation returns details of the specified shared IP group.
    * 
    * @return {@link SharedIpGroup#NOT_FOUND} if the shared IP group is not found
    * @see SharedIpGroup
    */
   @GET
   @ResponseParser(ParseSharedIpGroupFromGsonResponse.class)
   @Query(key = "format", value = "json")
   @ExceptionParser(ReturnSharedIpGroupNotFoundOn404.class)
   @Path("/shared_ip_groups/{id}")
   // TODO: cloudServersFault (400, 500), serviceUnavailable (503), unauthorized (401), badRequest
   // (400)
   SharedIpGroup getSharedIpGroup(@PathParam("id") int id);

   /**
    * This operation creates a new shared IP group. Please note, all responses to requests for
    * shared_ip_groups return an array of servers. However, on a create request, the shared IP group
    * can be created empty or can be initially populated with a single server. Use
    * {@link CreateSharedIpGroupOptions} to specify an server.
    */
   @POST
   @ResponseParser(ParseSharedIpGroupFromGsonResponse.class)
   @Query(key = "format", value = "json")
   @Path("/shared_ip_groups")
   @PostBinder(CreateSharedIpGroupOptions.class)
   // TODO: cloudSharedIpGroupsFault (400, 500), serviceUnavailable (503), unauthorized (401),
   // badRequest (400), badMediaType(415), overLimit (413)
   SharedIpGroup createSharedIpGroup(@PostParam("name") String name,
            CreateSharedIpGroupOptions... options);

   /**
    * This operation deletes the specified shared IP group. This operation will ONLY succeed if 1)
    * there are no active servers in the group (i.e. they have all been terminated) or 2) no servers
    * in the group are actively sharing IPs.
    * 
    * @return false if the shared ip group is not found
    * @see SharedIpGroup
    */
   @DELETE
   @ExceptionParser(ReturnFalseOn404.class)
   @Path("/shared_ip_groups/{id}")
   // TODO:cloudServersFault (400, 500), serviceUnavailable (503), unauthorized (401), badRequest
   // (400)
   boolean deleteSharedIpGroup(@PathParam("id") int id);
}
