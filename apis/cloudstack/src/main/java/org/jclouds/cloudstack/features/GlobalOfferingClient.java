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
package org.jclouds.cloudstack.features;

import org.jclouds.cloudstack.domain.DiskOffering;
import org.jclouds.cloudstack.domain.NetworkOffering;
import org.jclouds.cloudstack.domain.ServiceOffering;
import org.jclouds.cloudstack.options.CreateDiskOfferingOptions;
import org.jclouds.cloudstack.options.CreateServiceOfferingOptions;
import org.jclouds.cloudstack.options.UpdateDiskOfferingOptions;
import org.jclouds.cloudstack.options.UpdateNetworkOfferingOptions;
import org.jclouds.cloudstack.options.UpdateServiceOfferingOptions;

/**
 * Provides synchronous access to CloudStack zone features.
 * <p/>
 *
 * @see GlobalOfferingAsyncClient
 * @see <a href="http://download.cloud.com/releases/2.2.0/api_2.2.12/TOC_Global_Admin.html" />
 * @author Andrei Savu
 */
public interface GlobalOfferingClient extends OfferingClient {

   /**
    * Create a new service offering
    *
    * @param name
    *          name of the service offering
    * @param displayText
    *          display name
    * @param cpuNumber
    *          number of CPUs
    * @param cpuSpeedInMHz
    *          CPU speed in MHz
    * @param memoryInMB
    *          the total memory of the service offering in MB
    * @param options
    *          optional arguments
    * @return
    *          service offering instance
    */
   ServiceOffering createServiceOffering(String name, String displayText, int cpuNumber,
         int cpuSpeedInMHz, int memoryInMB, CreateServiceOfferingOptions... options);

   /**
    * Update an existing service offering
    *
    * @param id
    *          service offering ID
    * @param options
    *          optional arguments
    * @return
    *          service offering instance
    */
   ServiceOffering updateServiceOffering(String id, UpdateServiceOfferingOptions... options);

   /**
    * Delete service offering
    *
    * @param id
    *       the ID of the service offering
    */
   Void deleteServiceOffering(String id);

   /**
    * Create a new disk offering
    *
    * @param name
    *          name of the disk offering
    * @param displayText
    *          display text for disk offering
    * @param options
    *          optional arguments
    * @return
    *          disk offering instance
    */
   DiskOffering createDiskOffering(String name, String displayText, CreateDiskOfferingOptions... options);

   /**
    * Update a disk offering
    *
    * @param id
    *          disk offering ID
    * @param options
    *          optional arguments
    * @return
    *          disk offering instance
    */
   DiskOffering updateDiskOffering(String id, UpdateDiskOfferingOptions... options);

   /**
    * Delete disk offering
    *
    * @param id
    *       the ID of the disk offering
    */
   Void deleteDiskOffering(String id);

   /**
    * Update network offering
    *
    * @param id
    *       the id of the network offering
    * @param options
    *       optional arguments
    * @return
    *       network offering instance
    */
   NetworkOffering updateNetworkOffering(String id, UpdateNetworkOfferingOptions... options);
}
