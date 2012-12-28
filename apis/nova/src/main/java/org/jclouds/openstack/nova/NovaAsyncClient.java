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
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.MapBinder;
import org.jclouds.rest.annotations.Payload;
import org.jclouds.rest.annotations.PayloadParam;
import org.jclouds.rest.annotations.QueryParams;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.Unwrap;

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
 * 
 * @deprecated Deprecated in jclouds 1.6, to be removed in jclouds 1.7. See NovaAsyncApi.
 */
@RequestFilters({AuthenticateRequest.class, AddTimestampQuery.class})
@Endpoint(ServerManagement.class)
@Deprecated
public interface NovaAsyncClient {

   /**
    * @see NovaClient#listServers
    * 
    * @deprecated Deprecated in jclouds 1.6, to be removed in jclouds 1.7. See {@link org.jclouds.openstack.nova.v2_0.features.ServerAsyncApi#list()} in openstack-nova.
    */
   @GET
   @Unwrap
   @Consumes(MediaType.APPLICATION_JSON)
   @QueryParams(keys = "format", values = "json")
   @Path("/servers")
   @Fallback(EmptySetOnNotFoundOr404.class)
   @Deprecated
   ListenableFuture<? extends Set<Server>> listServers(ListOptions... options);

   /**
    * @see NovaClient#getServer
    * 
    * @deprecated Deprecated in jclouds 1.6, to be removed in jclouds 1.7. See {@link org.jclouds.openstack.nova.v2_0.features.ServerAsyncApi#get(String)} in openstack-nova.
    */
   @GET
   @Unwrap
   @Consumes(MediaType.APPLICATION_JSON)
   @QueryParams(keys = "format", values = "json")
   @Fallback(NullOnNotFoundOr404.class)
   @Path("/servers/{id}")
   @Deprecated
   ListenableFuture<Server> getServer(@PathParam("id") int id);

   /**
    * @see NovaClient#getServer
    * 
    * @deprecated Deprecated in jclouds 1.6, to be removed in jclouds 1.7. See {@link org.jclouds.openstack.nova.v2_0.features.ServerAsyncApi#get(String)} in openstack-nova.
    */
   @GET
   @Unwrap
   @Consumes(MediaType.APPLICATION_JSON)
   @QueryParams(keys = "format", values = "json")
   @Fallback(NullOnNotFoundOr404.class)
   @Path("/servers/{uuid}")
   @Deprecated
   ListenableFuture<Server> getServer(@PathParam("uuid") String uuid);

   /**
    * @see NovaClient#deleteServer
    * 
    * @deprecated Deprecated in jclouds 1.6, to be removed in jclouds 1.7. See {@link org.jclouds.openstack.nova.v2_0.features.ServerAsyncApi#delete(String)} in openstack-nova.
    */
   @DELETE
   @Consumes
   @Fallback(FalseOnNotFoundOr404.class)
   @Path("/servers/{id}")
   @Deprecated
   ListenableFuture<Boolean> deleteServer(@PathParam("id") int id);

    /**
     * @see NovaClient#deleteServer
     * 
     * @deprecated Deprecated in jclouds 1.6, to be removed in jclouds 1.7. See {@link org.jclouds.openstack.nova.v2_0.features.ServerAsyncApi#delete(String)} in openstack-nova.
     */
    @DELETE
    @Consumes
    @Fallback(FalseOnNotFoundOr404.class)
    @Path("/servers/{uuid}")
    @Deprecated
    ListenableFuture<Boolean> deleteServer(@PathParam("uuid") String uuid);

   /**
    * @see NovaClient#rebootServer
    * 
    * @deprecated Deprecated in jclouds 1.6, to be removed in jclouds 1.7. See {@link org.jclouds.openstack.nova.v2_0.features.ServerAsyncApi#reboot(String, RebootType)} in openstack-nova.
    */
   @POST
   @QueryParams(keys = "format", values = "json")
   @Path("/servers/{id}/action")
   @Consumes
   @Produces(MediaType.APPLICATION_JSON)
   @Payload("%7B\"reboot\":%7B\"type\":\"{type}\"%7D%7D")
   @Deprecated
   ListenableFuture<Void> rebootServer(@PathParam("id") int id, @PayloadParam("type") RebootType rebootType);

   /**
    * @see NovaClient#resizeServer
    * 
    * @deprecated Deprecated in jclouds 1.6, to be removed in jclouds 1.7. See {@link org.jclouds.openstack.nova.v2_0.features.ServerAsyncApi#resize(String, String)} in openstack-nova.
    */
   @POST
   @QueryParams(keys = "format", values = "json")
   @Path("/servers/{id}/action")
   @Consumes
   @Produces(MediaType.APPLICATION_JSON)
   @Payload("%7B\"resize\":%7B\"flavorId\":{flavorId}%7D%7D")
   @Deprecated
   ListenableFuture<Void> resizeServer(@PathParam("id") int id, @PayloadParam("flavorId") int flavorId);

