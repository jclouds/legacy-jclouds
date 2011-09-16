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
package org.jclouds.vcloud;

import static org.jclouds.vcloud.VCloudMediaType.CATALOGITEM_XML;
import static org.jclouds.vcloud.VCloudMediaType.CATALOG_XML;
import static org.jclouds.vcloud.VCloudMediaType.DEPLOYVAPPPARAMS_XML;
import static org.jclouds.vcloud.VCloudMediaType.GUESTCUSTOMIZATIONSECTION_XML;
import static org.jclouds.vcloud.VCloudMediaType.NETWORKCONNECTIONSECTION_XML;
import static org.jclouds.vcloud.VCloudMediaType.NETWORK_XML;
import static org.jclouds.vcloud.VCloudMediaType.ORG_XML;
import static org.jclouds.vcloud.VCloudMediaType.TASKSLIST_XML;
import static org.jclouds.vcloud.VCloudMediaType.TASK_XML;
import static org.jclouds.vcloud.VCloudMediaType.UNDEPLOYVAPPPARAMS_XML;
import static org.jclouds.vcloud.VCloudMediaType.VAPPTEMPLATE_XML;
import static org.jclouds.vcloud.VCloudMediaType.VAPP_XML;
import static org.jclouds.vcloud.VCloudMediaType.VDC_XML;
import static org.jclouds.vcloud.VCloudMediaType.VM_XML;

import java.io.InputStream;
import java.net.URI;
import java.util.Map;

import org.jclouds.javax.annotation.Nullable;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.jclouds.ovf.Envelope;
import org.jclouds.ovf.xml.EnvelopeHandler;
import org.jclouds.predicates.validators.DnsNameValidator;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.Delegate;
import org.jclouds.rest.annotations.Endpoint;
import org.jclouds.rest.annotations.EndpointParam;
import org.jclouds.rest.annotations.ExceptionParser;
import org.jclouds.rest.annotations.MapBinder;
import org.jclouds.rest.annotations.ParamValidators;
import org.jclouds.rest.annotations.PayloadParam;
import org.jclouds.rest.annotations.PayloadParams;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.XMLResponseParser;
import org.jclouds.rest.functions.ReturnNullOnNotFoundOr404;
import org.jclouds.rest.functions.ReturnVoidOnNotFoundOr404;
import org.jclouds.vcloud.binders.BindCaptureVAppParamsToXmlPayload;
import org.jclouds.vcloud.binders.BindCloneVAppParamsToXmlPayload;
import org.jclouds.vcloud.binders.BindDeployVAppParamsToXmlPayload;
import org.jclouds.vcloud.binders.BindGuestCustomizationSectionToXmlPayload;
import org.jclouds.vcloud.binders.BindInstantiateVAppTemplateParamsToXmlPayload;
import org.jclouds.vcloud.binders.BindNetworkConnectionSectionToXmlPayload;
import org.jclouds.vcloud.binders.BindUndeployVAppParamsToXmlPayload;
import org.jclouds.vcloud.domain.Catalog;
import org.jclouds.vcloud.domain.CatalogItem;
import org.jclouds.vcloud.domain.GuestCustomizationSection;
import org.jclouds.vcloud.domain.NetworkConnectionSection;
import org.jclouds.vcloud.domain.Org;
import org.jclouds.vcloud.domain.ReferenceType;
import org.jclouds.vcloud.domain.Task;
import org.jclouds.vcloud.domain.TasksList;
import org.jclouds.vcloud.domain.VApp;
import org.jclouds.vcloud.domain.VAppTemplate;
import org.jclouds.vcloud.domain.VDC;
import org.jclouds.vcloud.domain.Vm;
import org.jclouds.vcloud.domain.network.OrgNetwork;
import org.jclouds.vcloud.endpoints.OrgList;
import org.jclouds.vcloud.features.CatalogAsyncClient;
import org.jclouds.vcloud.features.NetworkAsyncClient;
import org.jclouds.vcloud.features.OrgAsyncClient;
import org.jclouds.vcloud.features.TaskAsyncClient;
import org.jclouds.vcloud.features.VAppAsyncClient;
import org.jclouds.vcloud.features.VAppTemplateAsyncClient;
import org.jclouds.vcloud.features.VAppTemplateClient;
import org.jclouds.vcloud.features.VDCAsyncClient;
import org.jclouds.vcloud.features.VmAsyncClient;
import org.jclouds.vcloud.filters.SetVCloudTokenCookie;
import org.jclouds.vcloud.functions.OrgNameAndCatalogNameToEndpoint;
import org.jclouds.vcloud.functions.OrgNameAndVDCNameToEndpoint;
import org.jclouds.vcloud.functions.OrgNameCatalogNameItemNameToEndpoint;
import org.jclouds.vcloud.functions.OrgNameCatalogNameVAppTemplateNameToEndpoint;
import org.jclouds.vcloud.functions.OrgNameToEndpoint;
import org.jclouds.vcloud.functions.OrgNameToTasksListEndpoint;
import org.jclouds.vcloud.functions.OrgNameVDCNameNetworkNameToEndpoint;
import org.jclouds.vcloud.functions.OrgNameVDCNameResourceEntityNameToEndpoint;
import org.jclouds.vcloud.options.CaptureVAppOptions;
import org.jclouds.vcloud.options.CloneVAppOptions;
import org.jclouds.vcloud.options.InstantiateVAppTemplateOptions;
import org.jclouds.vcloud.xml.CatalogHandler;
import org.jclouds.vcloud.xml.CatalogItemHandler;
import org.jclouds.vcloud.xml.OrgHandler;
import org.jclouds.vcloud.xml.OrgListHandler;
import org.jclouds.vcloud.xml.OrgNetworkHandler;
import org.jclouds.vcloud.xml.TaskHandler;
import org.jclouds.vcloud.xml.TasksListHandler;
import org.jclouds.vcloud.xml.VAppHandler;
import org.jclouds.vcloud.xml.VAppTemplateHandler;
import org.jclouds.vcloud.xml.VDCHandler;
import org.jclouds.vcloud.xml.VmHandler;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * Provides access to VCloud resources via their REST API.
 * <p/>
 * 
 * @see <a href= "https://community.vcloudexpress.terremark.com/en-us/discussion_forums/f/60.aspx"
 *      />
 * @author Adrian Cole
 */
