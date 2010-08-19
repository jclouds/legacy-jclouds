/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
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

import static org.jclouds.vcloud.VCloudExpressMediaType.CATALOGITEM_XML;
import static org.jclouds.vcloud.VCloudExpressMediaType.CATALOG_XML;
import static org.jclouds.vcloud.VCloudExpressMediaType.NETWORK_XML;
import static org.jclouds.vcloud.VCloudExpressMediaType.ORG_XML;
import static org.jclouds.vcloud.VCloudExpressMediaType.TASKSLIST_XML;
import static org.jclouds.vcloud.VCloudExpressMediaType.TASK_XML;
import static org.jclouds.vcloud.VCloudExpressMediaType.VAPPTEMPLATE_XML;
import static org.jclouds.vcloud.VCloudExpressMediaType.VAPP_XML;
import static org.jclouds.vcloud.VCloudExpressMediaType.VDC_XML;

import java.net.URI;

import javax.annotation.Nullable;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.jclouds.predicates.validators.DnsNameValidator;
import org.jclouds.rest.annotations.Endpoint;
import org.jclouds.rest.annotations.EndpointParam;
import org.jclouds.rest.annotations.ExceptionParser;
import org.jclouds.rest.annotations.MapBinder;
import org.jclouds.rest.annotations.MapPayloadParam;
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
import org.jclouds.vcloud.functions.OrgNameAndCatalogNameToEndpoint;
import org.jclouds.vcloud.functions.OrgNameAndTasksListNameToEndpoint;
import org.jclouds.vcloud.functions.OrgNameAndVDCNameToEndpoint;
import org.jclouds.vcloud.functions.OrgNameCatalogNameItemNameToEndpoint;
import org.jclouds.vcloud.functions.OrgNameCatalogNameVAppTemplateNameToEndpoint;
import org.jclouds.vcloud.functions.OrgNameToEndpoint;
import org.jclouds.vcloud.functions.OrgNameVDCNameResourceEntityNameToEndpoint;
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
public interface VCloudExpressAsyncClient {
   /**
    * @see VCloudExpressClient#getDefaultOrganization
    */
   @Deprecated
   @GET
   @Endpoint(Org.class)
   @Consumes(ORG_XML)
   @XMLResponseParser(OrgHandler.class)
   ListenableFuture<? extends Organization> getDefaultOrganization();

   /**
    * @see VCloudExpressClient#getOrganization
    */
   @GET
   @XMLResponseParser(OrgHandler.class)
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   @Consumes(ORG_XML)
   ListenableFuture<? extends Organization> getOrganization(@EndpointParam URI orgId);

   /**
    * @see VCloudExpressClient#getOrganizationNamed
    */
   @GET
   @XMLResponseParser(OrgHandler.class)
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   @Consumes(ORG_XML)
   ListenableFuture<? extends Organization> findOrganizationNamed(
         @Nullable @EndpointParam(parser = OrgNameToEndpoint.class) String orgName);

   /**
    * @see VCloudExpressClient#getDefaultCatalog
    */
   @Deprecated
   @GET
   @Endpoint(org.jclouds.vcloud.endpoints.Catalog.class)
   @Consumes(CATALOG_XML)
   @XMLResponseParser(CatalogHandler.class)
   ListenableFuture<? extends Catalog> getDefaultCatalog();

   /**
    * @see VCloudExpressClient#getCatalog
    */
   @GET
   @XMLResponseParser(CatalogHandler.class)
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   @Consumes(CATALOG_XML)
   ListenableFuture<? extends Catalog> getCatalog(@EndpointParam URI catalogId);

   /**
    * @see VCloudExpressClient#findCatalogInOrgNamed
    */
   @GET
   @XMLResponseParser(CatalogHandler.class)
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   @Consumes(CATALOG_XML)
   ListenableFuture<? extends Catalog> findCatalogInOrgNamed(
         @Nullable @EndpointParam(parser = OrgNameAndCatalogNameToEndpoint.class) String orgName,
         @Nullable @EndpointParam(parser = OrgNameAndCatalogNameToEndpoint.class) String catalogName);

   /**
    * @see VCloudExpressClient#getVAppTemplate
    */
   @GET
   @Consumes(VAPPTEMPLATE_XML)
   @XMLResponseParser(VAppTemplateHandler.class)
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<? extends VAppTemplate> getVAppTemplate(@EndpointParam URI vAppTemplate);

