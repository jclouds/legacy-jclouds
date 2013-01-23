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
package org.jclouds.fujitsu.fgcp.services;

import java.util.Set;

import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.jclouds.fujitsu.fgcp.FGCPAsyncApi;
import org.jclouds.fujitsu.fgcp.binders.BindAlsoToSystemId;
import org.jclouds.fujitsu.fgcp.compute.functions.SingleElementResponseToElement;
import org.jclouds.fujitsu.fgcp.domain.BuiltinServer;
import org.jclouds.fujitsu.fgcp.domain.BuiltinServerBackup;
import org.jclouds.fujitsu.fgcp.domain.BuiltinServerConfiguration;
import org.jclouds.fujitsu.fgcp.domain.BuiltinServerStatus;
import org.jclouds.fujitsu.fgcp.filters.RequestAuthenticator;
import org.jclouds.fujitsu.fgcp.reference.RequestParameters;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.JAXBResponseParser;
import org.jclouds.rest.annotations.PayloadParams;
import org.jclouds.rest.annotations.QueryParams;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.Transform;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * Non-blocking API relating to built-in servers, also called extended function
 * module (EFM), such as a firewall or load balancer (SLB).
 * 
 * @author Dies Koper
 */
@RequestFilters(RequestAuthenticator.class)
@QueryParams(keys = RequestParameters.VERSION, values = FGCPAsyncApi.VERSION)
@PayloadParams(keys = RequestParameters.VERSION, values = FGCPAsyncApi.VERSION)
@Consumes(MediaType.TEXT_XML)
public interface BuiltinServerAsyncApi {

   @Named("StartEFM")
   @GET
   @JAXBResponseParser
   @QueryParams(keys = "Action", values = "StartEFM")
   ListenableFuture<Void> start(
         @BinderParam(BindAlsoToSystemId.class) @QueryParam("efmId") String id);

   @Named("StopEFM")
   @GET
   @JAXBResponseParser
   @QueryParams(keys = "Action", values = "StopEFM")
   ListenableFuture<Void> stop(
         @BinderParam(BindAlsoToSystemId.class) @QueryParam("efmId") String id);

   @Named("DestroyEFM")
   @GET
   @JAXBResponseParser
   @QueryParams(keys = "Action", values = "DestroyEFM")
   ListenableFuture<Void> destroy(
         @BinderParam(BindAlsoToSystemId.class) @QueryParam("efmId") String id);

   @Named("BackupEFM")
   @GET
   @JAXBResponseParser
   @QueryParams(keys = "Action", values = "BackupEFM")
   ListenableFuture<Void> backup(
         @BinderParam(BindAlsoToSystemId.class) @QueryParam("efmId") String id);

   @Named("RestoreEFM")
   @GET
   @JAXBResponseParser
   @QueryParams(keys = "Action", values = "RestoreEFM")
   ListenableFuture<Void> restore(
         @BinderParam(BindAlsoToSystemId.class) @QueryParam("efmId") String id,
         @QueryParam("backupId") String backupId);

   @Named("ListEFMBackup")
   @GET
   @JAXBResponseParser
   @QueryParams(keys = "Action", values = "ListEFMBackup")
   ListenableFuture<Set<BuiltinServerBackup>> listBackups(
         @BinderParam(BindAlsoToSystemId.class) @QueryParam("efmId") String id);

   @Named("DestroyEFMBackup")
   @GET
   @JAXBResponseParser
   @QueryParams(keys = "Action", values = "DestroyEFMBackup")
   ListenableFuture<Void> destroyBackup(
         @BinderParam(BindAlsoToSystemId.class) @QueryParam("efmId") String id,
         @QueryParam("backupId") String backupId);

   @Named("GetEFMAttributes")
   @GET
   @JAXBResponseParser
   @QueryParams(keys = "Action", values = "GetEFMAttributes")
   @Transform(SingleElementResponseToElement.class)
   ListenableFuture<BuiltinServer> get(
         @BinderParam(BindAlsoToSystemId.class) @QueryParam("efmId") String id);

   @Named("UpdateEFMAttribute")
   @GET
   @JAXBResponseParser
   @QueryParams(keys = "Action", values = "UpdateEFMAttribute")
   ListenableFuture<Void> update(
         @BinderParam(BindAlsoToSystemId.class) @QueryParam("efmId") String id,
         @QueryParam("attributeName") String name,
         @QueryParam("attributeValue") String value);

   @Named("GetEFMStatus")
   @GET
   @JAXBResponseParser
   @QueryParams(keys = "Action", values = "GetEFMStatus")
   @Transform(SingleElementResponseToElement.class)
   ListenableFuture<BuiltinServerStatus> getStatus(
         @BinderParam(BindAlsoToSystemId.class) @QueryParam("efmId") String id);

   @Named("GetEFMConfiguration")
   @GET
   @JAXBResponseParser
   @QueryParams(keys = "Action", values = "GetEFMConfiguration")
   @Transform(SingleElementResponseToElement.class)
   ListenableFuture<BuiltinServer> getConfiguration(
         @BinderParam(BindAlsoToSystemId.class) @QueryParam("efmId") String id,
         @QueryParam("configurationName") BuiltinServerConfiguration configuration);

//  @Named("GetEFMConfiguration")
//  @POST
//  @JAXBResponseParser
//  @QueryParams(keys = "Action", values = "GetEFMConfiguration")
//  @Transform(SingleElementResponseToElement.class)
//  ListenableFuture<Set<Rule>> getUpdateDetails(String id);

   // ListenableFuture<Void>
   // updateConfiguration(@BinderParam(BindAlsoToSystemId.class)
   // @QueryParam("efmId") String id, xml?);
//   EFM_UPDATE,       getUpdateStatus(String id);
}
