/*
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
package org.jclouds.vcloud.director.v1_5.features;

import static org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType.ANY_IMAGE;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType.DEPLOY_VAPP_PARAMS;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType.GUEST_CUSTOMIZATION_SECTION;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType.MEDIA_PARAMS;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType.NETWORK_CONNECTION_SECTION;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType.OPERATING_SYSTEM_SECTION;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType.OVF_RASD_ITEM;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType.OVF_RASD_ITEMS_LIST;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType.PRODUCT_SECTION_LIST;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType.RELOCATE_VM_PARAMS;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType.TASK;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType.UNDEPLOY_VAPP_PARAMS;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType.VIRTUAL_HARDWARE_SECTION;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType.VM;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType.VM_PENDING_ANSWER;

import java.net.URI;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.Delegate;
import org.jclouds.rest.annotations.EndpointParam;
import org.jclouds.rest.annotations.ExceptionParser;
import org.jclouds.rest.annotations.JAXBResponseParser;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.ResponseParser;
import org.jclouds.rest.binders.BindToXMLPayload;
import org.jclouds.rest.functions.ReturnNullOnNotFoundOr404;
import org.jclouds.vcloud.director.v1_5.domain.ProductSectionList;
import org.jclouds.vcloud.director.v1_5.domain.RasdItemsList;
import org.jclouds.vcloud.director.v1_5.domain.ScreenTicket;
import org.jclouds.vcloud.director.v1_5.domain.Task;
import org.jclouds.vcloud.director.v1_5.domain.Vm;
import org.jclouds.vcloud.director.v1_5.domain.VmPendingQuestion;
import org.jclouds.vcloud.director.v1_5.domain.VmQuestionAnswer;
import org.jclouds.vcloud.director.v1_5.domain.dmtf.RasdItem;
import org.jclouds.vcloud.director.v1_5.domain.params.DeployVAppParams;
import org.jclouds.vcloud.director.v1_5.domain.params.MediaInsertOrEjectParams;
import org.jclouds.vcloud.director.v1_5.domain.params.RelocateParams;
import org.jclouds.vcloud.director.v1_5.domain.params.UndeployVAppParams;
import org.jclouds.vcloud.director.v1_5.domain.section.GuestCustomizationSection;
import org.jclouds.vcloud.director.v1_5.domain.section.NetworkConnectionSection;
import org.jclouds.vcloud.director.v1_5.domain.section.OperatingSystemSection;
import org.jclouds.vcloud.director.v1_5.domain.section.RuntimeInfoSection;
import org.jclouds.vcloud.director.v1_5.domain.section.VirtualHardwareSection;
import org.jclouds.vcloud.director.v1_5.filters.AddVCloudAuthorizationAndCookieToRequest;
import org.jclouds.vcloud.director.v1_5.functions.ReturnPayloadBytes;
import org.jclouds.vcloud.director.v1_5.functions.href.VmURNToHref;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * @author grkvlt@apache.org, Adrian Cole
 * @see VmApi
 */
@RequestFilters(AddVCloudAuthorizationAndCookieToRequest.class)
public interface VmAsyncApi {

   /**
    * @see VmApi#get(String)
    */
   @GET
   @Consumes(VM)
   @JAXBResponseParser
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<Vm> get(@EndpointParam(parser = VmURNToHref.class) String vmUrn);

   /**
    * @see VmApi#edit(String, Vm)
    */
   @PUT
   @Produces(VM)
   @Consumes(TASK)
   @JAXBResponseParser
   ListenableFuture<Task> edit(@EndpointParam(parser = VmURNToHref.class) String vmUrn,
            @BinderParam(BindToXMLPayload.class) Vm vApp);

   /**
    * @see VmApi#remove(String)
    */
   @DELETE
   @Consumes(TASK)
   @JAXBResponseParser
   ListenableFuture<Task> remove(@EndpointParam(parser = VmURNToHref.class) String vmUrn);

   /**
    * @see VmApi#consolidate(String)
    */
   @POST
   @Path("/action/consolidate")
   @Consumes(TASK)
   @JAXBResponseParser
   ListenableFuture<Task> consolidate(@EndpointParam(parser = VmURNToHref.class) String vmUrn);