@RequestFilters(SetVCloudTokenCookie.class)
public interface VCloudAsyncClient {
   /**
    * @see VCloudClient#getOrg
    */
   @GET
   @XMLResponseParser(OrgHandler.class)
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   @Consumes(ORG_XML)
   ListenableFuture<? extends Org> getOrg(@EndpointParam URI orgId);

   /**
    * @see VCloudClient#getOrgNamed
    */
   @GET
   @XMLResponseParser(OrgHandler.class)
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   @Consumes(ORG_XML)
   ListenableFuture<? extends Org> findOrgNamed(
            @Nullable @EndpointParam(parser = OrgNameToEndpoint.class) String orgName);

   /**
    * @see VCloudClient#getCatalog
    */
   @GET
   @XMLResponseParser(CatalogHandler.class)
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   @Consumes(CATALOG_XML)
   ListenableFuture<? extends Catalog> getCatalog(@EndpointParam URI catalogId);

   /**
    * @see VCloudClient#findCatalogInOrgNamed
    */
   @GET
   @XMLResponseParser(CatalogHandler.class)
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   @Consumes(CATALOG_XML)
   ListenableFuture<? extends Catalog> findCatalogInOrgNamed(
            @Nullable @EndpointParam(parser = OrgNameAndCatalogNameToEndpoint.class) String orgName,
            @Nullable @EndpointParam(parser = OrgNameAndCatalogNameToEndpoint.class) String catalogName);

   /**
    * @see VCloudClient#getCatalogItem
    */
   @GET
   @Consumes(CATALOGITEM_XML)
   @XMLResponseParser(CatalogItemHandler.class)
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<? extends CatalogItem> getCatalogItem(@EndpointParam URI catalogItem);

   /**
    * @see VCloudClient#getCatalogItemInOrg
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
    * @see VCloudClient#findNetworkInOrgVDCNamed
    */
   @GET
   @Consumes(NETWORK_XML)
   @XMLResponseParser(OrgNetworkHandler.class)
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<? extends OrgNetwork> findNetworkInOrgVDCNamed(
            @Nullable @EndpointParam(parser = OrgNameVDCNameNetworkNameToEndpoint.class) String orgName,
            @Nullable @EndpointParam(parser = OrgNameVDCNameNetworkNameToEndpoint.class) String catalogName,
            @EndpointParam(parser = OrgNameVDCNameNetworkNameToEndpoint.class) String networkName);

   /**
    * @see VCloudClient#getNetwork
    */
   @GET
   @Consumes(NETWORK_XML)
   @XMLResponseParser(OrgNetworkHandler.class)
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<? extends OrgNetwork> getNetwork(@EndpointParam URI network);

   /**
    * @see VCloudClient#getVDC(URI)
    */
   @GET
   @XMLResponseParser(VDCHandler.class)
   @Consumes(VDC_XML)
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<? extends VDC> getVDC(@EndpointParam URI vdc);

