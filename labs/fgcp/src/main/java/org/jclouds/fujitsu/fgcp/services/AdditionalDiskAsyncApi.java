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

import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.jclouds.fujitsu.fgcp.FGCPAsyncApi;
import org.jclouds.fujitsu.fgcp.binders.BindAlsoToSystemId;
import org.jclouds.fujitsu.fgcp.compute.functions.SingleElementResponseToElement;
import org.jclouds.fujitsu.fgcp.domain.VDisk;
import org.jclouds.fujitsu.fgcp.domain.VDiskStatus;
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
 * Non-blocking API relating to additional storage.
 * 
 * @author Dies Koper
 */
@RequestFilters(RequestAuthenticator.class)
@QueryParams(keys = RequestParameters.VERSION, values = FGCPAsyncApi.VERSION)
@PayloadParams(keys = RequestParameters.VERSION, values = FGCPAsyncApi.VERSION)
@Consumes(MediaType.TEXT_XML)
public interface AdditionalDiskAsyncApi {

   @Named("GetVDiskStatus")
   @GET
   @JAXBResponseParser
   @QueryParams(keys = "Action", values = "GetVDiskStatus")
   @Transform(SingleElementResponseToElement.class)
   ListenableFuture<VDiskStatus> getStatus(
         @BinderParam(BindAlsoToSystemId.class) @QueryParam("vdiskId") String id);

   @Named("GetVDiskAttributes")
   @GET
   @JAXBResponseParser
   @QueryParams(keys = "Action", values = "GetVDiskAttributes")
   @Transform(SingleElementResponseToElement.class)
   ListenableFuture<VDisk> get(
         @BinderParam(BindAlsoToSystemId.class) @QueryParam("vdiskId") String id);

   @Named("UpdateVDiskAttribute")
   @GET
   @QueryParams(keys = "Action", values = "UpdateVDiskAttribute")
   ListenableFuture<Void> update(
         @BinderParam(BindAlsoToSystemId.class) @QueryParam("vdiskId") String id,
         @QueryParam("attributeName") String name,
         @QueryParam("attributeValue") String value);

   @Named("BackupVDisk")
   @GET
   @JAXBResponseParser
   @QueryParams(keys = "Action", values = "BackupVDisk")
   ListenableFuture<Void> backup(
         @BinderParam(BindAlsoToSystemId.class) @QueryParam("vdiskId") String id);

   @Named("RestoreVDisk")
   @GET
   @JAXBResponseParser
   @QueryParams(keys = "Action", values = "RestoreVDisk")
   ListenableFuture<Void> restore(@QueryParam("vsysId") String systemId,
         @QueryParam("backupId") String backupId);

   @Named("DestroyVDisk")
   @GET
   @JAXBResponseParser
   @QueryParams(keys = "Action", values = "DestroyVDisk")
   ListenableFuture<Void> destroy(
         @BinderParam(BindAlsoToSystemId.class) @QueryParam("vdiskId") String id);

   @Named("DetachVDisk")
   @GET
   @JAXBResponseParser
   @QueryParams(keys = "Action", values = "DetachVDisk")
   ListenableFuture<Void> detach(
         @BinderParam(BindAlsoToSystemId.class) @QueryParam("vdiskId") String diskId,
         @QueryParam("vserverId") String serverId);

   @Named("DestroyVDiskBackup")
   @GET
   @JAXBResponseParser
   @QueryParams(keys = "Action", values = "DestroyVDiskBackup")
   ListenableFuture<Void> destroyBackup(@QueryParam("vsysId") String sysId,
         @QueryParam("backupId") String backupId);

   // Set<> listBackups(String sysId);

}
