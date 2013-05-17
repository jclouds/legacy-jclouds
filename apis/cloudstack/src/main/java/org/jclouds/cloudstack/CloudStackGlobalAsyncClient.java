/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.cloudstack;

import org.jclouds.cloudstack.features.GlobalAccountAsyncClient;
import org.jclouds.cloudstack.features.GlobalAlertAsyncClient;
import org.jclouds.cloudstack.features.GlobalCapacityAsyncClient;
import org.jclouds.cloudstack.features.GlobalConfigurationAsyncClient;
import org.jclouds.cloudstack.features.GlobalDomainAsyncClient;
import org.jclouds.cloudstack.features.GlobalHostAsyncClient;
import org.jclouds.cloudstack.features.GlobalOfferingAsyncClient;
import org.jclouds.cloudstack.features.GlobalPodAsyncClient;
import org.jclouds.cloudstack.features.GlobalStoragePoolAsyncClient;
import org.jclouds.cloudstack.features.GlobalUsageAsyncClient;
import org.jclouds.cloudstack.features.GlobalUserAsyncClient;
import org.jclouds.cloudstack.features.GlobalVlanAsyncClient;
import org.jclouds.cloudstack.features.GlobalZoneAsyncClient;
import org.jclouds.rest.annotations.Delegate;

/**
 * Provides asynchronous access to CloudStack via their REST API.
 * <p/>
 * 
 * @author Adrian Cole
 * @see CloudStackGlobalClient
 * @see <a href=
 *      "http://download.cloud.com/releases/2.2.0/api_2.2.12/TOC_Global_Admin.html"
 *      />
 * @deprecated please use {@code org.jclouds.ContextBuilder#buildApi(CloudStackGlobalClient.class)} as
 *             {@link CloudStackGlobalAsyncClient} interface will be removed in jclouds 1.7.
 */
@Deprecated
public interface CloudStackGlobalAsyncClient extends CloudStackDomainAsyncClient {

   /**
    * Provides asynchronous access to Accounts
    */
   @Delegate
   @Override
   GlobalAccountAsyncClient getAccountClient();

   /**
    * Provides asynchronous access to Users
    */
   @Delegate
   @Override
   GlobalUserAsyncClient getUserClient();

   /**
    * Provides asynchronous access to Alerts
    */
   @Delegate
   GlobalAlertAsyncClient getAlertClient();

   /**
    * Provides asynchronous access to Capacities
    */
   @Delegate
   GlobalCapacityAsyncClient getCapacityClient();

   /**
    * Provides asynchronous access to Offerings
    */
   @Delegate
   @Override
   GlobalOfferingAsyncClient getOfferingClient();

   /**
    * Provides asynchronous access to Hosts
    */
   @Delegate
   GlobalHostAsyncClient getHostClient();

   /**
    * Provides synchronous access to Storage Pools
    */
   @Delegate
   GlobalStoragePoolAsyncClient getStoragePoolClient();

   /**
    * Provides asynchronous access to Usage
    */
   @Delegate
   GlobalUsageAsyncClient getUsageClient();

   /**
    * Provides asynchronous access to Configuration
    */
   @Delegate
   @Override
   GlobalConfigurationAsyncClient getConfigurationClient();

   /**
    * Provides asynchronous access to Domain
    */
   @Delegate
   @Override
   GlobalDomainAsyncClient getDomainClient();

   /**
    * Provides asynchronous access to Zone
    */
   @Delegate
   @Override
   GlobalZoneAsyncClient getZoneClient();

   /**
    * Provides asynchronous access to Pod
    */
   @Delegate
   GlobalPodAsyncClient getPodClient();

   /**
    * Provides asynchronous access to Vlan
    */
   @Delegate
   GlobalVlanAsyncClient getVlanClient();
}
