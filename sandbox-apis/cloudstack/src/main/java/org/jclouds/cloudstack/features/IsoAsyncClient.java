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
package org.jclouds.cloudstack.features;

import com.google.common.util.concurrent.ListenableFuture;
import org.jclouds.cloudstack.domain.AsyncCreateResponse;
import org.jclouds.cloudstack.domain.ExtractMode;
import org.jclouds.cloudstack.domain.Iso;
import org.jclouds.cloudstack.domain.IsoPermissions;
import org.jclouds.cloudstack.filters.QuerySigner;
import org.jclouds.cloudstack.options.AccountInDomainOptions;
import org.jclouds.cloudstack.options.DeleteIsoOptions;
import org.jclouds.cloudstack.options.ExtractIsoOptions;
import org.jclouds.cloudstack.options.ListIsosOptions;
import org.jclouds.cloudstack.options.RegisterIsoOptions;
import org.jclouds.cloudstack.options.UpdateIsoOptions;
import org.jclouds.cloudstack.options.UpdateIsoPermissionsOptions;
import org.jclouds.rest.annotations.QueryParams;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.SkipEncoding;
import org.jclouds.rest.annotations.Unwrap;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.util.Set;

/**
 * 
 * <p/>
 * 
 * @see IsoClient
 * @see http://download.cloud.com/releases/2.2.12/api/TOC_User.html
 * @author Richard Downer
 */
@RequestFilters(QuerySigner.class)
@QueryParams(keys = "response", values = "json")
@SkipEncoding({'/', ','})
public interface IsoAsyncClient {

   /**
    * Attaches an ISO to a virtual machine.
    *
    * @param isoId the ID of the ISO file
    * @param vmId the ID of the virtual machine
    * @return an asynchronous job response.
    */
   @GET
   @Consumes(MediaType.APPLICATION_JSON)
   @QueryParams(keys = "command", values = "attachIso")
   @Unwrap
   ListenableFuture<AsyncCreateResponse> attachIso(@QueryParam("id") long isoId, @QueryParam("virtualmachineid") long vmId);

   /**
    * Detaches any ISO file (if any) currently attached to a virtual machine.
    *
    * @param vmId The ID of the virtual machine
    * @return an asynchronous job response.
    */
   @GET
   @Consumes(MediaType.APPLICATION_JSON)
   @QueryParams(keys = "command", values = "detachIso")
   @Unwrap
   ListenableFuture<AsyncCreateResponse> detachIso(@QueryParam("virtualmachineid") long vmId);

   /**
    * Gets information about an ISO by its ID.
    *
    * @param id the ID of the ISO file
    * @return the ISO object matching the ID
    */
   @GET
   @Consumes(MediaType.APPLICATION_JSON)
   @QueryParams(keys = "command", values = "listIsos")
   @Unwrap
   ListenableFuture<Iso> getIso(@QueryParam("id") long id);

   /**
    * Lists all available ISO files.
    *
    * @param options optional arguments
    * @return a set of ISO objects the match the filter
    */
   @GET
   @Consumes(MediaType.APPLICATION_JSON)
   @QueryParams(keys = "command", values = "listIsos")
   @Unwrap
   ListenableFuture<Set<Iso>> listIsos(ListIsosOptions... options);

   /**
    * Registers an existing ISO into the Cloud.com Cloud.
    *
    * @param name the name of the ISO
    * @param displayText the display text of the ISO. This is usually used for display purposes.
    * @param url the URL to where the ISO is currently being hosted
    * @param zoneId the ID of the zone you wish to register the ISO to.
    * @param options optional arguments
    * @return the newly-added ISO
    */
   @GET
   @Consumes(MediaType.APPLICATION_JSON)
   @QueryParams(keys = "command", values = "registerIso")
   @Unwrap
   ListenableFuture<Iso> registerIso(@QueryParam("name") String name, @QueryParam("displaytext") String displayText, @QueryParam("url") String url, @QueryParam("zoneid") long zoneId, RegisterIsoOptions... options);

   /**
    * 
    *
    * @param id the ID of the ISO file
    * @param options optional arguments
    * @return the ISO object matching the ID
    */
   @GET
   @Consumes(MediaType.APPLICATION_JSON)
   @QueryParams(keys = "command", values = "updateIso")
   @Unwrap
   ListenableFuture<Iso> updateIso(@QueryParam("id") long id, UpdateIsoOptions... options);

   /**
    * Deletes an ISO file.
    *
    * @param id the ID of the ISO file
    * @param options optional arguments
    * @return an asynchronous job response.
    */
   @GET
   @Consumes(MediaType.APPLICATION_JSON)
   @QueryParams(keys = "command", values = "deleteIso")
   @Unwrap
   ListenableFuture<AsyncCreateResponse> deleteIso(@QueryParam("id") long id, DeleteIsoOptions... options);

   /**
    * Copies a template from one zone to another.
    *
    * @param isoId Template ID.
    * @param sourceZoneId ID of the zone the template is currently hosted on.
    * @param destZoneId ID of the zone the template is being copied to.
    * @return an asynchronous job response.
    */
   @GET
   @Consumes(MediaType.APPLICATION_JSON)
   @QueryParams(keys = "command", values = "copyIso")
   @Unwrap
   ListenableFuture<AsyncCreateResponse> copyIso(@QueryParam("id") long isoId, @QueryParam("sourcezoneid") long sourceZoneId, @QueryParam("destzoneid") long destZoneId);

   /**
    * Updates iso permissions
    *
    * @param id the template ID
    * @param options optional arguments
    * @return 
    */
   @GET
   @Consumes(MediaType.APPLICATION_JSON)
   @QueryParams(keys = "command", values = "updateIsoPermissions")
   @Unwrap
   ListenableFuture<Void> updateIsoPermissions(@QueryParam("id") long id, UpdateIsoPermissionsOptions... options);

   /**
    * List template visibility and all accounts that have permissions to view this template.
    *
    * @param id the template ID
    * @param options optional arguments
    * @return A set of the permissions on this ISO
    */
   @GET
   @Consumes(MediaType.APPLICATION_JSON)
   @QueryParams(keys = "command", values = "listIsoPermissions")
   @Unwrap
   ListenableFuture<Set<IsoPermissions>> listIsoPermissions(@QueryParam("id") long id, AccountInDomainOptions... options);

   /**
    * Extracts an ISO
    *
    * @param id the ID of the ISO file
    * @param mode the mode of extraction - HTTP_DOWNLOAD or FTP_UPLOAD
    * @param zoneId the ID of the zone where the ISO is originally located
    * @param options optional arguments
    * @return an asynchronous job response.
    */
   @GET
   @Consumes(MediaType.APPLICATION_JSON)
   @QueryParams(keys = "command", values = "extractIso")
   @Unwrap
   ListenableFuture<AsyncCreateResponse> extractIso(@QueryParam("id") long id, @QueryParam("mode") ExtractMode mode, @QueryParam("zoneid") long zoneId, ExtractIsoOptions... options);

}
