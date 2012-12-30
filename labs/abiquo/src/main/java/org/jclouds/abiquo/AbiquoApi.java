/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.jclouds.abiquo;

import org.jclouds.abiquo.features.AdminApi;
import org.jclouds.abiquo.features.CloudApi;
import org.jclouds.abiquo.features.ConfigApi;
import org.jclouds.abiquo.features.EnterpriseApi;
import org.jclouds.abiquo.features.EventApi;
import org.jclouds.abiquo.features.InfrastructureApi;
import org.jclouds.abiquo.features.PricingApi;
import org.jclouds.abiquo.features.TaskApi;
import org.jclouds.abiquo.features.VirtualMachineTemplateApi;
import org.jclouds.rest.annotations.Delegate;

/**
 * Provides synchronous access to Abiquo.
 * 
 * @see API: <a href="http://community.abiquo.com/display/ABI20/API+Reference">
 *      http://community.abiquo.com/display/ABI20/API+Reference</a>
 * @see AbiquoAsyncApi
 * @author Ignasi Barrera
 */
public interface AbiquoApi {
   /**
    * Provides synchronous access to Admin features.
    */
   @Delegate
   AdminApi getAdminApi();

   /**
    * Provides synchronous access to Infrastructure features.
    */
   @Delegate
   InfrastructureApi getInfrastructureApi();

   /**
    * Provides synchronous access to Cloud features.
    */
   @Delegate
   CloudApi getCloudApi();

   /**
    * Provides synchronous access to Apps library features.
    */
   @Delegate
   VirtualMachineTemplateApi getVirtualMachineTemplateApi();

   /**
    * Provides synchronous access to Enterprise features.
    */
   @Delegate
   EnterpriseApi getEnterpriseApi();

   /**
    * Provides synchronous access to configuration features.
    */
   @Delegate
   ConfigApi getConfigApi();

   /**
    * Provides synchronous access to task asynchronous features.
    */
   @Delegate
   TaskApi getTaskApi();

   /**
    * Provides synchronous access to Event features.
    */
   @Delegate
   EventApi getEventApi();

   /**
    * Provides synchronous access to Pricing features.
    */
   @Delegate
   PricingApi getPricingApi();

}
