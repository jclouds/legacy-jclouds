/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
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
import org.jclouds.rackspace.cloudservers.binders.BindAdminPassToJsonPayload;
import org.jclouds.rackspace.cloudservers.binders.BindBackupScheduleToJsonPayload;
import org.jclouds.rackspace.cloudservers.binders.BindConfirmResizeToJsonPayload;
import org.jclouds.rackspace.cloudservers.binders.BindCreateImageToJsonPayload;
import org.jclouds.rackspace.cloudservers.binders.BindRebootTypeToJsonPayload;
import org.jclouds.rackspace.cloudservers.binders.BindResizeFlavorToJsonPayload;
import org.jclouds.rackspace.cloudservers.binders.BindRevertResizeToJsonPayload;
import org.jclouds.rackspace.cloudservers.binders.BindServerNameToJsonPayload;
import org.jclouds.rackspace.cloudservers.binders.BindSharedIpGroupToJsonPayload;
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
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.Endpoint;
import org.jclouds.rest.annotations.ExceptionParser;
import org.jclouds.rest.annotations.MapBinder;
import org.jclouds.rest.annotations.MapPayloadParam;
import org.jclouds.rest.annotations.ParamParser;
import org.jclouds.rest.annotations.QueryParams;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.ResponseParser;
import org.jclouds.rest.annotations.SkipEncoding;

/**
 * Provides asynchronous access to Cloud Servers via their REST API.
 * <p/>
 * All commands return a Future of the result from Cloud Servers. Any exceptions incurred during
 * processing will be wrapped in an {@link ExecutionException} as documented in {@link Future#get()}.
 * 
 * @see CloudServersClient
 * @see <a href="http://docs.rackspacecloud.com/servers/api/cs-devguide-latest.pdf" />
 * @author Adrian Cole
 */
@SkipEncoding('/')
@RequestFilters(AuthenticateRequest.class)
@Endpoint(CloudServers.class)
public interface CloudServersAsyncClient {

   /**
    * @see CloudServersClient#listServers
    */
   @GET
   @ResponseParser(ParseServerListFromJsonResponse.class)
   @QueryParams(keys = "format", values = "json")
   @Path("/servers")
   Future<? extends List<Server>> listServers(ListOptions... options);

   /**
    * @see CloudServersClient#getServer
    */
   @GET
   @ResponseParser(ParseServerFromJsonResponse.class)
   @QueryParams(keys = "format", values = "json")
   @ExceptionParser(ReturnServerNotFoundOn404.class)
   @Path("/servers/{id}")
   Future<Server> getServer(@PathParam("id") int id);

   /**
    * @see CloudServersClient#deleteServer
    */
   @DELETE
   @ExceptionParser(ReturnFalseOn404.class)
   @Path("/servers/{id}")
   Future<Boolean> deleteServer(@PathParam("id") int id);

   /**
    * @see CloudServersClient#rebootServer
    */
   @POST
   @QueryParams(keys = "format", values = "json")
   @Path("/servers/{id}/action")
   @ExceptionParser(ReturnFalseOn404.class)
   Future<Boolean> rebootServer(@PathParam("id") int id,
            @BinderParam(BindRebootTypeToJsonPayload.class) RebootType rebootType);

   /**
    * @see CloudServersClient#resizeServer
    */
   @POST
   @QueryParams(keys = "format", values = "json")
   @Path("/servers/{id}/action")
   @ExceptionParser(ReturnFalseOn404.class)
   Future<Boolean> resizeServer(@PathParam("id") int id,
            @BinderParam(BindResizeFlavorToJsonPayload.class) int flavorId);

   /**
    * @see CloudServersClient#confirmResizeServer
    */
   @POST
   @QueryParams(keys = "format", values = "json")
   @Path("/servers/{id}/action")
   @ExceptionParser(ReturnFalseOn404.class)
   Future<Boolean> confirmResizeServer(
            @PathParam("id") @BinderParam(BindConfirmResizeToJsonPayload.class) int id);

