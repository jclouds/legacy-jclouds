/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.jclouds.abiquo.features;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.jclouds.abiquo.binders.AppendToPath;
import org.jclouds.abiquo.binders.BindToPath;
import org.jclouds.abiquo.binders.BindToXMLPayloadAndPath;
import org.jclouds.abiquo.binders.cloud.BindHardDiskRefsToPayload;
import org.jclouds.abiquo.binders.cloud.BindMoveVolumeToPath;
import org.jclouds.abiquo.binders.cloud.BindNetworkConfigurationRefToPayload;
import org.jclouds.abiquo.binders.cloud.BindNetworkRefToPayload;
import org.jclouds.abiquo.binders.cloud.BindVirtualDatacenterRefToPayload;
import org.jclouds.abiquo.binders.cloud.BindVolumeRefsToPayload;
import org.jclouds.abiquo.domain.cloud.VirtualDatacenter;
import org.jclouds.abiquo.domain.cloud.options.VirtualApplianceOptions;
import org.jclouds.abiquo.domain.cloud.options.VirtualDatacenterOptions;
import org.jclouds.abiquo.domain.cloud.options.VirtualMachineOptions;
import org.jclouds.abiquo.domain.cloud.options.VirtualMachineTemplateOptions;
import org.jclouds.abiquo.domain.cloud.options.VolumeOptions;
import org.jclouds.abiquo.domain.enterprise.Enterprise;
import org.jclouds.abiquo.domain.infrastructure.Datacenter;
import org.jclouds.abiquo.domain.network.options.IpOptions;
import org.jclouds.abiquo.functions.ReturnTaskReferenceOrNull;
import org.jclouds.abiquo.functions.cloud.ReturnMovedVolume;
import org.jclouds.abiquo.functions.enterprise.ParseEnterpriseId;
import org.jclouds.abiquo.functions.infrastructure.ParseDatacenterId;
import org.jclouds.abiquo.http.filters.AbiquoAuthentication;
import org.jclouds.abiquo.http.filters.AppendApiVersionToMediaType;
import org.jclouds.abiquo.reference.annotations.EnterpriseEdition;
import org.jclouds.abiquo.rest.annotations.EndpointLink;
import org.jclouds.http.functions.ReturnStringIf2xx;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.ExceptionParser;
import org.jclouds.rest.annotations.JAXBResponseParser;
import org.jclouds.rest.annotations.ParamParser;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.ResponseParser;
import org.jclouds.rest.binders.BindToXMLPayload;
import org.jclouds.rest.functions.ReturnNullOnNotFoundOr404;

import com.abiquo.model.transport.AcceptedRequestDto;
import com.abiquo.model.transport.LinksDto;
import com.abiquo.server.core.appslibrary.VirtualMachineTemplateDto;
import com.abiquo.server.core.appslibrary.VirtualMachineTemplatesDto;
import com.abiquo.server.core.cloud.VirtualApplianceDto;
import com.abiquo.server.core.cloud.VirtualApplianceStateDto;
import com.abiquo.server.core.cloud.VirtualAppliancesDto;
import com.abiquo.server.core.cloud.VirtualDatacenterDto;
import com.abiquo.server.core.cloud.VirtualDatacentersDto;
import com.abiquo.server.core.cloud.VirtualMachineDto;
import com.abiquo.server.core.cloud.VirtualMachineStateDto;
import com.abiquo.server.core.cloud.VirtualMachineTaskDto;
import com.abiquo.server.core.cloud.VirtualMachineWithNodeExtendedDto;
import com.abiquo.server.core.cloud.VirtualMachinesWithNodeExtendedDto;
import com.abiquo.server.core.enterprise.EnterpriseDto;
import com.abiquo.server.core.infrastructure.DatacenterDto;
import com.abiquo.server.core.infrastructure.network.PrivateIpDto;
import com.abiquo.server.core.infrastructure.network.PrivateIpsDto;
import com.abiquo.server.core.infrastructure.network.PublicIpDto;
import com.abiquo.server.core.infrastructure.network.PublicIpsDto;
import com.abiquo.server.core.infrastructure.network.VLANNetworkDto;
import com.abiquo.server.core.infrastructure.network.VLANNetworksDto;
import com.abiquo.server.core.infrastructure.network.VMNetworkConfigurationDto;
import com.abiquo.server.core.infrastructure.network.VMNetworkConfigurationsDto;
import com.abiquo.server.core.infrastructure.storage.DiskManagementDto;
import com.abiquo.server.core.infrastructure.storage.DisksManagementDto;
import com.abiquo.server.core.infrastructure.storage.MovedVolumeDto;
import com.abiquo.server.core.infrastructure.storage.TierDto;
import com.abiquo.server.core.infrastructure.storage.TiersDto;
import com.abiquo.server.core.infrastructure.storage.VolumeManagementDto;
import com.abiquo.server.core.infrastructure.storage.VolumesManagementDto;
import com.google.common.util.concurrent.ListenableFuture;

