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
import org.jclouds.cloudstack.binders.BindTemplateMetadataToQueryParams;
import org.jclouds.cloudstack.domain.AsyncCreateResponse;
import org.jclouds.cloudstack.domain.ExtractMode;
import org.jclouds.cloudstack.domain.Template;
import org.jclouds.cloudstack.domain.TemplateMetadata;
import org.jclouds.cloudstack.domain.TemplatePermission;
import org.jclouds.cloudstack.filters.AuthenticationFilter;
import org.jclouds.cloudstack.options.AccountInDomainOptions;
import org.jclouds.cloudstack.options.CreateTemplateOptions;
import org.jclouds.cloudstack.options.DeleteTemplateOptions;
import org.jclouds.cloudstack.options.ExtractTemplateOptions;
import org.jclouds.cloudstack.options.ListTemplatesOptions;
import org.jclouds.cloudstack.options.RegisterTemplateOptions;
import org.jclouds.cloudstack.options.UpdateTemplateOptions;
import org.jclouds.cloudstack.options.UpdateTemplatePermissionsOptions;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.OnlyElement;
import org.jclouds.rest.annotations.QueryParams;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.SelectJson;
import org.jclouds.rest.annotations.Unwrap;

/**
 * Provides synchronous access to cloudstack via their REST API.
 * <p/>
 * 
 * @see <a
 *      href="http://download.cloud.com/releases/2.2.0/api_2.2.12/TOC_User.html"
 *      />
 * @author Adrian Cole
 */
@RequestFilters(AuthenticationFilter.class)
@QueryParams(keys = "response", values = "json")
public interface TemplateApi {

   /**
    * Creates a template of a virtual machine. The virtual machine must be in a
    * STOPPED state. A template created from this command is automatically
    * designated as a private template visible to the account that created it.
    * 
    * @see http
    *      ://download.cloud.com/releases/2.2.0/api_2.2.8/user/createTemplate
    *      .html
    * @param templateMetadata
    *           overall description of the template
    * @param options
    *           optional arguments
    * @return an asynchronous job response
    */
   @Named("createTemplate")
   @GET
   @QueryParams(keys = "command", values = "createTemplate")
   @Unwrap
   @Consumes(MediaType.APPLICATION_JSON)
   AsyncCreateResponse createTemplate(
         @BinderParam(BindTemplateMetadataToQueryParams.class) TemplateMetadata templateMetadata,
         CreateTemplateOptions... options);

   /**
    * Registers an existing template into the Cloud.com cloud.
    * 
    * @see http 
    *      ://download.cloud.com/releases/2.2.0/api_2.2.8/user/registerTemplate
    *      .html
    * @param templateMetadata
    *           overall description of the template
    * @param format
    *           the format for the template. Possible values include QCOW2, RAW,
    *           and VHD.
    * 
    * @param url
    *           the URL of where the template is hosted. Possible URL include
    *           http:// and https://
    * @param zoneId
    *           the ID of the zone the template is to be hosted on
    * @param options
    *           optional arguments
    * @return data about the newly-registered template
    */
   @Named("registerTemplate")
   @GET
   @QueryParams(keys = "command", values = "registerTemplate")
   @SelectJson("template")
   @Consumes(MediaType.APPLICATION_JSON)
   Set<Template> registerTemplate(
         @BinderParam(BindTemplateMetadataToQueryParams.class) TemplateMetadata templateMetadata,
         @QueryParam("format") String format, @QueryParam("hypervisor") String hypervisor,
         @QueryParam("url") String url, @QueryParam("zoneid") String zoneId, RegisterTemplateOptions... options);

   /**
    * Updates attributes of a template.
    * 
    * @see http
    *      ://download.cloud.com/releases/2.2.0/api_2.2.8/user/updateTemplate
    *      .html
    * @param id
    *           the ID of the image file
    * @param options
    *           optional arguments
    * @return updated data about the template
    */
   @Named("updateTemplate")
   @GET
   @QueryParams(keys = "command", values = "updateTemplate")
   @SelectJson("template")
   @Consumes(MediaType.APPLICATION_JSON)
   Template updateTemplate(@QueryParam("id") String id, UpdateTemplateOptions... options);

   /**
    * Copies a template from one zone to another.
    * 
    * @see http 
    *      ://download.cloud.com/releases/2.2.0/api_2.2.8/user/copyTemplate.html
    * @param id
    *           Template ID.
    * @param sourceZoneId
    *           ID of the zone the template is currently hosted on.
    * @param destZoneId
    *           ID of the zone the template is being copied to.
    * @return an asynchronous job response
    */
   @Named("copyTemplate")
   @GET
   @QueryParams(keys = "command", values = "copyTemplate")
   @Unwrap
   @Consumes(MediaType.APPLICATION_JSON)
   AsyncCreateResponse copyTemplateToZone(@QueryParam("id") String id,
         @QueryParam("sourcezoneid") String sourceZoneId, @QueryParam("destzoneid") String destZoneId);

