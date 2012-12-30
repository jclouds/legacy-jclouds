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
package org.jclouds.cloudstack;

import org.jclouds.cloudstack.features.GlobalAccountClient;
import org.jclouds.cloudstack.features.GlobalAlertClient;
import org.jclouds.cloudstack.features.GlobalCapacityClient;
import org.jclouds.cloudstack.features.GlobalConfigurationClient;
import org.jclouds.cloudstack.features.GlobalDomainClient;
import org.jclouds.cloudstack.features.GlobalHostClient;
import org.jclouds.cloudstack.features.GlobalOfferingClient;
import org.jclouds.cloudstack.features.GlobalPodClient;
import org.jclouds.cloudstack.features.GlobalStoragePoolClient;
import org.jclouds.cloudstack.features.GlobalUsageClient;
import org.jclouds.cloudstack.features.GlobalUserClient;
import org.jclouds.cloudstack.features.GlobalVlanClient;
import org.jclouds.cloudstack.features.GlobalZoneClient;
import org.jclouds.rest.annotations.Delegate;

/**
 * Provides synchronous access to CloudStack.
 * <p/>
 * 
 * @author Adrian Cole
 * @see CloudStackDomainAsyncClient
 * @see <a href=
 *      "http://download.cloud.com/releases/2.2.0/api_2.2.12/TOC_Global_Admin.html"
 *      />
 */
public interface CloudStackGlobalClient extends CloudStackDomainClient {

   /**
    * Provides synchronous access to Accounts
    */
   @Delegate
   @Override
   GlobalAccountClient getAccountClient();

   /**
    * Provides synchronous access to Users
    */
   @Delegate
   @Override
   GlobalUserClient getUserClient();

   /**
    * Provides synchronous access to Alerts
    */
   @Delegate
   GlobalAlertClient getAlertClient();

   /**
    * Provides synchronous access to Capacities
    */
   @Delegate
   GlobalCapacityClient getCapacityClient();

   /**
    * Provides synchronous access to Offerings
    */
   @Delegate
   @Override
   GlobalOfferingClient getOfferingClient();

   /**
    * Provides synchronous access to Hosts
    */
   @Delegate
   GlobalHostClient getHostClient();

   /**
    * Provides synchronous access to Storage Pools
    */
   @Delegate
   GlobalStoragePoolClient getStoragePoolClient();

   /**
    * Provides synchronous access to Usage
    */
   @Delegate
   GlobalUsageClient getUsageClient();

   /**
    * Provides synchronous access to Configuration
    */
   @Delegate
   @Override
   GlobalConfigurationClient getConfigurationClient();

   /**
    * Provides synchronous access to Domain
    */
   @Delegate
   @Override
   GlobalDomainClient getDomainClient();

   /**
    * Provides synchronous access to Zone
    */
   @Delegate
   @Override
   GlobalZoneClient getZoneClient();

   /**
    * Provides synchronous access to Pod
    */
   @Delegate
   GlobalPodClient getPodClient();

   /**
    * Provides synchronous access to Vlan
    */
   @Delegate
   GlobalVlanClient getVlanClient();
}