   /**
    * @see VmApi#deploy(String, DeployVAppParams)
    */
   @POST
   @Path("/action/deploy")
   @Produces(DEPLOY_VAPP_PARAMS)
   @Consumes(TASK)
   @JAXBResponseParser
   ListenableFuture<Task> deploy(@EndpointParam(parser = VmURNToHref.class) String vmUrn,
            @BinderParam(BindToXMLPayload.class) DeployVAppParams params);

   /**
    * @see VmApi#discardSuspendedState(String)
    */
   @POST
   @Path("/action/discardSuspendedState")
   @Consumes(TASK)
   @JAXBResponseParser
   ListenableFuture<Task> discardSuspendedState(@EndpointParam(parser = VmURNToHref.class) String vmUrn);

   /**
    * @see VmApi#installVMwareTools(String)
    */
   @POST
   @Path("/action/installVMwareTools")
   @Consumes(TASK)
   @JAXBResponseParser
   ListenableFuture<Task> installVMwareTools(@EndpointParam(parser = VmURNToHref.class) String vmUrn);

   /**
    * @see VmApi#relocate(String, RelocateParams)
    */
   @POST
   @Path("/action/relocate")
   @Produces(RELOCATE_VM_PARAMS)
   @Consumes(TASK)
   @JAXBResponseParser
   ListenableFuture<Task> relocate(@EndpointParam(parser = VmURNToHref.class) String vmUrn,
            @BinderParam(BindToXMLPayload.class) RelocateParams params);

   /**
    * @see VmApi#undeploy(String, UndeployVAppParams)
    */
   @POST
   @Path("/action/undeploy")
   @Produces(UNDEPLOY_VAPP_PARAMS)
   @Consumes(TASK)
   @JAXBResponseParser
   ListenableFuture<Task> undeploy(@EndpointParam(parser = VmURNToHref.class) String vmUrn,
            @BinderParam(BindToXMLPayload.class) UndeployVAppParams params);

   /**
    * @see VmApi#upgradeHardwareVersion(String)
    */
   @POST
   @Path("/action/upgradeHardwareVersion")
   @Consumes(TASK)
   @JAXBResponseParser
   ListenableFuture<Task> upgradeHardwareVersion(@EndpointParam(parser = VmURNToHref.class) String vmUrn);

   /**
    * @see VmApi#powerOff(String)
    */
   @POST
   @Path("/power/action/powerOff")
   @Consumes(TASK)
   @JAXBResponseParser
   ListenableFuture<Task> powerOff(@EndpointParam(parser = VmURNToHref.class) String vmUrn);

   /**
    * @see VmApi#powerOn(String)
    */
   @POST
   @Path("/power/action/powerOn")
   @Consumes(TASK)
   @JAXBResponseParser
   ListenableFuture<Task> powerOn(@EndpointParam(parser = VmURNToHref.class) String vmUrn);

   /**
    * @see VmApi#reboot(String)
    */
   @POST
   @Path("/power/action/powerOff")
   @Consumes(TASK)
   @JAXBResponseParser
   ListenableFuture<Task> reboot(@EndpointParam(parser = VmURNToHref.class) String vmUrn);

   /**
    * @see VmApi#reset(String)
    */
   @POST
   @Path("/power/action/reset")
   @Consumes(TASK)
   @JAXBResponseParser
   ListenableFuture<Task> reset(@EndpointParam(parser = VmURNToHref.class) String vmUrn);

   /**
    * @see VmApi#shutdown(String)
    */
   @POST
   @Path("/power/action/shutdown")
   @Consumes(TASK)
   @JAXBResponseParser
   ListenableFuture<Task> shutdown(@EndpointParam(parser = VmURNToHref.class) String vmUrn);

   /**
    * @see VmApi#suspend(String)
    */
   @POST
   @Path("/power/action/suspend")
   @Consumes(TASK)
   @JAXBResponseParser
   ListenableFuture<Task> suspend(@EndpointParam(parser = VmURNToHref.class) String vmUrn);

   /**
    * @see VmApi#getGuestCustomizationSection(String)
    */
   @GET
   @Path("/guestCustomizationSection")
   @Consumes
   @JAXBResponseParser
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<GuestCustomizationSection> getGuestCustomizationSection(
            @EndpointParam(parser = VmURNToHref.class) String vmUrn);

