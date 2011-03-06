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

import static org.jclouds.vcloud.VCloudMediaType.DEPLOYVAPPPARAMS_XML;
import static org.jclouds.vcloud.VCloudMediaType.GUESTCUSTOMIZATIONSECTION_XML;
import static org.jclouds.vcloud.VCloudMediaType.NETWORKCONNECTIONSECTION_XML;
import static org.jclouds.vcloud.VCloudMediaType.TASK_XML;
import static org.jclouds.vcloud.VCloudMediaType.UNDEPLOYVAPPPARAMS_XML;
import static org.jclouds.vcloud.VCloudMediaType.VAPPTEMPLATE_XML;
import static org.jclouds.vcloud.VCloudMediaType.VAPP_XML;
import static org.jclouds.vcloud.VCloudMediaType.VM_XML;

import java.io.InputStream;
import java.net.URI;
import java.util.Map;

import javax.annotation.Nullable;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.jclouds.predicates.validators.DnsNameValidator;
import org.jclouds.rest.annotations.BinderParam;
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
import org.jclouds.vcloud.domain.GuestCustomizationSection;
import org.jclouds.vcloud.domain.NetworkConnectionSection;
import org.jclouds.vcloud.domain.ReferenceType;
import org.jclouds.vcloud.domain.Task;
import org.jclouds.vcloud.domain.VApp;
import org.jclouds.vcloud.domain.VAppTemplate;
import org.jclouds.vcloud.domain.Vm;
import org.jclouds.vcloud.domain.ovf.OvfEnvelope;
import org.jclouds.vcloud.endpoints.OrgList;
import org.jclouds.vcloud.filters.SetVCloudTokenCookie;
import org.jclouds.vcloud.functions.OrgNameCatalogNameVAppTemplateNameToEndpoint;
import org.jclouds.vcloud.functions.OrgNameVDCNameResourceEntityNameToEndpoint;
import org.jclouds.vcloud.options.CaptureVAppOptions;
import org.jclouds.vcloud.options.CloneVAppOptions;
import org.jclouds.vcloud.options.InstantiateVAppTemplateOptions;
import org.jclouds.vcloud.xml.OrgListHandler;
import org.jclouds.vcloud.xml.TaskHandler;
import org.jclouds.vcloud.xml.VAppHandler;
import org.jclouds.vcloud.xml.VAppTemplateHandler;
import org.jclouds.vcloud.xml.VmHandler;
import org.jclouds.vcloud.xml.ovf.OvfEnvelopeHandler;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * Provides access to VCloud resources via their REST API.
 * <p/>
 * 
 * @see <a href="https://community.vcloudexpress.terremark.com/en-us/discussion_forums/f/60.aspx" />
 * @author Adrian Cole
 */
@RequestFilters(SetVCloudTokenCookie.class)
public interface VCloudAsyncClient extends CommonVCloudAsyncClient {

   /**
    * 
    * @see VCloudClient#getThumbnailOfVm
    */
   @GET
   @Path("/screen")
   @Consumes("image/png")
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<InputStream> getThumbnailOfVm(@EndpointParam URI vm);

   /**
    * 
    * @see VCloudClient#listOrgs
    */
   @GET
   @Endpoint(OrgList.class)
   @XMLResponseParser(OrgListHandler.class)
   @Consumes(VCloudMediaType.ORGLIST_XML)
   ListenableFuture<Map<String, ReferenceType>> listOrgs();

   /**
    * @see VCloudClient#getVAppTemplate
    */
   @GET
   @Consumes(VAPPTEMPLATE_XML)
   @XMLResponseParser(VAppTemplateHandler.class)
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<? extends VAppTemplate> getVAppTemplate(@EndpointParam URI vAppTemplate);

   /**
    * @see VCloudClient#getOvfEnvelopeForVAppTemplate
    */
   @GET
   @Consumes(MediaType.TEXT_XML)
   @Path("/ovf")
   @XMLResponseParser(OvfEnvelopeHandler.class)
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<? extends OvfEnvelope> getOvfEnvelopeForVAppTemplate(@EndpointParam URI vAppTemplate);

   /**
    * @see VCloudClient#findVAppTemplateInOrgCatalogNamed
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
    * @see VCloudClient#instantiateVAppTemplateInVDC
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
    * @see VCloudClient#cloneVAppInVDC
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
    * @see VCloudClient#captureVAppInVDC
    */
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
    * @see VCloudClient#findVAppInOrgVDCNamed
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
    * @see VCloudClient#getVApp
    */
   @GET
   @Consumes(VAPP_XML)
   @XMLResponseParser(VAppHandler.class)
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<? extends VApp> getVApp(@EndpointParam URI vApp);

   /**
    * @see VCloudClient#getVm
    */
   @GET
   @Consumes(VM_XML)
   @XMLResponseParser(VmHandler.class)
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<? extends Vm> getVm(@EndpointParam URI vm);

