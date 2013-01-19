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

import org.jclouds.azure.management.domain.DeploymentParams;
import org.jclouds.azure.management.domain.role.PersistentVMRole;

/**
 * The Service Management API includes operations for managing the virtual
 * machines in your subscription.
 * 
 * @see <a href="http://msdn.microsoft.com/en-us/library/jj157206">docs</a>
 * @see RoleAsyncApi
 * @author Gerald Pereira, Adrian Cole
 */
public interface RoleApi {

   // TODO: this is not the good REST call !!! Use getDeployment instead :@
   PersistentVMRole getRole(String serviceName, String deploymentName, String roleName);

   String restartRole(String serviceName, String deploymentName, String roleName);

   /**
    * http://msdn.microsoft.com/en-us/library/jj157194
    * 
    * @param serviceName
    * @param deploymentParams
    * @return
    */
   String createDeployment(String serviceName, DeploymentParams deploymentParams);

   /**
    * http://msdn.microsoft.com/en-us/library/jj157201
    * 
    * @return
    */
   String captureRole(String serviceName, String deploymentName, String roleName, String imageName, String imageLabel);

   /**
    * http://msdn.microsoft.com/en-us/library/jj157195
    * 
    * @param serviceName
    * @param deploymentName
    * @param roleName
    * @return
    */
   String shutdownRole(String serviceName, String deploymentName, String roleName);

   /**
    * http://msdn.microsoft.com/en-us/library/jj157189
    * 
    * @param serviceName
    * @param deploymentName
    * @param roleName
    * @return
    */
   String startRole(String serviceName, String deploymentName, String roleName);

}
