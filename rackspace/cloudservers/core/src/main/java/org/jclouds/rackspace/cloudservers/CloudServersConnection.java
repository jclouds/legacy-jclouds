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

import java.net.InetAddress;
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
import org.jclouds.rackspace.CloudServers;
import org.jclouds.rackspace.cloudservers.decorators.AddBackupScheduleAsJsonEntity;
import org.jclouds.rackspace.cloudservers.decorators.AddAdminPassAsJsonEntity;
import org.jclouds.rackspace.cloudservers.decorators.AddServerNameAsJsonEntity;
import org.jclouds.rackspace.cloudservers.decorators.AddConfirmResizeAsJsonEntity;
import org.jclouds.rackspace.cloudservers.decorators.AddCreateImageAsJsonEntity;
import org.jclouds.rackspace.cloudservers.decorators.AddRebootTypeAsJsonEntity;
import org.jclouds.rackspace.cloudservers.decorators.AddResizeFlavorAsJsonEntity;
import org.jclouds.rackspace.cloudservers.decorators.AddRevertResizeAsJsonEntity;
import org.jclouds.rackspace.cloudservers.decorators.AddSharedIpGroupAsJsonEntity;
import org.jclouds.rackspace.cloudservers.domain.Addresses;
import org.jclouds.rackspace.cloudservers.domain.BackupSchedule;
import org.jclouds.rackspace.cloudservers.domain.Flavor;
import org.jclouds.rackspace.cloudservers.domain.Image;
import org.jclouds.rackspace.cloudservers.domain.RebootType;
import org.jclouds.rackspace.cloudservers.domain.Server;
import org.jclouds.rackspace.cloudservers.domain.SharedIpGroup;
import org.jclouds.rackspace.cloudservers.functions.IpAddress;
import org.jclouds.rackspace.cloudservers.functions.ParseAddressesFromJsonResponse;
import org.jclouds.rackspace.cloudservers.functions.ParseBackupScheduleFromJsonResponse;
import org.jclouds.rackspace.cloudservers.functions.ParseFlavorFromJsonResponse;
import org.jclouds.rackspace.cloudservers.functions.ParseFlavorListFromJsonResponse;
import org.jclouds.rackspace.cloudservers.functions.ParseImageFromJsonResponse;
import org.jclouds.rackspace.cloudservers.functions.ParseImageListFromJsonResponse;
import org.jclouds.rackspace.cloudservers.functions.ParseInetAddressListFromJsonResponse;
import org.jclouds.rackspace.cloudservers.functions.ParseServerFromJsonResponse;
import org.jclouds.rackspace.cloudservers.functions.ParseServerListFromJsonResponse;
import org.jclouds.rackspace.cloudservers.functions.ParseSharedIpGroupFromJsonResponse;
import org.jclouds.rackspace.cloudservers.functions.ParseSharedIpGroupListFromJsonResponse;
import org.jclouds.rackspace.cloudservers.functions.ReturnFlavorNotFoundOn404;
import org.jclouds.rackspace.cloudservers.functions.ReturnImageNotFoundOn404;
import org.jclouds.rackspace.cloudservers.functions.ReturnServerNotFoundOn404;
import org.jclouds.rackspace.cloudservers.functions.ReturnSharedIpGroupNotFoundOn404;
import org.jclouds.rackspace.cloudservers.options.CreateServerOptions;
import org.jclouds.rackspace.cloudservers.options.CreateSharedIpGroupOptions;
import org.jclouds.rackspace.cloudservers.options.ListOptions;
import org.jclouds.rackspace.cloudservers.options.RebuildServerOptions;
import org.jclouds.rackspace.filters.AuthenticateRequest;
import org.jclouds.rest.annotations.DecoratorParam;
import org.jclouds.rest.annotations.Endpoint;
import org.jclouds.rest.annotations.ExceptionParser;
import org.jclouds.rest.annotations.MapBinder;
import org.jclouds.rest.annotations.MapEntityParam;
import org.jclouds.rest.annotations.ParamParser;
import org.jclouds.rest.annotations.QueryParams;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.ResponseParser;
import org.jclouds.rest.annotations.SkipEncoding;

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
@Endpoint(CloudServers.class)
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
   @ResponseParser(ParseServerListFromJsonResponse.class)
   @QueryParams(keys = "format", values = "json")
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
   @ResponseParser(ParseServerFromJsonResponse.class)
   @QueryParams(keys = "format", values = "json")
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
    * The reboot function allows for either a soft or hard reboot of a server.
    * <p/>
    * Status Transition:
    * <p/>
    * ACTIVE - REBOOT - ACTIVE (soft reboot)
    * <p/>
    * ACTIVE - HARD_REBOOT - ACTIVE (hard reboot)
    * 
    * @param rebootType
    *           With a soft reboot, the operating system is signaled to restart, which allows for a
    *           graceful shutdown of all processes. A hard reboot is the equivalent of power cycling
    *           the server.
    */
   @POST
   @QueryParams(keys = "format", values = "json")
   @Path("/servers/{id}/action")
   @ExceptionParser(ReturnFalseOn404.class)
   // TODO:cloudServersFault (400, 500), serviceUnavailable (503), unauthorized (401), badRequest
   // (400), badMediaType(415),buildInProgress (409), overLimit (403)
   boolean rebootServer(@PathParam("id") int id,
            @DecoratorParam(AddRebootTypeAsJsonEntity.class) RebootType rebootType);

   /**
    * The resize function converts an existing server to a different flavor, in essence, scaling the
    * server up or down. The original server is saved for a period of time to allow rollback if
    * there is a problem. All resizes should be tested and explicitly confirmed, at which time the
    * original server is removed. All resizes are automatically confirmed after 24 hours if they are
    * not confirmed or reverted.
    * <p/>
    * Status Transition:
    * <p/>
    * ACTIVE - QUEUE_RESIZE - PREP_RESIZE - VERIFY_RESIZE
    * <p/>
    * ACTIVE - QUEUE_RESIZE - ACTIVE (on error)
    */
   @POST
   @QueryParams(keys = "format", values = "json")
   @Path("/servers/{id}/action")
   @ExceptionParser(ReturnFalseOn404.class)
   // TODO:cloudServersFault (400, 500), serviceUnavailable (503), unauthorized (401), badRequest
   // (400), badMediaType(415), itemNotFound (404), buildInProgress (409), serverCapacityUnavailable
   // (503), overLimit (413), resizeNotAllowed (403)
   boolean resizeServer(@PathParam("id") int id, @DecoratorParam(AddResizeFlavorAsJsonEntity.class) int flavorId);

   /**
    * The resize function converts an existing server to a different flavor, in essence, scaling the
    * server up or down. The original server is saved for a period of time to allow rollback if
    * there is a problem. All resizes should be tested and explicitly confirmed, at which time the
    * original server is removed. All resizes are automatically confirmed after 24 hours if they are
    * not confirmed or reverted.
    * <p/>
    * Status Transition:
    * <p/>
    * VERIFY_RESIZE - ACTIVE
    */
   @POST
   @QueryParams(keys = "format", values = "json")
   @Path("/servers/{id}/action")
   @ExceptionParser(ReturnFalseOn404.class)
   // TODO:cloudServersFault (400, 500), serviceUnavailable (503), unauthorized (401), badRequest
   // (400), badMediaType(415), itemNotFound (404), buildInProgress (409), serverCapacityUnavailable
   // (503), overLimit (413), resizeNotAllowed (403)
   boolean confirmResizeServer(@PathParam("id") @DecoratorParam(AddConfirmResizeAsJsonEntity.class) int id);

   /**
    * The resize function converts an existing server to a different flavor, in essence, scaling the
    * server up or down. The original server is saved for a period of time to allow rollback if
    * there is a problem. All resizes should be tested and explicitly reverted, at which time the
    * original server is removed. All resizes are automatically reverted after 24 hours if they are
    * not reverted or reverted.
    * <p/>
    * Status Transition:
    * <p/>
    * VERIFY_RESIZE - ACTIVE
    */
   @POST
   @QueryParams(keys = "format", values = "json")
   @Path("/servers/{id}/action")
   @ExceptionParser(ReturnFalseOn404.class)
   // TODO:cloudServersFault (400, 500), serviceUnavailable (503), unauthorized (401), badRequest
   // (400), badMediaType(415), itemNotFound (404), buildInProgress (409), serverCapacityUnavailable
   // (503), overLimit (413), resizeNotAllowed (403)
   boolean revertResizeServer(@PathParam("id") @DecoratorParam(AddRevertResizeAsJsonEntity.class) int id);

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
   @ResponseParser(ParseServerFromJsonResponse.class)
   @QueryParams(keys = "format", values = "json")
   @Path("/servers")
   @MapBinder(CreateServerOptions.class)
   // TODO:cloudServersFault (400, 500), serviceUnavailable (503), unauthorized (401),
   // badMediaType(415), badRequest (400), serverCapacityUnavailable (503), overLimit (413)
   Server createServer(@MapEntityParam("name") String name, @MapEntityParam("imageId") int imageId,
            @MapEntityParam("flavorId") int flavorId, CreateServerOptions... options);

   /**
    * The rebuild function removes all data on the server and replaces it with the specified image.
    * Server ID and IP addresses remain the same.
    * <p/>
    * Status Transition:
    * <p/>
    * ACTIVE - REBUILD - ACTIVE
    * <p/>
    * ACTIVE - REBUILD - ERROR (on error)
    * <p/>
    * 
    * @param options
    *           - imageId is an optional argument. If it is not specified, the server is rebuilt
    *           with the original imageId.
    */
   @POST
   @QueryParams(keys = "format", values = "json")
   @Path("/servers/{id}/action")
   @ExceptionParser(ReturnFalseOn404.class)
   @MapBinder(RebuildServerOptions.class)
   // TODO: cloudServersFault (400, 500), serviceUnavailable (503), unauthorized (401), badRequest
   // (400), badMediaType(415), itemNotFound (404), buildInProgress (409), serverCapacityUnavailable
   // (503), overLimit (413)
   boolean rebuildServer(@PathParam("id") int id, RebuildServerOptions... options);

   /**
    * /** This operation allows you share an IP address to the specified server
    * <p/>
    * This operation shares an IP from an existing server in the specified shared IP group to
    * another specified server in the same group. The operation modifies cloud network restrictions
    * to allow IP traffic for the given IP to/from the server specified.
    * 
    * <p/>
    * Status Transition: ACTIVE - SHARE_IP - ACTIVE (if configureServer is true) ACTIVE -
    * SHARE_IP_NO_CONFIG - ACTIVE
    * 
    * @param configureServer
    *           <p/>
    *           if set to true, the server is configured with the new address, though the address is
    *           not enabled. Note that configuring the server does require a reboot.
    *           <p/>
    *           If set to false, does not bind the IP to the server itself. A heartbeat facility
    *           (e.g. keepalived) can then be used within the servers to perform health checks and
    *           manage IP failover.
    * @return false if the server is not found
    */
   @PUT
   @ExceptionParser(ReturnFalseOn404.class)
   @Path("/servers/{id}/ips/public/{address}")
   @MapBinder(AddSharedIpGroupAsJsonEntity.class)
   // TODO: cloudServersFault (400, 500), serviceUnavailable (503), unauthorized (401), badRequest
   // (400), badMediaType(415), buildInProgress (409), overLimit (413)
   boolean shareIp(@PathParam("address") @ParamParser(IpAddress.class) InetAddress addressToShare,
            @PathParam("id") int serverToAssignAddressTo,
            @MapEntityParam("sharedIpGroupId") int sharedIpGroup,
            @MapEntityParam("configureServer") boolean configureServer);

   /**
    * This operation removes a shared IP address from the specified server.
    * <p/>
    * Status Transition: ACTIVE - DELETE_IP - ACTIVE
    * 
    * @param addressToShare
    * @param serverToAssignAddressTo
    * @return
    */
   @DELETE
   @ExceptionParser(ReturnFalseOn404.class)
   @Path("/servers/{id}/ips/public/{address}")
   // TODO: cloudServersFault (400, 500), serviceUnavailable (503), unauthorized(401), badRequest
   // (400),
   // badMediaType(415), overLimit (413)
   boolean unshareIp(
            @PathParam("address") @ParamParser(IpAddress.class) InetAddress addressToShare,
            @PathParam("id") int serverToAssignAddressTo);

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
            @DecoratorParam(AddAdminPassAsJsonEntity.class) String adminPass);

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
            @DecoratorParam(AddServerNameAsJsonEntity.class) String newName);

   /**
    * 
    * List available flavors (IDs and names only)
    * 
    * in order to retrieve all details, pass the option {@link ListOptions#withDetails()
    * withDetails()}
    */
   @GET
   @ResponseParser(ParseFlavorListFromJsonResponse.class)
   @QueryParams(keys = "format", values = "json")
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
   @ResponseParser(ParseFlavorFromJsonResponse.class)
   @QueryParams(keys = "format", values = "json")
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
   @ResponseParser(ParseImageListFromJsonResponse.class)
   @QueryParams(keys = "format", values = "json")
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
   @ResponseParser(ParseImageFromJsonResponse.class)
   @QueryParams(keys = "format", values = "json")
   @ExceptionParser(ReturnImageNotFoundOn404.class)
   @Path("/images/{id}")
   // TODO: cloudServersFault (400, 500), serviceUnavailable (503), unauthorized (401), badRequest
   // (400)
   Image getImage(@PathParam("id") int id);

   /**
    * 
    * This operation creates a new image for the given server ID. Once complete, a new image will be
    * available that can be used to rebuild or create servers. Specifying the same image name as an
    * existing custom image replaces the image. The image creation status can be queried by
    * performing a GET on /images/id and examining the status and progress attributes.
    * 
    * Status Transition:
    * <p/>
    * QUEUED - PREPARING - SAVING - ACTIVE
    * <p/>
    * QUEUED - PREPARING - SAVING - FAILED (on error)
    * <p/>
    * Note: At present, image creation is an asynchronous operation, so coordinating the creation
    * with data quiescence, etc. is currently not possible.
    * 
    * @return {@link Image#NOT_FOUND} if the server is not found
    * @see Image
    */
   @POST
   @ResponseParser(ParseImageFromJsonResponse.class)
   @QueryParams(keys = "format", values = "json")
   @ExceptionParser(ReturnImageNotFoundOn404.class)
   @MapBinder(AddCreateImageAsJsonEntity.class)
   @Path("/images")
   // TODO: cloudServersFault (400, 500), serviceUnavailable (503), unauthorized (401), badRequest
   // (400), badMediaType(415), buildInProgress (409), serverCapacityUnavailable (503), overLimit
   // (413), resizeNotAllowed (403), backupOrResizeInProgress (409)
   Image createImageFromServer(@MapEntityParam("imageName") String imageName,
            @MapEntityParam("serverId") int serverId);

   /**
    * 
    * List shared IP groups (IDs and names only)
    * 
    * in order to retrieve all details, pass the option {@link ListOptions#withDetails()
    * withDetails()}
    */
   @GET
   @ResponseParser(ParseSharedIpGroupListFromJsonResponse.class)
   @QueryParams(keys = "format", values = "json")
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
   @ResponseParser(ParseSharedIpGroupFromJsonResponse.class)
   @QueryParams(keys = "format", values = "json")
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
   @ResponseParser(ParseSharedIpGroupFromJsonResponse.class)
   @QueryParams(keys = "format", values = "json")
   @Path("/shared_ip_groups")
   @MapBinder(CreateSharedIpGroupOptions.class)
   // TODO: cloudSharedIpGroupsFault (400, 500), serviceUnavailable (503), unauthorized (401),
   // badRequest (400), badMediaType(415), overLimit (413)
   SharedIpGroup createSharedIpGroup(@MapEntityParam("name") String name,
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

   /**
    * List the backup schedule for the specified server
    */
   @GET
   @ResponseParser(ParseBackupScheduleFromJsonResponse.class)
   @QueryParams(keys = "format", values = "json")
   @Path("/servers/{id}/backup_schedule")
   // TODO: cloudServersFault (400, 500), serviceUnavailable (503), unauthorized (401), badRequest
   // (400), itemNotFound (404)
   BackupSchedule listBackupSchedule(@PathParam("id") int serverId);

   /**
    * Delete backup schedule for the specified server.
    * <p/>
    * Web Hosting #119571 currently disables the schedule, not deletes it.
    * 
    * @return false if the server is not found
    */
   @DELETE
   @ExceptionParser(ReturnFalseOn404.class)
   @Path("/servers/{id}/backup_schedule")
   // TODO:cloudServersFault (400, 500), serviceUnavailable (503), unauthorized (401), badRequest
   // (400), buildInProgress (409), serverCapacityUnavailable (503), backupOrResizeInProgress(409)
   boolean deleteBackupSchedule(@PathParam("id") int serverId);

   /**
    * Enable/update the backup schedule for the specified server
    * 
    * @return false if the server is not found
    */
   @POST
   @ExceptionParser(ReturnFalseOn404.class)
   @Path("/servers/{id}/backup_schedule")
   // TODO: cloudServersFault (400, 500), serviceUnavailable (503), unauthorized (401), badRequest
   // (400), badMediaType(415), buildInProgress (409), serverCapacityUnavailable (503),
   // backupOrResizeInProgress(409), resizeNotAllowed (403). overLimit (413)
   boolean replaceBackupSchedule(@PathParam("id") int id,
            @DecoratorParam(AddBackupScheduleAsJsonEntity.class) BackupSchedule backupSchedule);

   /**
    * List all server addresses
    */
   @GET
   @ResponseParser(ParseAddressesFromJsonResponse.class)
   @QueryParams(keys = "format", values = "json")
   @Path("/servers/{id}/ips")
   // TODO: cloudServersFault (400, 500), serviceUnavailable (503), unauthorized (401), badRequest
   // (400), overLimit (413)
   Addresses listAddresses(@PathParam("id") int serverId);

   /**
    * List all public server addresses
    */
   @GET
   @ResponseParser(ParseInetAddressListFromJsonResponse.class)
   @QueryParams(keys = "format", values = "json")
   @Path("/servers/{id}/ips/public")
   // TODO: cloudServersFault (400, 500), serviceUnavailable (503), unauthorized (401), badRequest
   // (400), overLimit (413)
   List<InetAddress> listPublicAddresses(@PathParam("id") int serverId);

   /**
    * List all private server addresses
    */
   @GET
   @ResponseParser(ParseInetAddressListFromJsonResponse.class)
   @QueryParams(keys = "format", values = "json")
   @Path("/servers/{id}/ips/private")
   // TODO: cloudServersFault (400, 500), serviceUnavailable (503), unauthorized (401), badRequest
   // (400), overLimit (413)
   List<InetAddress> listPrivateAddresses(@PathParam("id") int serverId);

}
