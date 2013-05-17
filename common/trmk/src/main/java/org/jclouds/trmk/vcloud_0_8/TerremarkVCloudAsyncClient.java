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
package org.jclouds.trmk.vcloud_0_8;

import static org.jclouds.trmk.vcloud_0_8.TerremarkVCloudMediaType.CATALOGITEMCUSTOMIZATIONPARAMETERS_XML;
import static org.jclouds.trmk.vcloud_0_8.TerremarkVCloudMediaType.CATALOGITEM_XML;
import static org.jclouds.trmk.vcloud_0_8.TerremarkVCloudMediaType.CATALOG_XML;
import static org.jclouds.trmk.vcloud_0_8.TerremarkVCloudMediaType.INTERNETSERVICESLIST_XML;
import static org.jclouds.trmk.vcloud_0_8.TerremarkVCloudMediaType.INTERNETSERVICE_XML;
import static org.jclouds.trmk.vcloud_0_8.TerremarkVCloudMediaType.NETWORK_XML;
import static org.jclouds.trmk.vcloud_0_8.TerremarkVCloudMediaType.NODESERVICE_XML;
import static org.jclouds.trmk.vcloud_0_8.TerremarkVCloudMediaType.ORG_XML;
import static org.jclouds.trmk.vcloud_0_8.TerremarkVCloudMediaType.PUBLICIPSLIST_XML;
import static org.jclouds.trmk.vcloud_0_8.TerremarkVCloudMediaType.PUBLICIP_XML;
import static org.jclouds.trmk.vcloud_0_8.TerremarkVCloudMediaType.TASKSLIST_XML;
import static org.jclouds.trmk.vcloud_0_8.TerremarkVCloudMediaType.TASK_XML;
import static org.jclouds.trmk.vcloud_0_8.TerremarkVCloudMediaType.VAPPTEMPLATE_XML;
import static org.jclouds.trmk.vcloud_0_8.TerremarkVCloudMediaType.VAPP_XML;
import static org.jclouds.trmk.vcloud_0_8.TerremarkVCloudMediaType.VDC_XML;