   /**
    * @see NovaClient#confirmResizeServer
    * 
    * @deprecated Deprecated in jclouds 1.6, to be removed in jclouds 1.7. See {@link org.jclouds.openstack.nova.v2_0.features.ServerAsyncApi#confirmResize(String)} in openstack-nova.
    */
   @POST
   @QueryParams(keys = "format", values = "json")
   @Path("/servers/{id}/action")
   @Consumes
   @Produces(MediaType.APPLICATION_JSON)
   @Payload("{\"confirmResize\":null}")
   @Deprecated
   ListenableFuture<Void> confirmResizeServer(@PathParam("id") int id);

   /**
    * @see NovaClient#revertResizeServer
    * 
    * @deprecated Deprecated in jclouds 1.6, to be removed in jclouds 1.7. See {@link org.jclouds.openstack.nova.v2_0.features.ServerAsyncApi#revertResize(String)} in openstack-nova.
    */
   @POST
   @QueryParams(keys = "format", values = "json")
   @Path("/servers/{id}/action")
   @Consumes
   @Produces(MediaType.APPLICATION_JSON)
   @Payload("{\"revertResize\":null}")
   @Deprecated
   ListenableFuture<Void> revertResizeServer(@PathParam("id") int id);

   /**
    * @see NovaClient#createServer
    * 
    * @deprecated Deprecated in jclouds 1.6, to be removed in jclouds 1.7. See {@link org.jclouds.openstack.nova.v2_0.features.ServerAsyncApi#create(String, String, String, CreateServerOptions)} in openstack-nova.
    */
   @POST
   @Unwrap
   @Consumes(MediaType.APPLICATION_JSON)
   @QueryParams(keys = "format", values = "json")
   @Path("/servers")
   @MapBinder(CreateServerOptions.class)
   @Deprecated
   ListenableFuture<Server> createServer(@PayloadParam("name") String name, @PayloadParam("imageRef") String imageRef,
                                         @PayloadParam("flavorRef") String flavorRef, CreateServerOptions... options);

   /**
    * @see NovaClient#rebuildServer
    * 
    * @deprecated Deprecated in jclouds 1.6, to be removed in jclouds 1.7. See {@link org.jclouds.openstack.nova.v2_0.features.ServerAsyncApi#rebuild(String, RebuildServerOptions)} in openstack-nova.
    */
   @POST
   @QueryParams(keys = "format", values = "json")
   @Path("/servers/{id}/action")
   @Consumes
   @MapBinder(RebuildServerOptions.class)
   @Deprecated
   ListenableFuture<Void> rebuildServer(@PathParam("id") int id, RebuildServerOptions... options);


   /**
    * @see NovaClient#changeAdminPass
    * 
    * @deprecated Deprecated in jclouds 1.6, to be removed in jclouds 1.7. See {@link org.jclouds.openstack.nova.v2_0.features.ServerAsyncApi#changeAdminPass(String, String)} in openstack-nova.
    */
   @POST
   @Path("/servers/{id}/action")
   @Consumes
   @Produces(MediaType.APPLICATION_JSON)
   @Payload("%7B\"changePassword\":%7B\"adminPass\":\"{adminPass}\"%7D%7D")
   @Deprecated
   ListenableFuture<Void> changeAdminPass(@PathParam("id") int id, @PayloadParam("adminPass") String adminPass);

   /**
    * @see NovaClient#renameServer
    * 
    * @deprecated Deprecated in jclouds 1.6, to be removed in jclouds 1.7. See {@link org.jclouds.openstack.nova.v2_0.features.ServerAsyncApi#rename(String, String)} in openstack-nova.
    */
   @PUT
   @Path("/servers/{id}")
   @Consumes
   @Produces(MediaType.APPLICATION_JSON)
   @Payload("%7B\"server\":%7B\"name\":\"{name}\"%7D%7D")
   @Deprecated
   ListenableFuture<Void> renameServer(@PathParam("id") int id, @PayloadParam("name") String newName);

   /**
    * @see NovaClient#listFlavors
    * 
    * @deprecated Deprecated in jclouds 1.6, to be removed in jclouds 1.7. See {@link org.jclouds.openstack.nova.v2_0.features.FlavorAsyncApi#list()} in openstack-nova.
    */
   @GET
   @Unwrap
   @Consumes(MediaType.APPLICATION_JSON)
   @QueryParams(keys = "format", values = "json")
   @Path("/flavors")
   @Fallback(EmptySetOnNotFoundOr404.class)
   @Deprecated
   ListenableFuture<? extends Set<Flavor>> listFlavors(ListOptions... options);