   /**
    * @see CloudServersClient#revertResizeServer
    */
   @POST
   @QueryParams(keys = "format", values = "json")
   @Path("/servers/{id}/action")
   @ExceptionParser(ReturnFalseOn404.class)
   Future<Boolean> revertResizeServer(
            @PathParam("id") @BinderParam(BindRevertResizeToJsonPayload.class) int id);

   /**
    * @see CloudServersClient#createServer
    */
   @POST
   @ResponseParser(ParseServerFromJsonResponse.class)
   @QueryParams(keys = "format", values = "json")
   @Path("/servers")
   @MapBinder(CreateServerOptions.class)
   Future<Server> createServer(@MapPayloadParam("name") String name,
            @MapPayloadParam("imageId") int imageId, @MapPayloadParam("flavorId") int flavorId,
            CreateServerOptions... options);

   /**
    * @see CloudServersClient#rebuildServer
    */
   @POST
   @QueryParams(keys = "format", values = "json")
   @Path("/servers/{id}/action")
   @ExceptionParser(ReturnFalseOn404.class)
   @MapBinder(RebuildServerOptions.class)
   Future<Boolean> rebuildServer(@PathParam("id") int id, RebuildServerOptions... options);

   /**
    * @see CloudServersClient#shareIp
    */
   @PUT
   @ExceptionParser(ReturnFalseOn404.class)
   @Path("/servers/{id}/ips/public/{address}")
   @MapBinder(BindSharedIpGroupToJsonPayload.class)
   Future<Boolean> shareIp(
            @PathParam("address") @ParamParser(IpAddress.class) InetAddress addressToShare,
            @PathParam("id") int serverToTosignBindressTo,
            @MapPayloadParam("sharedIpGroupId") int sharedIpGroup,
            @MapPayloadParam("configureServer") boolean configureServer);

   /**
    * @see CloudServersClient#unshareIp
    */
   @DELETE
   @ExceptionParser(ReturnFalseOn404.class)
   @Path("/servers/{id}/ips/public/{address}")
   Future<Boolean> unshareIp(
            @PathParam("address") @ParamParser(IpAddress.class) InetAddress addressToShare,
            @PathParam("id") int serverToTosignBindressTo);

   /**
    * @see CloudServersClient#changeAdminPass
    */
   @PUT
   @ExceptionParser(ReturnFalseOn404.class)
   @Path("/servers/{id}")
   Future<Boolean> changeAdminPass(@PathParam("id") int id,
            @BinderParam(BindAdminPassToJsonPayload.class) String adminPass);

   /**
    * @see CloudServersClient#renameServer
    */
   @PUT
   @ExceptionParser(ReturnFalseOn404.class)
   @Path("/servers/{id}")
   Future<Boolean> renameServer(@PathParam("id") int id,
            @BinderParam(BindServerNameToJsonPayload.class) String newName);

   /**
    * @see CloudServersClient#listFlavors
    */
   @GET
   @ResponseParser(ParseFlavorListFromJsonResponse.class)
   @QueryParams(keys = "format", values = "json")
   @Path("/flavors")
   Future<? extends List<Flavor>> listFlavors(ListOptions... options);

   /**
    * @see CloudServersClient#getFlavor
    */
   @GET
   @ResponseParser(ParseFlavorFromJsonResponse.class)
   @QueryParams(keys = "format", values = "json")
   @ExceptionParser(ReturnFlavorNotFoundOn404.class)
   @Path("/flavors/{id}")
   Future<Flavor> getFlavor(@PathParam("id") int id);

   /**
    * @see CloudServersClient#listImages
    */
   @GET
   @ResponseParser(ParseImageListFromJsonResponse.class)
   @QueryParams(keys = "format", values = "json")
   @Path("/images")
   Future<? extends List<Image>> listImages(ListOptions... options);

   /**
    * @see CloudServersClient#getImage
    */
   @GET
   @ResponseParser(ParseImageFromJsonResponse.class)
   @QueryParams(keys = "format", values = "json")
   @ExceptionParser(ReturnImageNotFoundOn404.class)
   @Path("/images/{id}")
   Future<Image> getImage(@PathParam("id") int id);