   /**
    * @see VCloudExpressClient#findVAppTemplateInOrgCatalogNameds
    */
   @GET
   @Consumes(VAPPTEMPLATE_XML)
   @XMLResponseParser(VAppTemplateHandler.class)
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<? extends VAppTemplate> findVAppTemplateInOrgCatalogNamed(
         @Nullable @EndpointParam(parser = OrgNameCatalogNameVAppTemplateNameToEndpoint.class) String orgName,
         @Nullable @EndpointParam(parser = OrgNameCatalogNameVAppTemplateNameToEndpoint.class) String catalogName,
         @EndpointParam(parser = OrgNameCatalogNameVAppTemplateNameToEndpoint.class) String itemName);

   /**
    * @see VCloudExpressClient#getCatalogItem
    */
   @GET
   @Consumes(CATALOGITEM_XML)
   @XMLResponseParser(CatalogItemHandler.class)
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<? extends CatalogItem> getCatalogItem(@EndpointParam URI catalogItem);

   /**
    * @see VCloudExpressClient#getCatalogItemInOrg
    */
   @GET
   @Consumes(CATALOGITEM_XML)
   @XMLResponseParser(CatalogItemHandler.class)
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<? extends CatalogItem> findCatalogItemInOrgCatalogNamed(
         @Nullable @EndpointParam(parser = OrgNameCatalogNameItemNameToEndpoint.class) String orgName,
         @Nullable @EndpointParam(parser = OrgNameCatalogNameItemNameToEndpoint.class) String catalogName,
         @EndpointParam(parser = OrgNameCatalogNameItemNameToEndpoint.class) String itemName);

   /**
    * @see VCloudExpressClient#findNetworkInOrgVDCNamed
    */
   @GET
   @Consumes(NETWORK_XML)
   @XMLResponseParser(NetworkHandler.class)
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<? extends Network> findNetworkInOrgVDCNamed(
         @Nullable @EndpointParam(parser = OrgNameVDCNameResourceEntityNameToEndpoint.class) String orgName,
         @Nullable @EndpointParam(parser = OrgNameVDCNameResourceEntityNameToEndpoint.class) String catalogName,
         @EndpointParam(parser = OrgNameVDCNameResourceEntityNameToEndpoint.class) String networkName);

   /**
    * @see VCloudExpressClient#getNetwork
    */
   @GET
   @Consumes(NETWORK_XML)
   @XMLResponseParser(NetworkHandler.class)
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<? extends Network> getNetwork(@EndpointParam URI network);

   /**
    * @see VCloudExpressClient#getDefaultVDC
    */
   @Deprecated
   @GET
   @Endpoint(org.jclouds.vcloud.endpoints.VDC.class)
   @XMLResponseParser(VDCHandler.class)
   @Consumes(VDC_XML)
   ListenableFuture<? extends VDC> getDefaultVDC();

   /**
    * @see VCloudExpressClient#getVDC(URI)
    */
   @GET
   @XMLResponseParser(VDCHandler.class)
   @Consumes(VDC_XML)
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<? extends VDC> getVDC(@EndpointParam URI vdc);

   /**
    * @see VCloudExpressClient#findVDCInOrgNamed(String, String)
    */
   @GET
   @XMLResponseParser(VDCHandler.class)
   @Consumes(VDC_XML)
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<? extends VDC> findVDCInOrgNamed(
         @Nullable @EndpointParam(parser = OrgNameAndVDCNameToEndpoint.class) String orgName,
         @Nullable @EndpointParam(parser = OrgNameAndVDCNameToEndpoint.class) String vdcName);

   /**
    * @see VCloudExpressClient#getTasksList
    */
   @GET
   @Consumes(TASKSLIST_XML)
   @XMLResponseParser(TasksListHandler.class)
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<? extends TasksList> getTasksList(@EndpointParam URI tasksListId);

   /**
    * @see VCloudExpressClient#getTasksListInOrg
    */
   @GET
   @Consumes(TASKSLIST_XML)
   @XMLResponseParser(TasksListHandler.class)
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<? extends TasksList> findTasksListInOrgNamed(
         @Nullable @EndpointParam(parser = OrgNameAndTasksListNameToEndpoint.class) String orgName,
         @Nullable @EndpointParam(parser = OrgNameAndTasksListNameToEndpoint.class) String tasksListName);

   /**
    * @see VCloudExpressClient#getDefaultTasksList
    */
   @Deprecated
   @GET
   @Endpoint(org.jclouds.vcloud.endpoints.TasksList.class)
   @Consumes(TASKSLIST_XML)
   @XMLResponseParser(TasksListHandler.class)
   ListenableFuture<? extends TasksList> getDefaultTasksList();

   /**
    * @see VCloudExpressClient#deployVApp
    */
   @POST
   @Consumes(TASK_XML)
   @Path("/action/deploy")
   @XMLResponseParser(TaskHandler.class)
   ListenableFuture<? extends Task> deployVApp(@EndpointParam URI vAppId);