import java.io.Closeable;
import java.net.URI;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.jclouds.Fallbacks.EmptySetOnNotFoundOr404;
import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.Fallbacks.VoidOnNotFoundOr404;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.predicates.validators.DnsNameValidator;
import org.jclouds.rest.annotations.EndpointParam;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.MapBinder;
import org.jclouds.rest.annotations.ParamValidators;
import org.jclouds.rest.annotations.PayloadParam;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.ResponseParser;
import org.jclouds.rest.annotations.XMLResponseParser;
import org.jclouds.trmk.vcloud_0_8.TerremarkVCloudFallbacks.VoidOnDeleteDefaultIp;
import org.jclouds.trmk.vcloud_0_8.binders.BindCloneVAppParamsToXmlPayload;
import org.jclouds.trmk.vcloud_0_8.binders.BindInstantiateVAppTemplateParamsToXmlPayload;
import org.jclouds.trmk.vcloud_0_8.binders.BindNodeConfigurationToXmlPayload;
import org.jclouds.trmk.vcloud_0_8.binders.BindVAppConfigurationToXmlPayload;
import org.jclouds.trmk.vcloud_0_8.binders.OrgNameAndCatalogNameToEndpoint;
import org.jclouds.trmk.vcloud_0_8.binders.OrgNameAndTasksListNameToEndpoint;
import org.jclouds.trmk.vcloud_0_8.binders.OrgNameAndVDCNameToEndpoint;
import org.jclouds.trmk.vcloud_0_8.binders.OrgNameCatalogNameItemNameToEndpoint;
import org.jclouds.trmk.vcloud_0_8.binders.OrgNameCatalogNameVAppTemplateNameToEndpoint;
import org.jclouds.trmk.vcloud_0_8.binders.OrgNameVDCNameResourceEntityNameToEndpoint;
import org.jclouds.trmk.vcloud_0_8.domain.Catalog;
import org.jclouds.trmk.vcloud_0_8.domain.CatalogItem;
import org.jclouds.trmk.vcloud_0_8.domain.CustomizationParameters;
import org.jclouds.trmk.vcloud_0_8.domain.InternetService;
import org.jclouds.trmk.vcloud_0_8.domain.Network;
import org.jclouds.trmk.vcloud_0_8.domain.Node;
import org.jclouds.trmk.vcloud_0_8.domain.Protocol;
import org.jclouds.trmk.vcloud_0_8.domain.PublicIpAddress;
import org.jclouds.trmk.vcloud_0_8.domain.ReferenceType;
import org.jclouds.trmk.vcloud_0_8.domain.Task;
import org.jclouds.trmk.vcloud_0_8.domain.TasksList;
import org.jclouds.trmk.vcloud_0_8.domain.VApp;
import org.jclouds.trmk.vcloud_0_8.domain.VAppConfiguration;
import org.jclouds.trmk.vcloud_0_8.domain.VAppTemplate;
import org.jclouds.trmk.vcloud_0_8.domain.VDC;
import org.jclouds.trmk.vcloud_0_8.endpoints.Org;
import org.jclouds.trmk.vcloud_0_8.filters.SetVCloudTokenCookie;
import org.jclouds.trmk.vcloud_0_8.functions.OrgNameToEndpoint;
import org.jclouds.trmk.vcloud_0_8.functions.ParseTaskFromLocationHeader;
import org.jclouds.trmk.vcloud_0_8.functions.VDCURIToInternetServicesEndpoint;
import org.jclouds.trmk.vcloud_0_8.functions.VDCURIToPublicIPsEndpoint;
import org.jclouds.trmk.vcloud_0_8.options.AddInternetServiceOptions;
import org.jclouds.trmk.vcloud_0_8.options.AddNodeOptions;
import org.jclouds.trmk.vcloud_0_8.options.CloneVAppOptions;
import org.jclouds.trmk.vcloud_0_8.options.InstantiateVAppTemplateOptions;
import org.jclouds.trmk.vcloud_0_8.xml.CatalogHandler;
import org.jclouds.trmk.vcloud_0_8.xml.CatalogItemHandler;
import org.jclouds.trmk.vcloud_0_8.xml.CustomizationParametersHandler;
import org.jclouds.trmk.vcloud_0_8.xml.InternetServiceHandler;
import org.jclouds.trmk.vcloud_0_8.xml.InternetServicesHandler;
import org.jclouds.trmk.vcloud_0_8.xml.NetworkHandler;
import org.jclouds.trmk.vcloud_0_8.xml.NodeHandler;
import org.jclouds.trmk.vcloud_0_8.xml.NodesHandler;
import org.jclouds.trmk.vcloud_0_8.xml.OrgHandler;
import org.jclouds.trmk.vcloud_0_8.xml.PublicIpAddressesHandler;
import org.jclouds.trmk.vcloud_0_8.xml.TaskHandler;
import org.jclouds.trmk.vcloud_0_8.xml.TasksListHandler;
import org.jclouds.trmk.vcloud_0_8.xml.VAppHandler;
import org.jclouds.trmk.vcloud_0_8.xml.VAppTemplateHandler;
import org.jclouds.trmk.vcloud_0_8.xml.VDCHandler;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.inject.Provides;

/**
 * Provides access to VCloud resources via their REST API.
 * <p/>
 * 
 * @see <a href=
 *      "https://community.vcloudexpress.terremark.com/en-us/discussion_forums/f/60.aspx"
 *      />
 * @author Adrian Cole
 */
@RequestFilters(SetVCloudTokenCookie.class)
public interface TerremarkVCloudAsyncClient extends Closeable {

   /**
    * @see TerremarkVCloudClient#getCatalogItemInOrg
    */
   @GET
   @Consumes(CATALOGITEM_XML)
   @XMLResponseParser(CatalogItemHandler.class)
   @Fallback(NullOnNotFoundOr404.class)
   @MapBinder(OrgNameCatalogNameItemNameToEndpoint.class)
   ListenableFuture<? extends CatalogItem> findCatalogItemInOrgCatalogNamed(@Nullable @PayloadParam("orgName") String orgName,
         @Nullable @PayloadParam("catalogName") String catalogName, @PayloadParam("itemName") String itemName);

   /**
    * @see TerremarkVCloudClient#getCatalogItem
    */
   @GET
   @Consumes(CATALOGITEM_XML)
   @XMLResponseParser(CatalogItemHandler.class)
   @Fallback(NullOnNotFoundOr404.class)
   ListenableFuture<? extends CatalogItem> getCatalogItem(@EndpointParam URI catalogItem);