/**
 * Provides asynchronous access to Abiquo Cloud API.
 * 
 * @see API: <a href="http://community.abiquo.com/display/ABI20/API+Reference">
 *      http://community.abiquo.com/display/ABI20/API+Reference</a>
 * @see CloudApi
 * @author Ignasi Barrera
 * @author Francesc Montserrat
 */
@RequestFilters({AbiquoAuthentication.class, AppendApiVersionToMediaType.class})
@Path("/cloud")
public interface CloudAsyncApi
{
    /*********************** Virtual Datacenter ***********************/

    /**
     * @see CloudApi#listVirtualDatacenters(VirtualDatacenterOptions)
     */
    @GET
    @Path("/virtualdatacenters")
    @Consumes(VirtualDatacentersDto.BASE_MEDIA_TYPE)
    @JAXBResponseParser
    ListenableFuture<VirtualDatacentersDto> listVirtualDatacenters(VirtualDatacenterOptions options);

    /**
     * @see CloudApi#getVirtualDatacenter(Integer)
     */
    @GET
    @Path("/virtualdatacenters/{virtualdatacenter}")
    @ExceptionParser(ReturnNullOnNotFoundOr404.class)
    @Consumes(VirtualDatacenterDto.BASE_MEDIA_TYPE)
    @JAXBResponseParser
    ListenableFuture<VirtualDatacenterDto> getVirtualDatacenter(
        @PathParam("virtualdatacenter") Integer virtualDatacenterId);

    /**
     * @see CloudApi#createVirtualDatacenter(VirtualDatacenterDto, Datacenter, Enterprise)
     */
    @POST
    @Path("/virtualdatacenters")
    @Consumes(VirtualDatacenterDto.BASE_MEDIA_TYPE)
    @Produces(VirtualDatacenterDto.BASE_MEDIA_TYPE)
    @JAXBResponseParser
    ListenableFuture<VirtualDatacenterDto> createVirtualDatacenter(
        @BinderParam(BindToXMLPayload.class) final VirtualDatacenterDto virtualDatacenter,
        @QueryParam("datacenter") @ParamParser(ParseDatacenterId.class) final DatacenterDto datacenter,
        @QueryParam("enterprise") @ParamParser(ParseEnterpriseId.class) final EnterpriseDto enterprise);

    /**
     * @see CloudApi#updateVirtualDatacenter(VirtualDatacenterDto)
     */
    @PUT
    @Consumes(VirtualDatacenterDto.BASE_MEDIA_TYPE)
    @Produces(VirtualDatacenterDto.BASE_MEDIA_TYPE)
    @JAXBResponseParser
    ListenableFuture<VirtualDatacenterDto> updateVirtualDatacenter(
        @EndpointLink("edit") @BinderParam(BindToXMLPayloadAndPath.class) VirtualDatacenterDto virtualDatacenter);

    /**
     * @see CloudApi#deleteVirtualDatacenter(VirtualDatacenterDto)
     */
    @DELETE
    ListenableFuture<Void> deleteVirtualDatacenter(
        @EndpointLink("edit") @BinderParam(BindToPath.class) VirtualDatacenterDto virtualDatacenter);

    /**
     * @see CloudApi#listAvailableTemplates(VirtualDatacenterDto)
     */
    @GET
    @Consumes(VirtualMachineTemplatesDto.BASE_MEDIA_TYPE)
    @JAXBResponseParser
    ListenableFuture<VirtualMachineTemplatesDto> listAvailableTemplates(
        @EndpointLink("templates") @BinderParam(BindToPath.class) VirtualDatacenterDto virtualDatacenter);

    /**
     * @see CloudApi#listAvailableTemplates(VirtualDatacenterDto, VirtualMachineTemplateOptions)
     */
    @GET
    @Consumes(VirtualMachineTemplatesDto.BASE_MEDIA_TYPE)
    @JAXBResponseParser
    ListenableFuture<VirtualMachineTemplatesDto> listAvailableTemplates(
        @EndpointLink("templates") @BinderParam(BindToPath.class) VirtualDatacenterDto virtualDatacenter,
        VirtualMachineTemplateOptions options);

    /**
     * @see CloudApi#listStorageTiers(VirtualDatacenterDto)
     */
    @EnterpriseEdition
    @GET
    @Consumes(TiersDto.BASE_MEDIA_TYPE)
    @JAXBResponseParser
    ListenableFuture<TiersDto> listStorageTiers(
        @EndpointLink("tiers") @BinderParam(BindToPath.class) VirtualDatacenterDto virtualDatacenter);

