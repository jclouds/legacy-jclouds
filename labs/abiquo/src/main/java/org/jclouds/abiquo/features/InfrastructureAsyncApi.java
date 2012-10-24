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
import org.jclouds.abiquo.binders.infrastructure.AppendMachineIdToPath;
import org.jclouds.abiquo.binders.infrastructure.AppendRemoteServiceTypeToPath;
import org.jclouds.abiquo.binders.infrastructure.BindSupportedDevicesLinkToPath;
import org.jclouds.abiquo.binders.infrastructure.ucs.BindLogicServerParameters;
import org.jclouds.abiquo.binders.infrastructure.ucs.BindOrganizationParameters;
import org.jclouds.abiquo.domain.infrastructure.options.DatacenterOptions;
import org.jclouds.abiquo.domain.infrastructure.options.IpmiOptions;
import org.jclouds.abiquo.domain.infrastructure.options.MachineOptions;
import org.jclouds.abiquo.domain.infrastructure.options.StoragePoolOptions;
import org.jclouds.abiquo.domain.network.options.IpOptions;
import org.jclouds.abiquo.domain.network.options.NetworkOptions;
import org.jclouds.abiquo.domain.options.search.FilterOptions;
import org.jclouds.abiquo.functions.ReturnAbiquoExceptionOnNotFoundOr4xx;
import org.jclouds.abiquo.functions.ReturnFalseIfNotAvailable;
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

import com.abiquo.model.enumerator.HypervisorType;
import com.abiquo.model.enumerator.RemoteServiceType;
import com.abiquo.server.core.cloud.HypervisorTypesDto;
import com.abiquo.server.core.cloud.VirtualMachineWithNodeExtendedDto;
import com.abiquo.server.core.cloud.VirtualMachinesWithNodeExtendedDto;
import com.abiquo.server.core.enterprise.DatacentersLimitsDto;
import com.abiquo.server.core.enterprise.EnterpriseDto;
import com.abiquo.server.core.infrastructure.BladeLocatorLedDto;
import com.abiquo.server.core.infrastructure.DatacenterDto;
import com.abiquo.server.core.infrastructure.DatacentersDto;
import com.abiquo.server.core.infrastructure.FsmsDto;
import com.abiquo.server.core.infrastructure.LogicServerDto;
import com.abiquo.server.core.infrastructure.LogicServersDto;
import com.abiquo.server.core.infrastructure.MachineDto;
import com.abiquo.server.core.infrastructure.MachineIpmiStateDto;
import com.abiquo.server.core.infrastructure.MachineStateDto;
import com.abiquo.server.core.infrastructure.MachinesDto;
import com.abiquo.server.core.infrastructure.OrganizationDto;
import com.abiquo.server.core.infrastructure.OrganizationsDto;
import com.abiquo.server.core.infrastructure.RackDto;
import com.abiquo.server.core.infrastructure.RacksDto;
import com.abiquo.server.core.infrastructure.RemoteServiceDto;
import com.abiquo.server.core.infrastructure.RemoteServicesDto;
import com.abiquo.server.core.infrastructure.UcsRackDto;
import com.abiquo.server.core.infrastructure.UcsRacksDto;
import com.abiquo.server.core.infrastructure.network.ExternalIpDto;
import com.abiquo.server.core.infrastructure.network.ExternalIpsDto;
import com.abiquo.server.core.infrastructure.network.PublicIpDto;
import com.abiquo.server.core.infrastructure.network.PublicIpsDto;
import com.abiquo.server.core.infrastructure.network.UnmanagedIpDto;
import com.abiquo.server.core.infrastructure.network.UnmanagedIpsDto;
import com.abiquo.server.core.infrastructure.network.VLANNetworkDto;
import com.abiquo.server.core.infrastructure.network.VLANNetworksDto;
import com.abiquo.server.core.infrastructure.network.VlanTagAvailabilityDto;
import com.abiquo.server.core.infrastructure.storage.StorageDeviceDto;
import com.abiquo.server.core.infrastructure.storage.StorageDevicesDto;
import com.abiquo.server.core.infrastructure.storage.StorageDevicesMetadataDto;
import com.abiquo.server.core.infrastructure.storage.StoragePoolDto;
import com.abiquo.server.core.infrastructure.storage.StoragePoolsDto;
import com.abiquo.server.core.infrastructure.storage.TierDto;
import com.abiquo.server.core.infrastructure.storage.TiersDto;
import com.google.common.util.concurrent.ListenableFuture;

/**
 * Provides asynchronous access to Abiquo Infrastructure API.
 * 
 * @see API: <a href="http://community.abiquo.com/display/ABI20/API+Reference">
 *      http://community.abiquo.com/display/ABI20/API+Reference</a>
 * @see InfrastructureApi
 * @author Ignasi Barrera
 * @author Francesc Montserrat
 */
@RequestFilters({ AbiquoAuthentication.class, AppendApiVersionToMediaType.class })
@Path("/admin")
public interface InfrastructureAsyncApi {
   /*********************** Datacenter ***********************/

