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
package org.jclouds.openstack.nova.v2_0.extensions;

import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.jclouds.Fallbacks.EmptyFluentIterableOnNotFoundOr404;
import org.jclouds.Fallbacks.FalseOnNotFoundOr404;
import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.openstack.keystone.v2_0.filters.AuthenticateRequest;
import org.jclouds.openstack.nova.v2_0.domain.VolumeAttachment;
import org.jclouds.openstack.v2_0.ServiceType;
import org.jclouds.openstack.v2_0.services.Extension;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.PayloadParam;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.SelectJson;
import org.jclouds.rest.annotations.WrapWith;

import com.google.common.annotations.Beta;
import com.google.common.collect.FluentIterable;
import com.google.common.util.concurrent.ListenableFuture;

/**
 * Provides asynchronous access to Volume Attachments .
 * 
 * @see VolumeAttachmentApi
 * @author Everett Toews
 */
@Beta
@Extension(of = ServiceType.COMPUTE, namespace = ExtensionNamespaces.VOLUMES)
@RequestFilters(AuthenticateRequest.class)
public interface VolumeAttachmentAsyncApi {
   /**
    * @see VolumeAttachmentApi#listAttachmentsOnServer(String)
    */
   @Named("volumeattachment:list")
   @GET
   @Path("/servers/{server_id}/os-volume_attachments")
   @SelectJson("volumeAttachments")
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(EmptyFluentIterableOnNotFoundOr404.class)
   ListenableFuture<? extends FluentIterable<? extends VolumeAttachment>> listAttachmentsOnServer(
         @PathParam("server_id") String serverId);

   /**
    * @see VolumeAttachmentApi#getAttachmentForVolumeOnServer(String, String)
    */
   @Named("volumeattachment:get")
   @GET
   @Path("/servers/{server_id}/os-volume_attachments/{id}")
   @SelectJson("volumeAttachment")
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(NullOnNotFoundOr404.class)
   ListenableFuture<? extends VolumeAttachment> getAttachmentForVolumeOnServer(
         @PathParam("id") String volumeId,
         @PathParam("server_id") String serverId);

   /**
    * @see VolumeAttachmentApi#attachVolumeToServerAsDevice(String, String, String)
    */
   @Named("volumeattachment:attach")
   @POST
   @Path("/servers/{server_id}/os-volume_attachments")
   @SelectJson("volumeAttachment")
   @Produces(MediaType.APPLICATION_JSON)
   @Consumes(MediaType.APPLICATION_JSON)
   @WrapWith("volumeAttachment")
   ListenableFuture<? extends VolumeAttachment> attachVolumeToServerAsDevice(
         @PayloadParam("volumeId") String volumeId,
         @PathParam("server_id") String serverId, 
         @PayloadParam("device") String device);

   /**
    * @see VolumeAttachmentApi#detachVolumeFromServer(String, String)
    */
   @Named("volumeattachment:detach")
   @DELETE
   @Path("/servers/{server_id}/os-volume_attachments/{id}")
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(FalseOnNotFoundOr404.class)
   ListenableFuture<Boolean> detachVolumeFromServer(
         @PathParam("id") String volumeId, 
         @PathParam("server_id") String serverId);
}
