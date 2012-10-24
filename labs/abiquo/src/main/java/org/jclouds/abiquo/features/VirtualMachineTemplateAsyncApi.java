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

import org.jclouds.abiquo.binders.AppendToPath;
import org.jclouds.abiquo.binders.BindToPath;
import org.jclouds.abiquo.binders.BindToXMLPayloadAndPath;
import org.jclouds.abiquo.domain.cloud.options.ConversionOptions;
import org.jclouds.abiquo.domain.cloud.options.VirtualMachineTemplateOptions;
import org.jclouds.abiquo.functions.ReturnTaskReferenceOrNull;
import org.jclouds.abiquo.http.filters.AbiquoAuthentication;
import org.jclouds.abiquo.http.filters.AppendApiVersionToMediaType;
import org.jclouds.abiquo.rest.annotations.EndpointLink;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.ExceptionParser;
import org.jclouds.rest.annotations.JAXBResponseParser;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.ResponseParser;
import org.jclouds.rest.binders.BindToXMLPayload;
import org.jclouds.rest.functions.ReturnNullOnNotFoundOr404;

import com.abiquo.model.enumerator.DiskFormatType;
import com.abiquo.model.transport.AcceptedRequestDto;
import com.abiquo.server.core.appslibrary.ConversionDto;
import com.abiquo.server.core.appslibrary.ConversionsDto;
import com.abiquo.server.core.appslibrary.DatacenterRepositoryDto;
import com.abiquo.server.core.appslibrary.VirtualMachineTemplateDto;
import com.abiquo.server.core.appslibrary.VirtualMachineTemplatePersistentDto;
import com.abiquo.server.core.appslibrary.VirtualMachineTemplatesDto;
import com.google.common.util.concurrent.ListenableFuture;

/**
 * Provides asynchronous access to Abiquo Abiquo Apps library API. * @see API:
 * <a href="http://community.abiquo.com/display/ABI20/API+Reference">
 * http://community.abiquo.com/display/ABI20/API+Reference</a>
 * 
 * @see VirtualMachineTemplateApi
 * @author Ignasi Barrera
 * @author Francesc Montserrat
 */
@RequestFilters({ AbiquoAuthentication.class, AppendApiVersionToMediaType.class })
@Path("/admin/enterprises")
public interface VirtualMachineTemplateAsyncApi {
   /*********************** Virtual Machine Template ***********************/