   /**
    * @see NovaClient#getFlavor
    * 
    * @deprecated Deprecated in jclouds 1.6, to be removed in jclouds 1.7. See {@link org.jclouds.openstack.nova.v2_0.features.FlavorAsyncApi#get(String)} in openstack-nova.
    */
   @GET
   @Unwrap
   @Consumes(MediaType.APPLICATION_JSON)
   @QueryParams(keys = "format", values = "json")
   @Path("/flavors/{id}")
   @Fallback(NullOnNotFoundOr404.class)
   @Deprecated
   ListenableFuture<Flavor> getFlavor(@PathParam("id") int id);

   /**
    * @see NovaClient#getFlavor
    * 
    * @deprecated Deprecated in jclouds 1.6, to be removed in jclouds 1.7. See {@link org.jclouds.openstack.nova.v2_0.features.FlavorAsyncApi#get(String)} in openstack-nova.
    */
   @GET
   @Unwrap
   @Consumes(MediaType.APPLICATION_JSON)
   @QueryParams(keys = "format", values = "json")
   @Path("/flavors/{uuid}")
   @Fallback(NullOnNotFoundOr404.class)
   @Deprecated
   ListenableFuture<Flavor> getFlavor(@PathParam("uuid") String uuid);

   /**
    * @see NovaClient#listImages
    * 
    * @deprecated Deprecated in jclouds 1.6, to be removed in jclouds 1.7. See {@link org.jclouds.openstack.nova.v2_0.features.ImageAsyncApi#list()} in openstack-nova.
    */
   @GET
   @Unwrap
   @Consumes(MediaType.APPLICATION_JSON)
   @QueryParams(keys = "format", values = "json")
   @Path("/images")
   @Fallback(EmptySetOnNotFoundOr404.class)
   @Deprecated
   ListenableFuture<? extends Set<Image>> listImages(ListOptions... options);

   /**
    * @see NovaClient#getImage
    * 
    * @deprecated Deprecated in jclouds 1.6, to be removed in jclouds 1.7. See {@link org.jclouds.openstack.nova.v2_0.features.ImageAsyncApi#get(String)} in openstack-nova.
    */
   @GET
   @Unwrap
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(NullOnNotFoundOr404.class)
   @QueryParams(keys = "format", values = "json")
   @Path("/images/{id}")
   @Deprecated
   ListenableFuture<Image> getImage(@PathParam("id") int id);

   /**
    * @see NovaClient#getImage
    * 
    * @deprecated Deprecated in jclouds 1.6, to be removed in jclouds 1.7. See {@link org.jclouds.openstack.nova.v2_0.features.ImageAsyncApi#get(String)} in openstack-nova.
    */
   @GET
   @Unwrap
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(NullOnNotFoundOr404.class)
   @QueryParams(keys = "format", values = "json")
   @Path("/images/{uuid}")
   @Deprecated
   ListenableFuture<Image> getImage(@PathParam("uuid") String uuid);

   /**
    * @see NovaClient#deleteImage
    * 
    * @deprecated Deprecated in jclouds 1.6, to be removed in jclouds 1.7. See {@link org.jclouds.openstack.nova.v2_0.features.ImageAsyncApi#delete(String)} in openstack-nova.
    */
   @DELETE
   @Fallback(FalseOnNotFoundOr404.class)
   @Consumes
   @Path("/images/{id}")
   @Deprecated
   ListenableFuture<Boolean> deleteImage(@PathParam("id") int id);

   /**
    * @see NovaClient#deleteImage
    * 
    * @deprecated Deprecated in jclouds 1.6, to be removed in jclouds 1.7. See {@link org.jclouds.openstack.nova.v2_0.features.ImageAsyncApi#delete(String)} in openstack-nova.
    */
   @DELETE
   @Fallback(FalseOnNotFoundOr404.class)
   @Consumes
   @Path("/images/{id}")
   @Deprecated
   ListenableFuture<Boolean> deleteImage(@PathParam("id") String id);
   
   /**
    * @see NovaClient#createImageFromServer
    * 
    * @deprecated Deprecated in jclouds 1.6, to be removed in jclouds 1.7. See {@link org.jclouds.openstack.nova.v2_0.features.ServerAsyncApi#createImageFromServer(String, String)} in openstack-nova.
    */
   @POST
   @Unwrap
   @Consumes(MediaType.APPLICATION_JSON)
   @QueryParams(keys = "format", values = "json")
   @Path("/images")
   @Produces(MediaType.APPLICATION_JSON)
   @Payload("%7B\"image\":%7B\"serverId\":{serverId},\"name\":\"{name}\"%7D%7D")
   @Deprecated
   ListenableFuture<Image> createImageFromServer(@PayloadParam("name") String imageName,
                                                 @PayloadParam("serverId") int serverId);

