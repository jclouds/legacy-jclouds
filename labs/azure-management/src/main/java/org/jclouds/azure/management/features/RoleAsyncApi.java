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
package org.jclouds.azure.management.features;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.jclouds.azure.management.binders.BindDeploymentParamsToXmlPayload;
import org.jclouds.azure.management.domain.DeploymentParams;
import org.jclouds.azure.management.domain.role.PersistentVMRole;
import org.jclouds.azure.management.functions.ParseRequestIdHeader;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.ExceptionParser;
import org.jclouds.rest.annotations.Headers;
import org.jclouds.rest.annotations.JAXBResponseParser;
import org.jclouds.rest.annotations.Payload;
import org.jclouds.rest.annotations.PayloadParam;
import org.jclouds.rest.annotations.ResponseParser;
import org.jclouds.rest.annotations.SkipEncoding;
import org.jclouds.rest.functions.ReturnNullOnNotFoundOr404;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * The Service Management API includes operations for managing the virtual
 * machines in your subscription.
 * 
 * @see <a href="http://msdn.microsoft.com/en-us/library/jj157206">docs</a>
 * @see RoleApi
 * @author Gerald Pereira, Adrian Cole
 */
@SkipEncoding('/')
@Headers(keys = "x-ms-version", values = "2012-03-01")
public interface RoleAsyncApi {

	 @GET
	 @Path("/services/hostedservices/{serviceName}/deployments/{deploymentName}/roles/{roleName}")
	 @Consumes(MediaType.APPLICATION_ATOM_XML)
	 @JAXBResponseParser
	 @ExceptionParser(ReturnNullOnNotFoundOr404.class)
	 ListenableFuture<PersistentVMRole> getRole(@PathParam("serviceName")
	 String serviceName,
	 @PathParam("deploymentName") String deploymentName,
	 @PathParam("roleName") String roleName);

	// This is a PaaS REST service ! => Delete the deployment instead
	// @DELETE
	// @Path("/services/hostedservices/{serviceName}/deployments/{deploymentName}/roles/{roleName}")
	// @Consumes(MediaType.APPLICATION_ATOM_XML)
	// @JAXBResponseParser
	// @ExceptionParser(ReturnNullOnNotFoundOr404.class)
	// ListenableFuture<Void> deleteRole(@PathParam("serviceName") String
	// serviceName,
	// @PathParam("deploymentName") String deploymentName,
	// @PathParam("roleName") String roleName);

	@POST
	// Warning : the url in the documentation is WRONG ! @see
	// http://social.msdn.microsoft.com/Forums/pl-PL/WAVirtualMachinesforWindows/thread/7ba2367b-e450-49e0-89e4-46c240e9d213
	@Path("/services/hostedservices/{serviceName}/deployments/{deploymentName}/roleInstances/{roleName}/Operations")
	@Consumes(MediaType.APPLICATION_ATOM_XML)
	@Produces(MediaType.APPLICATION_ATOM_XML)
	@ExceptionParser(ReturnNullOnNotFoundOr404.class)
	@ResponseParser(ParseRequestIdHeader.class)
	@Payload(value = "<RestartRoleOperation xmlns=\"http://schemas.microsoft.com/windowsazure\" xmlns:i=\"http://www.w3.org/2001/XMLSchema-instance\"><OperationType>RestartRoleOperation</OperationType></RestartRoleOperation>")
	ListenableFuture<String> restartRole(
			@PathParam("serviceName") String serviceName,
			@PathParam("deploymentName") String deploymentName,
			@PathParam("roleName") String roleName);

