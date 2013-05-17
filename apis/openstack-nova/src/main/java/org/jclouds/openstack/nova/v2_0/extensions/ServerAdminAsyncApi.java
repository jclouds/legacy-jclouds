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
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.jclouds.Fallbacks.FalseOnNotFoundOr404;
import org.jclouds.fallbacks.MapHttp4xxCodesToExceptions;
import org.jclouds.openstack.keystone.v2_0.filters.AuthenticateRequest;
import org.jclouds.openstack.nova.v2_0.domain.BackupType;
import org.jclouds.openstack.nova.v2_0.functions.ParseImageIdFromLocationHeader;
import org.jclouds.openstack.nova.v2_0.options.CreateBackupOfServerOptions;
import org.jclouds.openstack.v2_0.ServiceType;
import org.jclouds.openstack.v2_0.services.Extension;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.Payload;
import org.jclouds.rest.annotations.PayloadParam;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.ResponseParser;
import org.jclouds.rest.annotations.WrapWith;

import com.google.common.annotations.Beta;
import com.google.common.util.concurrent.ListenableFuture;

/**
 * Provide access to Admin Server Actions via REST API
 *
 * @author Adam Lowe
 * @see org.jclouds.openstack.nova.v2_0.extensions.ServerAdminApi
 */
@Beta
@Extension(of = ServiceType.COMPUTE, namespace = ExtensionNamespaces.ADMIN_ACTIONS)
@RequestFilters(AuthenticateRequest.class)
@Path("/servers/{id}/action")
public interface ServerAdminAsyncApi {

   /**
    * @see ServerAdminApi#suspend(String)
    */
   @Named("serveradmin:suspend")
   @POST
   @Produces(MediaType.APPLICATION_JSON)
   @Payload("{\"suspend\":null}")
   @Fallback(FalseOnNotFoundOr404.class)
   ListenableFuture<Boolean> suspend(@PathParam("id") String id);

   /**
    * @see ServerAdminApi#resume(String)
    */
   @Named("serveradmin:resume")
   @POST
   @Produces(MediaType.APPLICATION_JSON)
   @Payload("{\"resume\":null}")
   @Fallback(FalseOnNotFoundOr404.class)
   ListenableFuture<Boolean> resume(@PathParam("id") String id);

   /**
    * @see ServerAdminApi#migrate(String)
    */
   @Named("serveradmin:migrate")
   @POST
   @Produces(MediaType.APPLICATION_JSON)
   @Payload("{\"migrate\":null}")
   @Fallback(FalseOnNotFoundOr404.class)
   ListenableFuture<Boolean> migrate(@PathParam("id") String id);

   /**
    * @see ServerAdminApi#lock(String)
    */
   @Named("serveradmin:lock")
   @POST
   @Produces(MediaType.APPLICATION_JSON)
   @Payload("{\"lock\":null}")
   @Fallback(FalseOnNotFoundOr404.class)
   ListenableFuture<Boolean> lock(@PathParam("id") String id);

   /**
    * @see ServerAdminApi#unlock(String)
    */
   @Named("serveradmin:unlock")
   @POST
   @Produces(MediaType.APPLICATION_JSON)
   @Payload("{\"unlock\":null}")
   @Fallback(FalseOnNotFoundOr404.class)
   ListenableFuture<Boolean> unlock(@PathParam("id") String id);

   /**
    * @see ServerAdminApi#resetNetwork(String)
    */
   @Named("serveradmin:resetnetwork")
   @POST
   @Produces(MediaType.APPLICATION_JSON)
   @Payload("{\"resetNetwork\":null}")
   @Fallback(FalseOnNotFoundOr404.class)
   ListenableFuture<Boolean> resetNetwork(@PathParam("id") String id);

   /**
    * @see ServerAdminApi#createBackup
    */
   @Named("serveradmin:createbackup")
   @POST
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   @WrapWith("createBackup")
   @Fallback(MapHttp4xxCodesToExceptions.class)
   @ResponseParser(ParseImageIdFromLocationHeader.class)
   ListenableFuture<String> createBackup(@PathParam("id") String id,
                                                  @PayloadParam("name") String imageName,
                                                  @PayloadParam("backup_type") BackupType backupType,
                                                  @PayloadParam("rotation") int rotation,
                                                  CreateBackupOfServerOptions... options);

   /**
    * @see ServerAdminApi#pause(String)
    */
   @Named("serveradmin:pause")
   @POST
   @Produces(MediaType.APPLICATION_JSON)
   @Payload("{\"pause\":null}")
   @Fallback(FalseOnNotFoundOr404.class)
   ListenableFuture<Boolean> pause(@PathParam("id") String id);

   /**
    * @see ServerAdminApi#unpause(String)
    */
   @Named("serveradmin:unpause")
   @POST
   @Produces(MediaType.APPLICATION_JSON)
   @Payload("{\"unpause\":null}")
   @Fallback(FalseOnNotFoundOr404.class)
   ListenableFuture<Boolean> unpause(@PathParam("id") String id);

   /**
    * @see ServerAdminApi#injectNetworkInfo(String)
    */
   @Named("serveradmin:injectnetwork")
   @POST
   @Produces(MediaType.APPLICATION_JSON)
   @Payload("{\"injectNetworkInfo\":null}")
   @Fallback(FalseOnNotFoundOr404.class)
   ListenableFuture<Boolean> injectNetworkInfo(@PathParam("id") String id);

   /**
    * @see ServerAdminApi#liveMigrate(String)
    */
   @Named("serveradmin:livemigrate")
   @POST
   @Produces(MediaType.APPLICATION_JSON)
   @Fallback(FalseOnNotFoundOr404.class)
   @WrapWith("os-migrateLive")
   ListenableFuture<Boolean> liveMigrate(@PathParam("id") String id,
                                               @PayloadParam("host") String host,
                                               @PayloadParam("block_migration") boolean blockMigration,
                                               @PayloadParam("disk_over_commit") boolean diskOverCommit);
}
