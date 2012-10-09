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
import javax.ws.rs.HeaderParam;
import javax.ws.rs.core.MediaType;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.ExceptionParser;
import org.jclouds.rest.annotations.Headers;
import org.jclouds.rest.annotations.PayloadParam;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.ResponseParser;
import org.jclouds.rest.annotations.SkipEncoding;
import org.jclouds.rest.functions.ReturnNullOnNotFoundOr404;
import org.jclouds.snia.cdmi.v1.binders.BindQueryParmsToSuffix;
import org.jclouds.snia.cdmi.v1.domain.DataObject;
import org.jclouds.snia.cdmi.v1.filters.AuthenticationFilterSwitch;
import org.jclouds.snia.cdmi.v1.filters.BasicAuthenticationAndTenantId;
import org.jclouds.snia.cdmi.v1.filters.StripExtraAcceptHeader;
import org.jclouds.snia.cdmi.v1.queryparams.DataObjectQueryParams;
import org.jclouds.snia.cdmi.v1.functions.ParseObjectFromHeadersAndHttpContent;
import com.google.common.util.concurrent.ListenableFuture;
import org.jclouds.rest.annotations.Payload;

/**
 * Non CDMI Content Type Data Object Resource Operations
 * 
 * @see DataNonCDMIContentTypeApi
 * @see DataAsyncApi
 * @author Kenneth Nagin
 * @see <a href="http://www.snia.org/cdmi">api doc</a>
 */
@SkipEncoding({ '/', '=' })
@RequestFilters({ AuthenticationFilterSwitch.class,
		StripExtraAcceptHeader.class})
public interface DataNonCDMIContentTypeAsyncApi {
	/**
	 * @see DataNonCDMIContentTypeApi#getDataObjectValue(String containerName,
	 *      String dataObjectName)
	 */
	@GET
	@Consumes(MediaType.MEDIA_TYPE_WILDCARD)
	@ResponseParser(ParseObjectFromHeadersAndHttpContent.class)
	@ExceptionParser(ReturnNullOnNotFoundOr404.class)
	@Path("/{containerName}{dataObjectName}")
	ListenableFuture<org.jclouds.io.Payload> getDataObjectValue(
			@PathParam("containerName") String containerName,
			@PathParam("dataObjectName") String dataObjectName);

	/**
	 * @see DataNonCDMIContentTypeApi#getDataObjectValue(String containerName,
	 *      String dataObjectName, String range )
	 */

	@GET
	@Consumes(MediaType.MEDIA_TYPE_WILDCARD)
	@ResponseParser(ParseObjectFromHeadersAndHttpContent.class)
	@ExceptionParser(ReturnNullOnNotFoundOr404.class)
	@Path("/{containerName}{dataObjectName}")
	ListenableFuture<org.jclouds.io.Payload> getDataObjectValue(
			@PathParam("containerName") String containerName,
			@PathParam("dataObjectName") String dataObjectName,
			@HeaderParam("Range") String range);

	/**
	 * @see DataNonCDMIContentTypeApi#getDataObject(String containerName, String
	 *      dataObjectName, DataObjectQueryParams queryParams )
	 */
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	//@Consumes(MediaType.TEXT_PLAIN)
	@ExceptionParser(ReturnNullOnNotFoundOr404.class)
	@Path("/{containerName}{dataObjectName}")
	ListenableFuture<DataObject> getDataObject(
			@PathParam("containerName") String containerName,
			@PathParam("dataObjectName") String dataObjectName,
			@BinderParam(BindQueryParmsToSuffix.class) DataObjectQueryParams queryParams);

	/**
	 * @see DataNonCDMIContentTypeApi#createDataObject(String containerName,
	 *      String dataObjectName, org.jclouds.io.Payload payload )
	 */
	@PUT
	@Consumes(MediaType.MEDIA_TYPE_WILDCARD)
	@ExceptionParser(ReturnNullOnNotFoundOr404.class)
	@Path("/{containerName}{dataObjectName}")
	ListenableFuture<Void> createDataObject(
			@PathParam("containerName") String containerName,
			@PathParam("dataObjectName") String dataObjectName,
			org.jclouds.io.Payload payload);

	/**
	 * @see DataNonCDMIContentTypeApi#createDataObjectPartial(String
	 *      containerName, String dataObjectName, org.jclouds.io.Payload payload
	 *      )
	 */
	@PUT
	@Consumes(MediaType.MEDIA_TYPE_WILDCARD)
	@ExceptionParser(ReturnNullOnNotFoundOr404.class)
	@Path("/{containerName}{dataObjectName}")
	@Headers(keys = "X-CDMI-Partial", values = "true")
	ListenableFuture<Void> createDataObjectPartial(
			@PathParam("containerName") String containerName,
			@PathParam("dataObjectName") String dataObjectName,
			org.jclouds.io.Payload payload);

	/**
	 * @see DataNonCDMIContentTypeApi#createDataObject(String containerName,
	 *      String dataObjectName, String input )
	 */
	@PUT
	@Consumes(MediaType.MEDIA_TYPE_WILDCARD)
	@Produces(MediaType.TEXT_PLAIN)
	@ExceptionParser(ReturnNullOnNotFoundOr404.class)
	@Path("/{containerName}{dataObjectName}")
	@Payload("{input}")
	ListenableFuture<Void> createDataObject(
			@PathParam("containerName") String containerName,
			@PathParam("dataObjectName") String dataObjectName,
			@PayloadParam("input") String input);

	/**
	 * @see DataNonCDMIContentTypeApi#deleteDataObject(String containerName,
	 *      String dataObjectName)
	 */
	@DELETE
	@Consumes(MediaType.MEDIA_TYPE_WILDCARD)
	@ExceptionParser(ReturnNullOnNotFoundOr404.class)
	@Path("/{containerName}{dataObjectName}")
	ListenableFuture<Void> deleteDataObject(
			@PathParam("containerName") String containerName,
			@PathParam("dataObjectName") String dataObjectName);

}
