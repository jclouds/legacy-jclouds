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
package org.jclouds.vcloud.director.v1_5.features.admin;

import java.net.URI;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.jclouds.rest.annotations.Delegate;
import org.jclouds.rest.annotations.EndpointParam;
import org.jclouds.rest.annotations.ExceptionParser;
import org.jclouds.rest.annotations.JAXBResponseParser;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.functions.ReturnNullOnNotFoundOr404;
import org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType;
import org.jclouds.vcloud.director.v1_5.domain.AdminVdc;
import org.jclouds.vcloud.director.v1_5.domain.Task;
import org.jclouds.vcloud.director.v1_5.features.MetadataAsyncApi;
import org.jclouds.vcloud.director.v1_5.features.VdcAsyncApi;
import org.jclouds.vcloud.director.v1_5.filters.AddVCloudAuthorizationAndCookieToRequest;
import org.jclouds.vcloud.director.v1_5.functions.href.VdcURNToAdminHref;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * @see AdminVdcApi
 * @author danikov, Adrian Cole
 */
@RequestFilters(AddVCloudAuthorizationAndCookieToRequest.class)
public interface AdminVdcAsyncApi extends VdcAsyncApi {
   /**
    * @see AdminVdcApi#get(String)
    */
   @Override
   @GET
   @Consumes
   @JAXBResponseParser
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<AdminVdc> get(@EndpointParam(parser = VdcURNToAdminHref.class) String vdcUrn);

   /**
    * @see AdminVdcApi#edit(String, AdminVdc)
    */
   @PUT
   @Consumes
   @Produces(VCloudDirectorMediaType.ADMIN_VDC)
   @JAXBResponseParser
   ListenableFuture<Task> edit(@EndpointParam(parser = VdcURNToAdminHref.class) String vdcUrn, AdminVdc vdc);

   /**
    * @see AdminVdcApi#remove(String)
    */
   @DELETE
   @Consumes
   @JAXBResponseParser
   ListenableFuture<Task> remove(@EndpointParam(parser = VdcURNToAdminHref.class) String vdcUrn);

   /**
    * @see AdminVdcApi#enable(String)
    */
   @POST
   @Consumes
   @Path("/action/enable")
   @JAXBResponseParser
   ListenableFuture<Void> enable(@EndpointParam(parser = VdcURNToAdminHref.class) String vdcUrn);

   /**
    * @see AdminVdcApi#disable(String)
    */
   @POST
   @Consumes
   @Path("/action/disable")
   @JAXBResponseParser
   ListenableFuture<Void> disable(@EndpointParam(parser = VdcURNToAdminHref.class) String vdcUrn);

   /**
    * @see AdminVdcApi#get(URI)
    */
   @Override
   @GET
   @Consumes
   @JAXBResponseParser
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<AdminVdc> get(@EndpointParam URI vdcAdminHref);

   /**
    * @see AdminVdcApi#edit(URI, AdminVdc)
    */
   @PUT
   @Consumes
   @Produces(VCloudDirectorMediaType.ADMIN_VDC)
   @JAXBResponseParser
   ListenableFuture<Task> edit(@EndpointParam URI vdcAdminHref, AdminVdc vdc);

   /**
    * @see AdminVdcApi#remove(URI)
    */
   @DELETE
   @Consumes
   @JAXBResponseParser
   ListenableFuture<Task> remove(@EndpointParam URI vdcAdminHref);

   /**
    * @see AdminVdcApi#enable(URI)
    */
   @POST
   @Consumes
   @Path("/action/enable")
   @JAXBResponseParser
   ListenableFuture<Void> enable(@EndpointParam URI vdcAdminHref);

   /**
    * @see AdminVdcApi#disable(URI)
    */
   @POST
   @Consumes
   @Path("/action/disable")
   @JAXBResponseParser
   ListenableFuture<Void> disable(@EndpointParam URI vdcAdminHref);

   /**
    * @return asynchronous access to {@link Writeable} features
    */
   @Override
   @Delegate
   MetadataAsyncApi.Writeable getMetadataApi(@EndpointParam(parser = VdcURNToAdminHref.class) String vdcUrn);

   @Override
   @Delegate
   MetadataAsyncApi.Writeable getMetadataApi(@EndpointParam URI vdcAdminHref);

}
