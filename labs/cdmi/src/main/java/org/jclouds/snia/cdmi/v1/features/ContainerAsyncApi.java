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
package org.jclouds.snia.cdmi.v1.features;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.Fallbacks.FalseOnNotFoundOr404;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.Headers;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.SkipEncoding;
import org.jclouds.snia.cdmi.v1.ObjectTypes;
import org.jclouds.snia.cdmi.v1.binders.BindQueryParmsToSuffix;
import org.jclouds.snia.cdmi.v1.domain.CDMIObjectCapability;
import org.jclouds.snia.cdmi.v1.domain.Container;
import org.jclouds.snia.cdmi.v1.filters.AuthenticationFilter;
import org.jclouds.snia.cdmi.v1.filters.StripExtraAcceptHeader;
import org.jclouds.snia.cdmi.v1.options.CreateContainerOptions;
import org.jclouds.snia.cdmi.v1.queryparams.ContainerQueryParams;
import com.google.common.util.concurrent.ListenableFuture;
import javax.inject.Named;

/**
 * CDMI Container Object Resource Operations
 * 
 * @see ContainerApi
 * @author Kenneth Nagin
 * @see <a href="http://www.snia.org/cdmi">api doc</a>
 */
@SkipEncoding({ '/', '=' })
@RequestFilters({ AuthenticationFilter.class, StripExtraAcceptHeader.class })
@Headers(keys = "X-CDMI-Specification-Version", values = "{jclouds.api-version}")
public interface ContainerAsyncApi {

   /**
    * get CDMI root Container
    * 
    * @return ListenableFuture<Container>
    * @see ContainerApi#get()
    */
   @Named("GetContainer")
   @GET
   @Consumes({ ObjectTypes.CONTAINER, MediaType.APPLICATION_JSON })
   @Fallback(NullOnNotFoundOr404.class)
   @Path("")
   ListenableFuture<Container> get();

   /**
    * get CDMI Container *
    * 
    * @param containerName
    *           containerName must end with a forward slash, /.
    * @return ListenableFuture<Container>
    * @see ContainerApi#get(String containerName)
    */
   @Named("GetContainer")
   @GET
   @Consumes({ ObjectTypes.CONTAINER, MediaType.APPLICATION_JSON })
   @Fallback(NullOnNotFoundOr404.class)
   @Path("{containerName}/")
   ListenableFuture<Container> get(@PathParam("containerName") String containerName);

   /**
    * get CDMI Container
    * 
    * @param containerName
    * @param queryParams
    *           enables getting only certain fields, metadata, children range
    * @return ListenableFuture<Container>
    * @see ContainerApi#get(String containerName, ContainerQueryParams
    *      queryParams)
    */
   @Named("GetContainer")
   @GET
   @Consumes({ ObjectTypes.CONTAINER, MediaType.APPLICATION_JSON })
   @Fallback(NullOnNotFoundOr404.class)
   @Path("{containerName}/")
   ListenableFuture<Container> get(@PathParam("containerName") String containerName,
            @BinderParam(BindQueryParmsToSuffix.class) ContainerQueryParams queryParams);

   /**
    * Create CDMI Container
    * 
    * @param containerName
    *           containerName must end with a forward slash, /.
    * @return ListenableFuture<Container>
    * @see ContainerApi#create(String containerName)
    */
   @Named("PutContainer")
   @PUT
   @Consumes({ ObjectTypes.CONTAINER, MediaType.APPLICATION_JSON })
   @Produces({ ObjectTypes.CONTAINER })
   @Fallback(NullOnNotFoundOr404.class)
   @Path("{containerName}/")
   ListenableFuture<Container> create(@PathParam("containerName") String containerName);

   /**
    * Create CDMI Container
    * 
    * @param containerName
    * @param options
    *           enables adding metadata
    * @return ListenableFuture<Container>
    * @see ContainerApi#create(String containerName, CreateContainerOptions
    *      options)
    */
   @Named("PutContainer")
   @PUT
   @Consumes({ ObjectTypes.CONTAINER, MediaType.APPLICATION_JSON })
   @Produces({ ObjectTypes.CONTAINER })
   @Fallback(NullOnNotFoundOr404.class)
   @Path("{containerName}/")
   ListenableFuture<Container> create(@PathParam("containerName") String containerName,
            CreateContainerOptions... options);

   /**
    * Delete Container
    * 
    * @param containerName
    * @return ListenableFuture<Void>
    * @see ContainerApi#delete(String containerName)
    */
   @Named("DeleteContainer")
   @DELETE
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(NullOnNotFoundOr404.class)
   @Path("{containerName}/")
   ListenableFuture<Void> delete(@PathParam("containerName") String containerName);

   /**
    * Check whether Container exists
    * 
    * @param containerName
    * @return ListenableFuture<Boolean>
    * @see ContainerApi#containerExists(String containerName)
    */
   @Named("GetContainer")
   @GET
   @Consumes({ ObjectTypes.CONTAINER, MediaType.APPLICATION_JSON })
   @Path("{container}/")
   @Fallback(FalseOnNotFoundOr404.class)
   ListenableFuture<Boolean> containerExists(@PathParam("container") String container);

   /**
    * Get CDMI Capabilities
    * 
    * @return ListenableFuture<CDMIObjectCapability>
    * @see getCapabilites()
    */
   @Named("GetCapabilites")
   @GET
   @Consumes({ ObjectTypes.CAPABILITY, MediaType.APPLICATION_JSON })
   @Path("cdmi_capabilities/")
   @Fallback(NullOnNotFoundOr404.class)
   ListenableFuture<CDMIObjectCapability> getCapabilites();

}
