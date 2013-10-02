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
package org.jclouds.cloudstack.features;

import java.util.Set;

import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.jclouds.Fallbacks.EmptySetOnNotFoundOr404;
import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.cloudstack.domain.AsyncCreateResponse;
import org.jclouds.cloudstack.domain.ExtractMode;
import org.jclouds.cloudstack.domain.ISO;
import org.jclouds.cloudstack.domain.ISOPermissions;
import org.jclouds.cloudstack.filters.AuthenticationFilter;
import org.jclouds.cloudstack.options.AccountInDomainOptions;
import org.jclouds.cloudstack.options.DeleteISOOptions;
import org.jclouds.cloudstack.options.ExtractISOOptions;
import org.jclouds.cloudstack.options.ListISOsOptions;
import org.jclouds.cloudstack.options.RegisterISOOptions;
import org.jclouds.cloudstack.options.UpdateISOOptions;
import org.jclouds.cloudstack.options.UpdateISOPermissionsOptions;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.OnlyElement;
import org.jclouds.rest.annotations.QueryParams;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.SelectJson;
import org.jclouds.rest.annotations.Unwrap;

/**
 * 
 * <p/>
 * 
 * @see http://download.cloud.com/releases/2.2.12/api/TOC_User.html
 * @author Richard Downer
 */
@RequestFilters(AuthenticationFilter.class)
@QueryParams(keys = "response", values = "json")
public interface ISOApi {

   /**
    * Attaches an ISO to a virtual machine.
    *
    * @param isoId the ID of the ISO file
    * @param vmId the ID of the virtual machine
    * @return an asynchronous job response.
    */
   @Named("attachIso")
   @GET
   @Consumes(MediaType.APPLICATION_JSON)
   @QueryParams(keys = "command", values = "attachIso")
   @Unwrap
   AsyncCreateResponse attachISO(@QueryParam("id") String isoId, @QueryParam("virtualmachineid") String vmId);

   /**
    * Detaches any ISO file (if any) currently attached to a virtual machine.
    *
    * @param vmId The ID of the virtual machine
    * @return an asynchronous job response.
    */
   @Named("detachIso")
   @GET
   @Consumes(MediaType.APPLICATION_JSON)
   @QueryParams(keys = "command", values = "detachIso")
   @Unwrap
   AsyncCreateResponse detachISO(@QueryParam("virtualmachineid") String vmId);

   /**
    * Gets information about an ISO by its ID.
    *
    * @param id the ID of the ISO file
    * @return the ISO object matching the ID
    */
   @Named("listIsos")
   @GET
   @Consumes(MediaType.APPLICATION_JSON)
   @QueryParams(keys = { "command", "listAll" }, values = { "listIsos", "true" })
   @SelectJson("iso")
   @OnlyElement
   @Fallback(NullOnNotFoundOr404.class)
   ISO getISO(@QueryParam("id") String id);

   /**
    * Lists all available ISO files.
    *
    * @param options optional arguments
    * @return a set of ISO objects the match the filter
    */
   @Named("listIsos")
   @GET
   @Consumes(MediaType.APPLICATION_JSON)
   @QueryParams(keys = { "command", "listAll" }, values = { "listIsos", "true" })
   @SelectJson("iso")
   @Fallback(EmptySetOnNotFoundOr404.class)
   Set<ISO> listISOs(ListISOsOptions... options);

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
   @Named("registerIso")
   @GET
   @Consumes(MediaType.APPLICATION_JSON)
   @QueryParams(keys = "command", values = "registerIso")
   @SelectJson("iso")
   @OnlyElement
   ISO registerISO(@QueryParam("name") String name, @QueryParam("displaytext") String displayText, @QueryParam("url") String url, @QueryParam("zoneid") String zoneId, RegisterISOOptions... options);

   /**
    * 
    *
    * @param id the ID of the ISO file
    * @param options optional arguments
    * @return the ISO object matching the ID
    */
   @Named("updateIso")
   @GET
   @Consumes(MediaType.APPLICATION_JSON)
   @QueryParams(keys = "command", values = "updateIso")
   @Unwrap
   ISO updateISO(@QueryParam("id") String id, UpdateISOOptions... options);

   /**
    * Deletes an ISO file.
    *
    * @param id the ID of the ISO file
    * @param options optional arguments
    * @return an asynchronous job response.
    */
   @Named("deleteIso")
   @GET
   @Consumes(MediaType.APPLICATION_JSON)
   @QueryParams(keys = "command", values = "deleteIso")
   @Unwrap
   AsyncCreateResponse deleteISO(@QueryParam("id") String id, DeleteISOOptions... options);

   /**
    * Copies a template from one zone to another.
    *
    * @param isoId Template ID.
    * @param sourceZoneId ID of the zone the template is currently hosted on.
    * @param destZoneId ID of the zone the template is being copied to.
    * @return an asynchronous job response.
    */
   @Named("copyIso")
   @GET
   @Consumes(MediaType.APPLICATION_JSON)
   @QueryParams(keys = "command", values = "copyIso")
   @Unwrap
   AsyncCreateResponse copyISO(@QueryParam("id") String isoId, @QueryParam("sourcezoneid") String sourceZoneId, @QueryParam("destzoneid") String destZoneId);

   /**
    * Updates iso permissions
    *
    * @param id the template ID
    * @param options optional arguments
    * @return 
    */
   @Named("updateIsoPermissions")
   @GET
   @Consumes(MediaType.APPLICATION_JSON)
   @QueryParams(keys = "command", values = "updateIsoPermissions")
   @Unwrap
   void updateISOPermissions(@QueryParam("id") String id, UpdateISOPermissionsOptions... options);

   /**
    * List template visibility and all accounts that have permissions to view this template.
    *
    * @param id the template ID
    * @param options optional arguments
    * @return A set of the permissions on this ISO
    */
   @Named("listIsoPermissions")
   @GET
   @Consumes(MediaType.APPLICATION_JSON)
   @QueryParams(keys = { "command", "listAll" }, values = { "listIsoPermissions", "true" })
   @SelectJson("templatepermission")
   ISOPermissions listISOPermissions(@QueryParam("id") String id, AccountInDomainOptions... options);

   /**
    * Extracts an ISO
    *
    * @param id the ID of the ISO file
    * @param mode the mode of extraction - HTTP_DOWNLOAD or FTP_UPLOAD
    * @param zoneId the ID of the zone where the ISO is originally located
    * @param options optional arguments
    * @return an asynchronous job response.
    */
   @Named("extractIso")
   @GET
   @Consumes(MediaType.APPLICATION_JSON)
   @QueryParams(keys = "command", values = "extractIso")
   @Unwrap
   AsyncCreateResponse extractISO(@QueryParam("id") String id, @QueryParam("mode") ExtractMode mode, @QueryParam("zoneid") String zoneId, ExtractISOOptions... options);

}