   /**
    * @see VmApi#editGuestCustomizationSection(String, GuestCustomizationSection)
    */
   @PUT
   @Path("/guestCustomizationSection")
   @Produces(GUEST_CUSTOMIZATION_SECTION)
   @Consumes(TASK)
   @JAXBResponseParser
   ListenableFuture<Task> editGuestCustomizationSection(@EndpointParam(parser = VmURNToHref.class) String vmUrn,
            @BinderParam(BindToXMLPayload.class) GuestCustomizationSection section);

   /**
    * @see VmApi#ejectMedia(String, MediaInsertOrEjectParams)
    */
   @POST
   @Path("/media/action/ejectMedia")
   @Produces(MEDIA_PARAMS)
   @Consumes(TASK)
   @JAXBResponseParser
   ListenableFuture<Task> ejectMedia(@EndpointParam(parser = VmURNToHref.class) String vmUrn,
            @BinderParam(BindToXMLPayload.class) MediaInsertOrEjectParams mediaParams);

   /**
    * @see VmApi#insertMedia(String, MediaInsertOrEjectParams)
    */
   @POST
   @Path("/media/action/insertMedia")
   @Produces(MEDIA_PARAMS)
   @Consumes(TASK)
   @JAXBResponseParser
   ListenableFuture<Task> insertMedia(@EndpointParam(parser = VmURNToHref.class) String vmUrn,
            @BinderParam(BindToXMLPayload.class) MediaInsertOrEjectParams mediaParams);

   /**
    * @see VmApi#getNetworkConnectionSection(String)
    */
   @GET
   @Path("/networkConnectionSection")
   @Consumes
   @JAXBResponseParser
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<NetworkConnectionSection> getNetworkConnectionSection(
            @EndpointParam(parser = VmURNToHref.class) String vmUrn);

   /**
    * @see VmApi#editNetworkConnectionSection(String, NetworkConnectionSection)
    */
   @PUT
   @Path("/networkConnectionSection")
   @Produces(NETWORK_CONNECTION_SECTION)
   @Consumes(TASK)
   @JAXBResponseParser
   ListenableFuture<Task> editNetworkConnectionSection(@EndpointParam(parser = VmURNToHref.class) String vmUrn,
            @BinderParam(BindToXMLPayload.class) NetworkConnectionSection section);

   /**
    * @see VmApi#getOperatingSystemSection(String)
    */
   @GET
   @Path("/operatingSystemSection")
   @Consumes
   @JAXBResponseParser
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<OperatingSystemSection> getOperatingSystemSection(
            @EndpointParam(parser = VmURNToHref.class) String vmUrn);

   /**
    * @see VmApi#editOperatingSystemSection(String, OperatingSystemSection)
    */
   @PUT
   @Path("/operatingSystemSection")
   @Produces(OPERATING_SYSTEM_SECTION)
   @Consumes(TASK)
   @JAXBResponseParser
   ListenableFuture<Task> editOperatingSystemSection(@EndpointParam(parser = VmURNToHref.class) String vmUrn,
            @BinderParam(BindToXMLPayload.class) OperatingSystemSection section);

   /**
    * @see VmApi#getProductSections(String)
    */
   @GET
   @Path("/productSections")
   @Consumes
   @JAXBResponseParser
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<ProductSectionList> getProductSections(@EndpointParam(parser = VmURNToHref.class) String vmUrn);

   /**
    * @see VmApi#editProductSections(String, ProductSectionList)
    */
   @PUT
   @Path("/productSections")
   @Produces(PRODUCT_SECTION_LIST)
   @Consumes(TASK)
   @JAXBResponseParser
   ListenableFuture<Task> editProductSections(@EndpointParam(parser = VmURNToHref.class) String vmUrn,
            @BinderParam(BindToXMLPayload.class) ProductSectionList sectionList);

   /**
    * @see VmApi#getPendingQuestion(String)
    */
   @GET
   @Path("/question")
   @Consumes
   @JAXBResponseParser
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<VmPendingQuestion> getPendingQuestion(@EndpointParam(parser = VmURNToHref.class) String vmUrn);

   /**
    * @see VmApi#answerQuestion(String, VmQuestionAnswer)
    */
   @POST
   @Path("/question/action/answer")
   @Produces(VM_PENDING_ANSWER)
   @Consumes
   @JAXBResponseParser
   ListenableFuture<Void> answerQuestion(@EndpointParam(parser = VmURNToHref.class) String vmUrn,
            @BinderParam(BindToXMLPayload.class) VmQuestionAnswer answer);

