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
package org.jclouds.openstack.nova.v2_0.extensions;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.jclouds.openstack.keystone.v2_0.filters.AuthenticateRequest;
import org.jclouds.openstack.nova.v2_0.domain.BackupType;
import org.jclouds.openstack.nova.v2_0.functions.ParseImageIdFromLocationHeader;
import org.jclouds.openstack.nova.v2_0.options.CreateBackupOfServerOptions;
import org.jclouds.openstack.v2_0.ServiceType;
import org.jclouds.openstack.v2_0.services.Extension;
import org.jclouds.rest.annotations.ExceptionParser;
import org.jclouds.rest.annotations.Payload;
import org.jclouds.rest.annotations.PayloadParam;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.ResponseParser;
import org.jclouds.rest.annotations.SkipEncoding;
import org.jclouds.rest.annotations.WrapWith;
import org.jclouds.rest.functions.MapHttp4xxCodesToExceptions;
import org.jclouds.rest.functions.ReturnFalseOnNotFoundOr404;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * Provide access to Admin Server Actions via REST API
 *
 * @author Adam Lowe
 * @see org.jclouds.openstack.nova.v2_0.extensions.AdminActionsClient
 */
@Extension(of = ServiceType.COMPUTE, namespace = ExtensionNamespaces.ADMIN_ACTIONS)
@SkipEncoding( { '/', '=' })
@RequestFilters(AuthenticateRequest.class)
@Path("/servers/{id}/action")
public interface AdminActionsAsyncClient {

   /**
    * @see AdminActionsClient#suspendServer(String)
    */
   @POST
   @Produces(MediaType.APPLICATION_JSON)
   @Payload("{\"suspend\":null}")
   @ExceptionParser(ReturnFalseOnNotFoundOr404.class)
   ListenableFuture<Boolean> suspendServer(@PathParam("id") String id);

   /**
    * @see AdminActionsClient#resumeServer(String)
    */
   @POST
   @Produces(MediaType.APPLICATION_JSON)
   @Payload("{\"resume\":null}")
   @ExceptionParser(ReturnFalseOnNotFoundOr404.class)
   ListenableFuture<Boolean> resumeServer(@PathParam("id") String id);

   /**
    * @see AdminActionsClient#migrateServer(String)
    */
   @POST
   @Produces(MediaType.APPLICATION_JSON)
   @Payload("{\"migrate\":null}")
   @ExceptionParser(ReturnFalseOnNotFoundOr404.class)
   ListenableFuture<Boolean> migrateServer(@PathParam("id") String id);

   /**
    * @see AdminActionsClient#suspendServer(String)
    */
   @POST
   @Produces(MediaType.APPLICATION_JSON)
   @Payload("{\"lock\":null}")
   @ExceptionParser(ReturnFalseOnNotFoundOr404.class)
   ListenableFuture<Boolean> lockServer(@PathParam("id") String id);

   /**
    * @see AdminActionsClient#unlockServer(String)
    */
   @POST
   @Produces(MediaType.APPLICATION_JSON)
   @Payload("{\"unlock\":null}")
   @ExceptionParser(ReturnFalseOnNotFoundOr404.class)
   ListenableFuture<Boolean> unlockServer(@PathParam("id") String id);

   /**
    * @see AdminActionsClient#resetNetworkOfServer(String)
    */
   @POST
   @Produces(MediaType.APPLICATION_JSON)
   @Payload("{\"resetNetwork\":null}")
   @ExceptionParser(ReturnFalseOnNotFoundOr404.class)
   ListenableFuture<Boolean> resetNetworkOfServer(@PathParam("id") String id);

   /**
    * @see AdminActionsClient#createBackupOfServer
    */
   @POST
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   @WrapWith("createBackup")
   @ExceptionParser(MapHttp4xxCodesToExceptions.class)
   @ResponseParser(ParseImageIdFromLocationHeader.class)
   ListenableFuture<String> createBackupOfServer(@PathParam("id") String id,
                                                  @PayloadParam("name") String imageName,
                                                  @PayloadParam("backup_type") BackupType backupType,
                                                  @PayloadParam("rotation") int rotation,
                                                  CreateBackupOfServerOptions... options);

   /**
    * @see AdminActionsClient#pauseServer(String)
    */
   @POST
   @Produces(MediaType.APPLICATION_JSON)
   @Payload("{\"pause\":null}")
   @ExceptionParser(ReturnFalseOnNotFoundOr404.class)
   ListenableFuture<Boolean> pauseServer(@PathParam("id") String id);

   /**
    * @see AdminActionsClient#unpauseServer(String)
    */
   @POST
   @Produces(MediaType.APPLICATION_JSON)
   @Payload("{\"unpause\":null}")
   @ExceptionParser(ReturnFalseOnNotFoundOr404.class)
   ListenableFuture<Boolean> unpauseServer(@PathParam("id") String id);

   /**
    * @see AdminActionsClient#suspendServer(String)
    */
   @POST
   @Produces(MediaType.APPLICATION_JSON)
   @Payload("{\"injectNetworkInfo\":null}")
   @ExceptionParser(ReturnFalseOnNotFoundOr404.class)
   ListenableFuture<Boolean> injectNetworkInfoIntoServer(@PathParam("id") String id);

   /**
    * @see AdminActionsClient#migrateServer(String)
    */
   @POST
   @Produces(MediaType.APPLICATION_JSON)
   @ExceptionParser(ReturnFalseOnNotFoundOr404.class)
   @WrapWith("os-migrateLive")
   ListenableFuture<Boolean> liveMigrateServer(@PathParam("id") String id,
                                               @PayloadParam("host") String host,
                                               @PayloadParam("block_migration") boolean blockMigration,
                                               @PayloadParam("disk_over_commit") boolean diskOverCommit);
}