   /**
    * @see VCloudClient#findVDCInOrgNamed(String, String)
    */
   @GET
   @XMLResponseParser(VDCHandler.class)
   @Consumes(VDC_XML)
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<? extends VDC> findVDCInOrgNamed(
            @Nullable @EndpointParam(parser = OrgNameAndVDCNameToEndpoint.class) String orgName,
            @Nullable @EndpointParam(parser = OrgNameAndVDCNameToEndpoint.class) String vdcName);

   /**
    * @see VCloudClient#getTasksList
    */
   @GET
   @Consumes(TASKSLIST_XML)
   @XMLResponseParser(TasksListHandler.class)
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<? extends TasksList> getTasksList(@EndpointParam URI tasksListId);

   /**
    * @see VCloudClient#findTasksListInOrgNamed
    */
   @GET
   @Consumes(TASKSLIST_XML)
   @XMLResponseParser(TasksListHandler.class)
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<? extends TasksList> findTasksListInOrgNamed(
            @Nullable @EndpointParam(parser = OrgNameToTasksListEndpoint.class) String orgName);

   /**
    * @see VCloudClient#getTask
    */
   @GET
   @Consumes(TASK_XML)
   @XMLResponseParser(TaskHandler.class)
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<? extends Task> getTask(@EndpointParam URI taskId);

   /**
    * @see VCloudClient#cancelTask
    */
   @POST
   @Path("/action/cancel")
   ListenableFuture<Void> cancelTask(@EndpointParam URI taskId);
   
   /**
    * Provides asynchronous access to VApp Template features.
    * 
    * @see VCloudClient#getVAppTemplateClient
    * 
    */
   @Delegate
   VAppTemplateAsyncClient getVAppTemplateClient();

   /**
    * Provides asynchronous access to VApp features.
    * 
    * @see VCloudClient#getVAppClient
    * 
    */
   @Delegate
   VAppAsyncClient getVAppClient();

   /**
    * Provides asynchronous access to Vm features.
    * 
    * @see VCloudClient#getVmClient
    * 
    */
   @Delegate
   VmAsyncClient getVmClient();

   /**
    * Provides asynchronous access to Catalog features.
    * 
    * @see VCloudClient#getCatalogClient
    * 
    */
   @Delegate
   CatalogAsyncClient getCatalogClient();

   /**
    * Provides asynchronous access to Task features.
    * 
    * @see VCloudClient#getTaskClient
    * 
    */
   @Delegate
   TaskAsyncClient getTaskClient();

   /**
    * Provides asynchronous access to VDC features.
    * 
    * @see VCloudClient#getVDCClient
    * 
    */
   @Delegate
   VDCAsyncClient getVDCClient();

   /**
    * Provides asynchronous access to Network features.
    * 
    * @see VCloudClient#getNetworkClient
    * 
    */
   @Delegate
   NetworkAsyncClient getNetworkClient();

   /**
    * Provides asynchronous access to Org features.
    * 
    * @see VCloudClient#getOrgClient
    * 
    */
   @Delegate
   OrgAsyncClient getOrgClient();

   /**
    * 
    * @see VmAsyncClient#getScreenThumbnailForVm
    */
   @Deprecated
   @GET
   @Path("/screen")
   @Consumes("image/png")
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<InputStream> getThumbnailOfVm(@EndpointParam URI vm);

   /**
    * 
    * @see OrgAsyncClient#listOrgs
    */
   @Deprecated
   @GET
   @Endpoint(OrgList.class)
   @XMLResponseParser(OrgListHandler.class)
   @Consumes(VCloudMediaType.ORGLIST_XML)
   ListenableFuture<Map<String, ReferenceType>> listOrgs();

   /**
    * @see VAppTemplateAsyncClient#getVAppTemplate
    */
   @Deprecated
   @GET
   @Consumes(VAPPTEMPLATE_XML)
   @XMLResponseParser(VAppTemplateHandler.class)
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<? extends VAppTemplate> getVAppTemplate(@EndpointParam URI vAppTemplate);

   /**
    * @see VAppTemplateClient#getOvfEnvelopeForVAppTemplate
    */
   @Deprecated
   @GET
   @Consumes(MediaType.TEXT_XML)
   @Path("/ovf")
   @XMLResponseParser(EnvelopeHandler.class)
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<? extends Envelope> getOvfEnvelopeForVAppTemplate(@EndpointParam URI vAppTemplate);