   /**
    * @see VmApi#getRuntimeInfoSection(String)
    */
   @GET
   @Path("/runtimeInfoSection")
   @Consumes
   @JAXBResponseParser
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<RuntimeInfoSection> getRuntimeInfoSection(@EndpointParam(parser = VmURNToHref.class) String vmUrn);

   /**
    * @see VmApi#getScreenImage(String)
    */
   @GET
   @Path("/screen")
   @Consumes(ANY_IMAGE)
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   @ResponseParser(ReturnPayloadBytes.class)
   ListenableFuture<byte[]> getScreenImage(@EndpointParam(parser = VmURNToHref.class) String vmUrn);

   /**
    * @see VmApi#getScreenTicket(String)
    */
   @POST
   @Path("/screen/action/acquireTicket")
   @Consumes
   @JAXBResponseParser
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<ScreenTicket> getScreenTicket(@EndpointParam(parser = VmURNToHref.class) String vmUrn);

   /**
    * @see VmApi#getVirtualHardwareSection(String)
    */
   @GET
   @Path("/virtualHardwareSection")
   @Consumes
   @JAXBResponseParser
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<VirtualHardwareSection> getVirtualHardwareSection(
            @EndpointParam(parser = VmURNToHref.class) String vmUrn);

   /**
    * @see VmApi#editVirtualHardwareSection(String, VirtualHardwareSection)
    */
   @PUT
   @Path("/virtualHardwareSection")
   @Produces(VIRTUAL_HARDWARE_SECTION)
   @Consumes(TASK)
   @JAXBResponseParser
   ListenableFuture<Task> editVirtualHardwareSection(@EndpointParam(parser = VmURNToHref.class) String vmUrn,
            @BinderParam(BindToXMLPayload.class) VirtualHardwareSection section);

   /**
    * @see VmApi#getVirtualHardwareSectionCpu(String)
    */
   @GET
   @Path("/virtualHardwareSection/cpu")
   @Consumes
   @JAXBResponseParser
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<RasdItem> getVirtualHardwareSectionCpu(@EndpointParam(parser = VmURNToHref.class) String vmUrn);

   /**
    * @see VmApi#editVirtualHardwareSectionCpu(String, ResourceAllocationSettingData)
    */
   @PUT
   @Path("/virtualHardwareSection/cpu")
   @Produces(OVF_RASD_ITEM)
   @Consumes(TASK)
   @JAXBResponseParser
   ListenableFuture<Task> editVirtualHardwareSectionCpu(@EndpointParam(parser = VmURNToHref.class) String vmUrn,
            @BinderParam(BindToXMLPayload.class) RasdItem rasd);

   /**
    * @see VmApi#getVirtualHardwareSectionDisks(String)
    */
   @GET
   @Path("/virtualHardwareSection/disks")
   @Consumes
   @JAXBResponseParser
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<RasdItemsList> getVirtualHardwareSectionDisks(
            @EndpointParam(parser = VmURNToHref.class) String vmUrn);

   /**
    * @see VmApi#editVirtualHardwareSectionDisks(String, RasdItemsList)
    */
   @PUT
   @Path("/virtualHardwareSection/disks")
   @Produces(OVF_RASD_ITEMS_LIST)
   @Consumes(TASK)
   @JAXBResponseParser
   ListenableFuture<Task> editVirtualHardwareSectionDisks(@EndpointParam(parser = VmURNToHref.class) String vmUrn,
            @BinderParam(BindToXMLPayload.class) RasdItemsList rasdItemsList);

   /**
    * @see VmApi#getVirtualHardwareSectionMedia(String)
    */
   @GET
   @Path("/virtualHardwareSection/media")
   @Consumes
   @JAXBResponseParser
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<RasdItemsList> getVirtualHardwareSectionMedia(
            @EndpointParam(parser = VmURNToHref.class) String vmUrn);

   /**
    * @see VmApi#getVirtualHardwareSectionMemory(String)
    */
   @GET
   @Path("/virtualHardwareSection/memory")
   @Consumes
   @JAXBResponseParser
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<RasdItem> getVirtualHardwareSectionMemory(@EndpointParam(parser = VmURNToHref.class) String vmUrn);

