/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */
package org.jclouds.vcloud.hostingdotcom.compute.config;

import javax.inject.Singleton;

import org.jclouds.compute.ComputeService;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.internal.ComputeServiceContextImpl;
import org.jclouds.rest.RestContext;
import org.jclouds.vcloud.hostingdotcom.HostingDotComVCloudAsyncClient;
import org.jclouds.vcloud.hostingdotcom.compute.HostingDotComVCloudComputeService;
import org.jclouds.vcloud.hostingdotcom.config.HostingDotComVCloudContextModule;

import com.google.inject.Provides;

/**
 * Configures the {@link HostingDotComVCloudComputeServiceContext}; requires
 * {@link HostingDotComVCloudComputeService} bound.
 * 
 * @author Adrian Cole
 */
public class HostingDotComVCloudComputeServiceContextModule extends
         HostingDotComVCloudContextModule {

   @Override
   protected void configure() {
      super.configure();
      bind(ComputeService.class).to(HostingDotComVCloudComputeService.class).asEagerSingleton();
   }

   @Provides
   @Singleton
   ComputeServiceContext provideContext(ComputeService computeService,
            RestContext<HostingDotComVCloudAsyncClient, HostingDotComVCloudAsyncClient> context) {
      return new ComputeServiceContextImpl<HostingDotComVCloudAsyncClient, HostingDotComVCloudAsyncClient>(
               computeService, context);
   }
}
