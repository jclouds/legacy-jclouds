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
import java.util.concurrent.TimeUnit;

import org.jclouds.azure.servicemanagement.domain.virtualmachine.Deployment;
import org.jclouds.azure.servicemanagement.domain.virtualmachine.Images;
import org.jclouds.azure.servicemanagement.domain.virtualmachine.OSImage;
import org.jclouds.azure.servicemanagement.domain.virtualmachine.PersistentVMRole;
import org.jclouds.concurrent.Timeout;

/**
 * Provides synchronous access to Role.
 * <p/>
 * 
 * @author Gerald Pereira
 * @see VirtualMachineAsyncClient
 * @see <a href="http://msdn.microsoft.com/en-us/library/jj157206">api doc</a>
 */
@Timeout(duration = 30, timeUnit = TimeUnit.SECONDS)
public interface VirtualMachineClient {

	/**
	 * http://msdn.microsoft.com/en-us/library/jj157193
	 * 
	 * @param subscriptionId
	 *            your subscription ID
	 * @param serviceName
	 *            the name of your service
	 * @param deploymentName
	 *            the name of your deployment
	 * @param roleName
	 *            the name of the virtual machine to retrieve
	 */
	PersistentVMRole getRole(String subscriptionId, String serviceName,
			String deploymentName, String roleName);

	/**
	 * http://msdn.microsoft.com/en-us/library/jj157186
	 * 
	 * @param subscriptionId
	 *            your subscription ID
	 * @param serviceName
	 *            the name of your service
	 * @param deploymentName
	 *            the name of your deployment
	 * @param role
	 *            the virtual machine to add
	 */
	void addRole(String subscriptionId, String serviceName,
			String deploymentName, PersistentVMRole role);

	/**
	 * http://msdn.microsoft.com/en-us/library/jj157194
	 * 
	 * @param subscriptionId
	 *            your subscription ID
	 * @param serviceName
	 *            the name of your service
	 * @param deployment
	 *            the deployment to create
	 */
	void createVirtualMachineDeployment(String subscriptionId,
			String serviceName, Deployment deployment);

	/**
	 * http://msdn.microsoft.com/en-us/library/jj157191
	 * 
	 * @param subscriptionId
	 *            your subscription ID
	 * @return the list of images
	 */
	Images listOSImages(String subscriptionId);
	
//	String listOSImagesStr(String subscriptionId);

}