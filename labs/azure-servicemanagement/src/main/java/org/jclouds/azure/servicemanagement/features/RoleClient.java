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

import org.jclouds.azure.servicemanagement.domain.role.Role;
import org.jclouds.concurrent.Timeout;

/**
 * Provides synchronous access to Role.
 * <p/>
 * 
 * @author Gerald Pereira
 * @see RoleAsyncClient
 * @see <a href="http://msdn.microsoft.com/en-us/library/jj157206">api doc</a>
 */
@Timeout(duration = 30, timeUnit = TimeUnit.SECONDS)
public interface RoleClient {


	/**
	 *  http://msdn.microsoft.com/en-us/library/jj157193
	 */
	Role getRole(String subscriptionId,
			String serviceName,
			String deploymentName,
			String roleName);

	/**
	 * http://msdn.microsoft.com/en-us/library/jj157186
	 */
	String addRole(String subscriptionId,
			String serviceName,
			String deploymentName,Role role);

}