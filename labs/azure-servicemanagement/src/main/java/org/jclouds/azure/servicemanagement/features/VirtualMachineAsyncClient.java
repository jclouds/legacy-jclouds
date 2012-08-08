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

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.jclouds.azure.servicemanagement.config.AzureServiceManagementProperties;
import org.jclouds.azure.servicemanagement.domain.virtualmachine.Deployment;
import org.jclouds.azure.servicemanagement.domain.virtualmachine.Images;
import org.jclouds.azure.servicemanagement.domain.virtualmachine.OSImage;
import org.jclouds.azure.servicemanagement.domain.virtualmachine.PersistentVMRole;
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
 * @see VirtualMachineClient
 * @see <a href="http://msdn.microsoft.com/en-us/library/jj157206">api doc</a>
 */

@SkipEncoding('/')
//TODO We should rename this AzureStorageHeaders to AzureHeaders 
@Headers(keys = AzureStorageHeaders.VERSION, values = "2012-03-01")
// TODO I get a java.lang.IllegalArgumentException: The template variable, jclouds.azure.management.subscription-id, has no value with this
//@Path("/{"+AzureServiceManagementProperties.SUBSCRIPTION_ID+"}")
@Path("/")
public interface VirtualMachineAsyncClient {

	@GET
	@Path("{subscription-id}/services/hostedservices/{service-name}/deployments/{deployment-name}/roles/{role-name}")
	@Consumes(MediaType.APPLICATION_ATOM_XML)
	@JAXBResponseParser
	@ExceptionParser(ReturnNullOnNotFoundOr404.class)
	ListenableFuture<PersistentVMRole> getRole(@PathParam("subscription-id") String subscriptionId,
			@PathParam("service-name") String serviceName,
			@PathParam("deployment-name") String deploymentName,
			@PathParam("role-name") String roleName);
	
	
	@POST
	@Path("{subscription-id}/services/hostedservices/{service-name}/deployments/{deployment-name}/roles")
	@Produces(MediaType.APPLICATION_ATOM_XML)
	@Consumes(MediaType.TEXT_PLAIN)
	@ExceptionParser(ReturnNullOnNotFoundOr404.class)
	ListenableFuture<Void> addRole(@PathParam("subscription-id") String subscriptionId,
			@PathParam("service-name") String serviceName,
			@PathParam("deployment-name") String deploymentName,
			@BinderParam(BindToXMLPayload.class) PersistentVMRole role);
	
	@POST
	@Path("{subscription-id}/services/hostedservices/{service-name}/deployments")
	@Produces(MediaType.APPLICATION_ATOM_XML)
	@Consumes(MediaType.TEXT_PLAIN)
	@ExceptionParser(ReturnNullOnNotFoundOr404.class)
	ListenableFuture<Void> createVirtualMachineDeployment(@PathParam("subscription-id") String subscriptionId,
			@PathParam("service-name") String serviceName,
			@BinderParam(BindToXMLPayload.class) Deployment deployment);
	
	@GET
	@Path("{subscription-id}/services/images")
	@Consumes(MediaType.APPLICATION_ATOM_XML)
	@JAXBResponseParser
	@ExceptionParser(ReturnNullOnNotFoundOr404.class)
	ListenableFuture<Images> listOSImages(@PathParam("subscription-id") String subscriptionId);
	
//	@GET
//	@Path("{subscription-id}/services/images")
//	@Consumes(MediaType.TEXT_PLAIN)
//	@ExceptionParser(ReturnNullOnNotFoundOr404.class)
//	ListenableFuture<String> listOSImagesStr(@PathParam("subscription-id") String subscriptionId);
	

}