    /**
     * @see CloudApi#getStorageTier(VirtualDatacenterDto, Integer)
     */
    @EnterpriseEdition
    @GET
    @ExceptionParser(ReturnNullOnNotFoundOr404.class)
    @Consumes(TierDto.BASE_MEDIA_TYPE)
    @JAXBResponseParser
    ListenableFuture<TierDto> getStorageTier(
        @EndpointLink("tiers") @BinderParam(BindToPath.class) VirtualDatacenterDto virtualDatacenter,
        @BinderParam(AppendToPath.class) Integer tierId);

    /*********************** Public IP ***********************/

    /**
     * @see CloudApi#listAvailablePublicIps(VirtualDatacenterDto, IpOptions)
     */
    @GET
    @Consumes(PublicIpsDto.BASE_MEDIA_TYPE)
    @JAXBResponseParser
    ListenableFuture<PublicIpsDto> listAvailablePublicIps(
        @EndpointLink("topurchase") @BinderParam(BindToPath.class) VirtualDatacenterDto virtualDatacenter,
        IpOptions options);

    /**
     * @see CloudApi#listPurchasedPublicIps(VirtualDatacenterDto, IpOptions)
     */
    @GET
    @Consumes(PublicIpsDto.BASE_MEDIA_TYPE)
    @JAXBResponseParser
    ListenableFuture<PublicIpsDto> listPurchasedPublicIps(
        @EndpointLink("purchased") @BinderParam(BindToPath.class) VirtualDatacenterDto virtualDatacenter,
        IpOptions options);

    /**
     * @see CloudApi#purchasePublicIp(PublicIpDto)
     */
    @PUT
    @Consumes(PublicIpDto.BASE_MEDIA_TYPE)
    @JAXBResponseParser
    ListenableFuture<PublicIpDto> purchasePublicIp(
        @EndpointLink("purchase") @BinderParam(BindToPath.class) PublicIpDto publicIp);

    /**
     * @see CloudApi#releasePublicIp(PublicIpDto)
     */
    @PUT
    @Consumes(PublicIpDto.BASE_MEDIA_TYPE)
    @JAXBResponseParser
    ListenableFuture<PublicIpDto> releasePublicIp(
        @EndpointLink("release") @BinderParam(BindToPath.class) PublicIpDto publicIp);

    /*********************** Private Network ***********************/

    /**
     * @see CloudApi#listPrivateNetworks(VirtualDatacenter)
     */
    @GET
    @Consumes(VLANNetworksDto.BASE_MEDIA_TYPE)
    @JAXBResponseParser
    ListenableFuture<VLANNetworksDto> listPrivateNetworks(
        @EndpointLink("privatenetworks") @BinderParam(BindToPath.class) VirtualDatacenterDto virtualDatacenter);

    /**
     * @see CloudApi#getPrivateNetwork(VirtualDatacenterDto, Integer)
     */
    @GET
    @ExceptionParser(ReturnNullOnNotFoundOr404.class)
    @Consumes(VLANNetworkDto.BASE_MEDIA_TYPE)
    @JAXBResponseParser
    ListenableFuture<VLANNetworkDto> getPrivateNetwork(
        @EndpointLink("privatenetworks") @BinderParam(BindToPath.class) VirtualDatacenterDto virtualDatacenter,
        @BinderParam(AppendToPath.class) Integer privateNetworkId);

    /**
     * @see CloudApi#createPrivateNetwork(VirtualDatacenterDto, VLANNetworkDto)
     */
    @POST
    @Consumes(VLANNetworkDto.BASE_MEDIA_TYPE)
    @Produces(VLANNetworkDto.BASE_MEDIA_TYPE)
    @JAXBResponseParser
    ListenableFuture<VLANNetworkDto> createPrivateNetwork(
        @EndpointLink("privatenetworks") @BinderParam(BindToPath.class) VirtualDatacenterDto virtualDatacenter,
        @BinderParam(BindToXMLPayload.class) VLANNetworkDto privateNetwork);

    /**
     * @see CloudApi#updatePrivateNetwork(VLANNetworkDto)
     */
    @PUT
    @Consumes(VLANNetworkDto.BASE_MEDIA_TYPE)
    @Produces(VLANNetworkDto.BASE_MEDIA_TYPE)
    @JAXBResponseParser
    ListenableFuture<VLANNetworkDto> updatePrivateNetwork(
        @EndpointLink("edit") @BinderParam(BindToXMLPayloadAndPath.class) VLANNetworkDto privateNetwork);

    /**
     * @see CloudApi#deletePrivateNetwork(VLANNetworkDto)
     */
    @DELETE
    ListenableFuture<Void> deletePrivateNetwork(
        @EndpointLink("edit") @BinderParam(BindToPath.class) VLANNetworkDto privateNetwork);