   /**
    * @see VmApi#editVirtualHardwareSectionMemory(String, ResourceAllocationSettingData)
    */
   @PUT
   @Path("/virtualHardwareSection/memory")
   @Produces(OVF_RASD_ITEM)
   @Consumes(TASK)
   @JAXBResponseParser
   ListenableFuture<Task> editVirtualHardwareSectionMemory(@EndpointParam(parser = VmURNToHref.class) String vmUrn,
            @BinderParam(BindToXMLPayload.class) RasdItem rasd);

   /**
    * @see VmApi#getVirtualHardwareSectionNetworkCards(String)
    */
   @GET
   @Path("/virtualHardwareSection/networkCards")
   @Consumes
   @JAXBResponseParser
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<RasdItemsList> getVirtualHardwareSectionNetworkCards(
            @EndpointParam(parser = VmURNToHref.class) String vmUrn);

   /**
    * @see VmApi#editVirtualHardwareSectionNetworkCards(String, RasdItemsList)
    */
   @PUT
   @Path("/virtualHardwareSection/networkCards")
   @Produces(OVF_RASD_ITEMS_LIST)
   @Consumes(TASK)
   @JAXBResponseParser
   ListenableFuture<Task> editVirtualHardwareSectionNetworkCards(
            @EndpointParam(parser = VmURNToHref.class) String vmUrn,
            @BinderParam(BindToXMLPayload.class) RasdItemsList rasdItemsList);

   /**
    * @see VmApi#getVirtualHardwareSectionSerialPorts(String)
    */
   @GET
   @Path("/virtualHardwareSection/serialPorts")
   @Consumes
   @JAXBResponseParser
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<RasdItemsList> getVirtualHardwareSectionSerialPorts(
            @EndpointParam(parser = VmURNToHref.class) String vmUrn);

   /**
    * @see VmApi#editVirtualHardwareSectionSerialPorts(String, RasdItemsList)
    */
   @PUT
   @Path("/virtualHardwareSection/serialPorts")
   @Produces(OVF_RASD_ITEMS_LIST)
   @Consumes(TASK)
   @JAXBResponseParser
   ListenableFuture<Task> editVirtualHardwareSectionSerialPorts(
            @EndpointParam(parser = VmURNToHref.class) String vmUrn,
            @BinderParam(BindToXMLPayload.class) RasdItemsList rasdItemsList);

   /**
    * @see VmApi#get(URI)
    */
   @GET
   @Consumes(VM)
   @JAXBResponseParser
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<Vm> get(@EndpointParam URI vmHref);

   /**
    * @see VmApi#edit(URI, Vm)
    */
   @PUT
   @Produces(VM)
   @Consumes(TASK)
   @JAXBResponseParser
   ListenableFuture<Task> edit(@EndpointParam URI vmHref, @BinderParam(BindToXMLPayload.class) Vm vApp);

   /**
    * @see VmApi#remove(URI)
    */
   @DELETE
   @Consumes(TASK)
   @JAXBResponseParser
   ListenableFuture<Task> remove(@EndpointParam URI vmHref);

   /**
    * @see VmApi#consolidate(URI)
    */
   @POST
   @Path("/action/consolidate")
   @Consumes(TASK)
   @JAXBResponseParser
   ListenableFuture<Task> consolidate(@EndpointParam URI vmHref);

   /**
    * @see VmApi#deploy(URI, DeployVAppParams)
    */
   @POST
   @Path("/action/deploy")
   @Produces(DEPLOY_VAPP_PARAMS)
   @Consumes(TASK)
   @JAXBResponseParser
   ListenableFuture<Task> deploy(@EndpointParam URI vmHref, @BinderParam(BindToXMLPayload.class) DeployVAppParams params);

   /**
    * @see VmApi#discardSuspendedState(URI)
    */
   @POST
   @Path("/action/discardSuspendedState")
   @Consumes(TASK)
   @JAXBResponseParser
   ListenableFuture<Task> discardSuspendedState(@EndpointParam URI vmHref);

   /**
    * @see VmApi#installVMwareTools(URI)
    */
   @POST
   @Path("/action/installVMwareTools")
   @Consumes(TASK)
   @JAXBResponseParser
   ListenableFuture<Task> installVMwareTools(@EndpointParam URI vmHref);

   /**
    * @see VmApi#relocate(URI, RelocateParams)
    */
   @POST
   @Path("/action/relocate")
   @Produces(RELOCATE_VM_PARAMS)
   @Consumes(TASK)
   @JAXBResponseParser
   ListenableFuture<Task> relocate(@EndpointParam URI vmHref, @BinderParam(BindToXMLPayload.class) RelocateParams params);

