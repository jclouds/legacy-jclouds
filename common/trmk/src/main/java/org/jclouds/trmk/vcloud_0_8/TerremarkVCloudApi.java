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

import org.jclouds.Fallbacks;
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
import org.jclouds.trmk.vcloud_0_8.domain.KeyPair;
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
public interface TerremarkVCloudApi extends Closeable {
   @GET
   @XMLResponseParser(CatalogHandler.class)
   @Consumes(CATALOG_XML)
   @Fallback(Fallbacks.NullOnNotFoundOr404.class)
   Catalog getCatalog(@EndpointParam URI catalogId);

   /**
    * returns the catalog in the organization associated with the specified
    * name. Note that both parameters can be null to choose default.
    * 
    * @param orgName
    *           organization name, or null for the default
    * @param catalogName
    *           catalog name, or null for the default
    * @throws NoSuchElementException
    *            if you specified an org or catalog name that isn't present
    */
   @GET
   @XMLResponseParser(CatalogHandler.class)
   @Fallback(Fallbacks.NullOnNotFoundOr404.class)
   @Consumes(CATALOG_XML)
   @MapBinder(OrgNameAndCatalogNameToEndpoint.class)
   Catalog findCatalogInOrgNamed(@Nullable @PayloadParam("orgName") String orgName,
                                 @Nullable @PayloadParam("catalogName") String catalogName);

   @GET
   @Consumes(CATALOGITEM_XML)
   @XMLResponseParser(CatalogItemHandler.class)
   @Fallback(Fallbacks.NullOnNotFoundOr404.class)
   CatalogItem getCatalogItem(@EndpointParam URI catalogItem);

   /**
    * returns the catalog item in the catalog associated with the specified
    * name. Note that the org and catalog parameters can be null to choose
    * default.
    * 
    * @param orgName
    *           organization name, or null for the default
    * @param catalogName
    *           catalog name, or null for the default
    * @param itemName
    *           item you wish to lookup
    * 
    * @throws NoSuchElementException
    *            if you specified an org, catalog, or catalog item name that
    *            isn't present
    */
   @GET
   @Consumes(CATALOGITEM_XML)
   @XMLResponseParser(CatalogItemHandler.class)
   @Fallback(Fallbacks.NullOnNotFoundOr404.class)
   @MapBinder(OrgNameCatalogNameItemNameToEndpoint.class)
   CatalogItem findCatalogItemInOrgCatalogNamed(@Nullable @PayloadParam("orgName") String orgName,
                                                @Nullable @PayloadParam("catalogName") String catalogName, @PayloadParam("itemName") String itemName);

   @GET
   @Consumes(NETWORK_XML)
   @XMLResponseParser(NetworkHandler.class)
   @Fallback(Fallbacks.NullOnNotFoundOr404.class)
   @MapBinder(OrgNameVDCNameResourceEntityNameToEndpoint.class)
   Network findNetworkInOrgVDCNamed(@Nullable @PayloadParam("orgName") String orgName,
                                                                @Nullable @PayloadParam("vdcName") String vdcName, @PayloadParam("resourceName") String networkName);

   @GET
   @Consumes(NETWORK_XML)
   @XMLResponseParser(NetworkHandler.class)
   @Fallback(Fallbacks.NullOnNotFoundOr404.class)
   Network getNetwork(@EndpointParam URI network);

   /**
    * returns the VDC in the organization associated with the specified name.
    * Note that both parameters can be null to choose default.
    * 
    * @param orgName
    *           organization name, or null for the default
    * @param vdcName
    *           catalog name, or null for the default
    * @throws NoSuchElementException
    *            if you specified an org or vdc name that isn't present
    */
   @GET
   @XMLResponseParser(VDCHandler.class)
   @Consumes(VDC_XML)
   @Fallback(Fallbacks.NullOnNotFoundOr404.class)
   @MapBinder(OrgNameAndVDCNameToEndpoint.class)
   VDC findVDCInOrgNamed(@Nullable @PayloadParam("orgName") String orgName,
                                                     @Nullable @PayloadParam("vdcName") String vdcName);

   @GET
   @Consumes(TASKSLIST_XML)
   @XMLResponseParser(TasksListHandler.class)
   @Fallback(Fallbacks.NullOnNotFoundOr404.class)
   TasksList getTasksList(@EndpointParam URI tasksListId);

   @GET
   @XMLResponseParser(TasksListHandler.class)
   @Fallback(Fallbacks.NullOnNotFoundOr404.class)
   @Consumes(TASKSLIST_XML)
   @MapBinder(OrgNameAndTasksListNameToEndpoint.class)
   TasksList findTasksListInOrgNamed(@Nullable @PayloadParam("orgName") String orgName,
                                     @Nullable @PayloadParam("tasksListName") String tasksListName);