    /**
     * @see CloudApi#getDefaultNetwork(VirtualDatacenterDto)
     */
    @GET
    @Consumes(VLANNetworkDto.BASE_MEDIA_TYPE)
    @JAXBResponseParser
    ListenableFuture<VLANNetworkDto> getDefaultNetwork(
        @EndpointLink("defaultnetwork") @BinderParam(BindToPath.class) VirtualDatacenterDto virtualDatacenter);

    /**
     * @see CloudApi#setDefaultNetwork(VirtualDatacenterDto, VLANNetworkDto)
     */
    @PUT
    @Produces(LinksDto.BASE_MEDIA_TYPE)
    ListenableFuture<Void> setDefaultNetwork(
        @EndpointLink("defaultvlan") @BinderParam(BindToPath.class) VirtualDatacenterDto virtualDatacenter,
        @BinderParam(BindNetworkRefToPayload.class) VLANNetworkDto network);

    /*********************** Private Network IPs ***********************/

    /**
     * @see CloudApi#listPrivateNetworkIps(VLANNetworkDto)
     */
    @GET
    @Consumes(PrivateIpsDto.BASE_MEDIA_TYPE)
    @JAXBResponseParser
    ListenableFuture<PrivateIpsDto> listPrivateNetworkIps(
        @EndpointLink("ips") @BinderParam(BindToPath.class) VLANNetworkDto network);

    /**
     * @see CloudApi#listPrivateNetworkIps(VLANNetworkDto, IpOptions)
     */
    @GET
    @Consumes(PrivateIpsDto.BASE_MEDIA_TYPE)
    @JAXBResponseParser
    ListenableFuture<PrivateIpsDto> listPrivateNetworkIps(
        @EndpointLink("ips") @BinderParam(BindToPath.class) VLANNetworkDto network,
        IpOptions options);

    /**
     * @see CloudApi#getPrivateNetworkIp(VLANNetworkDto, Integer)
     */
    @GET
    @Consumes(PrivateIpDto.BASE_MEDIA_TYPE)
    @JAXBResponseParser
    ListenableFuture<PrivateIpDto> getPrivateNetworkIp(
        @EndpointLink("ips") @BinderParam(BindToPath.class) VLANNetworkDto network,
        @BinderParam(AppendToPath.class) Integer ipId);

    /*********************** Virtual Appliance ***********************/

    /**
     * @see CloudApi#listVirtualAppliances(VirtualDatacenterDto)
     */
    @GET
    @Consumes(VirtualAppliancesDto.BASE_MEDIA_TYPE)
    @JAXBResponseParser
    ListenableFuture<VirtualAppliancesDto> listVirtualAppliances(
        @EndpointLink("virtualappliances") @BinderParam(BindToPath.class) VirtualDatacenterDto virtualDatacenter);

    /**
     * @see CloudApi#getVirtualAppliance(VirtualDatacenterDto, Integer)
     */
    @GET
    @ExceptionParser(ReturnNullOnNotFoundOr404.class)
    @Consumes(VirtualApplianceDto.BASE_MEDIA_TYPE)
    @JAXBResponseParser
    ListenableFuture<VirtualApplianceDto> getVirtualAppliance(
        @EndpointLink("virtualappliances") @BinderParam(BindToPath.class) VirtualDatacenterDto virtualDatacenter,
        @BinderParam(AppendToPath.class) Integer virtualApplianceId);

    /**
     * @see CloudApi#getVirtualApplianceState(VirtualApplianceDto)
     */
    @GET
    @Consumes(VirtualApplianceStateDto.BASE_MEDIA_TYPE)
    @JAXBResponseParser
    ListenableFuture<VirtualApplianceStateDto> getVirtualApplianceState(
        @EndpointLink("state") @BinderParam(BindToPath.class) VirtualApplianceDto virtualAppliance);

    /**
     * @see CloudApi#createVirtualAppliance(VirtualDatacenterDto, VirtualApplianceDto)
     */
    @POST
    @Consumes(VirtualApplianceDto.BASE_MEDIA_TYPE)
    @Produces(VirtualApplianceDto.BASE_MEDIA_TYPE)
    @JAXBResponseParser
    ListenableFuture<VirtualApplianceDto> createVirtualAppliance(
        @EndpointLink("virtualappliances") @BinderParam(BindToPath.class) VirtualDatacenterDto virtualDatacenter,
        @BinderParam(BindToXMLPayload.class) VirtualApplianceDto virtualAppliance);

    /**
     * @see CloudApi#updateVirtualAppliance(VirtualApplianceDto)
     */
    @PUT
    @Consumes(VirtualApplianceDto.BASE_MEDIA_TYPE)
    @Produces(VirtualApplianceDto.BASE_MEDIA_TYPE)
    @JAXBResponseParser
    ListenableFuture<VirtualApplianceDto> updateVirtualAppliance(
        @EndpointLink("edit") @BinderParam(BindToXMLPayloadAndPath.class) VirtualApplianceDto virtualAppliance);