   /**
    * @see VmApi#undeploy(URI, UndeployVAppParams)
    */
   @POST
   @Path("/action/undeploy")
   @Produces(UNDEPLOY_VAPP_PARAMS)
   @Consumes(TASK)
   @JAXBResponseParser
   ListenableFuture<Task> undeploy(@EndpointParam URI vmHref,
            @BinderParam(BindToXMLPayload.class) UndeployVAppParams params);

   /**
    * @see VmApi#upgradeHardwareVersion(URI)
    */
   @POST
   @Path("/action/upgradeHardwareVersion")
   @Consumes(TASK)
   @JAXBResponseParser
   ListenableFuture<Task> upgradeHardwareVersion(@EndpointParam URI vmHref);

   /**
    * @see VmApi#powerOff(URI)
    */
   @POST
   @Path("/power/action/powerOff")
   @Consumes(TASK)
   @JAXBResponseParser
   ListenableFuture<Task> powerOff(@EndpointParam URI vmHref);

   /**
    * @see VmApi#powerOn(URI)
    */
   @POST
   @Path("/power/action/powerOn")
   @Consumes(TASK)
   @JAXBResponseParser
   ListenableFuture<Task> powerOn(@EndpointParam URI vmHref);

   /**
    * @see VmApi#reboot(URI)
    */
   @POST
   @Path("/power/action/powerOff")
   @Consumes(TASK)
   @JAXBResponseParser
   ListenableFuture<Task> reboot(@EndpointParam URI vmHref);

   /**
    * @see VmApi#reset(URI)
    */
   @POST
   @Path("/power/action/reset")
   @Consumes(TASK)
   @JAXBResponseParser
   ListenableFuture<Task> reset(@EndpointParam URI vmHref);

   /**
    * @see VmApi#shutdown(URI)
    */
   @POST
   @Path("/power/action/shutdown")
   @Consumes(TASK)
   @JAXBResponseParser
   ListenableFuture<Task> shutdown(@EndpointParam URI vmHref);

   /**
    * @see VmApi#suspend(URI)
    */
   @POST
   @Path("/power/action/suspend")
   @Consumes(TASK)
   @JAXBResponseParser
   ListenableFuture<Task> suspend(@EndpointParam URI vmHref);

   /**
    * @see VmApi#getGuestCustomizationSection(URI)
    */
   @GET
   @Path("/guestCustomizationSection")
   @Consumes
   @JAXBResponseParser
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<GuestCustomizationSection> getGuestCustomizationSection(@EndpointParam URI vmHref);

   /**
    * @see VmApi#editGuestCustomizationSection(URI, GuestCustomizationSection)
    */
   @PUT
   @Path("/guestCustomizationSection")
   @Produces(GUEST_CUSTOMIZATION_SECTION)
   @Consumes(TASK)
   @JAXBResponseParser
   ListenableFuture<Task> editGuestCustomizationSection(@EndpointParam URI vmHref,
            @BinderParam(BindToXMLPayload.class) GuestCustomizationSection section);

   /**
    * @see VmApi#ejectMedia(URI, MediaInsertOrEjectParams)
    */
   @POST
   @Path("/media/action/ejectMedia")
   @Produces(MEDIA_PARAMS)
   @Consumes(TASK)
   @JAXBResponseParser
   ListenableFuture<Task> ejectMedia(@EndpointParam URI vmHref,
            @BinderParam(BindToXMLPayload.class) MediaInsertOrEjectParams mediaParams);

   /**
    * @see VmApi#insertMedia(URI, MediaInsertOrEjectParams)
    */
   @POST
   @Path("/media/action/insertMedia")
   @Produces(MEDIA_PARAMS)
   @Consumes(TASK)
   @JAXBResponseParser
   ListenableFuture<Task> insertMedia(@EndpointParam URI vmHref,
            @BinderParam(BindToXMLPayload.class) MediaInsertOrEjectParams mediaParams);

   /**
    * @see VmApi#getNetworkConnectionSection(URI)
    */
   @GET
   @Path("/networkConnectionSection")
   @Consumes
   @JAXBResponseParser
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<NetworkConnectionSection> getNetworkConnectionSection(@EndpointParam URI vmHref);

