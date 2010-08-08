/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */
package org.jclouds.vcloud;

import static org.jclouds.vcloud.VCloudMediaType.CATALOGITEM_XML;
import static org.jclouds.vcloud.VCloudMediaType.CATALOG_XML;
import static org.jclouds.vcloud.VCloudMediaType.NETWORK_XML;
import static org.jclouds.vcloud.VCloudMediaType.ORG_XML;
import static org.jclouds.vcloud.VCloudMediaType.TASKSLIST_XML;
import static org.jclouds.vcloud.VCloudMediaType.TASK_XML;
import static org.jclouds.vcloud.VCloudMediaType.VAPPTEMPLATE_XML;
import static org.jclouds.vcloud.VCloudMediaType.VAPP_XML;
import static org.jclouds.vcloud.VCloudMediaType.VDC_XML;

import javax.annotation.Nullable;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.jclouds.predicates.validators.DnsNameValidator;
import org.jclouds.rest.annotations.Endpoint;
import org.jclouds.rest.annotations.EndpointParam;
import org.jclouds.rest.annotations.ExceptionParser;
import org.jclouds.rest.annotations.MapBinder;
import org.jclouds.rest.annotations.MapPayloadParam;
import org.jclouds.rest.annotations.ParamParser;
import org.jclouds.rest.annotations.ParamValidators;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.XMLResponseParser;
import org.jclouds.rest.functions.ReturnNullOnNotFoundOr404;
import org.jclouds.rest.functions.ReturnVoidOnNotFoundOr404;
import org.jclouds.vcloud.binders.BindCloneVAppParamsToXmlPayload;
import org.jclouds.vcloud.binders.BindInstantiateVAppTemplateParamsToXmlPayload;
import org.jclouds.vcloud.domain.Catalog;
import org.jclouds.vcloud.domain.CatalogItem;
import org.jclouds.vcloud.domain.Network;
import org.jclouds.vcloud.domain.Organization;
import org.jclouds.vcloud.domain.Task;
import org.jclouds.vcloud.domain.TasksList;
import org.jclouds.vcloud.domain.VApp;
import org.jclouds.vcloud.domain.VAppTemplate;
import org.jclouds.vcloud.domain.VDC;
import org.jclouds.vcloud.endpoints.Org;
import org.jclouds.vcloud.filters.SetVCloudTokenCookie;
import org.jclouds.vcloud.functions.OrgNameToEndpoint;
import org.jclouds.vcloud.functions.VAppIdToUri;
import org.jclouds.vcloud.functions.VAppTemplateIdToUri;
import org.jclouds.vcloud.options.CloneVAppOptions;
import org.jclouds.vcloud.options.InstantiateVAppTemplateOptions;
import org.jclouds.vcloud.xml.CatalogHandler;
import org.jclouds.vcloud.xml.CatalogItemHandler;
import org.jclouds.vcloud.xml.NetworkHandler;
import org.jclouds.vcloud.xml.OrgHandler;
import org.jclouds.vcloud.xml.TaskHandler;
import org.jclouds.vcloud.xml.TasksListHandler;
import org.jclouds.vcloud.xml.VAppHandler;
import org.jclouds.vcloud.xml.VAppTemplateHandler;
import org.jclouds.vcloud.xml.VDCHandler;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * Provides access to VCloud resources via their REST API.
 * <p/>
 * 
 * @see <a href="https://community.vcloudexpress.terremark.com/en-us/discussion_forums/f/60.aspx"
 *      />
 * @author Adrian Cole
 */
@RequestFilters(SetVCloudTokenCookie.class)
public interface VCloudAsyncClient {
   /**
    * @see VCloudClient#getDefaultOrganization
    */
   @Deprecated
   @GET
   @Endpoint(Org.class)
   @Consumes(ORG_XML)
   @XMLResponseParser(OrgHandler.class)
   ListenableFuture<? extends Organization> getDefaultOrganization();

   /**
    * @see VCloudClient#getOrganization
    */
   @Deprecated
   @GET
   @Endpoint(org.jclouds.vcloud.endpoints.VCloudApi.class)
   @Path("/org/{orgId}")
   @XMLResponseParser(OrgHandler.class)
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   @Consumes(ORG_XML)
   ListenableFuture<? extends Organization> getOrganization(@PathParam("orgId") String orgId);

   /**
    * @see VCloudClient#getOrganizationNamed
    */
   @GET
   @XMLResponseParser(OrgHandler.class)
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   @Consumes(ORG_XML)
   ListenableFuture<? extends Organization> getOrganizationNamed(
         @Nullable @EndpointParam(parser = OrgNameToEndpoint.class) String orgName);