   /**
    * @see NovaClient#getAddresses
    * 
    * @deprecated Deprecated in jclouds 1.6, to be removed in jclouds 1.7. See {@link org.jclouds.openstack.nova.v2_0.features.ServerAsyncApi#get(String)} in openstack-nova.
    */
   @GET
   @Unwrap
   @Consumes(MediaType.APPLICATION_JSON)
   @QueryParams(keys = "format", values = "json")
   @Path("/servers/{id}/ips")
   @Deprecated
   ListenableFuture<Addresses> getAddresses(@PathParam("id") int serverId);

   /**
    * @see NovaClient#listPublicAddresses
    * 
    * @deprecated Deprecated in jclouds 1.6, to be removed in jclouds 1.7. See {@link org.jclouds.openstack.nova.v2_0.features.ServerAsyncApi#get(String)} in openstack-nova.
    */
   @GET
   @Unwrap
   @Consumes(MediaType.APPLICATION_JSON)
   @QueryParams(keys = "format", values = "json")
   @Path("/servers/{id}/ips/public")
   @Fallback(EmptySetOnNotFoundOr404.class)
   @Deprecated
   ListenableFuture<? extends Set<String>> listPublicAddresses(@PathParam("id") int serverId);

   /**
    * @see NovaClient#listPrivateAddresses
    * 
    * @deprecated Deprecated in jclouds 1.6, to be removed in jclouds 1.7. See {@link org.jclouds.openstack.nova.v2_0.features.ServerAsyncApi#get(String)} in openstack-nova.
    */
   @GET
   @Unwrap
   @Consumes(MediaType.APPLICATION_JSON)
   @QueryParams(keys = "format", values = "json")
   @Path("/servers/{id}/ips/private")
   @Fallback(EmptySetOnNotFoundOr404.class)
   @Deprecated
   ListenableFuture<? extends Set<String>> listPrivateAddresses(@PathParam("id") int serverId);
   
   /**
    * @deprecated Deprecated in jclouds 1.6, to be removed in jclouds 1.7. See {@link org.jclouds.openstack.nova.v2_0.extensions.FloatingIPAsyncApi#addToServer(String, String)} in openstack-nova.
    */
   @POST
   @Path("/servers/{id}/action")
   @Consumes
   @Produces(MediaType.APPLICATION_JSON)
   @Payload("%7B\"addFloatingIp\":%7B\"address\":\"{address}\"%7D%7D")
   @Deprecated
   ListenableFuture<Void> addFloatingIP(@PathParam("id") int serverId, @PayloadParam("address") String ip);

   /**
    * @deprecated Deprecated in jclouds 1.6, to be removed in jclouds 1.7. See {@link org.jclouds.openstack.nova.v2_0.extensions.FloatingIPAsyncApi#list()} in openstack-nova.
    */
   @GET
   @Unwrap
   @Consumes(MediaType.APPLICATION_JSON)
   @QueryParams(keys = "format", values = "json")
   @Path("/os-floating-ips")
   @Fallback(EmptySetOnNotFoundOr404.class)
   @Deprecated
   ListenableFuture<? extends Set<FloatingIP>> listFloatingIPs();
   
   /**
    * @deprecated Deprecated in jclouds 1.6, to be removed in jclouds 1.7. See {@link org.jclouds.openstack.nova.v2_0.extensions.FloatingIPAsyncApi#get(String)} in openstack-nova.
    */
   @GET
   @Unwrap
   @Consumes(MediaType.APPLICATION_JSON)
   @QueryParams(keys = "format", values = "json")
   @Path("/os-floating-ips/{id}")
   @Fallback(NullOnNotFoundOr404.class)
   @Deprecated
   ListenableFuture<FloatingIP> getFloatingIP(@PathParam("id") int id);

   /**
    * @deprecated Deprecated in jclouds 1.6, to be removed in jclouds 1.7. See {@link org.jclouds.openstack.nova.v2_0.extensions.SecurityGroupAsyncApi#list()} in openstack-nova.
    */
   @GET
   @Unwrap
   @Consumes(MediaType.APPLICATION_JSON)
   @QueryParams(keys = "format", values = "json")
   @Path("/os-security-groups")
   @Fallback(EmptySetOnNotFoundOr404.class)
   @Deprecated
   ListenableFuture<? extends Set<SecurityGroup>> listSecurityGroups();
   
   /**
    * @deprecated Deprecated in jclouds 1.6, to be removed in jclouds 1.7. See {@link org.jclouds.openstack.nova.v2_0.extensions.SecurityGroupAsyncApi#get(String)} in openstack-nova.
    */
   @GET
   @Unwrap
   @Consumes(MediaType.APPLICATION_JSON)
   @QueryParams(keys = "format", values = "json")
   @Path("/os-security-groups/{id}")
   @Fallback(NullOnNotFoundOr404.class)
   @Deprecated
   ListenableFuture<SecurityGroup> getSecurityGroup(@PathParam("id") int id);

}