   /**
    * @see VmApi#editNetworkConnectionSection(URI, NetworkConnectionSection)
    */
   @PUT
   @Path("/networkConnectionSection")
   @Produces(NETWORK_CONNECTION_SECTION)
   @Consumes(TASK)
   @JAXBResponseParser
   ListenableFuture<Task> editNetworkConnectionSection(@EndpointParam URI vmHref,
            @BinderParam(BindToXMLPayload.class) NetworkConnectionSection section);

   /**
    * @see VmApi#getOperatingSystemSection(URI)
    */
   @GET
   @Path("/operatingSystemSection")
   @Consumes
   @JAXBResponseParser
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<OperatingSystemSection> getOperatingSystemSection(@EndpointParam URI vmHref);

   /**
    * @see VmApi#editOperatingSystemSection(URI, OperatingSystemSection)
    */
   @PUT
   @Path("/operatingSystemSection")
   @Produces(OPERATING_SYSTEM_SECTION)
   @Consumes(TASK)
   @JAXBResponseParser
   ListenableFuture<Task> editOperatingSystemSection(@EndpointParam URI vmHref,
            @BinderParam(BindToXMLPayload.class) OperatingSystemSection section);

   /**
    * @see VmApi#getProductSections(URI)
    */
   @GET
   @Path("/productSections")
   @Consumes
   @JAXBResponseParser
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<ProductSectionList> getProductSections(@EndpointParam URI vmHref);

   /**
    * @see VmApi#editProductSections(URI, ProductSectionList)
    */
   @PUT
   @Path("/productSections")
   @Produces(PRODUCT_SECTION_LIST)
   @Consumes(TASK)
   @JAXBResponseParser
   ListenableFuture<Task> editProductSections(@EndpointParam URI vmHref,
            @BinderParam(BindToXMLPayload.class) ProductSectionList sectionList);

   /**
    * @see VmApi#getPendingQuestion(URI)
    */
   @GET
   @Path("/question")
   @Consumes
   @JAXBResponseParser
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<VmPendingQuestion> getPendingQuestion(@EndpointParam URI vmHref);

   /**
    * @see VmApi#answerQuestion(URI, VmQuestionAnswer)
    */
   @POST
   @Path("/question/action/answer")
   @Produces(VM_PENDING_ANSWER)
   @Consumes
   @JAXBResponseParser
   ListenableFuture<Void> answerQuestion(@EndpointParam URI vmHref,
            @BinderParam(BindToXMLPayload.class) VmQuestionAnswer answer);

   /**
    * @see VmApi#getRuntimeInfoSection(URI)
    */
   @GET
   @Path("/runtimeInfoSection")
   @Consumes
   @JAXBResponseParser
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<RuntimeInfoSection> getRuntimeInfoSection(@EndpointParam URI vmHref);

   /**
    * @see VmApi#getScreenImage(URI)
    */
   @GET
   @Path("/screen")
   @Consumes(ANY_IMAGE)
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   @ResponseParser(ReturnPayloadBytes.class)
   ListenableFuture<byte[]> getScreenImage(@EndpointParam URI vmHref);

   /**
    * @see VmApi#getScreenTicket(URI)
    */
   @POST
   @Path("/screen/action/acquireTicket")
   @Consumes
   @JAXBResponseParser
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<ScreenTicket> getScreenTicket(@EndpointParam URI vmHref);

   /**
    * @see VmApi#getVirtualHardwareSection(URI)
    */
   @GET
   @Path("/virtualHardwareSection")
   @Consumes
   @JAXBResponseParser
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<VirtualHardwareSection> getVirtualHardwareSection(@EndpointParam URI vmHref);

   /**
    * @see VmApi#editVirtualHardwareSection(URI, VirtualHardwareSection)
    */
   @PUT
   @Path("/virtualHardwareSection")
   @Produces(VIRTUAL_HARDWARE_SECTION)
   @Consumes(TASK)
   @JAXBResponseParser
   ListenableFuture<Task> editVirtualHardwareSection(@EndpointParam URI vmHref,
            @BinderParam(BindToXMLPayload.class) VirtualHardwareSection section);

   /**
    * @see VmApi#getVirtualHardwareSectionCpu(URI)
    */
   @GET
   @Path("/virtualHardwareSection/cpu")
   @Consumes
   @JAXBResponseParser
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<RasdItem> getVirtualHardwareSectionCpu(@EndpointParam URI vmHref);