   /**
    * Whenever the result of a request cannot be returned immediately, the
    * server creates a Task object and includes it in the response, as a member
    * of the Tasks container in the response body. Each Task has an href value,
    * which is a URL that the client can use to retrieve the Task element alone,
    * without the rest of the response in which it was contained. All
    * information about the task is included in the Task element when it is
    * returned in the response's Tasks container, so a client does not need to
    * make an additional request to the Task URL unless it wants to follow the
    * progress of a task that was incomplete.
    */
   @GET
   @Consumes(TASK_XML)
   @XMLResponseParser(TaskHandler.class)
   @Fallback(Fallbacks.NullOnNotFoundOr404.class)
   Task getTask(@EndpointParam URI taskId);

   @POST
   @Path("/action/cancel")
   void cancelTask(@EndpointParam URI taskId);

   /**
    * 
    * @return a listing of all orgs that the current user has access to.
    */
   @Provides
   @org.jclouds.trmk.vcloud_0_8.endpoints.Org
   Map<String, ReferenceType> listOrgs();

   @POST
   @Path("/action/instantiateVAppTemplate")
   @Produces("application/vnd.vmware.vcloud.instantiateVAppTemplateParams+xml")
   @Consumes(VAPP_XML)
   @XMLResponseParser(VAppHandler.class)
   @MapBinder(BindInstantiateVAppTemplateParamsToXmlPayload.class)
   VApp instantiateVAppTemplateInVDC(@EndpointParam URI vdc,
                                                                 @PayloadParam("template") URI template,
                                                                 @PayloadParam("name") @ParamValidators(DnsNameValidator.class) String appName,
                                                                 InstantiateVAppTemplateOptions... options);

   @POST
   @Path("/action/cloneVApp")
   @Produces("application/vnd.vmware.vcloud.cloneVAppParams+xml")
   @Consumes(TASK_XML)
   @XMLResponseParser(TaskHandler.class)
   @MapBinder(BindCloneVAppParamsToXmlPayload.class)
   Task cloneVAppInVDC(@EndpointParam URI vdc, @PayloadParam("vApp") URI toClone,
                                                   @PayloadParam("newName") @ParamValidators(DnsNameValidator.class) String newName, CloneVAppOptions... options);

   @GET
   @Consumes(VAPPTEMPLATE_XML)
   @XMLResponseParser(VAppTemplateHandler.class)
   @Fallback(Fallbacks.NullOnNotFoundOr404.class)
   VAppTemplate getVAppTemplate(@EndpointParam URI vAppTemplate);

   /**
    * returns the vapp template corresponding to a catalog item in the catalog
    * associated with the specified name. Note that the org and catalog
    * parameters can be null to choose default.
    * 
    * @param orgName
    *           organization name, or null for the default
    * @param catalogName
    *           catalog name, or null for the default
    * @param itemName
    *           item you wish to lookup
    * 
    * @throws NoSuchElementException
    *            if you specified an org, catalog, or catalog item name that
    *            isn't present
    */
   @GET
   @Consumes(VAPPTEMPLATE_XML)
   @XMLResponseParser(VAppTemplateHandler.class)
   @Fallback(Fallbacks.NullOnNotFoundOr404.class)
   @MapBinder(OrgNameCatalogNameVAppTemplateNameToEndpoint.class)
   VAppTemplate findVAppTemplateInOrgCatalogNamed(@Nullable @PayloadParam("orgName") String orgName,
                                                  @Nullable @PayloadParam("catalogName") String catalogName, @PayloadParam("itemName") String itemName);

   @GET
   @Consumes(VAPP_XML)
   @XMLResponseParser(VAppHandler.class)
   @Fallback(Fallbacks.NullOnNotFoundOr404.class)
   @MapBinder(OrgNameVDCNameResourceEntityNameToEndpoint.class)
   VApp findVAppInOrgVDCNamed(@Nullable @PayloadParam("orgName") String orgName,
                                                          @Nullable @PayloadParam("vdcName") String vdcName, @PayloadParam("resourceName") String vAppName);

   @GET
   @Consumes(VAPP_XML)
   @XMLResponseParser(VAppHandler.class)
   @Fallback(Fallbacks.NullOnNotFoundOr404.class)
   VApp getVApp(@EndpointParam URI vApp);

   @POST
   @Consumes(TASK_XML)
   @Path("/action/deploy")
   @XMLResponseParser(TaskHandler.class)
   Task deployVApp(@EndpointParam URI vAppId);

