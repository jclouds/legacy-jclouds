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

import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.Encoded;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.MatrixParam;

import javax.ws.rs.core.MediaType;

import org.jclouds.blobstore.binders.BindMapToHeadersWithPrefix;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.ExceptionParser;
import org.jclouds.rest.annotations.Headers;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.SkipEncoding;
import org.jclouds.rest.functions.ReturnNullOnNotFoundOr404;
import org.jclouds.snia.cdmi.v1.ObjectTypes;
import org.jclouds.snia.cdmi.v1.binders.BindQueryParmsToSuffix;
import org.jclouds.snia.cdmi.v1.domain.Container;
import org.jclouds.snia.cdmi.v1.filters.BasicAuthenticationAndTenantId;
import org.jclouds.snia.cdmi.v1.filters.StripExtraAcceptHeader;
import org.jclouds.snia.cdmi.v1.options.CreateContainerOptions;
import org.jclouds.snia.cdmi.v1.options.GetContainerOptions;
import org.jclouds.rest.annotations.QueryParams;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * CDMI Container Object Resource Operations
 * 
 * @see ContainerApi
 * @author Kenneth Nagin
 * @see <a href="http://www.snia.org/cdmi">api doc</a>
 */
@SkipEncoding({ '/', '=' })
@RequestFilters({ BasicAuthenticationAndTenantId.class,
		StripExtraAcceptHeader.class })
@Headers(keys = "X-CDMI-Specification-Version", values = "{jclouds.api-version}")
public interface ContainerAsyncApi {

	/**
	 * get CDMI Container
	 * 
	 * @param containerName
	 */
	@GET
	@Consumes({ ObjectTypes.CONTAINER, MediaType.APPLICATION_JSON })
	@ExceptionParser(ReturnNullOnNotFoundOr404.class)
	@Path("/{containerName}/")
	ListenableFuture<Container> getContainer(
			@PathParam("containerName") String containerName);

	/**
	 * get CDMI Container
	 * 
	 * @param parentURI
	 * @param containerName
	 */
	@GET
	@Consumes({ ObjectTypes.CONTAINER, MediaType.APPLICATION_JSON })
	@ExceptionParser(ReturnNullOnNotFoundOr404.class)
	@Path("/{parentURI}{containerName}/")
	ListenableFuture<Container> getContainer(
			@PathParam("parentURI") String parentURI,
			@PathParam("containerName") String containerName);

	/**
	 * get CDMI Container
	 * 
	 * @param containerName
	 * @param options
	 *            enables getting only certain fields, metadata, children range
	 * @see GetContainerOptions
	 */
	@GET
	@Consumes({ ObjectTypes.CONTAINER, MediaType.APPLICATION_JSON })
	@ExceptionParser(ReturnNullOnNotFoundOr404.class)
	@Path("/{containerName}/")
	ListenableFuture<Container> getContainer(
			@PathParam("containerName") String containerName,
			GetContainerOptions... options);

	/**
	 * get CDMI Container
	 * 
	 * @param parentURI
	 * @param containerName
	 * @param options
	 *            enables getting only certain fields, metadata, children range
	 * @see GetContainerOptions
	 */
	@GET
	@Consumes({ ObjectTypes.CONTAINER, MediaType.APPLICATION_JSON })
	@ExceptionParser(ReturnNullOnNotFoundOr404.class)
	@Path("/{parentURI}{containerName}/")
	ListenableFuture<Container> getContainer(
			@PathParam("parentURI") String parentURI,
			@PathParam("containerName") String containerName,
			GetContainerOptions... options);

	/**
	 * Create CDMI Container
	 * 
	 * @param containerName
	 */
	@PUT
	@Consumes({ ObjectTypes.CONTAINER, MediaType.APPLICATION_JSON })
	@Produces({ ObjectTypes.CONTAINER })
	@ExceptionParser(ReturnNullOnNotFoundOr404.class)
	@Path("/{containerName}/")
	ListenableFuture<Container> createContainer(
			@PathParam("containerName") String containerName);