	// This is a PaaS REST service !
	// @POST
	// @Path("/services/hostedservices/{serviceName}/deployments/{deploymentName}/roles")
	// @Produces(MediaType.APPLICATION_ATOM_XML)
	// @Consumes(MediaType.TEXT_PLAIN)
	// @ExceptionParser(ReturnNullOnNotFoundOr404.class)
	// ListenableFuture<Void> addRole(@PathParam("serviceName") String
	// serviceName,
	// @PathParam("deploymentName") String deploymentName,
	// @BinderParam(BindToXMLPayload.class) PersistentVMRole role);

//	@Deprecated
//	@POST
//	@Path("/services/hostedservices/{serviceName}/deployments")
//	@Produces(MediaType.APPLICATION_ATOM_XML)
//	@Consumes(MediaType.TEXT_PLAIN)
//	@ExceptionParser(ReturnNullOnNotFoundOr404.class)
//	@ResponseParser(ParseRequestIdHeader.class)
//	ListenableFuture<String> createVirtualMachineDeployment(
//			@PathParam("serviceName") String serviceName,
//			@BinderParam(BindToXMLPayload.class) Deployment deployment);

	@POST
	@Path("/services/hostedservices/{serviceName}/deployments")
	@Produces(MediaType.APPLICATION_ATOM_XML)
	@Consumes(MediaType.APPLICATION_ATOM_XML)
	@ExceptionParser(ReturnNullOnNotFoundOr404.class)
	@ResponseParser(ParseRequestIdHeader.class)
	ListenableFuture<String> createDeployment(
			@PathParam("serviceName") String serviceName,
			@BinderParam(BindDeploymentParamsToXmlPayload.class) DeploymentParams deploymentParams);

	@POST
	@Path("/services/hostedservices/{serviceName}/deployments/{deploymentName}/roleInstances/{roleName}/Operations")
	@Consumes(MediaType.APPLICATION_ATOM_XML)
	@Produces(MediaType.APPLICATION_ATOM_XML)
	@ExceptionParser(ReturnNullOnNotFoundOr404.class)
	@ResponseParser(ParseRequestIdHeader.class)
	@Payload(value = "<CaptureRoleOperation xmlns=\"http://schemas.microsoft.com/windowsazure\" xmlns:i=\"http://www.w3.org/2001/XMLSchema-instance\"><OperationType>CaptureRoleOperation</OperationType><PostCaptureAction>Delete</PostCaptureAction><TargetImageLabel>{imageLabel}</TargetImageLabel><TargetImageName>{imageName}</TargetImageName></CaptureRoleOperation>")
	ListenableFuture<String> captureRole(
			@PathParam("serviceName") String serviceName,
			@PathParam("deploymentName") String deploymentName,
			@PathParam("roleName") String roleName,
			@PayloadParam("imageName") String imageName,
			@PayloadParam("imageLabel") String imageLabel);
	
	@POST
	@Path("/services/hostedservices/{serviceName}/deployments/{deploymentName}/roleInstances/{roleName}/Operations")
	@Consumes(MediaType.APPLICATION_ATOM_XML)
	@Produces(MediaType.APPLICATION_ATOM_XML)
	@ExceptionParser(ReturnNullOnNotFoundOr404.class)
	@ResponseParser(ParseRequestIdHeader.class)
	@Payload(value = "<ShutdownRoleOperation xmlns=\"http://schemas.microsoft.com/windowsazure\" xmlns:i=\"http://www.w3.org/2001/XMLSchema-instance\"><OperationType>ShutdownRoleOperation</OperationType></ShutdownRoleOperation>")
	ListenableFuture<String> shutdownRole(
			@PathParam("serviceName") String serviceName,
			@PathParam("deploymentName") String deploymentName,
			@PathParam("roleName") String roleName);
	
	@POST
	@Path("/services/hostedservices/{serviceName}/deployments/{deploymentName}/roleInstances/{roleName}/Operations")
	@Consumes(MediaType.APPLICATION_ATOM_XML)
	@Produces(MediaType.APPLICATION_ATOM_XML)
	@ExceptionParser(ReturnNullOnNotFoundOr404.class)
	@ResponseParser(ParseRequestIdHeader.class)
	@Payload(value = "<StartRoleOperation xmlns=\"http://schemas.microsoft.com/windowsazure\" xmlns:i=\"http://www.w3.org/2001/XMLSchema-instance\"><OperationType>StartRoleOperation</OperationType></StartRoleOperation>")
	ListenableFuture<String> startRole(
			@PathParam("serviceName") String serviceName,
			@PathParam("deploymentName") String deploymentName,
			@PathParam("roleName") String roleName);

}