   /**
    * @see VAppTemplateAsyncClient#findVAppTemplateInOrgCatalogNamed
    */
   @Deprecated
   @GET
   @Consumes(VAPPTEMPLATE_XML)
   @XMLResponseParser(VAppTemplateHandler.class)
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<? extends VAppTemplate> findVAppTemplateInOrgCatalogNamed(
            @Nullable @EndpointParam(parser = OrgNameCatalogNameVAppTemplateNameToEndpoint.class) String orgName,
            @Nullable @EndpointParam(parser = OrgNameCatalogNameVAppTemplateNameToEndpoint.class) String catalogName,
            @EndpointParam(parser = OrgNameCatalogNameVAppTemplateNameToEndpoint.class) String itemName);

   /**
    * @see VAppTemplateAsyncClient#createVAppInVDCByInstantiatingTemplate
    */
   @Deprecated
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
    * @see VAppAsyncClient#copyVAppToVDCAndName
    */
   @Deprecated
   @POST
   @Path("/action/cloneVApp")
   @Produces("application/vnd.vmware.vcloud.cloneVAppParams+xml")
   @Consumes(TASK_XML)
   @XMLResponseParser(TaskHandler.class)
   @MapBinder(BindCloneVAppParamsToXmlPayload.class)
   ListenableFuture<? extends Task> cloneVAppInVDC(@EndpointParam URI vdc, @PayloadParam("Source") URI toClone,
            @PayloadParam("name") @ParamValidators(DnsNameValidator.class) String newName, CloneVAppOptions... options);

   /**
    * @see VAppTemplateAsyncClient#captureVAppInVDC
    */
   @Deprecated
   @POST
   @Path("/action/captureVApp")
   @Produces("application/vnd.vmware.vcloud.captureVAppParams+xml")
   @Consumes(VAPPTEMPLATE_XML)
   @XMLResponseParser(VAppTemplateHandler.class)
   @MapBinder(BindCaptureVAppParamsToXmlPayload.class)
   ListenableFuture<? extends VAppTemplate> captureVAppInVDC(@EndpointParam URI vdc,
            @PayloadParam("vApp") URI toCapture,
            @PayloadParam("templateName") @ParamValidators(DnsNameValidator.class) String templateName,
            CaptureVAppOptions... options);

   /**
    * @see VAppAsyncClient#findVAppInOrgVDCNamed
    */
   @Deprecated
   @GET
   @Consumes(VAPP_XML)
   @XMLResponseParser(VAppHandler.class)
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<? extends VApp> findVAppInOrgVDCNamed(
            @Nullable @EndpointParam(parser = OrgNameVDCNameResourceEntityNameToEndpoint.class) String orgName,
            @Nullable @EndpointParam(parser = OrgNameVDCNameResourceEntityNameToEndpoint.class) String catalogName,
            @EndpointParam(parser = OrgNameVDCNameResourceEntityNameToEndpoint.class) String vAppName);

   /**
    * @see VAppAsyncClient#getVApp
    */
   @Deprecated
   @GET
   @Consumes(VAPP_XML)
   @XMLResponseParser(VAppHandler.class)
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<? extends VApp> getVApp(@EndpointParam URI vApp);

   /**
    * @see VmAsyncClient#getVm
    */
   @Deprecated
   @GET
   @Consumes(VM_XML)
   @XMLResponseParser(VmHandler.class)
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<? extends Vm> getVm(@EndpointParam URI vm);

   /**
    * @see VmAsyncClient#updateGuestCustomizationOfVm
    */
   @Deprecated
   @PUT
   @Consumes(TASK_XML)
   @Produces(GUESTCUSTOMIZATIONSECTION_XML)
   @Path("/guestCustomizationSection")
   @XMLResponseParser(TaskHandler.class)
   ListenableFuture<? extends Task> updateGuestCustomizationOfVm(
            @EndpointParam URI vm,
            @BinderParam(BindGuestCustomizationSectionToXmlPayload.class) GuestCustomizationSection guestCustomizationSection);

   /**
    * @see VmAsyncClient#updateNetworkConnectionOfVm
    */
   @Deprecated
   @PUT
   @Consumes(TASK_XML)
   @Produces(NETWORKCONNECTIONSECTION_XML)
   @Path("/networkConnectionSection")
   @XMLResponseParser(TaskHandler.class)
   ListenableFuture<? extends Task> updateNetworkConnectionOfVm(
            @EndpointParam URI vm,
            @BinderParam(BindNetworkConnectionSectionToXmlPayload.class) NetworkConnectionSection networkConnectionSection);