    /**
     * @see CloudApi#deleteVirtualAppliance(VirtualApplianceDto)
     */
    @DELETE
    ListenableFuture<Void> deleteVirtualAppliance(
        @EndpointLink("edit") @BinderParam(BindToPath.class) VirtualApplianceDto virtualAppliance);

    /**
     * @see CloudApi#deleteVirtualAppliance(VirtualApplianceDto, VirtualApplianceOptions)
     */
    @DELETE
    ListenableFuture<Void> deleteVirtualAppliance(
        @EndpointLink("edit") @BinderParam(BindToPath.class) VirtualApplianceDto virtualAppliance,
        VirtualApplianceOptions options);

    /**
     * @see CloudApi#deployVirtualAppliance(VirtualApplianceDto, VirtualMachineTaskDto)
     */
    @POST
    @Consumes(AcceptedRequestDto.BASE_MEDIA_TYPE)
    @Produces(VirtualMachineTaskDto.BASE_MEDIA_TYPE)
    @JAXBResponseParser
    ListenableFuture<AcceptedRequestDto<String>> deployVirtualAppliance(
        @EndpointLink("deploy") @BinderParam(BindToPath.class) VirtualApplianceDto virtualAppliance,
        @BinderParam(BindToXMLPayload.class) VirtualMachineTaskDto task);

    /**
     * @see CloudApi#undeployVirtualAppliance(VirtualApplianceDto, VirtualMachineTaskDto)
     */
    @POST
    @Consumes(AcceptedRequestDto.BASE_MEDIA_TYPE)
    @Produces(VirtualMachineTaskDto.BASE_MEDIA_TYPE)
    @JAXBResponseParser
    ListenableFuture<AcceptedRequestDto<String>> undeployVirtualAppliance(
        @EndpointLink("undeploy") @BinderParam(BindToPath.class) VirtualApplianceDto virtualAppliance,
        @BinderParam(BindToXMLPayload.class) VirtualMachineTaskDto task);

    /**
     * @see CloudApi#getVirtualAppliancePrice(VirtualApplianceDto)
     */
    @GET
    @Consumes(MediaType.TEXT_PLAIN)
    @ResponseParser(ReturnStringIf2xx.class)
    ListenableFuture<String> getVirtualAppliancePrice(
        @EndpointLink("price") @BinderParam(BindToPath.class) VirtualApplianceDto virtualAppliance);

    /*********************** Virtual Machine ***********************/

    /**
     * @see CloudApi#listVirtualMachines(VirtualApplianceDto)
     */
    @GET
    @Consumes(VirtualMachinesWithNodeExtendedDto.BASE_MEDIA_TYPE)
    @JAXBResponseParser
    ListenableFuture<VirtualMachinesWithNodeExtendedDto> listVirtualMachines(
        @EndpointLink("virtualmachines") @BinderParam(BindToPath.class) VirtualApplianceDto virtualAppliance);

    /**
     * @see CloudApi#listVirtualMachines(VirtualApplianceDto, VirtualMachineOptions)
     */
    @GET
    @Consumes(VirtualMachinesWithNodeExtendedDto.BASE_MEDIA_TYPE)
    @JAXBResponseParser
    ListenableFuture<VirtualMachinesWithNodeExtendedDto> listVirtualMachines(
        @EndpointLink("virtualmachines") @BinderParam(BindToPath.class) VirtualApplianceDto virtualAppliance,
        VirtualMachineOptions options);

    /**
     * @see CloudApi#getVirtualMachine(VirtualApplianceDto, Integer)
     */
    @GET
    @ExceptionParser(ReturnNullOnNotFoundOr404.class)
    @Consumes(VirtualMachineWithNodeExtendedDto.BASE_MEDIA_TYPE)
    @JAXBResponseParser
    ListenableFuture<VirtualMachineWithNodeExtendedDto> getVirtualMachine(
        @EndpointLink("virtualmachines") @BinderParam(BindToPath.class) VirtualApplianceDto virtualAppliance,
        @BinderParam(AppendToPath.class) Integer virtualMachineId);

    /**
     * @see CloudApi#createVirtualMachine(VirtualApplianceDto, VirtualMachineWithNodeExtendedDto)
     */
    @POST
    @Consumes(VirtualMachineWithNodeExtendedDto.BASE_MEDIA_TYPE)
    @Produces(VirtualMachineWithNodeExtendedDto.BASE_MEDIA_TYPE)
    @JAXBResponseParser
    ListenableFuture<VirtualMachineWithNodeExtendedDto> createVirtualMachine(
        @EndpointLink("virtualmachines") @BinderParam(BindToPath.class) VirtualApplianceDto virtualAppliance,
        @BinderParam(BindToXMLPayload.class) VirtualMachineWithNodeExtendedDto virtualMachine);

