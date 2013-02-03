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

import com.google.common.util.concurrent.ListenableFuture;

/**
 * Provides asynchronous access to cloudstack via their REST API.
 * <p/>
 * 
 * @see TemplateClient
 * @see <a
 *      href="http://download.cloud.com/releases/2.2.0/api_2.2.12/TOC_User.html"
 *      />
 * @author Adrian Cole
 */
@RequestFilters(AuthenticationFilter.class)
@QueryParams(keys = "response", values = "json")
public interface TemplateAsyncClient {

   /**
    * @see TemplateClient#createTemplate
    */
   @Named("createTemplate")
   @GET
   @QueryParams(keys = "command", values = "createTemplate")
   @Unwrap
   @Consumes(MediaType.APPLICATION_JSON)
   ListenableFuture<AsyncCreateResponse> createTemplate(
         @BinderParam(BindTemplateMetadataToQueryParams.class) TemplateMetadata templateMetadata,
         CreateTemplateOptions... options);

   /**
    * @see TemplateClient#registerTemplate
    */
   @Named("registerTemplate")
   @GET
   @QueryParams(keys = "command", values = "registerTemplate")
   @SelectJson("template")
   @Consumes(MediaType.APPLICATION_JSON)
   ListenableFuture<Set<Template>> registerTemplate(
         @BinderParam(BindTemplateMetadataToQueryParams.class) TemplateMetadata templateMetadata,
         @QueryParam("format") String format, @QueryParam("hypervisor") String hypervisor,
         @QueryParam("url") String url, @QueryParam("zoneid") String zoneId, RegisterTemplateOptions... options);

   /**
    * @see TemplateClient#updateTemplate
    */
   @Named("updateTemplate")
   @GET
   @QueryParams(keys = "command", values = "updateTemplate")
   @SelectJson("template")
   @Consumes(MediaType.APPLICATION_JSON)
   ListenableFuture<Template> updateTemplate(@QueryParam("id") String id, UpdateTemplateOptions... options);

   /**
    * @see TemplateClient#copyTemplate
    */
   @Named("copyTemplate")
   @GET
   @QueryParams(keys = "command", values = "copyTemplate")
   @Unwrap
   @Consumes(MediaType.APPLICATION_JSON)
   ListenableFuture<AsyncCreateResponse> copyTemplateToZone(@QueryParam("id") String id,
         @QueryParam("sourcezoneid") String sourceZoneId, @QueryParam("destzoneid") String destZoneId);

   /**
    * @see TemplateClient#deleteTemplate
    */
   @Named("deleteTemplate")
   @GET
   @QueryParams(keys = "command", values = "deleteTemplate")
   @Unwrap
   @Consumes(MediaType.APPLICATION_JSON)
   ListenableFuture<AsyncCreateResponse> deleteTemplate(@QueryParam("id") String id, DeleteTemplateOptions... options);

   /**
    * @see TemplateClient#listTemplates
    */
   @Named("listTemplates")
   @GET
   @QueryParams(keys = { "command", "listAll", "templatefilter" }, values = { "listTemplates", "true", "executable" })
   @SelectJson("template")
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(EmptySetOnNotFoundOr404.class)
   ListenableFuture<Set<Template>> listTemplates();

   /**
    * @see TemplateClient#listTemplates(ListTemplatesOptions)
    */
   @Named("listTemplates")
   @GET
   @QueryParams(keys = { "command", "listAll" }, values = { "listTemplates", "true" })
   @SelectJson("template")
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(EmptySetOnNotFoundOr404.class)
   ListenableFuture<Set<Template>> listTemplates(ListTemplatesOptions options);

   /**
    * @see TemplateClient#getTemplate
    */
   @Named("listTemplates")
   @GET
   // templatefilter required in at least 2.2.8 version
   @QueryParams(keys = { "command", "listAll", "templatefilter" }, values = { "listTemplates", "true", "executable" })
   @SelectJson("template")
   @OnlyElement
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(NullOnNotFoundOr404.class)
   ListenableFuture<Template> getTemplateInZone(@QueryParam("id") String templateId, @QueryParam("zoneid") String zoneId);

   /**
    * @see TemplateClient#updateTemplatePermissions
    */
   @Named("updateTemplatePermissions")
   @GET
   @QueryParams(keys = "command", values = "updateTemplatePermissions")
   ListenableFuture<Void> updateTemplatePermissions(@QueryParam("id") String id,
         UpdateTemplatePermissionsOptions... options);

   /**
    * @see TemplateClient#listTemplatePermissions
    */
   @Named("listTemplatePermissions")
   @GET
   @QueryParams(keys = { "command", "listAll" }, values = { "listTemplatePermissions", "true" })
   @Unwrap
   @Consumes(MediaType.APPLICATION_JSON)
   ListenableFuture<Set<TemplatePermission>> listTemplatePermissions(@QueryParam("id") String id,
         AccountInDomainOptions... options);

   /**
    * @see TemplateClient#extractTemplate
    */
   @Named("extractTemplate")
   @GET
   @QueryParams(keys = "command", values = "extractTemplate")
   @Unwrap
   @Consumes(MediaType.APPLICATION_JSON)
   ListenableFuture<AsyncCreateResponse> extractTemplate(@QueryParam("id") String id,
         @QueryParam("mode") ExtractMode mode, @QueryParam("zoneid") String zoneId, ExtractTemplateOptions... options);
}
