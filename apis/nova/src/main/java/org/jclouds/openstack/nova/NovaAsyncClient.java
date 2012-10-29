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
package org.jclouds.openstack.nova;

import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.jclouds.openstack.filters.AddTimestampQuery;
import org.jclouds.openstack.filters.AuthenticateRequest;
import org.jclouds.openstack.nova.domain.Addresses;
import org.jclouds.openstack.nova.domain.Flavor;
import org.jclouds.openstack.nova.domain.FloatingIP;
import org.jclouds.openstack.nova.domain.Image;
import org.jclouds.openstack.nova.domain.RebootType;
import org.jclouds.openstack.nova.domain.SecurityGroup;
import org.jclouds.openstack.nova.domain.Server;
import org.jclouds.openstack.nova.options.CreateServerOptions;
import org.jclouds.openstack.nova.options.ListOptions;
import org.jclouds.openstack.nova.options.RebuildServerOptions;
import org.jclouds.rest.annotations.Endpoint;
import org.jclouds.rest.annotations.ExceptionParser;
import org.jclouds.rest.annotations.MapBinder;
import org.jclouds.rest.annotations.Payload;
import org.jclouds.rest.annotations.PayloadParam;
import org.jclouds.rest.annotations.QueryParams;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.SkipEncoding;
import org.jclouds.rest.annotations.Unwrap;
import org.jclouds.rest.functions.ReturnEmptySetOnNotFoundOr404;
import org.jclouds.rest.functions.ReturnFalseOnNotFoundOr404;
import org.jclouds.rest.functions.ReturnNullOnNotFoundOr404;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * Provides asynchronous access to OpenStack Nova via their REST API.
 * <p/>
 * All commands return a ListenableFuture of the result from OpenStack Nova. Any exceptions incurred
 * during processing will be backend in an {@link ExecutionException} as documented in
 * {@link ListenableFuture#get()}.
 *
 * @author Adrian Cole
 * @see NovaClient
 * @see <a href="http://wiki.openstack.org/OpenStackAPI_1-1" />
 */
@SkipEncoding({'/', '='})
@RequestFilters({AuthenticateRequest.class, AddTimestampQuery.class})
@Endpoint(ServerManagement.class)
public interface NovaAsyncClient {

   /**
    * @see NovaClient#listServers
    */
   @GET
   @Unwrap
   @Consumes(MediaType.APPLICATION_JSON)
   @QueryParams(keys = "format", values = "json")
   @Path("/servers")
   @ExceptionParser(ReturnEmptySetOnNotFoundOr404.class)
   ListenableFuture<? extends Set<Server>> listServers(ListOptions... options);

   /**
    * @see NovaClient#getServer
    */
   @GET
   @Unwrap
   @Consumes(MediaType.APPLICATION_JSON)
   @QueryParams(keys = "format", values = "json")
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   @Path("/servers/{id}")
   ListenableFuture<Server> getServer(@PathParam("id") int id);

   /**
    * @see NovaClient#getServer
    */
   @GET
   @Unwrap
   @Consumes(MediaType.APPLICATION_JSON)
   @QueryParams(keys = "format", values = "json")
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   @Path("/servers/{uuid}")
   ListenableFuture<Server> getServer(@PathParam("uuid") String uuid);

   /**
    * @see NovaClient#deleteServer
    */
   @DELETE
   @Consumes
   @ExceptionParser(ReturnFalseOnNotFoundOr404.class)
   @Path("/servers/{id}")
   ListenableFuture<Boolean> deleteServer(@PathParam("id") int id);

    /**
     * @see NovaClient#deleteServer
     */
    @DELETE
    @Consumes
    @ExceptionParser(ReturnFalseOnNotFoundOr404.class)
    @Path("/servers/{uuid}")
    ListenableFuture<Boolean> deleteServer(@PathParam("uuid") String uuid);

   /**
    * @see NovaClient#rebootServer
    */
   @POST
   @QueryParams(keys = "format", values = "json")
   @Path("/servers/{id}/action")
   @Consumes
   @Produces(MediaType.APPLICATION_JSON)
   @Payload("%7B\"reboot\":%7B\"type\":\"{type}\"%7D%7D")
   ListenableFuture<Void> rebootServer(@PathParam("id") int id, @PayloadParam("type") RebootType rebootType);

   /**
    * @see NovaClient#resizeServer
    */
   @POST
   @QueryParams(keys = "format", values = "json")
   @Path("/servers/{id}/action")
   @Consumes
   @Produces(MediaType.APPLICATION_JSON)
   @Payload("%7B\"resize\":%7B\"flavorId\":{flavorId}%7D%7D")
   ListenableFuture<Void> resizeServer(@PathParam("id") int id, @PayloadParam("flavorId") int flavorId);

   /**
    * @see NovaClient#confirmResizeServer
    */
   @POST
   @QueryParams(keys = "format", values = "json")
   @Path("/servers/{id}/action")
   @Consumes
   @Produces(MediaType.APPLICATION_JSON)
   @Payload("{\"confirmResize\":null}")
   ListenableFuture<Void> confirmResizeServer(@PathParam("id") int id);

   /**
    * @see NovaClient#revertResizeServer
    */
   @POST
   @QueryParams(keys = "format", values = "json")
   @Path("/servers/{id}/action")
   @Consumes
   @Produces(MediaType.APPLICATION_JSON)
   @Payload("{\"revertResize\":null}")
   ListenableFuture<Void> revertResizeServer(@PathParam("id") int id);

   /**
    * @see NovaClient#createServer
    */
   @POST
   @Unwrap
   @Consumes(MediaType.APPLICATION_JSON)
   @QueryParams(keys = "format", values = "json")
   @Path("/servers")
   @MapBinder(CreateServerOptions.class)
   ListenableFuture<Server> createServer(@PayloadParam("name") String name, @PayloadParam("imageRef") String imageRef,
                                         @PayloadParam("flavorRef") String flavorRef, CreateServerOptions... options);

   /**
    * @see NovaClient#rebuildServer
    */
   @POST
   @QueryParams(keys = "format", values = "json")
   @Path("/servers/{id}/action")
   @Consumes
   @MapBinder(RebuildServerOptions.class)
   ListenableFuture<Void> rebuildServer(@PathParam("id") int id, RebuildServerOptions... options);


   /**
    * @see NovaClient#changeAdminPass
    */
   @POST
   @Path("/servers/{id}/action")
   @Consumes
   @Produces(MediaType.APPLICATION_JSON)
   @Payload("%7B\"changePassword\":%7B\"adminPass\":\"{adminPass}\"%7D%7D")
   ListenableFuture<Void> changeAdminPass(@PathParam("id") int id, @PayloadParam("adminPass") String adminPass);

   /**
    * @see NovaClient#renameServer
    */
   @PUT
   @Path("/servers/{id}")
   @Consumes
   @Produces(MediaType.APPLICATION_JSON)
   @Payload("%7B\"server\":%7B\"name\":\"{name}\"%7D%7D")
   ListenableFuture<Void> renameServer(@PathParam("id") int id, @PayloadParam("name") String newName);

   /**
    * @see NovaClient#listFlavors
    */
   @GET
   @Unwrap
   @Consumes(MediaType.APPLICATION_JSON)
   @QueryParams(keys = "format", values = "json")
   @Path("/flavors")
   @ExceptionParser(ReturnEmptySetOnNotFoundOr404.class)
   ListenableFuture<? extends Set<Flavor>> listFlavors(ListOptions... options);

   /**
    * @see NovaClient#getFlavor
    */
   @GET
   @Unwrap
   @Consumes(MediaType.APPLICATION_JSON)
   @QueryParams(keys = "format", values = "json")
   @Path("/flavors/{id}")
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<Flavor> getFlavor(@PathParam("id") int id);

   /**
    * @see NovaClient#getFlavor
    */
   @GET
   @Unwrap
   @Consumes(MediaType.APPLICATION_JSON)
   @QueryParams(keys = "format", values = "json")
   @Path("/flavors/{uuid}")
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<Flavor> getFlavor(@PathParam("uuid") String uuid);

   /**
    * @see NovaClient#listImages
    */
   @GET
   @Unwrap
   @Consumes(MediaType.APPLICATION_JSON)
   @QueryParams(keys = "format", values = "json")
   @Path("/images")
   @ExceptionParser(ReturnEmptySetOnNotFoundOr404.class)
   ListenableFuture<? extends Set<Image>> listImages(ListOptions... options);

   /**
    * @see NovaClient#getImage
    */
   @GET
   @Unwrap
   @Consumes(MediaType.APPLICATION_JSON)
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   @QueryParams(keys = "format", values = "json")
   @Path("/images/{id}")
   ListenableFuture<Image> getImage(@PathParam("id") int id);

   /**
    * @see NovaClient#getImage
    */
   @GET
   @Unwrap
   @Consumes(MediaType.APPLICATION_JSON)
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   @QueryParams(keys = "format", values = "json")
   @Path("/images/{uuid}")
   ListenableFuture<Image> getImage(@PathParam("uuid") String uuid);

   /**
    * @see NovaClient#deleteImage
    */
   @DELETE
   @ExceptionParser(ReturnFalseOnNotFoundOr404.class)
   @Consumes
   @Path("/images/{id}")
   ListenableFuture<Boolean> deleteImage(@PathParam("id") int id);

   /**
    * @see NovaClient#deleteImage
    */
   @DELETE
   @ExceptionParser(ReturnFalseOnNotFoundOr404.class)
   @Consumes
   @Path("/images/{id}")
   ListenableFuture<Boolean> deleteImage(@PathParam("id") String id);
   
   /**
    * @see NovaClient#createImageFromServer
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
    * @see NovaClient#getAddresses
    */
   @GET
   @Unwrap
   @Consumes(MediaType.APPLICATION_JSON)
   @QueryParams(keys = "format", values = "json")
   @Path("/servers/{id}/ips")
   ListenableFuture<Addresses> getAddresses(@PathParam("id") int serverId);

   /**
    * @see NovaClient#listPublicAddresses
    */
   @GET
   @Unwrap
   @Consumes(MediaType.APPLICATION_JSON)
   @QueryParams(keys = "format", values = "json")
   @Path("/servers/{id}/ips/public")
   @ExceptionParser(ReturnEmptySetOnNotFoundOr404.class)
   ListenableFuture<? extends Set<String>> listPublicAddresses(@PathParam("id") int serverId);

   /**
    * @see NovaClient#listPrivateAddresses
    */
   @GET
   @Unwrap
   @Consumes(MediaType.APPLICATION_JSON)
   @QueryParams(keys = "format", values = "json")
   @Path("/servers/{id}/ips/private")
   @ExceptionParser(ReturnEmptySetOnNotFoundOr404.class)
   ListenableFuture<? extends Set<String>> listPrivateAddresses(@PathParam("id") int serverId);
   
   @POST
   @Path("/servers/{id}/action")
   @Consumes
   @Produces(MediaType.APPLICATION_JSON)
   @Payload("%7B\"addFloatingIp\":%7B\"address\":\"{address}\"%7D%7D")
   ListenableFuture<Void> addFloatingIP(@PathParam("id") int serverId, @PayloadParam("address") String ip);

   @GET
   @Unwrap
   @Consumes(MediaType.APPLICATION_JSON)
   @QueryParams(keys = "format", values = "json")
   @Path("/os-floating-ips")
   @ExceptionParser(ReturnEmptySetOnNotFoundOr404.class)
   ListenableFuture<? extends Set<FloatingIP>> listFloatingIPs();
   
   @GET
   @Unwrap
   @Consumes(MediaType.APPLICATION_JSON)
   @QueryParams(keys = "format", values = "json")
   @Path("/os-floating-ips/{id}")
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<FloatingIP> getFloatingIP(@PathParam("id") int id);

   @GET
   @Unwrap
   @Consumes(MediaType.APPLICATION_JSON)
   @QueryParams(keys = "format", values = "json")
   @Path("/os-security-groups")
   @ExceptionParser(ReturnEmptySetOnNotFoundOr404.class)
   ListenableFuture<? extends Set<SecurityGroup>> listSecurityGroups();
   
   @GET
   @Unwrap
   @Consumes(MediaType.APPLICATION_JSON)
   @QueryParams(keys = "format", values = "json")
   @Path("/os-security-groups/{id}")
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<SecurityGroup> getSecurityGroup(@PathParam("id") int id);

}