   /**
    * @see CloudServersClient#createImageFromServer
    */
   @POST
   @ResponseParser(ParseImageFromJsonResponse.class)
   @QueryParams(keys = "format", values = "json")
   @ExceptionParser(ReturnImageNotFoundOn404.class)
   @MapBinder(BindCreateImageToJsonPayload.class)
   @Path("/images")
   Future<Image> createImageFromServer(@MapPayloadParam("imageName") String imageName,
            @MapPayloadParam("serverId") int serverId);

   /**
    * @see CloudServersClient#listSharedIpGroups
    */
   @GET
   @ResponseParser(ParseSharedIpGroupListFromJsonResponse.class)
   @QueryParams(keys = "format", values = "json")
   @Path("/shared_ip_groups")
   Future<? extends List<SharedIpGroup>> listSharedIpGroups(ListOptions... options);

   /**
    * @see CloudServersClient#getSharedIpGroup
    */
   @GET
   @ResponseParser(ParseSharedIpGroupFromJsonResponse.class)
   @QueryParams(keys = "format", values = "json")
   @ExceptionParser(ReturnSharedIpGroupNotFoundOn404.class)
   @Path("/shared_ip_groups/{id}")
   Future<SharedIpGroup> getSharedIpGroup(@PathParam("id") int id);

   /**
    * @see CloudServersClient#createSharedIpGroup
    */
   @POST
   @ResponseParser(ParseSharedIpGroupFromJsonResponse.class)
   @QueryParams(keys = "format", values = "json")
   @Path("/shared_ip_groups")
   @MapBinder(CreateSharedIpGroupOptions.class)
   Future<SharedIpGroup> createSharedIpGroup(@MapPayloadParam("name") String name,
            CreateSharedIpGroupOptions... options);

   /**
    * @see CloudServersClient#deleteSharedIpGroup
    */
   @DELETE
   @ExceptionParser(ReturnFalseOn404.class)
   @Path("/shared_ip_groups/{id}")
   Future<Boolean> deleteSharedIpGroup(@PathParam("id") int id);

   /**
    * @see CloudServersClient#listBackupSchedule
    */
   @GET
   @ResponseParser(ParseBackupScheduleFromJsonResponse.class)
   @QueryParams(keys = "format", values = "json")
   @Path("/servers/{id}/backup_schedule")
   Future<BackupSchedule> listBackupSchedule(@PathParam("id") int serverId);

   /**
    * @see CloudServersClient#deleteBackupSchedule
    */
   @DELETE
   @ExceptionParser(ReturnFalseOn404.class)
   @Path("/servers/{id}/backup_schedule")
   Future<Boolean> deleteBackupSchedule(@PathParam("id") int serverId);

   /**
    * @see CloudServersClient#replaceBackupSchedule
    */
   @POST
   @ExceptionParser(ReturnFalseOn404.class)
   @Path("/servers/{id}/backup_schedule")
   Future<Boolean> replaceBackupSchedule(@PathParam("id") int id,
            @BinderParam(BindBackupScheduleToJsonPayload.class) BackupSchedule backupSchedule);

   /**
    * @see CloudServersClient#listAddresses
    */
   @GET
   @ResponseParser(ParseAddressesFromJsonResponse.class)
   @QueryParams(keys = "format", values = "json")
   @Path("/servers/{id}/ips")
   Future<Addresses> listAddresses(@PathParam("id") int serverId);

   /**
    * @see CloudServersClient#listPublicAddresses
    */
   @GET
   @ResponseParser(ParseInetAddressListFromJsonResponse.class)
   @QueryParams(keys = "format", values = "json")
   @Path("/servers/{id}/ips/public")
   Future<? extends List<InetAddress>> listPublicAddresses(@PathParam("id") int serverId);

   /**
    * @see CloudServersClient#listPrivateAddresses
    */
   @GET
   @ResponseParser(ParseInetAddressListFromJsonResponse.class)
   @QueryParams(keys = "format", values = "json")
   @Path("/servers/{id}/ips/private")
   Future<? extends List<InetAddress>> listPrivateAddresses(@PathParam("id") int serverId);

}
