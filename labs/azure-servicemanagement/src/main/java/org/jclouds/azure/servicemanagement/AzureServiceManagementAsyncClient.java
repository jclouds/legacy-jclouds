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
package org.jclouds.azure.servicemanagement;

import org.jclouds.azure.servicemanagement.features.HostedServiceAsyncClient;
import org.jclouds.azure.servicemanagement.features.VirtualMachineAsyncClient;
import org.jclouds.rest.annotations.Delegate;

/**
 * Provides asynchronous access to Azure Service Management via their REST API.
 * <p/>
 * 
 * @see AzureServiceManagementClient
 * @see <a href="TODO: insert URL of provider documentation" />
 * @author Gerald Pereira
 */

public interface AzureServiceManagementAsyncClient {

	/**
	 * Provides asynchronous access to Role features.
	 */
	@Delegate
	VirtualMachineAsyncClient getRoleClient();

	/**
	 * Provides asynchronous access to Hosted Service features.
	 */
	@Delegate
	HostedServiceAsyncClient getHostedServiceClient();
}