   /**
    * @see VCloudClient#getDefaultCatalog
    */
   @GET
   @Endpoint(org.jclouds.vcloud.endpoints.Catalog.class)
   @Consumes(CATALOG_XML)
   @XMLResponseParser(CatalogHandler.class)
   ListenableFuture<? extends Catalog> getDefaultCatalog();

   /**
    * @see VCloudClient#getCatalog
    */
   @GET
   @Endpoint(org.jclouds.vcloud.endpoints.VCloudApi.class)
   @Path("/catalog/{catalogId}")
   @XMLResponseParser(CatalogHandler.class)
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   @Consumes(CATALOG_XML)
   ListenableFuture<? extends Catalog> getCatalog(@PathParam("catalogId") String catalogId);

   /**
    * @see VCloudClient#getVAppTemplate
    */
   @GET
   @Endpoint(org.jclouds.vcloud.endpoints.VCloudApi.class)
   @Path("/vAppTemplate/{vAppTemplateId}")
   @Consumes(VAPPTEMPLATE_XML)
   @XMLResponseParser(VAppTemplateHandler.class)
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<? extends VAppTemplate> getVAppTemplate(@PathParam("vAppTemplateId") String vAppTemplateId);

   /**
    * @see VCloudClient#getCatalogItem
    */
   @GET
   @Endpoint(org.jclouds.vcloud.endpoints.VCloudApi.class)
   @Path("/catalogItem/{catalogItemId}")
   @Consumes(CATALOGITEM_XML)
   @XMLResponseParser(CatalogItemHandler.class)
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<? extends CatalogItem> getCatalogItem(@PathParam("catalogItemId") String catalogItemId);

   /**
    * @see VCloudClient#getNetwork
    */
   @GET
   @Endpoint(org.jclouds.vcloud.endpoints.VCloudApi.class)
   @Path("/network/{networkId}")
   @Consumes(NETWORK_XML)
   @XMLResponseParser(NetworkHandler.class)
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<? extends Network> getNetwork(@PathParam("networkId") String networkId);

   /**
    * @see VCloudClient#getDefaultVDC
    */
   @GET
   @Endpoint(org.jclouds.vcloud.endpoints.VDC.class)
   @XMLResponseParser(VDCHandler.class)
   @Consumes(VDC_XML)
   ListenableFuture<? extends VDC> getDefaultVDC();

   /**
    * @see VCloudClient#getVDC
    */
   @GET
   @Endpoint(org.jclouds.vcloud.endpoints.VCloudApi.class)
   @Path("/vdc/{vDCId}")
   @XMLResponseParser(VDCHandler.class)
   @Consumes(VDC_XML)
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<? extends VDC> getVDC(@PathParam("vDCId") String vDCId);

   /**
    * @see VCloudClient#getTasksList
    */
   @GET
   @Endpoint(org.jclouds.vcloud.endpoints.VCloudApi.class)
   @Path("/tasksList/{tasksListId}")
   @Consumes(TASKSLIST_XML)
   @XMLResponseParser(TasksListHandler.class)
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<? extends TasksList> getTasksList(@PathParam("tasksListId") String tasksListId);

   /**
    * @see VCloudClient#getDefaultTasksList
    */
   @GET
   @Endpoint(org.jclouds.vcloud.endpoints.TasksList.class)
   @Consumes(TASKSLIST_XML)
   @XMLResponseParser(TasksListHandler.class)
   ListenableFuture<? extends TasksList> getDefaultTasksList();

   /**
    * @see VCloudClient#deployVApp
    */
   @POST
   @Consumes(TASK_XML)
   @Endpoint(org.jclouds.vcloud.endpoints.VCloudApi.class)
   @Path("/vApp/{vAppId}/action/deploy")
   @XMLResponseParser(TaskHandler.class)
   ListenableFuture<? extends Task> deployVApp(@PathParam("vAppId") String vAppId);

   /**
    * @see VCloudClient#deleteVApp
    */
   @DELETE
   @Endpoint(org.jclouds.vcloud.endpoints.VCloudApi.class)
   @ExceptionParser(ReturnVoidOnNotFoundOr404.class)
   @Path("/vApp/{vAppId}")
   ListenableFuture<Void> deleteVApp(@PathParam("vAppId") String vAppId);

   /**
    * @see VCloudClient#undeployVApp
    */
   @POST
   @Consumes(TASK_XML)
   @Endpoint(org.jclouds.vcloud.endpoints.VCloudApi.class)
   @Path("/vApp/{vAppId}/action/undeploy")
   @XMLResponseParser(TaskHandler.class)
   ListenableFuture<? extends Task> undeployVApp(@PathParam("vAppId") String vAppId);

