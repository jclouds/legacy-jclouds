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

import org.jclouds.abiquo.features.services.AdministrationService;
import org.jclouds.abiquo.features.services.CloudService;
import org.jclouds.abiquo.features.services.EventService;
import org.jclouds.abiquo.features.services.MonitoringService;
import org.jclouds.abiquo.features.services.PricingService;
import org.jclouds.abiquo.features.services.SearchService;
import org.jclouds.abiquo.internal.AbiquoContextImpl;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.rest.RestContext;

import com.google.inject.ImplementedBy;

/**
 * Abiquo {@link ComputeServiceContext} implementation to expose high level
 * Abiquo functionalities.
 * 
 * @author Ignasi Barrera
 */
@ImplementedBy(AbiquoContextImpl.class)
public interface AbiquoContext extends ComputeServiceContext {
   /**
    * Returns the Abiquo API context, providing direct access to the Abiquo Rest
    * API.
    * 
    * @return The Abiquo API context.
    */
   RestContext<AbiquoApi, AbiquoAsyncApi> getApiContext();

   /**
    * Returns the administration service.
    * <p>
    * This service provides an entry point to infrastructure administration
    * tasks.
    */
   AdministrationService getAdministrationService();

   /**
    * Returns the cloud service.
    * <p>
    * This service provides an entry point to cloud management tasks.
    */
   CloudService getCloudService();

   /**
    * Returns the search service.
    * <p>
    * This service provides an entry point to listing and filtering tasks.
    */
   SearchService getSearchService();

   /**
    * Returns the monitoring service.
    * <p>
    * This service provides an entry point to asynchronous task monitoring
    * tasks.
    */
   MonitoringService getMonitoringService();

   /**
    * Returns the event service.
    * <p>
    * This service provides an entry point to event management tasks.
    */
   EventService getEventService();

   /**
    * Returns the pricing service.
    * <p>
    * This service provides an entry point to pricing management tasks.
    */
   PricingService getPricingService();
}
