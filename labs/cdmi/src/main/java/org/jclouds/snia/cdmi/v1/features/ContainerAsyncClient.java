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

import org.jclouds.rest.annotations.ExceptionParser;
import org.jclouds.rest.annotations.Headers;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.SkipEncoding;
import org.jclouds.rest.functions.ReturnNullOnNotFoundOr404;
import org.jclouds.snia.cdmi.v1.ObjectTypes;
import org.jclouds.snia.cdmi.v1.domain.Container;
import org.jclouds.snia.cdmi.v1.filters.BasicAuthenticationAndTenantId;
import org.jclouds.snia.cdmi.v1.filters.StripExtraAcceptHeader;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * Container Object Resource Operations
 * 
 * @see ContainerClient
 * @author Kenneth Nagin
 * @see <a href="http://www.snia.org/cdmi">api doc</a>
 */
@SkipEncoding( { '/', '=' })
@RequestFilters( { BasicAuthenticationAndTenantId.class, StripExtraAcceptHeader.class })
@Headers(keys="X-CDMI-Specification-Version", values = "{jclouds.api-version}")
public interface ContainerAsyncClient {

   /**
    * @see ContainerClient#listContainers()
    */
   @GET
   @Consumes( { ObjectTypes.CONTAINER, MediaType.APPLICATION_JSON })
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   @Path("/{containerName}/")
   ListenableFuture<Container> getContainer(@PathParam("containerName") String containerName);
   
   /**
    * @see ContainerClient#createContainer
    */
   @PUT
   @Consumes( { ObjectTypes.CONTAINER, MediaType.APPLICATION_JSON })
   @Produces( { ObjectTypes.CONTAINER})
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)   
   @Path("/{containerName}/")
   ListenableFuture<Container> createContainer(@PathParam("containerName") String containerName);

   /**
    * @see ContainerClient#createContainer()
    */
   @DELETE
//   @Consumes( { ObjectTypes.CONTAINER, MediaType.APPLICATION_JSON })
   @Consumes( MediaType.APPLICATION_JSON )
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   @Path("/{containerName}/")
   ListenableFuture<Void> deleteContainer(@PathParam("containerName") String containerName);

}