   /**
    * @see TerremarkVCloudClient#getTasksList
    */
   @GET
   @Consumes(TASKSLIST_XML)
   @XMLResponseParser(TasksListHandler.class)
   @Fallback(NullOnNotFoundOr404.class)
   ListenableFuture<? extends TasksList> getTasksList(@EndpointParam URI tasksListId);

   /**
    * @see TerremarkVCloudClient#findTasksListInOrgNamed
    */
   @GET
   @XMLResponseParser(TasksListHandler.class)
   @Fallback(NullOnNotFoundOr404.class)
   @Consumes(TASKSLIST_XML)
   @MapBinder(OrgNameAndTasksListNameToEndpoint.class)
   ListenableFuture<? extends TasksList> findTasksListInOrgNamed(@Nullable @PayloadParam("orgName") String orgName,
         @Nullable @PayloadParam("tasksListName") String tasksListName);

   /**
    * @see TerremarkVCloudClient#getTask
    */
   @GET
   @Consumes(TASK_XML)
   @XMLResponseParser(TaskHandler.class)
   @Fallback(NullOnNotFoundOr404.class)
   ListenableFuture<? extends Task> getTask(@EndpointParam URI taskId);

   /**
    * @see TerremarkVCloudClient#cancelTask
    */
   @POST
   @Path("/action/cancel")
   ListenableFuture<Void> cancelTask(@EndpointParam URI taskId);

   /**
    * 
    * @return a listing of all orgs that the current user has access to.
    */
   @Provides
   @Org
   Map<String, ReferenceType> listOrgs();

   /**
    * @see TerremarkVCloudClient#findCatalogInOrgNamed
    */
   @GET
   @XMLResponseParser(CatalogHandler.class)
   @Fallback(NullOnNotFoundOr404.class)
   @Consumes(CATALOG_XML)
   @MapBinder(OrgNameAndCatalogNameToEndpoint.class)
   ListenableFuture<? extends Catalog> findCatalogInOrgNamed(@Nullable @PayloadParam("orgName") String orgName,
         @Nullable @PayloadParam("catalogName") String catalogName);

   /**
    * @see VCloudClient#getVAppTemplate
    */
   @GET
   @Consumes(VAPPTEMPLATE_XML)
   @XMLResponseParser(VAppTemplateHandler.class)
   @Fallback(NullOnNotFoundOr404.class)
   ListenableFuture<? extends VAppTemplate> getVAppTemplate(@EndpointParam URI vAppTemplate);

   /**
    * @see VCloudClient#findVAppTemplateInOrgCatalogNamed
    */
   @GET
   @Consumes(VAPPTEMPLATE_XML)
   @XMLResponseParser(VAppTemplateHandler.class)
   @Fallback(NullOnNotFoundOr404.class)
   @MapBinder(OrgNameCatalogNameVAppTemplateNameToEndpoint.class)
   ListenableFuture<? extends VAppTemplate> findVAppTemplateInOrgCatalogNamed(@Nullable @PayloadParam("orgName") String orgName,
         @Nullable @PayloadParam("catalogName") String catalogName, @PayloadParam("itemName") String itemName);

   /**
    * @see VCloudClient#findNetworkInOrgVDCNamed
    */
   @GET
   @Consumes(NETWORK_XML)
   @XMLResponseParser(NetworkHandler.class)
   @Fallback(NullOnNotFoundOr404.class)
   @MapBinder(OrgNameVDCNameResourceEntityNameToEndpoint.class)
   ListenableFuture<? extends Network> findNetworkInOrgVDCNamed(@Nullable @PayloadParam("orgName") String orgName,
         @Nullable @PayloadParam("vdcName") String vdcName, @PayloadParam("resourceName") String networkName);

   /**
    * @see VCloudClient#getNetwork
    */
   @GET
   @Consumes(NETWORK_XML)
   @XMLResponseParser(NetworkHandler.class)
   @Fallback(NullOnNotFoundOr404.class)
   ListenableFuture<? extends Network> getNetwork(@EndpointParam URI network);

