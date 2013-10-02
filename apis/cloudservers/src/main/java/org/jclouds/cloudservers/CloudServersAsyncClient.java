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
package org.jclouds.cloudservers;

import java.io.Closeable;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.jclouds.Fallbacks.EmptySetOnNotFoundOr404;
import org.jclouds.Fallbacks.FalseOnNotFoundOr404;
import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.Fallbacks.VoidOnNotFoundOr404;
import org.jclouds.cloudservers.binders.BindBackupScheduleToJsonPayload;
import org.jclouds.cloudservers.domain.Addresses;
import org.jclouds.cloudservers.domain.BackupSchedule;
import org.jclouds.cloudservers.domain.Flavor;
import org.jclouds.cloudservers.domain.Image;
import org.jclouds.cloudservers.domain.Limits;
import org.jclouds.cloudservers.domain.RebootType;
import org.jclouds.cloudservers.domain.Server;
import org.jclouds.cloudservers.domain.SharedIpGroup;
import org.jclouds.cloudservers.options.CreateServerOptions;
import org.jclouds.cloudservers.options.CreateSharedIpGroupOptions;
import org.jclouds.cloudservers.options.ListOptions;
import org.jclouds.cloudservers.options.RebuildServerOptions;
import org.jclouds.openstack.filters.AddTimestampQuery;
import org.jclouds.openstack.filters.AuthenticateRequest;
import org.jclouds.openstack.services.Compute;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.Endpoint;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.MapBinder;
import org.jclouds.rest.annotations.Payload;
import org.jclouds.rest.annotations.PayloadParam;
import org.jclouds.rest.annotations.QueryParams;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.Unwrap;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * Provides asynchronous access to Cloud Servers via their REST API.
 * <p/>
 * All commands return a ListenableFuture of the result from Cloud Servers. Any exceptions incurred
 * during processing will be backend in an {@link ExecutionException} as documented in
 * {@link ListenableFuture#get()}.
 * 
 * @see CloudServersClient
 * @see <a href="http://docs.rackspacecloud.com/servers/api/cs-devguide-latest.pdf" />
 * @author Adrian Cole
 * @deprecated please use {@code org.jclouds.ContextBuilder#buildApi(CloudServersClient.class)} as
 *             {@link CloudServersAsyncClient} interface will be removed in jclouds 1.7.
 */
@Deprecated
@RequestFilters({ AuthenticateRequest.class, AddTimestampQuery.class })
@Endpoint(Compute.class)
public interface CloudServersAsyncClient extends Closeable {

   /**
    * @see CloudServersClient#getLimits
    */
   @GET
   @Unwrap
   @Consumes(MediaType.APPLICATION_JSON)
   @QueryParams(keys = "format", values = "json")
   @Path("/limits")
   @Fallback(EmptySetOnNotFoundOr404.class)
   ListenableFuture<Limits> getLimits();

   /**
    * @see CloudServersClient#listServers
    */
   @GET
   @Unwrap
   @Consumes(MediaType.APPLICATION_JSON)
   @QueryParams(keys = "format", values = "json")
   @Path("/servers")
   @Fallback(EmptySetOnNotFoundOr404.class)
   ListenableFuture<? extends Set<Server>> listServers(ListOptions... options);

   /**
    * @see CloudServersClient#getServer
    */
   @GET
   @Unwrap
   @Consumes(MediaType.APPLICATION_JSON)
   @QueryParams(keys = "format", values = "json")
   @Fallback(NullOnNotFoundOr404.class)
   @Path("/servers/{id}")
   ListenableFuture<Server> getServer(@PathParam("id") int id);

   /**
    * @see CloudServersClient#deleteServer
    */
   @DELETE
   @Fallback(FalseOnNotFoundOr404.class)
   @Path("/servers/{id}")
   ListenableFuture<Boolean> deleteServer(@PathParam("id") int id);

   /**
    * @see CloudServersClient#rebootServer
    */
   @POST
   @QueryParams(keys = "format", values = "json")
   @Path("/servers/{id}/action")
   @Produces(MediaType.APPLICATION_JSON)
   @Payload("%7B\"reboot\":%7B\"type\":\"{type}\"%7D%7D")
   ListenableFuture<Void> rebootServer(@PathParam("id") int id, @PayloadParam("type") RebootType rebootType);

   /**
    * @see CloudServersClient#resizeServer
    */
   @POST
   @QueryParams(keys = "format", values = "json")
   @Path("/servers/{id}/action")
   @Produces(MediaType.APPLICATION_JSON)
   @Payload("%7B\"resize\":%7B\"flavorId\":{flavorId}%7D%7D")
   ListenableFuture<Void> resizeServer(@PathParam("id") int id, @PayloadParam("flavorId") int flavorId);

   /**
    * @see CloudServersClient#confirmResizeServer
    */
   @POST
   @QueryParams(keys = "format", values = "json")
   @Path("/servers/{id}/action")
   @Produces(MediaType.APPLICATION_JSON)
   @Payload("{\"confirmResize\":null}")
   ListenableFuture<Void> confirmResizeServer(@PathParam("id") int id);

   /**
    * @see CloudServersClient#revertResizeServer
    */
   @POST
   @QueryParams(keys = "format", values = "json")
   @Path("/servers/{id}/action")
   @Produces(MediaType.APPLICATION_JSON)
   @Payload("{\"revertResize\":null}")
   ListenableFuture<Void> revertResizeServer(@PathParam("id") int id);

   /**
    * @see CloudServersClient#createServer
    */
   @POST
   @Unwrap
   @Consumes(MediaType.APPLICATION_JSON)
   @QueryParams(keys = "format", values = "json")
   @Path("/servers")
   @MapBinder(CreateServerOptions.class)
   ListenableFuture<Server> createServer(@PayloadParam("name") String name, @PayloadParam("imageId") int imageId,
         @PayloadParam("flavorId") int flavorId, CreateServerOptions... options);

   /**
    * @see CloudServersClient#rebuildServer
    */
   @POST
   @QueryParams(keys = "format", values = "json")
   @Path("/servers/{id}/action")
   @MapBinder(RebuildServerOptions.class)
   ListenableFuture<Void> rebuildServer(@PathParam("id") int id, RebuildServerOptions... options);

   /**
    * @see CloudServersClient#shareIp
    */
   @PUT
   @Path("/servers/{id}/ips/public/{address}")
   @Produces(MediaType.APPLICATION_JSON)
   @Payload("%7B\"shareIp\":%7B\"sharedIpGroupId\":{sharedIpGroupId},\"configureServer\":{configureServer}%7D%7D")
   ListenableFuture<Void> shareIp(@PathParam("address") String addressToShare,
         @PathParam("id") int serverToTosignBindressTo, @PayloadParam("sharedIpGroupId") int sharedIpGroup,
         @PayloadParam("configureServer") boolean configureServer);

   /**
    * @see CloudServersClient#unshareIp
    */
   @DELETE
   @Path("/servers/{id}/ips/public/{address}")
   @Fallback(VoidOnNotFoundOr404.class)
   ListenableFuture<Void> unshareIp(@PathParam("address") String addressToShare,
         @PathParam("id") int serverToTosignBindressTo);

   /**
    * @see CloudServersClient#changeAdminPass
    */
   @PUT
   @Path("/servers/{id}")
   @Produces(MediaType.APPLICATION_JSON)
   @Payload("%7B\"server\":%7B\"adminPass\":\"{adminPass}\"%7D%7D")
   ListenableFuture<Void> changeAdminPass(@PathParam("id") int id, @PayloadParam("adminPass") String adminPass);

   /**
    * @see CloudServersClient#renameServer
    */
   @PUT
   @Path("/servers/{id}")
   @Produces(MediaType.APPLICATION_JSON)
   @Payload("%7B\"server\":%7B\"name\":\"{name}\"%7D%7D")
   ListenableFuture<Void> renameServer(@PathParam("id") int id, @PayloadParam("name") String newName);

   /**
    * @see CloudServersClient#listFlavors
    */
   @GET
   @Unwrap
   @Consumes(MediaType.APPLICATION_JSON)
   @QueryParams(keys = "format", values = "json")
   @Path("/flavors")
   @Fallback(EmptySetOnNotFoundOr404.class)
   ListenableFuture<? extends Set<Flavor>> listFlavors(ListOptions... options);

   /**
    * @see CloudServersClient#getFlavor
    */
   @GET
   @Unwrap
   @Consumes(MediaType.APPLICATION_JSON)
   @QueryParams(keys = "format", values = "json")
   @Path("/flavors/{id}")
   @Fallback(NullOnNotFoundOr404.class)
   ListenableFuture<Flavor> getFlavor(@PathParam("id") int id);

   /**
    * @see CloudServersClient#listImages
    */
   @GET
   @Unwrap
   @Consumes(MediaType.APPLICATION_JSON)
   @QueryParams(keys = "format", values = "json")
   @Path("/images")
   @Fallback(EmptySetOnNotFoundOr404.class)
   ListenableFuture<? extends Set<Image>> listImages(ListOptions... options);

   /**
    * @see CloudServersClient#getImage
    */
   @GET
   @Unwrap
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(NullOnNotFoundOr404.class)
   @QueryParams(keys = "format", values = "json")
   @Path("/images/{id}")
   ListenableFuture<Image> getImage(@PathParam("id") int id);

   /**
    * @see CloudServersClient#deleteImage
    */
   @DELETE
   @Fallback(FalseOnNotFoundOr404.class)
   @Path("/images/{id}")
   ListenableFuture<Boolean> deleteImage(@PathParam("id") int id);

   /**
    * @see CloudServersClient#createImageFromServer
    */
   @POST
   @Unwrap
   @Consumes(MediaType.APPLICATION_JSON)
   @QueryParams(keys = "format", values = "json")
   @Path("/images")
   @Produces(MediaType.APPLICATION_JSON)
   @Payload("%7B\"image\":%7B\"serverId\":{serverId},\"name\":\"{name}\"%7D%7D")
   ListenableFuture<Image> createImageFromServer(@PayloadParam("name") String imageName,
         @PayloadParam("serverId") int serverId);

   /**
    * @see CloudServersClient#listSharedIpGroups
    */
   @GET
   @Unwrap
   @Consumes(MediaType.APPLICATION_JSON)
   @QueryParams(keys = "format", values = "json")
   @Path("/shared_ip_groups")
   @Fallback(EmptySetOnNotFoundOr404.class)
   ListenableFuture<? extends Set<SharedIpGroup>> listSharedIpGroups(ListOptions... options);

   /**
    * @see CloudServersClient#getSharedIpGroup
    */
   @GET
   @Unwrap
   @Consumes(MediaType.APPLICATION_JSON)
   @QueryParams(keys = "format", values = "json")
   @Path("/shared_ip_groups/{id}")
   @Fallback(NullOnNotFoundOr404.class)
   ListenableFuture<SharedIpGroup> getSharedIpGroup(@PathParam("id") int id);

   /**
    * @see CloudServersClient#createSharedIpGroup
    */
   @POST
   @Unwrap
   @Consumes(MediaType.APPLICATION_JSON)
   @QueryParams(keys = "format", values = "json")
   @Path("/shared_ip_groups")
   @MapBinder(CreateSharedIpGroupOptions.class)
   ListenableFuture<SharedIpGroup> createSharedIpGroup(@PayloadParam("name") String name,
         CreateSharedIpGroupOptions... options);

   /**
    * @see CloudServersClient#deleteSharedIpGroup
    */
   @DELETE
   @Fallback(FalseOnNotFoundOr404.class)
   @Path("/shared_ip_groups/{id}")
   ListenableFuture<Boolean> deleteSharedIpGroup(@PathParam("id") int id);

   /**
    * @see CloudServersClient#listBackupSchedule
    */
   @GET
   @Unwrap
   @Consumes(MediaType.APPLICATION_JSON)
   @QueryParams(keys = "format", values = "json")
   @Path("/servers/{id}/backup_schedule")
   ListenableFuture<BackupSchedule> getBackupSchedule(@PathParam("id") int serverId);

   /**
    * @see CloudServersClient#deleteBackupSchedule
    */
   @DELETE
   @Fallback(FalseOnNotFoundOr404.class)
   @Path("/servers/{id}/backup_schedule")
   ListenableFuture<Boolean> deleteBackupSchedule(@PathParam("id") int serverId);

   /**
    * @see CloudServersClient#replaceBackupSchedule
    */
   @POST
   @Path("/servers/{id}/backup_schedule")
   ListenableFuture<Void> replaceBackupSchedule(@PathParam("id") int id,
         @BinderParam(BindBackupScheduleToJsonPayload.class) BackupSchedule backupSchedule);

   /**
    * @see CloudServersClient#listAddresses
    */
   @GET
   @Unwrap
   @Consumes(MediaType.APPLICATION_JSON)
   @QueryParams(keys = "format", values = "json")
   @Path("/servers/{id}/ips")
   ListenableFuture<Addresses> getAddresses(@PathParam("id") int serverId);

   /**
    * @see CloudServersClient#listPublicAddresses
    */
   @GET
   @Unwrap
   @Consumes(MediaType.APPLICATION_JSON)
   @QueryParams(keys = "format", values = "json")
   @Path("/servers/{id}/ips/public")
   @Fallback(EmptySetOnNotFoundOr404.class)
   ListenableFuture<? extends Set<String>> listPublicAddresses(@PathParam("id") int serverId);

   /**
    * @see CloudServersClient#listPrivateAddresses
    */
   @GET
   @Unwrap
   @Consumes(MediaType.APPLICATION_JSON)
   @QueryParams(keys = "format", values = "json")
   @Path("/servers/{id}/ips/private")
   @Fallback(EmptySetOnNotFoundOr404.class)
   ListenableFuture<? extends Set<String>> listPrivateAddresses(@PathParam("id") int serverId);

}