   /**
    * @see VCloudExpressClient#deleteVApp
    */
   @DELETE
   @ExceptionParser(ReturnVoidOnNotFoundOr404.class)
   ListenableFuture<Void> deleteVApp(@EndpointParam URI vAppId);

   /**
    * @see VCloudExpressClient#undeployVApp
    */
   @POST
   @Consumes(TASK_XML)
   @Path("/action/undeploy")
   @XMLResponseParser(TaskHandler.class)
   ListenableFuture<? extends Task> undeployVApp(@EndpointParam URI vAppId);

   /**
    * @see VCloudExpressClient#powerOnVApp
    */
   @POST
   @Consumes(TASK_XML)
   @Path("/power/action/powerOn")
   @XMLResponseParser(TaskHandler.class)
   ListenableFuture<? extends Task> powerOnVApp(@EndpointParam URI vAppId);

   /**
    * @see VCloudExpressClient#powerOffVApp
    */
   @POST
   @Consumes(TASK_XML)
   @Path("/power/action/powerOff")
   @XMLResponseParser(TaskHandler.class)
   ListenableFuture<? extends Task> powerOffVApp(@EndpointParam URI vAppId);

   /**
    * @see VCloudExpressClient#shutdownVApp
    */
   @POST
   @Path("/power/action/shutdown")
   ListenableFuture<Void> shutdownVApp(@EndpointParam URI vAppId);

   /**
    * @see VCloudExpressClient#resetVApp
    */
   @POST
   @Consumes(TASK_XML)
   @Path("/power/action/reset")
   @XMLResponseParser(TaskHandler.class)
   ListenableFuture<? extends Task> resetVApp(@EndpointParam URI vAppId);

   /**
    * @see VCloudExpressClient#suspendVApp
    */
   @POST
   @Consumes(TASK_XML)
   @Path("/power/action/suspend")
   @XMLResponseParser(TaskHandler.class)
   ListenableFuture<? extends Task> suspendVApp(@EndpointParam URI vAppId);

   /**
    * @see VCloudExpressClient#getTask
    */
   @GET
   @Consumes(TASK_XML)
   @XMLResponseParser(TaskHandler.class)
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<? extends Task> getTask(@EndpointParam URI taskId);

   /**
    * @see VCloudExpressClient#cancelTask
    */
   @POST
   @Path("/action/cancel")
   ListenableFuture<Void> cancelTask(@EndpointParam URI taskId);

   /**
    * @see VCloudExpressClient#findVAppInOrgVDCNamed
    */
   @GET
   @Consumes(VAPP_XML)
   @XMLResponseParser(VAppHandler.class)
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<? extends VApp> findVAppInOrgVDCNamed(
         @Nullable @EndpointParam(parser = OrgNameVDCNameResourceEntityNameToEndpoint.class) String orgName,
         @Nullable @EndpointParam(parser = OrgNameVDCNameResourceEntityNameToEndpoint.class) String catalogName,
         @EndpointParam(parser = OrgNameVDCNameResourceEntityNameToEndpoint.class) String vAppName);

   /**
    * @see VCloudExpressClient#getVApp
    */
   @GET
   @Consumes(VAPP_XML)
   @XMLResponseParser(VAppHandler.class)
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<? extends VApp> getVApp(@EndpointParam URI vApp);

   /**
    * @see VCloudExpressClient#instantiateVAppTemplateInVDC
    */
   @POST
   @Path("action/instantiateVAppTemplate")
   @Produces("application/vnd.vmware.vcloud.instantiateVAppTemplateParams+xml")
   @Consumes(VAPP_XML)
   @XMLResponseParser(VAppHandler.class)
   @MapBinder(BindInstantiateVAppTemplateParamsToXmlPayload.class)
   ListenableFuture<? extends VApp> instantiateVAppTemplateInVDC(@EndpointParam URI vdc,
         @MapPayloadParam("template") URI template,
         @MapPayloadParam("name") @ParamValidators(DnsNameValidator.class) String appName,
         InstantiateVAppTemplateOptions... options);

   /**
    * @see VCloudExpressClient#cloneVAppInVDC
    */
   @POST
   @Path("/action/cloneVApp")
   @Produces("application/vnd.vmware.vcloud.cloneVAppParams+xml")
   @Consumes(TASK_XML)
   @XMLResponseParser(TaskHandler.class)
   @MapBinder(BindCloneVAppParamsToXmlPayload.class)
   ListenableFuture<? extends Task> cloneVAppInVDC(@EndpointParam URI vdc, @MapPayloadParam("vApp") URI toClone,
         @MapPayloadParam("newName") @ParamValidators(DnsNameValidator.class) String newName,
         CloneVAppOptions... options);

}
