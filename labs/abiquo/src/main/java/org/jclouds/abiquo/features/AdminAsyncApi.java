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
import javax.ws.rs.QueryParam;

import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.abiquo.binders.BindToPath;
import org.jclouds.abiquo.binders.BindToXMLPayloadAndPath;
import org.jclouds.abiquo.domain.enterprise.Enterprise;
import org.jclouds.abiquo.functions.enterprise.ParseEnterpriseId;
import org.jclouds.abiquo.http.filters.AbiquoAuthentication;
import org.jclouds.abiquo.http.filters.AppendApiVersionToMediaType;
import org.jclouds.abiquo.rest.annotations.EndpointLink;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.JAXBResponseParser;
import org.jclouds.rest.annotations.ParamParser;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.binders.BindToXMLPayload;

import com.abiquo.server.core.enterprise.EnterpriseDto;
import com.abiquo.server.core.enterprise.PrivilegesDto;
import com.abiquo.server.core.enterprise.RoleDto;
import com.abiquo.server.core.enterprise.RolesDto;
import com.abiquo.server.core.enterprise.UserDto;
import com.google.common.util.concurrent.ListenableFuture;

/**
 * Provides asynchronous access to Abiquo Admin API.
 * 
 * @see API: <a href="http://community.abiquo.com/display/ABI20/API+Reference">
 *      http://community.abiquo.com/display/ABI20/API+Reference</a>
 * @see AdminApi
 * @author Ignasi Barrera
 * @author Francesc Montserrat
 */
@RequestFilters({ AbiquoAuthentication.class, AppendApiVersionToMediaType.class })
public interface AdminAsyncApi {
   /*********************** Login ***********************/

   /**
    * @see AdminApi#getCurrentUser()
    */
   @Named("user:get")
   @GET
   @Path("/login")
   @Consumes(UserDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   ListenableFuture<UserDto> getCurrentUser();

   /*********************** Role ***********************/

   /**
    * @see AdminApi#listRoles()
    */
   @Named("role:list")
   @GET
   @Path("/admin/roles")
   @Consumes(RolesDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   ListenableFuture<RolesDto> listRoles();

   /**
    * @see AdminApi#listRoles(Enterprise enterprise)
    */
   @Named("role:list")
   @GET
   @Path("/admin/roles")
   @Consumes(RolesDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   ListenableFuture<RolesDto> listRoles(
         @QueryParam("identerprise") @ParamParser(ParseEnterpriseId.class) final EnterpriseDto enterprise);

   /**
    * @see AdminApi#getRole(UserDto)
    */
   @Named("role:get")
   @GET
   @Consumes(RoleDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   @Fallback(NullOnNotFoundOr404.class)
   ListenableFuture<RoleDto> getRole(@EndpointLink("role") @BinderParam(BindToPath.class) UserDto user);

   /**
    * @see AdminApi#getRole(Integer)
    */
   @Named("role:get")
   @GET
   @Path("/admin/roles/{role}")
   @Consumes(RoleDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   @Fallback(NullOnNotFoundOr404.class)
   ListenableFuture<RoleDto> getRole(@PathParam("role") Integer roleId);

   /**
    * @see AdminApi#deleteRole(RoleDto)
    */
   @Named("role:delete")
   @DELETE
   ListenableFuture<Void> deleteRole(@EndpointLink("edit") @BinderParam(BindToPath.class) RoleDto role);

   /**
    * @see AdminApi#updateRole(RoleDto)
    */
   @Named("role:update")
   @PUT
   @Produces(RoleDto.BASE_MEDIA_TYPE)
   @Consumes(RoleDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   ListenableFuture<RoleDto> updateRole(@EndpointLink("edit") @BinderParam(BindToXMLPayloadAndPath.class) RoleDto role);

   /**
    * @see AdminApi#createRole(RoleDto)
    */
   @Named("role:create")
   @POST
   @Path("/admin/roles")
   @Produces(RoleDto.BASE_MEDIA_TYPE)
   @Consumes(RoleDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   ListenableFuture<RoleDto> createRole(@BinderParam(BindToXMLPayload.class) RoleDto role);

   /**
    * @see AdminApi#listPrivileges(RoleDto)
    */
   @Named("privilege:list")
   @GET
   @Consumes(PrivilegesDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   ListenableFuture<PrivilegesDto> listPrivileges(
         @EndpointLink("privileges") @BinderParam(BindToPath.class) RoleDto role);
}
