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
package org.jclouds.azure.management.compute;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.azure.management.AzureManagementApi;
import org.jclouds.azure.management.AzureManagementAsyncApi;
import org.jclouds.azure.management.domain.Deployment;
import org.jclouds.azure.management.domain.OSImage;
import org.jclouds.azure.management.domain.RoleSize;
import org.jclouds.compute.ComputeServiceAdapter;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.logging.Logger;

/**
 * defines the connection between the {@link AzureApi} implementation and the
 * jclouds {@link ComputeService}
 * 
 */
@Singleton
public class AzureManagementComputeServiceAdapter implements
		ComputeServiceAdapter<Deployment, RoleSize, OSImage, String> {

	@Resource
	@Named(ComputeServiceConstants.COMPUTE_LOGGER)
	protected Logger logger = Logger.NULL;

	private final AzureManagementApi api;
	private final AzureManagementAsyncApi aapi;

	@Inject
	public AzureManagementComputeServiceAdapter(AzureManagementApi api,
			AzureManagementAsyncApi aapi) {
		this.api = checkNotNull(api, "api");
		this.aapi = checkNotNull(aapi, "aapi");
	}

	@Override
	public org.jclouds.compute.ComputeServiceAdapter.NodeAndInitialCredentials<Deployment> createNodeWithGroupEncodedIntoName(
			String group, String name, Template template) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Iterable<RoleSize> listHardwareProfiles() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Iterable<OSImage> listImages() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OSImage getImage(String id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Iterable<String> listLocations() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Deployment getNode(String id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void destroyNode(String id) {
		// TODO Auto-generated method stub

	}

	@Override
	public void rebootNode(String id) {
		// TODO Auto-generated method stub

	}

	@Override
	public void resumeNode(String id) {
		// TODO Auto-generated method stub

	}

	@Override
	public void suspendNode(String id) {
		// TODO Auto-generated method stub

	}

	@Override
	public Iterable<Deployment> listNodes() {
		// TODO Auto-generated method stub
		return null;
	}

}
