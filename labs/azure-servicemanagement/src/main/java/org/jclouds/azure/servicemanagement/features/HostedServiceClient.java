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

import java.util.concurrent.TimeUnit;

import org.jclouds.azure.servicemanagement.domain.hosted.CreateDeployment;
import org.jclouds.azure.servicemanagement.domain.hosted.CreateHostedService;
import org.jclouds.concurrent.Timeout;

/**
 * Provides synchronous access to Deployment.
 * <p/>
 * 
 * @author Gerald Pereira
 * @see HostedServiceClient
 * @see <a href="http://msdn.microsoft.com/en-us/library/ee460812">api doc</a>
 */
@Timeout(duration = 30, timeUnit = TimeUnit.SECONDS)
public interface HostedServiceClient {

	/**
	 * http://msdn.microsoft.com/en-us/library/gg441304
	 * 
	 * @param subscriptionId your subscription ID
	 * @param createHostedService the hosted service to create
	 */
	void createHostedService(String subscriptionId,CreateHostedService createHostedService);
	
	/**
	 * http://msdn.microsoft.com/en-us/library/ee460813
	 * 
	 * @param subscriptionId your subscription ID
	 * @param serviceName the unique DNS Prefix value in the Windows Azure Management Portal
	 * @param deploymentSlotName "staging" or "production", depending on where you wish to deploy your service package
	 * @param createDeployment the deployment to create
	 */
	void createDeployment(String subscriptionId,String serviceName,	String deploymentSlotName, CreateDeployment createDeployment);

}