   /**
    * @see VCloudClient#updateGuestCustomizationOfVm
    */
   @PUT
   @Consumes(TASK_XML)
   @Produces(GUESTCUSTOMIZATIONSECTION_XML)
   @Path("/guestCustomizationSection")
   @XMLResponseParser(TaskHandler.class)
   ListenableFuture<? extends Task> updateGuestCustomizationOfVm(
         @EndpointParam URI vm,
         @BinderParam(BindGuestCustomizationSectionToXmlPayload.class) GuestCustomizationSection guestCustomizationSection);

   /**
    * @see VCloudClient#updateNetworkConnectionOfVm
    */
   @PUT
   @Consumes(TASK_XML)
   @Produces(NETWORKCONNECTIONSECTION_XML)
   @Path("/networkConnectionSection")
   @XMLResponseParser(TaskHandler.class)
   ListenableFuture<? extends Task> updateNetworkConnectionOfVm(@EndpointParam URI vm,
         @BinderParam(BindNetworkConnectionSectionToXmlPayload.class) NetworkConnectionSection networkConnectionSection);

   /**
    * @see VCloudClient#deployVAppOrVm
    */
   @POST
   @Consumes(TASK_XML)
   @Produces(DEPLOYVAPPPARAMS_XML)
   @Path("/action/deploy")
   @MapBinder(BindDeployVAppParamsToXmlPayload.class)
   @XMLResponseParser(TaskHandler.class)
   ListenableFuture<? extends Task> deployVAppOrVm(@EndpointParam URI vAppOrVmId);

   /**
    * @see VCloudClient#deployAndPowerOnVAppOrVm
    */
   @POST
   @Consumes(TASK_XML)
   @Produces(DEPLOYVAPPPARAMS_XML)
   @Path("/action/deploy")
   @MapBinder(BindDeployVAppParamsToXmlPayload.class)
   @PayloadParams(keys = "powerOn", values = "true")
   @XMLResponseParser(TaskHandler.class)
   ListenableFuture<? extends Task> deployAndPowerOnVAppOrVm(@EndpointParam URI vAppOrVmId);

   /**
    * @see VCloudClient#undeployVAppOrVm
    */
   @POST
   @Consumes(TASK_XML)
   @Produces(UNDEPLOYVAPPPARAMS_XML)
   @Path("/action/undeploy")
   @MapBinder(BindUndeployVAppParamsToXmlPayload.class)
   @XMLResponseParser(TaskHandler.class)
   ListenableFuture<? extends Task> undeployVAppOrVm(@EndpointParam URI vAppOrVmId);

   /**
    * @see VCloudClient#undeployAndSaveStateOfVAppOrVm
    */
   @POST
   @Consumes(TASK_XML)
   @Produces(UNDEPLOYVAPPPARAMS_XML)
   @Path("/action/undeploy")
   @MapBinder(BindUndeployVAppParamsToXmlPayload.class)
   @PayloadParams(keys = "saveState", values = "true")
   @XMLResponseParser(TaskHandler.class)
   ListenableFuture<? extends Task> undeployAndSaveStateOfVAppOrVm(@EndpointParam URI vAppOrVmId);

   /**
    * @see VCloudClient#powerOnVAppOrVm
    */
   @POST
   @Consumes(TASK_XML)
   @Path("/power/action/powerOn")
   @XMLResponseParser(TaskHandler.class)
   ListenableFuture<? extends Task> powerOnVAppOrVm(@EndpointParam URI vAppOrVmId);

   /**
    * @see VCloudClient#powerOffVAppOrVm
    */
   @POST
   @Consumes(TASK_XML)
   @Path("/power/action/powerOff")
   @XMLResponseParser(TaskHandler.class)
   ListenableFuture<? extends Task> powerOffVAppOrVm(@EndpointParam URI vAppOrVmId);

   /**
    * @see VCloudClient#shutdownVAppOrVm
    */
   @POST
   @Path("/power/action/shutdown")
   ListenableFuture<Void> shutdownVAppOrVm(@EndpointParam URI vAppOrVmId);

   /**
    * @see VCloudClient#resetVAppOrVm
    */
   @POST
   @Consumes(TASK_XML)
   @Path("/power/action/reset")
   @XMLResponseParser(TaskHandler.class)
   ListenableFuture<? extends Task> resetVAppOrVm(@EndpointParam URI vAppOrVmId);

   /**
    * @see VCloudClient#rebootVAppOrVm
    */
   @POST
   @Path("/power/action/reboot")
   ListenableFuture<Void> rebootVAppOrVm(@EndpointParam URI vAppOrVmId);

   /**
    * @see VCloudClient#suspendVAppOrVm
    */
   @POST
   @Consumes(TASK_XML)
   @Path("/power/action/suspend")
   @XMLResponseParser(TaskHandler.class)
   ListenableFuture<? extends Task> suspendVAppOrVm(@EndpointParam URI vAppOrVmId);

   /**
    * @see CommonVCloudClient#deleteVApp
    */
   @DELETE
   @ExceptionParser(ReturnVoidOnNotFoundOr404.class)
   @XMLResponseParser(TaskHandler.class)
   ListenableFuture<? extends Task> deleteVApp(@EndpointParam URI vAppId);

}