   /**
    * @see TerremarkVCloudClient#cloneVAppInVDC
    */
   @POST
   @Path("/action/cloneVApp")
   @Produces("application/vnd.vmware.vcloud.cloneVAppParams+xml")
   @Consumes(TASK_XML)
   @XMLResponseParser(TaskHandler.class)
   @MapBinder(BindCloneVAppParamsToXmlPayload.class)
   ListenableFuture<? extends Task> cloneVAppInVDC(@EndpointParam URI vdc, @PayloadParam("vApp") URI toClone,
         @PayloadParam("newName") @ParamValidators(DnsNameValidator.class) String newName, CloneVAppOptions... options);

   /**
    * @see VCloudClient#findVAppInOrgVDCNamed
    */
   @GET
   @Consumes(VAPP_XML)
   @XMLResponseParser(VAppHandler.class)
   @Fallback(NullOnNotFoundOr404.class)
   @MapBinder(OrgNameVDCNameResourceEntityNameToEndpoint.class)
   ListenableFuture<? extends VApp> findVAppInOrgVDCNamed(@Nullable @PayloadParam("orgName") String orgName,
         @Nullable @PayloadParam("vdcName") String vdcName, @PayloadParam("resourceName") String vAppName);

   /**
    * @see VCloudClient#getVApp
    */
   @GET
   @Consumes(VAPP_XML)
   @XMLResponseParser(VAppHandler.class)
   @Fallback(NullOnNotFoundOr404.class)
   ListenableFuture<? extends VApp> getVApp(@EndpointParam URI vApp);

   /**
    * @see TerremarkVCloudClient#deployVApp
    */
   @POST
   @Consumes(TASK_XML)
   @Path("/action/deploy")
   @XMLResponseParser(TaskHandler.class)
   ListenableFuture<? extends Task> deployVApp(@EndpointParam URI vAppId);

   /**
    * @see TerremarkVCloudClient#undeployVApp
    */
   @POST
   @Consumes(TASK_XML)
   @Path("/action/undeploy")
   @XMLResponseParser(TaskHandler.class)
   ListenableFuture<? extends Task> undeployVApp(@EndpointParam URI vAppId);

   /**
    * @see TerremarkVCloudClient#powerOnVApp
    */
   @POST
   @Consumes(TASK_XML)
   @Path("/power/action/powerOn")
   @XMLResponseParser(TaskHandler.class)
   ListenableFuture<? extends Task> powerOnVApp(@EndpointParam URI vAppId);

   /**
    * @see TerremarkVCloudClient#powerOffVApp
    */
   @POST
   @Consumes(TASK_XML)
   @Path("/power/action/powerOff")
   @XMLResponseParser(TaskHandler.class)
   ListenableFuture<? extends Task> powerOffVApp(@EndpointParam URI vAppId);

   /**
    * @see TerremarkVCloudClient#shutdownVApp
    */
   @POST
   @Path("/power/action/shutdown")
   ListenableFuture<Void> shutdownVApp(@EndpointParam URI vAppId);

   /**
    * @see TerremarkVCloudClient#resetVApp
    */
   @POST
   @Consumes(TASK_XML)
   @Path("/power/action/reset")
   @XMLResponseParser(TaskHandler.class)
   ListenableFuture<? extends Task> resetVApp(@EndpointParam URI vAppId);

   /**
    * @see TerremarkVCloudClient#suspendVApp
    */
   @POST
   @Consumes(TASK_XML)
   @Path("/power/action/suspend")
   @XMLResponseParser(TaskHandler.class)
   ListenableFuture<? extends Task> suspendVApp(@EndpointParam URI vAppId);

   /**
    * @see TerremarkVCloudClient#deleteVApp
    */
   @DELETE
   @ResponseParser(ParseTaskFromLocationHeader.class)
   @Fallback(NullOnNotFoundOr404.class)
   ListenableFuture<Task> deleteVApp(@EndpointParam URI vAppId);

   @GET
   @XMLResponseParser(OrgHandler.class)
   @Fallback(NullOnNotFoundOr404.class)
   @Consumes(ORG_XML)
   ListenableFuture<? extends org.jclouds.trmk.vcloud_0_8.domain.Org> getOrg(@EndpointParam URI orgId);