	/**
	 * Create CDMI Container
	 * 
	 * @param parentContainerURI
	 * @param containerName
	 */
	@PUT
	@Consumes({ ObjectTypes.CONTAINER, MediaType.APPLICATION_JSON })
	@Produces({ ObjectTypes.CONTAINER })
	@ExceptionParser(ReturnNullOnNotFoundOr404.class)
	@Path("/{parentContainerURI}{containerName}/")
	ListenableFuture<Container> createContainer(
			@PathParam("parentContainerURI") String parentContainerURI,
			@PathParam("containerName") String containerName);

	/**
	 * Create CDMI Container
	 * 
	 * @param containerName
	 * @param options
	 *            enables adding metadata
	 * @see CreateContainerOptions
	 */
	@PUT
	@Consumes({ ObjectTypes.CONTAINER, MediaType.APPLICATION_JSON })
	@Produces({ ObjectTypes.CONTAINER })
	@ExceptionParser(ReturnNullOnNotFoundOr404.class)
	@Path("/{containerName}/")
	ListenableFuture<Container> createContainer(
			@PathParam("containerName") String containerName,
			CreateContainerOptions... options);

	/**
	 * Create CDMI Container
	 * 
	 * @param parentContainerURI
	 * @param containerName
	 * @param options
	 *            enables adding metadata
	 * @see CreateContainerOptions
	 */
	@PUT
	@Consumes({ ObjectTypes.CONTAINER, MediaType.APPLICATION_JSON })
	@Produces({ ObjectTypes.CONTAINER })
	@ExceptionParser(ReturnNullOnNotFoundOr404.class)
	@Path("/{parentContainerURI}{containerName}/")
	ListenableFuture<Container> createContainer(
			@PathParam("parentContainerURI") String parentContainerURI,
			@PathParam("containerName") String containerName,
			CreateContainerOptions... options);

	/**
	 * Delete Container
	 * 
	 * @param containerName
	 */
	@DELETE
	@Consumes(MediaType.APPLICATION_JSON)
	@ExceptionParser(ReturnNullOnNotFoundOr404.class)
	@Path("/{containerName}/")
	ListenableFuture<Void> deleteContainer(
			@PathParam("containerName") String containerName);

	/**
	 * Delete Container
	 * 
	 * @param parentContainerURI
	 * @param containerName
	 */
	@DELETE
	@Consumes(MediaType.APPLICATION_JSON)
	@ExceptionParser(ReturnNullOnNotFoundOr404.class)
	@Path("/{parentContainerURI}{containerName}/")
	ListenableFuture<Void> deleteContainer(
			@PathParam("parentContainerURI") String parentContainerURI,
			@PathParam("containerName") String containerName);

	/**
	 * Experimenting with this to see if can solve ?,;,: encoding binding
	 */
	// @GET
	// @Consumes( { ObjectTypes.CONTAINER, MediaType.APPLICATION_JSON })
	// @ExceptionParser(ReturnNullOnNotFoundOr404.class)
	// @Path("/{containerName}/")
	// ListenableFuture<Container> getContainer(@PathParam("containerName")
	// String containerName, @BinderParam(BindQueryParmsToSuffix.class) String
	// queryParams);

	/**
	 * Experimenting with String queryParmas. This is superceeded get with
	 * options
	 */
	// @GET
	// @Consumes({ ObjectTypes.CONTAINER, MediaType.APPLICATION_JSON })
	// @ExceptionParser(ReturnNullOnNotFoundOr404.class)
	// @Path("/{containerName}/?{queryParams}")
	// ListenableFuture<Container> getContainer(
	// @PathParam("containerName") String containerName,
	// @PathParam("queryParams") String queryParams);

	/**
	 * @MatrixParam Experiment for less ackward CDMI definition
	 */
	// @GET
	// @Consumes({ ObjectTypes.CONTAINER, MediaType.APPLICATION_JSON })
	// @ExceptionParser(ReturnNullOnNotFoundOr404.class)
	// @Path("/{containerName}/")
	// ListenableFuture<Container> getContainer(
	// @PathParam("containerName") String containerName,
	// @MatrixParam("fields") String queryParams,
	// @MatrixParam("children") String queryParams2);

}