   /**
    * @see InfrastructureApi#listDatacenters()
    */
   @GET
   @Path("/datacenters")
   @Consumes(DatacentersDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   ListenableFuture<DatacentersDto> listDatacenters();

   /**
    * @see InfrastructureApi#createDatacenter(DatacenterDto)
    */
   @POST
   @Path("/datacenters")
   @Produces(DatacenterDto.BASE_MEDIA_TYPE)
   @Consumes(DatacenterDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   ListenableFuture<DatacenterDto> createDatacenter(@BinderParam(BindToXMLPayload.class) DatacenterDto datacenter);

   /**
    * @see InfrastructureApi#getDatacenter(Integer)
    */
   @GET
   @Path("/datacenters/{datacenter}")
   @Consumes(DatacenterDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<DatacenterDto> getDatacenter(@PathParam("datacenter") Integer datacenterId);

   /**
    * @see InfrastructureApi#updateDatacenter(DatacenterDto)
    */
   @PUT
   @Produces(DatacenterDto.BASE_MEDIA_TYPE)
   @Consumes(DatacenterDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   ListenableFuture<DatacenterDto> updateDatacenter(
         @EndpointLink("edit") @BinderParam(BindToXMLPayloadAndPath.class) DatacenterDto datacenter);

   /**
    * @see InfrastructureApi#deleteDatacenter(DatacenterDto)
    */
   @DELETE
   ListenableFuture<Void> deleteDatacenter(@EndpointLink("edit") @BinderParam(BindToPath.class) DatacenterDto datacenter);

   /**
    * @see InfrastructureApi#discoverSingleMachine(DatacenterDto, String,
    *      HypervisorType, String, String)
    */
   @GET
   @Consumes(MachineDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   @ExceptionParser(ReturnAbiquoExceptionOnNotFoundOr4xx.class)
   ListenableFuture<MachineDto> discoverSingleMachine(
         @EndpointLink("discoversingle") @BinderParam(BindToPath.class) DatacenterDto datacenter,
         @QueryParam("ip") String ip, @QueryParam("hypervisor") HypervisorType hypervisorType,
         @QueryParam("user") String user, @QueryParam("password") String password);

   /**
    * @see InfrastructureApi#discoverSingleMachine(DatacenterDto, String,
    *      HypervisorType, String, String, MachineOptions)
    */
   @GET
   @Consumes(MachineDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   @ExceptionParser(ReturnAbiquoExceptionOnNotFoundOr4xx.class)
   ListenableFuture<MachineDto> discoverSingleMachine(
         @EndpointLink("discoversingle") @BinderParam(BindToPath.class) DatacenterDto datacenter,
         @QueryParam("ip") String ip, @QueryParam("hypervisor") HypervisorType hypervisorType,
         @QueryParam("user") String user, @QueryParam("password") String password, MachineOptions options);

   /**
    * @see InfrastructureApi#discoverMultipleMachines(DatacenterDto, String,
    *      String, HypervisorType, String, String)
    */
   @GET
   @Consumes(MachinesDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   @ExceptionParser(ReturnAbiquoExceptionOnNotFoundOr4xx.class)
   ListenableFuture<MachineDto> discoverMultipleMachines(
         @EndpointLink("discovermultiple") @BinderParam(BindToPath.class) DatacenterDto datacenter,
         @QueryParam("ipFrom") String ipFrom, @QueryParam("ipTo") String ipTo,
         @QueryParam("hypervisor") HypervisorType hypervisorType, @QueryParam("user") String user,
         @QueryParam("password") String password);

   /**
    * @see InfrastructureApi#discoverMultipleMachines(DatacenterDto, String,
    *      String, HypervisorType, String, String, MachineOptions)
    */
   @GET
   @Consumes(MachinesDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   @ExceptionParser(ReturnAbiquoExceptionOnNotFoundOr4xx.class)
   ListenableFuture<MachineDto> discoverMultipleMachines(
         @EndpointLink("discovermultiple") @BinderParam(BindToPath.class) DatacenterDto datacenter,
         @QueryParam("ipFrom") String ipFrom, @QueryParam("ipTo") String ipTo,
         @QueryParam("hypervisor") HypervisorType hypervisorType, @QueryParam("user") String user,
         @QueryParam("password") String password, MachineOptions options);

   /**
    * @see InfrastructureApi#listLimits(DatacenterDto)
    */
   @GET
   @Consumes(DatacentersLimitsDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   ListenableFuture<DatacentersLimitsDto> listLimits(
         @EndpointLink("getLimits") @BinderParam(BindToPath.class) DatacenterDto datacenter);

   /**
    * @see InfrastructureApi#checkMachineState(DatacenterDto, String, String,
    *      HypervisorType, String, String)
    */
   @GET
   @Consumes(MachineStateDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   @ExceptionParser(ReturnAbiquoExceptionOnNotFoundOr4xx.class)
   ListenableFuture<MachineStateDto> checkMachineState(
         @EndpointLink("checkmachinestate") @BinderParam(BindToPath.class) DatacenterDto datacenter,
         @QueryParam("ip") String ip, @QueryParam("hypervisor") HypervisorType hypervisorType,
         @QueryParam("user") String user, @QueryParam("password") String password);

   /**
    * @see InfrastructureApi#checkMachineState(DatacenterDto, String, String,
    *      HypervisorType, String, String, MachineOptions)
    */
   @GET
   @Consumes(MachineStateDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   @ExceptionParser(ReturnAbiquoExceptionOnNotFoundOr4xx.class)
   ListenableFuture<MachineStateDto> checkMachineState(
         @EndpointLink("checkmachinestate") @BinderParam(BindToPath.class) DatacenterDto datacenter,
         @QueryParam("ip") String ip, @QueryParam("hypervisor") HypervisorType hypervisorType,
         @QueryParam("user") String user, @QueryParam("password") String password, MachineOptions options);

   /**
    * @see InfrastructureApi#checkMachineIpmiState(DatacenterDto, String,
    *      String, String)
    */
   @GET
   @Consumes(MachineIpmiStateDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   @ExceptionParser(ReturnAbiquoExceptionOnNotFoundOr4xx.class)
   ListenableFuture<MachineIpmiStateDto> checkMachineIpmiState(
         @EndpointLink("checkmachineipmistate") @BinderParam(BindToPath.class) DatacenterDto datacenter,
         @QueryParam("ip") String ip, @QueryParam("user") String user, @QueryParam("password") String password);

   /**
    * @see InfrastructureApi#checkMachineIpmiState(DatacenterDto, String,
    *      String, String, IpmiOptions)
    */
   @GET
   @Consumes(MachineIpmiStateDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   @ExceptionParser(ReturnAbiquoExceptionOnNotFoundOr4xx.class)
   ListenableFuture<MachineIpmiStateDto> checkMachineIpmiState(
         @EndpointLink("checkmachineipmistate") @BinderParam(BindToPath.class) DatacenterDto datacenter,
         @QueryParam("ip") String ip, @QueryParam("user") String user, @QueryParam("password") String password,
         IpmiOptions options);

   /*********************** Hypervisor ***********************/
   /**
    * @see InfrastructureApi#getHypervisorTypeFromMachine(DatacenterDto,
    *      DatacenterOptions)
    */
   @GET
   @Consumes(MediaType.TEXT_PLAIN)
   @ResponseParser(ReturnStringIf2xx.class)
   ListenableFuture<String> getHypervisorTypeFromMachine(
         @EndpointLink("hypervisor") @BinderParam(BindToPath.class) DatacenterDto datacenter, DatacenterOptions options);

   /**
    * @see InfrastructureApi#getHypervisorTypes(DatacenterDto)
    */
   @GET
   @Consumes(HypervisorTypesDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   ListenableFuture<HypervisorTypesDto> getHypervisorTypes(
         @EndpointLink("hypervisors") @BinderParam(BindToPath.class) DatacenterDto datacenter);

   /*********************** Unmanaged Rack ***********************/

   /**
    * @see InfrastructureApi#listRacks(DatacenterDto)
    */
   @GET
   @Consumes(RacksDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   ListenableFuture<RacksDto> listRacks(@EndpointLink("racks") @BinderParam(BindToPath.class) DatacenterDto datacenter);

   /**
    * @see InfrastructureApi#createRack(DatacenterDto, RackDto)
    */
   @POST
   @Produces(RackDto.BASE_MEDIA_TYPE)
   @Consumes(RackDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   ListenableFuture<RackDto> createRack(@EndpointLink("racks") @BinderParam(BindToPath.class) DatacenterDto datacenter,
         @BinderParam(BindToXMLPayload.class) RackDto rack);

   /**
    * @see InfrastructureApi#getRack(DatacenterDto, Integer)
    */
   @GET
   @Consumes(RackDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<RackDto> getRack(@EndpointLink("racks") @BinderParam(BindToPath.class) DatacenterDto datacenter,
         @BinderParam(AppendToPath.class) Integer rackId);

   /**
    * @see InfrastructureApi#updateRack(RackDto)
    */
   @PUT
   @Consumes(RackDto.BASE_MEDIA_TYPE)
   @Produces(RackDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   ListenableFuture<RackDto> updateRack(@EndpointLink("edit") @BinderParam(BindToXMLPayloadAndPath.class) RackDto rack);

   /**
    * @see InfrastructureApi#deleteRack(RackDto)
    */
   @DELETE
   ListenableFuture<Void> deleteRack(@EndpointLink("edit") @BinderParam(BindToPath.class) RackDto rack);

   /*********************** Managed Rack ***********************/

   /**
    * @see InfrastructureApi#listManagedRacks(DatacenterDto)
    */
   @EnterpriseEdition
   @GET
   @Consumes(UcsRacksDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   ListenableFuture<UcsRacksDto> listManagedRacks(
         @EndpointLink("racks") @BinderParam(BindToPath.class) DatacenterDto datacenter);

   /**
    * @see InfrastructureApi#createManagedRack(DatacenterDto, UcsRackDto)
    */
   @EnterpriseEdition
   @POST
   @Produces(UcsRackDto.BASE_MEDIA_TYPE)
   @Consumes(UcsRackDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   ListenableFuture<UcsRackDto> createManagedRack(
         @EndpointLink("racks") @BinderParam(BindToPath.class) DatacenterDto datacenter,
         @BinderParam(BindToXMLPayload.class) UcsRackDto rack);

   /**
    * @see InfrastructureApi#getManagedRack(DatacenterDto, Integer)
    */
   @EnterpriseEdition
   @GET
   @Consumes(UcsRackDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<UcsRackDto> getManagedRack(
         @EndpointLink("racks") @BinderParam(BindToPath.class) DatacenterDto datacenter,
         @BinderParam(AppendToPath.class) Integer rackId);

   /**
    * @see InfrastructureApi#updateManagedRack(UcsRackDto)
    */
   @EnterpriseEdition
   @PUT
   @Consumes(UcsRackDto.BASE_MEDIA_TYPE)
   @Produces(UcsRackDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   ListenableFuture<UcsRackDto> updateManagedRack(
         @EndpointLink("edit") @BinderParam(BindToXMLPayloadAndPath.class) UcsRackDto rack);

   /**
    * @see InfrastructureApi#listServiceProfiles(UcsRackDto)
    */
   @EnterpriseEdition
   @GET
   @Consumes(LogicServersDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   ListenableFuture<LogicServersDto> listServiceProfiles(
         @EndpointLink("logicservers") @BinderParam(BindToPath.class) UcsRackDto rack);

   /**
    * @see InfrastructureApi#listServiceProfiles(UcsRackDto, QueryOptions)
    */
   @EnterpriseEdition
   @GET
   @Consumes(LogicServersDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   ListenableFuture<LogicServersDto> listServiceProfiles(
         @EndpointLink("logicservers") @BinderParam(BindToPath.class) UcsRackDto rack, FilterOptions options);

   /**
    * @see InfrastructureApi#listServiceProfileTemplates(UcsRackDto)
    */
   @EnterpriseEdition
   @GET
   @Consumes(LogicServersDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   ListenableFuture<LogicServersDto> listServiceProfileTemplates(
         @EndpointLink("ls-templates") @BinderParam(BindToPath.class) UcsRackDto rack);

   /**
    * @see InfrastructureApi#listServiceProfileTemplates(UcsRackDto,
    *      LogicServerOptions)
    */
   @EnterpriseEdition
   @GET
   @Consumes(LogicServersDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   ListenableFuture<LogicServersDto> listServiceProfileTemplates(
         @EndpointLink("ls-templates") @BinderParam(BindToPath.class) UcsRackDto rack, FilterOptions options);

   /**
    * @see InfrastructureApi#listOrganizations(UcsRackDto)
    */
   @EnterpriseEdition
   @GET
   @Consumes(OrganizationsDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   ListenableFuture<OrganizationsDto> listOrganizations(
         @EndpointLink("organizations") @BinderParam(BindToPath.class) UcsRackDto rack);

   /**
    * @see InfrastructureApi#listOrganizations(UcsRackDto, OrganizationOptions)
    */
   @EnterpriseEdition
   @GET
   @Consumes(OrganizationsDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   ListenableFuture<OrganizationsDto> listOrganizations(
         @EndpointLink("organizations") @BinderParam(BindToPath.class) UcsRackDto rack, FilterOptions options);

   /**
    * @see InfrastructureApi#cloneLogicServer(UcsRackDto, LogicServerDto,
    *      OrganizationDto, String)
    */
   @EnterpriseEdition
   @POST
   ListenableFuture<Void> cloneLogicServer(@EndpointLink("ls-clone") @BinderParam(BindToPath.class) UcsRackDto rack,
         @BinderParam(BindLogicServerParameters.class) LogicServerDto logicServer,
         @BinderParam(BindOrganizationParameters.class) OrganizationDto organization,
         @QueryParam("newName") String newName);

   /**
    * @see InfrastructureApi#associateLogicServer(UcsRackDto, LogicServerDto,
    *      OrganizationDto, String)
    */
   @EnterpriseEdition
   @POST
   ListenableFuture<Void> associateLogicServer(
         @EndpointLink("ls-associate") @BinderParam(BindToPath.class) UcsRackDto rack,
         @BinderParam(BindLogicServerParameters.class) LogicServerDto logicServer,
         @BinderParam(BindOrganizationParameters.class) OrganizationDto organization,
         @QueryParam("bladeDn") String bladeName);

   /**
    * @see InfrastructureApi#associateTemplate(UcsRackDto, LogicServerDto,
    *      OrganizationDto, String, String)
    */
   @EnterpriseEdition
   @POST
   ListenableFuture<Void> associateTemplate(
         @EndpointLink("ls-associatetemplate") @BinderParam(BindToPath.class) UcsRackDto rack,
         @BinderParam(BindLogicServerParameters.class) LogicServerDto logicServer,
         @BinderParam(BindOrganizationParameters.class) OrganizationDto organization,
         @QueryParam("newName") String newName, @QueryParam("bladeDn") String bladeName);

   /**
    * @see InfrastructureApi#cloneAndAssociateLogicServer(UcsRackDto,
    *      LogicServerDto, OrganizationDto, String, String)
    */
   @EnterpriseEdition
   @POST
   ListenableFuture<Void> cloneAndAssociateLogicServer(
         @EndpointLink("ls-associateclone") @BinderParam(BindToPath.class) UcsRackDto rack,
         @BinderParam(BindLogicServerParameters.class) LogicServerDto logicServer,
         @BinderParam(BindOrganizationParameters.class) OrganizationDto organization,
         @QueryParam("newName") String newName, @QueryParam("bladeDn") String bladeName);

   /**
    * @see InfrastructureApi#dissociateLogicServer(UcsRackDto, LogicServerDto)
    */
   @EnterpriseEdition
   @POST
   ListenableFuture<Void> dissociateLogicServer(
         @EndpointLink("ls-dissociate") @BinderParam(BindToPath.class) UcsRackDto rack,
         @BinderParam(BindLogicServerParameters.class) LogicServerDto logicServer);

   /**
    * @see InfrastructureApi#deleteLogicServer(UcsRackDto, LogicServerDto)
    */
   @EnterpriseEdition
   @POST
   ListenableFuture<Void> deleteLogicServer(@EndpointLink("ls-delete") @BinderParam(BindToPath.class) UcsRackDto rack,
         @BinderParam(BindLogicServerParameters.class) LogicServerDto logicServer);

   /**
    * @see InfrastructureApi#listFsms(UcsRackDto, String)
    */
   @EnterpriseEdition
   @GET
   @Consumes(FsmsDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   ListenableFuture<FsmsDto> listFsms(@EndpointLink("fsm") @BinderParam(BindToPath.class) UcsRackDto rack,
         @QueryParam("dn") String dn);

   /*********************** Remote Service ***********************/

   /**
    * @see InfrastructureApi#listRemoteServices(DatacenterDto)
    */
   @GET
   @Consumes(RemoteServicesDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   ListenableFuture<RemoteServicesDto> listRemoteServices(
         @EndpointLink("remoteservices") @BinderParam(BindToPath.class) DatacenterDto datacenter);

   /**
    * @see InfrastructureApi#createRemoteService(DatacenterDto,
    *      RemoteServiceDto)
    */
   @POST
   @Produces(RemoteServiceDto.BASE_MEDIA_TYPE)
   @Consumes(RemoteServiceDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   ListenableFuture<RemoteServiceDto> createRemoteService(
         @EndpointLink("remoteservices") @BinderParam(BindToPath.class) DatacenterDto datacenter,
         @BinderParam(BindToXMLPayload.class) RemoteServiceDto remoteService);

   /**
    * @see InfrastructureApi#getRemoteService(DatacenterDto, RemoteServiceType)
    */
   @GET
   @Consumes(RemoteServiceDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<RemoteServiceDto> getRemoteService(
         @EndpointLink("remoteservices") @BinderParam(BindToPath.class) final DatacenterDto datacenter,
         @BinderParam(AppendRemoteServiceTypeToPath.class) final RemoteServiceType remoteServiceType);

   /**
    * @see InfrastructureApi#updateRemoteService(RemoteServiceDto)
    */
   @PUT
   @Consumes(RemoteServiceDto.BASE_MEDIA_TYPE)
   @Produces(RemoteServiceDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   ListenableFuture<RemoteServiceDto> updateRemoteService(
         @EndpointLink("edit") @BinderParam(BindToXMLPayloadAndPath.class) RemoteServiceDto remoteService);

   /**
    * @see InfrastructureApi#deleteRemoteService(RemoteServiceDto)
    */
   @DELETE
   ListenableFuture<Void> deleteRemoteService(
         @EndpointLink("edit") @BinderParam(BindToPath.class) RemoteServiceDto remoteService);

   /**
    * @see InfrastructureApi#isAvailable(RemoteServiceDto)
    */
   @GET
   @ExceptionParser(ReturnFalseIfNotAvailable.class)
   ListenableFuture<Boolean> isAvailable(
         @EndpointLink("check") @BinderParam(BindToPath.class) RemoteServiceDto remoteService);

   /*********************** Machine ***********************/

   /**
    * @see InfrastructureApi#listMachines(RackDto)
    */
   @GET
   @Consumes(MachinesDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   ListenableFuture<MachinesDto> listMachines(@EndpointLink("machines") @BinderParam(BindToPath.class) RackDto rack);

   /**
    * @see InfrastructureApi#createMachine(RackDto, MachineDto)
    */
   @POST
   @Produces(MachineDto.BASE_MEDIA_TYPE)
   @Consumes(MachineDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   ListenableFuture<MachineDto> createMachine(@EndpointLink("machines") @BinderParam(BindToPath.class) RackDto rack,
         @BinderParam(BindToXMLPayload.class) MachineDto machine);

   /**
    * @see InfrastructureApi#getMachine(RackDto, Integer)
    */
   @GET
   @Consumes(MachineDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<MachineDto> getMachine(@EndpointLink("machines") @BinderParam(BindToPath.class) final RackDto rack,
         @BinderParam(AppendToPath.class) Integer machineId);

   /**
    * @see InfrastructureApi#checkMachineState(MachineDto)
    */
   @GET
   @Consumes(MachineStateDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   ListenableFuture<MachineStateDto> checkMachineState(
         @EndpointLink("checkstate") @BinderParam(BindToPath.class) final MachineDto machine,
         @QueryParam("sync") boolean sync);

   /**
    * @see InfrastructureApi#checkMachineIpmiState(MachineDto)
    */
   @GET
   @Consumes(MachineIpmiStateDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   ListenableFuture<MachineIpmiStateDto> checkMachineIpmiState(
         @EndpointLink("checkipmistate") @BinderParam(BindToPath.class) final MachineDto machine);

   /**
    * @see InfrastructureApi#updateMachine(MachineDto)
    */
   @PUT
   @Produces(MachineDto.BASE_MEDIA_TYPE)
   @Consumes(MachineDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   ListenableFuture<MachineDto> updateMachine(
         @EndpointLink("edit") @BinderParam(BindToXMLPayloadAndPath.class) MachineDto machine);

   /**
    * @see InfrastructureApi#deleteMachine(MachineDto)
    */
   @DELETE
   ListenableFuture<Void> deleteMachine(@EndpointLink("edit") @BinderParam(BindToPath.class) MachineDto machine);

   /**
    * @see InfrastructureApi#reserveMachine(EnterpriseDto, MachineDto)
    */
   @POST
   @Consumes(MachineDto.BASE_MEDIA_TYPE)
   @Produces(MachineDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   ListenableFuture<MachineDto> reserveMachine(
         @EndpointLink("reservedmachines") @BinderParam(BindToPath.class) EnterpriseDto enterprise,
         @BinderParam(BindToXMLPayload.class) MachineDto machine);

   /**
    * @see InfrastructureApi#cancelReservation(EnterpriseDto, MachineDto)
    */
   @DELETE
   ListenableFuture<Void> cancelReservation(
         @EndpointLink("reservedmachines") @BinderParam(BindToPath.class) EnterpriseDto enterprise,
         @BinderParam(AppendMachineIdToPath.class) MachineDto machine);

   /*********************** Blade ***********************/

   /**
    * @see InfrastructureApi#powerOff(MachineDto)
    */
   @EnterpriseEdition
   @PUT
   ListenableFuture<Void> powerOff(@EndpointLink("poweroff") @BinderParam(BindToPath.class) MachineDto machine);

   /**
    * @see InfrastructureApi#powerOn(MachineDto)
    */
   @EnterpriseEdition
   @PUT
   ListenableFuture<Void> powerOn(@EndpointLink("poweron") @BinderParam(BindToPath.class) MachineDto machine);

   /**
    * @see InfrastructureApi#getLogicServer(MachineDto)
    */
   @EnterpriseEdition
   @GET
   @Consumes(LogicServerDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   ListenableFuture<LogicServerDto> getLogicServer(
         @EndpointLink("logicserver") @BinderParam(BindToPath.class) MachineDto machine);

   /**
    * @see InfrastructureApi#ledOn(MachineDto)
    */
   @EnterpriseEdition
   @POST
   ListenableFuture<Void> ledOn(@EndpointLink("ledon") @BinderParam(BindToPath.class) MachineDto machine);

   /**
    * @see InfrastructureApi#ledOff(MachineDto)
    */
   @EnterpriseEdition
   @POST
   ListenableFuture<Void> ledOff(@EndpointLink("ledoff") @BinderParam(BindToPath.class) MachineDto machine);

   /**
    * @see InfrastructureApi#getLedLocator(MachineDto)
    */
   @EnterpriseEdition
   @GET
   @Consumes(BladeLocatorLedDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   ListenableFuture<BladeLocatorLedDto> getLocatorLed(
         @EndpointLink("led") @BinderParam(BindToPath.class) MachineDto machine);

   /*********************** Storage Device ***********************/

   /**
    * @see InfrastructureApi#listVirtualMachinesByMachine(MachineDto)
    */
   @GET
   @Consumes(VirtualMachinesWithNodeExtendedDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   ListenableFuture<VirtualMachinesWithNodeExtendedDto> listVirtualMachinesByMachine(
         @EndpointLink("virtualmachines") @BinderParam(BindToPath.class) MachineDto machine, MachineOptions options);

   /**
    * @see InfrastructureApi#getVirtualMachine(MachineDto, Integer)
    */
   @GET
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   @Consumes(VirtualMachineWithNodeExtendedDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   ListenableFuture<VirtualMachineWithNodeExtendedDto> getVirtualMachine(
         @EndpointLink("virtualmachines") @BinderParam(BindToPath.class) MachineDto machine,
         @BinderParam(AppendToPath.class) Integer virtualMachineId);

   /*********************** Storage Device ***********************/

   /**
    * @see InfrastructureApi#listStorageDevices(DatacenterDto)
    */
   @EnterpriseEdition
   @GET
   @Consumes(StorageDevicesDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   ListenableFuture<StorageDevicesDto> listStorageDevices(
         @EndpointLink("devices") @BinderParam(BindToPath.class) DatacenterDto datacenter);

   /**
    * @see InfrastructureApi#listSupportedStorageDevices(DatacenterDto)
    */
   @EnterpriseEdition
   @GET
   @Consumes(StorageDevicesMetadataDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   ListenableFuture<StorageDevicesMetadataDto> listSupportedStorageDevices(
         @EndpointLink("devices") @BinderParam(BindSupportedDevicesLinkToPath.class) DatacenterDto datacenter);

   /**
    * @see InfrastructureApi#getStorageDevice(DatacenterDto, Integer)
    */
   @EnterpriseEdition
   @GET
   @Consumes(StorageDeviceDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<StorageDeviceDto> getStorageDevice(
         @EndpointLink("devices") @BinderParam(BindToPath.class) DatacenterDto datacenter,
         @BinderParam(AppendToPath.class) Integer storageDeviceId);

   /**
    * @see InfrastructureApi#createStorageDevice(DatacenterDto,
    *      StorageDeviceDto)
    */
   @EnterpriseEdition
   @POST
   @Produces(StorageDeviceDto.BASE_MEDIA_TYPE)
   @Consumes(StorageDeviceDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   ListenableFuture<StorageDeviceDto> createStorageDevice(
         @EndpointLink("devices") @BinderParam(BindToPath.class) DatacenterDto datacenter,
         @BinderParam(BindToXMLPayload.class) StorageDeviceDto storageDevice);

   /**
    * @see InfrastructureApi#deleteStorageDevice(StorageDeviceDto)
    */
   @EnterpriseEdition
   @DELETE
   ListenableFuture<Void> deleteStorageDevice(
         @EndpointLink("edit") @BinderParam(BindToPath.class) StorageDeviceDto storageDevice);

   /**
    * @see InfrastructureApi#updateStorageDevice(StorageDeviceDto)
    */
   @EnterpriseEdition
   @PUT
   @Produces(StorageDeviceDto.BASE_MEDIA_TYPE)
   @Consumes(StorageDeviceDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   ListenableFuture<StorageDeviceDto> updateStorageDevice(
         @EndpointLink("edit") @BinderParam(BindToXMLPayloadAndPath.class) StorageDeviceDto storageDevice);

   /*********************** Tier ***********************/

   /**
    * @see InfrastructureApi#listTiers(DatacenterDto)
    */
   @EnterpriseEdition
   @GET
   @Consumes(TiersDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   ListenableFuture<TiersDto> listTiers(@EndpointLink("tiers") @BinderParam(BindToPath.class) DatacenterDto datacenter);

   /**
    * @see InfrastructureApi#updateTier(TierDto)
    */
   @EnterpriseEdition
   @PUT
   @Produces(TierDto.BASE_MEDIA_TYPE)
   @Consumes(TierDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   ListenableFuture<TierDto> updateTier(@EndpointLink("edit") @BinderParam(BindToXMLPayloadAndPath.class) TierDto tier);

   /**
    * @see InfrastructureApi#getTier(DatacenterDto, Integer)
    */
   @EnterpriseEdition
   @GET
   @Consumes(TierDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<TierDto> getTier(@EndpointLink("tiers") @BinderParam(BindToPath.class) DatacenterDto datacenter,
         @BinderParam(AppendToPath.class) Integer tierId);

   /*********************** Storage Pool ***********************/

   /**
    * @see InfrastructureApi#listStoragePools(StorageDeviceDto,
    *      StoragePoolOptions)
    */
   @EnterpriseEdition
   @GET
   @Consumes(StoragePoolsDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   ListenableFuture<StoragePoolsDto> listStoragePools(
         @EndpointLink("pools") @BinderParam(BindToPath.class) StorageDeviceDto storageDevice,
         StoragePoolOptions options);

   /**
    * @see InfrastructureApi#listStoragePools(TierDto)
    */
   @EnterpriseEdition
   @GET
   @Consumes(StoragePoolsDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   ListenableFuture<StoragePoolsDto> listStoragePools(@EndpointLink("pools") @BinderParam(BindToPath.class) TierDto tier);

   /**
    * @see InfrastructureApi#createStoragePool(StorageDeviceDto, StoragePoolDto)
    */
   @EnterpriseEdition
   @POST
   @Consumes(StoragePoolDto.BASE_MEDIA_TYPE)
   @Produces(StoragePoolDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   ListenableFuture<StoragePoolDto> createStoragePool(
         @EndpointLink("pools") @BinderParam(BindToPath.class) StorageDeviceDto storageDevice,
         @BinderParam(BindToXMLPayload.class) StoragePoolDto storagePool);

   /**
    * @see InfrastructureApi#updateStoragePool(StoragePoolDto)
    */
   @EnterpriseEdition
   @PUT
   // For the most strangest reason in world, compiler does not accept
   // constants StoragePoolDto.BASE_MEDIA_TYPE for this method.
   @Consumes("application/vnd.abiquo.storagepool+xml")
   @Produces("application/vnd.abiquo.storagepool+xml")
   @JAXBResponseParser
   ListenableFuture<StoragePoolDto> updateStoragePool(
         @EndpointLink("edit") @BinderParam(BindToXMLPayloadAndPath.class) StoragePoolDto StoragePoolDto);

   /**
    * @see InfrastructureApi#deleteStoragePool(StoragePoolDto)
    */
   @EnterpriseEdition
   @DELETE
   ListenableFuture<Void> deleteStoragePool(
         @EndpointLink("edit") @BinderParam(BindToPath.class) StoragePoolDto storagePool);

   /**
    * @see InfrastructureApi#getStoragePool(StorageDeviceDto, String)
    */
   @EnterpriseEdition
   @GET
   @Consumes(StoragePoolDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<StoragePoolDto> getStoragePool(
         @EndpointLink("pools") @BinderParam(BindToPath.class) final StorageDeviceDto storageDevice,
         @BinderParam(AppendToPath.class) final String storagePoolId);

   /**
    * @see InfrastructureApi#refreshStoragePool(StoragePoolDto,
    *      StoragePoolOptions)
    */
   @EnterpriseEdition
   @GET
   @Consumes(StoragePoolDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   ListenableFuture<StoragePoolDto> refreshStoragePool(
         @EndpointLink("edit") @BinderParam(BindToPath.class) StoragePoolDto storagePool, StoragePoolOptions options);

   /*********************** Network ***********************/

   /**
    * @see InfrastructureApi#listNetworks(DatacenterDto)
    */
   @EnterpriseEdition
   @GET
   @Consumes(VLANNetworksDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   ListenableFuture<VLANNetworksDto> listNetworks(
         @EndpointLink("network") @BinderParam(BindToPath.class) DatacenterDto datacenter);

   /**
    * @see InfrastructureApi#listNetwork(DatacenterDto, NetworkOptions)
    */
   @EnterpriseEdition
   @GET
   @Consumes(VLANNetworksDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   ListenableFuture<VLANNetworksDto> listNetworks(
         @EndpointLink("network") @BinderParam(BindToPath.class) DatacenterDto datacenter, NetworkOptions options);

   /**
    * @see InfrastructureApi#getNetwork(DatacenterDto, Integer)
    */
   @EnterpriseEdition
   @GET
   @Consumes(VLANNetworkDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<VLANNetworkDto> getNetwork(
         @EndpointLink("network") @BinderParam(BindToPath.class) DatacenterDto datacenter,
         @BinderParam(AppendToPath.class) Integer networkId);

   /**
    * @see InfrastructureApi#createNetwork(DatacenterDto, VLANNetworkDto)
    */
   @EnterpriseEdition
   @POST
   @Produces(VLANNetworkDto.BASE_MEDIA_TYPE)
   @Consumes(VLANNetworkDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   ListenableFuture<VLANNetworkDto> createNetwork(
         @EndpointLink("network") @BinderParam(BindToPath.class) DatacenterDto datacenter,
         @BinderParam(BindToXMLPayload.class) VLANNetworkDto network);

   /**
    * @see InfrastructureApi#updateNetwork(VLANNetworkDto)
    */
   @EnterpriseEdition
   @PUT
   @Produces(VLANNetworkDto.BASE_MEDIA_TYPE)
   @Consumes(VLANNetworkDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   ListenableFuture<VLANNetworkDto> updateNetwork(
         @EndpointLink("edit") @BinderParam(BindToXMLPayloadAndPath.class) VLANNetworkDto network);

   /**
    * @see InfrastructureApi#deleteNetwork(VLANNetworkDto)
    */
   @EnterpriseEdition
   @DELETE
   ListenableFuture<Void> deleteNetwork(@EndpointLink("edit") @BinderParam(BindToPath.class) VLANNetworkDto network);

   /**
    * @see InfrastructureApi#checkTagAvailability(DatacenterDto, Integer)
    */
   @EnterpriseEdition
   @GET
   @Path("/datacenters/{datacenter}/network/action/checkavailability")
   @Consumes(VlanTagAvailabilityDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   ListenableFuture<VlanTagAvailabilityDto> checkTagAvailability(
         @PathParam("datacenter") @ParamParser(ParseDatacenterId.class) DatacenterDto datacenter,
         @QueryParam("tag") Integer tag);

   /*********************** Public Network IPs ***********************/

   /**
    * @see InfrastructureApi#listPublicIps(VLANNetworkDto)
    */
   @GET
   @Consumes(PublicIpsDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   ListenableFuture<PublicIpsDto> listPublicIps(
         @EndpointLink("ips") @BinderParam(BindToPath.class) VLANNetworkDto network);

   /**
    * @see InfrastructureApi#listPublicIps(VLANNetworkDto, IpOptions)
    */
   @GET
   @Consumes(PublicIpsDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   ListenableFuture<PublicIpsDto> listPublicIps(
         @EndpointLink("ips") @BinderParam(BindToPath.class) VLANNetworkDto network, IpOptions options);

   /**
    * @see InfrastructureApi#getPublicIp(VLANNetworkDto, Integer)
    */
   @GET
   @Consumes(PublicIpDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   ListenableFuture<PublicIpDto> getPublicIp(
         @EndpointLink("ips") @BinderParam(BindToPath.class) VLANNetworkDto network,
         @BinderParam(AppendToPath.class) Integer ipId);

   /**
    * @see InfrastructureApi#listExternalIps(VLANNetworkDto)
    */
   @GET
   @Consumes(ExternalIpsDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   ListenableFuture<ExternalIpsDto> listExternalIps(
         @EndpointLink("ips") @BinderParam(BindToPath.class) VLANNetworkDto network);

   /**
    * @see InfrastructureApi#listExternalIps(VLANNetworkDto, IpOptions)
    */
   @GET
   @Consumes(ExternalIpsDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   ListenableFuture<ExternalIpsDto> listExternalIps(
         @EndpointLink("ips") @BinderParam(BindToPath.class) VLANNetworkDto network, IpOptions options);

   /**
    * @see InfrastructureApi#getExternalIp(VLANNetworkDto, Integer)
    */
   @GET
   @Consumes(ExternalIpDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   ListenableFuture<ExternalIpDto> getExternalIp(
         @EndpointLink("ips") @BinderParam(BindToPath.class) VLANNetworkDto network,
         @BinderParam(AppendToPath.class) Integer ipId);

   /**
    * @see InfrastructureApi#listUnmanagedIps(VLANNetworkDto)
    */
   @GET
   @Consumes(UnmanagedIpsDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   ListenableFuture<UnmanagedIpsDto> listUnmanagedIps(
         @EndpointLink("ips") @BinderParam(BindToPath.class) VLANNetworkDto network);

   /**
    * @see InfrastructureApi#listUnmanagedIps(VLANNetworkDto, IpOptions)
    */
   @GET
   @Consumes(UnmanagedIpsDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   ListenableFuture<UnmanagedIpsDto> listUnmanagedIps(
         @EndpointLink("ips") @BinderParam(BindToPath.class) VLANNetworkDto network, IpOptions options);

   /**
    * @see InfrastructureApi#getUnmanagedIp(VLANNetworkDto, Integer)
    */
   @GET
   @Consumes(UnmanagedIpDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   ListenableFuture<UnmanagedIpDto> getUnmanagedIp(
         @EndpointLink("ips") @BinderParam(BindToPath.class) VLANNetworkDto network,
         @BinderParam(AppendToPath.class) Integer ipId);
}