   /**
    * @see TerremarkVCloudClient#findOrgNamed
    */
   @GET
   @XMLResponseParser(OrgHandler.class)
   @Fallback(NullOnNotFoundOr404.class)
   @Consumes(ORG_XML)
   ListenableFuture<? extends org.jclouds.trmk.vcloud_0_8.domain.Org> findOrgNamed(
         @Nullable @EndpointParam(parser = OrgNameToEndpoint.class) String orgName);

   /**
    * Terremark does not have multiple catalogs, so we ignore this parameter.
    */
   @GET
   @XMLResponseParser(CatalogHandler.class)
   @Consumes(CATALOG_XML)
   @Fallback(NullOnNotFoundOr404.class)
   ListenableFuture<? extends Catalog> getCatalog(@EndpointParam URI catalogId);

   /**
    * @see TerremarkTerremarkVCloudClient#getVDC
    */
   @GET
   @XMLResponseParser(VDCHandler.class)
   @Consumes(VDC_XML)
   @Fallback(NullOnNotFoundOr404.class)
   ListenableFuture<? extends VDC> getVDC(@EndpointParam URI vdc);

   /**
    * @see TerremarkVCloudClient#findVDCInOrgNamed
    */
   @GET
   @XMLResponseParser(VDCHandler.class)
   @Consumes(VDC_XML)
   @Fallback(NullOnNotFoundOr404.class)
   @MapBinder(OrgNameAndVDCNameToEndpoint.class)
   ListenableFuture<? extends VDC> findVDCInOrgNamed(@Nullable @PayloadParam("orgName") String orgName,
         @Nullable @PayloadParam("vdcName") String vdcName);

   /**
    * @see TerremarkVCloudClient#instantiateVAppTemplateInVDC
    */
   @POST
   @Path("/action/instantiateVAppTemplate")
   @Produces("application/vnd.vmware.vcloud.instantiateVAppTemplateParams+xml")
   @Consumes(VAPP_XML)
   @XMLResponseParser(VAppHandler.class)
   @MapBinder(BindInstantiateVAppTemplateParamsToXmlPayload.class)
   ListenableFuture<? extends VApp> instantiateVAppTemplateInVDC(@EndpointParam URI vdc,
         @PayloadParam("template") URI template,
         @PayloadParam("name") @ParamValidators(DnsNameValidator.class) String appName,
         InstantiateVAppTemplateOptions... options);

   /**
    * @see TerremarkTerremarkVCloudClient#getAllInternetServicesInVDC
    */
   @GET
   @Consumes(INTERNETSERVICESLIST_XML)
   @XMLResponseParser(InternetServicesHandler.class)
   @Fallback(EmptySetOnNotFoundOr404.class)
   ListenableFuture<? extends Set<InternetService>> getAllInternetServicesInVDC(
         @EndpointParam(parser = VDCURIToInternetServicesEndpoint.class) URI vDCId);

   /**
    * @see TerremarkTerremarkVCloudClient#addInternetServiceToExistingIp
    */
   @POST
   @Path("/internetServices")
   @Produces(INTERNETSERVICE_XML)
   @Consumes(INTERNETSERVICE_XML)
   @XMLResponseParser(InternetServiceHandler.class)
   @MapBinder(AddInternetServiceOptions.class)
   ListenableFuture<? extends InternetService> addInternetServiceToExistingIp(@EndpointParam URI publicIpId,
         @PayloadParam("name") String serviceName, @PayloadParam("protocol") Protocol protocol,
         @PayloadParam("port") int port, AddInternetServiceOptions... options);

   /**
    * @see TerremarkTerremarkVCloudClient#deletePublicIp
    */
   @DELETE
   @Fallback(VoidOnDeleteDefaultIp.class)
   ListenableFuture<Void> deletePublicIp(@EndpointParam URI ipId);

   /**
    * @see TerremarkTerremarkVCloudClient#getInternetServicesOnPublicIP
    */
   @GET
   @Path("/internetServices")
   @Consumes(INTERNETSERVICESLIST_XML)
   @XMLResponseParser(InternetServicesHandler.class)
   @Fallback(EmptySetOnNotFoundOr404.class)
   ListenableFuture<? extends Set<InternetService>> getInternetServicesOnPublicIp(@EndpointParam URI ipId);

   /**
    * @see TerremarkTerremarkVCloudClient#getPublicIp
    */
   @GET
   @Consumes(PUBLICIP_XML)
   @XMLResponseParser(InternetServicesHandler.class)
   @Fallback(EmptySetOnNotFoundOr404.class)
   ListenableFuture<? extends Set<InternetService>> getPublicIp(@EndpointParam URI ipId);

