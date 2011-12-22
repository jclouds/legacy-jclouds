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
package org.jclouds.vcloud;

import org.jclouds.rest.annotations.Delegate;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.vcloud.features.CatalogAsyncClient;
import org.jclouds.vcloud.features.NetworkAsyncClient;
import org.jclouds.vcloud.features.OrgAsyncClient;
import org.jclouds.vcloud.features.TaskAsyncClient;
import org.jclouds.vcloud.features.VAppAsyncClient;
import org.jclouds.vcloud.features.VAppTemplateAsyncClient;
import org.jclouds.vcloud.features.VDCAsyncClient;
import org.jclouds.vcloud.features.VmAsyncClient;
import org.jclouds.vcloud.filters.SetVCloudTokenCookie;

/**
 * Provides access to VCloud resources via their REST API.
 * <p/>
 * 
 * @see <a href= "https://community.vcloudexpress.terremark.com/en-us/discussion_forums/f/60.aspx"
 *      />
 * @author Adrian Cole
 */
@RequestFilters(SetVCloudTokenCookie.class)
public interface VCloudAsyncClient {

   /**
    * Provides asynchronous access to VApp Template features.
    * 
    * @see VCloudClient#getVAppTemplateClient
    * 
    */
   @Delegate
   VAppTemplateAsyncClient getVAppTemplateClient();

   /**
    * Provides asynchronous access to VApp features.
    * 
    * @see VCloudClient#getVAppClient
    * 
    */
   @Delegate
   VAppAsyncClient getVAppClient();

   /**
    * Provides asynchronous access to Vm features.
    * 
    * @see VCloudClient#getVmClient
    * 
    */
   @Delegate
   VmAsyncClient getVmClient();

   /**
    * Provides asynchronous access to Catalog features.
    * 
    * @see VCloudClient#getCatalogClient
    * 
    */
   @Delegate
   CatalogAsyncClient getCatalogClient();

   /**
    * Provides asynchronous access to Task features.
    * 
    * @see VCloudClient#getTaskClient
    * 
    */
   @Delegate
   TaskAsyncClient getTaskClient();

   /**
    * Provides asynchronous access to VDC features.
    * 
    * @see VCloudClient#getVDCClient
    * 
    */
   @Delegate
   VDCAsyncClient getVDCClient();

   /**
    * Provides asynchronous access to Network features.
    * 
    * @see VCloudClient#getNetworkClient
    * 
    */
   @Delegate
   NetworkAsyncClient getNetworkClient();

   /**
    * Provides asynchronous access to Org features.
    * 
    * @see VCloudClient#getOrgClient
    * 
    */
   @Delegate
   OrgAsyncClient getOrgClient();

}