   /**
    * 
    */
   @POST
   @Consumes(TASK_XML)
   @Path("/action/undeploy")
   @XMLResponseParser(TaskHandler.class)
   Task undeployVApp(@EndpointParam URI vAppId);

   /**
    * This call powers on the vApp, as specified in the vApp's ovf:Startup
    * element.
    */
   @POST
   @Consumes(TASK_XML)
   @Path("/power/action/powerOn")
   @XMLResponseParser(TaskHandler.class)
   Task powerOnVApp(@EndpointParam URI vAppId);

   /**
    * This call powers off the vApp, as specified in the vApp's ovf:Startup
    * element.
    */
   @POST
   @Consumes(TASK_XML)
   @Path("/power/action/powerOff")
   @XMLResponseParser(TaskHandler.class)
   Task powerOffVApp(@EndpointParam URI vAppId);

   /**
    * This call shuts down the vApp.
    */
   @POST
   @Path("/power/action/shutdown")
   void shutdownVApp(@EndpointParam URI vAppId);

   /**
    * This call resets the vApp.
    */
   @POST
   @Consumes(TASK_XML)
   @Path("/power/action/reset")
   @XMLResponseParser(TaskHandler.class)
   Task resetVApp(@EndpointParam URI vAppId);

   /**
    * This call suspends the vApp.
    */
   @POST
   @Consumes(TASK_XML)
   @Path("/power/action/suspend")
   @XMLResponseParser(TaskHandler.class)
   Task suspendVApp(@EndpointParam URI vAppId);

   @DELETE
   @ResponseParser(ParseTaskFromLocationHeader.class)
   @Fallback(Fallbacks.NullOnNotFoundOr404.class)
   Task deleteVApp(@EndpointParam URI vAppId);

   /**
    * {@inheritDoc}
    */
   @GET
   @XMLResponseParser(VDCHandler.class)
   @Consumes(VDC_XML)
   @Fallback(Fallbacks.NullOnNotFoundOr404.class)
   VDC getVDC(@EndpointParam URI vdc);

   @GET
   @XMLResponseParser(OrgHandler.class)
   @Fallback(Fallbacks.NullOnNotFoundOr404.class)
   @Consumes(ORG_XML)
   org.jclouds.trmk.vcloud_0_8.domain.Org getOrg(@EndpointParam URI orgId);

   @GET
   @XMLResponseParser(OrgHandler.class)
   @Fallback(Fallbacks.NullOnNotFoundOr404.class)
   @Consumes(ORG_XML)
   org.jclouds.trmk.vcloud_0_8.domain.Org findOrgNamed(
           @Nullable @EndpointParam(parser = OrgNameToEndpoint.class) String orgName);

   @GET
   @XMLResponseParser(CustomizationParametersHandler.class)
   @Consumes(CATALOGITEMCUSTOMIZATIONPARAMETERS_XML)
   CustomizationParameters getCustomizationOptions(@EndpointParam URI customization);

   /**
    * This call returns a list of public IP addresses.
    */
   @GET
   @Consumes(PUBLICIPSLIST_XML)
   @XMLResponseParser(PublicIpAddressesHandler.class)
   @Fallback(Fallbacks.EmptySetOnNotFoundOr404.class)
   Set<PublicIpAddress> getPublicIpsAssociatedWithVDC(
           @EndpointParam(parser = VDCURIToPublicIPsEndpoint.class) URI vDCId);

   @DELETE
   @Fallback(TerremarkVCloudFallbacks.VoidOnDeleteDefaultIp.class)
   void deletePublicIp(@EndpointParam URI ipId);

   /**
    * This call adds an internet service to a known, existing public IP. This
    * call is identical to Add Internet Service except you specify the public IP
    * in the request.
    * 
    */
   @POST
   @Path("/internetServices")
   @Produces(INTERNETSERVICE_XML)
   @Consumes(INTERNETSERVICE_XML)
   @XMLResponseParser(InternetServiceHandler.class)
   @MapBinder(AddInternetServiceOptions.class)
   InternetService addInternetServiceToExistingIp(@EndpointParam URI publicIpId,
                                                                              @PayloadParam("name") String serviceName, @PayloadParam("protocol") Protocol protocol,
                                                                              @PayloadParam("port") int port, AddInternetServiceOptions... options);

   @DELETE
   @Fallback(Fallbacks.VoidOnNotFoundOr404.class)
   void deleteInternetService(@EndpointParam URI internetServiceId);

   @GET
   @Consumes(INTERNETSERVICESLIST_XML)
   @XMLResponseParser(InternetServiceHandler.class)
   @Fallback(Fallbacks.NullOnNotFoundOr404.class)
   InternetService getInternetService(@EndpointParam URI internetServiceId);