   /**
    * @see TerremarkTerremarkVCloudClient#getPublicIpsAssociatedWithVDC
    */
   @GET
   @Consumes(PUBLICIPSLIST_XML)
   @XMLResponseParser(PublicIpAddressesHandler.class)
   @Fallback(EmptySetOnNotFoundOr404.class)
   ListenableFuture<? extends Set<PublicIpAddress>> getPublicIpsAssociatedWithVDC(
         @EndpointParam(parser = VDCURIToPublicIPsEndpoint.class) URI vDCId);

   /**
    * @see TerremarkTerremarkVCloudClient#deleteInternetService
    */
   @DELETE
   @Fallback(VoidOnNotFoundOr404.class)
   ListenableFuture<Void> deleteInternetService(@EndpointParam URI internetServiceId);

   /**
    * @see TerremarkTerremarkVCloudClient#getInternetService
    */
   @GET
   @Consumes(INTERNETSERVICESLIST_XML)
   @XMLResponseParser(InternetServiceHandler.class)
   @Fallback(NullOnNotFoundOr404.class)
   ListenableFuture<? extends InternetService> getInternetService(@EndpointParam URI internetServiceId);

   /**
    * @see TerremarkTerremarkVCloudClient#addNode
    */
   @POST
   @Path("/nodeServices")
   @Produces(NODESERVICE_XML)
   @Consumes(NODESERVICE_XML)
   @XMLResponseParser(NodeHandler.class)
   @MapBinder(AddNodeOptions.class)
   ListenableFuture<? extends Node> addNode(@EndpointParam URI internetServiceId,
         @PayloadParam("ipAddress") String ipAddress, @PayloadParam("name") String name,
         @PayloadParam("port") int port, AddNodeOptions... options);

   /**
    * @see TerremarkTerremarkVCloudClient#getNodes
    */
   @GET
   @Path("/nodeServices")
   @XMLResponseParser(NodesHandler.class)
   @Fallback(EmptySetOnNotFoundOr404.class)
   @Consumes(NODESERVICE_XML)
   ListenableFuture<? extends Set<Node>> getNodes(@EndpointParam URI internetServiceId);

   /**
    * @see TerremarkTerremarkVCloudClient#getNode
    */
   @GET
   @XMLResponseParser(NodeHandler.class)
   @Consumes(NODESERVICE_XML)
   @Fallback(NullOnNotFoundOr404.class)
   ListenableFuture<? extends Node> getNode(@EndpointParam URI nodeId);

   /**
    * @see TerremarkTerremarkVCloudClient#configureNode
    */
   @PUT
   @Produces(NODESERVICE_XML)
   @Consumes(NODESERVICE_XML)
   @XMLResponseParser(NodeHandler.class)
   @MapBinder(BindNodeConfigurationToXmlPayload.class)
   ListenableFuture<? extends Node> configureNode(@EndpointParam URI nodeId, @PayloadParam("name") String name,
         @PayloadParam("enabled") boolean enabled, @Nullable @PayloadParam("description") String description);

   /**
    * @see TerremarkTerremarkVCloudClient#deleteNode
    */
   @DELETE
   @Fallback(VoidOnNotFoundOr404.class)
   ListenableFuture<Void> deleteNode(@EndpointParam URI nodeId);

   /**
    * @see TerremarkTerremarkVCloudClient#configureVApp
    */
   @PUT
   @Produces(VAPP_XML)
   @Consumes(VAPP_XML)
   @MapBinder(BindVAppConfigurationToXmlPayload.class)
   @ResponseParser(ParseTaskFromLocationHeader.class)
   ListenableFuture<? extends Task> configureVApp(
         @EndpointParam(parser = BindVAppConfigurationToXmlPayload.class) VApp vApp, VAppConfiguration configuration);

   /**
    * @see TerremarkVCloudClient#getCustomizationOptions
    */
   @GET
   @XMLResponseParser(CustomizationParametersHandler.class)
   @Consumes(CATALOGITEMCUSTOMIZATIONPARAMETERS_XML)
   ListenableFuture<? extends CustomizationParameters> getCustomizationOptions(@EndpointParam URI customization);

}
