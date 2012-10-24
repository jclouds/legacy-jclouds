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

import org.jclouds.abiquo.binders.AppendToPath;
import org.jclouds.abiquo.binders.BindToPath;
import org.jclouds.abiquo.binders.BindToXMLPayloadAndPath;
import org.jclouds.abiquo.domain.enterprise.options.EnterpriseOptions;
import org.jclouds.abiquo.functions.infrastructure.ParseDatacenterId;
import org.jclouds.abiquo.http.filters.AbiquoAuthentication;
import org.jclouds.abiquo.http.filters.AppendApiVersionToMediaType;
import org.jclouds.abiquo.reference.annotations.EnterpriseEdition;
import org.jclouds.abiquo.rest.annotations.EndpointLink;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.ExceptionParser;
import org.jclouds.rest.annotations.JAXBResponseParser;
import org.jclouds.rest.annotations.ParamParser;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.binders.BindToXMLPayload;
import org.jclouds.rest.functions.ReturnNullOnNotFoundOr404;

import com.abiquo.am.model.TemplatesStateDto;
import com.abiquo.server.core.appslibrary.DatacenterRepositoryDto;
import com.abiquo.server.core.appslibrary.TemplateDefinitionListDto;
import com.abiquo.server.core.appslibrary.TemplateDefinitionListsDto;
import com.abiquo.server.core.cloud.VirtualAppliancesDto;
import com.abiquo.server.core.cloud.VirtualDatacentersDto;
import com.abiquo.server.core.cloud.VirtualMachinesWithNodeExtendedDto;
import com.abiquo.server.core.enterprise.DatacenterLimitsDto;
import com.abiquo.server.core.enterprise.DatacentersLimitsDto;
import com.abiquo.server.core.enterprise.EnterpriseDto;
import com.abiquo.server.core.enterprise.EnterprisePropertiesDto;
import com.abiquo.server.core.enterprise.EnterprisesDto;
import com.abiquo.server.core.enterprise.UserDto;
import com.abiquo.server.core.enterprise.UsersDto;
import com.abiquo.server.core.infrastructure.DatacenterDto;
import com.abiquo.server.core.infrastructure.DatacentersDto;
import com.abiquo.server.core.infrastructure.MachinesDto;
import com.abiquo.server.core.infrastructure.network.VLANNetworksDto;
import com.google.common.util.concurrent.ListenableFuture;

/**
 * Provides asynchronous access to Abiquo Enterprise API.
 * 
 * @see API: <a href="http://community.abiquo.com/display/ABI20/API+Reference">
 *      http://community.abiquo.com/display/ABI20/API+Reference</a>
 * @see EnterpriseApi
 * @author Ignasi Barrera
 * @author Francesc Montserrat
 */
@RequestFilters({ AbiquoAuthentication.class, AppendApiVersionToMediaType.class })
@Path("/admin")
public interface EnterpriseAsyncApi {
   /*********************** Enterprise ***********************/