   @GET
   @Consumes(INTERNETSERVICESLIST_XML)
   @XMLResponseParser(InternetServicesHandler.class)
   @Fallback(Fallbacks.EmptySetOnNotFoundOr404.class)
   Set<InternetService> getAllInternetServicesInVDC(
           @EndpointParam(parser = VDCURIToInternetServicesEndpoint.class) URI vDCId);

   /**
    * This call returns information about the internet service on a public IP.
    */
   @GET
   @Path("/internetServices")
   @Consumes(INTERNETSERVICESLIST_XML)
   @XMLResponseParser(InternetServicesHandler.class)
   @Fallback(Fallbacks.EmptySetOnNotFoundOr404.class)
   Set<InternetService> getInternetServicesOnPublicIp(@EndpointParam URI ipId);

   @GET
   @Consumes(PUBLICIP_XML)
   @XMLResponseParser(InternetServicesHandler.class)
   @Fallback(Fallbacks.EmptySetOnNotFoundOr404.class)
   Set<InternetService> getPublicIp(@EndpointParam URI ipId);

   /**
    * This call adds a node to an existing internet service.
    * <p/>
    * Every vDC is assigned a network of 60 IP addresses that can be used as
    * nodes. Each node can associated with multiple internet service. You can
    * get a list of the available IP addresses by calling Get IP Addresses for a
    * Network.
    * 
    * @param internetServiceId
    * @param ipAddress
    * @param name
    * @param port
    * @param options
    * @return
    */
   @POST
   @Path("/nodeServices")
   @Produces(NODESERVICE_XML)
   @Consumes(NODESERVICE_XML)
   @XMLResponseParser(NodeHandler.class)
   @MapBinder(AddNodeOptions.class)
   Node addNode(@EndpointParam URI internetServiceId,
                                            @PayloadParam("ipAddress") String ipAddress, @PayloadParam("name") String name,
                                            @PayloadParam("port") int port, AddNodeOptions... options);

   @GET
   @XMLResponseParser(NodeHandler.class)
   @Consumes(NODESERVICE_XML)
   @Fallback(Fallbacks.NullOnNotFoundOr404.class)
   Node getNode(@EndpointParam URI nodeId);

   @PUT
   @Produces(NODESERVICE_XML)
   @Consumes(NODESERVICE_XML)
   @XMLResponseParser(NodeHandler.class)
   @MapBinder(BindNodeConfigurationToXmlPayload.class)
   Node configureNode(@EndpointParam URI nodeId, @PayloadParam("name") String name,
                                                  @PayloadParam("enabled") boolean enabled, @Nullable @PayloadParam("description") String description);

   @DELETE
   @Fallback(Fallbacks.VoidOnNotFoundOr404.class)
   void deleteNode(@EndpointParam URI nodeId);

   @GET
   @Path("/nodeServices")
   @XMLResponseParser(NodesHandler.class)
   @Fallback(Fallbacks.EmptySetOnNotFoundOr404.class)
   @Consumes(NODESERVICE_XML)
   Set<Node> getNodes(@EndpointParam URI internetServiceId);

   /**
    * This call configures the settings of an existing vApp by passing the new
    * configuration. The existing vApp must be in a powered off state (status =
    * 2).
    * <p/>
    * You can change the following items for a vApp.
    * <ol>
    * <li>vApp name Number of virtual CPUs</li>
    * <li>Amount of virtual memory</li>
    * <li>Add a virtual disk</li>
    * <li>Delete a virtual disk</li>
    * </ol>
    * You can make more than one change in a single request. For example, you
    * can increase the number of virtual CPUs and the amount of virtual memory
    * in the same request.
    * 
    * @param VApp
    *           vApp to change in power state off
    * @param configuration
    *           (s) to change
    * @return task of configuration change
    */
   @PUT
   @Produces(VAPP_XML)
   @Consumes(VAPP_XML)
   @MapBinder(BindVAppConfigurationToXmlPayload.class)
   @ResponseParser(ParseTaskFromLocationHeader.class)
   Task configureVApp(
           @EndpointParam(parser = BindVAppConfigurationToXmlPayload.class) VApp vApp, VAppConfiguration configuration);

   /**
    */
   Set<KeyPair> listKeyPairsInOrg(URI org);

   /**
    * @throws IllegalStateException
    *            if a key of the same name already exists
    */
   KeyPair generateKeyPairInOrg(URI org, String name, boolean makeDefault);

   /**
    */
   KeyPair findKeyPairInOrg(URI org, String keyPairName);

   KeyPair getKeyPair(URI keyPair);

   // TODO
   // KeyPair configureKeyPair(int keyPairId, KeyPairConfiguration
   // keyPairConfiguration);

   void deleteKeyPair(URI keyPair);

}