   /**
    * @see VmApi#editVirtualHardwareSectionCpu(URI, ResourceAllocationSettingData)
    */
   @PUT
   @Path("/virtualHardwareSection/cpu")
   @Produces(OVF_RASD_ITEM)
   @Consumes(TASK)
   @JAXBResponseParser
   ListenableFuture<Task> editVirtualHardwareSectionCpu(@EndpointParam URI vmHref,
            @BinderParam(BindToXMLPayload.class) RasdItem rasd);

   /**
    * @see VmApi#getVirtualHardwareSectionDisks(URI)
    */
   @GET
   @Path("/virtualHardwareSection/disks")
   @Consumes
   @JAXBResponseParser
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<RasdItemsList> getVirtualHardwareSectionDisks(@EndpointParam URI vmHref);

   /**
    * @see VmApi#editVirtualHardwareSectionDisks(URI, RasdItemsList)
    */
   @PUT
   @Path("/virtualHardwareSection/disks")
   @Produces(OVF_RASD_ITEMS_LIST)
   @Consumes(TASK)
   @JAXBResponseParser
   ListenableFuture<Task> editVirtualHardwareSectionDisks(@EndpointParam URI vmHref,
            @BinderParam(BindToXMLPayload.class) RasdItemsList rasdItemsList);

   /**
    * @see VmApi#getVirtualHardwareSectionMedia(URI)
    */
   @GET
   @Path("/virtualHardwareSection/media")
   @Consumes
   @JAXBResponseParser
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<RasdItemsList> getVirtualHardwareSectionMedia(@EndpointParam URI vmHref);

   /**
    * @see VmApi#getVirtualHardwareSectionMemory(URI)
    */
   @GET
   @Path("/virtualHardwareSection/memory")
   @Consumes
   @JAXBResponseParser
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<RasdItem> getVirtualHardwareSectionMemory(@EndpointParam URI vmHref);

   /**
    * @see VmApi#editVirtualHardwareSectionMemory(URI, ResourceAllocationSettingData)
    */
   @PUT
   @Path("/virtualHardwareSection/memory")
   @Produces(OVF_RASD_ITEM)
   @Consumes(TASK)
   @JAXBResponseParser
   ListenableFuture<Task> editVirtualHardwareSectionMemory(@EndpointParam URI vmHref,
            @BinderParam(BindToXMLPayload.class) RasdItem rasd);

   /**
    * @see VmApi#getVirtualHardwareSectionNetworkCards(URI)
    */
   @GET
   @Path("/virtualHardwareSection/networkCards")
   @Consumes
   @JAXBResponseParser
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<RasdItemsList> getVirtualHardwareSectionNetworkCards(@EndpointParam URI vmHref);

   /**
    * @see VmApi#editVirtualHardwareSectionNetworkCards(URI, RasdItemsList)
    */
   @PUT
   @Path("/virtualHardwareSection/networkCards")
   @Produces(OVF_RASD_ITEMS_LIST)
   @Consumes(TASK)
   @JAXBResponseParser
   ListenableFuture<Task> editVirtualHardwareSectionNetworkCards(@EndpointParam URI vmHref,
            @BinderParam(BindToXMLPayload.class) RasdItemsList rasdItemsList);

   /**
    * @see VmApi#getVirtualHardwareSectionSerialPorts(URI)
    */
   @GET
   @Path("/virtualHardwareSection/serialPorts")
   @Consumes
   @JAXBResponseParser
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<RasdItemsList> getVirtualHardwareSectionSerialPorts(@EndpointParam URI vmHref);

   /**
    * @see VmApi#editVirtualHardwareSectionSerialPorts(URI, RasdItemsList)
    */
   @PUT
   @Path("/virtualHardwareSection/serialPorts")
   @Produces(OVF_RASD_ITEMS_LIST)
   @Consumes(TASK)
   @JAXBResponseParser
   ListenableFuture<Task> editVirtualHardwareSectionSerialPorts(@EndpointParam URI vmHref,
            @BinderParam(BindToXMLPayload.class) RasdItemsList rasdItemsList);

   /**
    * Asynchronous access to {@Vm} {@link Metadata} features.
    */
   @Delegate
   MetadataAsyncApi.Writeable getMetadataApi(@EndpointParam(parser = VmURNToHref.class) String vmUrn);

   @Delegate
   MetadataAsyncApi.Writeable getMetadataApi(@EndpointParam URI vmHref);

}