   /**
    * @see VCloudClient#powerOnVApp
    */
   @POST
   @Consumes(TASK_XML)
   @Endpoint(org.jclouds.vcloud.endpoints.VCloudApi.class)
   @Path("/vApp/{vAppId}/power/action/powerOn")
   @XMLResponseParser(TaskHandler.class)
   ListenableFuture<? extends Task> powerOnVApp(@PathParam("vAppId") String vAppId);

   /**
    * @see VCloudClient#powerOffVApp
    */
   @POST
   @Consumes(TASK_XML)
   @Endpoint(org.jclouds.vcloud.endpoints.VCloudApi.class)
   @Path("/vApp/{vAppId}/power/action/powerOff")
   @XMLResponseParser(TaskHandler.class)
   ListenableFuture<? extends Task> powerOffVApp(@PathParam("vAppId") String vAppId);

   /**
    * @see VCloudClient#shutdownVApp
    */
   @POST
   @Endpoint(org.jclouds.vcloud.endpoints.VCloudApi.class)
   @Path("/vApp/{vAppId}/power/action/shutdown")
   ListenableFuture<Void> shutdownVApp(@PathParam("vAppId") String vAppId);

   /**
    * @see VCloudClient#resetVApp
    */
   @POST
   @Consumes(TASK_XML)
   @Endpoint(org.jclouds.vcloud.endpoints.VCloudApi.class)
   @Path("/vApp/{vAppId}/power/action/reset")
   @XMLResponseParser(TaskHandler.class)
   ListenableFuture<? extends Task> resetVApp(@PathParam("vAppId") String vAppId);

   /**
    * @see VCloudClient#suspendVApp
    */
   @POST
   @Consumes(TASK_XML)
   @Endpoint(org.jclouds.vcloud.endpoints.VCloudApi.class)
   @Path("/vApp/{vAppId}/power/action/suspend")
   @XMLResponseParser(TaskHandler.class)
   ListenableFuture<? extends Task> suspendVApp(@PathParam("vAppId") String vAppId);

   /**
    * @see VCloudClient#getTask
    */
   @GET
   @Consumes(TASK_XML)
   @Endpoint(org.jclouds.vcloud.endpoints.VCloudApi.class)
   @Path("/task/{taskId}")
   @XMLResponseParser(TaskHandler.class)
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<? extends Task> getTask(@PathParam("taskId") String taskId);

   /**
    * @see VCloudClient#cancelTask
    */
   @POST
   @Endpoint(org.jclouds.vcloud.endpoints.VCloudApi.class)
   @Path("/task/{taskId}/action/cancel")
   ListenableFuture<Void> cancelTask(@PathParam("taskId") String taskId);

   /**
    * @see VCloudClient#getVApp
    */
   @GET
   @Consumes(VAPP_XML)
   @Endpoint(org.jclouds.vcloud.endpoints.VCloudApi.class)
   @Path("/vApp/{vAppId}")
   @XMLResponseParser(VAppHandler.class)
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<? extends VApp> getVApp(@PathParam("vAppId") String appId);

   /**
    * @see VCloudClient#instantiateVAppTemplateInVDC
    */
   @POST
   @Endpoint(org.jclouds.vcloud.endpoints.VCloudApi.class)
   @Path("/vdc/{vDCId}/action/instantiateVAppTemplate")
   @Produces("application/vnd.vmware.vcloud.instantiateVAppTemplateParams+xml")
   @Consumes(VAPP_XML)
   @XMLResponseParser(VAppHandler.class)
   @MapBinder(BindInstantiateVAppTemplateParamsToXmlPayload.class)
   ListenableFuture<? extends VApp> instantiateVAppTemplateInVDC(@PathParam("vDCId") String vDCId,
         @MapPayloadParam("name") @ParamValidators(DnsNameValidator.class) String appName,
         @MapPayloadParam("template") @ParamParser(VAppTemplateIdToUri.class) String templateId,
         InstantiateVAppTemplateOptions... options);

   /**
    * @see VCloudClient#cloneVAppInVDC
    */
   @POST
   @Endpoint(org.jclouds.vcloud.endpoints.VCloudApi.class)
   @Path("/vdc/{vDCId}/action/cloneVApp")
   @Produces("application/vnd.vmware.vcloud.cloneVAppParams+xml")
   @Consumes(TASK_XML)
   @XMLResponseParser(TaskHandler.class)
   @MapBinder(BindCloneVAppParamsToXmlPayload.class)
   ListenableFuture<? extends Task> cloneVAppInVDC(@PathParam("vDCId") String vDCId,
         @MapPayloadParam("vApp") @ParamParser(VAppIdToUri.class) String vAppIdToClone,
         @MapPayloadParam("newName") @ParamValidators(DnsNameValidator.class) String newName,
         CloneVAppOptions... options);
}