    /**
     * @see CloudApi#deleteVirtualMachine(VirtualMachineDto)
     */
    @DELETE
    ListenableFuture<Void> deleteVirtualMachine(
        @EndpointLink("edit") @BinderParam(BindToPath.class) VirtualMachineDto virtualMachine);

    /**
     * @see CloudApi#updateVirtualMachine(VirtualMachineWithNodeExtendedDto)
     */
    @PUT
    @ResponseParser(ReturnTaskReferenceOrNull.class)
    @Consumes(AcceptedRequestDto.BASE_MEDIA_TYPE)
    @Produces(VirtualMachineWithNodeExtendedDto.BASE_MEDIA_TYPE)
    ListenableFuture<AcceptedRequestDto<String>> updateVirtualMachine(
        @EndpointLink("edit") @BinderParam(BindToXMLPayloadAndPath.class) VirtualMachineWithNodeExtendedDto virtualMachine);

    /**
     * @see CloudApi#updateVirtualMachine(VirtualMachineDto, VirtualMachineOptions)
     */
    @PUT
    @ResponseParser(ReturnTaskReferenceOrNull.class)
    @Consumes(AcceptedRequestDto.BASE_MEDIA_TYPE)
    @Produces(VirtualMachineWithNodeExtendedDto.BASE_MEDIA_TYPE)
    ListenableFuture<AcceptedRequestDto<String>> updateVirtualMachine(
        @EndpointLink("edit") @BinderParam(BindToXMLPayloadAndPath.class) VirtualMachineWithNodeExtendedDto virtualMachine,
        VirtualMachineOptions options);

    /**
     * @see CloudApi#changeVirtualMachineState(VirtualMachineDto, VirtualMachineStateDto)
     */
    @PUT
    @Consumes(AcceptedRequestDto.BASE_MEDIA_TYPE)
    @Produces(VirtualMachineStateDto.BASE_MEDIA_TYPE)
    @JAXBResponseParser
    ListenableFuture<AcceptedRequestDto<String>> changeVirtualMachineState(
        @EndpointLink("state") @BinderParam(BindToPath.class) VirtualMachineDto virtualMachine,
        @BinderParam(BindToXMLPayload.class) VirtualMachineStateDto state);

    /**
     * @see CloudApi#getVirtualMachineState(VirtualMachineDto)
     */
    @GET
    @Consumes(VirtualMachineStateDto.BASE_MEDIA_TYPE)
    @JAXBResponseParser
    ListenableFuture<VirtualMachineStateDto> getVirtualMachineState(
        @EndpointLink("state") @BinderParam(BindToPath.class) VirtualMachineDto virtualMachine);

    /**
     * @see CloudApi#listNetworkConfigurations(VirtualMachineDto)
     */
    @GET
    @Consumes(VMNetworkConfigurationsDto.BASE_MEDIA_TYPE)
    @JAXBResponseParser
    ListenableFuture<VMNetworkConfigurationsDto> listNetworkConfigurations(
        @EndpointLink("configurations") @BinderParam(BindToPath.class) VirtualMachineDto virtualMachine);

    /**
     * @see CloudApi#setGatewayNetwork(VirtualMachineDto, VMNetworkConfigurationDto)
     */
    @PUT
    @Produces(LinksDto.BASE_MEDIA_TYPE)
    ListenableFuture<Void> setGatewayNetwork(
        @EndpointLink("configurations") @BinderParam(BindToPath.class) VirtualMachineDto virtualMachine,
        @BinderParam(BindNetworkConfigurationRefToPayload.class) VLANNetworkDto network);

    /**
     * @see CloudApi#rebootVirtualMachine(VirtualMachineDto)
     */
    @POST
    @Consumes(AcceptedRequestDto.BASE_MEDIA_TYPE)
    @JAXBResponseParser
    ListenableFuture<AcceptedRequestDto<String>> rebootVirtualMachine(
        @EndpointLink("reset") @BinderParam(BindToPath.class) VirtualMachineDto virtualMachine);

    /*********************** Virtual Machine Template ***********************/

    /**
     * @see CloudApi#getVirtualMachineTemplate(VirtualMachineTemplateDto)
     */
    @GET
    @Consumes(VirtualMachineTemplateDto.BASE_MEDIA_TYPE)
    @JAXBResponseParser
    ListenableFuture<VirtualMachineTemplateDto> getVirtualMachineTemplate(
        @EndpointLink("virtualmachinetemplate") @BinderParam(BindToPath.class) VirtualMachineDto virtualMachine);

    /**
     * @see CloudApi#listAttachedVolumes(VirtualMachineDto)
     */
    @GET
    @Consumes(VolumesManagementDto.BASE_MEDIA_TYPE)
    @JAXBResponseParser
    ListenableFuture<VolumesManagementDto> listAttachedVolumes(
        @EndpointLink("volumes") @BinderParam(BindToPath.class) VirtualMachineDto virtualMachine);

