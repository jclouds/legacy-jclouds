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

import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.abiquo.binders.BindToPath;
import org.jclouds.abiquo.binders.BindToXMLPayloadAndPath;
import org.jclouds.abiquo.domain.config.options.LicenseOptions;
import org.jclouds.abiquo.domain.config.options.PropertyOptions;
import org.jclouds.abiquo.http.filters.AbiquoAuthentication;
import org.jclouds.abiquo.http.filters.AppendApiVersionToMediaType;
import org.jclouds.abiquo.reference.annotations.EnterpriseEdition;
import org.jclouds.abiquo.rest.annotations.EndpointLink;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.JAXBResponseParser;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.binders.BindToXMLPayload;

import com.abiquo.server.core.appslibrary.CategoriesDto;
import com.abiquo.server.core.appslibrary.CategoryDto;
import com.abiquo.server.core.cloud.VirtualDatacenterDto;
import com.abiquo.server.core.config.LicenseDto;
import com.abiquo.server.core.config.LicensesDto;
import com.abiquo.server.core.config.SystemPropertiesDto;
import com.abiquo.server.core.config.SystemPropertyDto;
import com.abiquo.server.core.enterprise.PrivilegeDto;
import com.abiquo.server.core.enterprise.PrivilegesDto;
import com.google.common.util.concurrent.ListenableFuture;

/**
 * Provides asynchronous access to Abiquo Config API.
 * 
 * @see API: <a href="http://community.abiquo.com/display/ABI20/API+Reference">
 *      http://community.abiquo.com/display/ABI20/API+Reference</a>
 * @see AdminApi
 * @author Ignasi Barrera
 * @author Francesc Montserrat
 */
@RequestFilters({ AbiquoAuthentication.class, AppendApiVersionToMediaType.class })
@Path("/config")
public interface ConfigAsyncApi {
   /*********************** License ***********************/

   /**
    * @see ConfigApi#listLicenses()
    */
   @Named("license:list")
   @EnterpriseEdition
   @GET
   @Path("/licenses")
   @Consumes(LicensesDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   ListenableFuture<LicensesDto> listLicenses();

   /**
    * @see ConfigApi#listLicenses(LicenseOptions)
    */
   @Named("license:list")
   @EnterpriseEdition
   @GET
   @Path("/licenses")
   @Consumes(LicensesDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   ListenableFuture<LicensesDto> listLicenses(LicenseOptions options);

   /**
    * @see ConfigApi#addLicense(LicenseDto)
    */
   @Named("license:add")
   @EnterpriseEdition
   @POST
   @Produces(LicenseDto.BASE_MEDIA_TYPE)
   @Consumes(LicenseDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   @Path("/licenses")
   ListenableFuture<LicenseDto> addLicense(@BinderParam(BindToXMLPayload.class) LicenseDto license);

   /**
    * @see ConfigApi#removeLicense(LicenseDto)
    */
   @Named("license:remove")
   @DELETE
   @EnterpriseEdition
   ListenableFuture<Void> removeLicense(@EndpointLink("edit") @BinderParam(BindToPath.class) LicenseDto license);

   /*********************** Privilege ***********************/

   /**
    * @see ConfigApi#listPrivileges()
    */
   @Named("privilege:list")
   @GET
   @Path("/privileges")
   @Consumes(PrivilegesDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   ListenableFuture<PrivilegesDto> listPrivileges();

   /**
    * @see ConfigApi#getPrivilege(Integer)
    */
   @Named("privilege:get")
   @GET
   @Path("/privileges/{privilege}")
   @Consumes(PrivilegeDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   @Fallback(NullOnNotFoundOr404.class)
   ListenableFuture<PrivilegeDto> getPrivilege(@PathParam("privilege") Integer privilegeId);

   /*********************** System Properties ***********************/

   /**
    * @see ConfigApi#listSystemProperties()
    */
   @Named("property:list")
   @GET
   @Path("/properties")
   @Consumes(SystemPropertiesDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   ListenableFuture<SystemPropertiesDto> listSystemProperties();

   /**
    * @see ConfigApi#listSystemProperties(PropertyOptions)
    */
   @Named("property:list")
   @GET
   @Path("/properties")
   @Consumes(SystemPropertiesDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   ListenableFuture<SystemPropertiesDto> listSystemProperties(PropertyOptions options);

   /**
    * @see ConfigApi#updateSystemProperty(VirtualDatacenterDto)
    */
   @Named("property:update")
   @PUT
   @Produces(SystemPropertyDto.BASE_MEDIA_TYPE)
   @Consumes(SystemPropertyDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   ListenableFuture<SystemPropertyDto> updateSystemProperty(
         @EndpointLink("edit") @BinderParam(BindToXMLPayloadAndPath.class) SystemPropertyDto property);

   /*********************** Category ***********************/

   /**
    * @see ConfigApi#listCategories()
    */
   @Named("category:list")
   @GET
   @Path("/categories")
   @Consumes(CategoriesDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   ListenableFuture<CategoriesDto> listCategories();

   /**
    * @see ConfigApi#getCategory(Integer)
    */
   @Named("category:get")
   @GET
   @Path("/categories/{category}")
   @Consumes(CategoryDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   @Fallback(NullOnNotFoundOr404.class)
   ListenableFuture<CategoryDto> getCategory(@PathParam("category") Integer categoryId);

   /**
    * @see ConfigApi#createCategory(CategoryDto)
    */
   @Named("category:create")
   @POST
   @Path("/categories")
   @Produces(CategoryDto.BASE_MEDIA_TYPE)
   @Consumes(CategoryDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   ListenableFuture<CategoryDto> createCategory(@BinderParam(BindToXMLPayload.class) CategoryDto category);

   /**
    * @see ConfigApi#updateCategory(CategoryDto)
    */
   @Named("category:update")
   @PUT
   @Produces(CategoryDto.BASE_MEDIA_TYPE)
   @Consumes(CategoryDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   ListenableFuture<CategoryDto> updateCategory(
         @EndpointLink("edit") @BinderParam(BindToXMLPayloadAndPath.class) CategoryDto category);

   /**
    * @see ConfigApi#deleteCategory(CategoryDto)
    */
   @Named("category:delete")
   @DELETE
   ListenableFuture<Void> deleteCategory(@EndpointLink("edit") @BinderParam(BindToPath.class) CategoryDto category);
}