   /**
    * Deletes a template from the system. All virtual machines using the deleted
    * template will not be affected.
    * 
    * @see http
    *      ://download.cloud.com/releases/2.2.0/api_2.2.8/user/deleteTemplate
    *      .html
    * @param id
    *           the ID of the template
    * @param options
    *           optional arguments
    */
   @Named("deleteTemplate")
   @GET
   @QueryParams(keys = "command", values = "deleteTemplate")
   @Unwrap
   @Consumes(MediaType.APPLICATION_JSON)
   AsyncCreateResponse deleteTemplate(@QueryParam("id") String id, DeleteTemplateOptions... options);

   /**
    * List all executable templates.
    * 
    * @see http
    *      ://download.cloud.com/releases/2.2.0/api_2.2.8/user/listTemplates.
    *      html
    * @return all executable templates, or empty set, if no templates are found
    */
   @Named("listTemplates")
   @GET
   @QueryParams(keys = { "command", "listAll", "templatefilter" }, values = { "listTemplates", "true", "executable" })
   @SelectJson("template")
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(EmptySetOnNotFoundOr404.class)
   Set<Template> listTemplates();

   /**
    * List all public, private, and privileged templates.
    * 
    * @see http
    *      ://download.cloud.com/releases/2.2.0/api_2.2.8/user/listTemplates.
    *      html
    * @param options
    *           if present, how to constrain the list, defaults to all
    *           executable templates
    * @return templates matching query, or empty set, if no templates are found
    * @see TemplateFilter
    */
   @Named("listTemplates")
   @GET
   @QueryParams(keys = { "command", "listAll" }, values = { "listTemplates", "true" })
   @SelectJson("template")
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(EmptySetOnNotFoundOr404.class)
   Set<Template> listTemplates(ListTemplatesOptions options);

   /**
    * get a specific template by id
    * 
    * 
    * @param templateId
    * @param zoneId
    *           zone template is defined in
    * @return template or null if not found
    */
   @Named("listTemplates")
   @GET
   // templatefilter required in at least 2.2.8 version
   @QueryParams(keys = { "command", "listAll", "templatefilter" }, values = { "listTemplates", "true", "executable" })
   @SelectJson("template")
   @OnlyElement
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(NullOnNotFoundOr404.class)
   Template getTemplateInZone(@QueryParam("id") String templateId, @QueryParam("zoneid") String zoneId);

   /**
    * Updates a template visibility permissions. A public template is visible to
    * all accounts within the same domain. A private template is visible only to
    * the owner of the template. A privileged template is a private template
    * with account permissions added. Only accounts specified under the template
    * permissions are visible to them.
    * 
    * @see http://download.cloud.com/releases/2.2.0/api_2.2.8/user/
    *      updateTemplatePermissions.html
    * @param id
    *           the template ID
    * @param options
    *           optional arguments
    */
   @Named("updateTemplatePermissions")
   @GET
   @QueryParams(keys = "command", values = "updateTemplatePermissions")
   void updateTemplatePermissions(@QueryParam("id") String id,
         UpdateTemplatePermissionsOptions... options);

   /**
    * List template visibility and all accounts that have permissions to view
    * this template.
    * 
    * @see http://download.cloud.com/releases/2.2.0/api_2.2.8/user/
    *      listTemplatePermissions.html
    * @param id
    *           the template ID
    * @param options
    *           optional arguments
    * @return the list of permissions that apply to the template
    */
   @Named("listTemplatePermissions")
   @GET
   @QueryParams(keys = { "command", "listAll" }, values = { "listTemplatePermissions", "true" })
   @Unwrap
   @Consumes(MediaType.APPLICATION_JSON)
   Set<TemplatePermission> listTemplatePermissions(@QueryParam("id") String id,
         AccountInDomainOptions... options);

   /**
    * 
    * @see http 
    *      ://download.cloud.com/releases/2.2.0/api_2.2.8/user/extractTemplate
    *      .html
    * @param id
    *           the ID of the template
    * @param mode
    *           FIXME the mode of extraction - HTTP_DOWNLOAD or FTP_UPLOAD
    * @param zoneId
    *           the ID of the zone where the ISO is originally located
    * @param options
    *           optional arguments
    * @return an asynchronous job response
    */
   @Named("extractTemplate")
   @GET
   @QueryParams(keys = "command", values = "extractTemplate")
   @Unwrap
   @Consumes(MediaType.APPLICATION_JSON)
   AsyncCreateResponse extractTemplate(@QueryParam("id") String id,
         @QueryParam("mode") ExtractMode mode, @QueryParam("zoneid") String zoneId, ExtractTemplateOptions... options);
}