    /**
     * @see CloudApi#detachAllVolumes(VirtualMachineDto)
     */
    @DELETE
    @ResponseParser(ReturnTaskReferenceOrNull.class)
    @Consumes(AcceptedRequestDto.BASE_MEDIA_TYPE)
    ListenableFuture<AcceptedRequestDto<String>> detachAllVolumes(
        @EndpointLink("volumes") @BinderParam(BindToPath.class) VirtualMachineDto virtualMachine);

    /**
     * @see CloudApi#replaceVolumes(VirtualMachineDto, VirtualMachineOptions,
     *      VolumeManagementDto...)
     */
    @PUT
    @ResponseParser(ReturnTaskReferenceOrNull.class)
    @Consumes(AcceptedRequestDto.BASE_MEDIA_TYPE)
    @Produces(LinksDto.BASE_MEDIA_TYPE)
    ListenableFuture<AcceptedRequestDto<String>> replaceVolumes(
        @EndpointLink("volumes") @BinderParam(BindToPath.class) VirtualMachineDto virtualMachine,
        VirtualMachineOptions options,
        @BinderParam(BindVolumeRefsToPayload.class) VolumeManagementDto... volumes);

    /**
     * @see CloudApi#listAttachedHardDisks(VirtualMachineDto)
     */
    @GET
    @Consumes(DisksManagementDto.BASE_MEDIA_TYPE)
    @JAXBResponseParser
    ListenableFuture<DisksManagementDto> listAttachedHardDisks(
        @EndpointLink("disks") @BinderParam(BindToPath.class) VirtualMachineDto virtualMachine);

    /**
     * @see CloudApi#detachAllHardDisks(VirtualMachineDto)
     */
    @DELETE
    @ResponseParser(ReturnTaskReferenceOrNull.class)
    @Consumes(AcceptedRequestDto.BASE_MEDIA_TYPE)
    ListenableFuture<AcceptedRequestDto<String>> detachAllHardDisks(
        @EndpointLink("disks") @BinderParam(BindToPath.class) VirtualMachineDto virtualMachine);

    /**
     * @see CloudApi#replaceHardDisks(VirtualMachineDto, DiskManagementDto...)
     */
    @PUT
    @ResponseParser(ReturnTaskReferenceOrNull.class)
    @Consumes(AcceptedRequestDto.BASE_MEDIA_TYPE)
    @Produces(LinksDto.BASE_MEDIA_TYPE)
    ListenableFuture<AcceptedRequestDto<String>> replaceHardDisks(
        @EndpointLink("disks") @BinderParam(BindToPath.class) VirtualMachineDto virtualMachine,
        @BinderParam(BindHardDiskRefsToPayload.class) DiskManagementDto... hardDisks);

    /**
     * @see CloudApi#deployVirtualMachine(VirtualMachineDto, VirtualMachineTaskDto)
     */
    @POST
    @Consumes(AcceptedRequestDto.BASE_MEDIA_TYPE)
    @Produces(VirtualMachineTaskDto.BASE_MEDIA_TYPE)
    @JAXBResponseParser
    ListenableFuture<AcceptedRequestDto<String>> deployVirtualMachine(
        @EndpointLink("deploy") @BinderParam(BindToPath.class) VirtualMachineDto virtualMachine,
        @BinderParam(BindToXMLPayload.class) VirtualMachineTaskDto task);

    /**
     * @see CloudApi#undeployVirtualMachine(VirtualMachineDto, VirtualMachineTaskDto)
     */
    @POST
    @Consumes(AcceptedRequestDto.BASE_MEDIA_TYPE)
    @Produces(VirtualMachineTaskDto.BASE_MEDIA_TYPE)
    @JAXBResponseParser
    ListenableFuture<AcceptedRequestDto<String>> undeployVirtualMachine(
        @EndpointLink("undeploy") @BinderParam(BindToPath.class) VirtualMachineDto virtualMachine,
        @BinderParam(BindToXMLPayload.class) VirtualMachineTaskDto task);

    /*********************** Hard disks ***********************/

    /**
     * @see CloudApi#listHardDisks(VirtualDatacenterDto)
     */
    @GET
    @Consumes(DisksManagementDto.BASE_MEDIA_TYPE)
    @JAXBResponseParser
    ListenableFuture<DisksManagementDto> listHardDisks(
        @EndpointLink("disks") @BinderParam(BindToPath.class) VirtualDatacenterDto virtualDatacenter);

    /**
     * @see CloudApi#getHardDisk(VirtualDatacenterDto, Integer)
     */
    @GET
    @ExceptionParser(ReturnNullOnNotFoundOr404.class)
    @Consumes(DiskManagementDto.BASE_MEDIA_TYPE)
    @JAXBResponseParser
    ListenableFuture<DiskManagementDto> getHardDisk(
        @EndpointLink("disks") @BinderParam(BindToPath.class) VirtualDatacenterDto virtualDatacenter,
        @BinderParam(AppendToPath.class) Integer diskId);