   /**
    * @see EnterpriseApi#listEnterprises()
    */
   @GET
   @Path("/enterprises")
   @Consumes(EnterprisesDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   ListenableFuture<EnterprisesDto> listEnterprises();

   /**
    * @see EnterpriseApi#listEnterprises(EnterpriseOptions)
    */
   @GET
   @Path("/enterprises")
   @Consumes(EnterprisesDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   ListenableFuture<EnterprisesDto> listEnterprises(EnterpriseOptions options);

   /**
    * @see EnterpriseApi#listEnterprises(DatacenterDto, EnterpriseOptions)
    */
   @GET
   @Consumes(EnterprisesDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   ListenableFuture<EnterprisesDto> listEnterprises(
         @EndpointLink("enterprises") @BinderParam(BindToPath.class) DatacenterDto datacenter, EnterpriseOptions options);

   /**
    * @see EnterpriseApi#createEnterprise(EnterpriseDto)
    */
   @POST
   @Path("/enterprises")
   @Produces(EnterpriseDto.BASE_MEDIA_TYPE)
   @Consumes(EnterpriseDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   ListenableFuture<EnterpriseDto> createEnterprise(@BinderParam(BindToXMLPayload.class) EnterpriseDto enterprise);

   /**
    * @see EnterpriseApi#getEnterprise(Integer)
    */
   @GET
   @Path("/enterprises/{enterprise}")
   @Consumes(EnterpriseDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<EnterpriseDto> getEnterprise(@PathParam("enterprise") Integer enterpriseId);

   /**
    * @see EnterpriseApi#updateEnterprise(EnterpriseDto)
    */
   @PUT
   @Produces(EnterpriseDto.BASE_MEDIA_TYPE)
   @Consumes(EnterpriseDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   ListenableFuture<EnterpriseDto> updateEnterprise(
         @EndpointLink("edit") @BinderParam(BindToXMLPayloadAndPath.class) EnterpriseDto enterprise);

   /**
    * @see EnterpriseApi#deleteEnterprise(EnterpriseDto)
    */
   @DELETE
   ListenableFuture<Void> deleteEnterprise(@EndpointLink("edit") @BinderParam(BindToPath.class) EnterpriseDto enterprise);

   /**
    * @see EnterpriseApi#listAllowedDatacenters(Integer)
    */
   @GET
   @Path("/datacenters")
   @Consumes(DatacentersDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   ListenableFuture<DatacentersDto> listAllowedDatacenters(@QueryParam("idEnterprise") Integer enterpriseId);

   /**
    * @see EnterpriseApi#listVirtualDatacenters(EnterpriseDto)
    */
   @GET
   @Consumes(VirtualDatacentersDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   ListenableFuture<VirtualDatacentersDto> listVirtualDatacenters(
         @EndpointLink("cloud/virtualdatacenters") @BinderParam(BindToPath.class) EnterpriseDto enterprise);

   /*********************** Enterprise Properties ***********************/

   /**
    * @see EnterpriseApi#getEnterpriseProperties(EnterpriseDto)
    */
   @EnterpriseEdition
   @GET
   @Consumes(EnterprisePropertiesDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   ListenableFuture<EnterprisePropertiesDto> getEnterpriseProperties(
         @EndpointLink("properties") @BinderParam(BindToPath.class) EnterpriseDto enterprise);

   /**
    * @see EnterpriseApi#updateEnterpriseProperties(EnterprisePropertiesDto)
    */
   @EnterpriseEdition
   @PUT
   @Produces(EnterprisePropertiesDto.BASE_MEDIA_TYPE)
   @Consumes(EnterprisePropertiesDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   ListenableFuture<EnterprisePropertiesDto> updateEnterpriseProperties(
         @EndpointLink("edit") @BinderParam(BindToXMLPayloadAndPath.class) EnterprisePropertiesDto properties);

   /*********************** Enterprise Limits ***********************/

   /**
    * @see EnterpriseApi#createLimits(EnterpriseDto, DatacenterDto,
    *      DatacenterLimitsDto)
    */
   @POST
   @Produces(DatacenterLimitsDto.BASE_MEDIA_TYPE)
   @Consumes(DatacenterLimitsDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   ListenableFuture<DatacenterLimitsDto> createLimits(
         @EndpointLink("limits") @BinderParam(BindToPath.class) final EnterpriseDto enterprise,
         @QueryParam("datacenter") @ParamParser(ParseDatacenterId.class) final DatacenterDto datacenter,
         @BinderParam(BindToXMLPayload.class) DatacenterLimitsDto limits);

   /**
    * @see EnterpriseApi#getLimits(EnterpriseDto, DatacenterDto)
    */
   @GET
   @Consumes(DatacentersLimitsDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<DatacentersLimitsDto> getLimits(
         @EndpointLink("limits") @BinderParam(BindToPath.class) final EnterpriseDto enterprise,
         @QueryParam("datacenter") @ParamParser(ParseDatacenterId.class) final DatacenterDto datacenter);

   /**
    * @see EnterpriseApi#updateLimits(DatacenterLimitsDto)
    */
   @PUT
   @Produces(DatacenterLimitsDto.BASE_MEDIA_TYPE)
   @Consumes(DatacenterLimitsDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   ListenableFuture<DatacenterLimitsDto> updateLimits(
         @EndpointLink("edit") @BinderParam(BindToXMLPayloadAndPath.class) DatacenterLimitsDto limits);

   /**
    * @see EnterpriseApi#deleteLimits(DatacenterLimitsDto)
    */
   @DELETE
   ListenableFuture<Void> deleteLimits(@EndpointLink("edit") @BinderParam(BindToPath.class) DatacenterLimitsDto limits);

   /**
    * @see EnterpriseApi#listLimits(EnterpriseDto)
    */
   @GET
   @Consumes(DatacentersLimitsDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   ListenableFuture<DatacentersLimitsDto> listLimits(
         @EndpointLink("limits") @BinderParam(BindToPath.class) EnterpriseDto enterprise);

   /*********************** User ***********************/

   /**
    * @see EnterpriseApi#listUsers(EnterpriseDto)
    */
   @GET
   @Consumes(UsersDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   ListenableFuture<UsersDto> listUsers(@EndpointLink("users") @BinderParam(BindToPath.class) EnterpriseDto enterprise);

   /**
    * @see EnterpriseApi#getUser(EnterpriseDto, Integer)
    */
   @GET
   @Consumes(UserDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<UserDto> getUser(@EndpointLink("users") @BinderParam(BindToPath.class) EnterpriseDto enterprise,
         @BinderParam(AppendToPath.class) Integer userId);

   /**
    * @see EnterpriseApi#createUser(EnterpriseDto)
    */
   @POST
   @Produces(UserDto.BASE_MEDIA_TYPE)
   @Consumes(UserDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   ListenableFuture<UserDto> createUser(@EndpointLink("users") @BinderParam(BindToPath.class) EnterpriseDto enterprise,
         @BinderParam(BindToXMLPayload.class) UserDto user);

   /**
    * @see EnterpriseApi#updateUser(UserDto)
    */
   @PUT
   @Produces(UserDto.BASE_MEDIA_TYPE)
   @Consumes(UserDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   ListenableFuture<UserDto> updateUser(@EndpointLink("edit") @BinderParam(BindToXMLPayloadAndPath.class) UserDto user);

   /**
    * @see EnterpriseApi#deleteUser(UserDto)
    */
   @DELETE
   ListenableFuture<Void> deleteUser(@EndpointLink("edit") @BinderParam(BindToPath.class) UserDto user);

   /**
    * @see EnterpriseApi#listVirtualMachines(UserDto)
    */
   @GET
   @Consumes(VirtualMachinesWithNodeExtendedDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   ListenableFuture<VirtualMachinesWithNodeExtendedDto> listVirtualMachines(
         @EndpointLink("virtualmachines") @BinderParam(BindToPath.class) final UserDto user);

   /*********************** Datacenter Repository ***********************/

   /**
    * @see EnterpriseApi#getDatacenterRepository(EnterpriseDto, Integer)
    */
   @GET
   @Consumes(DatacenterRepositoryDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<UserDto> getDatacenterRepository(
         @EndpointLink("datacenterrepositories") @BinderParam(BindToPath.class) EnterpriseDto enterprise,
         @BinderParam(AppendToPath.class) Integer datacenterRepositoryId);

   /**
    * @see EnterpriseApi#refreshTemplateRepository(Integer, Integer)
    */
   @PUT
   @Path("/enterprises/{enterprise}/datacenterrepositories/{datacenterrepository}/actions/refresh")
   ListenableFuture<Void> refreshTemplateRepository(@PathParam("enterprise") Integer enterpriseId,
         @PathParam("datacenterrepository") Integer datacenterRepositoryId);

   /*********************** External Network ***********************/

   /**
    * @see EnterpriseApi#listExternalNetworks(EnterpriseDto)
    */
   @EnterpriseEdition
   @GET
   @Consumes(VLANNetworksDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   ListenableFuture<VLANNetworksDto> listExternalNetworks(
         @EndpointLink("externalnetworks") @BinderParam(BindToPath.class) EnterpriseDto enterprise);

   /*********************** Cloud ***********************/

   /**
    * @see EnterpriseApi#listVirtualAppliances(EnterpriseDto)
    */
   @GET
   @Consumes(VirtualAppliancesDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   ListenableFuture<VirtualAppliancesDto> listVirtualAppliances(
         @EndpointLink("virtualappliances") @BinderParam(BindToPath.class) final EnterpriseDto enterprise);

   /**
    * @see EnterpriseApi#listVirtualMachines(EnterpriseDto)
    */
   @GET
   @Consumes(VirtualMachinesWithNodeExtendedDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   ListenableFuture<VirtualMachinesWithNodeExtendedDto> listVirtualMachines(
         @EndpointLink("virtualmachines") @BinderParam(BindToPath.class) EnterpriseDto enterprise);

   /*********************** Machine ***********************/

   /**
    * @see EnterpriseApi#listVirtualMachines(EnterpriseDto)
    */
   @GET
   @Consumes(MachinesDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   ListenableFuture<MachinesDto> listReservedMachines(
         @EndpointLink("reservedmachines") @BinderParam(BindToPath.class) EnterpriseDto enterprise);

   /*********************** Template definition list ***********************/

   /**
    * @see EnterpriseApi#listTemplateDefinitionLists(EnterpriseDto)
    */
   @GET
   @Consumes(TemplateDefinitionListsDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   ListenableFuture<TemplateDefinitionListsDto> listTemplateDefinitionLists(
         @EndpointLink("appslib/templateDefinitionLists") @BinderParam(BindToPath.class) EnterpriseDto enterprise);

   /**
    * @see EnterpriseApi#createTemplateDefinitionList(EnterpriseDto,
    *      TemplateDefinitionListDto)
    */
   @POST
   @Produces(TemplateDefinitionListDto.BASE_MEDIA_TYPE)
   @Consumes(TemplateDefinitionListDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   ListenableFuture<TemplateDefinitionListDto> createTemplateDefinitionList(
         @EndpointLink("appslib/templateDefinitionLists") @BinderParam(BindToPath.class) EnterpriseDto enterprise,
         @BinderParam(BindToXMLPayload.class) TemplateDefinitionListDto templateList);

   /**
    * @see EnterpriseApi#updateTemplateDefinitionList(TemplateDefinitionListDto)
    */
   @PUT
   @Produces(TemplateDefinitionListDto.BASE_MEDIA_TYPE)
   @Consumes(TemplateDefinitionListDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   ListenableFuture<TemplateDefinitionListDto> updateTemplateDefinitionList(
         @EndpointLink("edit") @BinderParam(BindToXMLPayloadAndPath.class) TemplateDefinitionListDto templateList);

   /**
    * @see EnterpriseApi#deleteTemplateDefinitionList(EnterpriseDto)
    */
   @DELETE
   ListenableFuture<Void> deleteTemplateDefinitionList(
         @EndpointLink("edit") @BinderParam(BindToPath.class) TemplateDefinitionListDto templateList);

   /**
    * @see EnterpriseApi#getTemplateDefinitionList(EnterpriseDto, Integer)
    */
   @GET
   @Consumes(TemplateDefinitionListDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<TemplateDefinitionListsDto> getTemplateDefinitionList(
         @EndpointLink("appslib/templateDefinitionLists") @BinderParam(BindToPath.class) EnterpriseDto enterprise,
         @BinderParam(AppendToPath.class) Integer templateListId);

   /**
    * @see EnterpriseApi#getTemplateDefinitionList(EnterpriseDto, Integer)
    */
   @GET
   @Consumes(TemplatesStateDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   ListenableFuture<TemplatesStateDto> listTemplateListStatus(
         @EndpointLink("repositoryStatus") @BinderParam(BindToPath.class) TemplateDefinitionListDto templateList,
         @QueryParam("datacenterId") @ParamParser(ParseDatacenterId.class) DatacenterDto datacenter);
}