   /**
    * @see VAppAsyncClient#deployVApp
    * @see VmAsyncClient#deployVm
    */
   @Deprecated
   @POST
   @Consumes(TASK_XML)
   @Produces(DEPLOYVAPPPARAMS_XML)
   @Path("/action/deploy")
   @MapBinder(BindDeployVAppParamsToXmlPayload.class)
   @XMLResponseParser(TaskHandler.class)
   ListenableFuture<? extends Task> deployVAppOrVm(@EndpointParam URI vAppOrVmId);

   /**
    * @see VAppAsyncClient#deployAndPowerOnVApp
    * @see VmAsyncClient#deployAndPowerOnVm
    */
   @Deprecated
   @POST
   @Consumes(TASK_XML)
   @Produces(DEPLOYVAPPPARAMS_XML)
   @Path("/action/deploy")
   @MapBinder(BindDeployVAppParamsToXmlPayload.class)
   @PayloadParams(keys = "powerOn", values = "true")
   @XMLResponseParser(TaskHandler.class)
   ListenableFuture<? extends Task> deployAndPowerOnVAppOrVm(@EndpointParam URI vAppOrVmId);

   /**
    * @see VAppAsyncClient#undeployVApp
    * @see VmAsyncClient#undeployVm
    */
   @Deprecated
   @POST
   @Consumes(TASK_XML)
   @Produces(UNDEPLOYVAPPPARAMS_XML)
   @Path("/action/undeploy")
   @MapBinder(BindUndeployVAppParamsToXmlPayload.class)
   @XMLResponseParser(TaskHandler.class)
   ListenableFuture<? extends Task> undeployVAppOrVm(@EndpointParam URI vAppOrVmId);

   /**
    * @see VAppAsyncClient#undeployAndSaveStateOfVApp
    * @see VmAsyncClient#undeployAndSaveStateOfVm
    */
   @Deprecated
   @POST
   @Consumes(TASK_XML)
   @Produces(UNDEPLOYVAPPPARAMS_XML)
   @Path("/action/undeploy")
   @MapBinder(BindUndeployVAppParamsToXmlPayload.class)
   @PayloadParams(keys = "saveState", values = "true")
   @XMLResponseParser(TaskHandler.class)
   ListenableFuture<? extends Task> undeployAndSaveStateOfVAppOrVm(@EndpointParam URI vAppOrVmId);

   /**
    * @see VAppAsyncClient#powerOnVApp
    * @see VmAsyncClient#powerOnVm
    */
   @Deprecated
   @POST
   @Consumes(TASK_XML)
   @Path("/power/action/powerOn")
   @XMLResponseParser(TaskHandler.class)
   ListenableFuture<? extends Task> powerOnVAppOrVm(@EndpointParam URI vAppOrVmId);

   /**
    * @see VAppAsyncClient#powerOffVApp
    * @see VmAsyncClient#powerOffVm
    */
   @Deprecated
   @POST
   @Consumes(TASK_XML)
   @Path("/power/action/powerOff")
   @XMLResponseParser(TaskHandler.class)
   ListenableFuture<? extends Task> powerOffVAppOrVm(@EndpointParam URI vAppOrVmId);

   /**
    * @see VAppAsyncClient#shutdownVApp
    * @see VmAsyncClient#shutdownVm
    */
   @Deprecated
   @POST
   @Path("/power/action/shutdown")
   ListenableFuture<Void> shutdownVAppOrVm(@EndpointParam URI vAppOrVmId);

   /**
    * @see VAppAsyncClient#resetVApp
    * @see VmAsyncClient#resetVm
    */
   @Deprecated
   @POST
   @Consumes(TASK_XML)
   @Path("/power/action/reset")
   @XMLResponseParser(TaskHandler.class)
   ListenableFuture<? extends Task> resetVAppOrVm(@EndpointParam URI vAppOrVmId);

   /**
    * @see VAppAsyncClient#rebootVApp
    * @see VmAsyncClient#rebootVm
    */
   @Deprecated
   @POST
   @Path("/power/action/reboot")
   ListenableFuture<Void> rebootVAppOrVm(@EndpointParam URI vAppOrVmId);

   /**
    * @see VAppAsyncClient#suspendVApp
    * @see VmAsyncClient#suspendVm
    */
   @Deprecated
   @POST
   @Consumes(TASK_XML)
   @Path("/power/action/suspend")
   @XMLResponseParser(TaskHandler.class)
   ListenableFuture<? extends Task> suspendVAppOrVm(@EndpointParam URI vAppOrVmId);

   /**
    * @see VAppAsyncClient#deleteVApp
    */
   @Deprecated
   @DELETE
   @ExceptionParser(ReturnVoidOnNotFoundOr404.class)
   @XMLResponseParser(TaskHandler.class)
   ListenableFuture<? extends Task> deleteVApp(@EndpointParam URI id);

}