    /**
     * @see CloudApi#createHardDisk(VirtualDatacenterDto, DiskManagementDto)
     */
    @POST
    @Consumes(DiskManagementDto.BASE_MEDIA_TYPE)
    @Produces(DiskManagementDto.BASE_MEDIA_TYPE)
    @JAXBResponseParser
    ListenableFuture<DiskManagementDto> createHardDisk(
        @EndpointLink("disks") @BinderParam(BindToPath.class) VirtualDatacenterDto virtualDatacenter,
        @BinderParam(BindToXMLPayload.class) DiskManagementDto hardDisk);

    /**
     * @see CloudApi#deleteHardDisk(DiskManagementDto)
     */
    @DELETE
    ListenableFuture<Void> deleteHardDisk(
        @EndpointLink("edit") @BinderParam(BindToPath.class) DiskManagementDto hardDisk);

    /*********************** Volumes ***********************/

    /**
     * @see CloudApi#listVolumes(VirtualDatacenterDto)
     */
    @EnterpriseEdition
    @GET
    @Consumes(VolumesManagementDto.BASE_MEDIA_TYPE)
    @JAXBResponseParser
    ListenableFuture<VolumesManagementDto> listVolumes(
        @EndpointLink("volumes") @BinderParam(BindToPath.class) VirtualDatacenterDto virtualDatacenter);

    /**
     * @see CloudApi#listVolumes(VirtualDatacenterDto, VolumeOptions)
     */
    @EnterpriseEdition
    @GET
    @Consumes(VolumesManagementDto.BASE_MEDIA_TYPE)
    @JAXBResponseParser
    ListenableFuture<VolumesManagementDto> listVolumes(
        @EndpointLink("volumes") @BinderParam(BindToPath.class) VirtualDatacenterDto virtualDatacenter,
        VolumeOptions options);

    /**
     * @see CloudApi#getVolume(VirtualDatacenterDto, Integer)
     */
    @EnterpriseEdition
    @GET
    @ExceptionParser(ReturnNullOnNotFoundOr404.class)
    @Consumes(VolumeManagementDto.BASE_MEDIA_TYPE)
    @JAXBResponseParser
    ListenableFuture<VolumeManagementDto> getVolume(
        @EndpointLink("volumes") @BinderParam(BindToPath.class) VirtualDatacenterDto virtualDatacenter,
        @BinderParam(AppendToPath.class) Integer volumeId);

    /**
     * @see CloudApi#createVolume(VirtualDatacenterDto, VolumeManagementDto)
     */
    @EnterpriseEdition
    @POST
    @Consumes(VolumeManagementDto.BASE_MEDIA_TYPE)
    @Produces(VolumeManagementDto.BASE_MEDIA_TYPE)
    @JAXBResponseParser
    ListenableFuture<VolumeManagementDto> createVolume(
        @EndpointLink("volumes") @BinderParam(BindToPath.class) VirtualDatacenterDto virtualDatacenter,
        @BinderParam(BindToXMLPayload.class) VolumeManagementDto volume);

    /**
     * @see CloudApi#updateVolume(VolumeManagementDto)
     */
    @EnterpriseEdition
    @PUT
    @ResponseParser(ReturnTaskReferenceOrNull.class)
    @Consumes(AcceptedRequestDto.BASE_MEDIA_TYPE)
    @Produces(VolumeManagementDto.BASE_MEDIA_TYPE)
    ListenableFuture<AcceptedRequestDto<String>> updateVolume(
        @EndpointLink("edit") @BinderParam(BindToXMLPayloadAndPath.class) VolumeManagementDto volume);

    /**
     * @see CloudApi#updateVolume(VolumeManagementDto)
     */
    @EnterpriseEdition
    @DELETE
    ListenableFuture<Void> deleteVolume(
        @EndpointLink("edit") @BinderParam(BindToPath.class) VolumeManagementDto volume);

    /**
     * @see CloudApi#moveVolume(VolumeManagementDto, VirtualDatacenterDto)
     */
    @EnterpriseEdition
    @POST
    @ExceptionParser(ReturnMovedVolume.class)
    @Consumes(MovedVolumeDto.BASE_MEDIA_TYPE)
    @Produces(LinksDto.BASE_MEDIA_TYPE)
    @JAXBResponseParser
    ListenableFuture<VolumeManagementDto> moveVolume(
        @BinderParam(BindMoveVolumeToPath.class) VolumeManagementDto volume,
        @BinderParam(BindVirtualDatacenterRefToPayload.class) VirtualDatacenterDto newVirtualDatacenter);

}
