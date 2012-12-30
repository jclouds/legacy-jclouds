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
package org.jclouds.azure.management;

import org.jclouds.azure.management.features.DiskApi;
import org.jclouds.azure.management.features.HostedServiceApi;
import org.jclouds.azure.management.features.LocationApi;
import org.jclouds.azure.management.features.OSImageApi;
import org.jclouds.azure.management.features.OperationApi;
import org.jclouds.azure.management.features.RoleApi;
import org.jclouds.rest.annotations.Delegate;

/**
 * The Windows Azure Service Management API is a REST API for managing your services and
 * deployments.
 * <p/>
 * 
 * @see AzureManagementAsyncApi
 * @see <a href="http://msdn.microsoft.com/en-us/library/ee460799" >doc</a>
 * @author Gerald Pereira, Adrian Cole
 */
public interface AzureManagementApi {
   /**
    * The Service Management API includes operations for listing the available data center locations
    * for a hosted service in your subscription.
    * 
    * @see <a href="http://msdn.microsoft.com/en-us/library/gg441299">docs</a>
    * @see AzureManagementAsyncApi#getLocationApi()
    */
   @Delegate
   LocationApi getLocationApi();

   /**
    * The Service Management API includes operations for managing the hosted services beneath your
    * subscription.
    * 
    * @see <a href="http://msdn.microsoft.com/en-us/library/ee460812">docs</a>
    * @see AzureManagementAsyncApi#getHostedServiceApi()
    */
   @Delegate
   HostedServiceApi getHostedServiceApi();

   /**
    * The Service Management API includes operations for managing the virtual machines in your
    * subscription.
    * 
    * @see <a href="http://msdn.microsoft.com/en-us/library/jj157206">docs</a>
    * @see AzureManagementAsyncApi#getRoleApi()
    */
   @Delegate
   RoleApi getRoleApi();

   /**
    * The Service Management API includes operations for managing the OS images in your
    * subscription.
    * 
    * @see <a href="http://msdn.microsoft.com/en-us/library/jj157175">docs</a>
    * @see AzureManagementAsyncApi#getOSImageApi()
    */
   @Delegate
   OSImageApi getOSImageApi();
   
   /**
    * The Service Management API includes operations for Tracking Asynchronous Service Management Requests.
    * 
    * @see <a href="http://msdn.microsoft.com/en-us/library/ee460791">docs</a>
    * @see AzureManagementAsyncApi#getOperationApi()
    */
   @Delegate
   OperationApi getOperationApi();
   
   /**
    * The Service Management API includes operations for managing Disks in your subscription.
    * 
    * @see <a href="http://msdn.microsoft.com/en-us/library/jj157188">docs</a>
    * @see AzureManagementAsyncApi#getDiskApi()
    */
   @Delegate
   DiskApi getDiskApi();
}
