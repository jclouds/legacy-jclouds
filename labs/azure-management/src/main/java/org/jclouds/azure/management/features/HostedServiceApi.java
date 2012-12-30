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

import java.util.Set;
import org.jclouds.azure.management.domain.Deployment;
import org.jclouds.azure.management.domain.HostedService;
import org.jclouds.azure.management.domain.HostedServiceWithDetailedProperties;
import org.jclouds.azure.management.options.CreateHostedServiceOptions;

/**
 * The Service Management API includes operations for managing the hosted services beneath your
 * subscription.
 * 
 * @see <a href="http://msdn.microsoft.com/en-us/library/ee460812">docs</a>
 * @see HostedServiceAsyncApi
 * @author Gerald Pereira, Adrian Cole
 */
public interface HostedServiceApi {

   /**
    * The List Hosted Services operation lists the hosted services available under the current
    * subscription.
    * 
    * @return the response object
    */
   Set<HostedServiceWithDetailedProperties> list();

   /**
    * The Create Hosted Service operation creates a new hosted service in Windows Azure.
    * 
    * @param serviceName
    *           A name for the hosted service that is unique within Windows Azure. This name is the
    *           DNS prefix name and can be used to access the hosted service.
    * 
    *           For example: http://ServiceName.cloudapp.net//
    * @param label
    *           The name can be used identify the storage account for your tracking purposes. The
    *           name can be up to 100 characters in length.
    * @param location
    *           The location where the hosted service will be created.
    * @return the requestId to track this async request progress
    * 
    * @see <a href="http://msdn.microsoft.com/en-us/library/ee460812">docs</a>
    */
   String createServiceWithLabelInLocation(String serviceName, String label, String location);

   /**
    * same as {@link #createServiceWithLabelInLocation(String, String, String)} , except you can
    * specify optional parameters such as extended properties or a description.
    * 
    * @param options
    *           parameters such as extended properties or a description.
    */
   String createServiceWithLabelInLocation(String serviceName, String label, String location,
            CreateHostedServiceOptions options);

   /**
    * The Get Hosted Service Properties operation retrieves system properties for the specified
    * hosted service. These properties include the service name and service type; the name of the
    * affinity group to which the service belongs, or its location if it is not part of an affinity
    * group.
    * 
    * @param serviceName
    *           the unique DNS Prefix value in the Windows Azure Management Portal
    */
   HostedService get(String serviceName);

   /**
    * like {@link #get(String)}, except additional data such as status and deployment information is
    * returned.
    * 
    * @param serviceName
    *           the unique DNS Prefix value in the Windows Azure Management Portal
    */
   HostedServiceWithDetailedProperties getDetails(String serviceName);

   /**
    * The Delete Hosted Service operation deletes the specified hosted service from Windows Azure.
    * 
    * @param serviceName
    *           the unique DNS Prefix value in the Windows Azure Management Portal
    *           
    * @return request id or null, if not found
    */
   String delete(String serviceName);

   /**
    * http://msdn.microsoft.com/en-us/library/ee460813
    * 
    * @param serviceName
    *           the unique DNS Prefix value in the Windows Azure Management Portal
    * @param deploymentSlotName
    *           "staging" or "production", depending on where you wish to deploy your service
    *           package
    * @param createDeployment
    *           the deployment to create
    */
 // This is a PaaS REST service !
//   void createDeployment(String serviceName, String deploymentSlotName, CreateDeployment createDeployment);

   /**
    * The Delete Deployment operation deletes the specified deployment from Windows Azure.
    * 
    * @param serviceName
    *           the unique DNS Prefix value in the Windows Azure Management Portal
    * @param deploymentName
    *           the unique DNS Prefix value in the Windows Azure Management Portal
    */
   String deleteDeployment(String serviceName, String deploymentName);
   
   
   /**
    * The Get Deployment operation returns the specified deployment from Windows Azure.
    * 
    * @param serviceName
    *           the unique DNS Prefix value in the Windows Azure Management Portal
    * @param deploymentName
    *           the unique DNS Prefix value in the Windows Azure Management Portal
    */
   Deployment getDeployment(String serviceName, String deploymentName);

}