   /**
    * @see VirtualMachineTemplateApi#listVirtualMachineTemplates(Integer,
    *      Integer)
    */
   @GET
   @Path("/{enterprise}/datacenterrepositories/{datacenterrepository}/virtualmachinetemplates")
   @Consumes(VirtualMachineTemplatesDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   ListenableFuture<VirtualMachineTemplatesDto> listVirtualMachineTemplates(
         @PathParam("enterprise") Integer enterpriseId,
         @PathParam("datacenterrepository") Integer datacenterRepositoryId);

   /**
    * @see VirtualMachineTemplateApi#listVirtualMachineTemplates(Integer,
    *      Integer, VirtualMachineTemplateOptions)
    */
   @GET
   @Path("/{enterprise}/datacenterrepositories/{datacenterrepository}/virtualmachinetemplates")
   @Consumes(VirtualMachineTemplatesDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   ListenableFuture<VirtualMachineTemplatesDto> listVirtualMachineTemplates(
         @PathParam("enterprise") Integer enterpriseId,
         @PathParam("datacenterrepository") Integer datacenterRepositoryId, VirtualMachineTemplateOptions options);

   /**
    * @see VirtualMachineTemplateApi#getVirtualMachineTemplate(Integer, Integer,
    *      Integer)
    */
   @GET
   @Path("/{enterprise}/datacenterrepositories/{datacenterrepository}/virtualmachinetemplates/{virtualmachinetemplate}")
   @Consumes(VirtualMachineTemplateDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<VirtualMachineTemplateDto> getVirtualMachineTemplate(@PathParam("enterprise") Integer enterpriseId,
         @PathParam("datacenterrepository") Integer datacenterRepositoryId,
         @PathParam("virtualmachinetemplate") Integer virtualMachineTemplateId);

   /**
    * @see VirtualMachineTemplateApi#updateVirtualMachineTemplate(VirtualMachineTemplateDto)
    */
   @PUT
   @Produces(VirtualMachineTemplateDto.BASE_MEDIA_TYPE)
   @Consumes(VirtualMachineTemplateDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   ListenableFuture<VirtualMachineTemplateDto> updateVirtualMachineTemplate(
         @EndpointLink("edit") @BinderParam(BindToXMLPayloadAndPath.class) VirtualMachineTemplateDto template);

   /**
    * @see VirtualMachineTemplateApi#deleteVirtualMachineTemplate(VirtualMachineTemplateDto)
    */
   @DELETE
   ListenableFuture<Void> deleteVirtualMachineTemplate(
         @EndpointLink("edit") @BinderParam(BindToPath.class) VirtualMachineTemplateDto template);

   /**
    * @see VirtualMachineTemplateApi#createPersistentVirtualMachineTemplate(DatacenterRepositoryDto,
    *      VirtualMachineTemplatePersistentDto)
    */
   @POST
   @Consumes(AcceptedRequestDto.BASE_MEDIA_TYPE)
   @Produces(VirtualMachineTemplatePersistentDto.BASE_MEDIA_TYPE)
   @Path("/{enterprise}/datacenterrepositories/{datacenterrepository}/virtualmachinetemplates")
   @JAXBResponseParser
   ListenableFuture<AcceptedRequestDto<String>> createPersistentVirtualMachineTemplate(
         @PathParam("enterprise") Integer enterpriseId,
         @PathParam("datacenterrepository") Integer datacenterRepositoryId,
         @BinderParam(BindToXMLPayload.class) VirtualMachineTemplatePersistentDto persistentOptions);

   /*********************** Conversions ***********************/

   /**
    * @see VirtualMachineTemplateApi#listConversions(VirtualMachineTemplateDto)
    */
   @GET
   @Consumes(ConversionsDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   ListenableFuture<ConversionsDto> listConversions(
         @EndpointLink("conversions") @BinderParam(BindToPath.class) VirtualMachineTemplateDto template);

   /**
    * @see VirtualMachineTemplateApi#listConversions(VirtualMachineTemplateDto,
    *      ConversionOptions)
    */
   @GET
   @Consumes(ConversionsDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   ListenableFuture<ConversionsDto> listConversions(
         @EndpointLink("conversions") @BinderParam(BindToPath.class) final VirtualMachineTemplateDto template,
         ConversionOptions options);

   /**
    * @see VirtualMachineTemplateApi#getConversion(VirtualMachineTemplateDto,
    *      DiskFormatType)
    */
   @GET
   @Consumes(ConversionDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<ConversionDto> getConversion(
         @EndpointLink("conversions") @BinderParam(BindToPath.class) final VirtualMachineTemplateDto template,
         @BinderParam(AppendToPath.class) DiskFormatType targetFormat);

   /**
    * @see VirtualMachineTemplateApi#updateConversion(ConversinoDto)
    */
   @PUT
   @ResponseParser(ReturnTaskReferenceOrNull.class)
   @Consumes(AcceptedRequestDto.BASE_MEDIA_TYPE)
   @Produces(ConversionDto.BASE_MEDIA_TYPE)
   ListenableFuture<AcceptedRequestDto<String>> requestConversion(
         @EndpointLink("conversions") @BinderParam(BindToPath.class) final VirtualMachineTemplateDto template,
         @BinderParam(AppendToPath.class) DiskFormatType targetFormat,
         @BinderParam(BindToXMLPayload.class) ConversionDto conversion);
}
