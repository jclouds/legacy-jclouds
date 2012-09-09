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
 * Unles required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either expres or implied.  See the License for the
 * specific language governing permisions and limitations
 * under the License.
 */
package org.jclouds.azure.management.features;

import org.jclouds.azure.management.AzureManagementApi;
import org.jclouds.azure.management.domain.DeploymentParams;
import org.jclouds.azure.management.domain.OSType;
import org.jclouds.azure.management.domain.RoleSize;
import org.jclouds.azure.management.internal.BaseAzureManagementApiExpectTest;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.testng.annotations.Test;

/**
 * @author Gérald Pereira
 */
@Test(groups = "unit", testName = "RoleApiExpectTest")
public class RoleApiExpectTest extends BaseAzureManagementApiExpectTest {

	private static final String DEPLOYMENT_NAME = "mydeployment";
	private static final String IMAGE_NAME = "myImageName";
	private static final String IMAGE_LABEL = "myImageLabel";

	HttpRequest add = HttpRequest
			.builder()
			.method("POST")
			.endpoint(
					"https://management.core.windows.net/" + subscriptionId
							+ "/services/hostedservices/" + DEPLOYMENT_NAME
							+ "/deployments")
			.addHeader("x-ms-version", "2012-03-01")
			.addHeader("Accept", "application/atom+xml")
			.payload(
					payloadFromResourceWithContentType("/deploymentparams.xml",
							"application/atom+xml")).build();

	public void testAddWhenResponseIs2xx() throws Exception {
		HttpResponse addResponse = HttpResponse.builder().statusCode(200)
				.addHeader("x-ms-request-id", "fakerequestid").build();

		AzureManagementApi apiWhenExist = requestSendsResponse(add, addResponse);
		DeploymentParams params = DeploymentParams
				.builder()
				.osType(OSType.LINUX)
				.name(DEPLOYMENT_NAME)
				.username("username")
				.password("testpwd")
				.size(RoleSize.MEDIUM)
				.sourceImageName(
						"OpenLogic__OpenLogic-CentOS-62-20120531-en-us-30GB.vhd")
				.storageAccount("portalvhds0g7xhnq2x7t21").build();

		apiWhenExist.getRoleApi().createDeployment(DEPLOYMENT_NAME, params);
	}

	HttpRequest start = HttpRequest
			.builder()
			.method("POST")
			.endpoint(
					"https://management.core.windows.net/" + subscriptionId
							+ "/services/hostedservices/" + DEPLOYMENT_NAME
							+ "/deployments/" + DEPLOYMENT_NAME
							+ "/roleInstances/" + DEPLOYMENT_NAME
							+ "/Operations")
			.addHeader("x-ms-version", "2012-03-01")
			.addHeader("Accept", "application/atom+xml")
			.payload(
					payloadFromResourceWithContentType("/startrolepayload.xml",
							"application/atom+xml")).build();

	public void testStartWhenResponseIs2xx() throws Exception {
		HttpResponse addResponse = HttpResponse.builder().statusCode(200)
				.addHeader("x-ms-request-id", "fakerequestid").build();

		AzureManagementApi apiWhenExist = requestSendsResponse(start,
				addResponse);

		apiWhenExist.getRoleApi().startRole(DEPLOYMENT_NAME, DEPLOYMENT_NAME,
				DEPLOYMENT_NAME);
	}

	HttpRequest shutdown = HttpRequest
			.builder()
			.method("POST")
			.endpoint(
					"https://management.core.windows.net/" + subscriptionId
							+ "/services/hostedservices/" + DEPLOYMENT_NAME
							+ "/deployments/" + DEPLOYMENT_NAME
							+ "/roleInstances/" + DEPLOYMENT_NAME
							+ "/Operations")
			.addHeader("x-ms-version", "2012-03-01")
			.addHeader("Accept", "application/atom+xml")
			.payload(
					payloadFromResourceWithContentType(
							"/shutdownrolepayload.xml", "application/atom+xml"))
			.build();

	public void testShutdownWhenResponseIs2xx() throws Exception {
		HttpResponse addResponse = HttpResponse.builder().statusCode(200)
				.addHeader("x-ms-request-id", "fakerequestid").build();

		AzureManagementApi apiWhenExist = requestSendsResponse(shutdown,
				addResponse);

		apiWhenExist.getRoleApi().shutdownRole(DEPLOYMENT_NAME,
				DEPLOYMENT_NAME, DEPLOYMENT_NAME);
	}

	HttpRequest restart = HttpRequest
			.builder()
			.method("POST")
			.endpoint(
					"https://management.core.windows.net/" + subscriptionId
							+ "/services/hostedservices/" + DEPLOYMENT_NAME
							+ "/deployments/" + DEPLOYMENT_NAME
							+ "/roleInstances/" + DEPLOYMENT_NAME
							+ "/Operations")
			.addHeader("x-ms-version", "2012-03-01")
			.addHeader("Accept", "application/atom+xml")
			.payload(
					payloadFromResourceWithContentType(
							"/restartrolepayload.xml", "application/atom+xml"))
			.build();

	public void testRestartWhenResponseIs2xx() throws Exception {
		HttpResponse addResponse = HttpResponse.builder().statusCode(200)
				.addHeader("x-ms-request-id", "fakerequestid").build();

		AzureManagementApi apiWhenExist = requestSendsResponse(restart,
				addResponse);

		apiWhenExist.getRoleApi().restartRole(DEPLOYMENT_NAME, DEPLOYMENT_NAME,
				DEPLOYMENT_NAME);
	}

	HttpRequest capture = HttpRequest
			.builder()
			.method("POST")
			.endpoint(
					"https://management.core.windows.net/" + subscriptionId
							+ "/services/hostedservices/" + DEPLOYMENT_NAME
							+ "/deployments/" + DEPLOYMENT_NAME
							+ "/roleInstances/" + DEPLOYMENT_NAME
							+ "/Operations")
			.addHeader("x-ms-version", "2012-03-01")
			.addHeader("Accept", "application/atom+xml")
			.payload(
					payloadFromResourceWithContentType(
							"/capturerolepayload.xml", "application/atom+xml"))
			.build();

	public void testCaptureWhenResponseIs2xx() throws Exception {
		HttpResponse addResponse = HttpResponse.builder().statusCode(200)
				.addHeader("x-ms-request-id", "fakerequestid").build();

		AzureManagementApi apiWhenExist = requestSendsResponse(capture,
				addResponse);

		apiWhenExist.getRoleApi().captureRole(DEPLOYMENT_NAME, DEPLOYMENT_NAME,
				DEPLOYMENT_NAME, IMAGE_NAME, IMAGE_LABEL);
	}
}
