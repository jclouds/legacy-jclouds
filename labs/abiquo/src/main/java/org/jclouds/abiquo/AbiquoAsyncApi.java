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

import org.jclouds.abiquo.features.AdminAsyncApi;
import org.jclouds.abiquo.features.CloudAsyncApi;
import org.jclouds.abiquo.features.ConfigAsyncApi;
import org.jclouds.abiquo.features.EnterpriseAsyncApi;
import org.jclouds.abiquo.features.EventAsyncApi;
import org.jclouds.abiquo.features.InfrastructureAsyncApi;
import org.jclouds.abiquo.features.PricingAsyncApi;
import org.jclouds.abiquo.features.TaskAsyncApi;
import org.jclouds.abiquo.features.VirtualMachineTemplateAsyncApi;
import org.jclouds.rest.annotations.Delegate;

import com.abiquo.model.transport.SingleResourceTransportDto;

/**
 * Provides asynchronous access to Abiquo via their REST API.
 * 
 * @see API: <a href="http://community.abiquo.com/display/ABI20/API+Reference">
 *      http://community.abiquo.com/display/ABI20/API+Reference</a>
 * @see InfrastructureAsyncApi
 * @author Ignasi Barrera
 */
public interface AbiquoAsyncApi {
   /**
    * The version of the supported Abiquo API.
    */
   public static final String API_VERSION = SingleResourceTransportDto.API_VERSION;

   /**
    * The supported build version of the Abiquo Api.
    */
   public static final String BUILD_VERSION = "7bbfe95-158721b";

   /**
    * Provides asynchronous access to Admin features.
    */
   @Delegate
   AdminAsyncApi getAdminApi();

   /**
    * Provides asynchronous access to Infrastructure features.
    */
   @Delegate
   InfrastructureAsyncApi getInfrastructureApi();

   /**
    * Provides asynchronous access to Cloud features.
    */
   @Delegate
   CloudAsyncApi getCloudApi();

   /**
    * Provides asynchronous access to Apps library features.
    */
   @Delegate
   VirtualMachineTemplateAsyncApi getVirtualMachineTemplateApi();

   /**
    * Provides asynchronous access to Enterprise features.
    */
   @Delegate
   EnterpriseAsyncApi getEnterpriseApi();

   /**
    * Provides asynchronous access to configuration features.
    */
   @Delegate
   ConfigAsyncApi getConfigApi();

   /**
    * Provides asynchronous access to task asynchronous features.
    */
   @Delegate
   TaskAsyncApi getTaskApi();

   /**
    * Provides asynchronous access to Event features.
    */
   @Delegate
   EventAsyncApi getEventApi();

   /**
    * Provides asynchronous access to Pricing features.
    */
   @Delegate
   PricingAsyncApi getPricingApi();
}
