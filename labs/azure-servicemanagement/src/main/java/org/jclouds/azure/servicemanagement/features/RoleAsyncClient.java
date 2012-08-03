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
package org.jclouds.azure.servicemanagement.features;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.jclouds.azure.servicemanagement.domain.role.Role;
import org.jclouds.azure.storage.reference.AzureStorageHeaders;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.ExceptionParser;
import org.jclouds.rest.annotations.Headers;
import org.jclouds.rest.annotations.JAXBResponseParser;
import org.jclouds.rest.annotations.SkipEncoding;
import org.jclouds.rest.binders.BindToXMLPayload;
import org.jclouds.rest.functions.ReturnNullOnNotFoundOr404;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * Provides asynchronous access to Service Management Role via their REST API.
 * <p/>
 * 
 * @author Gerald Pereira
 * @see RoleClient
 * @see <a href="http://msdn.microsoft.com/en-us/library/jj157206">api doc</a>
 */

@SkipEncoding('/')
//TODO We should rename this AzureStorageHeaders to AzureHeaders 
@Headers(keys = AzureStorageHeaders.VERSION, values = "2012-03-01")
@Path("/")
public interface RoleAsyncClient {

	@GET
	@Path("{subscription-id}/services/hostedservices/{service-name}/deployments/{deployment-name}/roles/{role-name}")
	@Consumes(MediaType.APPLICATION_ATOM_XML)
	@JAXBResponseParser
	@ExceptionParser(ReturnNullOnNotFoundOr404.class)
	ListenableFuture<Role> getRole(@PathParam("subscription-id") String subscriptionId,
			@PathParam("service-name") String serviceName,
			@PathParam("deployment-name") String deploymentName,
			@PathParam("role-name") String roleName);
	
	
	@POST
	@Path("{subscription-id}/services/hostedservices/{service-name}/deployments/{deployment-name}/roles")
	@Produces(MediaType.APPLICATION_ATOM_XML)
	@Consumes(MediaType.TEXT_PLAIN)
	@ExceptionParser(ReturnNullOnNotFoundOr404.class)
	ListenableFuture<String> addRole(@PathParam("subscription-id") String subscriptionId,
			@PathParam("service-name") String serviceName,
			@PathParam("deployment-name") String deploymentName,
			@BinderParam(BindToXMLPayload.class) Role role);
	
	
}
